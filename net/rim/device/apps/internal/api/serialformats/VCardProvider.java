package net.rim.device.apps.internal.api.serialformats;

import java.util.Date;

public interface VCardProvider extends ParserTypes {
   int VERSION_2_1 = 1;
   int VERSION_3_0 = 2;
   int ATTRIBUTE_VERSION = 1;
   int ATTRIBUTE_FN = 2;
   int ATTRIBUTE_N = 4;
   int ATTRIBUTE_PHOTO = 8;
   int ATTRIBUTE_BDAY = 16;
   int ATTRIBUTE_ADR = 32;
   int ATTRIBUTE_LABEL = 64;
   int ATTRIBUTE_TEL = 128;
   int ATTRIBUTE_EMAIL = 256;
   int ATTRIBUTE_MAILER = 512;
   int ATTRIBUTE_TZ = 1024;
   int ATTRIBUTE_GEO = 2048;
   int ATTRIBUTE_TITLE = 4096;
   int ATTRIBUTE_ROLE = 8192;
   int ATTRIBUTE_LOGO = 16384;
   int ATTRIBUTE_AGENT = 32768;
   int ATTRIBUTE_ORG = 65536;
   int ATTRIBUTE_NOTE = 131072;
   int ATTRIBUTE_REV = 262144;
   int ATTRIBUTE_SOUND = 524288;
   int ATTRIBUTE_URL = 1048576;
   int ATTRIBUTE_UID = 2097152;
   int ATTRIBUTE_KEY = 4194304;
   int ATTRIBUTE_NICKNAME = 8388608;
   int ATTRIBUTE_CATEGORIES = 16777216;
   int ATTRIBUTE_PRODID = 33554432;
   int ATTRIBUTE_CLASS = 67108864;
   int ATTRIBUTE_SORT_STRING = 134217728;
   int ATTRIBUTE_TIME_STAMP = 268435456;
   int FIRST = 1;
   int NEXT = 2;
   int ADDR_DOM = 1;
   int ADDR_INTL = 2;
   int ADDR_POSTAL = 4;
   int ADDR_PARCEL = 8;
   int ADDR_HOME = 16;
   int ADDR_WORK = 32;
   int ADDR_PREF = 64;
   int TEL_HOME = 1;
   int TEL_WORK = 2;
   int TEL_PREF = 4;
   int TEL_VOICE = 8;
   int TEL_FAX = 16;
   int TEL_MSG = 32;
   int TEL_CELL = 64;
   int TEL_PAGER = 128;
   int TEL_BBS = 256;
   int TEL_MODEM = 512;
   int TEL_CAR = 1024;
   int TEL_ISDN = 2048;
   int TEL_VIDEO = 4096;
   int TEL_PCS = 8192;
   int PREF = 1;
   int AOL = 2;
   int APPLELINK = 4;
   int ATTMAIL = 8;
   int CIS = 16;
   int EWORLD = 32;
   int INTERNET = 64;
   int IBMMAIL = 128;
   int MCIMAIL = 256;
   int POWERSHARE = 512;
   int PRODIGY = 1024;
   int TLX = 2048;
   int X400 = 4096;

   boolean isValidVCardObject();

   void setSource(String var1);

   String getSource();

   void setName(String var1);

   String getName();

   void setProfile(String var1);

   String getProfile();

   String getFormattedName();

   void setFormattedName(String var1);

   void setFamilyName(String var1);

   String getFamilyName();

   void setGivenName(String var1);

   String getGivenName();

   void setAdditionalName(String var1);

   String getAdditionalName();

   void setNamePrefix(String var1);

   String getNamePrefix();

   void setNameSuffix(String var1);

   String getNameSuffix();

   void setNickname(String var1);

   String getNickname();

   void setPhotoValue(int var1);

   void setPhotoType(int var1);

   void setPhotoData(byte[] var1);

   int getPhotoValue();

   int getPhotoType();

   byte[] getPhotoData();

   void setBirthdate(Date var1);

   Date getBirthdate();

   void addAddress(int var1, String var2, String var3, String var4, String var5, String var6, String var7, String var8);

   boolean hasAddress();

   boolean hasNextAddress();

   int getAddressType(int var1);

   String getPostOfficeAddress(int var1);

   String getExtendedAddress(int var1);

   String getStreet(int var1);

   String getLocality(int var1);

   String getRegion(int var1);

   String getPostalCode(int var1);

   String getCountry(int var1);

   int getLabelType();

   void setLabelType(int var1);

   String getLabel();

   void setLabel(String var1);

   void addTelephone(int var1, String var2);

   boolean hasTelephone();

   boolean hasNextTelephone();

   int getTelephoneType(int var1);

   String getTelephoneNumber(int var1);

   void addEmail(int var1, String var2);

   boolean hasEmail();

   boolean hasNextEmail();

   int getEmailType(int var1);

   String getEmailAddress(int var1);

   void setMailer(String var1);

   String getMailer();

   void setTimeZone(String var1);

   String getTimeZone();

   void setGeo(String var1);

   String getGeo();

   void setTitle(String var1);

   String getTitle();

   void setRole(String var1);

   String getRole();

   void setLogoValue(int var1);

   void setLogoType(int var1);

   void setLogoData(byte[] var1);

   int getLogoValue();

   int getLogoType();

   byte[] getLogoData();

   void setAgentInVcard(Object var1);

   void setAgentInText(int var1, String var2);

   Object getAgentInVcard();

   String getAgentInText();

   int getAgentTextValue();

   void setOrganizationName(String var1);

   String getOrganizationName();

   void setOrganizationUnit1(String var1);

   String getOrganizationUnit1();

   void setOrganizationUnit2(String var1);

   String getOrganizationUnit2();

   void setCategories(String var1);

   String getCategories();

   void setNote(String var1);

   String getNote();

   void setProdid(String var1);

   String getProdid();

   void setRev(Date var1);

   Date getRev();

   void setSortString(String var1);

   String getSortString();

   void setSoundValue(int var1);

   void setSoundType(int var1);

   void setSoundData(byte[] var1);

   int getSoundValue();

   int getSoundType();

   byte[] getSoundData();

   void setUrl(String var1);

   String getUrl();

   void setUid(String var1);

   String getUid();

   void setVersion(int var1);

   int getVersion();

   void setClassId(int var1);

   int getClassId();

   void setKeyValue(int var1);

   void setKeyType(int var1);

   void setKeyData(byte[] var1);

   int getKeyValue();

   int getKeyType();

   byte[] getKeyData();

   void addExtension(String var1, String var2);

   boolean hasExtension();

   boolean hasNextExtension();

   String getExtensionName(int var1);

   String getExtensionData(int var1);

   VCardProvider createVCardProvider();

   void addNestedVCard(Object var1);

   boolean hasNestedVCard();

   boolean hasNextNestedVCard();

   VCardProvider getNestedVCard(int var1);
}
