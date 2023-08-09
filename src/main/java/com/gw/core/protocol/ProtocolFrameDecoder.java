package com.gw.core.protocol;

import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * Description: 协议帧解码器
 *
 * @author LinHuiBa-YanAn
 * @date 2023/8/7 20:51
 */
public class ProtocolFrameDecoder extends LengthFieldBasedFrameDecoder {

    public ProtocolFrameDecoder() {
        this(1024, 12, 4, 0, 0);
    }

    public ProtocolFrameDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength, int lengthAdjustment, int initialBytesToStrip) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength, lengthAdjustment, initialBytesToStrip);
    }
}

