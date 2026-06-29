package net.rim.device.apps.internal.calendar.viewer;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.RIMGlobalMessagePoster;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.component.RadioButtonField;
import net.rim.device.api.ui.component.RadioButtonGroup;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.container.PopupScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.ui.theme.Tag;
import net.rim.device.apps.api.calendar.caldb.CalendarKey;
import net.rim.device.apps.api.calendar.caldb.CalendarOptions;
import net.rim.device.apps.api.calendar.caldb.CalendarService;
import net.rim.device.apps.api.calendar.caldb.CalendarServiceManager;

final class ViewCalendarPopupScreen extends PopupScreen {
   private RadioButtonGroup _radioGroup = new RadioButtonGroup();
   private ViewCalendarPopupScreen$CalendarFolderController[] _folders;
   private boolean _isAllSelected = false;
   private RadioButtonField _viewAllField;
   private ResourceBundle _rb = ResourceBundle.getBundle(912302513268743237L, "net.rim.device.apps.internal.resource.Calendar");
   private static final Tag TAG = Tag.create("view-calendars-area");
   private static final Tag FOLDER_TEXT_TAG = Tag.create("view-calendars-folder-text");

   public ViewCalendarPopupScreen(CalendarKey[] folderKeys) {
      super(new VerticalFieldManager(299067162755072L), 0);
      this.getDelegate().setTag(TAG);
      this.add(this._viewAllField = new RadioButtonField(this._rb.getString(654), this._radioGroup, true));
      this.add(new SeparatorField());
      int numFolders = folderKeys.length;
      this._folders = new ViewCalendarPopupScreen$CalendarFolderController[numFolders];

      for (int i = 0; i < numFolders; i++) {
         this._folders[i] = new ViewCalendarPopupScreen$CalendarFolderController(folderKeys[i]);
         if (this._folders[i].isValid()) {
            this.add(this._folders[i].getField(this._radioGroup));
            if (this._folders[i].isVisible() && !this._isAllSelected) {
               if (this._radioGroup.getSelectedIndex() <= 0) {
                  this._radioGroup.setSelectedIndex(this._radioGroup.getSize() - 1);
               } else {
                  this._viewAllField.setSelected(true);
                  this._isAllSelected = true;
               }
            }
         }
      }
   }

   protected final void activateService(ViewCalendarPopupScreen$CalendarFolderController folder) {
      for (int i = 0; i < this._folders.length; i++) {
         if (this._folders[i].isValid()) {
            if (folder != null && folder != this._folders[i]) {
               this._folders[i].setVisible(false);
            } else {
               this._folders[i].setVisible(true);
            }
         }
      }

      CalendarService baseCalendarService = CalendarServiceManager.getInstance().getBaseSystemCalendarService();
      if (folder != null && baseCalendarService.getCalendarDatabase().size() == 0) {
         CalendarKey folderCalendarKey = folder.getCalendarKey();
         if (baseCalendarService.getUniqueServiceID() != folderCalendarKey.getCalendarServiceID()) {
            CalendarKey[] calendarKeys = baseCalendarService.getCalendarKeys();
            if (calendarKeys != null && calendarKeys.length > 0) {
               CalendarOptions.getOptions().setShowAppointments(calendarKeys[0], false);
            }
         }
      }

      RIMGlobalMessagePoster.postGlobalEvent(5483692278053761660L, 0, 0, null, null);
   }

   protected final void activateSelection() {
      Field focusField = this.getLeafFieldWithFocus();
      if (focusField instanceof RadioButtonField) {
         ((RadioButtonField)focusField).setSelected(true);
         this.activateService((ViewCalendarPopupScreen$CalendarFolderController)focusField.getCookie());
         this.delayedClose();
      }
   }

   protected final void delayedClose() {
      Application.getApplication().invokeLater(new ViewCalendarPopupScreen$1(this));
   }

   @Override
   protected final boolean trackwheelClick(int status, int time) {
      this.activateSelection();
      return true;
   }

   @Override
   protected final boolean navigationClick(int status, int time) {
      return this.trackwheelClick(status, time);
   }

   @Override
   protected final boolean keyDown(int keycode, int time) {
      int key = Keypad.key(keycode);
      boolean handled = true;
      if (key != 27 && key != 4098 && key != 17 && key != 18 && key != 19 && key != 21) {
         return super.keyDown(keycode, time);
      }

      if (key == 17) {
         handled = false;
      }

      this.close();
      return handled;
   }

   @Override
   protected final boolean keyChar(char keyChar, int statusInt, int timeInt) {
      switch (keyChar) {
         case '\n':
         case ' ':
            this.activateSelection();
         default:
            return super.keyChar(keyChar, statusInt, timeInt);
      }
   }

   @Override
   public final boolean dispatchKeyEvent(int event, char key, int keycode, int time) {
      int keypress = Keypad.key(keycode);
      if (keypress == 18 && event == 513) {
         this.close();
         return false;
      } else {
         return super.dispatchKeyEvent(event, key, keycode, time);
      }
   }
}
