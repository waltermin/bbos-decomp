package net.rim.device.apps.api.messaging.messagelist;

public interface MessagePartsProvider {
   boolean inbound();

   boolean allowDescriptiveForwardHeader();

   String getSender();

   String[] getRecipients();

   String getBody();

   String getSubject();

   String getName();

   void setRead(Object var1);

   long getSentDate();

   MessageAttachment[] getAttachments();
}
