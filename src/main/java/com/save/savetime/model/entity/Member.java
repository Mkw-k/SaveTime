package com.save.savetime.model.entity;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@EqualsAndHashCode(of = "idx")
@ToString(exclude = {"role"})
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Member {
    //XXX 디폴트 생성전략이 테스트 조건에 알맞지 않음
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "idx")
    private long idx;

    @Column(unique = true, name = "email")
    private String email;

    @Column(name = "pw")
    private String password;

    @Column(name = "name")
    private String name;

    @Column(name = "phone")
    private String phone;

    @Column(name = "remote_addr")
    private String remoteAddr;

    @Column(name = "del")
    private String del;

    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Role> role = new HashSet<>();

    @PrePersist
    public void prePersist(){
        this.del = this.del == null ? "N" : this.del;
    }

    // 역할 정보를 가져오는 메서드
    public List<GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();

        //LAZY Load이기 때문에 get해줘야함
        Set<Role> userRoleSet = this.getRole();

        // Member 객체가 가지는 모든 역할을 가져와서 SimpleGrantedAuthority로 변환하여 authorities 리스트에 추가
        for (Role role : userRoleSet) {
            authorities.add(new SimpleGrantedAuthority(role.getRoleName()));
        }
        return authorities;
    }
}