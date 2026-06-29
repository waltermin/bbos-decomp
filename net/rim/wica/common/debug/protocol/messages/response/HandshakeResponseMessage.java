package net.rim.wica.common.debug.protocol.messages.response;

import net.rim.wica.common.debug.io.IInputByteStreamAdapter;
import net.rim.wica.common.debug.io.IOutputByteStreamAdapter;

public final class HandshakeResponseMessage extends AbstractResponseMessage implements IHandshakeResponseMessage {
   private int _handshakePattern;

   @Override
   public final void deserialize(IInputByteStreamAdapter stream) {
      this._handshakePattern = stream.readInt();
   }

   @Override
   public final void serialize(IOutputByteStreamAdapter stream) {
      stream.writeInt(this._handshakePattern);
   }

   @Override
   public final void setHandshakePattern(int handshakePattern) {
      this._handshakePattern = handshakePattern;
   }

   @Override
   public final String toString() {
      return ((StringBuffer)(new Object("Handshake response: handshakePattern="))).append(this._handshakePattern).toString();
   }
}
