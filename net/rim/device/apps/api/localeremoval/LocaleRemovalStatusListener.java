package net.rim.device.apps.api.localeremoval;

public interface LocaleRemovalStatusListener {
   byte STATE_SEARCHING_FOR_MODULES = 0;
   byte STATE_DELETING_MODULES = 1;
   byte STATE_REMOVAL_COMPLETE = 2;

   void localeRemovalStateChanged(byte var1);

   void localeRemovalProgressUpdated(int var1, int var2);
}
