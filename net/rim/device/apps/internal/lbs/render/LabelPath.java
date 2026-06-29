package net.rim.device.apps.internal.lbs.render;

import net.rim.device.api.ui.Graphics;
import net.rim.device.api.util.Comparator;
import net.rim.device.apps.internal.lbs.Utilities;
import net.rim.device.apps.internal.lbs.maplet.MapPoint;
import net.rim.device.apps.internal.lbs.maplet.Maplet;

final class LabelPath extends ClipPath implements Comparator {
   byte[] _labelData;
   int _textIndex;
   int _pathLength;
   int _textLength;
   int _offsetAlongPath;
   int _priority;
   int _dir;
   boolean _used = false;
   LabelPath _next;
   LabelPath _splice;
   LabelPath _prevSplice;
   int _splicedLength;
   Maplet _maplet;
   PathMarker _pathMarker;
   int _paddingShield;
   static LabelPathCallback _callback = new LabelPathCallback();

   @Override
   public final int compare(Object o1, Object o2) {
      return ((LabelPath)o1).compare((LabelPath)o2);
   }

   final MapPoint getStart() {
      return this._dir == 1 ? super._start : super._end;
   }

   final MapPoint getEnd() {
      LabelPath label = this;

      while (label._splice != null) {
         label = label._splice;
      }

      return label._dir == 1 ? label._end : label._start;
   }

   final int compare(LabelPath other) {
      if (this._pathMarker == null) {
         int iChar1 = this._textIndex;
         int iChar2 = other._textIndex;

         while (true) {
            int ch1 = this._labelData[iChar1];
            int ch2 = other._labelData[iChar2];
            if (ch1 != ch2) {
               return ch2 - ch1;
            }

            if (ch1 == 0 && ch2 == 0) {
               return 0;
            }

            if (ch1 == 0 && ch2 != 0) {
               return 1;
            }

            if (ch1 != 0 && ch2 == 0) {
               return -1;
            }

            iChar1++;
            iChar2++;
         }
      } else {
         int iChar1 = 0;
         int iChar2 = 0;
         byte[] value1 = this._pathMarker._value.getBytes();
         byte[] value2 = other._pathMarker._value.getBytes();

         do {
            int ch1 = value1[iChar1];
            int ch2 = value2[iChar2];
            if (ch1 != ch2) {
               return ch2 - ch1;
            }

            iChar1++;
            if (iChar1 == value1.length && ++iChar2 == value2.length) {
               return 0;
            }

            if (iChar1 == value1.length && iChar2 < value2.length) {
               return 1;
            }
         } while (iChar1 >= value1.length || iChar2 != value2.length);

         return -1;
      }
   }

   final void chooseDir(int rotation) {
      int dx = super._end._x - super._start._x;
      int dy = super._end._y - super._start._y;
      rotation /= 10;
      rotation += Utilities.getRotation(dx, dy);

      while (rotation > 35) {
         rotation -= 36;
      }

      this._dir = rotation > 9 && rotation <= 27 ? -1 : 1;
   }

   final void addSplice(LabelPath splice) {
      if (splice._used) {
         System.out.println("Error: Segment cannot be spliced onto labelPath -- already in use.");
      } else if (splice._splice == this) {
         System.out.println("yikes");
      } else {
         LabelPath end = this;

         while (end._splice != null) {
            end = end._splice;
         }

         if (end == splice) {
            System.out.println("yikes");
         } else {
            end._splice = splice;
            splice._prevSplice = end;
            splice._labelData = this._labelData;
            splice._textIndex = this._textIndex;
            splice._used = true;
         }
      }
   }

   static final boolean match(MapPoint p1, MapPoint p2, int zoom) {
      return zoom >= 5 ? Math.abs(p1._path - p2._x) < 50 && Math.abs(p1._start - p2._y) < 50 : p1._path == p2._x && p1._start == p2._y;
   }

   static final LabelPath add(LabelPath list, LabelPath label, int zoom) {
      boolean spliced = false;
      boolean prepend = false;
      boolean append = false;
      LabelPath candidate = list;
      LabelPath previous = null;
      LabelPath startMatch = null;

      while (candidate != null) {
         if (match(label._start, candidate.getStart(), zoom)) {
            label._dir = -1;
            prepend = true;
         }

         if (match(label._start, candidate.getEnd(), zoom)) {
            label._dir = 1;
            append = true;
         }

         if (prepend) {
            label.addSplice(candidate);
            if (previous == null) {
               list = label;
            } else {
               previous._next = label;
            }

            spliced = true;
            startMatch = label;
            break;
         }

         if (append) {
            candidate.addSplice(label);
            spliced = true;
            startMatch = candidate;
            break;
         }

         previous = candidate;
         candidate = candidate._next;
      }

      prepend = false;
      append = false;
      candidate = list;
      previous = null;

      while (candidate != null) {
         if (candidate == startMatch) {
            previous = candidate;
            candidate = candidate._next;
         } else {
            if (match(label._end, candidate.getStart(), zoom)) {
               if (candidate.getStart()._x > candidate.getEnd()._x || label._start._x < label._end._x) {
                  label._dir = 1;
               }

               prepend = true;
            }

            if (match(label._end, candidate.getEnd(), zoom)) {
               label._dir = -1;
               append = true;
            }

            if (prepend) {
               label.addSplice(candidate);
               if (previous == null) {
                  list = label;
               } else {
                  previous._next = label;
               }

               spliced = true;
               break;
            }

            if (append) {
               candidate.addSplice(label);
               spliced = true;
               break;
            }

            previous = candidate;
            candidate = candidate._next;
         }
      }

      if (!spliced) {
         candidate = list;

         while (candidate._next != null) {
            candidate = candidate._next;
         }

         candidate._next = label;
      }

      return list;
   }

   final LabelPath center() {
      LabelPath currentPath = this;
      int totalLength = 0;
      this._offsetAlongPath = 0;
      totalLength = currentPath._splicedLength;
      int offsetAlongPath = (totalLength - this._textLength) / 2;
      if (offsetAlongPath < -2 && currentPath._labelData != null) {
         offsetAlongPath = totalLength - this._textLength;
         if (currentPath._lengthStart != 0) {
            if (currentPath._dir == -1) {
               offsetAlongPath = 2;
            }

            currentPath._length = currentPath._length + currentPath._lengthStart;
            currentPath._splicedLength = currentPath._splicedLength + currentPath._lengthStart;
         } else if (currentPath._lengthEnd != 0) {
            if (currentPath._dir == 1) {
               offsetAlongPath = 2;
            }

            currentPath._length = currentPath._length + currentPath._lengthEnd;
            currentPath._splicedLength = currentPath._splicedLength + currentPath._lengthEnd;
         }

         if (this._textLength > currentPath._splicedLength) {
            return null;
         }
      }

      currentPath = this;
      totalLength = currentPath._length;
      if (totalLength <= offsetAlongPath && currentPath._splice != null) {
         while (currentPath._splice != null) {
            currentPath = currentPath._splice;
            totalLength += currentPath._length;
            if (totalLength > offsetAlongPath) {
               totalLength -= currentPath._length;
               break;
            }
         }

         currentPath._offsetAlongPath = offsetAlongPath - totalLength;
         return currentPath;
      } else {
         currentPath._offsetAlongPath = offsetAlongPath;
         return currentPath;
      }
   }

   final LabelPath place() {
      LabelPath labelPath = this;
      LabelPath splicedPath = null;
      LabelPath longestPath = this;

      while (labelPath != null) {
         splicedPath = labelPath;

         while (splicedPath._splice != null) {
            splicedPath = splicedPath._splice;
         }

         int splicedLength = 0;

         while (splicedPath != null) {
            splicedLength += splicedPath._length;
            splicedPath._splicedLength = splicedLength;
            splicedPath = splicedPath._prevSplice;
         }

         labelPath = labelPath._next;
      }

      for (LabelPath var5 = this; var5 != null; var5 = var5._next) {
         if (var5._splicedLength > longestPath._splicedLength) {
            longestPath = var5;
         }
      }

      return longestPath;
   }

   final boolean render(Graphics graphics, int rotation, int paddingShield) {
      LabelPath currentPath = this;
      int offsetAlongPath = this._offsetAlongPath;
      int initialOffsetAlongPath = this._offsetAlongPath;
      boolean moveForward = false;
      boolean moveBackward = false;
      boolean triedForward = false;
      boolean triedBackward = false;
      boolean tryOutSidePoint = false;
      int advanceAmount;
      if (this._pathMarker != null) {
         advanceAmount = Math.min(this._pathMarker._rect.width / 2, this._pathMarker._rect.height / 2);
      } else {
         advanceAmount = 14;
      }

      boolean clear = false;

      while (!clear) {
         _callback.init(currentPath, rotation, paddingShield);
         _callback._test = true;
         TextOnPath.drawTextOnPath(graphics, _callback, offsetAlongPath);
         if (_callback._collision && offsetAlongPath < -2) {
            return false;
         }

         if (!_callback._collision) {
            clear = true;
         } else {
            if (_callback._printedDist > this._textLength) {
               this._textLength = _callback._printedDist;
            }

            if (moveForward) {
               int delta = _callback._printedDist + advanceAmount;

               for (offsetAlongPath += delta; currentPath != null && offsetAlongPath >= currentPath._length; currentPath = currentPath._splice) {
                  offsetAlongPath -= currentPath._length;
               }

               if (currentPath == null || offsetAlongPath + this._textLength >= currentPath._splicedLength) {
                  if (triedBackward) {
                     tryOutSidePoint = true;
                     break;
                  }

                  currentPath = this;
                  offsetAlongPath = initialOffsetAlongPath;
                  triedForward = true;
                  moveForward = false;
                  moveBackward = true;
               }
            } else if (moveBackward) {
               int delta = this._textLength - _callback._printedDist + 2;
               offsetAlongPath -= delta;

               while (currentPath != null && offsetAlongPath < 0) {
                  currentPath = currentPath._prevSplice;
                  if (currentPath != null) {
                     offsetAlongPath += currentPath._length;
                  }
               }

               if (currentPath == null) {
                  if (triedForward) {
                     tryOutSidePoint = true;
                     break;
                  }

                  currentPath = this;
                  offsetAlongPath = initialOffsetAlongPath;
                  triedBackward = true;
                  moveBackward = false;
                  moveForward = true;
               }
            } else if (_callback._printedDist < this._textLength / 2) {
               moveForward = true;
               int delta = _callback._printedDist + advanceAmount;

               for (offsetAlongPath += delta; currentPath != null && offsetAlongPath >= currentPath._length; currentPath = currentPath._splice) {
                  offsetAlongPath -= currentPath._length;
               }

               if (currentPath == null) {
                  currentPath = this;
                  offsetAlongPath = initialOffsetAlongPath;
                  triedForward = true;
                  moveForward = false;
                  moveBackward = true;
               }
            } else {
               moveBackward = true;
               int delta = this._textLength - _callback._printedDist + 2;
               offsetAlongPath -= delta;

               while (currentPath != null && offsetAlongPath < 0) {
                  currentPath = currentPath._prevSplice;
                  if (currentPath != null) {
                     offsetAlongPath += currentPath._length;
                  }
               }

               if (currentPath == null) {
                  currentPath = this;
                  offsetAlongPath = initialOffsetAlongPath;
                  triedBackward = true;
                  moveBackward = false;
                  moveForward = true;
               }
            }
         }
      }

      if (tryOutSidePoint) {
         if (this._pathMarker != null) {
            return false;
         }

         currentPath = this;
         offsetAlongPath = initialOffsetAlongPath;
         if (currentPath._lengthStart != 0) {
            if (currentPath._dir == -1) {
               offsetAlongPath = 2;
            } else {
               offsetAlongPath = (currentPath._length - currentPath._textLength) / 2;
            }
         } else if (currentPath._lengthEnd != 0) {
            if (currentPath._dir == 1) {
               offsetAlongPath = 2;
            } else {
               offsetAlongPath = (currentPath._length - currentPath._textLength) / 2;
            }
         }

         _callback.init(currentPath, rotation, this._paddingShield);
         _callback._test = true;
         TextOnPath.drawTextOnPath(graphics, _callback, offsetAlongPath);
         if (_callback._collision) {
            return false;
         }
      }

      _callback.init(currentPath, rotation, this._paddingShield);
      _callback._test = false;
      TextOnPath.drawTextOnPath(graphics, _callback, offsetAlongPath);
      return true;
   }
}
