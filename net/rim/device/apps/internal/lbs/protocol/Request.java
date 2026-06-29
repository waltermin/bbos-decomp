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
   public static final int MAP_DATA_REQUEST;
   public static final int LOCATOR_REQUEST;
   public static final int DIRECTIONS_REQUEST;
   public static final int POI_REQUEST;
   public static final int SUCCESS;
   public static final int ERROR;
   public static final int UNNECESSARY;
   public static final int DATA_ERROR;
   public static final int CONNECT_ERROR;
   public static final int WIRELESS_ERROR;
   public static final int COVERAGE_ERROR;
   public static final int BIS_ERROR;
   public static final int BES_ERROR;
   public static final int BIS_BES_ERROR;
   public static final int MDS_UID_ERROR;
   public static final int URL_CONTEXT_ERROR;

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
