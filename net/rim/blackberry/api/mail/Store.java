package net.rim.blackberry.api.mail;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import net.rim.blackberry.api.mail.event.FolderListener;
import net.rim.blackberry.api.mail.event.StoreListener;
import net.rim.device.api.util.LongHashtable;
import net.rim.device.apps.api.messaging.MessageLookups;
import net.rim.device.apps.internal.blackberryemail.email.EmailMessageModel;
import net.rim.device.apps.internal.blackberryemail.folder.EmailFolder;
import net.rim.device.apps.internal.blackberryemail.folder.EmailHierarchy;

public class Store extends Service {
   Vector _listeners = new Vector();
   EmailHierarchy _emailHierarchy;
   Hashtable _nameToFolderCache = new Hashtable();
   LongHashtable _idToFolderCache = new LongHashtable();
   Folder _draftFolder;
   Folder _anonymousDraftFolder;

   Store() {
   }

   public Folder[] list(int type) {
      Folder[] list = this.list();
      Vector v = new Vector();
      if (type != 4 && type != 3) {
         list(list, type, v);
      } else {
         list(list, 4, v);
         list(list, 3, v);
      }

      list = new Folder[v.size()];
      v.copyInto(list);
      return list;
   }

   static Folder[] list(Folder[] list, int type) {
      Vector v = new Vector();
      list(list, type, v);
      list = new Folder[v.size()];
      v.copyInto(list);
      return list;
   }

   static void list(Folder[] list, int type, Vector v) {
      int size = list.length;

      for (int i = 0; i < size; i++) {
         if (list[i].getType() == type) {
            v.addElement(list[i]);
         }
      }
   }

   public Folder getFolder(long folderId) throws FolderNotFoundException {
      Folder f = this.checkCache(folderId);
      if (f == null) {
         EmailFolder ef = EmailHierarchy.getEmailFolder(folderId);
         if (ef == null) {
            throw new FolderNotFoundException("<id=" + folderId, "Unknown folder id");
         }

         f = new Folder(ef);
         this.updateCache(f);
      }

      return f;
   }

   public Folder getFolder(String name) throws FolderNotFoundException {
      char sep = Folder.getSeparator();
      int length = name.length();
      name = name.substring(0, name.charAt(length - 1) == sep ? length - 1 : length);
      String rootsep = Folder.ROOT_SEPARATOR_STRING;
      int index = name.indexOf(rootsep);
      if (index == -1) {
         String serviceName = this._emailHierarchy.getFriendlyName();
         name = serviceName + rootsep + name;
      }

      Folder f = this.checkCache(name);
      if (null != f) {
         return f;
      }

      Folder[] folders = this.list();
      if (folders.length == 0) {
         throw new FolderNotFoundException(name, "No Folder Exists");
      }

      Folder cf = null;

      label44:
      while (true) {
         boolean foundsubtree = false;
         int i = 0;

         while (true) {
            if (i < folders.length) {
               cf = folders[i];
               String fullname = cf.getFullName();
               if (name.indexOf(fullname) == -1) {
                  i++;
                  continue;
               }

               foundsubtree = true;
               if (fullname.equals(name)) {
                  break label44;
               }
            }

            folders = cf.list();
            cf = null;
            if (!foundsubtree) {
               break label44;
            }
            break;
         }
      }

      if (cf == null) {
         throw new FolderNotFoundException(name, "Folder Not Found");
      }

      this.updateCache(cf);
      return cf;
   }

   public Folder[] findFolder(String substring) {
      substring = substring.toLowerCase();
      Vector v = new Vector();
      Folder[] list = this.list();
      findFolder(v, list, substring);
      list = new Folder[v.size()];
      v.copyInto(list);
      return list;
   }

   static void findFolder(Vector v, Folder[] list, String substring) {
      for (Folder f : list) {
         String name = f.getName().toLowerCase();
         if (name.indexOf(substring) != -1) {
            v.addElement(f);
         }

         findFolder(v, f.list(), substring);
      }
   }

   private Folder checkCache(long folderid, EmailFolder ef) {
      Folder f = (Folder)this._idToFolderCache.get(folderid);
      if (f == null && ef != null) {
         f = new Folder(ef);
         this.updateCache(f);
      }

      return f;
   }

   private Folder checkCache(long folderid) {
      return this.checkCache(folderid, null);
   }

   private Folder checkCache(String name) {
      return (Folder)this._nameToFolderCache.get(name);
   }

   private void updateCache(Folder f) {
      EmailFolder ef = (EmailFolder)f.getEmailFolder();
      this._idToFolderCache.put(ef.getLUID(), f);
      this._nameToFolderCache.put(f.getFullName(), f);
   }

   public Folder[] list() {
      return this.list(this._emailHierarchy);
   }

   Folder[] list(EmailHierarchy eh) {
      Folder[] folders = new Folder[0];
      if (eh.containsSubFolders()) {
         EmailFolder f = null;
         Enumeration e = eh.getSubFolders();
         Vector v = new Vector();

         while (e.hasMoreElements()) {
            f = (EmailFolder)e.nextElement();
            v.addElement(this.checkCache(f.getFolderId(), f));
         }

         f = (EmailFolder)eh.getFolder(eh.getSentFolder());
         Folder apifolder = this.checkCache(f.getFolderId(), f);
         if (!v.contains(apifolder)) {
            v.addElement(apifolder);
         }

         if (eh.getServiceUidHash() != -1 && eh.getServiceNameHash() != -1) {
            if (this._draftFolder == null) {
               this._draftFolder = new Folder(f);
            }

            this._draftFolder.setType(10);
            v.addElement(this._draftFolder);
         } else {
            if (this._anonymousDraftFolder == null) {
               this._anonymousDraftFolder = new Folder(f);
            }

            this._anonymousDraftFolder.setType(10);
            v.addElement(this._anonymousDraftFolder);
         }

         int count = v.size();
         folders = new Folder[count];
         v.copyInto(folders);
      }

      return folders;
   }

   private boolean containsFolder(Folder folder, Folder f) {
      if (folder.getId() == f.getId()) {
         return true;
      }

      Folder[] folders = f.list();
      if (folders != null && folders.length != 0) {
         for (int i = 0; i < folders.length; i++) {
            if (this.containsFolder(folder, folders[i])) {
               return true;
            }
         }
      }

      return false;
   }

   public void addFolderListener(FolderListener l) {
      ListenerManager lm = ListenerManager.getInstance();
      lm.addStoreFolderListener(this._emailHierarchy.getServiceUidHash(), l);
   }

   public void removeFolderListener(FolderListener l) {
      ListenerManager lm = ListenerManager.getInstance();
      lm.removeStoreFolderListener(this._emailHierarchy.getServiceUidHash(), l);
   }

   public void addStoreListener(StoreListener l) {
      if (l == null) {
         throw new NullPointerException();
      }

      ListenerManager lm = ListenerManager.getInstance();
      lm.addStoreListener(l);
   }

   public void removeStoreListener(StoreListener l) {
      ListenerManager lm = ListenerManager.getInstance();
      lm.removeStoreListener(l);
   }

   EmailHierarchy getEmailHierarchy() {
      return this._emailHierarchy;
   }

   void setEmailHierarchy(EmailHierarchy eh) {
      this._emailHierarchy = eh;
   }

   @Override
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      }

      if (!(o instanceof Store)) {
         return false;
      }

      Store other = (Store)o;
      return other.getServiceConfiguration().equals(this.getServiceConfiguration());
   }

   public static Message getMessage(int id) {
      Object o = MessageLookups.get(-4420850319371185992L, id);
      if (!(o instanceof EmailMessageModel)) {
         return null;
      }

      EmailMessageModel emm = (EmailMessageModel)o;
      return new Message(emm);
   }

   public void addSendListener(SendListener listener) {
      if (listener == null) {
         throw new NullPointerException("SendListener is null");
      }

      ListenerManager lm = ListenerManager.getInstance();
      lm.addEmailSendListener(this._emailHierarchy.getServiceUidHash(), listener);
   }

   public void removeSendListener(SendListener l) {
      ListenerManager lm = ListenerManager.getInstance();
      lm.removeEmailSendListener(this._emailHierarchy.getServiceUidHash(), l);
   }

   public int getUnreadMessageCount() {
      Folder[] folders = this.list();
      int count = 0;
      if (folders != null && folders.length != 0) {
         for (int i = 0; i < folders.length; i++) {
            count += this.getMessageCount(folders[i]);
         }
      }

      return count;
   }

   private int getMessageCount(Folder folder) {
      int count = 0;

      try {
         Message[] messages = folder.getMessages();

         for (int i = 0; i < messages.length; i++) {
            if (messages[i].isInbound() && !messages[i].isSet(1)) {
               count++;
            }
         }

         Folder[] folders = folder.list();
         if (folders != null && folders.length != 0) {
            for (int j = 0; j < folders.length; j++) {
               count += this.getMessageCount(folders[j]);
            }
         }
      } catch (MessagingException var6) {
      }

      return count;
   }
}
