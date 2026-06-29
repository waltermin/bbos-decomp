package net.rim.device.apps.internal.browser.webfeed;

final class WebFeedItemManager$9 extends Thread {
   private final WebFeedItemManager this$0;

   WebFeedItemManager$9(WebFeedItemManager _1) {
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      try {
         this.this$0._descriptionContent.finishLoading();
      } finally {
         return;
      }
   }
}
