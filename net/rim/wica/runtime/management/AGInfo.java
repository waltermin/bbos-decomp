package net.rim.wica.runtime.management;

import java.util.Enumeration;
import java.util.Hashtable;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.Persistable;
import net.rim.device.apps.api.framework.model.SyncBuffer;
import net.rim.device.cldc.io.utility.URL;
import net.rim.wica.compatibility.VersionContext;
import net.rim.wica.runtime.management.versioning.REVersionUtils;
import net.rim.wica.runtime.util.Util;

public final class AGInfo implements Persistable {
   private int _serviceRecordID;
   private String _UID;
   private String _agRegURL;
   private String _agCompactMsgURL;
   private String _IPPP_UID;
   private String _AGFriendlyName;
   private String _BESGroups;
   private String _SPList;
   private boolean _enterprise = true;
   private int _generationCount;
   private int _securityVersion;
   private int _transportVersion;
   private Hashtable _versions;
   private long _agID;
   private static String AG_COMPACT_MSG_URL = "/CompactListener";
   private static final byte ENCODED_TYPE_AG_REG_URL = 1;
   private static final byte ENCODED_TYPE_AG_MSG_URL = 2;
   private static final byte ENCODED_TYPE_TRANSPORT_SERV_UID = 3;
   private static final byte ENCODED_TYPE_AG_FRIENDLY_NAME = 4;
   private static final byte ENCODED_TYPE_BES_GROUPS = 5;
   private static final byte ENCODED_TYPE_GENERATION_COUNT = 6;
   private static final byte ENCODED_TYPE_SP_LIST = 7;

   public AGInfo() {
   }

   public AGInfo(AGInfo agInfo) {
      this._serviceRecordID = agInfo._serviceRecordID;
      this._UID = agInfo._UID;
      this._agRegURL = agInfo._agRegURL;
      this._agCompactMsgURL = agInfo._agCompactMsgURL;
      this._IPPP_UID = agInfo._IPPP_UID;
      this._AGFriendlyName = agInfo._AGFriendlyName;
      this._BESGroups = agInfo._BESGroups;
      this._SPList = agInfo._SPList;
      this._enterprise = agInfo._enterprise;
      this._generationCount = agInfo._generationCount;
      this._agID = agInfo._agID;
      this._securityVersion = agInfo._securityVersion;
      this._transportVersion = agInfo._transportVersion;
      this._versions = agInfo._versions;
   }

   public final void updateServiceRecordFields(AGInfo agInfo) {
      this._serviceRecordID = agInfo._serviceRecordID;
      this._UID = agInfo._UID;
      this._agRegURL = agInfo._agRegURL;
      this._agCompactMsgURL = agInfo._agCompactMsgURL;
      this._IPPP_UID = agInfo._IPPP_UID;
      this._SPList = agInfo._SPList;
      this._enterprise = agInfo._enterprise;
      this._AGFriendlyName = agInfo._AGFriendlyName;
      this._BESGroups = agInfo._BESGroups;
      this._generationCount = agInfo._generationCount;
   }

   public AGInfo(ServiceRecord sr) {
      if (sr != null && sr.getCid() != null) {
         this._enterprise = sr.getCid().equalsIgnoreCase("MDS");
         if (this._enterprise || sr.getCid().equalsIgnoreCase("PMDS")) {
            this._serviceRecordID = sr.getId();
            this._UID = sr.getUid();
            byte[] data = sr.getApplicationData();
            if (data != null && data.length > 0) {
               DataBuffer _tmpDataBuffer = new DataBuffer(data, 0, data.length, true);
               SyncBuffer _tmpSyncBuffer = new SyncBuffer(_tmpDataBuffer, 0, 0);

               while (!_tmpSyncBuffer.isEmpty()) {
                  try {
                     int fieldType = _tmpSyncBuffer.getFieldType(true);
                     switch (fieldType) {
                        case 0:
                           _tmpSyncBuffer.skipField();
                           break;
                        case 1:
                        default:
                           this._agRegURL = _tmpSyncBuffer.getString().trim();
                           break;
                        case 2:
                           this._agCompactMsgURL = createAGCompactMsgURL(_tmpSyncBuffer.getString().trim());
                           break;
                        case 3:
                           this._IPPP_UID = _tmpSyncBuffer.getString();
                           break;
                        case 4:
                           this._AGFriendlyName = _tmpSyncBuffer.getString();
                           break;
                        case 5:
                           this._BESGroups = _tmpSyncBuffer.getString();
                           break;
                        case 6:
                           this._generationCount = _tmpSyncBuffer.getInt();
                           break;
                        case 7:
                           this._SPList = _tmpSyncBuffer.getString();
                     }
                  } finally {
                     continue;
                  }
               }
            }
         }
      }
   }

   public final boolean isValid() {
      try {
         new URL(this._agRegURL);
         new URL(this._agCompactMsgURL);
         if (Util.isNonEmptyString(this._agRegURL) && Util.isNonEmptyString(this._agCompactMsgURL) && Util.isNonEmptyString(this._IPPP_UID)) {
            return true;
         }
      } finally {
         return false;
      }

      return false;
   }

   public final String getAgRegURL() {
      return this._agRegURL;
   }

   public final String getAgCompactMsgURL() {
      return this._agCompactMsgURL;
   }

   public final int getServiceRecordID() {
      return this._serviceRecordID;
   }

   public final String getMDSUID() {
      return this._UID;
   }

   public final long getAgID() {
      return this._agID;
   }

   public final int getSecurityVersion() {
      return this._securityVersion;
   }

   public final void setSecurityVersion(int version) {
      this._securityVersion = version;
   }

   public final int getTransportVersion() {
      return this._transportVersion;
   }

   public final Object getVersion(String key) {
      return this._versions.get(key);
   }

   public final void setVersions(byte[] versions) {
      this._versions = new Hashtable();
      if (versions == null) {
         this._transportVersion = 1;
         this._versions.put("Discovery", "1.1.0");
         this._versions.put("Provisioning", "1.1.0");
         this._versions.put("ControlCenter", "1.1.0");
         this._versions.put("System", Integer.toString(1));
         this._versions.put("Transport", Integer.toString(1));
      } else {
         VersionContext context = REVersionUtils.getVersionContext(versions);
         Enumeration keys = context.keys();

         while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            this._versions.put(key, context.get(key));
         }

         String transport = (String)this._versions.get("Transport");

         try {
            this._transportVersion = Integer.parseInt(transport);
         } finally {
            return;
         }
      }
   }

   public final void setAgID(long _agid) {
      this._agID = _agid;
   }

   public static final String createAGCompactMsgURL(String baseURL) {
      return Util.isValidURL(baseURL) ? baseURL + AG_COMPACT_MSG_URL : "";
   }

   public final void setAGCompactMsgURL(String compactMsgURL) {
      this._agCompactMsgURL = compactMsgURL;
   }

   public final void setAGRegURL(String regURL) {
      this._agRegURL = regURL;
   }

   public final void setServiceRecordID(int recordID) {
      this._serviceRecordID = recordID;
   }

   public final void setMDSUID(String _uid) {
      this._UID = _uid;
   }

   public final boolean AGUrlsEqual(String regURL, String msgURL) {
      return this._agRegURL == null || this._agCompactMsgURL == null ? false : this._agRegURL.equals(regURL) && this._agCompactMsgURL.equals(msgURL);
   }

   public final String getIPPP_UID() {
      return this._IPPP_UID;
   }

   public final void setIPPP_UID(String ippp_uid) {
      this._IPPP_UID = ippp_uid;
   }

   public final String getAGFriendlyName() {
      return this._AGFriendlyName;
   }

   public final void setAGFriendlyName(String friendlyName) {
      this._AGFriendlyName = friendlyName;
   }

   public final String getBESGroups() {
      return this._BESGroups;
   }

   public final void setBESGroups(String groups) {
      this._BESGroups = groups;
   }

   public final int getGenerationCount() {
      return this._generationCount;
   }

   public final void setGenerationCount(int count) {
      this._generationCount = count;
   }

   public final String getSPList() {
      return this._SPList;
   }

   public final void setSPList(String SPList) {
      this._SPList = SPList;
   }

   public final boolean isCorporate() {
      return this._enterprise;
   }

   public final void setCorporate(boolean corporate) {
      this._enterprise = corporate;
   }
}
