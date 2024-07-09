package com.cheeterlee.rpc.core.protocol;

import com.cheeterlee.rpc.core.constant.ProtocolConstants;
import com.cheeterlee.rpc.core.enums.MessageType;
import com.cheeterlee.rpc.core.enums.SerializationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <pre>
 *   --------------------------------------------------------------------
 *  | magic num (4byte) | version (1byte)  | serialization algo (1byte)  | message type (1byte) |
 *  -------------------------------------------------------------------
 *  |  message status (1byte)  |     sequence id (4byte)   |     message len (4byte)    |
 *  --------------------------------------------------------------------
 * </pre>
 *
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageHeader {

    private byte[] magicNum;

    private byte version;

    private byte serializerType;

    private byte messageType;

    private byte messageStatus;

    private int sequenceId;


    private int length;

    public static MessageHeader build(String serializeName) {
        return MessageHeader.builder()
                .magicNum(ProtocolConstants.MAGIC_NUM)
                .version(ProtocolConstants.VERSION)
                .serializerType(SerializationType.parseByName(serializeName).getType())
                .messageType(MessageType.REQUEST.getType())
                .sequenceId(ProtocolConstants.getSequenceId())
                .build();
    }
}

