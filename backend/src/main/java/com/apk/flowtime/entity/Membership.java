package com.apk.flowtime.entity;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;

@Entity
@Table(name = "memberships")
@Getter
public class Membership 
{
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "id", nullable = false)
	private UUID id;
	
	@ManyToOne(fetch = FetchType.LAZY, optional = false) //to avoid N(1)+ query
	@JoinColumn(name = "organization_id", nullable = false)
	private Organization organization;
	
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;
	
	@ManyToOne(fetch =  FetchType.LAZY, optional = false)
	@JoinColumn(name = "role_id", nullable = false)
	private Role role;
	
	@Column(name = "joined_at", nullable = false, updatable = false)
	private Instant joinedAt;
	
	protected Membership()
	{
		
	}
	
	public Membership( 
			Organization organization,
			User user,
			Role role) 
	{
		this.organization = organization;
		this.user = user;
		this.role = role;
	}
	
	@PrePersist
	protected void onCreate()
	{
		if(joinedAt == null)
		{
			joinedAt = Instant.now();
		}
	}
}
