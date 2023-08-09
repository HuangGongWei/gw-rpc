package com.gw.core.server.handler;

import com.gw.core.message.RpcRequestMessage;
import com.gw.core.message.RpcResponseMessage;
import com.gw.core.service.ServicesFactory;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Description: Rpc请求消息处理程序
 *
 * @author LinHuiBa-YanAn
 * @date 2023/8/7 20:52
 */
@Slf4j
@ChannelHandler.Sharable
public class RpcRequestMessageHandler extends SimpleChannelInboundHandler<RpcRequestMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequestMessage rpcRequest) {
        RpcResponseMessage rpcResponse = new RpcResponseMessage();
        log.info("Netty rpc server receives the request:{}", rpcRequest);
        rpcResponse.setSequenceId(rpcRequest.getSequenceId());
        rpcResponse.setMessageType(rpcRequest.getMessageType());
        try {
            Object service = ServicesFactory.getService(Class.forName(rpcRequest.getInterfaceName()));
            Method method = service.getClass().getMethod(rpcRequest.getMethodName(), rpcRequest.getParameterTypes());
            Object invoke = method.invoke(service, rpcRequest.getParameterValue());
            rpcResponse.setReturnValue(invoke);
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            log.error("RPC processing failed. An exception occurred. Procedure. exception:{}", e.getMessage());
            rpcResponse.setExceptionValue(e);
        }
        ctx.writeAndFlush(rpcResponse);
    }
}
