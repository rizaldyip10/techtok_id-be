package com.rizaldyip.techtokidserver.entities;

import com.rizaldyip.techtokidserver.dtos.response.RoleResDto;
import com.rizaldyip.techtokidserver.dtos.response.UserResDto;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Data
@Table(name = "users", schema = "techtok_id")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "phone")
    private int phone;

    @Column(name = "profile_img")
    private String profileImg;

    @Column(name = "is_verified", nullable = false)
    private boolean isVerified;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles", schema = "techtok_id",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private List<Role> roles;

    @Column(name = "created_at", nullable = false)
    @CreationTimestamp
    private Timestamp createdAt;

    @Column(name = "updated_at", nullable = false)
    @UpdateTimestamp
    private Timestamp updatedAt;

    public UserResDto toUserResDto() {
        UserResDto userResDto = new UserResDto();
        userResDto.setId(this.id);
        userResDto.setName(this.name);
        userResDto.setEmail(this.email);
        userResDto.setPhone(this.phone);
        userResDto.setProfileImg(this.profileImg);
        userResDto.setRoles(this.roles.stream().map(Role::toRoleResDto).collect(Collectors.toCollection(ArrayList::new)));
        return userResDto;
    }

}
