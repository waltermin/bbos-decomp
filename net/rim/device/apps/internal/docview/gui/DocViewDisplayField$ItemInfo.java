package net.rim.device.apps.internal.docview.gui;

public final class DocViewDisplayField$ItemInfo {
   private String _itemName;
   String _arbDOMID;
   byte _available;
   int _chunkHint;
   int _currentItemMaxBlockIndex;
   int _currentItemAutoLoadMaxBlockIndex;

   DocViewDisplayField$ItemInfo(String name, String domID, byte available, int chunkHint) {
      this._itemName = name;
      this._arbDOMID = domID;
      this._available = available;
      this._currentItemMaxBlockIndex = this._currentItemAutoLoadMaxBlockIndex = this._chunkHint = chunkHint;
   }

   @Override
   public final String toString() {
      return this._itemName;
   }
}
