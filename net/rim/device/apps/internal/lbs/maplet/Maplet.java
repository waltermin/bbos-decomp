package net.rim.device.apps.internal.lbs.maplet;

import java.io.DataInputStream;
import net.rim.device.api.math.Fixed32;
import net.rim.device.api.math.VecMath;
import net.rim.device.api.synchronization.ConverterUtilities;
import net.rim.device.api.synchronization.SyncObject;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.Persistable;
import net.rim.device.apps.internal.lbs.LBSOptions;
import net.rim.device.apps.internal.lbs.Transform;
import net.rim.device.apps.internal.lbs.Utilities;

public final class Maplet implements Persistable, SyncObject {
   private Layer[] _layers;
   private Layer[] _orgLayers;
   private int _blLat;
   private int _blLong;
   private byte _version;
   private int[] _matrix = new int[9];
   private int[] _translate = new int[9];
   private int[] _rotate = new int[9];
   private static final boolean DEBUG = false;
   private static int[] _mapletSize = new int[]{
      5000, 50000, 500000, 5000000, -805044094, 1196314761, 169478669, 218103808, 1380206665, 134217728, 134217728, 770, 1449244928, 24, 1414287372, 69
   };
   protected static byte[] _zoomScale = new byte[]{2, 6, 10, 10};
   private static final int[] DEPartSize = new int[]{
      4, 36, 4, 2, -805037244, 1041973275, 1196314761, 169478669, 218103808, 1380206665, 1006632960, 922746880, 1544, 691752960, 177, 1497919497
   };
   private static final int VERSION = 3;
   private static final int FIELD_LAYER_COUNT = 1;
   private static final int FIELD_LAYER_HEADER = 2;
   private static final int FIELD_DENTRY_HEADER = 4;
   private static final int FIELD_DENTRY_DATA = 5;
   private static final boolean USE_VECMATH = true;

   public final int getMapletBLY() {
      if (this._layers != null) {
         return this._layers.length > 0 ? this._layers[0].getLayerAttribute((byte)9) : -1;
      } else {
         return Integer.MAX_VALUE;
      }
   }

   final int getHashKey() {
      return getHashKey(this.getMapletBLX(), this.getMapletBLY(), this.getMapletLevel());
   }

   public final int getMapletBLX() {
      if (this._layers != null) {
         return this._layers.length > 0 ? this._layers[0].getLayerAttribute((byte)8) : -1;
      } else {
         return Integer.MAX_VALUE;
      }
   }

   @Override
   public final int getUID() {
      return this.getHashKey();
   }

   public final char getMapletLevel() {
      if (this._layers != null) {
         return this._layers.length > 0 ? (char)this._layers[0].getLayerAttribute((byte)3) : (char)0;
      } else {
         return 'ÿ';
      }
   }

   public final int getLayerCount() {
      return this._layers != null ? this._layers.length : 0;
   }

   public final Layer getOrgLayer(int ix) {
      return this._orgLayers[ix];
   }

   public final Layer getLayerByID(int id) {
      if (id <= 255) {
         id += 56832;
      } else {
         id = (id & 0xFF) + 56832;
      }

      if (this._layers != null) {
         for (int i = 0; i < this._layers.length; i++) {
            if (this._layers[i].getLayerAttribute((byte)1) == id) {
               return this._layers[i];
            }
         }
      }

      return null;
   }

   public final int parseMapletFile(DataInputStream istream) {
      int count = this.readLayers(istream);
      boolean bad = false;

      for (int j = 0; j < this._layers.length; j++) {
         Layer layer = this._layers[j];
         int layerCount = layer.getLayerAttribute((byte)7);

         for (int i = 0; i < layerCount; i++) {
            byte[] header = new byte[12];
            int read = 0;
            read = istream.read(header);
            if (read < 12) {
               throw new Object();
            }

            count += 12;
            short[] sheader = new short[]{
               getReversedValue(header[4], header[5]),
               getReversedValue(header[6], header[7]),
               getReversedValue(header[8], header[9]),
               getReversedValue(header[10], header[11]),
               0
            };
            int headerData = header[0] & 255 | (header[1] & 255) << 8 | (header[2] & 255) << 16 | (header[3] & 255) << 24;
            int numPoints = (headerData & 1048320) >> 8;
            sheader[4] = (short)numPoints;
            layer.addDEntryHeader(i, sheader);
            int deType = header[1] & 15;
            int numBytes = ((headerData & 268431360) >> 12) * DEPartSize[deType];
            byte[] data = new byte[numBytes];
            istream.read(data);
            layer.addDEntryContents(i, data);
         }

         int dentryKey1 = readReversedInt(istream);
         int dentryKey2 = readReversedInt(istream);
         if (dentryKey1 != -286331154 || dentryKey2 != -286331154) {
            System.err.println("NO DENTRY KEY FOUND (0xeeeeeeee)!!");
         }
      }

      return count;
   }

   public final int parseMaplet(DataInputStream istream) {
      istream.readInt();
      int count = 4;
      count += this.readLayers(istream);
      boolean bad = false;

      for (int j = 0; j < this._layers.length; j++) {
         Layer layer = this._layers[j];

         for (int i = 0; i < layer.getLayerAttribute((byte)7); i++) {
            byte[] header = new byte[12];
            int read = 0;
            read = istream.read(header);
            if (read < 12) {
               bad = true;
            }

            if (!bad) {
               count += 12;
               short[] sheader = new short[]{
                  getReversedValue(header[4], header[5]),
                  getReversedValue(header[6], header[7]),
                  getReversedValue(header[8], header[9]),
                  getReversedValue(header[10], header[11]),
                  0
               };
               int headerData = header[0] & 255 | (header[1] & 255) << 8 | (header[2] & 255) << 16 | (header[3] & 255) << 24;
               int numPoints = (headerData & 1048320) >> 8;
               sheader[4] = (short)numPoints;
               layer.addDEntryHeader(i, sheader);
            }

            bad = false;
         }

         int dentryKey = readReversedInt(istream);
         if (dentryKey != -286331154) {
            System.err.println("NO DENTRY KEY FOUND (0xeeeeeeee)!!");
         }
      }

      return count;
   }

   public final void load(DataBuffer db) {
      this._version = db.readByte();
      int layerCount = ConverterUtilities.readInt(db);
      this._layers = new Layer[layerCount];
      this._orgLayers = new Layer[layerCount];

      for (int iLayer = 0; iLayer < layerCount; iLayer++) {
         byte[] header = ConverterUtilities.readByteArray(db);
         Layer layer = new Layer();
         layer.parseHeader(header);
         this._layers[iLayer] = layer;
         this._orgLayers[iLayer] = layer;
         int dentryCount = layer.getLayerAttribute((byte)7);

         for (int iDEntry = 0; iDEntry < dentryCount; iDEntry++) {
            short[] dentryHeader = ConverterUtilities.readShortArray(db);
            layer.addDEntryHeader(iDEntry, dentryHeader);
            byte[] dentryData = ConverterUtilities.readByteArray(db);
            if (dentryData.length > 0) {
               layer.addDEntryContents(iDEntry, dentryData);
            }
         }
      }
   }

   public final void save(DataBuffer db) {
      db.write(new byte[]{this._version});
      int layerCount = this._orgLayers.length;
      ConverterUtilities.writeInt(db, 1, layerCount);

      for (int iLayer = 0; iLayer < layerCount; iLayer++) {
         Layer layer = this._orgLayers[iLayer];
         ConverterUtilities.writeByteArray(db, 2, layer._layerHeader);
         int dentryCount = layer.getLayerAttribute((byte)7);

         for (int iDEntry = 0; iDEntry < dentryCount; iDEntry++) {
            ConverterUtilities.writeShortArray(db, 4, layer.getDEntryHeader(iDEntry));
            byte[] data = layer.getDEntry(iDEntry);
            ConverterUtilities.writeByteArray(db, 5, data != null ? data : new byte[0]);
         }
      }
   }

   public final int[] getGraphicsMatrix(MapRect rect, int zoom, int rotation) {
      int level = this.getMapletLevel();
      int mapletScale = getMapletZoomScale(level);
      int yscale = 65536 << mapletScale >> zoom;
      int xscale = yscale;
      int blx = this.getMapletBLX();
      int bly = this.getMapletBLY();
      int tx1 = blx - rect._left >> zoom;
      int ty1 = rect._top - bly >> zoom;
      int sphericalCorrection = 65536;
      if (LBSOptions.SPHERICAL_CORRECTION) {
         sphericalCorrection = Transform.getSphericalCorrection((rect._bottom + rect._top) / 2, zoom);
         xscale = sphericalCorrection << mapletScale >> zoom;
         tx1 = tx1 * sphericalCorrection + 32768 >> 16;
      }

      int[] matrix = this._matrix;
      matrix[0] = xscale;
      matrix[1] = 0;
      matrix[2] = tx1 * 65536;
      matrix[3] = 0;
      matrix[4] = -1 * yscale;
      matrix[5] = ty1 * 65536;
      matrix[6] = 0;
      matrix[7] = 0;
      matrix[8] = 65536;
      if (rotation == 0) {
         return matrix;
      }

      int tx;
      if (LBSOptions.SPHERICAL_CORRECTION) {
         int geoWidth = Fixed32.mul(rect._right - rect._left, Fixed32.div(sphericalCorrection, 65536));
         tx = -65536 * (geoWidth >> zoom + 1);
      } else {
         tx = -65536 * (rect._right - rect._left >> zoom + 1);
      }

      int ty = 65536 * (rect._bottom - rect._top >> zoom + 1);
      int[] translate = this._translate;
      translate[0] = 65536;
      translate[2] = tx;
      translate[4] = 65536;
      translate[5] = ty;
      translate[8] = 65536;
      VecMath.multiply3x3Affine(translate, 0, matrix, 0, matrix, 0);
      int sin = Utilities.sin(rotation);
      int cos = Utilities.cos(rotation);
      int[] rotate = this._rotate;
      rotate[0] = cos;
      rotate[1] = -sin;
      rotate[3] = sin;
      rotate[4] = cos;
      rotate[8] = 65536;
      VecMath.multiply3x3Affine(rotate, 0, matrix, 0, matrix, 0);
      translate[2] = -tx;
      translate[5] = -ty;
      VecMath.multiply3x3Affine(translate, 0, matrix, 0, matrix, 0);
      return matrix;
   }

   public final void setGraphicsMatrix(Graphics graphics, MapRect rect, int zoom, int rotation) {
      graphics.setMatrix(this.getGraphicsMatrix(rect, zoom, rotation));
   }

   public final void convertToMapletUnits(MapRect dst, MapRect src) {
      int level = this.getMapletLevel();
      int mapletScale = getMapletZoomScale(level);
      int blx = this.getMapletBLX();
      int bly = this.getMapletBLY();
      dst._left = src._left - blx >> mapletScale;
      dst._right = src._right - blx >> mapletScale;
      dst._top = src._top - bly >> mapletScale;
      dst._bottom = src._bottom - bly >> mapletScale;
   }

   public final void convertToMapletUnits(MapRect rect) {
      int level = this.getMapletLevel();
      int mapletScale = getMapletZoomScale(level);
      int blx = this.getMapletBLX();
      int bly = this.getMapletBLY();
      rect._left = rect._left - blx >> mapletScale;
      rect._right = rect._right - blx >> mapletScale;
      rect._top = rect._top - bly >> mapletScale;
      rect._bottom = rect._bottom - bly >> mapletScale;
   }

   private final int readLayers(DataInputStream istream) {
      int count = 0;
      int key = readReversedInt(istream);
      if (key != -1582186031) {
         throw new Object();
      }

      int layerCount = readReversedInt(istream);
      this._layers = new Layer[layerCount];
      this._orgLayers = new Layer[layerCount];

      for (int i = 0; i < layerCount; i++) {
         byte[] header = new byte[20];
         istream.read(header);
         count += 20;
         Layer layer = new Layer();
         layer.parseHeader(header);
         this._layers[i] = layer;
         this._orgLayers[i] = layer;
      }

      return count;
   }

   static final int getHashKey(int x, int y, int level) {
      x = x + 18000000 >> 12;
      y = y + 9000000 >> 12;
      return level << 28 | x << 13 | y;
   }

   public Maplet(byte version) {
      this._version = version;
   }

   public static final int getMapletZoomScale(int level) {
      return _zoomScale[level];
   }

   public static final int getMapletSize(int zoom) {
      return _mapletSize[getMapletLevel(zoom)];
   }

   public static final int getMapletLevel(int zoom) {
      if (zoom <= 4) {
         return 0;
      } else if (zoom <= 8) {
         return 1;
      } else {
         return zoom <= 11 ? 2 : 3;
      }
   }

   protected static final int readReversedInt(DataInputStream istream) {
      return istream.readByte() & 0xFF | (istream.readByte() & 0xFF) << 8 | (istream.readByte() & 0xFF) << 16 | (istream.readByte() & 0xFF) << 24;
   }

   private static final short getReversedValue(byte a, byte b) {
      return (short)(((b & 255) << 8) + (a & 255));
   }
}
