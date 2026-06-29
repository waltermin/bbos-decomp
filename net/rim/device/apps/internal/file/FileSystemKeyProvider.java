package net.rim.device.apps.internal.file;

import net.rim.device.api.crypto.AbstractPseudoRandomSource;
import net.rim.device.api.crypto.RandomSource;
import net.rim.device.api.itpolicy.ITPolicy;
import net.rim.device.cldc.io.file.FileSystemEncryption;
import net.rim.device.internal.io.file.FileSystem;
import net.rim.device.internal.io.file.KeyProvider;
import net.rim.device.internal.system.DevicePasswordListener;
import net.rim.device.internal.system.NvStore;
import net.rim.device.internal.system.Security;
import net.rim.vm.Memory;

public final class FileSystemKeyProvider implements KeyProvider, DevicePasswordListener {
   private byte[] _password;
   private static final int DEVICE_KEY_LENGTH;
   private static final int USER_KEY_LENGTH;

   public FileSystemKeyProvider() {
      if (FileSystem.isFileSystemSupported(1)) {
         Security.getInstance().setFileSystemEncryptionListener(this);
      }
   }

   @Override
   public final byte[] getUserKey(byte[] password, byte[] salt) {
      if (password != null && salt != null) {
         try {
            return ((AbstractPseudoRandomSource)(new Object(password, salt, 3))).getBytes(32);
         } finally {
            ;
         }
      } else {
         return null;
      }
   }

   @Override
   public final synchronized byte[] getUserKey(byte[] salt) {
      return this.getUserKey(this._password, salt);
   }

   @Override
   public final synchronized byte[] getDeviceKey() {
      byte[] deviceKey = NvStore.readData(44);
      if (deviceKey == null) {
         deviceKey = RandomSource.getBytes(32);
         NvStore.writeData(44, deviceKey);
      }

      return deviceKey;
   }

   @Override
   public final synchronized void changePassword(String oldPassword, String newPassword) {
      if (ITPolicy.getBoolean(24, 58, false)) {
         this._password = null;
      } else {
         FileSystemEncryption.changePassword(oldPassword, newPassword);
         if (newPassword != null) {
            this._password = Memory.copyToRAMOnlyBytes(newPassword.getBytes());
         } else {
            this._password = null;
         }
      }
   }

   @Override
   public final synchronized void clearPassword() {
      this._password = null;
   }

   @Override
   public final synchronized void unlock(String password) {
      if (password != null && password.length() > 0) {
         if (ITPolicy.getBoolean(24, 58, false)) {
            this._password = null;
         } else {
            this._password = Memory.copyToRAMOnlyBytes(password.getBytes());
         }
      }
   }
}
