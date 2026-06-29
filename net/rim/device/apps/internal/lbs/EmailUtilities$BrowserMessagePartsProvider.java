package net.rim.device.apps.internal.lbs;

import net.rim.device.apps.api.messaging.messagelist.MessageAttachment;
import net.rim.device.apps.api.messaging.messagelist.MessagePartsProvider;
import net.rim.device.apps.internal.lbs.resources.LBSResources;

final class EmailUtilities$BrowserMessagePartsProvider implements MessagePartsProvider {
   private String _url;
   private String _title;
   private MessageAttachment[] _attachments;

   EmailUtilities$BrowserMessagePartsProvider(String url, String title) {
      this._url = url;
      this._title = title;
   }

   EmailUtilities$BrowserMessagePartsProvider(String url, String title, MessageAttachment attachment) {
      this._url = url;
      this._title = title;
      if (attachment != null) {
         this._attachments = new MessageAttachment[]{attachment};
      }
   }

   @Override
   public final boolean inbound() {
      return false;
   }

   @Override
   public final boolean allowDescriptiveForwardHeader() {
      return false;
   }

   @Override
   public final String getSender() {
      return null;
   }

   @Override
   public final String[] getRecipients() {
      return null;
   }

   @Override
   public final String getBody() {
      return this._url;
   }

   @Override
   public final String getSubject() {
      return this._title;
   }

   @Override
   public final String getName() {
      return LBSResources.getString(274);
   }

   @Override
   public final void setRead(Object context) {
   }

   @Override
   public final long getSentDate() {
      return 0;
   }

   @Override
   public final MessageAttachment[] getAttachments() {
      return this._attachments;
   }
}
