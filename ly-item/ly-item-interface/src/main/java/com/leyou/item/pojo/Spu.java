package com.leyou.item.pojo;

import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;

@Data
@Table(name = "tb_spu")
public class Spu {
    @Id
    @KeySql(useGeneratedKeys = true)
    private Long id;

    private String title;

    private String subTitle;

    private Long cid1;

    private Long cid2;

    private Long cid3;

    @Transient
    private String cname;

    private Long brandId;

    @Transient
    private String bname;

    private Boolean saleable;

    private Boolean valid;

    private Date createTime;

    private Date lastUpdateTime;
}
