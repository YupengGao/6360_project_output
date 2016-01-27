package com.ots.framework.constant;

import java.util.HashMap;
import java.util.Map;

public class GlobalConstant {

	public static final String SESSION_INFO = "sessionInfo";

	public static final Integer ENABLE = 0;
	public static final Integer DISABLE = 1;

	public static final Integer DEFAULT = 0;
	public static final Integer NOT_DEFAULT = 1;

	@SuppressWarnings("serial")
	public static final Map<String, String> sexlist = new HashMap<String, String>() {
		{
			put("0", "male");
			put("1", "female");
		}
	};
	@SuppressWarnings("serial")
	public static final Map<String, String> statelist = new HashMap<String, String>() {
		{
			put("0", "in use");
			put("1", "stop");
		}
	};

}
