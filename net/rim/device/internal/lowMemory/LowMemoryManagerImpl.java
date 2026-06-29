package net.rim.device.internal.lowMemory;

import net.rim.device.api.lowmemory.LowMemoryFailedListener;
import net.rim.device.api.lowmemory.LowMemoryListener;
import net.rim.device.api.lowmemory.LowMemoryManager;
import net.rim.device.api.system.ControlledAccess;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.api.system.RIMPersistentStore;
import net.rim.device.api.util.ListenerUtilities;
import net.rim.device.internal.system.ExactLowMemoryManager;
import net.rim.vm.Memory;

final class LowMemoryManagerImpl extends LowMemoryManager implements ExactLowMemoryManager, GlobalEventListener, Runnable {
   private Object[] _listeners;
   private Object[] _failedListeners;
   private boolean _started;
   private static int POLL_TYPE_EXPLICIT = 0;
   private static int POLL_TYPE_AUTOMATIC = 1;
   private static int POLL_TYPE_EXACT = 2;
   private static final int PURGES_FROM_ONE_LISTENER = 8;
   private static final int FLASH_FROM_ONE_LISTENER = 8192;

   @Override
   protected final synchronized void doAddLowMemoryListener(LowMemoryListener listener) {
      this._listeners = ListenerUtilities.addListener(this._listeners, listener);
   }

   @Override
   protected final synchronized void doRemoveLowMemoryListener(LowMemoryListener listener) {
      this._listeners = ListenerUtilities.removeListener(this._listeners, listener);
   }

   @Override
   protected final synchronized void doAddLowMemoryFailedListener(LowMemoryFailedListener listener) {
      this._failedListeners = ListenerUtilities.addListener(this._failedListeners, listener);
   }

   @Override
   protected final synchronized void doRemoveLowMemoryFailedListener(LowMemoryFailedListener listener) {
      this._failedListeners = ListenerUtilities.removeListener(this._failedListeners, listener);
   }

   private static final boolean stopRecoveringFlash(int pollType) {
      return Memory.stopRecoveringFlash(pollType == POLL_TYPE_EXPLICIT || pollType == POLL_TYPE_EXACT);
   }

   private static final int getFlashNeeded(int pollType) {
      return Memory.getFlashNeeded(pollType == POLL_TYPE_EXPLICIT);
   }

   private static final int getHandlesNeeded(int pollType) {
      return Memory.getHandlesNeeded(pollType == POLL_TYPE_EXPLICIT);
   }

   private static final boolean recoveryNeeded(int pollType) {
      return stopRecoveringFlash(pollType) ? false : getFlashNeeded(pollType) > 0 || getHandlesNeeded(pollType) > 0;
   }

   @Override
   protected final void doPoll(boolean explicit) {
      int pollType = explicit ? POLL_TYPE_EXPLICIT : POLL_TYPE_AUTOMATIC;
      this.doPoll(pollType);
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   protected final void doPoll(int pollType) {
      boolean var19 = false /* VF: Semaphore variable */;

      label179: {
         label180: {
            label181: {
               try {
                  var19 = true;
                  synchronized (RIMPersistentStore.getSynchObject()) {
                     if (!recoveryNeeded(pollType)) {
                        var19 = false;
                        break label179;
                     }

                     for (int prio = 0; prio <= 2; prio++) {
                        if (this.freeStaleObjects(prio, pollType)) {
                           Memory.suggestThoroughGC();
                           var19 = false;
                           break label180;
                        }
                     }

                     System.gc();
                     if (!recoveryNeeded(pollType)) {
                        var19 = false;
                        break label181;
                     }

                     this.notifyFailedListeners();
                     Memory.recoverFlash(0);
                     var19 = false;
                  }
               } finally {
                  if (var19) {
                     if (pollType == POLL_TYPE_AUTOMATIC) {
                        synchronized (this) {
                           this._started = false;
                        }
                     }
                  }
               }

               if (pollType == POLL_TYPE_AUTOMATIC) {
                  synchronized (this) {
                     this._started = false;
                     return;
                  }
               }

               return;
            }

            if (pollType == POLL_TYPE_AUTOMATIC) {
               synchronized (this) {
                  this._started = false;
                  return;
               }
            }

            return;
         }

         if (pollType == POLL_TYPE_AUTOMATIC) {
            synchronized (this) {
               this._started = false;
               return;
            }
         }

         return;
      }

      if (pollType == POLL_TYPE_AUTOMATIC) {
         synchronized (this) {
            this._started = false;
         }
      }
   }

   private final boolean freeStaleObjects(int priority, int pollType) {
      Object[] listeners = this._listeners;
      if (listeners == null) {
         return false;
      }

      int lo = 0;
      int hi = listeners.length - 1;

      while (true) {
         int nextLo = -1;
         int nextHi = -1;

         label115:
         for (int i = lo; i <= hi; i++) {
            LowMemoryListener listener = (LowMemoryListener)listeners[i];
            int howManyLeft = lo == hi ? Integer.MAX_VALUE : 8;
            int howMuchLeft = lo == hi ? Integer.MAX_VALUE : 8192;
            int lastFlashNeeded = getFlashNeeded(pollType);

            do {
               int beforeRecoverableCalls = Memory.getRecoverableCalls();

               try {
                  if (!listener.freeStaleObject(priority)) {
                     continue label115;
                  }
               } finally {
                  continue label115;
               }

               if (!recoveryNeeded(pollType)) {
                  return true;
               }

               int afterRecoverableCalls = Memory.getRecoverableCalls();
               if (afterRecoverableCalls == beforeRecoverableCalls) {
                  continue label115;
               }

               howManyLeft--;
               int currFlashNeeded = getFlashNeeded(pollType);
               if (currFlashNeeded < lastFlashNeeded) {
                  howMuchLeft -= lastFlashNeeded - currFlashNeeded;
               }

               lastFlashNeeded = currFlashNeeded;
            } while (howManyLeft > 0 && howMuchLeft > 0);

            if (nextHi < 0) {
               nextLo = i;
            }

            nextHi = i;
         }

         if (nextHi < 0) {
            return false;
         }

         lo = nextLo;
         hi = nextHi;
      }
   }

   private final void notifyFailedListeners() {
      System.out.println("LMM: failed!");
      Object[] listeners = this._failedListeners;
      if (listeners != null) {
         int num = listeners.length;

         for (int i = 0; i < num; i++) {
            LowMemoryFailedListener listener = (LowMemoryFailedListener)listeners[i];

            try {
               listener.lowMemoryManagerFailed();
            } finally {
               continue;
            }
         }
      }
   }

   @Override
   public final void run() {
      this.doPoll(false);
   }

   @Override
   public final void exactPoll() {
      ControlledAccess.assertRRISignatures(true);
      this.doPoll(POLL_TYPE_EXACT);
   }

   @Override
   public final synchronized void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (guid == 945659952435832745L && !this._started) {
         this._started = true;
         ((Thread)(new Object(this))).start();
      }
   }
}
