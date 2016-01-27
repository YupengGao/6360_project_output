package com.ots.service.oil;

import java.util.List;

import com.ots.dto.base.PageFilter;
import com.ots.dto.oil.StatisticsInfo;
import com.ots.dto.oil.TransactionSearch;

public interface StatisticsServiceI {

	public List<StatisticsInfo> dataGrid(TransactionSearch search,
			PageFilter ph);

	public Long count(TransactionSearch search, final PageFilter ph);
}
