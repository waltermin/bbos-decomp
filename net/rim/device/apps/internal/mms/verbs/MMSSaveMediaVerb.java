package net.rim.device.apps.internal.mms.verbs;

import java.io.InputStream;
import net.rim.device.api.io.MIMETypeAssociations;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.apps.api.framework.file.ExplorerServices;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.mms.MMSUtilities;
import net.rim.device.apps.internal.mms.api.AttachmentDataProvider;
import net.rim.device.apps.internal.mms.api.MMSAttachment;
import net.rim.device.apps.internal.mms.service.DRMConverter;
import net.rim.device.internal.io.file.FileUtilities;

public class MMSSaveMediaVerb extends Verb {
   private String[] _names;
   private String _whichText;
   private String _saveText;
   private AttachmentDataProvider _attachmentProvider;
   private boolean _drmProtected;

   public MMSSaveMediaVerb(String[] names, AttachmentDataProvider attachmentProvider, boolean drmProtected, String saveText, String whichText) {
      super(341584);
      this._names = names;
      this._attachmentProvider = attachmentProvider;
      this._drmProtected = drmProtected;
      this._saveText = saveText;
      this._whichText = whichText;
   }

   @Override
   public String toString() {
      return this._saveText;
   }

   @Override
   public Object invoke(Object context) {
      MMSAttachment attachment;
      if (this._names.length == 1) {
         attachment = this._attachmentProvider.getAttachment(this._names[0]);
      } else {
         int choice = Dialog.ask(this._whichText, this._names, 0);
         if (choice >= 0) {
            attachment = this._attachmentProvider.getAttachment(this._names[choice]);
         } else {
            attachment = null;
         }
      }

      if (attachment != null) {
         saveMedia(attachment, this._drmProtected);
      }

      return null;
   }

   private static void saveMedia(MMSAttachment attachment, boolean drmProtected) {
      while (attachment.getType() == 72) {
         attachment = DRMConverter.unwrap(attachment);
         if (attachment == null) {
            return;
         }

         drmProtected = true;
      }

      String attachmentName = attachment.getName();
      byte[] attachmentData = attachment.getData();
      String mimeTypeString = MMSUtilities.getMIMETypeString(attachment.getType());
      if (mimeTypeString != null && !mimeTypeString.equals(MIMETypeAssociations.getMIMEType(attachmentName))) {
         StringBuffer buf = (StringBuffer)(new Object(attachmentName));
         buf.append(".");
         buf.append(MIMETypeAssociations.getExtensionFromMIMEType(mimeTypeString));
         attachmentName = buf.toString();
      }

      if (attachmentData != null) {
         if (mimeTypeString != null && mimeTypeString.toLowerCase().equals("audio/amr")) {
            String path = FileUtilities.getDefaultPathForMIMEType(mimeTypeString);
            ExplorerServices.saveInputStream(
               path, attachmentName, (InputStream)(new Object(attachmentData)), MIMETypeAssociations.getMediaType(attachmentName), true, false, drmProtected
            );
            return;
         }

         ExplorerServices.saveInputStream(
            attachmentName, (InputStream)(new Object(attachmentData)), MIMETypeAssociations.getMediaType(attachmentName), true, false, drmProtected
         );
      }
   }
}
