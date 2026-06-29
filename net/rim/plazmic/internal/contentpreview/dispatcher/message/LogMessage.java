package net.rim.plazmic.internal.contentpreview.dispatcher.message;

import net.rim.plazmic.internal.contentpreview.dispatcher.DispatcherEventHandler;

public final class LogMessage extends Model {
   private final String _sessionName;
   private final int _type;
   private final String _message;
   private final String[] _data;
   public static final String rcsid = "$Id: //depot/dev/pbaldwin/advancedgraphics/src/net/rim/plazmic/internal/contentpreview/message/LogMessage.java#1 $";
   public static final int MESSAGE = 1;
   public static final int WARNING = 2;
   public static final int ERROR = 3;

   public LogMessage(String sessionName, int type, String message, String[] data) {
      this._sessionName = sessionName;
      this._type = type;
      this._message = message;
      this._data = data;
   }

   @Override
   public final void toEvent(DispatcherEventHandler handler) {
      handler.logMessage(this._sessionName, this._type, this._message, this._data);
   }

   @Override
   final String getClassName() {
      return "LogMessage";
   }

   @Override
   final String getProperties() {
      return ((StringBuffer)(new Object()))
         .append(this.toPropertyString("sessionName", String.valueOf(this._sessionName)))
         .append(this.toPropertyString("type", String.valueOf(this._type)))
         .append(this.toPropertyString("message", String.valueOf(this._message)))
         .append(this.toPropertyString("data", String.valueOf(this._data)))
         .toString();
   }
}
