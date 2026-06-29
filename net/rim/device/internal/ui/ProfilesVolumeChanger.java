package net.rim.device.internal.ui;

public interface ProfilesVolumeChanger {
   long PROFILES_VOLUME_CHANGER_ID;

   int adjustCurrentProfileVolume(int var1);

   String getCurrentProfileName();
}
