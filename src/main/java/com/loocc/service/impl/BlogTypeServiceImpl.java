package com.loocc.service.impl;

import com.loocc.NotFoundException;

import com.loocc.dao.BlogTypeRepository;
import com.loocc.domain.BlogType;
import com.loocc.service.BlogTypeService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class BlogTypeServiceImpl implements BlogTypeService {
    @Autowired
    private BlogTypeRepository typeRepository;

    @Transactional
    @Override
    public BlogType saveType(BlogType type) {
        return typeRepository.save(type);
    }

    @Transactional
    @Override
    public BlogType getType(Long id) {
        return typeRepository.getOne(id);
    }

    @Override
    public BlogType getTypeByName(String name) {
        return typeRepository.findByName(name);
    }

    @Transactional
    @Override
    public Page<BlogType> listType(Pageable pageable) {
        return typeRepository.findAll(pageable);
    }

    @Override
    public List<BlogType> listType() {
        return typeRepository.findAll();
    }


    @Override
    public List<BlogType> listTypeTop(Integer size) {
        Sort sort = Sort.by(Sort.Direction.DESC,"blogs.size");
        Pageable pageable = PageRequest.of(0,size,sort);
        return typeRepository.findTop(pageable);
    }


    @Transactional
    @Override
    public BlogType updateType(Long id, BlogType type) {
        BlogType t = typeRepository.getOne(id);
        if (t == null) {
            throw new NotFoundException("不存在该类型");
        }
        BeanUtils.copyProperties(type,t);
        return typeRepository.save(t);
    }



    @Transactional
    @Override
    public void deleteType(Long id) {
        typeRepository.deleteById(id);
    }

    @Override
    public BlogType findById(Long id) {
        Optional<BlogType> byId = typeRepository.findById(id);
        return byId.get();
    }

    @Override
    public int saveAll(List<BlogType> blogTypeList, JdbcTemplate jdbcTemplate) {
        return typeRepository.insertTypes(blogTypeList,jdbcTemplate);
    }

}
