package net.rim.wica.common.debug.protocol.messages.targetevents;

import net.rim.wica.common.debug.io.IInputByteStreamAdapter;
import net.rim.wica.common.debug.io.IOutputByteStreamAdapter;

final class TraceTargetEventMessage extends ApplicationTargetEventMessage implements ITraceTargetEventMessage {
   private String _message;

   @Override
   public final void serialize(IOutputByteStreamAdapter stream) {
      super.serialize(stream);
      stream.writeString(this._message);
   }

   @Override
   public final void deserialize(IInputByteStreamAdapter stream) {
      super.deserialize(stream);
      this._message = stream.readString();
   }

   public final String getMessage() {
      return this._message;
   }

   @Override
   public final void setMessage(String message) {
      this._message = message;
   }

   @Override
   public final String toString() {
      return ((StringBuffer)(new Object("Trace Event: message="))).append(this.getMessage()).toString();
   }
}
