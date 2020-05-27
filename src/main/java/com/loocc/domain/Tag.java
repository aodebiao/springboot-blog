package com.loocc.domain;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

/**
 * 标签类
 */

@Entity
@Table(name="t_tag")
@JsonIgnoreProperties(value = {"blogs"})
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    @Setter
    private Long id;
    @NotBlank(message = "标签名字不能为空！")
    @Getter@Setter
    private String name;

    //关系被维护方
    @ManyToMany(mappedBy = "tags")
    @Getter@Setter
    private List<Blog> blogs = new ArrayList<>();

    public Tag(@NotBlank(message = "标签名字不能为空！") String name) {
        this.name = name;
    }
    public Tag(){}
}
