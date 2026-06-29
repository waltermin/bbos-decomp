package net.rim.wica.common.debug.protocol.messages.request;

import net.rim.wica.common.debug.io.IInputByteStreamAdapter;
import net.rim.wica.common.debug.io.IOutputByteStreamAdapter;

public final class HandshakeRequestMessage extends AbstractRequestMessage implements IHandshakeRequestMessage {
   private int _handshakePattern;

   HandshakeRequestMessage() {
   }

   @Override
   public final void serialize(IOutputByteStreamAdapter stream) {
      stream.writeInt(this._handshakePattern);
   }

   @Override
   public final void deserialize(IInputByteStreamAdapter stream) {
      this._handshakePattern = stream.readInt();
   }

   @Override
   public final int getHandshakePattern() {
      return this._handshakePattern;
   }

   @Override
   public final String toString() {
      return "Handshake request:  pattern=" + this._handshakePattern;
   }
}
