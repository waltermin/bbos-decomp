package net.rim.device.apps.internal.api.crypto.certificate;

import net.rim.device.api.crypto.certificate.Certificate;
import net.rim.device.api.crypto.keystore.KeyStoreData;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.FactoryUtil;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.transmission.rim.CMIMEParameters;
import net.rim.device.apps.internal.api.crypto.CryptoCommonResources;
import net.rim.device.apps.internal.blackberryemail.email.EmailEditorScreen;
import net.rim.device.apps.internal.blackberryemail.email.EmailMessageModel;
import net.rim.device.apps.internal.blackberryemail.header.EmailHeaderModel;
import net.rim.device.apps.internal.commonmodels.body.BodyModel;

public class SendCertificatesVerb extends Verb {
   KeyStoreData[] _keyStoreDatas;
   boolean _usePIN;
   CertificateAttachmentModelFactory _attachmentModelFactory;

   public SendCertificatesVerb(boolean usePIN, CertificateAttachmentModelFactory attachmentModelFactory) {
      super(usePIN ? 16864022 : 16864021, CryptoCommonResources.getBundle(), usePIN ? 32 : 31);
      this._usePIN = usePIN;
      this._attachmentModelFactory = attachmentModelFactory;
   }

   public void initialize(KeyStoreData[] keyStoreDatas) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   @Override
   public Object invoke(Object parameter) {
      EmailMessageModel message = (EmailMessageModel)FactoryUtil.createInstance(-6822293833372928884L, null);
      message.setType((byte)32);
      ContextObject addrContext = (ContextObject)(new Object());
      ContextObject.setFlag(addrContext, 5);
      if (this._usePIN) {
         ContextObject.setFlag(addrContext, 94);
      }

      EmailHeaderModel addrHeader = (EmailHeaderModel)FactoryUtil.createInstance(-8034039608019345282L, addrContext);
      if (addrHeader.getInsideModel() == null) {
         return null;
      }

      message.add(addrHeader);
      BodyModel bodyModel = (BodyModel)FactoryUtil.createInstance(5987399499453925075L, null);
      message.add(bodyModel);
      if (this._usePIN) {
         message.setFlags(8192);
      }

      StringBuffer bodyText = (StringBuffer)(new Object());

      for (int i = 0; i < this._keyStoreDatas.length; i++) {
         Certificate certificate = this._keyStoreDatas[i].getCertificate();
         if (i != 0) {
            bodyText.append("\n\n");
         }

         bodyText.append((String)certificate.getInformation(-5753772986264564736L, null, ""));
         byte[] encoding = certificate.getEncoding();
         CMIMEParameters parameters = (CMIMEParameters)(new Object((DataBuffer)(new Object()), 2, 1));
         parameters.addCMIMEInteger((byte)-13, encoding.length);
         parameters.addCMIMEInteger((byte)-16, encoding.length);
         ContextObject certificateContext = (ContextObject)(new Object());
         certificateContext.put(-4886909117188079897L, this._keyStoreDatas[i].getLabel());
         certificateContext.put(316628257119802273L, certificate);
         certificateContext.put(8849067667159082262L, encoding);
         certificateContext.put(-7353832199068708928L, parameters);
         CertificateAttachmentModel certificateModel = (CertificateAttachmentModel)this._attachmentModelFactory.createInstance(certificateContext);
         message.add(certificateModel);
      }

      bodyModel.setText(bodyText.toString());
      ContextObject editorContext = (ContextObject)(new Object());
      editorContext.setFlag(0);
      editorContext.setFlag(121);
      if (this._usePIN) {
         editorContext.setFlag(94);
      }

      EmailEditorScreen editor = (EmailEditorScreen)(new Object(editorContext));
      editor.setModel(message);
      editor.go();
      return null;
   }
}
