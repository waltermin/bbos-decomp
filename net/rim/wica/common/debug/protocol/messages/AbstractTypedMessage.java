package net.rim.wica.common.debug.protocol.messages;

import net.rim.wica.common.debug.io.IInputByteStreamAdapter;
import net.rim.wica.common.debug.io.IOutputByteStreamAdapter;

public class AbstractTypedMessage extends AbstractSerializableMessage implements IBodyMessage {
   protected int _type;

   protected AbstractTypedMessage(int type) {
      this._type = type;
   }

   @Override
   public final void serialize(IOutputByteStreamAdapter stream) {
      stream.writeInt(this._type);
      this.doSerialize(stream);
   }

   @Override
   public final void deserialize(IInputByteStreamAdapter stream) {
      this._type = stream.readInt();
      this.doDeserialize(stream);
   }

   protected void doSerialize(IOutputByteStreamAdapter _1) {
      throw null;
   }

   protected void doDeserialize(IInputByteStreamAdapter _1) {
      throw null;
   }
}
