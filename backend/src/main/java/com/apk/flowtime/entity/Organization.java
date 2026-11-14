package com.apk.flowtime.entity;

import com.apk.flowtime.common.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "organizations")
@Getter
@AllArgsConstructor
public class Organization extends BaseEntity 
{
	@Column(name = "name", nullable = false)
	private String name;
	
	@Column(name = "slug", nullable = false)
	private String slug;
	
	@Column(name = "timezone", nullable = false)
	private String timezone;
	
	//Encapsulation purpose
	protected Organization()
	{
		
	}
	public void updateName(String name) {
        this.name = name;
    }

    public void updateTimezone(String timezone) {
        this.timezone = timezone;
    }
}
