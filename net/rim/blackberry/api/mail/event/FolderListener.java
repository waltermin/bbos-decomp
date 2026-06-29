package net.rim.blackberry.api.mail.event;

public interface FolderListener extends EventListener {
   void messagesAdded(FolderEvent var1);

   void messagesRemoved(FolderEvent var1);
}
