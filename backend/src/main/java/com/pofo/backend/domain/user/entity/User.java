package com.pofo.backend.domain.user.entity;

import com.pofo.backend.common.jpa.entity.BaseEntity;
import com.pofo.backend.domain.project.entity.Project;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name="users")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
public class User extends BaseEntity {

    String name;
    String email;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Project> projects = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (o instanceof User){
            return Objects.equals(((User) o).getId(), this.getId());
        }
        return super.equals(o);
    }
}
