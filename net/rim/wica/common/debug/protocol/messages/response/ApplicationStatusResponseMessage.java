package net.rim.wica.common.debug.protocol.messages.response;

import net.rim.wica.common.debug.io.IInputByteStreamAdapter;
import net.rim.wica.common.debug.io.IOutputByteStreamAdapter;

final class ApplicationStatusResponseMessage extends ApplicationCommandResponseMessage implements IApplicationStatusResponseMessage {
   private boolean _running;

   @Override
   public final void serialize(IOutputByteStreamAdapter stream) {
      super.serialize(stream);
      stream.writeBoolean(this._running);
   }

   @Override
   public final void deserialize(IInputByteStreamAdapter stream) {
      super.deserialize(stream);
      this._running = stream.readBoolean();
   }

   public final boolean isRunning() {
      return this._running;
   }

   @Override
   public final void setIsRunning(boolean running) {
      this._running = running;
   }

   @Override
   public final String toString() {
      return "Application Status Response: app id=" + this.getApplicationId() + ", isRunning=" + this.isRunning();
   }
}
