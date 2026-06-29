package net.rim.device.apps.internal.secureemail;

import net.rim.device.api.crypto.certificate.CertificateServerInfo;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.util.FactoryUtil;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.utility.general.Copyable;
import net.rim.device.apps.api.utility.general.SetParameter;
import net.rim.device.apps.internal.blackberryemail.email.EmailEditorScreen;
import net.rim.device.apps.internal.blackberryemail.email.EmailMessageModel;
import net.rim.device.apps.internal.blackberryemail.header.EmailHeaderModel;
import net.rim.device.apps.internal.commonmodels.body.BodyModel;

public class SendCertificateServerVerb extends Verb implements Copyable, SetParameter {
   private boolean _usePin;
   private CertificateServerInfo _serverInfo;
   public static String TYPE_APPLICATION_CERTIFICATE_SERVERS = "application/x-rimdevice-certificate-servers";
   private static final ResourceBundle _rbCertificateServerOptions = ResourceBundle.getBundle(
      -3843310740975580338L, "net.rim.device.internal.resource.crypto.CertificateServersOptions"
   );

   public void sendCertificateServerAsAttachment(CertificateServerInfo info, boolean usePIN) {
      if (info == null) {
         throw new Object();
      }

      EmailMessageModel message = (EmailMessageModel)FactoryUtil.createInstance(-6822293833372928884L, null);
      message.setType((byte)32);
      ContextObject addrContext = (ContextObject)(new Object());
      ContextObject.setFlag(addrContext, 5);
      if (usePIN) {
         ContextObject.setFlag(addrContext, 94);
      }

      EmailHeaderModel addrHeader = (EmailHeaderModel)FactoryUtil.createInstance(-8034039608019345282L, addrContext);
      if (addrHeader.getInsideModel() != null) {
         message.add(addrHeader);
         BodyModel bodyModel = (BodyModel)FactoryUtil.createInstance(5987399499453925075L, null);
         bodyModel.setText(info.toString());
         message.add(bodyModel);
         if (usePIN) {
            message.setFlags(8192);
         }

         ContextObject serverContext = (ContextObject)(new Object());
         ContextObject.put(serverContext, -8616111138651597975L, info.clone());
         CertificateServersAttachmentModel serversModel = new CertificateServersAttachmentModel(serverContext);
         message.add(serversModel);
         ContextObject editorContext = (ContextObject)(new Object());
         editorContext.setFlag(0);
         editorContext.setFlag(121);
         if (usePIN) {
            editorContext.setFlag(94);
         }

         EmailEditorScreen editor = (EmailEditorScreen)(new Object(editorContext));
         editor.setModel(message);
         editor.go();
      }
   }

   @Override
   public Object copy() {
      SendCertificateServerVerb verb = new SendCertificateServerVerb(this._usePin);
      verb.setParameter(this._serverInfo);
      return verb;
   }

   @Override
   public void setParameter(Object parameter) {
      if (parameter instanceof Object) {
         this._serverInfo = (CertificateServerInfo)parameter;
      }
   }

   @Override
   public Object invoke(Object parameter) {
      this.sendCertificateServerAsAttachment(this._serverInfo, this._usePin);
      return null;
   }

   public SendCertificateServerVerb(boolean usePin) {
      super(usePin ? 16864022 : 16864021, _rbCertificateServerOptions.getFamily(), usePin ? 106 : 113);
      this._usePin = usePin;
   }
}
