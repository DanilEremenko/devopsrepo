package com.ws.bebetter.web.dto;

import lombok.Data;
import org.springframework.data.domain.Page;

@Data
public class ListPage<T> {
    private int number;
    private int perPage;
    private int pageCount;

    public ListPage(Page<T> page) {
        this.number = page.getNumber() + 1;
        this.perPage = page.getSize();
        this.pageCount = (int) Math.ceil((double) page.getTotalElements() / page.getSize());
    }
}
