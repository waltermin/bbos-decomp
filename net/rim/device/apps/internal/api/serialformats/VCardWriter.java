package net.rim.device.apps.internal.api.serialformats;

import java.io.OutputStream;
import java.util.Calendar;
import java.util.Date;
import net.rim.device.api.i18n.SimpleDateFormat;

public final class VCardWriter extends TokenWriter implements VCardDefine {
   private VCardProvider _vCard;
   private int _version;

   public VCardWriter(VCardProvider vCard, OutputStream os, String encoding) {
      super(os, encoding);
      this._vCard = vCard;
   }

   private static final String getString(int parameterType, int str) {
      switch (parameterType) {
         case 64655:
            switch (str) {
               case 1:
                  return "DOM";
               case 2:
                  return "INTL";
               case 4:
                  return "POSTAL";
               case 8:
                  return "PARCEL";
               case 16:
                  return "HOME";
               case 32:
                  return "WORK";
               case 64:
                  return "PREF";
               default:
                  throw new InvalidFormatException();
            }
         case 74303:
            switch (str) {
               case 79161:
                  return "PGP";
               case 2674086:
                  return "X509";
               default:
                  throw new InvalidFormatException();
            }
         case 82939:
            switch (str) {
               case 1:
                  return "HOME";
               case 2:
                  return "WORK";
               case 4:
                  return "PREF";
               case 8:
                  return "VOICE";
               case 16:
                  return "FAX";
               case 32:
                  return "MSG";
               case 64:
                  return "CELL";
               case 128:
                  return "PAGER";
               case 256:
                  return "BBS";
               case 512:
                  return "MODEM";
               case 1024:
                  return "CAR";
               case 2048:
                  return "ISDN";
               case 4096:
                  return "VIDEO";
               case 8192:
                  return "PCS";
               default:
                  throw new InvalidFormatException();
            }
         case 2342315:
         case 76105234:
            switch (str) {
               case 2563:
                  return "PS";
               case 65204:
                  return "AVI";
               case 65893:
                  return "BMP";
               case 66665:
                  return "CGM";
               case 67677:
                  return "DIB";
               case 70564:
                  return "GIF";
               case 76220:
                  return "MET";
               case 79058:
                  return "PDF";
               case 79333:
                  return "PMB";
               case 86064:
                  return "WMF";
               case 2283624:
                  return "JPEG";
               case 2372997:
                  return "MPEG";
               case 2455594:
                  return "PICT";
               case 2574837:
                  return "TIFF";
               case 73562957:
                  return "MPEG2";
               case 77380254:
                  return "QTIME";
               default:
                  throw new InvalidFormatException();
            }
         case 64205144:
            switch (str) {
               case -1924094359:
                  return "PUBLIC";
               case 364290440:
                  return "CONFIDENTIAL";
               case 403485027:
                  return "PRIVATE";
               default:
                  throw new InvalidFormatException();
            }
         case 66081660:
            switch (str) {
               case 1:
                  return "PREF";
               case 2:
                  return "AOL";
               case 4:
                  return "APPLELINK";
               case 8:
                  return "ATTMAIL";
               case 16:
                  return "CIS";
               case 32:
                  return "EWORLD";
               case 64:
                  return "INTERNET";
               case 128:
                  return "IBMMAIL";
               case 256:
                  return "MCIMAIL";
               case 512:
                  return "POWERSHARE";
               case 1024:
                  return "PRODIGY";
               case 2048:
                  return "TLX";
               case 4096:
                  return "X400";
               default:
                  throw new InvalidFormatException();
            }
         case 79089903:
            switch (str) {
               case 79034:
                  return "PCM";
               case 2008808:
                  return "AIFF";
               case 2657017:
                  return "WAVE";
               case 62970894:
                  return "BASIC";
            }
      }

      throw new InvalidFormatException();
   }

   public final void encodeVCard() {
      if (this._vCard != null && this._vCard.isValidVCardObject()) {
         this._version = this._vCard.getVersion();
         this.resetLength();
         this.addPropertyTag("BEGIN:VCARD");
         this.addLineBreak();
         this.buildNAME_SOURCE_PROFILE();
         this.buildVERSION();
         this.buildUid();
         this.buildFN();
         this.buildN();
         this.buildSORT_STRING();
         this.buildNICKNAME();
         this.buildDateTime(2033658);
         this.buildBinary(76105234);
         this.buildORG();
         this.buildBinary(2342315);
         this.buildTITLE();
         this.buildROLE();
         this.buildCATEGORIES();
         this.buildADR();
         this.buildLABEL();
         this.buildTEL();
         this.buildEMAIL();
         this.buildNOTE();
         this.buildCLASS();
         this.buildURL();
         this.buildPRODID();
         this.buildMAILER();
         this.buildGEO();
         this.buildTZ();
         this.buildAGENT();
         this.buildBinary(79089903);
         this.buildKEY();
         this.buildDateTime(81027);
         this.buildEXTENSION();
         this.addPropertyTag("END:VCARD");
         this.addLineBreak();
      } else {
         throw new IllegalArgumentException();
      }
   }

   private final void buildNAME_SOURCE_PROFILE() {
      if (this._version != 1) {
         String name = this._vCard.getName();
         String source = this._vCard.getSource();
         if (source != null) {
            if (name != null) {
               this.addProperty("NAME:", name);
            }

            this.addProperty("SOURCE:", source);
         }

         String profile = this._vCard.getProfile();
         if (profile != null) {
            this.addPropertyTag("PROFILE:VCARD");
            this.addLineBreak();
         }
      }
   }

   private final void buildUid() {
      String uid = this._vCard.getUid();
      if (uid != null) {
         this.addProperty("UID:", uid);
      }
   }

   private final void buildVERSION() {
      String ver;
      if (this._version == 1) {
         ver = "2.1";
      } else {
         if (this._version != 2) {
            throw new InvalidFormatException();
         }

         ver = "3.0";
      }

      this.addProperty("VERSION:", ver);
   }

   private final void buildURL() {
      String url = this._vCard.getUrl();
      if (url != null) {
         this.addProperty("URL:", url);
      }
   }

   private final void buildPRODID() {
      if (this._version != 1) {
         String prodid = this._vCard.getProdid();
         if (prodid != null) {
            this.addProperty("PRODID:", prodid);
         }
      }
   }

   private final void buildMAILER() {
      String mailer = this._vCard.getMailer();
      if (mailer != null) {
         this.addProperty("MAILER:", mailer);
      }
   }

   private final void buildGEO() {
      String geo = this._vCard.getGeo();
      if (geo != null) {
         this.addProperty("GEO:", geo);
      }
   }

   private final void buildTZ() {
      String tz = this._vCard.getTimeZone();
      if (tz != null) {
         this.addProperty("TZ:", tz);
      }
   }

   private final void buildCLASS() {
      if (this._version != 1) {
         int classid = this._vCard.getClassId();
         if (classid != 0) {
            String str = "CLASS:" + getString(64205144, classid);
            this.addPropertyTag(str);
            this.addLineBreak();
         }
      }
   }

   private final void buildFN() {
      String fn = this._vCard.getFormattedName();
      if (fn == null) {
         fn = "";
      }

      this.addProperty("FN:", fn);
   }

   private final void buildN() {
      String familyName = this._vCard.getFamilyName();
      boolean hasFamilyName = familyName != null;
      String givenName = this._vCard.getGivenName();
      boolean hasGivenName = givenName != null;
      String additionalName = this._vCard.getAdditionalName();
      boolean hasAdditionalName = additionalName != null;
      String namePrefix = this._vCard.getNamePrefix();
      boolean hasNamePrefix = namePrefix != null;
      String nameSuffix = this._vCard.getNameSuffix();
      boolean hasNameSuffix = nameSuffix != null;
      this.addPropertyTag("N:");
      this.resetLength();
      if (hasFamilyName) {
         this.addString(familyName);
      }

      if (hasGivenName || hasAdditionalName || hasNamePrefix || hasNameSuffix) {
         this.addSemiColonSeparator();
         if (hasGivenName) {
            this.addString(givenName);
         }

         if (hasAdditionalName || hasNamePrefix || hasNameSuffix) {
            this.addSemiColonSeparator();
            if (hasAdditionalName) {
               this.addString(additionalName);
            }

            if (hasNamePrefix || hasNameSuffix) {
               this.addSemiColonSeparator();
               if (hasNamePrefix) {
                  this.addString(namePrefix);
               }

               if (hasNameSuffix) {
                  this.addSemiColonSeparator();
                  this.addString(nameSuffix);
               }
            }
         }
      }

      this.addLineBreak();
   }

   private final void buildSORT_STRING() {
      if (this._version != 1) {
         String sortString = this._vCard.getSortString();
         if (sortString != null) {
            this.addProperty("SORT-STRING:", sortString);
         }
      }
   }

   private final void buildNICKNAME() {
      if (this._version != 1) {
         String nickname = this._vCard.getNickname();
         if (nickname != null) {
            this.addProperty("NICKNAME:", nickname);
         }
      }
   }

   private final void buildDateTime(int type) {
      Date date;
      String typeStr;
      if (type == 2033658) {
         date = this._vCard.getBirthdate();
         typeStr = "BDAY:";
      } else {
         date = this._vCard.getRev();
         typeStr = "REV:";
      }

      if (date != null) {
         Calendar calendar = Calendar.getInstance();
         calendar.setTime(date);
         SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'Z'");
         StringBuffer dateSB = new StringBuffer();
         sdf.format(calendar, dateSB, null);
         this.addProperty(typeStr, dateSB.toString());
      }
   }

   private final void buildBinary(int property) {
      String typeStr = null;
      byte[] imageData = null;
      int value = 0;
      int type = 0;
      switch (property) {
         case 2342315:
            typeStr = "LOGO;";
            imageData = this._vCard.getLogoData();
            value = this._vCard.getLogoValue();
            type = this._vCard.getLogoType();
            break;
         case 76105234:
            typeStr = "PHOTO;";
            imageData = this._vCard.getPhotoData();
            value = this._vCard.getPhotoValue();
            type = this._vCard.getPhotoType();
            break;
         case 79089903:
            typeStr = "SOUND;";
            imageData = this._vCard.getSoundData();
            value = this._vCard.getSoundValue();
            type = this._vCard.getSoundType();
      }

      if (imageData != null) {
         this.addPropertyTag(typeStr);
         if (type != 0) {
            String str = getString(property, type) + ";";
            this.addString(str);
         }

         if (value == 84300) {
            this.addPropertyTag("VALUE=URI:");
            this.addByteArray(imageData);
            this.addLineBreak();
         } else {
            if (this._version == 1) {
               this.addPropertyTag("ENCODING=BASE64:");
            } else {
               this.addPropertyTag("ENCODING=B:");
            }

            try {
               this.addDataInBase64Format(imageData);
            } finally {
               return;
            }
         }
      }
   }

   private final void buildKEY() {
      byte[] keyData = this._vCard.getKeyData();
      if (keyData != null) {
         int type = this._vCard.getKeyType();
         int value = this._vCard.getKeyValue();
         StringBuffer typeStr = new StringBuffer();
         if (value == 0) {
            if (type == 0) {
               typeStr.append("KEY:");
            } else {
               typeStr.append("KEY;TYPE=");
               typeStr.append(getString(74303, type));
               typeStr.append(':');
            }

            this.addPropertyTag(typeStr.toString());
            this.addByteArray(keyData);
            this.addLineBreak();
         } else {
            if (this._version == 1) {
               typeStr.append("KEY;ENCODING=BASE64");
            } else {
               typeStr.append("KEY;ENCODING=B");
            }

            if (type != 0) {
               typeStr.append(";TYPE=");
               typeStr.append(getString(74303, type));
            }

            typeStr.append(':');
            this.addPropertyTag(typeStr.toString());

            try {
               this.addDataInBase64Format(keyData);
            } finally {
               return;
            }
         }
      }
   }

   private final void buildEMAIL() {
      if (this._vCard.hasEmail()) {
         String email = this._vCard.getEmailAddress(1);
         int type = this._vCard.getEmailType(1);
         this.addPropertyTag("EMAIL");
         this.addMultiType(type, 66081660);
         this.addPropertyTag(":");
         this.addString(email);
         this.addLineBreak();

         while (this._vCard.hasNextEmail()) {
            email = this._vCard.getEmailAddress(2);
            type = this._vCard.getEmailType(2);
            this.addPropertyTag("EMAIL");
            this.addMultiType(type, 66081660);
            this.addPropertyTag(":");
            this.addString(email);
            this.addLineBreak();
         }
      }
   }

   private final void buildORG() {
      String company = this._vCard.getOrganizationName();
      String unit1 = this._vCard.getOrganizationUnit1();
      String unit2 = this._vCard.getOrganizationUnit2();
      if (company != null || unit1 != null || unit2 != null) {
         this.addPropertyTag("ORG:");
         if (company != null) {
            this.addString(company);
         }

         this.addSemiColonSeparator();
         if (unit1 != null) {
            this.addString(unit1);
         }

         this.addSemiColonSeparator();
         if (unit2 != null) {
            this.addString(unit2);
         }

         this.addLineBreak();
      }
   }

   private final void buildTITLE() {
      String title = this._vCard.getTitle();
      if (title != null) {
         this.addProperty("TITLE:", title);
      }
   }

   private final void buildROLE() {
      String role = this._vCard.getRole();
      if (role != null) {
         this.addProperty("ROLE:", role);
      }
   }

   private final void buildCATEGORIES() {
      if (this._version != 1) {
         String categories = this._vCard.getCategories();
         if (categories != null) {
            this.addProperty("CATEGORIES:", categories);
         }
      }
   }

   private final void buildTEL() {
      if (this._vCard.hasTelephone()) {
         int phoneType = this._vCard.getTelephoneType(1);
         String phoneNumber = this._vCard.getTelephoneNumber(1);
         this.addPropertyTag("TEL");
         this.addMultiType(phoneType, 82939);
         this.addPropertyTag(":");
         this.addString(phoneNumber);
         this.addLineBreak();

         while (this._vCard.hasNextTelephone()) {
            phoneType = this._vCard.getTelephoneType(2);
            phoneNumber = this._vCard.getTelephoneNumber(2);
            this.resetLength();
            this.addPropertyTag("TEL");
            this.addMultiType(phoneType, 82939);
            this.addPropertyTag(":");
            this.addString(phoneNumber);
            this.addLineBreak();
         }
      }
   }

   private final void buildADR() {
      if (this._vCard.hasAddress()) {
         int addrType = this._vCard.getAddressType(1);
         this.addPropertyTag("ADR");
         this.addMultiType(addrType, 64655);
         this.addPropertyTag(":");
         this.resetLength();
         String str = this._vCard.getPostOfficeAddress(1);
         if (str != null) {
            this.addString(str);
         }

         this.addSemiColonSeparator();
         str = this._vCard.getExtendedAddress(1);
         if (str != null) {
            this.addString(str);
         }

         this.addSemiColonSeparator();
         str = this._vCard.getStreet(1);
         if (str != null) {
            this.addString(str);
         }

         this.addSemiColonSeparator();
         str = this._vCard.getLocality(1);
         if (str != null) {
            this.addString(str);
         }

         this.addSemiColonSeparator();
         str = this._vCard.getRegion(1);
         if (str != null) {
            this.addString(str);
         }

         this.addSemiColonSeparator();
         str = this._vCard.getPostalCode(1);
         if (str != null) {
            this.addString(str);
         }

         this.addSemiColonSeparator();
         str = this._vCard.getCountry(1);
         if (str != null) {
            this.addString(str);
         }

         this.addLineBreak();

         for (; this._vCard.hasNextAddress(); this.addLineBreak()) {
            addrType = this._vCard.getAddressType(2);
            this.addPropertyTag("ADR");
            this.addMultiType(addrType, 64655);
            this.addPropertyTag(":");
            this.resetLength();
            str = this._vCard.getPostOfficeAddress(2);
            if (str != null) {
               this.addString(str);
            }

            this.addSemiColonSeparator();
            str = this._vCard.getExtendedAddress(2);
            if (str != null) {
               this.addString(str);
            }

            this.addSemiColonSeparator();
            str = this._vCard.getStreet(2);
            if (str != null) {
               this.addString(str);
            }

            this.addSemiColonSeparator();
            str = this._vCard.getLocality(2);
            if (str != null) {
               this.addString(str);
            }

            this.addSemiColonSeparator();
            str = this._vCard.getRegion(2);
            if (str != null) {
               this.addString(str);
            }

            this.addSemiColonSeparator();
            str = this._vCard.getPostalCode(2);
            if (str != null) {
               this.addString(str);
            }

            this.addSemiColonSeparator();
            str = this._vCard.getCountry(2);
            if (str != null) {
               this.addString(str);
            }
         }
      }
   }

   private final void buildLABEL() {
      String label = this._vCard.getLabel();
      if (label != null) {
         int type = this._vCard.getLabelType();
         this.addPropertyTag("LABEL");
         this.addMultiType(type, 64655);
         this.addPropertyTag(":");
         if (this._version == 1) {
            this.addDataInQuotedPrintableFormat(label.getBytes());
            return;
         }

         this.addString(label);
         this.addLineBreak();
      }
   }

   private final void buildNOTE() {
      String note = this._vCard.getNote();
      if (note != null) {
         if (note.charAt(note.length() - 1) == ' ') {
            note = note.substring(0, note.length() - 1);
         }

         note.trim();
         if (this._version == 1) {
            this.addPropertyTag("NOTE;ENCODING=QUOTED-PRINTABLE:");
            this.addDataInQuotedPrintableFormat(note.getBytes());
            return;
         }

         this.addProperty("NOTE:", note);
      }
   }

   private final void buildEXTENSION() {
      if (this._vCard.hasExtension()) {
         StringBuffer name = new StringBuffer("X-");
         name.append(this._vCard.getExtensionName(1));
         String content = this._vCard.getExtensionData(1);
         if (name.length() == 2 || content == null) {
            throw new IllegalArgumentException();
         }

         name.append(':');
         this.addPropertyTag(name.toString());
         if (this._version == 1) {
            this.addDataInQuotedPrintableFormat(content.getBytes());
         } else {
            this.addString(content);
            this.addLineBreak();
         }

         while (this._vCard.hasNextExtension()) {
            name = new StringBuffer("X-");
            name.append(this._vCard.getExtensionName(2));
            content = this._vCard.getExtensionData(2);
            name.append(':');
            this.addPropertyTag(name.toString());
            if (this._version == 1) {
               this.addDataInQuotedPrintableFormat(content.getBytes());
            } else {
               this.addString(content);
               this.addLineBreak();
            }
         }
      }
   }

   private final void buildAGENT() {
      String text = this._vCard.getAgentInText();
      if (text != null) {
         int value = this._vCard.getAgentTextValue();
         String str;
         if (value == 84300) {
            str = "AGENT;VALUE=URI:";
         } else {
            str = "AGENT:";
         }

         this.addProperty(str, text);
      } else {
         VCardProvider vCard = (VCardProvider)this._vCard.getAgentInVcard();
         if (vCard != null) {
            this._vCard = vCard;
            this.addPropertyTag("AGENT:");
            this.addLineBreak();
            this.encodeVCard();
         }
      }
   }

   private final void addSemiColonSeparator() {
      this.addOneByte(59);
   }

   private final void addMultiType(int type, int parameterType) {
      int count = 0;
      switch (parameterType) {
         case 64655:
            count = 7;
            break;
         case 82939:
            count = 14;
            break;
         case 66081660:
            count = 13;
      }

      String typeStr = null;
      int currentType = 1;

      for (int i = 0; i < count; i++) {
         if ((type & currentType) == currentType) {
            String temp = getString(parameterType, currentType);
            if (typeStr == null) {
               typeStr = temp;
            } else {
               typeStr = typeStr.concat(",");
               typeStr = typeStr.concat(temp);
            }
         }

         currentType *= 2;
      }

      if (typeStr != null) {
         this.addPropertyTag(";TYPE=");
         this.addPropertyTag(typeStr);
      }
   }
}
