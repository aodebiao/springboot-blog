package com.loocc.domain;

import lombok.*;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;

/**
 * 单独一个实体类，方便es保存，后面搜索需要
 */
@Data
@Document(indexName = "myblog",type = "blog")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class EsBlog {

    @Field(store = true,index = false,type = FieldType.Long)
    private Long id;


    @Field(store = true,index = true,analyzer = "ik_max_word",searchAnalyzer = "ik_smart",type = FieldType.Text)
    private String title;

    @Field(store = true,index = true,analyzer = "ik_max_word",searchAnalyzer = "ik_smart",type = FieldType.Text)
    private String content;

    @Field(store = true,index = false,type = FieldType.Text)
    private String firstPicture;

    @Field(store = true,index = false,type = FieldType.Integer)
    private Integer views;

    @Field(store = true,index = true,analyzer = "ik_max_word",searchAnalyzer = "ik_smart",type = FieldType.Text)
    private String description;

    @Field(store = true,index = false,type = FieldType.Text)
    private String type;

    @Field(store = true,index = false,type = FieldType.Text)
    private String userName;

    @Field(store = true,index = false,type = FieldType.Date)
    private Date createTime;

    @Field(store = true,index = false,type = FieldType.Text)
    private String avatar;

    @Field(store = true,index = false,type = FieldType.Text)
    private String nickName;




}
