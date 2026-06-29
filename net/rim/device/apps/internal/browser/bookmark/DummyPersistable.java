package net.rim.device.apps.internal.browser.bookmark;

import net.rim.device.api.util.Persistable;

class DummyPersistable implements Persistable {
   long _luid = -1;

   public DummyPersistable(long luid) {
      this._luid = luid;
   }

   public long getLuid() {
      return this._luid;
   }
}
