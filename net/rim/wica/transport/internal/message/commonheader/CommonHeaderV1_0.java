package net.rim.wica.transport.internal.message.commonheader;

import net.rim.wica.transport.message.TransportMessageException;
import net.rim.wica.transport.message.commonheader.CommonHeaderV1;
import net.rim.wica.transport.util.CompressedBuffer;
import net.rim.wica.transport.util.DataException;

public class CommonHeaderV1_0 extends CommonHeaderV1_Base implements CommonHeaderV1 {
   private int _deliveryMode;
   protected int _securityMode;
   private int _securityVersion;

   public CommonHeaderV1_0() {
   }

   public CommonHeaderV1_0(CommonHeaderV1_0 clone) {
      super(clone);
      this._deliveryMode = clone.getDeliveryMode();
      this._securityMode = clone.getSecurityMode();
      this._securityVersion = clone.getSecurityVersion();
   }

   public int getDeliveryMode() {
      return this._deliveryMode;
   }

   public void setDeliveryMode(int deliveryMode) {
      if (deliveryMode != 0 && deliveryMode != 1 && deliveryMode != 2) {
         throw new Object();
      }

      this._deliveryMode = deliveryMode;
   }

   public int getSecurityMode() {
      return this._securityMode;
   }

   public void setSecurityMode(int mode) {
      this._securityMode = mode;
   }

   public int getSecurityVersion() {
      return this._securityVersion;
   }

   public void setSecurityVersion(int version) {
      this._securityVersion = version;
   }

   public boolean isUnsecure() {
      return this._securityMode == 2;
   }

   public boolean isSignedOnly() {
      return this._securityMode == 0;
   }

   public boolean isSignedAndEncrypted() {
      return this._securityMode == 1;
   }

   public int deserialize(CompressedBuffer buffer) {
      int offset = buffer.cursor();

      try {
         super._version = buffer.readUnsignedByte();
         byte next = buffer.readByte();
         super._more = (next & 128) != 0;
         this._deliveryMode = (next & 96) >>> 5;
         this._securityMode = (next & 16) >>> 4;
         this._securityVersion = next & 15;
         super._messageCount = buffer.readByte() & 255;
         super._senderId = buffer.readLong();
         super._wicletId = buffer.readLong();
      } catch (DataException e) {
         throw new TransportMessageException(0);
      }

      if (this._deliveryMode != 0 && this._deliveryMode != 1 && this._deliveryMode != 2) {
         throw new TransportMessageException(0);
      }

      if (super._messageCount < 1) {
         throw new TransportMessageException(0);
      }

      if ((super._version | this._securityVersion) < 0) {
         throw new TransportMessageException(0);
      }

      if (this._securityVersion > 0 && this._securityMode != 0 && this._securityMode != 1) {
         throw new TransportMessageException(0);
      }

      if (this._securityVersion == 0) {
         this._securityMode = 2;
      }

      return buffer.cursor() - offset;
   }

   public void serialize(CompressedBuffer buffer) {
      buffer.writeByte((byte)super._version);
      buffer.writeByte((byte)((super._more ? 128 : 0) | this._deliveryMode << 5 & 96 | this._securityMode << 4 & 16 | this._securityVersion & 15));
      buffer.writeByte((byte)super._messageCount);
      buffer.writeLong(super._senderId);
      buffer.writeLong(super._wicletId);
   }

   public int getHeaderLength() {
      return 3 + CompressedBuffer.getCompressedLongSize(super._senderId) + CompressedBuffer.getCompressedLongSize(super._wicletId);
   }
}
