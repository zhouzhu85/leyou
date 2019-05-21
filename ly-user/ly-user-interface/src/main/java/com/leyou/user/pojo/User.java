package com.leyou.user.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * @author zhouzhu
 * @Description 用户实体类
 * @create 2019-05-21 16:41
 */
@Table(name = "tb_user")
@Data
public class User {
    @Id
    @KeySql(useGeneratedKeys = true)
    private Long id;
    private String username;
    @JsonIgnore
    private String password;
    private String phone;
    /**
     * 创建时间
     */
    private Date created;
    /**
     * 密码的盐值
     */
    @JsonIgnore
    private String salt;
}
