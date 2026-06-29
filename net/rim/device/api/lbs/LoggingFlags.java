package net.rim.device.api.lbs;

import net.rim.device.api.util.Persistable;

final class LoggingFlags implements Persistable {
   private long _flags;

   final void setFlag(int type) {
      this._flags |= type;
   }

   final boolean getFlag(int type) {
      return (this._flags & type) != 0;
   }

   final void clearFlag(int type) {
      this._flags &= ~type;
   }
}
