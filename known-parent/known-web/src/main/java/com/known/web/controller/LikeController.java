package com.known.web.controller;

import com.known.common.enums.Code;
import com.known.common.model.Like;
import com.known.common.model.UserRedis;
import com.known.common.utils.Constants;
import com.known.common.vo.OutResponse;
import com.known.exception.BussinessException;
import com.known.service.LikeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/like")
public class LikeController extends BaseController {
	
	private Logger logger = LoggerFactory.getLogger(LikeController.class);
	
	@Autowired
	private LikeService likeService;

	@Value("${SESSION_USER_KEY}")
	private String SESSION_USER_KEY;
	
	@ResponseBody
	@RequestMapping("doLike")
	public OutResponse<Object> doLike(HttpSession session, Like like){
		OutResponse<Object> outResponse = new OutResponse<>();
		UserRedis sessionUser = (UserRedis) session.getAttribute(SESSION_USER_KEY);
		if(sessionUser==null){
			outResponse.setCode(Code.BUSSINESSERROR);
			outResponse.setMsg("请先登录");
			return outResponse;
		}
		try {
			this.likeService.addLike(like);
			outResponse.setCode(Code.SUCCESS);
		} catch (BussinessException e) {
			outResponse.setMsg(e.getLocalizedMessage());
			outResponse.setCode(Code.BUSSINESSERROR);
			logger.error("{}赞出错{}", sessionUser.getUserName(), e.getLocalizedMessage());
		}catch (Exception e) {
			logger.error("{}赞出错{}", sessionUser.getUserName(), e.getLocalizedMessage());
			outResponse.setMsg("服务器出错");
			outResponse.setCode(Code.SERVERERROR);
		}
		return outResponse;
	}
}
