package net.rim.device.apps.internal.browser.verbs;

import net.rim.device.apps.api.messaging.messagelist.MessageAttachment;
import net.rim.device.apps.api.messaging.messagelist.MessagePartsProvider;
import net.rim.device.apps.internal.browser.resources.BrowserResources;

class ShowUrlVerb$BrowserMessagePartsProvider implements MessagePartsProvider {
   private String _url;
   private String _title;

   ShowUrlVerb$BrowserMessagePartsProvider(String url, String title) {
      this._url = url;
      this._title = title;
   }

   @Override
   public boolean inbound() {
      return false;
   }

   @Override
   public boolean allowDescriptiveForwardHeader() {
      return false;
   }

   @Override
   public String getSender() {
      return null;
   }

   @Override
   public String[] getRecipients() {
      return null;
   }

   @Override
   public String getBody() {
      return this._url;
   }

   @Override
   public String getSubject() {
      return this._title;
   }

   @Override
   public String getName() {
      return BrowserResources.getString(676);
   }

   @Override
   public void setRead(Object context) {
   }

   @Override
   public long getSentDate() {
      return 0;
   }

   @Override
   public MessageAttachment[] getAttachments() {
      return null;
   }
}
