package net.rim.plazmic.internal.contentpreview.dispatcher.message;

import net.rim.plazmic.internal.contentpreview.dispatcher.DispatcherEventHandler;

public final class PushFile extends Model {
   private String _sessionName;
   private String _fileName;
   public static final String rcsid = "$Id: //depot/dev/pbaldwin/advancedgraphics/src/net/rim/plazmic/internal/contentpreview/message/PushFile.java#1 $";

   public PushFile(String sessionName, String fileName) {
      this._sessionName = sessionName;
      this._fileName = fileName;
   }

   @Override
   public final void toEvent(DispatcherEventHandler handler) {
      handler.pushFile(this._sessionName, this._fileName);
   }

   @Override
   final String getClassName() {
      return "PushFile";
   }

   @Override
   final String getProperties() {
      return this.toPropertyString("sessionName", this._sessionName) + this.toPropertyString("fileName", this._fileName);
   }
}
