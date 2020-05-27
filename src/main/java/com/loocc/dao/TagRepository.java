package com.loocc.dao;



import com.loocc.domain.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
public interface TagRepository extends JpaRepository<Tag,Long> {
    Tag findByName(String name);

    @Query(value = "select t from Tag t")
    List<Tag> findTop(Pageable pageable);


    //默认的批量插入方法
    default int  insertTags(List<Tag> tagList,JdbcTemplate jdbcTemplate){
        String sql = "insert into t_tag(name) values(?)";
        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement preparedStatement, int i) throws SQLException {
                String name  = tagList.get(i).getName();
                preparedStatement.setString(1,name);
            }

            @Override
            public int getBatchSize() {
                return tagList.size();
            }
        });
        return 0;
    };

}
