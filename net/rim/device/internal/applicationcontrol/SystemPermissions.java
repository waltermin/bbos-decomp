package net.rim.device.internal.applicationcontrol;

import net.rim.device.api.itpolicy.ITPolicy;
import net.rim.device.api.util.Persistable;
import net.rim.device.internal.system.CodeStore;
import net.rim.device.internal.system.ITPolicyInternal;

final class SystemPermissions implements Persistable {
   private long _defaultPermissions;
   private long _permittedPermissions;
   private boolean _AppControlPolicyDataPresent;
   private boolean _disableServiceBookXmit;
   private int _crc;
   private int[] _handles;
   private int _check = 0;

   final long getDefaultPermissions() {
      return this._defaultPermissions;
   }

   final long getPermittedPermissions() {
      return this._permittedPermissions;
   }

   final int[] getHandles() {
      return this._handles;
   }

   final boolean areValid(int crc) {
      return this._crc == crc;
   }

   final void invalidate() {
      this._crc++;
   }

   final boolean isAppControlPolicyDataPresent() {
      return this._AppControlPolicyDataPresent;
   }

   final void disableXmit() {
      this._disableServiceBookXmit = true;
   }

   final boolean isXmitDisabled() {
      return this._disableServiceBookXmit;
   }

   final int getCheck() {
      return this._check;
   }

   final void init() {
      if (this._handles == null) {
         this._handles = new int[0];
      }

      this._crc = CodeStore.getModuleHandles(this._handles);
      this._check++;
      this._defaultPermissions = this.getDefaults();
      System.out.println("DEFAULT POLICY PERMISSIONS: " + Long.toString(this._defaultPermissions, 16));
      this._permittedPermissions = this._defaultPermissions;
      this._AppControlPolicyDataPresent = ITPolicy.getByteArray(24, 25) != null;
      if (!this._AppControlPolicyDataPresent) {
         byte[] defaults = getConsumerDefaults();
         this._permittedPermissions = this.permsIntoMask(defaults);
      }

      System.out.println("PERMITTED POLICY PERMISSIONS: " + Long.toString(this._permittedPermissions, 16));
   }

   final int[] loadHandles() {
      int[] oldHandles = this._handles;
      this._handles = new int[0];
      this._crc = CodeStore.getModuleHandles(this._handles);
      return oldHandles;
   }

   private final long getDefaults() {
      byte[] defaults;
      if (ITPolicyInternal.isITPolicyEnabled()) {
         defaults = getDefaultPermissionsImpl();
      } else {
         defaults = getCorporateDefaults();
      }

      return this.permsIntoMask(defaults);
   }

   private final long permsIntoMask(byte[] perms) {
      long mask = 0;

      for (int i = 0; i < perms.length; i++) {
         mask <<= 8;
         mask |= perms[i] & 0xFF;
      }

      if ((mask & Integer.MAX_VALUE) != 0) {
         System.out.println("Invalid Permissions! " + Long.toString(mask, 16));
         mask &= Integer.MIN_VALUE;
      }

      return mask;
   }

   private static final native byte[] getDefaultPermissionsImpl();

   private static final native byte[] getConsumerDefaults();

   private static final native byte[] getCorporateDefaults();
}
