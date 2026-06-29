package net.rim.device.api.io;

import java.io.IOException;
import net.rim.device.api.io.file.FileIOException;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.internal.applicationcontrol.ApplicationControl;
import net.rim.device.internal.io.file.FileSystem;
import net.rim.device.internal.io.file.FileSystemInfo;
import net.rim.device.internal.system.EventDispatchManager;

public final class File {
   public static final int FILESYSTEM_PATRIOT = 0;

   private File() {
   }

   private static final void assertPermission() {
      ApplicationControl.assertIPCAllowed(true);
   }

   public static final boolean isSupported() {
      return RadioInfo.areWAFsSupported(8) && FileSystem.isSupported();
   }

   public static final boolean isFileSystemSupported(int fs) {
      return isSupported() ? FileSystem.isFileSystemSupported(fs) : false;
   }

   public static final void delete(int fs, String fileName) {
      if (fs != 0) {
         throw new IllegalArgumentException("Wrong API, use javax.microedition.io.file");
      }

      assertPermission();
      FileEventDispatcher dispatcher = getEventDispatcher();
      synchronized (dispatcher) {
         int status = FileSystem.delete(fs, fileName);
         if (!checkStatus(status)) {
            status = dispatcher.waitForCompletion(6);
            checkStatus(status);
         }
      }
   }

   public static final void rename(int fs, String oldFileName, String newFileName) {
      if (fs != 0) {
         throw new IllegalArgumentException("Wrong API, use javax.microedition.io.file");
      }

      assertPermission();
      FileEventDispatcher dispatcher = getEventDispatcher();
      synchronized (dispatcher) {
         int status = FileSystem.rename(fs, oldFileName, newFileName);
         if (!checkStatus(status)) {
            status = dispatcher.waitForCompletion(5);
            checkStatus(status);
         }
      }
   }

   public static final long getFileSystemTotalSpace(int fs) {
      if (fs != 0) {
         throw new IllegalArgumentException("Wrong API, use javax.microedition.io.file");
      }

      FileSystemInfo fsInfo = getFileSystemInfo(fs);
      return fsInfo.getTotalSpace();
   }

   public static final long getFileSystemFreeSpace(int fs) {
      if (fs != 0) {
         throw new IllegalArgumentException("Wrong API, use javax.microedition.io.file");
      }

      FileSystemInfo fsInfo = getFileSystemInfo(fs);
      return fsInfo.getFreeSpace();
   }

   public static final long getFileSize(int fs, String fileName) {
      if (fs != 0) {
         throw new IllegalArgumentException("Wrong API, use javax.microedition.io.file");
      }

      FileInfo fileInfo = new FileInfo();
      return findFirst(fs, fileName, fileInfo) ? fileInfo.getFileSize() : 0;
   }

   public static final String findFirst(int fs, String pattern) {
      if (fs != 0) {
         throw new IllegalArgumentException("Wrong API, use javax.microedition.io.file");
      }

      FileInfo fileInfo = new FileInfo();
      return findFirst(fs, pattern, fileInfo) ? fileInfo.getFileName() : null;
   }

   public static final boolean findFirst(int fs, String pattern, FileInfo fileInfo) {
      if (fs != 0) {
         throw new IllegalArgumentException("Wrong API, use javax.microedition.io.file");
      }

      assertPermission();
      FileEventDispatcher dispatcher = getEventDispatcher();
      synchronized (dispatcher) {
         while (true) {
            int status = (int)FileSystem.findFirst(fs, pattern, fileInfo);
            if (checkStatus(status)) {
               return fileInfo.getFileName() != null;
            }

            status = dispatcher.waitForCompletion(7);
            checkStatus(status);
         }
      }
   }

   public static final String findNext(int fs) {
      if (fs != 0) {
         throw new IllegalArgumentException("Wrong API, use javax.microedition.io.file");
      }

      FileInfo fileInfo = new FileInfo();
      return findNext(fs, fileInfo) ? fileInfo.getFileName() : null;
   }

   public static final boolean findNext(int fs, FileInfo fileInfo) {
      if (fs != 0) {
         throw new IllegalArgumentException("Wrong API, use javax.microedition.io.file");
      }

      assertPermission();
      FileEventDispatcher dispatcher = getEventDispatcher();
      synchronized (dispatcher) {
         while (true) {
            int status = FileSystem.findNext(fs, 0, fileInfo);
            if (checkStatus(status)) {
               return fileInfo.getFileName() != null;
            }

            status = dispatcher.waitForCompletion(8);
            checkStatus(status);
         }
      }
   }

   static final FileSystemInfo getFileSystemInfo(int fs) {
      if (fs != 0) {
         throw new IllegalArgumentException("Wrong API, use javax.microedition.io.file");
      }

      FileEventDispatcher dispatcher = getEventDispatcher();
      synchronized (dispatcher) {
         FileSystemInfo fsInfo = new FileSystemInfo();

         while (true) {
            int status = FileSystem.getFileSystemInfo(fs, fsInfo);
            if (checkStatus(status)) {
               return fsInfo;
            }

            status = dispatcher.waitForCompletion(0);
            checkStatus(status);
         }
      }
   }

   static final int open(int fs, String fileName, int mode) {
      if (fs != 0) {
         throw new IllegalArgumentException("Wrong API, use javax.microedition.io.file");
      }

      assertPermission();
      FileEventDispatcher dispatcher = getEventDispatcher();
      synchronized (dispatcher) {
         while (true) {
            long result = FileSystem.open(fs, fileName, mode);
            int status = (int)result;
            if (checkStatus(status)) {
               return (int)(result >> 32);
            }

            status = dispatcher.waitForCompletion(1);
            checkStatus(status);
         }
      }
   }

   static final void close(int handle) {
      assertPermission();
      FileEventDispatcher dispatcher = getEventDispatcher();
      synchronized (dispatcher) {
         if (handle == -1) {
            throw new IOException("File not open");
         }

         int status = FileSystem.close(handle);
         if (!checkStatus(status)) {
            status = dispatcher.waitForCompletion(2);
            checkStatus(status);
         }
      }
   }

   static final boolean checkStatus(int status) throws FileIOException {
      switch (status) {
         case -1:
            throw new FileIOException(status);
         case 0:
         default:
            return true;
         case 1:
            return false;
      }
   }

   static final FileEventDispatcher getEventDispatcher() {
      EventDispatchManager dispatchManager = EventDispatchManager.getInstance();
      synchronized (dispatchManager) {
         FileEventDispatcher dispatcher = (FileEventDispatcher)dispatchManager.getDispatcher(19);
         if (dispatcher == null) {
            dispatcher = new FileEventDispatcher();
            dispatchManager.setDispatcher(19, dispatcher);
         }

         return dispatcher;
      }
   }

   static final long read(int handle, byte[] data) {
      assertPermission();
      return FileSystem.read(handle, data);
   }

   static final int write(int handle, byte[] data, int length) {
      assertPermission();
      return FileSystem.write(handle, data, length);
   }

   static {
      if (isFileSystemSupported(0)) {
         try {
            getFileSystemInfo(0);
            return;
         } catch (IOException var1) {
         }
      }
   }
}
