package com.cmp.mall.dto;

import java.util.List;

/** 通用分页结果。字段名与前端表格组件的习惯保持一致 */
public record PageResult<T>(
        long total,
        int pageNum,
        int pageSize,
        int pages,
        List<T> list
) {
    public static <T> PageResult<T> of(long total, int pageNum, int pageSize, List<T> list) {
        int pages = pageSize <= 0 ? 0 : (int) ((total + pageSize - 1) / pageSize);
        return new PageResult<>(total, pageNum, pageSize, pages, list);
    }
}
