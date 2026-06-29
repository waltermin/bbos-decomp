package net.rim.device.apps.internal.secureemail;

import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.apps.api.framework.verb.Verb;

class SecureEmailEncryptionField$DisplayWeakEncryptionReasonVerb extends Verb {
   String _cipherAlgorithm;
   String _publicKeyAlgorithm;
   int _publicKeyAlgorithmLength;
   boolean _weakRecipient;

   SecureEmailEncryptionField$DisplayWeakEncryptionReasonVerb(
      String cipherAlgorithm, String publicKeyAlgorithm, int publicKeyAlgorithmLength, boolean weakRecipient
   ) {
      super(1200224, SecureEmailResources.getBundle(), 49);
      this._cipherAlgorithm = cipherAlgorithm;
      this._publicKeyAlgorithm = publicKeyAlgorithm;
      this._publicKeyAlgorithmLength = publicKeyAlgorithmLength;
      this._weakRecipient = weakRecipient;
   }

   @Override
   public Object invoke(Object context) {
      if (this._cipherAlgorithm != null) {
         Dialog.inform(MessageFormat.format(SecureEmailResources.getString(55), new String[]{this._cipherAlgorithm}));
         return null;
      }

      if (this._publicKeyAlgorithm != null) {
         Dialog.inform(
            MessageFormat.format(SecureEmailResources.getString(56), new String[]{this._publicKeyAlgorithm, Integer.toString(this._publicKeyAlgorithmLength)})
         );
         return null;
      }

      if (this._weakRecipient) {
         Dialog.inform(SecureEmailResources.getString(146));
      }

      return null;
   }
}
