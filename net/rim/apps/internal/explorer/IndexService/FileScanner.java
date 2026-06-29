package net.rim.apps.internal.explorer.IndexService;

import net.rim.device.api.system.DeviceInfo;
import net.rim.device.internal.io.file.FileUtilities;

final class FileScanner extends Thread {
   private FileIndexServiceImpl _service;
   private String _dequeuedPathURL;
   private String _dequeuedName;
   private FileScanner$ScanQueue _queue;
   private FileScanner$ScanQueue _lowPriorityQueue;
   private static final int ADD_FOLDER_PATH = 1;
   private static final int ADD_FILE_PATH = 2;
   private static final int REMOVE_FOLDER_PATH = 3;
   private static final int REMOVE_FILE_PATH = 4;
   private static final int SCAN_COMPLETE = 5;
   private static final int RESCAN = 6;
   private static final int FILE_ADDED_JOURNAL = 7;
   private static final int FILE_CHANGED_JOURNAL = 8;
   private static final int MIN_LOW_PRIORITY_IDLE_SECONDS = 60;
   private static String FILE_COLON_SLASH_SLASH = "file://";

   FileScanner(FileIndexServiceImpl service) {
      this._service = service;
      this._queue = new FileScanner$ScanQueue(this, 50);
      this._lowPriorityQueue = new FileScanner$ScanQueue(this, 50);
      this.setPriority(this.getPriority() - 1);
   }

   public final void addFolderPath(String path) {
      this.enqueueNormal(1, path, null);
   }

   public final void addFilePath(int mediaType, String path) {
      this.addFilePath(mediaType, path, null);
   }

   public final void rescan(String path) {
      this.enqueueNormal(6, path, null);
   }

   public final void addFilePath(int mediaType, String path, String name) {
      if (mediaType != 1) {
         this.enqueueNormal(2, path, name);
      } else {
         synchronized (this) {
            this._lowPriorityQueue.enqueue(2, path, name);
            this.notify();
         }
      }
   }

   public final void addFileAddedJournal(String path) {
      this.enqueueNormal(7, path, null);
   }

   public final void addFileChangedJournal(String path) {
      this.enqueueNormal(8, path, null);
   }

   public final synchronized void removeFolderPath(String path) {
      this._queue.removePath(path, 0);
      this._lowPriorityQueue.removePath(path, 0);
      this.enqueueNormal(3, path, null);
   }

   public final void removeFilePath(int mediaType, String path) {
      if (mediaType != 1) {
         this.enqueueNormal(4, path, null);
      } else {
         int nameOffset = path.lastIndexOf(47);
         if (nameOffset > 0) {
            synchronized (this) {
               this._lowPriorityQueue.removePath(path, nameOffset);
               this.enqueueNormal(4, path, null);
            }
         }
      }
   }

   public final void scanComplete() {
      this.enqueueNormal(5, null, null);
   }

   @Override
   public final void run() {
      while (true) {
         switch (this.dequeue()) {
            case 0:
               break;
            case 1:
               this._service.processAddFolderPath(this._dequeuedPathURL);
               break;
            case 2: {
               String pathURL = this._dequeuedPathURL;
               String fileName = this._dequeuedName;
               if (fileName == null) {
                  fileName = FileUtilities.getName(this._dequeuedPathURL);
                  pathURL = pathURL.substring(0, pathURL.length() - fileName.length());
               }

               this._service.processAddFilePath(pathURL, fileName);
               break;
            }
            case 3:
               this._service.processRemoveFolderPath(this._dequeuedPathURL);
               break;
            case 4: {
               String fileName = FileUtilities.getName(this._dequeuedPathURL);
               String pathURL = this._dequeuedPathURL.substring(0, this._dequeuedPathURL.length() - fileName.length());
               this._service.processRemoveFilePath(pathURL, fileName);
               break;
            }
            case 5:
            default:
               this._service.processScanComplete();
               break;
            case 6:
               this._service.processRescan(this._dequeuedPathURL);
               break;
            case 7:
               this._service.processFileAddedJournal(this._dequeuedPathURL);
               break;
            case 8:
               this._service.processFileChangedJournal(this._dequeuedPathURL);
         }
      }
   }

   private final synchronized void enqueueNormal(int eventType, String path, String name) {
      this._queue.enqueue(eventType, path, name);
      this.notify();
   }

   private final synchronized int dequeue() {
      while (true) {
         int event = this._queue.dequeue();
         if (event != 0) {
            return event;
         }

         long idleTime = DeviceInfo.getIdleTime();
         if (idleTime >= 60) {
            event = this._lowPriorityQueue.dequeue();
            if (event != 0) {
               return event;
            }
         }

         try {
            if (this._lowPriorityQueue.isEmpty()) {
               this.wait();
            } else {
               this.wait(Math.max(1000, (60 - idleTime) * 1000));
            }
         } finally {
            continue;
         }
      }
   }
}
