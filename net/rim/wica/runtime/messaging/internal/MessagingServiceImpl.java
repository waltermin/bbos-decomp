package net.rim.wica.runtime.messaging.internal;

import net.rim.device.api.util.IntHashtable;
import net.rim.wica.runtime.event.EventListener;
import net.rim.wica.runtime.event.EventService;
import net.rim.wica.runtime.messaging.InboundQueueConnection;
import net.rim.wica.runtime.messaging.Message;
import net.rim.wica.runtime.messaging.MessageConsumer;
import net.rim.wica.runtime.messaging.MessagingException;
import net.rim.wica.runtime.messaging.MessagingService;
import net.rim.wica.runtime.messaging.OutboundQueueConnection;
import net.rim.wica.runtime.messaging.internal.inbound.InboundProcessor;
import net.rim.wica.runtime.messaging.internal.inbound.ServiceMessageProcessor;
import net.rim.wica.runtime.messaging.internal.inbound.SystemMessageProcessor;
import net.rim.wica.runtime.messaging.internal.inbound.WicletMessageProcessor;
import net.rim.wica.runtime.messaging.internal.outbound.OutboundProcessor;
import net.rim.wica.runtime.messaging.internal.util.InternalLogger;
import net.rim.wica.runtime.messaging.internal.util.Provider;
import net.rim.wica.runtime.security.SecurityService;
import net.rim.wica.runtime.service.ServiceProvider;
import net.rim.wica.runtime.service.Serviceable;
import net.rim.wica.runtime.service.Startable;
import net.rim.wica.transport.message.TransportMessageFactory;
import net.rim.wica.transport.security.KeyProvider;
import net.rim.wica.transport.security.SecureMessageFactory;
import net.rim.wica.transport.security.SecurityProvider;
import net.rim.wica.transport.security.SequenceProvider;

public final class MessagingServiceImpl implements Serviceable, Startable, EventListener, MessagingService {
   private boolean _started;
   private boolean _running;
   private InboundProcessor _inboundProcessor;
   private OutboundProcessor _outboundProcessor;
   private WicletMessageProcessor _wicletMessageProcessor;
   private SystemMessageProcessor _systemMessageProcessor;
   private ServiceMessageProcessor _serviceMessageProcessor;
   private IntHashtable _routingTable = (IntHashtable)(new Object(4));
   private VersionProviderImpl _versionProvider;
   private Scheduler _scheduler = new Scheduler();
   private Scheduler _bgScheduler = new Scheduler();
   static Class class$net$rim$wica$transport$message$TransportMessageFactory;
   static Class class$net$rim$wica$runtime$security$SecurityService;
   static Class class$net$rim$wica$transport$security$SecureMessageFactory;
   static Class class$net$rim$wica$runtime$messaging$internal$PersistenceHelper;
   static Class class$net$rim$wica$runtime$event$EventService;

   public final boolean hasTransportVersions(long agId) {
      return this._versionProvider.hasVersions(agId);
   }

   public final void registerMessageHandler(int destinationType, MessageHandler messageHandler) {
      this._routingTable.put(destinationType, messageHandler);
   }

   @Override
   public final synchronized void stop() {
      this.deactivate();
   }

   @Override
   public final synchronized Message createMessageInstance() {
      return this._versionProvider.createMessage();
   }

   @Override
   public final synchronized void start() {
      this._running = true;
      this._started = true;
      this.startThreads();
      this._wicletMessageProcessor.startup();
      this._systemMessageProcessor.startup();
      this._serviceMessageProcessor.startup();
      this._outboundProcessor.startup();
      this._inboundProcessor.startup();
   }

   @Override
   public final InboundQueueConnection getInboundQueueConnection(long wicletID) {
      return this._wicletMessageProcessor.getInboundQueueConnection(wicletID);
   }

   @Override
   public final OutboundQueueConnection getOutboundQueueConnection(long wicletID) {
      return this._outboundProcessor.getOutboundQueueConnection(wicletID);
   }

   @Override
   public final void registerServiceMessageConsumer(String serviceID, MessageConsumer consumer) {
      this._serviceMessageProcessor.registerServiceMessageConsumer(serviceID, consumer);
   }

   @Override
   public final void deregisterServiceMessageConsumer(MessageConsumer consumer) {
      this._serviceMessageProcessor.deregisterServiceMessageConsumer(consumer);
   }

   @Override
   public final void registerSystemMessageConsumer(int[] messageFilter, MessageConsumer consumer) {
      this._systemMessageProcessor.registerSystemMessageConsumer(messageFilter, consumer);
   }

   @Override
   public final void deregisterSystemMessageConsumer(MessageConsumer consumer) {
      this._systemMessageProcessor.deregisterSystemMessageConsumer(consumer);
   }

   @Override
   public final int getWicletIncomingMessageCount(long wicletID) {
      return this._wicletMessageProcessor.getWicletMessageCount(wicletID);
   }

   @Override
   public final int getWicletOutgoingMessageCount(long wicletID) {
      return this._outboundProcessor.getWicletMessageCount(wicletID);
   }

   @Override
   public final int getWicletFlowControlState(long wicletID) {
      return this._wicletMessageProcessor.getWicletFlowControlState(wicletID);
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final Message sendMessage(Message requestMessage) throws MessagingException {
      MessagingException e;
      try {
         try {
            return ((MessageHandler)this._routingTable.get(((MessageImpl)requestMessage).getDestinationType())).handleMessage(requestMessage);
         } catch (MessagingException var5) {
            e = var5;
         }
      } catch (Throwable var6) {
         InternalLogger.logError(this, null, t, null);
         throw new MessagingException("Message send failed");
      }

      throw e;
   }

   @Override
   public final void setServices(ServiceProvider serviceProvider) {
      Provider provider = new Provider(serviceProvider);
      provider.registerComponent("Scheduler", this._scheduler);
      provider.registerComponent("BackgroundScheduler", this._bgScheduler);
      TransportMessageFactory factory = new TransportMessageFactory(this._versionProvider);
      provider.registerComponent(
         class$net$rim$wica$transport$message$TransportMessageFactory == null
            ? (class$net$rim$wica$transport$message$TransportMessageFactory = class$("net.rim.wica.transport.message.TransportMessageFactory"))
            : class$net$rim$wica$transport$message$TransportMessageFactory,
         factory
      );
      SecurityService securityService = (SecurityService)serviceProvider.getService(
         class$net$rim$wica$runtime$security$SecurityService == null
            ? (class$net$rim$wica$runtime$security$SecurityService = class$("net.rim.wica.runtime.security.SecurityService"))
            : class$net$rim$wica$runtime$security$SecurityService
      );
      KeyProvider keyProvider = securityService.getKeyProvider();
      SecurityProvider securityProvider = securityService.getSecurityProvider();
      SequenceProvider sequenceProvider = securityService.getSequenceProvider();
      SecureMessageFactory smFactory = new SecureMessageFactory(this._versionProvider, keyProvider, securityProvider, sequenceProvider);
      provider.registerComponent(
         class$net$rim$wica$transport$security$SecureMessageFactory == null
            ? (class$net$rim$wica$transport$security$SecureMessageFactory = class$("net.rim.wica.transport.security.SecureMessageFactory"))
            : class$net$rim$wica$transport$security$SecureMessageFactory,
         smFactory
      );
      PersistenceHelper persistenceHelper = new PersistenceHelper(provider);
      provider.registerComponent(
         class$net$rim$wica$runtime$messaging$internal$PersistenceHelper == null
            ? (class$net$rim$wica$runtime$messaging$internal$PersistenceHelper = class$("net.rim.wica.runtime.messaging.internal.PersistenceHelper"))
            : class$net$rim$wica$runtime$messaging$internal$PersistenceHelper,
         persistenceHelper
      );
      EventService eventService = (EventService)serviceProvider.getService(
         class$net$rim$wica$runtime$event$EventService == null
            ? (class$net$rim$wica$runtime$event$EventService = class$("net.rim.wica.runtime.event.EventService"))
            : class$net$rim$wica$runtime$event$EventService
      );
      eventService.addListener(new int[]{106, 102, -804651004, 129, 133, 130, 128, -804651003}, this);
      this._inboundProcessor.initialize(provider);
      this._outboundProcessor.initialize(provider);
      this._wicletMessageProcessor.initialize(provider);
      this._systemMessageProcessor.initialize(provider);
      this._serviceMessageProcessor.initialize(provider);
      this._versionProvider.initialize(provider);
   }

   @Override
   public final void handleEvent(Object sender, int event, int eventParam, Object data) {
      switch (event) {
         case 102:
            this.deactivate();
            return;
         case 106:
            this.activate();
            return;
         default:
            InternalLogger.logUnkownEvent(this, event);
      }
   }

   private final void startThreads() {
      this._scheduler.start(this.toString());
      this._bgScheduler.start(((StringBuffer)(new Object())).append(this.toString()).append(" Background").toString());
   }

   private final synchronized void activate() {
      if (this._started && !this._running) {
         this._running = true;
         this.startThreads();
         this._wicletMessageProcessor.activate();
         this._systemMessageProcessor.activate();
         this._serviceMessageProcessor.activate();
         this._outboundProcessor.activate();
         this._inboundProcessor.activate();
      }
   }

   @Override
   public final String toString() {
      return "MDS Runtime Messaging";
   }

   public MessagingServiceImpl() {
      this._inboundProcessor = new InboundProcessor();
      this._outboundProcessor = new OutboundProcessor();
      this._wicletMessageProcessor = new WicletMessageProcessor();
      this._systemMessageProcessor = new SystemMessageProcessor();
      this._serviceMessageProcessor = new ServiceMessageProcessor();
      this._versionProvider = new VersionProviderImpl();
   }

   private final synchronized void deactivate() {
      if (this._running) {
         this._running = false;
         this._scheduler.stop();
         this._bgScheduler.stop();
         this._inboundProcessor.deactivate();
         this._outboundProcessor.deactivate();
         this._wicletMessageProcessor.deactivate();
         this._systemMessageProcessor.deactivate();
         this._serviceMessageProcessor.deactivate();
      }
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
