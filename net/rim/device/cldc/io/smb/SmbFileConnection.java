package net.rim.device.cldc.io.smb;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.file.FileConnection;
import net.rim.device.api.util.Arrays;
import net.rim.device.cldc.io.utility.URLParameters;
import net.rim.device.internal.io.file.NetworkFileConnection;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

public final class SmbFileConnection extends DefaultHandler implements StreamConnection, NetworkFileConnection, FileConnection {
   private DataInputStream _in;
   private DataOutputStream _out;
   private StreamConnection _conn;
   private int _attributes;
   private long _contentLength;
   private long _lastModified;
   private boolean _open;
   private SmbURL _smbURL;
   private String[] _fileNames = new Object[0];
   private String _absolutePath;
   private URLParameters _urlParameters;
   private static final int ATTR_EXISTS;
   private static final int ATTR_READABLE;
   private static final int ATTR_WRITEABLE;
   private static final int ATTR_HIDDEN;
   private static final int ATTR_DIRECTORY;
   private static final int ATTR_FILE;

   SmbFileConnection(String smburl, StreamConnection conn) {
      this._conn = conn;
      this._smbURL = new SmbURL(smburl);
   }

   @Override
   public final OutputStream openOutputStream() {
      this.ensureOpen();
      if (this._out == null && this._conn != null) {
         this._out = this._conn.openDataOutputStream();
      }

      return this._out;
   }

   @Override
   public final OutputStream openOutputStream(long offset) {
      return this.openOutputStream();
   }

   @Override
   public final void setRequestProperty(String header, String value) {
      if (this._urlParameters == null) {
         this._urlParameters = (URLParameters)(new Object());
      }

      this._urlParameters.setParameter(header, value);
   }

   @Override
   public final InputStream openInputStream() {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aload 0
      // 01: invokespecial net/rim/device/cldc/io/smb/SmbFileConnection.ensureOpen ()V
      // 04: aload 0
      // 05: getfield net/rim/device/cldc/io/smb/SmbFileConnection._in Ljava/io/DataInputStream;
      // 08: ifnonnull 87
      // 0b: aload 0
      // 0c: getfield net/rim/device/cldc/io/smb/SmbFileConnection._conn Ljavax/microedition/io/StreamConnection;
      // 0f: ifnull 87
      // 12: aload 0
      // 13: aload 0
      // 14: getfield net/rim/device/cldc/io/smb/SmbFileConnection._conn Ljavax/microedition/io/StreamConnection;
      // 17: invokeinterface javax/microedition/io/InputConnection.openDataInputStream ()Ljava/io/DataInputStream; 1
      // 1c: putfield net/rim/device/cldc/io/smb/SmbFileConnection._in Ljava/io/DataInputStream;
      // 1f: aload 0
      // 20: getfield net/rim/device/cldc/io/smb/SmbFileConnection._in Ljava/io/DataInputStream;
      // 23: invokevirtual java/io/DataInputStream.read ()I
      // 26: istore 1
      // 27: iload 1
      // 28: bipush 96
      // 2a: if_icmpeq 38
      // 2d: new java/lang/Object
      // 30: dup
      // 31: ldc_w "Unexpected data"
      // 34: invokespecial java/io/IOException.<init> (Ljava/lang/String;)V
      // 37: athrow
      // 38: aload 0
      // 39: getfield net/rim/device/cldc/io/smb/SmbFileConnection._in Ljava/io/DataInputStream;
      // 3c: invokevirtual java/io/DataInputStream.readInt ()I
      // 3f: istore 2
      // 40: iload 2
      // 41: newarray 8
      // 43: astore 3
      // 44: aload 0
      // 45: getfield net/rim/device/cldc/io/smb/SmbFileConnection._in Ljava/io/DataInputStream;
      // 48: aload 3
      // 49: invokevirtual java/io/DataInputStream.readFully ([B)V
      // 4c: invokestatic net/rim/device/api/xml/parsers/SAXParserFactory.newInstance ()Lnet/rim/device/api/xml/parsers/SAXParserFactory;
      // 4f: astore 4
      // 51: aload 4
      // 53: invokevirtual net/rim/device/api/xml/parsers/SAXParserFactory.newSAXParser ()Lnet/rim/device/api/xml/parsers/SAXParser;
      // 56: astore 5
      // 58: aload 5
      // 5a: new java/lang/Object
      // 5d: dup
      // 5e: aload 3
      // 5f: invokespecial java/io/ByteArrayInputStream.<init> ([B)V
      // 62: aload 0
      // 63: invokevirtual net/rim/device/api/xml/parsers/SAXParser.parse (Ljava/io/InputStream;Lorg/xml/sax/helpers/DefaultHandler;)V
      // 66: goto 87
      // 69: astore 4
      // 6b: new java/lang/Object
      // 6e: dup
      // 6f: aload 4
      // 71: invokevirtual java/lang/Throwable.getMessage ()Ljava/lang/String;
      // 74: invokespecial java/io/IOException.<init> (Ljava/lang/String;)V
      // 77: athrow
      // 78: astore 4
      // 7a: new java/lang/Object
      // 7d: dup
      // 7e: aload 4
      // 80: invokevirtual org/xml/sax/SAXException.getMessage ()Ljava/lang/String;
      // 83: invokespecial java/io/IOException.<init> (Ljava/lang/String;)V
      // 86: athrow
      // 87: aload 0
      // 88: getfield net/rim/device/cldc/io/smb/SmbFileConnection._in Ljava/io/DataInputStream;
      // 8b: areturn
      // try (36 -> 48): 49 null
      // try (36 -> 48): 56 null
   }

   private final void ensureOpen() {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: aload 0
      // 001: getfield net/rim/device/cldc/io/smb/SmbFileConnection._open Z
      // 004: ifeq 008
      // 007: return
      // 008: aload 0
      // 009: getfield net/rim/device/cldc/io/smb/SmbFileConnection._smbURL Lnet/rim/device/cldc/io/smb/SmbURL;
      // 00c: ifnonnull 01a
      // 00f: new java/lang/Object
      // 012: dup
      // 013: ldc_w "URL not specified"
      // 016: invokespecial java/io/IOException.<init> (Ljava/lang/String;)V
      // 019: athrow
      // 01a: aload 0
      // 01b: bipush 1
      // 01c: putfield net/rim/device/cldc/io/smb/SmbFileConnection._open Z
      // 01f: aload 0
      // 020: getfield net/rim/device/cldc/io/smb/SmbFileConnection._conn Ljavax/microedition/io/StreamConnection;
      // 023: ifnonnull 033
      // 026: aload 0
      // 027: ldc_w "socket://smbhandler;ConnectionHandler=smb;deviceside=false"
      // 02a: invokestatic javax/microedition/io/Connector.open (Ljava/lang/String;)Ljavax/microedition/io/Connection;
      // 02d: checkcast java/lang/Object
      // 030: putfield net/rim/device/cldc/io/smb/SmbFileConnection._conn Ljavax/microedition/io/StreamConnection;
      // 033: aload 0
      // 034: aload 0
      // 035: invokevirtual net/rim/device/cldc/io/smb/SmbFileConnection.openDataOutputStream ()Ljava/io/DataOutputStream;
      // 038: putfield net/rim/device/cldc/io/smb/SmbFileConnection._out Ljava/io/DataOutputStream;
      // 03b: aload 0
      // 03c: getfield net/rim/device/cldc/io/smb/SmbFileConnection._smbURL Lnet/rim/device/cldc/io/smb/SmbURL;
      // 03f: invokevirtual net/rim/device/cldc/io/smb/SmbURL.getDomain ()Ljava/lang/String;
      // 042: ifnull 05b
      // 045: aload 0
      // 046: getfield net/rim/device/cldc/io/smb/SmbFileConnection._out Ljava/io/DataOutputStream;
      // 049: bipush 1
      // 04a: invokevirtual java/io/DataOutputStream.write (I)V
      // 04d: aload 0
      // 04e: getfield net/rim/device/cldc/io/smb/SmbFileConnection._out Ljava/io/DataOutputStream;
      // 051: aload 0
      // 052: getfield net/rim/device/cldc/io/smb/SmbFileConnection._smbURL Lnet/rim/device/cldc/io/smb/SmbURL;
      // 055: invokevirtual net/rim/device/cldc/io/smb/SmbURL.getDomain ()Ljava/lang/String;
      // 058: invokevirtual java/io/DataOutputStream.writeUTF (Ljava/lang/String;)V
      // 05b: aload 0
      // 05c: getfield net/rim/device/cldc/io/smb/SmbFileConnection._smbURL Lnet/rim/device/cldc/io/smb/SmbURL;
      // 05f: invokevirtual net/rim/device/cldc/io/smb/SmbURL.getUser ()Ljava/lang/String;
      // 062: ifnull 07c
      // 065: aload 0
      // 066: getfield net/rim/device/cldc/io/smb/SmbFileConnection._out Ljava/io/DataOutputStream;
      // 069: bipush 2
      // 06b: invokevirtual java/io/DataOutputStream.write (I)V
      // 06e: aload 0
      // 06f: getfield net/rim/device/cldc/io/smb/SmbFileConnection._out Ljava/io/DataOutputStream;
      // 072: aload 0
      // 073: getfield net/rim/device/cldc/io/smb/SmbFileConnection._smbURL Lnet/rim/device/cldc/io/smb/SmbURL;
      // 076: invokevirtual net/rim/device/cldc/io/smb/SmbURL.getUser ()Ljava/lang/String;
      // 079: invokevirtual java/io/DataOutputStream.writeUTF (Ljava/lang/String;)V
      // 07c: aload 0
      // 07d: getfield net/rim/device/cldc/io/smb/SmbFileConnection._smbURL Lnet/rim/device/cldc/io/smb/SmbURL;
      // 080: invokevirtual net/rim/device/cldc/io/smb/SmbURL.getPassword ()Ljava/lang/String;
      // 083: ifnull 09d
      // 086: aload 0
      // 087: getfield net/rim/device/cldc/io/smb/SmbFileConnection._out Ljava/io/DataOutputStream;
      // 08a: bipush 3
      // 08c: invokevirtual java/io/DataOutputStream.write (I)V
      // 08f: aload 0
      // 090: getfield net/rim/device/cldc/io/smb/SmbFileConnection._out Ljava/io/DataOutputStream;
      // 093: aload 0
      // 094: getfield net/rim/device/cldc/io/smb/SmbFileConnection._smbURL Lnet/rim/device/cldc/io/smb/SmbURL;
      // 097: invokevirtual net/rim/device/cldc/io/smb/SmbURL.getPassword ()Ljava/lang/String;
      // 09a: invokevirtual java/io/DataOutputStream.writeUTF (Ljava/lang/String;)V
      // 09d: aload 0
      // 09e: getfield net/rim/device/cldc/io/smb/SmbFileConnection._smbURL Lnet/rim/device/cldc/io/smb/SmbURL;
      // 0a1: invokevirtual net/rim/device/cldc/io/smb/SmbURL.getFilepath ()Ljava/lang/String;
      // 0a4: ifnull 0be
      // 0a7: aload 0
      // 0a8: getfield net/rim/device/cldc/io/smb/SmbFileConnection._out Ljava/io/DataOutputStream;
      // 0ab: bipush 5
      // 0ad: invokevirtual java/io/DataOutputStream.write (I)V
      // 0b0: aload 0
      // 0b1: getfield net/rim/device/cldc/io/smb/SmbFileConnection._out Ljava/io/DataOutputStream;
      // 0b4: aload 0
      // 0b5: getfield net/rim/device/cldc/io/smb/SmbFileConnection._smbURL Lnet/rim/device/cldc/io/smb/SmbURL;
      // 0b8: invokevirtual net/rim/device/cldc/io/smb/SmbURL.getFilepath ()Ljava/lang/String;
      // 0bb: invokevirtual java/io/DataOutputStream.writeUTF (Ljava/lang/String;)V
      // 0be: aload 0
      // 0bf: getfield net/rim/device/cldc/io/smb/SmbFileConnection._smbURL Lnet/rim/device/cldc/io/smb/SmbURL;
      // 0c2: invokevirtual net/rim/device/cldc/io/smb/SmbURL.getServer ()Ljava/lang/String;
      // 0c5: ifnull 0df
      // 0c8: aload 0
      // 0c9: getfield net/rim/device/cldc/io/smb/SmbFileConnection._out Ljava/io/DataOutputStream;
      // 0cc: bipush 4
      // 0ce: invokevirtual java/io/DataOutputStream.write (I)V
      // 0d1: aload 0
      // 0d2: getfield net/rim/device/cldc/io/smb/SmbFileConnection._out Ljava/io/DataOutputStream;
      // 0d5: aload 0
      // 0d6: getfield net/rim/device/cldc/io/smb/SmbFileConnection._smbURL Lnet/rim/device/cldc/io/smb/SmbURL;
      // 0d9: invokevirtual net/rim/device/cldc/io/smb/SmbURL.getServer ()Ljava/lang/String;
      // 0dc: invokevirtual java/io/DataOutputStream.writeUTF (Ljava/lang/String;)V
      // 0df: aload 0
      // 0e0: getfield net/rim/device/cldc/io/smb/SmbFileConnection._urlParameters Lnet/rim/device/cldc/io/utility/URLParameters;
      // 0e3: ifnull 13c
      // 0e6: aload 0
      // 0e7: getfield net/rim/device/cldc/io/smb/SmbFileConnection._urlParameters Lnet/rim/device/cldc/io/utility/URLParameters;
      // 0ea: invokevirtual net/rim/device/cldc/io/utility/URLParameters.getKeys ()Ljava/util/Vector;
      // 0ed: astore 1
      // 0ee: aload 0
      // 0ef: getfield net/rim/device/cldc/io/smb/SmbFileConnection._urlParameters Lnet/rim/device/cldc/io/utility/URLParameters;
      // 0f2: invokevirtual net/rim/device/cldc/io/utility/URLParameters.getValues ()Ljava/util/Vector;
      // 0f5: astore 2
      // 0f6: bipush 0
      // 0f7: istore 3
      // 0f8: iload 3
      // 0f9: aload 1
      // 0fa: invokevirtual java/util/Vector.size ()I
      // 0fd: if_icmpge 13c
      // 100: aload 0
      // 101: getfield net/rim/device/cldc/io/smb/SmbFileConnection._out Ljava/io/DataOutputStream;
      // 104: bipush 6
      // 106: invokevirtual java/io/DataOutputStream.write (I)V
      // 109: aload 0
      // 10a: getfield net/rim/device/cldc/io/smb/SmbFileConnection._out Ljava/io/DataOutputStream;
      // 10d: new java/lang/Object
      // 110: dup
      // 111: invokespecial java/lang/StringBuffer.<init> ()V
      // 114: aload 1
      // 115: iload 3
      // 116: invokevirtual java/util/Vector.elementAt (I)Ljava/lang/Object;
      // 119: checkcast java/lang/Object
      // 11c: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 11f: ldc_w "="
      // 122: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 125: aload 2
      // 126: iload 3
      // 127: invokevirtual java/util/Vector.elementAt (I)Ljava/lang/Object;
      // 12a: checkcast java/lang/Object
      // 12d: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 130: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 133: invokevirtual java/io/DataOutputStream.writeUTF (Ljava/lang/String;)V
      // 136: iinc 3 1
      // 139: goto 0f8
      // 13c: aload 0
      // 13d: getfield net/rim/device/cldc/io/smb/SmbFileConnection._out Ljava/io/DataOutputStream;
      // 140: invokevirtual java/io/DataOutputStream.flush ()V
      // 143: return
      // 144: astore 1
      // 145: new java/lang/Object
      // 148: dup
      // 149: ldc_w "Invalid SMB URL"
      // 14c: invokespecial java/io/IOException.<init> (Ljava/lang/String;)V
      // 14f: athrow
      // 150: astore 1
      // 151: aload 0
      // 152: invokevirtual net/rim/device/cldc/io/smb/SmbFileConnection.close ()V
      // 155: return
      // try (4 -> 142): 143 null
      // try (4 -> 142): 149 null
   }

   @Override
   public final DataInputStream openDataInputStream() {
      this.ensureOpen();
      return (DataInputStream)this.openInputStream();
   }

   @Override
   public final DataOutputStream openDataOutputStream() {
      this.ensureOpen();
      return (DataOutputStream)this.openOutputStream();
   }

   @Override
   public final void close() {
      this._open = false;
      if (this._in != null) {
         this._in.close();
         this._in = null;
      }

      if (this._out != null) {
         this._out.close();
         this._out = null;
      }

      if (this._conn != null) {
         this._conn.close();
         this._conn = null;
      }
   }

   @Override
   public final Enumeration list() {
      return new SmbFileConnection$1(this);
   }

   @Override
   public final Enumeration list(String wildcard, boolean includeHidden) {
      return this.list();
   }

   @Override
   public final boolean isOpen() {
      return this._open;
   }

   @Override
   public final long lastModified() {
      return this._lastModified;
   }

   @Override
   public final String getURL() {
      return this._smbURL.toString();
   }

   @Override
   public final String getPath() {
      return this._smbURL != null ? this._smbURL.getFilepath() : "";
   }

   @Override
   public final boolean isHidden() {
      return (this._attributes & 8) == 8;
   }

   @Override
   public final void setHidden(boolean hidden) {
   }

   @Override
   public final void setWritable(boolean writable) {
   }

   @Override
   public final void setReadable(boolean readable) {
   }

   @Override
   public final long fileSize() {
      this.ensureOpen();
      return this._contentLength;
   }

   @Override
   public final long directorySize(boolean includeSubDirs) {
      return 0;
   }

   @Override
   public final long usedSize() {
      return 0;
   }

   @Override
   public final long availableSize() {
      return 0;
   }

   @Override
   public final long totalSize() {
      return 0;
   }

   @Override
   public final boolean isDirectory() {
      return (this._attributes & 16) == 16;
   }

   @Override
   public final boolean exists() {
      return (this._attributes & 1) == 1;
   }

   @Override
   public final boolean canRead() {
      return (this._attributes & 2) == 2;
   }

   @Override
   public final boolean canWrite() {
      return (this._attributes & 4) == 4;
   }

   @Override
   public final String getName() {
      String t = this._absolutePath.replace('\\', '/');
      int i = t.lastIndexOf(47);
      return i != -1 ? t.substring(i + 1) : t;
   }

   @Override
   public final void setFileConnection(String fileName) {
      this.close();
      this._smbURL = new SmbURL(fileName);
      this.ensureOpen();
   }

   @Override
   public final void truncate(long byteOffset) {
   }

   @Override
   public final void rename(String newName) {
   }

   @Override
   public final void delete() {
   }

   @Override
   public final void mkdir() {
   }

   @Override
   public final void create() {
   }

   private static final boolean parseBoolean(String s) {
      return s == null ? false : s.equals("true");
   }

   private final void setFlag(int mask, boolean flag) {
      if (flag) {
         this._attributes |= mask;
      } else {
         this._attributes &= ~mask;
      }
   }

   @Override
   public final void startElement(String uri, String localName, String qName, Attributes attributes) {
      if (!localName.equals("fileObject")) {
         if (!localName.equals("directory")) {
            String name = attributes.getValue("name");
            Arrays.add(this._fileNames, name);
         }
      } else {
         this._absolutePath = attributes.getValue("absolutePath");
         this.setFlag(1, parseBoolean(attributes.getValue("exists")));
         this.setFlag(8, parseBoolean(attributes.getValue("isHidden")));
         this.setFlag(2, parseBoolean(attributes.getValue("canRead")));
         this.setFlag(4, parseBoolean(attributes.getValue("canWrite")));
         this.setFlag(32, parseBoolean(attributes.getValue("isFile")));
         this.setFlag(16, parseBoolean(attributes.getValue("isDirectory")));
         String l = attributes.getValue("length");
         if (l != null) {
            this._contentLength = Long.parseLong(l);
         }

         l = attributes.getValue("lastModified");
         if (l != null) {
            this._lastModified = Long.parseLong(l);
         }

         l = attributes.getValue("contentType");
      }
   }
}
