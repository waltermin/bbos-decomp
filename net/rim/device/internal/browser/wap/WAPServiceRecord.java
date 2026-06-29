package net.rim.device.internal.browser.wap;

import java.io.IOException;
import net.rim.device.api.servicebook.ServiceBook;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.synchronization.ConverterUtilities;
import net.rim.device.api.util.DataBuffer;

public final class WAPServiceRecord {
   private int _secureAccess = 0;
   private String _authUsername;
   private String _authPassword;
   private int _sessionResume = 1;
   private int _wtlsMode = 0;
   private int _wtlsClientIdType = -1;
   private String _wtlsClientIdValue = "";
   private boolean _containsWtlsValues = true;
   private String _cid;
   private String _uid;
   private int _wap20Conformance = 0;
   private String _mmscUrl;
   private String _mmsMessageUrlPrefix;
   private String _mmsAuthenticationHeader;
   private int _mmsRetrievalMode = -1;
   private int _mmscVersion = 16;
   private static final byte VERSION = 1;
   private static final byte ENCODED_TYPE_SECURE_ACCESS = 1;
   private static final byte ENCODED_TYPE_AUTH_USERNAME = 2;
   private static final byte ENCODED_TYPE_AUTH_PASSWORD = 3;
   private static final byte ENCODED_TYPE_SESSION_RESUME = 4;
   private static final byte ENCODED_TYPE_WTLS_MODE = 5;
   private static final byte ENCODED_TYPE_WTLS_CLIENT_ID_TYPE = 6;
   private static final byte ENCODED_TYPE_WTLS_CLIENT_ID_VALUE = 7;
   private static final byte ENCODED_TYPE_WAP_20_CONFORMANCE = 8;
   private static final byte ENCODED_TYPE_MMSC_URL = 9;
   private static final byte ENCODED_TYPE_MMS_RETRIEVAL_MODE = 10;
   private static final byte ENCODED_TYPE_MMS_MESSAGE_URL_PREFIX = 11;
   private static final byte ENCODED_TYPE_MMS_AUTHENTICATION_HEADER = 12;
   private static final byte ENCODED_TYPE_MMSC_VERSION = 13;
   private static final byte ENCODED_TYPE_MMSC_SUPPRESS_EXTRA_BYTE_COMPLIANCE_MODE = 14;
   public static String SERVICE_CID = "WAP";
   public static final int SESSION_RESUME_FALSE = 0;
   public static final int SESSION_RESUME_TRUE = 1;
   public static final int SECURE_ACCESS_FALSE = 0;
   public static final int SECURE_ACCESS_TRUE = 1;
   public static final int WTLS_MODE_WAP = 0;
   public static final int WTLS_MODE_OPENWAVE = 1;
   public static final int WAP_20_CONFORMANCE_FALSE = 0;
   public static final int WAP_20_CONFORMANCE_TRUE = 1;
   public static final int WTLS_CLIENT_ID_TYPE_INVALID = -1;
   public static final int WTLS_CLIENT_ID_TYPE_IPV4_DEVICE = 0;
   public static final int WTLS_CLIENT_ID_TYPE_IPV4_SPECIFIED = 1;
   public static final int WTLS_CLIENT_ID_TYPE_GSM_MSISDN_DEVICE = 2;
   public static final int WTLS_CLIENT_ID_TYPE_GSM_MSISDN_SPECIFIED = 3;
   public static final int WTLS_CLIENT_ID_TYPE_CDMA_MDN_DEVICE = 4;
   public static final int WTLS_CLIENT_ID_TYPE_CDMA_IMSI_15_DEVICE = 5;
   public static final int WTLS_CLIENT_ID_TYPE_CDMA_IMSI_SPECIFIED = 6;
   public static final int WTLS_CLIENT_ID_TYPE_CDMA_IMSI_10_DEVICE = 7;
   public static final int MMS_RETRIEVAL_MODE_UNSPECIFIED = -1;
   public static final int MMS_RETRIEVAL_MODE_ALWAYS = 0;
   public static final int MMS_RETRIEVAL_MODE_NEVER = 1;
   public static final int MMS_RETRIEVAL_MODE_HOMEONLY = 2;
   public static final int MMSC_VERSION_1_0 = 16;
   public static final int MMSC_VERSION_1_1 = 17;
   public static final int MMSC_VERSION_1_2 = 18;
   public static final int MMSC_VERSION_1_3 = 19;

   public static final WAPServiceRecord getRecord(ServiceRecord rec) {
      if (rec == null) {
         return null;
      }

      byte[] data = rec.getApplicationData();
      WAPServiceRecord newRecord = new WAPServiceRecord();
      newRecord._cid = rec.getCid();
      newRecord._uid = rec.getUid();
      if (data == null) {
         return newRecord;
      }

      DataBuffer tmpDataBuffer = new DataBuffer(data, 0, data.length, true);
      newRecord._containsWtlsValues = false;

      try {
         byte version = tmpDataBuffer.readByte();

         while (!tmpDataBuffer.eof()) {
            switch (getFieldType(tmpDataBuffer)) {
               case 0:
                  skipField(tmpDataBuffer);
                  break;
               case 1:
               default:
                  newRecord._secureAccess = getInt(tmpDataBuffer);
                  break;
               case 2:
                  newRecord._authUsername = getString(tmpDataBuffer);
                  break;
               case 3:
                  newRecord._authPassword = getString(tmpDataBuffer);
                  break;
               case 4:
                  newRecord._sessionResume = getInt(tmpDataBuffer);
                  break;
               case 5:
                  newRecord._containsWtlsValues = true;
                  newRecord._wtlsMode = getInt(tmpDataBuffer);
                  break;
               case 6:
                  newRecord._containsWtlsValues = true;
                  newRecord._wtlsClientIdType = getInt(tmpDataBuffer);
                  break;
               case 7:
                  newRecord._containsWtlsValues = true;
                  newRecord._wtlsClientIdValue = getString(tmpDataBuffer);
                  break;
               case 8:
                  newRecord._wap20Conformance = getInt(tmpDataBuffer);
                  break;
               case 9:
                  newRecord._mmscUrl = getString(tmpDataBuffer);
                  break;
               case 10:
                  newRecord._mmsRetrievalMode = getInt(tmpDataBuffer);
                  break;
               case 11:
                  newRecord._mmsMessageUrlPrefix = getString(tmpDataBuffer);
                  break;
               case 12:
                  newRecord._mmsAuthenticationHeader = getString(tmpDataBuffer);
                  break;
               case 13:
                  newRecord._mmscVersion = getInt(tmpDataBuffer);
                  break;
               case 14:
                  getInt(tmpDataBuffer);
            }
         }

         return newRecord;
      } finally {
         ;
      }
   }

   public static final WAPServiceRecord getRecord(String cid, String uid) {
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

   public final boolean saveData() {
      byte[] data = getEncodedData(
         this._secureAccess,
         this._authUsername,
         this._authPassword,
         this._sessionResume,
         this._wtlsMode,
         this._wtlsClientIdType,
         this._wtlsClientIdValue,
         this._wap20Conformance,
         this._mmscUrl,
         this._mmsRetrievalMode,
         this._mmsMessageUrlPrefix,
         this._mmsAuthenticationHeader,
         this._mmscVersion
      );
      ServiceBook sb = ServiceBook.getSB();
      ServiceRecord rec = sb.getRecordByUidAndCid(this._uid, this._cid);
      if (rec == null) {
         return false;
      }

      rec.setApplicationData(data);
      sb.commit();
      return true;
   }

   public static final byte[] getEncodedData(
      int secureAccess,
      String authUsername,
      String authPassword,
      int sessionResume,
      int wtlsMode,
      int wtlsClientIdType,
      String wtlsClientIdValue,
      int wap20Conformance
   ) {
      return getEncodedData(
         secureAccess, authUsername, authPassword, sessionResume, wtlsMode, wtlsClientIdType, wtlsClientIdValue, wap20Conformance, null, -1, null, null, 16
      );
   }

   public static final byte[] getEncodedData(
      int secureAccess,
      String authUsername,
      String authPassword,
      int sessionResume,
      int wtlsMode,
      int wtlsClientIdType,
      String wtlsClientIdValue,
      int wap20Conformance,
      String mmscUrl,
      int mmsRetrievalMode,
      String mmsMessageUrlPrefix,
      String mmsAuthenticationHeader,
      int mmscVersion
   ) throws IOException {
      DataBuffer tmpDataBuffer = new DataBuffer();
      tmpDataBuffer.writeByte(1);
      addInt(tmpDataBuffer, 1, secureAccess, 1);
      if (authUsername != null && authPassword != null) {
         addField(tmpDataBuffer, 2, authUsername);
         addField(tmpDataBuffer, 3, authPassword);
      }

      addInt(tmpDataBuffer, 5, wtlsMode, 1);
      if (wtlsClientIdType != -1) {
         addInt(tmpDataBuffer, 6, wtlsClientIdType, 1);
         if (wtlsClientIdValue != null) {
            addField(tmpDataBuffer, 7, wtlsClientIdValue);
         }
      }

      addInt(tmpDataBuffer, 4, sessionResume, 1);
      addInt(tmpDataBuffer, 8, wap20Conformance, 1);
      if (mmscUrl != null) {
         addField(tmpDataBuffer, 9, mmscUrl);
      }

      if (mmsMessageUrlPrefix != null) {
         addField(tmpDataBuffer, 11, mmsMessageUrlPrefix);
      }

      if (mmsAuthenticationHeader != null) {
         addField(tmpDataBuffer, 12, mmsAuthenticationHeader);
      }

      if (mmsRetrievalMode != -1) {
         addInt(tmpDataBuffer, 10, mmsRetrievalMode, 1);
      }

      if (mmscVersion != 16) {
         addInt(tmpDataBuffer, 13, mmscVersion, 1);
      }

      byte[] encodedData = tmpDataBuffer.toArray();
      if (encodedData.length > 1023) {
         throw new IOException("Encoded Browser ServiceBook data (" + encodedData.length + " bytes) exceeds 1023 bytes");
      } else {
         return encodedData;
      }
   }

   public final int getSecureAccess() {
      return this._secureAccess;
   }

   public final int getSessionResume() {
      return this._sessionResume;
   }

   public final String getAuthUsername() {
      return this._authUsername;
   }

   public final String getAuthPassword() {
      return this._authPassword;
   }

   public final int getWtlsModeValue() {
      return this._wtlsMode;
   }

   public final int getWtlsClientIdType() {
      return this._wtlsClientIdType;
   }

   public final boolean getContainsWtlsClientIdValue() {
      return this._containsWtlsValues;
   }

   public final String getWtlsClientIdValue() {
      return this._wtlsClientIdValue;
   }

   public final int getWAP20Conformance() {
      return this._wap20Conformance;
   }

   public final String getMMSCUrl() {
      return this._mmscUrl;
   }

   public final int getMMSCVersion() {
      return this._mmscVersion;
   }

   public final String getMMSAuthenticationHeader() {
      return this._mmsAuthenticationHeader;
   }

   public final String getMMSMessageUrlPrefix() {
      return this._mmsMessageUrlPrefix;
   }

   public final int getMMSRetrievalMode() {
      return this._mmsRetrievalMode;
   }

   public final void setWAP20Conformance(int value) {
      this._wap20Conformance = value;
   }

   public final void setSecureAccess(int value) {
      this._secureAccess = value;
   }

   public final void setSessionResume(int value) {
      this._sessionResume = value;
   }

   public final void setAuthUsername(String value) {
      this._authUsername = value;
   }

   public final void setAuthPassword(String value) {
      this._authPassword = value;
   }

   public final void setWtlsModeValue(int value) {
      this._wtlsMode = value;
   }

   public final void setWtlsClientIdType(int value) {
      this._wtlsClientIdType = value;
   }

   public final void setContainsWtlsClientIdValue(boolean value) {
      this._containsWtlsValues = value;
   }

   public final void setWtlsClientIdValue(String value) {
      this._wtlsClientIdValue = value;
   }

   public final void setMMSCUrl(String url) {
      this._mmscUrl = url;
   }

   public final void setMMSCVersion(int value) {
      this._mmscVersion = value;
   }

   public final void setMMSAuthenticationHeader(String header) {
      this._mmsAuthenticationHeader = header;
   }

   public final void setMMSMessageUrlPrefix(String prefix) {
      this._mmsMessageUrlPrefix = prefix;
   }

   public final void setMMSRetrievalMode(int value) {
      this._mmsRetrievalMode = value;
   }

   private static final void addField(DataBuffer buffer, int field, String value) {
      ConverterUtilities.writeStringIntellisync(buffer, field, value);
   }

   private static final void addInt(DataBuffer buffer, int fieldID, int data, int len) {
      ConverterUtilities.convertInt(buffer, fieldID, data, len);
   }

   private static final int getInt(DataBuffer buffer) {
      return ConverterUtilities.readInt(buffer);
   }

   private static final String getString(DataBuffer buffer) {
      String value = ConverterUtilities.readString(buffer);
      if (value != null && value.length() == 0) {
         value = null;
      }

      return value;
   }

   private static final int getFieldType(DataBuffer buffer) {
      try {
         return ConverterUtilities.getType(buffer);
      } finally {
         ;
      }
   }

   private static final void skipField(DataBuffer buffer) {
      int len = buffer.readUnsignedShort();
      buffer.readByte();
      if (buffer.available() >= len) {
         buffer.skipBytes(len);
      }
   }
}
