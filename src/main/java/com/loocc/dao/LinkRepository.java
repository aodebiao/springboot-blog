package com.loocc.dao;

import com.loocc.domain.Link;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface LinkRepository extends JpaRepository<Link,Long>, JpaSpecificationExecutor<Link> {
    @Query(value = "select l from Link l")
    Page<Link> list(Pageable pageable);
}
