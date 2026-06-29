package net.rim.device.internal.io.store;

import net.rim.device.api.io.IOUtilities;
import net.rim.device.api.io.file.FileSystemJournal;
import net.rim.device.api.io.file.FileSystemJournalEntry;
import net.rim.device.api.io.file.FileSystemJournalListener;
import net.rim.device.api.system.ObjectGroup;
import net.rim.device.api.ui.FontRegistry;

class AAALibrary$FontListener implements FileSystemJournalListener {
   ContentStoreImpl _store = ContentStoreImpl.getInstance();
   FolderImpl _folder;
   long _myStoredUSN = FileSystemJournal.getNextUSN();

   public void load(FileImpl descriptor) {
      try {
         byte[] bytes = IOUtilities.streamToBytes(descriptor.openInputStream());
         ObjectGroup.createGroup(bytes);
         String name = descriptor.getName();
         name = name.substring(0, name.lastIndexOf(46));
         FontRegistry.loadFont(bytes, name, true);
      } finally {
         return;
      }
   }

   public void reload() {
      this._folder = this._store.getFolder(AAALibrary.FONT_DIR);
      FileListImpl list = this._store.list(AAALibrary.FONT_DIR, null);
      int end = list.size();

      for (int lv = 0; lv < end; lv++) {
         FileImpl file = list.getAtFileDescriptor(lv);
         if (file != null) {
            this.load(file);
         }
      }
   }

   @Override
   public void fileJournalChanged() {
      long nextUSN = FileSystemJournal.getNextUSN();

      for (long lookUSN = nextUSN - 1; lookUSN >= this._myStoredUSN; lookUSN -= 1) {
         FileSystemJournalEntry entry = FileSystemJournal.getEntry(lookUSN);
         if (entry == null) {
            break;
         }

         String path = entry.getPath();
         if (path != null && path.startsWith(AAALibrary.STORE_FONT_DIR)) {
            this.reload();
            break;
         }
      }

      this._myStoredUSN = nextUSN;
   }
}
