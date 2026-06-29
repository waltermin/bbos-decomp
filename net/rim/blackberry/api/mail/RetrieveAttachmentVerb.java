package net.rim.blackberry.api.mail;

import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.attachment.AttachmentViewerModel;
import net.rim.device.apps.internal.blackberryemail.email.api.EmailMoreVerb;
import net.rim.device.apps.internal.blackberryemail.resources.EmailResources;

final class RetrieveAttachmentVerb extends Verb {
   private AttachmentViewerModel _attachmentViewerModel;

   RetrieveAttachmentVerb(AttachmentViewerModel attachmentViewerModel) {
      super(603904);
      this._attachmentViewerModel = attachmentViewerModel;
   }

   @Override
   public final Object invoke(Object context) {
      int preferredConversion = this._attachmentViewerModel.getPreferredConversion();
      EmailMoreVerb retrieveMoreVerb = new EmailMoreVerb(
         this._attachmentViewerModel, (byte)1, this._attachmentViewerModel._conversionsAvailable[preferredConversion]
      );
      return retrieveMoreVerb.invoke(context);
   }

   @Override
   public final String toString() {
      return EmailResources.getString(150);
   }
}
