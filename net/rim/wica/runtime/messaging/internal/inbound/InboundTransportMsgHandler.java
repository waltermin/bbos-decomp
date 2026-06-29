package net.rim.wica.runtime.messaging.internal.inbound;

import net.rim.wica.runtime.messaging.Message;
import net.rim.wica.runtime.messaging.MessagingException;
import net.rim.wica.runtime.messaging.MessagingService;
import net.rim.wica.runtime.messaging.internal.MessageV1Impl;
import net.rim.wica.runtime.messaging.internal.MessageV2Impl;
import net.rim.wica.runtime.messaging.internal.MessagingServiceImpl;
import net.rim.wica.runtime.messaging.internal.util.InternalLogger;
import net.rim.wica.runtime.messaging.internal.util.Provider;
import net.rim.wica.transport.message.TransportMessageHandler;
import net.rim.wica.transport.message.TransportMessageV1;
import net.rim.wica.transport.message.TransportMessageV2;

public class InboundTransportMsgHandler extends TransportMessageHandler {
   private MessagingService _messaging;
   static Class class$net$rim$wica$runtime$messaging$MessagingService;

   public InboundTransportMsgHandler(Provider provider) {
      this._messaging = (MessagingServiceImpl)provider.getService(
         class$net$rim$wica$runtime$messaging$MessagingService == null
            ? (class$net$rim$wica$runtime$messaging$MessagingService = class$("net.rim.wica.runtime.messaging.MessagingService"))
            : class$net$rim$wica$runtime$messaging$MessagingService
      );
   }

   @Override
   public void handleMessage(TransportMessageV1 tm) {
      this.handleMessage(new MessageV1Impl(tm));
   }

   @Override
   public void handleMessage(TransportMessageV2 tm) {
      this.handleMessage(new MessageV2Impl(tm));
   }

   private void handleMessage(Message m) {
      m.setDestinationType(m.getWicletID() == 0 ? 3 : 1);

      try {
         this._messaging.sendMessage(m);
      } catch (MessagingException e) {
         InternalLogger.logError(this, null, e, m);
      }
   }

   @Override
   public String toString() {
      return "MDS Runtime Messaging.InboundProcessor";
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   static Class class$(String x0) {
      try {
         return Class.forName(x0);
      } catch (Throwable var3) {
         throw new Object(x1.getMessage());
      }
   }
}
