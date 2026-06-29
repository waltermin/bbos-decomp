package net.rim.wica.transport.internal.message.messageheader;

import net.rim.wica.transport.message.TransportMessageException;
import net.rim.wica.transport.message.messageheader.MessageHeaderV1;
import net.rim.wica.transport.util.CompressedBuffer;
import net.rim.wica.transport.util.DataException;

public class MessageHeaderV1_1 extends MessageHeaderV1_Base implements MessageHeaderV1 {
   public MessageHeaderV1_1() {
   }

   public MessageHeaderV1_1(MessageHeaderV1_1 clone) {
      super(clone);
   }

   public int deserialize(CompressedBuffer buffer) {
      int offset = buffer.cursor();

      try {
         super._messageLength = buffer.readUncompressedInt();
         if (super._messageLength < 0) {
            throw new TransportMessageException(0);
         }

         byte notificationFlag = buffer.readByte();
         super._notification = (notificationFlag & 4) != 0;
         super._bgProcessingEnabled = (notificationFlag & 2) != 0;
         super._keepLast = (notificationFlag & 1) != 0;
         super._messageCode = buffer.readInt();
      } catch (DataException e) {
         throw new TransportMessageException(0);
      }

      return buffer.cursor() - offset;
   }

   public void serialize(CompressedBuffer buffer) {
      buffer.writeUncompressedInt(super._messageLength);
      buffer.writeByte((byte)((super._notification ? 4 : 0) | (super._bgProcessingEnabled ? 2 : 0) | (super._keepLast ? 1 : 0)));
      buffer.writeInt(super._messageCode);
   }

   public int getHeaderLength() {
      return 5 + CompressedBuffer.getCompressedIntSize(super._messageCode);
   }

   public void setMessageLength(int dataLength) {
      super._messageLength = dataLength;
   }
}
