package net.rim.device.apps.internal.api.crypto.certificate;

import net.rim.device.api.crypto.keystore.KeyStore;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.registration.RIMModelFactory;
import net.rim.device.apps.api.framework.verb.Verb;

public class CertificateAttachmentModelFactory extends RIMModelFactory {
   private Verb[] _attachCertificatesVerbs;

   @Override
   public int getMinimumCount(Object context) {
      return Integer.MIN_VALUE;
   }

   @Override
   public Verb[] getVerbs(Object context) {
      if (ContextObject.getFlag(context, 43)) {
         if (this._attachCertificatesVerbs == null) {
            this._attachCertificatesVerbs = new Object[]{new AttachCertificatesVerb(this)};
         }

         if (this.getPreferredKeyStore().size() > 0) {
            return this._attachCertificatesVerbs;
         }
      }

      return null;
   }

   @Override
   public Object createInstance(Object initialData) {
      return this.createCertificateAttachmentModel(initialData);
   }

   @Override
   public boolean recognize(Object o) {
      return this.recognizeCertificateAttachmentModel(o);
   }

   public KeyStore getPreferredKeyStore() {
      throw null;
   }

   public String getKeyStoreBrowserContextName() {
      throw null;
   }

   public Object createCertificateAttachmentModel(Object _1) {
      throw null;
   }

   public boolean recognizeCertificateAttachmentModel(Object _1) {
      throw null;
   }

   public String getPublicKeyContainerString(boolean _1, boolean _2) {
      throw null;
   }
}
