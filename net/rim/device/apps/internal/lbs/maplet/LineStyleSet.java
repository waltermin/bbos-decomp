package net.rim.device.apps.internal.lbs.maplet;

import net.rim.device.api.ui.Graphics;
import net.rim.device.api.util.Arrays;

final class LineStyleSet extends StyleSet {
   LayerStyle[] _styles = new LayerStyle[0];

   final void add(int minZoom, int maxZoom, int borderColor, int borderWidth) {
      Arrays.insertAt(this._styles, new LayerStyle(minZoom, maxZoom, borderColor, borderWidth), 0);
   }

   @Override
   final boolean isVisibleAt(int zoom) {
      int i = this._styles.length - 1;

      while (i >= 0) {
         LayerStyle style = this._styles[i--];
         if (style.isVisibleAt(zoom)) {
            return true;
         }
      }

      return false;
   }

   @Override
   final boolean apply(Graphics graphics, int zoom, int phase) {
      int i = this._styles.length - 1;

      while (i >= 0) {
         LayerStyle style = this._styles[i--];
         if (zoom <= style._maxZoom) {
            return style.apply(graphics, phase);
         }
      }

      return false;
   }
}
