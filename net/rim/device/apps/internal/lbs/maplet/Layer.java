package net.rim.device.apps.internal.lbs.maplet;

import java.io.DataInputStream;
import net.rim.device.api.util.Persistable;

public final class Layer implements Persistable {
   byte[] _layerHeader;
   private int _layerAttribs;
   private int _attr1;
   private int _attr2;
   private short[][][] _dentryHeaders;
   private byte[][][] _dentries;
   public static final byte ATTRIB_LAYER_ID;
   public static final byte ATTRIB_SHAPE_TYPE;
   public static final byte ATTRIB_ZOOM_INDEX;
   public static final byte ATTRIB_DENTRY_COUNT;
   public static final byte ATTRIB_BLX;
   public static final byte ATTRIB_BLY;
   public static final byte ATTRIB_DETYPE;
   public static final int SHAPE_LINE;
   public static final int SHAPE_POLYGON;
   public static final int DETYPE_POINT;
   public static final int DETYPE_LABEL;

   public final void parseHeader(byte[] header) {
      this._layerHeader = header;
      this._layerAttribs = getReversedValue(this._layerHeader[16], this._layerHeader[17], this._layerHeader[18], this._layerHeader[19]);
      int count = this.getLayerAttribute((byte)7);
      this._dentryHeaders = new short[count][][];
      this._dentries = new byte[count][][];
   }

   public final int getLayerAttribute(byte attrib) {
      switch (attrib) {
         case 0:
         case 4:
         case 5:
         case 6:
            return -1;
         case 1:
         default:
            return getReversedValue(this._layerHeader[0], this._layerHeader[1]) & 65535;
         case 2:
            return (this._layerAttribs & 1792) >> 8;
         case 3:
            return (this._layerAttribs & 30720) >> 11;
         case 7:
            return this._layerHeader[3] & 0xFF;
         case 8:
            return getReversedValue(this._layerHeader[4], this._layerHeader[5], this._layerHeader[6], this._layerHeader[7]);
         case 9:
            return getReversedValue(this._layerHeader[8], this._layerHeader[9], this._layerHeader[10], this._layerHeader[11]);
         case 10:
            return (this._layerHeader[2] & 240) >> 4;
      }
   }

   public final int getDEntryCount() {
      return this.getLayerAttribute((byte)7);
   }

   public final void addDEntryHeader(int ix, short[] header) {
      this._dentryHeaders[ix] = (short[][])header;
   }

   public final short[] getDEntryHeader(int ix) {
      return (short[])this._dentryHeaders[ix];
   }

   public final void addDEntryContents(int ix, byte[] data) {
      this._dentries[ix] = (byte[][])data;
   }

   public final int addDEntryContents(int ix, DataInputStream istream) {
      int count = 0;
      int numBytes = istream.readInt();
      count += 4 + numBytes;
      this._dentries[ix] = (byte[][])(new byte[numBytes]);
      istream.read((byte[])this._dentries[ix]);
      return count;
   }

   public final byte[] getDEntry(int ix) {
      return (byte[])this._dentries[ix];
   }

   protected static final int getReversedValue(int a, int b, int c, int d) {
      return ((d & 0xFF) << 24) + ((c & 0xFF) << 16) + ((b & 0xFF) << 8) + (a & 0xFF);
   }

   protected static final int getReversedValue(int a, int b) {
      return ((b & 0xFF) << 8) + (a & 0xFF);
   }
}
