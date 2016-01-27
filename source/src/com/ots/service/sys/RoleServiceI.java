package com.ots.service.sys;

import java.util.List;

import com.ots.dto.base.PageFilter;
import com.ots.dto.base.Tree;
import com.ots.dto.sys.Role;

public interface RoleServiceI {

	public List<Role> dataGrid(Role role, PageFilter ph);

	public Long count(Role role, PageFilter ph);

	public void add(Role role);

	public void delete(Long id);

	public void edit(Role role);

	public Role get(Long id);

	public void grant(Role role);

	public List<Tree> tree();

}
