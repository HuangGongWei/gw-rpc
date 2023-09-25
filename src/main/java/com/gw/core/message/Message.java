package com.gw.core.message;

import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
/**
 * Description: 抽象消息体
 *
 * @author LinHuiBa-YanAn
 * @date 2023/8/7 20:37
 */
@Data
public abstract class Message implements Serializable {

    /**
     * 根据消息类型字节，获得对应的消息 class
     * @param messageType 消息类型字节
     * @return 消息 class
     */
    public static Class<? extends Message> getMessageClass(int messageType) {
        return messageClasses.get(messageType);
    }

    /**
     * 序列Id
     */
    private int sequenceId;

    /**
     * 消息类型
     */
    private int messageType;

    public abstract int getMessageType();

    /**
     * Rpc请求体 byte 值
     */
    public static final int RPC_MESSAGE_TYPE_REQUEST = 0;
    /**
     * Rpc响应体 byte 值
     */
    public static final int RPC_MESSAGE_TYPE_RESPONSE = 1;

    private static final Map<Integer, Class<? extends Message>> messageClasses = new HashMap<>();

    static {
        messageClasses.put(RPC_MESSAGE_TYPE_REQUEST, RpcRequestMessage.class);
        messageClasses.put(RPC_MESSAGE_TYPE_RESPONSE, RpcResponseMessage.class);
    }
}
