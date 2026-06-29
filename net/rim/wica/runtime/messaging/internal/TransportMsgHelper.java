package net.rim.wica.runtime.messaging.internal;

import net.rim.wica.runtime.messaging.Message;
import net.rim.wica.transport.message.TransportMessageHandler;
import net.rim.wica.transport.message.TransportMessageV1;
import net.rim.wica.transport.message.TransportMessageV2;

public class TransportMsgHelper extends TransportMessageHandler {
   private Message _message;

   @Override
   public void handleMessage(TransportMessageV1 tm) {
      this._message = new MessageV1Impl(tm);
   }

   @Override
   public void handleMessage(TransportMessageV2 tm) {
      this._message = new MessageV2Impl(tm);
   }

   public Message getMessage() {
      return this._message;
   }
}
