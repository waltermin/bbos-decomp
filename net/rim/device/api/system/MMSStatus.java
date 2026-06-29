package net.rim.device.api.system;

import java.util.Vector;

final class MMSStatus {
   private boolean _hasServiceBook;
   private Vector _actions;
   private static final long MMS_STATUS_GUID;
   private static MMSStatus _instance;

   static final MMSStatus getInstance() {
      if (_instance == null) {
         ApplicationRegistry applicationRegistry = ApplicationRegistry.getApplicationRegistry();
         synchronized (applicationRegistry) {
            _instance = (MMSStatus)applicationRegistry.get(-5592630518458187252L);
            if (_instance == null) {
               _instance = new MMSStatus();
               applicationRegistry.put(-5592630518458187252L, _instance);
            }
         }
      }

      return _instance;
   }

   public final boolean hasServiceBook() {
      return this._hasServiceBook;
   }

   public final void setServiceBookStatus(boolean hasServiceBook) {
      Vector actions = null;
      synchronized (this) {
         this._hasServiceBook = hasServiceBook;
         if (MMS.isEnabled() && this._actions != null) {
            actions = this._actions;
            this._actions = null;
         }
      }

      if (actions != null) {
         runActions(actions);
      }
   }

   public final synchronized void onEnabled(Runnable action) {
      if (MMS.isEnabled()) {
         action.run();
      } else {
         if (this._actions == null) {
            this._actions = new Vector();
         }

         this._actions.addElement(action);
      }
   }

   private static final void runActions(Vector v) {
      int count = v.size();

      for (int idx = 0; idx < count; idx++) {
         Runnable action = (Runnable)v.elementAt(idx);
         action.run();
      }
   }
}
