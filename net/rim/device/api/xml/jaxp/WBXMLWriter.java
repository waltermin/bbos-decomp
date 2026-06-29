package net.rim.device.api.xml.jaxp;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.Vector;
import net.rim.device.api.util.IntHashtable;
import net.rim.device.api.util.ToIntHashtable;
import net.rim.device.api.xml.WBXMLConstants;
import org.xml.sax.Attributes;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.helpers.DefaultHandler;

public class WBXMLWriter extends DefaultHandler implements RIMExtendedHandler, WBXMLConstants {
   private OutputStream _writer;
   private Vector _pendingNamespaces;
   private EntityResolver _resolver;
   private ByteArrayOutputStream _writerBuf;
   private Vector _strTable;
   private ToIntHashtable _strIndex;
   private int _version;
   private int _charset;
   private int _publicid;
   private IntHashtable _tagTable;
   private IntHashtable _attrNameTable;
   private IntHashtable _attrValueTable;
   private int _currentPage;

   public WBXMLWriter(OutputStream out) {
      this._writer = out;
      this._writerBuf = (ByteArrayOutputStream)(new Object());
      this._tagTable = (IntHashtable)(new Object());
      this._attrNameTable = (IntHashtable)(new Object());
      this._attrValueTable = (IntHashtable)(new Object());
      this._strTable = (Vector)(new Object());
      this._strIndex = (ToIntHashtable)(new Object());
      this._publicid = 1;
      this._version = 3;
      this._charset = 106;
      this._currentPage = 0;
   }

   public void setCharset(int charset) {
      switch (charset) {
         case 4:
         case 106:
            this._charset = charset;
            return;
         default:
            throw new Object("Unsupported character set");
      }
   }

   public void setPublicID(int publicID) {
      switch (publicID) {
         case 2:
         case 4:
         case 9:
         case 10:
            this._publicid = publicID;
            return;
         default:
            throw new Object("Unsupported public id");
      }
   }

   public void setVersion(int version) {
      switch (version) {
         case 0:
            throw new Object("Unsupported version");
         case 1:
         case 2:
         case 3:
         default:
            this._version = version;
      }
   }

   public void setTagTable(int page, String[] tagTable) {
      if (page > 0) {
         throw new Object("page>0 unsupported");
      }

      if (tagTable.length >= 59) {
         throw new Object("table too big");
      }

      ToIntHashtable table = (ToIntHashtable)(new Object());

      for (int i = 0; i < tagTable.length; i++) {
         String tag = tagTable[i];
         if (tag != null) {
            table.put(tag, i + 5);
         }
      }

      this._tagTable.put(page, table);
   }

   public void setAttrStartTable(int page, String[] attrStartTable) {
      if (page > 0) {
         throw new Object("page>0 not supported yet");
      }

      if (attrStartTable.length >= 256) {
         throw new Object("table too big");
      }

      ToIntHashtable table = (ToIntHashtable)(new Object());

      for (int i = 0; i < attrStartTable.length; i++) {
         String attrStart = attrStartTable[i];
         if (attrStart != null) {
            table.put(attrStart, i + 5);
         }
      }

      this._attrNameTable.put(page, table);
   }

   public void setAttrValueTable(int page, String[] attrValueTable) {
      if (page > 0) {
         throw new Object("page>0 not supported yet");
      }

      if (attrValueTable.length >= 256) {
         throw new Object("table too big");
      }

      ToIntHashtable table = (ToIntHashtable)(new Object());

      for (int i = 0; i < attrValueTable.length; i++) {
         String attrValue = attrValueTable[i];
         if (attrValue != null) {
            table.put(attrValue, i + 133);
         }
      }

      this._attrValueTable.put(page, table);
   }

   public void setEncoding(String encoding) {
      if (encoding != null) {
         if (encoding.equals("utf-8")) {
            this._charset = 106;
            return;
         }

         if (encoding.equals("iso-8859-1")) {
            this._charset = 4;
            return;
         }
      }

      throw new Object("encoding not supported for WBXML");
   }

   public void setEntityResolver(EntityResolver entityResolver) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   private void write_STR_I(OutputStream os, String s) {
      for (int i = 0; i < s.length(); i++) {
         os.write((byte)s.charAt(i));
      }

      os.write(0);
   }

   private void write_STR_I(OutputStream os, char[] buff, int offset, int count) {
      for (int end = offset + count; offset < end; offset++) {
         os.write((byte)buff[offset]);
      }

      os.write(0);
   }

   private void write_STR_T(OutputStream os, String s) {
      this.write_mb_u_int32(os, this.strTableLookup(s));
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private int strTableLookup(String s) {
      int i = this._strIndex.get(s);
      if (i != -1) {
         return i;
      }

      WBXMLWriter$utf8 u = new WBXMLWriter$utf8();
      if (this._charset == 106) {
         boolean var6 = false /* VF: Semaphore variable */;

         label32:
         try {
            var6 = true;
            u.utf8str = s.getBytes("utf-8");
            var6 = false;
         } finally {
            if (var6) {
               u.utf8str = s.getBytes();
               break label32;
            }
         }
      } else {
         u.utf8str = s.getBytes();
      }

      u.offset = 0;
      if (this._strTable.size() > 0) {
         WBXMLWriter$utf8 last = (WBXMLWriter$utf8)this._strTable.lastElement();
         u.offset = last.offset + last.utf8str.length + 1;
      }

      this._strTable.addElement(u);
      this._strIndex.put(s, u.offset);
      return u.offset;
   }

   private void outputStrTable(OutputStream os) {
      int len = 0;
      int s = this._strTable.size();
      if (s > 0) {
         WBXMLWriter$utf8 last = (WBXMLWriter$utf8)this._strTable.lastElement();
         len = last.offset + last.utf8str.length + 1;
      }

      this.write_mb_u_int32(os, len);

      for (int i = 0; i < s; i++) {
         os.write(((WBXMLWriter$utf8)this._strTable.elementAt(i)).utf8str);
         os.write(0);
      }
   }

   private int tableLookup(IntHashtable table, String str) {
      ToIntHashtable currentTable = (ToIntHashtable)table.get(this._currentPage);
      if (currentTable == null) {
         return 0;
      }

      int i = currentTable.get(str);
      return i == -1 ? 0 : i;
   }

   private void write_mb_u_int32(OutputStream os, int i) {
      byte[] buf = new byte[5];
      int idx = 0;

      do {
         buf[idx++] = (byte)(i & 127);
         i >>= 7;
      } while (i != 0);

      while (idx > 1) {
         os.write(buf[--idx] | 128);
      }

      os.write(buf[0]);
   }

   @Override
   public void startDTD() {
   }

   @Override
   public void endDTD(String name, String publicIdStr, String systemId, String body) {
      if (publicIdStr == null) {
         this._publicid = 10;
      } else {
         if (publicIdStr.startsWith("-//WAPFORUM//DTD WML 1.")) {
            char WMLVersion = publicIdStr.charAt(23);
            switch (WMLVersion) {
               case '/':
                  this._publicid = 10;
                  break;
               case '0':
               default:
                  this._publicid = 2;
                  return;
               case '1':
                  this._publicid = 4;
                  return;
               case '2':
                  this._publicid = 9;
                  return;
            }
         }
      }
   }

   @Override
   public void comment(char[] text, int offset, int length) {
   }

   @Override
   public void cdataSection(char[] text, int offset, int length) {
      this.characters(text, offset, length);
   }

   @Override
   public void startEntityReference(String name, String publicId, String systemId) {
   }

   @Override
   public void endEntityReference(String name) {
   }

   @Override
   public void entityDecl(String name, String value) {
   }

   @Override
   public void defaultAttribute(String element, String attribute, String defaultValue) {
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public void characters(char[] ch, int start, int length) {
      try {
         this._writerBuf.write(3);
         this.write_STR_I(this._writerBuf, ch, start, length);
      } catch (Throwable var6) {
         throw new Object(ioe.getMessage());
      }
   }

   @Override
   public void ignorableWhitespace(char[] ch, int start, int length) {
   }

   @Override
   public void processingInstruction(String target, String data) {
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public void startDocument() {
      try {
         this._writer.write(this._version);
         this._writer.write(this._publicid);
         this._writer.write(this._charset);
      } catch (Throwable var3) {
         throw new Object(ioe.getMessage());
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public void endDocument() {
      try {
         this.outputStrTable(this._writer);
         this._writer.write(this._writerBuf.toByteArray());
         this._writer.flush();
      } catch (Throwable var3) {
         throw new Object(ioe.getMessage());
      }
   }

   private void attribute(String name, String value) {
      int idx = this.tableLookup(this._attrNameTable, name);
      if (idx == 0) {
         this._writerBuf.write(4);
         this.write_STR_T(this._writerBuf, name);
      } else {
         this._writerBuf.write(idx);
      }

      idx = this.tableLookup(this._attrValueTable, value);
      if (idx == 0) {
         this._writerBuf.write(3);
         this.write_STR_I(this._writerBuf, value);
      } else {
         this._writerBuf.write(idx);
      }
   }

   @Override
   public void startAndEndElement(String eUri, String localName, String qName, Attributes attributes) {
      this.element(eUri, localName, qName, attributes, false);
   }

   @Override
   public void startElement(String eUri, String localName, String qName, Attributes attributes) {
      this.element(eUri, localName, qName, attributes, true);
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private void element(String eUri, String localName, String qName, Attributes attributes, boolean hasContent) {
      try {
         boolean hasAttributes = attributes.getLength() != 0 || this._pendingNamespaces != null;
         int idx = this.tableLookup(this._tagTable, qName);
         int str_idx = -1;
         if (idx == 0) {
            idx = 4;
            str_idx = this.strTableLookup(qName);
         }

         if (hasAttributes) {
            idx |= 128;
         }

         if (hasContent) {
            idx |= 64;
         }

         this._writerBuf.write(idx);
         if (str_idx != -1) {
            this.write_mb_u_int32(this._writerBuf, str_idx);
         }

         if (hasAttributes) {
            if (this._pendingNamespaces != null) {
               int len = this._pendingNamespaces.size();

               for (int i = 0; i < len; i += 2) {
                  String prefix = (String)this._pendingNamespaces.elementAt(i);
                  String uri = (String)this._pendingNamespaces.elementAt(i + 1);
                  if (prefix.length() != 0) {
                     this.attribute(((StringBuffer)(new Object("xmlns:"))).append(prefix).toString(), uri);
                  } else {
                     this.attribute("xmlns", uri);
                  }
               }

               this._pendingNamespaces = null;
            }

            int numAttributes = attributes.getLength();

            for (int i = 0; i < numAttributes; i++) {
               this.attribute(attributes.getQName(i), attributes.getValue(i));
            }

            this._writerBuf.write(1);
         }
      } catch (Throwable var14) {
         throw new Object(ioe.getMessage());
      }
   }

   @Override
   public void endElement(String uri, String localName, String qName) {
      this._writerBuf.write(1);
   }

   @Override
   public void startPrefixMapping(String prefix, String uri) {
      if (!prefix.equals(XMLParser.XML) || !uri.equals(XMLParser.XMLURL)) {
         if (this._pendingNamespaces == null) {
            this._pendingNamespaces = (Vector)(new Object());
         }

         this._pendingNamespaces.addElement(prefix);
         this._pendingNamespaces.addElement(uri);
      }
   }

   @Override
   public InputSource resolveEntity(String publicId, String systemId) {
      return this._resolver != null ? this._resolver.resolveEntity(publicId, systemId) : super.resolveEntity(publicId, systemId);
   }
}
