package com.leyou.item.web;

import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.service.SpecGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("spec")
public class SpecGroupController {
    @Autowired
    private SpecGroupService specGroupService;

    /**
     * 根据分类id查询规格组
     * @param cid
     * @return
     */
    @GetMapping("{cid}")
    private ResponseEntity<List<SpecGroup>> queryGroupByCid(@PathVariable("cid") Long cid){
        return ResponseEntity.ok(specGroupService.queryGroupByCid(cid));
    }
}
