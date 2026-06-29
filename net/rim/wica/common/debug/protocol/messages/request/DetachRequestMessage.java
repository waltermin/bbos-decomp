package net.rim.wica.common.debug.protocol.messages.request;

import net.rim.wica.common.debug.io.IInputByteStreamAdapter;
import net.rim.wica.common.debug.io.IOutputByteStreamAdapter;

public final class DetachRequestMessage extends AbstractRequestMessage implements IDetachRequestMessage {
   private int _sessionId;

   DetachRequestMessage() {
   }

   @Override
   public final void serialize(IOutputByteStreamAdapter stream) {
      stream.writeInt(this._sessionId);
   }

   @Override
   public final void deserialize(IInputByteStreamAdapter stream) {
      this._sessionId = stream.readInt();
   }

   @Override
   public final int getSessionId() {
      return this._sessionId;
   }

   @Override
   public final String toString() {
      return "Detach Request: sessionId=" + this._sessionId;
   }
}
