package net.rim.device.apps.internal.browser.javascript;

import net.rim.ecmascript.runtime.GlobalObject;

class JavaScriptEngine$WatchDog implements Runnable {
   private final JavaScriptEngine this$0;

   JavaScriptEngine$WatchDog(JavaScriptEngine _1) {
      this.this$0 = _1;
   }

   @Override
   public void run() {
      GlobalObject obj = this.this$0._globalObject;
      if (obj != null) {
         obj.stop();
      }

      this.this$0._watchDogId = -1;
   }
}
