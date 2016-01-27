package com.ots.service.oil;

import java.util.List;

import com.ots.domain.oil.OilBuy;
import com.ots.dto.base.PageFilter;
import com.ots.dto.oil.TransactionSearch;

public interface OilBuyServiceI {
	public List<OilBuy> dataGrid(TransactionSearch search, PageFilter ph);

	public Long count(TransactionSearch search, final PageFilter ph);

	public void add(OilBuy oilBuy);

	public void delete(Long id);

	public void pay(Long id);
	
	public void cancel(Long id);

	public void edit(OilBuy oilBuy);

	public boolean isLastRecord(Long id);

	public OilBuy get(Long id);
}
