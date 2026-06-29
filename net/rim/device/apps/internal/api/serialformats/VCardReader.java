package net.rim.device.apps.internal.api.serialformats;

import java.io.InputStream;
import java.util.Date;
import java.util.Stack;
import net.rim.device.api.util.IntVector;

public final class VCardReader implements VCardDefine {
   private VCardProvider _vCardCurrent;
   private int _version = 1;
   private TokenParser _tokenParser;
   private PropertyParser _propertyParser;
   private IntVector _usedTags = new IntVector();
   private boolean _nonConformanceMode;

   public VCardReader(VCardProvider vCardProvider, InputStream in, String encoding) {
      this._tokenParser = new TokenParser(in, encoding);
      this._propertyParser = new PropertyParser();
      this._vCardCurrent = vCardProvider;
   }

   public final void setNonConformanceMode(boolean value) {
      this._nonConformanceMode = value;
   }

   private static final boolean isValidEncoding(int encoding) {
      switch (encoding) {
         case -151191742:
         case 66:
         case 1704278:
         case 1734069:
         case 1952093519:
            return true;
         default:
            return false;
      }
   }

   private static final boolean isValidValue(int value) {
      switch (value) {
         case -2130667879:
         case 66718:
         case 84300:
         case 749627151:
         case 1959329793:
            return true;
         default:
            return false;
      }
   }

   private static final int getFlag(int tag, int flag) {
      switch (tag) {
         case 64655:
            switch (flag) {
               case -1942096887:
                  return 8;
               case -1929121589:
                  return 4;
               case 67874:
                  return 1;
               case 2223327:
                  return 16;
               case 2252381:
                  return 2;
               case 2464291:
                  return 64;
               case 2670353:
                  return 32;
               default:
                  return -1;
            }
         case 82939:
            switch (flag) {
               case 65555:
                  return 256;
               case 66484:
                  return 1024;
               case 69373:
                  return 16;
               case 76641:
                  return 32;
               case 79040:
                  return 4096;
               case 2064738:
                  return 64;
               case 2223327:
                  return 1;
               case 2256692:
                  return 2048;
               case 2464291:
                  return 4;
               case 2670353:
                  return 2;
               case 73532170:
                  return 512;
               case 75888547:
                  return 128;
               case 81665115:
                  return 4096;
               case 81848594:
                  return 8;
               default:
                  return -1;
            }
         case 66081660:
            switch (flag) {
               case -1968714229:
                  return 128;
               case 64990:
                  return 2;
               case 66733:
                  return 16;
               case 83168:
                  return 2048;
               case 2464291:
                  return 1;
               case 2673116:
                  return 4096;
               case 42980280:
                  return 8;
               case 408497220:
                  return 1024;
               case 693175860:
                  return 4;
               case 944147962:
                  return 512;
               case 1353037633:
                  return 64;
               case 1606235562:
                  return 256;
               case 2058192461:
                  return 32;
            }
      }

      return -1;
   }

   public final VCardProvider parseIt() {
      int currentProperty = 0;
      Stack vCardStack = new Stack();
      boolean beginTagFound = false;

      while (true) {
         int previousProperty = currentProperty;
         currentProperty = this.nextProperty();
         switch (currentProperty) {
            case -2028243804:
               this._vCardCurrent.setMailer(this.buildTextProperty());
               break;
            case -1926485326:
               if (!this._nonConformanceMode && this._version == 1) {
                  throw new InvalidFormatException();
               }

               this._vCardCurrent.setProdid(this.buildStringProperty());
               break;
            case -1843176421:
               if (!this._nonConformanceMode && this._version == 1) {
                  throw new InvalidFormatException();
               }

               this._vCardCurrent.setSource(this.buildSourceProperty());
               break;
            case 78:
               this.buildN();
               break;
            case 2248:
               this._vCardCurrent.setFormattedName(this.buildTextProperty());
               break;
            case 2694:
               this._vCardCurrent.setTimeZone(this.buildTZ());
               break;
            case 2773:
               this.buildEXTENSION();
               break;
            case 64655:
               this.buildADR();
               break;
            case 68795:
               if (!beginTagFound) {
                  throw new InvalidFormatException();
               }

               if (vCardStack.empty()) {
                  if (!this._vCardCurrent.isValidVCardObject()) {
                     throw new InvalidFormatException();
                  }

                  return this._vCardCurrent;
               }

               this._vCardCurrent = (VCardProvider)vCardStack.pop();
               break;
            case 70449:
               this._vCardCurrent.setGeo(this.buildTextProperty());
               break;
            case 74303:
               this.buildBinaryProperty(74303);
               break;
            case 78532:
               this.buildORG();
               break;
            case 81027:
               this._vCardCurrent.setRev(this.buildDateTime());
               break;
            case 82939:
               this.buildTEL();
               break;
            case 84016:
               this._vCardCurrent.setUid(this.buildStringProperty());
               break;
            case 84303:
               this.buildURL();
               break;
            case 2033658:
               this._vCardCurrent.setBirthdate(this.buildDateTime());
               break;
            case 2342315:
               this.buildBinaryProperty(2342315);
               break;
            case 2388619:
               if (!this._nonConformanceMode && this._version == 1) {
                  throw new InvalidFormatException();
               }

               this._vCardCurrent.setName(this.buildStringProperty());
               break;
            case 2402290:
               this._vCardCurrent.setNote(this.buildTextProperty());
               break;
            case 2521206:
               this._vCardCurrent.setRole(this.buildTextProperty());
               break;
            case 62212837:
               this.buildAGENT();
               break;
            case 63078537:
               if (beginTagFound) {
                  VCardProvider tempVCard = this._vCardCurrent.createVCardProvider();
                  if (previousProperty == 62212837) {
                     this._vCardCurrent.setAgentInVcard(tempVCard);
                  } else {
                     this._vCardCurrent.addNestedVCard(tempVCard);
                  }

                  vCardStack.push(this._vCardCurrent);
                  this._vCardCurrent = tempVCard;
                  this._vCardCurrent = tempVCard;
               } else {
                  beginTagFound = true;
               }
               break;
            case 64205144:
               if (!this._nonConformanceMode && this._version == 1) {
                  throw new InvalidFormatException();
               }

               this._vCardCurrent.setClassId(this.buildClassProperty());
               break;
            case 66081660:
               this.buildEMAIL();
               break;
            case 72189652:
               this.buildLABEL();
               break;
            case 76105234:
               this.buildBinaryProperty(76105234);
               break;
            case 79089903:
               this.buildBinaryProperty(79089903);
               break;
            case 79833656:
               this._vCardCurrent.setTitle(this.buildTextProperty());
               break;
            case 408556937:
               if (!this._nonConformanceMode && this._version == 1) {
                  throw new InvalidFormatException();
               }

               this._vCardCurrent.setProfile(this.buildStringProperty());
               break;
            case 461300512:
               if (!this._nonConformanceMode && this._version == 1) {
                  throw new InvalidFormatException();
               }

               String sortString = this.buildTextProperty();
               String familyName = this._vCardCurrent.getFamilyName();
               String givenName = this._vCardCurrent.getGivenName();
               if (familyName != null && familyName.indexOf(sortString) != -1) {
                  this._vCardCurrent.setSortString(sortString);
                  break;
               } else {
                  if (givenName != null && givenName.indexOf(sortString) != -1) {
                     this._vCardCurrent.setSortString(sortString);
                     break;
                  }

                  throw new InvalidFormatException();
               }
            case 853317742:
               if (!this._nonConformanceMode && this._version == 1) {
                  throw new InvalidFormatException();
               }

               this._vCardCurrent.setNickname(this.buildTextProperty());
               break;
            case 1069590712:
               this._version = this.buildVersionProperty();
               this._vCardCurrent.setVersion(this._version);
               break;
            case 1781608988:
               if (!this._nonConformanceMode && this._version == 1) {
                  throw new InvalidFormatException();
               }

               this._vCardCurrent.setCategories(this.buildTextProperty());
               break;
            default:
               throw new InvalidFormatException();
         }
      }
   }

   private final int nextProperty() {
      String propertyName = this._tokenParser.nextProperty().trim();
      int id = propertyName.hashCode();
      switch (id) {
         case 68795:
         case 63078537:
            if (this._usedTags.contains(id)) {
               throw new InvalidFormatException();
            } else if (this._tokenParser.getBreakingCharacter() != 58) {
               throw new InvalidFormatException();
            } else if (id == 63078537 && !this._tokenParser.getCommonOneLine(0).equals("VCARD")) {
               throw new InvalidFormatException();
            } else {
               if (id == 68795 && !this._tokenParser.readIfLeadingString("VCARD")) {
                  throw new InvalidFormatException();
               }

               this._usedTags.addElement(id);
               return id;
            }
         default:
            if (this._usedTags.contains(id)) {
               if (!this._nonConformanceMode) {
                  throw new InvalidFormatException();
               }
            } else {
               this._usedTags.addElement(id);
            }
         case 2773:
         case 64655:
         case 82939:
         case 66081660:
         case 72189652:
            return id;
      }
   }

   private final String buildStringProperty() {
      if (this._tokenParser.getBreakingCharacter() != 58) {
         throw new InvalidFormatException();
      } else {
         return this._tokenParser.getCommonOneLine(0);
      }
   }

   private final String buildSourceProperty() {
      if (this._tokenParser.getBreakingCharacter() != 58) {
         this._tokenParser.setEndOfTokenCharacters(";:,=");
         this._tokenParser.ignoreWhitespaceTokens(true);

         while (this._propertyParser.getNextParameterAndValue(this._tokenParser)) {
            String paraName = this._propertyParser.getParameterName();
            String paraValue = this._propertyParser.getParameterValue();
            if (paraName.equals("VALUE")) {
               if (!paraValue.equals("URI")) {
                  throw new InvalidFormatException();
               }
            } else if (paraName.equals("CONTEXT")) {
               if (!paraValue.equals("WORD")) {
                  throw new InvalidFormatException();
               }
            } else if (!this.isExtensionName(paraName)) {
               throw new InvalidFormatException();
            }
         }

         this._tokenParser.clearEndOfTokenCharacters(";:,=");
      }

      return this._tokenParser.getCommonOneLine(0);
   }

   private final String buildTextProperty() {
      PARAMETER para = new PARAMETER();
      this.getPropertyBasicTextParameter(para);
      return new String(this.getContent(para._encoding, 0));
   }

   private final void buildN() {
      PARAMETER para = new PARAMETER();
      this.getPropertyBasicTextParameter(para);
      this._tokenParser.setEndOfTokenCharacters(";");
      this._tokenParser.ignoreWhitespaceTokens(false);
      int count = 0;

      do {
         String str = this._tokenParser.nextToken();
         switch (count) {
            case -1:
               break;
            case 0:
            default:
               this._vCardCurrent.setFamilyName(str);
               count++;
               break;
            case 1:
               this._vCardCurrent.setGivenName(str);
               count++;
               break;
            case 2:
               this._vCardCurrent.setAdditionalName(str);
               count++;
               break;
            case 3:
               this._vCardCurrent.setNamePrefix(str);
               count++;
               break;
            case 4:
               this._vCardCurrent.setNameSuffix(str);
               count++;
         }
      } while (count != 5 && !this._tokenParser.isLineBreak());

      this._tokenParser.clearEndOfTokenCharacters(";");
   }

   private final void buildBinaryProperty(int tag) {
      int type = 0;
      int value = 1959329793;
      int encoding = 0;
      if (this._tokenParser.getBreakingCharacter() != 58) {
         this._tokenParser.setEndOfTokenCharacters(";:,=");
         this._tokenParser.ignoreWhitespaceTokens(true);

         while (this._propertyParser.getNextParameterAndValue(this._tokenParser)) {
            String paraName = this._propertyParser.getParameterName();
            String paraValue = this._propertyParser.getParameterValue();
            if (paraValue == null) {
               throw new InvalidFormatException();
            }

            int para = paraValue.hashCode();
            if (paraName != null) {
               if (paraName.equals("VALUE")) {
                  value = para;
               } else if (paraName.equals("ENCODING")) {
                  encoding = para;
               } else if (paraName.equals("TYPE")) {
                  type = para;
               } else if (!this.isExtensionName(paraName)) {
                  throw new InvalidFormatException();
               }
            } else {
               if (this._version != 1) {
                  throw new InvalidFormatException();
               }

               if (isValidValue(para)) {
                  value = para;
               } else if (isValidEncoding(para)) {
                  encoding = para;
               } else {
                  type = para;
               }
            }
         }

         this._tokenParser.clearEndOfTokenCharacters(";:,=");
         if (value != 1959329793) {
            if (tag == 74303 && value != 0) {
               throw new InvalidFormatException();
            }

            if (value != 84300) {
               throw new InvalidFormatException();
            }
         } else if (!this._nonConformanceMode && this._version == 2 && encoding != 66) {
            throw new InvalidFormatException();
         }
      }

      byte[] data = this.getContent(encoding, 0);
      switch (tag) {
         case 74303:
            this._vCardCurrent.setKeyType(type);
            this._vCardCurrent.setKeyValue(value);
            this._vCardCurrent.setKeyData(data);
            return;
         case 2342315:
            this._vCardCurrent.setLogoType(type);
            this._vCardCurrent.setLogoValue(value);
            this._vCardCurrent.setLogoData(data);
         default:
            return;
         case 76105234:
            this._vCardCurrent.setPhotoType(type);
            this._vCardCurrent.setPhotoValue(value);
            this._vCardCurrent.setPhotoData(data);
            return;
         case 79089903:
            this._vCardCurrent.setSoundType(type);
            this._vCardCurrent.setSoundValue(value);
            this._vCardCurrent.setSoundData(data);
      }
   }

   private final Date buildDateTime() {
      if (this._tokenParser.getBreakingCharacter() != 58) {
         this._tokenParser.setEndOfTokenCharacters(";:,=");
         this._tokenParser.ignoreWhitespaceTokens(true);
         if (this._propertyParser.getNextParameterAndValue(this._tokenParser)) {
            String paraName = this._propertyParser.getParameterName();
            String paraValue = this._propertyParser.getParameterValue();
            if (paraValue == null) {
               throw new InvalidFormatException();
            }

            if (!paraValue.equals("DATE-TIME") && !paraValue.equals("DATE")) {
               throw new InvalidFormatException();
            }

            if (this._version == 2) {
               if (paraName == null) {
                  throw new InvalidFormatException();
               }

               if (!paraName.equals("VALUE")) {
                  throw new InvalidFormatException();
               }
            } else {
               if (this._version != 1) {
                  throw new InvalidFormatException();
               }

               if (paraName != null && !paraName.equals("VALUE")) {
                  throw new InvalidFormatException();
               }
            }
         }

         this._tokenParser.clearEndOfTokenCharacters(";:,=");
      }

      if (this._tokenParser.getBreakingCharacter() != 58) {
         throw new InvalidFormatException();
      } else {
         return this._tokenParser.getDateTime();
      }
   }

   private final void buildADR() {
      int addrType = 0;
      PARAMETER para = new PARAMETER();
      if (this._tokenParser.getBreakingCharacter() != 58) {
         this._tokenParser.setEndOfTokenCharacters(";:,=");
         this._tokenParser.ignoreWhitespaceTokens(true);

         while (this._propertyParser.getNextParameterAndValue(this._tokenParser)) {
            String name = this._propertyParser.getParameterName();
            String value = this._propertyParser.getParameterValue();
            if (!this.getTextParameter(name, value, para)) {
               int data;
               if ((data = getFlag(64655, value.hashCode())) == -1) {
                  throw new InvalidFormatException();
               }

               addrType |= data;
            }
         }

         this._tokenParser.clearEndOfTokenCharacters(";:,=");
      }

      int count = 0;
      String postOfficeAddress = null;
      String extendedAddress = null;
      String street = null;
      String locality = null;
      String region = null;
      String postalCode = null;
      String country = null;

      do {
         String str = new String(this.getContent(para._encoding, 59));
         switch (count) {
            case -1:
               break;
            case 0:
            default:
               postOfficeAddress = str;
               count++;
               break;
            case 1:
               extendedAddress = str;
               count++;
               break;
            case 2:
               street = str;
               count++;
               break;
            case 3:
               locality = str;
               count++;
               break;
            case 4:
               region = str;
               count++;
               break;
            case 5:
               postalCode = str;
               count++;
               break;
            case 6:
               country = str;
               count++;
         }
      } while (count != 7 && !this._tokenParser.isLineBreak());

      this._vCardCurrent.addAddress(addrType, postOfficeAddress, extendedAddress, street, locality, region, postalCode, country);
   }

   private final void buildLABEL() {
      PARAMETER para = new PARAMETER();
      if (this._tokenParser.getBreakingCharacter() != 58) {
         this._tokenParser.setEndOfTokenCharacters(";:,=");
         this._tokenParser.ignoreWhitespaceTokens(true);

         while (this._propertyParser.getNextParameterAndValue(this._tokenParser)) {
            String name = this._propertyParser.getParameterName();
            String value = this._propertyParser.getParameterValue();
            if (!this.getTextParameter(name, value, para)) {
               int data;
               if ((data = getFlag(64655, value.hashCode())) == -1) {
                  throw new InvalidFormatException();
               }

               this._vCardCurrent.setLabelType(data);
            }
         }

         this._tokenParser.clearEndOfTokenCharacters(";:,=");
      }

      this._vCardCurrent.setLabel(new String(this.getContent(para._encoding, 0)));
   }

   private final void buildURL() {
      PARAMETER para = new PARAMETER();
      if (this._tokenParser.getBreakingCharacter() != 58) {
         this._tokenParser.setEndOfTokenCharacters(";:,=");
         this._tokenParser.ignoreWhitespaceTokens(true);

         while (this._propertyParser.getNextParameterAndValue(this._tokenParser)) {
            String name = this._propertyParser.getParameterName();
            String value = this._propertyParser.getParameterValue();
            if (this.getTextParameter(name, value, para)) {
            }
         }

         this._tokenParser.clearEndOfTokenCharacters(";:,=");
      }

      this._vCardCurrent.setUrl(this._tokenParser.getCommonOneLine(0));
   }

   private final void buildTEL() {
      int phoneType = 0;
      if (this._tokenParser.getBreakingCharacter() != 58) {
         this._tokenParser.setEndOfTokenCharacters(";:,=");
         this._tokenParser.ignoreWhitespaceTokens(true);
         this._propertyParser.reset();

         while (this._propertyParser.getNextParameterAndValue(this._tokenParser)) {
            String paraName = this._propertyParser.getParameterName();
            String paraValue = this._propertyParser.getParameterValue();
            if (paraValue == null) {
               throw new InvalidFormatException();
            }

            switch (this._version) {
               case 0:
                  throw new InvalidFormatException();
               case 1:
               default:
                  if (paraName != null && !paraName.equals("TYPE")) {
                     throw new InvalidFormatException();
                  }

                  int data;
                  if ((data = getFlag(82939, paraValue.hashCode())) == -1) {
                     throw new InvalidFormatException();
                  }

                  phoneType |= data;
                  break;
               case 2:
                  if (paraName == null || !paraName.equals("TYPE")) {
                     throw new InvalidFormatException();
                  }

                  int data;
                  if ((data = getFlag(82939, paraValue.hashCode())) == -1) {
                     throw new InvalidFormatException();
                  }

                  phoneType |= data;
            }
         }

         this._tokenParser.clearEndOfTokenCharacters(";:,=");
      }

      this._vCardCurrent.addTelephone(phoneType, this._tokenParser.getCommonOneLine(0));
   }

   private final void buildEMAIL() {
      int type = 0;
      if (this._tokenParser.getBreakingCharacter() != 58) {
         this._tokenParser.setEndOfTokenCharacters(";:,=");
         this._tokenParser.ignoreWhitespaceTokens(true);
         this._propertyParser.reset();

         while (this._propertyParser.getNextParameterAndValue(this._tokenParser)) {
            String paraName = this._propertyParser.getParameterName();
            String paraValue = this._propertyParser.getParameterValue();
            if (paraValue == null) {
               throw new InvalidFormatException();
            }

            switch (this._version) {
               case 0:
                  throw new InvalidFormatException();
               case 1:
               default:
                  if (paraName != null && !paraName.equals("TYPE")) {
                     throw new InvalidFormatException();
                  }

                  int data;
                  if ((data = getFlag(66081660, paraValue.hashCode())) != -1) {
                     type |= data;
                  }
                  break;
               case 2:
                  if (paraName == null || !paraName.equals("TYPE")) {
                     throw new InvalidFormatException();
                  }

                  int data;
                  if ((data = getFlag(66081660, paraValue.hashCode())) != -1) {
                     type |= data;
                  }
            }
         }

         this._tokenParser.clearEndOfTokenCharacters(";:,=");
      }

      if (type == 0) {
         type = 64;
      }

      this._vCardCurrent.addEmail(type, this._tokenParser.getCommonOneLine(0));
   }

   private final int buildVersionProperty() {
      if (this._tokenParser.getBreakingCharacter() != 58) {
         throw new InvalidFormatException();
      } else {
         String ver = this._tokenParser.getCommonOneLine(0);
         if (ver.equals("2.1")) {
            return 1;
         } else if (ver.equals("3.0")) {
            return 2;
         } else {
            throw new InvalidFormatException();
         }
      }
   }

   private final int buildClassProperty() {
      if (this._tokenParser.getBreakingCharacter() != 58) {
         throw new InvalidFormatException();
      }

      String str = this._tokenParser.getCommonOneLine(0);
      return str.hashCode();
   }

   private final void buildORG() {
      PARAMETER para = new PARAMETER();
      this.getPropertyBasicTextParameter(para);
      int count = 0;

      do {
         String str = new String(this.getContent(para._encoding, 59));
         switch (count) {
            case -1:
               break;
            case 0:
            default:
               this._vCardCurrent.setOrganizationName(str);
               count++;
               break;
            case 1:
               this._vCardCurrent.setOrganizationUnit1(str);
               count++;
               break;
            case 2:
               this._vCardCurrent.setOrganizationUnit2(str);
               count++;
         }

         if (count == 3) {
            return;
         }
      } while (!this._tokenParser.isLineBreak());
   }

   private final String buildTZ() {
      if (this._tokenParser.getBreakingCharacter() != 58) {
         this._tokenParser.setEndOfTokenCharacters(";:,=");
         this._tokenParser.ignoreWhitespaceTokens(true);

         while (this._propertyParser.getNextParameterAndValue(this._tokenParser)) {
            String paraName = this._propertyParser.getParameterName();
            String paraValue = this._propertyParser.getParameterValue();
            if (!paraName.equals("VALUE") || !paraValue.equals("TEXT")) {
               throw new InvalidFormatException();
            }
         }

         this._tokenParser.clearEndOfTokenCharacters(";:,=");
      }

      return this._tokenParser.getCommonOneLine(0);
   }

   private final void buildAGENT() {
      if (this._version != 1) {
         if (this._tokenParser.getBreakingCharacter() == 58) {
            if (!this._tokenParser.isLeadingString("BEGIN")) {
               throw new InvalidFormatException();
            }

            this._vCardCurrent.setAgentInText(0, this._tokenParser.getCommonOneLine(0));
         } else {
            this._tokenParser.setEndOfTokenCharacters(";:,=");
            this._tokenParser.ignoreWhitespaceTokens(true);

            while (this._propertyParser.getNextParameterAndValue(this._tokenParser)) {
               String paraName = this._propertyParser.getParameterName();
               String paraValue = this._propertyParser.getParameterValue();
               if (!paraName.equals("VALUE") || !paraValue.equals("URI")) {
                  throw new InvalidFormatException();
               }
            }

            this._tokenParser.clearEndOfTokenCharacters(";:,=");
            this._vCardCurrent.setAgentInText(84300, this._tokenParser.getCommonOneLine(0));
         }
      }
   }

   private final void buildEXTENSION() {
      this._tokenParser.setEndOfTokenCharacters(":");
      this._tokenParser.ignoreWhitespaceTokens(true);
      String extName = this._tokenParser.nextToken();
      this._tokenParser.clearEndOfTokenCharacters(":");
      String extData = this._tokenParser.getCommonOneLine(0);
      this._vCardCurrent.addExtension(extName, extData);
   }

   private final byte[] getContent(int encodingType, int additionalStopSign) {
      if (encodingType == -151191742) {
         return this._tokenParser.getQuotedPrintableContent(additionalStopSign);
      } else {
         return encodingType != 1952093519 && encodingType != 66
            ? this._tokenParser.getCommonOneLine(additionalStopSign).getBytes()
            : this._tokenParser.getBase64Content(additionalStopSign);
      }
   }

   private final boolean isExtensionName(String name) {
      return name.length() < 2 ? false : name.charAt(0) == 'X' && name.charAt(1) == '-';
   }

   private final boolean getPropertyBasicTextParameter(PARAMETER pm) {
      if (this._tokenParser.getBreakingCharacter() != 58) {
         this._tokenParser.setEndOfTokenCharacters(";:,=");
         this._tokenParser.ignoreWhitespaceTokens(true);

         while (this._propertyParser.getNextParameterAndValue(this._tokenParser)) {
            String paraName = this._propertyParser.getParameterName();
            String paraValue = this._propertyParser.getParameterValue();
            if (!this.getTextParameter(paraName, paraValue, pm)) {
               throw new InvalidFormatException();
            }
         }

         this._tokenParser.clearEndOfTokenCharacters(";:,=");
         return true;
      } else {
         return false;
      }
   }

   private final boolean getTextParameter(String paraName, String paraValue, PARAMETER pm) {
      if (paraValue == null) {
         throw new InvalidFormatException();
      }

      boolean flag = true;
      if (this._version == 1) {
         if (paraName != null && paraName.equals("LANGUAGE")) {
            pm._language = paraValue;
         } else if (paraName != null && paraName.equals("CHARSET")) {
            pm._charset = paraValue;
         } else {
            int encoding;
            if (isValidEncoding(encoding = paraValue.hashCode())) {
               pm._encoding = encoding;
            } else if (paraName != null && this.isExtensionName(paraName)) {
               pm.setExtension(paraName, paraValue);
            } else {
               flag = false;
            }
         }
      } else {
         if (this._version != 2) {
            throw new InvalidFormatException();
         }

         if (paraName == null) {
            throw new InvalidFormatException();
         }

         if (paraName.equals("VALUE")) {
            if (!paraValue.equals("PTEXT")) {
               throw new InvalidFormatException();
            }
         } else if (paraName.equals("LANGUAGE")) {
            pm._language = paraValue;
         } else {
            int encoding;
            if (paraName.equals("ENCODING") && isValidEncoding(encoding = paraValue.hashCode())) {
               pm._encoding = encoding;
            } else if (this.isExtensionName(paraName)) {
               pm.setExtension(paraName, paraValue);
            } else {
               flag = false;
            }
         }
      }

      return flag;
   }
}
