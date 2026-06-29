package net.rim.device.internal.system;

public interface SecurityManager {
   long GUID_SYSTEM_LOCKED;
   long GUID_REQUEST_UNLOCK;
   long GUID_SYSTEM_UNLOCKED;

   boolean isLockRequired();

   int lockSystem(boolean var1);
}
