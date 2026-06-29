package net.rim.device.apps.internal.calendar.viewer;

import java.util.TimeZone;
import net.rim.device.api.system.PersistentStore;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.apps.api.calendar.caldb.CalDB;

final class DeletePriorVerb$DeletePriorThread implements Runnable {
   private long _time;
   private CalDB[] _calDBs;
   private UiApplication _app;

   public DeletePriorVerb$DeletePriorThread(CalDB[] calDBs, long time, UiApplication app) {
      this._calDBs = calDBs;
      this._time = time;
      this._app = app;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void run() {
      DeletePriorVerb$BusyDialog dialog = null;
      dialog = new DeletePriorVerb$BusyDialog(this._app);
      dialog.show();

      label99:
      try {
         for (int i = 0; i < this._calDBs.length; i++) {
            synchronized (this._calDBs[i].getLockObject()) {
               synchronized (PersistentStore.getSynchObject()) {
                  this._calDBs[i].suspendNotification(null);
                  boolean var16 = false /* VF: Semaphore variable */;

                  try {
                     var16 = true;
                     this._calDBs[i].removePrior(this._time, TimeZone.getDefault());
                     var16 = false;
                  } finally {
                     if (var16) {
                        this._calDBs[i].resumeNotification(null);
                     }
                  }

                  this._calDBs[i].resumeNotification(null);
               }
            }
         }
      } finally {
         break label99;
      }

      dialog.hide();
   }
}
