package com.apk.flowtime.repository;


import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.apk.flowtime.entity.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role,UUID>
{
	Optional<Role> findByCode(String Code);
	
	boolean existsByCode(String Code);
}
