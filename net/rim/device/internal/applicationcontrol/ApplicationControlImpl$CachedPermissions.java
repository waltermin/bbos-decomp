package net.rim.device.internal.applicationcontrol;

import net.rim.device.api.util.Arrays;

final class ApplicationControlImpl$CachedPermissions {
   int[] _trace;
   long _perms;
   int _check;

   ApplicationControlImpl$CachedPermissions(int[] trace) {
      this._trace = trace;
   }

   final void setCachedPerms(long perms, int check) {
      this._perms = perms;
      this._check = check;
   }

   final boolean equals(ApplicationControlImpl$CachedPermissions cp) {
      return Arrays.equals(this._trace, cp._trace);
   }

   final boolean equals(int[] trace) {
      return Arrays.equals(this._trace, trace);
   }
}
