package net.rim.plazmic.internal.contentpreview.dispatcher.message;

public class FailureModel extends Model {
   private String _message;
   public static final String rcsid = "$Id: //depot/dev/pbaldwin/advancedgraphics/src/net/rim/plazmic/internal/contentpreview/message/FailureModel.java#1 $";

   public FailureModel(String message) {
      this._message = message;
   }

   public String getMessage() {
      return this._message;
   }

   @Override
   String getProperties() {
      return this.toPropertyString("message", this._message);
   }
}
