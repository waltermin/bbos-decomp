package net.rim.plazmic.internal.contentpreview.service;

public final class ServiceException extends Exception {
   private Throwable _cause;
   public static final String rcsid;

   public ServiceException() {
   }

   public ServiceException(Throwable cause) {
      super(cause == null ? null : cause.toString());
      this._cause = cause;
   }
}
