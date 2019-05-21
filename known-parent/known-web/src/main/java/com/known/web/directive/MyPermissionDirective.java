package com.known.web.directive;

import com.known.common.model.SysRes;
import com.known.common.model.UserRedis;
import com.known.common.utils.Constants;
import com.known.service.SysResService;
import com.known.service.SysRoleService;
import freemarker.core.Environment;
import freemarker.template.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * FreeMarker 设置权限宏定义, 避免后台重复操作
 * @author tangjunxiang
 * @version 1.0
 * @date 2019-05-21 14:13
 */
@Component
public class MyPermissionDirective implements TemplateDirectiveModel {

	@Autowired
	private SysResService sysResService;
	
	@Autowired
	private SysRoleService sysRoleService;

	@Value("${SESSION_USER_KEY}")
	private String  SESSION_USER_KEY;
	
	@Override
	public void execute(Environment env, Map params, TemplateModel[] loopVars,
                        TemplateDirectiveBody body) throws TemplateException, IOException {

			HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
			HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();

			UserRedis sessionUser = (UserRedis) request.getSession().getAttribute(SESSION_USER_KEY);
			TemplateModel templateModel;

			if(sessionUser!= null){
				//查找系统用户角色id集合 role_id
				Set<Integer> roleSet = sysRoleService.findRoleIdsByUserId(sessionUser.getUserid());

				//查找角色结果集 sys_res
				List<SysRes> list = sysResService.findMenuByRoleIds(roleSet);
				if(list != null){
					templateModel	 = ObjectWrapper.DEFAULT_WRAPPER.wrap(list);
					env.setGlobalVariable("menu",templateModel);
					body.render(env.getOut());
				}
			} else{
				response.sendRedirect("/login?redirect=" + request.getRequestURI());
			}

	}

}
