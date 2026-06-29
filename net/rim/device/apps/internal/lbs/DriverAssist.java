package net.rim.device.apps.internal.lbs;

import net.rim.device.api.math.Fixed32;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.XYPoint;
import net.rim.device.api.util.IntHashtable;
import net.rim.device.api.util.ToIntHashtable;
import net.rim.device.apps.internal.lbs.maplet.DEntry;
import net.rim.device.apps.internal.lbs.maplet.DEntryCache;
import net.rim.device.apps.internal.lbs.maplet.Layer;
import net.rim.device.apps.internal.lbs.maplet.LayerDictionary;
import net.rim.device.apps.internal.lbs.maplet.MapPoint;
import net.rim.device.apps.internal.lbs.maplet.MapRect;
import net.rim.device.apps.internal.lbs.maplet.Maplet;
import net.rim.device.apps.internal.lbs.maplet.MarkerDictionary;
import net.rim.device.apps.internal.lbs.render.LabelRender;
import net.rim.device.apps.internal.lbs.util.ByteBuffer;

public final class DriverAssist {
   private byte _resolution = 0;
   private IntHashtable _colorHash;
   private ToIntHashtable _lblHash;
   private Bitmap _map;
   private Graphics _mapG;
   private int _width;
   private int _height;
   private String _currentPath;
   private XYPoint _currentCell;
   private int _color;
   private int _pathColor;
   private MapPoint _curPathPoint = new MapPoint();
   private byte[][] _quads = new byte[][]{
      {-1, -1, 0, -1, 1, -1, 1, 0, 1, 1}, {1, -1, 1, 0, 1, 1, 0, 1, -1, 1}, {-1, -1, 0, 1, -1, 1, -1, 0, 1, 1}, {-1, 1, -1, 0, -1, -1, 0, -1, 1, -1}
   };
   private Transform _transform = new Transform();
   private MapRect _zRect = new MapRect();
   private MapRect _mRect = new MapRect();
   private boolean _useZoom = false;
   private static final boolean DEBUG = false;
   private static final byte RESOLUTION = 2;
   private static final int NULL = 16777215;
   private static final ByteBuffer _textBuffer = new ByteBuffer();

   public DriverAssist(int width, int height, byte scale, boolean useZoom) {
      this._useZoom = useZoom;
      this._width = width >> scale;
      this._height = height >> scale;
      this._currentCell = (XYPoint)(new Object());
      this._colorHash = (IntHashtable)(new Object());
      this._lblHash = (ToIntHashtable)(new Object());
      this._resolution = scale;
      this.init();
   }

   public DriverAssist(int width, int height) {
      this(width, height, (byte)2, false);
   }

   private final void init() {
      this._map = (Bitmap)(new Object(197, this._width, this._height));
      this._mapG = (Graphics)(new Object(this._map));
      this._mapG.setDrawingStyle(1, false);
   }

   public final void populateMap(DEntry dentry, byte[] label, Transform transform, int bx, int by, int zoom) {
      if (label != null) {
         MapPoint p1 = new MapPoint();
         MapPoint p2 = new MapPoint();
         int numParts = dentry._startpoints != null ? dentry._startpoints.length - 1 : 1;
         int start = 0;
         int end = 0;
         int j = 0;
         int labelPos = 0;
         int zoomScale = Maplet.getMapletZoomScale(Maplet.getMapletLevel(zoom));
         synchronized (_textBuffer) {
            for (int i = 0; i < numParts; i++) {
               start = dentry._startpoints == null ? 0 : dentry._startpoints[i];
               end = dentry._startpoints == null ? dentry._x.length - 1 : dentry._startpoints[i + 1] - 1;
               j = start;
               _textBuffer.setLength(0);
               if (zoomScale == 2) {
                  int labelStart = labelPos + 4;
                  int labelEnd = labelStart;
                  if (label != null) {
                     while (label[labelEnd] != 0) {
                        labelEnd++;
                     }

                     labelPos = labelEnd;
                     if (labelEnd - labelStart == 0) {
                        labelPos++;
                        continue;
                     }

                     _textBuffer.append(LabelRender.toLowerCase((String)(new Object(label, labelStart, labelEnd - labelStart)), 0));
                     LabelRender.addLabelInfo(_textBuffer, label, labelStart - 4);
                  }

                  labelPos++;
               } else {
                  boolean europeanHwyV2 = false;
                  boolean europeanHwyV1 = false;
                  int markerExtrFlag = 0;
                  String markerInfo = "";
                  int markerStart = label[labelPos++] & 255;
                  int markerMiddle = (label[labelPos++] & 255 | (label[labelPos++] & 255) << 8) & 65535;
                  byte markerEnd = label[labelPos++];
                  int newHwyInfo = label[labelPos] & 255;
                  if (newHwyInfo != 0) {
                     markerExtrFlag = (label[labelPos] & 255) >> 7;
                     if (markerExtrFlag == 0) {
                        int startIdx = labelPos;
                        int endIdx = startIdx;
                        ByteBuffer buffer = new ByteBuffer();

                        do {
                           endIdx++;
                        } while ((label[labelPos++] & 255) != 0);

                        buffer.append(label, startIdx, endIdx - startIdx);
                        markerInfo = buffer.toString();
                        europeanHwyV1 = true;
                        europeanHwyV2 = false;
                     } else {
                        int secBit = (label[labelPos] & 64) << 2 & 0xFF;
                        int sDicIdx = ((label[labelPos++] & 255) << 2 | (label[labelPos] & 255) >> 6) & 0xFF;
                        int eDicIdx = label[labelPos++] & 31;
                        String startSt = "";
                        String endSt = "";
                        sDicIdx += secBit;
                        if (sDicIdx >= 0 && sDicIdx < MarkerDictionary.MARKER_EUROPEAN_NAME_ARRAY.length) {
                           startSt = MarkerDictionary.MARKER_EUROPEAN_NAME_ARRAY[sDicIdx];
                           europeanHwyV2 = true;
                        } else {
                           europeanHwyV2 = false;
                        }

                        if (eDicIdx >= 0 && eDicIdx < MarkerDictionary.MARKER_END_ARRAY.length) {
                           endSt = MarkerDictionary.MARKER_END_ARRAY[eDicIdx];
                           europeanHwyV2 = true;
                        } else {
                           europeanHwyV2 = false;
                        }

                        if (europeanHwyV2) {
                           markerStart = sDicIdx;
                           markerInfo = ((StringBuffer)(new Object())).append(startSt).append(markerMiddle).append(endSt).toString().trim();
                           europeanHwyV1 = false;

                           while (label[labelPos++] != 0) {
                           }
                        }
                     }
                  }

                  if (newHwyInfo == 0 || !europeanHwyV1 && !europeanHwyV2) {
                     markerInfo = this.getMarker(markerStart, markerMiddle, markerEnd);
                     europeanHwyV2 = false;
                     europeanHwyV1 = false;

                     while (label[labelPos++] != 0) {
                     }
                  }

                  if (markerMiddle == 0 || markerStart == 105 || markerStart == 45) {
                     continue;
                  }

                  _textBuffer.append(markerInfo);
               }

               if (_textBuffer.length() > 0) {
                  String lbl = _textBuffer.toString();
                  int color = this._lblHash.get(lbl);
                  if (color == -1) {
                     color = this.chg565To888(this._color++);
                     this._lblHash.put(lbl, color);
                     this._colorHash.put(color, lbl);
                  }

                  for (; j < end; j++) {
                     if (dentry._x.length > j + 1) {
                        p1._x = (dentry._x[j] << zoomScale) + bx;
                        p1._y = (dentry._y[j] << zoomScale) + by;
                        p2._x = (dentry._x[j + 1] << zoomScale) + bx;
                        p2._y = (dentry._y[j + 1] << zoomScale) + by;
                        this.drawLine(p1, p2, color, transform);
                     }
                  }
               }
            }
         }
      }
   }

   public final void populateMap(Maplet[] maplets, MapRect rect, int zoom, int rotation, Transform transform) {
      if (maplets != null) {
         int newZoom = this._useZoom ? zoom : 0;
         this.calculateZoomedRect(rect, transform, newZoom);
         this._transform.setScreenExtent(transform._screenView._right, transform._screenView._bottom);
         this._transform.update(this._zRect, newZoom, transform._rotation);
         this._color = 0;

         for (int i = 0; i < maplets.length; i++) {
            maplets[i].convertToMapletUnits(this._mRect, this._zRect);
            int[] layerIds = LayerDictionary.getInstance().getDrawLayers(zoom);

            for (int j = 0; j < layerIds.length; j++) {
               int lid = layerIds[j];
               if (lid >= 56890 && lid <= 56901) {
                  Layer layer = maplets[i].getLayerByID(lid);
                  Layer lblLayer;
                  if (zoom < 5) {
                     lblLayer = maplets[i].getLayerByID(lid + 7);
                  } else {
                     if (zoom >= 13) {
                        return;
                     }

                     lblLayer = maplets[i].getLayerByID(lid + 25);
                  }

                  if (layer != null && lblLayer != null) {
                     int dcnt = layer.getDEntryCount();

                     for (int k = 0; k < dcnt; k++) {
                        short[] header = layer.getDEntryHeader(k);
                        if (this._mRect.intersects(header)) {
                           byte[] points = layer.getDEntry(k);
                           if (points != null) {
                              DEntry dentry = DEntryCache.get(maplets[i], layer, layer.getDEntryHeader(k), points);
                              this.populateMap(
                                 dentry,
                                 lblLayer != null ? lblLayer.getDEntry(k) : null,
                                 this._transform,
                                 maplets[i].getMapletBLX(),
                                 maplets[i].getMapletBLY(),
                                 zoom
                              );
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private final String getMarker(int start, int middle, int end) {
      try {
         return ((StringBuffer)(new Object()))
            .append(MarkerDictionary.MARKER_START_ARRAY[start])
            .append(" ")
            .append(middle)
            .append(MarkerDictionary.MARKER_END_ARRAY[end])
            .toString()
            .trim();
      } finally {
         ;
      }
   }

   private final void calculateZoomedRect(MapRect rect, Transform transform, int zoom) {
      int mapWidth = transform._screenView._right << zoom;
      int mapHeight = transform._screenView._bottom << zoom;
      if (LBSOptions.SPHERICAL_CORRECTION) {
         int sphericalCorrection = Transform.getSphericalCorrection(transform._latitude, zoom);
         mapWidth = Fixed32.mul(mapWidth, Fixed32.div(65536, sphericalCorrection));
      }

      this._zRect._left = transform._longitude - (mapWidth >> 1);
      this._zRect._right = transform._longitude + (mapWidth >> 1);
      this._zRect._top = transform._latitude + (mapHeight >> 1);
      this._zRect._bottom = transform._latitude - (mapHeight >> 1);
   }

   private final void drawLine(MapPoint p1, MapPoint p2, int color, Transform transform) {
      transform.convertWorldToScreen(p1);
      transform.convertWorldToScreen(p2);
      p1._x = p1._x >> this._resolution;
      p1._y = p1._y >> this._resolution;
      p2._x = p2._x >> this._resolution;
      p2._y = p2._y >> this._resolution;
      if (p1._x != p2._x || p1._y != p2._y) {
         int side = this.side(p1);
         if (side == 0 || side != this.side(p2)) {
            this._mapG.setColor(color);
            this._mapG.drawLine(p1._x, p1._y, p2._x, p2._y);
         }
      }
   }

   private final int side(MapPoint p) {
      if (p._x < 0) {
         return 1;
      } else if (p._y < 0) {
         return 2;
      } else if (p._x >= this._width) {
         return 3;
      } else {
         return p._y >= this._height ? 4 : 0;
      }
   }

   public final void clear() {
      this._mapG.setBackgroundColor(16777215);
      this._mapG.clear();
      this._colorHash.clear();
      this._lblHash.clear();
   }

   private final int chg565To888(int colour) {
      int retVal = (colour & 63488) << 8 | (colour & 57344) << 3;
      retVal |= (colour & 2016) << 5 | (colour & 1536) >> 1;
      return retVal | (colour & 31) << 3 | (colour & 28) >> 2;
   }

   public final String findCurrentPath(int orgX, int orgY) {
      int x = orgX >> this._resolution;
      int y = orgY >> this._resolution;
      if (x >= 0 && x < this._width && y >= 0 && y < this._height) {
         int tx = 0;
         int ty = 0;
         int dist = 100;
         String path = null;
         this._currentPath = null;
         int pathColor = 16777215;
         int[] data = new int[1];
         int threshold = 7;

         for (int i = 0; i < threshold; i++) {
            for (int j = x - i; j < x + i; j++) {
               if (j >= 0 && j < this._width) {
                  for (int k = y - 1; k < y + i; k++) {
                     if (k >= 0 && k < this._height) {
                        this._map.getARGB(data, 0, 1, j, k, 1, 1);
                        data[0] &= 16777215;
                        if (data[0] != 16777215) {
                           int tmp = (j - x) * (j - x) + (k - y) * (k - y);
                           if (tmp < dist) {
                              dist = tmp;
                              tx = j;
                              ty = k;
                              path = (String)this._colorHash.get(data[0]);
                              pathColor = data[0];
                           }
                        }
                     }
                  }
               }
            }

            if (path != null) {
               break;
            }
         }

         if (path != null) {
            this._currentPath = path;
            this._currentCell.x = tx;
            this._currentCell.y = ty;
            this._pathColor = pathColor;
            return path;
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   public final String findCurrentPath(int lat, int lon, int zoom, Transform transform) {
      this._curPathPoint._x = lon;
      this._curPathPoint._y = lat;
      transform.convertWorldToScreen(this._curPathPoint);
      return this.findCurrentPath(this._curPathPoint._x, this._curPathPoint._y);
   }

   public final String findNextStreet(int bearing) {
      if (this._currentPath == null) {
         return null;
      }

      byte threshold = 10;
      int bQuad = bearing / 90;
      byte[] quad = this._quads[bQuad];
      int x = this._currentCell.x;
      int y = this._currentCell.y;
      int nextX = -1;
      int nextY = -1;
      int[] data = new int[1];

      for (int t = 0; t < threshold; t++) {
         for (int i = 0; i < quad.length; i += 2) {
            int tx = x + quad[i];
            int ty = y + quad[i + 1];
            if (tx >= 0 && tx < this._width && ty >= 0 && ty < this._height) {
               this._map.getARGB(data, 0, 1, tx, ty, 1, 1);
               data[0] &= 16777215;
               if (data[0] != 16777215 && data[0] != this._pathColor) {
                  String path = (String)this._colorHash.get(data[0]);
                  return path;
               }

               if (data[0] != 16777215 && data[0] == this._pathColor) {
                  nextX = tx;
                  nextY = ty;
               }
            }
         }

         if (nextX == -1 || nextY == -1) {
            return null;
         }

         int tx = nextX - x;
         int ty = nextY - y;
         if (tx >= 0 && ty < 0) {
            quad = this._quads[0];
         } else if (tx > 0 && ty >= 0) {
            quad = this._quads[1];
         } else if (tx <= 0 && ty > 0) {
            quad = this._quads[2];
         } else {
            if (tx >= 0 || ty > 0) {
               return null;
            }

            quad = this._quads[3];
         }

         x = nextX;
         y = nextY;
      }

      return null;
   }
}
