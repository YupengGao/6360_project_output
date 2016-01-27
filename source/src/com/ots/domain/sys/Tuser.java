package com.ots.domain.sys;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.validator.constraints.NotBlank;

import com.ots.domain.base.IdEntity;

@Entity
@Table(name = "sys_user")
@DynamicInsert(true)
@DynamicUpdate(true)
public class Tuser extends IdEntity implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	private String loginname;
	private String password;
	private String name;
	private Integer sex;
	private String createdatetime;
	private Integer state;
	private Set<Trole> roles = new HashSet<Trole>(0);

	public Tuser() {
		super();
	}

	public Tuser(String loginname, String password, String name, Integer sex,
			Integer age, String createdatetime, Integer usertype,
			Integer isdefault, Integer state) {
		super();
		this.loginname = loginname;
		this.password = password;
		this.name = name;
		this.sex = sex;
		this.createdatetime = createdatetime;
		this.state = state;
	}

	@NotBlank
	public String getLoginname() {
		return loginname;
	}

	public void setLoginname(String loginname) {
		this.loginname = loginname;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@NotBlank
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getSex() {
		return sex;
	}

	public void setSex(Integer sex) {
		this.sex = sex;
	}

	// @Temporal(TemporalType.TIMESTAMP)
	// @Column(name = "CREATEDATETIME", length = 19)
	public String getCreatedatetime() {
		return createdatetime;
	}

	public void setCreatedatetime(String createdatetime) {
		this.createdatetime = createdatetime;
	}

	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "sys_user_role", joinColumns = { @JoinColumn(name = "user_id", nullable = false, updatable = false) }, inverseJoinColumns = { @JoinColumn(name = "role_id", nullable = false, updatable = false) })
	public Set<Trole> getRoles() {
		return roles;
	}

	public void setRoles(Set<Trole> roles) {
		this.roles = roles;
	}

}