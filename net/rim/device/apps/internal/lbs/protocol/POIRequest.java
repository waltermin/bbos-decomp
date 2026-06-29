package net.rim.device.apps.internal.lbs.protocol;

import java.io.DataInputStream;
import net.rim.device.api.io.IOUtilities;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.apps.api.addressbook.MailingAddressModel;
import net.rim.device.apps.internal.lbs.LBSApplication;
import net.rim.device.apps.internal.lbs.LBSOptions;
import net.rim.device.apps.internal.lbs.model.SearchAddressModel;
import net.rim.device.apps.internal.lbs.resources.LBSResources;

public final class POIRequest extends Request {
   protected String _keywords;
   protected int _bly;
   protected int _blx;
   protected int _try;
   protected int _trx;
   protected byte _zoom;
   private MailingAddressModel _model;
   private long _originLatitude;
   private long _originLongitude;
   private byte[] _rawXMLData;
   protected String _poiXmlString;
   public static final int SUCCESS;
   public static final int INVALID_REQUEST;
   public static final int SERVER_INTERNAL_ERROR;
   public static final int NO_DATA;
   public static final int SERVER_BUSY;
   public static final int SERVER_TEMPORARILY_UNAVAILABLE;

   @Override
   public final byte getCommand() {
      return 17;
   }

   public static final POIRequest request(Request$Listener listener, int BLy, int BLx, int TRy, int TRx, byte zoom, String keywords, SearchAddressModel model) {
      System.out.println("POIRequest.request()");
      POIRequest poiRequest = new POIRequest(listener, BLy, BLx, TRy, TRx, zoom, keywords, model);
      RequestThread.addRequest(poiRequest);
      return poiRequest;
   }

   public POIRequest(Request$Listener listener, int BLy, int BLx, int TRy, int TRx, byte zoom, String keywords, MailingAddressModel model) {
      System.out.println("POIRequest() constructor");
      System.out.println(((StringBuffer)(new Object("BLy="))).append(BLy).toString());
      System.out.println(((StringBuffer)(new Object("BLx="))).append(BLx).toString());
      System.out.println(((StringBuffer)(new Object("TRy="))).append(TRy).toString());
      System.out.println(((StringBuffer)(new Object("TRx="))).append(TRx).toString());
      System.out.println(((StringBuffer)(new Object("zoom="))).append(zoom).toString());
      System.out.println(((StringBuffer)(new Object("keywords="))).append(keywords).toString());
      super._listener = listener;
      this._bly = BLy;
      this._blx = BLx;
      this._try = TRy;
      this._trx = TRx;
      this._zoom = zoom;
      this._keywords = keywords;
      this._model = (MailingAddressModel)model;
   }

   public final byte[] getRawData() {
      return this._rawXMLData;
   }

   @Override
   public final boolean writeRequest(DataBuffer db) {
      System.out.println("POIRequest.writeRequest()");
      if (this._model == null) {
         this.writeAttribute("", db);
         this.writeAttribute("", db);
         this.writeAttribute("", db);
         this.writeAttribute("", db);
         this.writeAttribute("ENG", db);
         db.writeLong((this._bly + this._try) / 2);
         db.writeLong((this._blx + this._trx) / 2);
      } else {
         String address = this._model.getAddressLine1() != null ? this._model.getAddressLine1() : "";
         String and = ((StringBuffer)(new Object(" "))).append(LBSResources.getString(226)).append(" ").toString();
         int ix = address.indexOf(and);
         if (ix > 0) {
            address = ((StringBuffer)(new Object()))
               .append(address.substring(0, ix))
               .append(" & ")
               .append(address.substring(ix + and.length(), address.length()))
               .toString();
         }

         this.writeAttribute(address, db);
         this.writeAttribute(this._model.getCity(), db);
         this.writeAttribute(this._model.getArea(), db);
         this.writeAttribute(this._model.getCountry(), db);
         this.writeAttribute("ENG", db);
         db.writeLong(this._originLatitude);
         db.writeLong(this._originLongitude);
      }

      db.writeInt(this._bly);
      db.writeInt(this._blx);
      db.writeInt(this._try);
      db.writeInt(this._trx);
      db.writeByte(this._zoom);
      this.writeAttribute(this._keywords, db);
      return true;
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

   // $VF: Could not inline inconsistent finally blocks
   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void readResponse(DataInputStream dis, int length) {
      System.out.println("POIRequest.readResponse(dis)");

      try {
         byte[] data = IOUtilities.streamToBytes(dis);
         this._poiXmlString = (String)(new Object(data, "UTF-8"));
         LBSOptions._dataCount += data.length;
         LBSOptions.setInt(8640332184073563572L, LBSOptions._dataCount);
         String encoding = "UTF-8";
         boolean var10 = false /* VF: Semaphore variable */;

         label44:
         try {
            var10 = true;
            int e = this._poiXmlString.indexOf("encoding=");
            if (e != -1) {
               e += 9;
               encoding = this._poiXmlString.substring(e + 1, this._poiXmlString.indexOf(34, e + 1));
            }

            this._rawXMLData = this._poiXmlString.getBytes(encoding);
            var10 = false;
         } finally {
            if (var10) {
               EventLogger.logEvent(LBSApplication.UID, ((StringBuffer)(new Object("UnsupportedEncoding: "))).append(encoding).toString().getBytes(), 2);
               this._rawXMLData = this._poiXmlString.getBytes();
               break label44;
            }
         }

         this._poiXmlString = (String)(new Object(data, "UTF-8"));
      } catch (Throwable var12) {
         System.out.println("*** error while converting input stream to byte array ***");
         System.out.println(ioe);
         return;
      }
   }

   public final String getPOIXmlString() {
      return this._poiXmlString;
   }

   @Override
   public final String getURL() {
      return LBSOptions.getURL(3589376987760903020L);
   }
}
