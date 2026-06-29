package net.rim.device.api.system;

public interface PersistentContentListener {
   int PERSISTENT_CONTENT_UNLOCKED;
   int PERSISTENT_CONTENT_LOCKING;
   int PERSISTENT_CONTENT_LOCKED_SECURE;
   int PERSISTENT_CONTENT_LOCKED_INSECURE;

   void persistentContentStateChanged(int var1);

   void persistentContentModeChanged(int var1);
}
