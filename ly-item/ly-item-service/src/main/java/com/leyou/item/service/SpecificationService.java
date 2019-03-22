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

import java.util.List;

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

    public List<SpecParam> queryParamByGId(Long gid) {
        SpecParam specParam=new SpecParam();
        specParam.setGroupId(gid);
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
}
