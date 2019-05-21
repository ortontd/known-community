package com.known.service_impl;

import com.known.common.enums.DateTimePatternEnum;
import com.known.common.enums.TextLengthEnum;
import com.known.common.enums.VoteType;
import com.known.common.model.TopicVote;
import com.known.common.model.TopicVoteDetail;
import com.known.common.model.TopicVoteUser;
import com.known.common.utils.DateUtil;
import com.known.common.utils.StringUtils;
import com.known.exception.BussinessException;
import com.known.manager.mapper.TopicVoteDetailMapper;
import com.known.manager.mapper.TopicVoteMapper;
import com.known.manager.mapper.TopicVoteUserMapper;
import com.known.manager.query.TopicVoteDetailQuery;
import com.known.manager.query.TopicVoteQuery;
import com.known.manager.query.TopicVoteUserQuery;
import com.known.service.TopicVoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class TopicVoteServiceImpl implements TopicVoteService {
	
	@Autowired
	private TopicVoteMapper<TopicVote, TopicVoteQuery> topicVoteMapper;
	
	@Autowired
	private TopicVoteDetailMapper<TopicVoteDetail, TopicVoteDetailQuery> topicVoteDetailMapper;
	
	@Autowired
	private TopicVoteUserMapper<TopicVoteUser, TopicVoteUserQuery> topicVoteUserMapper;
	
	private final int MIN_VOTE_TITLE_LENGTH = 2;
	
	private final int MAX_VOTE_TITLE_LENGTH = 10;
	
	public void addVote(TopicVote topicVote, String[] voteTitle)
			throws BussinessException {
		if(voteTitle == null || voteTitle.length > MAX_VOTE_TITLE_LENGTH ||
				voteTitle.length < MIN_VOTE_TITLE_LENGTH || 
				StringUtils.isEmpty(topicVote.getEndDateString()) ||
				topicVote.getVoteType() == null
				){
			throw new BussinessException("参数错误");
		}
		for(String voteItem : voteTitle){
			if(StringUtils.isEmpty(voteItem) || voteItem.length() > TextLengthEnum.TEXT_200_LENGTH.getLength()){
				throw new BussinessException("参数错误");
			}
		}
		topicVote.setEndDate(DateUtil.parse(topicVote.getEndDateString(), DateTimePatternEnum.YYYY_MM_DD.getPattern()));
		this.topicVoteMapper.insert(topicVote);
		List<TopicVoteDetail> topicVoteDetails = new ArrayList<TopicVoteDetail>();
		for(String title : voteTitle){
			TopicVoteDetail topicVoteDetail = new TopicVoteDetail();
			topicVoteDetail.setTitle(title);
			topicVoteDetail.setVoteId(topicVote.getVoteId());
			topicVoteDetails.add(topicVoteDetail);
		}
		this.topicVoteDetailMapper.insertBatch(topicVoteDetails);
	}

	public TopicVote getTopicVote(Integer topicId, Integer userId) {
		if(topicId == null){
			return null;
		}
		TopicVote topicVote = this.topicVoteMapper.selectVoteByTopicId(topicId);
		boolean canVote = true;
		if(userId != null){
			TopicVoteUserQuery topicVoteUserQuery = new TopicVoteUserQuery();
			topicVoteUserQuery.setUserId(userId);
			topicVoteUserQuery.setVoteId(topicVote.getVoteId());
			List<TopicVoteUser> list = this.topicVoteUserMapper.selectList(topicVoteUserQuery);
			topicVote.setTopicVoteUserList(list);
			if(!list.isEmpty() || DateUtil.daysBetween(new Date(), topicVote.getEndDate()) < 0){
				canVote = false;
			}
		}
		List<TopicVoteDetail> topicVoteDetailList = topicVote.getTopicVoteDetailList();
		int sumCount = 0;
		for(TopicVoteDetail topicVoteDetail : topicVoteDetailList){
			sumCount = sumCount + topicVoteDetail.getCount();
		}
		topicVote.setSumCount(sumCount);
		topicVote.setCanVote(canVote);
		return topicVote;
	}

	@Transactional(propagation= Propagation.REQUIRES_NEW, rollbackFor=BussinessException.class)
	public TopicVote doVote(Integer voteId, Integer voteType,
			Integer[] voteDetailId, Integer userId, Integer topicId) throws BussinessException {
		if(voteId == null || VoteType.getVoteTypeByValue(voteType) == null ||
				voteDetailId == null ||(VoteType.getVoteTypeByValue(voteId) == VoteType.SingleSelection && 
				voteDetailId.length > 1)){
			throw new BussinessException("参数错误");
		}
		TopicVoteUserQuery topicVoteUserQuery = new TopicVoteUserQuery();
		topicVoteUserQuery.setUserId(userId);
		topicVoteUserQuery.setVoteId(voteId);
		List<TopicVoteUser> topicVoteUserList = this.topicVoteUserMapper.selectList(topicVoteUserQuery);
		if(!topicVoteUserList.isEmpty()){
			throw new BussinessException("您已经投过票了");
		}
		Date curDate = new Date();
		TopicVote topicVote = this.topicVoteMapper.selectVoteByTopicId(topicId);
		if(topicVote == null){
			throw new BussinessException("投票不存在");
		}
		if(DateUtil.daysBetween(curDate, topicVote.getEndDate()) < 0){
			throw new BussinessException("投票已截止");
		}
		List<TopicVoteUser> list = new ArrayList<TopicVoteUser>();
		List<Integer> voteDetailList = new ArrayList<Integer>();
		for(Integer detailId : voteDetailId){
			TopicVoteUser voteUser = new TopicVoteUser();
			voteUser.setUserId(userId);
			voteUser.setVoteDate(curDate);
			voteUser.setVoteDetailId(detailId);
			list.add(voteUser);
			voteDetailList.add(detailId);
		}
		this.topicVoteUserMapper.insertBatch(list);
		this.topicVoteDetailMapper.updateVoteCountBatch(voteDetailList);
		return getTopicVote(topicId, userId);
	}
	
}
