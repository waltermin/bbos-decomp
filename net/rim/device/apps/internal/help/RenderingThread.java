package net.rim.device.apps.internal.help;

import net.rim.device.api.browser.field.BrowserContent;
import net.rim.device.api.system.Application;

final class RenderingThread extends Thread {
   private BrowserContent _browserField;
   private int _scrollPosition;
   private int _focusPosition;
   private HelpScreen _screenReference;

   RenderingThread(BrowserContent field, int scrollPosition, int focusPosition, HelpScreen screenReference) {
      this._browserField = field;
      this._scrollPosition = scrollPosition;
      this._focusPosition = focusPosition;
      this._screenReference = screenReference;
   }

   @Override
   public final void run() {
      try {
         this._browserField.finishLoading();
         this._screenReference.topicFinishedLoading();
         if (this._scrollPosition != -1) {
            Application.getApplication().invokeLater(new RenderingThread$1(this));
            return;
         }
      } finally {
         return;
      }
   }
}
