package net.rim.device.internal.applicationcontrol;

import net.rim.device.api.synchronization.SyncObject;
import net.rim.device.api.synchronization.UIDGenerator;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.Persistable;

final class UserSetting implements Persistable, SyncObject {
   private byte[] _hash;
   private long _permissions;
   private long _dontPrompt;
   private long _isSet;
   private int _uid;
   private static final long USER_PERMS_KEY;
   static final int VERSION;
   static final int HASH;
   static final int PERMS;
   static final int DONTPROMPT;
   static final int ISSET;
   static final int L_PERMS;
   static final int L_DONTPROMPT;
   static final int L_ISSET;

   final void setPermissions(UserSetting us) {
      this.setPermissions(us._permissions, us._dontPrompt, us._isSet);
   }

   final long getPermissions() {
      return this._permissions;
   }

   final void setPermissions(long perms) {
      this._permissions = perms;
   }

   @Override
   public final int getUID() {
      return this._uid;
   }

   final void setPermissions(long permissions, long dontPrompt, long isSet) {
      this._permissions = permissions;
      this._dontPrompt = dontPrompt;
      this._isSet = isSet;
   }

   final boolean hashEquals(byte[] hash) {
      return Arrays.equals(this._hash, hash);
   }

   final byte[] getHash() {
      return this._hash;
   }

   final long getIsSet() {
      return this._isSet;
   }

   final long getDontPrompt() {
      return this._dontPrompt;
   }

   final void resetPrompt(long mask) {
      this._permissions = this._permissions | this._dontPrompt & mask;
      this._dontPrompt &= mask ^ -1;
   }

   final void resetPrompts() {
      this._permissions = this._permissions | this._dontPrompt;
      this._dontPrompt = 0;
   }

   UserSetting(byte[] moduleHash, long perms) {
      this._hash = new byte[moduleHash.length];
      System.arraycopy(moduleHash, 0, this._hash, 0, moduleHash.length);
      this._permissions = perms;
      this._dontPrompt = 0;
      this._isSet = 0;
      this._uid = Arrays.equals(this._hash, ApplicationControlConstants.EMPTY_HASH) ? 1 : UIDGenerator.getUID();
   }

   UserSetting(UserSetting us) {
      this(us._hash, us._permissions, us._dontPrompt, us._isSet, us._uid);
   }

   UserSetting(byte[] moduleHash, long perms, long dontprompt, long isSet) {
      this._hash = new byte[moduleHash.length];
      System.arraycopy(moduleHash, 0, this._hash, 0, moduleHash.length);
      this._permissions = perms;
      this._dontPrompt = dontprompt;
      this._isSet = isSet;
      this._uid = UIDGenerator.getUID();
   }

   UserSetting(byte[] moduleHash, long perms, long dontprompt, long isSet, int uid) {
      this._hash = new byte[moduleHash.length];
      System.arraycopy(moduleHash, 0, this._hash, 0, moduleHash.length);
      this._permissions = perms;
      this._dontPrompt = dontprompt;
      this._isSet = isSet;
      this._uid = uid;
   }

   @Override
   public final boolean equals(Object obj) {
      if (obj == null) {
         return false;
      }

      if (this == obj) {
         return true;
      }

      if (!(obj instanceof UserSetting)) {
         return false;
      }

      UserSetting us = (UserSetting)obj;
      return Arrays.equals(us._hash, this._hash);
   }
}
