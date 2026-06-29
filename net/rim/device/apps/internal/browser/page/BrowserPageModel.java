package net.rim.device.apps.internal.browser.page;

import net.rim.device.api.collection.CollectionListener;
import net.rim.device.api.collection.ReadableList;
import net.rim.device.api.collection.WritableSet;
import net.rim.device.api.io.http.HttpHeaders;
import net.rim.device.api.lowmemory.LowMemoryManager;
import net.rim.device.api.notification.NotificationsManager;
import net.rim.device.api.synchronization.SyncObject;
import net.rim.device.api.synchronization.UIDGenerator;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.api.util.WeakReferenceUtilities;
import net.rim.device.apps.api.framework.model.ActionProvider;
import net.rim.device.apps.api.framework.model.ColumnPaintProvider;
import net.rim.device.apps.api.framework.model.ColumnPainter;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.ConversionProvider;
import net.rim.device.apps.api.framework.model.EncryptableProvider;
import net.rim.device.apps.api.framework.model.FolderProvider;
import net.rim.device.apps.api.framework.model.HotKeyProvider;
import net.rim.device.apps.api.framework.model.KeyProvider;
import net.rim.device.apps.api.framework.model.SyncBuffer;
import net.rim.device.apps.api.framework.model.UniqueIDProvider;
import net.rim.device.apps.api.framework.model.VerbProvider;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.messaging.Folder;
import net.rim.device.apps.api.messaging.FolderHierarchies;
import net.rim.device.apps.api.ribbon.indicators.UnreadCountManager;
import net.rim.device.apps.api.utility.lowMemory.PurgeProvider;
import net.rim.device.apps.internal.browser.core.IBrowserContext;
import net.rim.device.apps.internal.browser.stack.HeaderParser;
import net.rim.device.apps.internal.browser.stack.ModelResult;
import net.rim.device.apps.internal.browser.store.BrowserFolders;
import net.rim.device.apps.internal.browser.ui.BrowserIcons;
import net.rim.device.apps.internal.browser.verbs.ChangeStatusVerb;
import net.rim.device.apps.internal.browser.verbs.OpenFromMessageListVerb;
import net.rim.vm.Array;
import net.rim.vm.WeakReference;

public final class BrowserPageModel
   implements PageModel,
   SyncObject,
   VerbProvider,
   KeyProvider,
   ColumnPaintProvider,
   UniqueIDProvider,
   ActionProvider,
   ConversionProvider,
   HotKeyProvider,
   PurgeProvider,
   EncryptableProvider,
   FolderProvider {
   private long _luid;
   private long _timeStamp;
   private int _status;
   private Object _titleEncoding;
   private ModelResult _modelResult;
   private long _parentFolderId;
   private boolean _homePage;
   private Object _iconUrlEncoding;
   private byte _updateFlags;
   private int _updateStart;
   private int _updatePeriod;
   private long _lastAccessedTime;
   private static final int PAGE_MODEL_VERSION = 4;
   private static final int MODEL_RESULT_VERSION = 4;
   public static final int AUTO_UPDATE_NONE = 0;
   public static final int AUTO_UPDATE_WHEN_PERIOD_EXPIRED = 1;
   static final int ICON_PAINT_WIDTH = 15;
   static final int TIME_PAINT_WIDTH = 35;
   static final int DESCRIPTION_PAINT_WIDTH = 105;
   static final int ICON_COLUMN = 0;
   static final int TIME_COLUMN = 1;
   static final int DESCRIPTION_COLUMN = 2;
   private static WeakReference _dataBufferWR = new WeakReference(null);
   private static ContextObject _notificationsContext = new ContextObject();

   public final void setStatus(int status) {
      this._status = status;
   }

   @Override
   public final Verb getVerbs(Object context, Verb[] verbs) {
      if (ContextObject.getFlag(context, 87)) {
         return null;
      }

      int pageModelStatus = this.getStatus();
      switch (pageModelStatus) {
         case 0:
            return null;
         case 1:
         case 2:
         case 5:
         default:
            Array.resize(verbs, 1);
            verbs[0] = new OpenFromMessageListVerb(this);
            return verbs[0];
         case 3:
         case 4:
            boolean loaded = pageModelStatus == 3;
            Array.resize(verbs, 2);
            verbs[0] = new OpenFromMessageListVerb(this);
            verbs[1] = new ChangeStatusVerb(this, loaded ? 4 : 3, loaded ? 1325 : 1350);
            return verbs[0];
      }
   }

   @Override
   public final long getLUID(Object context) {
      return this.getLUID();
   }

   @Override
   public final int getUID() {
      return (int)this._luid;
   }

   @Override
   public final boolean perform(long actionId, Object context) {
      if (actionId == -3967872215949752466L) {
         Folder browserFolder = FolderHierarchies.getFolder(BrowserFolders.RIM_BROWSER_MESSAGES_HIERARCHY_ID, BrowserFolders.BROWSER_MESSAGES_FOLDER_ID);
         if (browserFolder != null) {
            this.changeStatus(4);
            WritableSet browserItems = (WritableSet)browserFolder.getContainedItems();
            browserItems.remove(this);
            return true;
         }
      } else {
         if (actionId == -6225946334564270161L) {
            this.changeStatus(4);
            return true;
         }

         if (actionId == 5803508244060051872L) {
            this.changeStatus(4);
            return true;
         }

         if (actionId == -8629311385729242560L) {
            this.changeStatus(3);
            return true;
         }

         if (actionId == -5544992959212130441L) {
            if (this.getStatus() == 3) {
               return true;
            }

            return false;
         }

         if (actionId == 477896226347912237L) {
            if (this.getStatus() == 4) {
               return true;
            }

            return false;
         }

         if (actionId == 6780594967363292755L) {
            Folder browserFolder = FolderHierarchies.getFolder(BrowserFolders.RIM_BROWSER_MESSAGES_HIERARCHY_ID, BrowserFolders.BROWSER_MESSAGES_FOLDER_ID);
            if (browserFolder != null) {
               this.changeStatus(4);
               WritableSet browserItems = (WritableSet)browserFolder.getContainedItems();
               browserItems.remove(this);
               return true;
            }

            return false;
         }

         if (actionId == 4951292880494466830L) {
            int status = this.getStatus();
            if (status == 3 && this.isInMessageList()) {
               UnreadCountManager.incrementUnreadCount(6);
            }

            return true;
         }

         if (actionId == -198247372487919817L) {
            this.changeStatus(4);
         }
      }

      return false;
   }

   @Override
   public final boolean convert(Object context, Object target) {
      if (!(target instanceof SyncBuffer)) {
         return false;
      }

      SyncBuffer syncBuffer = (SyncBuffer)target;
      return this.writeBrowserPageModel(syncBuffer);
   }

   public final void setUpdateFlags(byte flags) {
      this._updateFlags = flags;
   }

   @Override
   public final Object invokeHotkey(Object context, int hotkeyID) {
      switch (hotkeyID) {
         case 152:
            if (this.getStatus() == 4) {
               Verb verb = new ChangeStatusVerb(this, 3, 1350);
               return verb.invoke(context);
            } else {
               if (this.getStatus() == 3) {
                  Verb verb = new ChangeStatusVerb(this, 4, 1325);
                  return verb.invoke(context);
               }

               return null;
            }
         default:
            return null;
      }
   }

   final void setModelResult(ModelResult modelResult) {
      this._modelResult = modelResult;
   }

   public final void setIconUrl(String iconUrl) {
      this._iconUrlEncoding = PersistentContent.encode(iconUrl, false, true);
   }

   @Override
   public final boolean isCompleted() {
      switch (this.getStatus()) {
         case -1:
            return true;
         case 0:
         case 1:
         case 2:
         default:
            return false;
      }
   }

   @Override
   public final String getTitle() {
      try {
         return PersistentContent.decodeString(this._titleEncoding);
      } finally {
         ;
      }
   }

   @Override
   public final void setTitle(String title) {
      this._titleEncoding = PersistentContent.encode(title, false, true);
   }

   @Override
   public final String getIconUrl() {
      try {
         return PersistentContent.decodeString(this._iconUrlEncoding);
      } finally {
         ;
      }
   }

   @Override
   public final void setLastAccessedTime(long time) {
      this._lastAccessedTime = time;
   }

   @Override
   public final ModelResult getModelResult() {
      return this._modelResult;
   }

   @Override
   public final String getUrl() {
      return this._modelResult != null ? this._modelResult.getURL() : null;
   }

   @Override
   public final void setLUID(long luid) {
      this._luid = luid;
   }

   @Override
   public final long getLastAccessedTime() {
      return this._lastAccessedTime;
   }

   @Override
   public final boolean isHomePage() {
      return this._homePage;
   }

   @Override
   public final int getUpdateStart() {
      return this._updateStart;
   }

   @Override
   public final int getUpdatePeriod() {
      return this._updatePeriod;
   }

   @Override
   public final byte getUpdateFlags() {
      return this._updateFlags;
   }

   @Override
   public final int getStatus() {
      return this._status;
   }

   @Override
   public final int getKeys(Object context, Object[] keyArray, int index, long keyRequested) {
      return 0;
   }

   @Override
   public final int getKeys(Object context, int[] keyArray, int index, long keyRequested) {
      return 0;
   }

   @Override
   public final int getKeys(Object context, long[] keyArray, int index, long keyRequested) {
      if (keyRequested != 92199951187614847L && keyRequested != -7628247220259263034L) {
         return 0;
      }

      keyArray[index] = this.getTimeStamp();
      return 1;
   }

   @Override
   public final boolean canPurge(int purgeType) {
      return purgeType == 0 ? this.isCompleted() : false;
   }

   @Override
   public final void purge(int purgeType) {
      if (this.perform(6780594967363292755L, null)) {
         LowMemoryManager.markAsRecoverable(this);
      }
   }

   @Override
   public final long getTimeStamp() {
      return this._timeStamp;
   }

   @Override
   public final void setFolderId(long parentFolderId) {
      this._parentFolderId = parentFolderId;
   }

   @Override
   public final void paint(ColumnPainter painter, Object context) {
      int icon;
      switch (this.getStatus()) {
         case 0:
            icon = 3;
            break;
         case 1:
         default:
            icon = 0;
            break;
         case 2:
            icon = 1;
            break;
         case 3:
            icon = 2;
            break;
         case 4:
            icon = 3;
            break;
         case 5:
            icon = 4;
      }

      painter.drawIcon(1, BrowserIcons.getIcons(), icon);
      painter.drawTime(2, this.getTimeStamp());
      if (painter.isColumnEmpty(3)) {
         painter.drawText(3, this.toString(), true);
      } else {
         if (this.getStatus() != 4) {
            painter.setEmphasis(true);
         }

         painter.drawText(4, this.toString(), true);
      }
   }

   @Override
   public final long getLUID() {
      return this._luid;
   }

   @Override
   public final void changeStatus(int newStatus) {
      int currentStatus = this.getStatus();
      if (currentStatus != newStatus) {
         switch (currentStatus) {
            case -1:
               break;
            case 0:
            default:
               if (newStatus == 3) {
                  boolean listUpdated = this.updateListWithLoadedPage();
                  if (this._parentFolderId != 0 && FolderHierarchies.getFolder(BrowserFolders.RIM_BROWSER_MESSAGES_HIERARCHY_ID, this._parentFolderId) != null) {
                     UnreadCountManager.incrementUnreadCount(6);
                     if (listUpdated) {
                        NotificationsManager.triggerImmediateEvent(4665536253483290822L, 0, null, _notificationsContext);
                     }
                  }
               }
               break;
            case 1:
               if (newStatus == 3) {
                  this.updateListWithLoadedPage();
                  if (this.isInMessageList()) {
                     UnreadCountManager.incrementUnreadCount(6);
                     NotificationsManager.triggerImmediateEvent(4665536253483290822L, 0, null, _notificationsContext);
                  }
               } else if (newStatus == 4) {
                  return;
               }
               break;
            case 2:
               if (newStatus == 3) {
                  if (this.isInMessageList()) {
                     UnreadCountManager.incrementUnreadCount(6);
                     NotificationsManager.triggerImmediateEvent(4665536253483290822L, 0, null, _notificationsContext);
                  }
               } else if (newStatus == 4) {
                  return;
               }
               break;
            case 3:
               if (this.isInMessageList()) {
                  UnreadCountManager.decrementUnreadCount(6);
                  NotificationsManager.cancelImmediateEvent(4665536253483290822L, 0, null, _notificationsContext);
               }
               break;
            case 4:
               if (newStatus == 3 && this.isInMessageList()) {
                  UnreadCountManager.incrementUnreadCount(6);
               }
               break;
            case 5:
               if (newStatus == 3) {
                  if (this.isInMessageList()) {
                     UnreadCountManager.incrementUnreadCount(6);
                     NotificationsManager.triggerImmediateEvent(4665536253483290822L, 0, null, _notificationsContext);
                  }
               } else if (newStatus == 4) {
                  return;
               }
         }

         this.setStatus(newStatus);
         PersistentObject.commit(this);
         if (this._parentFolderId != 0) {
            Folder browserFolder = FolderHierarchies.getFolder(BrowserFolders.RIM_BROWSER_MESSAGES_HIERARCHY_ID, this._parentFolderId);
            if (browserFolder == null) {
               browserFolder = FolderHierarchies.getFolder(BrowserFolders.RIM_BROWSER_BOOKMARKS_HIERARCHY_ID, this._parentFolderId);
            }

            if (browserFolder != null) {
               ReadableList browserItems = (ReadableList)browserFolder.getContainedItems();
               if (browserItems.getIndex(this) != -1) {
                  ((CollectionListener)browserItems).elementUpdated(null, this, this);
               }
            }
         }
      }
   }

   @Override
   public final void setTimeStamp(long newTimeStamp) {
      this._timeStamp = newTimeStamp;
   }

   @Override
   public final long getFolderId() {
      return this._parentFolderId;
   }

   @Override
   public final boolean checkCrypt(boolean compress, boolean encrypt) {
      return PersistentContent.checkEncoding(this._titleEncoding, false, encrypt)
         && PersistentContent.checkEncoding(this._iconUrlEncoding, false, encrypt)
         && (this._modelResult == null || this._modelResult.checkCrypt(compress, encrypt));
   }

   @Override
   public final Object reCrypt(boolean compress, boolean encrypt) {
      this._titleEncoding = PersistentContent.reEncode(this._titleEncoding, false, encrypt);
      this._iconUrlEncoding = PersistentContent.reEncode(this._iconUrlEncoding, false, encrypt);
      if (this._modelResult != null) {
         this._modelResult.reCrypt(compress, encrypt);
      }

      return null;
   }

   public BrowserPageModel(long luid, long timeStamp, int status, String title, ModelResult modelResult, long parentFolderId) {
      this(luid, timeStamp, status, title, modelResult, parentFolderId, false, null, (byte)0, 0, 0);
   }

   public static final void registerWithNotificationsManager() {
      NotificationsManager.registerSource(4665536253483290822L, new BrowserPageModel$1(), 3);
   }

   public BrowserPageModel(long luid, int status, String title, ModelResult modelResult, long parentFolderId) {
      this(luid, System.currentTimeMillis(), status, title, modelResult, parentFolderId);
   }

   private final boolean updateListWithLoadedPage() {
      Folder browserFolder = FolderHierarchies.getFolder(BrowserFolders.RIM_BROWSER_MESSAGES_HIERARCHY_ID, BrowserFolders.BROWSER_MESSAGES_FOLDER_ID);
      synchronized (FolderHierarchies.getLockObject()) {
         WritableSet browserItems = (WritableSet)browserFolder.getContainedItems();
         if (!browserItems.contains(this)) {
            return false;
         }

         browserItems.remove(this);
         long timestamp = System.currentTimeMillis();
         this.setTimeStamp(timestamp);
         browserItems.add(this);
         return true;
      }
   }

   private final boolean isInMessageList() {
      Folder browserFolder = FolderHierarchies.getFolder(BrowserFolders.RIM_BROWSER_MESSAGES_HIERARCHY_ID, BrowserFolders.BROWSER_MESSAGES_FOLDER_ID);
      synchronized (FolderHierarchies.getLockObject()) {
         WritableSet browserItems = (WritableSet)browserFolder.getContainedItems();
         return browserItems.contains(this);
      }
   }

   public static final long makeLUID() {
      return UIDGenerator.getUID();
   }

   public BrowserPageModel(long luid, long timeStamp, int status, String title, ModelResult modelResult) {
      this(luid, timeStamp, status, title, modelResult, 0);
   }

   private final boolean writeBrowserPageModel(SyncBuffer syncBuffer) {
      DataBuffer dataBuffer = WeakReferenceUtilities.getDataBuffer(_dataBufferWR, false);
      dataBuffer.setLength(0);
      dataBuffer.writeCompressedLong(this._luid);
      dataBuffer.writeCompressedLong(this._timeStamp);
      dataBuffer.writeCompressedInt(this._status);
      if (this._titleEncoding == null) {
         dataBuffer.writeBoolean(false);
      } else {
         dataBuffer.writeBoolean(true);

         try {
            dataBuffer.writeUTF(this.getTitle());
         } finally {
            ;
         }
      }

      dataBuffer.writeCompressedLong(this._parentFolderId);
      dataBuffer.writeBoolean(this._homePage);
      dataBuffer.writeCompressedInt(4);
      String iconUrl = this.getIconUrl();
      if (iconUrl == null) {
         dataBuffer.writeBoolean(false);
      } else {
         dataBuffer.writeBoolean(true);

         try {
            dataBuffer.writeUTF(iconUrl);
         } finally {
            ;
         }
      }

      if (this._updateFlags == 0) {
         dataBuffer.writeCompressedInt(0);
      } else {
         dataBuffer.writeCompressedInt(1);
         dataBuffer.writeCompressedInt(this._updateFlags);
         dataBuffer.writeCompressedInt(this._updateStart);
         dataBuffer.writeCompressedInt(this._updatePeriod);
      }

      dataBuffer.writeCompressedLong(this._lastAccessedTime);
      syncBuffer.addBytes(17, dataBuffer.toArray());
      ModelResult modelResult = this._modelResult;
      if (modelResult == null) {
         return true;
      }

      try {
         return writeModelResult(modelResult, syncBuffer);
      } finally {
         ;
      }
   }

   private static final boolean writeModelResult(ModelResult modelResult, SyncBuffer syncBuffer) {
      DataBuffer dataBuffer = WeakReferenceUtilities.getDataBuffer(_dataBufferWR, false);
      dataBuffer.setLength(0);
      dataBuffer.writeUTF(modelResult.getURL());
      dataBuffer.writeBoolean((modelResult.getRenderingFlags() & 8192) != 0);
      dataBuffer.writeCompressedInt(modelResult.getRenderingFlags());
      HttpHeaders requestHeaders = modelResult.getRequestHeaders();
      if (requestHeaders == null) {
         dataBuffer.writeCompressedInt(0);
      } else {
         dataBuffer.writeCompressedInt(requestHeaders.size());
         int size = requestHeaders.size();

         for (int i = 0; i < size; i++) {
            String key = requestHeaders.getPropertyKey(i);
            dataBuffer.writeUTF(key);
            dataBuffer.writeUTF(requestHeaders.getPropertyValue(i));
         }
      }

      dataBuffer.writeCompressedInt(0);
      String referrer = null;
      if (requestHeaders != null) {
         referrer = requestHeaders.getPropertyValue(HeaderParser.REFERER);
      }

      if (referrer == null) {
         dataBuffer.writeBoolean(false);
      } else {
         dataBuffer.writeBoolean(true);
         dataBuffer.writeUTF(referrer);
      }

      byte[] postData = modelResult.getPostData();
      String contentType = null;
      if (postData != null
         && requestHeaders != null
         && (contentType = requestHeaders.getPropertyValue("Content-Type")) != null
         && StringUtilities.startsWithIgnoreCase(contentType, "application/x-www-form-urlencoded", 1701707776)) {
         dataBuffer.writeBoolean(true);
         dataBuffer.writeUTF(new String(postData));
         postData = null;
      } else {
         dataBuffer.writeBoolean(false);
      }

      dataBuffer.writeBoolean(modelResult.isHomePage());
      String uid = modelResult.getConfigUID();
      if (uid == null) {
         dataBuffer.writeBoolean(false);
      } else {
         dataBuffer.writeBoolean(true);
         dataBuffer.writeUTF(uid);
      }

      dataBuffer.writeBoolean(false);
      dataBuffer.writeCompressedInt(4);
      if (postData != null) {
         dataBuffer.writeBoolean(true);
         dataBuffer.writeByteArray(postData);
      } else {
         dataBuffer.writeBoolean(false);
      }

      uid = modelResult.getTransportCID();
      if (uid == null) {
         dataBuffer.writeBoolean(false);
      } else {
         dataBuffer.writeBoolean(true);
         dataBuffer.writeUTF(uid);
      }

      dataBuffer.writeCompressedInt(modelResult.getConfigType());
      syncBuffer.addBytes(18, dataBuffer.toArray());
      IBrowserContext context = (IBrowserContext)modelResult.getContext();
      if (context != null) {
         context.serialize(syncBuffer);
      }

      return modelResult.getCacheResult() == null ? true : modelResult.getCacheResult().writeCacheResult(syncBuffer);
   }

   public BrowserPageModel(
      long luid,
      long timeStamp,
      int status,
      String title,
      ModelResult modelResult,
      long parentFolderId,
      boolean homePage,
      String iconUrl,
      byte updateFlags,
      int updateStart,
      int updatePeriod
   ) {
      this._luid = luid;
      this._timeStamp = timeStamp;
      this._status = status;
      this._modelResult = modelResult;
      this._parentFolderId = parentFolderId;
      this._homePage = homePage;
      this._updateFlags = updateFlags;
      this._updateStart = updateStart;
      this._updatePeriod = updatePeriod;
      this.setTitle(title);
      this.setIconUrl(iconUrl);
   }

   @Override
   public final String toString() {
      return this._titleEncoding != null ? this.getTitle() : this.getUrl();
   }

   public BrowserPageModel(int status, String title, ModelResult modelResult, long parentFolderId) {
      this(makeLUID(), status, title, modelResult, parentFolderId);
   }

   public BrowserPageModel(int status, String title, ModelResult modelResult) {
      this(status, title, modelResult, 0);
   }

   static {
      _notificationsContext.putIntegerData(0);
   }
}
