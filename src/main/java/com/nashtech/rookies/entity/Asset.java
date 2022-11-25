package com.nashtech.rookies.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

import javax.persistence.*;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Asset {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;
	private String code;
	private String specification;
	private String location;
	private String state;
	private Date installedDate;

	@ManyToOne
	private Category category;

	@ManyToOne
	private Users users;

	public Asset(String name, String code, String specification, String location, String state, Category category, Users users) {
		this.name = name;
		this.code = code;
		this.specification = specification;
		this.location = location;
		this.state = state;
		this.category = category;
		this.users = users;
	}
}
