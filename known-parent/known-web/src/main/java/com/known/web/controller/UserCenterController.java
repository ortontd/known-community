package com.known.web.controller;

import com.known.common.config.UrlConfig;
import com.known.common.config.UserConfig;
import com.known.common.enums.Code;
import com.known.common.model.*;
import com.known.common.vo.OutResponse;
import com.known.common.vo.PageResult;
import com.known.exception.BussinessException;
import com.known.manager.query.*;
import com.known.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/userCenter")
public class UserCenterController extends BaseController {
	
	private Logger logger = LoggerFactory.getLogger(UserCenterController.class);
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private ShuoShuoService shuoShuoService;
	
	@Autowired
	private UserFriendService userFriendService;
	
	@Autowired
	private TopicService topicService;
	
	@Autowired
	private AskService askService;
	
	@Autowired
	private KnowledgeService knowledgeService;

	@Resource
	private UserConfig userConfig;

	@Resource
	private UrlConfig urlConfig;

	/**
	 * 用户个人中心首页
	 * @param session
	 * @param userId
	 * @return
	 */
	@RequestMapping(value="/{userId}")
	public ModelAndView user(HttpSession session, @PathVariable Integer userId){
		ModelAndView view = new ModelAndView("/page/user/home");
		try {
			//用户基本信息
			User user = userService.findUserInfo4UserHome(userId);
			view.addObject("user", user);

			//前端页面显示类型
			// 0-本人 1-未登录 2-已关注
			UserFriendQuery ufq1 = new UserFriendQuery();
			ufq1.setUserId(getUserid(session));
			ufq1.setFriendUserId(userId);
			int focusType =userFriendService.findFocusType4UserHome(ufq1);
			view.addObject("focusType", focusType);

			//获取粉丝和关注数量
			UserFriendQuery ufq2 = new UserFriendQuery();
			ufq2.setFriendUserId(userId);
			view.addObject("fansCount", userFriendService.findCount(ufq2));
			UserFriendQuery ufq3 = new UserFriendQuery();
			ufq3.setUserId(userId);
			view.addObject("focusCount", userFriendService.findCount(ufq3));
		} catch (BussinessException e) {
			logger.error("获取用户信息失败：", e);
			view.setViewName("redirect:" + urlConfig.getError_404());
			return view;
		}
		return view;
	}
	

	@ResponseBody
	@RequestMapping("/loadShuoShuoDetail")
	public OutResponse<Object> loadShuoShuoDetail(ShuoShuoQuery shuoShuoQuery){
		OutResponse<Object> outResponse = new OutResponse<>();
		try {
			ShuoShuo shuoShuo = shuoShuoService.findShuoShuo(shuoShuoQuery);
			outResponse.setData(shuoShuo);
			outResponse.setCode(Code.SUCCESS);
		} catch (Exception e) {
			logger.error("加载说说异常", e);
			outResponse.setMsg("加载说说出错");
			outResponse.setCode(Code.SERVERERROR);
		}
		return outResponse;
	}
	
	@ResponseBody
	@RequestMapping("/loadUserFriend")
	public OutResponse<Object> loadUserFriend(HttpSession session, int pageNum){
		OutResponse<Object> outResponse = new OutResponse<>();
		SessionUser sessionUser = (SessionUser) session.getAttribute(userConfig.getSession_User_Key());
		if(sessionUser==null){
			outResponse.setCode(Code.BUSSINESSERROR);
			outResponse.setMsg("请先登录");
			return outResponse;
		}
		int userId = getUserid(session);
		UserFriendQuery ufq = new UserFriendQuery();
		ufq.setUserId(userId);
		ufq.setPageNum(pageNum);
		PageResult<UserFriend> pageResult = userFriendService.findFriendList(ufq);
		outResponse.setData(pageResult);
		return outResponse;
	}


	@ResponseBody
	@RequestMapping("/loadShuoShuos")
	public OutResponse<Object> loadShuoShuos(HttpSession session,ShuoShuoQuery shuoShuoQuery){

		OutResponse<Object> outResponse = new OutResponse<>();

		try {
			PageResult<ShuoShuo> pageResult = shuoShuoService.findShuoShuoList(shuoShuoQuery);
			outResponse.setData(pageResult);
			outResponse.setCode(Code.SUCCESS);
		} catch (Exception e) {
			logger.error("加载说说异常", e);
			outResponse.setMsg("加载说说出错");
			outResponse.setCode(Code.SERVERERROR);
		}
		return outResponse;
	}

	@ResponseBody
	@RequestMapping("/loadTopic")
	public OutResponse<Object> loadTopic(HttpSession session, TopicQuery topicQuery){
		OutResponse<Object> outResponse = new OutResponse<Object>();
		try {
			PageResult<Topic> pageResult = topicService.findTopicByPage(topicQuery);
			outResponse.setData(pageResult);
			outResponse.setCode(Code.SUCCESS);
		} catch (Exception e) {
			logger.error("加载话题异常", e);
			outResponse.setMsg("加载话题出错");
			outResponse.setCode(Code.SERVERERROR);
		}
		return outResponse;
	}
	
	@ResponseBody
	@RequestMapping("/loadAsk")
	public OutResponse<Object> loadAsk(HttpSession session, AskQuery askQuery){
		OutResponse<Object> outResponse = new OutResponse<Object>();
		try {
			PageResult<Ask> pageResult = askService.findAskByPage(askQuery);
			outResponse.setData(pageResult);
			outResponse.setCode(Code.SUCCESS);
		} catch (Exception e) {
			logger.error("加载问答异常", e);
			outResponse.setMsg("加载问答出错");
			outResponse.setCode(Code.SERVERERROR);
		}
		return outResponse;
	}
	
	@ResponseBody
	@RequestMapping("/loadKnowledge")
	public OutResponse<Object> loadKnowledge(HttpSession session, KnowledgeQuery knowledgeQuery){
		OutResponse<Object> outResponse = new OutResponse<Object>();
		try {
			PageResult<Knowledge> pageResult = knowledgeService.findKnowledgeByPage(knowledgeQuery);
			outResponse.setData(pageResult);
			outResponse.setCode(Code.SUCCESS);
		} catch (Exception e) {
			logger.error("加载知识库异常", e);
			outResponse.setMsg("加载知识库出错");
			outResponse.setCode(Code.SERVERERROR);
		}
		return outResponse;
	}
	
	@ResponseBody
	@RequestMapping("/loadFocus")
	public OutResponse<Object> loadUserFriend(HttpSession session, UserFriendQuery ufq){
		OutResponse<Object> outResponse = new OutResponse<Object>();
		try{
			PageResult<UserFriend> pageResult = userFriendService.findFriendList(ufq);
			outResponse.setData(pageResult);
		} catch (Exception e) {
			logger.error("加载关注用户异常", e);
			outResponse.setMsg("加载关注用户出错");
			outResponse.setCode(Code.SERVERERROR);
		}
		return outResponse;
	}
	
	@ResponseBody
	@RequestMapping("/loadFans")
	public OutResponse<Object> loadUserFans(HttpSession session, UserFriendQuery ufq){
		OutResponse<Object> outResponse = new OutResponse<Object>();
		try{
			PageResult<UserFriend> pageResult = userFriendService.findFansList(ufq);
			outResponse.setData(pageResult);
		} catch (Exception e) {
			logger.error("加载用户粉丝异常", e);
			outResponse.setMsg("加载粉丝出错");
			outResponse.setCode(Code.SERVERERROR);
		}
		return outResponse;
	}
	
	@RequestMapping(value = "/{userId}/shuoshuo/{id}")
	public ModelAndView shuoshuo(HttpSession session, @PathVariable Integer userId, @PathVariable Integer id){
		ModelAndView view = new ModelAndView("/page/user/shuoshuo");
		try {
			User user = userService.findUserInfo4UserHome(userId);
			view.addObject("user", user);
			view.addObject("id", id);
			UserFriendQuery ufq = new UserFriendQuery();
			ufq.setUserId(getUserid(session));
			ufq.setFriendUserId(userId);
			view.addObject("focusType", userFriendService.findFocusType4UserHome(ufq));
			
			//获取粉丝和关注数量
			ufq = new UserFriendQuery();
			ufq.setFriendUserId(userId);
			view.addObject("fansCount", userFriendService.findCount(ufq));
			ufq = new UserFriendQuery();
			ufq.setUserId(userId);
			view.addObject("focusCount", userFriendService.findCount(ufq));
		} catch (Exception e) {
			logger.error("获取说说信息失败：", e);
			view.setViewName("redirect:" + urlConfig.getError_404());
			return view;
		}
		return view;
	}
	
	@ResponseBody
	@RequestMapping("/focus.action")
	public OutResponse<Object> focus(HttpSession session, UserFriend userFriend){
		OutResponse<Object> outResponse = new OutResponse<Object>();
		try{
			setUserBaseInfo(UserFriend.class, userFriend, session);
			userFriendService.addFocus(userFriend);
		}catch (BussinessException e) {
			logger.error("关注异常", e);
			outResponse.setMsg(e.getLocalizedMessage());
			outResponse.setCode(Code.SERVERERROR);
		} 
		catch (Exception e) {
			logger.error("加载用户粉丝异常", e);
			outResponse.setMsg("关注出错,请重试");
			outResponse.setCode(Code.SERVERERROR);
		}
		return outResponse;
	}
	
	@ResponseBody
	@RequestMapping("/cancel_focus.action")
	public OutResponse<Object> cancel_focus(HttpSession session, UserFriend userFriend){
		OutResponse<Object> outResponse = new OutResponse<Object>();
		try{
			setUserBaseInfo(UserFriend.class, userFriend, session);
			userFriendService.cancelFocus(userFriend);
		}catch (BussinessException e) {
			logger.error("关注异常", e);
			outResponse.setMsg(e.getLocalizedMessage());
			outResponse.setCode(Code.SERVERERROR);
		} 
		catch (Exception e) {
			logger.error("加载用户粉丝异常", e);
			outResponse.setMsg("关注出错,请重试");
			outResponse.setCode(Code.SERVERERROR);
		}
		return outResponse;
	}
	
}
