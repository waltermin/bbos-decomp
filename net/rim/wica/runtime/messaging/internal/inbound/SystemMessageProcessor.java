package net.rim.wica.runtime.messaging.internal.inbound;

import net.rim.device.api.util.IntHashtable;
import net.rim.wica.runtime.messaging.Message;
import net.rim.wica.runtime.messaging.MessageConsumer;
import net.rim.wica.runtime.messaging.MessagingException;
import net.rim.wica.runtime.messaging.internal.MessageHandler;
import net.rim.wica.runtime.messaging.internal.MessageImpl;
import net.rim.wica.runtime.messaging.internal.MessagingServiceImpl;
import net.rim.wica.runtime.messaging.internal.PersistenceHelper;
import net.rim.wica.runtime.messaging.internal.Processor;
import net.rim.wica.runtime.messaging.internal.Scheduler;
import net.rim.wica.runtime.messaging.internal.util.InternalLogger;
import net.rim.wica.runtime.messaging.internal.util.Provider;
import net.rim.wica.runtime.util.LinkedQueue;

public final class SystemMessageProcessor extends Processor implements MessageHandler {
   private MessagingServiceImpl _messagingService;
   private PersistenceHelper _persistenceHelper;
   private IntHashtable _consumerTable = (IntHashtable)(new Object());
   private LinkedQueue _messageQueue = new LinkedQueue();
   static Class class$net$rim$wica$runtime$messaging$MessagingService;
   static Class class$net$rim$wica$runtime$messaging$internal$PersistenceHelper;

   public final void initialize(Provider provider) {
      this._messagingService = (MessagingServiceImpl)provider.getService(
         class$net$rim$wica$runtime$messaging$MessagingService == null
            ? (class$net$rim$wica$runtime$messaging$MessagingService = class$("net.rim.wica.runtime.messaging.MessagingService"))
            : class$net$rim$wica$runtime$messaging$MessagingService
      );
      this._messagingService.registerMessageHandler(3, this);
      this._persistenceHelper = (PersistenceHelper)provider.getService(
         class$net$rim$wica$runtime$messaging$internal$PersistenceHelper == null
            ? (class$net$rim$wica$runtime$messaging$internal$PersistenceHelper = class$("net.rim.wica.runtime.messaging.internal.PersistenceHelper"))
            : class$net$rim$wica$runtime$messaging$internal$PersistenceHelper
      );
      super._scheduler = (Scheduler)provider.getService("Scheduler");
   }

   public final void startup() {
      this._persistenceHelper.loadSystemMessages(this);
   }

   public final void activate() {
      this._persistenceHelper.deleteSystemMessages();
      this.schedule();
   }

   public final void deactivate() {
      this._persistenceHelper.storeSystemMessages(this._messageQueue);
   }

   public final void registerSystemMessageConsumer(int[] messageFilter, MessageConsumer consumer) {
      if (messageFilter != null && messageFilter.length != 0) {
         for (int i = messageFilter.length - 1; i >= 0; i--) {
            this._consumerTable.put(messageFilter[i], consumer);
         }
      } else {
         throw new Object();
      }
   }

   public final void deregisterSystemMessageConsumer(MessageConsumer consumer) {
      if (consumer == null) {
         throw new Object();
      }

      this._consumerTable.removeValue(consumer);
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final Message handleMessage(Message message) throws MessagingException {
      if (message.getWicletID() != 0) {
         throw new MessagingException();
      }

      if (((MessageImpl)message).isBundle()) {
         try {
            MessageImpl[] ma = ((MessageImpl)message).debundle();
            int bundleSize = ma.length;

            for (int i = 0; i < bundleSize; i++) {
               this._messageQueue.put(ma[i]);
            }
         } catch (Throwable var6) {
            InternalLogger.logError(this, null, e, message);
            return null;
         }
      } else {
         this._messageQueue.put(message);
      }

      this.schedule();
      return null;
   }

   @Override
   protected final boolean shouldSchedule() {
      return !super._scheduled && !this._messageQueue.isEmpty();
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void run() {
      while (true) {
         boolean var7 = false /* VF: Semaphore variable */;

         try {
            var7 = true;
            if (super._scheduler.isRunning()) {
               Message m;
               if ((m = (Message)this._messageQueue.take()) != null) {
                  MessageConsumer consumer = (MessageConsumer)this._consumerTable.get(m.getMessageCode());
                  if (consumer != null) {
                     try {
                        Message response = consumer.processMessage(m);
                        if (response != null) {
                           this._messagingService.sendMessage(response);
                        }
                        continue;
                     } catch (Throwable var10) {
                        InternalLogger.logError(this, null, t, null);
                        continue;
                     }
                  }

                  InternalLogger.logWarning(this, "Dropping system message", null, m);
                  continue;
               }

               var7 = false;
            } else {
               var7 = false;
            }
         } finally {
            if (var7) {
               this.reschedule();
            }
         }

         this.reschedule();
         return;
      }
   }

   @Override
   public final String toString() {
      return "MDS Runtime Messaging.SystemMessageProcessor";
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   static final Class class$(String x0) {
      try {
         return Class.forName(x0);
      } catch (Throwable var3) {
         throw new Object(x1.getMessage());
      }
   }
}
