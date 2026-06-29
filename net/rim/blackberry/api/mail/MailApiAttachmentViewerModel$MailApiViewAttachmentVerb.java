package net.rim.blackberry.api.mail;

import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.blackberryemail.email.EmailMessageModel;

final class MailApiAttachmentViewerModel$MailApiViewAttachmentVerb extends Verb {
   private MailApiAttachmentViewerModel _attachmentViewerModel;
   private AttachmentHandler _handler;

   public MailApiAttachmentViewerModel$MailApiViewAttachmentVerb(AttachmentHandler handler, MailApiAttachmentViewerModel attachmentViewerModel) {
      super(16863840);
      this._handler = handler;
      this._attachmentViewerModel = attachmentViewerModel;
   }

   @Override
   public final Object invoke(Object context) {
      if (context instanceof ContextObject) {
         ContextObject c = (ContextObject)context;
         EmailMessageModel emm = (EmailMessageModel)c.get(250);
         Message m = new Message(emm);
         Multipart mp = (Multipart)m.getContent();
         int count = mp.getCount();

         for (int i = count - 1; i >= 0; i--) {
            BodyPart p = mp.getBodyPart(i);
            if (p instanceof SupportedAttachmentPart && ((SupportedAttachmentPart)p)._data == this._attachmentViewerModel) {
               this._handler.run(m, (SupportedAttachmentPart)p);
            }
         }
      }

      return null;
   }

   @Override
   public final String toString() {
      return this._handler.menuString();
   }
}
