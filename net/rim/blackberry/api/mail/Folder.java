package net.rim.blackberry.api.mail;

import java.util.Enumeration;
import java.util.Vector;
import net.rim.blackberry.api.mail.event.FolderListener;
import net.rim.device.api.collection.ReadableList;
import net.rim.device.api.servicebook.ServiceBook;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.system.RIMPersistentStore;
import net.rim.device.apps.api.framework.model.ActionProvider;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.DefaultProvider;
import net.rim.device.apps.api.messaging.FolderHierarchies;
import net.rim.device.apps.api.messaging.MessageLookups;
import net.rim.device.apps.internal.blackberryemail.email.EmailMessageModel;
import net.rim.device.apps.internal.blackberryemail.email.EmailPayloadModel;
import net.rim.device.apps.internal.blackberryemail.email.api.EmailModifier;
import net.rim.device.apps.internal.blackberryemail.email.api.EmailSendUtility;
import net.rim.device.apps.internal.blackberryemail.folder.EmailFolder;
import net.rim.device.apps.internal.blackberryemail.folder.EmailHierarchy;

public class Folder {
   private int _folderType = -1;
   Vector _fListeners;
   protected Store _store;
   private Folder _parent;
   private EmailFolder _emailFolder;
   public static final int SUBTREE = 0;
   public static final int DELETED = 1;
   public static final int INBOX = 2;
   public static final int OUTBOX = 3;
   public static final int SENT = 4;
   public static final int OTHER = 5;
   public static final int ORPHAN = 80;
   public static final int INVALID = -1;
   public static final int FILED = 82;
   public static final int UNFILED = 81;
   public static final int DRAFT = 10;
   private static String DRAFT_FOLDER_NAME = "draft";
   public static final char SEPARATOR_CHAR = '/';
   public static final char ROOT_SEPARATOR = ':';
   public static String ROOT_SEPARATOR_STRING = "://";
   public static final int INVALID_FOLDER_ID = Integer.MAX_VALUE;
   public static String NEW_FOLDER_NAME = "New_Folder";
   private static final String DEFAULT_INBOX_NAME = "inbox";
   static final String DEFAULT_OUTBOX_FOLDER_NAME = "outbox";
   static final String DEFAULT_SENT_FOLDER_NAME = "sent items";
   private static ContextObject _fileMessageContext = (ContextObject)(new Object());

   Folder(Folder folder, EmailFolder f) {
      this(folder.getStore(), f);
      this._parent = folder;
   }

   Folder(EmailFolder f) {
      ServiceBook sb = ServiceBook.getSB();
      ServiceRecord r = sb.getRecordByCidAndUserId(f.getServiceContentIdentifier(), f.getServiceUserId(), f.getServiceNameHash(), f.getServiceUidHash());
      Store s = null;
      if (r == null) {
         Session session = Session.getDefaultInstance();
         if (null != session) {
            s = session.getStore();
         }
      } else {
         ServiceConfiguration sc = new ServiceConfiguration(r);
         s = Session.getInstance(sc).getStore();
      }

      this.commonInit(s, f);
   }

   Folder(Store store, EmailFolder f) {
      this.commonInit(store, f);
   }

   private void commonInit(Store store, EmailFolder f) {
      this._store = store;
      this._fListeners = (Vector)(new Object());
      if (f == null) {
         throw new Object("unreachable!");
      }

      this._emailFolder = f;
   }

   public String getName() {
      return this._folderType == 10 ? DRAFT_FOLDER_NAME : this._emailFolder.getFriendlyName();
   }

   public String getFullName() {
      StringBuffer fullName = (StringBuffer)(new Object());
      String storeName = this._emailFolder.getEmailHierarchy().getFriendlyName();
      fullName.append(storeName);
      fullName.append(':');
      fullName.append('/');
      fullName.append('/');
      int index = fullName.length();
      fullName.append(this.getName());

      for (Folder parent = this.getParent(); parent != null; parent = parent.getParent()) {
         fullName.insert(index, '/');
         fullName.insert(index, parent.getName());
      }

      return fullName.toString();
   }

   public Store getStore() {
      return this._store;
   }

   public Folder getParent() {
      if (this._parent == null) {
         net.rim.device.apps.api.messaging.Folder parent = this._emailFolder.getParentFolder();
         if (parent instanceof Object) {
            EmailFolder ef = (EmailFolder)parent;
            this._parent = new Folder(ef);
         }
      }

      return this._parent;
   }

   public Folder[] list() {
      Vector v = (Vector)(new Object());
      Enumeration e = this._emailFolder.getSubFolders();

      while (e.hasMoreElements()) {
         v.addElement(new Folder(this, (EmailFolder)e.nextElement()));
      }

      int size = v.size();
      Folder[] folders = new Folder[size];
      v.copyInto(folders);
      return folders;
   }

   public Folder[] list(int type) {
      return Store.list(this.list(), type);
   }

   public static char getSeparator() {
      return '/';
   }

   public int getType() {
      return this._folderType == -1 ? this._emailFolder.getFolderType() : this._folderType;
   }

   void setType(int type) {
      this._folderType = type;
   }

   public Folder getFolder(String name) throws FolderNotFoundException {
      if (name.indexOf(58) != -1) {
         throw new Object("Absolute folder names are not supported for Folder.getFolder()");
      }

      char sep = getSeparator();
      if (name.charAt(name.length() - 1) == sep) {
         throw new FolderNotFoundException(name, ((StringBuffer)(new Object("Last character should not be "))).append(sep).toString());
      }

      int sep1 = -1;
      sep1 = name.indexOf(sep);
      String fn;
      if (sep1 == -1) {
         fn = name;
      } else {
         fn = name.substring(0, sep1);
      }

      Enumeration enumeration = this._emailFolder.getSubFolders();
      EmailFolder f = null;

      while (enumeration.hasMoreElements()) {
         f = (EmailFolder)enumeration.nextElement();
         if (fn.compareTo(f.getFriendlyName()) == 0) {
            Folder folder = new Folder(this, f);
            if (sep1 == -1) {
               return folder;
            }

            return folder.getFolder(name.substring(sep1 + 1));
         }
      }

      throw new FolderNotFoundException(name, null);
   }

   public Message[] getMessages() {
      if (this._emailFolder != null) {
         long luid = this._emailFolder.getLUID();
         ReadableList filed = (ReadableList)EmailHierarchy.getStorageCollection(luid, true);
         ReadableList unfiled = (ReadableList)EmailHierarchy.getStorageCollection(luid, false);
         Vector v = (Vector)(new Object(10));
         this.extractMatchingMessages(luid, filed, v);
         this.extractMatchingMessages(luid, unfiled, v);
         Message[] array = new Message[v.size()];
         v.copyInto(array);
         return this._folderType == 10 ? this.getDraftMessages(array) : array;
      } else {
         return new Message[0];
      }
   }

   public Message[] getDraftMessages(Message[] array) {
      Vector v = (Vector)(new Object());

      for (int i = 0; i < array.length; i++) {
         if (array[i].getStatus() == Integer.MAX_VALUE) {
            v.addElement(array[i]);
         }
      }

      Message[] temp = new Message[v.size()];
      v.copyInto(temp);
      return temp;
   }

   private void extractMatchingMessages(long folderid, ReadableList l, Vector dest) {
      int size = l.size();
      boolean thisIsDefaultInbox = 2 == this._emailFolder.getFolderType();

      for (int i = 0; i < size; i++) {
         EmailMessageModel m = (EmailMessageModel)l.getAt(i);
         long mfid = m.getFolderId();
         if (mfid == folderid || null == EmailHierarchy.getEmailFolder(mfid) && thisIsDefaultInbox) {
            dest.addElement(new Message(this, m));
         }
      }
   }

   public boolean appendMessage(Message msg) {
      boolean retval = false;
      EmailMessageModel em = msg.getEmailMessageModel();
      EmailPayloadModel oldpayload = EmailModifier.beginChanges(em, null);
      if ((em.getFlags() & 262144) != 0) {
         em.clearFlags(262144);
         em.setCMIMEReferenceIdentifier(0);
      }

      if (!EmailSendUtility.determineWhetherMessageAlreadyFiled(em, null)) {
         EmailSendUtility.assignCMIMEReferenceIdentifierToMessage(em, null);
         EmailHierarchy.addMessage(em, this._emailFolder.getLUID());
         if (msg.isInbound() && !msg.isSet(1)) {
            msg.getEmailMessageModel().setFlags(1);
            msg.getEmailMessageModel().changeStatus(0, 1, 0, 0, false, false, false, false, null);
         }

         MessageLookups.put(-4420850319371185992L, em.getCMIMEReferenceIdentifier(), em);
         msg._folder = this;
         retval = true;
      } else {
         synchronized (RIMPersistentStore.getSynchObject()) {
            synchronized (FolderHierarchies.getLockObject()) {
               synchronized (_fileMessageContext) {
                  _fileMessageContext.put(-1219344331000926502L, this._emailFolder);
                  EmailMessageModel emm = msg.getEmailMessageModel();
                  if (emm instanceof Object) {
                     ActionProvider actionProvider = (ActionProvider)emm;
                     actionProvider.perform(1092577344890817449L, _fileMessageContext);
                  }

                  if (emm instanceof Object) {
                     DefaultProvider defaultProvider = (DefaultProvider)emm;
                     defaultProvider.updateDefault(this._emailFolder, _fileMessageContext);
                  }

                  msg._folder = this;
                  retval = true;
               }
            }
         }
      }

      EmailModifier.endChanges(em, oldpayload, null);
      return retval;
   }

   public boolean appendMessages(Message[] msgs) {
      boolean retval = true;

      for (int i = msgs.length - 1; i >= 0; i--) {
         retval &= this.appendMessage(msgs[i]);
      }

      return retval;
   }

   public boolean deleteMessage(Message msg) {
      EmailMessageModel em = msg.getEmailMessageModel();
      if (em.getFolderId() == 0) {
         return false;
      }

      ActionProvider ap = (ActionProvider)em;
      ContextObject context = (ContextObject)(new Object());
      ap.perform(6780594967363292755L, context);
      return true;
   }

   public boolean deleteMessage(Message msg, boolean forceDeleteSaved) {
      EmailMessageModel em = msg.getEmailMessageModel();
      if (em.getFolderId() == 0) {
         return false;
      } else {
         ActionProvider ap = (ActionProvider)em;
         if (forceDeleteSaved) {
            ap.perform(3675472832548253043L, null);
            return true;
         } else {
            ap.perform(6780594967363292755L, null);
            return true;
         }
      }
   }

   public synchronized void addFolderListener(FolderListener l) {
      ListenerManager lm = ListenerManager.getInstance();
      lm.addFolderListener(this._emailFolder, l);
   }

   public synchronized void removeFolderListener(FolderListener l) {
      ListenerManager lm = ListenerManager.getInstance();
      lm.removeFolderListener(this._emailFolder, l);
   }

   public long getId() {
      return this._emailFolder != null ? this._emailFolder.getLUID() : Integer.MAX_VALUE;
   }

   @Override
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      }

      if (!(o instanceof Folder)) {
         return false;
      }

      Folder f = (Folder)o;
      return this._emailFolder.getLUID() == f._emailFolder.getLUID();
   }

   @Override
   public int hashCode() {
      long uid = this._emailFolder.getLUID();
      return (int)(uid ^ uid >>> 32);
   }

   @Override
   public String toString() {
      return this.getFullName() != null ? this.getFullName() : super.toString();
   }

   synchronized boolean setEmailFolder(EmailFolder f) {
      this._emailFolder = f;
      return true;
   }

   public Object getEmailFolder() {
      return this._emailFolder;
   }
}
