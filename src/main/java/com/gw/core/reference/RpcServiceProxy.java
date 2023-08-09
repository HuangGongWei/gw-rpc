package com.gw.core.reference;

import com.gw.core.message.RpcRequestMessage;
import com.gw.core.protocol.MessageCodecSharable;
import com.gw.core.protocol.ProtocolFrameDecoder;
import com.gw.core.protocol.SequenceIdGenerator;
import com.gw.core.reference.handler.RpcResponseMessageHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.DefaultPromise;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Description: RPC服务代理类
 *
 * @author LinHuiBa-YanAn
 * @date 2023/8/8 10:43
 */
@Slf4j
public class RpcServiceProxy {

    /**
     * 获取代理实例
     *
     * @param serviceClass 服务类.class
     * @param <T>          服务类.class
     * @return 执行结果
     */
    public static <T> T getProxyService(Class<T> serviceClass) {
        ClassLoader loader = serviceClass.getClassLoader();
        Class<?>[] interfaces = new Class[]{serviceClass};
        Object obj = Proxy.newProxyInstance(loader, interfaces, (proxy, method, args) -> {
            // 1. 将方法调用转换为 消息对象
            int sequenceId = SequenceIdGenerator.nextId();
            RpcRequestMessage msg = new RpcRequestMessage(
                    sequenceId,
                    serviceClass.getName(),
                    method.getName(),
                    method.getReturnType(),
                    method.getParameterTypes(),
                    args
            );
            // 2. 将消息对象发送出去
            RpcClient.getChannel().writeAndFlush(msg);
            return null;
        });
        return (T) obj;
    }

    /**
     * The class that actually implements the proxy logic
     */
    static class RpcServiceProxyInvocationHandler implements InvocationHandler {

        private final Class referenceConfig;

        public RpcServiceProxyInvocationHandler(Class referenceConfig) {
            this.referenceConfig = referenceConfig;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            int sequenceId = SequenceIdGenerator.nextId();
            RpcRequestMessage msg = new RpcRequestMessage(
                    sequenceId,
                    referenceConfig.getName(),
                    method.getName(),
                    method.getReturnType(),
                    method.getParameterTypes(),
                    args
            );
            RpcClient.getChannel().writeAndFlush(msg);
            DefaultPromise<Object> promise = new DefaultPromise<>(RpcClient.getChannel().eventLoop());
            RpcResponseMessageHandler.PROMISES.put(sequenceId, promise);
            promise.await();
            if (promise.isSuccess()) {
                return promise.getNow();
            } else {
                throw new RuntimeException(promise.cause());
            }
        }
    }

    /**
     * 内嵌Netty客户端
     */
    static class RpcClient {
        /**
         * Channel
         */
        private static Channel channel = null;
        /**
         * lock
         */
        private static final Object LOCK = new Object();

        /**
         * get channel
         *
         * @return Channel
         */
        public static Channel getChannel() {
            if (channel != null) {
                return channel;
            }
            synchronized (LOCK) {
                if (channel != null) {
                    return channel;
                }
                initChannel();
                return channel;
            }
        }

        /**
         * init channel
         */
        private static void initChannel() {
            NioEventLoopGroup group = new NioEventLoopGroup();
            LoggingHandler loggingHandler = new LoggingHandler(LogLevel.DEBUG);
            MessageCodecSharable messageCodec = new MessageCodecSharable();
            RpcResponseMessageHandler rpcHandler = new RpcResponseMessageHandler();
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.group(group);
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) {
                    ch.pipeline().addLast(new ProtocolFrameDecoder());
                    ch.pipeline().addLast(loggingHandler);
                    ch.pipeline().addLast(messageCodec);
                    ch.pipeline().addLast(rpcHandler);
                }
            });
            try {
                channel = bootstrap.connect("localhost", 8080).sync().channel();
                channel.closeFuture().addListener(future -> group.shutdownGracefully());
            } catch (Exception e) {
                log.error("client error", e);
            }
        }
    }


}
