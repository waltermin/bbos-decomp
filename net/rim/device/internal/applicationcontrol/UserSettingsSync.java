package net.rim.device.internal.applicationcontrol;

import java.io.IOException;
import java.util.Vector;
import net.rim.device.api.collection.CollectionEventSource;
import net.rim.device.api.collection.util.CollectionListenerManager;
import net.rim.device.api.i18n.Locale;
import net.rim.device.api.synchronization.ConverterUtilities;
import net.rim.device.api.synchronization.OTASyncCapable;
import net.rim.device.api.synchronization.SyncCollection;
import net.rim.device.api.synchronization.SyncCollectionSchema;
import net.rim.device.api.synchronization.SyncCollectionStatistics;
import net.rim.device.api.synchronization.SyncCollectionStatisticsManager;
import net.rim.device.api.synchronization.SyncConverter;
import net.rim.device.api.synchronization.SyncManager;
import net.rim.device.api.synchronization.SyncObject;
import net.rim.device.api.system.CodeModuleManager;
import net.rim.device.api.util.DataBuffer;

final class UserSettingsSync implements SyncCollection, SyncCollectionStatistics, SyncConverter, OTASyncCapable, CollectionEventSource, Runnable {
   private UserPermissions _userPermissions;
   private CollectionListenerManager _collectionListenerManager = new CollectionListenerManager();
   private SyncCollectionSchema _schema;
   private static final int USR_RESET_INTERVAL;
   private static final int DEFAULT_RECORD_TYPE;
   private static final int[] KEY_FIELD_IDS = new int[]{0, 0, 0, 0};

   final void settingRemoved(UserSetting us) {
      this._collectionListenerManager.fireElementRemoved(this, us);
   }

   final void reset() {
      this._collectionListenerManager.fireReset(this);
   }

   final void settingAdded(UserSetting us) {
      this._collectionListenerManager.fireElementAdded(this, us);
   }

   final void settingUpdated(UserSetting oldUS, UserSetting newUS) {
      this._collectionListenerManager.fireElementUpdated(this, oldUS, newUS);
   }

   @Override
   public final void run() {
      SyncManager manager = SyncManager.getInstance();
      if (manager != null) {
         manager.enableSynchronization(this);
      }
   }

   @Override
   public final boolean addSyncObject(SyncObject object) {
      if (object instanceof UserSetting && this._userPermissions.getStorage() != null) {
         UserSetting element = (UserSetting)object;
         int index = this._userPermissions.getStorage().indexOf(element);
         if (index != -1) {
            this.updateSyncObject((SyncObject)this._userPermissions.getStorage().elementAt(index), element);
            return true;
         }

         byte[] hash = element.getHash();
         int handle = CodeModuleManager.getModuleHandle(hash);
         if (handle != 0) {
            this._userPermissions.putSetting(handle, element);
            if (ApplicationControlImpl.setModulePermission(hash, handle, element.getPermissions())) {
               ApplicationControlImpl.scheduleDeviceReset("USSa", 3600000);
            }
         } else {
            this._userPermissions.getStorage().addElement(element);
            this._userPermissions.commit();
         }

         this._collectionListenerManager.fireElementAdded(this, element);
         return true;
      } else {
         return false;
      }
   }

   @Override
   public final boolean removeSyncObject(SyncObject object) {
      if (object instanceof UserSetting && this._userPermissions.getStorage() != null) {
         UserSetting element = (UserSetting)object;
         if (!element.hashEquals(ApplicationControlConstants.EMPTY_HASH)) {
            if (element.hashEquals(ApplicationControlConstants.FILLED_HASH)) {
               this._userPermissions.removeBackedUpDefaultSetting();
            } else {
               this._userPermissions.getStorage().removeElement(element);
               this._userPermissions.commit();
            }

            ApplicationControl.reloadModulePermissions();
            this._collectionListenerManager.fireElementRemoved(this, element);
         }

         return true;
      } else {
         return false;
      }
   }

   @Override
   public final boolean removeAllSyncObjects() {
      int elements = this._userPermissions.getStorage().size();

      while (--elements >= 0) {
         this.removeSyncObject((SyncObject)this._userPermissions.getStorage().elementAt(elements));
      }

      return true;
   }

   @Override
   public final boolean updateSyncObject(SyncObject oldObject, SyncObject newObject) {
      if (oldObject instanceof UserSetting && newObject instanceof UserSetting) {
         boolean resetRequired = false;
         if (this._userPermissions.getStorage() != null) {
            UserSetting oldUS = (UserSetting)oldObject;
            UserSetting newUS = (UserSetting)newObject;
            int handle = CodeModuleManager.getModuleHandle(newUS.getHash());
            int index = this._userPermissions.getStorage().indexOf(oldUS);
            if (index != -1) {
               if (handle != 0) {
                  UserSetting target = this._userPermissions.getSetting(handle);
                  this._userPermissions.setPermissions(target, newUS, false);
                  resetRequired = ApplicationControlImpl.setModuleUserPermission(newUS.getHash(), handle, newUS.getPermissions());
               } else if (newUS.hashEquals(ApplicationControlConstants.EMPTY_HASH)) {
                  UserSetting defaults = this._userPermissions.getDefaultSetting();
                  this._userPermissions.setPermissions(oldUS, defaults, newUS);
                  resetRequired = ApplicationControl.reloadDefaultModulePermissions();
               } else {
                  this._userPermissions.getStorage().setElementAt(newUS, index);
                  this._userPermissions.commit();
               }
            }

            if (oldUS.getUID() != newUS.getUID()) {
               this._collectionListenerManager.fireElementRemoved(this, oldUS);
               this._collectionListenerManager.fireElementAdded(this, newUS);
            } else {
               this._collectionListenerManager.fireElementUpdated(this, newUS, oldUS);
            }

            if (resetRequired) {
               ApplicationControlImpl.scheduleDeviceReset("USSu", 3600000);
            }

            return true;
         }
      }

      return false;
   }

   @Override
   public final void beginTransaction() {
   }

   @Override
   public final void endTransaction() {
   }

   @Override
   public final void addCollectionListener(Object listener) {
      this._collectionListenerManager.addCollectionListener(listener);
   }

   @Override
   public final void removeCollectionListener(Object listener) {
      this._collectionListenerManager.removeCollectionListener(listener);
   }

   @Override
   public final SyncConverter getSyncConverter() {
      return this;
   }

   @Override
   public final String getSyncName() {
      return "Application Permissions";
   }

   @Override
   public final String getSyncName(Locale locale) {
      return this.getSyncName();
   }

   @Override
   public final int getSyncObjectCount() {
      return this._userPermissions.getStorage() == null ? 0 : this._userPermissions.getStorage().size();
   }

   @Override
   public final synchronized int getSyncCollectionSize() {
      return SyncCollectionStatisticsManager.getSyncCollectionSize(this);
   }

   @Override
   public final SyncObject[] getSyncObjects() {
      Vector permissions = this._userPermissions.getStorage();
      int size = permissions == null ? 0 : permissions.size();
      if (size <= 0) {
         return new SyncObject[0];
      }

      SyncObject[] array = new SyncObject[size];
      if (permissions != null) {
         for (int i = 0; i < size; i++) {
            array[i] = (SyncObject)permissions.elementAt(i);
         }
      }

      return array;
   }

   @Override
   public final SyncObject getSyncObject(int uid) {
      Vector permissions = this._userPermissions.getStorage();
      int size = permissions == null ? 0 : permissions.size();

      for (int i = 0; i < size; i++) {
         UserSetting element = (UserSetting)permissions.elementAt(i);
         if (element.getUID() == uid) {
            return element;
         }
      }

      return null;
   }

   @Override
   public final int getSyncVersion() {
      return 1;
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
   public final SyncObject convert(DataBuffer buffer, int version, int uid) {
      byte[] hash = null;
      long permissions = 0;
      long dontPrompt = 0;
      long isSet = 0;

      try {
         buffer.rewind();
         if (ConverterUtilities.findType(buffer, -1)) {
            int var13 = ConverterUtilities.readShort(buffer);
         }

         buffer.rewind();
         if (ConverterUtilities.findType(buffer, 0)) {
            hash = ConverterUtilities.readByteArray(buffer);
            buffer.rewind();
            if (ConverterUtilities.findType(buffer, 4)) {
               permissions = ConverterUtilities.readLong(buffer);
               permissions ^= 7769595838464L;
               permissions &= Integer.MIN_VALUE;
            } else {
               buffer.rewind();
               if (ConverterUtilities.findType(buffer, 1)) {
                  permissions = (long)ConverterUtilities.readInt(buffer) << 32;
                  permissions ^= 7769595838464L;
                  permissions &= Integer.MIN_VALUE;
               }
            }

            buffer.rewind();
            if (ConverterUtilities.findType(buffer, 5)) {
               dontPrompt = ConverterUtilities.readLong(buffer);
               dontPrompt &= Integer.MIN_VALUE;
            } else {
               buffer.rewind();
               if (ConverterUtilities.findType(buffer, 2)) {
                  dontPrompt = (long)ConverterUtilities.readInt(buffer) << 32;
                  dontPrompt &= Integer.MIN_VALUE;
               }
            }

            buffer.rewind();
            if (ConverterUtilities.findType(buffer, 6)) {
               isSet = ConverterUtilities.readLong(buffer);
               isSet &= Integer.MIN_VALUE;
            } else {
               buffer.rewind();
               if (ConverterUtilities.findType(buffer, 3)) {
                  isSet = (long)ConverterUtilities.readInt(buffer) << 32;
                  isSet &= Integer.MIN_VALUE;
               }
            }

            return new UserSetting(hash, permissions, dontPrompt, isSet, uid);
         } else {
            return null;
         }
      } catch (IOException var12) {
         return null;
      }
   }

   @Override
   public final boolean convert(SyncObject object, DataBuffer buffer, int version) {
      if (!(object instanceof UserSetting)) {
         return false;
      }

      UserSetting element = (UserSetting)object;
      ConverterUtilities.writeShort(buffer, -1, (short)4);
      ConverterUtilities.writeByteArray(buffer, 0, element.getHash());
      long permissions = element.getPermissions();
      permissions ^= 7769595838464L;
      ConverterUtilities.writeInt(buffer, 1, (int)(permissions >> 32));
      ConverterUtilities.writeInt(buffer, 2, (int)(element.getDontPrompt() >> 32));
      ConverterUtilities.writeInt(buffer, 3, (int)(element.getIsSet() >> 32));
      ConverterUtilities.writeLong(buffer, 4, permissions);
      ConverterUtilities.writeLong(buffer, 5, element.getDontPrompt());
      ConverterUtilities.writeLong(buffer, 6, element.getIsSet());
      return true;
   }

   @Override
   public final SyncCollectionSchema getSchema() {
      return this._schema;
   }

   UserSettingsSync(UserPermissions userPermissions) {
      this._userPermissions = userPermissions;
      this._schema = new SyncCollectionSchema();
      this._schema.setDefaultRecordType(1);
      this._schema.setKeyFieldIds(1, KEY_FIELD_IDS);
   }
}
