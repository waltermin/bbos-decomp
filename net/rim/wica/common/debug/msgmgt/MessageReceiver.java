package net.rim.wica.common.debug.msgmgt;

import net.rim.wica.common.debug.io.IInputByteStreamAdapter;
import net.rim.wica.common.debug.protocol.messages.IMessageEnvelope;
import net.rim.wica.common.debug.protocol.messages.MessageFactory;

public final class MessageReceiver {
   private MessageQueue _queue = new MessageQueue();
   private IInputByteStreamAdapter _inStream;

   public MessageReceiver(IInputByteStreamAdapter inStream) {
      this._inStream = inStream;
   }

   public final void getAndEnqueueMessage() {
      IMessageEnvelope msg = MessageFactory.createEmptyMessageEnvelope();
      msg.deserialize(this._inStream);
      this._queue.add(msg);
   }

   public final IMessageEnvelope receive(int timeOutMs) {
      return this._queue.get(timeOutMs);
   }
}
