package com.known.web.controller;

import com.known.common.config.UserConfig;
import com.known.common.enums.Code;
import com.known.common.model.Comment;
import com.known.common.model.SessionUser;
import com.known.common.utils.Constants;
import com.known.common.vo.OutResponse;
import com.known.common.vo.PageResult;
import com.known.exception.BussinessException;
import com.known.manager.query.CommentQuery;
import com.known.service.CommentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/comment")
public class CommentController extends BaseController {
	
	private Logger logger = LoggerFactory.getLogger(CommentController.class);
	
	@Autowired
	private CommentService commentService;

	@Resource
	private UserConfig userConfig;

	@RequestMapping("/loadComment")
	public OutResponse<PageResult<Comment>> loadComment(HttpSession session, CommentQuery commentQuery){
		 OutResponse<PageResult<Comment>> outResponse = new OutResponse<>();
		 try {
			 PageResult<Comment> pageResult = this.commentService.findCommentByPage(commentQuery);
			 outResponse.setData(pageResult);
			 outResponse.setCode(Code.SUCCESS);
		} catch (Exception e) {
			logger.error("{}加载评论出错", e);
			outResponse.setMsg("加载评论出错");
			outResponse.setCode(Code.SERVERERROR);
		}
		 return outResponse;
	}
	
	@ResponseBody
	@RequestMapping("/addComment")
	public OutResponse<Object> addComment(HttpSession session, Comment comment){
		OutResponse<Object> outResponse = new OutResponse<>();
		SessionUser sessionUser = (SessionUser) session.getAttribute(userConfig.getSession_User_Key());
		if(sessionUser==null){
			outResponse.setCode(Code.BUSSINESSERROR);
			outResponse.setMsg("请先登录");
			return outResponse;
		}
		this.setUserBaseInfo(Comment.class, comment, session);
		try {
			this.commentService.addComment(comment);
			outResponse.setData(comment);
			 outResponse.setCode(Code.SUCCESS);
		} catch (BussinessException e) {
			outResponse.setCode(Code.BUSSINESSERROR);
			outResponse.setMsg(e.getLocalizedMessage());
			logger.error("{}评论出错", sessionUser.getUserName());
		}catch (Exception e) {
			outResponse.setCode(Code.SERVERERROR);
			outResponse.setMsg("服务器出错");
			logger.error("{}评论出错", sessionUser.getUserName());
		}
		return outResponse;
	}


	@RequestMapping("/addComment2")
	public OutResponse<Object> addComment2(HttpSession session, Comment comment){
		OutResponse<Object> outResponse = new OutResponse<>();
		SessionUser sessionUser = (SessionUser) session.getAttribute(userConfig.getSession_User_Key());
		if(sessionUser==null){
			outResponse.setCode(Code.BUSSINESSERROR);
			outResponse.setMsg("请先登录");
			return outResponse;
		}
		this.setUserBaseInfo(Comment.class, comment, session);
		try {
			this.commentService.addComment(comment);
			outResponse.setData(comment);
			outResponse.setCode(Code.SUCCESS);
		} catch (BussinessException e) {
			outResponse.setCode(Code.BUSSINESSERROR);
			outResponse.setMsg(e.getLocalizedMessage());
			logger.error("{}评论出错", sessionUser.getUserName());
		}catch (Exception e) {
			outResponse.setCode(Code.SERVERERROR);
			outResponse.setMsg("服务器出错");
			logger.error("{}评论出错", sessionUser.getUserName());
		}
		return outResponse;
	}


}