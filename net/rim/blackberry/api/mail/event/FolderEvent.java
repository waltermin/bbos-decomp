package net.rim.blackberry.api.mail.event;

import net.rim.blackberry.api.mail.Folder;
import net.rim.blackberry.api.mail.Message;

public class FolderEvent extends MailEvent {
   private Message _message;
   private int _type;
   public static final int MESSAGE_ADDED = 1;
   public static final int MESSAGE_REMOVED = 2;
   public static final int FOLDER_CREATED = 3;
   public static final int FOLDER_DELETED = 4;
   public static final int FOLDER_UPDATED = 5;

   public FolderEvent(Folder folder, int type, Message m) {
      super(folder);
      this._type = type;
      this._message = m;
   }

   @Override
   public void dispatch(Object listener) {
      if (listener instanceof ServiceListener) {
         ServiceListener l = (ServiceListener)listener;
         switch (this._type) {
            case 2:
               break;
            case 3:
            default:
               l.folderCreated(this);
               return;
            case 4:
               l.folderDeleted(this);
               return;
            case 5:
               l.folderUpdated(this);
               return;
         }
      }

      if (listener instanceof FolderListener) {
         FolderListener l = (FolderListener)listener;
         switch (this._type) {
            case 0:
               break;
            case 1:
            default:
               l.messagesAdded(this);
               return;
            case 2:
               l.messagesRemoved(this);
               return;
         }
      }
   }

   public Message getMessage() {
      return this._message;
   }

   public int getType() {
      return this._type;
   }

   @Override
   public String toString() {
      return "FolderEvent: " + this._type;
   }
}
