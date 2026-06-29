package net.rim.device.apps.api.quickcontact;

public interface QuickContactList$Listener {
   int ADD;
   int REMOVE;
   int UPDATE;

   void onQuickContactListEvent(int var1, int var2, QuickContactItem var3);
}
