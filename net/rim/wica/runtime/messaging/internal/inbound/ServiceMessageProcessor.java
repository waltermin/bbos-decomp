package net.rim.wica.runtime.messaging.internal.inbound;

import java.util.Hashtable;
import net.rim.wica.runtime.messaging.Message;
import net.rim.wica.runtime.messaging.MessageConsumer;
import net.rim.wica.runtime.messaging.MessagingException;
import net.rim.wica.runtime.messaging.internal.MessageHandler;
import net.rim.wica.runtime.messaging.internal.MessagingServiceImpl;
import net.rim.wica.runtime.messaging.internal.util.InternalLogger;
import net.rim.wica.runtime.messaging.internal.util.Provider;

public final class ServiceMessageProcessor implements MessageHandler {
   Hashtable _consumerTable = new Hashtable(8);
   static Class class$net$rim$wica$runtime$messaging$MessagingService;

   public final void initialize(Provider provider) {
      MessagingServiceImpl messagingService = (MessagingServiceImpl)provider.getService(
         class$net$rim$wica$runtime$messaging$MessagingService == null
            ? (class$net$rim$wica$runtime$messaging$MessagingService = class$("net.rim.wica.runtime.messaging.MessagingService"))
            : class$net$rim$wica$runtime$messaging$MessagingService
      );
      messagingService.registerMessageHandler(2, this);
   }

   public final void startup() {
   }

   public final void activate() {
   }

   public final void deactivate() {
   }

   public final void registerServiceMessageConsumer(String serviceID, MessageConsumer consumer) {
      if (serviceID != null && serviceID != "") {
         this._consumerTable.put(serviceID, consumer);
      } else {
         throw new IllegalArgumentException();
      }
   }

   public final void deregisterServiceMessageConsumer(MessageConsumer consumer) {
      if (consumer == null) {
         throw new IllegalArgumentException();
      }

      this._consumerTable.remove(consumer);
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final Message handleMessage(Message message) throws MessagingException {
      String serviceID = message.getServiceID();
      MessageConsumer consumer;
      if (serviceID != null && serviceID != "" && (consumer = (MessageConsumer)this._consumerTable.get(serviceID)) != null) {
         try {
            return consumer.processMessage(message);
         } catch (Throwable var6) {
            InternalLogger.logError(this, null, t, message);
            return null;
         }
      } else {
         throw new MessagingException("Invalid destination service " + serviceID);
      }
   }

   @Override
   public final String toString() {
      return "MDS Runtime Messaging.ServiceMessageProcessor";
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   static final Class class$(String x0) {
      try {
         return Class.forName(x0);
      } catch (Throwable var3) {
         throw new NoClassDefFoundError(x1.getMessage());
      }
   }
}
