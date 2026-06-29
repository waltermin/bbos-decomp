package net.rim.device.apps.internal.lbs.render;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.FontRegistry;
import net.rim.device.api.ui.Graphics;
import net.rim.device.apps.api.messaging.MessageIcons;
import net.rim.device.apps.internal.lbs.maplet.DEntry;
import net.rim.device.apps.internal.lbs.maplet.DEntryCache;
import net.rim.device.apps.internal.lbs.maplet.Layer;
import net.rim.device.apps.internal.lbs.maplet.LayerDictionary;
import net.rim.device.apps.internal.lbs.maplet.MapRect;
import net.rim.device.apps.internal.lbs.maplet.Maplet;
import net.rim.device.apps.internal.lbs.maplet.MapletCache;
import net.rim.device.apps.internal.lbs.maplet.NoMapletCache;
import net.rim.device.apps.internal.lbs.resources.LBSResources;

public final class MapRender {
   int[] _x = new int[4];
   int[] _y = new int[4];
   public int _titleHeight;
   Bitmap _pendingHash = Bitmap.getBitmapResource("pending_hash.png");
   Bitmap _statusUnderlay = Bitmap.getBitmapResource("status_underlay.png");
   private boolean _phaseOne = true;
   private boolean _autoPan = false;
   static LayerDictionary _layerDictionary = LayerDictionary.getInstance();
   static MapRect _mapletRect = new MapRect();

   public final void renderPending(RenderThread renderThread, Graphics graphics, MapRect rect, int zoom) {
      graphics.setMatrix(65536, 0, 0, 0, 65536, 0, 0, 0, 65536);
      MapletCache cache = MapletCache.getInstance();
      graphics.setColor(8421504);
      graphics.setStrokeWidth(1);
      NoMapletCache noCache = NoMapletCache.getInstance();
      MapRect mapletRect = MapRect.getMapletRect(rect, zoom);
      int mapletSize = Maplet.getMapletSize(zoom);

      for (int x = mapletRect._left; x < mapletRect._right; x += mapletSize) {
         for (int y = mapletRect._bottom; y < mapletRect._top; y += mapletSize) {
            if (x < -18000000) {
               x = -18000000;
            }

            if (y < -9000000) {
               y = -9000000;
            }

            Maplet maplet = cache.getMaplet(x, y, Maplet.getMapletLevel(zoom));
            if (maplet == null) {
               if (!noCache.contains(x, y, Maplet.getMapletLevel(zoom))) {
                  renderThread.dataMissing();
               }

               int ix = Math.max(x, rect._left) - rect._left >> zoom;
               int iy = rect._top - Math.min(y + mapletSize, rect._top) >> zoom;
               int ix2 = Math.min(x + mapletSize, rect._right) - rect._left >> zoom;
               int iy2 = rect._top - Math.max(y, rect._bottom) >> zoom;
               this._x[0] = this._x[3] = ix;
               this._y[0] = this._y[1] = iy;
               this._x[1] = this._x[2] = ix2;
               this._y[2] = this._y[3] = iy2;
               graphics.drawTexturedPath(this._x, this._y, null, null, 0, 0, 65536, 0, 0, 65536, this._pendingHash);
               if (ix2 - ix > 26 && iy2 - iy > 26) {
                  if (!noCache.contains(x, y, Maplet.getMapletLevel(zoom))) {
                     graphics.drawBitmap(ix + 3, iy + 3, this._statusUnderlay.getWidth(), this._statusUnderlay.getHeight(), this._statusUnderlay, 0, 0);
                     MessageIcons.paint(graphics, ix + 6, iy + 6, 14, 14, 8);
                  } else {
                     Font oldFont = graphics.getFont();
                     Font f = FontRegistry.get("BBClarity").getFont(1, 6, 2);
                     String lbl = LBSResources.getString(277);
                     int w = f.getAdvance(lbl);
                     if (w + 6 < ix2 - ix && f.getHeight() + 6 < iy2 - iy) {
                        int oldColor = graphics.getColor();
                        int oldAlpha = graphics.getGlobalAlpha();
                        graphics.setGlobalAlpha(128);
                        graphics.setColor(16777215);
                        graphics.fillRect(ix + (ix2 - ix - w) / 2 - 3, iy + (iy2 - iy - f.getHeight()) / 2 - 3, w + 6, f.getHeight() + 6);
                        graphics.setColor(0);
                        graphics.setGlobalAlpha(255);
                        graphics.setFont(f);
                        graphics.drawText(lbl, ix + (ix2 - ix - w) / 2 - 3, iy + (iy2 - iy) / 2, 36);
                        graphics.setColor(oldColor);
                        graphics.setGlobalAlpha(oldAlpha);
                        graphics.setFont(oldFont);
                     }
                  }
               }
            }
         }
      }
   }

   public final void renderMapletBoundaries(Graphics graphics, MapRect rect, int zoom) {
   }

   final void renderLayer(RenderThread renderThread, Graphics graphics, Maplet maplet, int layerId, MapRect rect, int zoom, int rotation) {
      Layer layer = maplet.getLayerByID(layerId);
      if (layer != null) {
         maplet.convertToMapletUnits(_mapletRect, rect);
         maplet.setGraphicsMatrix(graphics, rect, zoom, rotation);
         int count = layer.getLayerAttribute((byte)7);

         for (int i = 0; i < count; i++) {
            short[] header = layer.getDEntryHeader(i);
            if (_mapletRect.intersects(header)) {
               byte[] data = layer.getDEntry(i);
               if (data != null) {
                  DEntry dentry = DEntryCache.get(maplet, layer, header, data);
                  if (dentry._x.length != 0 && dentry._y.length != 0) {
                     dentry.render(graphics);
                  }
               } else {
                  renderThread.dataMissing();
                  if (zoom < 16) {
                     int oldColor = graphics.getColor();
                     graphics.setColor(8421504);
                     this._x[0] = this._x[3] = DEntry.getBLX(header);
                     this._y[0] = this._y[1] = DEntry.getBLY(header);
                     this._x[1] = this._x[2] = DEntry.getTRX(header);
                     this._y[2] = this._y[3] = DEntry.getTRY(header);
                     graphics.drawTexturedPath(this._x, this._y, null, null, 0, 0, 65536, this._x[0] % 8, this._y[0] % 8, 65536, this._pendingHash);
                     graphics.setColor(oldColor);
                  }
               }
            }
         }
      }
   }

   public final void paintBackground(Graphics graphics, MapRect rect, int zoom) {
      graphics.setMatrix(65536, 0, 0, 0, 65536, 0, 0, 0, 65536);
      graphics.setColor(LayerDictionary.getBackgroundColor());
      graphics.fillRect(0, 0, rect.width() >> zoom, rect.height() >> zoom);
   }

   public final boolean isPhaseOne() {
      return this._phaseOne;
   }

   public final void setPhase(boolean phase) {
      this._phaseOne = phase;
   }

   public final void setAutoPan(boolean autoPan) {
      this._autoPan = autoPan;
   }

   public final boolean isAutoPan() {
      return this._autoPan;
   }

   public final void render(RenderThread renderThread, Graphics graphics, MapRect rect, int zoom, int rotation) {
      Maplet[] maplets = MapletCache.getInstance().getMaplets(rect, zoom);
      if (this._phaseOne || this._autoPan) {
         DEntryCache.trim(rect);
         this.paintBackground(graphics, rect, zoom);
         graphics.setDrawingStyle(2, true);

         for (int layerId : _layerDictionary.getPolygonLayers(zoom)) {
            if (_layerDictionary.applyDrawStyle(graphics, layerId, zoom, 0)) {
               for (int maplet = 0; maplet < maplets.length; maplet++) {
                  if (renderThread.isPending()) {
                     renderThread.renderAborted();
                     return;
                  }

                  this.renderLayer(renderThread, graphics, maplets[maplet], layerId, rect, zoom, rotation);
               }
            }
         }
      }

      if (!this._phaseOne || this._autoPan) {
         graphics.setStrokeStyle(2);
         graphics.setStrokeStyle(32);
         graphics.setDrawingStyle(1, true);
         int[] layerIds = _layerDictionary.getDrawLayers(zoom);

         for (int layerId : layerIds) {
            if (_layerDictionary.applyDrawStyle(graphics, layerId, zoom, 0)) {
               for (int maplet = 0; maplet < maplets.length; maplet++) {
                  if (renderThread.isPending()) {
                     renderThread.renderAborted();
                     return;
                  }

                  this.renderLayer(renderThread, graphics, maplets[maplet], layerId, rect, zoom, rotation);
               }
            }
         }

         byte var13;
         for (int i = 0; i < var13; i++) {
            int layerId = layerIds[i];
            if (_layerDictionary.applyDrawStyle(graphics, layerId, zoom, 1)) {
               for (int maplet = 0; maplet < maplets.length; maplet++) {
                  if (renderThread.isPending()) {
                     renderThread.renderAborted();
                     return;
                  }

                  this.renderLayer(renderThread, graphics, maplets[maplet], layerId, rect, zoom, rotation);
               }
            }
         }

         if (zoom < 16) {
            this.renderMapletBoundaries(graphics, rect, zoom);
            this.renderPending(renderThread, graphics, rect, zoom);
         }
      }
   }
}
