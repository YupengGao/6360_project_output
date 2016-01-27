package com.ots.framework.interceptors;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.ots.dto.base.SessionInfo;
import com.ots.framework.constant.GlobalConstant;

/**
 * security interceptor.
 * 
 */
public class SecurityInterceptor implements HandlerInterceptor {

	private List<String> excludeUrls;

	public List<String> getExcludeUrls() {
		return excludeUrls;
	}

	public void setExcludeUrls(List<String> excludeUrls) {
		this.excludeUrls = excludeUrls;
	}

	/**
	 * Invoke after render page.
	 */
	@Override
	public void afterCompletion(HttpServletRequest request,
			HttpServletResponse response, Object object, Exception exception)
			throws Exception {

	}

	/**
	 * Invoke after exceute method of controller.
	 */
	@Override
	public void postHandle(HttpServletRequest request,
			HttpServletResponse response, Object object,
			ModelAndView modelAndView) throws Exception {

	}

	/**
	 * Invoke before exceute method of controller.
	 */
	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object object) throws Exception {
		String requestUri = request.getRequestURI();
		String contextPath = request.getContextPath();
		String url = requestUri.substring(contextPath.length());
		SessionInfo sessionInfo = (SessionInfo) request.getSession()
				.getAttribute(GlobalConstant.SESSION_INFO);

		System.out.println(url.indexOf("/admin/"));
		System.out.println(excludeUrls + "====" + excludeUrls);
		if ((url.indexOf("/admin/") > -1) || excludeUrls.contains(url)) {
			return true;
		}

		if ((sessionInfo == null) || (sessionInfo.getId() == null)) {
			request.setAttribute(
					"msg",
					"You have not logined or have timed out, please login again and then refresh the page!");
			request.getRequestDispatcher("/error/noSession.jsp").forward(
					request, response);
			return false;
		}

		if (!sessionInfo.getResourceAllList().contains(url)) {
			return true;
		}

		if (!sessionInfo.getResourceList().contains(url)) {
			request.setAttribute(
					"msg",
					"You have no the privilige to access this resource<br/>please contact the administrator to give you access to <br/>["
							+ url + "]<br/>!");
			request.getRequestDispatcher("/error/noSecurity.jsp").forward(
					request, response);
			return false;
		}

		return true;
	}
}
