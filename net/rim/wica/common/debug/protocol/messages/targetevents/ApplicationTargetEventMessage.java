package net.rim.wica.common.debug.protocol.messages.targetevents;

import net.rim.wica.common.debug.io.IInputByteStreamAdapter;
import net.rim.wica.common.debug.io.IOutputByteStreamAdapter;

class ApplicationTargetEventMessage implements IApplicationTargetEventMessage {
   private long _applicationId;

   @Override
   public void serialize(IOutputByteStreamAdapter stream) {
      stream.writeLong(this._applicationId);
   }

   @Override
   public void deserialize(IInputByteStreamAdapter stream) {
      this._applicationId = stream.readLong();
   }

   public long getApplicationId() {
      return this._applicationId;
   }

   @Override
   public void setApplicationId(long applicationId) {
      this._applicationId = applicationId;
   }

   @Override
   public String toString() {
      return "Application target event: appId = " + this.getApplicationId();
   }
}
