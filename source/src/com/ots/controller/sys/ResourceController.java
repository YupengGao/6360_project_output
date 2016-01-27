package com.ots.controller.sys;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ots.controller.base.BaseController;
import com.ots.dto.base.Json;
import com.ots.dto.base.SessionInfo;
import com.ots.dto.base.Tree;
import com.ots.dto.sys.Resource;
import com.ots.framework.constant.GlobalConstant;
import com.ots.service.sys.ResourceServiceI;

@Controller
@RequestMapping("/resource")
public class ResourceController extends BaseController {

	@Autowired
	private ResourceServiceI resourceService;
	
	@RequestMapping("/manager")
	public String manager() {
		return "/admin/resource";
	}

	@RequestMapping("/tree")
	@ResponseBody
	public List<Tree> tree(HttpSession session) {
		SessionInfo sessionInfo = (SessionInfo) session.getAttribute(GlobalConstant.SESSION_INFO);
		return resourceService.tree(sessionInfo);
	}

	@RequestMapping("/allTree")
	@ResponseBody
	public List<Tree> allTree(boolean flag) {//true获取全部资源,false只获取菜单资源
		return resourceService.allTree(flag);
	}

	@RequestMapping("/treeGrid")
	@ResponseBody
	public List<Resource> treeGrid() {
		return resourceService.treeGrid();
	}

	@RequestMapping("/get")
	@ResponseBody
	public Resource get(Long id) {
		return resourceService.get(id);
	}
	
	@RequestMapping("/editPage")
	public String editPage(HttpServletRequest request,Long id) {
		Resource r = resourceService.get(id);
		request.setAttribute("resource", r);
		return "/admin/resourceEdit";
	}


	@RequestMapping("/edit")
	@ResponseBody
	public Json edit(Resource resource) throws InterruptedException {
		Json j = new Json();
		try {
			resourceService.edit(resource);
			j.setSuccess(true);
			j.setMsg("Edit successfully!");
		} catch (Exception e) {
			j.setMsg(e.getMessage());
		}
		return j;
	}

	@RequestMapping("/delete")
	@ResponseBody
	public Json delete(Long id) {
		Json j = new Json();
		try {
			resourceService.delete(id);
			j.setMsg("Delete successfully!");
			j.setSuccess(true);
		} catch (Exception e) {
			j.setMsg(e.getMessage());
		}
		return j;
	}
	
	@RequestMapping("/addPage")
	public String addPage() {
		return "/admin/resourceAdd";
	}

	@RequestMapping("/add")
	@ResponseBody
	public Json add(Resource resource) {
		Json j = new Json();
		try {
			resourceService.add(resource);
			j.setSuccess(true);
			j.setMsg("Add successfully!");
		} catch (Exception e) {
			j.setMsg(e.getMessage());
		}
		return j;
	}

}
