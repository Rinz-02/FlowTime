package com.apk.flowtime.entity;

import com.apk.flowtime.common.entity.BaseEntity;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Entity
@Table(name = "users")
@Getter
@AllArgsConstructor
public class User extends BaseEntity
{
	@Column(name = "email", nullable = false)
	private String email;
	
	@Column(name = "password_hash")
	private String passwordHash;
	
	@Column(name = "first_name", nullable = false)
	private String firstName;
	
	@Column(name = "last_name", nullable = false)
	private String lastName;
	
	@Column(name = "is_active", nullable = false)
	private boolean isActive = true;
	
	@Column(name = "email_verified_at", nullable = false)
	private Instant emailVerifiedAt;
	
	//Encapsulation purpose
	protected User()
	{
		
	}
	
	public void updateEmail(String email)
	{
		this.email = email;
	}
	
	public void changePasswotd(String passwordHash)
	{
		this.passwordHash = passwordHash;
	}
	
	public void updateName(String firstName, String lastName)
	{
		this.firstName = firstName;
		this.lastName = lastName;
	}
	
	public void deactivate()
	{
		this.isActive = false;
	}
	
	public void activate()
	{
		this.isActive = true;
	}
	
	public void verifyEmail()
	{
		this.emailVerifiedAt = Instant.now();
	}
	
}
