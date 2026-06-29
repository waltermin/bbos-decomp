package net.rim.wica.common.debug.protocol.messages.request;

import net.rim.wica.common.debug.io.IInputByteStreamAdapter;
import net.rim.wica.common.debug.io.IOutputByteStreamAdapter;

final class ApplicationCommandRequestMessage implements IApplicationCommandRequestMessage {
   private long _applicationId;

   @Override
   public final void serialize(IOutputByteStreamAdapter stream) {
      stream.writeLong(this._applicationId);
   }

   @Override
   public final void deserialize(IInputByteStreamAdapter stream) {
      this._applicationId = stream.readLong();
   }

   @Override
   public final long getApplicationId() {
      return this._applicationId;
   }

   @Override
   public final String toString() {
      return "Application command request: app id = " + this.getApplicationId();
   }
}
