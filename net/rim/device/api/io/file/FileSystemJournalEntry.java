package net.rim.device.api.io.file;

public final class FileSystemJournalEntry {
   private String _path;
   private String _oldPath;
   private int _event;
   private long _usn;
   public static final int FILE_ADDED;
   public static final int FILE_DELETED;
   public static final int FILE_CHANGED;
   public static final int FILE_RENAMED;

   private FileSystemJournalEntry() {
   }

   private FileSystemJournalEntry(long usn, String path, String oldPath, int event) {
      this._usn = usn;
      this._path = path;
      this._oldPath = oldPath;
      this._event = event;
   }

   public final String getPath() {
      return this._path;
   }

   public final String getOldPath() {
      return this._oldPath;
   }

   public final int getEvent() {
      return this._event;
   }

   public final long getUSN() {
      return this._usn;
   }

   public static final FileSystemJournalEntry createEntry(long usn, String path, String oldPath, int event) {
      return new FileSystemJournalEntry(usn, path, oldPath, event);
   }
}
