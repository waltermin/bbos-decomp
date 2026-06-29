package net.rim.device.apps.api.transmission;

import javax.microedition.io.DatagramConnection;
import net.rim.device.api.io.DatagramConnectionBase;
import net.rim.device.api.io.DatagramStatusListener;
import net.rim.device.api.system.EventLogger;
import net.rim.device.cldc.io.daemon.ProtocolDaemon;

public class BaseConnectionThread extends Thread implements EventLoggerEvents, DatagramConnectionUser, DatagramStatusListener {
   protected long _eventLoggerGUID;
   protected DatagramConnection _connection;

   public BaseConnectionThread(long eventLoggerGUID) {
      this._eventLoggerGUID = eventLoggerGUID;
      this._connection = null;
      this.setPriority(3);
   }

   @Override
   public void setDatagramConnection(DatagramConnection aDatagramConnection) {
      if (aDatagramConnection != null) {
         boolean startThread = this._connection == null;
         this._connection = aDatagramConnection;
         EventLogger.logEvent(this._eventLoggerGUID, 1129530708, 5);
         if (this._connection instanceof Object) {
            ((DatagramConnectionBase)this._connection).setDatagramStatusListener(this);
         }

         if (startThread) {
            ProtocolDaemon.getInstance().startThread(this);
            return;
         }
      } else {
         EventLogger.logEvent(this._eventLoggerGUID, 1129206612, 3);
      }
   }

   @Override
   public void updateDatagramStatus(int tagInt, int codeInt, Object contextObject) {
   }
}
