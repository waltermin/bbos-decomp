package net.rim.device.apps.internal.secureemail.encodings.smime.cache;

import net.rim.device.api.crypto.certificate.x509.X509Certificate;
import net.rim.device.api.crypto.cms.EMSAcceptRequestInputStream;
import net.rim.device.api.ui.Manager;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.internal.secureemail.cache.CachedField;
import net.rim.device.apps.internal.secureemail.encodings.smime.EMSAcceptRequestField;
import net.rim.device.apps.internal.secureemail.encodings.smime.SMIMEResources;

public class CachedEMSAcceptRequestField extends CachedField {
   private EMSAcceptRequestInputStream _emsAcceptRequestInputStream;

   public CachedEMSAcceptRequestField(EMSAcceptRequestInputStream emsAcceptRequestInputStream) {
      this._emsAcceptRequestInputStream = emsAcceptRequestInputStream;
   }

   @Override
   public void fillManager(Manager manager, Object context) {
      if (!ContextObject.getFlag(context, 101)) {
         String displayName = this._emsAcceptRequestInputStream.getDisplayName();
         if (displayName == null) {
            String[] names = this._emsAcceptRequestInputStream.getNames();
            if (names != null && names.length > 0) {
               displayName = names[0];
            }

            if (displayName == null) {
               displayName = SMIMEResources.getString(2028);
            }
         }

         X509Certificate[] certificates = this._emsAcceptRequestInputStream.getCertificates();
         if (certificates != null && certificates.length != 0) {
            manager.add(new EMSAcceptRequestField(this._emsAcceptRequestInputStream, displayName, certificates[0], super._emailMessageModel));
         }
      }
   }
}
