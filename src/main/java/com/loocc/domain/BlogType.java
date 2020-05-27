package com.loocc.domain;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;


@Table(name="t_type")
@Entity
@Getter
@Setter
@JsonIgnoreProperties(value = {"blogs"})
public class BlogType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    @Setter
    private Long id;
    //简单的后端校验
    @NotBlank(message = "分类名称不能为空！")
    @Getter@Setter
    private String name;
    @OneToMany(mappedBy = "type")
    @Getter@Setter
    private List<Blog> blogs = new ArrayList<>();

    public BlogType() {
    }
    public BlogType(String name){
        this.name = name;
    }

    @Override
    public String toString() {
        return "BlogType{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", blogs=" + blogs +
                '}';
    }




}
