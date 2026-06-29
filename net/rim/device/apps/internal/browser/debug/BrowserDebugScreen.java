package net.rim.device.apps.internal.browser.debug;

import net.rim.device.api.system.Application;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.api.ui.component.ListFieldCallback;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.apps.internal.browser.common.BrowserLockScreen;
import net.rim.device.apps.internal.browser.core.BrowserDaemonRegistry;
import net.rim.device.apps.internal.browser.resources.BrowserResources;

public final class BrowserDebugScreen extends MainScreen implements ListFieldCallback, BrowserLockScreen {
   private DebugListItem[] _options = new DebugListItem[]{new ContextState(), new RawDataCacheState(), new CookieCacheState(), new PageInfo()};
   private ListField _fields;

   public BrowserDebugScreen() {
      this.setTitle(new LabelField(BrowserResources.getString(370)));
      this._fields = new ListField(this._options.length);
      this._fields.setCallback(this);
      this.add(this._fields);
   }

   @Override
   public final void drawListRow(ListField listField, Graphics graphics, int index, int y, int width) {
      graphics.drawText(this.get(index).getLabel(), 0, y);
   }

   @Override
   public final int getPreferredWidth(ListField listField) {
      return listField.getPreferredWidth();
   }

   @Override
   public final int indexOfList(ListField listField, String prefix, int start) {
      return 0;
   }

   @Override
   protected final boolean keyChar(char key, int status, int time) {
      if (key == 27) {
         synchronized (Application.getEventLock()) {
            UiApplication.getUiApplication().popScreen(this);
            this.cleanupScreen();
            return true;
         }
      } else if (key == '\n') {
         this.handleSelection();
         return true;
      } else {
         return super.keyChar(key, status, time);
      }
   }

   @Override
   public final void cleanupScreen() {
      BrowserDaemonRegistry.getInstance().releaseBrowserLock();
   }

   @Override
   protected final boolean trackwheelClick(int status, int time) {
      return this.invokeAction(1);
   }

   @Override
   protected final boolean invokeAction(int action) {
      switch (action) {
         case 1:
            this.handleSelection();
            return true;
         default:
            return false;
      }
   }

   private final void handleSelection() {
      UiApplication app = UiApplication.getUiApplication();
      Screen s = app.getActiveScreen();
      Field fieldWithFocus = s.getLeafFieldWithFocus();
      if (fieldWithFocus instanceof ListField) {
         int index = ((ListField)fieldWithFocus).getSelectedIndex();
         Screen screen = this.get(index).getScreen();
         synchronized (Application.getEventLock()) {
            app.pushScreen(screen);
         }
      }
   }

   @Override
   public final Object get(ListField listField, int index) {
      return this.get(index);
   }

   private final DebugListItem get(int index) {
      return this._options[index];
   }
}
