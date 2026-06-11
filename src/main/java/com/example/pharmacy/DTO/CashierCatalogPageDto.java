package com.example.pharmacy.DTO;

import java.util.List;

public class CashierCatalogPageDto {

    private final List<CashierProductRowDto> content;
    private final int currentPage;
    private final int totalPages;
    private final long totalElements;
    private final int pageSize;

    public CashierCatalogPageDto(List<CashierProductRowDto> content,
                                 int currentPage,
                                 int totalPages,
                                 long totalElements,
                                 int pageSize) {
        this.content = content;
        this.currentPage = currentPage;
        this.totalPages = totalPages;
        this.totalElements = totalElements;
        this.pageSize = pageSize;
    }

    public List<CashierProductRowDto> getContent() {
        return content;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public int getPageSize() {
        return pageSize;
    }

    public boolean isEmpty() {
        return content.isEmpty();
    }
}
