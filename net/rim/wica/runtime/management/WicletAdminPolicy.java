package net.rim.wica.runtime.management;

import net.rim.device.api.util.Persistable;
import net.rim.wica.runtime.messaging.MessageException;
import net.rim.wica.runtime.messaging.ReadableDataStream;

public final class WicletAdminPolicy implements Persistable {
   private long _id;
   private boolean _messageSendingAllowed;
   private int _inboundQueueSizeLimit;
   private int _outboundQueueSizeLimit;
   private boolean _performanceMode;
   private int _externalAccessAllowed;
   private boolean _dedicatedServerAllowed;
   private boolean _clearErrorsAllowed;

   public final long getId() {
      return this._id;
   }

   public final void setId(long id) {
      this._id = id;
   }

   public final boolean isMessageSendingAllowed() {
      return this._messageSendingAllowed;
   }

   public final void setMessageSendingAllowed(boolean messageSendingAllowed) {
      this._messageSendingAllowed = messageSendingAllowed;
   }

   public final int getInboundQueueSizeLimit() {
      return this._inboundQueueSizeLimit;
   }

   public final void setInboundQueueSizeLimit(int inboundQueueSizeLimit) {
      this._inboundQueueSizeLimit = inboundQueueSizeLimit;
   }

   public final int getOutboundQueueSizeLimit() {
      return this._outboundQueueSizeLimit;
   }

   public final void setOutboundQueueSizeLimit(int outboundQueueSizeLimit) {
      this._outboundQueueSizeLimit = outboundQueueSizeLimit;
   }

   public final boolean isPerformanceMode() {
      return this._performanceMode;
   }

   public final void setPerformanceMode(boolean performanceMode) {
      this._performanceMode = performanceMode;
   }

   public final int getExternalAccessAllowed() {
      return this._externalAccessAllowed;
   }

   public final void setExternalAccessAllowed(int externalAccessAllowed) {
      this._externalAccessAllowed = externalAccessAllowed;
   }

   public final boolean isDedicatedServerAllowed() {
      return this._dedicatedServerAllowed;
   }

   public final void setDedicatedServerAllowed(boolean dedicatedServerAllowed) {
      this._dedicatedServerAllowed = dedicatedServerAllowed;
   }

   public final boolean isClearErrorsAllowed() {
      return this._clearErrorsAllowed;
   }

   public final void setClearErrorsAllowed(boolean clearErrorsAllowed) {
      this._clearErrorsAllowed = clearErrorsAllowed;
   }

   public static final WicletAdminPolicy readFromStream(ReadableDataStream readStream) {
      try {
         if (!readStream.startComponentRead()) {
            return null;
         }

         WicletAdminPolicy policy = new WicletAdminPolicy();
         policy._id = readStream.readLong();
         policy._messageSendingAllowed = readStream.readBoolean();
         policy._inboundQueueSizeLimit = readStream.readInt();
         policy._outboundQueueSizeLimit = readStream.readInt();
         policy._performanceMode = readStream.readBoolean();
         policy._externalAccessAllowed = readStream.readInt();
         policy._dedicatedServerAllowed = readStream.readBoolean();
         policy._clearErrorsAllowed = readStream.readBoolean();
         return policy;
      } catch (MessageException e) {
         return null;
      }
   }
}
