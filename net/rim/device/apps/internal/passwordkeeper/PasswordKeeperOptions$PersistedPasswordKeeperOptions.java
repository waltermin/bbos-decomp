package net.rim.device.apps.internal.passwordkeeper;

import net.rim.vm.Persistable;

final class PasswordKeeperOptions$PersistedPasswordKeeperOptions implements Persistable {
   private byte[] _hash;
   private int _counter = 1;
   private int _threshold = 10;
   private int _randomPasswordLength = 8;
   private boolean _alpha = true;
   private boolean _numeric = true;
   private boolean _symbol = true;
   private boolean _confirm = true;
   private boolean _allowCopy = true;
   private boolean _showPassword = true;
   private boolean _otaSync = true;

   private PasswordKeeperOptions$PersistedPasswordKeeperOptions() {
   }

   PasswordKeeperOptions$PersistedPasswordKeeperOptions(PasswordKeeperOptions$1 x0) {
      this();
   }

   static final int access$308(PasswordKeeperOptions$PersistedPasswordKeeperOptions x0) {
      return x0._counter++;
   }
}
