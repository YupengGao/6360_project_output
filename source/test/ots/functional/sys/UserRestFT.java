package light.mvc.functional.sys;

import javax.annotation.Resource;

import light.mvc.functional.base.BaseTestTemplate;
import light.mvc.pageModel.sys.User;
import light.mvc.service.sys.UserServiceI;

import org.junit.Test;

/**
 * Test for user management.
 * 
 * @author
 */
public class UserRestFT extends BaseTestTemplate {

	@Resource
	private UserServiceI userService;
	
	
	@Test
	public void list() {
		User user = new User();
		user.setLoginname("admin");
		user.setPassword("admin");
		User u = userService.login(user);
	}
}
