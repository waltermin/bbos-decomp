package net.rim.device.api.system;

public class SMSParameters {
   private String _peerAddress;
   private int _peerType;
   private int _peerPlan;
   private String _scAddress;
   private int _scType;
   private int _scPlan;
   private int _protocolMeaning;
   private int _protocolId;
   private int _messageCoding;
   private int _messageClass;
   private int _validityPeriod;
   private int _privacy;
   private int _priority;
   private int _language;
   private String _callbackAddress;
   private int _callbackType;
   private int _callbackPlan;
   private int _deliveryPeriod;
   public static final int NUM_UNKNOWN = 0;
   public static final int NUM_INTERNATIONAL = 1;
   public static final int NUM_NATIONAL = 2;
   public static final int NUM_NETWORK_SPEC = 3;
   public static final int NUM_SUBSCRIBER = 4;
   public static final int NUM_ALPHANUMERIC = 5;
   public static final int NUM_ABBREVIATED = 6;
   public static final int NUM_RESERVED = 7;
   public static final int NUM_EMAIL = 100;
   public static final int PLAN_UNKNOWN = 0;
   public static final int PLAN_ISDN = 1;
   public static final int PLAN_DATA = 3;
   public static final int PLAN_TELEX = 4;
   public static final int PLAN_NATIONAL = 8;
   public static final int PLAN_PRIVATE = 9;
   public static final int PLAN_ERMES = 10;
   public static final int PLAN_RESERVED = 15;
   public static final int PROTOCOL_MEANING_SM_AL_PROTOCOL = 0;
   public static final int PROTOCOL_MEANING_TELEMATIC_INTERWORK = 32;
   public static final int PROTOCOL_MEANING_SM_FUNCTIONS = 64;
   public static final int PROTOCOL_MEANING_RESERVED = 128;
   public static final int PROTOCOL_MEANING_SC_SPECIFIC = 192;
   public static final int PROTOCOL_MEANING_RAW_DATA = 193;
   public static final int PROTOCOL_ID_IMPLICIT = 0;
   public static final int PROTOCOL_ID_TELEX = 1;
   public static final int PROTOCOL_ID_GRP3_FAX = 2;
   public static final int PROTOCOL_ID_GRP4_FAX = 3;
   public static final int PROTOCOL_ID_VOICE = 4;
   public static final int PROTOCOL_ID_ERMES = 5;
   public static final int PROTOCOL_ID_NAT_PAGING = 6;
   public static final int PROTOCOL_ID_VIDEOTEX = 7;
   public static final int PROTOCOL_ID_UNSPEC_TELETEX = 8;
   public static final int PROTOCOL_ID_PSPDN_TELETEX = 9;
   public static final int PROTOCOL_ID_CSPDN_TELETEX = 10;
   public static final int PROTOCOL_ID_PSTN_TELETEX = 11;
   public static final int PROTOCOL_ID_ISDN_TELETEX = 12;
   public static final int PROTOCOL_ID_UCI = 13;
   public static final int PROTOCOL_ID_MSG_HANDLING = 16;
   public static final int PROTOCOL_ID_X400 = 17;
   public static final int PROTOCOL_ID_INTERNET_EMAIL = 18;
   public static final int PROTOCOL_ID_SC_SPECIFIC_1 = 24;
   public static final int PROTOCOL_ID_SC_SPECIFIC_2 = 25;
   public static final int PROTOCOL_ID_SC_SPECIFIC_3 = 26;
   public static final int PROTOCOL_ID_SC_SPECIFIC_4 = 27;
   public static final int PROTOCOL_ID_SC_SPECIFIC_5 = 28;
   public static final int PROTOCOL_ID_SC_SPECIFIC_6 = 29;
   public static final int PROTOCOL_ID_SC_SPECIFIC_7 = 30;
   public static final int PROTOCOL_ID_GSM_MS = 31;
   public static final int PROTOCOL_ID_SM_TYPE0 = 0;
   public static final int PROTOCOL_ID_REPLACE_SM_TYPE1 = 1;
   public static final int PROTOCOL_ID_REPLACE_SM_TYPE2 = 2;
   public static final int PROTOCOL_ID_REPLACE_SM_TYPE3 = 3;
   public static final int PROTOCOL_ID_REPLACE_SM_TYPE4 = 4;
   public static final int PROTOCOL_ID_REPLACE_SM_TYPE5 = 5;
   public static final int PROTOCOL_ID_REPLACE_SM_TYPE6 = 6;
   public static final int PROTOCOL_ID_REPLACE_SM_TYPE7 = 7;
   public static final int PROTOCOL_ID_RETURN_CALL_MSG = 31;
   public static final int PROTOCOL_ID_DEPERSONALIZATION_SM = 62;
   public static final int PROTOCOL_ID_SIM_DATA_DOWNLOAD = 63;
   public static final int PERIOD_IMMEDIATE = 0;
   public static final int PERIOD_INDEFINITE = -1;
   public static final int PERIOD_TILL_ACTIVE = -3;
   public static final int PERIOD_TILL_AREA = -4;
   public static final int MESSAGE_CODING_DEFAULT = 0;
   public static final int MESSAGE_CODING_8_BIT = 1;
   public static final int MESSAGE_CODING_UCS2 = 2;
   public static final int MESSAGE_CODING_KOREAN_KSX1001 = 6;
   public static final int MESSAGE_CODING_ASCII = 4;
   public static final int MESSAGE_CODING_ISO8859_1 = 5;
   public static final int MESSAGE_CLASS_0 = 0;
   public static final int MESSAGE_CLASS_1 = 1;
   public static final int MESSAGE_CLASS_2 = 2;
   public static final int MESSAGE_CLASS_3 = 3;
   public static final int MESSAGE_CLASS_NOT_GIVEN = 4;
   public static final int PRIORITY_NORMAL = 0;
   public static final int PRIORITY_INTERACTIVE = 1;
   public static final int PRIORITY_URGENT = 2;
   public static final int PRIORITY_EMERGENCY = 3;
   public static final int PRIORITY_NONE = 4;
   public static final int PRIVACY_NORMAL = 0;
   public static final int PRIVACY_RESTRICTED = 1;
   public static final int PRIVACY_CONFIDENTIAL = 2;
   public static final int PRIVACY_SECRET = 3;
   public static final int PRIVACY_NONE = 4;
   public static final int LANGUAGE_UNSPECIFIED = 0;
   public static final int LANGUAGE_ENGLISH = 1;
   public static final int LANGUAGE_FRENCH = 2;
   public static final int LANGUAGE_SPANISH = 3;
   public static final int LANGUAGE_JAPANESE = 4;
   public static final int LANGUAGE_KOREAN = 5;
   public static final int LANGUAGE_CHINESE = 6;
   public static final int LANGUAGE_HEBREW = 7;
   public static final int LANGUAGE_NONE = 8;

   public SMSParameters() {
      this.reset();
   }

   public void reset() {
      this._peerAddress = null;
      this._peerType = 0;
      this._peerPlan = 1;
      this._scAddress = null;
      this._scType = 0;
      this._scPlan = 1;
      this._protocolMeaning = 0;
      this._protocolId = 0;
      this._messageCoding = 0;
      this._messageClass = 4;
      this._validityPeriod = -1;
      this._privacy = 4;
      this._priority = 4;
      this._language = 8;
      this._callbackAddress = null;
      this._callbackType = 0;
      this._callbackPlan = 1;
      this._deliveryPeriod = -1;
   }

   public String getPeerAddress() {
      return this._peerAddress;
   }

   public int getPeerType() {
      return this._peerType;
   }

   public int getPeerPlan() {
      return this._peerPlan;
   }

   public void setPeerAddress(String peerAddress) {
      this._peerAddress = peerAddress;
      this._peerType = 0;
      this._peerPlan = 1;
   }

   public void setPeerAddress(String peerAddress, int peerType, int peerPlan) {
      this._peerAddress = peerAddress;
      this._peerType = peerType;
      this._peerPlan = peerPlan;
   }

   public String getSCAddress() {
      return this._scAddress;
   }

   public int getSCType() {
      return this._scType;
   }

   public int getSCPlan() {
      return this._scPlan;
   }

   public void setSCAddress(String scAddress) {
      this._scAddress = scAddress;
      this._scType = 0;
      this._scPlan = 1;
   }

   public void setSCAddress(String scAddress, int scType, int scPlan) {
      this._scAddress = scAddress;
      this._scType = scType;
      this._scPlan = scPlan;
   }

   public int getProtocolMeaning() {
      return this._protocolMeaning;
   }

   public void setProtocolMeaning(int protocolMeaning) {
      this._protocolMeaning = protocolMeaning;
   }

   public int getProtocolId() {
      return this._protocolId;
   }

   public void setProtocolId(int protocolId) {
      this._protocolId = protocolId;
   }

   public int getMessageCoding() {
      return this._messageCoding;
   }

   public void setMessageCoding(int messageCoding) {
      this._messageCoding = messageCoding;
   }

   public int getMessageClass() {
      return this._messageClass;
   }

   public void setMessageClass(int messageClass) {
      this._messageClass = messageClass;
   }

   public int getValidityPeriod() {
      return this._validityPeriod;
   }

   public void setValidityPeriod(int validityPeriod) {
      this._validityPeriod = validityPeriod;
   }

   public int getPrivacy() {
      return this._privacy;
   }

   public void setPrivacy(int privacy) {
      this._privacy = privacy;
   }

   public int getPriority() {
      return this._priority;
   }

   public void setPriority(int priority) {
      this._priority = priority;
   }

   public int getLanguage() {
      return this._language;
   }

   public void setLanguage(int language) {
      this._language = language;
   }

   public String getCallbackAddress() {
      return this._callbackAddress;
   }

   public int getCallbackType() {
      return this._callbackType;
   }

   public int getCallbackPlan() {
      return this._callbackPlan;
   }

   public void setCallbackAddress(String callbackAddress) {
      this._callbackAddress = callbackAddress;
      this._callbackType = 0;
      this._callbackPlan = 1;
   }

   public void setCallbackAddress(String callbackAddress, int callbackType, int callbackPlan) {
      this._callbackAddress = callbackAddress;
      this._callbackType = callbackType;
      this._callbackPlan = callbackPlan;
   }

   public int getDeliveryPeriod() {
      return this._deliveryPeriod;
   }

   public void setDeliveryPeriod(int deliveryPeriod) {
      this._deliveryPeriod = deliveryPeriod;
   }
}
