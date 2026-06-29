package net.rim.device.internal.applicationcontrol;

import java.util.Enumeration;
import java.util.Vector;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.CodeModuleManager;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.RIMPersistentStore;
import net.rim.device.api.util.IntEnumeration;
import net.rim.device.api.util.IntHashtable;
import net.rim.device.internal.proxy.Proxy;
import net.rim.device.internal.system.CodeStore;
import net.rim.vm.Array;

final class UserPermissions {
   private IntHashtable _lookupTable = new IntHashtable();
   private Vector _storage;
   private PersistentObject _persistentStorage = RIMPersistentStore.getPersistentObject(3147043076836770146L);
   private UserSetting _backedUpDefaults;
   private UserSetting _defaults;
   private UserSettingsSync _sync;
   private static final long STORAGE_KEY = 3147043076836770146L;
   private static final int DEFAULTS_HANDLE = 0;
   private static final int BACKUP_DEFAULTS_HANDLE = -1;
   private static final long USER_SETTINGS_SYNC_KEY = -8759977085745458596L;

   UserPermissions() {
      if ((this._sync = (UserSettingsSync)ApplicationRegistry.getApplicationRegistry().getOrWaitFor(-8759977085745458596L)) == null) {
         this._sync = new UserSettingsSync(this);
         Proxy.getInstance().invokeLater(this._sync);
         ApplicationRegistry.getApplicationRegistry().put(-8759977085745458596L, this._sync);
      }
   }

   public final void load(long defaultPermissions) {
      this._lookupTable.clear();
      if (this._storage == null) {
         synchronized (this._persistentStorage) {
            this._storage = (Vector)this._persistentStorage.getContents();
            if (this._storage == null) {
               this._storage = new Vector();
               UserSetting defaults = new UserSetting(ApplicationControlConstants.EMPTY_HASH, defaultPermissions);
               this._storage.addElement(defaults);
               this._sync.settingAdded(defaults);
               this._persistentStorage.setContents(this._storage, 51);
               this._persistentStorage.commit();
            }
         }
      }

      Enumeration userSettingEnum = this._storage.elements();

      while (userSettingEnum.hasMoreElements()) {
         UserSetting userSetting = (UserSetting)userSettingEnum.nextElement();
         if (userSetting.hashEquals(ApplicationControlConstants.EMPTY_HASH)) {
            this.putDefaultSetting(userSetting, false);
         } else if (userSetting.hashEquals(ApplicationControlConstants.FILLED_HASH)) {
            this.putBackedUpDefaultSetting(userSetting, false);
         } else {
            int currHandle = CodeModuleManager.getModuleHandle(userSetting.getHash());
            if (currHandle != 0 && !this.containsSetting(currHandle)) {
               this.putSetting(currHandle, userSetting, false);
            }
         }
      }
   }

   public final void clear(long defaultPermissions) {
      this._sync.reset();
      this._storage = null;
      this._persistentStorage.setContents(null, 51);
      this.load(defaultPermissions);
   }

   final void putDefaultSetting(UserSetting userSetting) {
      this.putDefaultSetting(userSetting, true);
   }

   final void putDefaultSetting(UserSetting userSetting, boolean commit) {
      this._defaults = userSetting;
      this.putSetting(0, this._defaults, commit);
   }

   final void putBackedUpDefaultSetting(UserSetting userSetting) {
      this.putBackedUpDefaultSetting(userSetting, true);
   }

   final void putBackedUpDefaultSetting(UserSetting userSetting, boolean commit) {
      this._backedUpDefaults = userSetting;
      this.putSetting(-1, this._backedUpDefaults, commit);
   }

   final void putSetting(int handle, UserSetting userSetting) {
      this.putSetting(handle, userSetting, true);
   }

   final void putSetting(int handle, UserSetting userSetting, boolean commit) {
      this.putSetting(handle, userSetting, commit, true);
   }

   final void putSetting(int handle, UserSetting userSetting, boolean commit, boolean applyToSiblings) {
      this._lookupTable.put(handle, userSetting);
      if (!this._storage.contains(userSetting)) {
         this._storage.addElement(userSetting);
         this._sync.settingAdded(userSetting);
      }

      if (applyToSiblings) {
         this.applyToSiblings(handle, userSetting);
      }

      if (commit) {
         this.commit();
      }
   }

   final UserSetting getBackedUpDefaultSetting() {
      return this._backedUpDefaults;
   }

   final UserSetting getDefaultSetting() {
      return this._defaults;
   }

   final UserSetting getSetting(int handle) {
      return (UserSetting)this._lookupTable.get(handle);
   }

   final UserSetting getSetting(byte[] hash) {
      Enumeration elements = this._storage.elements();

      while (elements.hasMoreElements()) {
         UserSetting cur = (UserSetting)elements.nextElement();
         if (cur.hashEquals(hash)) {
            return cur;
         }
      }

      return null;
   }

   final int[] getLoadedHandles() {
      int[] keys = new int[this._lookupTable.size()];
      IntEnumeration keysEnum = this._lookupTable.keys();
      int loc = 0;

      while (keysEnum.hasMoreElements()) {
         int currKey = keysEnum.nextElement();
         if (currKey != 0 && currKey != -1) {
            keys[loc++] = currKey;
         }
      }

      Array.resize(keys, loc);
      return keys;
   }

   final void removeBackedUpDefaultSetting() {
      if (this._backedUpDefaults != null) {
         this._lookupTable.removeValue(this._backedUpDefaults);
         this._storage.removeElement(this._backedUpDefaults);
         this._sync.settingRemoved(this._backedUpDefaults);
         this.commit();
      }

      this._backedUpDefaults = null;
   }

   final void removeSetting(int handle) {
      this.removeSetting(handle, null);
   }

   final void removeSetting(UserSetting userSetting) {
      this._lookupTable.removeValue(userSetting);
      if (userSetting != null) {
         this._sync.settingRemoved(userSetting);
         this._storage.removeElement(userSetting);
         this.removeSiblings(userSetting);
         this.commit();
      }
   }

   final void removeSetting(int handle, UserSetting userSetting) {
      this._lookupTable.remove(handle);
      if (userSetting != null) {
         this._sync.settingRemoved(userSetting);
         this._storage.removeElement(userSetting);
         this.removeSiblings(handle, userSetting);
         this.commit();
      }
   }

   final boolean containsSetting(UserSetting value) {
      return this._lookupTable.contains(value);
   }

   final boolean containsSetting(int handle) {
      return this._lookupTable.containsKey(handle);
   }

   final int numberOfSettings() {
      return this._lookupTable.size();
   }

   final void commit() {
      this._persistentStorage.commit();
   }

   final Vector getStorage() {
      return this._storage;
   }

   final void setPermissions(UserSetting target, UserSetting source) {
      this.setPermissions(null, target, source, true, true);
   }

   final void setPermissions(UserSetting target, UserSetting source, boolean commit) {
      this.setPermissions(null, target, source, commit, true);
   }

   final void setPermissions(UserSetting old, UserSetting target, UserSetting source) {
      this.setPermissions(old, target, source, true, true);
   }

   final void setPermissions(UserSetting old, UserSetting target, UserSetting source, boolean commit) {
      this.setPermissions(old, target, source, commit, true);
   }

   private final void setPermissions(UserSetting old, UserSetting target, UserSetting source, boolean commit, boolean applyToSiblings) {
      target.setPermissions(source);
      if (old != null) {
         this._sync.settingUpdated(old, target);
      }

      if (applyToSiblings) {
         this.applyToSiblings(target);
      }

      if (commit) {
         this.commit();
      }
   }

   final void setPermissions(UserSetting us, long perms) {
      this.setPermissions(null, us, perms);
   }

   final void setPermissions(UserSetting old, UserSetting us, long perms) {
      us.setPermissions(perms);
      if (old != null) {
         this._sync.settingUpdated(old, us);
      }

      this.applyToSiblings(us);
      this.commit();
   }

   final void setPermissions(UserSetting us, long permissions, long dontPrompt, long isSet) {
      this.setPermissions(null, us, permissions, dontPrompt, isSet);
   }

   final void setPermissions(UserSetting old, UserSetting us, long permissions, long dontPrompt, long isSet) {
      this.setPermissions(old, us, permissions, dontPrompt, isSet, true);
   }

   final void setPermissions(UserSetting old, UserSetting us, long permissions, long dontPrompt, long isSet, boolean commit) {
      us.setPermissions(permissions, dontPrompt, isSet);
      if (old != null) {
         this._sync.settingUpdated(old, us);
      }

      this.applyToSiblings(us);
      if (commit) {
         this.commit();
      }
   }

   final void resetPrompt(UserSetting old, UserSetting us, long mask) {
      this.resetPrompt(old, us, mask, true);
   }

   final void resetPrompt(UserSetting old, UserSetting us, long mask, boolean commit) {
      us.resetPrompt(mask);
      if (old != null) {
         this._sync.settingUpdated(old, us);
      }

      this.applyToSiblings(us);
      if (commit) {
         this.commit();
      }
   }

   final void resetPrompts(UserSetting old, UserSetting us) {
      this.resetPrompts(old, us, true);
   }

   final void resetPrompts(UserSetting old, UserSetting us, boolean commit) {
      us.resetPrompts();
      if (old != null) {
         this._sync.settingUpdated(old, us);
      }

      this.applyToSiblings(us);
      if (commit) {
         this.commit();
      }
   }

   private final void applyToSiblings(UserSetting us) {
      int handle = CodeModuleManager.getModuleHandle(us.getHash());
      this.applyToSiblings(handle, us);
   }

   private final void applyToSiblings(int handle, UserSetting us) {
      this.applyToSiblings(handle, us, false);
   }

   private final void removeSiblings(UserSetting us) {
      int handle = CodeModuleManager.getModuleHandle(us.getHash());
      this.removeSiblings(handle, us);
   }

   private final void removeSiblings(int handle, UserSetting us) {
      this.applyToSiblings(handle, us, true);
   }

   private final void applyToSiblings(int handle, UserSetting us, boolean doRemove) {
      if (handle != 0 && handle != -1) {
         boolean remove = doRemove | us.getIsSet() == 0;
         int[] handles = CodeStore.getSiblingHandles(handle);
         byte[] hash = new byte[20];
         int count = handles == null ? 0 : handles.length;
         if (count > 1) {
            for (int i = 0; i < count; i++) {
               int sibling = handles[i];
               UserSetting sus = this.getSetting(sibling);
               if (sus == null) {
                  if (CodeModuleManager.getModuleHash(sibling, hash) && !remove && this.getSetting(hash) == null) {
                     sus = new UserSetting(hash, us.getPermissions(), us.getDontPrompt(), us.getIsSet());
                     this.putSetting(sibling, sus, false, false);
                  }
               } else if (remove) {
                  ApplicationControlImpl.removeUserSetting(sibling);
               } else {
                  UserSetting oldUS = new UserSetting(sus);
                  this.setPermissions(oldUS, sus, us, false, false);
               }
            }
         }
      }
   }
}
