package net.rim.device.apps.internal.browser.debug;

import net.rim.device.api.system.Application;
import net.rim.device.api.system.KeyListener;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.api.ui.component.ListFieldCallback;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.apps.internal.browser.resources.BrowserResources;
import net.rim.device.internal.system.InternalServices;

class CacheState implements DebugListItem, ListFieldCallback, KeyListener {
   protected ListField _fields;
   protected DebugListItem[] _options = null;
   private int _titleId;

   void refreshOptions() {
      throw null;
   }

   void handleSave(Object _1) {
      throw null;
   }

   void handleExpire(Object _1) {
      throw null;
   }

   void handleDelete(Object _1) {
      throw null;
   }

   @Override
   public int indexOfList(ListField listField, String prefix, int start) {
      return listField.indexOfList(prefix, start);
   }

   @Override
   public Object get(ListField listField, int index) {
      return this.get(index);
   }

   @Override
   public boolean keyDown(int keycode, int time) {
      int key = Keypad.key(keycode);
      if (key == 127 || key == 8) {
         this.handleDelete();
         return true;
      }

      if (key == 69) {
         this.handleExpire();
         return true;
      }

      if ((key != 65 || !InternalServices.isReducedFormFactor()) && key != 83) {
         return false;
      }

      this.handleSave();
      return true;
   }

   @Override
   public boolean keyChar(char key, int status, int time) {
      if (key == '\n') {
         this.handleSelection();
         return true;
      } else {
         return false;
      }
   }

   @Override
   public boolean keyRepeat(int arg0, int arg1) {
      return false;
   }

   @Override
   public boolean keyStatus(int arg0, int arg1) {
      return false;
   }

   @Override
   public boolean keyUp(int arg0, int arg1) {
      return false;
   }

   @Override
   public int getPreferredWidth(ListField listField) {
      return listField.getPreferredWidth();
   }

   @Override
   public void drawListRow(ListField listField, Graphics graphics, int index, int y, int width) {
      graphics.drawText(this.get(index).getLabel(), 0, y, 70, listField.getManager().getWidth());
   }

   @Override
   public Screen getScreen() {
      MainScreen screen = new CacheState$MyMainScreen(this);
      screen.setTitle((Field)(new Object(BrowserResources.getString(this._titleId))));
      screen.addKeyListener(this);
      this._fields = (ListField)(new Object(0));
      this.refreshOptions();
      this._fields.setCallback(this);
      screen.add(this._fields);
      return screen;
   }

   @Override
   public String getLabel() {
      return BrowserResources.getString(this._titleId);
   }

   private void handleSave() {
      Screen s = UiApplication.getUiApplication().getActiveScreen();
      Field fieldWithFocus = s.getLeafFieldWithFocus();
      if (fieldWithFocus instanceof Object) {
         int index = ((ListField)fieldWithFocus).getSelectedIndex();
         DebugListItem property = this.get(index);
         if (property != null) {
            this.handleSave(property);
         }
      }
   }

   private void handleSelection() {
      Screen s = UiApplication.getUiApplication().getActiveScreen();
      Field fieldWithFocus = s.getLeafFieldWithFocus();
      if (fieldWithFocus instanceof Object) {
         int index = ((ListField)fieldWithFocus).getSelectedIndex();
         DebugListItem property = this.get(index);
         if (property != null) {
            Screen screen = property.getScreen();
            synchronized (Application.getEventLock()) {
               UiApplication.getUiApplication().pushScreen(screen);
               return;
            }
         }
      }
   }

   private DebugListItem get(int index) {
      return this._options[index];
   }

   private void handleDelete() {
      Screen s = UiApplication.getUiApplication().getActiveScreen();
      Field fieldWithFocus = s.getLeafFieldWithFocus();
      if (fieldWithFocus instanceof Object) {
         int index = ((ListField)fieldWithFocus).getSelectedIndex();
         DebugListItem property = this.get(index);
         if (property != null) {
            this.handleDelete(property);
         }
      }
   }

   private void handleExpire() {
      Screen s = UiApplication.getUiApplication().getActiveScreen();
      Field fieldWithFocus = s.getLeafFieldWithFocus();
      if (fieldWithFocus instanceof Object) {
         int index = ((ListField)fieldWithFocus).getSelectedIndex();
         DebugListItem property = this.get(index);
         if (property != null) {
            this.handleExpire(property);
         }
      }
   }

   CacheState(int titleId) {
      this._titleId = titleId;
   }
}
