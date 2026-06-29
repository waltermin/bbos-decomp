package net.rim.device.api.system;

class EventLogger$MyPersistentContentListener implements PersistentContentListener {
   private boolean _isEncryptionEnabled = false;

   private EventLogger$MyPersistentContentListener() {
   }

   boolean isEncryptionEnabled() {
      return this._isEncryptionEnabled;
   }

   @Override
   public void persistentContentModeChanged(int generation) {
      this._isEncryptionEnabled = PersistentContent.isEncryptionEnabled();
   }

   @Override
   public void persistentContentStateChanged(int state) {
   }

   EventLogger$MyPersistentContentListener(EventLogger$1 x0) {
      this();
   }
}
