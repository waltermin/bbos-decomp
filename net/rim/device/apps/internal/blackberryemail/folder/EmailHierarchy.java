package net.rim.device.apps.internal.blackberryemail.folder;

import java.util.Enumeration;
import java.util.Vector;
import net.rim.device.api.collection.Collection;
import net.rim.device.api.collection.LongKeyProviderAdaptor;
import net.rim.device.api.collection.ReadableList;
import net.rim.device.api.collection.WritableSet;
import net.rim.device.api.servicebook.ServiceBook;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.RIMPersistentStore;
import net.rim.device.api.util.Comparator;
import net.rim.device.api.util.IntHashtable;
import net.rim.device.api.util.LongHashtable;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.FolderProvider;
import net.rim.device.apps.api.messaging.Folder;
import net.rim.device.apps.api.messaging.FolderHierarchies;
import net.rim.device.apps.api.messaging.FolderMerge;
import net.rim.device.apps.api.messaging.util.PersistedSortedCollection;
import net.rim.device.apps.api.messaging.util.PersistedSortedCollectionEncryptor;
import net.rim.device.apps.api.messaging.util.SortedCollection;
import net.rim.device.apps.api.utility.lowMemory.PurgeManager;
import net.rim.device.apps.internal.blackberryemail.email.EmailMessageModel;
import net.rim.device.apps.internal.blackberryemail.resources.EmailResources;
import net.rim.vm.Array;

public class EmailHierarchy implements EmailFolderBase, Folder {
   private long _luid;
   private EmailFolder[] _subFolders;
   private EmailHierarchy$SingleMapObjectForCommit _singleMapObjectForCommit = new EmailHierarchy$SingleMapObjectForCommit(this);
   private int _userID;
   private String _friendlyName;
   private int _uidHash;
   private int _nameHash;
   private String _cid;
   private int _folderId;
   private boolean _inFolderDatabase;
   private int _desktopUID;
   private int _attributes;
   private long _missingFolderTimestamp;
   private EmailFolder _orphanedSavedFolder;
   private EmailFolder _unfiledFolder;
   private EmailFolder _filedFolder;
   private EmailFolder _inboxFolder;
   private EmailFolder _sentFolder;
   private EmailFolder _deletedFolder;
   private IntHashtable _refIdToMessageTable;
   private GhostMessageData _ghostData = new GhostMessageData();
   private static final long FOLDER_KEY = 4848635152656490553L;
   private static final long COMPARATOR_KEY = -7100114292399893306L;
   private static final long EMAIL_FAMILY = 283552684730285077L;
   private static final long EMAIL_FOLDER_LISTENERS_KEY = -8357190046905569807L;
   private static EmailHierarchy[] _hierarchies;
   private static PersistentObject _persistentObject = RIMPersistentStore.getPersistentObject(4848635152656490553L);
   private static LongHashtable _folderCollections;
   private static Comparator _longKeyProviderAdaptorComparator;
   private static Vector _emailFolderListeners;
   private static final long PREVERIFYLISTENERS_ID = 1684962353143705358L;
   private static Vector _preverifyListeners;
   static final int MIN_PURGE_SIZE = 50;

   public Object getGhostMessageLock() {
      return this._ghostData.getLock();
   }

   public GhostMessageData getGhostMessageData() {
      return this._ghostData;
   }

   public void addGhostMessageReference(int referenceTag, int info) {
      ServiceBook sb = ServiceBook.getSB();
      ServiceRecord sr = null;
      if (this._userID != -1) {
         sr = sb.getRecordByCidAndUserId(this._cid, this._userID);
      } else {
         sr = sb.getRecordByCidAndHash(this._cid, this._nameHash, this._uidHash);
      }

      this._ghostData.add(referenceTag, info, this._folderId, sr);
   }

   @Override
   public int getUID() {
      return this._desktopUID;
   }

   public int getGhostMessageTag(int index) {
      return this._ghostData.getTagAt(index);
   }

   public int getGhostMessageInfo(int index) {
      return this._ghostData.getInfoAt(index);
   }

   public void removeGhostMessageReference(int referenceTag) {
      this._ghostData.remove(referenceTag);
   }

   public void removeAllGhostMessageReferences() {
      this._ghostData.removeAll();
   }

   void registerFolderInCache(EmailFolder folder) {
      this._singleMapObjectForCommit._folderIdToFolderMap.put(folder.getFolderId(), folder);
      this._singleMapObjectForCommit._folderLuidToFolderMap.put(folder.getLUID(), folder);
      PersistentObject.commit(this._singleMapObjectForCommit);
   }

   void unregisterFolderInCache(EmailFolder folder) {
      int folderId = folder.getFolderId();
      long folderLuid = folder.getLUID();
      boolean changed = false;
      EmailFolder oldFolder = (EmailFolder)this._singleMapObjectForCommit._folderIdToFolderMap.get(folderId);
      if (oldFolder == folder) {
         this._singleMapObjectForCommit._folderIdToFolderMap.remove(folderId);
         changed = true;
      }

      oldFolder = (EmailFolder)this._singleMapObjectForCommit._folderLuidToFolderMap.get(folderLuid);
      if (oldFolder == folder) {
         this._singleMapObjectForCommit._folderLuidToFolderMap.remove(folderLuid);
         changed = true;
      }

      if (changed) {
         PersistentObject.commit(this._singleMapObjectForCommit);
      }
   }

   public long getSentFolder() {
      return this._sentFolder.getLUID();
   }

   public long getInboxFolder() {
      return this._inboxFolder.getLUID();
   }

   public EmailFolder getUnfiledFolder() {
      return this._unfiledFolder;
   }

   public EmailFolder getFiledFolder() {
      return this._filedFolder;
   }

   public EmailFolder getOrphanedSavedFolder() {
      return this._orphanedSavedFolder;
   }

   public EmailFolder getDeletedFolder() {
      return this._deletedFolder;
   }

   public void ensureFiledMessagesInFiledCollection() {
      if (this._missingFolderTimestamp != -1) {
         ((Thread)(new Object(new EmailHierarchy$MoveFiledMessagesWorker(this, null)))).start();
      }
   }

   public void updateMissingFolderTimestamp(long time) {
      if (this._missingFolderTimestamp == -1 || time < this._missingFolderTimestamp) {
         this._missingFolderTimestamp = time;
      }
   }

   public EmailMessageModel getMessage(int refid) {
      return (EmailMessageModel)this._refIdToMessageTable.get(refid);
   }

   void removeTrackingInfo(Object message) {
      this.removeTrackingInfo(message, true);
   }

   void removeTrackingInfo(Object message, boolean commit) {
      if (message instanceof EmailMessageModel) {
         EmailMessageModel model = (EmailMessageModel)message;
         int refid = model.getCMIMEReferenceIdentifier();
         if (this._refIdToMessageTable.get(refid) != null) {
            this._refIdToMessageTable.remove(refid);
            if (commit) {
               PersistentObject.commit(this._refIdToMessageTable);
            }
         }
      }
   }

   void addTrackingInfo(Object message) {
      if (message instanceof EmailMessageModel) {
         EmailMessageModel model = (EmailMessageModel)message;
         this._refIdToMessageTable.put(model.getCMIMEReferenceIdentifier(), model);
         PersistentObject.commit(this._refIdToMessageTable);
      }
   }

   public long getEmailFolder(int id) {
      if (id == 0) {
         return this._inboxFolder.getLUID();
      }

      EmailFolder f = this.findFolderById(id);
      return f != null ? f.getLUID() : makeFolderLUID(this._luid, id);
   }

   public long getEmailFolder(int id, int type) {
      switch (type) {
         case 2:
            return this._inboxFolder.getLUID();
         case 4:
            return this._sentFolder.getLUID();
         case 80:
            return this._orphanedSavedFolder.getLUID();
         default:
            return this.getEmailFolder(id);
      }
   }

   public EmailFolderBase establishEmailFolder(int id, int parentId, int type, String name, int desktopUID) {
      synchronized (FolderHierarchies.getLockObject()) {
         EmailFolder folder = this.findFolderById(id);
         if (id == 0 && type == 0) {
            this._folderId = id;
            this._friendlyName = name;
            this._desktopUID = desktopUID;
            this.setInFolderDatabase(true);
            if (folder != null) {
               this.moveSubFolders(folder, this);
               folder.setInFolderDatabase(false);
               folder.removeIfPossible();
            }

            PersistentObject.commit(this);
            return this;
         } else {
            EmailFolder stickyFolder = null;
            switch (type) {
               case 2:
                  stickyFolder = this._inboxFolder;
                  break;
               case 4:
                  stickyFolder = this._sentFolder;
                  break;
               case 80:
                  stickyFolder = this._orphanedSavedFolder;
                  break;
               case 81:
                  stickyFolder = this._unfiledFolder;
                  break;
               case 82:
                  stickyFolder = this._filedFolder;
            }

            if (stickyFolder != null && stickyFolder != folder) {
               if (stickyFolder.getFolderId() != id) {
                  this.unregisterFolderInCache(stickyFolder);
                  stickyFolder.setFolderId(id);
                  this.registerFolderInCache(stickyFolder);
               }

               if (folder != null) {
                  this.adjustFolderLuidsForAllMessages(folder.getLUID(), stickyFolder.getLUID());
                  this.moveSubFolders(folder, stickyFolder);
                  folder.setInFolderDatabase(false);
                  folder.removeIfPossible();
               }

               folder = stickyFolder;
            }

            if (folder == null) {
               folder = this.makeNewFolder(id, type, false, false, name);
            } else {
               if (!name.equals(folder.getFriendlyName())) {
                  folder.setFriendlyName(name);
               }

               if (type != folder.getFolderType()) {
                  folder.setFolderType(type);
               }
            }

            if (folder.getUID() != desktopUID) {
               folder.setDesktopUID(desktopUID);
            }

            if (!folder.isInFolderDatabase()) {
               folder.setInFolderDatabase(true);
            }

            if (type == 1 && folder != this._deletedFolder) {
               this._deletedFolder = folder;
            }

            EmailFolderBase parent;
            if (parentId == this._folderId) {
               parent = this;
            } else {
               parent = this.findFolderById(parentId);
            }

            if (parent == null) {
               parent = this.makeNewFolder(parentId, 5, true, false, EmailResources.getString(73));
            }

            folder.setParentFolder(parent);
            PersistentObject.commit(this);
            PersistentObject.commit(folder);

            for (int i = _emailFolderListeners.size() - 1; i >= 0; i--) {
               EmailFolderListener efl = (EmailFolderListener)_emailFolderListeners.elementAt(i);
               if (efl != null) {
                  efl.folderAdded(folder);
               }
            }

            return folder;
         }
      }
   }

   public void disestablishEmailFolder(EmailFolderBase f) {
      if (f == this) {
         this.setInFolderDatabase(false);
         if (!this.removeIfPossible()) {
            PersistentObject.commit(this);
         }
      } else {
         EmailFolder ef = (EmailFolder)f;

         for (int i = _emailFolderListeners.size() - 1; i >= 0; i--) {
            EmailFolderListener efl = (EmailFolderListener)_emailFolderListeners.elementAt(i);
            if (efl != null) {
               efl.folderRemoved(ef);
            }
         }

         if (ef.isInFolderDatabase()) {
            ef.setInFolderDatabase(false);
            if (!ef.removeIfPossible()) {
               PersistentObject.commit(ef);
            }
         }
      }
   }

   public boolean canPurge(EmailMessageModel message) {
      ReadableList collection = (ReadableList)(message.flagsSet(2) ? this._filedFolder : this._unfiledFolder).getContainedItems();
      return collection.size() > 50;
   }

   public void updateEmailFolder(EmailFolder ef) {
      for (int i = _emailFolderListeners.size() - 1; i >= 0; i--) {
         EmailFolderListener efl = (EmailFolderListener)_emailFolderListeners.elementAt(i);
         if (efl != null) {
            efl.folderUpdated(ef);
         }
      }
   }

   public void addMessagesToFolderMerge(long mergeId) {
      FolderMerge.registerMergedFolder(mergeId, this._unfiledFolder);
      FolderMerge.registerMergedFolder(mergeId, this._filedFolder);
      FolderMerge.registerMergedFolder(mergeId, this._orphanedSavedFolder);
   }

   public int getMessageCount() {
      ReadableList readableList = (ReadableList)this._unfiledFolder.getContainedItems();
      int count = readableList.size();
      readableList = (ReadableList)this._filedFolder.getContainedItems();
      count += readableList.size();
      readableList = (ReadableList)this._orphanedSavedFolder.getContainedItems();
      return count + readableList.size();
   }

   public int getNumberOfGhostMessages() {
      return this._ghostData.size();
   }

   public void setServiceContentIdentifier(String cid) {
      this._cid = cid;
   }

   protected void deregister() {
      FolderHierarchies.deregisterFolderHierarchy(this);
      FolderMerge.deregisterMergedFolder(6368823655991217730L, this._orphanedSavedFolder);
      FolderMerge.deregisterMergedFolder(2993144521330132876L, this._unfiledFolder);
      FolderMerge.deregisterMergedFolder(-5581791943352753293L, this._unfiledFolder);
      FolderMerge.deregisterMergedFolder(-5581791943352753293L, this._filedFolder);
      FolderMerge.deregisterMergedFolder(FolderMerge.getMergeCollectionId(this.getLUID(), 734877078), this._unfiledFolder);
      FolderMerge.deregisterMergedFolder(FolderMerge.getMergeCollectionId(this.getLUID(), -271343505), this._unfiledFolder);
      FolderMerge.deregisterMergedFolder(FolderMerge.getMergeCollectionId(this.getLUID(), -271343505), this._filedFolder);
      FolderMerge.deregisterMergedFolder(7509894771240321003L, this._unfiledFolder);
      FolderMerge.deregisterMergedFolder(7509894771240321003L, this._filedFolder);
      PurgeManager.getInstance().removeCollection((Collection)this._unfiledFolder.getContainedItems());
      PurgeManager.getInstance().removeCollection((Collection)this._filedFolder.getContainedItems());
      PurgeManager.getInstance().removeCollection((Collection)this._orphanedSavedFolder.getContainedItems());
   }

   protected void register() {
      FolderHierarchies.registerFolderHierarchy(this);
      FolderMerge.registerMergedFolder(6368823655991217730L, this._orphanedSavedFolder);
      FolderMerge.registerMergedFolder(2993144521330132876L, this._unfiledFolder);
      FolderMerge.registerMergedFolder(-5581791943352753293L, this._unfiledFolder);
      FolderMerge.registerMergedFolder(-5581791943352753293L, this._filedFolder);
      FolderMerge.registerMergedFolder(FolderMerge.getMergeCollectionId(this.getLUID(), 734877078), this._unfiledFolder);
      FolderMerge.registerMergedFolder(FolderMerge.getMergeCollectionId(this.getLUID(), -271343505), this._unfiledFolder);
      FolderMerge.registerMergedFolder(FolderMerge.getMergeCollectionId(this.getLUID(), -271343505), this._filedFolder);
      FolderMerge.registerMergedFolder(7509894771240321003L, this._unfiledFolder);
      FolderMerge.registerMergedFolder(7509894771240321003L, this._filedFolder);
      FolderMerge.registerMergedFolder(7509894771240321003L, this._orphanedSavedFolder);
      PurgeManager.getInstance().addCollection((Collection)this._unfiledFolder.getContainedItems());
      PurgeManager.getInstance().addCollection((Collection)this._filedFolder.getContainedItems());
      PurgeManager.getInstance().addCollection((Collection)this._orphanedSavedFolder.getContainedItems());
   }

   public int getFolderIdForFolder(long folderLUID) {
      EmailFolder folder = (EmailFolder)this.getFolder(folderLUID);
      return folder != null ? folder.getFolderId() : (int)(folderLUID & 4294967295L);
   }

   public EmailFolder findFolderById(int id) {
      return (EmailFolder)this._singleMapObjectForCommit._folderIdToFolderMap.get(id);
   }

   protected void setInFolderDatabase(boolean value) {
      this._inFolderDatabase = value;
   }

   protected void setFolderId(int id) {
      this._folderId = id;
   }

   public void setFriendlyName(String friendlyName) {
      this._friendlyName = friendlyName;
   }

   protected void setFolderType(int type) {
   }

   @Override
   public int getFolderType() {
      return 0;
   }

   @Override
   public int getFolderId() {
      return this._folderId;
   }

   @Override
   public boolean isInFolderDatabase() {
      return this._inFolderDatabase;
   }

   @Override
   public EmailHierarchy getEmailHierarchy() {
      return this;
   }

   @Override
   public boolean removeIfPossible() {
      if (this._nameHash == -1 || this._uidHash == -1 || this.isInFolderDatabase()) {
         return false;
      }

      if (!allFoldersEmpty(this)) {
         return false;
      }

      ReadableList readableList = (ReadableList)this._unfiledFolder.getContainedItems();
      if (readableList.size() > 0) {
         return false;
      }

      readableList = (ReadableList)this._filedFolder.getContainedItems();
      if (readableList.size() > 0) {
         return false;
      }

      readableList = (ReadableList)this._orphanedSavedFolder.getContainedItems();
      if (readableList.size() > 0) {
         return false;
      }

      ServiceBook sb = ServiceBook.getSB();
      ServiceRecord sr = sb.getRecordByCidAndUserId(this._cid, this._userID, this._nameHash, this._uidHash);
      if (sr != null) {
         return false;
      }

      this.deregister();
      synchronized (FolderHierarchies.getLockObject()) {
         EmailHierarchy[] hierarchies = _hierarchies;
         int num = hierarchies.length;

         for (int i = 0; i < num; i++) {
            if (hierarchies[i] == this) {
               System.arraycopy(hierarchies, i + 1, hierarchies, i, num - i - 1);
               Array.resize(hierarchies, num - 1);
               PersistentObject.commit(hierarchies);
               break;
            }
         }

         return true;
      }
   }

   @Override
   public void putFolder(EmailFolder folder) {
      synchronized (FolderHierarchies.getLockObject()) {
         this.addSubFolder(folder);
         PersistentObject.commit(this._subFolders);
      }
   }

   @Override
   public void removeFolder(EmailFolder folder) {
      synchronized (FolderHierarchies.getLockObject()) {
         this.removeSubFolder(folder);
         PersistentObject.commit(this._subFolders);
      }
   }

   @Override
   public Folder getBaseFolder() {
      return this._unfiledFolder;
   }

   @Override
   public Collection getContainedItems() {
      return null;
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
   public boolean containsSubFolders() {
      return this._subFolders.length > 0;
   }

   @Override
   public Enumeration getSubFolders() {
      synchronized (FolderHierarchies.getLockObject()) {
         int length = this._subFolders.length;
         Object[] objects = new Object[length];
         System.arraycopy(this._subFolders, 0, objects, 0, length);
         return (Enumeration)(new Object(objects));
      }
   }

   @Override
   public String getServiceContentIdentifier() {
      return this._cid;
   }

   @Override
   public Folder getParentFolder() {
      return null;
   }

   @Override
   public int getServiceUidHash() {
      return this._uidHash;
   }

   @Override
   public int getServiceUserId() {
      return this._userID;
   }

   @Override
   public int getServiceNameHash() {
      return this._nameHash;
   }

   @Override
   public Folder getFolder(long folderLUID) {
      long hierarchyUID = getHierarchyLUIDFromFolderLUID(folderLUID);
      if (hierarchyUID != -4294967296L && hierarchyUID != 0 && this._luid != hierarchyUID) {
         return null;
      }

      Folder folder = (Folder)this._singleMapObjectForCommit._folderLuidToFolderMap.get(folderLUID);
      if (folder == null) {
         folder = (Folder)this._singleMapObjectForCommit._folderIdToFolderMap.get((int)(folderLUID & 4294967295L));
      }

      return folder;
   }

   @Override
   public boolean isVisible(Object context) {
      return ContextObject.getFlag(context, 22) ? this._userID != -1 || this._uidHash != -1 || this._nameHash != -1 : true;
   }

   @Override
   public String getFriendlyName() {
      return this._friendlyName;
   }

   @Override
   public long getLUID() {
      return this._luid;
   }

   @Override
   public boolean canContainItems() {
      return this._inFolderDatabase && (this.getFolderAttributes() & 2) == 0;
   }

   @Override
   public void removeMessagesFromSubtree(boolean includeThis) {
      synchronized (FolderHierarchies.getLockObject()) {
         this._unfiledFolder.removeMessagesFromSubtree(true);
         this._filedFolder.removeMessagesFromSubtree(true);
         this._orphanedSavedFolder.removeMessagesFromSubtree(true);
      }
   }

   @Override
   public void commitSubtree(boolean includeThis) {
      synchronized (FolderHierarchies.getLockObject()) {
         this._unfiledFolder.commitSubtree(true);
         this._filedFolder.commitSubtree(true);
         this._orphanedSavedFolder.commitSubtree(true);
      }
   }

   @Override
   public int getActiveFolders(Object[] folders, int index) {
      synchronized (FolderHierarchies.getLockObject()) {
         int count = 0;
         int length = this._subFolders.length;
         Array.resize(folders, folders.length + length + 1);
         if (this.isInFolderDatabase()) {
            folders[index + count] = this;
            count++;
         }

         for (int i = length - 1; i >= 0; i--) {
            count += this._subFolders[i].getActiveFolders(folders, index + count);
         }

         return count;
      }
   }

   @Override
   public int getActiveFolderCount() {
      synchronized (FolderHierarchies.getLockObject()) {
         int count = 0;
         if (this.isInFolderDatabase()) {
            count++;
         }

         int length = this._subFolders.length;

         for (int i = length - 1; i >= 0; i--) {
            count += this._subFolders[i].getActiveFolderCount();
         }

         return count;
      }
   }

   public static EmailFolder getEmailFolder(long folderUid) {
      EmailHierarchy emailHierarchy = getEmailHierarchyForFolder(folderUid);
      return emailHierarchy == null ? null : (EmailFolder)emailHierarchy.getFolder(folderUid);
   }

   public static EmailHierarchy getEmailHierarchy(long hierarchyLuid) {
      synchronized (FolderHierarchies.getLockObject()) {
         for (int i = _hierarchies.length - 1; i >= 0; i--) {
            EmailHierarchy hierarchy = _hierarchies[i];
            if (hierarchy._luid == hierarchyLuid) {
               return hierarchy;
            }
         }

         return null;
      }
   }

   public static EmailHierarchy getEmailHierarchyForFolder(long folderLuid) {
      synchronized (FolderHierarchies.getLockObject()) {
         long hierarchyLUID = getHierarchyLUIDFromFolderLUID(folderLuid);
         return getEmailHierarchy(hierarchyLUID);
      }
   }

   private void adjustFolderLuidsForMessagesInFolder(Folder folder, long sourceLuid, long destinationLuid) {
      synchronized (FolderHierarchies.getLockObject()) {
         ReadableList collection = (ReadableList)folder.getContainedItems();

         for (int i = collection.size() - 1; i >= 0; i--) {
            Object o = collection.getAt(i);

            try {
               FolderProvider folderProvider = (FolderProvider)o;
               long currentLuid = folderProvider.getFolderId();
               if (currentLuid == sourceLuid) {
                  folderProvider.setFolderId(destinationLuid);
                  PersistentObject.commit(o);
               }
            } finally {
               continue;
            }
         }
      }
   }

   private void adjustFolderLuidsForAllMessages(long sourceLuid, long destinationLuid) {
      synchronized (FolderHierarchies.getLockObject()) {
         this.adjustFolderLuidsForMessagesInFolder(this._unfiledFolder, sourceLuid, destinationLuid);
         this.adjustFolderLuidsForMessagesInFolder(this._filedFolder, sourceLuid, destinationLuid);
      }
   }

   public static Collection getStorageCollection(long folderId, boolean filed) {
      EmailHierarchy hierarchy = getEmailHierarchyForFolder(folderId);
      if (hierarchy != null) {
         EmailFolder folder = (EmailFolder)hierarchy.getFolder(folderId);
         return hierarchy.getStorageCollection(folder, filed);
      } else {
         return null;
      }
   }

   private void setServiceUserId(int userID) {
      this._userID = userID;
   }

   public static void removeEmailFolderListener(EmailFolderListener efl) {
      synchronized (_emailFolderListeners) {
         _emailFolderListeners.removeElement(efl);
      }
   }

   static void removeCollection(long folderId) {
      synchronized (FolderHierarchies.getLockObject()) {
         PersistedSortedCollection psc = (PersistedSortedCollection)_folderCollections.get(folderId);
         if (psc != null) {
            _folderCollections.remove(folderId);
            psc.destroy();
         }
      }
   }

   public static void addEmailFolderListener(EmailFolderListener efl) {
      synchronized (_emailFolderListeners) {
         if (!_emailFolderListeners.contains(efl)) {
            _emailFolderListeners.addElement(efl);
         }
      }
   }

   private void removeSubFolder(EmailFolder folder) {
      long luid = folder.getLUID();
      synchronized (FolderHierarchies.getLockObject()) {
         int length = this._subFolders.length;

         for (int i = length - 1; i >= 0; i--) {
            if (luid == this._subFolders[i].getLUID()) {
               this._subFolders[i] = this._subFolders[length - 1];
               Array.resize(this._subFolders, length - 1);
               break;
            }
         }

         for (int i = this._subFolders.length - 1; i >= 0; i--) {
            this._subFolders[i].removeFromSiblingChain(folder);
         }
      }
   }

   private void addSubFolder(EmailFolder folder) {
      long luid = folder.getLUID();
      synchronized (FolderHierarchies.getLockObject()) {
         int length = this._subFolders.length;

         for (int i = length - 1; i >= 0; i--) {
            if (luid == this._subFolders[i].getLUID()) {
               return;
            }
         }

         Array.resize(this._subFolders, length + 1);
         this._subFolders[length] = folder;
      }
   }

   static SortedCollection getCollection(int folderId, long luid, Object context) {
      synchronized (FolderHierarchies.getLockObject()) {
         SortedCollection result = (SortedCollection)_folderCollections.get(folderId);
         if (result == null) {
            result = new PersistedSortedCollectionHook(luid);
            result.initialize(283552684730285077L, folderId, _longKeyProviderAdaptorComparator, context);
            _folderCollections.put(folderId, result);
         }

         return result;
      }
   }

   private EmailFolder makeNewFolder(int id, int type, boolean addToRoot, boolean isSticky, String name) {
      EmailFolder folder = new EmailFolder(id, type, makeFolderLUID(this._luid, id), isSticky, name);
      if (addToRoot) {
         folder.setParentFolder(this);
      }

      return folder;
   }

   public static int getEmailHierarchyCount() {
      synchronized (FolderHierarchies.getLockObject()) {
         return _hierarchies.length;
      }
   }

   public static EmailHierarchy getEmailHierarchy(int index) {
      synchronized (FolderHierarchies.getLockObject()) {
         return _hierarchies[index];
      }
   }

   private static boolean allFoldersEmpty(Folder folder) {
      Enumeration enumeration = folder.getSubFolders();

      while (enumeration.hasMoreElements()) {
         EmailFolderBase sub = (EmailFolderBase)enumeration.nextElement();
         if (sub.isInFolderDatabase()) {
            return false;
         }

         if (!allFoldersEmpty(sub)) {
            return false;
         }
      }

      return true;
   }

   public static void purge() {
      synchronized (FolderHierarchies.getLockObject()) {
         int num = _hierarchies.length;

         while (--num >= 0) {
            _hierarchies[num].removeIfPossible();
         }
      }
   }

   public static EmailHierarchy establishEmailHierarchy(int userID, int uidHash, int nameHash, String name) {
      EmailHierarchy hierarchy = getEmailHierarchy(userID, uidHash, nameHash, true);
      boolean modified = false;
      if (userID != hierarchy.getServiceUserId()) {
         hierarchy.setServiceUserId(userID);
         modified = true;
      }

      if (uidHash != hierarchy.getServiceUidHash()) {
         hierarchy.setServiceUidHash(uidHash);
         modified = true;
      }

      if (nameHash != hierarchy.getServiceNameHash()) {
         hierarchy.setServiceNameHash(nameHash);
         modified = true;
      }

      if (name != null && !name.equals(hierarchy.getFriendlyName())) {
         hierarchy.setFriendlyName(name);
         modified = true;
      }

      if (modified) {
         PersistentObject.commit(hierarchy);
      }

      return hierarchy;
   }

   public static void fileMessage(EmailMessageModel message, long oldFolderLuid, long newFolderLuid) {
      EmailHierarchy newHierarchy = getEmailHierarchyForFolder(newFolderLuid);
      boolean isNewFolderFiled = newFolderLuid != newHierarchy._inboxFolder.getLUID() && newFolderLuid != newHierarchy._sentFolder.getLUID();
      WritableSet oldCollection = (WritableSet)getStorageCollection(oldFolderLuid, message.flagsSet(2));
      WritableSet newCollection = (WritableSet)getStorageCollection(newFolderLuid, isNewFolderFiled);
      if (oldCollection != newCollection) {
         oldCollection.remove(message);
         message.setFolderId(newFolderLuid);
         newCollection.add(message);
      } else {
         message.setFolderId(newFolderLuid);
      }
   }

   public static boolean isInPersonalFolder(EmailMessageModel message) {
      EmailFolder folder = getEmailFolder(message.getFolderId());
      if (folder == null) {
         return false;
      } else if (folder.getFolderType() == 5) {
         long folderLUID = folder.getLUID();
         EmailHierarchy hierarchy = folder.getEmailHierarchy();
         return folderLUID != hierarchy.getInboxFolder() && folderLUID != hierarchy.getSentFolder();
      } else {
         return false;
      }
   }

   private Collection getStorageCollection(EmailFolder folder, boolean filed) {
      if (folder == null) {
         folder = filed ? this.getFiledFolder() : this.getUnfiledFolder();
      } else if (folder.getFolderType() != 80) {
         folder = filed ? this.getFiledFolder() : this.getUnfiledFolder();
      }

      return folder.getContainedItems();
   }

   private void moveSubFolders(EmailFolderBase src, EmailFolderBase dest) {
      Enumeration enumeration = src.getSubFolders();

      while (enumeration.hasMoreElements()) {
         EmailFolder f = (EmailFolder)enumeration.nextElement();
         f.setParentFolder(dest);
      }
   }

   public static boolean isInInboxOrSentItemsFolder(EmailMessageModel message) {
      EmailFolder folder = getEmailFolder(message.getFolderId());
      if (folder == null) {
         return false;
      } else {
         int folderType = folder.getFolderType();
         if (folderType != 2 && folderType != 4) {
            long folderLUID = folder.getLUID();
            EmailHierarchy hierarchy = folder.getEmailHierarchy();
            return folderLUID == hierarchy.getInboxFolder() || folderLUID == hierarchy.getSentFolder();
         } else {
            return true;
         }
      }
   }

   public static void removePreverifyListener(PreverifierListener l) {
      getPreverifyListeners();
      synchronized (_preverifyListeners) {
         _preverifyListeners.removeElement(l);
      }
   }

   public static long makeFolderLUID(long hierarchyLuid, int folderId) {
      return hierarchyLuid | folderId & 4294967295L;
   }

   public static void addPreverifyListener(PreverifierListener l) {
      getPreverifyListeners();
      synchronized (_preverifyListeners) {
         _preverifyListeners.addElement(l);
      }
   }

   public static void removeMessage(EmailMessageModel message, long oldFolder) {
      WritableSet collection = (WritableSet)getStorageCollection(oldFolder, message.flagsSet(2));
      collection.remove(message);
   }

   public static EmailHierarchy getEmailHierarchy(int userID, int uidHash, int nameHash, boolean createIfNotFound) {
      synchronized (FolderHierarchies.getLockObject()) {
         EmailHierarchy[] hierarchies = _hierarchies;
         EmailHierarchy nameMatchResult = null;

         for (EmailHierarchy result : hierarchies) {
            if (userID != -1 && result.getServiceUserId() != -1) {
               if (userID == result.getServiceUserId()) {
                  return result;
               }
            } else {
               if (result.getServiceUidHash() == uidHash) {
                  return result;
               }

               if (nameMatchResult == null && result.getServiceNameHash() == nameHash) {
                  nameMatchResult = result;
               }
            }
         }

         if (nameMatchResult != null) {
            return nameMatchResult;
         } else if (createIfNotFound) {
            byte num;
            Array.resize(hierarchies, num + 1);
            EmailHierarchy result = new EmailHierarchy(uidHash, nameHash, EmailFolderId.getNextFolderId(), userID);
            result.setFriendlyName(EmailResources.getString(85));
            hierarchies[num] = result;
            PersistentObject.commit(hierarchies);
            return result;
         } else {
            return null;
         }
      }
   }

   public static EmailHierarchy getEmailHierarchy(ServiceRecord sr, boolean createIfNotFound) {
      return getEmailHierarchy(sr.getUserId(), sr.getUidHash(), sr.getNameHash(), createIfNotFound);
   }

   private static boolean exclude(EmailMessageModel m) {
      getPreverifyListeners();
      synchronized (FolderHierarchies.getLockObject()) {
         boolean var10000;
         synchronized (_preverifyListeners) {
            boolean retval = false;

            for (int i = _preverifyListeners.size() - 1; i >= 0; i--) {
               PreverifierListener l = (PreverifierListener)_preverifyListeners.elementAt(i);
               retval |= l.excludeMessage(m);
            }

            var10000 = retval;
         }

         return var10000;
      }
   }

   public static EmailHierarchy getAnonymousEmailHierarchy() {
      return getEmailHierarchy(-1, -1, -1, true);
   }

   private static void getPreverifyListeners() {
      if (_preverifyListeners == null) {
         ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
         _preverifyListeners = (Vector)ar.getOrWaitFor(1684962353143705358L);
         if (_preverifyListeners == null) {
            _preverifyListeners = (Vector)(new Object(1));
            ar.put(1684962353143705358L, _preverifyListeners);
         }
      }
   }

   public static boolean addMessage(EmailMessageModel message, long newFolder) {
      message.setFolderId(newFolder);
      boolean retval = false;
      if (!message.flagsSet(33554432)) {
         retval = exclude(message);
      }

      if (!retval) {
         WritableSet collection = (WritableSet)getStorageCollection(newFolder, message.flagsSet(2));
         collection.add(message);
      }

      return !retval;
   }

   public static long getHierarchyLUIDFromFolderLUID(long folderLuid) {
      return folderLuid >> 32 << 32;
   }

   public static void crypt(int contentProtectionGeneration) {
      PersistedSortedCollectionEncryptor.crypt(283552684730285077L, contentProtectionGeneration);
   }

   private void setServiceNameHash(int nameHash) {
      this._nameHash = nameHash;
   }

   private void setServiceUidHash(int uidHash) {
      this._uidHash = uidHash;
   }

   public static long makeHierarchyLUID(int uidHash, int nameHash, int folderId) {
      return (long)(uidHash ^ nameHash) << 32;
   }

   private EmailHierarchy(int uidHash, int nameHash, int folderId, int userID) {
      this._cid = "CMIME";
      this._userID = userID;
      this._uidHash = uidHash;
      this._nameHash = nameHash;
      this._folderId = folderId;
      this._luid = makeHierarchyLUID(uidHash, nameHash, folderId);
      this._missingFolderTimestamp = -1;
      this._refIdToMessageTable = (IntHashtable)(new Object());
      this._subFolders = new EmailFolder[0];
      this._inboxFolder = this.makeNewFolder(EmailFolderId.getNextFolderId(), 2, true, true, EmailResources.getString(70));
      this._sentFolder = this.makeNewFolder(EmailFolderId.getNextFolderId(), 4, true, true, EmailResources.getString(71));
      this._orphanedSavedFolder = this.makeNewFolder(EmailFolderId.getNextFolderId(), 80, true, true, EmailResources.getString(72));
      this._unfiledFolder = this.makeNewFolder(EmailFolderId.getNextFolderId(), 81, true, true, EmailResources.getString(61));
      this._filedFolder = this.makeNewFolder(EmailFolderId.getNextFolderId(), 82, true, true, EmailResources.getString(741));
      this.register();
   }

   static {
      synchronized (_persistentObject) {
         _hierarchies = (EmailHierarchy[])_persistentObject.getContents();
         if (_hierarchies == null) {
            _hierarchies = new EmailHierarchy[0];
            _persistentObject.setContents(_hierarchies, 51, false);
            _persistentObject.commit();
         }

         ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
         _folderCollections = ar.getLongHashtable(4848635152656490553L);
         _longKeyProviderAdaptorComparator = (Comparator)ar.getOrWaitFor(-7100114292399893306L);
         if (_longKeyProviderAdaptorComparator == null) {
            _longKeyProviderAdaptorComparator = (Comparator)(new Object((LongKeyProviderAdaptor)(new Object())));
            ar.put(-7100114292399893306L, _longKeyProviderAdaptorComparator);
         }

         _emailFolderListeners = (Vector)ar.getOrWaitFor(-8357190046905569807L);
         if (_emailFolderListeners == null) {
            _emailFolderListeners = (Vector)(new Object());
            ar.put(-8357190046905569807L, _emailFolderListeners);
         }
      }
   }
}
