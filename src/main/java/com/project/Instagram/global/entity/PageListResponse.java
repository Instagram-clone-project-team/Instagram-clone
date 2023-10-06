package com.project.Instagram.global.entity;


import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Setter
public class PageListResponse<T> {
    private List<T> data;
    private PageInfo pageInfo;

    public PageListResponse(List<T> data, Page pages){
        this.data=data;
        this.pageInfo=new PageInfo(pages.getNumber() + 1,
                pages.getSize(), pages.getTotalElements(), pages.getTotalPages());
    }
}
