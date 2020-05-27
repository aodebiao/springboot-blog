package com.loocc.service.impl;

import com.loocc.dao.LinkRepository;
import com.loocc.domain.Link;
import com.loocc.service.LinkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class LinkServiceImpl implements LinkService {
    @Autowired
    private LinkRepository linkRepository;
    @Override
    public void save(Link link) {
        linkRepository.save(link);
    }



    @Override
    public void delete(Long id) {
        linkRepository.deleteById(id);
    }

    @Override
    public Page<Link> list(Pageable pageable) {
        return linkRepository.list(pageable);
    }

    @Override
    public Link getById(Long id) {
        return linkRepository.findById(id).get();
    }
}
