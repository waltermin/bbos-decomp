package net.rim.plazmic.internal.contentpreview.dispatcher.message;

import net.rim.plazmic.internal.contentpreview.dispatcher.DispatcherEventHandler;

public final class GetSessionProgress extends Model {
   private String _sessionName;
   public static final String rcsid;

   public GetSessionProgress(String sessionName) {
      this._sessionName = sessionName;
   }

   @Override
   public final void toEvent(DispatcherEventHandler handler) {
      handler.getSessionProgress(this._sessionName);
   }

   @Override
   final String getClassName() {
      return "GetSessionProgress";
   }

   @Override
   final String getProperties() {
      return this.toPropertyString("sessionName", this._sessionName);
   }
}
