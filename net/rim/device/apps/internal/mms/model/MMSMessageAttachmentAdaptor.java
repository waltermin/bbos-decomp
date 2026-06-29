package net.rim.device.apps.internal.mms.model;

import net.rim.device.apps.api.messaging.messagelist.MessageAttachment;
import net.rim.device.apps.internal.mms.MMSUtilities;
import net.rim.device.apps.internal.mms.api.MMSAttachment;

final class MMSMessageAttachmentAdaptor implements MessageAttachment {
   private MMSAttachment _attachment;

   public MMSMessageAttachmentAdaptor(MMSAttachment attachment) {
      this._attachment = attachment;
   }

   @Override
   public final String getProperty(String name) {
      if (name.equalsIgnoreCase("Content-Type")) {
         return MMSUtilities.getMIMETypeString(this._attachment.getType());
      } else if (name.equalsIgnoreCase("Content-Length")) {
         return Integer.toString(this._attachment.getDataSize());
      } else if (name.equalsIgnoreCase("Content-Encoding")) {
         return this._attachment.getCharset();
      } else {
         return name.equalsIgnoreCase("Content-Location") ? this._attachment.getName() : null;
      }
   }

   @Override
   public final Object getData() {
      return this._attachment.getData();
   }
}
