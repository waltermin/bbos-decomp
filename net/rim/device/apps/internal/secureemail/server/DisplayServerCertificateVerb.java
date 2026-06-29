package net.rim.device.apps.internal.secureemail.server;

import net.rim.device.api.crypto.certificate.Certificate;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.secureemail.SecureEmailFactory;
import net.rim.device.internal.ui.component.BackgroundDialog;
import net.rim.device.internal.ui.component.PopupDialog;

public final class DisplayServerCertificateVerb extends Verb {
   private String _description;
   private Certificate _certificate;
   private long _certificateProperties;
   private SecureEmailFactory _secureEmailFactory;

   public DisplayServerCertificateVerb(String description, Certificate certificate, long certificateProperties, SecureEmailFactory secureEmailFactory) {
      super(1200208);
      this._description = description;
      this._certificate = certificate;
      this._certificateProperties = certificateProperties;
      this._secureEmailFactory = secureEmailFactory;
   }

   @Override
   public final String toString() {
      return this._description;
   }

   @Override
   public final Object invoke(Object context) {
      PopupDialog certificatePropertiesDialog = this._secureEmailFactory.createCertificatePropertiesDialog(this._certificate, this._certificateProperties);
      BackgroundDialog.show(certificatePropertiesDialog);
      return null;
   }
}
