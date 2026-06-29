package net.rim.device.apps.internal.explorer.file;

import java.util.Vector;
import net.rim.device.api.collection.util.SortedReadableList;

final class ExploreManager$AddElementsTask implements Runnable {
   private Vector _addedShortcuts;
   private Vector _addedFiles;
   private SortedReadableList _shortcutList;
   private SortedReadableList _fileList;
   private final ExploreManager this$0;

   ExploreManager$AddElementsTask(ExploreManager _1, Vector addedShortcuts, Vector addedFiles, SortedReadableList shortcutList, SortedReadableList fileList) {
      this.this$0 = _1;
      this._addedShortcuts = addedShortcuts;
      this._addedFiles = addedFiles;
      this._shortcutList = shortcutList;
      this._fileList = fileList;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void run() {
      this.this$0._shortcuts.setBulkAddMode(true);
      boolean var11 = false /* VF: Semaphore variable */;

      try {
         var11 = true;
         SortedReadableList fileList = this._shortcutList;
         Vector addedFiles = this._addedShortcuts;
         int i = addedFiles.size();

         while (--i >= 0) {
            fileList.elementAdded(null, addedFiles.elementAt(i));
         }

         var11 = false;
      } finally {
         if (var11) {
            this.this$0._shortcuts.setBulkAddMode(false);
         }
      }

      this.this$0._shortcuts.setBulkAddMode(false);
      this.this$0._gallery.setBulkAddMode(true);
      boolean var8 = false /* VF: Semaphore variable */;

      try {
         var8 = true;
         SortedReadableList var14 = this._fileList;
         Vector var15 = this._addedFiles;
         int var16 = var15.size();

         while (--var16 >= 0) {
            var14.elementAdded(null, var15.elementAt(var16));
         }

         var8 = false;
      } finally {
         if (var8) {
            this.this$0._gallery.setBulkAddMode(false);
         }
      }

      this.this$0._gallery.setBulkAddMode(false);
      if (!this.this$0._openDirTask.isDone()) {
         this.this$0._thumbnailFetcher.setTask(this.this$0._openDirTask);
      }

      ExploreManager.access$500(this.this$0);
      this.this$0.invalidate();
   }
}
