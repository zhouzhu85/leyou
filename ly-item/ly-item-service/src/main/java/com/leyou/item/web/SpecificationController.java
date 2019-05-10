package com.leyou.item.web;

import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;
import com.leyou.item.service.SpecificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sun.net.www.http.HttpClient;

import java.util.List;

@RestController
@RequestMapping("spec")
public class SpecificationController {
    @Autowired
    private SpecificationService specificationService;

    /**
     * 根据分类id查询规格组
     * @param cid
     * @return
     */
    @GetMapping("groups/{cid}")
    public ResponseEntity<List<SpecGroup>> queryGroupByCid(@PathVariable("cid") Long cid){
        return ResponseEntity.ok(specificationService.queryGroupByCid(cid));
    }

    /**
     * 查询参数集合
     * @param gid
     * @return
     */
    @GetMapping("params")
    public ResponseEntity<List<SpecParam>> queryParamByGid(
            @RequestParam(value = "gid",required = false) Long gid,
            @RequestParam(value = "cid",required = false) Long cid,
            @RequestParam(value = "searching",required = false) Boolean searching
    ){
        return ResponseEntity.ok(specificationService.queryParams(gid,cid,searching));
    }

    @GetMapping("group")
    public ResponseEntity<List<SpecGroup>> queryListByCid(@RequestParam("cid") Long cid){
        return ResponseEntity.ok(specificationService.queryListByCid(cid));
    }
    /**
     * 新增规格参数
     * @param p
     * @return
     */
    @PostMapping("param")
    public ResponseEntity<Void> addParam(SpecParam p){
        specificationService.saveParam(p);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 修改规格参数
     * @param p
     * @return
     */
    @PutMapping("param")
    public ResponseEntity<Void> updateParam(SpecParam p){
        specificationService.saveParam(p);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 删除规格参数
     * @param id
     * @return
     */
    @DeleteMapping("param/{id}")
    public ResponseEntity<Void> deleteParam(@PathVariable("id") Long id){
        specificationService.deleteParamById(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
    /**
     * 新增规格组
     * @param group
     * @return
     */
    @PostMapping("group")
    public ResponseEntity<Void> addGroupByCid(SpecGroup group){
        specificationService.saveSpecGroup(group);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 修改规格组
     * @param group
     * @return
     */
    @PutMapping("group")
    public ResponseEntity<Void> updateGroupByCid(SpecGroup group){
        specificationService.saveSpecGroup(group);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 删除规格组
     * @param gid
     * @return
     */
    @DeleteMapping("group/{gid}")
    public ResponseEntity<Void> deleteGroupByCid(@PathVariable("gid") Long gid){
        specificationService.deleteGroupByCid(gid);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
