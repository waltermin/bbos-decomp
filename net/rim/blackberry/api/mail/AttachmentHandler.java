package net.rim.blackberry.api.mail;

public interface AttachmentHandler {
   boolean supports(String var1);

   String menuString();

   void run(Message var1, SupportedAttachmentPart var2);
}
