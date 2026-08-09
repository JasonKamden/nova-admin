import {localStg} from '@/utils/storage';

const CP1252_BYTE_MAP = new Map<number, number>([
  [8364, 128],
  [8218, 130],
  [402, 131],
  [8222, 132],
  [8230, 133],
  [8224, 134],
  [8225, 135],
  [710, 136],
  [8240, 137],
  [352, 138],
  [8249, 139],
  [338, 140],
  [381, 142],
  [8216, 145],
  [8217, 146],
  [8220, 147],
  [8221, 148],
  [8226, 149],
  [8211, 150],
  [8212, 151],
  [732, 152],
  [8482, 153],
  [353, 154],
  [8250, 155],
  [339, 156],
  [382, 158],
  [376, 159]
]);

function toSingleByte(char: string) {
  const code = char.charCodeAt(0);

  if (code <= 255) {
    return code;
  }

  return CP1252_BYTE_MAP.get(code) ?? null;
}

function repairUtf8Mojibake(value: string) {
  if (!/[À-ÿ]/.test(value)) {
    return value;
  }

  let result = '';
  let segment = '';

  const flushSegment = () => {
    if (!segment) {
      return;
    }

    if (!/[À-ÿ]/.test(segment)) {
      result += segment;
      segment = '';
      return;
    }

    try {
      const bytes = Uint8Array.from(
        Array.from(segment, char => {
          const byte = toSingleByte(char);

          return byte ?? char.charCodeAt(0);
        })
      );
      const decoded = new TextDecoder('utf-8', {fatal: true}).decode(bytes);

      result += decoded || segment;
    } catch {
      result += segment;
    }

    segment = '';
  };

  for (const char of value) {
    if (toSingleByte(char) !== null) {
      segment += char;
      continue;
    }

    flushSegment();
    result += char;
  }

  flushSegment();

  return result;
}

export function formatContextType(contextType: string | null | undefined) {
  const lang = localStg.get('lang') || 'zh-CN';
  const isZh = lang === 'zh-CN';

  if (contextType === 'PLATFORM') {
    return isZh ? '平台' : 'Platform';
  }

  return isZh ? '租户' : 'Tenant';
}

export function normalizeBusinessText(value: string | null | undefined) {
  if (!value) {
    return value || '-';
  }

  const normalizedValue = repairUtf8Mojibake(value);
  const lang = localStg.get('lang') || 'zh-CN';

  if (lang !== 'zh-CN') {
    return normalizedValue;
  }

  return normalizedValue
    .replace(/\bTenant\b/g, '租户')
    .replace(/\bDepartment\b/g, '部门')
    .replace(/\bContext\b/g, '上下文');
}
