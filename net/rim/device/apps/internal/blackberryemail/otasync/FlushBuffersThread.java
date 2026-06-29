package net.rim.device.apps.internal.blackberryemail.otasync;

import net.rim.device.api.system.DeviceInfo;

class FlushBuffersThread extends Thread {
   private OTAMessageSync _syncInstance;
   private boolean _checkForWork;
   private static final long MINIMUM_IDLE_TIME;

   FlushBuffersThread(OTAMessageSync syncInstance) {
      this._syncInstance = syncInstance;
   }

   void activateFlush() {
      this._checkForWork = true;
      synchronized (this) {
         this.notify();
      }
   }

   @Override
   public void run() {
      while (true) {
         if (!this._checkForWork) {
            label73:
            try {
               synchronized (this) {
                  this.wait();
               }
            } finally {
               break label73;
            }
         }

         for (long idleTime = DeviceInfo.getIdleTime(); idleTime < 5; idleTime = DeviceInfo.getIdleTime()) {
            try {
               Thread.sleep((5 - idleTime) * 1000);
            } finally {
               continue;
            }
         }

         this._checkForWork = false;
         this._syncInstance.flushBatchedCommands();
      }
   }
}
