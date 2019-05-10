package com.leyou.item.api;

import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface SpecificationApi {
    /**
     * 查询参数集合
     * @param gid
     * @param cid
     * @param searching
     * @return List<SpecParam>
     */
    @GetMapping("spec/params")
    List<SpecParam> queryParamByGid(
            @RequestParam(value = "gid",required = false) Long gid,
            @RequestParam(value = "cid",required = false) Long cid,
            @RequestParam(value = "searching",required = false) Boolean searching
    );

    /**
     * 根据分类查询规格组及组内参数
     * @param cid
     * @return List<SpecGroup>
     */
    @GetMapping("spec/group")
    List<SpecGroup> queryGroupByCid(@RequestParam("cid") Long cid);
}
