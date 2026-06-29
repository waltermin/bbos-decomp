package net.rim.device.api.xml.jaxp;

import java.io.OutputStream;
import java.util.Vector;
import org.xml.sax.Attributes;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.helpers.DefaultHandler;

public class XMLWriter extends DefaultHandler implements RIMExtendedHandler {
   private OutputStream _out;
   private Vector _pendingNamespaces;
   private int _supressOutput;
   private int _outputStyle = 2;
   private char _lastPrinted;
   private EntityResolver _resolver;
   private boolean _expandingEntities = false;
   private RIMExtendedAttributes _noExtendedAttributes = new XMLWriter$1(this);
   private static final int PRETTY_PRINT;
   private static final int COMPRESSED;
   private static final int PRESERVE;

   public XMLWriter(OutputStream out) {
      this._out = out;
   }

   public void setPrettyPrint() {
      this._outputStyle = 0;
   }

   public void setPrintCompressedOutput() {
      this._outputStyle = 1;
   }

   public void setPreserveSpacing() {
      this._outputStyle = 2;
   }

   public void setEntityResolver(EntityResolver entityResolver) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   public void setExpandingEntities(boolean expand) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   private void printUTF8(char ch) {
      this._lastPrinted = ch;
      if (ch <= 127) {
         this._out.write(ch);
      } else if (ch <= 2047) {
         this._out.write(192 + (ch >> 6));
         this._out.write(128 + (ch & '?'));
      } else {
         this._out.write(224 + (ch >> '\f'));
         this._out.write(128 + (ch >> 6 & 63));
         this._out.write(128 + (ch & '?'));
      }
   }

   private void print(String s) {
      if (this._supressOutput == 0) {
         int length = s.length();

         for (int i = 0; i < length; i++) {
            this.printUTF8(s.charAt(i));
         }
      }
   }

   private void print(char c) {
      if (this._supressOutput == 0) {
         this.printUTF8(c);
      }
   }

   private void write(char[] buff, int off, int len) {
      if (this._supressOutput == 0) {
         int end = off + len;

         for (int i = off; i < end; i++) {
            this.printUTF8(buff[i]);
         }
      }
   }

   private void writeEscaped(char[] buff, int off, int len) {
      if (this._supressOutput == 0) {
         int end = off + len;

         for (int i = off; i < end; i++) {
            char ch = buff[i];
            switch (ch) {
               case '\t':
                  this.print("&#9;");
                  break;
               case '\r':
                  this.print("&#13;");
                  break;
               case '"':
                  this.print("&quot;");
                  break;
               case '&':
                  this.print("&amp;");
                  break;
               case '\'':
                  this.print("&apos;");
                  break;
               case '<':
                  this.print("&lt;");
                  break;
               case '>':
                  this.print("&gt;");
                  break;
               default:
                  this.printUTF8(ch);
            }
         }
      }
   }

   private void forcePrintln() {
      this.printUTF8('\n');
   }

   private void println() {
      if (this._outputStyle == 0) {
         if (this._lastPrinted != '\n') {
            if (this._supressOutput == 0) {
               this.forcePrintln();
            }
         }
      }
   }

   private void quote(String s) {
      int length = s.length();
      StringBuffer b = (StringBuffer)(new Object());
      b.append('"');

      for (int i = 0; i < length; i++) {
         char ch = s.charAt(i);
         switch (ch) {
            case '\t':
               b.append("&#9;");
               break;
            case '\n':
               b.append("&#10;");
               break;
            case '\r':
               b.append("&#13;");
               break;
            case '"':
               b.append("&quot;");
               break;
            case '&':
               b.append("&amp;");
               break;
            case '<':
               b.append("&lt;");
               break;
            case '>':
               b.append("&gt;");
               break;
            default:
               b.append(ch);
         }
      }

      b.append('"');
      this.print(b.toString());
   }

   @Override
   public void startDTD() {
      this._supressOutput++;
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public void endDTD(String name, String publicId, String systemId, String body) {
      this._supressOutput--;

      try {
         this.print("<!DOCTYPE ");
         this.print(name);
         if (systemId != null) {
            if (publicId != null) {
               this.print(" PUBLIC ");
               this.quote(publicId);
               this.print(" ");
               this.quote(systemId);
            } else {
               this.print(" SYSTEM ");
               this.quote(systemId);
            }
         }

         this.print(" [");
         this.print(body);
         this.print("]>");
         this.forcePrintln();
      } catch (Throwable var7) {
         throw new Object(ioe.getMessage());
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public void comment(char[] text, int offset, int length) {
      try {
         this.println();
         this.print("<!--");
         this.write(text, offset, length);
         this.print("-->");
         this.println();
      } catch (Throwable var6) {
         throw new Object(ioe.getMessage());
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public void cdataSection(char[] text, int offset, int length) {
      try {
         this.print("<![CDATA[");
         this.write(text, offset, length);
         this.print("]]>");
      } catch (Throwable var6) {
         throw new Object(ioe.getMessage());
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public void startEntityReference(String name, String publicId, String systemId) {
      if (!this._expandingEntities) {
         try {
            this.print('&');
            this.print(name);
            this.print(';');
            this._supressOutput++;
         } catch (Throwable var6) {
            throw new Object(ioe.getMessage());
         }
      }
   }

   @Override
   public void endEntityReference(String name) {
      if (!this._expandingEntities) {
         this._supressOutput--;
      }
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
         this.writeEscaped(ch, start, length);
      } catch (Throwable var6) {
         throw new Object(ioe.getMessage());
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public void ignorableWhitespace(char[] ch, int start, int length) {
      if (this._outputStyle == 2) {
         try {
            this.writeEscaped(ch, start, length);
         } catch (Throwable var6) {
            throw new Object(ioe.getMessage());
         }
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public void processingInstruction(String target, String data) {
      try {
         this.println();
         this.print("<?");
         this.print(target);
         this.print(" ");
         this.print(data);
         this.print("?>");
         this.println();
      } catch (Throwable var5) {
         throw new Object(ioe.getMessage());
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public void startDocument() {
      try {
         this.print("<?xml version=\"1.0\"?>");
         this.forcePrintln();
      } catch (Throwable var3) {
         throw new Object(ioe.getMessage());
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public void endDocument() {
      try {
         this.forcePrintln();
         this._out.flush();
      } catch (Throwable var3) {
         throw new Object(ioe.getMessage());
      }
   }

   @Override
   public void startAndEndElement(String uri, String localName, String qName, Attributes attributes) {
      this.element(uri, localName, qName, attributes, false);
   }

   @Override
   public void startElement(String eUri, String localName, String qName, Attributes attributes) {
      this.element(eUri, localName, qName, attributes, true);
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private void element(String eUri, String localName, String qName, Attributes attributes, boolean hasContent) {
      try {
         this.println();
         this.print("<");
         this.print(qName);
         if (this._pendingNamespaces != null) {
            int len = this._pendingNamespaces.size();

            for (int i = 0; i < len; i += 2) {
               String prefix = (String)this._pendingNamespaces.elementAt(i);
               String uri = (String)this._pendingNamespaces.elementAt(i + 1);
               this.print(" xmlns");
               if (prefix.length() != 0) {
                  this.print(":");
                  this.print(prefix);
               }

               this.print("=");
               this.quote(uri);
            }

            this._pendingNamespaces = null;
         }

         RIMExtendedAttributes ext;
         if (!(attributes instanceof RIMExtendedAttributes)) {
            ext = this._noExtendedAttributes;
         } else {
            ext = (RIMExtendedAttributes)attributes;
         }

         int nAttributes = attributes.getLength();

         for (int i = 0; i < nAttributes; i++) {
            if (!ext.isDefault(i)) {
               this.print(" ");
               this.print(attributes.getQName(i));
               this.print("=");
               this.quote(attributes.getValue(i));
            }
         }

         if (!hasContent) {
            this.print("/");
         }

         this.print(">");
         this.println();
      } catch (Throwable var11) {
         throw new Object(ioe.getMessage());
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public void endElement(String uri, String localName, String qName) {
      try {
         this.println();
         this.print("</");
         this.print(qName);
         this.print(">");
         this.println();
      } catch (Throwable var6) {
         throw new Object(ioe.getMessage());
      }
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
