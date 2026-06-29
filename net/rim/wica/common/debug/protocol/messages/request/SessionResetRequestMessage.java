package net.rim.wica.common.debug.protocol.messages.request;

import net.rim.wica.common.debug.io.IInputByteStreamAdapter;
import net.rim.wica.common.debug.io.IOutputByteStreamAdapter;

public final class SessionResetRequestMessage extends AbstractRequestMessage implements ISessionResetRequestMessage {
   private int _newSessionId;

   SessionResetRequestMessage() {
   }

   @Override
   public final void serialize(IOutputByteStreamAdapter stream) {
      stream.writeInt(this._newSessionId);
   }

   @Override
   public final void deserialize(IInputByteStreamAdapter stream) {
      this._newSessionId = stream.readInt();
   }

   @Override
   public final int getNewSessionId() {
      return this._newSessionId;
   }

   @Override
   public final String toString() {
      return "Session Reset Request: sessionId=" + this._newSessionId;
   }
}
