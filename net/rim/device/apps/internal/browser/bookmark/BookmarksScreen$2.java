package net.rim.device.apps.internal.browser.bookmark;

class BookmarksScreen$2 implements Runnable {
   private final BookmarksScreen this$0;

   BookmarksScreen$2(BookmarksScreen _1) {
      this.this$0 = _1;
   }

   @Override
   public void run() {
      if (this.this$0._isInitialScreen) {
         this.this$0._bookmarksScreenCloseVerb.invoke(Boolean.TRUE);
      } else {
         this.this$0._bookmarksScreenCloseVerb.invoke(null);
      }
   }
}
