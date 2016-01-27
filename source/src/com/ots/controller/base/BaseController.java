package com.ots.controller.base;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ots.utils.StringEscapeEditor;

@Controller
@RequestMapping("/base")
public class BaseController {

	protected int page = 1;// current page
	protected int rows = 10;// row number on each page
	protected String sort;// order field
	protected String order = "asc";// asc/desc

	protected String ids;// key collection, separated by comma

	@InitBinder
	public void initBinder(ServletRequestDataBinder binder) {
		/**
		 * Auto convert the format of date type field.
		 */
		binder.registerCustomEditor(Date.class, new CustomDateEditor(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"), true));

		/**
		 * Prevent XSS attack.
		 */
		binder.registerCustomEditor(String.class, new StringEscapeEditor(true, false));
	}

	/**
	 * Jump to specified page by parameter.
	 * 
	 * This method don't consider the permission control.
	 * 
	 * @param folder
	 *            path
	 * @param jspName
	 *            JSP name
	 * @return specified JSP page
	 */
	@RequestMapping("/{folder}/{jspName}")
	public String redirectJsp(@PathVariable String folder, @PathVariable String jspName) {
		return "/" + folder + "/" + jspName;
	}

}
