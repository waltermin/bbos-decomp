package net.rim.device.apps.internal.lbs.protocol;

import java.io.DataInputStream;
import net.rim.device.api.io.IOUtilities;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.apps.internal.lbs.LBSApplication;
import net.rim.device.apps.internal.lbs.LBSOptions;
import net.rim.device.apps.internal.lbs.model.SearchAddressModel;

public final class DirectionsRequest extends Request {
   protected String _fromAddress;
   protected String _toAddress;
   protected int _fromLat;
   protected int _fromLong;
   protected int _toLat;
   protected int _toLong;
   protected String _routeXMLstring;
   protected byte[] _rawXMLData;
   public static final int SUCCESS;
   public static final int INVALID_REQUEST;
   public static final int SERVER_INTERNAL_ERROR;
   public static final int FAILED_TO_COMPUTE_NO_ROAD_ORIG;
   public static final int FAILED_TO_COMPUTE_NO_ROAD_DEST;
   public static final int FAILED_TO_COMPUTE_NO_PATH;
   public static final int SERVER_BUSY;

   @Override
   public final byte getCommand() {
      return 15;
   }

   public static final DirectionsRequest request(
      Request$Listener listener, SearchAddressModel model, String fromAddress, String toAddress, int fromLat, int fromLong, int toLat, int toLong
   ) {
      System.out.println("DirectionsRequest.request()");
      DirectionsRequest directionsRequest = new DirectionsRequest(listener, model, fromAddress, toAddress, fromLat, fromLong, toLat, toLong);
      RequestThread.addRequest(directionsRequest);
      return directionsRequest;
   }

   public DirectionsRequest(
      Request$Listener listener, SearchAddressModel model, String fromAddress, String toAddress, int fromLat, int fromLong, int toLat, int toLong
   ) {
      System.out.println("DirectionsRequest() constructor");
      System.out.println(((StringBuffer)(new Object("fromAddress="))).append(fromAddress).toString());
      System.out.println(((StringBuffer)(new Object("toAddress="))).append(toAddress).toString());
      System.out.println(((StringBuffer)(new Object("fromLat="))).append(fromLat).toString());
      System.out.println(((StringBuffer)(new Object("fromLong="))).append(fromLong).toString());
      System.out.println(((StringBuffer)(new Object("toLat="))).append(toLat).toString());
      System.out.println(((StringBuffer)(new Object("toLong="))).append(toLong).toString());
      super._listener = listener;
      this._fromAddress = fromAddress;
      this._toAddress = toAddress;
      this._fromLat = fromLat;
      this._fromLong = fromLong;
      this._toLat = toLat;
      this._toLong = toLong;
   }

   @Override
   public final boolean writeRequest(DataBuffer db) {
      System.out.println("DirectionsRequest.writeRequest()");
      db.writeInt(this._fromLat);
      db.writeInt(this._fromLong);
      db.writeInt(this._toLat);
      db.writeInt(this._toLong);
      return true;
   }

   // $VF: Could not inline inconsistent finally blocks
   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void readResponse(DataInputStream dis, int length) {
      System.out.println("DirectionsRequest.readResponse(dis)");

      try {
         this._rawXMLData = IOUtilities.streamToBytes(dis);
         LBSOptions._dataCount = LBSOptions._dataCount + this._rawXMLData.length;
         LBSOptions.setInt(8640332184073563572L, LBSOptions._dataCount);
         String encoding = "UTF-8";
         boolean var9 = false /* VF: Semaphore variable */;

         try {
            var9 = true;
            this._routeXMLstring = (String)(new Object(this._rawXMLData));
            int e = this._routeXMLstring.indexOf("encoding=");
            if (e != -1) {
               e += 9;
               encoding = this._routeXMLstring.substring(e + 1, this._routeXMLstring.indexOf(39, e + 1));
            }

            this._routeXMLstring = (String)(new Object(this._rawXMLData, encoding));
            this._rawXMLData = this._routeXMLstring.getBytes(encoding);
            var9 = false;
         } finally {
            if (var9) {
               EventLogger.logEvent(LBSApplication.UID, ((StringBuffer)(new Object("UnsupportedEncoding: "))).append(encoding).toString().getBytes(), 2);
               this._routeXMLstring = (String)(new Object(this._rawXMLData, encoding));
               this._rawXMLData = this._routeXMLstring.getBytes();
               return;
            }
         }
      } catch (Throwable var11) {
         System.out.println("*** error while converting input stream to byte array ***");
         System.out.println(ioe);
         return;
      }
   }

   public final String getRouteXMLstring() {
      return this._routeXMLstring;
   }

   public final byte[] getRawXMLData() {
      return this._rawXMLData;
   }

   @Override
   public final String getURL() {
      return LBSOptions.getURL(-254277793043409026L);
   }
}
