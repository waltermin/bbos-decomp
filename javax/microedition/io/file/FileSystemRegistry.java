package javax.microedition.io.file;

import java.util.Enumeration;
import net.rim.device.api.system.Application;
import net.rim.device.internal.io.file.FileSystem;

public class FileSystemRegistry {
   FileSystemRegistry() {
   }

   public static boolean addFileSystemListener(FileSystemListener listener) {
      Application app;
      try {
         app = Application.getApplication();
      } catch (IllegalStateException e) {
         throw new SecurityException("No application available for callback");
      }

      return FileSystem.addFileSystemListener(listener, app, false);
   }

   public static boolean removeFileSystemListener(FileSystemListener listener) {
      return FileSystem.removeFileSystemListener(listener);
   }

   public static Enumeration listRoots() {
      return FileSystem.getRoots();
   }
}
