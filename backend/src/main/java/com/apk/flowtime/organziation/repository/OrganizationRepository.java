package com.apk.flowtime.organziation.repository;

import com.apk.flowtime.organization.entity.*;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OrganizationRepository 
	extends JpaRepository<Organization, UUID>
{
	
}
