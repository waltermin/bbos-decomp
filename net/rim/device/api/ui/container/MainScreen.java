package net.rim.device.api.ui.container;

import java.util.Vector;
import net.rim.device.api.i18n.ResourceBundleFamily;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.Menu;

public class MainScreen extends FullScreen {
   private TitleStatusManager _manager = (TitleStatusManager)this.getDelegate();
   private Vector _menu;
   public static final long NO_TITLE_SEPARATOR = 2147483648L;
   public static final long NO_STATUS_SEPARATOR = 1073741824L;

   public MainScreen() {
      this(0);
   }

   public MainScreen(long style) {
      super(new TitleStatusManager(style), validateStyle(style));
   }

   public void addMenuItem(MenuItem item) {
      if (this._menu == null) {
         this._menu = new Vector();
      }

      this._menu.addElement(item);
   }

   @Override
   public String getAccessibleName() {
      return this._manager.getAccessibleName();
   }

   @Override
   public int getFieldCount() {
      return this.getMainManager().getFieldCount();
   }

   @Override
   public Field getFieldWithFocus() {
      Manager manager = (Manager)this._manager.getFieldWithFocus();
      return manager != null ? manager.getFieldWithFocus() : null;
   }

   @Override
   public int getFieldWithFocusIndex() {
      return this.getMainManager().getFieldWithFocusIndex();
   }

   @Override
   public Field getField(int index) {
      return this.getMainManager().getField(index);
   }

   public Manager getMainManager() {
      return this._manager.getMainManager();
   }

   @Override
   protected boolean keyCharUnhandled(char key, int status, int time) {
      if (super.keyCharUnhandled(key, status, time)) {
         return true;
      } else if (key == '\n' && !this.isSelecting()) {
         this.trackwheelRoll(1, 0, time);
         return true;
      } else {
         return false;
      }
   }

   @Override
   protected void makeMenu(Menu menu, int instance) {
      super.makeMenu(menu, instance);
      if (this._menu != null) {
         int end = this._menu.size();

         for (int lv = 0; lv < end; lv++) {
            menu.add((MenuItem)this._menu.elementAt(lv));
         }
      }
   }

   @Override
   protected boolean onSavePrompt() {
      boolean result = true;
      int answer = Dialog.ask(1);
      if (answer == 1) {
         return this.onSave();
      }

      if (answer == -1) {
         result = false;
      }

      return result;
   }

   public void removeAllMenuItems() {
      if (this._menu != null) {
         this._menu.removeAllElements();
      }
   }

   public void removeMenuItem(MenuItem item) {
      if (this._menu != null) {
         this._menu.removeElement(item);
      }
   }

   public void setBanner(Field banner) {
      this._manager.setBanner(banner);
   }

   public void setStatus(Field status) {
      this._manager.setStatus(status);
   }

   public void setTitle(Field title) {
      this._manager.setTitle(title);
   }

   public void setTitle(String title) {
      this._manager.setTitle(title);
   }

   public void setTitle(ResourceBundleFamily family, int id) {
      this._manager.setTitle(family, id);
   }

   private static long validateStyle(long style) {
      style &= -4486007441326081L;
      style |= 196608;
      return style & -65536;
   }

   @Override
   public void setId(String idName) {
      super.setId(idName);
      this._manager.setId(idName);
   }
}
