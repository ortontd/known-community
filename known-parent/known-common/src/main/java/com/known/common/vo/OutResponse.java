package com.known.common.vo;

import com.known.common.enums.Code;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author tangjunxiang
 * @version 1.0
 * @date 2019-05-21 23:14
 */
@Data
@NoArgsConstructor
public class OutResponse<T> {

    private Code code;

    private String msg;

    private T data;
}
