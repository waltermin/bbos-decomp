package net.rim.wica.common.debug.protocol.messages.response;

import net.rim.wica.common.debug.io.IInputByteStreamAdapter;
import net.rim.wica.common.debug.io.IOutputByteStreamAdapter;

public final class SessionResetResponseMessage extends AbstractResponseMessage implements ISessionResetResponseMessage {
   private int _newSessionId;

   SessionResetResponseMessage() {
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
   public final void setNewSessionId(int newSessionId) {
      this._newSessionId = newSessionId;
   }

   @Override
   public final String toString() {
      return "Session Reset Response: newSessionId: " + this._newSessionId;
   }
}
