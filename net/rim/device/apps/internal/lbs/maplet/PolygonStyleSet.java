package net.rim.device.apps.internal.lbs.maplet;

import net.rim.device.api.ui.Graphics;

final class PolygonStyleSet extends StyleSet {
   int _minZoom;
   int _maxZoom;
   int _color;

   PolygonStyleSet(int minZoom, int maxZoom, int color) {
      this._minZoom = minZoom;
      this._maxZoom = maxZoom;
      this._color = color;
   }

   @Override
   final boolean isVisibleAt(int zoom) {
      return zoom >= this._minZoom && zoom <= this._maxZoom;
   }

   @Override
   final boolean apply(Graphics graphics, int zoom, int phase) {
      graphics.setColor(this._color);
      return this.isVisibleAt(zoom);
   }
}
