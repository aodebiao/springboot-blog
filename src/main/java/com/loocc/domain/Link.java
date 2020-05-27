package com.loocc.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name="t_link")
@Getter
@Setter
public class Link {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String linkName;

    private String linkUrl;
    @Temporal(TemporalType.TIMESTAMP)
    private Date addDate;
}
