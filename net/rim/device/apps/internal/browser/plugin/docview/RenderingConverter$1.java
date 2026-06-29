package net.rim.device.apps.internal.browser.plugin.docview;

class RenderingConverter$1 implements Runnable {
   private final UCSPluginNotify val$notify;
   private final RenderingConverter this$0;

   RenderingConverter$1(RenderingConverter _1, UCSPluginNotify _2) {
      this.this$0 = _1;
      this.val$notify = _2;
   }

   @Override
   public void run() {
      this.val$notify.moreDataParsed();
   }
}
