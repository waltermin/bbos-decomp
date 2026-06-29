package net.rim.device.apps.internal.browser.verbs;

import net.rim.device.api.system.Application;

class HttpAddressLinkVerb$RunItOnAnotherThread implements Runnable {
   private Object _context;
   private final HttpAddressLinkVerb this$0;

   HttpAddressLinkVerb$RunItOnAnotherThread(HttpAddressLinkVerb _1, Object context) {
      this.this$0 = _1;
      this._context = context;
   }

   @Override
   public void run() {
      if (Application.isEventDispatchThread()) {
         this.this$0._onClickAction = null;
      }

      this.this$0.invoke(this._context);
   }
}
