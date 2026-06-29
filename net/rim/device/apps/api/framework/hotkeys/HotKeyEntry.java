package net.rim.device.apps.api.framework.hotkeys;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.i18n.ResourceBundleFamily;
import net.rim.device.apps.api.framework.model.EncryptableProvider;
import net.rim.device.apps.api.framework.verb.Verb;

final class HotKeyEntry {
   String _bundleName;
   long _familyId;
   int _rbKey;
   char _hotkey;
   Verb _verb;
   boolean _uppercase;

   HotKeyEntry(ResourceBundleFamily family, int rbKey, Verb verb, boolean uppercase) {
      if (family != null) {
         this._familyId = family.getId();
         this._bundleName = family.getName();
         this._rbKey = rbKey;
         this._uppercase = uppercase;
         this._hotkey = family.getString(rbKey).charAt(0);
         if (this._uppercase) {
            this._hotkey = Character.toUpperCase(this._hotkey);
         } else {
            this._hotkey = Character.toLowerCase(this._hotkey);
         }
      }

      this._verb = verb;
   }

   HotKeyEntry(char hotkey, Verb verb) {
      this._hotkey = hotkey;
      this._verb = verb;
   }

   final boolean canUpdate() {
      return this._familyId != 0;
   }

   final void update() {
      if (this.canUpdate()) {
         ResourceBundleFamily family = ResourceBundle.getBundle(this._familyId, this._bundleName);
         this._hotkey = family.getString(this._rbKey).charAt(0);
         if (this._uppercase) {
            this._hotkey = Character.toUpperCase(this._hotkey);
            return;
         }

         this._hotkey = Character.toLowerCase(this._hotkey);
      }
   }

   final void reCrypt(boolean compress, boolean encrypt) {
      if (this._verb instanceof EncryptableProvider) {
         EncryptableProvider encryptable = (EncryptableProvider)this._verb;
         if (!encryptable.checkCrypt(compress, encrypt)) {
            Verb newVerb = (Verb)encryptable.reCrypt(compress, encrypt);
            if (newVerb != null) {
               this._verb = newVerb;
            }
         }
      }
   }
}
