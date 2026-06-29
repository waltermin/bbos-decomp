package net.rim.device.cldc.io.sms;

import javax.wireless.messaging.Message;
import net.rim.device.api.io.DatagramBase;

final class Protocol$StoreMessage {
   private byte[][][] _segments;
   private int _count;
   private String _key;

   public Protocol$StoreMessage(int totalSegments, String key) {
      this._segments = new byte[totalSegments][][];
      this._key = key;
   }

   public final Message add(DatagramBase d) {
      int segment = 1;
      Integer ref = (Integer)d.getProperty(SmsUtil.PROPERTY_SEGMENT_NUMBER);
      if (ref != null) {
         segment = ref;
         this._segments[segment - 1] = (byte[][])d.getData();
         this._count++;

         for (int i = 0; i < this._segments.length; i++) {
            if (this._segments[i] == null) {
               return null;
            }
         }

         return this.getMessage(d);
      } else {
         return null;
      }
   }

   public final String getKey() {
      return this._key;
   }

   private final Message getMessage(DatagramBase datagram) {
      int totalSize = 0;

      for (int i = 0; i < this._segments.length; i++) {
         totalSize += this._segments[i].length;
      }

      byte[] array = new byte[totalSize];
      int count = 0;

      for (int i = 0; i < this._segments.length; i++) {
         byte[] b = (byte[])this._segments[i];
         System.arraycopy(b, 0, array, count, b.length);
         count += b.length;
      }

      datagram.setData(array, 0, array.length);
      return Protocol.makeMessage(datagram);
   }
}
