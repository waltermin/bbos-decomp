package net.rim.device.apps.internal.api.serialformats;

import java.util.Date;

public interface VCardProvider extends ParserTypes {
   int VERSION_2_1;
   int VERSION_3_0;
   int ATTRIBUTE_VERSION;
   int ATTRIBUTE_FN;
   int ATTRIBUTE_N;
   int ATTRIBUTE_PHOTO;
   int ATTRIBUTE_BDAY;
   int ATTRIBUTE_ADR;
   int ATTRIBUTE_LABEL;
   int ATTRIBUTE_TEL;
   int ATTRIBUTE_EMAIL;
   int ATTRIBUTE_MAILER;
   int ATTRIBUTE_TZ;
   int ATTRIBUTE_GEO;
   int ATTRIBUTE_TITLE;
   int ATTRIBUTE_ROLE;
   int ATTRIBUTE_LOGO;
   int ATTRIBUTE_AGENT;
   int ATTRIBUTE_ORG;
   int ATTRIBUTE_NOTE;
   int ATTRIBUTE_REV;
   int ATTRIBUTE_SOUND;
   int ATTRIBUTE_URL;
   int ATTRIBUTE_UID;
   int ATTRIBUTE_KEY;
   int ATTRIBUTE_NICKNAME;
   int ATTRIBUTE_CATEGORIES;
   int ATTRIBUTE_PRODID;
   int ATTRIBUTE_CLASS;
   int ATTRIBUTE_SORT_STRING;
   int ATTRIBUTE_TIME_STAMP;
   int FIRST;
   int NEXT;
   int ADDR_DOM;
   int ADDR_INTL;
   int ADDR_POSTAL;
   int ADDR_PARCEL;
   int ADDR_HOME;
   int ADDR_WORK;
   int ADDR_PREF;
   int TEL_HOME;
   int TEL_WORK;
   int TEL_PREF;
   int TEL_VOICE;
   int TEL_FAX;
   int TEL_MSG;
   int TEL_CELL;
   int TEL_PAGER;
   int TEL_BBS;
   int TEL_MODEM;
   int TEL_CAR;
   int TEL_ISDN;
   int TEL_VIDEO;
   int TEL_PCS;
   int PREF;
   int AOL;
   int APPLELINK;
   int ATTMAIL;
   int CIS;
   int EWORLD;
   int INTERNET;
   int IBMMAIL;
   int MCIMAIL;
   int POWERSHARE;
   int PRODIGY;
   int TLX;
   int X400;

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
