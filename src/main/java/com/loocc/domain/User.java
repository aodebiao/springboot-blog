package com.loocc.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
import javax.validation.constraints.Email;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Entity
@Table(name = "t_user")
@Getter
@Setter
@JsonIgnoreProperties(value = {"comments","blogs"})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;

    private String nickname;
    private String username;
    private String password;
    @Transient
    private String check_password;

    @Email(message = "请输入正确的邮箱格式！")
    private String email;

    private String avatar;

    private boolean status;

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    @ManyToOne
    private Role role;


    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime;

    @Transient
    private String verificationCode;//注册时的验证码

    private Integer gender;
    //指定数据库中的类型为timestamp
    @Temporal(TemporalType.TIMESTAMP)
    private Date updateTime;

    @OneToMany(mappedBy = "user")
    private List<Blog> blogs = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Comment> comments = new ArrayList<>();

    private String openId;
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", nickname='" + nickname + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", check_password='" + check_password + '\'' +
                ", email='" + email + '\'' +
                ", avatar='" + avatar + '\'' +
                ", status=" + status +
                ", createTime=" + createTime +
                ", verificationCode='" + verificationCode + '\'' +
                ", gender=" + gender +
                ", updateTime=" + updateTime +
                ", blogs=" + blogs +
                ", comments=" + comments +
                '}';
    }
}
