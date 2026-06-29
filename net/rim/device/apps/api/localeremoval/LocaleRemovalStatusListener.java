package net.rim.device.apps.api.localeremoval;

public interface LocaleRemovalStatusListener {
   byte STATE_SEARCHING_FOR_MODULES;
   byte STATE_DELETING_MODULES;
   byte STATE_REMOVAL_COMPLETE;

   void localeRemovalStateChanged(byte var1);

   void localeRemovalProgressUpdated(int var1, int var2);
}
