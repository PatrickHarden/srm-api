package com.scholastic.srmapi.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "user_login_session")
public class UserLoginSession implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long sourceId;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "login_date", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP", insertable = false,
            updatable = false)
    private Date loginDate;

    @Column(name = "last_update_date", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP", insertable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastUpdateDate;

}