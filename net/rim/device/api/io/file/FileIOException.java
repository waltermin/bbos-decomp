package net.rim.device.api.io.file;

import java.io.IOException;
import net.rim.device.api.i18n.ResourceBundle;

public class FileIOException extends IOException {
   private int _errorCode;
   public static final int INVALID_HANDLE = 2;
   public static final int INVALID_PARAMETER = 3;
   public static final int FILE_SYSTEM_UNAVAILABLE = 4;
   public static final int OS_BUSY = 5;
   public static final int GENERAL_ERROR = 6;
   public static final int FILENAME_ALREADY_EXISTS = 7;
   public static final int FILENAME_NOT_FOUND = 8;
   public static final int FILESYSTEM_FULL = 9;
   public static final int FILESYSTEM_EMPTY = 10;
   public static final int NO_FREE_HANDLES = 11;
   public static final int INVALID_OPERATION = 12;
   public static final int FILENAME_TOO_LONG = 13;
   public static final int DIRECTORY_FULL = 14;
   public static final int DIRECTORY_ALREADY_EXISTS = 15;
   public static final int DIRECTORY_NOT_FOUND = 16;
   public static final int DIRECTORY_NOT_EMPTY = 17;
   public static final int MEDIUM_NOT_FORMATTED = 18;
   public static final int FS_ALREADY_MOUNTED = 19;
   public static final int FILE_HANDLES_OPEN = 20;
   public static final int FS_NOT_MOUNTED = 21;
   public static final int FS_VERIFICATION_FAILED = 22;
   public static final int FILE_NOT_OPEN = 1000;
   public static final int NOT_A_FILE = 1001;
   public static final int NOT_A_DIRECTORY = 1002;
   public static final int NO_SUCH_ROOT = 1003;
   public static final int INVALID_CHARACTERS = 1004;
   public static final int STREAM_ALREADY_OPENED = 1005;
   public static final int IS_A_DIRECTORY = 1006;
   public static final int CONTENT_BUILT_IN = 1007;
   public static final int FILE_TOO_LARGE = 1008;
   private static ResourceBundle _rb = ResourceBundle.getBundle(-538091128501376688L, "net.rim.device.internal.resource.FileSystem");

   public FileIOException(int errorCode) {
      this._errorCode = errorCode;
   }

   public int getErrorCode() {
      return this._errorCode;
   }

   @Override
   public String getMessage() {
      int resCode;
      switch (this._errorCode) {
         case 4:
         case 5:
         case 18:
         case 19:
         case 21:
            resCode = 7;
            break;
         case 7:
            resCode = 1;
            break;
         case 8:
            resCode = 3;
            break;
         case 9:
            resCode = 0;
            break;
         case 10:
            resCode = 4;
            break;
         case 11:
         case 20:
            resCode = 6;
            break;
         case 13:
            resCode = 5;
            break;
         case 14:
            resCode = 8;
            break;
         case 15:
            resCode = 9;
            break;
         case 16:
            resCode = 10;
            break;
         case 17:
            resCode = 11;
            break;
         case 1008:
            resCode = 12;
            break;
         default:
            resCode = 2;
      }

      return _rb.getString(resCode);
   }
}
