package net.rim.wica.runtime.messaging.internal.inbound;

import java.util.Enumeration;
import net.rim.device.api.util.LongEnumeration;
import net.rim.device.api.util.LongHashtable;
import net.rim.device.api.util.LongIntHashtable;
import net.rim.wica.runtime.event.EventService;
import net.rim.wica.runtime.lifecycle.LifecycleService;
import net.rim.wica.runtime.lifecycle.Wiclet;
import net.rim.wica.runtime.management.ManagementService;
import net.rim.wica.runtime.messaging.InboundQueueConnection;
import net.rim.wica.runtime.messaging.Message;
import net.rim.wica.runtime.messaging.MessagingException;
import net.rim.wica.runtime.messaging.WritableDataStream;
import net.rim.wica.runtime.messaging.internal.MessageHandler;
import net.rim.wica.runtime.messaging.internal.MessagingServiceImpl;
import net.rim.wica.runtime.messaging.internal.util.InternalLogger;
import net.rim.wica.runtime.messaging.internal.util.Provider;
import net.rim.wica.runtime.persistence.PersistenceService;
import net.rim.wica.runtime.util.LongLongHashtable;

public final class WicletMessageProcessor implements MessageHandler {
   private MessagingServiceImpl _messaging;
   private LifecycleService _lifecycle;
   private PersistenceService _persistence;
   private ManagementService _management;
   private Provider _provider;
   private LongHashtable _wicletTable = (LongHashtable)(new Object());
   private LongLongHashtable _aliasTable;
   private LongIntHashtable _flowControlTable;
   static Class class$net$rim$wica$runtime$lifecycle$LifecycleService;
   static Class class$net$rim$wica$runtime$persistence$PersistenceService;
   static Class class$net$rim$wica$runtime$management$ManagementService;
   static Class class$net$rim$wica$runtime$messaging$MessagingService;
   static Class class$net$rim$wica$runtime$event$EventService;

   public final void initialize(Provider provider) {
      this._provider = provider;
      this._lifecycle = (LifecycleService)provider.getService(
         class$net$rim$wica$runtime$lifecycle$LifecycleService == null
            ? (class$net$rim$wica$runtime$lifecycle$LifecycleService = class$("net.rim.wica.runtime.lifecycle.LifecycleService"))
            : class$net$rim$wica$runtime$lifecycle$LifecycleService
      );
      this._persistence = (PersistenceService)provider.getService(
         class$net$rim$wica$runtime$persistence$PersistenceService == null
            ? (class$net$rim$wica$runtime$persistence$PersistenceService = class$("net.rim.wica.runtime.persistence.PersistenceService"))
            : class$net$rim$wica$runtime$persistence$PersistenceService
      );
      this._management = (ManagementService)provider.getService(
         class$net$rim$wica$runtime$management$ManagementService == null
            ? (class$net$rim$wica$runtime$management$ManagementService = class$("net.rim.wica.runtime.management.ManagementService"))
            : class$net$rim$wica$runtime$management$ManagementService
      );
      this._messaging = (MessagingServiceImpl)provider.getService(
         class$net$rim$wica$runtime$messaging$MessagingService == null
            ? (class$net$rim$wica$runtime$messaging$MessagingService = class$("net.rim.wica.runtime.messaging.MessagingService"))
            : class$net$rim$wica$runtime$messaging$MessagingService
      );
      this._messaging.registerMessageHandler(1, this);
      EventService eventService = (EventService)provider.getService(
         class$net$rim$wica$runtime$event$EventService == null
            ? (class$net$rim$wica$runtime$event$EventService = class$("net.rim.wica.runtime.event.EventService"))
            : class$net$rim$wica$runtime$event$EventService
      );
      if (this._lifecycle != null && this._management != null && this._persistence != null && eventService != null) {
         eventService.addListener(
            new int[]{200, 201, 202, -804651004, 500, 107, 203, 204, 521863424, 1886404972, 16827085, 1466703643},
            new WicletMessageProcessor$LifecycleEventListener(this, null)
         );
         eventService.addListener(
            new int[]{
               500,
               107,
               203,
               204,
               521863424,
               1886404972,
               16827085,
               1466703643,
               133481,
               1813977857,
               1835888517,
               6555155,
               1813977857,
               1886999746,
               1208025189,
               186346607
            },
            new WicletMessageProcessor$MiscellaneousEventListener(this, null)
         );
      } else {
         throw new Object();
      }
   }

   public final Provider getServiceProvider() {
      return this._provider;
   }

   public final void startup() {
      this._aliasTable = this._persistence.loadWicletAlias();
      this._flowControlTable = this._persistence.loadWicletStatus();
      this.loadWicletQueues();
   }

   public final void activate() {
      this.activateManagers();
   }

   public final void deactivate() {
      this.deactivateManagers();
   }

   public final int getWicletFlowControlState(long wicletId) {
      WicletQueueManager wm = (WicletQueueManager)this._wicletTable.get(wicletId);
      return wm != null ? wm.getFlowControlState() : 0;
   }

   final synchronized void setFlowControlState(WicletQueueManager wm) {
      if (this._flowControlTable == null) {
         this._flowControlTable = (LongIntHashtable)(new Object());
      }

      this._flowControlTable.put(wm.getWicletId(), wm.getFlowControlState());
      this._persistence.storeWicletStatus(this._flowControlTable);
   }

   final synchronized void clearFlowControlState(long wicletId) {
      if (this._flowControlTable != null) {
         this._flowControlTable.remove(wicletId);
         this._persistence.storeWicletStatus(this._flowControlTable);
      }
   }

   final synchronized void requestFlowControl(WicletQueueManager wm) {
      this.setFlowControlState(wm);
      Message m = this._messaging.createMessageInstance();
      m.setWicletID(0);
      m.setAGID(wm.getAgId());
      m.setMessageCode(2);
      m.setDestinationType(0);
      WritableDataStream s = m.openWritableDataStream();
      s.writeLong(wm.getWicletId());
      s.writeInt(wm.getFlowControlState());

      try {
         this._messaging.sendMessage(m);
      } catch (MessagingException e) {
         InternalLogger.logError(this, "Flow control message send failed.", e, m);
      }
   }

   public final InboundQueueConnection getInboundQueueConnection(long wicletID) {
      if (wicletID == 0) {
         throw new Object();
      } else {
         return (WicletQueueManager)this._wicletTable.get(wicletID);
      }
   }

   public final int getWicletMessageCount(long wicletID) {
      WicletQueueManager wm = (WicletQueueManager)this._wicletTable.get(wicletID);
      return wm == null ? 0 : wm.getMessageCount();
   }

   final synchronized void removeAlias(long mappedTo) {
      if (this._aliasTable != null) {
         this._aliasTable.removeValue(mappedTo, false);
         this._persistence.storeWicletAlias(this._aliasTable);
      }
   }

   @Override
   public final Message handleMessage(Message message) {
      long wicletId = message.getWicletID();
      WicletQueueManager wm = (WicletQueueManager)this._wicletTable.get(wicletId);
      if (wm != null) {
         return wm.handleMessage(message);
      }

      wm = this.getAlias(wicletId);
      if (wm != null) {
         return wm.handleMessage(message);
      }

      if (message.getAGID() == 0) {
         throw new MessagingException(((StringBuffer)(new Object("Application "))).append(wicletId).append(" not found").toString());
      }

      this._management.sendREStatusMessage(message.getAGID());
      return null;
   }

   private final void activateManagers() {
      synchronized (this._wicletTable) {
         Enumeration e = this._wicletTable.elements();

         while (e.hasMoreElements()) {
            WicletQueueManager wm = (WicletQueueManager)e.nextElement();
            wm.handleRuntimeActivate();
         }
      }
   }

   private final void deactivateManagers() {
      synchronized (this._wicletTable) {
         Enumeration e = this._wicletTable.elements();

         while (e.hasMoreElements()) {
            WicletQueueManager wm = (WicletQueueManager)e.nextElement();
            wm.handleRuntimeDeactivate();
         }
      }
   }

   private final long getReverseAlias(long wicletId) {
      if (this._aliasTable != null && this._aliasTable.contains(wicletId)) {
         LongEnumeration e = this._aliasTable.keys();

         while (e.hasMoreElements()) {
            long key = e.nextElement();
            if (this._aliasTable.get(key) == wicletId) {
               return key;
            }
         }
      }

      return 0;
   }

   private final WicletQueueManager getAlias(long wicletId) {
      return this._aliasTable == null ? null : (WicletQueueManager)this._wicletTable.get(this._aliasTable.get(wicletId));
   }

   private final void loadWicletQueues() {
      Wiclet[] wiclets = this._lifecycle.getWiclets();

      for (int i = wiclets.length - 1; i >= 0; i--) {
         Wiclet w = wiclets[i];
         WicletQueueManager wm = new WicletQueueManager(
            this,
            w.getId(),
            this.getReverseAlias(w.getId()),
            w.getAgId(),
            w.getMessageDelivery(),
            w.getInboundQueueSizeLimit(),
            this.getPersistedFlowControlState(w.getId()),
            true
         );
         this._wicletTable.put(wm.getWicletId(), wm);
      }
   }

   private final synchronized void createAlias(long mapFrom, long mapTo) {
      if (this._aliasTable == null) {
         this._aliasTable = new LongLongHashtable();
      }

      this._aliasTable.put(mapFrom, mapTo);
      this._persistence.storeWicletAlias(this._aliasTable);
   }

   private final synchronized int getPersistedFlowControlState(long wicletId) {
      return this._flowControlTable != null && this._flowControlTable.containsKey(wicletId) ? this._flowControlTable.get(wicletId) : 0;
   }

   private final WicletQueueManager installWiclet(Wiclet w) {
      if (w.getAgId() == 0) {
         throw new Object(((StringBuffer)(new Object("Invalid MDS Services ID 0 for application "))).append(w.getId()).toString());
      }

      WicletQueueManager wm = new WicletQueueManager(this, w.getId(), 0, w.getAgId(), w.getMessageDelivery(), w.getInboundQueueSizeLimit(), 0, false);
      this._wicletTable.put(w.getId(), wm);
      return wm;
   }

   private final void uninstallWiclet(long wicletId) {
      WicletQueueManager wm = (WicletQueueManager)this._wicletTable.remove(wicletId);
      if (wm != null) {
         wm.handleUninstall();
      }

      this.removeAlias(wicletId);
      this.clearFlowControlState(wicletId);
   }

   private final void upgradeWiclet(long id, Wiclet w) {
      WicletQueueManager wm = (WicletQueueManager)this._wicletTable.get(id);
      if (wm == null) {
         wm = this.installWiclet(w);
      } else {
         this.removeAlias(id);
         this.createAlias(id, w.getId());
         wm.handleUpgradeComplete(w);
         this._wicletTable.put(w.getId(), wm);
         this._wicletTable.remove(id);
         this.clearFlowControlState(id);
         this.setFlowControlState(wm);
      }
   }

   @Override
   public final String toString() {
      return "MDS Runtime Messaging.ApplicationMessageProcessor";
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
