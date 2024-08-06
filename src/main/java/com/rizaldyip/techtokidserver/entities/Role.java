package com.rizaldyip.techtokidserver.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rizaldyip.techtokidserver.dtos.response.RoleResDto;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;
import java.util.List;

@Entity
@Data
@Table(name = "roles", schema = "techtok_id")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @JsonIgnore
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles", schema = "techtok_id",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private List<User> users;

    @CreationTimestamp
    private Timestamp createdAt;

    @UpdateTimestamp
    private Timestamp updatedAt;

    public RoleResDto toRoleResDto() {
        RoleResDto roleResDto = new RoleResDto();
        roleResDto.setId(this.id);
        roleResDto.setName(this.name);
        return roleResDto;
    }
}
