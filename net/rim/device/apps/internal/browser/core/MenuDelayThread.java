package net.rim.device.apps.internal.browser.core;

import net.rim.device.apps.internal.browser.bookmark.BookmarksScreen;

public final class MenuDelayThread extends Thread {
   private int _delay;
   private BrowserImpl$NavigationThread _navigationThread;
   private BookmarksScreen _bookmarksScreen;
   private boolean _inTimer;

   MenuDelayThread(BrowserImpl$NavigationThread navigationThread, int delay) {
      this._navigationThread = navigationThread;
      this._delay = delay;
   }

   @Override
   public final synchronized void run() {
      while (true) {
         if (!this._inTimer) {
            try {
               this.wait();
            } finally {
               continue;
            }
         } else {
            label55:
            try {
               this.wait(this._delay);
            } finally {
               break label55;
            }

            if (this._inTimer) {
               if (this._bookmarksScreen != null) {
                  this._bookmarksScreen.trackwheelHold();
               } else {
                  this._navigationThread.requestTask(9, null);
               }

               this._inTimer = false;
            }
         }
      }
   }

   public final synchronized void trackwheelClicked() {
      this._inTimer = true;
      this.notify();
   }

   public final synchronized boolean trackwheelUnclicked() {
      boolean retVal = this._inTimer;
      this._inTimer = false;
      this.notify();
      return retVal;
   }

   public final synchronized void setBookmarksScreen(BookmarksScreen bookmarksScreen) {
      this._bookmarksScreen = bookmarksScreen;
   }

   public final void setDelay(int delay) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }
}
