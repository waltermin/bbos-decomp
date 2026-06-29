package net.rim.device.apps.internal.lbs.protocol;

import java.io.DataInputStream;
import net.rim.device.api.io.IOUtilities;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.apps.api.addressbook.MailingAddressModel;
import net.rim.device.apps.internal.lbs.LBSOptions;
import net.rim.device.apps.internal.lbs.Location;
import net.rim.device.apps.internal.lbs.MapScreen;
import net.rim.device.apps.internal.lbs.locator.SearchUtility;
import net.rim.device.apps.internal.lbs.maplet.MapPoint;
import net.rim.device.apps.internal.lbs.render.LabelRender;
import net.rim.device.apps.internal.lbs.resources.LBSResources;

public final class SearchRequest extends Request {
   private Location[] _searchMatches;
   private MailingAddressModel _model;
   private long _originLatitude;
   private long _originLongitude;
   private int _zoomLevel = 10;
   public static final int SUCCESS = 0;
   public static final int INVALID_REQUEST = 1;
   public static final int SERVER_INTERNAL_ERROR = 2;
   public static final int UNSUPPORTED_ADDRESS_TYPE = 3;
   public static final int INVALID_ADDRESS_NUMBER = 4;
   public static final int ITEM_NOT_FOUND = 5;
   public static final int TOO_MANY_ITEMS = 6;
   public static final int UNSUPPORTED_CHARACTER = 7;
   public static final int INVALID_STATE = 8;
   public static final int INVALID_COUNTRY = 9;
   public static final int INVALID_STATE_AND_COUNTRY = 10;
   public static final int CITY_NOT_FOUND = 11;
   public static final int CITY_AMBIGUOUS_PLEASE_SPECIFY_STATE = 13;
   public static final int MALFORMED_INTERSECTION_ADDRESS = 14;
   public static final int CITY_NOT_SPECIFIED = 15;
   public static final int INTERNAL_LOOKUP = -100;
   private static final SearchRequest _internalSearchRequest = new SearchRequest(null, null);

   @Override
   public final byte getCommand() {
      return 11;
   }

   public static final void request(Request$Listener listener, MailingAddressModel model) {
      RequestThread.addRequest(new SearchRequest(listener, model));
   }

   public SearchRequest(Request$Listener listener, MailingAddressModel model) {
      this._model = model;
      super._listener = listener;
      MapPoint p = MapScreen.getMapCentre();
      this._originLatitude = p._y;
      this._originLongitude = p._x;
      this._zoomLevel = this.calculateZoom(this._model);
   }

   private final int calculateZoom(MailingAddressModel model) {
      if (model != null) {
         String text = this.getFromAddressLine(model);
         if (text.length() > 0) {
            return 1;
         }

         text = model.getCity();
         if (text != null && text.length() > 0) {
            return 5;
         }

         text = model.getArea();
         if (text != null && text.length() > 0) {
            return 6;
         }

         text = model.getCountry();
         if (text != null && text.length() > 0) {
            return 8;
         }
      }

      return 10;
   }

   private final String getFromAddressLine(MailingAddressModel model) {
      String addressText = "";
      if (model != null) {
         addressText = model.getAddressLine1();
         if (addressText == null || addressText.length() == 0) {
            addressText = model.getAddressLine2();
         }

         if (addressText == null) {
            addressText = "";
         }
      }

      return addressText;
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   final int writeAttribute(String value, DataBuffer db) {
      int length = 0;
      if (value != null) {
         value = value.trim();
         db.writeByte(0);
         db.writeByte(0);
         int pos = db.getPosition();

         try {
            db.writeUTF(value);
         } catch (Throwable var7) {
            e.printStackTrace();
            return db.getPosition() - pos;
         }

         return db.getPosition() - pos;
      } else {
         db.writeInt(length);
         return length;
      }
   }

   @Override
   public final boolean writeRequest(DataBuffer db) {
      int length = 0;
      String address = this.getFromAddressLine(this._model);
      String and = ((StringBuffer)(new Object(" "))).append(LBSResources.getString(226)).append(" ").toString();
      int ix = address.indexOf(and);
      if (ix > 0) {
         address = ((StringBuffer)(new Object()))
            .append(address.substring(0, ix))
            .append(" & ")
            .append(address.substring(ix + and.length(), address.length()))
            .toString();
      } else {
         ix = address.indexOf(and.toUpperCase());
         if (ix > 0) {
            address = ((StringBuffer)(new Object()))
               .append(address.substring(0, ix))
               .append(" & ")
               .append(address.substring(ix + and.length(), address.length()))
               .toString();
         }
      }

      length = this.writeAttribute(address, db);
      String city = this._model.getCity();
      if (city == null) {
         city = "";
      }

      int index = city.indexOf("(");
      if (index != -1) {
         city = city.substring(0, index);
      }

      length += this.writeAttribute(city, db);
      length += this.writeAttribute(this._model.getArea(), db);
      length += this.writeAttribute(this._model.getCountry(), db);
      length += this.writeAttribute("ENG", db);
      db.writeLong(this._originLatitude);
      length += 8;
      db.writeLong(this._originLongitude);
      length += 8;
      return length > 0;
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void readResponse(DataInputStream dis, int byteLength) {
      try {
         byte[] data = IOUtilities.streamToBytes(dis);

         for (int i = 0; i < 3; i++) {
            if (data[i] != 0) {
               this.setResponse(-3);
               return;
            }
         }

         LBSOptions._dataCount += data.length;
         LBSOptions.setInt(8640332184073563572L, LBSOptions._dataCount);
         DataBuffer db = null;
         if (data != null) {
            db = (DataBuffer)(new Object(data, 0, data.length, true));
         }

         int length = db.readInt();
         this._searchMatches = new Location[length];

         for (int i = 0; i < length; i++) {
            this._searchMatches[i] = new Location();
            this.readMatch(this._searchMatches[i], db, super._version);
            this._searchMatches[i]._zoom = this._zoomLevel;
         }
      } catch (Throwable var8) {
         System.out.println(ioe);
         return;
      }
   }

   private final void readMatch(Location location, DataBuffer db, int version) {
      location._longitude = (int)db.readLong();
      location._latitude = (int)db.readLong();
      String locationName;
      if (version >= 11) {
         db.readByte();
         db.readByte();
         locationName = db.readUTF();
      } else {
         int stringLength = db.readInt();
         locationName = this.readString(db, stringLength);
      }

      for (int offset = locationName.indexOf(","); offset != -1; offset = locationName.indexOf(",", offset + 1)) {
         if (!locationName.substring(offset + 1, offset + 2).equals(" ")) {
            locationName = ((StringBuffer)(new Object()))
               .append(locationName.substring(0, offset + 1))
               .append(" ")
               .append(locationName.substring(offset + 1, locationName.length()))
               .toString();
         }
      }

      locationName = LabelRender.toLowerCase(locationName, 2);
      location._label = locationName;
      SearchUtility.parseLocationName(location, locationName, this._model);
      if (version >= 11) {
         db.readByte();
         db.readByte();
         db.readUTF();
      } else {
         int stringLength = db.readInt();
         this.readString(db, stringLength);
      }

      location._merit = db.readByte();
   }

   private final String readString(DataBuffer db, int length) {
      if (length <= 0) {
         return null;
      }

      byte[] data = new byte[length];

      for (int i = 0; i < length; i++) {
         data[i] = db.readByte();
      }

      return (String)(new Object(data));
   }

   public final Location[] getResults() {
      return this._searchMatches;
   }

   @Override
   public final String getURL() {
      return LBSOptions.getURL(6933732722635403673L);
   }

   static {
      _internalSearchRequest._searchMatches = new Location[1];
   }
}
