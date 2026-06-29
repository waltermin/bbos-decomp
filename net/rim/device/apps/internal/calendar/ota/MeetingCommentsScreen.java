package net.rim.device.apps.internal.calendar.ota;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.ui.AppsMainScreen;
import net.rim.device.apps.api.ui.SystemEnabledMenu;

class MeetingCommentsScreen extends AppsMainScreen {
   private EditField _commentField = (EditField)(new Object());
   private boolean _sendSelected;
   private static final int MENU_ID_SEND;
   private static final int MENU_ID_CANCEL;

   private MeetingCommentsScreen(String title) {
      super(17592186044416L);
      this.setDefaultClose(false);
      this.setTitle(title);
      this.add(this._commentField);
   }

   static String showScreen(String title) {
      MeetingCommentsScreen screen = new MeetingCommentsScreen(title);
      UiApplication.getUiApplication().pushModalScreen(screen);
      return screen._sendSelected ? screen._commentField.getText() : null;
   }

   private boolean confirmCancel() {
      ResourceBundle resources = ResourceBundle.getBundle(8008824311162635875L, "net.rim.device.apps.internal.resource.CalendarOTA");
      int retVal = Dialog.ask(4, resources.getString(612), -1);
      return retVal != -1;
   }

   @Override
   protected void makeMenu(SystemEnabledMenu menu, int instance) {
      Verb defaultVerb = new MeetingCommentsScreen$SendWithCommentsVerb(this);
      menu.add(defaultVerb);
      menu.add(new MeetingCommentsScreen$CancelCommentsVerb(this));
      menu.setDefault(defaultVerb);
      this._sendSelected = false;
   }

   @Override
   protected boolean keyChar(char key, int status, int time) {
      if (key != 27) {
         return super.keyChar(key, status, time);
      }

      if (!this._commentField.isDirty() || this.confirmCancel()) {
         UiApplication.getUiApplication().popScreen(UiApplication.getUiApplication().getActiveScreen());
      }

      return true;
   }
}
