package net.rim.device.internal.synchronization.ota.session;

import net.rim.device.internal.synchronization.ota.api.Logger;

final class CpTicketHolder extends Thread {
   private Object _cpTicket;
   private long _timeToWait;
   private int _action;
   private static final byte KILL_TIMER_ACTION = 1;
   private static final byte RESET_TIMER_ACTION = 2;

   public CpTicketHolder(Object cpTicket) {
      this.setPriority(10);
      this._cpTicket = cpTicket;
   }

   @Override
   public final void run() {
      synchronized (this) {
         Logger.logCPTicket(true);

         label47:
         try {
            while (this._action != 1) {
               if (this._action == 2) {
                  this._action = 0;
               }

               this.wait(this._timeToWait);
               if (this._action == 0) {
                  break;
               }
            }
         } finally {
            break label47;
         }

         this._cpTicket = null;
         Logger.logCPTicket(false);
      }
   }

   public final void setTimer(long time) {
      synchronized (this) {
         this._timeToWait = time;
         this.resetTimer();
      }
   }

   public final void resetTimer() {
      synchronized (this) {
         if (this._action != 1) {
            this._action = 2;
            this.notify();
         }
      }
   }

   public final void killTimer() {
      synchronized (this) {
         this._action = 1;
         this.notify();
      }

      try {
         this.join();
      } finally {
         return;
      }
   }

   public final Object getTicket() {
      synchronized (this) {
         return this._cpTicket;
      }
   }
}
