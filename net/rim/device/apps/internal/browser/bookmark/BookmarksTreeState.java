package net.rim.device.apps.internal.browser.bookmark;

import net.rim.device.api.util.Persistable;

class BookmarksTreeState implements Persistable {
   long[] _collapsedFolders;
   Persistable _selectedNode;

   BookmarksTreeState(long[] collapsedFolders, Persistable selectedNode) {
      this._collapsedFolders = collapsedFolders;
      this._selectedNode = selectedNode;
   }

   public long[] getCollpasedFolders() {
      return this._collapsedFolders;
   }

   public Persistable getSelectedNode() {
      return this._selectedNode;
   }
}
