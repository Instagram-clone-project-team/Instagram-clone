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
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "member_roles", joinColumns = @JoinColumn(name = "member_id"))
    @Enumerated(EnumType.STRING)
    private Set<MemberRole> roles = new HashSet<>();

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

    @Column(name = "member_image", nullable = false)
    private String image = "https://instagram-clone-luee-bucket.s3.ap-northeast-2.amazonaws.com/Profile/%EA%B8%B0%EB%B3%B8%EC%9D%B4%EB%AF%B8%EC%A7%80.jpg";

    public void setEncryptedPassword(String encryptedPassword) {
        this.password = encryptedPassword;
    }

    @Builder
    public Member(String username, String name, String password, String email, Set<MemberRole> roles) {
        this.username = username;
        this.name = name;
        this.password = password;
        this.email = email;
        this.gender = Gender.PRIVATE;
        this.roles = roles;
    }

    public void setRestoreMembership(String username, String encryptedPassword, String name) {
        this.username = username;
        this.password = encryptedPassword;
        this.name = name;
    }

    public void updateUsername(String username) {
        this.username = username;
    }

    public void updateName(String name) {
        this.name = name;
    }

    public void updateLink(String link) {
        this.link = link;
    }

    public void updateIntroduce(String introduce) {
        this.introduce = introduce;
    }

    public void updateEmail(String email) {
        this.email = email;
    }

    public void updatePhone(String phone) {
        this.phone = phone;
    }

    public void updateGender(Gender gender) {
        this.gender = gender;
    }

}
