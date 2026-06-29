package net.rim.plazmic.internal.contentpreview.dispatcher.message;

import net.rim.plazmic.internal.contentpreview.dispatcher.DispatcherEventHandler;

public final class GetControlPanelPort extends Model {
   private String _sessionName;
   public static final String rcsid;

   public GetControlPanelPort(String sessionName) {
      this._sessionName = sessionName;
   }

   @Override
   public final void toEvent(DispatcherEventHandler handler) {
      handler.getControlPanelPort(this._sessionName);
   }

   @Override
   final String getClassName() {
      return "GetControlPanelPort";
   }

   @Override
   final String getProperties() {
      return this.toPropertyString("sessionName", this._sessionName);
   }
}
