package net.rim.plazmic.internal.contentpreview.service;

public final class ServiceException extends Exception {
   private Throwable _cause;
   public static final String rcsid = "$Id: //depot/projects/JavaDevice/4.3.0/JavaApplications/sdk/CDK/net/rim/plazmic/internal/contentpreview/service/ServiceException.java#1 $";

   public ServiceException() {
   }

   public ServiceException(Throwable cause) {
      super(cause == null ? null : cause.toString());
      this._cause = cause;
   }
}
