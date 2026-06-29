package net.rim.device.apps.api.utility.framework;

final class SimplePersistentEncryptedSyncCollection$SPESCWaitForUnlock extends WaitForUnlock {
   private final SimplePersistentEncryptedSyncCollection this$0;

   public SimplePersistentEncryptedSyncCollection$SPESCWaitForUnlock(SimplePersistentEncryptedSyncCollection _1) {
      super(_1.getContentProtectionEnabledMessage());
      this.this$0 = _1;
   }

   @Override
   public final void performWork(Object cookie) {
      this.this$0.verifySorted();
   }
}
