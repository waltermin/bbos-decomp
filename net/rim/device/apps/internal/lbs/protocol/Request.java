package net.rim.device.apps.internal.lbs.protocol;

import java.io.DataInputStream;
import javax.microedition.io.Connection;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.apps.internal.lbs.LBSOptions;

public class Request {
   public byte[] _lastRequest;
   public Request$Listener _listener;
   private int _responseCode;
   protected byte _version;
   private Connection _connection;
   private boolean _connectionForcedClosed;
   protected int _length;
   boolean _useCustomURL = LBSOptions.getBoolean(-6271428560607580713L, false);
   public static final int MAP_DATA_REQUEST = 1;
   public static final int LOCATOR_REQUEST = 11;
   public static final int DIRECTIONS_REQUEST = 15;
   public static final int POI_REQUEST = 17;
   public static final int SUCCESS = 0;
   public static final int ERROR = -1;
   public static final int UNNECESSARY = -2;
   public static final int DATA_ERROR = -3;
   public static final int CONNECT_ERROR = -4;
   public static final int WIRELESS_ERROR = -5;
   public static final int COVERAGE_ERROR = -6;
   public static final int BIS_ERROR = -7;
   public static final int BES_ERROR = -8;
   public static final int BIS_BES_ERROR = -9;
   public static final int MDS_UID_ERROR = -10;
   public static final int URL_CONTEXT_ERROR = -11;

   public byte getCommand() {
      throw null;
   }

   public boolean writeRequest(DataBuffer _1) {
      throw null;
   }

   public void readResponse(DataInputStream _1, int _2) {
      throw null;
   }

   public void emptyResponse() {
   }

   public void setResponse(int responseCode) {
      this._responseCode = responseCode;
   }

   public int getResponseCode() {
      return this._responseCode;
   }

   void setVersion(byte ver) {
      this._version = ver;
   }

   public byte getVersion() {
      return this._version;
   }

   public void setLength(int length) {
      this._length = length;
   }

   public String getURL() {
      throw null;
   }

   void setConnection(Connection con) {
      this._connection = con;
   }

   boolean isConnectionForcedClosed() {
      return this._connectionForcedClosed;
   }

   void badRequest() {
   }
}
