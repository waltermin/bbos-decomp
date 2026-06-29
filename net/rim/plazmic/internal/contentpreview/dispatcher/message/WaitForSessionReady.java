package net.rim.plazmic.internal.contentpreview.dispatcher.message;

import net.rim.plazmic.internal.contentpreview.dispatcher.DispatcherEventHandler;

public final class WaitForSessionReady extends Model {
   private String _sessionName;
   private int _timeout;
   public static final String rcsid = "$Id: //depot/projects/JavaDevice/4.3.0/JavaApplications/sdk/CDK/net/rim/plazmic/internal/contentpreview/dispatcher/message/WaitForSessionReady.java#1 $";

   public WaitForSessionReady(String sessionName, int timeout) {
      this._sessionName = sessionName;
      this._timeout = timeout;
   }

   @Override
   public final void toEvent(DispatcherEventHandler handler) {
      handler.waitForSessionReady(this._sessionName, this._timeout);
   }

   @Override
   final String getClassName() {
      return "WaitForSessionReady";
   }

   @Override
   final String getProperties() {
      return this.toPropertyString("sessionName", this._sessionName) + this.toPropertyString("timeout", String.valueOf(this._timeout));
   }
}
