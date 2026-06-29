package net.rim.wica.common.debug.protocol.messages.response;

import net.rim.wica.common.debug.io.IInputByteStreamAdapter;
import net.rim.wica.common.debug.io.IOutputByteStreamAdapter;

class ApplicationCommandResponseMessage implements IApplicationCommandResponseMessage {
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
      return ((StringBuffer)(new Object("Application command response: appid = "))).append(this.getApplicationId()).toString();
   }
}
