package net.rim.device.internal.ui;

import net.rim.device.api.ui.component.Dialog;

class MessageTiming implements Runnable {
   private long _start = System.currentTimeMillis();

   public MessageTiming() {
   }

   @Override
   public void run() {
      long end = System.currentTimeMillis();
      long elapsed = end - this._start;
      Dialog.inform("100 iterations in " + Backdoor.sayTime(elapsed) + "s. " + ' ' + Backdoor.sayTime(elapsed * 1000 / 100) + "ms per iteration.");
   }
}
