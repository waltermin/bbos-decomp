package net.rim.device.api.smartcard;

import net.rim.device.api.crypto.CryptoSmartCardUtilities;
import net.rim.device.api.crypto.keystore.DeviceKeyStore;
import net.rim.device.api.crypto.keystore.TrustedKeyStore;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.apps.api.framework.verb.Verb;

final class SmartCardImportVerb extends Verb {
   SmartCardImportVerb() {
      super(1200277, ResourceBundle.getBundle(7215549882295292649L, "net.rim.device.internal.resource.SmartCard").getFamily(), 31);
   }

   @Override
   public final Object invoke(Object parameter) {
      CryptoSmartCardUtilities.importCertificates(DeviceKeyStore.getInstance(), TrustedKeyStore.getInstance(), null);
      return null;
   }
}
