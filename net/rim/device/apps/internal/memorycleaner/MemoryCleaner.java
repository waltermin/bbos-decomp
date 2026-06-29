package net.rim.device.apps.internal.memorycleaner;

import net.rim.device.api.memorycleaner.MemoryCleanerListener;
import net.rim.device.api.memorycleaner.MemoryCleanerManager;

public final class MemoryCleaner {
   private MemoryCleanerDialog _dialog;
   private MemoryCleanerListener[] _listeners = MemoryCleanerManager.getInstance().getListeners();
   private boolean _runFromRibbon;

   public final void start(boolean runFromRibbon) {
      this._runFromRibbon = runFromRibbon;
      this._dialog = new MemoryCleanerDialog(this, this._listeners);
      this._dialog.show();
   }

   public final void dialogDisplayed() {
      new MemoryCleaner$Cleaner(this).start();
   }
}
