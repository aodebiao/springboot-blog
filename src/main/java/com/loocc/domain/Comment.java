package com.loocc.domain;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "t_comment")
@AllArgsConstructor
@NoArgsConstructor
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    @Setter
    private Long id;
    @ManyToOne
    @Getter@Setter
    private User user;
    @Getter@Setter
    private String content;

    @Temporal(TemporalType.TIMESTAMP)
    @Getter@Setter
    private Date createTime;
    //评论回复
    @ManyToOne
    @Getter@Setter
    private Comment parentComment;

    @Getter@Setter
    boolean adminComment;


    //类似楼中楼
    @OneToMany(mappedBy = "parentComment")
    @Getter@Setter
    private List<Comment> replyComments = new ArrayList<>();

    @ManyToOne()
    @Getter@Setter
    private Blog blog;


}
