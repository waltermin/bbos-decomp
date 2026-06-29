package net.rim.device.apps.internal.explorer.file.options;

import net.rim.device.api.util.IntIntHashtable;
import net.rim.vm.Persistable;

final class ExplorerOptions$PersistedExplorerOptions implements Persistable {
   int _slideShowDisplayTime = 2;
   int _numberOfColumns = ExplorerOptions.DEFAULT_NUMBER_OF_COLUMNS;
   int _filelistSortProperty = 0;
   int _filelistSortDirection = 1;
   IntIntHashtable _viewMode = (IntIntHashtable)(new Object());

   ExplorerOptions$PersistedExplorerOptions() {
      this._viewMode.put(0, 1);
      this._viewMode.put(1, 0);
      this._viewMode.put(2, 2);
      this._viewMode.put(3, 1);
      this._viewMode.put(4, 2);
   }
}
