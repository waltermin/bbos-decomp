package net.rim.device.apps.internal.mms.service;

import java.util.Vector;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.api.system.PhoneCallListener;
import net.rim.device.api.system.RadioStatusListener;
import net.rim.device.apps.internal.mms.MMSUtilities;
import net.rim.device.apps.internal.mms.api.MMSTask;
import net.rim.device.apps.internal.mms.options.MMSClientServiceBook;
import net.rim.device.internal.proxy.Proxy;
import net.rim.vm.WeakReference;

public final class BackgroundTaskThread extends Thread implements RadioStatusListener, GlobalEventListener, PhoneCallListener {
   private Vector _tasklist;

   public final void mobilityManagementEvent(int eventCode, int cause) {
   }

   @Override
   public final synchronized void signalLevel(int level) {
      this.notify();
   }

   @Override
   public final synchronized void networkStateChange(int state) {
   }

   @Override
   public final synchronized void networkStarted(int networkId, int service) {
      this.notify();
   }

   @Override
   public final synchronized void networkServiceChange(int networkId, int service) {
      this.notify();
   }

   @Override
   public final void baseStationChange() {
   }

   @Override
   public final void radioTurnedOff() {
   }

   @Override
   public final void pdpStateChange(int apn, int state, int cause) {
      this.notify();
   }

   @Override
   public final void networkScanComplete(boolean success) {
   }

   @Override
   public final synchronized void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (guid == -3556743465989743742L) {
         this.notify();
      }
   }

   @Override
   public final void callIncoming(int callId) {
   }

   @Override
   public final void callDisplayUpdated(int callId) {
   }

   @Override
   public final void callWaiting(int callId) {
   }

   @Override
   public final void callInitiated(int callId) {
   }

   @Override
   public final void callConnected(int callId) {
   }

   @Override
   public final synchronized void callFailed(int callId, int error) {
      this.notify();
   }

   @Override
   public final void callDelivered(int callId) {
   }

   @Override
   public final void callManipulateFailed(int callId, int error) {
   }

   @Override
   public final synchronized void callDisconnected(int callId) {
      this.notify();
   }

   @Override
   public final void callHeld(int callId) {
   }

   @Override
   public final void callResumed(int callId) {
   }

   @Override
   public final void callAdded(int callId) {
   }

   @Override
   public final void callRemoved(int callId) {
   }

   @Override
   public final void callTransferred(int status, int reason) {
   }

   @Override
   public final void callTransferStateUpdated(int callId, int state) {
   }

   @Override
   public final void callVoicePrivacyUpdated(int callId, boolean on) {
   }

   @Override
   public final void callOTAStatusUpdated(int callId, int status) {
   }

   @Override
   public final void dtmfData(int dtmf) {
   }

   private final synchronized MMSTask getNextTask() {
      if (this._tasklist != null && this._tasklist.size() != 0) {
         MMSTask task = (MMSTask)this._tasklist.elementAt(0);
         this._tasklist.removeElementAt(0);
         return task;
      } else {
         return null;
      }
   }

   private BackgroundTaskThread() {
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final synchronized void waitForTaskPreConditions(MMSTask task) {
      if (task.requiresRadioCoverage()) {
         Application app = Application.getApplication();
         app.addRadioListener(this);
         app.addGlobalEventListener(this);

         while (true) {
            boolean var7 = false /* VF: Semaphore variable */;

            try {
               var7 = true;
               if (MMSUtilities.hasDataCoverage()) {
                  var7 = false;
                  break;
               }

               try {
                  this.wait();
               } finally {
                  continue;
               }
            } finally {
               if (var7) {
                  app.removeRadioListener(this);
                  app.removeGlobalEventListener(this);
               }
            }
         }

         app.removeRadioListener(this);
         app.removeGlobalEventListener(this);
      }
   }

   private static final BackgroundTaskThread getInstance(long guid) {
      int max = MMSClientServiceBook.getMaxTransportThreads();
      if (max <= 1 && guid != -3436621066262173388L) {
         guid = -1627158063255138358L;
      }

      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      WeakReference ref = (WeakReference)ar.get(guid);
      if (ref != null) {
         BackgroundTaskThread thread = (BackgroundTaskThread)ref.get();
         if (thread != null && thread.isAlive()) {
            return thread;
         }
      }

      BackgroundTaskThread thread = new BackgroundTaskThread();
      ar.replace(guid, new Object(thread));
      return thread;
   }

   public static final void addTask(MMSTask task, int seconds) {
      Proxy.getInstance().invokeLater(new BackgroundTaskThread$1TaskAppendingRunnable(task), seconds * 1000, false);
   }

   public static final void addTask(MMSTask task) {
      while (true) {
         BackgroundTaskThread thread = getInstance(task.getTaskThreadGuid());
         synchronized (thread) {
            if (thread.isNotStarted() || thread.isAlive()) {
               thread.addRunnable(task);
               return;
            }
         }
      }
   }

   @Override
   public final void run() {
      System.out.println("MMS TaskThread starting.");

      while (this.haveNextTask()) {
         try {
            MMSTask task = this.getNextTask();
            if (task != null) {
               this.waitForTaskPreConditions(task);
               task.run();
            }
         } finally {
            continue;
         }
      }

      System.out.println("MMS TaskThread done.");
   }

   private final boolean isNotStarted() {
      return this._tasklist == null;
   }

   private final synchronized void addRunnable(Runnable task) {
      if (this.isNotStarted()) {
         this._tasklist = (Vector)(new Object());
         this._tasklist.addElement(task);
         Proxy.getInstance().startThread(this);
      } else {
         this._tasklist.addElement(task);
      }
   }

   private final synchronized boolean haveNextTask() {
      return this._tasklist != null && this._tasklist.size() > 0;
   }
}
