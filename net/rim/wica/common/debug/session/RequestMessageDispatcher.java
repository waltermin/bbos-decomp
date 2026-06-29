package net.rim.wica.common.debug.session;

import net.rim.wica.common.debug.msgmgt.MessageReceiver;
import net.rim.wica.common.debug.protocol.messages.IBodyMessage;
import net.rim.wica.common.debug.protocol.messages.IMessageEnvelope;
import net.rim.wica.common.debug.protocol.messages.request.IRequestMessage;

public final class RequestMessageDispatcher extends AbstractMessageDispatcher {
   private MessageReceiver _msgReceiver;
   private IRequestMessageHandler _msgHandler;

   public RequestMessageDispatcher(MessageReceiver msgReceiver, IRequestMessageHandler msgHandler) {
      this._msgReceiver = msgReceiver;
      this._msgHandler = msgHandler;
   }

   public final boolean processNextMessage() {
      IMessageEnvelope msgEnvelope = this.getNextMessage(this._msgReceiver);
      if (msgEnvelope == null) {
         throw new RuntimeException();
      }

      IBodyMessage bodyMessage = this.getBodyMessage(msgEnvelope);
      if (msgEnvelope != null && bodyMessage != null) {
         switch (msgEnvelope.getMessageSet()) {
            case 0:
               IRequestMessage request = (IRequestMessage)bodyMessage;
               this._msgHandler.handleRequestMessage(msgEnvelope.getMessageType(), msgEnvelope.getMessageId(), request);
               return true;
            default:
               throw new RuntimeException("Unsupported message set: " + msgEnvelope.getMessageSet());
         }
      } else {
         return false;
      }
   }
}
