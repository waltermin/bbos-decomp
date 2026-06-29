package net.rim.device.api.crypto.keystore;

import net.rim.device.api.crypto.certificate.CertificateStatus;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.ControlledAccess;
import net.rim.device.internal.applicationcontrol.ApplicationControl;

public final class DeviceKeyStore extends SyncableRIMKeyStore {
   private static final long DEVICE = -6191874725138003853L;
   private static SyncableRIMKeyStore _deviceKeyStore;

   private DeviceKeyStore() {
      super("Handheld", KeyStoreResources.getString(6073), -6191874725138003853L, null, new DevicePersistableRIMKeyStoreFactory(), null);
   }

   private static final void assertPermission() {
      ApplicationControl.assertHandheldKeyStoreAllowed(true);
   }

   public static final KeyStore getInstance() {
      assertPermission();
      if (_deviceKeyStore == null) {
         ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
         _deviceKeyStore = (SyncableRIMKeyStore)ar.getOrWaitFor(-6191874725138003853L);
         if (_deviceKeyStore == null) {
            try {
               _deviceKeyStore = new DeviceKeyStore();
               ar.put(-6191874725138003853L, _deviceKeyStore);
            } catch (KeyStoreRegisterException e) {
               throw new RuntimeException();
            }
         }
      }

      return _deviceKeyStore;
   }

   final KeyStoreData set(String label, byte[] encoding, String type, CertificateStatus status) {
      ControlledAccess.assertRCISignatures(true);
      RIMKeyStoreData data = new RIMKeyStoreData(label, encoding, type, status);
      this.set(null, data);
      return data;
   }

   @Override
   public final String getName() {
      return KeyStoreResources.getString(6073);
   }
}
