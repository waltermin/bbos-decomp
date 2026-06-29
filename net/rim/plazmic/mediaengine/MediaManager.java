package net.rim.plazmic.mediaengine;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import net.rim.device.api.system.ControlledAccess;
import net.rim.device.internal.applicationcontrol.ApplicationControl;
import net.rim.device.internal.i18n.CommonResource;
import net.rim.plazmic.internal.mediaengine.MediaFactory;
import net.rim.plazmic.internal.mediaengine.MediaTypes;
import net.rim.plazmic.internal.mediaengine.ResourceContext;
import net.rim.plazmic.internal.mediaengine.ResourceProvider;
import net.rim.plazmic.internal.mediaengine.util.MEUtilities;
import net.rim.plazmic.internal.mediaengine.util.Platform;
import net.rim.plazmic.internal.mediaengine.util.SafeArray;
import net.rim.plazmic.mediaengine.io.ConnectionInfo;
import net.rim.plazmic.mediaengine.io.Connector;
import net.rim.plazmic.mediaengine.io.LoadingStatus;
import net.rim.vm.TraceBack;

public class MediaManager implements ResourceProvider {
   private SafeArray _listeners;
   protected MediaException _interruptedException;
   private ResourceProvider _customProvider;
   private byte[] _dataBuffer;
   protected String _documentBase = "";
   private boolean _isResolveURLOn = true;
   protected boolean _cancelDownload;
   protected ConnectionInfo _connectionInfo;
   protected Connector _connector;
   protected Connector _defaultConnector;
   protected MediaManager$Downloader _downloader;
   private Platform _platform;
   public static final String GATEWAY = "Gateway";
   public static final String URL_BASE = "URI_BASE";
   protected static final int DEFAULT_HEADER_LENGTH = 12;
   private static final int DATA_BUFFER_SIZE = 2048;

   protected InputStream getInputStream(String uri, ConnectionInfo info) {
      return this._connector == null ? null : this._connector.getInputStream(uri, info);
   }

   protected void releaseConnection(ConnectionInfo info) {
      if (this._connector != null) {
         this._connector.releaseConnection(info);
      }

      if (info != null) {
         info.dispose();
      }
   }

   public void setConnector(Connector connector) {
      this._connector = connector;
   }

   public Connector getConnector() {
      return this._connector;
   }

   public Connector getDefaultConnector() {
      if (this._defaultConnector == null) {
         this._defaultConnector = MediaFactory.createDefaultConnector();
      }

      return this._defaultConnector;
   }

   public void setProperty(String name, String value) {
      if ("URI_BASE".equals(name)) {
         this.setDocumentBase(value);
      } else {
         if (this._connector != null) {
            this._connector.setProperty(name, value);
         }
      }
   }

   protected void setDocumentBase(String absURL) {
      this._documentBase = absURL == null ? "" : extractDocumentBase(absURL);
   }

   public String resolveURL(String url) {
      return resolveURL(url, this._documentBase);
   }

   public void addMediaListener(MediaListener listener) {
      this.assertPermission();
      if (listener == null) {
         throw new Object("Listener can not be null");
      }

      if (!this._listeners.contains(listener)) {
         this._listeners.add(listener);
      }
   }

   public void removeMediaListener(MediaListener listener) {
      this.assertPermission();
      if (listener != null) {
         this._listeners.remove(listener);
      }
   }

   protected final void fireMediaEvent(int event, int eventParam, Object data) {
      ControlledAccess.assertRRISignature(TraceBack.getCallingModule(0));
      MEUtilities.fireMediaEvent(this, this._listeners, event, eventParam, data);
   }

   public Object createMedia() {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: ldc_w "net.rim.plazmic.internal.mediaengine.model.intarray.v1_2.ModelInteractorImpl"
      // 03: astore 1
      // 04: aload 1
      // 05: invokestatic java/lang/Class.forName (Ljava/lang/String;)Ljava/lang/Class;
      // 08: invokevirtual java/lang/Class.newInstance ()Ljava/lang/Object;
      // 0b: areturn
      // 0c: astore 1
      // 0d: aconst_null
      // 0e: areturn
      // 0f: astore 1
      // 10: aconst_null
      // 11: areturn
      // 12: astore 1
      // 13: aconst_null
      // 14: areturn
      // try (0 -> 5): 6 null
      // try (0 -> 5): 9 null
      // try (0 -> 5): 12 null
   }

   public synchronized Object createMedia(String uri) {
      return this.createResourceFromURI(uri, null, null, null, this._isResolveURLOn);
   }

   public synchronized Object createMedia(String uri, String suggestedType) {
      return this.createResourceFromURI(uri, suggestedType, null, null, this._isResolveURLOn);
   }

   public void createMediaLater(String uri) {
      this.assertPermission();
      if (this._downloader == null) {
         this._downloader = new MediaManager$Downloader(this);
         this._downloader.start();
      }

      this._downloader.loadMedia(uri);
   }

   public void setCustomResourceProvider(ResourceProvider customProvider) {
      this._customProvider = customProvider;
   }

   public void dispose() {
      if (this._downloader != null) {
         synchronized (this._downloader) {
            this._downloader._running = false;
            this._downloader.notify();
         }
      }
   }

   protected Object createResource(String param1, Object param2, ResourceContext param3, Object param4, LoadingStatus param5) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: aload 0
      // 001: invokespecial net/rim/plazmic/mediaengine/MediaManager.assertPermission ()V
      // 004: aload 3
      // 005: ifnonnull 00c
      // 008: invokestatic net/rim/plazmic/internal/mediaengine/ResourceContext.createContext ()Lnet/rim/plazmic/internal/mediaengine/ResourceContext;
      // 00b: astore 3
      // 00c: aconst_null
      // 00d: astore 6
      // 00f: aload 0
      // 010: getfield net/rim/plazmic/mediaengine/MediaManager._customProvider Lnet/rim/plazmic/internal/mediaengine/ResourceProvider;
      // 013: ifnull 025
      // 016: aload 0
      // 017: getfield net/rim/plazmic/mediaengine/MediaManager._customProvider Lnet/rim/plazmic/internal/mediaengine/ResourceProvider;
      // 01a: aload 1
      // 01b: aload 2
      // 01c: aload 3
      // 01d: aload 0
      // 01e: invokeinterface net/rim/plazmic/internal/mediaengine/ResourceProvider.createResource (Ljava/lang/String;Ljava/lang/Object;Lnet/rim/plazmic/internal/mediaengine/ResourceContext;Ljava/lang/Object;)Ljava/lang/Object; 5
      // 023: astore 6
      // 025: aload 6
      // 027: ifnull 02d
      // 02a: goto 12c
      // 02d: aload 5
      // 02f: ifnonnull 036
      // 032: bipush 1
      // 033: goto 037
      // 036: bipush 0
      // 037: istore 7
      // 039: iload 7
      // 03b: ifeq 04f
      // 03e: new net/rim/plazmic/mediaengine/io/LoadingStatus
      // 041: dup
      // 042: invokespecial net/rim/plazmic/mediaengine/io/LoadingStatus.<init> ()V
      // 045: astore 5
      // 047: aload 5
      // 049: aconst_null
      // 04a: aload 1
      // 04b: bipush 1
      // 04c: invokevirtual net/rim/plazmic/mediaengine/io/LoadingStatus.init (Ljava/lang/String;Ljava/lang/String;I)V
      // 04f: aload 0
      // 050: invokespecial net/rim/plazmic/mediaengine/MediaManager.checkCancelDownload ()V
      // 053: aload 1
      // 054: ifnonnull 05b
      // 057: ldc_w "application/x-vnd.rim.pme"
      // 05a: astore 1
      // 05b: aconst_null
      // 05c: astore 8
      // 05e: aload 1
      // 05f: invokestatic net/rim/plazmic/internal/mediaengine/MediaFactory.verifyContentType (Ljava/lang/String;)V
      // 062: goto 076
      // 065: astore 9
      // 067: aload 1
      // 068: invokestatic net/rim/plazmic/internal/mediaengine/MediaTypes.getTypeCategory (Ljava/lang/String;)I
      // 06b: bipush 1
      // 06c: if_icmpne 072
      // 06f: aload 9
      // 071: athrow
      // 072: aload 9
      // 074: astore 8
      // 076: aload 5
      // 078: bipush 2
      // 07a: invokevirtual net/rim/plazmic/mediaengine/io/LoadingStatus.setStatus (I)V
      // 07d: aload 0
      // 07e: bipush 9
      // 080: bipush -1
      // 082: aload 5
      // 084: invokevirtual net/rim/plazmic/mediaengine/MediaManager.fireMediaEvent (IILjava/lang/Object;)V
      // 087: aload 8
      // 089: ifnonnull 09a
      // 08c: aload 0
      // 08d: aload 1
      // 08e: aload 2
      // 08f: aload 3
      // 090: aload 5
      // 092: invokespecial net/rim/plazmic/mediaengine/MediaManager.readRegisteredType (Ljava/lang/String;Ljava/lang/Object;Lnet/rim/plazmic/internal/mediaengine/ResourceContext;Lnet/rim/plazmic/mediaengine/io/LoadingStatus;)Ljava/lang/Object;
      // 095: astore 6
      // 097: goto 0ad
      // 09a: aload 0
      // 09b: aload 1
      // 09c: aload 2
      // 09d: aload 3
      // 09e: aload 5
      // 0a0: invokespecial net/rim/plazmic/mediaengine/MediaManager.readUnregisteredType (Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;Lnet/rim/plazmic/mediaengine/io/LoadingStatus;)Ljava/lang/Object;
      // 0a3: astore 6
      // 0a5: goto 0ad
      // 0a8: astore 9
      // 0aa: aload 8
      // 0ac: athrow
      // 0ad: iload 7
      // 0af: ifeq 0b9
      // 0b2: aload 5
      // 0b4: bipush 3
      // 0b6: invokevirtual net/rim/plazmic/mediaengine/io/LoadingStatus.setStatus (I)V
      // 0b9: aload 5
      // 0bb: ifnull 0d6
      // 0be: aload 6
      // 0c0: invokestatic net/rim/plazmic/internal/mediaengine/util/MEUtilities.getMediaModel (Ljava/lang/Object;)Lnet/rim/plazmic/internal/mediaengine/MediaModel;
      // 0c3: astore 9
      // 0c5: aload 9
      // 0c7: ifnull 0d6
      // 0ca: aload 9
      // 0cc: aload 5
      // 0ce: invokevirtual net/rim/plazmic/mediaengine/io/LoadingStatus.getSource ()Ljava/lang/String;
      // 0d1: invokeinterface net/rim/plazmic/internal/mediaengine/MediaModel.setSource (Ljava/lang/String;)V 2
      // 0d6: iload 7
      // 0d8: ifeq 12c
      // 0db: aload 0
      // 0dc: aconst_null
      // 0dd: putfield net/rim/plazmic/mediaengine/MediaManager._dataBuffer [B
      // 0e0: aload 0
      // 0e1: bipush 9
      // 0e3: bipush -1
      // 0e5: aload 5
      // 0e7: invokevirtual net/rim/plazmic/mediaengine/MediaManager.fireMediaEvent (IILjava/lang/Object;)V
      // 0ea: goto 12c
      // 0ed: astore 8
      // 0ef: new net/rim/plazmic/mediaengine/MediaException
      // 0f2: dup
      // 0f3: bipush -1
      // 0f5: aload 8
      // 0f7: invokevirtual java/lang/Throwable.getMessage ()Ljava/lang/String;
      // 0fa: aload 8
      // 0fc: invokespecial net/rim/plazmic/mediaengine/MediaException.<init> (ILjava/lang/String;Ljava/lang/Object;)V
      // 0ff: athrow
      // 100: astore 8
      // 102: new net/rim/plazmic/mediaengine/MediaException
      // 105: dup
      // 106: bipush -1
      // 108: aload 8
      // 10a: invokevirtual java/lang/Throwable.getMessage ()Ljava/lang/String;
      // 10d: aload 8
      // 10f: invokespecial net/rim/plazmic/mediaengine/MediaException.<init> (ILjava/lang/String;Ljava/lang/Object;)V
      // 112: athrow
      // 113: astore 10
      // 115: iload 7
      // 117: ifeq 129
      // 11a: aload 0
      // 11b: aconst_null
      // 11c: putfield net/rim/plazmic/mediaengine/MediaManager._dataBuffer [B
      // 11f: aload 0
      // 120: bipush 9
      // 122: bipush -1
      // 124: aload 5
      // 126: invokevirtual net/rim/plazmic/mediaengine/MediaManager.fireMediaEvent (IILjava/lang/Object;)V
      // 129: aload 10
      // 12b: athrow
      // 12c: aload 6
      // 12e: areturn
      // try (47 -> 49): 50 net/rim/plazmic/mediaengine/MediaException
      // try (77 -> 84): 85 net/rim/plazmic/mediaengine/MediaException
      // try (39 -> 104): 115 null
      // try (39 -> 104): 124 null
      // try (39 -> 104): 133 null
      // try (115 -> 134): 133 null
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public byte[] getByteArray(String uri) {
      this._cancelDownload = false;
      this.checkCancelDownload();
      if (this._isResolveURLOn) {
         uri = this.resolveURL(uri);
      }

      InputStream in = null;
      boolean var5 = false /* VF: Semaphore variable */;

      try {
         var5 = true;
         this._connectionInfo.setSource(uri);
         in = this.getInputStream(uri, this._connectionInfo);
         if (in == null) {
            throw new Object();
         }

         var5 = false;
      } finally {
         if (var5) {
            this.releaseConnection(this._connectionInfo);
            if (in != null) {
               in.close();
            }
         }
      }

      this.releaseConnection(this._connectionInfo);
      if (in != null) {
         in.close();
      }

      ByteArrayOutputStream out = (ByteArrayOutputStream)(new Object(12));
      this.loadData(in, out, 12, null);
      this.loadData(in, out, -1, null);
      out.close();
      return out.toByteArray();
   }

   public void cancel() {
      this._cancelDownload = true;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   protected Object createResourceFromURI(String uri, String suggestedType, ResourceContext context, Object referrer, boolean updateDocumentBase) {
      this.assertPermission();
      if (uri == null) {
         throw new Object("Must have non-null URI");
      }

      if (context == null) {
         context = ResourceContext.createContext();
      }

      this._cancelDownload = false;
      if (this._customProvider != null) {
         Object object = this._customProvider.createResourceFromURI(uri, suggestedType, context, this);
         if (object != null) {
            return object;
         }
      }

      if (uri.startsWith("x-object:/")) {
         int query = uri.indexOf(63);
         String type = null;
         String data = null;
         if (query != -1) {
            type = uri.substring(0, query);
            data = uri.substring(query + 1);
         } else {
            type = uri;
         }

         Object object = null;
         return this.createResource(type, data, context, referrer);
      } else {
         this.checkCancelDownload();
         String oldDocumentBase = null;
         if (this._isResolveURLOn) {
            uri = this.resolveURL(uri);
            oldDocumentBase = this._documentBase;
         }

         LoadingStatus status = new LoadingStatus();
         status.init(uri, null, 1);
         this.fireMediaEvent(9, -1, status);
         boolean failed = true;
         InputStream in = null;
         boolean var15 = false /* VF: Semaphore variable */;

         String ext;
         try {
            var15 = true;
            this._connectionInfo.setSource(uri);
            in = this.getInputStream(uri, this._connectionInfo);
            String contentType = this._connectionInfo.getContentType();
            status.setContentType(contentType);
            status.setTotalBytes(this._connectionInfo.getLength());
            if (in == null) {
               throw new Object();
            }

            if (updateDocumentBase) {
               this.setDocumentBase(this._connectionInfo.getSource());
            }

            if (contentType != null && suggestedType != null) {
               if (contentType.equals("application/x-vnd.rim.pme") && suggestedType.equals("audio/midi")) {
                  contentType = suggestedType;
               }

               if (suggestedType.equals("font")) {
                  contentType = suggestedType;
               }
            } else if (contentType == null && suggestedType == null) {
               int uriLen = uri.length();
               ext = uri.substring(uriLen - 4, uriLen);
               if (uri.length() > 3 && this._platform.strEqualIgnoreCase(ext, ".pmb")) {
                  contentType = "application/x-vnd.rim.pme.b";
               }
            }

            Object object = this.createResource(contentType != null ? contentType : suggestedType, in, context, referrer, status);
            failed = false;
            ext = (String)object;
            var15 = false;
         } finally {
            if (var15) {
               if (failed && updateDocumentBase) {
                  this._documentBase = oldDocumentBase;
               }

               this.releaseConnection(this._connectionInfo);
               if (in != null) {
                  in.close();
               }

               status.setStatus(failed ? 4 : 3);
               this.fireMediaEvent(9, -1, status);
               status.clear();
            }
         }

         if (failed && updateDocumentBase) {
            this._documentBase = oldDocumentBase;
         }

         this.releaseConnection(this._connectionInfo);
         if (in != null) {
            in.close();
         }

         status.setStatus(failed ? 4 : 3);
         this.fireMediaEvent(9, -1, status);
         status.clear();
         return ext;
      }
   }

   protected int loadData(InputStream in, OutputStream out, int size, LoadingStatus status) {
      if (this._dataBuffer == null) {
         this._dataBuffer = new byte[2048];
      }

      int len = size;
      if (size < 0 || size > this._dataBuffer.length) {
         len = this._dataBuffer.length;
      }

      int totalCopied = 0;

      int read;
      while ((read = in.read(this._dataBuffer, 0, len)) != -1) {
         if (status != null) {
            status.setReadBytes(status.getReadBytes() + read);
         }

         out.write(this._dataBuffer, 0, read);
         totalCopied += read;
         this.checkCancelDownload();
         if (size > 0) {
            if (totalCopied >= size) {
               break;
            }

            len = Math.min(size - totalCopied, this._dataBuffer.length);
         }
      }

      return totalCopied;
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public Object createResourceFromURI(String uri, String suggestedType, ResourceContext context, Object referrer) throws MediaException {
      try {
         return this.createResourceFromURI(uri, suggestedType, context, referrer, false);
      } catch (Throwable var7) {
         throw new MediaException(-1, e.getMessage(), e);
      }
   }

   @Override
   public synchronized Object createResource(String type, Object data, ResourceContext context, Object referrer) {
      this._cancelDownload = false;
      return this.createResource(type, data, context, referrer, null);
   }

   private Object readUnregisteredType(String type, Object data, Object context, LoadingStatus status) throws MediaException {
      if (data instanceof Object) {
         InputStream in = (InputStream)data;
         int numBytes = (int)status.getTotalBytes();
         int t = MediaTypes.getTypeCategory(type);
         switch (t) {
            case 2:
               return this._platform.createImage(this.loadBytes(in, numBytes), this._connectionInfo.getContentType());
            case 4:
               return this._platform.createSound((InputStream)(new Object(this.loadBytes(in, numBytes))), type);
            case 32:
               if (context instanceof ResourceContext) {
                  return this._platform.loadFont(this.loadBytes(in, numBytes), (String)((ResourceContext)context).get("FontFamily"));
               }
         }
      }

      throw new MediaException();
   }

   public MediaManager(Connector c, boolean enableRelative) {
      if (c == null) {
         throw new Object("Connector can not be null");
      }

      this._interruptedException = new MediaException(5);
      this._connectionInfo = new ConnectionInfo();
      this._listeners = new SafeArray();
      this._connector = c;
      this._isResolveURLOn = enableRelative;
      this._platform = MediaFactory.getPlatform();
   }

   private Object readRegisteredType(String type, Object data, ResourceContext context, LoadingStatus status) {
      ResourceProvider provider = null;
      if (data instanceof Object) {
         ByteArrayOutputStream out = (ByteArrayOutputStream)(new Object(12));
         this.loadData((InputStream)data, out, 12, status);
         byte[] header = out.toByteArray();
         provider = MediaFactory.createResourceProvider(type, header);
         this.loadData((InputStream)data, out, -1, status);
         out.close();
         data = out.toByteArray();
      } else {
         provider = MediaFactory.createResourceProvider(type, !(data instanceof byte[]) ? null : (byte[])data);
      }

      return provider != null ? provider.createResource(type, data, context, this) : null;
   }

   public static String resolveURL(String url, String base) {
      StringBuffer buf = (StringBuffer)(new Object());
      if (base != null && !base.endsWith("/")) {
         base = base.concat("/");
      }

      int absBaseIdx = base != null ? base.indexOf(58) : -1;
      int absUrlIdx = url.indexOf(58);
      int queryUrlIdx = url.indexOf(63);
      int firstSlashIdx = -1;
      if (base != null) {
         if (absBaseIdx != -1) {
            firstSlashIdx = base.indexOf(47, absBaseIdx + 3);
         }

         if (url.startsWith("//") && absBaseIdx != -1) {
            buf.append(base.substring(0, absBaseIdx + 1));
            buf.append(url);
            return buf.toString();
         }

         if (url.startsWith("/") && firstSlashIdx != -1) {
            buf.append(base.substring(0, firstSlashIdx));
         } else if (absUrlIdx == -1 || absUrlIdx > queryUrlIdx && queryUrlIdx > 0) {
            buf.append(base);
         }
      }

      if (absBaseIdx == -1 && url.startsWith("/")) {
         url = url.substring(1);
         buf.append(url);
      } else {
         buf.append(url);
      }

      int slash = 0;
      int i = 0;

      while (buf.length() >= 2 && i < buf.length() - 2) {
         if (buf.charAt(i++) == '/') {
            slash = i - 1;
            if (buf.charAt(i++) == '.' && buf.charAt(i++) == '.') {
               if (i < buf.length() - 1 && buf.charAt(i++) == '/') {
                  while (buf.charAt(--slash) != '/') {
                  }

                  if (slash <= absBaseIdx + 3) {
                     buf.delete(firstSlashIdx + 1, i);
                  } else {
                     buf.delete(slash + 1, i);
                  }

                  i = slash;
               }
            } else {
               i = slash + 1;
               if (buf.charAt(i++) == '.' && buf.charAt(i++) == '/') {
                  buf.delete(i - 2, i);
                  i = slash;
               }
            }
         }
      }

      return buf.toString();
   }

   public MediaManager() {
      this(MediaFactory.createDefaultConnector(), true);
      this._defaultConnector = this._connector;
   }

   private byte[] loadBytes(InputStream in, int totalBytes) {
      if (totalBytes > 0) {
         byte[] bytes = new byte[totalBytes];
         in.read(bytes, 0, totalBytes);
         return bytes;
      } else {
         ByteArrayOutputStream out = (ByteArrayOutputStream)(new Object());
         this.loadData(in, out, -1, null);
         in.close();
         out.close();
         return out.toByteArray();
      }
   }

   public MediaManager(Connector c) {
      this(c, true);
   }

   private final void checkCancelDownload() throws MediaException {
      if (this._cancelDownload) {
         throw this._interruptedException;
      }
   }

   public static String extractDocumentBase(String absURL) {
      int end = absURL.indexOf(63);
      if (end == -1) {
         end = absURL.length() - 1;
      }

      int indexOfLastSlash = absURL.lastIndexOf(47, end);
      if (absURL.charAt(indexOfLastSlash - 1) != '/') {
         end = indexOfLastSlash;
      }

      return absURL.substring(0, end + 1);
   }

   private void assertPermission() {
      ApplicationControl.assertMediaPermitted(true, CommonResource.getBundle(), 10177);
   }
}
