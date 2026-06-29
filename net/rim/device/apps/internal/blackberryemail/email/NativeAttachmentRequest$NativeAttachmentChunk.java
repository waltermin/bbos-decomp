package net.rim.device.apps.internal.blackberryemail.email;

class NativeAttachmentRequest$NativeAttachmentChunk {
   private int _offset;
   private int _length;
   private int _transmissionId;
   private int _status;
   private int _resendCount;

   public int getOffset() {
      return this._offset;
   }

   public int getStatus() {
      return this._status;
   }

   public int getLength() {
      return this._length;
   }

   public int getID() {
      return this._transmissionId;
   }

   public void setStatus(int status) {
      this._status = status;
   }

   public void clearStatus() {
      this._status = -1;
   }

   public void incrementResendCount() {
      this._resendCount++;
   }

   public int getResendCount() {
      return this._resendCount;
   }

   public void setID(int id) {
      this._transmissionId = id;
   }

   public boolean shouldResend() {
      return this._resendCount < 3 && (NativeAttachmentRequest.isNonFatalError(this._status) || this._status == 1);
   }

   public NativeAttachmentRequest$NativeAttachmentChunk(int offset, int length, int id) {
      this._offset = offset;
      this._length = length;
      this._status = -1;
      this._resendCount = 0;
      this._transmissionId = id;
   }
}
