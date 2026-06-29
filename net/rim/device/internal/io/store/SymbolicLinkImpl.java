package net.rim.device.internal.io.store;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import net.rim.device.api.io.file.FileIOException;
import net.rim.device.api.util.Persistable;

final class SymbolicLinkImpl extends FileImpl implements Persistable {
   private int _linkType;
   private String _linkName = "";
   private static final int TYPE_UNDETERMINED = -1;
   private static final int TYPE_FILE = 0;
   private static final int TYPE_URL = 1;
   private static final String PROTOCOL_COD = "cod";
   private static final String PROTOCOL_STORE = "store";

   protected SymbolicLinkImpl(int uid) {
      super(uid);
      this._linkType = -1;
   }

   private final FileImpl getFilePrivate() throws IOException, FileIOException {
      ContentStoreImpl store = ContentStoreImpl.getInstance();
      String absLinkName = this._linkName;
      if (absLinkName != null && absLinkName.length() > 0 && absLinkName.charAt(0) != '/') {
         absLinkName = this.getFolder().getPath() + absLinkName;
      }

      switch (this._linkType) {
         case -2:
            throw new IOException("Unrecognised link type: " + this._linkType);
         case -1:
         default:
            FileImpl file = store.get(absLinkName);
            this._linkType = 0;
            if (file == null) {
               throw new FileIOException(8);
            }

            return file;
         case 0:
            FileImpl file = store.get(absLinkName);
            if (file == null) {
               throw new FileIOException(8);
            }

            return file;
         case 1:
            throw new IOException("getFilePrivate() should never be called on a URL-type link");
      }
   }

   @Override
   public final long getLength() {
      if (this._linkName.length() == 0) {
         return -1;
      } else if (this._linkType == 0) {
         return this.getFilePrivate().getLength();
      } else if (this._linkType != -1) {
         HttpConnection connection = (HttpConnection)Connector.open(this._linkName);
         return connection.getLength();
      } else {
         return 0;
      }
   }

   @Override
   public final int getDrmAttributes() {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aload 0
      // 01: getfield net/rim/device/internal/io/store/SymbolicLinkImpl._linkType I
      // 04: ifne 19
      // 07: bipush 0
      // 08: istore 1
      // 09: aload 0
      // 0a: invokespecial net/rim/device/internal/io/store/SymbolicLinkImpl.getFilePrivate ()Lnet/rim/device/internal/io/store/FileImpl;
      // 0d: invokevirtual net/rim/device/internal/io/store/FileImpl.getDrmAttributes ()I
      // 10: istore 1
      // 11: iload 1
      // 12: ireturn
      // 13: astore 2
      // 14: iload 1
      // 15: ireturn
      // 16: astore 2
      // 17: iload 1
      // 18: ireturn
      // 19: aload 0
      // 1a: invokespecial net/rim/device/internal/io/store/FileImpl.getDrmAttributes ()I
      // 1d: ireturn
      // try (5 -> 9): 11 null
      // try (5 -> 9): 14 null
   }

   public final String getLink() {
      return this._linkName;
   }

   @Override
   public final InputStream openInputStream() {
      if (this._linkType == 0) {
         return this.getFilePrivate().openInputStream();
      } else if (this._linkType != -1) {
         HttpConnection connection = (HttpConnection)Connector.open(this._linkName);
         return connection.openInputStream();
      } else {
         return null;
      }
   }

   @Override
   public final OutputStream openOutputStream(long offset) throws IOException {
      if (this._linkType == 0) {
         return this.getFilePrivate().openOutputStream(offset);
      } else if (this._linkType != -1) {
         throw new IOException("Cannot openOutputStream on an URL");
      } else {
         return null;
      }
   }

   @Override
   public final void setContent(Object object) throws IOException {
      if (this._linkType == 0) {
         this.getFilePrivate().setContent(object);
      } else {
         throw new IOException("Cannot setContent on an URL");
      }
   }

   public final void setLink(String link) {
      int colon = link.indexOf(58);
      String protocol;
      if (colon < 0) {
         protocol = "store";
      } else {
         protocol = link.substring(0, colon);
      }

      if (protocol.equals("cod")) {
         this._linkType = 1;
      } else if (protocol.equals("store")) {
         this._linkType = 0;
         link = link.substring(colon + 1);
         if (link.startsWith("//")) {
            link = link.substring(1);
         }
      } else {
         this._linkType = -1;
      }

      this._linkName = link;
      this.setAttribute(FileImpl.ATTRIB_SYMLINK, link);
   }

   @Override
   final void setName(String name, boolean notify) {
      synchronized (ContentStoreImpl.getInstance().getMonitor()) {
         if (this._linkType == 1) {
            super.setName(name, notify);
         } else {
            super.setName(name, notify);
         }
      }
   }

   final boolean isCodURL() {
      return this._linkType == 1;
   }
}
