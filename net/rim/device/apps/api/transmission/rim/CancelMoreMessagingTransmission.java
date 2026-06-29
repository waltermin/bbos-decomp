package net.rim.device.apps.api.transmission.rim;

import net.rim.device.api.util.DataBuffer;

public class CancelMoreMessagingTransmission extends RIMMessagingTransmission {
   private int _contentId;
   private int _refId;
   private byte[] _errorDescription;
   private byte _errorCode;
   private int _config;

   public CancelMoreMessagingTransmission() {
   }

   public CancelMoreMessagingTransmission(int contentId, int refId, byte errorCode, byte[] reason) {
      this(contentId, refId, errorCode, reason, Integer.MIN_VALUE);
   }

   public CancelMoreMessagingTransmission(int contentId, int refId, byte errorCode, byte[] reason, int config) {
      this._contentId = contentId;
      this._refId = refId;
      this._config = config;
      this._errorCode = errorCode;
      this._errorDescription = reason != null ? reason : "".getBytes();
   }

   public int getContentId() {
      return this._contentId;
   }

   public int getCMimeRefId() {
      return this._refId;
   }

   public byte[] getAbortReasonData() {
      return this._errorDescription;
   }

   public byte getErrorCode() {
      return this._errorCode;
   }

   private byte[] createErrorData(byte errorCode, byte[] description) {
      DataBuffer errorDb = new DataBuffer(true);
      errorDb.writeByte(errorCode);
      errorDb.writeByteArray(description);
      return errorDb.getArray();
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private void populateErrorDescriptionAndCode(byte[] data) {
      if (data != null) {
         boolean var4 = false /* VF: Semaphore variable */;

         try {
            var4 = true;
            DataBuffer e = new DataBuffer();
            e.setData(data, 0, data.length);
            e.readCompressedInt();
            this._errorCode = e.readByte();
            if (e.readCompressedInt() <= 0) {
               this._errorDescription = "".getBytes();
               return;
            }

            this._errorDescription = e.readByteArray();
            var4 = false;
         } finally {
            if (var4) {
               this._errorCode = 0;
               this._errorDescription = "".getBytes();
               return;
            }
         }
      }
   }

   public boolean cancelAttachmentOnly() {
      return this._contentId != Integer.MIN_VALUE;
   }

   public boolean retryAttachment() {
      return this._config == 1;
   }

   @Override
   public void read(DataBuffer packetDataBuffer) {
      CMIMEParameters params = new CMIMEParameters(3, 1);
      params.read(packetDataBuffer, (byte)0);
      this._contentId = CMIMEUtilities.getGMEInteger(params.get((byte)2), 0, Integer.MIN_VALUE);
      this._refId = CMIMEUtilities.getGMEInteger(params.get((byte)1), 0, Integer.MIN_VALUE);
      this._config = CMIMEUtilities.getGMEInteger(params.get((byte)4), 0, Integer.MIN_VALUE);
      this.populateErrorDescriptionAndCode(params.getFirst((byte)3));
   }

   @Override
   public DataBuffer write() {
      DataBuffer result = new DataBuffer(true);
      result.writeByte(8);
      result.writeByte(1);
      result.writeCompressedInt(4);
      result.writeInt(this._refId);
      result.writeByte(3);
      result.writeByteArray(this.createErrorData(this._errorCode, this._errorDescription));
      if (this._contentId != Integer.MIN_VALUE) {
         result.writeByte(2);
         result.writeCompressedInt(4);
         result.writeInt(this._contentId);
      }

      result.writeByte(0);
      return result;
   }
}
