package net.rim.device.apps.internal.browser.html;

class HTMLImg$1 implements Runnable {
   private final HTMLBrowserContent val$parent;
   private final String val$onLoad;
   private final HTMLImg this$0;

   HTMLImg$1(HTMLImg _1, HTMLBrowserContent _2, String _3) {
      this.this$0 = _1;
      this.val$parent = _2;
      this.val$onLoad = _3;
   }

   @Override
   public void run() {
      try {
         this.val$parent.executeJavaScriptAction(this, this.val$onLoad, null);
      } finally {
         return;
      }
   }
}
