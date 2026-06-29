package net.rim.device.apps.internal.lbs;

import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.XYPoint;
import net.rim.device.apps.internal.lbs.render.TextOnPath$Callback;

final class RouteCallback implements TextOnPath$Callback {
   RRoute _route;

   RouteCallback(RRoute route) {
      this._route = route;
   }

   @Override
   public final boolean getNextPoint(XYPoint point) {
      return false;
   }

   @Override
   public final int getNextChar() {
      return 32;
   }

   @Override
   public final int testForCollision(int x, int y, int char_dist) {
      return -2;
   }

   @Override
   public final void setCollision() {
   }

   @Override
   public final boolean isMarker() {
      return false;
   }

   @Override
   public final int getMarkerWidth() {
      return 27;
   }

   @Override
   public final void drawMarker(Graphics graphics, int x, int y) {
   }
}
