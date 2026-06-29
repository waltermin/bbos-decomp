package net.rim.device.apps.internal.qm.peer;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.util.IntHashtable;
import net.rim.vm.WeakReference;

final class MessageFieldLookup {
   private IntHashtable _lookup = (IntHashtable)(new Object());

   final void add(int cookie, Field field) {
      this._lookup.put(cookie, new Object(field));
   }

   final void remove(int cookie) {
      this._lookup.remove(cookie);
   }

   final void messageStateChange(int cookie, int state) {
      WeakReference weakReference = (WeakReference)this._lookup.get(cookie);
      if (weakReference != null) {
         Object field = weakReference.get();
         if (field == null) {
            this.remove(cookie);
         } else if (field instanceof TextMessageField) {
            TextMessageField messageField = (TextMessageField)field;
            synchronized (UiApplication.getUiApplication().getAppEventLock()) {
               messageField.updateStatus(state);
            }
         }
      }
   }
}
