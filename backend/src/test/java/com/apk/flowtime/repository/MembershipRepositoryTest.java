package com.apk.flowtime.repository;

import java.util.UUID;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import jakarta.persistence.EntityManager;

import com.apk.flowtime.config.JpaConfig;
import com.apk.flowtime.entity.*;

@DataJpaTest
@AutoConfigureTestDatabase(
		replace =  AutoConfigureTestDatabase.Replace.NONE
)
@Import(JpaConfig.class)
public class MembershipRepositoryTest 
{
	@Autowired
	private MembershipRepository membershipRepository;
	
	@Autowired
	private EntityManager entityManager;
	
	private Organization organizationA;
	private Organization organizationB;
	
	private User userA;
	private User userB;
	
	private Role memberRole;
	
	@BeforeEach
	void setup()
	{
		//Create Organizations
		organizationA = new Organization(
				"FlowTime Test A",
				"flowtime-test-a-" + UUID.randomUUID(),
				"Asia/Malaysia"
			);
		
		organizationB = new Organization(
				"FlowTime Test B",
				"flowtime-test-b-" + UUID.randomUUID(),
				"Asia/Malaysia"
			);
		
		entityManager.persist(organizationA);
		entityManager.persist(organizationB);
		
		//Create Users
		userA = new User(
				"user-a-" + UUID.randomUUID() + "@test.com",
				"test-password-hash",
				"User",
				"A"
			);
		
		userB = new User(
				"user-b-" + UUID.randomUUID() + "@test.com",
				"test-password-hash",
				"User",
				"A"
			);
		
		entityManager.persist(userA);
		entityManager.persist(userB);
		
		entityManager.flush();
		
		//Retrieve existing MemberRole by FlyWay V1
		memberRole = entityManager.createQuery(
				"SELECT r FROM Role r WHERE r.code = :code",
				Role.class
			)
			.setParameter("code", "MEMBER")
			.getSingleResult();
		
		//Create Memberships
		Membership membership1 = new Membership(
				organizationA,
				userA,
				memberRole
			);
		
		Membership membership2 = new Membership(
				organizationA,
				userB,
				memberRole
			);
		
		Membership membership3 = new Membership(
				organizationB,
				userA,
				memberRole
			);
		
		membershipRepository.saveAllAndFlush(
				List.of(membership1, membership2, membership3)
			);
		
	}
	
	//FindMembershipBy organization_id and user_id
	@Test
	@DisplayName(value = "Find By organizationId and userId")
	void shouldFindByMembershipByOrganizationIdAndUserId() 
	{
		var result = membershipRepository
				.findByOrganization_IdAndUser_Id(
						organizationA.getId(), 
						userA.getId()
					);
		assertTrue(result.isPresent());
		
		Membership membership = result.orElseThrow();
		
		assertNotNull(membership.getId());
		
		assertEquals(
				organizationA.getId(),
				membership.getOrganization().getId()
			);
		
		assertEquals(
				userA.getId(),
				membership.getUser().getId()
			);
		
	}
	
	//Check Membership Existence By organization_id and user_id
	@Test
	@DisplayName(value = "Check Membership Existence")
	void shouldCheckMembershipExistence()
	{
		boolean exists = membershipRepository
				.existsByOrganization_IdAndUser_Id(
						organizationA.getId(),
						userA.getId()
					);
		
		boolean doesNotExist = membershipRepository
				.existsByOrganization_IdAndUser_Id(
						organizationB.getId(),
						userB.getId()
					);
		
		assertTrue(exists);
		assertFalse(doesNotExist);
		
	}
	
	//Find All Membership by organization_id
	@Test
	@DisplayName(value = "Find All By organizationId")
	void shouldFindAllMembershipByOrganization()
	{
		List<Membership> memberships = membershipRepository
				.findAllByOrganization_Id(
						organizationA.getId()
					);
		
		assertEquals(2, memberships.size());
		
		assertTrue(
				memberships.stream().allMatch(
						membership -> membership.getOrganization()
						.getId().equals(organizationA.getId())
					)
			);
	}
	
	//Find All Membership by user_id
	@Test
	@DisplayName(value = "Find All By userId")
	void shouldFindAllMembershipByUserId()
	{
		List<Membership> memberships = membershipRepository
				.findAllByUser_Id(
						userA.getId()
					);
		assertEquals(2, memberships.size());
		
		assertTrue(
				memberships.stream().allMatch(
						membership -> membership.getUser()
						.getId().equals(userA.getId())
					)
			);
	}
	
}
