package net.rim.wica.runtime.messaging.internal.outbound;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import net.rim.device.api.util.LongHashtable;
import net.rim.wica.runtime.comm.CommunicationService;
import net.rim.wica.runtime.comm.OutgoingRequest;
import net.rim.wica.runtime.comm.Response;
import net.rim.wica.runtime.comm.ResponseListener;
import net.rim.wica.runtime.event.EventService;
import net.rim.wica.runtime.lifecycle.LifecycleService;
import net.rim.wica.runtime.lifecycle.Wiclet;
import net.rim.wica.runtime.management.AGInfo;
import net.rim.wica.runtime.management.ManagementService;
import net.rim.wica.runtime.management.RuntimeInfo;
import net.rim.wica.runtime.messaging.Message;
import net.rim.wica.runtime.messaging.MessagingException;
import net.rim.wica.runtime.messaging.OutboundQueueConnection;
import net.rim.wica.runtime.messaging.WritableDataStream;
import net.rim.wica.runtime.messaging.internal.MessageHandler;
import net.rim.wica.runtime.messaging.internal.MessageImpl;
import net.rim.wica.runtime.messaging.internal.MessagingServiceImpl;
import net.rim.wica.runtime.messaging.internal.PersistenceHelper;
import net.rim.wica.runtime.messaging.internal.Scheduler;
import net.rim.wica.runtime.messaging.internal.util.ConcurrentQueue;
import net.rim.wica.runtime.messaging.internal.util.ConcurrentQueue$Iterator;
import net.rim.wica.runtime.messaging.internal.util.InternalLogger;
import net.rim.wica.runtime.messaging.internal.util.Provider;
import net.rim.wica.runtime.messaging.internal.util.SplitQueue$SplitIterator;
import net.rim.wica.runtime.resources.RuntimeResources;
import net.rim.wica.runtime.util.LinkedQueue;
import net.rim.wica.runtime.util.LinkedQueue$Iterator;

public final class OutboundProcessor implements ResponseListener, MessageHandler {
   private MessagingServiceImpl _messagingService;
   private CommunicationService _communicationService;
   private PersistenceHelper _persistenceHelper;
   private Scheduler _scheduler;
   private Provider _provider;
   private OutboundSecureMsgHandler _secureMsgHandler;
   private ConcurrentQueue _intermediaryQueue;
   private LongHashtable _queueTable = new LongHashtable();
   private Hashtable _urlToQueueTable;
   private LongHashtable _queueConnTable = new LongHashtable();
   private OutboundProcessor$MessageProcessor _messageProcessor;
   private OutboundProcessor$ResponseProcessor _responseProcessor;
   private OutboundProcessor$StateProcessor _stateProcessor;
   private OutboundProcessor$HeartbeatProcessor _hbProcessor;
   private OutboundProcessor$GCProcessor _gcProcessor;
   private OutboundProcessor$LifecycleEventListener _lcEventListener;
   private OutboundProcessor$CommEventListener _commEventListener;
   private OutboundProcessor$ManagementEventListener _mgmtEventListener;
   private Vector _bundle;
   private long _deviceId;
   private long _defaultAg;
   private static final int _INTERMEDIARY_Q = 0;
   private static final int _MAX_ATTEMPTS = 72;
   static Class class$net$rim$wica$runtime$messaging$MessagingService;
   static Class class$net$rim$wica$runtime$comm$CommunicationService;
   static Class class$net$rim$wica$runtime$messaging$internal$PersistenceHelper;
   static Class class$net$rim$wica$runtime$event$EventService;
   static Class class$net$rim$wica$runtime$management$ManagementService;
   static Class class$net$rim$wica$runtime$lifecycle$LifecycleService;

   public OutboundProcessor() {
      this._urlToQueueTable = new Hashtable();
      this._intermediaryQueue = new ConcurrentQueue();
      this._lcEventListener = new OutboundProcessor$LifecycleEventListener(this, null);
      this._commEventListener = new OutboundProcessor$CommEventListener(this, null);
      this._mgmtEventListener = new OutboundProcessor$ManagementEventListener(this, null);
      this._bundle = new Vector();
   }

   @Override
   public final String toString() {
      return "MDS Runtime Messaging.OutboundProcessor";
   }

   public final void initialize(Provider provider) {
      this._provider = provider;
      this._messagingService = (MessagingServiceImpl)provider.getService(
         class$net$rim$wica$runtime$messaging$MessagingService == null
            ? (class$net$rim$wica$runtime$messaging$MessagingService = class$("net.rim.wica.runtime.messaging.MessagingService"))
            : class$net$rim$wica$runtime$messaging$MessagingService
      );
      this._messagingService.registerMessageHandler(0, this);
      this._communicationService = (CommunicationService)provider.getService(
         class$net$rim$wica$runtime$comm$CommunicationService == null
            ? (class$net$rim$wica$runtime$comm$CommunicationService = class$("net.rim.wica.runtime.comm.CommunicationService"))
            : class$net$rim$wica$runtime$comm$CommunicationService
      );
      this._persistenceHelper = (PersistenceHelper)provider.getService(
         class$net$rim$wica$runtime$messaging$internal$PersistenceHelper == null
            ? (class$net$rim$wica$runtime$messaging$internal$PersistenceHelper = class$("net.rim.wica.runtime.messaging.internal.PersistenceHelper"))
            : class$net$rim$wica$runtime$messaging$internal$PersistenceHelper
      );
      this._scheduler = (Scheduler)provider.getService("Scheduler");
      this._secureMsgHandler = new OutboundSecureMsgHandler(provider);
      EventService eventService = (EventService)provider.getService(
         class$net$rim$wica$runtime$event$EventService == null
            ? (class$net$rim$wica$runtime$event$EventService = class$("net.rim.wica.runtime.event.EventService"))
            : class$net$rim$wica$runtime$event$EventService
      );
      if (this._scheduler != null && this._communicationService != null && this._messagingService != null && eventService != null) {
         eventService.addListener(
            new int[]{200, 201, 202, 521863424, 157898604, 1661009922, 1300917516, 1956816636, 712179968, 712179968, -1975817147, 16806977},
            this._lcEventListener
         );
         eventService.addListener(
            new int[]{302, 303, 300, 301, 51, -804651004, 105, 100, 104, 107, -804651005, 200, 201, 202, 521863424, 157898604}, this._commEventListener
         );
         eventService.addListener(
            new int[]{
               105, 100, 104, 107, -804651005, 200, 201, 202, 521863424, 157898604, 1661009922, 1300917516, 1956816636, 712179968, 712179968, -1975817147
            },
            this._mgmtEventListener
         );
         this._messageProcessor = new OutboundProcessor$MessageProcessor(this, null);
         this._responseProcessor = new OutboundProcessor$ResponseProcessor(this, null);
         this._stateProcessor = new OutboundProcessor$StateProcessor(this, null);
         this._hbProcessor = new OutboundProcessor$HeartbeatProcessor(this, null);
         this._gcProcessor = new OutboundProcessor$GCProcessor(this, null);
      } else {
         throw new RuntimeException();
      }
   }

   public final void startup() {
      ManagementService management = (ManagementService)this._provider
         .getService(
            class$net$rim$wica$runtime$management$ManagementService == null
               ? (class$net$rim$wica$runtime$management$ManagementService = class$("net.rim.wica.runtime.management.ManagementService"))
               : class$net$rim$wica$runtime$management$ManagementService
         );
      RuntimeInfo runtimeInfo = management.getRuntimeInfo();
      if (runtimeInfo.isRegistered()) {
         this._deviceId = runtimeInfo.getDeviceId();
         this._defaultAg = runtimeInfo.getDefaultAGInfo().getAgID();
         String defaultAgURL = runtimeInfo.getDefaultAGInfo().getAgCompactMsgURL();
         this.addQueue(this._defaultAg, defaultAgURL);
         this.buildWicletInfoTable();
         this.loadPersistedMessages();
      }
   }

   public final void activate() {
      this.deletePersistedMessages();
      this._messageProcessor.schedule();
      this._responseProcessor.schedule();
      this._stateProcessor.schedule();
      this._gcProcessor.schedule();
   }

   public final void deactivate() {
      this.persistMessages();
   }

   private final void buildWicletInfoTable() {
      LifecycleService lcService = (LifecycleService)this._provider
         .getService(
            class$net$rim$wica$runtime$lifecycle$LifecycleService == null
               ? (class$net$rim$wica$runtime$lifecycle$LifecycleService = class$("net.rim.wica.runtime.lifecycle.LifecycleService"))
               : class$net$rim$wica$runtime$lifecycle$LifecycleService
         );
      Wiclet[] wiclets = lcService.getWiclets();

      for (int i = wiclets.length - 1; i >= 0; i--) {
         this.installWiclet(wiclets[i]);
      }
   }

   private final void loadPersistedMessages() {
      this._persistenceHelper.loadOutgoingMessages(this._defaultAg, this);
      this._persistenceHelper.loadOutgoingMessages(0, this);
   }

   private final void deletePersistedMessages() {
      this._persistenceHelper.deleteOutgoingMessages(0);
      this._persistenceHelper.deleteOutgoingMessages(this._defaultAg);
   }

   private final void persistMessages() {
      this._persistenceHelper.storeOutgoingMessages(0, this._intermediaryQueue);
      LinkedQueue q = (LinkedQueue)this._queueTable.get(this._defaultAg);
      if (q != null) {
         this._persistenceHelper.storeOutgoingMessages(this._defaultAg, (Object)q);
      }
   }

   private final OutboundQueue addQueue(long agId, String agURL) {
      OutboundQueue q = new OutboundQueue(agId, agURL);
      this._queueTable.put(agId, q);
      this._urlToQueueTable.put(agURL, q);
      return q;
   }

   private final void installWiclet(Wiclet w) {
      if (w.getAgId() == 0) {
         throw new RuntimeException("Invalid MDS Services ID 0 for application " + w.getId());
      }

      OutboundQueueConnectionImpl wInfo = new OutboundQueueConnectionImpl(w.getId(), w.getAgId(), w.getOutboundQueueSizeLimit());
      this._queueConnTable.put(w.getId(), wInfo);
   }

   private final void filterByWicletId(OutboundQueue q, long wicletId) {
      LinkedQueue$Iterator i = q.iterator();

      while (i.hasNext()) {
         if (((MessageImpl)i.next()).getWicletID() == wicletId) {
            i.remove();
         }
      }
   }

   private final void filterIntermediaryByWicletId(long wicletId) {
      this._intermediaryQueue.lock();
      ConcurrentQueue$Iterator i = this._intermediaryQueue.iterator();

      while (i.hasNext()) {
         if (((MessageImpl)i.next()).getWicletID() == wicletId) {
            i.remove();
         }
      }

      this._intermediaryQueue.unlock();
   }

   public final int getWicletMessageCount(long wicletID) {
      OutboundQueueConnectionImpl w = (OutboundQueueConnectionImpl)this._queueConnTable.get(wicletID);
      return w != null ? w.getMessageCount() : 0;
   }

   public final OutboundQueueConnection getOutboundQueueConnection(long wicletID) {
      return (OutboundQueueConnection)this._queueConnTable.get(wicletID);
   }

   @Override
   public final Message handleMessage(Message message) throws MessagingException {
      long wicletID = message.getWicletID();
      if (wicletID != 0) {
         OutboundQueueConnectionImpl qConn = (OutboundQueueConnectionImpl)this._queueConnTable.get(wicletID);
         if (qConn == null) {
            throw new MessagingException("Application [" + wicletID + "] not found.");
         }

         qConn.incrementMessageCount();
         message.setAGID(qConn.getAgId());
      } else {
         if (message.getAGID() == 0) {
            InternalLogger.logError(this, "MDS Services " + message.getAGID() + " not found; dropping message.", null, message);
            return null;
         }

         message.setSecurityMode(1);
      }

      this._intermediaryQueue.put(message);
      this._messageProcessor.schedule();
      return null;
   }

   @Override
   public final void processResponse(Response response, OutgoingRequest request) {
      this.processResponse(response, (MessageImpl)request.getCustomData());
   }

   private final void processResponse(Response response, MessageImpl m) {
      OutboundQueue q = (OutboundQueue)this._queueTable.get(m.getAGID());
      if (q != null) {
         m.setResponse(response);
         this._responseProcessor.scheduleTask(q);
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final void sendMessage(MessageImpl m, OutboundQueue q) {
      m.setDeviceId(this._deviceId);

      try {
         OutgoingRequest r = this._communicationService.createOutgoingRequestInstance(q.getIOURL());
         r.setCustomData(m);
         r.setResponseListener(this);
         r.setRequestor(this._secureMsgHandler);
         r.setMaxAttempts(72);
         r.setRequestMethod("POST");
         this._communicationService.sendRequest(r);
      } catch (Throwable var5) {
         this.processResponse(new OutboundProcessor$ErrorResponse(null), m);
         InternalLogger.logError(this, null, t, m);
         return;
      }
   }

   private final void sendNextMessage(OutboundQueue q) {
      SplitQueue$SplitIterator i = q.splitIterator(true);
      MessageImpl m;
      if ((m = this.nextBundle(i)) != null) {
         this.sendMessage(m, q);
      }
   }

   private final MessageImpl nextBundle(LinkedQueue$Iterator i) {
      if (!i.hasNext()) {
         return null;
      }

      MessageImpl head = (MessageImpl)i.next();
      if (i.hasNext()) {
         this._bundle.removeAllElements();
         this._bundle.addElement(head);
         int version = head.getVersion();
         long wicletId = head.getWicletID();
         int securityMode = head.getSecurityMode();

         while (this._scheduler.isRunning() && i.hasNext()) {
            MessageImpl m = (MessageImpl)i.peekNext();
            if (m.getVersion() != version || m.getWicletID() != wicletId || m.getSecurityMode() != securityMode) {
               break;
            }

            i.removeNext();
            this._bundle.addElement(m);
         }

         if (this._bundle.size() > 1) {
            long agId = head.getAGID();

            try {
               head = head.bundle(this._bundle);
            } catch (PersistenceHelper e) {
               this.processResponse(new OutboundProcessor$ErrorResponse(null), head);
               this.handleMessageDropped(wicletId, this._bundle.size() - 1);
               return null;
            }

            head.setAGID(agId);
            head.setSecurityMode(securityMode);
            i.replace(head);
         }
      }

      return head;
   }

   private final void processEnabledQueue(OutboundQueue q) {
      SplitQueue$SplitIterator i = q.splitIterator(true);

      MessageImpl m;
      while (this._scheduler.isRunning() && (m = this.nextBundle(i)) != null) {
         this.sendMessage(m, q);
      }

      i.markSplit();
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final void processDisabledQueue(OutboundQueue q) {
      if (this._communicationService.isInCoverage()) {
         if (q.splitIterator(true).hasNext()) {
            try {
               boolean destinationPolled = this._communicationService.isDestinationServerPolled(q.getIOURL());
               if (!destinationPolled) {
                  this.sendNextMessage(q);
                  return;
               }

               this._communicationService.resendDeferredRequests();
            } catch (Throwable var4) {
               InternalLogger.logError(this, null, t, null);
               return;
            }
         }
      }
   }

   private final void changeStateOnAgEnabled(OutboundQueue q) {
      if (q.atBridgeOnAgEnabled()) {
         this._stateProcessor.scheduleTask(q);
      }
   }

   private final void changeStateOnDeviceEnabled(OutboundQueue q) {
      if (q.atBridgeOnDeviceEnabled()) {
         this._stateProcessor.scheduleTask(q);
      }
   }

   private final void changeStateOnAgDisabled(OutboundQueue q) {
      if (q.atBridgeOnAgDisabled()) {
         this._stateProcessor.scheduleTask(q);
      }
   }

   private final void changeStateOnDeviceDisabled(OutboundQueue q) {
      if (q.atBridgeOnDeviceDisabled()) {
         this._stateProcessor.scheduleTask(q);
      }
   }

   private final void processResponses(OutboundQueue q) {
      while (!q.isEmpty()) {
         MessageImpl m = (MessageImpl)q.peek();
         if (m.hasResponse()) {
            Response response = m.getResponse();
            if (!response.isSuccessful()) {
               this.sendError(m);
            } else if (q.shouldHeartbeat()) {
               q.shouldHeartbeat(false);
            }

            this.handleMessageRemoved(m);
            q.take();
            continue;
         }
         break;
      }
   }

   private final void handleMessageRemoved(MessageImpl m) {
      OutboundQueueConnectionImpl qConn;
      if (m.getWicletID() != 0 && (qConn = (OutboundQueueConnectionImpl)this._queueConnTable.get(m.getWicletID())) != null) {
         qConn.decrementMessageCount(m.getMessageCount());
      }
   }

   private final void handleMessageDropped(long wicletId, int numMessages) {
      OutboundQueueConnectionImpl qConn;
      if (wicletId != 0 && (qConn = (OutboundQueueConnectionImpl)this._queueConnTable.get(wicletId)) != null) {
         qConn.decrementMessageCount(numMessages);
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final void sendError(MessageImpl m) {
      if (m.getWicletID() != 0) {
         MessageImpl error = (MessageImpl)this._messagingService.createMessageInstance();
         error.setDestinationType(1);
         error.setWicletID(m.getWicletID());
         error.setMessageCode(0);
         WritableDataStream ws = error.openWritableDataStream();
         ws.writeInt(m.getMessageCode());
         ws.writeInt(1);
         ws.writeInt(0);
         ws.writeString(RuntimeResources.getString(91));
         ws.writeString(RuntimeResources.getString(109));

         try {
            this._messagingService.sendMessage(error);
         } catch (Throwable var6) {
            InternalLogger.logError(this, "Internal error message failed", t, error);
            return;
         }
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final void sendHeartbeat(OutboundQueue q) {
      if (this._messagingService.hasTransportVersions(q.getId())) {
         MessageImpl m = (MessageImpl)this._messagingService.createMessageInstance();
         m.setAGID(q.getId());
         m.setDeviceId(this._deviceId);
         m.setWicletID(0);
         m.setMessageCode(1);
         m.setSecurityMode(0);

         try {
            OutgoingRequest r = this._communicationService.createOutgoingRequestInstance(q.getIOURL());
            r.setCustomData(m);
            r.setRequestor(this._secureMsgHandler);
            r.setResponseListener(this._hbProcessor);
            r.setMaxAttempts(72);
            r.setRequestMethod("POST");
            this._communicationService.sendRequest(r);
         } catch (Throwable var5) {
            InternalLogger.logError(this, "Heartbeat to MDS Services " + q.getId() + " failed.", t, null);
            return;
         }
      }
   }

   private final synchronized void registerDefaultAg() {
      ManagementService management = (ManagementService)this._provider
         .getService(
            class$net$rim$wica$runtime$management$ManagementService == null
               ? (class$net$rim$wica$runtime$management$ManagementService = class$("net.rim.wica.runtime.management.ManagementService"))
               : class$net$rim$wica$runtime$management$ManagementService
         );
      AGInfo ag = management.getRuntimeInfo().getDefaultAGInfo();
      long newDefaultAg = ag.getAgID();
      if (newDefaultAg == 0) {
         InternalLogger.logError(this, "MDS Services Registration: invalid MDS Services ID 0", null, null);
      } else if (newDefaultAg != this._defaultAg) {
         if (this._defaultAg != 0) {
            this.unregisterDefaultAg();
         }

         this.addQueue(newDefaultAg, ag.getAgCompactMsgURL());
         if (management.getRuntimeInfo().isReactivate()) {
            synchronized (this._queueConnTable) {
               Enumeration e = this._queueConnTable.elements();

               while (e.hasMoreElements()) {
                  OutboundQueueConnectionImpl qConn = (OutboundQueueConnectionImpl)e.nextElement();
                  qConn.setAgId(newDefaultAg);
               }
            }
         }

         this._defaultAg = newDefaultAg;
      }
   }

   private final synchronized void unregisterDefaultAg() {
      if (this._defaultAg == 0) {
         InternalLogger.logWarning(this, "MDS Services Unregistration: invalid MDS Services ID 0", null, null);
      } else {
         OutboundQueue q = (OutboundQueue)this._queueTable.remove(this._defaultAg);
         this._defaultAg = 0;
         q.shouldHeartbeat(false);
         q.changeStateOnUnregister();
         this._intermediaryQueue.removeAll();
         this._urlToQueueTable.remove(q.getURL());
      }
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
