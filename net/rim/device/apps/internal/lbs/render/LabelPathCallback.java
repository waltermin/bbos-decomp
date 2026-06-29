package net.rim.device.apps.internal.lbs.render;

import net.rim.device.api.math.Fixed32;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.XYPoint;
import net.rim.device.apps.internal.lbs.Transform;

final class LabelPathCallback implements TextOnPath$Callback {
   LabelPath _labelPath;
   int _iChar;
   int _iVertex;
   int _dir;
   boolean _test;
   int _printedDist;
   boolean _collision;
   int _paddingShield;
   static Transform _transform = new Transform();
   static CollisionArray _collisionArray = new CollisionArray();

   final void init(LabelPath labelPath, int rotation, int paddingShield) {
      this._labelPath = labelPath;
      this._iChar = labelPath._textIndex;
      this._iVertex = Integer.MIN_VALUE;
      this._printedDist = 0;
      this._collision = false;
      this._paddingShield = paddingShield;
   }

   @Override
   public final boolean getNextPoint(XYPoint point) {
      if (this._iVertex == Integer.MIN_VALUE) {
         if (this._labelPath._dir == 1) {
            point.x = this._labelPath._start._x;
            point.y = this._labelPath._start._y;
            this._iVertex = this._labelPath._startIndex;
         } else {
            point.x = this._labelPath._end._x;
            point.y = this._labelPath._end._y;
            this._iVertex = this._labelPath._endIndex;
         }

         _transform.convertWorldToMaplet(point, this._labelPath._maplet);
         _transform.convertMapletToScreen(point);
         return false;
      } else {
         if (this._labelPath._dir == 1) {
            if (this._iVertex > this._labelPath._endIndex) {
               if (this.nextSplice(point)) {
                  return false;
               }

               point.x = this._labelPath._end._x;
               point.y = this._labelPath._end._y;
               _transform.convertWorldToMaplet(point, this._labelPath._maplet);
               _transform.convertMapletToScreen(point);
               return true;
            }
         } else if (this._iVertex < this._labelPath._startIndex) {
            if (this.nextSplice(point)) {
               return false;
            }

            point.x = this._labelPath._start._x;
            point.y = this._labelPath._start._y;
            _transform.convertWorldToMaplet(point, this._labelPath._maplet);
            _transform.convertMapletToScreen(point);
            return true;
         }

         point.x = this._labelPath._path._x[this._iVertex];
         point.y = this._labelPath._path._y[this._iVertex];
         if (_transform.getMaplet() != this._labelPath._maplet) {
            _transform.setMaplet(this._labelPath._maplet);
         }

         _transform.convertMapletToScreen(point);
         this._iVertex = this._iVertex + this._labelPath._dir;
         return false;
      }
   }

   public final boolean nextSplice(XYPoint point) {
      LabelPath splice = this._labelPath._splice;
      if (splice == null) {
         return false;
      }

      this._labelPath = splice;
      if (this._labelPath._dir == 1) {
         point.x = this._labelPath._start._x;
         point.y = this._labelPath._start._y;
         this._iVertex = this._labelPath._startIndex;
      } else {
         point.x = this._labelPath._end._x;
         point.y = this._labelPath._end._y;
         this._iVertex = this._labelPath._endIndex;
      }

      _transform.convertWorldToMaplet(point, this._labelPath._maplet);
      _transform.convertMapletToScreen(point);
      return true;
   }

   @Override
   public final int getNextChar() {
      int c = 0;

      try {
         return this._labelPath._labelData[this._iChar++];
      } finally {
         this._iChar = this._labelPath._labelData.length - 1;
         return this._labelPath._labelData[this._iChar];
      }
   }

   @Override
   public final int testForCollision(int x, int y, int char_dist) {
      x = Fixed32.toRoundedInt(x);
      y = Fixed32.toRoundedInt(y);
      char_dist = Fixed32.toRoundedInt(char_dist);
      if (this._test) {
         if (_collisionArray.testArea(x, y, char_dist, char_dist)) {
            this._collision = true;
            return 0;
         } else {
            this._printedDist += char_dist;
            return -1;
         }
      } else {
         _collisionArray.markArea(x, y, char_dist, char_dist);
         return -2;
      }
   }

   @Override
   public final void setCollision() {
      this._collision = true;
   }

   @Override
   public final boolean isMarker() {
      return this._labelPath._pathMarker != null;
   }

   @Override
   public final int getMarkerWidth() {
      return this._labelPath._pathMarker._rect.width;
   }

   @Override
   public final void drawMarker(Graphics graphics, int x, int y) {
      int height = this._labelPath._pathMarker._rect.height;
      int width = this._labelPath._pathMarker._rect.width;
      int left = x - width / 2;
      int top = y - height / 2;
      if (this._labelPath._pathMarker._type == 73) {
         height = height * 3 / 2;
      }

      if (this._test) {
         if (left >= 0
            && left + width <= Display.getWidth()
            && top + height <= Display.getHeight()
            && top >= 0
            && !_collisionArray.testArea(left, top, width, height, this._paddingShield)) {
            this._printedDist += width;
         } else {
            this._collision = true;
         }
      } else {
         _collisionArray.markArea(left, top, width, height);
         this._labelPath._pathMarker.render(graphics, x, y);
      }
   }
}
