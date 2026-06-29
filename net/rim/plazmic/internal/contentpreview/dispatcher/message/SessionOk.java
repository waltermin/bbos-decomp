package net.rim.plazmic.internal.contentpreview.dispatcher.message;

import net.rim.plazmic.internal.contentpreview.dispatcher.DispatcherEventHandler;

public final class SessionOk extends Model {
   private String _sessionName;
   public static final String rcsid = "$Id: //depot/dev/pbaldwin/advancedgraphics/src/net/rim/plazmic/internal/contentpreview/message/SessionOk.java#1 $";

   public SessionOk(String sessionName) {
      this._sessionName = sessionName;
   }

   public final String getSessionName() {
      return this._sessionName;
   }

   @Override
   public final void toEvent(DispatcherEventHandler handler) {
      handler.sessionOk(this._sessionName);
   }

   @Override
   final String getClassName() {
      return "SessionOk";
   }

   @Override
   final String getProperties() {
      return this.toPropertyString("sessionName", this._sessionName);
   }
}
