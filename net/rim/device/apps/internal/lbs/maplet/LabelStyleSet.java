package net.rim.device.apps.internal.lbs.maplet;

import net.rim.device.api.ui.Graphics;

class LabelStyleSet extends StyleSet {
   int _minZoom;
   int _maxZoom;
   int _associatedPathLayer;

   LabelStyleSet(int minZoom, int maxZoom, int associatedPathLayer) {
      this._minZoom = minZoom;
      this._maxZoom = maxZoom;
      this._associatedPathLayer = associatedPathLayer;
   }

   @Override
   boolean isVisibleAt(int zoom) {
      return zoom >= this._minZoom && zoom <= this._maxZoom;
   }

   @Override
   boolean apply(Graphics graphics, int zoom, int phase) {
      return this.isVisibleAt(zoom);
   }
}
