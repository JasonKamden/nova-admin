package org.dromara.nova.common.core.util;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

/**
 * 通用只读树构建工具。
 */
public final class TreeUtils {
    private TreeUtils() {
    }

    /**
     * 从树形源数据中筛选父节点不在当前集合内的根节点。
     *
     * @param source       树形源数据
     * @param idGetter     节点 ID 读取函数
     * @param parentGetter 父节点 ID 读取函数
     * @return 根节点列表
     */
    public static <T, ID> List<T> roots(List<T> source, Function<T, ID> idGetter, Function<T, ID> parentGetter) {
        if (source == null || source.isEmpty()) return List.of();
        Set<ID> ids = new HashSet<>();
        source.forEach(item -> ids.add(idGetter.apply(item)));
        return source.stream().filter(item -> parentGetter.apply(item) == null || !ids.contains(parentGetter.apply(item))).toList();
    }

    /**
     * 沿父节点链检查新的父子关系是否会形成循环引用。
     *
     * @param id             当前节点 ID
     * @param parentId       候选父节点 ID
     * @param parentResolver 父节点解析函数
     * @return 是否形成循环
     */
    public static <ID> boolean wouldCreateCycle(ID id, ID parentId, Function<ID, ID> parentResolver) {
        if (id == null || parentId == null) return false;
        Set<ID> visited = new HashSet<>();
        ID current = parentId;
        while (current != null && visited.add(current)) {
            if (Objects.equals(id, current)) return true;
            current = parentResolver.apply(current);
        }
        return false;
    }
}
