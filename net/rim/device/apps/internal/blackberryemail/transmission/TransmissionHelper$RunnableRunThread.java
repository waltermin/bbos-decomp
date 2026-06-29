package net.rim.device.apps.internal.blackberryemail.transmission;

final class TransmissionHelper$RunnableRunThread extends Thread {
   Runnable _runnable;

   TransmissionHelper$RunnableRunThread(Runnable runnable) {
      this._runnable = runnable;
      this.start();
   }

   @Override
   public final void run() {
      this._runnable.run();
   }
}
