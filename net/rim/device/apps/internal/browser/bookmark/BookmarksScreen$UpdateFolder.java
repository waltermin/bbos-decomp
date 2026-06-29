package net.rim.device.apps.internal.browser.bookmark;

final class BookmarksScreen$UpdateFolder implements Runnable {
   @Override
   public final void run() {
      try {
         BookmarksScreen.updateHomePageBookmarks();
      } finally {
         return;
      }
   }
}
