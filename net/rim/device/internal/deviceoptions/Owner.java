package net.rim.device.internal.deviceoptions;

import net.rim.device.api.itpolicy.ITPolicy;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.RIMGlobalMessagePoster;
import net.rim.device.api.system.RIMPersistentStore;

public final class Owner {
   public static final long GUID_OWNER_OPTIONS_CHANGED;
   public static final int OWNER_NAME_MAX_CHARS;
   public static final int OWNER_INFO_MAX_CHARS;
   private static final long OWNER_DATA_KEY;
   private static PersistentObject _persistentObject = RIMPersistentStore.getPersistentObject(6182193625217474049L);
   private static Owner$OwnerData _ownerData;

   private Owner() {
   }

   public static final String getOwnerName() {
      return _ownerData._name;
   }

   public static final boolean setOwnerName(String name) {
      return setOwnerName(name, false);
   }

   public static final boolean setOwnerName(String name, boolean force) {
      if (!force) {
         byte allowUserChanges = ITPolicy.getByte(21, 1, (byte)0);
         if ((allowUserChanges & 2) != 0) {
            return false;
         }
      }

      if (name.length() > 39) {
         name = name.substring(0, 39);
      }

      _ownerData._name = name;
      commit(true);
      return true;
   }

   public static final String getOwnerInfo() {
      return _ownerData._info;
   }

   public static final boolean setOwnerInfo(String info) {
      return setOwnerInfo(info, false);
   }

   public static final boolean setOwnerInfo(String info, boolean force) {
      if (!force) {
         byte allowUserChanges = ITPolicy.getByte(21, 1, (byte)0);
         if ((allowUserChanges & 1) != 0) {
            return false;
         }
      }

      if (info.length() > 127) {
         info = info.substring(0, 127);
      }

      _ownerData._info = info;
      commit(true);
      return true;
   }

   public static final void resetToDefaults() {
      _ownerData._name = "";
      _ownerData._info = "";
      commit(true);
   }

   private static final void commit(boolean notifyOfChanges) {
      _persistentObject.commit();
      if (notifyOfChanges) {
         RIMGlobalMessagePoster.postGlobalEvent(-3297167379286550693L);
      }
   }

   static {
      synchronized (_persistentObject) {
         _ownerData = (Owner$OwnerData)_persistentObject.getContents();
         if (_ownerData == null) {
            _ownerData = new Owner$OwnerData();
            _persistentObject.setContents(_ownerData, 51, false);
            commit(false);
         }
      }
   }
}
