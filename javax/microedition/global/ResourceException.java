package javax.microedition.global;

public final class ResourceException extends RuntimeException {
   private int _err;
   public static final int UNKNOWN_ERROR = 0;
   public static final int RESOURCE_NOT_FOUND = 1;
   public static final int WRONG_RESOURCE_TYPE = 2;
   public static final int NO_RESOURCES_FOR_BASE_NAME = 3;
   public static final int NO_SYSTEM_DEFAULT_LOCALE = 4;
   public static final int DATA_ERROR = 5;
   public static final int UNKNOWN_RESOURCE_TYPE = 6;
   public static final int METAFILE_NOT_FOUND = 7;
   private static final int MAX_ERROR_CODE = 7;

   public ResourceException(int err, String message) {
      super(message);
      if (err >= 0 && err <= 7) {
         this._err = err;
      } else {
         throw new IllegalArgumentException();
      }
   }

   public final int getErrorCode() {
      return this._err;
   }
}
