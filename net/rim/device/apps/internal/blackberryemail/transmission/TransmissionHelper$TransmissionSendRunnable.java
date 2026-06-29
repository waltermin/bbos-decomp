package net.rim.device.apps.internal.blackberryemail.transmission;

import java.util.Enumeration;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.RIMPersistentStore;
import net.rim.device.api.util.IntHashtable;
import net.rim.device.api.util.SimpleSortingVector;
import net.rim.device.apps.api.transmission.TransmissionService;
import net.rim.device.apps.api.transmission.TransmissionStatusListener;
import net.rim.device.apps.internal.blackberryemail.header.EmailHeaderModel;

final class TransmissionHelper$TransmissionSendRunnable implements TransmissionStatusListener, Runnable {
   private SimpleSortingVector _wrappers;
   private TransmissionService _service;
   private IntHashtable _sentWrappers;
   private IntHashtable _persistedWrappers;
   private boolean _run = false;
   private static long DEFAULT_WAIT_PERIOD = 300000;

   @Override
   public final synchronized void updateTransmissionStatus(TransmissionService aTransmissionService, int tag, int code, Object contextObject) {
      if (this.isKnownSentWrapper(tag)) {
         TransmissionWrapper wrapper = this.getSentWrapper(tag);
         boolean fatalError = (code & 128) != 0;
         boolean remove = false;
         switch (code) {
            case 0:
               wrapper.updateTransmissionStatus((byte)6, 0);
               if (wrapper.successOnSent()) {
                  remove = true;
                  wrapper.updateTransmissionStatus((byte)2, 0);
               } else {
                  this.handleAgingWrapper(wrapper);
               }
               break;
            case 4243:
            case 4560:
               fatalError = !this.handleResend(wrapper);
         }

         if (remove || fatalError) {
            if (fatalError) {
               wrapper.updateTransmissionStatus((byte)1, code);
            }

            this.removeSentWrapper(tag);
         }
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void run() {
      TransmissionWrapper wrapper = null;
      boolean wrapperRemoved = false;
      long time = 1;

      while (true) {
         boolean var14 = false /* VF: Semaphore variable */;
         boolean var20 = false /* VF: Semaphore variable */;

         try {
            label278:
            try {
               var20 = true;
               var14 = true;
               if (!this._run) {
                  var14 = false;
                  var20 = false;
                  break;
               }

               synchronized (this) {
                  if (time > 1) {
                     label268:
                     try {
                        this.wait(time);
                     } finally {
                        break label268;
                     }
                  }

                  if (!this._wrappers.isEmpty()) {
                     TransmissionWrapper var37 = (EmailHeaderModel)this._wrappers.elementAt(0);
                     wrapperRemoved = false;
                     time = System.currentTimeMillis();
                     boolean var26 = false /* VF: Semaphore variable */;

                     label262: {
                        try {
                           var26 = true;
                           if (var37.getScheduledActionTime() - time <= 0) {
                              this._wrappers.remove(0);
                              wrapperRemoved = true;
                              if (var37.isSendActionPending()) {
                                 this.attemptSend(var37);
                              } else {
                                 this.removeSentWrapper(var37.getTag());
                                 var37.updateTransmissionStatus((byte)2, 0);
                              }

                              time = 1;
                              var26 = false;
                              break label262;
                           }

                           time = var37.getScheduledActionTime() - time;
                           var26 = false;
                        } finally {
                           if (var26) {
                              label292: {
                                 if (!wrapperRemoved) {
                                    this._wrappers.remove(0);
                                    wrapperRemoved = true;
                                 }

                                 if (var37 != null) {
                                    this.removeSentWrapper(var37.getTag());
                                    var37.updateTransmissionStatus((byte)1, 0);
                                 }
                                 break label292;
                              }
                           }
                        }

                        var20 = false;
                        continue;
                     }

                     var20 = false;
                     continue;
                  }

                  if (time == DEFAULT_WAIT_PERIOD) {
                     this._run = false;
                  } else {
                     time = DEFAULT_WAIT_PERIOD;
                  }

                  var20 = false;
                  continue;
               }
            } finally {
               if (var20) {
                  RIMPersistentStore.destroyPersistentObject(TransmissionHelper.MESSAGING_TRANSMISSION_HELPER);
                  var14 = false;
                  break label278;
               }
            }
         } finally {
            if (var14) {
               this._run = false;
            }
         }

         this._run = false;
         return;
      }

      this._run = false;
   }

   private final synchronized void queueWrapperForFutureAction(TransmissionWrapper wrapper) {
      this._wrappers.add(wrapper);
      if (this._run) {
         this.notify();
      } else {
         this._run = true;
         new TransmissionHelper$RunnableRunThread(this);
      }
   }

   private final synchronized void sendWrapper(TransmissionWrapper wrapper) {
      this.addSentWrapper(wrapper, true);
      this.queueWrapperForFutureAction(wrapper);
   }

   private final synchronized void attemptSend(TransmissionWrapper param1) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aload 1
      // 01: invokevirtual net/rim/device/apps/internal/blackberryemail/transmission/TransmissionWrapper.canSend ()Z
      // 04: ifeq 3d
      // 07: aload 1
      // 08: invokevirtual net/rim/device/apps/internal/blackberryemail/transmission/TransmissionWrapper.incrementTransmissionAttempts ()B
      // 0b: pop
      // 0c: aload 0
      // 0d: aload 1
      // 0e: bipush 0
      // 0f: invokespecial net/rim/device/apps/internal/blackberryemail/transmission/TransmissionHelper$TransmissionSendRunnable.addSentWrapper (Lnet/rim/device/apps/internal/blackberryemail/transmission/TransmissionWrapper;Z)V
      // 12: aload 0
      // 13: getfield net/rim/device/apps/internal/blackberryemail/transmission/TransmissionHelper$TransmissionSendRunnable._service Lnet/rim/device/apps/api/transmission/TransmissionService;
      // 16: aload 1
      // 17: invokevirtual net/rim/device/apps/internal/blackberryemail/transmission/TransmissionWrapper.getType ()Ljava/lang/String;
      // 1a: aload 1
      // 1b: invokevirtual net/rim/device/apps/internal/blackberryemail/transmission/TransmissionWrapper.getObject ()Ljava/lang/Object;
      // 1e: aload 0
      // 1f: aload 1
      // 20: invokevirtual net/rim/device/apps/internal/blackberryemail/transmission/TransmissionWrapper.getTag ()I
      // 23: aload 1
      // 24: invokevirtual net/rim/device/apps/internal/blackberryemail/transmission/TransmissionWrapper.getContext ()Ljava/lang/Object;
      // 27: invokeinterface net/rim/device/apps/api/transmission/TransmissionService.transmitObject (Ljava/lang/String;Ljava/lang/Object;Lnet/rim/device/apps/api/transmission/TransmissionStatusListener;ILjava/lang/Object;)V 6
      // 2c: return
      // 2d: astore 2
      // 2e: aload 1
      // 2f: bipush 5
      // 31: bipush 0
      // 32: invokevirtual net/rim/device/apps/internal/blackberryemail/transmission/TransmissionWrapper.updateTransmissionStatus (BI)V
      // 35: return
      // 36: astore 2
      // 37: aload 1
      // 38: bipush 1
      // 39: bipush 0
      // 3a: invokevirtual net/rim/device/apps/internal/blackberryemail/transmission/TransmissionWrapper.updateTransmissionStatus (BI)V
      // 3d: return
      // try (0 -> 22): 23 null
      // try (0 -> 22): 29 null
   }

   TransmissionHelper$TransmissionSendRunnable(TransmissionService service) {
      this._wrappers = (SimpleSortingVector)(new Object());
      this._wrappers.setSortComparator(new TransmissionHelper$TransmissionWrapperComparator(null));
      this._wrappers.setSort(true);
      this._service = service;
      this._sentWrappers = (IntHashtable)(new Object());
      this._persistedWrappers = this.getWrappersFromPersistence();
   }

   private final boolean handleResend(TransmissionWrapper wrapper) {
      int delay = wrapper.getTransmissionRetryDelay();
      if (delay <= 0 || wrapper.getTransmissionRetryLimit() != -1 && wrapper.getTransmissionSendAttempts() > wrapper.getTransmissionRetryLimit()) {
         return false;
      }

      wrapper.updateTransmissionStatus((byte)3, delay);
      wrapper.setScheduledActionTime(System.currentTimeMillis() + delay, true);
      this.removeSentWrapper(wrapper.getTag());
      wrapper.generateTag();
      this.addSentWrapper(wrapper, true);
      this.queueWrapperForFutureAction(wrapper);
      return true;
   }

   private final void handleAgingWrapper(TransmissionWrapper wrapper) {
      wrapper.setScheduledActionTime(System.currentTimeMillis() + 21600000, false);
      this.wrapperUpdated(wrapper.getTag());
      this._wrappers.remove(this._wrappers.indexOf(wrapper));
      this.queueWrapperForFutureAction(wrapper);
   }

   private final void addSentWrapper(TransmissionWrapper wrapper, boolean onlyIfPersistable) {
      if (wrapper.successOnSent() && !wrapper.requiresPersitence()) {
         if (!onlyIfPersistable) {
            this._sentWrappers.put(wrapper.getTag(), wrapper);
         }
      } else {
         this._persistedWrappers.put(wrapper.getTag(), wrapper);
         PersistentObject.commit(this._persistedWrappers);
      }
   }

   private final void removeSentWrapper(int tag) {
      if (this._sentWrappers.containsKey(tag)) {
         this._sentWrappers.remove(tag);
      } else {
         if (this._persistedWrappers.containsKey(tag)) {
            this._persistedWrappers.remove(tag);
            PersistentObject.commit(this._persistedWrappers);
         }
      }
   }

   private final void wrapperUpdated(int tag) {
      if (this._persistedWrappers.containsKey(tag)) {
         PersistentObject.commit(this._persistedWrappers);
      }
   }

   private final boolean isKnownSentWrapper(int tag) {
      return this._sentWrappers.containsKey(tag) || this._persistedWrappers.containsKey(tag);
   }

   private final TransmissionWrapper getSentWrapper(int tag) {
      if (this._sentWrappers.containsKey(tag)) {
         return (EmailHeaderModel)this._sentWrappers.get(tag);
      } else {
         return this._persistedWrappers.containsKey(tag) ? (EmailHeaderModel)this._persistedWrappers.get(tag) : null;
      }
   }

   private final IntHashtable getWrappersFromPersistence() {
      PersistentObject persistentObject = RIMPersistentStore.getPersistentObject(TransmissionHelper.MESSAGING_TRANSMISSION_HELPER);
      IntHashtable wrapperTable = null;
      if (persistentObject != null) {
         wrapperTable = (IntHashtable)persistentObject.getContents();
         if (wrapperTable == null) {
            wrapperTable = (IntHashtable)(new Object());
            persistentObject.setContents(wrapperTable, 51);
            return wrapperTable;
         }

         Enumeration wrappers = wrapperTable.elements();

         while (wrappers.hasMoreElements()) {
            TransmissionWrapper wrapper = (EmailHeaderModel)wrappers.nextElement();
            this.queueWrapperForFutureAction(wrapper);
         }
      }

      return wrapperTable;
   }
}
