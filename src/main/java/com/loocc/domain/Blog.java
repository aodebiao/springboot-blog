package com.loocc.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 博客实体
 * indexName类似数据库名字，type类似表名
 *
 */
@Entity
@Table(name="t_blog")
@Getter@Setter
@JsonIgnoreProperties(value = {"user"})
public class Blog {
    @Id
    //@org.springframework.data.annotation.Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Basic(fetch = FetchType.LAZY)
    @Lob//表示大对象
    private String content;
    //首图
    private String firstPicture;

    //博客来源
    private String flag;
    private Integer views;
    //赞赏功能是否开启
    private boolean appreciation;
    //转载声明是否开启
    private boolean shareStatement;
    //是否开启评论功能
    private boolean commentabled;
    //是否发布
    private boolean published;
    //是否推荐
    private boolean recommend;
    @Transient//不会映射到数据库中
    private String tagIds;

    private String description;
    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime;
    @Temporal(TemporalType.TIMESTAMP)
    private Date updateTime;

    @ManyToOne
    private BlogType type;

//级联关系,添加博客的时候，输入全新标签，也会在tag表中插入新的
    @ManyToMany(cascade = {CascadeType.PERSIST})
    private List<Tag> tags = new ArrayList<>();

    @ManyToOne
    private User user;

    public void init() {
        this.tagIds = tagsToIds(this.getTags());
    }
    @OneToMany(mappedBy = "blog")
    private List<Comment> comments = new ArrayList<>();
    private String tagsToIds(List<Tag> tags) {
        if (!tags.isEmpty()) {
            StringBuffer ids = new StringBuffer();
            boolean flag = false;
            for (Tag tag : tags) {
                if (flag) {
                    ids.append(",");
                } else {
                    flag = true;
                }
                ids.append(tag.getId());
            }
            return ids.toString();
        } else {
            return tagIds;
        }
    }

    @Override
    public String toString() {
        return "Blog{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", firstPicture='" + firstPicture + '\'' +
                ", flag='" + flag + '\'' +
                ", views=" + views +
                ", appreciation=" + appreciation +
                ", shareStatement=" + shareStatement +
                ", commentabled=" + commentabled +
                ", published=" + published +
                ", recommend=" + recommend +
                ", tagIds='" + tagIds + '\'' +
                ", description='" + description + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}
