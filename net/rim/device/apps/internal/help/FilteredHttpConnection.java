package net.rim.device.apps.internal.help;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import javax.microedition.io.HttpConnection;
import net.rim.device.api.xml.jaxp.RIMWBXMLHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

final class FilteredHttpConnection extends DefaultHandler implements HttpConnection, RIMWBXMLHandler {
   private HttpConnection _conn;
   private ByteArrayInputStream _unfilteredByteStream;
   private byte[] _data;
   private byte[] _dataToInsert;
   private static final int WML_SELECT = 55;

   public FilteredHttpConnection(HttpConnection conn, byte[] dataToInsert) {
      if (conn != null && dataToInsert != null) {
         this._conn = conn;
         this._dataToInsert = dataToInsert;
      } else {
         throw new NullPointerException();
      }
   }

   private final synchronized void initializeFilter() {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: aload 0
      // 001: getfield net/rim/device/apps/internal/help/FilteredHttpConnection._data [B
      // 004: ifnull 00a
      // 007: goto 138
      // 00a: aload 0
      // 00b: getfield net/rim/device/apps/internal/help/FilteredHttpConnection._conn Ljavax/microedition/io/HttpConnection;
      // 00e: invokeinterface javax/microedition/io/InputConnection.openInputStream ()Ljava/io/InputStream; 1
      // 013: astore 1
      // 014: aload 0
      // 015: aload 1
      // 016: invokestatic net/rim/device/api/io/IOUtilities.streamToBytes (Ljava/io/InputStream;)[B
      // 019: putfield net/rim/device/apps/internal/help/FilteredHttpConnection._data [B
      // 01c: aload 1
      // 01d: invokevirtual java/io/InputStream.close ()V
      // 020: aload 0
      // 021: new java/io/ByteArrayInputStream
      // 024: dup
      // 025: aload 0
      // 026: getfield net/rim/device/apps/internal/help/FilteredHttpConnection._data [B
      // 029: invokespecial java/io/ByteArrayInputStream.<init> ([B)V
      // 02c: putfield net/rim/device/apps/internal/help/FilteredHttpConnection._unfilteredByteStream Ljava/io/ByteArrayInputStream;
      // 02f: invokestatic net/rim/device/api/xml/parsers/SAXParserFactory.newInstance ()Lnet/rim/device/api/xml/parsers/SAXParserFactory;
      // 032: invokevirtual net/rim/device/api/xml/parsers/SAXParserFactory.newSAXParser ()Lnet/rim/device/api/xml/parsers/SAXParser;
      // 035: astore 2
      // 036: aload 2
      // 037: instanceof net/rim/device/api/xml/jaxp/SAXParserImpl
      // 03a: ifne 040
      // 03d: goto 138
      // 040: new net/rim/device/api/util/IntHashtable
      // 043: dup
      // 044: bipush 9
      // 046: invokespecial net/rim/device/api/util/IntHashtable.<init> (I)V
      // 049: astore 3
      // 04a: aload 3
      // 04b: bipush 36
      // 04d: ldc_w "b"
      // 050: invokevirtual net/rim/device/api/util/IntHashtable.put (ILjava/lang/Object;)Ljava/lang/Object;
      // 053: pop
      // 054: aload 3
      // 055: bipush 38
      // 057: ldc_w "br"
      // 05a: invokevirtual net/rim/device/api/util/IntHashtable.put (ILjava/lang/Object;)Ljava/lang/Object;
      // 05d: pop
      // 05e: aload 3
      // 05f: bipush 39
      // 061: ldc_w "card"
      // 064: invokevirtual net/rim/device/api/util/IntHashtable.put (ILjava/lang/Object;)Ljava/lang/Object;
      // 067: pop
      // 068: aload 3
      // 069: bipush 40
      // 06b: ldc_w "do"
      // 06e: invokevirtual net/rim/device/api/util/IntHashtable.put (ILjava/lang/Object;)Ljava/lang/Object;
      // 071: pop
      // 072: aload 3
      // 073: bipush 43
      // 075: ldc_w "go"
      // 078: invokevirtual net/rim/device/api/util/IntHashtable.put (ILjava/lang/Object;)Ljava/lang/Object;
      // 07b: pop
      // 07c: aload 3
      // 07d: bipush 44
      // 07f: ldc_w "head"
      // 082: invokevirtual net/rim/device/api/util/IntHashtable.put (ILjava/lang/Object;)Ljava/lang/Object;
      // 085: pop
      // 086: aload 3
      // 087: bipush 32
      // 089: ldc_w "p"
      // 08c: invokevirtual net/rim/device/api/util/IntHashtable.put (ILjava/lang/Object;)Ljava/lang/Object;
      // 08f: pop
      // 090: aload 3
      // 091: bipush 55
      // 093: ldc_w "select"
      // 096: invokevirtual net/rim/device/api/util/IntHashtable.put (ILjava/lang/Object;)Ljava/lang/Object;
      // 099: pop
      // 09a: aload 3
      // 09b: bipush 63
      // 09d: ldc_w "wml"
      // 0a0: invokevirtual net/rim/device/api/util/IntHashtable.put (ILjava/lang/Object;)Ljava/lang/Object;
      // 0a3: pop
      // 0a4: new net/rim/device/api/util/IntHashtable
      // 0a7: dup
      // 0a8: bipush 1
      // 0a9: invokespecial net/rim/device/api/util/IntHashtable.<init> (I)V
      // 0ac: astore 4
      // 0ae: aload 4
      // 0b0: bipush 0
      // 0b1: aload 3
      // 0b2: invokevirtual net/rim/device/api/util/IntHashtable.put (ILjava/lang/Object;)Ljava/lang/Object;
      // 0b5: pop
      // 0b6: new net/rim/device/api/util/IntHashtable
      // 0b9: dup
      // 0ba: bipush 7
      // 0bc: invokespecial net/rim/device/api/util/IntHashtable.<init> (I)V
      // 0bf: astore 5
      // 0c1: aload 5
      // 0c3: bipush 84
      // 0c5: ldc_w "class"
      // 0c8: invokevirtual net/rim/device/api/util/IntHashtable.put (ILjava/lang/Object;)Ljava/lang/Object;
      // 0cb: pop
      // 0cc: aload 5
      // 0ce: bipush 74
      // 0d0: ldc_w "href"
      // 0d3: invokevirtual net/rim/device/api/util/IntHashtable.put (ILjava/lang/Object;)Ljava/lang/Object;
      // 0d6: pop
      // 0d7: aload 5
      // 0d9: bipush 85
      // 0db: ldc_w "id"
      // 0de: invokevirtual net/rim/device/api/util/IntHashtable.put (ILjava/lang/Object;)Ljava/lang/Object;
      // 0e1: pop
      // 0e2: aload 5
      // 0e4: bipush 24
      // 0e6: ldc_w "label"
      // 0e9: invokevirtual net/rim/device/api/util/IntHashtable.put (ILjava/lang/Object;)Ljava/lang/Object;
      // 0ec: pop
      // 0ed: aload 5
      // 0ef: bipush 33
      // 0f1: ldc_w "name"
      // 0f4: invokevirtual net/rim/device/api/util/IntHashtable.put (ILjava/lang/Object;)Ljava/lang/Object;
      // 0f7: pop
      // 0f8: aload 5
      // 0fa: bipush 54
      // 0fc: ldc_w "title"
      // 0ff: invokevirtual net/rim/device/api/util/IntHashtable.put (ILjava/lang/Object;)Ljava/lang/Object;
      // 102: pop
      // 103: aload 5
      // 105: bipush 55
      // 107: ldc_w "type"
      // 10a: invokevirtual net/rim/device/api/util/IntHashtable.put (ILjava/lang/Object;)Ljava/lang/Object;
      // 10d: pop
      // 10e: new net/rim/device/api/util/IntHashtable
      // 111: dup
      // 112: bipush 1
      // 113: invokespecial net/rim/device/api/util/IntHashtable.<init> (I)V
      // 116: astore 6
      // 118: aload 6
      // 11a: bipush 0
      // 11b: aload 5
      // 11d: invokevirtual net/rim/device/api/util/IntHashtable.put (ILjava/lang/Object;)Ljava/lang/Object;
      // 120: pop
      // 121: aload 2
      // 122: checkcast net/rim/device/api/xml/jaxp/SAXParserImpl
      // 125: aload 0
      // 126: getfield net/rim/device/apps/internal/help/FilteredHttpConnection._unfilteredByteStream Ljava/io/ByteArrayInputStream;
      // 129: aload 0
      // 12a: aload 4
      // 12c: aload 6
      // 12e: aconst_null
      // 12f: invokevirtual net/rim/device/api/xml/jaxp/SAXParserImpl.parse (Ljava/io/InputStream;Lorg/xml/sax/helpers/DefaultHandler;Lnet/rim/device/api/util/IntHashtable;Lnet/rim/device/api/util/IntHashtable;Lnet/rim/device/api/util/IntHashtable;)V
      // 132: return
      // 133: astore 2
      // 134: return
      // 135: astore 2
      // 136: return
      // 137: astore 2
      // 138: return
      // try (21 -> 147): 148 null
      // try (21 -> 147): 150 null
      // try (21 -> 147): 152 null
   }

   @Override
   public final void startElement(int elementCode, String uri, String localName, String qName, Attributes attributes) throws SAXException {
      if (elementCode == 55) {
         byte[] newData = new byte[this._data.length + this._dataToInsert.length];
         int lengthBeforeSelect = this._data.length - this._unfilteredByteStream.available();
         System.arraycopy(this._data, 0, newData, 0, lengthBeforeSelect);
         System.arraycopy(this._dataToInsert, 0, newData, lengthBeforeSelect, this._dataToInsert.length);
         System.arraycopy(this._data, lengthBeforeSelect, newData, lengthBeforeSelect + this._dataToInsert.length, this._data.length - lengthBeforeSelect);
         this._data = newData;
         this._dataToInsert = null;
         this._unfilteredByteStream = null;
         throw new SAXException("parse cancelled");
      }
   }

   @Override
   public final void endElement(int elementCode, String uri, String localName, String qName) {
   }

   @Override
   public final void close() {
      this._conn.close();
   }

   @Override
   public final String getType() {
      return this._conn.getType();
   }

   @Override
   public final String getEncoding() {
      return this._conn.getEncoding();
   }

   @Override
   public final long getLength() {
      try {
         this.initializeFilter();
         return this._data.length;
      } finally {
         return -1;
      }
   }

   @Override
   public final InputStream openInputStream() {
      this.initializeFilter();
      return new ByteArrayInputStream(this._data);
   }

   @Override
   public final DataInputStream openDataInputStream() {
      return new DataInputStream(this.openInputStream());
   }

   @Override
   public final OutputStream openOutputStream() {
      return this._conn.openOutputStream();
   }

   @Override
   public final DataOutputStream openDataOutputStream() {
      return this._conn.openDataOutputStream();
   }

   @Override
   public final String getURL() {
      return this._conn.getURL();
   }

   @Override
   public final String getProtocol() {
      return this._conn.getProtocol();
   }

   @Override
   public final String getHost() {
      return this._conn.getHost();
   }

   @Override
   public final String getFile() {
      return this._conn.getFile();
   }

   @Override
   public final String getRef() {
      return this._conn.getRef();
   }

   @Override
   public final String getQuery() {
      return this._conn.getQuery();
   }

   @Override
   public final int getPort() {
      return this._conn.getPort();
   }

   @Override
   public final String getRequestMethod() {
      return this._conn.getRequestMethod();
   }

   @Override
   public final void setRequestMethod(String method) {
      this._conn.setRequestMethod(method);
   }

   @Override
   public final String getRequestProperty(String key) {
      return this._conn.getRequestProperty(key);
   }

   @Override
   public final void setRequestProperty(String key, String value) {
      this._conn.setRequestProperty(key, value);
   }

   @Override
   public final int getResponseCode() {
      return this._conn.getResponseCode();
   }

   @Override
   public final String getResponseMessage() {
      return this._conn.getResponseMessage();
   }

   @Override
   public final long getExpiration() {
      return this._conn.getExpiration();
   }

   @Override
   public final long getDate() {
      return this._conn.getDate();
   }

   @Override
   public final long getLastModified() {
      return this._conn.getLastModified();
   }

   @Override
   public final String getHeaderField(String name) {
      return this._conn.getHeaderField(name);
   }

   @Override
   public final int getHeaderFieldInt(String name, int def) {
      return this._conn.getHeaderFieldInt(name, def);
   }

   @Override
   public final long getHeaderFieldDate(String name, long def) {
      return this._conn.getHeaderFieldDate(name, def);
   }

   @Override
   public final String getHeaderField(int n) {
      return this._conn.getHeaderField(n);
   }

   @Override
   public final String getHeaderFieldKey(int n) {
      return this._conn.getHeaderFieldKey(n);
   }
}
