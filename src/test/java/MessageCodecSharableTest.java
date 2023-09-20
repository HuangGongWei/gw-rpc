import com.gw.core.config.Config;
import com.gw.core.message.RpcRequestMessage;
import com.gw.core.protocol.MessageCodecSharable;
import com.gw.core.protocol.ProtocolFrameDecoder;
import com.gw.core.protocol.SequenceIdGenerator;
import com.gw.core.service.HelloService;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.logging.LoggingHandler;

import java.lang.reflect.Method;

/**
 * Description:
 *
 * @author LinHuiBa-YanAn
 * @date 2023/9/20 21:03
 */
public class MessageCodecSharableTest {
    public static void main(String[] args) throws Exception {
        EmbeddedChannel channel = new EmbeddedChannel(
                new LoggingHandler(),
                new ProtocolFrameDecoder(),
                new MessageCodecSharable()
        );
        int sequenceId = SequenceIdGenerator.nextId();

        Class<HelloService> serviceClass = HelloService.class;
        Method method = HelloService.class.getMethod("sayHello", String.class);
        String[] parameterValue = new String[]{"yanan"};
        RpcRequestMessage message = new RpcRequestMessage(sequenceId, serviceClass.getName(), method.getName(), method.getReturnType(), method.getParameterTypes(), parameterValue);
        // channel.writeOutbound(message);

        ByteBuf out = ByteBufAllocator.DEFAULT.buffer();
        out.writeBytes(new byte[]{1, 2, 3, 4});
        out.writeByte(1);
        out.writeByte(Config.getSerializerAlgorithm().ordinal());
        out.writeByte(message.getMessageType());
        out.writeInt(message.getSequenceId());
        out.writeByte(0xff);
        byte[] bytes = Config.getSerializerAlgorithm().serialize(message);
        out.writeInt(bytes.length);
        out.writeBytes(bytes);

        channel.writeInbound(out);
    }
}
