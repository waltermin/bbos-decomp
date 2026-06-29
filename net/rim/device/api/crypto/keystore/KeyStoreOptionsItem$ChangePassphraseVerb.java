package net.rim.device.api.crypto.keystore;

import net.rim.device.api.ui.component.Dialog;
import net.rim.device.apps.api.options.OptionsItemVerb;

class KeyStoreOptionsItem$ChangePassphraseVerb extends OptionsItemVerb {
   KeyStoreOptionsItem$ChangePassphraseVerb(String displayString) {
      super(displayString, 10000);
   }

   @Override
   public Object invoke(Object parameter) {
      int resourceId;
      try {
         KeyStorePasswordManager.getInstance().changePassword();
         resourceId = 7009;
      } catch (KeyStoreCancelException e) {
         resourceId = 7010;
      }

      Dialog.inform(KeyStoreOptionsItem._rb.getString(resourceId));
      return null;
   }
}
