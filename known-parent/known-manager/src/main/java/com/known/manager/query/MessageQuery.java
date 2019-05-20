package com.known.manager.query;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.known.common.enums.MessageStatus;
import com.known.common.enums.OrderByEnum;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class MessageQuery extends BaseQuery {

	private Integer id;
	
	private MessageStatus status;
	
	private Integer receivedUserId;
	
	private Integer[] ids;
	
	private String startDate;
	
	private String endDate;
	
	private OrderByEnum orderBy;

}
