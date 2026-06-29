package net.rim.device.apps.internal.docview.gui;

import net.rim.device.api.ui.XYRect;
import net.rim.device.api.util.IntHashtable;
import net.rim.device.api.util.IntVector;

public final class CommandHandler {
   private static final String COMMAND_CODE = "BBASCMD";
   private static final String COMMAND_DESCRIPTION = "CD";
   private static final String COMMAND_PARAMS = "CP";
   private static final String UNICODE_HINT = "&#x";
   private static final int UNICODE_HINT_LENGTH = 3;
   private static final int NO_UNICODE = 0;
   private static final int ONLY_UNICODE = 1;
   private static final int MIXED_UNICODE = 2;
   private static final int FIND_FWDINSENSITIVE = 0;
   private static final int FIND_FWDSENSITIVE = 1;
   private static final int FIND_BACKINSENSITIVE = 2;
   private static final int FIND_BACKSENSITIVE = 3;
   private static final String XML_STARTCHAR = "<";
   private static final String XML_ENDCHAR = ">";
   private static final String XML_END = "/";
   private static final char XML_STARTCHAR_CH = '<';
   private static final char XML_ENDCHAR_CH = '>';
   static final String XML_UATTRNAME = "U";
   static final String XML_FATTRNAME = "F";
   static final String XML_SATTRNAME = "S";
   static final String XML_LATTRNAME = "L";
   static final String XML_LECATTRNAME = "LEC";
   static final String XML_MATTRNAME = "M";
   static final String ERROR_CODE = "ERRNO";
   static final String PARTINDEX_CODE = "PARTIDX";
   static final String BLOCKINDEX_CODE = "BLOCKIDX";
   static final String TOTALBLOCKS_CODE = "BLOCKCNT";
   static final String DOMID_CODE = "DOMID";
   static final String PWD_CODE = "PWD";
   static final String CHUNKHINT_CODE = "CHINT";
   static final String SRCTYPE_CODE = "SRCTY";
   static final String ARCHIDX_CODE = "AIDX";
   static final String DEVICETYPE_CODE = "DT";
   static final String CHUNKSIZE_CODE = "CS";
   static final String XCHUNKSIZE_CODE = "XCS";
   static final String FIRSTTIMEREQ_CODE = "FTR";
   static final String DOCTYPE_CODE = "FT";
   static final String EMBDOCTYPE_CODE = "EFT";
   static final String DEVIMGTYPE_CODE = "DI";
   static final String XDEVIMGTYPE_CODE = "XDI";
   static final String FINDPATTERN_CODE = "PTN";
   static final String BLOCKRANGE_CODE = "BR";
   static final String FINDINC_CODE = "MV";
   static final String ZIPENTRY_CODE = "FN";
   static final String ZIPNESTEDENTRY_CODE = "NN";
   static final String IMAGEREGIONREQ_CODE = "IRD";
   static final String VERSION_CODE = "VER";
   static final String ARBDOMID_CODE = "SArDOMID";
   static final String ARBDOMIDS_CODE = "SArDOMIDs";
   static final String ZIPSIZE_CODE = "SIZE";
   static final String AUDIOPARAM_CODE = "AF";
   static final String PAGEDOMIDS_CODE = "RDomIds";
   static final String REQCSFORIMAGES_CODE = "DCS";
   private static final String[] _responseTags = new String[]{
      "ERRNO",
      "PARTIDX",
      "BLOCKIDX",
      "BLOCKCNT",
      "SRCTY",
      "DOMID",
      "AIDX",
      "PTN",
      "MV",
      "FT",
      "EFT",
      "IRD",
      "SArDOMID",
      "FTR",
      "SArDOMIDs",
      "RDomIds",
      "DI",
      "XDI",
      "XCS",
      "CS"
   };

   public static final String createClientRequest(ClientRequest request) {
      throw new RuntimeException("cod2jar: field: unresolved slot");
   }

   private static final CommandHandler$EncodeVal encodeForXMLString(String candidateString) {
      int iStringSize = candidateString.length();
      StringBuffer retValue = new StringBuffer(iStringSize + 64);
      boolean isUnicodeChar = false;
      boolean isNormalChar = false;

      for (int i = 0; i < iStringSize; i++) {
         char ch = candidateString.charAt(i);
         switch (ch) {
            case '"':
               retValue.append("&quot;");
               isNormalChar = true;
               break;
            case '&':
               retValue.append("&amp;");
               isNormalChar = true;
               break;
            case '\'':
               retValue.append("&apos;");
               isNormalChar = true;
               break;
            case '<':
               retValue.append("&lt;");
               isNormalChar = true;
               break;
            case '>':
               retValue.append("&gt;");
               isNormalChar = true;
               break;
            default:
               if (ch > 255) {
                  isUnicodeChar = true;
               } else {
                  isNormalChar = true;
               }

               retValue.append(ch);
         }
      }

      if (isUnicodeChar) {
         int iCrtSize = retValue.length();
         StringBuffer processString = new StringBuffer();

         for (int i = 0; i < iCrtSize; i++) {
            char ch = retValue.charAt(i);
            if (ch > 255) {
               if (isNormalChar) {
                  processString.append("&#x");
               }

               processString.append(getXMLCharValue(ch));
            } else {
               processString.append(ch);
            }
         }

         retValue = processString;
      }

      int unicodeType = 0;
      if (isUnicodeChar) {
         unicodeType = isNormalChar ? 2 : 1;
      }

      return new CommandHandler$EncodeVal(retValue.toString(), unicodeType);
   }

   private static final String decodeFromXMLString(String candidateString, int unicodeVersion) {
      if (unicodeVersion != 0 && unicodeVersion != 1 && unicodeVersion != 2) {
         throw new IllegalArgumentException();
      }

      String retValue = candidateString;
      if (unicodeVersion == 0 || unicodeVersion == 2) {
         retValue = replaceString(retValue, "&amp;", '&');
         retValue = replaceString(retValue, "&lt;", '<');
         retValue = replaceString(retValue, "&gt;", '>');
         retValue = replaceString(retValue, "&apos;", '\\');
         retValue = replaceString(retValue, "&quot;", '"');
      }

      if (unicodeVersion == 1) {
         int groups = retValue.length() / 4;
         int incompleteGroups = retValue.length() % 4 == 2 ? 1 : 0;
         StringBuffer value = new StringBuffer();
         byte[] bytes = retValue.getBytes();

         for (int i = 0; i < groups + incompleteGroups; i++) {
            int byte1 = getNextByteFromHexCharBuffer(bytes, i * 4);
            int crtChar = byte1 << 8;
            if (incompleteGroups == 0 || i < groups + incompleteGroups - 1) {
               int byte2 = getNextByteFromHexCharBuffer(bytes, i * 4 + 2);
               crtChar += byte2;
            }

            value.append((char)crtChar);
         }

         return value.toString();
      } else {
         if (unicodeVersion == 2) {
            int unicodeCharStart = retValue.indexOf("&#x");
            if (unicodeCharStart != -1) {
               StringBuffer processBuffer = new StringBuffer(retValue);

               while (unicodeCharStart != -1) {
                  byte[] bytes = retValue.getBytes();
                  int byte1 = getNextByteFromHexCharBuffer(bytes, unicodeCharStart + 3);
                  int crtChar = byte1 << 8;
                  int byte2 = getNextByteFromHexCharBuffer(bytes, unicodeCharStart + 3 + 2);
                  crtChar += byte2;
                  processBuffer.delete(unicodeCharStart, unicodeCharStart + 3 + 4);
                  processBuffer.insert(unicodeCharStart, (char)crtChar);
                  retValue = processBuffer.toString();
                  unicodeCharStart = retValue.indexOf("&#x", unicodeCharStart + 1);
               }

               Object var16 = null;
            }
         }

         return retValue;
      }
   }

   static final String getStringRect(XYRect rect) {
      return rect.x + "x" + rect.y + "x" + rect.width + "x" + rect.height;
   }

   static final String readTagValue(String xmlString, String tag) {
      String strTag = "<" + tag + ">";
      int cmdStart = xmlString.indexOf(strTag);
      if (cmdStart != -1) {
         int cmdEnd = xmlString.indexOf("</" + tag + ">");
         if (cmdEnd > cmdStart + strTag.length()) {
            return xmlString.substring(cmdStart + strTag.length(), cmdEnd);
         } else {
            throw new IllegalArgumentException();
         }
      } else {
         return null;
      }
   }

   private static final String getStringAudioFormat(int serverCodec) {
      StringBuffer audioFormat = new StringBuffer(String.valueOf(serverCodec));
      switch (serverCodec) {
         case 0:
         case 49:
            audioFormat.append("x" + String.valueOf(1) + "x" + 8000 + "x" + 0);
            break;
         case 85:
            audioFormat.append("x" + String.valueOf(1) + "x" + 16000 + "x" + 0 + "x" + 16000);
      }

      return audioFormat.toString();
   }

   public static final ServerResponse decodeServerResponse(String responseString) {
      ServerResponse response = new ServerResponse();
      boolean setArbDomIDStartChunkAsCurrentBlockIndex = false;

      for (int i = 0; i < _responseTags.length; i++) {
         if (_responseTags[i].compareTo("SArDOMID") != 0) {
            if (_responseTags[i].compareTo("SArDOMIDs") != 0) {
               if (_responseTags[i].compareTo("PTN") != 0) {
                  String value = readTagValue(responseString, _responseTags[i]);
                  if (value != null) {
                     response.setParam(_responseTags[i], value);
                  }
               } else {
                  String strTag = "<" + _responseTags[i];
                  int cmdStart = responseString.indexOf(strTag);
                  if (cmdStart != -1) {
                     int cmdEnd = responseString.indexOf("</" + _responseTags[i] + ">");
                     if (cmdEnd <= cmdStart + strTag.length()) {
                        throw new IllegalArgumentException();
                     }

                     int cmdStartEnd = responseString.indexOf(62, cmdStart);
                     String paramValue = responseString.substring(cmdStart + strTag.length(), cmdStartEnd);
                     String uValueStr = getAttributeValue(paramValue, "U");
                     int uValue = uValueStr == null ? 0 : Integer.parseInt(uValueStr);
                     String fValue = getAttributeValue(paramValue, "F");
                     if (fValue != null) {
                        response.setParam("F", fValue);
                     }

                     response.setParam(_responseTags[i], decodeFromXMLString(responseString.substring(cmdStartEnd + 1, cmdEnd), uValue));
                  }
               }
            } else {
               String strTag = "<" + _responseTags[i];
               int cmdStart = responseString.indexOf(strTag);
               if (cmdStart != -1) {
                  int cmdEnd = responseString.indexOf("</" + _responseTags[i] + ">");
                  if (cmdEnd <= cmdStart + strTag.length()) {
                     throw new IllegalArgumentException();
                  }

                  int cmdStartEnd = responseString.indexOf(62, cmdStart);
                  response.setParam(_responseTags[i], responseString.substring(cmdStartEnd + 1, cmdEnd));
                  String paramValue = responseString.substring(cmdStart + strTag.length(), cmdStartEnd);
                  String lecValue = getAttributeValue(paramValue, "LEC");
                  response.setParam("LEC", lecValue == null ? String.valueOf(-2) : lecValue);
               }
            }
         } else {
            String strTag = "<" + _responseTags[i] + ' ';
            int cmdStart = responseString.indexOf(strTag);
            if (cmdStart == -1) {
               String value = readTagValue(responseString, _responseTags[i]);
               if (value != null) {
                  setArbDomIDStartChunkAsCurrentBlockIndex = true;
                  response.setParam("L", String.valueOf(-2));
                  response.setParam(_responseTags[i], value);
                  continue;
               }
            }

            if (cmdStart != -1) {
               int cmdEnd = responseString.indexOf("</" + _responseTags[i] + ">");
               if (cmdEnd <= cmdStart + strTag.length()) {
                  throw new IllegalArgumentException();
               }

               int cmdStartEnd = responseString.indexOf(62, cmdStart);
               String paramValue = responseString.substring(cmdStart + strTag.length(), cmdStartEnd);
               String sValue = getAttributeValue(paramValue, "S");
               if (sValue != null) {
                  String lValue = getAttributeValue(paramValue, "L");
                  response.setParam("S", sValue);
                  response.setParam("L", lValue == null ? String.valueOf(-2) : lValue);
               }

               response.setParam(_responseTags[i], responseString.substring(cmdStartEnd + 1, cmdEnd));
            }
         }
      }

      if (setArbDomIDStartChunkAsCurrentBlockIndex && response._crtBlockIndex != -1) {
         response.setParam("S", String.valueOf(response._crtBlockIndex));
      }

      if (response._chunkSize > 0) {
         response._xcsChunkSize = 0;
      }

      return response;
   }

   public static final IntHashtable decodeArchiveContents(byte[] data) {
      String responseData = new String(data);
      String nestedTagString = "<NN>";
      String nestedEndTagString = "</NN>";
      IntHashtable retHash = new IntHashtable();
      IntVector nestedStartIndex = null;
      IntVector nestedEndIndex = null;
      int nestedStart = responseData.indexOf(nestedTagString);

      while (nestedStart != -1) {
         int nestedEnd = responseData.indexOf(nestedEndTagString, nestedStart + nestedTagString.length());
         if (nestedEnd == -1) {
            return retHash;
         }

         int levels = 0;

         for (int nextNestedStart = responseData.indexOf(nestedTagString, nestedStart + nestedTagString.length());
            nextNestedStart != -1 && nextNestedStart < nestedEnd;
            nextNestedStart = responseData.indexOf(nestedTagString, nextNestedStart + nestedTagString.length())
         ) {
            levels++;
         }

         for (int i = 0; i < levels; i++) {
            nestedEnd = responseData.indexOf(nestedEndTagString, nestedEnd + nestedEndTagString.length());
            if (nestedEnd == -1) {
               return retHash;
            }
         }

         if (nestedStartIndex == null) {
            nestedStartIndex = new IntVector();
         }

         if (nestedEndIndex == null) {
            nestedEndIndex = new IntVector();
         }

         nestedStartIndex.addElement(nestedStart);
         nestedEndIndex.addElement(nestedEnd + nestedEndTagString.length());
         nestedStart = responseData.indexOf(nestedTagString, nestedEnd + nestedEndTagString.length());
      }

      try {
         String startTagString = "<FN";
         String endTagString = "</FN>";

         for (int startTag = responseData.indexOf(startTagString); startTag != -1; startTag = responseData.indexOf(startTagString, startTag + 1)) {
            if (!isIndexInRegion(nestedStartIndex, nestedEndIndex, startTag)) {
               int endTag = responseData.indexOf(endTagString, startTag);
               if (endTag != -1) {
                  int endIndex = responseData.indexOf(62, startTag + startTagString.length());
                  if (endIndex != -1) {
                     try {
                        String uValueStr = getAttributeValue(responseData.substring(startTag + startTagString.length(), endIndex), "U");
                        int uValue = uValueStr == null ? 0 : Integer.parseInt(uValueStr);
                        retHash.put(
                           retHash.size() + getSkippedNestedRegions(nestedStartIndex, nestedEndIndex, startTag),
                           decodeFromXMLString(responseData.substring(endIndex + 1, endTag), uValue)
                        );
                     } finally {
                        continue;
                     }
                  }
               }
            }
         }
      } finally {
         return retHash;
      }

      return retHash;
   }

   private static final boolean isIndexInRegion(IntVector startOffset, IntVector endOffset, int index) {
      if (startOffset != null && endOffset != null) {
         int regions = startOffset.size();
         if (endOffset.size() != regions) {
            throw new IllegalArgumentException();
         }

         for (int i = 0; i < regions; i++) {
            if (index < startOffset.elementAt(i)) {
               return false;
            }

            if (index >= startOffset.elementAt(i) && index < endOffset.elementAt(i)) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   private static final int getSkippedNestedRegions(IntVector startOffset, IntVector endOffset, int index) {
      int retValue = 0;
      if (startOffset != null && endOffset != null) {
         int regions = startOffset.size();
         if (endOffset.size() != regions) {
            throw new IllegalArgumentException();
         }

         for (int i = 0; i < regions; i++) {
            if (index < startOffset.elementAt(i)) {
               return retValue;
            }

            if (index >= startOffset.elementAt(i) && index >= endOffset.elementAt(i)) {
               retValue++;
            }
         }

         return retValue;
      } else {
         return retValue;
      }
   }

   private static final String getAttributeValue(String tagString, String XMLAttribute) {
      String retValue = null;
      String lookForAttr = XMLAttribute + '=' + "\"";
      int findAttrStart = tagString.indexOf(lookForAttr);
      if (findAttrStart != -1) {
         int findAttrEnd = tagString.indexOf("\"", findAttrStart + lookForAttr.length());
         if (findAttrEnd != -1) {
            retValue = tagString.substring(findAttrStart + lookForAttr.length(), findAttrEnd);
         }
      }

      return retValue;
   }

   private static final String replaceString(String inputString, String pattern, char replaceChar) {
      int crtStart = inputString.indexOf(pattern);
      if (crtStart == -1) {
         return inputString;
      }

      StringBuffer retValue = new StringBuffer(inputString.length());

      int prevStart;
      for (prevStart = 0; crtStart != -1; crtStart = inputString.indexOf(pattern, prevStart)) {
         retValue.append(inputString.substring(prevStart, crtStart));
         retValue.append(replaceChar);
         prevStart = crtStart + pattern.length();
      }

      retValue.append(inputString.substring(prevStart));
      return retValue.toString();
   }

   private static final void addFindPatternXMLData(StringBuffer buffer, String findPattern, boolean findNext, boolean findCaseSensitive) {
      if (findPattern != null) {
         int searchCode = 0;
         if (findCaseSensitive) {
            searchCode = findNext ? 1 : 3;
         } else if (!findNext) {
            searchCode = 2;
         }

         addXMLData(buffer, "PTN", searchCode, false, findPattern);
      }
   }

   private static final void addXMLData(StringBuffer buffer, String strCode, String strValue) {
      addXMLData(buffer, strCode, 0, false, strValue);
   }

   private static final void addXMLData(StringBuffer buffer, String strCode, int searchCode, boolean addMultipleRenderRequest, String strValue) {
      if (strValue != null) {
         CommandHandler$EncodeVal val = encodeForXMLString(strValue);
         buffer.append('<');
         buffer.append(strCode);
         if (val._unicodeType != 0) {
            buffer.append(" U=\"" + String.valueOf(val._unicodeType) + '"');
         }

         if (searchCode != 0) {
            buffer.append(" F=\"" + String.valueOf(searchCode) + '"');
         }

         if (addMultipleRenderRequest) {
            buffer.append(" M=\"1\"");
         }

         buffer.append('>');
         buffer.append(val._stringVal);
         buffer.append('<');
         buffer.append("/");
         buffer.append(strCode);
         buffer.append('>');
      }
   }

   private static final byte encodeNibble(byte ch) {
      if (Character.isDigit((char)ch)) {
         return (byte)(ch - 48);
      } else if (ch >= 97) {
         return (byte)(ch - 97 + 10);
      } else {
         return ch >= 65 ? (byte)(ch - 65 + 10) : 0;
      }
   }

   private static final int getNextByteFromHexCharBuffer(byte[] inputBytes, int index) {
      return (encodeNibble(inputBytes[index]) << 4) + encodeNibble(inputBytes[index + 1]);
   }

   private static final String getXMLCharValue(char chInputChar) {
      String retValue = "";
      retValue = retValue + Integer.toHexString(chInputChar >>> '\f');
      retValue = retValue + Integer.toHexString(chInputChar >>> '\b' & 15);
      retValue = retValue + Integer.toHexString(chInputChar >>> 4 & 15);
      return retValue + Integer.toHexString(chInputChar & 15);
   }
}
