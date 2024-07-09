package com.cheeterlee.rpc.core.codec;

import com.cheeterlee.rpc.core.common.RpcRequest;
import com.cheeterlee.rpc.core.common.RpcResponse;
import com.cheeterlee.rpc.core.constant.ProtocolConstants;
import com.cheeterlee.rpc.core.enums.MessageType;
import com.cheeterlee.rpc.core.enums.SerializationType;
import com.cheeterlee.rpc.core.protocol.MessageHeader;
import com.cheeterlee.rpc.core.protocol.RpcMessage;
import com.cheeterlee.rpc.core.serialization.Serialization;
import com.cheeterlee.rpc.core.serialization.SerializationFactory;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;

import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * protocol：
 * <pre>
 *   --------------------------------------------------------------------
 *  | magic num (4byte) | version (1byte)  | serialize algo (1byte) |  message type (1byte) |
 *  -------------------------------------------------------------------
 *  |    message status (1byte)  |    message id (4byte)   |   message length (4byte)   |
 *  --------------------------------------------------------------------
 *  |                        message content (unfixed size)                         |
 *  -------------------------------------------------------------------
 * </pre>
 *
 */
@ChannelHandler.Sharable
public class SharableRpcMessageCodec extends MessageToMessageCodec<ByteBuf, RpcMessage> {

    // encode RpcMessage to ByteBuf object
    @Override
    protected void encode(ChannelHandlerContext ctx, RpcMessage msg, List<Object> out) throws Exception {
        ByteBuf buf = ctx.alloc().buffer();
        MessageHeader header = msg.getHeader();
        buf.writeBytes(header.getMagicNum());
        buf.writeByte(header.getVersion());
        buf.writeByte(header.getSerializerType());
        buf.writeByte(header.getMessageType());
        buf.writeByte(header.getMessageStatus());
        buf.writeInt(header.getSequenceId());

        // get msg body
        Object body = msg.getBody();
        // get serialization algo
        Serialization serialization = SerializationFactory
                .getSerialization(SerializationType.parseByType(header.getSerializerType()));
        // serialize
        byte[] bytes = serialization.serialize(body);
        // set body length
        header.setLength(bytes.length);

        buf.writeInt(header.getLength());

        buf.writeBytes(bytes);

        out.add(buf);
    }

    // decode ByteBuf to RpcMessage object
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        int len = ProtocolConstants.MAGIC_NUM.length;
        byte[] magicNum = new byte[len];
        msg.readBytes(magicNum, 0, len);

        // validate magic number
        for (int i = 0; i < len; i++) {
            if (magicNum[i] != ProtocolConstants.MAGIC_NUM[i]) {
                throw new IllegalArgumentException("Unknown magic code: " + Arrays.toString(magicNum));
            }
        }

        byte version = msg.readByte();
        if (version != ProtocolConstants.VERSION) {
            throw new IllegalArgumentException("The version isn't compatible " + version);
        }

        byte serializeType = msg.readByte();
        byte messageType = msg.readByte();
        byte messageStatus = msg.readByte();
        int sequenceId = msg.readInt();
        int length = msg.readInt();

        byte[] bytes = new byte[length];
        msg.readBytes(bytes, 0, length);

        MessageHeader header = MessageHeader.builder()
                .magicNum(magicNum)
                .version(version)
                .serializerType(serializeType)
                .messageType(messageType)
                .sequenceId(sequenceId)
                .messageStatus(messageStatus)
                .length(length).build();

        Serialization serialization = SerializationFactory
                .getSerialization(SerializationType.parseByType(serializeType));
        MessageType type = MessageType.parseByType(messageType);
        RpcMessage protocol = new RpcMessage();
        protocol.setHeader(header);
        if (type == MessageType.REQUEST) {
            // deserialize
            RpcRequest request = serialization.deserialize(RpcRequest.class, bytes);
            protocol.setBody(request);
        } else if (type == MessageType.RESPONSE) {
            // deserialize
            RpcResponse response = serialization.deserialize(RpcResponse.class, bytes);
            protocol.setBody(response);
        } else if (type == MessageType.HEARTBEAT_REQUEST || type == MessageType.HEARTBEAT_RESPONSE) {
            String message = serialization.deserialize(String.class, bytes);
            protocol.setBody(message);
        }
        out.add(protocol);
    }
}
