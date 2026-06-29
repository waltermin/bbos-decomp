package net.rim.wica.common.debug.session;

class StoppableLoopThread extends Thread {
   private boolean _canContinue;
   private ThreadListener _listener;

   public final synchronized boolean canContinue() {
      return this._canContinue;
   }

   public final synchronized void stopLoop() {
      this._canContinue = false;
   }

   public final synchronized void stopLoopWithInterrupt() {
      this.stopLoop();
      this.interrupt();
   }

   @Override
   public synchronized void start() {
      this._canContinue = true;
      super.start();
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void run() {
      while (this.canContinue()) {
         try {
            this.doRunLoopOnce();
         } catch (Throwable var3) {
            e.printStackTrace();
            this.onError();
            continue;
         }
      }
   }

   public void setListener(ThreadListener l) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   protected void onError() {
      this.stopLoop();
      if (this._listener != null) {
         this._listener.notifyError(this);
      }
   }

   protected void doRunLoopOnce() {
      throw null;
   }
}
