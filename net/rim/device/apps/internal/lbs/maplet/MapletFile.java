package net.rim.device.apps.internal.lbs.maplet;

import java.io.DataInputStream;
import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;

public final class MapletFile {
   private static int[] _mapletSize = new int[]{5000, 50000, 500000, 5000000, -804651000, -24, -3, -3, 18, 18, -3, -3, -24, 712179968, 712179968, 16806977};

   private static final String format3(int i) {
      String s = "000" + Integer.toString(i);
      return s.substring(s.length() - 3);
   }

   private static final String genLatDir(int y) {
      return "Y" + format3(y / 10 * 10);
   }

   private static final String genLonDir(int x) {
      return "X" + format3(x / 10 * 10);
   }

   private static final String genMBLFileName(int x, int y, int level) {
      StringBuffer sb = new StringBuffer();
      sb.append((char)(65 + level));
      sb.append('A');
      sb.append(format3(x) + format3(y));
      return sb.toString();
   }

   protected static final int readReversedInt(DataInputStream istream) {
      return istream.readByte() & 0xFF | (istream.readByte() & 0xFF) << 8 | (istream.readByte() & 0xFF) << 16 | (istream.readByte() & 0xFF) << 24;
   }

   static final int roundForLevel(int i, int level) {
      i /= 100000;
      switch (level) {
         case 0:
            return i;
         case 1:
         default:
            return i / 10 * 10;
         case 2:
            return i / 100 * 100;
         case 3:
            return 0;
      }
   }

   public static final boolean getMaplet(int x, int y, int level) {
      int mx = x + 18000000;
      int my = y + 9000000;
      int rx = roundForLevel(mx, level);
      int ry = roundForLevel(my, level);
      String filename = "file:///SDCard/" + genLatDir(ry) + "/" + genLonDir(rx) + "/" + genMBLFileName(rx, ry, level) + ".mbl";

      try {
         FileConnection fileConnection = (FileConnection)Connector.open(filename);
         if (!fileConnection.exists()) {
            return false;
         }

         DataInputStream istream = fileConnection.openDataInputStream();
         istream.mark(3208);
         int mapletSize = _mapletSize[level];
         mx /= mapletSize;
         my /= mapletSize;
         int mblIndex = mx % 20 + my % 20 * 20;
         int pos = mblIndex * 8;
         istream.skip(pos);
         int offset = readReversedInt(istream);
         int size = readReversedInt(istream);
         istream.reset();
         istream.skip(3200 + offset);
         MapletCache mapCache = MapletCache.getInstance();
         Maplet maplet = new Maplet((byte)1);
         maplet.parseMapletFile(istream);
         mapCache.add(maplet);
         istream.close();
         fileConnection.close();
         return true;
      } finally {
         ;
      }
   }
}
