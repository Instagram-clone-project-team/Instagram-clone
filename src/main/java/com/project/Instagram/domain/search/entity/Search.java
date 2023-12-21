package com.project.Instagram.domain.search.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
@Setter
@Getter
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn
@Table(name = "searches")
public class Search {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "search_id")
    private Long id;

    @Column(name = "search_count")
    private Long count;

    @Column(insertable = false, updatable = false)
    private String dtype;

    @Transient
    public void setDtype() {
        this.dtype = getClass().getAnnotation(DiscriminatorValue.class).value();
    }

    protected Search() {
        this.count = 0L;
    }

    public void upCount(){this.count = count+1;}
}
