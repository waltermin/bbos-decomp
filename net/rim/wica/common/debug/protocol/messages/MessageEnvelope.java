package net.rim.wica.common.debug.protocol.messages;

import net.rim.wica.common.debug.io.ByteStreamBadOnDeserialize;
import net.rim.wica.common.debug.io.IInputByteStreamAdapter;
import net.rim.wica.common.debug.io.IOutputByteStreamAdapter;

final class MessageEnvelope extends AbstractSerializableMessage implements IMessageEnvelope {
   private final int _magic = 12246278;
   private int _size;
   private int _check;
   private int _messageSet;
   private int _messageType;
   private int _sessionId;
   private int _messageId = Integer.MAX_VALUE;
   private byte[] _body;
   protected static final int MAGIC_NUMBER = 12246278;
   protected static final int MESSAGEID_NULL = Integer.MAX_VALUE;

   MessageEnvelope() {
      this.calculateCheck();
   }

   MessageEnvelope(int messageSet) {
      this.setMessageSet(messageSet);
      this.calculateCheck();
   }

   public final void deserializeHeader(IInputByteStreamAdapter stream) throws ByteStreamBadOnDeserialize {
      int magic = stream.readInt();
      if (!this.isMagic(magic)) {
         throw new ByteStreamBadOnDeserialize(((StringBuffer)(new Object("Wrong magic number: "))).append(magic).toString());
      }

      this._size = stream.readInt();
      this._check = stream.readInt();
      if (!this.isCheckOk()) {
         throw new ByteStreamBadOnDeserialize(
            ((StringBuffer)(new Object("Quick checksum failed.  Got: "))).append(this._check).append(" Expected: ").append(this.calculateCheck()).toString()
         );
      }

      this._messageSet = stream.readInt();
      this._messageType = stream.readInt();
      this._sessionId = stream.readInt();
      this._messageId = stream.readInt();
   }

   public final void serializeHeader(IOutputByteStreamAdapter stream) {
      stream.writeInt(12246278);
      stream.writeInt(this._size);
      stream.writeInt(this._check);
      stream.writeInt(this._messageSet);
      stream.writeInt(this._messageType);
      stream.writeInt(this._sessionId);
      stream.writeInt(this._messageId);
   }

   @Override
   public final void deserialize(IInputByteStreamAdapter stream) {
      this.deserializeHeader(stream);
      this._body = new byte[this._size];
      stream.readBuffer(this._size, this._body);
   }

   @Override
   public final void serialize(IOutputByteStreamAdapter stream) {
      this.serializeHeader(stream);
      byte[] body = this._body;
      if (body == null) {
         body = new byte[0];
      }

      stream.writeBuffer(this._size, body);
   }

   public final boolean isMagic(int number) {
      return number == 12246278;
   }

   public final boolean isCheckOk() {
      return this._check == this.calculateCheck();
   }

   public final int calculateCheck() {
      return 12246278 ^ ~this._size;
   }

   public final void setBody(byte[] buffer) {
      this.setBody(buffer.length, buffer);
   }

   public final void setBody(int numBytes, byte[] buffer) {
      if (numBytes > buffer.length) {
         throw new Object(((StringBuffer)(new Object("numBytes is: "))).append(numBytes).append(" but buffer length is: ").append(buffer.length).toString());
      }

      this._body = buffer;
      this._size = numBytes;
      this.setCheck();
   }

   protected final void setCheck() {
      this._check = this.calculateCheck();
   }

   private final void setMessageSet(int messageSet) {
      if (messageSet > 3) {
         throw new Object(((StringBuffer)(new Object("Attempt to set messageSet to: "))).append(messageSet).append(" max is ").append(3).toString());
      }

      this._messageSet = messageSet;
   }

   @Override
   public final int getMessageSet() {
      return this._messageSet;
   }

   @Override
   public final int getMessageId() {
      return this._messageId;
   }

   @Override
   public final void setMessageId(int messageId) {
      this._messageId = messageId;
   }

   @Override
   public final void setSessionId(int sessionId) {
      this._sessionId = sessionId;
   }

   @Override
   public final byte[] getBody() {
      return this._body;
   }

   @Override
   public final int getMessageType() {
      return this._messageType;
   }

   public final void setMessageType(int messageType) {
      this._messageType = messageType;
   }

   @Override
   public final String toString() {
      return ((StringBuffer)(new Object("Size: ")))
         .append(this._size)
         .append(", Magic Ok: ")
         .append(this.isMagic(12246278))
         .append(", Check Ok: ")
         .append(this.isCheckOk())
         .append(", Set: ")
         .append(IMessageEnvelope.MESSAGE_SET_NAMES[this._messageSet])
         .append(", Type: ")
         .append(this._messageType)
         .append(", Session Id: ")
         .append(this._sessionId)
         .append(", Message Id: ")
         .append(this._messageId)
         .toString();
   }
}
