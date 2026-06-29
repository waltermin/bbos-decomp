package net.rim.device.apps.internal.diagnostic;

import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.util.Persistable;

public final class RecipientDepot implements Persistable {
   private Object emailRecpt = null;
   private Object pinRecpt = null;

   RecipientDepot() {
   }

   final void setEmailRecpt(String email) {
      if (email.equals("")) {
         this.emailRecpt = null;
      } else {
         this.emailRecpt = PersistentContent.encode(email, false, true);
      }
   }

   final void setPinRecpt(String pin) {
      if (pin.equals("")) {
         this.pinRecpt = null;
      } else {
         this.pinRecpt = PersistentContent.encode(pin, false, true);
      }
   }

   final String getEmailRecpt() {
      return this.emailRecpt == null ? "" : PersistentContent.decodeString(this.emailRecpt);
   }

   final String getPinRecpt() {
      return this.pinRecpt == null ? "" : PersistentContent.decodeString(this.pinRecpt);
   }
}
