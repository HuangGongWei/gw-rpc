package com.gw.core.message;

import lombok.Data;
import lombok.ToString;

/**
 * Description: Rpc响应体
 *
 * @author LinHuiBa-YanAn
 * @date 2023/8/7 20:43
 */
@Data
@ToString(callSuper = true)
public class RpcResponseMessage extends Message {
    /**
     * 返回值
     */
    private Object returnValue;
    /**
     * 异常值
     */
    private Exception exceptionValue;

    @Override
    public int getMessageType() {
        return RPC_MESSAGE_TYPE_RESPONSE;
    }
}
