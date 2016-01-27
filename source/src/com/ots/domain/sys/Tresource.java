package com.ots.domain.sys;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.validator.constraints.NotBlank;

import com.ots.domain.base.IdEntity;

@Entity
@Table(name = "sys_resource")
@DynamicInsert(true)
@DynamicUpdate(true)
public class Tresource extends IdEntity implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	private String createdatetime;
	private String name;
	private String url;
	private String description;
	private String icon;
	private Integer seq;
	private Integer resourcetype;
	private Tresource resource;
	private Integer state;
	private Set<Trole> roles = new HashSet<Trole>(0);
	private Set<Tresource> resources = new HashSet<Tresource>(0);

	public Tresource() {
	}

	public Tresource(Long id, String createdatetime, String name, String url,
			String description, String icon, Integer seq, Integer resourcetype,
			Tresource resource, Integer state) {
		super();
		this.id = id;
		this.createdatetime = createdatetime;
		this.name = name;
		this.url = url;
		this.description = description;
		this.icon = icon;
		this.seq = seq;
		this.resourcetype = resourcetype;
		this.resource = resource;
		this.state = state;
	}

	// @Temporal(TemporalType.TIMESTAMP)
	// @Column(name = "CREATEDATETIME", length = 19)
	public String getCreatedatetime() {
		return createdatetime;
	}

	public void setCreatedatetime(String createdatetime) {
		this.createdatetime = createdatetime;
	}

	@NotBlank
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	public Integer getResourcetype() {
		return resourcetype;
	}

	public void setResourcetype(Integer resourcetype) {
		this.resourcetype = resourcetype;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "pid")
	public Tresource getResource() {
		return resource;
	}

	public void setResource(Tresource resource) {
		this.resource = resource;
	}

	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "sys_role_resource", joinColumns = { @JoinColumn(name = "resource_id", nullable = false, updatable = false) }, inverseJoinColumns = { @JoinColumn(name = "role_id", nullable = false, updatable = false) })
	@OrderBy("id ASC")
	public Set<Trole> getRoles() {
		return roles;
	}

	public void setRoles(Set<Trole> roles) {
		this.roles = roles;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "resource")
	public Set<Tresource> getResources() {
		return resources;
	}

	public void setResources(Set<Tresource> resources) {
		this.resources = resources;
	}

}