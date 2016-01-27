package com.ots.service.sys;

import java.util.List;

import com.ots.dto.base.SessionInfo;
import com.ots.dto.base.Tree;
import com.ots.dto.sys.Resource;

public interface ResourceServiceI {

	public List<Resource> treeGrid();

	public void add(Resource resource);

	public void delete(Long id);

	public void edit(Resource resource);

	public Resource get(Long id);

	public List<Tree> tree(SessionInfo sessionInfo);

	public List<Tree> allTree(boolean flag);

	public List<String> resourceAllList();

}
