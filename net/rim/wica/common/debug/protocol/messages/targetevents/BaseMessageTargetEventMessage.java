package net.rim.wica.common.debug.protocol.messages.targetevents;

import net.rim.wica.common.debug.io.IInputByteStreamAdapter;
import net.rim.wica.common.debug.io.IOutputByteStreamAdapter;

class BaseMessageTargetEventMessage extends ApplicationTargetEventMessage implements IBaseMessageTargetEventMessage {
   private String _type;
   private long _correlationId;

   @Override
   public void serialize(IOutputByteStreamAdapter stream) {
      super.serialize(stream);
      stream.writeString(this._type);
      stream.writeLong(this._correlationId);
   }

   @Override
   public void deserialize(IInputByteStreamAdapter stream) {
      super.deserialize(stream);
      this._type = stream.readString();
      this._correlationId = stream.readLong();
   }

   public String getType() {
      return this._type;
   }

   @Override
   public void setType(String type) {
      this._type = type;
   }

   @Override
   public void setCorrelationId(long correlationId) {
      this._correlationId = correlationId;
   }

   @Override
   public String toString() {
      return ((StringBuffer)(new Object("Message target event: messageType = "))).append(this.getType()).toString();
   }
}
