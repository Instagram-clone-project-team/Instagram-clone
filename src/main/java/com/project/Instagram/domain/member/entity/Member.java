package com.project.Instagram.domain.member.entity;

import com.project.Instagram.global.entity.BaseTimeEntity;

import lombok.*;

import javax.persistence.*;
import java.util.*;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "members")
public class Member extends BaseTimeEntity {

    @Id
    @Column(name = "member_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "member_username", nullable = false, length = 20, unique = true)
    private String username;

    @Column(name = "member_role")
    @CollectionTable(name = "member_roles", joinColumns = @JoinColumn(name = "member_id"))
    @Enumerated(EnumType.STRING)
    @ElementCollection(fetch = FetchType.EAGER)
    private List<MemberRole> roles = new ArrayList<>();

    @Column(name = "member_password", nullable = false)
    private String password;

    @Column(name = "member_name", nullable = false, length = 20)
    private String name;

    @Column(name = "member_link")
    private String link;

    @Lob
    @Column(name = "member_introduce")
    private String introduce;

    @Column(name = "member_email", unique = true)
    private String email;

    @Column(name = "member_phone")
    private String phone;

    @Column(name = "member_gender")
    @Enumerated(EnumType.STRING)
    private Gender gender;

    public void setEncryptedPassword(String encryptedPassword) {
        this.password = encryptedPassword;
    }

    @Builder
    public Member(String username, String name, String password, String email, List<MemberRole> roles) {
        this.username = username;
        this.name = name;
        this.password = password;
        this.email = email;
        this.roles = roles;
        this.gender = Gender.PRIVATE;
    }

    public void setRestoreMembership(String username, String encryptedPassword, String name) {
        this.username = username;
        this.password = encryptedPassword;
        this.name = name;
    }

    public void updateUsername(String username){this.username = username;}
    public void updateName(String name){this.name = name;}
    public void updateLink(String link){this.link = link;}
    public void updateIntroduce(String introduce){this.introduce = introduce;}
    public void updateEmail(String email){this.email = email;}
    public void updatePhone(String phone){this.phone = phone;}
    public void updateGender(Gender gender){this.gender = gender;}

}
