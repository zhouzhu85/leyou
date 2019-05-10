package com.leyou.item.service;

import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.item.mapper.SpecGroupMapper;
import com.leyou.item.mapper.SpecParamMapper;
import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SpecificationService {
    @Autowired
    private SpecGroupMapper specGroupMapper;

    @Autowired
    private SpecParamMapper specParamMapper;

    public List<SpecGroup> queryGroupByCid(Long cid) {
        SpecGroup specGroup=new SpecGroup();
        specGroup.setCid(cid);
        List<SpecGroup> specGroupList = specGroupMapper.select(specGroup);
        if (CollectionUtils.isEmpty(specGroupList)){
            //抛出'没查到'信息
            throw new LyException(ExceptionEnum.SPEC_GROUP_NOT_FOND);
        }
        return specGroupList;
    }

    public List<SpecParam> queryParams(Long gid,Long cid,Boolean searching) {
        SpecParam specParam=new SpecParam();
        specParam.setGroupId(gid);
        specParam.setCid(cid);
        specParam.setSearching(searching);
        List<SpecParam> specParamList = specParamMapper.select(specParam);
        if (CollectionUtils.isEmpty(specParamList)){
            throw new LyException(ExceptionEnum.SPEC_PARAM_NOT_FOND);
        }
        return specParamList;
    }

    public void saveSpecGroup(SpecGroup specGroup) {
        int count=0;
        if (specGroup.getId()!=null){
            count = specGroupMapper.updateByPrimaryKey(specGroup);
        }else {
         count= specGroupMapper.insert(specGroup);
        }
        if (count!=1){
            throw new LyException(ExceptionEnum.SPEC_GROUP_SAVE_ERROR);
        }
    }

    public void saveParam(SpecParam param) {
        int count=0;
        if (param.getId()==null){
            count = specParamMapper.insert(param);
        }else {
            count=specParamMapper.updateByPrimaryKey(param);
        }
        if (count!=1){
            throw new LyException(ExceptionEnum.SPEC_PARAM_SAVE_ERROR);
        }
    }

    public void deleteGroupByCid(Long gid) {
        SpecParam specParam=new SpecParam();
        specParam.setGroupId(gid);
        List<SpecParam> paramList = specParamMapper.select(specParam);
        if (paramList.size()>0){
            throw new LyException(ExceptionEnum.SPEC_GROUP_HAVE_PARAM);
        }else {
            specGroupMapper.deleteByPrimaryKey(gid);
        }
    }

    public void deleteParamById(Long id) {
        int count = specParamMapper.deleteByPrimaryKey(id);
        if (count!=1){
            throw new LyException(ExceptionEnum.SPEC_PARAM_DELETE_ERROR);
        }
    }

    public List<SpecGroup> queryListByCid(Long cid) {
        //查询规格组
        List<SpecGroup> specGroups = queryGroupByCid(cid);
        //查询当前分类下的参数
        List<SpecParam> specParams = queryParams(null, cid, null);
        //先把规格参数变成map，map的key是规格组id，map的值是组下的所有参数
        Map<Long,List<SpecParam>> map=new HashMap<>();
        specParams.forEach(specParam -> {
            if (!map.containsKey(specParam.getGroupId())){
                //这个组id在map中不存在，新增一个list
                map.put(specParam.getGroupId(),new ArrayList<>());
            }
            map.get(specParam.getGroupId()).add(specParam);
        });
        //填充param到group
        specGroups.forEach(specGroup -> {
            specGroup.setParams(map.get(specGroup.getId()));
        });
        return specGroups;
    }
}
