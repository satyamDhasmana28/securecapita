package com.satyam.securecapita.user.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity(name = "m_roles")
@Getter
@Setter
@NoArgsConstructor
public class Role implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Long id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "enabled" ,columnDefinition = "boolean default true")
    private boolean isEnabled;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(
            name = "m_role_permissions",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "permission_id")
    )
    private Set<Permission> permissions = new HashSet<>();

    public Role(String name, String description, boolean isEnabled) {
        this.name = "ROLE_"+name;
        this.description = description;
        this.isEnabled = isEnabled;
    }

    public boolean addPermission(final Permission permission){
        return this.permissions.add(permission);
    }

    public boolean removePermission(final Permission permission){
        return this.permissions.remove(permission);
    }

    public void clearAllPermission(){
         this.permissions.clear();
    }

}
