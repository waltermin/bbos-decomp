package net.rim.device.apps.internal.lbs;

import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.util.Arrays;
import net.rim.device.apps.internal.lbs.resources.LBSResources;

final class CurrentDecisions {
   Route _route;
   int _focus;
   int _count;
   Decision[] _decisions = new Decision[0];
   XYRect _bbox = new XYRect();
   MapField _mapField;
   boolean _startModified = false;

   public CurrentDecisions(MapField mapField, Route route) {
      this._mapField = mapField;
      this._route = route;
   }

   final Decision getFocus() {
      return this._focus >= 0 && this._focus < this._count ? this._decisions[this._focus] : null;
   }

   final Decision getAt(int i) {
      return this._decisions[i];
   }

   public final void add(Decision decision) {
      this._count++;
      decision._label = this._count + ". " + decision._label;
      decision._number = this._count;
      Arrays.add(this._decisions, decision);
      this._bbox.union(decision._longitude, decision._latitude, 1, 1);
      this._route.setBBox(this._bbox);
      if (this._count == 2) {
         String direction = "";
         Decision start = this._decisions[0];
         double dlat = decision._latitude - start._latitude;
         double dlon = decision._longitude - start._longitude;
         int theta = (int)(Distance.atan(dlat / dlon) * 4640537203540230144L / 4614256656552045848L);
         if (theta < 0) {
            theta += 360;
         }

         if (dlon < 0L) {
            theta += 180;
         }

         if (theta >= 360) {
            theta -= 360;
         }

         if (theta >= 337 || theta <= 23) {
            direction = LBSResources.getString(188);
         } else if (theta > 23 && theta < 68) {
            direction = LBSResources.getString(187);
         } else if (theta >= 68 && theta <= 113) {
            direction = LBSResources.getString(186);
         } else if (theta > 113 && theta < 158) {
            direction = LBSResources.getString(193);
         } else if (theta >= 158 && theta <= 203) {
            direction = LBSResources.getString(192);
         } else if (theta > 203 && theta < 248) {
            direction = LBSResources.getString(191);
         } else if (theta >= 248 && theta <= 293) {
            direction = LBSResources.getString(190);
         } else if (theta > 293 && theta < 337) {
            direction = LBSResources.getString(189);
         }

         if (start._name.length() == 0) {
            start._caption = "1. " + MessageFormat.format(LBSResources.getString(266), new String[]{direction});
         } else {
            start._caption = "1. " + MessageFormat.format(LBSResources.getString(267), new String[]{direction, start._name});
         }

         float numKilometres = start._distance / 1148846080;
         start._caption = start._caption + " " + MessageFormat.format(LBSResources.getString(199), new String[]{Distance.formatDistance(numKilometres)});
         start._label = start._caption;
      }
   }

   final boolean moveFocus(int inc, boolean refocus) {
      throw new RuntimeException("cod2jar: field: unresolved slot");
   }

   final boolean setFocus(Decision decision) {
      if (decision == null) {
         this._focus = -1;
         return true;
      }

      for (int i = 0; i < this._route._decisions._count; i++) {
         if (this.getAt(i) == decision) {
            this._focus = i;
            return true;
         }
      }

      return false;
   }

   final boolean setFocusIndex(int index) {
      throw new RuntimeException("cod2jar: field: unresolved slot");
   }

   final void paint(Graphics graphics, Transform transform, boolean drawCaption) {
      Decision decision = this.getFocus();
      if (decision != null) {
         decision.paint(graphics, transform, true, drawCaption);
      }
   }
}
