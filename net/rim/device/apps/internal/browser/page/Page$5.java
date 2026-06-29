package net.rim.device.apps.internal.browser.page;

class Page$5 implements Runnable {
   private final BrowserScreen val$browserScreen;
   private final boolean val$onLhs;
   private final Page this$0;

   Page$5(Page _1, BrowserScreen _2, boolean _3) {
      this.this$0 = _1;
      this.val$browserScreen = _2;
      this.val$onLhs = _3;
   }

   @Override
   public void run() {
      this.val$browserScreen.setScrollbarLocation(this.val$onLhs);
   }
}
