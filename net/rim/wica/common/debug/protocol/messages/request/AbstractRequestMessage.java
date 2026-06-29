package net.rim.wica.common.debug.protocol.messages.request;

import net.rim.wica.common.debug.io.IInputByteStreamAdapter;
import net.rim.wica.common.debug.io.IOutputByteStreamAdapter;

class AbstractRequestMessage implements IRequestMessage {
   @Override
   public void deserialize(IInputByteStreamAdapter _1) {
      throw null;
   }

   @Override
   public void serialize(IOutputByteStreamAdapter _1) {
      throw null;
   }
}
