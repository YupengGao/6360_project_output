package com.ots.service.sys;

import java.util.List;

import com.ots.domain.oil.Client;
import com.ots.domain.sys.Tresource;
import com.ots.dto.base.PageFilter;
import com.ots.dto.base.SessionInfo;
import com.ots.dto.base.Tree;
import com.ots.dto.sys.JsonClient;
import com.ots.dto.sys.User;

public interface ClientServiceI {

	public List<Client> dataGrid(Client client, PageFilter ph);

	public Long count(Client client, PageFilter ph);

	public void add(Client client);

	public void delete(Long id);

	public void edit(Client user);

	public Client get(Long id);
	public JsonClient getClientSimpleInfo(Long id);

	public User login(User user);

	public List<String> resourceList(Long id);

	public boolean editUserPwd(SessionInfo sessionInfo, String oldPwd,
			String pwd);

	public Long getByLoginName(Client client);

	public Long getByLoginNameAndId(Client client);

	public Long getClientsByTraderId(Long traderId);

	public List<Tresource> resourceTree(final int type, final Long clientId);
	
	public List<Tree> tree(SessionInfo sessionInfo);
	public Long getTransactionsByClientId(Long clientId);

}
