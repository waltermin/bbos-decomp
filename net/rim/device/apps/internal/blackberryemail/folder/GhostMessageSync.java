package net.rim.device.apps.internal.blackberryemail.folder;

import net.rim.device.api.i18n.Locale;
import net.rim.device.api.synchronization.StateInfoListener;
import net.rim.device.api.synchronization.SyncCollection;
import net.rim.device.api.synchronization.SyncConverter;
import net.rim.device.api.synchronization.SyncObject;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.apps.internal.blackberryemail.EmailSyncState;
import net.rim.vm.Array;

final class GhostMessageSync extends EmailSyncState implements SyncCollection, SyncConverter, StateInfoListener {
   private static final int SYNC_VERSION = 0;
   private static String SYNC_NAME = "Purged Messages";

   @Override
   public final boolean addSyncObject(SyncObject object) {
      GhostMessageSync$GhostMessageSyncObject o = (GhostMessageSync$GhostMessageSyncObject)object;
      o._h.addGhostMessageReference(o._uid, o._info);
      return true;
   }

   @Override
   public final boolean updateSyncObject(SyncObject oldObject, SyncObject newObject) {
      return false;
   }

   @Override
   public final boolean removeSyncObject(SyncObject object) {
      GhostMessageSync$GhostMessageSyncObject o = (GhostMessageSync$GhostMessageSyncObject)object;
      if (o._h.getNumberOfGhostMessages() > 0) {
         o._h.removeAllGhostMessageReferences();
      }

      return true;
   }

   @Override
   public final boolean removeAllSyncObjects() {
      for (int i = EmailHierarchy.getEmailHierarchyCount() - 1; i >= 0; i--) {
         EmailHierarchy h = EmailHierarchy.getEmailHierarchy(i);
         h.removeAllGhostMessageReferences();
      }

      return true;
   }

   @Override
   public final SyncObject[] getSyncObjects() {
      int num = EmailHierarchy.getEmailHierarchyCount();
      SyncObject[] objects = new Object[500 * num];
      int sofar = 0;

      for (int i = 0; i < num; i++) {
         EmailHierarchy h = EmailHierarchy.getEmailHierarchy(i);
         GhostMessageData ghostData = (GhostMessageData)h.getGhostMessageData();
         synchronized (ghostData.getLock()) {
            ghostData.removeDeletedEntries();

            for (int j = ghostData.size() - 1; j >= 0; j--) {
               objects[sofar++] = new GhostMessageSync$GhostMessageSyncObject(h, ghostData.getTagAt(j), ghostData.getInfoAt(j));
            }
         }
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
         EmailHierarchy h = EmailHierarchy.getEmailHierarchy(i);
         GhostMessageData ghostData = (GhostMessageData)h.getGhostMessageData();
         ghostData.removeDeletedEntries();
         count += h.getNumberOfGhostMessages();
      }

      return count;
   }

   @Override
   public final boolean isSyncObjectDirty(SyncObject object) {
      return true;
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
   }

   @Override
   public final void endTransaction() {
   }

   @Override
   public final int getStateInfo(SyncObject object) {
      GhostMessageSync$GhostMessageSyncObject o = (GhostMessageSync$GhostMessageSyncObject)object;
      return o._info | EmailSyncState.makeStateInfo(0, false, false, this.getUseFlag(o._h), false);
   }

   @Override
   public final void setStateInfo(SyncObject object, int version, int stateInfo) {
   }

   @Override
   public final boolean convert(SyncObject object, DataBuffer buffer, int version) {
      GhostMessageSync$GhostMessageSyncObject o = (GhostMessageSync$GhostMessageSyncObject)object;
      int info = o._info;
      buffer.writeShort(16);
      buffer.writeByte(0);
      buffer.writeInt(EmailSyncState.getFlags(info));
      EmailFolderSync.writeFolder(o._h.getEmailFolder(EmailSyncState.getFolderId(info)), buffer);
      return true;
   }

   @Override
   public final SyncObject convert(DataBuffer buffer, int version, int uid) {
      try {
         int length = buffer.readShort();
         if ((length == 16 || length == 20) && buffer.readByte() == 0) {
            int flags = buffer.readInt();
            long folderLUID = EmailFolderSync.readFolder(buffer);
            EmailHierarchy hierarchy = EmailHierarchy.getEmailHierarchyForFolder(folderLUID);
            return new GhostMessageSync$GhostMessageSyncObject(hierarchy, uid, EmailSyncState.makeStateInfo(hierarchy.getFolderIdForFolder(folderLUID), flags));
         } else {
            return null;
         }
      } finally {
         ;
      }
   }
}
