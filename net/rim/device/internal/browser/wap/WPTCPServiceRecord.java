package net.rim.device.internal.browser.wap;

import net.rim.device.api.servicebook.ServiceBook;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.TLEUtilities;

public final class WPTCPServiceRecord {
   private String _httpProxyAddress;
   private String _connectProxyAddress;
   private String _cid;
   private String _uid;
   private boolean _persistentConnections;
   private boolean _useHttp11;
   private String _proxyAuthConfigUrl;
   private boolean _useDNSLoadBalancing;
   private String _primaryDNS;
   private String _secondaryDNS;
   private int _proxyUsernameType;
   private int _proxyPasswordType;
   private String _proxyUsernameValue;
   private String _proxyPasswordValue;
   private String _mmscUrl;
   private String _mmsMessageUrlPrefix;
   private String _mmsAuthenticationHeader;
   private int _mmsRetrievalMode = -1;
   private int _mmscVersion = 16;
   private int _defaultTcpMode;
   private String _interface;
   private int _sessionTimeout = -1;
   private boolean _alwaysSendBasicProxyAuth;
   private static final byte VERSION = 1;
   public static final byte ENCODED_TYPE_HTTP_PROXY_ADDRESS = 1;
   public static final byte ENCODED_TYPE_PERSISTENT_CONNECTIONS = 2;
   public static final byte ENCODED_TYPE_USE_HTTP_11 = 3;
   public static final byte ENCODED_TYPE_PROXY_AUTH_CONFIG_URL = 4;
   public static final byte ENCODED_TYPE_USE_DNS_LOADBALANCING = 5;
   public static final byte ENCODED_TYPE_PRIMARY_DNS = 6;
   public static final byte ENCODED_TYPE_SECONDARY_DNS = 7;
   public static final byte ENCODED_TYPE_CONNECT_PROXY_ADDRESS = 8;
   public static final byte ENCODED_TYPE_PROXY_AUTH_USERNAME_TYPE = 9;
   public static final byte ENCODED_TYPE_PROXY_AUTH_PASSWORD_TYPE = 10;
   public static final byte ENCODED_TYPE_PROXY_AUTH_USERNAME_VALUE = 11;
   public static final byte ENCODED_TYPE_PROXY_AUTH_PASSWORD_VALUE = 12;
   public static final byte ENCODED_TYPE_MMSC_URL = 13;
   public static final byte ENCODED_TYPE_MMS_RETRIEVAL_MODE = 14;
   public static final byte ENCODED_TYPE_MMS_MESSAGE_URL_PREFIX = 15;
   public static final byte ENCODED_TYPE_MMS_AUTHENTICATION_HEADER = 16;
   public static final byte ENCODED_TYPE_MMSC_VERSION = 17;
   public static final byte ENCODED_TYPE_DEFAULT_TCP_CONFIG_MODE = 18;
   public static final byte ENCODED_TYPE_INTERFACE = 19;
   public static final byte ENCODED_TYPE_SESSION_TIMEOUT = 20;
   public static final byte ENCODED_TYPE_ALWAYS_SEND_BASIC_PROXY_AUTH = 21;
   public static final int AUTH_TYPE_NOT_SPECIFIED = 0;
   public static final int AUTH_TYPE_FROM_SERVICE_RECORD = 1;
   public static final int AUTH_TYPE_RIM_QUERY_URL = 2;
   public static final int AUTH_TYPE_MD5_ESN_AKEY = 3;
   public static final int AUTH_TYPE_CLIENT_ID_ICCID = 4;
   public static final int AUTH_TYPE_CLIENT_ID_MIN = 5;
   public static final int AUTH_TYPE_CLIENT_ID_ESN = 6;
   public static final int AUTH_TYPE_CLIENT_ID_MSISDN = 7;
   public static final int AUTH_TYPE_CLIENT_ID_IMSI = 8;
   public static final int AUTH_TYPE_CLIENT_ID_IPV4 = 9;
   public static final int AUTH_TYPE_CLIENT_ID_IPV6 = 10;
   public static final int AUTH_TYPE_CLIENT_ID_ITSI = 11;
   public static final int AUTH_TYPE_CLIENT_ID_MAN = 12;
   public static final int AUTH_TYPE_CLIENT_ID_ICCID_WITHOUT_ID = 13;
   public static final int AUTH_TYPE_CLIENT_ID_MIN_WITHOUT_ID = 14;
   public static final int AUTH_TYPE_CLIENT_ID_ESN_WITHOUT_ID = 15;
   public static final int AUTH_TYPE_CLIENT_ID_MSISDN_WITHOUT_ID = 16;
   public static final int AUTH_TYPE_CLIENT_ID_IMSI_WITHOUT_ID = 17;
   public static final int AUTH_TYPE_CLIENT_ID_IPV4_WITHOUT_ID = 18;
   public static final int AUTH_TYPE_CLIENT_ID_IPV6_WITHOUT_ID = 19;
   public static final int AUTH_TYPE_CLIENT_ID_ITSI_WITHOUT_ID = 20;
   public static final int AUTH_TYPE_CLIENT_ID_MAN_WITHOUT_ID = 21;
   public static final int AUTH_TYPE_HMAC_MD5_MDN_ESN = 22;
   public static final int AUTH_TYPE_CLIENT_ID_MDN = 23;
   public static final int AUTH_TYPE_CLIENT_ID_MDN_WITHOUT_ID = 24;
   public static final byte DEFAULT_TCP_CONFIG_MODE_NOT_USED = 0;
   public static final byte DEFAULT_TCP_CONFIG_MODE_USED_FOR_EXPLICT_DEVICE_SIDE = 1;
   public static final int DEFAULT_SESSION_TIMEOUT = -1;
   public static String SERVICE_CID = "WPTCP";

   public static final WPTCPServiceRecord getRecord(ServiceRecord rec) {
      if (rec == null) {
         return null;
      }

      byte[] data = rec.getApplicationData();
      WPTCPServiceRecord newRecord = new WPTCPServiceRecord();
      newRecord._cid = rec.getCid();
      newRecord._uid = rec.getUid();
      if (data == null) {
         return newRecord;
      }

      DataBuffer tmpDataBuffer = new DataBuffer(data, 0, data.length, true);

      try {
         tmpDataBuffer.readByte();

         int type;
         while (!tmpDataBuffer.eof() && (type = tmpDataBuffer.readUnsignedByte()) != 0) {
            switch (type) {
               case 0:
                  tmpDataBuffer.skipBytes(tmpDataBuffer.readCompressedInt());
                  break;
               case 1:
               case 4:
               case 6:
               case 7:
               case 8:
               case 11:
               case 12:
               case 13:
               case 15:
               case 16:
               case 19:
               default:
                  tmpDataBuffer.setPosition(tmpDataBuffer.getPosition() - 1);
                  newRecord.setProperty(type, TLEUtilities.readStringField(tmpDataBuffer, type, true));
                  break;
               case 2:
               case 3:
               case 5:
               case 21:
                  tmpDataBuffer.setPosition(tmpDataBuffer.getPosition() - 1);
                  newRecord.setProperty(type, TLEUtilities.readIntegerField(tmpDataBuffer, type) != 0);
                  break;
               case 9:
               case 10:
               case 14:
               case 17:
               case 18:
               case 20:
                  tmpDataBuffer.setPosition(tmpDataBuffer.getPosition() - 1);
                  newRecord.setProperty(type, TLEUtilities.readIntegerField(tmpDataBuffer, type));
            }
         }

         return newRecord;
      } finally {
         ;
      }
   }

   public static final WPTCPServiceRecord getRecord(String cid, String uid) {
      if (cid != null && uid != null) {
         ServiceRecord rec = null;
         if (uid != null && cid != null) {
            ServiceBook sb = ServiceBook.getSB();
            rec = sb.getRecordByUidAndCid(uid, cid);
         }

         return getRecord(rec);
      } else {
         return null;
      }
   }

   public final byte[] getEncodedData() {
      DataBuffer tmpDataBuffer = new DataBuffer();
      tmpDataBuffer.writeByte(1);
      if (this._httpProxyAddress != null) {
         TLEUtilities.writeStringField(tmpDataBuffer, 1, this._httpProxyAddress);
      }

      if (this._connectProxyAddress != null) {
         TLEUtilities.writeStringField(tmpDataBuffer, 8, this._connectProxyAddress);
      }

      TLEUtilities.writeIntegerField(tmpDataBuffer, 2, this._persistentConnections ? 1 : 0, false);
      TLEUtilities.writeIntegerField(tmpDataBuffer, 3, this._useHttp11 ? 1 : 0, false);
      if (this._proxyAuthConfigUrl != null) {
         TLEUtilities.writeStringField(tmpDataBuffer, 4, this._proxyAuthConfigUrl);
      }

      if (this._useDNSLoadBalancing) {
         TLEUtilities.writeIntegerField(tmpDataBuffer, 5, this._useDNSLoadBalancing ? 1 : 0, false);
      } else {
         this._primaryDNS = null;
         this._secondaryDNS = null;
      }

      if (this._primaryDNS != null && this._primaryDNS.length() > 0) {
         TLEUtilities.writeStringField(tmpDataBuffer, 6, this._primaryDNS);
      }

      if (this._secondaryDNS != null && this._secondaryDNS.length() > 0) {
         TLEUtilities.writeStringField(tmpDataBuffer, 7, this._secondaryDNS);
      }

      TLEUtilities.writeIntegerField(tmpDataBuffer, 9, this._proxyUsernameType, false);
      TLEUtilities.writeIntegerField(tmpDataBuffer, 10, this._proxyPasswordType, false);
      if (this._proxyUsernameValue != null) {
         TLEUtilities.writeStringField(tmpDataBuffer, 11, this._proxyUsernameValue);
      }

      if (this._proxyPasswordValue != null) {
         TLEUtilities.writeStringField(tmpDataBuffer, 12, this._proxyPasswordValue);
      }

      if (this._mmscUrl != null) {
         TLEUtilities.writeStringField(tmpDataBuffer, 13, this._mmscUrl);
      }

      if (this._mmsMessageUrlPrefix != null) {
         TLEUtilities.writeStringField(tmpDataBuffer, 15, this._mmsMessageUrlPrefix);
      }

      if (this._mmsAuthenticationHeader != null) {
         TLEUtilities.writeStringField(tmpDataBuffer, 16, this._mmsAuthenticationHeader);
      }

      if (this._mmscVersion != 16) {
         TLEUtilities.writeIntegerField(tmpDataBuffer, 17, this._mmscVersion, false);
      }

      if (this._mmsRetrievalMode != -1) {
         TLEUtilities.writeIntegerField(tmpDataBuffer, 14, this._mmsRetrievalMode, false);
      }

      if (this._defaultTcpMode != 0) {
         TLEUtilities.writeIntegerField(tmpDataBuffer, 18, this._defaultTcpMode, false);
      }

      if (this._interface != null) {
         TLEUtilities.writeStringField(tmpDataBuffer, 19, this._interface);
      }

      if (this._sessionTimeout >= 0) {
         TLEUtilities.writeIntegerField(tmpDataBuffer, 20, this._sessionTimeout, false);
      }

      TLEUtilities.writeIntegerField(tmpDataBuffer, 21, this._alwaysSendBasicProxyAuth ? 1 : 0, false);
      return tmpDataBuffer.toArray();
   }

   public final boolean saveData() {
      byte[] encodedData = this.getEncodedData();
      ServiceBook sb = ServiceBook.getSB();
      ServiceRecord rec = sb.getRecordByUidAndCid(this._uid, this._cid);
      if (rec == null) {
         return false;
      }

      rec.setApplicationData(encodedData);
      sb.commit();
      return true;
   }

   public final String getPropertyAsString(int property) {
      switch (property) {
         case 0:
         case 2:
         case 3:
         case 5:
         case 9:
         case 10:
         case 14:
         case 17:
         case 18:
            throw new IllegalArgumentException();
         case 1:
         default:
            return this._httpProxyAddress;
         case 4:
            return this._proxyAuthConfigUrl;
         case 6:
            return this._primaryDNS;
         case 7:
            return this._secondaryDNS;
         case 8:
            return this._connectProxyAddress;
         case 11:
            return this._proxyUsernameValue;
         case 12:
            return this._proxyPasswordValue;
         case 13:
            return this._mmscUrl;
         case 15:
            return this._mmsMessageUrlPrefix;
         case 16:
            return this._mmsAuthenticationHeader;
         case 19:
            return this._interface;
      }
   }

   public final boolean getPropertyAsBoolean(int property) {
      switch (property) {
         case 2:
            return this._persistentConnections;
         case 3:
            return this._useHttp11;
         case 5:
            return this._useDNSLoadBalancing;
         case 21:
            return this._alwaysSendBasicProxyAuth;
         default:
            throw new IllegalArgumentException();
      }
   }

   public final int getPropertyAsInt(int property) {
      switch (property) {
         case 9:
            return this._proxyUsernameType;
         case 10:
            return this._proxyPasswordType;
         case 14:
            return this._mmsRetrievalMode;
         case 17:
            return this._mmscVersion;
         case 18:
            return this._defaultTcpMode;
         case 20:
            return this._sessionTimeout;
         default:
            throw new IllegalArgumentException();
      }
   }

   public final void setProperty(int property, String value) {
      switch (property) {
         case 0:
         case 2:
         case 3:
         case 5:
         case 9:
         case 10:
         case 14:
         case 17:
         case 18:
            throw new IllegalArgumentException();
         case 1:
         default:
            this._httpProxyAddress = value;
            return;
         case 4:
            this._proxyAuthConfigUrl = value;
            return;
         case 6:
            this._primaryDNS = value;
            return;
         case 7:
            this._secondaryDNS = value;
            return;
         case 8:
            this._connectProxyAddress = value;
            return;
         case 11:
            this._proxyUsernameValue = value;
            return;
         case 12:
            this._proxyPasswordValue = value;
            return;
         case 13:
            this._mmscUrl = value;
            return;
         case 15:
            this._mmsMessageUrlPrefix = value;
            return;
         case 16:
            this._mmsAuthenticationHeader = value;
            return;
         case 19:
            this._interface = value;
      }
   }

   public final void setProperty(int property, boolean value) {
      switch (property) {
         case 2:
            this._persistentConnections = value;
            return;
         case 3:
            this._useHttp11 = value;
            return;
         case 5:
            this._useDNSLoadBalancing = value;
            return;
         case 21:
            this._alwaysSendBasicProxyAuth = value;
            return;
         default:
            throw new IllegalArgumentException();
      }
   }

   public final void setProperty(int property, int value) {
      switch (property) {
         case 9:
            this._proxyUsernameType = value;
            return;
         case 10:
            this._proxyPasswordType = value;
            return;
         case 14:
            this._mmsRetrievalMode = value;
            return;
         case 17:
            this._mmscVersion = value;
            return;
         case 18:
            this._defaultTcpMode = value;
            return;
         case 20:
            this._sessionTimeout = value;
            return;
         default:
            throw new IllegalArgumentException();
      }
   }
}
