package net.rim.device.apps.api.quickcontact;

public interface QuickContactList$Listener {
   int ADD = 0;
   int REMOVE = 1;
   int UPDATE = 2;

   void onQuickContactListEvent(int var1, int var2, QuickContactItem var3);
}
