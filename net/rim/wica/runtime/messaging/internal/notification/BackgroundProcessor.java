package net.rim.wica.runtime.messaging.internal.notification;

import net.rim.device.api.system.UnsupportedOperationException;
import net.rim.wica.runtime.lifecycle.LifecycleService;
import net.rim.wica.runtime.lifecycle.Wiclet;
import net.rim.wica.runtime.messaging.InboundQueueConnection;
import net.rim.wica.runtime.messaging.Message;
import net.rim.wica.runtime.messaging.ReadableDataStream;
import net.rim.wica.runtime.messaging.internal.Processor;
import net.rim.wica.runtime.messaging.internal.Scheduler;
import net.rim.wica.runtime.messaging.internal.util.InternalLogger;
import net.rim.wica.runtime.messaging.internal.util.Provider;
import net.rim.wica.runtime.metadata.WicletContext;
import net.rim.wica.runtime.metadata.WicletRuntime;
import net.rim.wica.runtime.metadata.component.Msg;
import net.rim.wica.runtime.metadata.internal.component.MsgImpl;
import net.rim.wica.runtime.metadata.internal.transaction.TransactionManager;
import net.rim.wica.runtime.metadata.internal.util.DataDecoder;
import net.rim.wica.runtime.metadata.internal.util.MsgMapper;
import net.rim.wica.runtime.metadata.internal.util.PersistenceListener;
import net.rim.wica.runtime.service.Container;
import net.rim.wica.runtime.service.DefaultContainer;

public class BackgroundProcessor extends Processor implements WicletRuntime {
   private boolean _run;
   private BackgroundWiclet _wiclet;
   private DataDecoder _decoder;
   private MsgMapper _msgMapper;
   private TransactionManager _transactions;
   private PersistenceListener _persistenceListener;
   private Container _container;
   private InboundQueueConnection _connection;
   static Class class$net$rim$wica$runtime$metadata$WicletRuntime;
   static Class class$net$rim$wica$runtime$access$data$AccessDataService;
   static Class class$net$rim$wica$runtime$metadata$internal$transaction$TransactionManager;
   static Class class$net$rim$wica$runtime$metadata$internal$util$PersistenceListener;
   static Class class$net$rim$wica$runtime$lifecycle$LifecycleService;

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public BackgroundProcessor(long wicletId, Provider provider, InboundQueueConnection connection) {
      this._connection = connection;
      this._run = true;
      super._scheduler = (Scheduler)provider.getService("BackgroundScheduler");

      try {
         this._container = new DefaultContainer(provider);
         this._container
            .registerComponent(
               class$net$rim$wica$runtime$metadata$WicletRuntime == null
                  ? (class$net$rim$wica$runtime$metadata$WicletRuntime = class$("net.rim.wica.runtime.metadata.WicletRuntime"))
                  : class$net$rim$wica$runtime$metadata$WicletRuntime,
               this
            );
         this._container
            .registerComponent(
               class$net$rim$wica$runtime$access$data$AccessDataService == null
                  ? (class$net$rim$wica$runtime$access$data$AccessDataService = class$("net.rim.wica.runtime.access.data.AccessDataService"))
                  : class$net$rim$wica$runtime$access$data$AccessDataService,
               Class.forName("net.rim.wica.runtime.access.internal.data.AccessDataServiceImpl")
            );
         this.load(wicletId);
         this._container
            .registerComponent(
               class$net$rim$wica$runtime$metadata$internal$transaction$TransactionManager == null
                  ? (
                     class$net$rim$wica$runtime$metadata$internal$transaction$TransactionManager = class$(
                        "net.rim.wica.runtime.metadata.internal.transaction.TransactionManager"
                     )
                  )
                  : class$net$rim$wica$runtime$metadata$internal$transaction$TransactionManager,
               Class.forName("net.rim.wica.runtime.metadata.internal.transaction.TransactionManager")
            );
         this._container
            .registerComponent(
               class$net$rim$wica$runtime$metadata$internal$util$PersistenceListener == null
                  ? (
                     class$net$rim$wica$runtime$metadata$internal$util$PersistenceListener = class$(
                        "net.rim.wica.runtime.metadata.internal.util.PersistenceListener"
                     )
                  )
                  : class$net$rim$wica$runtime$metadata$internal$util$PersistenceListener,
               class$net$rim$wica$runtime$metadata$internal$util$PersistenceListener == null
                  ? (
                     class$net$rim$wica$runtime$metadata$internal$util$PersistenceListener = class$(
                        "net.rim.wica.runtime.metadata.internal.util.PersistenceListener"
                     )
                  )
                  : class$net$rim$wica$runtime$metadata$internal$util$PersistenceListener
            );
         this._persistenceListener = (PersistenceListener)this._container
            .getService(
               class$net$rim$wica$runtime$metadata$internal$util$PersistenceListener == null
                  ? (
                     class$net$rim$wica$runtime$metadata$internal$util$PersistenceListener = class$(
                        "net.rim.wica.runtime.metadata.internal.util.PersistenceListener"
                     )
                  )
                  : class$net$rim$wica$runtime$metadata$internal$util$PersistenceListener
            );
         this._transactions = (TransactionManager)this._container
            .getService(
               class$net$rim$wica$runtime$metadata$internal$transaction$TransactionManager == null
                  ? (
                     class$net$rim$wica$runtime$metadata$internal$transaction$TransactionManager = class$(
                        "net.rim.wica.runtime.metadata.internal.transaction.TransactionManager"
                     )
                  )
                  : class$net$rim$wica$runtime$metadata$internal$transaction$TransactionManager
            );
         this._decoder = new DataDecoder(this._wiclet);
         this._msgMapper = new MsgMapper(this._wiclet);
      } catch (Throwable var7) {
         InternalLogger.logError(this, null, e, null);
         throw new RuntimeException("Unable to create background processor");
      }
   }

   private void load(long wicletId) {
      LifecycleService lc = (LifecycleService)this._container
         .getService(
            class$net$rim$wica$runtime$lifecycle$LifecycleService == null
               ? (class$net$rim$wica$runtime$lifecycle$LifecycleService = class$("net.rim.wica.runtime.lifecycle.LifecycleService"))
               : class$net$rim$wica$runtime$lifecycle$LifecycleService
         );
      Wiclet context = lc.getWiclet(wicletId);
      this._wiclet = new BackgroundWiclet(context, this);
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public void reload(long wicletId) {
      this._wiclet.getDataLock().acquire();
      boolean var5 = false /* VF: Semaphore variable */;

      try {
         var5 = true;
         this.load(wicletId);
         this._decoder = new DataDecoder(this._wiclet);
         this._msgMapper = new MsgMapper(this._wiclet);
         var5 = false;
      } finally {
         if (var5) {
            this._wiclet.getDataLock().release();
         }
      }

      this._wiclet.getDataLock().release();
   }

   @Override
   protected boolean shouldSchedule() {
      return this._run && !super._scheduled && !this._connection.isEmpty();
   }

   @Override
   public synchronized void schedule() {
      if (!super._scheduled) {
         super._scheduled = true;
         super._scheduler.schedule(this);
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public void run() {
      this._wiclet.getDataLock().acquire();
      boolean var4 = false /* VF: Semaphore variable */;

      try {
         var4 = true;
         if (!this._run) {
            var4 = false;
         } else {
            Message m;
            while ((m = this._connection.getNextMessage()) != null) {
               this.processNotification(m);
            }

            var4 = false;
         }
      } finally {
         if (var4) {
            this._wiclet.getDataLock().release();
            this.reschedule();
         }
      }

      this._wiclet.getDataLock().release();
      this.reschedule();
   }

   @Override
   public void start(WicletContext context) {
      this._run = true;
   }

   @Override
   public net.rim.wica.runtime.metadata.Wiclet getWiclet() {
      return this._wiclet;
   }

   @Override
   public boolean isStarted() {
      return this._run;
   }

   @Override
   public boolean isActive() {
      return this._run;
   }

   @Override
   public void activate() {
      this._run = true;
   }

   @Override
   public void deactivate() {
      this.stop(false, true);
   }

   @Override
   public void notifyStartupLock() {
      throw new UnsupportedOperationException();
   }

   @Override
   public void stop(boolean errorCondition, boolean forceful) {
      this._wiclet.getDataLock().acquire();
      this._run = false;
      this._persistenceListener.shutdown();
      this._wiclet.getDataLock().release();
   }

   @Override
   public void stop() {
      throw new UnsupportedOperationException();
   }

   @Override
   public void requestScreenBack() {
      throw new UnsupportedOperationException();
   }

   @Override
   public void requestProcessMessage() {
      this.schedule();
   }

   @Override
   public void requestMenuShow(int instance) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void enqueueRunnable(Runnable runnable) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void enqueuePriorityRunnable(Runnable runnable) {
      this._wiclet.getDataLock().acquire();
      if (this._run) {
         this._run = false;
         super._scheduler.schedulePriority(runnable);
         this._run = true;
      }

      this._wiclet.getDataLock().release();
      if (this.shouldSchedule()) {
         this.schedule();
      }
   }

   @Override
   public Object getService(Class serviceInterface) {
      return this._container.getService(serviceInterface);
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private void processNotification(Message m) {
      Msg wicletMsg = null;
      int transactionId = -1;
      boolean var10 = false /* VF: Semaphore variable */;
      boolean var13 = false /* VF: Semaphore variable */;

      label188: {
         try {
            try {
               var13 = true;
               var10 = true;
               String e = m.getMessageName();
               int msgCode = m.getMessageCode();
               wicletMsg = e != null ? this._wiclet.getMsg(this._wiclet.getDefHandle(e)) : this._wiclet.getMsgFromCode(msgCode);
               if (this._msgMapper.hasMapping(wicletMsg)) {
                  if (this._transactions != null) {
                     transactionId = this._transactions.startTransaction();
                  }

                  this._persistenceListener.startTransaction();
                  ReadableDataStream in = m.openReadableDataStream();
                  this._decoder.decode(wicletMsg, in);
                  this._msgMapper.applyInMapping(wicletMsg);
                  if (wicletMsg instanceof MsgImpl) {
                     ((MsgImpl)wicletMsg).clean();
                  }

                  if (transactionId != -1) {
                     this._transactions.complete(false, transactionId, true, true);
                  }

                  this._persistenceListener.commitTransaction();
                  var10 = false;
                  var13 = false;
               } else {
                  var10 = false;
                  var13 = false;
               }
               break label188;
            } finally {
               if (var13) {
                  label182: {
                     if (wicletMsg instanceof MsgImpl) {
                        ((MsgImpl)wicletMsg).clean();
                     }

                     if (transactionId != -1) {
                        this._transactions.undo(false, transactionId, true);
                     }

                     this._persistenceListener.undoTransaction();
                     var10 = false;
                     break label182;
                  }
               }
            }
         } finally {
            if (var10) {
               if (transactionId != -1) {
                  this._transactions.removeTrans(transactionId);
               }

               if (this._persistenceListener.isPersistenceReadable()) {
                  this._wiclet.save();
                  this._wiclet.clear();
               }
            }
         }

         if (transactionId != -1) {
            this._transactions.removeTrans(transactionId);
         }

         if (this._persistenceListener.isPersistenceReadable()) {
            this._wiclet.save();
            this._wiclet.clear();
            return;
         }

         return;
      }

      if (transactionId != -1) {
         this._transactions.removeTrans(transactionId);
      }

      if (this._persistenceListener.isPersistenceReadable()) {
         this._wiclet.save();
         this._wiclet.clear();
      }
   }

   @Override
   public String toString() {
      return "MDS Runtime Messaging.BackgroundProcessor";
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
