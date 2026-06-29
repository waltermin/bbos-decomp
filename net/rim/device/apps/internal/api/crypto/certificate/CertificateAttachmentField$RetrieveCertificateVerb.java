package net.rim.device.apps.internal.api.crypto.certificate;

import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.blackberryemail.email.api.EmailMoreVerb;
import net.rim.device.apps.internal.blackberryemail.unknown.UnknownMimePartModel;

class CertificateAttachmentField$RetrieveCertificateVerb extends Verb {
   UnknownMimePartModel _unknownMimePartModel;
   private final CertificateAttachmentField this$0;

   public CertificateAttachmentField$RetrieveCertificateVerb(CertificateAttachmentField _1, UnknownMimePartModel unknownMimePartModel) {
      super(16863808);
      this.this$0 = _1;
      this._unknownMimePartModel = unknownMimePartModel;
   }

   @Override
   public Object invoke(Object context) {
      if (this._unknownMimePartModel.isMoreAvailable()) {
         ((EmailMoreVerb)(new Object(this._unknownMimePartModel, (byte)2))).invoke(context);
      }

      return null;
   }

   @Override
   public String toString() {
      return this.this$0._certificateAttachmentModel.getRetrieveVerbDescription();
   }
}
