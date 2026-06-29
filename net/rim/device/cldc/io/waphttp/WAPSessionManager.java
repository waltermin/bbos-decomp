package net.rim.device.cldc.io.waphttp;

import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.RIMPersistentStore;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.Persistable;

final class WAPSessionManager implements Persistable {
   private WAPSession[] _sessions = new WAPSession[0];
   private static final long ID;
   private static PersistentObject _persist;

   private WAPSessionManager() {
   }

   public static final WAPSessionManager getInstance() {
      _persist = RIMPersistentStore.getPersistentObject(1236620482268733072L);
      WAPSessionManager store = null;
      synchronized (_persist) {
         if (!(_persist.getContents() instanceof WAPSessionManager)) {
            store = new WAPSessionManager();
            _persist.setContents(store, 51);
            _persist.commit();
         } else {
            store = (WAPSessionManager)_persist.getContents();
         }

         return store;
      }
   }

   public final void addSession(WAPSession newSession) {
      if (!newSession.checkCrypt()) {
         newSession.reCrypt();
      }

      String key = newSession.getKey();
      int count = this._sessions.length;

      for (int i = 0; i < count; i++) {
         if (this._sessions[i].equals(key)) {
            this._sessions[i] = newSession;
            _persist.commit();
            return;
         }
      }

      Arrays.add(this._sessions, newSession);
      _persist.commit();
   }

   public final WAPSession removeSession(String key) {
      int count = this._sessions.length;
      WAPSession result = null;

      int i;
      for (i = 0; i < count; i++) {
         if (this._sessions[i].equals(key)) {
            result = this._sessions[i];
            break;
         }
      }

      if (i != count) {
         Arrays.removeAt(this._sessions, i);
         _persist.commit();
      }

      return result;
   }
}
