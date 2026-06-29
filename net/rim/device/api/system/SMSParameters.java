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
   public static final int NUM_UNKNOWN;
   public static final int NUM_INTERNATIONAL;
   public static final int NUM_NATIONAL;
   public static final int NUM_NETWORK_SPEC;
   public static final int NUM_SUBSCRIBER;
   public static final int NUM_ALPHANUMERIC;
   public static final int NUM_ABBREVIATED;
   public static final int NUM_RESERVED;
   public static final int NUM_EMAIL;
   public static final int PLAN_UNKNOWN;
   public static final int PLAN_ISDN;
   public static final int PLAN_DATA;
   public static final int PLAN_TELEX;
   public static final int PLAN_NATIONAL;
   public static final int PLAN_PRIVATE;
   public static final int PLAN_ERMES;
   public static final int PLAN_RESERVED;
   public static final int PROTOCOL_MEANING_SM_AL_PROTOCOL;
   public static final int PROTOCOL_MEANING_TELEMATIC_INTERWORK;
   public static final int PROTOCOL_MEANING_SM_FUNCTIONS;
   public static final int PROTOCOL_MEANING_RESERVED;
   public static final int PROTOCOL_MEANING_SC_SPECIFIC;
   public static final int PROTOCOL_MEANING_RAW_DATA;
   public static final int PROTOCOL_ID_IMPLICIT;
   public static final int PROTOCOL_ID_TELEX;
   public static final int PROTOCOL_ID_GRP3_FAX;
   public static final int PROTOCOL_ID_GRP4_FAX;
   public static final int PROTOCOL_ID_VOICE;
   public static final int PROTOCOL_ID_ERMES;
   public static final int PROTOCOL_ID_NAT_PAGING;
   public static final int PROTOCOL_ID_VIDEOTEX;
   public static final int PROTOCOL_ID_UNSPEC_TELETEX;
   public static final int PROTOCOL_ID_PSPDN_TELETEX;
   public static final int PROTOCOL_ID_CSPDN_TELETEX;
   public static final int PROTOCOL_ID_PSTN_TELETEX;
   public static final int PROTOCOL_ID_ISDN_TELETEX;
   public static final int PROTOCOL_ID_UCI;
   public static final int PROTOCOL_ID_MSG_HANDLING;
   public static final int PROTOCOL_ID_X400;
   public static final int PROTOCOL_ID_INTERNET_EMAIL;
   public static final int PROTOCOL_ID_SC_SPECIFIC_1;
   public static final int PROTOCOL_ID_SC_SPECIFIC_2;
   public static final int PROTOCOL_ID_SC_SPECIFIC_3;
   public static final int PROTOCOL_ID_SC_SPECIFIC_4;
   public static final int PROTOCOL_ID_SC_SPECIFIC_5;
   public static final int PROTOCOL_ID_SC_SPECIFIC_6;
   public static final int PROTOCOL_ID_SC_SPECIFIC_7;
   public static final int PROTOCOL_ID_GSM_MS;
   public static final int PROTOCOL_ID_SM_TYPE0;
   public static final int PROTOCOL_ID_REPLACE_SM_TYPE1;
   public static final int PROTOCOL_ID_REPLACE_SM_TYPE2;
   public static final int PROTOCOL_ID_REPLACE_SM_TYPE3;
   public static final int PROTOCOL_ID_REPLACE_SM_TYPE4;
   public static final int PROTOCOL_ID_REPLACE_SM_TYPE5;
   public static final int PROTOCOL_ID_REPLACE_SM_TYPE6;
   public static final int PROTOCOL_ID_REPLACE_SM_TYPE7;
   public static final int PROTOCOL_ID_RETURN_CALL_MSG;
   public static final int PROTOCOL_ID_DEPERSONALIZATION_SM;
   public static final int PROTOCOL_ID_SIM_DATA_DOWNLOAD;
   public static final int PERIOD_IMMEDIATE;
   public static final int PERIOD_INDEFINITE;
   public static final int PERIOD_TILL_ACTIVE;
   public static final int PERIOD_TILL_AREA;
   public static final int MESSAGE_CODING_DEFAULT;
   public static final int MESSAGE_CODING_8_BIT;
   public static final int MESSAGE_CODING_UCS2;
   public static final int MESSAGE_CODING_KOREAN_KSX1001;
   public static final int MESSAGE_CODING_ASCII;
   public static final int MESSAGE_CODING_ISO8859_1;
   public static final int MESSAGE_CLASS_0;
   public static final int MESSAGE_CLASS_1;
   public static final int MESSAGE_CLASS_2;
   public static final int MESSAGE_CLASS_3;
   public static final int MESSAGE_CLASS_NOT_GIVEN;
   public static final int PRIORITY_NORMAL;
   public static final int PRIORITY_INTERACTIVE;
   public static final int PRIORITY_URGENT;
   public static final int PRIORITY_EMERGENCY;
   public static final int PRIORITY_NONE;
   public static final int PRIVACY_NORMAL;
   public static final int PRIVACY_RESTRICTED;
   public static final int PRIVACY_CONFIDENTIAL;
   public static final int PRIVACY_SECRET;
   public static final int PRIVACY_NONE;
   public static final int LANGUAGE_UNSPECIFIED;
   public static final int LANGUAGE_ENGLISH;
   public static final int LANGUAGE_FRENCH;
   public static final int LANGUAGE_SPANISH;
   public static final int LANGUAGE_JAPANESE;
   public static final int LANGUAGE_KOREAN;
   public static final int LANGUAGE_CHINESE;
   public static final int LANGUAGE_HEBREW;
   public static final int LANGUAGE_NONE;

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
