package net.rim.device.apps.internal.blackberryemail.folder;

import java.util.Enumeration;
import net.rim.device.api.i18n.Locale;
import net.rim.device.api.lowmemory.LowMemoryManager;
import net.rim.device.api.synchronization.ConverterUtilities;
import net.rim.device.api.synchronization.StateInfoListener;
import net.rim.device.api.synchronization.SyncCollection;
import net.rim.device.api.synchronization.SyncCollectionStatistics;
import net.rim.device.api.synchronization.SyncConverter;
import net.rim.device.api.synchronization.SyncEventListener;
import net.rim.device.api.synchronization.SyncObject;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.apps.internal.blackberryemail.EmailSyncState;
import net.rim.device.apps.internal.blackberryemail.otasync.OTAMessageSync;
import net.rim.vm.Array;
import net.rim.vm.Memory;

public final class EmailFolderSync
   extends EmailSyncState
   implements SyncCollection,
   SyncConverter,
   StateInfoListener,
   SyncEventListener,
   SyncCollectionStatistics {
   private static final int SYNC_VERSION = 0;
   private static String SYNC_NAME = "Folders";
   private static final int FOLDERID = 10;
   private static final int PARENTFOLDERID = 11;
   private static final int FOLDERNAME = 12;
   private static final int SERVICENAME = 13;
   private static final int SERVICEID = 14;
   private static final int FOLDERTYPE = 15;
   private static final int FOLDERATTR = 16;
   private static final int USERID = 17;

   EmailFolderSync() {
   }

   public static final long readFolder(DataBuffer buffer) {
      int nameHash = buffer.readInt();
      int uidHash = buffer.readInt();
      int folderId = buffer.readShort();
      int type = buffer.readByte();
      int userId = -1;
      buffer.readByte();
      EmailHierarchy h = EmailHierarchy.getEmailHierarchy(userId, uidHash, nameHash, true);
      return h.getEmailFolder(folderId, type);
   }

   public static final void writeFolder(long folderLUID, DataBuffer buffer) {
      EmailHierarchy h = EmailHierarchy.getEmailHierarchyForFolder(folderLUID);
      buffer.writeInt(h.getServiceNameHash());
      buffer.writeInt(h.getServiceUidHash());
      EmailFolder folder = (EmailFolder)h.getFolder(folderLUID);
      int folderID = 0;
      byte folderType = 0;
      if (folder == null) {
         folderID = h.getFolderIdForFolder(folderLUID);
         folderType = 5;
      } else {
         folderID = folder.getFolderId();
         folderType = (byte)folder.getFolderType();
      }

      buffer.writeShort(folderID);
      buffer.writeByte(folderType);
      buffer.writeByte(0);
   }

   @Override
   public final boolean addSyncObject(SyncObject object) {
      return true;
   }

   @Override
   public final boolean updateSyncObject(SyncObject oldObject, SyncObject newObject) {
      return false;
   }

   @Override
   public final boolean removeSyncObject(SyncObject object) {
      EmailFolderBase f = (EmailFolderBase)object;
      f.getEmailHierarchy().disestablishEmailFolder(f);
      return true;
   }

   @Override
   public final boolean removeAllSyncObjects() {
      for (int i = EmailHierarchy.getEmailHierarchyCount() - 1; i >= 0; i--) {
         EmailFolderBase f = EmailHierarchy.getEmailHierarchy(i);
         removeAllHierarchyFolders((EmailHierarchy)f, f);
      }

      LowMemoryManager.poll();
      return true;
   }

   private static final void removeAllHierarchyFolders(EmailHierarchy hier, EmailFolderBase f) {
      if (f.containsSubFolders()) {
         Enumeration e = f.getSubFolders();

         while (e.hasMoreElements()) {
            removeAllHierarchyFolders(hier, (EmailFolderBase)e.nextElement());
         }
      }

      if (f.isInFolderDatabase()) {
         hier.disestablishEmailFolder(f);
      }
   }

   @Override
   public final SyncObject[] getSyncObjects() {
      SyncObject[] objects = new SyncObject[0];
      int sofar = 0;

      for (int i = EmailHierarchy.getEmailHierarchyCount() - 1; i >= 0; i--) {
         EmailFolderBase f = EmailHierarchy.getEmailHierarchy(i);
         sofar += f.getActiveFolders(objects, sofar);
      }

      Array.resize(objects, sofar);
      return objects;
   }

   @Override
   public final SyncObject getSyncObject(int uid) {
      return null;
   }

   @Override
   public final int getSyncObjectCount() {
      int count = 0;

      for (int i = EmailHierarchy.getEmailHierarchyCount() - 1; i >= 0; i--) {
         EmailFolderBase f = EmailHierarchy.getEmailHierarchy(i);
         count += f.getActiveFolderCount();
      }

      return count;
   }

   @Override
   public final boolean isSyncObjectDirty(SyncObject object) {
      return false;
   }

   @Override
   public final void setSyncObjectDirty(SyncObject object) {
   }

   @Override
   public final void clearSyncObjectDirty(SyncObject object) {
   }

   @Override
   public final int getSyncVersion() {
      return 0;
   }

   @Override
   public final String getSyncName() {
      return SYNC_NAME;
   }

   @Override
   public final String getSyncName(Locale locale) {
      return null;
   }

   @Override
   public final SyncConverter getSyncConverter() {
      return this;
   }

   @Override
   public final void beginTransaction() {
      OTAMessageSync.getInstance().folderListSynced();
   }

   @Override
   public final void endTransaction() {
   }

   @Override
   public final int getStateInfo(SyncObject object) {
      EmailFolderBase f = (EmailFolderBase)object;
      EmailHierarchy h = f.getEmailHierarchy();
      int stateInfo = EmailSyncState.makeStateInfo(f.getFolderId(), false, false, this.getUseFlag(h), false);
      int attributes = f.getFolderAttributes();
      if ((attributes & 1) != 0) {
         stateInfo |= 1;
      }

      if ((attributes & 2) != 0) {
         stateInfo |= 2;
      }

      return stateInfo;
   }

   @Override
   public final void setStateInfo(SyncObject object, int version, int stateInfo) {
   }

   @Override
   public final boolean convert(SyncObject object, DataBuffer buffer, int version) {
      EmailFolderBase f = (EmailFolderBase)object;
      EmailHierarchy h = f.getEmailHierarchy();
      int id = f.getFolderId();
      ConverterUtilities.convertInt(buffer, 10, f.getFolderId(), 2);
      EmailFolderBase parent = (EmailFolderBase)f.getParentFolder();
      if (parent != null) {
         id = parent.getFolderId();
      }

      ConverterUtilities.convertInt(buffer, 11, id, 2);
      ConverterUtilities.writeStringSmart(buffer, 12, f.getFriendlyName());
      ConverterUtilities.convertInt(buffer, 13, h.getServiceNameHash(), 4);
      ConverterUtilities.convertInt(buffer, 14, h.getServiceUidHash(), 4);
      ConverterUtilities.convertInt(buffer, 15, f.getFolderType(), 1);
      ConverterUtilities.convertInt(buffer, 16, f.getFolderAttributes(), 1);
      ConverterUtilities.convertInt(buffer, 17, h.getServiceUserId(), 4);
      return true;
   }

   @Override
   public final SyncObject convert(DataBuffer buffer, int version, int uid) {
      try {
         int id = 0;
         int parent = 0;
         String name = null;
         int nameHash = -1;
         int uidHash = -1;
         int userId = -1;
         int type = 0;
         int folderAttr = 0;

         while (buffer.available() > 0) {
            int fieldId = ConverterUtilities.getType(buffer, true);
            switch (fieldId) {
               case 9:
                  buffer.skipBytes(buffer.readShort() + 1);
                  break;
               case 10:
               default:
                  id = ConverterUtilities.readInt(buffer);
                  break;
               case 11:
                  parent = ConverterUtilities.readInt(buffer);
                  break;
               case 12:
                  name = ConverterUtilities.readString(buffer);
                  break;
               case 13:
                  nameHash = ConverterUtilities.readInt(buffer);
                  break;
               case 14:
                  uidHash = ConverterUtilities.readInt(buffer);
                  break;
               case 15:
                  type = ConverterUtilities.readInt(buffer);
                  break;
               case 16:
                  folderAttr = ConverterUtilities.readInt(buffer);
                  break;
               case 17:
                  userId = ConverterUtilities.readInt(buffer);
            }
         }

         if (name != null && nameHash != -1 && uidHash != -1 && type != -1) {
            EmailHierarchy h = EmailHierarchy.getEmailHierarchy(userId, uidHash, nameHash, true);
            EmailFolderBase emailFolder = (EmailFolderBase)h.establishEmailFolder(id, parent, type, name, uid);
            emailFolder.setFolderAttributes(folderAttr);
            return emailFolder;
         } else {
            return null;
         }
      } finally {
         ;
      }
   }

   @Override
   public final void syncEventOccurred(int eventId, Object object) {
      switch (eventId) {
         case 2:
            EmailHierarchy.purge();
      }
   }

   @Override
   public final synchronized int getSyncCollectionSize() {
      int size = 0;
      SyncObject[] folders = this.getSyncObjects();
      if (folders != null) {
         for (int i = folders.length - 1; i >= 0; i--) {
            SyncObject so = folders[i];
            EmailFolderBase f = (EmailFolderBase)so;
            size += Memory.classSize(folders[i]);
            size += Memory.objectSize(f.getFriendlyName());
         }
      }

      return size;
   }
}
