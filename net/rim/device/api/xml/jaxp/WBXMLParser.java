package net.rim.device.api.xml.jaxp;

import java.io.InputStream;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import net.rim.device.api.util.IntHashtable;
import net.rim.device.api.xml.WBXMLConstants;
import org.xml.sax.Locator;
import org.xml.sax.helpers.DefaultHandler;

public class WBXMLParser implements WBXMLConstants, Locator {
   private String _enc;
   private InputStream _is;
   private int _version;
   private int _publicid;
   private String _publicidStr;
   private int _charset;
   private byte[] _strtbl;
   private IntHashtable _strtblTable;
   private IntHashtable _tagTable;
   private IntHashtable _attrStartTable;
   private IntHashtable _attrValueTable;
   private int _currentTagPage;
   private int _currentAttrPage;
   private Vector _namespaceMap;
   private Hashtable _currentNamespaceMapping;
   private RIMExtendedHandler _RIMExtendedHandler;
   private RIMWBXMLHandler _RIMWBXMLHandler;
   private RIMOpaqueDataHandler _RIMOpaqueDataHandler;
   private DefaultHandler _defaultHandler;
   private char[] _strBuffer;
   private int _strBufferSectionSize;
   private int _strBufferLen;
   SAXAttributesImpl _emptyAttributes = new SAXAttributesImpl();
   private int _pushBackBuffer;
   private boolean _pushBackBufferValid;
   private boolean _havePeek;
   private int _peek;
   private static final String WAPFORUM_DTD = "-//WAPFORUM//DTD ";
   private static final String[] PUBLIC_ID = new String[]{
      "WML 1.0//EN",
      "WTA 1.0//EN",
      "WML 1.1//EN",
      "SI 1.0//EN",
      "SL 1.0//EN",
      "CO 1.0//EN",
      "CHANNEL 1.1//EN",
      "WML 1.2//EN",
      "WML 1.3//EN",
      "PROV 1.0//EN",
      "WTA-WML 1.2//EN",
      "CHANNEL 1.2//EN"
   };
   private static final int _MAX_OPAQUE_SIZE = 60;
   private static final String _CDATA = "CDATA";
   private static final String emptyString = "";

   public void setTagTable(IntHashtable tagTables) {
      this._tagTable = tagTables;
   }

   public void setAttrStartTable(IntHashtable attrStartTables) {
      this._attrStartTable = attrStartTables;
   }

   public void setAttrValueTable(IntHashtable attrValueTables) {
      this._attrValueTable = attrValueTables;
   }

   public void setTagTable(int page, IntHashtable tagTable) {
      this._tagTable.put(page, tagTable);
   }

   public void setAttrStartTable(int page, IntHashtable attrStartTable) {
      this._attrStartTable.put(page, attrStartTable);
   }

   public void setAttrValueTable(int page, IntHashtable attrValueTable) {
      this._attrValueTable.put(page, attrValueTable);
   }

   public void setTagTable(int page, String[] tagTable) {
      this._tagTable.put(page, tokenArrayToIntHashtable(tagTable, 5));
   }

   public void setAttrStartTable(int page, String[] attrStartTable) {
      this._attrStartTable.put(page, tokenArrayToIntHashtable(attrStartTable, 5));
   }

   public void setAttrValueTable(int page, String[] attrValueTable) {
      this._attrValueTable.put(page, tokenArrayToIntHashtable(attrValueTable, 133));
   }

   public void parse(DefaultHandler dh) {
      this._defaultHandler = dh;
      if (dh instanceof RIMWBXMLHandler) {
         this._RIMWBXMLHandler = (RIMWBXMLHandler)dh;
      }

      if (dh instanceof RIMExtendedHandler) {
         this._RIMExtendedHandler = (RIMExtendedHandler)dh;
      }

      if (dh instanceof RIMOpaqueDataHandler) {
         this._RIMOpaqueDataHandler = (RIMOpaqueDataHandler)dh;
      }

      this._defaultHandler.setDocumentLocator(this);
      this._defaultHandler.startDocument();
      this.parse_body();
      this._defaultHandler.endDocument();
   }

   public int getVersion() {
      return this._version;
   }

   public int getCharset() {
      return this._charset;
   }

   @Override
   public String getSystemId() {
      return null;
   }

   @Override
   public int getLineNumber() {
      return -1;
   }

   @Override
   public int getColumnNumber() {
      return -1;
   }

   @Override
   public String getPublicId() {
      if (this._publicid == 0) {
         return this._publicidStr;
      } else {
         return this._publicid >= 2 && this._publicid <= 13
            ? ((StringBuffer)(new Object("-//WAPFORUM//DTD "))).append(PUBLIC_ID[this._publicid - 2]).toString()
            : null;
      }
   }

   private byte[] parseOpaque() {
      int readahead = this.read();
      if (readahead != 195) {
         throw new Object("Error parsing opaque data");
      }

      int length = this.read_mb_u_int32();
      byte[] opaqueData = new byte[length];
      int lenToRead = length;

      while (lenToRead > 0) {
         int bytesRead = this._is.read(opaqueData, length - lenToRead, lenToRead);
         if (bytesRead == -1) {
            throw new Object("Error receiving opaque data");
         }

         lenToRead -= bytesRead;
      }

      return opaqueData;
   }

   private void parseContent() {
      int readahead = this.read();
      if (readahead == 0) {
         this._currentTagPage = this.read();
         readahead = this.read();
      }

      switch (readahead) {
         case 0:
         case 64:
         case 65:
         case 66:
         case 128:
         case 129:
         case 130:
         case 192:
         case 193:
         case 194:
            if (readahead == 0) {
               this._currentTagPage = this.read();
               readahead = this.read();
            }

            switch (readahead) {
               case 64:
               case 65:
               case 66:
                  this.read_STR_I();
                  this._defaultHandler.characters(this._strBuffer, 0, this._strBufferLen);
                  return;
               case 128:
               case 129:
               case 130:
                  String s = String.valueOf(this.read_mb_u_int32());
                  this._defaultHandler.characters(s.toCharArray(), 0, s.length());
                  return;
               case 192:
               case 193:
               case 194:
                  this._strBuffer[0] = (char)this.read();
                  this._strBufferLen = 1;
                  this._defaultHandler.characters(this._strBuffer, 0, 1);
                  return;
               default:
                  throw new Object("Error parsing extension");
            }
         case 2:
            this._strBuffer[0] = (char)this.read_mb_u_int32();
            this._strBufferLen = 1;
            this._defaultHandler.characters(this._strBuffer, 0, 1);
            return;
         case 3:
         case 131:
            if (readahead == 131) {
               this.read_STR_T_toStrBuffer();
            } else {
               this.read_STR_I();
            }

            this._defaultHandler.characters(this._strBuffer, 0, this._strBufferLen);
            return;
         case 67:
            this.unread(readahead);
            this.parse_pi();
            return;
         case 195:
            this.unread(readahead);
            byte[] opaque = this.parseOpaque();
            if (this._RIMOpaqueDataHandler != null) {
               this._RIMOpaqueDataHandler.opaqueData(opaque, 0, opaque.length);
               return;
            }
            break;
         default:
            this.unread(readahead);
            this.parseElement();
      }
   }

   private void parseAttribute(SAXAttributesImpl ai) {
      this._strBufferLen = 0;
      int attrStart = this.read();
      if (attrStart >= 128) {
         throw new Object("Error parsing attrStart");
      }

      if (attrStart == 0) {
         this._currentAttrPage = this.read();
         attrStart = this.read();
      }

      String name = this.IdToString(this._attrStartTable, attrStart, this._currentAttrPage);
      int split = name.indexOf(61);
      if (split != -1) {
         this.appendToStrBuffer(name, split + 1, name.length());
         name = name.substring(0, split);
      }

      split = this.read();
      byte[][] opaqueData = (byte[][])null;

      while (
         split == 0
            || split > 128
            || split == 3
            || split == 131
            || split == 64
            || split == 65
            || split == 66
            || split == 128
            || split == 129
            || split == 130
            || split == 192
            || split == 193
            || split == 194
            || split == 2
            || split == 195
      ) {
         switch (split) {
            case 2:
               this.append_CHAR((char)this.read_mb_u_int32());
               break;
            case 3:
               this.append_STR_I();
               break;
            case 131:
               this.append_STR_T_toStrBuffer();
               break;
            case 195:
               this.unread(split);
               byte[] opaque = this.parseOpaque();
               if (opaqueData == null) {
                  opaqueData = new byte[1][];
               } else {
                  ArrayResize.byteArrayArrayResize(opaqueData, opaqueData.length + 1);
               }

               opaqueData[opaqueData.length - 1] = opaque;
               break;
            default:
               if (split == 0) {
                  this._currentAttrPage = this.read();
                  split = this.read();
               }

               switch (split) {
                  case 64:
                  case 65:
                  case 66:
                     this.append_STR_I();
                     break;
                  case 128:
                  case 129:
                  case 130:
                     String v = String.valueOf(this.read_mb_u_int32());
                     this.appendToStrBuffer(v, 0, v.length());
                     break;
                  case 192:
                  case 193:
                  case 194:
                     this.append_CHAR((char)this.read());
                     break;
                  default:
                     String s = this.IdToString(this._attrValueTable, split, this._currentAttrPage);
                     this.appendToStrBuffer(s, 0, s.length());
               }
         }

         split = this.read();
      }

      String val = (String)(new Object(this._strBuffer, 0, this._strBufferLen));
      boolean isNamespace = false;
      if (name.startsWith("xmlns")) {
         isNamespace = true;
         String prefix = "";
         int colonns = name.indexOf(58);
         if (colonns != -1) {
            prefix = name.substring(colonns + 1);
         }

         this._defaultHandler.startPrefixMapping(prefix, val);
         if (this._currentNamespaceMapping == null) {
            this._currentNamespaceMapping = (Hashtable)(new Object());
            this._namespaceMap.addElement(this._currentNamespaceMapping);
         }

         this._currentNamespaceMapping.put(prefix, val);
      }

      String nsuri = "";
      int colon = name.indexOf(58);
      String localName = name;
      if (colon != -1) {
         String prefix = name.substring(0, colon);

         label133:
         try {
            nsuri = this.searchNamespaceURI(prefix);
            localName = name.substring(colon + 1);
         } finally {
            break label133;
         }
      }

      if (!isNamespace) {
         ai.addAttribute(nsuri, localName, name, "CDATA", val, false, opaqueData);
      }

      this.unread(split);
   }

   private String IdToString(IntHashtable table, int id, int currentPage) {
      if ((id & 127) == 4) {
         return this.read_STR_T();
      }

      IntHashtable a = null;
      if (id >= 4 && table != null && (a = (IntHashtable)table.get(currentPage)) != null) {
         String s = (String)a.get(id);
         if (s == null) {
            throw new Object("Element does not exist in tag table");
         } else {
            return s;
         }
      } else {
         throw new Object("Page fault accessing tag table");
      }
   }

   public WBXMLParser(InputStream is) {
      this(is, -1);
   }

   private void parseElement() {
      int namespaceSize = this._namespaceMap.size();
      int stag = this.read();
      if (stag == 0) {
         this._currentTagPage = this.read();
         stag = this.read();
      }

      boolean attributesToFollow = (stag & 128) == 128;
      boolean containContent = (stag & 64) == 64;
      String tag = this.IdToString(this._tagTable, stag & 63, this._currentTagPage);
      SAXAttributesImpl ai = this._emptyAttributes;
      if (attributesToFollow) {
         this._currentNamespaceMapping = null;
         ai = new SAXAttributesImpl();

         for (int readahead = this.read(); readahead != 1; readahead = this.read()) {
            this.unread(readahead);
            this.parseAttribute(ai);
         }

         this._currentNamespaceMapping = null;
      }

      String nameSpaceURI = "";
      String localName = tag;
      int colon = tag.indexOf(58);
      if (colon != -1) {
         String nameSpacePrefix = tag.substring(0, colon);

         label117:
         try {
            nameSpaceURI = this.searchNamespaceURI(nameSpacePrefix);
            localName = tag.substring(colon + 1);
         } finally {
            break label117;
         }
      }

      int code = this._currentTagPage << 16 | stag & 63;
      if (containContent) {
         if (this._RIMWBXMLHandler != null) {
            this._RIMWBXMLHandler.startElement(code, nameSpaceURI, localName, tag, ai);
         } else {
            this._defaultHandler.startElement(nameSpaceURI, localName, tag, ai);
         }

         for (int readahead = this.read(); readahead != 1; readahead = this.read()) {
            this.unread(readahead);
            this.parseContent();
         }

         if (this._RIMWBXMLHandler != null) {
            this._RIMWBXMLHandler.endElement(code, nameSpaceURI, localName, tag);
         } else {
            this._defaultHandler.endElement(nameSpaceURI, localName, tag);
         }
      } else if (this._RIMWBXMLHandler != null) {
         this._RIMWBXMLHandler.startElement(code, nameSpaceURI, localName, tag, ai);
         this._RIMWBXMLHandler.endElement(code, nameSpaceURI, localName, tag);
      } else if (this._RIMExtendedHandler != null) {
         this._RIMExtendedHandler.startAndEndElement(nameSpaceURI, localName, tag, ai);
      } else {
         this._defaultHandler.startElement(nameSpaceURI, localName, tag, ai);
         this._defaultHandler.endElement(nameSpaceURI, localName, tag);
      }

      if (namespaceSize != this._namespaceMap.size()) {
         Hashtable mapping = (Hashtable)this._namespaceMap.lastElement();
         Enumeration e = mapping.keys();

         while (e.hasMoreElements()) {
            this._defaultHandler.endPrefixMapping((String)e.nextElement());
         }

         this._namespaceMap.removeElementAt(this._namespaceMap.size() - 1);
      }
   }

   WBXMLParser(InputStream is, int firstChar) {
      this._strBuffer = new char[0];
      this._strBufferSectionSize = ArrayResize.getSectionSize(this._strBuffer);
      this._strBuffer = ArrayResize.charArrayResize(this._strBuffer, this._strBufferSectionSize);
      this._strBufferLen = 0;
      this._strtblTable = (IntHashtable)(new Object());
      this._tagTable = (IntHashtable)(new Object());
      this._attrStartTable = (IntHashtable)(new Object());
      this._attrValueTable = (IntHashtable)(new Object());
      this._is = is;
      if (firstChar == -1) {
         this._version = this.read();
      } else {
         this._version = firstChar;
      }

      int readahead = this.read();
      int idIndex = 0;
      if (readahead != 0) {
         this.unread(readahead);
         this._publicid = this.read_mb_u_int32();
      } else {
         this._publicid = 0;
         idIndex = this.read_mb_u_int32();
      }

      this._charset = this.read_mb_u_int32();
      if (this._version >= 1 && this._version <= 3) {
         switch (this._charset) {
            case 106:
               this._enc = "utf-8";
               break;
            default:
               this._enc = "iso-8859-1";
         }

         this._strtbl = new byte[this.read_mb_u_int32()];
         is.read(this._strtbl);
         if (this._publicid == 0) {
            this._publicidStr = this.read_STR_T(idIndex);
         }

         this._namespaceMap = (Vector)(new Object());
         this._currentNamespaceMapping = null;
      } else {
         throw new Object("Unknown wbxml version");
      }
   }

   private void parse_body() {
      int b;
      for (b = this.read(); b == 67; b = this.read()) {
         this.unread(b);
         this.parse_pi();
      }

      if (b == -1) {
         throw new Object("Unexpected end of stream");
      }

      this.unread(b);
      this.parseElement();

      for (b = this.read(); b == 67; b = this.read()) {
         this.unread(b);
         this.parse_pi();
      }

      if (b != -1) {
         throw new Object("Unexpected token at parse_body");
      }
   }

   private void parse_pi() {
      int readahead = this.read();
      if (readahead != 67) {
         throw new Object("Not PI element..");
      }

      SAXAttributesImpl ai = new SAXAttributesImpl();
      readahead = this.read();

      for (this._currentNamespaceMapping = null; readahead != 1; readahead = this.read()) {
         this.unread(readahead);
         this.parseAttribute(ai);
      }

      if (ai.getLength() != 1) {
         throw new Object("Error parsing PI element");
      }

      this._defaultHandler.processingInstruction(ai.getQName(0), ai.getValue(0));
      if (this._currentNamespaceMapping != null) {
         this._namespaceMap.removeElementAt(this._namespaceMap.size() - 1);
         this._currentNamespaceMapping = null;
      }
   }

   private static IntHashtable tokenArrayToIntHashtable(String[] table, int startToken) {
      if (table == null) {
         return (IntHashtable)(new Object(1));
      }

      int len = table.length;
      IntHashtable tbl = (IntHashtable)(new Object(len + (len >> 1)));

      for (int i = 0; i < len; i++) {
         tbl.put(i + startToken, table[i]);
      }

      return tbl;
   }

   private int read_mb_u_int32() {
      boolean cont = true;
      int out = 0;

      while (cont) {
         int b = this.read();
         cont = (b & 128) == 128;
         out <<= 7;
         out |= b & 127;
      }

      return out;
   }

   private void append_STR_I() {
      int i;
      while ((i = this.readNextCharFromUTF8()) > 0) {
         this.append_CHAR((char)i);
      }

      if (i != 0) {
         throw new Object("Unexpected end of stream", null);
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private void append_CHAR(char i) {
      boolean var5 = false /* VF: Semaphore variable */;

      label17:
      try {
         var5 = true;
         this._strBuffer[this._strBufferLen] = i;
         var5 = false;
      } finally {
         if (var5) {
            int length = this._strBuffer.length;
            this._strBuffer = ArrayResize.charArrayResize(this._strBuffer, length + this._strBufferSectionSize);
            this._strBuffer[this._strBufferLen] = i;
            break label17;
         }
      }

      this._strBufferLen++;
   }

   private void read_STR_I() {
      this._strBufferLen = 0;
      this.append_STR_I();
   }

   private void read_STR_T_toStrBuffer() {
      this._strBufferLen = 0;
      this.append_STR_T_toStrBuffer();
   }

   private void append_STR_T_toStrBuffer() {
      String s = this.read_STR_T(this.read_mb_u_int32());
      this.appendToStrBuffer(s, 0, s.length());
   }

   private String read_STR_T() {
      return this.read_STR_T(this.read_mb_u_int32());
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private String read_STR_T(int offset) {
      String s = (String)this._strtblTable.get(offset);
      if (s != null) {
         return s;
      }

      int len = 0;

      while (true) {
         boolean var9 = false /* VF: Semaphore variable */;

         try {
            var9 = true;
            if (this._strtbl[offset + len] == 0) {
               var9 = false;
               break;
            }

            len++;
         } finally {
            if (var9) {
               throw new Object("Error parsing STR_T element", null);
            }
         }
      }

      try {
         s = (String)(new Object(this._strtbl, offset, len, this._enc));
         this._strtblTable.put(offset, s);
         return s;
      } finally {
         throw new Object("Error encoding string", null);
      }
   }

   private String searchNamespaceURI(String prefix) {
      int size = this._namespaceMap.size();

      for (int i = size - 1; i >= 0; i--) {
         String uri = (String)((Hashtable)this._namespaceMap.elementAt(i)).get(prefix);
         if (uri != null) {
            return uri;
         }
      }

      throw new Object();
   }

   private int read() {
      if (this._pushBackBufferValid) {
         this._pushBackBufferValid = false;
         return this._pushBackBuffer;
      } else {
         return this._is.read();
      }
   }

   private void unread(int b) {
      if (this._pushBackBufferValid) {
         throw new Object("Error unread");
      }

      this._pushBackBuffer = b;
      this._pushBackBufferValid = true;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private void appendToStrBuffer(String str, int srcBegin, int srcEnd) {
      int len = srcEnd - srcBegin;
      boolean var8 = false /* VF: Semaphore variable */;

      label20:
      try {
         var8 = true;
         str.getChars(srcBegin, srcEnd, this._strBuffer, this._strBufferLen);
         var8 = false;
      } finally {
         if (var8) {
            int newlen = ArrayResize.roundToSectionSize(this._strBuffer.length + len, this._strBufferSectionSize);
            this._strBuffer = ArrayResize.charArrayResize(this._strBuffer, newlen + this._strBufferSectionSize);
            str.getChars(srcBegin, srcEnd, this._strBuffer, this._strBufferLen);
            break label20;
         }
      }

      this._strBufferLen += len;
   }

   private int readNextCharFromUTF8() {
      if (this._havePeek) {
         this._havePeek = false;
         return this._peek;
      }

      int c0 = this.read();
      if (c0 <= 127) {
         return c0;
      }

      switch (c0 & 248) {
         case 192:
         case 200:
         case 208:
         case 216: {
            int c1 = this.read();
            if ((c1 & 192) == 128) {
               return ((c0 & 31) << 6) + (c1 & 63);
            }
            break;
         }
         case 224:
         case 232:
         case 248: {
            int c1 = this.read();
            if ((c1 & 192) == 128) {
               int c2x = this.read();
               if ((c2x & 192) == 128) {
                  return ((c0 & 15) << 12) + ((c1 & 63) << 6) + (c2x & 63);
               }
            }
            break;
         }
         case 240: {
            c0 &= 7;
            int c1 = this.read() & 63;
            int c2 = this.read() & 63;
            int z = this.read() & 63;
            int v = (c0 << 2) + (c1 >> 4) - 1;
            int w = c1 & 15;
            int x = c2 >> 4;
            int y = c2 & 15;
            this._peek = (char)(56320 + (y << 6) + z);
            this._havePeek = true;
            return (char)(55296 + (v << 6) + (w << 2) + x);
         }
      }

      throw new Object("UTF-8 Error", null);
   }
}
