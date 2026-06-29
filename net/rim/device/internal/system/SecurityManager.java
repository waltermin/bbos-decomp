package net.rim.device.internal.system;

public interface SecurityManager {
   long GUID_SYSTEM_LOCKED = -7131874474196788121L;
   long GUID_REQUEST_UNLOCK = 1597563888101360867L;
   long GUID_SYSTEM_UNLOCKED = 6345609069135580235L;

   boolean isLockRequired();

   int lockSystem(boolean var1);
}
