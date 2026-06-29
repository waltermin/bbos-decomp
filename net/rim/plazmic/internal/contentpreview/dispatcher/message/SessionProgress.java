package net.rim.plazmic.internal.contentpreview.dispatcher.message;

import net.rim.plazmic.internal.contentpreview.dispatcher.DispatcherEventHandler;

public final class SessionProgress extends Model {
   private int _progress;
   public static final String rcsid = "$Id: //depot/projects/JavaDevice/4.3.0/JavaApplications/sdk/CDK/net/rim/plazmic/internal/contentpreview/dispatcher/message/SessionProgress.java#1 $";

   public SessionProgress(int progress) {
      this._progress = progress;
   }

   @Override
   public final void toEvent(DispatcherEventHandler handler) {
      handler.sessionProgress(this._progress);
   }

   @Override
   final String getClassName() {
      return "SessionProgress";
   }

   @Override
   final String getProperties() {
      return this.toPropertyString("progress", String.valueOf(this._progress));
   }
}
