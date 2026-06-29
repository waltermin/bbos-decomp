package net.rim.device.apps.internal.api.crypto.certificate;

import net.rim.device.api.crypto.certificate.Certificate;
import net.rim.device.api.crypto.keystore.KeyStoreData;
import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.transmission.rim.CMIMEParameters;
import net.rim.device.apps.internal.api.crypto.CryptoCommonResources;
import net.rim.device.apps.internal.keystore.browser.LaunchKeyStoreBrowserVerb;

public class AttachCertificatesVerb extends Verb {
   private CertificateAttachmentModelFactory _certificateAttachmentModelFactory;

   public AttachCertificatesVerb(CertificateAttachmentModelFactory certificateAttachmentModelFactory) {
      super(16864016);
      this._certificateAttachmentModelFactory = certificateAttachmentModelFactory;
   }

   @Override
   public String toString() {
      String[] containerStringUpperPluralArray = new String[]{this._certificateAttachmentModelFactory.getPublicKeyContainerString(true, true)};
      return MessageFormat.format(CryptoCommonResources.getString(17), containerStringUpperPluralArray);
   }

   @Override
   public Object invoke(Object context) {
      ContextObject displayContext = new ContextObject();
      displayContext.setFlag(5);
      LaunchKeyStoreBrowserVerb keyStoreBrowser = new LaunchKeyStoreBrowserVerb(
         this._certificateAttachmentModelFactory.getKeyStoreBrowserContextName(), displayContext
      );
      keyStoreBrowser.invoke(null);
      KeyStoreData[] selectedKeyStoreDatas = (KeyStoreData[])ContextObject.get(displayContext, -5328662892314083964L);
      if (selectedKeyStoreDatas == null) {
         return null;
      }

      int numSelectedKeyStoreDatas = selectedKeyStoreDatas.length;
      CertificateAttachmentModel[] attachedModels = new CertificateAttachmentModel[numSelectedKeyStoreDatas];

      for (int i = 0; i < numSelectedKeyStoreDatas; i++) {
         Certificate cert = selectedKeyStoreDatas[i].getCertificate();
         byte[] encoding = cert.getEncoding();
         CMIMEParameters parameters = new CMIMEParameters(new DataBuffer(), 2, 1);
         parameters.addCMIMEInteger((byte)-13, encoding.length);
         parameters.addCMIMEInteger((byte)-16, encoding.length);
         ContextObject modelContext = new ContextObject();
         modelContext.put(-4886909117188079897L, selectedKeyStoreDatas[i].getLabel());
         modelContext.put(316628257119802273L, cert);
         modelContext.put(8849067667159082262L, encoding);
         modelContext.put(-7353832199068708928L, parameters);
         attachedModels[i] = (CertificateAttachmentModel)this._certificateAttachmentModelFactory.createInstance(modelContext);
      }

      return attachedModels;
   }

   @Override
   public boolean equals(Object other) {
      return this == other || other instanceof AttachCertificatesVerb;
   }
}
