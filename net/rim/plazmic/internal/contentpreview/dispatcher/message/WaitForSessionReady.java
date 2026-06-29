package net.rim.plazmic.internal.contentpreview.dispatcher.message;

import net.rim.plazmic.internal.contentpreview.dispatcher.DispatcherEventHandler;

public final class WaitForSessionReady extends Model {
   private String _sessionName;
   private int _timeout;
   public static final String rcsid;

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
      return ((StringBuffer)(new Object()))
         .append(this.toPropertyString("sessionName", this._sessionName))
         .append(this.toPropertyString("timeout", String.valueOf(this._timeout)))
         .toString();
   }
}
