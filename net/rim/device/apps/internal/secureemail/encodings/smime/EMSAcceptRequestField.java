package net.rim.device.apps.internal.secureemail.encodings.smime;

import net.rim.device.api.crypto.certificate.CertificateUtilities;
import net.rim.device.api.crypto.certificate.x509.X509Certificate;
import net.rim.device.api.crypto.cms.EMSAcceptRequestInputStream;
import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.apps.internal.blackberryemail.email.EmailMessageModel;
import net.rim.device.internal.i18n.CommonResource;
import net.rim.device.internal.ui.RichTextFieldUtilities;
import net.rim.device.internal.ui.component.PleaseWaitDialog;

public class EMSAcceptRequestField extends VerticalFieldManager implements FieldChangeListener {
   EMSAcceptRequestInputStream _emsAcceptRequestInputStream;
   EmailMessageModel _emailMessageModel;
   ButtonField _viewButton;
   ButtonField _acceptButton;
   ButtonField _declineButton;
   X509Certificate _certificate;

   public EMSAcceptRequestField(
      EMSAcceptRequestInputStream emsAcceptRequestInputStream, String displayName, X509Certificate certificate, EmailMessageModel emailMessageModel
   ) {
      this._emsAcceptRequestInputStream = emsAcceptRequestInputStream;
      this._certificate = certificate;
      this._emailMessageModel = emailMessageModel;
      String acceptRequestString = MessageFormat.format(SMIMEResources.getString(2095), new Object[]{displayName});
      this.add(RichTextFieldUtilities.getBoldFormattedRichTextField(acceptRequestString, 9007199254740992L));
      HorizontalFieldManager hfm = (HorizontalFieldManager)(new Object(12884901888L));
      this._viewButton = (ButtonField)(new Object(CommonResource.getString(10015), 98304));
      this._viewButton.setChangeListener(this);
      hfm.add(this._viewButton);
      this._acceptButton = (ButtonField)(new Object(SMIMEResources.getString(2097), 98304));
      this._acceptButton.setChangeListener(this);
      hfm.add(this._acceptButton);
      this._declineButton = (ButtonField)(new Object(SMIMEResources.getString(2098), 98304));
      this._declineButton.setChangeListener(this);
      hfm.add(this._declineButton);
      this.add((Field)(new Object(6)));
      this.add(hfm);
      this.add((Field)(new Object(6)));
   }

   @Override
   public void fieldChanged(Field field, int context) {
      boolean closeMessageViewer = false;
      if (field == this._viewButton) {
         SMIMEFactory smimeFactory = SMIMEFactory.getInstance();
         CertificateUtilities.displayCertificateDetails(
            this._certificate, null, smimeFactory.getPreferredKeyStore(), smimeFactory.getCryptoSystemProperties(), true, null
         );
      } else {
         closeMessageViewer = this.handleAcceptOrDecline(field == this._acceptButton);
      }

      if (closeMessageViewer) {
         this.getScreen().close();
      }
   }

   private boolean handleAcceptOrDecline(boolean accept) {
      if (!accept && this._emsAcceptRequestInputStream.getVersion() == 0) {
         return true;
      }

      EMSAcceptRequestField$SendAcceptResponseWorker sendAcceptResponseWorker = new EMSAcceptRequestField$SendAcceptResponseWorker(this, accept);
      PleaseWaitDialog pleaseWait = (PleaseWaitDialog)(new Object(SMIMEResources.getString(2096), sendAcceptResponseWorker));
      pleaseWait.display();
      return sendAcceptResponseWorker.getResult();
   }
}
