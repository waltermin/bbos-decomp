package net.rim.device.apps.internal.lbs.protocol;

import java.io.DataInputStream;
import java.util.Vector;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.apps.internal.lbs.LBSApplication;
import net.rim.device.apps.internal.lbs.LBSOptions;
import net.rim.device.apps.internal.lbs.Timing;
import net.rim.device.apps.internal.lbs.maplet.Layer;
import net.rim.device.apps.internal.lbs.maplet.LayerDictionary;
import net.rim.device.apps.internal.lbs.maplet.MapRect;
import net.rim.device.apps.internal.lbs.maplet.Maplet;
import net.rim.device.apps.internal.lbs.maplet.MapletCache;
import net.rim.device.apps.internal.lbs.maplet.MapletFile;
import net.rim.device.apps.internal.lbs.maplet.NoMapletCache;

public final class MapRequest extends Request {
   private boolean _ignoreEmptyMaplets = false;
   private Vector _mapletList;
   private int _dataCount = 0;
   Timing _timer;
   MapRect _rect = new MapRect();
   int _zoom;
   public static final int SUCCESS;
   public static final int INVALID_REQUEST;
   public static final int SERVER_INTERNAL_ERROR;
   public static final int NO_DATA;
   public static final int CLIENT_DOWNLOAD_LOC_NOT_INCLUDED;
   public static final int CLIENT_DOWNLOAD_LOC_INCLUDED;
   public static final byte DEFAULT_MAPLET_VERSION;
   static LayerDictionary _layerDictionary = LayerDictionary.getInstance();

   @Override
   public final byte getCommand() {
      return 1;
   }

   public static final void request(Request$Listener listener, MapRect rect, int zoom) {
      RequestThread.addRequest(new MapRequest(listener, rect, zoom));
   }

   public MapRequest(Request$Listener listener, MapRect rect, int zoom) {
      super._listener = listener;
      this._rect.copy(rect);
      this._zoom = zoom;
      super._version = (byte)(LBSOptions.getInt(3743068244816784828L, 1) & 0xFF);
      this._mapletList = (Vector)(new Object());
      this._timer = new Timing();
   }

   final boolean writeMaplets(DataBuffer db) {
      int marker = db.getPosition();
      db.writeInt(0);
      int count = 0;
      MapRect mapletRect = MapRect.getMapletRect(this._rect, this._zoom);
      int mapletSize = Maplet.getMapletSize(this._zoom);

      for (int x = mapletRect._left; x < mapletRect._right; x += mapletSize) {
         for (int y = mapletRect._bottom; y < mapletRect._top; y += mapletSize) {
            if (x < -18000000) {
               x = -18000000;
            }

            if (y < -9000000) {
               y = -9000000;
            }

            int level = Maplet.getMapletLevel(this._zoom);
            Maplet maplet = MapletCache.getInstance().getMaplet(x, y, level);
            if (maplet == null && !MapletFile.getMaplet(x, y, level)) {
               this._mapletList.addElement(new MapRequest$MapletValue(this, x, y, level));
               db.writeInt(y);
               db.writeInt(x);
               db.writeChar(65 + Maplet.getMapletLevel(this._zoom));
               count++;
            }
         }
      }

      int pos = db.getPosition();
      db.setPosition(marker);
      db.writeInt(count);
      db.setPosition(pos);
      if (count > 0) {
         db.writeInt(1);
         db.writeInt(this._rect._bottom);
         db.writeInt(this._rect._left);
         db.writeInt(this._rect._top);
         db.writeInt(this._rect._right);
         db.writeChar(65 + Maplet.getMapletLevel(this._zoom));
         byte[] layerIds = _layerDictionary.getVisibleLayers(this._zoom);
         if (layerIds == null) {
            db.writeInt(0);
         } else {
            db.writeInt(layerIds.length);
            db.write(layerIds);
         }
      } else {
         db.writeInt(0);
         this._timer.dataRetrieved();
      }

      return count > 0;
   }

   final boolean writeDEntries(DataBuffer db, int x, int y, int zoom) {
      Maplet maplet = MapletCache.getInstance().getMaplet(x, y, Maplet.getMapletLevel(zoom));
      if (maplet == null) {
         return false;
      }

      int layerCnt = maplet.getLayerCount();
      if (layerCnt == 0) {
         return false;
      }

      MapRect mRect = new MapRect();
      maplet.convertToMapletUnits(mRect, this._rect);
      int layerMask = 0;
      int[] dentryMasks = new int[0];
      int dentryMask = 0;
      boolean required = false;

      for (int i = 0; i < layerCnt; i++) {
         Layer layer = maplet.getOrgLayer(i);
         int id = layer.getLayerAttribute((byte)1);
         if (_layerDictionary.isVisible(id, zoom)) {
            int dCnt = layer.getLayerAttribute((byte)7);
            dentryMask = 0;

            for (int j = 0; j < dCnt; j++) {
               if (layer.getDEntry(j) == null) {
                  short[] dHeader = layer.getDEntryHeader(j);
                  if (mRect.intersects(dHeader)) {
                     dentryMask += 1 << j;
                     required = true;
                  }
               }
            }

            if (dentryMask != 0) {
               layerMask += 1 << i;
               Arrays.add(dentryMasks, dentryMask);
            }
         }
      }

      if (!required) {
         return false;
      }

      db.writeInt(maplet.getMapletBLY());
      db.writeInt(maplet.getMapletBLX());
      db.writeChar(65 + Maplet.getMapletLevel(zoom));
      db.writeInt(layerMask);
      db.writeInt(dentryMasks.length);

      for (int j = 0; j < dentryMasks.length; j++) {
         db.writeInt(dentryMasks[j]);
      }

      return true;
   }

   final boolean writeDEntries(DataBuffer db) {
      int marker = db.getPosition();
      db.writeInt(0);
      int count = 0;
      MapRect mapletRect = MapRect.getMapletRect(this._rect, this._zoom);
      int mapletSize = Maplet.getMapletSize(this._zoom);

      for (int x = mapletRect._left; x < mapletRect._right; x += mapletSize) {
         for (int y = mapletRect._bottom; y < mapletRect._top; y += mapletSize) {
            if (x < -18000000) {
               x = -18000000;
            }

            if (y < -9000000) {
               y = -9000000;
            }

            if (this.writeDEntries(db, x, y, this._zoom)) {
               count++;
            }
         }
      }

      int pos = db.getPosition();
      db.setPosition(marker);
      db.writeInt(count);
      db.setPosition(pos);
      return count > 0;
   }

   @Override
   public final boolean writeRequest(DataBuffer db) {
      boolean required = false;
      if (this.writeMaplets(db)) {
         required = true;
      }

      if (this.writeDEntries(db)) {
         required = true;
      }

      return required;
   }

   @Override
   final void badRequest() {
      NoMapletCache noCache = NoMapletCache.getInstance();

      for (int i = 0; i < this._mapletList.size(); i++) {
         MapRequest$MapletValue val = (MapRequest$MapletValue)this._mapletList.elementAt(i);
         noCache.add(val._x, val._y, val._level);
      }

      this._mapletList.removeAllElements();
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   final void readMaplets(DataInputStream istream) {
      int count = istream.readInt();
      this._dataCount += 4;
      super._listener.progressTick(this._dataCount, super._length);
      Maplet maplet = null;
      MapletCache mapCache = MapletCache.getInstance();
      NoMapletCache noMapCache = NoMapletCache.getInstance();

      while (count-- > 0) {
         maplet = new Maplet(super._version);

         try {
            this._dataCount = this._dataCount + maplet.parseMaplet(istream);
            super._listener.progressTick(this._dataCount, super._length);

            for (int i = this._mapletList.size() - 1; i >= 0; i--) {
               if (((MapRequest$MapletValue)this._mapletList.elementAt(i)).equals(maplet.getMapletBLX(), maplet.getMapletBLY(), maplet.getMapletLevel())) {
                  this._mapletList.removeElementAt(i);
                  if (noMapCache.contains(maplet.getMapletBLX(), maplet.getMapletBLY(), maplet.getMapletLevel())) {
                     noMapCache.remove(maplet.getMapletBLX(), maplet.getMapletBLY(), maplet.getMapletLevel());
                  }
               }
            }

            mapCache.add(maplet);
         } catch (Throwable var9) {
            System.out.println(ioe.toString());
            continue;
         }
      }

      for (int i = 0; i < this._mapletList.size(); i++) {
         MapRequest$MapletValue val = (MapRequest$MapletValue)this._mapletList.elementAt(i);
         noMapCache.add(val._x, val._y, val._level);
      }

      this._mapletList.removeAllElements();
   }

   final void readDEntries(DataInputStream istream) {
      int count = istream.readInt();
      this._dataCount += 4;
      super._listener.progressTick(this._dataCount, super._length);

      while (count-- > 0) {
         int blx = istream.readInt();
         int bly = istream.readInt();
         int level = istream.readChar() - 'A';
         byte layerId = istream.readByte();
         istream.readByte();
         byte dentryNum = istream.readByte();
         this._dataCount += 11;
         super._listener.progressTick(this._dataCount, super._length);
         Maplet maplet = MapletCache.getInstance().getMaplet(blx, bly, level);
         if (maplet == null) {
            istream.skip(istream.readInt());
            this._dataCount += 4;
            super._listener.progressTick(this._dataCount, super._length);
         } else {
            Layer layer = maplet.getLayerByID(layerId);
            if (layer != null && _layerDictionary.isVisible(layer.getLayerAttribute((byte)1), this._zoom)) {
               this._dataCount = this._dataCount + layer.addDEntryContents(dentryNum, istream);
               super._listener.progressTick(this._dataCount, super._length);
            } else {
               istream.skip(istream.readInt());
               this._dataCount += 4;
               super._listener.progressTick(this._dataCount, super._length);
            }
         }
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void readResponse(DataInputStream istream, int length) {
      int rc = this.getResponseCode();
      if (super._version > 0 && rc == 0) {
         boolean var6 = false /* VF: Semaphore variable */;

         label37:
         try {
            var6 = true;
            this._dataCount = 0;
            this.readMaplets(istream);
            super._listener.progressTick(this._dataCount, length);
            this.readDEntries(istream);
            super._listener.progressTick(this._dataCount, length);
            LBSOptions._dataCount = LBSOptions._dataCount + this._dataCount;
            LBSOptions.setInt(8640332184073563572L, LBSOptions._dataCount);
            this.markEmptyMaplets();
            var6 = false;
         } finally {
            if (var6) {
               System.err.println("io exception");
               break label37;
            }
         }

         MapletCache.getInstance().commit();
      } else {
         if (rc == 5) {
            String newURL = this.parseAppURL(istream);
            if (!newURL.equals("")) {
               LBSOptions.setString(-9040565055715388692L, newURL);
               return;
            }
         } else if (rc == 4) {
            LBSOptions.setString(-9040565055715388692L, null);
         }
      }
   }

   @Override
   public final void emptyResponse() {
      this.markEmptyMaplets();
   }

   private final void markEmptyMaplets() {
      if (!this._ignoreEmptyMaplets) {
         MapletCache cache = MapletCache.getInstance();
         int level = Maplet.getMapletLevel(this._zoom);
         MapRect mapletRect = MapRect.getMapletRect(this._rect, this._zoom);
         int mapletSize = Maplet.getMapletSize(this._zoom);

         for (int x = mapletRect._left; x < mapletRect._right; x += mapletSize) {
            for (int y = mapletRect._bottom; y < mapletRect._top; y += mapletSize) {
               Maplet maplet = cache.getMaplet(x, y, level);
               if (maplet == null) {
                  cache.addEmptyMaplet(x, y, level);
               }
            }
         }

         this._ignoreEmptyMaplets = false;
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final String parseAppURL(DataInputStream in) {
      StringBuffer URL = (StringBuffer)(new Object());

      try {
         int len = in.readInt();

         int c;
         for (int i = 0; i < len && (c = in.read()) != -1; i++) {
            URL.append((char)c);
         }
      } catch (Throwable var7) {
         System.err.println(((StringBuffer)(new Object("parseAppURL ioex: "))).append(ioex.getMessage()).toString());
         EventLogger.logEvent(LBSApplication.UID, ((StringBuffer)(new Object("parseAppURL ioex: "))).append(ioex.getMessage()).toString().getBytes(), 2);
         return URL.toString();
      }

      return URL.toString();
   }

   @Override
   public final String getURL() {
      return LBSOptions.getURL(-7064416726417485961L);
   }

   @Override
   final void setVersion(byte ver) {
      if (ver > 127) {
         ver = 1;
      }

      if (ver != super._version || super._version == 127 && ver == 1) {
         this._ignoreEmptyMaplets = true;
         MapletCache.getInstance().clear();
         LBSOptions.setInt(3743068244816784828L, ver);
         RequestThread.addRequest(this);
         EventLogger.logEvent(
            LBSApplication.UID,
            ((StringBuffer)(new Object("Maplet version change from ")))
               .append(Integer.toHexString(super._version & 255))
               .append(" to ")
               .append(ver)
               .append(", from: ")
               .append(this.getURL())
               .toString()
               .getBytes(),
            5
         );
      }

      super.setVersion(ver);
   }
}
