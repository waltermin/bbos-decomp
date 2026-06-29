package net.rim.device.internal.io.file;

import net.rim.device.api.io.file.FileSystemJournalListener;
import net.rim.vm.WeakReference;

class FileSystem$JournalChanged implements Runnable {
   private FileSystemJournalListener _listener;
   private final FileSystem this$0;

   public FileSystem$JournalChanged(FileSystem _1, FileSystemJournalListener listener) {
      this.this$0 = _1;
      this._listener = listener;
   }

   @Override
   public void run() {
      synchronized (this.this$0) {
         for (int i = this.this$0._fileJournalListeners.length - 1; i >= 0; i--) {
            Object obj = FileSystem._fileSystem._fileJournalListeners[i];
            if (obj == this._listener || obj instanceof WeakReference && ((WeakReference)obj).get() == this._listener) {
               this.this$0._fileJournalEventPending[i] = false;
               break;
            }
         }
      }

      this._listener.fileJournalChanged();
   }
}
