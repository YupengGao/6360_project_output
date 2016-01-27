package com.ots.service.oil;

import java.util.List;

import com.ots.domain.oil.OilSell;
import com.ots.dto.base.PageFilter;
import com.ots.dto.oil.StatisticsInfo;
import com.ots.dto.oil.TransactionSearch;

public interface OilSellServiceI {
	public List<OilSell> dataGrid(TransactionSearch search, PageFilter ph);

	public Long count(TransactionSearch search, final PageFilter ph);
	
	public List<StatisticsInfo> dataGrid4Statistics(TransactionSearch search, PageFilter ph);

	public Long count4Statistics(TransactionSearch search, final PageFilter ph);

	public void add(OilSell oilSell);

	public void delete(Long id);

	public void pay(Long id);
	
	public void cancel(Long id);

	public void edit(OilSell oilSell);

	public boolean isLastRecord(Long id);

	public OilSell get(Long id);
}
