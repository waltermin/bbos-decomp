package net.rim.wica.common.debug.protocol.messages.response;

import net.rim.wica.common.debug.io.IInputByteStreamAdapter;
import net.rim.wica.common.debug.io.IOutputByteStreamAdapter;

public final class DetachResponseMessage extends AbstractResponseMessage implements IDetachResponseMessage {
   private int _sessionId;

   DetachResponseMessage() {
   }

   @Override
   public final void deserialize(IInputByteStreamAdapter stream) {
      this._sessionId = stream.readInt();
   }

   @Override
   public final void serialize(IOutputByteStreamAdapter stream) {
      stream.writeInt(this._sessionId);
   }

   @Override
   public final void setSessionId(int sessionId) {
      this._sessionId = sessionId;
   }

   @Override
   public final String toString() {
      return "Detach response: sessionId=" + this._sessionId;
   }
}
