package com.apk.flowtime.repository;

import java.util.Optional;
import java.util.UUID;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.apk.flowtime.entity.Membership;

@Repository
public interface MembershipRepository extends JpaRepository<Membership,UUID> 
{
	Optional<Membership> findByOrganization_IdAndUser_Id(
			UUID organizationId,
			UUID userId);
	
	boolean existsByOrganization_IdAndUser_Id(
			UUID organizationId,
			UUID userId
			);
	
	List<Membership> findAllByOrganization_Id(UUID organizationId);
	
	List<Membership> findAllByUser_Id(UUID UserId);
	
}
