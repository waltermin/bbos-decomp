package net.rim.device.apps.internal.mms.options;

import net.rim.vm.Persistable;

final class MMSOptionsData implements Persistable {
   public int _receptionMode;
   public int _retrievalMode;
   public int _flags;

   public final void setFlags(int mask) {
      this._flags |= mask;
   }

   public final void clearFlags(int mask) {
      this._flags &= ~mask;
   }
}
