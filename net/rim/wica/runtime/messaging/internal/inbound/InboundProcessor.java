package net.rim.wica.runtime.messaging.internal.inbound;

import net.rim.wica.runtime.comm.CommunicationService;
import net.rim.wica.runtime.comm.IncomingRequest;
import net.rim.wica.runtime.comm.IncomingRequestListener;
import net.rim.wica.runtime.messaging.internal.PersistenceHelper;
import net.rim.wica.runtime.messaging.internal.Processor;
import net.rim.wica.runtime.messaging.internal.Scheduler;
import net.rim.wica.runtime.messaging.internal.util.ConcurrentQueue;
import net.rim.wica.runtime.messaging.internal.util.InternalLogger;
import net.rim.wica.runtime.messaging.internal.util.Provider;
import net.rim.wica.transport.security.SecureMessageFactory;

public class InboundProcessor extends Processor implements IncomingRequestListener {
   private ConcurrentQueue _requestQueue;
   private SecureMessageFactory _secureMsgFactory;
   private InboundSecureMsgHandler _secureMsgHandler;
   private PersistenceHelper _persistenceHelper;
   static Class class$net$rim$wica$runtime$comm$CommunicationService;
   static Class class$net$rim$wica$transport$security$SecureMessageFactory;
   static Class class$net$rim$wica$runtime$messaging$internal$PersistenceHelper;

   public void initialize(Provider provider) {
      CommunicationService communication = (CommunicationService)provider.getService(
         class$net$rim$wica$runtime$comm$CommunicationService == null
            ? (class$net$rim$wica$runtime$comm$CommunicationService = class$("net.rim.wica.runtime.comm.CommunicationService"))
            : class$net$rim$wica$runtime$comm$CommunicationService
      );
      communication.registerIncomingRequestListener(this);
      this._secureMsgFactory = (SecureMessageFactory)provider.getService(
         class$net$rim$wica$transport$security$SecureMessageFactory == null
            ? (class$net$rim$wica$transport$security$SecureMessageFactory = class$("net.rim.wica.transport.security.SecureMessageFactory"))
            : class$net$rim$wica$transport$security$SecureMessageFactory
      );
      this._persistenceHelper = (PersistenceHelper)provider.getService(
         class$net$rim$wica$runtime$messaging$internal$PersistenceHelper == null
            ? (class$net$rim$wica$runtime$messaging$internal$PersistenceHelper = class$("net.rim.wica.runtime.messaging.internal.PersistenceHelper"))
            : class$net$rim$wica$runtime$messaging$internal$PersistenceHelper
      );
      super._scheduler = (Scheduler)provider.getService("Scheduler");
      this._secureMsgHandler = new InboundSecureMsgHandler(provider);
      this._requestQueue = new ConcurrentQueue();
   }

   public void startup() {
      this._persistenceHelper.loadIncomingRequests(this._requestQueue);
      this.schedule();
   }

   public void activate() {
      this._persistenceHelper.deleteIncomingRequests();
      this.schedule();
   }

   public void deactivate() {
      this._persistenceHelper.storeIncomingRequests(this._requestQueue);
   }

   @Override
   public void processIncomingRequest(IncomingRequest request) {
      this._requestQueue.put(request.getData());
      this.schedule();
   }

   @Override
   protected boolean shouldSchedule() {
      return !super._scheduled && super._scheduler.isRunning() && !this._requestQueue.isEmpty();
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public void run() {
      while (true) {
         boolean var6 = false /* VF: Semaphore variable */;

         try {
            var6 = true;
            if (super._scheduler.isRunning()) {
               byte[] request;
               if ((request = (byte[])this._requestQueue.take()) != null) {
                  try {
                     this._secureMsgFactory.handleSecureMessage(request, this._secureMsgHandler);
                     continue;
                  } catch (Throwable var9) {
                     InternalLogger.logError(this, null, e, null);
                     continue;
                  }
               }

               var6 = false;
            } else {
               var6 = false;
            }
         } finally {
            if (var6) {
               this.reschedule();
            }
         }

         this.reschedule();
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
