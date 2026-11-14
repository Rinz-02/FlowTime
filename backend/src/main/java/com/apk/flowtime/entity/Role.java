package com.apk.flowtime.entity;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

@Entity
@Table(name = "roles")
@Getter
public class Role 
{

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "id", nullable = false, updatable = false)
	private UUID id;
	
	@Column(name = "code", nullable = false, length = 30)
	private String code;
	
	@Column(name = "name", nullable = false, length = 100)
	private String name;
	
	@Column(name = "description", columnDefinition = "text")
	private String description;
	
	protected Role()
	{
		
	}
	
	public Role(String code, String name, String description) {
        this.code = code;
        this.name = name;
        this.description = description;
    }
}
