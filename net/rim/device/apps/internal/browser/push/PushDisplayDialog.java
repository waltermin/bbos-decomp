package net.rim.device.apps.internal.browser.push;

import net.rim.device.api.notification.NotificationsManager;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.container.DialogFieldManager;
import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.apps.api.ui.CookieProviderUtilities;
import net.rim.device.apps.internal.browser.bookmark.BookmarksScreen;
import net.rim.device.apps.internal.browser.verbs.FollowLinkVerb;
import net.rim.device.internal.ui.component.PopupDialog;

public final class PushDisplayDialog extends PopupDialog implements FieldChangeListener {
   private PushActiveRichTextField _urlField;
   private ButtonField _loadButton;
   private ButtonField _postponeButton;
   private ButtonField _dismissButton;
   private DialogFieldManager _dfm;
   private String _url;
   public static final int POSTPONE = 1;

   final void loadPage(Object cookie) {
      if (cookie != null) {
         this.close(0);
         new FollowLinkVerb(this._url).invoke(null);
         Screen activeScreen = UiApplication.getUiApplication().getActiveScreen();
         if (activeScreen instanceof BookmarksScreen) {
            activeScreen.close();
         }
      }
   }

   @Override
   public final void fieldChanged(Field field, int context) {
      if (field == this._loadButton) {
         if (this._url != null) {
            this.loadPage(CookieProviderUtilities.getDefaultCookie(this._urlField.getCookieWithFocus()));
         } else {
            this.close(0);
         }
      } else if (field == this._postponeButton) {
         this.close(1);
      } else {
         if (field == this._dismissButton) {
            this.close(-1);
         }
      }
   }

   private PushDisplayDialog(String message, String url, boolean yesNo) {
      super(new DialogFieldManager(), 0);
      NotificationsManager.cancelImmediateEvent(4665536253483290822L, 0, null, null);
      NotificationsManager.triggerImmediateEvent(4665536253483290822L, 0, null, null);
      this._dfm = (DialogFieldManager)this.getDelegate();
      this._url = url;
      String displayMessage = message + '\n';
      if (url != null) {
         displayMessage = displayMessage + url;
      }

      this._urlField = new PushActiveRichTextField(displayMessage, this);
      this._dfm.setMessage(this._urlField);
      String[] yesNoArray = CommonResources.getYesNoArray(0);
      this._loadButton = new ButtonField(yesNo ? yesNoArray[0] : BrowserPushResources.getString(18), 12884901888L);
      this._loadButton.setChangeListener(this);
      this._dfm.addCustomField(this._loadButton);
      this._postponeButton = new ButtonField(BrowserPushResources.getString(16), 12884901888L);
      this._postponeButton.setChangeListener(this);
      if (!yesNo) {
         this._dfm.addCustomField(this._postponeButton);
      }

      this._dismissButton = new ButtonField(yesNo ? yesNoArray[1] : BrowserPushResources.getString(17), 12884901888L);
      this._dismissButton.setChangeListener(this);
      this._dfm.addCustomField(this._dismissButton);
   }

   @Override
   protected final void close(int closeReason) {
      NotificationsManager.cancelImmediateEvent(4665536253483290822L, 0, null, null);
      super.close(closeReason);
   }

   public PushDisplayDialog(String message, String url) {
      this(message, url, false);
   }

   @Override
   protected final boolean keyChar(char key, int status, int time) {
      boolean handled = super.keyChar(key, status, time);
      if (!handled) {
         if (key == '\n') {
            this.close(0);
            return true;
         }

         if (key == 27 && this.isCancelAllowed()) {
            this.close(-1);
         }
      }

      return handled;
   }

   public PushDisplayDialog(String message) {
      this(message, null, true);
   }
}
