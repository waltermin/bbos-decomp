package net.rim.device.api.ui.menu;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.accessibility.AccessibleContext;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.container.PopupScreen;
import net.rim.device.api.ui.theme.Tag;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.internal.ui.UiInternal;
import net.rim.device.internal.ui.VerticalFieldManager3;
import net.rim.tid.im.layout.SLKeyLayout;

public class DefaultMenuScreen extends PopupScreen implements MenuScreen {
   private Menu _menu;
   private MenuList _list;
   private long _hAlign = 8589934592L;
   private long _vAlign = 17179869184L;
   private int _xOrigin = -1;
   private int _yOrigin = -1;
   private static Tag TAG = Tag.create("menu");

   public DefaultMenuScreen() {
      this(new VerticalFieldManager3(299067162755072L));
   }

   public DefaultMenuScreen(Manager manager) {
      super(manager);
      this.setTag(TAG);
   }

   @Override
   protected void applyTheme() {
      super.applyTheme();
      this.setMargin(0, 0, 0, this.getMarginLeft());
   }

   @Override
   public MenuItem getCurrentItem() {
      return this._list.getCurrentItem();
   }

   @Override
   public Menu getMenu() {
      return this._menu;
   }

   @Override
   protected boolean invokeAction(int action) {
      switch (action) {
         case 1:
            this.notifyMenuSelected();
            return true;
         default:
            return false;
      }
   }

   @Override
   protected boolean navigationMovement(int dx, int dy, int status, int time) {
      if ((status & 536936448) != 0) {
         if (dx > 0 && this._list.getCurrentItem() instanceof CascadingMenuItem) {
            this.notifyMenuSelected();
            return true;
         }

         if (dx < 0 && (this._menu.getStyle() & 262144) != 0) {
            this.popMenu();
            return true;
         }
      }

      return super.navigationMovement(dx, dy, status, time);
   }

   @Override
   protected boolean keyChar(char key, int status, int time) {
      boolean result = false;
      switch (key) {
         case '\n':
            this.notifyMenuSelected();
            result = true;
            break;
         case '\u001b':
            this.popMenu();
            result = true;
            break;
         default:
            char keyToCheck = UiInternal.map(Keypad.getLayout().getOriginalKeyCode(key, SLKeyLayout.convertStatusToModifiers(status)), status);
            keyToCheck = Character.toLowerCase(keyToCheck);
            result = super.keyChar(keyToCheck, status, time);
      }

      return result;
   }

   @Override
   protected boolean keyControl(char key, int status, int time) {
      boolean result = super.keyControl(key, status, time);
      if (result) {
         return true;
      }

      switch (key) {
         case '\u0095':
            this.popMenu();
            if (this._menu.getInstance() == 65536) {
               MenuItem.getPrefab(18).run();
            }

            result = true;
         default:
            return result;
      }
   }

   private void notifyMenuSelected() {
      MenuItem item = this._list.getCurrentItem();
      if (item instanceof CascadingMenuItem) {
         XYRect rect = Ui.getTmpXYRect();
         Field f = this.getFieldWithFocus();
         f.getFocusRect(rect);
         f.transformToScreen(rect);
         int x = rect.x + 8;
         int y = rect.y + (rect.height >> 1);
         Ui.returnTmpXYRect(rect);
         ((CascadingMenuItem)item).invokeSubMenu(this._menu, x, y);
      } else {
         this._menu.notifySelected(item);
         this.popMenu();
      }
   }

   @Override
   protected void onUndisplay() {
      super.onUndisplay();
      this.onUnfocus();
   }

   @Override
   protected boolean openDevelopmentBackdoor(int secretCode) {
      switch (secretCode) {
         case 1229870670:
            Ui.setNewInvalidate(true);
            break;
         case 1229870671:
            Ui.setNewInvalidate(false);
            break;
         case 1263288909:
            Ui.setTrackballClickAction(0);
            break;
         case 1263288911:
            Ui.setTrackballClickAction(1);
            break;
         case 1263294285:
            Ui.setTrackwheelClickAction(0);
            break;
         case 1263294287:
            Ui.setTrackwheelClickAction(1);
      }

      return super.openDevelopmentBackdoor(secretCode);
   }

   @Override
   protected boolean openProductionBackdoor(int secretCode) {
      switch (secretCode) {
         case 1096237401:
            Ui.setMode(2);
            break;
         case 1212502597:
            Ui.setMode(1);
            break;
         case 1414024516:
            ThemeManager.setActiveTheme(ThemeManager.getNameOfDefaulTheme());
            break;
         case 1414024526:
            ThemeManager.setActiveTheme("");
            break;
         case 1431323726:
            Ui.setIncreaseDirection(1);
            break;
         case 1431328080:
            Ui.setIncreaseDirection(-1);
      }

      return super.openProductionBackdoor(secretCode);
   }

   private void popMenu() {
      Ui.getUiEngine().popScreen(this);
   }

   @Override
   public void setList(MenuList list) {
      Field field = (Field)list;
      this._list = list;
      this.deleteAll();
      this.add(field);
   }

   @Override
   public void setMenu(Menu menu) {
      this._menu = menu;
   }

   @Override
   public void close() {
      if (this.isDisplayed()) {
         super.close();
      }
   }

   @Override
   public void setAlignment(long hAlign, long vAlign) {
      this._hAlign = hAlign;
      this._vAlign = vAlign;
   }

   @Override
   public void setCurrentItem(MenuItem item) {
      this._list.setCurrentItem(item);
   }

   @Override
   public void setOrigin(int x, int y) {
      this._xOrigin = x;
      this._yOrigin = y;
   }

   @Override
   protected boolean stylusTap(int x, int y, int status, int time) {
      XYRect extent = this.getExtent();
      if (extent.contains(extent.x, extent.y, x, y)) {
         return this.invokeAction(1);
      }

      this.popMenu();
      return true;
   }

   @Override
   protected boolean stylusTapHold(int x, int y, int status, int time) {
      return true;
   }

   @Override
   protected void sublayout(int width, int height) {
      int borderBottom = this.getBorderBottom();
      height += this.getBorderTop() + borderBottom;
      int stylusX = this._xOrigin != -1 ? this._xOrigin : Ui.getUiEngine().getStylusX();
      if (stylusX != -1) {
         super.sublayout(width, height);
         int menuWidth = this.getWidth();
         int menuHeight = this.getHeight();
         int stylusY = this._yOrigin != -1 ? this._yOrigin : Ui.getUiEngine().getStylusY();
         int rightRoom = width - (stylusX + menuWidth);
         int posX;
         if (rightRoom > 0) {
            posX = stylusX;
         } else {
            int leftRoom = stylusX - menuWidth;
            if (leftRoom > 0) {
               posX = leftRoom;
            } else if (leftRoom > rightRoom) {
               posX = 0;
            } else {
               posX = width - menuWidth;
            }
         }

         int bottomRoom = height - (stylusY + menuHeight);
         int posY;
         if (bottomRoom > 0) {
            posY = stylusY;
         } else {
            int topRoom = stylusY - menuHeight;
            if (topRoom > 0) {
               posY = topRoom;
            } else if (topRoom > bottomRoom) {
               posY = 0;
            } else {
               posY = height - menuHeight;
            }
         }

         this.setPosition(posX, posY);
      } else {
         this.getMenu();
         Screen target = Menu.getTargetScreen();
         int x = width + this.getBorderLeft() + this.getPaddingLeft() + this.getBorderRight() + this.getPaddingRight();
         int y = 0;
         if (target != null) {
            x = target.getLeft() + target.getWidth();
            y = target.getTop();
         }

         super.sublayout(width, height - this.getBorderTop() - borderBottom - y);
         int xOffset = x - this.getWidth() + this.getBorderRight();
         if (this._hAlign == 4294967296L) {
            xOffset = (target != null ? target.getLeft() : 0) - this.getBorderLeft();
         } else if (this._hAlign == 12884901888L) {
            xOffset = x - this.getWidth() >> 1;
         }

         int yOffset = y - this.getBorderTop();
         if (this._vAlign == 34359738368L) {
            yOffset = y
               + Math.max(0, (target != null ? target.getHeight() + borderBottom : height + borderBottom + this.getPaddingBottom()) - this.getHeight());
         } else if (this._vAlign == 51539607552L) {
            yOffset = y + (target != null ? target.getHeight() + borderBottom : height) - this.getHeight() >> 1;
         }

         this.setPosition(xOffset, yOffset);
      }
   }

   @Override
   protected boolean trackwheelClick(int status, int time) {
      return this.invokeAction(1);
   }

   @Override
   public int getAccessibleRole() {
      return 4;
   }

   @Override
   public int getAccessibleChildCount() {
      return this.getMenu().getSize();
   }

   @Override
   public AccessibleContext getAccessibleChildAt(int index) {
      return this.getMenu().getItem(index);
   }

   @Override
   public int getAccessibleSelectionCount() {
      return 1;
   }

   @Override
   public AccessibleContext getAccessibleSelectionAt(int index) {
      return this.getMenu().getCurrentMenuItem();
   }

   @Override
   public boolean isAccessibleChildSelected(int index) {
      return this.getMenu().getItem(index).equals(this.getMenu().getCurrentMenuItem());
   }
}
