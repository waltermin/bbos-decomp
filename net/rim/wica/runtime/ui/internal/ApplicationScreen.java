package net.rim.wica.runtime.ui.internal;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.util.CharacterUtilities;
import net.rim.wica.runtime.ui.internal.component.ScreenContext;

final class ApplicationScreen extends Screen implements WicaScreen {
   private ScreenContext _context;
   private ApplicationScreen$ScreenManager _manager = (ApplicationScreen$ScreenManager)this.getDelegate();

   ApplicationScreen() {
      super(new ApplicationScreen$ScreenManager(), 65536);
   }

   final ScreenContext getContext() {
      return this._context;
   }

   final void setContext(ScreenContext context, boolean restoreFocus) {
      if (this._context != null) {
         this._context.close();
         this._context.setSavedFocus(this._manager._focusManager.getLeafFieldWithFocus());
      }

      String title = context.getTitle();
      this.setTitle(title);
      this._manager._focusManager.setContent(context.getLayout());
      Field savedFocus = context.getSavedFocus();
      if (restoreFocus && savedFocus != null && savedFocus.isVisible()) {
         this._manager._focusManager.restoreFocus(context.getSavedFocus());
      } else if (this._context != null) {
         this._manager._focusManager.onFocus(1);
      }

      this._context = context;
   }

   @Override
   public final int getMaxScreenContentHeight() {
      return FocusManager.DEVICE_SCREEN_HEIGHT - this._manager._titleField.getHeight();
   }

   @Override
   public final Manager getScrollingManager() {
      return this._manager._focusManager.getScrollingManager();
   }

   @Override
   public final boolean isFieldVisible(Field field) {
      return this._manager._focusManager.isFieldVisible(field);
   }

   @Override
   public final void populateMenu(Menu menu, int instance) {
      this.makeMenuWithContext(menu, instance);
   }

   @Override
   public final void setTitle(String title) {
      this._manager._titleField.setTitle(title);
   }

   @Override
   public final boolean keyChar(char key, int status, int time) {
      if (super.keyChar(key, status, time)) {
         return true;
      }

      if (key == ' ') {
         if ((status & 2) == 0) {
            this.scrollPage(512);
            return true;
         } else {
            this.scrollPage(256);
            return true;
         }
      } else {
         if ((status & 1) == 0) {
            switch (Hotkeys.map(CharacterUtilities.toUpperCase(key, 1701707776))) {
               case 153:
                  break;
               case 154:
                  this.showInFullScreenChange();
                  return true;
               case 155:
                  this.scrollPage(2);
                  return true;
               case 156:
               default:
                  this.scrollPage(1);
                  return true;
            }
         }

         return false;
      }
   }

   @Override
   public final boolean onMenu(int instance) {
      this._context.requestMenuShow(instance);
      return true;
   }

   @Override
   protected final void sublayout(int width, int height) {
      this.setPosition(0, 0);
      this.setExtent(width, height);
      this.setPositionDelegate(0, 0);
      this.layoutDelegate(width, height);
   }

   private final void showInFullScreenChange() {
      this._manager._showInFullScreen = !this._manager._showInFullScreen;
      this.invalidateLayout0();
      this.doLayout();
      this.invalidate();
   }

   private final void scrollPage(int where) {
      this._manager._focusManager.scrollPage(where);
   }
}
