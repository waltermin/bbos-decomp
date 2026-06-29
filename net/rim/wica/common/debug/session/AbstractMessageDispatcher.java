package net.rim.wica.common.debug.session;

import net.rim.wica.common.debug.msgmgt.MessageReceiver;
import net.rim.wica.common.debug.protocol.messages.IBodyMessage;
import net.rim.wica.common.debug.protocol.messages.IMessageEnvelope;
import net.rim.wica.common.debug.protocol.messages.MessageFactory;

public class AbstractMessageDispatcher {
   protected IMessageEnvelope getNextMessage(MessageReceiver msgReceiver) {
      return msgReceiver.receive(0);
   }

   protected IBodyMessage getBodyMessage(IMessageEnvelope msgEnvelope) {
      if (msgEnvelope != null) {
         try {
            return MessageFactory.extractBodyMessage(msgEnvelope);
         } finally {
            return null;
         }
      } else {
         return null;
      }
   }
}
