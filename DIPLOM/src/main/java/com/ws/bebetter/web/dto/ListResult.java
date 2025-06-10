package com.ws.bebetter.web.dto;

import lombok.Data;

import java.util.List;

@Data
public class ListResult<T> {

    private List<T> items;

    private ListPage<T> page;

    public static <T> ListResult<T> of(List<T> items, ListPage<T> page) {
        ListResult<T> listResult = new ListResult<>();
        listResult.setItems(items);
        listResult.setPage(page);
        return listResult;
    }

}

