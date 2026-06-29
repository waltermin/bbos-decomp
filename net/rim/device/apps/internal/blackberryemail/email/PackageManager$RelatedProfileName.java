package net.rim.device.apps.internal.blackberryemail.email;

import net.rim.device.apps.internal.blackberryemail.resources.EmailResources;

final class PackageManager$RelatedProfileName {
   String _emailAddress;

   PackageManager$RelatedProfileName(String emailAddress) {
      this._emailAddress = emailAddress;
   }

   @Override
   public final String toString() {
      return EmailResources.getString(800) + "[" + this._emailAddress + "]";
   }
}
