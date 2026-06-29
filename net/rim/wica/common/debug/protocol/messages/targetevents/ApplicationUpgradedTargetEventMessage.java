package net.rim.wica.common.debug.protocol.messages.targetevents;

import net.rim.wica.common.debug.io.IInputByteStreamAdapter;
import net.rim.wica.common.debug.io.IOutputByteStreamAdapter;

final class ApplicationUpgradedTargetEventMessage extends ApplicationTargetEventMessage implements IApplicationUpgradedTargetEventMessage {
   private long _newApplicationId;

   @Override
   public final void serialize(IOutputByteStreamAdapter stream) {
      super.serialize(stream);
      stream.writeLong(this._newApplicationId);
   }

   @Override
   public final void deserialize(IInputByteStreamAdapter stream) {
      super.deserialize(stream);
      this._newApplicationId = stream.readLong();
   }

   public final long getNewApplicationId() {
      return this._newApplicationId;
   }

   @Override
   public final void setNewApplicationId(long newApplicationId) {
      this._newApplicationId = newApplicationId;
   }

   @Override
   public final String toString() {
      return "Application upgraded event: old app id = " + this.getApplicationId() + ", new app id = " + this.getNewApplicationId();
   }
}
