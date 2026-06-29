package net.rim.wica.runtime.metadata.internal.component.ui;

import net.rim.wica.runtime.metadata.component.ui.MenuItemModel;
import net.rim.wica.runtime.metadata.component.ui.MenuModel;

public class MenuItemModelImpl implements MenuItemModel {
   private MenuModelImpl _menu;
   private String _label;
   private int _clickId;
   private boolean _visibility;

   void click() {
      ((ScreenModelImpl)this._menu.getScreen()).handleEvent(2, this._clickId);
   }

   @Override
   public void onClick() {
      if (this._clickId != -1) {
         this._menu.getScreen().getWiclet().getRuntime().enqueueRunnable(new MenuItemModelImpl$MenuAction(this));
      }
   }

   @Override
   public MenuModel getMenu() {
      return this._menu;
   }

   @Override
   public boolean isVisible() {
      return this._visibility;
   }

   @Override
   public void setVisible(boolean visibility) {
      this._visibility = visibility;
   }

   @Override
   public String getLabel() {
      return this._label;
   }

   @Override
   public void setLabel(String label) {
      this._label = label;
   }

   protected MenuItemModelImpl(MenuModelImpl menu, String label, boolean visibility, int clickId) {
      this._menu = menu;
      this._label = label;
      this._visibility = visibility;
      this._clickId = clickId;
   }
}
