package net.rim.wica.runtime.metadata.component.ui;

public interface MenuItemModel extends Command {
   String getLabel();

   MenuModel getMenu();

   boolean isVisible();

   void setLabel(String var1);

   void setVisible(boolean var1);
}
