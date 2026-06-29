package net.rim.device.apps.internal.explorer.file;

import net.rim.device.apps.api.framework.file.FileSelectionFilter;

final class ExploreManager$PathStackElement {
   FileConnectionHolder _view;
   FileSelectionFilter _filter;
   int _selection;
   FileConnectionHolder _selectedItem;
   FolderList _folderList;
   RootItem _root;

   ExploreManager$PathStackElement(FileConnectionHolder view, FileSelectionFilter filter, RootItem root, FolderList folderList) {
      this._filter = filter;
      this._view = view;
      this._selection = folderList.getSelectedIndex();
      this._selectedItem = folderList.getSelectedItem();
      this._folderList = folderList;
      this._root = root;
   }
}
