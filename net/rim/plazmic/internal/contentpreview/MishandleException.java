package net.rim.plazmic.internal.contentpreview;

public final class MishandleException extends Exception {
   private Throwable _cause;
   public static final String rcsid;

   public MishandleException() {
   }

   public MishandleException(String message, Throwable cause) {
      super(message);
      this._cause = cause;
   }

   public MishandleException(Throwable cause) {
      this(cause == null ? null : cause.toString(), cause);
   }
}
