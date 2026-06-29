package com.fourthpass.wapstack.util;

import java.util.Timer;

class WTimer$1 implements Runnable {
   private final WTimer this$0;

   WTimer$1(WTimer _1) {
      this.this$0 = _1;
   }

   @Override
   public void run() {
      this.this$0._timer = new Timer();
   }
}
