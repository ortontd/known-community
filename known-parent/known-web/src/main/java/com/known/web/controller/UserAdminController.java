package com.known.web.controller;

import com.known.common.config.MailConfig;
import com.known.common.config.UrlConfig;
import com.known.common.config.UserConfig;
import com.known.common.enums.CodeEnum;
import com.known.common.enums.OrderByEnum;
import com.known.common.model.Collection;
import com.known.common.model.Message;
import com.known.common.model.SessionUser;
import com.known.common.model.User;
import com.known.common.utils.AbsolutePathUtil;
import com.known.common.utils.StringUtil;
import com.known.common.vo.OutResponse;
import com.known.common.vo.PageResult;
import com.known.exception.BussinessException;
import com.known.manager.query.CollectionQuery;
import com.known.manager.query.MessageQuery;
import com.known.service.AttachmentService;
import com.known.service.CollectionService;
import com.known.service.MessageService;
import com.known.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.File;

@RestController
@RequestMapping("/userAdmin")
public class UserAdminController extends BaseController {

    private Logger logger = LoggerFactory.getLogger(UserAdminController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private AttachmentService attachmentService;

    @Autowired
    private MessageService messageService;

    @Autowired
    private CollectionService collectionService;

    @Resource
    private UserConfig userConfig;

    @Resource
    private UrlConfig urlConfig;

    @Resource
    private MailConfig mailConfig;

    @RequestMapping("/admin")
    public ModelAndView updateUser(HttpSession session) {
        ModelAndView view = new ModelAndView("/page/admin/update_user");
        SessionUser sessionUser = (SessionUser) session.getAttribute(userConfig.getSession_User_Key());
        if (sessionUser == null) {
            view.setViewName("redirect:" + urlConfig.getLoginAbsolutePath());
            return view;
        }
        try {
            User user = userService.findUserInfo4UserHome(sessionUser.getUserid());
            view.addObject("user", user);
        } catch (BussinessException e) {
            logger.error("获取用户信息失败：{}", e);
            view.setViewName("redirect:" + urlConfig.getError_404());
            return view;
        }
        return view;
    }

    @RequestMapping("/updateUserInfo")
    public OutResponse<Object> updateUserInfo(HttpSession session, User user) {
        OutResponse<Object> outResponse = new OutResponse<>();
        SessionUser sessionUser = (SessionUser) session.getAttribute(userConfig.getSession_User_Key());
        user.setUserid(sessionUser.getUserid());
        try {
            userService.updateUserInfo(user);
            outResponse.setCode(CodeEnum.SUCCESS);
        } catch (BussinessException e) {
            logger.error("修改出错", e);
            outResponse.setMsg(e.getLocalizedMessage());
            outResponse.setCode(CodeEnum.BUSSINESSERROR);
        } catch (Exception e) {
            logger.error("修改出错{}", e);
            outResponse.setMsg("修改出错,请重试");
            outResponse.setCode(CodeEnum.SERVERERROR);
        }
        return outResponse;
    }

    @RequestMapping("/preUpdateUserPage")
    public ModelAndView preUpdateUserPage(HttpSession session) {
        ModelAndView view = new ModelAndView("/page/admin/update_userpage");
        SessionUser sessionUser = (SessionUser) session.getAttribute(userConfig.getSession_User_Key());
        if (sessionUser == null) {
            view.setViewName("redirect:" + urlConfig.getLoginAbsolutePath());
            return view;
        }
        try {
            User user = userService.findUserInfo4UserHome(sessionUser.getUserid());
            view.addObject("user", user);
        } catch (BussinessException e) {
            logger.error("获取用户信息失败：{}", e);
            view.setViewName("redirect:" + urlConfig.getError_404());
            return view;
        }
        return view;
    }


    @RequestMapping("/saveUserPage")
    public OutResponse<Object> saveUserPage(HttpSession session, Integer userPage) {
        OutResponse<Object> outResponse = new OutResponse<Object>();
        String userId = getUserid(session);
        User user = new User();
        user.setUserid(userId);
        try {
            userPage = userPage == null ? 0 : userPage;
            user.setUserPage(userPage);
            userService.updateUserWithoutValidate(user);
            outResponse.setCode(CodeEnum.SUCCESS);
        } catch (Exception e) {
            logger.error("修改出错{}", e);
            outResponse.setMsg("修改出错,请重试");
            outResponse.setCode(CodeEnum.SERVERERROR);
        }
        return outResponse;
    }


    @RequestMapping("/preUpdatePassword")
    public ModelAndView preUpdatePassword(HttpSession session) {
        ModelAndView view = new ModelAndView("/page/admin/update_password");
        SessionUser sessionUser = (SessionUser) session.getAttribute(userConfig.getSession_User_Key());
        if (sessionUser == null) {
            view.setViewName("redirect:" + urlConfig.getLoginAbsolutePath());
            return view;
        }
        try {
            User user = userService.findUserInfo4UserHome(sessionUser.getUserid());
            view.addObject("user", user);
        } catch (BussinessException e) {
            logger.error("获取用户信息失败：{}", e);
            view.setViewName("redirect:" + urlConfig.getError_404());
            return view;
        }
        return view;
    }

    @RequestMapping("/modifyPassword")
    public OutResponse<Object> modifyPassword(HttpSession session, String oldPassword, String newPassword) {
        OutResponse<Object> outResponse = new OutResponse<>();
        SessionUser sessionUser = (SessionUser) session.getAttribute(userConfig.getSession_User_Key());
        try {
            userService.updatePassword(sessionUser.getUserid(), oldPassword, newPassword);
            outResponse.setCode(CodeEnum.SUCCESS);
        } catch (BussinessException e) {
            logger.error("修改出错", e);
            outResponse.setMsg(e.getLocalizedMessage());
            outResponse.setCode(CodeEnum.BUSSINESSERROR);
        } catch (Exception e) {
            logger.error("修改出错", e);
            outResponse.setMsg("修改出错,请重试{}");
            outResponse.setCode(CodeEnum.SERVERERROR);
        }
        return outResponse;
    }

    @RequestMapping("/preUpdateUserIcon")
    public ModelAndView preUpdateUserIcon(HttpSession session) {
        ModelAndView view = new ModelAndView("/page/admin/update_usericon");
        SessionUser sessionUser = (SessionUser) session.getAttribute(userConfig.getSession_User_Key());
        if (sessionUser == null) {
            view.setViewName("redirect:" + urlConfig.getLoginAbsolutePath());
            return view;
        }
        try {
            User user = userService.findUserInfo4UserHome(sessionUser.getUserid());
            view.addObject("user", user);
        } catch (BussinessException e) {
            logger.error("获取用户信息失败：{}", e);
            view.setViewName("redirect:" + urlConfig.getError_404());
            return view;
        }
        return view;
    }

    @RequestMapping("/saveSysUserIcon")
    public OutResponse<Object> saveSysUserIcon(HttpSession session, String userIcon) {
        OutResponse<Object> outResponse = new OutResponse<>();
        String userId = getUserid(session);
        User user = new User();
        user.setUserid(userId);
        try {
            System.out.println(userIcon);
            String destUserIcon = "/resources/images/" + userIcon;
            user.setUserIcon(destUserIcon);
            userService.updateUserWithoutValidate(user);
            outResponse.setCode(CodeEnum.SUCCESS);
        } catch (Exception e) {
            logger.error("修改出错{}", e);
            outResponse.setMsg("修改出错,请重试");
            outResponse.setCode(CodeEnum.SERVERERROR);
        }
        return outResponse;
    }

    @RequestMapping("/saveUserIcon")
    public OutResponse<Object> saveUserIcon() {
        OutResponse<Object> outResponse = new OutResponse<>();
        outResponse.setMsg("系统暂不支持自定义头像, 抱歉");
        outResponse.setCode(CodeEnum.SERVERERROR);
        return outResponse;
    }

    @RequestMapping("/preUpdateUserBg")
    public ModelAndView preUpdateUserBg(HttpSession session) {
        ModelAndView view = new ModelAndView("/page/admin/update_userbg");
        SessionUser sessionUser = (SessionUser) session.getAttribute(userConfig.getSession_User_Key());
        if (sessionUser == null) {
            view.setViewName("redirect:" + urlConfig.getLoginAbsolutePath());
            return view;
        }
        try {
            User user = userService.findUserInfo4UserHome(sessionUser.getUserid());
            view.addObject("user", user);
        } catch (BussinessException e) {
            logger.error("获取用户信息失败：{}", e);
            view.setViewName("redirect:" + urlConfig.getError_404());
            return view;
        }
        return view;
    }

    @RequestMapping("/saveSysUserBg")
    public OutResponse<Object> saveSysUserBg(HttpSession session, String background) {
        OutResponse<Object> outResponse = new OutResponse<>();
        String userId = getUserid(session);
        User user = new User();
        user.setUserid(userId);
        try {
            user.setUserBg("/resources/images/"+background);
            userService.updateUserWithoutValidate(user);
            outResponse.setCode(CodeEnum.SUCCESS);
        } catch (Exception e) {
            logger.error("修改出错{}", e);
            outResponse.setMsg("修改出错,请重试");
            outResponse.setCode(CodeEnum.SERVERERROR);
        }
        return outResponse;
    }


    @RequestMapping("/deleteAttachment")
    public OutResponse<Object> deleteBlogAttachment(String attachmentId) {
        OutResponse<Object> outResponse = new OutResponse<>();
        try {
            attachmentService.deleteFile(attachmentId);
            outResponse.setCode(CodeEnum.SUCCESS);
        } catch (Exception e) {
            logger.error("删除文件失败{}", e);
            outResponse.setMsg("删除文件失败");
            outResponse.setCode(CodeEnum.SERVERERROR);
        }
        return outResponse;
    }

    @RequestMapping("/messageList")
    public ModelAndView messageList(HttpSession session) {
        ModelAndView view = new ModelAndView("/page/admin/message_list");
        SessionUser sessionUser = (SessionUser) session.getAttribute(userConfig.getSession_User_Key());
        if (sessionUser == null) {
            view.setViewName("redirect:" + urlConfig.getLoginAbsolutePath());
            return view;
        }
        try {
            User user = userService.findUserInfo4UserHome(sessionUser.getUserid());
            view.addObject("user", user);
        } catch (BussinessException e) {
            logger.error("获取消息列表失败：{}", e);
            view.setViewName("redirect:" + urlConfig.getError_404());
            return view;
        }
        return view;
    }

    @RequestMapping("/load_user_message_list.action")
    public OutResponse<Object> load_user_message_list(HttpSession session, MessageQuery messageQuery) {
        OutResponse<Object> outResponse = new OutResponse<>();
        try {
            messageQuery.setReceivedUserId(getUserid(session));
            messageQuery.setOrderBy(OrderByEnum.MESSAGE_STATUS_ASC_CREATE_TIME_DESC);
            PageResult<Message> result = messageService.findMessageByPage(messageQuery);
            outResponse.setData(result);
            outResponse.setCode(CodeEnum.SUCCESS);
        } catch (Exception e) {
            logger.error("获取消息列表出错{}", e);
            outResponse.setMsg("获取消息列表出错,请重试");
            outResponse.setCode(CodeEnum.SERVERERROR);
        }
        return outResponse;
    }

    @RequestMapping("/mark_message_read.action")
    public OutResponse<Object> mark_message_read(HttpSession session, String[] ids) {
        OutResponse<Object> outResponse = new OutResponse<Object>();
        try {
            messageService.update(ids, getUserid(session));
            outResponse.setCode(CodeEnum.SUCCESS);
        } catch (Exception e) {
            logger.error("消息标记已读出错{}", e);
            outResponse.setMsg("标记已读出错,请重试");
            outResponse.setCode(CodeEnum.SERVERERROR);
        }
        return outResponse;
    }

    @RequestMapping("/load_user_message_count.action")
    public OutResponse<Object> load_user_message_count(HttpSession session, MessageQuery messageQuery) {
        OutResponse<Object> outResponse = new OutResponse<>();
        try {
            messageQuery.setReceivedUserId(getUserid(session));
            messageQuery.setOrderBy(OrderByEnum.MESSAGE_STATUS_ASC_CREATE_TIME_DESC);
            Integer count = messageService.findMessageCount(messageQuery);
            outResponse.setData(count);
            outResponse.setCode(CodeEnum.SUCCESS);
        } catch (Exception e) {
            logger.error("获取消息列表出错{}", e);
            outResponse.setMsg("获取消息列表出错,请重试");
            outResponse.setCode(CodeEnum.SERVERERROR);
        }
        return outResponse;
    }

    @RequestMapping("/del_message.action")
    public OutResponse<Object> del_message(HttpSession session, String[] ids) {
        OutResponse<Object> outResponse = new OutResponse<>();
        try {
            messageService.delMessage(getUserid(session), ids);
            outResponse.setCode(CodeEnum.SUCCESS);
        } catch (Exception e) {
            logger.error("消息删除出错{}", e);
            outResponse.setMsg("消息删除出错,请重试");
            outResponse.setCode(CodeEnum.SERVERERROR);
        }
        return outResponse;
    }

    @RequestMapping("/readMessage.action")
    public ModelAndView readMessage(HttpSession session, String id) {
        ModelAndView view = new ModelAndView(urlConfig.getError_404());
        SessionUser sessionUser = (SessionUser) session.getAttribute(userConfig.getSession_User_Key());
        if (sessionUser == null) {
            view.setViewName("redirect:" + urlConfig.getLoginAbsolutePath());
            return view;
        }
        try {
            User user = userService.findUserInfo4UserHome(sessionUser.getUserid());
            view.addObject("user", user);
            view.setViewName("redirect:" + messageService.getMessageById(id, getUserid(session)).getUrl());
            messageService.update(new String[]{id}, getUserid(session));
        } catch (BussinessException e) {
            logger.error("获取消息列表失败：{}", e);
            view.setViewName("redirect:" + urlConfig.getError_404());
            return view;
        }
        return view;
    }

    @RequestMapping("/collection_list.action")
    public ModelAndView collection_list(HttpSession session, CollectionQuery collectionQuery) {
        ModelAndView view = new ModelAndView("/page/admin/collection_list");
        SessionUser sessionUser = (SessionUser) session.getAttribute(userConfig.getSession_User_Key());
        if (sessionUser == null) {
            view.setViewName("redirect:" + urlConfig.getLoginAbsolutePath());
            return view;
        }
        try {
            User user = userService.findUserInfo4UserHome(sessionUser.getUserid());
            view.addObject("user", user);
            view.addObject("articleType", collectionQuery.getArticleType().getType());
        } catch (BussinessException e) {
            logger.error("获取收藏列表失败：{}", e);
            view.setViewName("redirect:" + urlConfig.getError_404());
            return view;
        }
        return view;
    }

    @RequestMapping("/load_collection.action")
    public OutResponse<Object> load_collection(CollectionQuery collectionQuery) {
        OutResponse<Object> outResponse = new OutResponse<>();
        try {
            System.out.println("===========" + collectionQuery);
            PageResult<Collection> result = collectionService.findCollectionByPage(collectionQuery);
            outResponse.setData(result);
            outResponse.setCode(CodeEnum.SUCCESS);
        } catch (Exception e) {
            logger.error("获取收藏列表出错{}", e);
            outResponse.setMsg("获取收藏列表出错,请重试");
            outResponse.setCode(CodeEnum.SERVERERROR);
        }
        return outResponse;
    }

    @RequestMapping("/del_collection.action")
    public OutResponse<Object> del_collection(Collection collection) {
        OutResponse<Object> outResponse = new OutResponse<>();
        try {
            collectionService.deleteCollection(collection);
            outResponse.setCode(CodeEnum.SUCCESS);
        } catch (Exception e) {
            logger.error("收藏删除出错{}", e);
            outResponse.setMsg("收藏删除出错,请重试");
            outResponse.setCode(CodeEnum.SERVERERROR);
        }
        return outResponse;
    }


}
