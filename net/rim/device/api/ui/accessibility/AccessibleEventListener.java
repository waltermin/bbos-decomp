package net.rim.device.api.ui.accessibility;

public interface AccessibleEventListener {
   long GUID;

   void accessibleEventOccurred(int var1, Object var2, Object var3, AccessibleContext var4);
}
