package net.rim.wica.runtime.messaging.internal.inbound;

import java.util.Enumeration;
import java.util.Hashtable;
import net.rim.device.api.util.IntIntHashtable;
import net.rim.wica.runtime.lifecycle.LifecycleService;
import net.rim.wica.runtime.lifecycle.Wiclet;
import net.rim.wica.runtime.messaging.InboundQueueConnection;
import net.rim.wica.runtime.messaging.InboundQueueListener;
import net.rim.wica.runtime.messaging.Message;
import net.rim.wica.runtime.messaging.MessagingException;
import net.rim.wica.runtime.messaging.internal.MessageHandler;
import net.rim.wica.runtime.messaging.internal.MessageImpl;
import net.rim.wica.runtime.messaging.internal.PersistenceHelper;
import net.rim.wica.runtime.messaging.internal.Processor;
import net.rim.wica.runtime.messaging.internal.Scheduler;
import net.rim.wica.runtime.messaging.internal.notification.BackgroundProcessor;
import net.rim.wica.runtime.messaging.internal.util.ConcurrentQueue;
import net.rim.wica.runtime.messaging.internal.util.ConcurrentQueue$Iterator;
import net.rim.wica.runtime.messaging.internal.util.InternalLogger;
import net.rim.wica.runtime.messaging.internal.util.Provider;
import net.rim.wica.runtime.persistence.PersistenceService;

final class WicletQueueManager extends Processor implements InboundQueueConnection, MessageHandler {
   private long _wicletId;
   private long _agId;
   private long _aliasId;
   private int _deliveryMode;
   private WicletMessageProcessor _parent;
   private PersistenceHelper _persistenceHelper;
   private PersistenceService _persistenceService;
   private LifecycleService _lifecycleService;
   private InboundQueueListener _listener;
   private BackgroundProcessor _bgProcessor;
   private Scheduler _bgScheduler;
   private ConcurrentQueue _fgQueue;
   private ConcurrentQueue _bgQueue;
   private ConcurrentQueue _activeQueue;
   private int _fgQueueSize;
   private int _localMsgFlag;
   private int _state;
   private WicletQueueManager$FlowControl _fcState;
   private int _openUpper;
   private int _closedFgUpper;
   private int _closedBgUpper;
   private int _bgDisableFlag;
   private IntIntHashtable _messageCodeMap;
   private Hashtable _replacementTable;
   private Object _managerLock = new Object();
   private Object _fcLock = new Object();
   private static final int _WICLET_STATE_OPEN = 0;
   private static final int _WICLET_STATE_DEACTIVATED = 1;
   private static final int _WICLET_STATE_CLOSED = 2;
   private static final int _MSG_BG = 1;
   private static final int _MSG_FG_NEW = 2;
   private static final int _MSG_FG_OVERWRITE = 4;
   private static final int _MSG_FG = 6;
   private static final int _DISABLE_ON_MD_STARTUP = 1;
   private static final int _DISABLE_ON_UPGRADE = 2;
   private static final int _DISABLE_ON_UNINSTALL = 4;
   private static final int _DISABLE_ON_DEACTIVATE = 8;
   private static final int _DISABLE_ON_ERROR = 255;
   private static final int _SEND_ALL = 0;
   private static final int _SUSPEND_FOREGROUND = 1;
   private static final int _SUSPEND_ALL = 2;
   private static final WicletQueueManager$SendAllOpen _SEND_ALL_OPEN = new WicletQueueManager$SendAllOpen(null);
   private static final WicletQueueManager$SuspendAllOpen _SUSPEND_ALL_OPEN = new WicletQueueManager$SuspendAllOpen(null);
   private static final WicletQueueManager$SendAllClosed _SEND_ALL_CLOSED = new WicletQueueManager$SendAllClosed(null);
   private static final WicletQueueManager$SuspendFgClosed _SUSPEND_FOREGROUND_CLOSED = new WicletQueueManager$SuspendFgClosed(null);
   private static final WicletQueueManager$SuspendAllClosed _SUSPEND_ALL_CLOSED = new WicletQueueManager$SuspendAllClosed(null);
   static Class class$net$rim$wica$runtime$persistence$PersistenceService;
   static Class class$net$rim$wica$runtime$lifecycle$LifecycleService;
   static Class class$net$rim$wica$runtime$messaging$internal$PersistenceHelper;

   final long getWicletId() {
      return this._wicletId;
   }

   final long getAgId() {
      return this._agId;
   }

   public final boolean isBestEffort() {
      return this._deliveryMode == 2;
   }

   public final boolean isReliable() {
      return this._deliveryMode == 1;
   }

   final int getMessageCount() {
      return this._fgQueue.size() + this._bgQueue.size();
   }

   final void handleRuntimeDeactivate() {
      synchronized (this._managerLock) {
         if (!this.isOpen()) {
            this.consolidateQueues();
         }

         this._persistenceHelper.storeWicletMessages(this._wicletId, this._fgQueue);
         this.disableBgProcessor(8);
      }
   }

   final void handleRuntimeActivate() {
      synchronized (this._managerLock) {
         if (this.isOpen()) {
            this._activeQueue = this._fgQueue;
         } else {
            this.prepareForBgProcessing();
            if (this.isDeactivated() && this.haveBgMessages()) {
               this._listener.noteMessageAvailable();
            }
         }

         this.persistenceOpClean();
         this.enableBgProcessor(8);
      }
   }

   final void upgradeMessage(Message m) {
      if (this._messageCodeMap != null && this._messageCodeMap.containsKey(m.getMessageCode())) {
         m.setMessageCode(this._messageCodeMap.get(m.getMessageCode()));
         m.setWicletID(this._wicletId);
      } else {
         throw new MessagingException(
            ((StringBuffer)(new Object("Message code map not found for upgraded wiclet, message = "))).append(m.toString()).toString()
         );
      }
   }

   final void handleUpgradeComplete(Wiclet w) {
      synchronized (this._managerLock) {
         this._aliasId = this._wicletId;
         this._wicletId = w.getId();
         this._deliveryMode = w.getMessageDelivery();
         this.setThreshold(w.getInboundQueueSizeLimit());
         this._persistenceService.deleteMessageUpgradeMap(this._aliasId);
         this._messageCodeMap = this._persistenceService.loadMessageUpgradeMap(this._wicletId);
         this.upgradeQueue(this._fgQueue);
         this.upgradeQueue(this._bgQueue);
         if (this._bgProcessor != null) {
            this._bgProcessor.reload(this._wicletId);
         }

         this.enableBgProcessor(2);
      }
   }

   final void handleUpgradeStarted() {
      synchronized (this._managerLock) {
         this.disableBgProcessor(2);
      }
   }

   final void handleUninstall() {
      synchronized (this._managerLock) {
         this.disableBgProcessor(4);
         this.persistenceOpClean();
         this._persistenceService.deleteMessageUpgradeMap(this._wicletId);
      }
   }

   final void handleMetadataShutdown() {
      synchronized (this._managerLock) {
         this.enableBgProcessor(1);
      }
   }

   final void handleMetadataStartup() {
      synchronized (this._managerLock) {
         this.disableBgProcessor(1);
      }
   }

   final void handlePolicyUpdate() {
      this.setThreshold(this._lifecycleService.getWiclet(this._wicletId).getInboundQueueSizeLimit());
      this.flowControlOnNewThreshold();
   }

   final int getFlowControlState() {
      return this._fcState.value();
   }

   final void setMessageCodeMap(IntIntHashtable map) {
      this._messageCodeMap = map;
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final Message handleMessage(Message message) {
      try {
         MessageImpl m = (MessageImpl)message;
         MessageImpl[] ma = m.isBundle() ? m.debundle() : null;
         synchronized (this._managerLock) {
            this.preMessageHandling(m, ma);
            this.handleMessageInternal(m, ma);
            this.postMessageHandling(m, ma);
         }
      } catch (Throwable var9) {
         InternalLogger.logError(this, null, e, message);
         return null;
      }

      return null;
   }

   @Override
   public final Message getNextMessage() {
      Message m = (Message)this._activeQueue.take();
      if (this._activeQueue.isEmpty()) {
         this.flowControlOnQueueEmpty();
      }

      return m;
   }

   @Override
   public final boolean isClosed() {
      return this._state == 2;
   }

   @Override
   public final boolean isDeactivated() {
      return this._state == 1;
   }

   @Override
   public final boolean isOpen() {
      return this._state == 0;
   }

   @Override
   public final void close() {
      synchronized (this._managerLock) {
         if (!this.isClosed()) {
            this._listener = null;
            int previousState = this._state;
            this._state = 2;
            if (previousState == 0) {
               this._activeQueue = this._bgQueue;
               this.prepareForBgProcessing();
            }

            this.flowControlOnDeactiveOrClose();
         }
      }
   }

   @Override
   public final void deactivate() {
      synchronized (this._managerLock) {
         if (!this.isDeactivated() && !this.isClosed()) {
            this._state = 1;
            this._activeQueue = this._bgQueue;
            this.prepareForBgProcessing();
            if (this.haveBgMessages()) {
               this.notifyListener();
            }

            this.flowControlOnDeactiveOrClose();
         }
      }
   }

   @Override
   public final void open(InboundQueueListener listener) {
      synchronized (this._managerLock) {
         if (!this.isOpen() || this._listener != listener) {
            this._listener = listener;
            if (!this.isOpen()) {
               this._state = 0;
               this._activeQueue = this._fgQueue;
               this.consolidateQueues();
               if (!this._activeQueue.isEmpty()) {
                  this.notifyListener();
               }

               this.flowControlOnOpen();
            }
         }
      }
   }

   @Override
   public final boolean isEmpty() {
      return this._activeQueue.isEmpty();
   }

   private final void handleBgEnabled(Message m) {
      this._bgQueue.put(m);
      this._localMsgFlag |= 1;
   }

   private final void handleKeepLast(Message m) {
      if (this._replacementTable == null) {
         this._replacementTable = (Hashtable)(new Object());
      }

      if (WicletQueueManager$ReplacementAlgorithm.replace(this._replacementTable, this._fgQueue, m)) {
         this._localMsgFlag |= 4;
      } else {
         this._localMsgFlag |= 2;
         this._fgQueueSize++;
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final void notifyListener() {
      try {
         this._listener.noteMessageAvailable();
      } catch (Throwable var3) {
         InternalLogger.logError(this, null, t, null);
         return;
      }
   }

   private final boolean haveBgMessages() {
      return !this._bgQueue.isEmpty();
   }

   private final void consolidateQueues() {
      if (!this._bgQueue.isEmpty()) {
         this._fgQueue.prepend(this._bgQueue);
      }
   }

   private final boolean aliased(long wicletId) {
      return this._aliasId == wicletId;
   }

   private final void prepareForBgProcessing() {
      if (this._replacementTable != null) {
         this._replacementTable.clear();
      }

      if (this._fgQueue.isEmpty()) {
         this._fgQueueSize = 0;
      } else {
         this.filterNotifications();
         this._fgQueueSize = this._fgQueue.size();
      }
   }

   private final void preMessageHandling(MessageImpl m, MessageImpl[] ma) {
      if (m.getWicletID() != this._wicletId) {
         if (!this.aliased(m.getWicletID())) {
            throw new MessagingException();
         }

         if (ma == null) {
            this.upgradeMessage(m);
            return;
         }

         int bundleSize = ma.length;

         for (int i = 0; i < bundleSize; i++) {
            this.upgradeMessage(ma[i]);
         }
      }
   }

   private final void handleMessageInternal(MessageImpl m, MessageImpl[] ma) {
      if (ma == null) {
         this.handleMessageInternal(m);
      } else {
         int bundleSize = ma.length;

         for (int i = 0; i < bundleSize; i++) {
            this.handleMessageInternal(ma[i]);
         }
      }
   }

   private final void postMessageHandling(MessageImpl m, MessageImpl[] ma) {
      if (ma == null) {
         this._lifecycleService.processIncomingMessage(this._wicletId, m.getMessageCode());
      } else {
         int bundleSize = ma.length;

         for (int i = 0; i < bundleSize; i++) {
            this._lifecycleService.processIncomingMessage(this._wicletId, ma[i].getMessageCode());
         }
      }

      if (this.isOpen()) {
         this.flowControlOnNewMsg();
         this.notifyListener();
      } else {
         if ((this._localMsgFlag & 1) != 0) {
            this.flowControlOnNewBgMsg();
            if (this.isDeactivated()) {
               this.notifyListener();
            } else {
               this.scheduleBgProcessor();
            }
         }

         if ((this._localMsgFlag & 2) != 0) {
            this.flowControlOnNewMsg();
         }

         this._localMsgFlag = 0;
      }

      if (m.hasMore()) {
         this.scheduleForFlowControl();
      }
   }

   private final void handleMessageInternal(MessageImpl m) {
      if (this.isOpen()) {
         this._fgQueue.put(m);
      } else if (!m.isNotification()) {
         this.handleStandard(m);
      } else if (m.backgroundProcessingEnabled()) {
         this.handleBgEnabled(m);
      } else if (m.keepLast()) {
         this.handleKeepLast(m);
      } else {
         this.handleStandard(m);
      }
   }

   private final void handleStandard(Message m) {
      this._fgQueue.put(m);
      this._localMsgFlag |= 2;
      this._fgQueueSize++;
   }

   private final void setFlowControlState(int fcState) {
      if (fcState == 1) {
         this._fcState = _SUSPEND_FOREGROUND_CLOSED;
      } else if (fcState == 2) {
         this._fcState = _SUSPEND_ALL_CLOSED;
      }

      this.flowControlOnStart();
   }

   private final void upgradeQueue(ConcurrentQueue q) {
      if (!q.isEmpty()) {
         q.lock();
         ConcurrentQueue$Iterator i = q.iterator();

         while (i.hasNext()) {
            try {
               this.upgradeMessage((MessageImpl)i.next());
            } catch (MessagingException e) {
               i.remove();
               InternalLogger.logError(this, null, e, null);
            }
         }

         q.unlock();
      }
   }

   WicletQueueManager(
      WicletMessageProcessor processor, long wicletId, long aliasId, long agId, int deliveryMode, int threshold, int flowControlState, boolean restart
   ) {
      this._parent = processor;
      this._wicletId = wicletId;
      this._aliasId = aliasId;
      this._agId = agId;
      this._deliveryMode = deliveryMode;
      this._state = 2;
      this._fcState = _SEND_ALL_CLOSED;
      this._fgQueue = new ConcurrentQueue();
      this._bgQueue = new ConcurrentQueue();
      this._activeQueue = this._bgQueue;
      this.setThreshold(threshold);
      Provider provider = this._parent.getServiceProvider();
      this._persistenceService = (PersistenceService)provider.getService(
         class$net$rim$wica$runtime$persistence$PersistenceService == null
            ? (class$net$rim$wica$runtime$persistence$PersistenceService = class$("net.rim.wica.runtime.persistence.PersistenceService"))
            : class$net$rim$wica$runtime$persistence$PersistenceService
      );
      this._lifecycleService = (LifecycleService)provider.getService(
         class$net$rim$wica$runtime$lifecycle$LifecycleService == null
            ? (class$net$rim$wica$runtime$lifecycle$LifecycleService = class$("net.rim.wica.runtime.lifecycle.LifecycleService"))
            : class$net$rim$wica$runtime$lifecycle$LifecycleService
      );
      this._persistenceHelper = (PersistenceHelper)provider.getService(
         class$net$rim$wica$runtime$messaging$internal$PersistenceHelper == null
            ? (class$net$rim$wica$runtime$messaging$internal$PersistenceHelper = class$("net.rim.wica.runtime.messaging.internal.PersistenceHelper"))
            : class$net$rim$wica$runtime$messaging$internal$PersistenceHelper
      );
      super._scheduler = (Scheduler)provider.getService("Scheduler");
      this._bgScheduler = (Scheduler)provider.getService("BackgroundScheduler");
      if (restart) {
         this._persistenceHelper.loadWicletMessages(this._wicletId, this);
      }

      this.setFlowControlState(flowControlState);
      if (this._aliasId != 0) {
         this._messageCodeMap = this._persistenceService.loadMessageUpgradeMap(this._wicletId);
      }
   }

   private final void setThreshold(int threshold) {
      if (threshold == 0) {
         InternalLogger.logWarning(
            this,
            ((StringBuffer)(new Object("Application ")))
               .append(this._wicletId)
               .append(" has inbound queue limit of 0. Application will immediately")
               .append(" suspend all messages from MDS Server.")
               .toString(),
            null,
            null
         );
      }

      synchronized (this._fcLock) {
         if (threshold == Integer.MAX_VALUE) {
            this._openUpper = threshold;
            this._closedFgUpper = threshold;
            this._closedBgUpper = threshold;
         } else {
            this._openUpper = 3 * threshold;
            this._closedFgUpper = threshold;
            this._closedBgUpper = 2 * threshold;
         }
      }
   }

   private final void enableBgProcessor(int clearIndex) {
      this._bgDisableFlag &= ~clearIndex;
      if (this._bgScheduler.isRunning() && this.haveBgMessages()) {
         this.scheduleBgProcessor();
      }
   }

   private final void disableBgProcessor(int index) {
      this._bgDisableFlag |= index;
      if (this._bgProcessor != null) {
         this._bgProcessor.stop(false, true);
         this._bgProcessor = null;
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final void scheduleBgProcessor() {
      if (this.runBgProcessor()) {
         if (this._bgProcessor == null) {
            boolean var3 = false /* VF: Semaphore variable */;

            try {
               var3 = true;
               this._bgProcessor = new BackgroundProcessor(this._wicletId, this._parent.getServiceProvider(), this);
               var3 = false;
            } finally {
               if (var3) {
                  this.disableBgProcessor(255);
                  return;
               }
            }
         }

         this._bgProcessor.requestProcessMessage();
      }
   }

   private final boolean runBgProcessor() {
      return this._bgDisableFlag == 0;
   }

   private final synchronized void scheduleForFlowControl() {
      this.schedule();
   }

   @Override
   public final boolean shouldSchedule() {
      return !super._scheduled;
   }

   @Override
   public final void run() {
      super._scheduled = false;
      this._parent.requestFlowControl(this);
   }

   private final void persistenceOpClean() {
      this._persistenceHelper.deleteWicletMessages(this._wicletId);
   }

   private final void flowControlOnNewMsg() {
      synchronized (this._fcLock) {
         this._fcState.onNewMsg(this);
      }
   }

   private final void flowControlOnNewBgMsg() {
      synchronized (this._fcLock) {
         this._fcState.onNewBgMsg(this);
      }
   }

   private final void flowControlOnQueueEmpty() {
      synchronized (this._fcLock) {
         this._fcState.onQueueEmpty(this);
      }
   }

   private final void flowControlOnOpen() {
      synchronized (this._fcLock) {
         this._fcState.onOpen(this);
      }
   }

   private final void flowControlOnDeactiveOrClose() {
      synchronized (this._fcLock) {
         this._fcState.onDeactiveOrClose(this);
      }
   }

   private final void flowControlOnNewThreshold() {
      synchronized (this._fcLock) {
         this._fcState.onNewThreshold(this);
      }
   }

   private final void flowControlOnStart() {
      synchronized (this._fcLock) {
         this._fcState.onStart(this);
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final void filterNotifications() {
      this._fgQueue.lock();
      ConcurrentQueue$Iterator i = this._fgQueue.iterator();
      String id = null;
      int posFromHead = 0;

      while (i.hasNext()) {
         MessageImpl m = (MessageImpl)i.next();
         if (!m.isNotification()) {
            posFromHead++;
         } else if (m.backgroundProcessingEnabled()) {
            this.handleBgEnabled(m);
            i.remove();
         } else if (m.keepLast()) {
            try {
               id = m.openReadableDataStream().readString();
            } catch (Throwable var9) {
               InternalLogger.logError(this, null, e, null);
               i.remove();
               continue;
            }

            if (this._replacementTable == null) {
               this._replacementTable = (Hashtable)(new Object());
            }

            if (this._replacementTable.containsKey(id)) {
               WicletQueueManager$Position p = (WicletQueueManager$Position)this._replacementTable.get(id);
               ConcurrentQueue$Iterator secondaryIterator = this._fgQueue.iterator();
               secondaryIterator.skip(p._posFromHead);
               secondaryIterator.replace(m);
               i.remove();
            } else {
               this._replacementTable.put(id, new WicletQueueManager$Position(posFromHead, 0));
               posFromHead++;
            }
         } else {
            posFromHead++;
         }
      }

      if (this._replacementTable != null) {
         int size = this._fgQueue.size() - 1;
         Enumeration e = this._replacementTable.elements();

         while (e.hasMoreElements()) {
            WicletQueueManager$Position p = (WicletQueueManager$Position)e.nextElement();
            p._posFromTail = size - p._posFromHead;
         }
      }

      this._fgQueue.unlock();
   }

   @Override
   public final String toString() {
      return "MDS Runtime Messaging.ApplicationQueueManager";
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
