package net.rim.device.apps.internal.secureemail.encodings.smime;

import net.rim.device.api.crypto.cms.CMSSignedDataInputStream;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.blackberryemail.email.EmailMessageModel;

public class SignedReceiptRequestedField$SendSignedReceiptVerb extends Verb {
   private CMSSignedDataInputStream _signedStream;
   private EmailMessageModel _messageModel;
   private final SignedReceiptRequestedField this$0;

   public SignedReceiptRequestedField$SendSignedReceiptVerb(
      SignedReceiptRequestedField _1, CMSSignedDataInputStream signedStream, EmailMessageModel messageModel
   ) {
      super(1200208, SMIMEResources.getBundle(), 2072);
      this.this$0 = _1;
      this._signedStream = signedStream;
      this._messageModel = messageModel;
   }

   @Override
   public Object invoke(Object context) {
      SignedReceiptHelper.processSignedReceiptRequest(this._signedStream, this._messageModel);
      this.this$0._signedReceiptSent = this._messageModel.flagsSet(16384);
      this.this$0._richTextField.setText(this.this$0.getText());
      return null;
   }
}
