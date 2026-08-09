function collectDescendantIds(nodes: Api.Menu.Item[], targetId: number): Set<number> {
    const ids = new Set<number>();

    function walk(list: Api.Menu.Item[]) {
        for (const item of list) {
            if (item.id === targetId) {
                collect(item);
                break;
            }

            if (item.children.length) {
                walk(item.children);
            }
        }
    }

    function collect(node: Api.Menu.Item) {
        ids.add(node.id);
        node.children.forEach(child => collect(child));
    }

    walk(nodes);
    return ids;
}

export function toMenuTreeOptions(nodes: Api.Menu.Item[], disabledIds: Set<number> = new Set()): Api.Menu.TreeOption[] {
    return nodes.map<Api.Menu.TreeOption>(item => ({
        label: item.menuName,
        key: item.id,
        value: item.id,
        disabled: disabledIds.has(item.id),
        children: item.children.length ? toMenuTreeOptions(item.children, disabledIds) : undefined
    }));
}

export {collectDescendantIds};
