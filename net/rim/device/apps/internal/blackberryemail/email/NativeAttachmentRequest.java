package net.rim.device.apps.internal.blackberryemail.email;

import java.lang.ref.WeakReference;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.util.IntHashtable;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.transmission.rim.RIMMessagingIncomingMoreRequest;

public class NativeAttachmentRequest {
   private int _contentPartId;
   private WeakReference _message;
   private int _offset;
   private int _length;
   private byte[] _componentId;
   private ServiceRecord _serviceRecord;
   private int _transmissionId;
   private int _cmimeRefId;
   private long _timestamp;
   private boolean _shouldResend;
   private boolean _requestFailed;
   private IntHashtable _chunks;
   public static final int BAD_INT_VALUE;
   public static final int MAX_RETRIES;
   public static final long MAX_SEND_TIME;
   public static final long INVALID_TIME;

   public void setShouldResend(boolean resend) {
      this._shouldResend = resend;
   }

   public void setRequestFailed(boolean failed) {
      this._requestFailed = failed;
   }

   public boolean hasFailed() {
      return this._requestFailed;
   }

   public boolean hasExpired() {
      if (this._timestamp == -1) {
         return false;
      }

      long currentTime = System.currentTimeMillis();
      return this._timestamp > currentTime ? true : currentTime - this._timestamp > 1800000;
   }

   public void updateTimestamp() {
      this._timestamp = System.currentTimeMillis();
   }

   public void removeAllChunks() {
      this._chunks.clear();
      this._shouldResend = false;
   }

   public static NativeAttachmentRequest createNativeAttachmentRequest(RIMMessagingIncomingMoreRequest messagingIncomingMoreRequest, Object contextObject) {
      if (messagingIncomingMoreRequest == null) {
         String logStr = "createNativeRequest: more request is null.";
         EventLogger.logEvent(-1237457833540244999L, logStr.getBytes(), 0);
         return null;
      }

      EmailMessageModel message = NativeAttachmentRequestProcessor$Helper.getMessageFromParameters(
         messagingIncomingMoreRequest.getMessageRefId(), contextObject
      );
      if (message == null) {
         String logStr = "createNativeRequest: message is null.";
         EventLogger.logEvent(-1237457833540244999L, logStr.getBytes(), 0);
         return null;
      }

      ServiceRecord serviceRecord = null;
      if (contextObject instanceof Object) {
         serviceRecord = (ServiceRecord)ContextObject.get(contextObject, -6095803566992128485L);
         if (serviceRecord == null) {
            String logStr = "createNativeRequest: service is null.";
            EventLogger.logEvent(-1237457833540244999L, logStr.getBytes(), 0);
            return null;
         }
      }

      NativeAttachmentRequest nativeAttachmentRequest = new NativeAttachmentRequest();
      nativeAttachmentRequest._chunks = (IntHashtable)(new Object());
      nativeAttachmentRequest._timestamp = -1;
      nativeAttachmentRequest.setComponentId(messagingIncomingMoreRequest.getComponentId());
      nativeAttachmentRequest.setContentPartId(messagingIncomingMoreRequest.getContentPartId());
      nativeAttachmentRequest.setOffset(messagingIncomingMoreRequest.getOffset());
      nativeAttachmentRequest.setLength(messagingIncomingMoreRequest.getLength());
      nativeAttachmentRequest.setCmimeRefId(message == null ? Integer.MIN_VALUE : message.getCMIMEReferenceIdentifier());
      nativeAttachmentRequest.setMessage(message);
      nativeAttachmentRequest.setServiceRecord(serviceRecord);
      return nativeAttachmentRequest;
   }

   public byte[] getComponentId() {
      return this._componentId;
   }

   public void setComponentId(byte[] componentId) {
      this._componentId = componentId;
   }

   public int getContentPartId() {
      return this._contentPartId;
   }

   public void setContentPartId(int contentPartId) {
      this._contentPartId = contentPartId;
   }

   public int getLength() {
      return this._length;
   }

   public void setLength(int length) {
      this._length = length;
   }

   public EmailMessageModel getMessage() {
      return (EmailMessageModel)this._message.get();
   }

   public void setMessage(EmailMessageModel message) {
      this._message = (WeakReference)(new Object(message));
   }

   public int getOffset() {
      return this._offset;
   }

   public void setOffset(int offset) {
      this._offset = offset;
   }

   public boolean isCompleted() {
      return this._chunks.size() == 0;
   }

   public ServiceRecord getServiceRecord() {
      return this._serviceRecord;
   }

   public void setServiceRecord(ServiceRecord serviceRecord) {
      this._serviceRecord = serviceRecord;
   }

   public int getTransmissionId() {
      return this._transmissionId;
   }

   public boolean shouldResend() {
      return this._shouldResend && !this._requestFailed;
   }

   public void setTransmissionId(int transmissionId) {
      this._transmissionId = transmissionId;
   }

   public int getCmimeRefId() {
      return this._cmimeRefId;
   }

   public void setCmimeRefId(int cmimeRefId) {
      this._cmimeRefId = cmimeRefId;
   }

   public void addChunk(int offset, int length, int id) {
      NativeAttachmentRequest$NativeAttachmentChunk chunk = new NativeAttachmentRequest$NativeAttachmentChunk(offset, length, id);
      this._chunks.put(id, chunk);
   }

   public IntHashtable getChunks() {
      return this._chunks;
   }

   public boolean updateStatus(int chunkId, int status) {
      NativeAttachmentRequest$NativeAttachmentChunk chunk = (NativeAttachmentRequest$NativeAttachmentChunk)this._chunks.get(chunkId);
      if (chunk != null) {
         if (status == 0) {
            this._chunks.remove(chunkId);
         } else if (isNonFatalError(status)) {
            if (chunk.getResendCount() < 3) {
               this.setShouldResend(true);
            } else {
               this.setRequestFailed(true);
            }
         } else if (isFatalError(status)) {
            this.setRequestFailed(true);
         }

         chunk.setStatus(status);
         return true;
      } else {
         return false;
      }
   }

   public static boolean isFatalError(int code) {
      return (code & 128) != 0 && !isNonFatalError(code);
   }

   public static boolean isNonFatalError(int code) {
      return code == 4243 || code == 4560;
   }
}
