package com.Movies.Cinema.City.repository;

import com.Movies.Cinema.City.model.Role;
import com.Movies.Cinema.City.model.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByRoleType(RoleType roleType);
}