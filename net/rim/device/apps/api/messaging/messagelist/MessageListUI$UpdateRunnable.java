package net.rim.device.apps.api.messaging.messagelist;

import net.rim.device.apps.api.messaging.FolderHierarchies;

class MessageListUI$UpdateRunnable implements Runnable {
   private int _numEntries;
   private boolean _setNumEntries;
   private int _itemIndex;
   private boolean _updateItemIndex;
   private boolean _updateAllElements;
   private final MessageListUI this$0;

   MessageListUI$UpdateRunnable(MessageListUI _1) {
      this.this$0 = _1;
   }

   void setNumEntries(int numEntries) {
      synchronized (FolderHierarchies.getLockObject()) {
         synchronized (this) {
            this._numEntries = numEntries;
            this._setNumEntries = true;
         }
      }
   }

   void setItemIndex(int itemIndex) {
      synchronized (FolderHierarchies.getLockObject()) {
         synchronized (this) {
            if (this._updateItemIndex && itemIndex != this._itemIndex) {
               this._updateAllElements = true;
            } else {
               this._itemIndex = itemIndex;
               this._updateItemIndex = true;
            }
         }
      }
   }

   @Override
   public void run() {
      synchronized (FolderHierarchies.getLockObject()) {
         synchronized (this) {
            if (this._setNumEntries) {
               this.this$0._listField.setSize(this._numEntries, this.this$0._selectedIndexManager.getSelectedIndex());
               this._setNumEntries = false;
               this._updateItemIndex = false;
               this._updateAllElements = false;
            } else if (this._updateItemIndex) {
               if (this._updateAllElements) {
                  this.this$0._listField.invalidate();
                  this._updateAllElements = false;
               } else {
                  this.this$0._listField.invalidate(this._itemIndex);
               }

               this._updateItemIndex = false;
            }
         }
      }
   }
}
