package net.rim.device.api.system;

public interface PersistentContentListener {
   int PERSISTENT_CONTENT_UNLOCKED = 1;
   int PERSISTENT_CONTENT_LOCKING = 2;
   int PERSISTENT_CONTENT_LOCKED_SECURE = 3;
   int PERSISTENT_CONTENT_LOCKED_INSECURE = 4;

   void persistentContentStateChanged(int var1);

   void persistentContentModeChanged(int var1);
}
