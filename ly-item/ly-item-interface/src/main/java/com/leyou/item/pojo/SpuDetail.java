package com.leyou.item.pojo;

import lombok.Data;

import javax.persistence.Table;

@Data
@Table(name = "tb_spu_detail")
public class SpuDetail {

    private Long spuId;

    private String description;

    private String specifications;

    private String specTemplate;

    private String packingList;

    private String afterService;

    private String genericSpec;

    private String specialSpec;
}
