package com.leyou.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum  ExceptionEnum {
    CATEGORY_NOT_FOND(404,"商品分类没查到"),
    GOODS_NOT_FOND(404,"商品没查到"),
    SPEC_GROUP_NOT_FOND(404,"商品规格组不存在"),
    SPEC_GROUP_HAVE_PARAM(500,"该规格组存在参数不能删除"),
    SPEC_PARAM_NOT_FOND(404,"商品规格参数不存在"),
    BRAND_NOT_FOUND(404,"商品分类没查到"),
    SPEC_GROUP_SAVE_ERROR(500,"新增规格组失败"),
    SPEC_PARAM_SAVE_ERROR(500,"保存规格参数失败"),
    SPEC_PARAM_DELETE_ERROR(500,"删除规格参数失败"),
    BRAND_SAVE_ERROR(500,"新增品牌失败"),
    UPLOAD_FILE_ERROR(500,"文件上传失败"),
    INVALID_FILE_TYPE(400,"无效的文件类型"),
    GOODS_SAVE_ERROR(500,"新增商品失败")
    ;
    private int code;
    private String msg;
}
