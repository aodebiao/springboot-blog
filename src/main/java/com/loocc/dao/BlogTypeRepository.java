package com.loocc.dao;


import com.loocc.domain.BlogType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public interface BlogTypeRepository extends JpaRepository<BlogType, Long>{
    BlogType findByName(String name);

    @Query(value = "select t from BlogType t")
    List<BlogType> findTop(Pageable pageable);
    default int  insertTypes(List<BlogType> typeList, JdbcTemplate jdbcTemplate){
        String sql = "insert into t_type(name) values(?)";
        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement preparedStatement, int i) throws SQLException {
                String name  = typeList.get(i).getName();
                preparedStatement.setString(1,name);
            }

            @Override
            public int getBatchSize() {
                return typeList.size();
            }
        });
        return 0;
    };
}
