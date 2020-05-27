package com.loocc.service;

import com.loocc.domain.Link;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface LinkService {
    void save(Link link);
    void delete(Long id);
    Page<Link> list(Pageable pageable);

    Link getById(Long id);


}
