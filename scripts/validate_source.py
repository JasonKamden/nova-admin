#!/usr/bin/env python3
from pathlib import Path
import re, sys, xml.etree.ElementTree as ET
ROOT=Path(__file__).resolve().parents[1]
errors=[]; warnings=[]

def fail(msg): errors.append(msg)
def warn(msg): warnings.append(msg)

for pom in ROOT.rglob('pom.xml'):
    try: ET.parse(pom)
    except Exception as e: fail(f'invalid XML {pom.relative_to(ROOT)}: {e}')

if (ROOT/'.editorconfig').exists(): fail('.editorconfig must not exist')
for bad in ['target','node_modules','dist','.idea','.git']:
    for p in ROOT.rglob(bad):
        if p.is_dir(): fail(f'generated/local directory present: {p.relative_to(ROOT)}')
for ext in ['*.class','*.jar','*.war','*.log','*.tmp','*.lastUpdated']:
    for p in ROOT.rglob(ext): fail(f'generated artifact present: {p.relative_to(ROOT)}')

for xml in ROOT.rglob('*.xml'):
    if xml.name!='pom.xml':
        if 'mapper' in str(xml).lower(): fail(f'Mapper XML is forbidden: {xml.relative_to(ROOT)}')

java=list((ROOT/'backend').rglob('*.java'))
text='\n'.join(p.read_text(errors='ignore') for p in java)
for token in ['BeanUtils.copyProperties','BeanCopier','@Select(','@Update(','@Insert(','@Delete(']:
    if token in text: fail(f'forbidden Java pattern found: {token}')
if re.search(r'\bDept[A-Z]|\bdept[A-Z_]|sys_dept\b', text): warn('possible Dept/dept formal naming found; inspect manually')
if 'NOVA_' in text or any('NOVA_' in p.read_text(errors='ignore') for p in ROOT.rglob('*.yml')): fail('NOVA_ environment prefix found')

# public top-level type filename consistency
for p in java:
    s=p.read_text(errors='ignore')
    m=re.search(r'(?m)^public\s+(?:abstract\s+|final\s+|sealed\s+)?(?:class|interface|enum|record)\s+(\w+)',s)
    if m and m.group(1)!=p.stem: fail(f'public type/file mismatch: {p.relative_to(ROOT)} -> {m.group(1)}')

# Internal import existence, excluding generated MyBatis-Flex TableDef imports
fqcn=set()
for p in java:
    s=p.read_text(errors='ignore')
    pm=re.search(r'(?m)^package\s+([\w.]+);',s)
    tm=re.search(r'(?m)^public\s+(?:abstract\s+|final\s+|sealed\s+)?(?:class|interface|enum|record)\s+(\w+)',s)
    if pm and tm: fqcn.add(pm.group(1)+'.'+tm.group(1))
for p in java:
    for imp in re.findall(r'(?m)^import\s+(?:static\s+)?(org\.dromara\.nova\.[\w.]+);',p.read_text(errors='ignore')):
        base=imp
        if '.entity.table.' in base: continue
        # static member import -> progressively trim to class
        while base not in fqcn and '.' in base: base=base.rsplit('.',1)[0]
        if base.startswith('org.dromara.nova.') and base not in fqcn:
            fail(f'unresolved internal import in {p.relative_to(ROOT)}: {imp}')


# API documentation/comment contract checks
controllers=list((ROOT/'backend').rglob('*Controller.java'))
for controller in controllers:
    source=controller.read_text(errors='ignore')
    if '@Tag(' not in source:
        fail(f'missing OpenAPI @Tag: {controller.relative_to(ROOT)}')
    # Every HTTP mapping method must carry an @Operation description in its annotation run.
    method_pattern=re.compile(r'((?:\s*@[A-Za-z][\w.]*\s*(?:\([^\n]*?\))?)+\s*)public\s+[^\n;{]+?\s+\w+\s*\(', re.M)
    for match in method_pattern.finditer(source):
        annotations=match.group(1)
        if any(mapping in annotations for mapping in ['@GetMapping','@PostMapping','@PutMapping','@DeleteMapping','@PatchMapping']):
            if '@Operation(' not in annotations:
                fail(f'missing OpenAPI @Operation: {controller.relative_to(ROOT)} near {match.group(0)[:120]!r}')

dto_files=[p for p in (ROOT/'backend').rglob('*.java') if '/dto/' in p.as_posix()]
for dto in dto_files:
    source=dto.read_text(errors='ignore')
    if '@Schema(' not in source:
        fail(f'missing OpenAPI @Schema: {dto.relative_to(ROOT)}')

# Core business write operations should be auditable. Query/read methods are intentionally excluded.
write_method_names={'create','update','delete','updateStatus','resetPassword','replaceRoles','replaceMenus','createType','updateType','deleteType','createData','updateData','deleteData','clear','refresh','batchClear','forceLogout','changePassword','updateAvatar','upload','batchDelete','send','withdraw','importUsers','export'}
for service in (ROOT/'backend').rglob('*ServiceImpl.java'):
    source=service.read_text(errors='ignore')
    method_pattern=re.compile(r'@Override(?P<annotations>(?:\s+@[A-Za-z][\w.]*\s*(?:\([^\n]*?\))?)*)\s+public\s+[^\n{]+?\s+(?P<name>\w+)\s*\(')
    for match in method_pattern.finditer(source):
        if match.group('name') in write_method_names and '@OperationAudit(' not in match.group('annotations'):
            fail(f'core write operation missing @OperationAudit: {service.relative_to(ROOT)}#{match.group("name")}')

# Do not introduce obvious sensitive values into normal runtime logs.
for java_file in java:
    source=java_file.read_text(errors='ignore')
    for log_call in re.findall(r'log\.(?:info|warn|error|debug)\([^;]*?\);', source, flags=re.S):
        lowered=log_call.lower()
        if any(token in lowered for token in ['request.password()', 'getpassword()', 'miniosecretkey', 'authorization', 'secretkey']):
            fail(f'possible sensitive runtime log: {java_file.relative_to(ROOT)}')

schema=(ROOT/'sql/schema.sql').read_text(errors='ignore')
required=['sys_user','sys_tenant','sys_user_tenant','sys_department','sys_role','sys_user_role','sys_menu','sys_role_menu','sys_role_department','sys_dict_type','sys_dict_data','sys_config','sys_login_log','sys_operation_log','sys_file','sys_file_relation','sys_message','sys_message_user','sys_message_file']
for table in required:
    if not re.search(rf'CREATE TABLE IF NOT EXISTS\s+{re.escape(table)}\b',schema,re.I): fail(f'missing SQL table: {table}')
for forbidden in ['sys_organization','sys_post','sys_user_post']:
    if re.search(rf'CREATE TABLE IF NOT EXISTS\s+{forbidden}\b',schema,re.I): fail(f'forbidden SQL table: {forbidden}')

# module path check
for pom in [ROOT/'backend/pom.xml',ROOT/'backend/nova-common/pom.xml',ROOT/'backend/nova-modules/pom.xml']:
    if pom.exists():
        raw=pom.read_text()
        for mod in re.findall(r'<module>([^<]+)</module>',raw):
            if not (pom.parent/mod).exists(): fail(f'missing Maven module path: {(pom.parent/mod).relative_to(ROOT)}')

print(f'Java files: {len(java)}')
print(f'POM files: {len(list(ROOT.rglob("pom.xml")))}')
print(f'SQL tables checked: {len(required)}')
for w in warnings: print('WARNING:',w)
if errors:
    for e in errors: print('ERROR:',e)
    sys.exit(1)
print('SOURCE VALIDATION: PASS')
