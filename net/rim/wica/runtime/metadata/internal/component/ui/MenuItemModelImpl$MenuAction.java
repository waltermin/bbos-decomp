package net.rim.wica.runtime.metadata.internal.component.ui;

final class MenuItemModelImpl$MenuAction implements Runnable {
   private MenuItemModelImpl _menuItemModel;

   MenuItemModelImpl$MenuAction(MenuItemModelImpl menuItemModel) {
      this._menuItemModel = menuItemModel;
   }

   @Override
   public final void run() {
      this._menuItemModel.click();
   }
}
