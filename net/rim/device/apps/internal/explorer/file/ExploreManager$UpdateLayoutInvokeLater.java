package net.rim.device.apps.internal.explorer.file;

import net.rim.device.api.system.Application;

final class ExploreManager$UpdateLayoutInvokeLater implements Runnable {
   private boolean _updatePending;
   private ExploreManager$PathStackElement _savedView;
   private boolean _dirOpenComplete;
   private final ExploreManager this$0;

   private ExploreManager$UpdateLayoutInvokeLater(ExploreManager _1) {
      this.this$0 = _1;
   }

   final void invokeLater() {
      if (!this._updatePending) {
         this._updatePending = true;
         Application.getApplication().invokeLaterInternal(this, 50, false);
      }
   }

   final void setSavedView(ExploreManager$PathStackElement savedView) {
      this._savedView = savedView;
   }

   final void dirOpenComplete() {
      this._dirOpenComplete = true;
   }

   @Override
   public final void run() {
      this._updatePending = false;
      if (this._dirOpenComplete) {
         this._dirOpenComplete = false;
         if (this._savedView != null) {
            ExploreManager$PathStackElement savedView = this._savedView;
            FileConnectionHolder fileItem = savedView._selectedItem;
            FolderList folderList = savedView._folderList;
            if (fileItem != null && !folderList.isUserSelected() && folderList.setSelectedItem(fileItem.getName()) >= 0) {
               this._savedView = null;
               this.this$0.setFieldWithFocus(savedView._folderList);
            }
         }
      }

      this.this$0.invalidate();
      ExploreManager.access$1000(this.this$0);
   }

   ExploreManager$UpdateLayoutInvokeLater(ExploreManager x0, ExploreManager$1 x1) {
      this(x0);
   }
}
