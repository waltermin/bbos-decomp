package net.rim.wica.runtime.messaging.internal.inbound;

import net.rim.wica.runtime.messaging.internal.util.InternalLogger;
import net.rim.wica.runtime.messaging.internal.util.Provider;
import net.rim.wica.transport.message.TransportMessageFactory;
import net.rim.wica.transport.security.SecureMessageHandler;
import net.rim.wica.transport.security.SecureMessageV1;

public class InboundSecureMsgHandler extends SecureMessageHandler {
   private TransportMessageFactory _transportMsgFactory;
   private InboundTransportMsgHandler _transportMsgHandler;
   static Class class$net$rim$wica$transport$message$TransportMessageFactory;

   InboundSecureMsgHandler(Provider provider) {
      this._transportMsgFactory = (TransportMessageFactory)provider.getService(
         class$net$rim$wica$transport$message$TransportMessageFactory == null
            ? (class$net$rim$wica$transport$message$TransportMessageFactory = class$("net.rim.wica.transport.message.TransportMessageFactory"))
            : class$net$rim$wica$transport$message$TransportMessageFactory
      );
      this._transportMsgHandler = new InboundTransportMsgHandler(provider);
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public void handleMessage(SecureMessageV1 message) {
      try {
         message.verifySecurity();
         this._transportMsgFactory.handleMessage(message.getPayload(), this._transportMsgHandler);
      } catch (Throwable var4) {
         InternalLogger.logError(this, null, t, null);
         return;
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
         throw new NoClassDefFoundError(x1.getMessage());
      }
   }
}
