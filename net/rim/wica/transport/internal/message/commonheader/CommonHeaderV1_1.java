package net.rim.wica.transport.internal.message.commonheader;

import net.rim.wica.transport.message.TransportMessageException;
import net.rim.wica.transport.message.commonheader.CommonHeaderV1;
import net.rim.wica.transport.util.CompressedBuffer;
import net.rim.wica.transport.util.DataException;

public class CommonHeaderV1_1 extends CommonHeaderV1_Base implements CommonHeaderV1 {
   public CommonHeaderV1_1() {
   }

   public CommonHeaderV1_1(CommonHeaderV1_1 clone) {
      super(clone);
   }

   public int deserialize(CompressedBuffer buffer) throws TransportMessageException {
      int offset = buffer.cursor();

      try {
         super._version = buffer.readUnsignedByte();
         byte next = buffer.readByte();
         super._more = (next & 1) != 0;
         super._messageCount = buffer.readByte() & 255;
         super._senderId = buffer.readLong();
         super._wicletId = buffer.readLong();
      } catch (DataException e) {
         throw new TransportMessageException(0);
      }

      if (super._version < 0) {
         throw new TransportMessageException(0);
      } else if (super._messageCount < 1) {
         throw new TransportMessageException(0);
      } else {
         return buffer.cursor() - offset;
      }
   }

   public void serialize(CompressedBuffer buffer) {
      buffer.writeByte((byte)super._version);
      buffer.writeByte((byte)(super._more ? 1 : 0));
      buffer.writeByte((byte)super._messageCount);
      buffer.writeLong(super._senderId);
      buffer.writeLong(super._wicletId);
   }

   public int getHeaderLength() {
      return 4 + CompressedBuffer.getCompressedLongSize(super._senderId) + CompressedBuffer.getCompressedLongSize(super._wicletId);
   }
}
