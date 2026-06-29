package net.rim.device.apps.internal.explorer.file;

import net.rim.device.api.collection.ReadableList;
import net.rim.device.api.collection.util.UnsortedReadableList;
import net.rim.device.api.io.file.FileSystemJournalEntry;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.internal.io.file.FileUtilities;

final class ExploreManager$UpdateFolderList implements Runnable {
   private final FileSystemJournalEntry _journalEntry;
   private UnsortedReadableList _containedList;
   private final ExploreManager this$0;

   ExploreManager$UpdateFolderList(ExploreManager _1, FileSystemJournalEntry entry) {
      this.this$0 = _1;
      this._journalEntry = entry;
   }

   @Override
   public final void run() {
      switch (this._journalEntry.getEvent()) {
         case -1:
            break;
         case 0:
         default:
            FileItemField fileItem = new FileItemField(this._journalEntry.getPath());
            if ((!fileItem.isHidden() || !this.this$0._filter.isHideFilteredOn()) && this.this$0.match(this.this$0._filter, fileItem)) {
               ReadableList list = fileItem.isDirectory() ? this.this$0._shortcuts.getList() : this.this$0._gallery.getList();
               if (list instanceof Object) {
                  ((UnsortedReadableList)list).elementAdded(null, fileItem);
                  return;
               }
            }
            break;
         case 1:
            FileItemField deletedItem = this.findByName(this._journalEntry.getPath(), null);
            if (deletedItem != null) {
               this._containedList.elementRemoved(null, deletedItem);
               return;
            }
            break;
         case 2:
            String changedPath = this._journalEntry.getPath();
            if (!FileUtilities.isDirectory(changedPath)) {
               FileItemField changedItem = this.findByName(this._journalEntry.getPath(), this.this$0._gallery.getList());
               if (changedItem != null) {
                  changedItem.refreshDetailInfo();
                  this._containedList.elementUpdated(null, changedItem, changedItem);
               }
            }
            break;
         case 3:
            String renamedToPath = this._journalEntry.getPath();
            FileItemField oldItem = this.findByName(this._journalEntry.getOldPath(), null);
            FileItemField overwrittenItem = this.findByName(renamedToPath, null);
            if (overwrittenItem != null && oldItem != null && overwrittenItem != oldItem) {
               this._containedList.elementRemoved(null, overwrittenItem);
            }

            FileItemField newItem = null;
            if (this._containedList != null && FileUtilities.getPathURL(renamedToPath).equals(this.this$0.getCurrentView().getURL())) {
               newItem = new FileItemField(renamedToPath);
            }

            if (oldItem != null) {
               if (newItem != null) {
                  this._containedList.elementUpdated(null, oldItem, newItem);
                  return;
               }

               this._containedList.elementRemoved(null, oldItem);
               return;
            }

            if (newItem != null) {
               ReadableList list = newItem.isDirectory() ? this.this$0._shortcuts.getList() : this.this$0._gallery.getList();
               if (list instanceof Object) {
                  ((UnsortedReadableList)list).elementAdded(null, newItem);
                  return;
               }
            }
      }
   }

   private final FileItemField findByName(String path, ReadableList list) {
      if (list == null) {
         list = FileUtilities.isDirectory(path) ? this.this$0._shortcuts.getList() : this.this$0._gallery.getList();
      }

      if (list instanceof Object) {
         UnsortedReadableList itemList = (UnsortedReadableList)list;
         String name = FileUtilities.getName(path);
         int i = itemList.size();

         while (--i >= 0) {
            FileItemField item = (FileItemField)itemList.getAt(i);
            if (FileUtilities.filenamesMatch(name, item.getName()) && StringUtilities.compareToIgnoreCase(path, item.getFullPath()) == 0) {
               this._containedList = itemList;
               return item;
            }
         }
      }

      return null;
   }
}
