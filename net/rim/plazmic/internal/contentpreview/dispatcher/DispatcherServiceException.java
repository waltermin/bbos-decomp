package net.rim.plazmic.internal.contentpreview.dispatcher;

public final class DispatcherServiceException extends Exception {
   private Throwable _cause;
   public static final String rcsid = "$Id: //depot/projects/JavaDevice/4.3.0/JavaApplications/sdk/CDK/net/rim/plazmic/internal/contentpreview/dispatcher/DispatcherServiceException.java#1 $";

   public DispatcherServiceException() {
   }

   public DispatcherServiceException(String message) {
   }

   public DispatcherServiceException(String message, Throwable cause) {
      super(message);
      this._cause = cause;
   }

   public DispatcherServiceException(Throwable cause) {
      this(cause == null ? null : cause.toString(), cause);
   }
}
