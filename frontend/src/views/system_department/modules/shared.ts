export function toDepartmentTreeOptions(
    departments: Api.Department.Item[],
    disabledIds: Set<number> = new Set()
): Api.Department.TreeOption[] {
    return departments.map(item => ({
        label: `${item.departmentName} (${item.departmentCode})`,
        key: item.id,
        value: item.id,
        disabled: disabledIds.has(item.id),
        children: item.children?.length ? toDepartmentTreeOptions(item.children, disabledIds) : undefined
    }));
}

export function collectDepartmentIds(departments: Api.Department.Item[], targetId: number): Set<number> {
    const result = new Set<number>();

    function walk(nodes: Api.Department.Item[], inTarget = false) {
        nodes.forEach(node => {
            const nextInTarget = inTarget || node.id === targetId;

            if (nextInTarget) {
                result.add(node.id);
            }

            walk(node.children || [], nextInTarget);
        });
    }

    walk(departments);

    return result;
}
