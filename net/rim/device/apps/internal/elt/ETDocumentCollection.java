package net.rim.device.apps.internal.elt;

import net.rim.device.api.util.DataBuffer;
import net.rim.device.apps.api.framework.model.SyncBuffer;

final class ETDocumentCollection {
   private static final byte TIMESTAMP;
   private static final byte LATITUDE;
   private static final byte LONGITUDE;
   private static final byte ALTITUDE;
   private static final byte DATA;
   private static final byte DEVICE_STATUS;
   private static final byte RESERVE1;

   private ETDocumentCollection() {
   }

   public static final void writeLocationPosition(
      DataBuffer db, long timestamp, int latitude, int longitude, int altitude, float speed, float bearing, String data, int deviceStatus, int reserve1
   ) {
      SyncBuffer buffer = (SyncBuffer)(new Object(db, 0, -1492538911));
      buffer.addInt(1, (int)timestamp, 4);
      buffer.addInt(2, latitude, 4);
      buffer.addInt(3, longitude, 4);
      buffer.addInt(4, altitude, 4);
      buffer.addField(5, data);
      buffer.addInt(6, deviceStatus, 4);
      buffer.addInt(7, reserve1, 4);
   }
}
