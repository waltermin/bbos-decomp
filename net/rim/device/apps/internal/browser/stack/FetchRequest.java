package net.rim.device.apps.internal.browser.stack;

import javax.microedition.io.SecurityInfo;
import net.rim.device.api.browser.field.Event;
import net.rim.device.api.collection.WritableSet;
import net.rim.device.apps.api.messaging.Folder;
import net.rim.device.apps.api.messaging.FolderHierarchies;
import net.rim.device.apps.internal.browser.common.AbortListener;
import net.rim.device.apps.internal.browser.core.BrowserDaemonRegistry;
import net.rim.device.apps.internal.browser.core.BrowserImpl;
import net.rim.device.apps.internal.browser.core.BrowserSession;
import net.rim.device.apps.internal.browser.core.PendingRequestListener;
import net.rim.device.apps.internal.browser.options.BrowserConfigRecord;
import net.rim.device.apps.internal.browser.page.BrowserPageModel;
import net.rim.device.apps.internal.browser.page.PageModel;
import net.rim.device.apps.internal.browser.store.BrowserFolders;
import net.rim.device.apps.internal.browser.util.Frame;
import net.rim.device.internal.system.InternalServices;

public final class FetchRequest {
   private ModelResult _modelResult;
   private AbortListener _abortListener;
   private Object _abortContext;
   private PageModel _savedPageModel;
   private SecurityInfo _securityInfo;
   private Object _error;
   private Frame _target;
   private BrowserConfigRecord _browserConfigRecord;
   private long _timeStarted;
   private int _flags;
   private Event _event;
   public static final int FLAG_ABORTED = 1;
   public static final int FLAG_SAVED_REQUEST = 4;
   public static final int FLAG_ADD_TO_LONG_TERM_CACHE = 8;
   public static final int FLAG_DIRTY_CACHE_HANDLING = 16;
   public static final int FLAG_PROGRAMMATIC = 32;
   public static final int FLAG_ESCAPE_EVENT = 64;
   public static final int FLAG_SAVE_ALLOWED = 512;
   public static final int FLAG_HISTORY_REQUEST = 1024;
   public static final int FLAG_SHORT_TIMEOUT = 2048;
   public static final int FLAG_RETURN_ORIGINAL_RESPONSE_CODE = 4096;

   public FetchRequest(ModelResult modelResult, int flags) {
      this._modelResult = modelResult;
      this._flags = flags;
      this._timeStarted = InternalServices.getUptime();
   }

   public FetchRequest(ModelResult modelResult) {
      this(modelResult, 0);
   }

   public FetchRequest(ModelResult modelResult, BrowserConfigRecord browserConfigRecord) {
      this(modelResult, browserConfigRecord, 0);
   }

   public FetchRequest(ModelResult modelResult, BrowserConfigRecord browserConfigRecord, int flags) {
      this(modelResult, flags);
      this._browserConfigRecord = browserConfigRecord;
   }

   public FetchRequest(PageModel savedPageModel) {
      this._modelResult = savedPageModel.getModelResult();
      this._savedPageModel = savedPageModel;
      this._modelResult.setNavigation(3);
      this._browserConfigRecord = BrowserConfigRecord.getDecodedConfig(
         this._modelResult.getConfigUID(), this._modelResult.getConfigType(), this._modelResult.getTransportCID()
      );
      this._flags = 4;
      this._timeStarted = InternalServices.getUptime();
   }

   public final void setEvent(Event event) {
      this._event = event;
   }

   public final Event getEvent() {
      return this._event;
   }

   public final synchronized void registerAbortListener(AbortListener abortListener, Object abortContext) {
      this._abortListener = abortListener;
      this._abortContext = abortContext;
   }

   public final synchronized void deRegisterAbortListener(AbortListener abortListener, Object abortContext) {
      if (this._abortListener == abortListener && this._abortContext == abortContext) {
         this._abortListener = null;
         this._abortContext = null;
      }
   }

   public final synchronized void abort() {
      if ((this._flags & 1) == 0) {
         this._flags |= 1;
         if (this._abortListener != null) {
            this._abortListener.abort(this._abortContext);
         }

         this._event = null;
         BrowserSession browserSession = BrowserSession.getCurrentSession();
         if (browserSession != null) {
            browserSession.requestCompleted();
         }
      }
   }

   public final synchronized void saveInMessageList(boolean addToSavedQueue, String uid, String cid) {
      this._flags |= 4;
      this._modelResult.setConfigUID(uid);
      this._modelResult.setTransportCID(cid);
      Folder browserFolder = FolderHierarchies.getFolder(BrowserFolders.RIM_BROWSER_MESSAGES_HIERARCHY_ID, BrowserFolders.BROWSER_MESSAGES_FOLDER_ID);
      if (browserFolder != null) {
         String pageTitle = this._modelResult.getURL();
         this._savedPageModel = new BrowserPageModel(0, pageTitle, this._modelResult, browserFolder.getLUID());
         BrowserPageModel browserModel = (BrowserPageModel)this._savedPageModel;
         if (!browserModel.checkCrypt(true, true)) {
            browserModel.reCrypt(true, true);
         }

         WritableSet browserItems = (WritableSet)browserFolder.getContainedItems();
         browserItems.add(this._savedPageModel);
         CacheResult cacheResult = this._modelResult.getCacheResult();
         if (cacheResult == null) {
            this._savedPageModel.changeStatus(1);
            if (addToSavedQueue) {
               BrowserDaemonRegistry.getInstance().addSavedRequest(this);
               return;
            }

            BrowserDaemonRegistry.getInstance().addPendingRequest(this, null);
            return;
         }

         int status = cacheResult.getStatus();
         if (status == 0) {
            this._savedPageModel.changeStatus(1);
            if (addToSavedQueue) {
               BrowserDaemonRegistry.getInstance().addSavedRequest(this);
               return;
            }

            BrowserDaemonRegistry.getInstance().addPendingRequest(this, null);
            return;
         }

         if (status >= 200 && status < 300) {
            if (cacheResult.getData().isClosed()) {
               this._savedPageModel.changeStatus(3);
               return;
            }

            this._savedPageModel.changeStatus(1);
            if (addToSavedQueue) {
               BrowserDaemonRegistry.getInstance().addSavedRequest(this);
               return;
            }

            BrowserDaemonRegistry.getInstance().addPendingRequest(this, null);
            return;
         }

         if (status < 200 || status >= 300) {
            this._savedPageModel.changeStatus(5);
         }
      }
   }

   public final synchronized void addToDownloadManager() {
      this._flags |= 4;
   }

   public final synchronized void addPendingRequest(PendingRequestListener listener) {
      this._flags |= 4;
      this._savedPageModel = new BrowserPageModel(0, null, this._modelResult);
      BrowserPageModel browserModel = (BrowserPageModel)this._savedPageModel;
      if (!browserModel.checkCrypt(true, true)) {
         browserModel.reCrypt(true, true);
      }

      this._savedPageModel.changeStatus(1);
      BrowserDaemonRegistry.getInstance().addPendingRequest(this, listener);
   }

   public final synchronized void addToQueue(String queue, String title) {
      if (title == null) {
         title = this._modelResult.getURL();
      }

      this._savedPageModel = new BrowserPageModel(0, title, this._modelResult);
      BrowserPageModel browserModel = (BrowserPageModel)this._savedPageModel;
      if (!browserModel.checkCrypt(true, true)) {
         browserModel.reCrypt(true, true);
      }

      BrowserImpl browserImpl = BrowserDaemonRegistry.getInstance();
      browserImpl.getOfflineQueue().addItem(queue, this._savedPageModel);
      browserImpl.addQueueRequest(this);
   }

   public final synchronized int getFlags() {
      return this._flags;
   }

   public final synchronized void setDirtyHandling(boolean value) {
      this._flags &= -17;
      if (value) {
         this._flags |= 16;
      }
   }

   public final synchronized ModelResult getModelResult() {
      return this._modelResult;
   }

   public final boolean isAborted() {
      return (this._flags & 1) != 0;
   }

   public final boolean isSaveAllowed() {
      return (this._flags & 512) != 0;
   }

   public final synchronized void setSaveAllowed(boolean value) {
      this._flags &= -513;
      if (value) {
         this._flags |= 512;
      }
   }

   public final boolean isHistoryRequest() {
      return (this._flags & 1024) != 0;
   }

   public final synchronized void setHistoryRequest(boolean value) {
      this._flags &= -1025;
      if (value) {
         this._flags |= 1024;
      }
   }

   public final synchronized PageModel getSavedPageModel() {
      return this._savedPageModel;
   }

   public final Object getError() {
      return this._error;
   }

   public final void setError(Object error) {
      this._error = error;
   }

   public final BrowserConfigRecord getBrowserConfigRecord() {
      return this._browserConfigRecord;
   }

   public final void setBrowserConfigRecord(BrowserConfigRecord browserConfigRecord) {
      this._browserConfigRecord = browserConfigRecord;
   }

   public final synchronized SecurityInfo getSecurityInfo() {
      return this._securityInfo;
   }

   public final synchronized void setSecurityInfo(SecurityInfo info) {
      this._securityInfo = info;
   }

   public final void setTarget(Frame frameset) {
      this._target = frameset;
   }

   public final Frame getTarget() {
      return this._target;
   }

   public final void recordStartTime() {
      this._timeStarted = InternalServices.getUptime();
   }

   public final long getStartTime() {
      return this._timeStarted;
   }
}
