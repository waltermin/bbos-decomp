package net.rim.wica.runtime.ui.internal;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.container.PopupScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.util.CharacterUtilities;
import net.rim.wica.runtime.ui.internal.component.ScreenContext;

final class ApplicationDialog extends PopupScreen implements WicaScreen {
   private ScreenContext _context;
   private FocusManager _manager = new FocusManager();
   private DialogTitleField _titleField;

   ApplicationDialog() {
      super(new VerticalFieldManager(2251799813685248L));
      this.add(this._manager);
   }

   final void setContext(ScreenContext context) {
      this.setTitle(context.getTitle());
      this._manager.setContent(context.getLayout());
      if (this._context != null) {
         this._manager.onFocus(1);
      }

      this._context = context;
   }

   public final Field getHeader() {
      return this._titleField;
   }

   @Override
   public final int getMaxScreenContentHeight() {
      int headerHeight = 0;
      Field header = this.getHeader();
      if (header != null) {
         headerHeight = header.getHeight();
      }

      return FocusManager.DEVICE_SCREEN_HEIGHT - this.getMarginTop() - this.getMarginBottom() - headerHeight;
   }

   @Override
   public final boolean isFieldVisible(Field field) {
      return this._manager.isFieldVisible(field);
   }

   @Override
   public final Manager getScrollingManager() {
      return this._manager.getScrollingManager();
   }

   @Override
   public final void populateMenu(Menu menu, int instance) {
      this.makeMenuWithContext(menu, instance);
   }

   @Override
   public final void setTitle(String title) {
      if (title != null && title.length() != 0) {
         if (this._titleField == null) {
            this._titleField = new DialogTitleField();
            this._titleField.setTitle(title);
            this.insert(this._titleField, 0);
         } else {
            this._titleField.setTitle(title);
         }
      } else {
         if (this._titleField != null) {
            this.delete(this._titleField);
            this._titleField = null;
         }
      }
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
               case 154:
                  break;
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

   private final void scrollPage(int where) {
      this._manager.scrollPage(where);
   }
}
