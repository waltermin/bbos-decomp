package net.rim.apps.internal.explorer.IndexService;

import net.rim.device.internal.io.file.FileUtilities;

final class FileScanner$ScanQueue {
   int _start;
   int _end;
   int[] _events;
   String[] _paths;
   String[] _names;
   private final FileScanner this$0;

   FileScanner$ScanQueue(FileScanner _1, int initialCapacity) {
      this.this$0 = _1;
      this._events = new int[initialCapacity];
      this._paths = new String[initialCapacity];
      this._names = new String[initialCapacity];
   }

   final boolean isEmpty() {
      return this._start == this._end;
   }

   final void removePath(String path, int fileNameOffset) {
      int end = this._end;
      int i = this._start;
      if (i != end) {
         int insertPos = i;
         int size = this._events.length;
         String[] paths = this._paths;
         String[] names = this._names;
         int pathOffset = path.startsWith(FileScanner.FILE_COLON_SLASH_SLASH) ? FileScanner.FILE_COLON_SLASH_SLASH.length() : 0;
         int pathLen = path.length() - pathOffset;

         do {
            boolean remove = false;
            String checkPath = paths[i];
            if (checkPath != null) {
               int checkOffset = checkPath.startsWith(FileScanner.FILE_COLON_SLASH_SLASH) ? FileScanner.FILE_COLON_SLASH_SLASH.length() : 0;
               if (fileNameOffset == 0) {
                  remove = path.regionMatches(true, pathOffset, checkPath, checkOffset, pathLen);
               } else {
                  String checkName = names[i];
                  if (checkName == null) {
                     remove = path.regionMatches(true, pathOffset, checkPath, checkOffset, Math.max(pathLen, checkPath.length() - checkOffset));
                  } else {
                     remove = path.regionMatches(
                           true, pathOffset, checkPath, checkOffset, Math.max(fileNameOffset - pathOffset, checkPath.length() - checkOffset)
                        )
                        && path.regionMatches(true, fileNameOffset, checkName, 0, Math.max(path.length() - fileNameOffset, checkName.length()));
                  }
               }
            }

            if (!remove) {
               if (insertPos != i) {
                  this._events[insertPos] = this._events[i];
                  paths[insertPos] = checkPath;
                  names[insertPos] = names[i];
               }

               if (++insertPos >= size) {
                  insertPos = 0;
               }
            }

            if (++i >= size) {
               i = 0;
            }
         } while (i != end);

         this._end = insertPos;

         while (insertPos != i) {
            paths[insertPos] = null;
            names[insertPos] = null;
            if (++insertPos >= size) {
               insertPos = 0;
            }
         }
      }
   }

   final void enqueue(int eventType, String path, String name) {
      int start = this._start;
      int end = this._end;
      this._events[end] = eventType;
      this._paths[end] = path;
      this._names[end] = name;
      if (++end >= this._events.length) {
         end = 0;
      }

      if (end == start) {
         int length = this._events.length;
         if (length >= 1000) {
            this._paths[start] = null;
            this._names[start] = null;
            if (this._events[start] == 5) {
               this.this$0._service.processScanComplete();
            }

            if (++start == length) {
               start = 0;
            }

            this._start = start;
         } else {
            int newLength = length + 50;
            this._events = (int[])this.growFullArray(this._events, new int[newLength], length);
            this._paths = (String[])this.growFullArray(this._paths, new String[newLength], length);
            this._names = (String[])this.growFullArray(this._names, new String[newLength], length);
            this._start = 0;
            end = length;
         }
      }

      this._end = end;
   }

   final int dequeue() {
      int start = this._start;
      if (start == this._end) {
         return 0;
      }

      this.this$0._dequeuedPathURL = this._paths[start];
      if (this.this$0._dequeuedPathURL != null) {
         this.this$0._dequeuedPathURL = FileUtilities.makeFileURL(this.this$0._dequeuedPathURL);
         this._paths[start] = null;
      }

      this.this$0._dequeuedName = this._names[start];
      this._names[start] = null;
      int event = this._events[start];
      if (++start == this._events.length) {
         start = 0;
      }

      this._start = start;
      return event;
   }

   private final Object growFullArray(Object old, Object newArray, int oldCapacity) {
      System.arraycopy(old, this._start, newArray, 0, oldCapacity - this._start);
      System.arraycopy(old, 0, newArray, oldCapacity - this._start, this._start);
      return newArray;
   }
}
