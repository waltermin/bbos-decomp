package net.rim.device.api.io.file;

import net.rim.device.internal.applicationcontrol.ApplicationControl;
import net.rim.device.internal.io.file.FileSystem;

public final class FileSystemJournal {
   private FileSystemJournal() {
   }

   private static final void assertPermissions() {
      ApplicationControl.assertFileApiAllowed(true);
      ApplicationControl.assertIPCAllowed(true);
   }

   public static final long getNextUSN() {
      return FileSystem.getNextJournalUSN();
   }

   public static final FileSystemJournalEntry getEntry(long usn) {
      assertPermissions();
      return FileSystem.getJournalEntry(usn);
   }
}
