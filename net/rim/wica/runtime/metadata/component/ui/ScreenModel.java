package net.rim.wica.runtime.metadata.component.ui;

import net.rim.wica.runtime.metadata.Wiclet;

public interface ScreenModel extends UIContainer {
   UIComponent getComponent(String var1);

   void updateUI();

   void updateData();

   void invalidateUI(boolean var1);

   boolean isDialog();

   boolean isDisplayed();

   MenuModel getMenuOnShow();

   MenuModel getMenu();

   String getTitle();

   Wiclet getWiclet();

   int getVarIndex(String var1);

   int getVarType(int var1);

   long getVarValue(int var1);

   void setVarValue(int var1, long var2);

   long getVarValue(int var1, boolean var2);

   long getVarValueByName(String var1);

   UIComponent getCurrentFocus();

   Command getDefaultAction();

   void setDefaultAction(Command var1);

   boolean performDefaultAction();
}
