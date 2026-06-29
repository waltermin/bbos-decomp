package net.rim.device.api.crypto.keystore;

import net.rim.device.api.system.PersistentContent;
import net.rim.vm.Memory;
import net.rim.vm.Process;

final class KeyStorePasswordManager$RIMKeyStorePasswordTicket implements KeyStorePasswordTicket {
   private int _processID = Process.currentProcess().getProcessId();
   private byte[] _password;
   private int _passwordVersion;

   public KeyStorePasswordManager$RIMKeyStorePasswordTicket(byte[] password) {
      if (password != null) {
         this._password = Memory.copyToRAMOnlyBytes(password);
         PersistentContent.markAsPlaintext(this._password);
      }

      this._passwordVersion = KeyStoreManagerHelper.getInstance().getPasswordVersion();
   }

   public final boolean access() {
      int currentProcessID = Process.currentProcess().getProcessId();
      return this._processID == currentProcessID && this._passwordVersion == KeyStoreManagerHelper.getInstance().getPasswordVersion();
   }

   public final byte[] getPassword() {
      return this._password;
   }
}
