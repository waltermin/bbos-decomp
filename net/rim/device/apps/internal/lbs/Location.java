package net.rim.device.apps.internal.lbs;

import java.util.Vector;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.FontFamily;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.component.ActiveFieldCookie;
import net.rim.device.api.ui.component.CookieProvider;
import net.rim.device.apps.api.framework.model.VerbProvider;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.ui.CookieProviderUtilities;
import net.rim.device.apps.internal.lbs.maplet.MapPoint;
import net.rim.vm.Array;

public class Location implements ActiveFieldCookie, VerbProvider {
   public int _latitude;
   public int _longitude;
   public int _zoom;
   public String _label;
   public String _description;
   public String _address;
   public String _city;
   public String _region;
   public String _country;
   public String _postalCode;
   public String _phone;
   public String _fax;
   public String _url;
   public String _email;
   public String _categories;
   public double _rating = (double)-4616189618054758400L;
   public String _source;
   public String _sponsored;
   public int _bearing = -1;
   public float _speed;
   public Route _route;
   public int _uid;
   public String _folderHierarchy;
   public Bitmap _routeSign;
   public String _routeSignText;
   public String _routName;
   public int _action;
   public byte _merit;
   MapField _field;
   MapPoint _point = new MapPoint();
   protected static final Arrow _pointerArrow = new PointerArrow();
   protected static final Arrow _bearingArrow = new BearingArrow();

   boolean isSaved() {
      return this._uid != 0;
   }

   @Override
   public Verb getVerbs(Object context, Verb[] verbs) {
      Verb verb = new DisplayMapVerb(this);
      int index = verbs.length;
      Array.resize(verbs, index + 1);
      verbs[index] = verb;
      return verb;
   }

   public Location copy(Location original) {
      Location copy = new Location();
      this.copyFrom(copy, original);
      return copy;
   }

   void copyFrom(Location copy, Location original) {
      copy._latitude = original._latitude;
      copy._longitude = original._longitude;
      copy._zoom = original._zoom;
      copy._label = original._label;
      copy._description = original._description;
      copy._address = original._address;
      copy._city = original._city;
      copy._region = original._region;
      copy._country = original._country;
      copy._postalCode = original._postalCode;
      copy._phone = original._phone;
      copy._fax = original._fax;
      copy._url = original._url;
      copy._email = original._email;
      copy._categories = original._categories;
      copy._rating = original._rating;
      copy._source = original._source;
      copy._sponsored = original._sponsored;
      copy._bearing = original._bearing;
      copy._speed = original._speed;
      copy._route = original._route;
      copy._uid = original._uid;
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public void paint(Graphics graphics, Transform transform, boolean focus, boolean drawCaption, int number) {
      this._point._x = this._longitude;
      this._point._y = this._latitude;
      int side = transform._cropView.hitTest(this._point._x, this._point._y);
      if (side == 0) {
         transform.convertWorldToScreen(this._point);
         if (transform._rotation == 0) {
            this.paintOnScreen(graphics, transform, this._point._x, this._point._y, focus, drawCaption, number);
            return;
         }

         side = transform._screenView.hitTest(this._point._x, this._point._y);
         if (side == 0) {
            this.paintOnScreen(graphics, transform, this._point._x, this._point._y, focus, drawCaption, number);
            return;
         }

         this._point._x = this._longitude;
         this._point._y = this._latitude;
         int dx = this._point._x - transform._longitude;
         int dy = this._point._y - transform._latitude;
         this._point._x += dx;
         this._point._y += dy;
         side = transform._cropView.hitTest(this._point._x, this._point._y);
      }

      transform._cropView.intersection(this._point, side, transform._longitude, transform._latitude, this._point._x, this._point._y);
      transform.convertWorldToScreen(this._point);
      int rotation = Utilities.getRotation(this._point._x - transform._screenAnchor._x, this._point._y - transform._screenAnchor._y) * 10;
      if (transform._rotation != 0) {
         side = transform._screenView.hitTest(this._point._x, this._point._y);
         if (side == 0) {
            int dx = this._point._x - transform._screenAnchor._x;
            int dy = this._point._y - transform._screenAnchor._y;
            this._point._x += dx;
            this._point._y += dy;
            side = transform._screenView.hitTest(this._point._x, this._point._y);
         }

         transform._screenView.intersection(this._point, side, transform._screenAnchor._x, transform._screenAnchor._y, this._point._x, this._point._y);
      }

      int colourFill = 16711680;
      boolean markerFlipped = false;
      int oldAlpha = graphics.getGlobalAlpha();
      graphics.setGlobalAlpha(60);
      _pointerArrow.paint(graphics, this._point._x, this._point._y, rotation, colourFill);
      graphics.setGlobalAlpha(oldAlpha);
      if (focus && drawCaption) {
         Bitmap logo;
         if (this._routeSign != null) {
            logo = this._routeSign;
         } else {
            logo = this.getBusinessLogo(this._label);
         }

         if (this._label != null && this._label.length() > 0 || logo != null) {
            Font font = null;

            label72:
            try {
               font = FontFamily.forName("BBSansSerif").getFont(1, 12, 0, 2, 0);
            } catch (Throwable var15) {
               e.printStackTrace();
               break label72;
            }

            String distance = Distance.formatDistance(this._latitude, this._longitude, transform._latitude, transform._longitude);
            _pointerArrow.drawCaption(
               graphics,
               transform,
               font,
               this._point._x,
               this._point._y,
               this._label,
               logo,
               distance,
               true,
               markerFlipped,
               this._routeSignText,
               this._routName,
               this._action
            );
         }
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   void paintOnScreen(Graphics graphics, Transform transform, int x, int y, boolean focus, boolean drawCaption, int number) {
      int colourStroke = 0;
      int colourFill = !focus && this.isRoute() ? 8421504 : 16711680;
      if (this._bearing != -1) {
         int rotation = 90 - this._bearing;
         if (rotation < 0) {
            rotation += 360;
         }

         boolean markerFlipped = false;
         if (this._speed > 0L) {
            _bearingArrow.paint(graphics, this._point._x, this._point._y, rotation, 16711680);
         } else {
            int r = 8;
            graphics.setColor(16711680);
            graphics.fillArc(x - r - 1, y - r - 1, 2 * r, 2 * r, 0, 360);
         }

         if (focus && drawCaption && this._label != null && this._label.length() > 0) {
            _bearingArrow.drawCaption(graphics, transform, null, this._point._x, this._point._y, this._label, null, null, true, markerFlipped, null, null, -1);
         }
      } else {
         Bitmap logo = null;
         boolean markerFlipped = _pointerArrow.drawMarker(graphics, this._point._x, this._point._y, colourStroke, colourFill, logo);
         if (number != -1) {
            int oldColour = graphics.getColor();
            int oldAlpha = graphics.getGlobalAlpha();
            String strNumber = "" + number;
            Font font = null;

            label155:
            try {
               font = FontFamily.forName("BBSansSerif").getFont(0, 11, 0, 2, 0);
            } catch (Throwable var23) {
               e.printStackTrace();
               break label155;
            }

            graphics.setFont(font);
            int textLength = font.getAdvance(strNumber);
            int textHeight = font.getHeight();
            graphics.setColor(16777215);
            graphics.setGlobalAlpha(255);
            if (!markerFlipped) {
               graphics.drawText(strNumber, x - textLength / 2, y - textHeight - 23, 64, textLength);
            } else {
               graphics.drawText(strNumber, x - textLength / 2, y + 23, 64, textLength);
            }

            graphics.setColor(oldColour);
            graphics.setGlobalAlpha(oldAlpha);
         }

         Bitmap var24 = null;
         if (focus && drawCaption && this._label != null && this._label.length() > 0) {
            Font font = null;

            label146:
            try {
               font = FontFamily.forName("BBSansSerif").getFont(1, 12, 0, 2, 0);
            } catch (Throwable var22) {
               e.printStackTrace();
               break label146;
            }

            if (this._routeSign != null) {
               var24 = this._routeSign;
            } else {
               var24 = this.getBusinessLogo(this._label);
            }

            _pointerArrow.drawCaption(
               graphics,
               transform,
               font,
               this._point._x,
               this._point._y,
               this._label,
               var24,
               null,
               true,
               markerFlipped,
               this._routeSignText,
               this._routName,
               this._action
            );
         }
      }
   }

   Bitmap getBusinessLogo(String label) {
      return null;
   }

   public boolean isRoute() {
      return this._route != null;
   }

   @Override
   public MenuItem getFocusVerbs(CookieProvider provider, Object context, Vector items) {
      return CookieProviderUtilities.getFocusVerbsForActiveField(this, context, items);
   }

   @Override
   public boolean invokeApplicationKeyVerb() {
      return false;
   }

   public Location() {
   }

   @Override
   public String toString() {
      if (this._label != null && !this._label.equals("")) {
         return this._label;
      } else {
         return this._description != null && !this._description.equals("") ? this._description : "lat=" + this._latitude + ", lon=" + this._longitude;
      }
   }

   @Override
   public int hashCode() {
      return this._label != null ? this._label.hashCode() : super.hashCode();
   }

   public Location(int latitude, int longitude, int zoom) {
      this(
         latitude,
         longitude,
         zoom,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         (double)-4616189618054758400L,
         null,
         null,
         null,
         null
      );
   }

   public Location(
      int latitude,
      int longitude,
      int zoom,
      String label,
      String description,
      String address,
      String city,
      String region,
      String country,
      String postalCode,
      String phone,
      String fax,
      String url,
      String email,
      String categories,
      double rating,
      String source,
      String sponsored,
      Route route,
      String folderHierarchy
   ) {
      this._latitude = latitude;
      this._longitude = longitude;
      this._zoom = zoom;
      this._label = label;
      this._description = description;
      this._address = address;
      this._city = city;
      this._region = region;
      this._country = country;
      this._postalCode = postalCode;
      this._phone = phone;
      this._fax = fax;
      this._url = url;
      this._email = email;
      this._categories = categories;
      this._rating = rating;
      this._source = source;
      this._sponsored = sponsored;
      this._route = route;
      this._folderHierarchy = folderHierarchy;
   }

   public Location(int latitude, int longitude, int zoom, String label) {
      this(
         latitude,
         longitude,
         zoom,
         label,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         (double)-4616189618054758400L,
         null,
         null,
         null,
         null
      );
   }
}
