package net.rim.device.apps.api.ui;

public class InvokeLaterRunnable implements Runnable {
   private Runnable _runnable;
   private boolean _doneProcessing = true;

   public void setRunnable(Runnable runnable) {
      this._runnable = runnable;
   }

   public boolean doneProcessing() {
      return this._doneProcessing;
   }

   public void resetDoneProcessing() {
      this._doneProcessing = false;
   }

   @Override
   public synchronized void run() {
      this._runnable.run();
      this._doneProcessing = true;
   }
}
