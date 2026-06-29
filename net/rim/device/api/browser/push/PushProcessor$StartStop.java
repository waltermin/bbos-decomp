package net.rim.device.api.browser.push;

class PushProcessor$StartStop implements Runnable {
   private boolean _start;

   public PushProcessor$StartStop(boolean start) {
      this._start = start;
   }

   @Override
   public void run() {
      if (this._start) {
         PushProcessor._instance.startImpl();
      } else {
         PushProcessor._instance.stopImpl();
      }
   }
}
