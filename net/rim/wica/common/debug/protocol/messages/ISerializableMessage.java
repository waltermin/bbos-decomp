package net.rim.wica.common.debug.protocol.messages;

import net.rim.wica.common.debug.io.IInputByteStreamAdapter;
import net.rim.wica.common.debug.io.IOutputByteStreamAdapter;

public interface ISerializableMessage {
   void serialize(IOutputByteStreamAdapter var1);

   void deserialize(IInputByteStreamAdapter var1);
}
