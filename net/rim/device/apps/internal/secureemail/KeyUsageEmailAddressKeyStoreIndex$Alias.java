package net.rim.device.apps.internal.secureemail;

import net.rim.device.api.util.CRC32;
import net.rim.device.api.util.StringUtilities;

class KeyUsageEmailAddressKeyStoreIndex$Alias {
   private int _usage;
   private byte[] _emailAddress;
   private int _hashCode;

   public static int computeHashCode(int usage, byte[] emailAddress) {
      String tempEmail = (String)(new Object(emailAddress));
      return usage ^ CRC32.update(0, StringUtilities.toLowerCase(tempEmail, 1701707776).getBytes());
   }

   public KeyUsageEmailAddressKeyStoreIndex$Alias(int usage, byte[] emailAddress) {
      this._usage = usage;
      this._emailAddress = emailAddress;
      this._hashCode = computeHashCode(usage, emailAddress);
   }

   @Override
   public int hashCode() {
      return this._hashCode;
   }
}
