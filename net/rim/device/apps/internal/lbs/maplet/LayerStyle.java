package net.rim.device.apps.internal.lbs.maplet;

import net.rim.device.api.ui.Graphics;

final class LayerStyle {
   int _minZoom;
   int _maxZoom;
   int _borderColor;
   int _fillColor;
   int _borderWidth;
   int _fillWidth;

   LayerStyle(int minZoom, int maxZoom, int borderColor, int borderWidth, int fillColor, int fillWidth) {
      this._minZoom = minZoom;
      this._maxZoom = maxZoom;
      this._borderColor = borderColor;
      this._borderWidth = borderWidth;
      this._fillColor = fillColor;
      this._fillWidth = fillWidth;
   }

   LayerStyle(int minZoom, int maxZoom, int borderColor, int borderWidth) {
      this._minZoom = minZoom;
      this._maxZoom = maxZoom;
      this._borderColor = borderColor;
      this._borderWidth = borderWidth;
      this._fillWidth = -1;
   }

   final boolean isVisibleAt(int zoom) {
      return zoom >= this._minZoom && zoom <= this._maxZoom;
   }

   final boolean apply(Graphics graphics, int phase) {
      if (phase == 0) {
         graphics.setColor(this._borderColor);
         graphics.setStrokeWidth(this._borderWidth);
         return true;
      }

      if (this._fillWidth == -1) {
         return false;
      }

      graphics.setColor(this._fillColor);
      graphics.setStrokeWidth(this._fillWidth);
      return true;
   }
}
