package net.rim.wica.common.debug.msgmgt;

import net.rim.wica.common.debug.io.IOutputByteStreamAdapter;
import net.rim.wica.common.debug.protocol.messages.IMessageEnvelope;

public final class MessageSender {
   private MessageQueue _queue = new MessageQueue();
   private IOutputByteStreamAdapter _outStream;

   public MessageSender(IOutputByteStreamAdapter outStream) {
      this._outStream = outStream;
   }

   public final boolean getMessageAndDispatch(int timeOutMs) {
      IMessageEnvelope msg = this._queue.get(timeOutMs);
      if (msg != null) {
         msg.serialize(this._outStream);
         this._outStream.flush();
         return true;
      } else {
         return false;
      }
   }

   public final void send(IMessageEnvelope msg) {
      this._queue.add(msg);
   }
}
