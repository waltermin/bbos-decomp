package net.rim.wica.transport.internal.message.messageheader;

import net.rim.wica.transport.message.TransportMessageException;
import net.rim.wica.transport.message.messageheader.MessageHeaderV1;
import net.rim.wica.transport.util.CompressedBuffer;
import net.rim.wica.transport.util.DataException;

public class MessageHeaderV1_0 extends MessageHeaderV1_Base implements MessageHeaderV1 {
   public MessageHeaderV1_0() {
   }

   public MessageHeaderV1_0(MessageHeaderV1_0 clone) {
      super(clone);
   }

   public int deserialize(CompressedBuffer buffer) {
      try {
         super._messageLength = buffer.readUncompressedInt();
         if (super._messageLength < 0) {
            throw new TransportMessageException(0);
         }

         byte notificationFlag = buffer.readByte();
         super._notification = (notificationFlag & 4) != 0;
         super._bgProcessingEnabled = (notificationFlag & 2) != 0;
         super._keepLast = (notificationFlag & 1) != 0;
         super._messageCode = 0 | buffer.readUnsignedByte() << 16 | buffer.readUnsignedByte() << 8 | buffer.readUnsignedByte();
         return 8;
      } catch (DataException e) {
         throw new TransportMessageException(0);
      }
   }

   public void serialize(CompressedBuffer buffer) {
      buffer.writeUncompressedInt(super._messageLength);
      buffer.writeByte((byte)((super._notification ? 4 : 0) | (super._bgProcessingEnabled ? 2 : 0) | (super._keepLast ? 1 : 0)));
      buffer.writeByte((byte)(super._messageCode >>> 16));
      buffer.writeByte((byte)(super._messageCode >>> 8));
      buffer.writeByte((byte)super._messageCode);
   }

   public int getHeaderLength() {
      return 8;
   }

   public void setMessageLength(int dataLength) {
      super._messageLength = this.getHeaderLength() + dataLength;
   }
}
