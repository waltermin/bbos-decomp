package net.rim.device.apps.internal.browser.push;

import net.rim.device.api.browser.push.PushOptions;
import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.apps.internal.browser.bookmark.BookmarksScreen;
import net.rim.device.apps.internal.browser.core.BrowserDaemonRegistry;
import net.rim.device.apps.internal.browser.core.BrowserImpl;

public class BrowserPushModel extends BasePushModel {
   private boolean _performAction;

   @Override
   public boolean showBrowser() {
      switch (PushOptions.getOptions().getAcceptMode(2, this.mapPushSourceToProtocolType(super._connectionType))) {
         case 0:
            return false;
         case 2:
            return false;
         default:
            return true;
      }
   }

   @Override
   public int rejectMessage() {
      return this.rejectMessage(2);
   }

   public void run(int messageId, String param) {
      this._performAction = true;
      if (PushOptions.getOptions().getAcceptMode(2, this.mapPushSourceToProtocolType(super._connectionType)) == 1) {
         Screen activeScreen = UiApplication.getUiApplication().getActiveScreen();
         if (activeScreen instanceof BookmarksScreen) {
            activeScreen.close();
         }

         PushDisplayDialog dialog = new PushDisplayDialog(MessageFormat.format(BrowserPushResources.getString(messageId), new String[]{param}));
         dialog.setModal(true);
         dialog.show();
         if (dialog.getCloseReason() != 0) {
            this._performAction = false;
         }

         BrowserImpl browser = BrowserDaemonRegistry.getInstance();
         if (!this.getBrowserForeground() && browser.enquedPushSize() == 0) {
            browser.closeBrowser(false);
         }
      }
   }

   protected boolean performAction() {
      return this._performAction;
   }

   protected void doAction() {
      throw null;
   }
}
