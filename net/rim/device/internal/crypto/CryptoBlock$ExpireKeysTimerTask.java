package net.rim.device.internal.crypto;

import java.util.TimerTask;

class CryptoBlock$ExpireKeysTimerTask extends TimerTask implements Runnable {
   int _expireKeyTaskID = -1;

   private CryptoBlock$ExpireKeysTimerTask() {
   }

   @Override
   public void run() {
      synchronized (CryptoBlock._persistentKeysById) {
         this._expireKeyTaskID = -1;
         CryptoBlock.scanAndScheduleToRemoveExpiredKeys(1);
      }
   }

   CryptoBlock$ExpireKeysTimerTask(CryptoBlock$1 x0) {
      this();
   }
}
