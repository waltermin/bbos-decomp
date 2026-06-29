package net.rim.device.apps.api.messaging.messagelist;

import net.rim.device.api.system.Application;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.vm.WeakReference;

final class DSSMAGlobalEventListener implements GlobalEventListener {
   private WeakReference _dssmaRef;
   private Application _app;

   DSSMAGlobalEventListener(DateSortedSeparatedMessageArray dssma, Application app) {
      this._dssmaRef = (WeakReference)(new Object(dssma));
      this._app = app;
   }

   @Override
   public final void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (guid == 3596208183088439728L) {
         DateSortedSeparatedMessageArray dssma = (DateSortedSeparatedMessageArray)this._dssmaRef.get();
         if (dssma == null) {
            this._app.removeGlobalEventListener(this);
         } else {
            dssma.timezoneChanged();
         }
      } else {
         if ((guid == 945659952435832745L || guid == -1270659756336956134L) && this._dssmaRef.get() == null) {
            this._app.removeGlobalEventListener(this);
         }
      }
   }
}
