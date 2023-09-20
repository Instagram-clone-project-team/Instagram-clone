package com.project.Instagram.domain.member.entity;

import com.project.Instagram.global.entity.BaseTimeEntity;
import lombok.*;

import javax.persistence.*;

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
    @Enumerated(EnumType.STRING)
    private MemberRole role;

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
    public Member(String username, String name, String password, String email) {
        this.username = username;
        this.name = name;
        this.password = password;
        this.email = email;
        this.role = MemberRole.ROLE_USER;
        this.gender = Gender.PRIVATE;
    }

    public void setRestoreMembership(String username, String encryptedPassword, String name) {
        this.username = username;
        this.password = encryptedPassword;
        this.name = name;
    }
}
