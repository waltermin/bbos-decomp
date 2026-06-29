package net.rim.plazmic.internal.mediaengine.service;

public interface FocusInteractor {
   String ID;
   int NAVIGATE_FORWARD;
   int NAVIGATE_BACKWARDS;

   boolean hasFocus();

   int moveFocus(int var1, int var2, boolean var3);

   boolean activateItemInFocus();

   boolean keyChar(int var1, int var2);

   void setFocusToItem(int var1);

   void setDefaultItem(int var1);

   int getItemInFocus();

   int getItemCount();

   int getFirstFocusableItem(int var1);

   Object getWrappedObject(int var1);

   int getNextFocusableItem(int var1, int var2, boolean var3);

   boolean getWrap();

   void setWrap(boolean var1);
}
