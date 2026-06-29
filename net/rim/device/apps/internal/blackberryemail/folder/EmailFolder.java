package net.rim.device.apps.internal.blackberryemail.folder;

import java.util.Enumeration;
import net.rim.device.api.collection.Collection;
import net.rim.device.api.collection.IntRangedActionTarget;
import net.rim.device.api.collection.WritableSet;
import net.rim.device.api.servicebook.ServiceBook;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.util.ObjectEnumerator;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.messaging.FilingTarget;
import net.rim.device.apps.api.messaging.Folder;
import net.rim.device.apps.api.messaging.FolderHierarchies;
import net.rim.device.apps.api.messaging.util.PersistedSortedCollection;
import net.rim.device.apps.internal.api.quincy.QuincyManager;
import net.rim.device.apps.internal.blackberryemail.email.EmailEventLoggerEvents;
import net.rim.device.apps.internal.blackberryemail.resources.EmailResources;
import net.rim.vm.Array;

public class EmailFolder implements Folder, FilingTarget, EmailFolderBase, EmailEventLoggerEvents {
   private long _luid;
   private int _folderId;
   private int _folderType;
   private boolean _inFolderDatabase;
   private boolean _isSticky;
   private int _desktopUID;
   private int _userId;
   private EmailFolderBase _parent;
   String _friendlyName;
   private EmailFolder _child;
   private EmailFolder _sibling;
   private int _attributes;
   private static final int[][] FOLDER_NAME_IDS = new int[][]{
      {95, 96, 97, 98, -805044223, 1644232835, 6595183, 1829528321, 1842762890, 1092364649, 490435700, 1929445491, 1929445418, 7618858, 1802466817, 1979777125},
      {63, 64, 65, 66, -804651004, 67, 68, 69, 74, -804651004, 95, 96, 97, 98, -805044223, 1644232835},
      {67, 68, 69, 74, -804651004, 95, 96, 97, 98, -805044223, 1644232835, 6595183, 1829528321, 1842762890, 1092364649, 490435700}
   };

   void setDesktopUID(int uid) {
      this._desktopUID = uid;
   }

   public boolean setParentFolder(EmailFolderBase parent) {
      String exceptionToLog = null;
      EmailFolderBase currentFolder = parent;

      while (currentFolder != null) {
         if (currentFolder == this) {
            exceptionToLog = "FPCC";
            break;
         }

         currentFolder = (EmailFolderBase)currentFolder.getParentFolder();
         if (currentFolder == parent) {
            exceptionToLog = "FCE";
            break;
         }
      }

      if (exceptionToLog != null) {
         try {
            throw new Throwable(exceptionToLog);
         } finally {
            QuincyManager.sendJavaLogworthy("EmailFolder:FolderCycle");
            return false;
         }
      } else {
         if (this._parent != null) {
            this._parent.removeFolder(this);
            this.getEmailHierarchy().unregisterFolderInCache(this);
         }

         this._parent = parent;
         this.getEmailHierarchy().registerFolderInCache(this);
         if (parent != null) {
            parent.putFolder(this);
         }

         return true;
      }
   }

   public boolean folderAttributesFlagsSet(int flags) {
      return (this._attributes & flags) != 0;
   }

   @Override
   public int getUID() {
      return this._desktopUID;
   }

   public void clearFolderAttributesFlags(int mask) {
      this._attributes &= ~mask;
   }

   public void setFolderAttributesFlags(int mask) {
      this._attributes |= mask;
   }

   public EmailFolder findFolderById(int id) {
      if (id == this._folderId) {
         return this;
      }

      if (this._parent != null) {
         return this.getEmailHierarchy().findFolderById(id);
      }

      EmailFolder foundFolder = null;

      for (EmailFolder currentFolder = this._child; currentFolder != null && foundFolder == null; currentFolder = currentFolder._sibling) {
         foundFolder = currentFolder.findFolderById(id);
      }

      return foundFolder;
   }

   void removeFromSiblingChain(EmailFolder folder) {
      EmailFolder previousFolder = null;

      for (EmailFolder currentFolder = this._sibling; currentFolder != null; currentFolder = currentFolder._sibling) {
         if (currentFolder == folder) {
            if (previousFolder == null) {
               this._sibling = currentFolder._sibling;
            } else {
               previousFolder._sibling = currentFolder._sibling;
            }

            currentFolder._sibling = null;
            break;
         }

         previousFolder = currentFolder;
      }

      if (folder._sibling != null) {
         try {
            throw new RuntimeException("Sibling should have been null");
         } finally {
            folder._sibling = null;
            return;
         }
      }
   }

   protected void setInFolderDatabase(boolean value) {
      this._inFolderDatabase = value;
   }

   public void setFriendlyName(String friendlyName) {
      synchronized (FolderHierarchies.getLockObject()) {
         this._friendlyName = friendlyName;
      }
   }

   public void setFolderType(int type) {
      this._folderType = type;
   }

   protected void setFolderId(int folderId) {
      this._folderId = folderId;
      this._luid = EmailHierarchy.makeFolderLUID(this.getEmailHierarchy().getLUID(), this._folderId);
   }

   @Override
   public Collection getContainedItems() {
      switch (this._folderType) {
         case 79:
            return null;
         case 80:
         case 81:
         case 82:
         default:
            return EmailHierarchy.getCollection(this._folderId, this._luid, this);
      }
   }

   @Override
   public Folder getBaseFolder() {
      return this.getEmailHierarchy().getBaseFolder();
   }

   @Override
   public boolean canContainItems() {
      return this._inFolderDatabase && (this.getFolderAttributes() & 2) == 0;
   }

   @Override
   public boolean canFile(Object object) {
      switch (this._folderType) {
         case 1:
         case 4:
            return false;
         default:
            return true;
      }
   }

   @Override
   public boolean isVisible(Object context) {
      return this._inFolderDatabase;
   }

   @Override
   public int getFolderId() {
      return this._folderId;
   }

   @Override
   public boolean containsSubFolders() {
      return this._child != null;
   }

   @Override
   public int getFolderType() {
      return this._folderType;
   }

   @Override
   public void removeMessagesFromSubtree(boolean includeThis) {
      ContextObject context = new ContextObject(111);
      this.removeMessagesFromSubtree(includeThis, context);
   }

   @Override
   public void commitSubtree(boolean includeThis) {
      if (includeThis) {
         label46:
         try {
            ((PersistedSortedCollection)this.getContainedItems()).commitFolder();
         } finally {
            break label46;
         }
      }

      synchronized (FolderHierarchies.getLockObject()) {
         for (EmailFolder currentFolder = this._child; currentFolder != null; currentFolder = currentFolder._sibling) {
            currentFolder.commitSubtree(true);
         }
      }
   }

   @Override
   public int getActiveFolders(Object[] folders, int index) {
      synchronized (FolderHierarchies.getLockObject()) {
         int count = 0;
         if (this.isInFolderDatabase()) {
            Array.resize(folders, folders.length + 1);
            folders[index + count] = this;
            count++;
         }

         for (EmailFolder currentFolder = this._child; currentFolder != null; currentFolder = currentFolder._sibling) {
            count += currentFolder.getActiveFolders(folders, index + count);
         }

         return count;
      }
   }

   @Override
   public boolean isInFolderDatabase() {
      return this._inFolderDatabase;
   }

   @Override
   public EmailHierarchy getEmailHierarchy() {
      return this._parent.getEmailHierarchy();
   }

   @Override
   public boolean removeIfPossible() {
      if (this.containsSubFolders() || this.isInFolderDatabase()) {
         return false;
      }

      if (this._isSticky) {
         EmailHierarchy hierarchy = this.getEmailHierarchy();
         EmailFolderBase oldParent = this._parent;
         if (this.setParentFolder(hierarchy)) {
            if (oldParent != hierarchy) {
               oldParent.removeIfPossible();
            }

            return hierarchy.removeIfPossible();
         } else {
            return false;
         }
      } else {
         this.getEmailHierarchy().unregisterFolderInCache(this);
         this._parent.removeFolder(this);
         this._parent.removeIfPossible();
         return true;
      }
   }

   @Override
   public int getActiveFolderCount() {
      synchronized (FolderHierarchies.getLockObject()) {
         int count = 0;
         if (this.isInFolderDatabase()) {
            count++;
         }

         for (EmailFolder currentFolder = this._child; currentFolder != null; currentFolder = currentFolder._sibling) {
            count += currentFolder.getActiveFolderCount();
         }

         return count;
      }
   }

   @Override
   public void putFolder(EmailFolder folder) {
      if (folder._parent != this) {
         EventLogger.logEvent(-1237457833540244999L, 541478985, 2);
      }

      this.addSubFolder(folder);
      PersistentObject.commit(this._child);
   }

   @Override
   public void removeFolder(EmailFolder folder) {
      this.removeSubFolder(folder);
      PersistentObject.commit(this);
   }

   @Override
   public Enumeration getSubFolders() {
      Object[] children = new Object[this.getSubFolderCount()];
      int i = 0;

      for (EmailFolder curr = this._child; curr != null; curr = curr._sibling) {
         children[i++] = curr;
      }

      return new ObjectEnumerator(children);
   }

   @Override
   public Folder getParentFolder() {
      return this._parent;
   }

   @Override
   public Folder getFolder(long folderUid) {
      if (this._parent != null) {
         return this.getEmailHierarchy().getFolder(folderUid);
      }

      Folder foundFolder = null;

      for (EmailFolder currentFolder = this._child; currentFolder != null && foundFolder == null; currentFolder = currentFolder._sibling) {
         foundFolder = currentFolder.getFolder(folderUid);
      }

      return foundFolder;
   }

   @Override
   public long getLUID() {
      return this._luid;
   }

   @Override
   public void setFolderAttributes(int attributes) {
      this._attributes = attributes;
   }

   @Override
   public int getFolderAttributes() {
      return this._attributes;
   }

   @Override
   public String getServiceContentIdentifier() {
      return "CMIME";
   }

   @Override
   public int getServiceUserId() {
      return this.getEmailHierarchy().getServiceUserId();
   }

   @Override
   public int getServiceUidHash() {
      return this.getEmailHierarchy().getServiceUidHash();
   }

   @Override
   public int getServiceNameHash() {
      return this.getEmailHierarchy().getServiceNameHash();
   }

   @Override
   public String getFriendlyName() {
      return this.localizeFriendlyName(this._friendlyName);
   }

   public EmailFolder(int folderId, int type, long luid, boolean isSticky, String friendlyName) {
      this._luid = luid;
      this._folderId = folderId;
      this._folderType = type;
      this._isSticky = isSticky;
      this._friendlyName = friendlyName;
   }

   private ServiceRecord getServiceRecord() {
      if (this._parent == null) {
         return null;
      }

      EmailHierarchy emailHierarchy = this.getEmailHierarchy();
      return emailHierarchy == null
         ? null
         : ServiceBook.getSB()
            .getRecordByCidAndUserId("CMIME", emailHierarchy.getServiceUserId(), emailHierarchy.getServiceNameHash(), emailHierarchy.getServiceUidHash());
   }

   private void removeSubFolder(EmailFolder folder) {
      synchronized (FolderHierarchies.getLockObject()) {
         if (this._child == folder) {
            this._child = folder._sibling;
            folder._sibling = null;
         } else if (this._child != null) {
            EmailFolder previousFolder = this._child;

            for (EmailFolder currentFolder = this._child._sibling; currentFolder != null; currentFolder = currentFolder._sibling) {
               if (currentFolder == folder) {
                  previousFolder._sibling = currentFolder._sibling;
                  currentFolder._sibling = null;
                  return;
               }

               previousFolder = currentFolder;
            }
         }
      }
   }

   private int getSubFolderCount() {
      int count = 0;

      for (EmailFolder curr = this._child; curr != null; curr = curr._sibling) {
         count++;
      }

      return count;
   }

   private void removeMessagesFromSubtree(boolean includeThis, Object context) {
      if (includeThis) {
         label46:
         try {
            Collection c = this.getContainedItems();
            ((IntRangedActionTarget)c).apply(Integer.MIN_VALUE, Integer.MAX_VALUE, -198247372487919817L, context);
            ((WritableSet)c).removeAll();
         } finally {
            break label46;
         }
      }

      synchronized (FolderHierarchies.getLockObject()) {
         for (EmailFolder currentFolder = this._child; currentFolder != null; currentFolder = currentFolder._sibling) {
            currentFolder.removeMessagesFromSubtree(true, context);
         }
      }
   }

   private void addSubFolder(EmailFolder folder) {
      long luid = folder.getLUID();
      synchronized (FolderHierarchies.getLockObject()) {
         if (this._child == null) {
            this._child = folder;
         } else {
            EmailFolder currentFolder = this._child;
            if (luid == currentFolder.getLUID()) {
               return;
            }

            while (currentFolder._sibling != null) {
               currentFolder = currentFolder._sibling;
               if (luid == currentFolder.getLUID()) {
                  return;
               }
            }

            currentFolder._sibling = folder;
         }
      }
   }

   private short getServiceIdentifierSubType() {
      ServiceRecord sr = this.getServiceRecord();
      return sr == null ? 0 : sr.getServiceIdentifierSubType();
   }

   private String localizeFriendlyName(String name) {
      int typeIndex = 0;
      byte var6;
      switch (this._folderType) {
         case 0:
            return name;
         case 1:
            var6 = 3;
            break;
         case 2:
         default:
            var6 = 0;
            break;
         case 3:
            var6 = 1;
            break;
         case 4:
            var6 = 2;
      }

      int serverIndex = 0;
      short besType = this.getServiceIdentifierSubType();
      byte var7;
      switch (besType) {
         case 0:
            return name;
         case 1:
         default:
            var7 = 0;
            break;
         case 2:
            var7 = 1;
            break;
         case 3:
            var7 = 2;
      }

      int resId = FOLDER_NAME_IDS[var7][var6];
      return EmailResources.getString(resId);
   }
}
