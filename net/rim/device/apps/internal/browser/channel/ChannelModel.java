package net.rim.device.apps.internal.browser.channel;

import java.io.InputStream;
import net.rim.device.api.browser.field.RenderingOptions;
import net.rim.device.api.browser.field.RenderingSession;
import net.rim.device.api.collection.CollectionListener;
import net.rim.device.api.io.http.HttpHeaders;
import net.rim.device.api.synchronization.SyncObject;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.WeakReferenceUtilities;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.ConversionProvider;
import net.rim.device.apps.api.framework.model.EncryptableProvider;
import net.rim.device.apps.api.framework.model.KeyProvider;
import net.rim.device.apps.api.framework.model.PersistableRIMModel;
import net.rim.device.apps.api.framework.model.SyncBuffer;
import net.rim.device.apps.api.framework.model.VerbProvider;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.messaging.Folder;
import net.rim.device.apps.api.messaging.FolderHierarchies;
import net.rim.device.apps.internal.browser.common.RenderingUtilities;
import net.rim.device.apps.internal.browser.core.BrowserDaemonRegistry;
import net.rim.device.apps.internal.browser.core.BrowserImpl;
import net.rim.device.apps.internal.browser.core.RenderingSessionImpl;
import net.rim.device.apps.internal.browser.img.ImageRenderingConverter;
import net.rim.device.apps.internal.browser.options.BrowserConfigRecord;
import net.rim.device.apps.internal.browser.stack.CacheResult;
import net.rim.device.apps.internal.browser.stack.FetchRequest;
import net.rim.device.apps.internal.browser.stack.ModelResult;
import net.rim.device.apps.internal.browser.stack.RawDataCache;
import net.rim.device.apps.internal.browser.store.BrowserFolders;
import net.rim.device.apps.internal.browser.util.RendererControl;
import net.rim.vm.Array;
import net.rim.vm.WeakReference;

public final class ChannelModel implements PersistableRIMModel, SyncObject, VerbProvider, KeyProvider, ConversionProvider, EncryptableProvider {
   private String _id;
   private Object _urlEncoding;
   private Object _deleteUrlEncoding;
   private Object _readIconUrlEncoding;
   private Object _unreadIconUrlEncoding;
   private Object _titleEncoding;
   private Object _descriptionEncoding;
   private Object _configUidEncoding;
   private Object _transportCidEncoding;
   private CacheResult _readIconData;
   private CacheResult _unreadIconData;
   private long _timestamp;
   private long _luid;
   private long _parentFolderLuid;
   private int _ribbonPosition;
   private int _status;
   private int _priority;
   private int _configType;
   public static final int PRIORITY_NONE = 0;
   public static final int PRIORITY_LOW = 1;
   public static final int PRIORITY_MEDIUM = 2;
   public static final int PRIORITY_HIGH = 3;
   public static final int DEFAULT_PRIORITY = 0;
   public static final int STATUS_READ = 0;
   public static final int STATUS_UNREAD = 1;
   private static final int VERSION = 7;
   private static WeakReference _dataBufferWR = (WeakReference)(new Object(null));

   public final String getID() {
      return this._id;
   }

   @Override
   public final Verb getVerbs(Object context, Verb[] verbs) {
      if (ContextObject.getFlag(context, 87)) {
         return null;
      }

      Verb defaultVerb = null;
      Array.resize(verbs, 1);
      verbs[0] = new OpenChannelVerb(this, 309, 1315152);
      if (ContextObject.getFlag(context, 2)) {
         defaultVerb = verbs[0];
      }

      return defaultVerb;
   }

   public final String getDeleteURL() {
      return PersistentContent.decodeString(this._deleteUrlEncoding);
   }

   @Override
   public final int getUID() {
      return (int)this._luid;
   }

   public final String getUnreadIconURL() {
      return PersistentContent.decodeString(this._unreadIconUrlEncoding);
   }

   @Override
   public final boolean convert(Object context, Object target) {
      if (!(target instanceof Object)) {
         return false;
      }

      SyncBuffer syncBuffer = (SyncBuffer)target;
      return this.writeChannelModel(syncBuffer);
   }

   public final String getDescription() {
      return PersistentContent.decodeString(this._descriptionEncoding);
   }

   public final int getRibbonPosition() {
      return this._ribbonPosition;
   }

   public final String getConfigUID() {
      return PersistentContent.decodeString(this._configUidEncoding);
   }

   public final String getTransportCID() {
      return PersistentContent.decodeString(this._transportCidEncoding);
   }

   public final int getConfigType() {
      return this._configType;
   }

   public final CacheResult getReadIconData() {
      return this._readIconData;
   }

   public final void setReadIconData(CacheResult iconData) {
      this._readIconData = iconData;
   }

   public final CacheResult getUnreadIconData() {
      return this._unreadIconData;
   }

   public final void setUneadIconData(CacheResult iconData) {
      this._unreadIconData = iconData;
   }

   public final long getTimestamp() {
      return this._timestamp;
   }

   public final void setTimestamp(long timestamp) {
      this._timestamp = timestamp;
   }

   public final long getLUID() {
      return this._luid;
   }

   public final void setLUID(long luid) {
      this._luid = luid;
   }

   public final long getParentFolderLUID() {
      return this._parentFolderLuid;
   }

   public final void setParentFolderLUID(long luid) {
      this._parentFolderLuid = luid;
   }

   public final int getStatus() {
      return this._status;
   }

   public final void setStatus(int status) {
      this._status = status;
   }

   public final int getPriority() {
      return this._priority;
   }

   public final Bitmap getReadIcon() {
      if (this._readIconUrlEncoding == null) {
         this._readIconData = null;
         return null;
      }

      if (this._readIconData != null && !this._readIconData.getURLWithoutFragment().equals(this.getReadIconURL())) {
         this._readIconData = null;
      }

      Bitmap icon = null;
      CacheResult cacheResult = this.getUrl(this.getReadIconURL());
      if (cacheResult != null) {
         icon = this.getIcon(cacheResult);
      }

      if (icon != null) {
         this._readIconData = cacheResult;
         return icon;
      }

      if (this._readIconData != null) {
         icon = this.getIcon(this._readIconData);
      }

      this.addPendingRequest(this.getReadIconURL(), 0);
      return icon;
   }

   public final Bitmap getUnreadIcon() {
      if (this._unreadIconUrlEncoding == null) {
         this._unreadIconData = null;
         return null;
      }

      if (this._unreadIconData != null && !this._unreadIconData.getURLWithoutFragment().equals(this.getUnreadIconURL())) {
         this._unreadIconData = null;
      }

      Bitmap icon = null;
      CacheResult cacheResult = this.getUrl(this.getUnreadIconURL());
      if (cacheResult != null) {
         icon = this.getIcon(cacheResult);
      }

      if (icon != null) {
         this._unreadIconData = cacheResult;
         return icon;
      }

      if (this._unreadIconData != null) {
         icon = this.getIcon(this._unreadIconData);
      }

      this.addPendingRequest(this.getUnreadIconURL(), 1);
      return icon;
   }

   public final String getReadIconURL() {
      return PersistentContent.decodeString(this._readIconUrlEncoding);
   }

   public final String getURL() {
      return PersistentContent.decodeString(this._urlEncoding);
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   final Bitmap getIcon(CacheResult cacheResult) {
      Bitmap icon = null;
      if (cacheResult != null && cacheResult.getStatus() >= 200 && cacheResult.getStatus() < 300) {
         byte[] data = this.readData(cacheResult.getStream());
         if (data != null) {
            try {
               return Bitmap.createBitmapFromBytes(data, 0, data.length, 1);
            } catch (Throwable var6) {
               EventLogger.logEvent(
                  1907089860548946979L, ((StringBuffer)(new Object("CMgi - "))).append(this._id).append('\n').append(x.toString()).toString().getBytes(), 0
               );
               return icon;
            }
         } else {
            EventLogger.logEvent(1907089860548946979L, ((StringBuffer)(new Object("CMgi - "))).append(this._id).append("\nnd").toString().getBytes(), 0);
            return icon;
         }
      } else if (cacheResult == null) {
         EventLogger.logEvent(1907089860548946979L, ((StringBuffer)(new Object("CMgi - "))).append(this._id).append("\nncr").toString().getBytes(), 0);
         return icon;
      } else {
         EventLogger.logEvent(
            1907089860548946979L,
            ((StringBuffer)(new Object("CMgi - ")))
               .append(this._id)
               .append('\n')
               .append(cacheResult.getStatus())
               .append(' ')
               .append(cacheResult.getExceptionString())
               .append(' ')
               .append(cacheResult.getExceptionDetail())
               .toString()
               .getBytes(),
            0
         );
         return icon;
      }
   }

   public final String getTitle() {
      try {
         return PersistentContent.decodeString(this._titleEncoding);
      } finally {
         ;
      }
   }

   @Override
   public final int getKeys(Object context, long[] keyArray, int index, long keyRequested) {
      if (keyRequested == 92199951187614847L) {
         keyArray[index] = this.getTimestamp();
         return 1;
      } else {
         return 0;
      }
   }

   @Override
   public final int getKeys(Object context, int[] keyArray, int index, long keyRequested) {
      return 0;
   }

   @Override
   public final int getKeys(Object context, Object[] keyArray, int index, long keyRequested) {
      return 0;
   }

   @Override
   public final boolean checkCrypt(boolean compress, boolean encrypt) {
      return PersistentContent.checkEncoding(this._titleEncoding, compress, encrypt)
         && PersistentContent.checkEncoding(this._urlEncoding, compress, encrypt)
         && PersistentContent.checkEncoding(this._deleteUrlEncoding, compress, encrypt)
         && PersistentContent.checkEncoding(this._readIconUrlEncoding, compress, encrypt)
         && PersistentContent.checkEncoding(this._unreadIconUrlEncoding, compress, encrypt)
         && PersistentContent.checkEncoding(this._descriptionEncoding, compress, encrypt)
         && (this._readIconData == null || this._readIconData.checkCrypt(compress, encrypt))
         && (this._unreadIconData == null || this._unreadIconData.checkCrypt(compress, encrypt));
   }

   @Override
   public final Object reCrypt(boolean compress, boolean encrypt) {
      this._titleEncoding = PersistentContent.reEncode(this._titleEncoding, compress, encrypt);
      this._urlEncoding = PersistentContent.reEncode(this._urlEncoding, compress, encrypt);
      this._deleteUrlEncoding = PersistentContent.reEncode(this._deleteUrlEncoding, compress, encrypt);
      this._readIconUrlEncoding = PersistentContent.reEncode(this._readIconUrlEncoding, compress, encrypt);
      this._unreadIconUrlEncoding = PersistentContent.reEncode(this._unreadIconUrlEncoding, compress, encrypt);
      this._descriptionEncoding = PersistentContent.reEncode(this._descriptionEncoding, compress, encrypt);
      if (this._readIconData != null) {
         this._readIconData.reCrypt(compress, encrypt);
      }

      if (this._unreadIconData != null) {
         this._unreadIconData.reCrypt(compress, encrypt);
      }

      return null;
   }

   public static final void changeStatus(ChannelModel model, int newStatus) {
      int currentStatus = model.getStatus();
      if (currentStatus != newStatus) {
         model.setStatus(newStatus);
         PersistentObject.commit(model);
         Folder parentFolder = FolderHierarchies.getFolder(BrowserFolders.RIM_BROWSER_CHANNELS_HIERARCHY_ID, model.getParentFolderLUID());
         if (parentFolder != null) {
            CollectionListener channelItems = (CollectionListener)parentFolder.getContainedItems();
            channelItems.elementUpdated(null, model, model);
         }

         Channels.addChannelToRibbon(model);
         BrowserDaemonRegistry.broadCastEvent(152, model);
      }
   }

   private final CacheResult getUrl(String url) {
      CacheResult cacheResult = null;
      if (url != null) {
         BrowserImpl browserImpl = BrowserDaemonRegistry.getInstance();
         HttpHeaders requestHeaders = (HttpHeaders)(new Object());
         RenderingUtilities.setTranscodeHeader(requestHeaders, false);
         RawDataCache rawDataCache = browserImpl.getRawDataCache();
         return rawDataCache.get(url, 1, null, null, requestHeaders, false);
      } else {
         EventLogger.logEvent(1907089860548946979L, ((StringBuffer)(new Object("CMgu - "))).append(this._id).append("\nnu").toString().getBytes(), 0);
         return cacheResult;
      }
   }

   private final void addPendingRequest(String url, int state) {
      String configUID = this.getConfigUID();
      String transportCID = this.getTransportCID();
      if (transportCID == null) {
         transportCID = BrowserConfigRecord.IPPP_SERVICE_CID;
      }

      BrowserConfigRecord browserConfigRecord = BrowserConfigRecord.getDecodedConfig(configUID, this.getConfigType(), transportCID);
      if (browserConfigRecord != null) {
         configUID = browserConfigRecord.getUid();
         transportCID = browserConfigRecord.getPropertyAsString(3);
      }

      BrowserImpl browserImpl = BrowserDaemonRegistry.getInstance();
      HttpHeaders requestHeaders = (HttpHeaders)(new Object());
      requestHeaders.setProperty("Accept", ImageRenderingConverter.getAcceptString());
      RenderingUtilities.setTranscodeHeader(requestHeaders, false);
      RenderingSession renderingSession = RenderingSessionImpl.getNewInstance();
      RenderingOptions renderingOptions = renderingSession.getRenderingOptions();
      browserImpl.setRenderingOptions(renderingOptions, browserConfigRecord);
      browserImpl.addStandardRequestHeaders(requestHeaders, renderingSession);
      ModelResult modelResult = new ModelResult(url, 1, requestHeaders);
      modelResult.setConfigUID(configUID);
      modelResult.setTransportCID(transportCID);
      FetchRequest fetchRequest = new FetchRequest(modelResult, browserConfigRecord, 8);
      fetchRequest.addPendingRequest(new IconLoadListener(this, state));
   }

   private final byte[] readData(InputStream inputStream) {
      if (inputStream != null) {
         return RendererControl.readBytesFromInputStream(inputStream);
      }

      EventLogger.logEvent(1907089860548946979L, ((StringBuffer)(new Object("CMrd - "))).append(this._id).append("\nnis").toString().getBytes(), 0);
      return null;
   }

   @Override
   public final String toString() {
      return this._titleEncoding != null ? this.getTitle() : this.getURL();
   }

   public ChannelModel(
      String id,
      String url,
      String deleteUrl,
      String readIconUrl,
      String unreadIconUrl,
      String title,
      String description,
      int ribbonPosition,
      int priority,
      String configUid,
      int configType,
      String transportCid
   ) {
      this._id = id;
      this._titleEncoding = title;
      this._urlEncoding = url;
      this._deleteUrlEncoding = deleteUrl;
      this._readIconUrlEncoding = readIconUrl;
      this._unreadIconUrlEncoding = unreadIconUrl;
      this._descriptionEncoding = description;
      this._configUidEncoding = configUid;
      this._ribbonPosition = ribbonPosition;
      this._status = 1;
      this._priority = priority;
      if (transportCid == null) {
         this._transportCidEncoding = BrowserConfigRecord.IPPP_SERVICE_CID;
      } else {
         this._transportCidEncoding = transportCid;
      }

      this._configType = configType;
   }

   private final boolean writeChannelModel(SyncBuffer syncBuffer) {
      try {
         DataBuffer dataBuffer = WeakReferenceUtilities.getDataBuffer(_dataBufferWR, false);
         dataBuffer.setLength(0);
         dataBuffer.writeUTF(this.getID());
         dataBuffer.writeUTF(this.getURL());
         if (this.getReadIconURL() == null) {
            dataBuffer.writeBoolean(false);
         } else {
            dataBuffer.writeBoolean(true);
            dataBuffer.writeUTF(this.getReadIconURL());
         }

         if (this.getUnreadIconURL() == null) {
            dataBuffer.writeBoolean(false);
         } else {
            dataBuffer.writeBoolean(true);
            dataBuffer.writeUTF(this.getUnreadIconURL());
         }

         if (this.getTitle() == null) {
            dataBuffer.writeBoolean(false);
         } else {
            dataBuffer.writeBoolean(true);
            dataBuffer.writeUTF(this.getTitle());
         }

         if (this.getDescription() == null) {
            dataBuffer.writeBoolean(false);
         } else {
            dataBuffer.writeBoolean(true);
            dataBuffer.writeUTF(this.getDescription());
         }

         dataBuffer.writeCompressedLong(this.getTimestamp());
         dataBuffer.writeCompressedLong(this.getLUID());
         dataBuffer.writeCompressedLong(this.getParentFolderLUID());
         dataBuffer.writeCompressedInt(this.getRibbonPosition());
         dataBuffer.writeCompressedInt(this.getStatus());
         dataBuffer.writeCompressedInt(7);
         String uid = this.getConfigUID();
         if (uid == null) {
            dataBuffer.writeBoolean(false);
         } else {
            dataBuffer.writeBoolean(true);
            dataBuffer.writeUTF(uid);
         }

         uid = this.getTransportCID();
         if (uid == null) {
            dataBuffer.writeBoolean(false);
         } else {
            dataBuffer.writeBoolean(true);
            dataBuffer.writeUTF(uid);
         }

         dataBuffer.writeCompressedInt(this.getConfigType());
         dataBuffer.writeCompressedInt(this.getPriority());
         uid = this.getDeleteURL();
         if (uid == null) {
            dataBuffer.writeBoolean(false);
         } else {
            dataBuffer.writeBoolean(true);
            dataBuffer.writeUTF(uid);
         }

         syncBuffer.addBytes(21, dataBuffer.toArray());
         return true;
      } finally {
         ;
      }
   }
}
