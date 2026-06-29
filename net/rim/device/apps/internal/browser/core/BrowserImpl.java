package net.rim.device.apps.internal.browser.core;

import com.sun.cldc.i18n.Helper;
import java.io.DataInput;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import javax.microedition.io.HttpConnection;
import javax.microedition.io.InputConnection;
import net.rim.device.api.browser.field.BrowserContent;
import net.rim.device.api.browser.field.Event;
import net.rim.device.api.browser.field.RedirectEvent;
import net.rim.device.api.browser.field.RenderingOptions;
import net.rim.device.api.browser.field.RenderingSession;
import net.rim.device.api.browser.util.UAProf;
import net.rim.device.api.browser.util.UserAgent;
import net.rim.device.api.collection.ReadableList;
import net.rim.device.api.hrt.HRUtils;
import net.rim.device.api.i18n.Locale;
import net.rim.device.api.io.http.HttpHeaders;
import net.rim.device.api.itpolicy.ITPolicy;
import net.rim.device.api.lowmemory.LowMemoryManager;
import net.rim.device.api.servicebook.ServiceBook;
import net.rim.device.api.servicebook.ServiceBookSyncCollection;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.servicebook.ServiceRouting;
import net.rim.device.api.servicebook.ServiceRoutingProperties;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.BackdoorKeyListener;
import net.rim.device.api.system.BackdoorKeyProcessor;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.Display;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.system.KeyListener;
import net.rim.device.api.system.Memory;
import net.rim.device.api.system.MemoryStats;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.ProgressiveImage;
import net.rim.device.api.system.RIMGlobalMessagePoster;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.system.SIMCard;
import net.rim.device.api.system.WLAN;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.Trackball;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.component.Status;
import net.rim.device.api.util.BitSet;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.api.framework.model.EncryptableProvider;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.messaging.Folder;
import net.rim.device.apps.api.messaging.FolderHierarchies;
import net.rim.device.apps.api.messaging.util.SimpleFolder;
import net.rim.device.apps.api.options.OptionsProviderRegistration;
import net.rim.device.apps.api.utility.general.URI;
import net.rim.device.apps.api.utility.serialization.Converter;
import net.rim.device.apps.internal.browser.api.UrlRequestedInternalEvent;
import net.rim.device.apps.internal.browser.bookmark.Bookmarks;
import net.rim.device.apps.internal.browser.bookmark.BookmarksFolderList;
import net.rim.device.apps.internal.browser.bookmark.BookmarksScreen;
import net.rim.device.apps.internal.browser.bookmark.BookmarksVerb;
import net.rim.device.apps.internal.browser.channel.Channels;
import net.rim.device.apps.internal.browser.cod.OTAStatusReportSender;
import net.rim.device.apps.internal.browser.common.AcceptValueProvider;
import net.rim.device.apps.internal.browser.common.BrowserConverterDescriptor;
import net.rim.device.apps.internal.browser.common.BrowserLockScreen;
import net.rim.device.apps.internal.browser.common.RenderingUtilities;
import net.rim.device.apps.internal.browser.cookie.CookieCache;
import net.rim.device.apps.internal.browser.debug.BrowserDebugScreen;
import net.rim.device.apps.internal.browser.download.DownloadPage;
import net.rim.device.apps.internal.browser.history.History;
import net.rim.device.apps.internal.browser.history.HistoryNode;
import net.rim.device.apps.internal.browser.javascript.JavaScriptRegistry;
import net.rim.device.apps.internal.browser.options.BrowserConfigChangeListener;
import net.rim.device.apps.internal.browser.options.BrowserConfigRecord;
import net.rim.device.apps.internal.browser.options.BrowserOptionsChangeListener;
import net.rim.device.apps.internal.browser.options.BrowserOptionsProvider;
import net.rim.device.apps.internal.browser.options.DomainOverrides;
import net.rim.device.apps.internal.browser.options.GeneralProperty;
import net.rim.device.apps.internal.browser.page.BrowserContentImpl;
import net.rim.device.apps.internal.browser.page.BrowserScreen;
import net.rim.device.apps.internal.browser.page.Page;
import net.rim.device.apps.internal.browser.page.PageCache;
import net.rim.device.apps.internal.browser.page.PageCacheContent;
import net.rim.device.apps.internal.browser.page.PageConverterWrapper;
import net.rim.device.apps.internal.browser.page.PageFooterField;
import net.rim.device.apps.internal.browser.page.PageHeaderField;
import net.rim.device.apps.internal.browser.page.PageModel;
import net.rim.device.apps.internal.browser.page.PageTimer;
import net.rim.device.apps.internal.browser.page.ProgressManager;
import net.rim.device.apps.internal.browser.page.RenderThread;
import net.rim.device.apps.internal.browser.page.SplashPage;
import net.rim.device.apps.internal.browser.page.StartupPage;
import net.rim.device.apps.internal.browser.push.BasePushModel;
import net.rim.device.apps.internal.browser.push.PushThread;
import net.rim.device.apps.internal.browser.resources.BrowserResources;
import net.rim.device.apps.internal.browser.sbloader.SBHelper;
import net.rim.device.apps.internal.browser.stack.CacheNode;
import net.rim.device.apps.internal.browser.stack.CacheResult;
import net.rim.device.apps.internal.browser.stack.CachedSecurityInfo;
import net.rim.device.apps.internal.browser.stack.FetchRequest;
import net.rim.device.apps.internal.browser.stack.HeaderParser;
import net.rim.device.apps.internal.browser.stack.ModelResult;
import net.rim.device.apps.internal.browser.stack.OfflineQueue;
import net.rim.device.apps.internal.browser.stack.RawDataCache;
import net.rim.device.apps.internal.browser.stack.ResourceValidationThread;
import net.rim.device.apps.internal.browser.stack.SBInjector;
import net.rim.device.apps.internal.browser.store.BrowserFolders;
import net.rim.device.apps.internal.browser.threading.BackgroundTaskThreadPool;
import net.rim.device.apps.internal.browser.ui.FrameManager;
import net.rim.device.apps.internal.browser.util.Asserts;
import net.rim.device.apps.internal.browser.util.DomainUtilities;
import net.rim.device.apps.internal.browser.util.FontCache;
import net.rim.device.apps.internal.browser.util.Frame;
import net.rim.device.apps.internal.browser.util.Mutex;
import net.rim.device.apps.internal.browser.util.QuincyUtil;
import net.rim.device.apps.internal.browser.util.RendererControl;
import net.rim.device.apps.internal.browser.util.ReregistrationListener;
import net.rim.device.apps.internal.browser.util.UAProfDiff;
import net.rim.device.apps.internal.browser.util.URLCache;
import net.rim.device.apps.internal.browser.wappush.WAPPushModel;
import net.rim.device.cldc.io.fastdormancy.FastDormancyManager;
import net.rim.device.internal.browser.util.Pipe;
import net.rim.device.internal.browser.util.TimeLogger;
import net.rim.device.internal.browser.wap.WAPServiceRecord;
import net.rim.device.internal.compress.YKDecode;
import net.rim.device.internal.system.CoverageInfoInternalImpl;
import net.rim.device.internal.system.InternalServices;
import net.rim.device.internal.system.RadioInternal;
import net.rim.device.internal.ui.UiInternal;
import net.rim.device.internal.ui.component.BackgroundDialog;
import net.rim.tid.im.layout.SLKeyLayout;
import net.rim.vm.Process;
import net.rim.vm.WeakReference;

public final class BrowserImpl extends UiApplication implements KeyListener, BackdoorKeyListener, BrowserConfigChangeListener, BrowserOptionsChangeListener {
   private BackdoorKeyProcessor _backdoor;
   private Vector _acceptValueProviders;
   private Object _acceptValueEncoding;
   private Object _acceptCharsetValueEncoding;
   private boolean _duringStartup = true;
   private PendingRequestThread _pendingRequestThread;
   private int _browserState = 0;
   private boolean _serialSync;
   private boolean _foldersModified;
   private RibbonManagerThread _ribbonManager;
   private int _lastCoverage;
   private Mutex _browserLock = (Mutex)(new Object());
   private BrowserImpl$NavigationThread _navigationThread;
   private MenuDelayThread _menuDelayThread;
   private WorkerThread _workerThread;
   private OfflineQueue _offlineQueue;
   private BrowserSession _currentSession;
   String _lastConfigUID;
   private boolean _singleBrowserMode;
   private String _initialConfigUID;
   private int _startupPage = BrowserConfigRecord.INVALID_VALUE;
   private Page _currentPage;
   private BrowserScreen _browserScreen;
   private int _state = 0;
   private RenderThread _currentRenderThread;
   private FetchRequest _currentFetchRequest;
   private WeakReference _resourceValidator = (WeakReference)(new Object(null));
   private long _lastExpiryCheck;
   private ProgressManager _progressManager;
   private RawDataCache _rawDataCache;
   private CookieCache _cookieCache;
   private FontCache _fontCache;
   private boolean _activateDueToFetchRequest;
   private boolean _activateDueToPush;
   private BasePushModel _activatePushObject;
   private boolean _viewingMode;
   private boolean _browserClosed = true;
   private Object _currentUrlEncoding;
   private PushThread _pushThread;
   private boolean _requestingNewPage;
   private boolean _escapeCharPressed;
   private StringBuffer _lastUrlLoaded = (StringBuffer)(new Object());
   private Object _activationObject = new Object();
   protected BrowserVerbRepository _verbRepository;
   private RenderingSession _renderingSession;
   private DownloadPage _downloadPage;
   private Vector _activeDownloads;
   private BackgroundTaskThreadPool _backgroundTask;
   private boolean _releaseLock = true;
   private boolean _isReducedKeyboard = InternalServices.isReducedFormFactor();
   private int _autoUpdateInvokeLater = -1;
   private long _lastAutoUpdateTime;
   private boolean _useReducedAcceptHeader = true;
   private int _ykMemLimitHint = 256;
   private boolean _wlanEnabled;
   public static final String RIM_ID_NAME;
   public static final int NO_COVERAGE;
   public static final int COVERAGE_SUFFICIENT_FOR_WAP;
   public static final int COVERAGE_SUFFICIENT_FOR_MDS;
   private static final long ACCEPT_VALUE_PROVIDERS_REGISTRY_KEY;
   private static final String TEST_FILE_MAGIC_COOKIE;
   private static final long TEST_FILE_WAIT_INTERVAL;
   public static final String TYPE_ACTIVATE_BROWSER;
   public static final String TYPE_FETCH_URL;
   public static final String TYPE_OPEN_CHANNEL;
   public static final int STATE_LOADED;
   public static final int STATE_LOADING_FETCHING;
   public static final int STATE_LOADING_RENDERING;
   public static final int STATE_LOADING_SECONDARY_URLS;
   public static final int STATE_EXECUTING_SCRIPT;
   public static final int STATE_TERMINATED;
   public static final int STATE_LOADING_NEGOTIATING;
   public static final int STATE_LOADING_SECURING;
   public static final int STATE_LOADING_IMAGE_FRAGMENTS;
   public static final int STATE_LOADING_WAITING;

   public final void activateConfig(String config, boolean refreshAcceptValues) {
      if (this._currentSession != null) {
         History currentHistory = this._currentSession.getHistory();
         this._currentSession.destroy();
         this._currentSession = BrowserSession.getOrCreateSessionWithHistory(config, currentHistory);
      } else {
         this._currentSession = BrowserSession.getOrCreateSession(config);
      }

      if (StringUtilities.strEqualIgnoreCase(config, GeneralProperty.getDefaultMdsBrowserConfigServiceUID(), 1701707776)) {
         GeneralProperty.setCurrentProperty(33, true);
      } else if (StringUtilities.strEqualIgnoreCase(config, GeneralProperty.getDefaultWapBrowserConfigServiceUID(), 1701707776)) {
         GeneralProperty.setCurrentProperty(32, true);
      }

      if (refreshAcceptValues) {
         this.refreshAcceptValues();
      }

      if (this._currentSession != null) {
         if (this._currentSession.getConfig() != null) {
            this._rawDataCache
               .clearShortTermCacheIfNecessary(this._currentSession.getConfig().getPropertyAsString(3), this._currentSession.getConfig().getPropertyAsString(4));
         }

         this._currentSession.activate();
      }

      this._lastConfigUID = config;
      BrowserScreen browserScreen = this.getBrowserScreen();
      if (browserScreen != null) {
         PageHeaderField header = browserScreen.getHeaderField();
         if (header != null) {
            header.configChanged();
         }

         PageFooterField footer = browserScreen.getFooterField();
         if (footer != null) {
            footer.configChanged();
         }
      }
   }

   public final boolean isBrowserAvailable() {
      return this._ribbonManager.isBrowserAvailable();
   }

   public final RenderingSession getCurrentRenderingSession() {
      return this._renderingSession;
   }

   public final BackgroundTaskThreadPool getBackgroundTask() {
      return this._backgroundTask;
   }

   public final void setRenderingOptions(RenderingOptions renderingOptions, BrowserConfigRecord browserConfigRecord) {
      this.setBrowserConfigRenderingOptions(renderingOptions, browserConfigRecord);
      this.setGeneralPropertyRenderingOptions(renderingOptions, GeneralProperty.ALL_OPTIONS_SET_MASK);
   }

   public final void changeBrowserState(int newState) {
      if (this._browserState != newState) {
         switch (newState) {
            case -1:
            case 1:
               break;
            case 0:
            case 2:
               FastDormancyManager.getInstance().setFastDormancy(true);
               if (SIMCard.isSupported()) {
                  label35:
                  try {
                     SIMCard.atEventActive(128);
                  } finally {
                     break label35;
                  }
               }
               break;
            case 3:
            case 4:
            default:
               FastDormancyManager.getInstance().setFastDormancy(false);
         }

         this._browserState = newState;
         if (this._browserScreen != null) {
            this._browserScreen.browserStateChanged();
         }

         BrowserDaemonRegistry.notifyBrowserStateListeners(newState);
      }
   }

   final boolean isStartingUp() {
      return this._duringStartup;
   }

   final void cardReady(ReregistrationListener[] listeners) {
      if (SIMCard.isSupported()) {
         String currentSimId = null;

         label107:
         try {
            currentSimId = SIMCard.iccidToString(SIMCard.getICCID());
         } finally {
            break label107;
         }

         if (currentSimId != null) {
            String previousSimId = GeneralProperty.getPreviousSimId();
            if (!currentSimId.equals(previousSimId)) {
               GeneralProperty.setPreviousSimId(currentSimId);
               boolean reregister = false;

               for (int i = listeners.length - 1; i >= 0; i--) {
                  try {
                     ReregistrationListener l = listeners[i];
                     if (l != null) {
                        reregister |= l.handleReregistration();
                     }
                  } finally {
                     continue;
                  }
               }

               if (GeneralProperty.getCurrentPropertyAsBoolean(12)) {
                  RibbonManagerThread.deleteBrowserServiceRecords();
                  this.injectDefaultBrowserServiceRecords(true);
                  reregister = true;
               }

               if (reregister) {
                  RibbonManagerThread.sendRegistrationRequest();
               }
            }
         }
      }
   }

   public final void showStartupPage(boolean newTab) {
      synchronized (this.getAppEventLock()) {
         this.pushPage(new StartupPage(this._renderingSession.getRenderingOptions()), true, newTab);
      }
   }

   public final boolean goBack(boolean isEscapeEvent, boolean programmatic) {
      if (this._currentSession != null && this._currentSession.getHistory().canGoBack()) {
         HistoryNode node = this._currentSession.getHistory().previousNode();
         if (node == null) {
            return false;
         }

         ModelResult modelResult = new ModelResult(node.getUrl(), 2, node.getRequestHeaders());
         modelResult.setContext(node.getContext());
         modelResult.setPostData(node.getPostData());
         modelResult.setHomePage(node.isHomePage());
         int flags = isEscapeEvent ? 64 : 0;
         flags = programmatic ? flags | 32 : flags;
         FetchRequest fRequest = new FetchRequest(modelResult, flags);
         fRequest.setTarget(node.getFrameset());
         fRequest.setHistoryRequest(true);
         String currentConfigUID = this._currentSession.getConfig().getUid();
         String historyConfigUID = currentConfigUID;
         BrowserConfigRecord historyConfig = BrowserConfigRecord.getDecodedConfig(node.getConfigUID(), node.getConfigType(), node.getTransportCID());
         if (historyConfig != null) {
            historyConfigUID = historyConfig.getUid();
         }

         if (!StringUtilities.strEqualIgnoreCase(historyConfigUID, currentConfigUID, 1701707776)) {
            this.activateConfig(historyConfigUID, true);
         }

         this.initiateFetchRequest(fRequest);
         return true;
      } else {
         return false;
      }
   }

   public final void initiateFetchRequest(FetchRequest fetchRequest) {
      if ((fetchRequest.getFlags() & 32) == 0 && !this.isForeground()) {
         this._activateDueToFetchRequest = true;
         this._viewingMode = true;
         if (fetchRequest.getModelResult().getContext() == null) {
            fetchRequest.getModelResult().setClearContext(true);
         }

         BrowserConfigRecord configRecord = fetchRequest.getBrowserConfigRecord();
         String configUid = null;
         boolean singleBrowserMode;
         if (configRecord != null) {
            singleBrowserMode = false;
            configUid = configRecord.getUid();
         } else {
            singleBrowserMode = true;
            configUid = GeneralProperty.getOrDetermineDefaultBrowserConfigServiceUID();
            configRecord = BrowserConfigRecord.getDecodedConfig(configUid, BrowserConfigRecord.INVALID_VALUE, null);
            String overrideUid = DomainOverrides.getInstance()
               .getOverride(
                  fetchRequest.getModelResult().getURL(), configRecord != null ? configRecord.getPropertyAsInt(12) : BrowserConfigRecord.INVALID_VALUE
               );
            if (overrideUid != null) {
               configUid = overrideUid;
               configRecord = BrowserConfigRecord.getDecodedConfig(configUid, BrowserConfigRecord.INVALID_VALUE, null);
            }

            fetchRequest.setBrowserConfigRecord(configRecord);
         }

         this.popAllScreens();
         this.activateBrowser(configUid, true, singleBrowserMode);
      } else {
         this._viewingMode = false;
      }

      if (this._currentFetchRequest != null) {
         this._navigationThread.requestTask(5, this._currentFetchRequest);
      }

      this.invokeLater(new BrowserImpl$1(this, fetchRequest));
   }

   public final int enquedPushSize() {
      return this._pushThread.getCount();
   }

   public final void initiatePushDisplay(BasePushModel model) {
      String preferredUID = null;
      String preferredTransportCID = null;
      int preferredConfigType;
      if (!(model instanceof WAPPushModel)) {
         preferredConfigType = 1;
      } else {
         WAPPushModel wapModel = (WAPPushModel)model;
         preferredUID = wapModel.getPreferredConfigUID();
         preferredTransportCID = wapModel.getPreferredTransportCID();
         preferredConfigType = wapModel.getPreferredConfigType();
      }

      BrowserConfigRecord configToUse = BrowserConfigRecord.getDecodedConfig(preferredUID, preferredConfigType, preferredTransportCID);
      String configUid = null;
      if (configToUse != null) {
         BrowserConfigRecord[] configs = BrowserConfigRecord.getValidBrowserConfigRecords();
         int count = configs.length;

         for (int i = 0; i < count; i++) {
            if (StringUtilities.strEqualIgnoreCase(configs[i].getUid(), configToUse.getUid(), 1701707776)) {
               configUid = configToUse.getUid();
               break;
            }
         }
      }

      if (configUid == null) {
         configUid = GeneralProperty.getOrDetermineDefaultBrowserConfigServiceUID();
      }

      this._activateDueToPush = true;
      this._activatePushObject = model;
      this.activateBrowser(configUid);
   }

   public final boolean isCurrentRenderThread(RenderThread thread) {
      return this._currentRenderThread == thread;
   }

   public final void processFetchRequest(FetchRequest fetchRequest, RenderThread localRenderThread) {
      ModelResult modelResult = fetchRequest.getModelResult();
      String url = modelResult.getURL();
      String absUrl = this.getAbsoluteUrl(url, fetchRequest);
      if (absUrl != url) {
         modelResult.setURL(absUrl);
      }

      this.resetJavaScriptOption(modelResult, fetchRequest);
      if (fetchRequest.getTarget() == null) {
         this.setTargetFrame(fetchRequest);
      }

      boolean updateFrame = false;
      int navigation = modelResult.getNavigation();
      if (this._currentPage != null && navigation != 3 && navigation != 4 && navigation != 5) {
         String currentUrl = this._currentPage.getURL();
         StateHolder temp = this.doIfRequestIsNotReload(fetchRequest, absUrl, currentUrl, modelResult, updateFrame, navigation);
         if (temp._exitFetchRequest) {
            return;
         }

         absUrl = temp._absUrl;
         currentUrl = temp._currentUrl;
         updateFrame = temp._updateFrame;
      }

      if (this._currentPage != null) {
         this._currentPage.cancelAllJobs();
      }

      EventLogger.logEvent(1907089860548946979L, 1114662516, 5);
      Page page = null;
      if (fetchRequest.getError() == null) {
         if (url.startsWith("device:close")) {
            this.closeWLAN();
            if (this._currentSession != null) {
               absUrl = null;
            }
         }

         page = this.getPage(fetchRequest, absUrl, modelResult, updateFrame, localRenderThread);
      }

      Asserts.productionUserAbortAssert(this._currentRenderThread == localRenderThread);
      Object error = fetchRequest.getError();
      if (page != null || error != null) {
         if (page != null) {
            page.setSecurityInfo(fetchRequest.getSecurityInfo());
         }

         if (error == null || this.doRequestErrorHandling(fetchRequest, page)) {
            if (modelResult.isHomePage() != ((ModelResult)page.getModelResult()).isHomePage()) {
               ((ModelResult)page.getModelResult()).setHomePage(modelResult.isHomePage());
            }

            if (TimeLogger._loggingEnabled) {
               TimeLogger.getInstance().startTimer(3, page.hashCode());
            }

            this.attachTimerIfRequired(page, fetchRequest);
            this.displayPage(page, localRenderThread, fetchRequest);
            if (TimeLogger._loggingEnabled) {
               TimeLogger.getInstance().stopTimer(3, page.hashCode());
            }
         }
      }
   }

   public final void pushSplashScreenIfNeeded() {
      synchronized (this.getAppEventLock()) {
         if (this._browserScreen == null || !this._browserScreen.isDisplayed()) {
            this.pushPage(SplashPage.createInstance(this._renderingSession.getRenderingOptions(), this._currentSession), true, false);
         }
      }
   }

   public final void saveRequestToMessageList() {
      if (this._currentFetchRequest != null) {
         String uid = null;
         String cid = null;
         if (this._currentSession != null) {
            BrowserConfigRecord config = this._currentSession.getConfig();
            if (config != null) {
               uid = config.getUid();
               cid = config.getPropertyAsString(3);
            }
         }

         this._currentFetchRequest.saveInMessageList(true, uid, cid);
         this.changeToLoadedState(null);
      }
   }

   public final void abortCurrentRequest() {
      if (Application.isEventDispatchThread()) {
         this._navigationThread.requestTask(5, this._currentFetchRequest);
      } else {
         this.changeToLoadedState(null);
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public final void abortRequest(RenderThread thread) {
      this.getBrowserLock();
      boolean var4 = false /* VF: Semaphore variable */;

      try {
         var4 = true;
         if (thread == this._currentRenderThread) {
            this.abortCurrentRequest();
            var4 = false;
         } else {
            var4 = false;
         }
      } finally {
         if (var4) {
            this.releaseBrowserLock();
         }
      }

      this.releaseBrowserLock();
   }

   public final void activateBrowser(String configUid) {
      this.activateBrowser(configUid, false, configUid == null);
   }

   public final String getInitialConfigUID() {
      return this._initialConfigUID;
   }

   public final void setInitialConfigUID(String configUID) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   public final boolean isSingleBrowserMode() {
      return this._singleBrowserMode;
   }

   final void activateBrowser(String configUid, boolean activateDueToFetchRequest, boolean singleBrowserMode) {
      synchronized (this.getAppEventLock()) {
         synchronized (this._activationObject) {
            boolean configChanged = true;
            BrowserConfigRecord config = null;
            if (configUid == null) {
               if (singleBrowserMode && this._currentSession != null) {
                  config = this._currentSession.getConfig();
                  if (config != null) {
                     configUid = config.getUid();
                     configChanged = false;
                  }
               }

               if (configUid == null) {
                  configUid = GeneralProperty.getOrDetermineDefaultBrowserConfigServiceUID();
               }
            }

            if (configChanged && this._currentSession != null) {
               config = this._currentSession.getConfig();
               if (config != null && StringUtilities.strEqualIgnoreCase(configUid, config.getUid(), 1701707776)) {
                  configChanged = false;
               } else {
                  config = null;
               }
            }

            if (configChanged && !this._browserClosed && (!singleBrowserMode || !this._singleBrowserMode)) {
               this._navigationThread.clear();
               if (this._currentSession != null) {
                  this._currentSession.getHistory().clear();
               }

               this.popAllScreens();
               if (!activateDueToFetchRequest) {
                  this._activateDueToFetchRequest = false;
                  this._viewingMode = false;
               }

               this._browserClosed = true;
            }

            this._singleBrowserMode = singleBrowserMode;
            this._initialConfigUID = configUid;
            this.requestForeground();
            if (configChanged) {
               this.activateConfig(configUid, false);
            }
         }
      }
   }

   final void fetchUrl(String url, String configUid) {
      if (this._currentSession != null) {
         this._currentSession.getHistory().clear();
      }

      ModelResult modelResult = new ModelResult(url, 8193, null);
      this.initiateFetchRequest(new FetchRequest(modelResult, BrowserConfigRecord.getDecodedConfig(configUid, BrowserConfigRecord.INVALID_VALUE, null)));
   }

   final void openChannel(String id) {
      if (this._currentSession != null) {
         this._currentSession.getHistory().clear();
      }

      String finalId = id;
      this._navigationThread.requestTask(12, new BrowserImpl$2(this, finalId));
   }

   public final void handleEscapeOnLastPage(boolean programmatic) {
      if (this._currentPage != null && this._currentPage.isModified() && !this._currentPage.savesContext()) {
         ConfirmLeavePageRunnable confirm = new ConfirmLeavePageRunnable();
         this.invokeAndWait(confirm);
         if (!confirm.getContinue()) {
            return;
         }
      }

      if (this._activateDueToFetchRequest) {
         Verb verb = this._verbRepository.getVerb(3, this.getVerbMask());
         if (verb != null) {
            verb.invoke(new Object(!this._viewingMode));
            return;
         }
      } else {
         if (this._startupPage == 0) {
            if (this._currentSession != null) {
               this._currentSession.getHistory().reset();
            }

            this.popAllScreens();
            this.showBookmarksScreen();
            return;
         }

         if (this._startupPage == 1) {
            boolean autoStart = this._currentSession != null ? this._currentSession.getConfig().getPropertyAsInt(20) == 1 : false;
            if (this._currentPage instanceof SplashPage && !autoStart) {
               Verb verb = this._verbRepository.getVerb(3, this.getVerbMask());
               if (verb != null) {
                  verb.invoke(Boolean.TRUE);
                  return;
               }
            } else {
               if (this._currentPage == null
                  || this._currentPage.getModelResult() == null
                  || !((ModelResult)this._currentPage.getModelResult()).isHomePage()
                  || autoStart) {
                  String errorString = null;
                  String url = this._currentSession != null ? this._currentSession.getConfig().getHomePageWithOverride() : null;
                  if (url == null) {
                     errorString = BrowserResources.getString(233);
                  }

                  ModelResult modelResult = new ModelResult(url, 8193, null);
                  modelResult.setHomePage(true);
                  if (this._currentSession != null) {
                     this._currentSession.getHistory().clear();
                  }

                  FetchRequest fetchRequest = new FetchRequest(modelResult, programmatic ? 32 : 0);
                  fetchRequest.setError(errorString);
                  this.initiateFetchRequest(fetchRequest);
                  return;
               }

               Verb verb = this._verbRepository.getVerb(3, this.getVerbMask());
               if (verb != null) {
                  verb.invoke(Boolean.TRUE);
                  return;
               }
            }
         } else {
            if (this._startupPage == 3 && (this._currentPage == null || !(this._currentPage instanceof StartupPage))) {
               if (this._currentSession != null) {
                  this._currentSession.getHistory().clear();
               }

               this.popAllScreens();
               this.showStartupPage(false);
               return;
            }

            Verb verb = this._verbRepository.getVerb(3, this.getVerbMask());
            if (verb != null) {
               verb.invoke(Boolean.TRUE);
            }
         }
      }
   }

   public final void closeBrowser(boolean confirm) {
      ConfirmCloseBrowserRunnable confirmClose = new ConfirmCloseBrowserRunnable(confirm);
      this.invokeAndWait(confirmClose);
      if (confirmClose.closeBrowser()) {
         this.requestBackground();
         this.popAllScreens();
         if (this._currentSession != null) {
            this._currentSession.destroy();
            this._currentSession = null;
         }

         this._navigationThread.requestExclusiveTask(3);
      }
   }

   public final boolean isBrowserClosed() {
      return this._browserClosed;
   }

   public final void addWLANActivationMenuItems(Menu menu) {
      if (this._currentSession != null) {
         BrowserConfigRecord config = this._currentSession.getConfig();
         if (config != null && "S TCP-WBC".equalsIgnoreCase(config.getUid())) {
            menu.add(new BrowserImpl$ActivateWLANMenuItem());
         }
      }
   }

   public final BrowserVerbRepository getBrowserVerbRepository() {
      return this._verbRepository;
   }

   public final Page getCurrentPage() {
      return this._currentPage;
   }

   public final BrowserScreen getBrowserScreen() {
      return this._browserScreen;
   }

   public final int getBrowserExecutionState() {
      return this._state;
   }

   public final int getVerbMask() {
      return this._currentPage != null ? this._currentPage.getVerbMask() : 4194303;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public final void pageLoaded(Page page) {
      this._browserLock.waitForLock();
      boolean var4 = false /* VF: Semaphore variable */;

      label50: {
         try {
            var4 = true;
            if (this._requestingNewPage) {
               var4 = false;
               break label50;
            }

            if (this._currentPage == page) {
               this.changeToLoadedState(this._currentPage);
               var4 = false;
            } else {
               var4 = false;
            }
         } finally {
            if (var4) {
               this._browserLock.releaseLock();
            }
         }

         this._browserLock.releaseLock();
         if (this._currentSession != null) {
            this._currentSession.requestCompleted();
         }

         return;
      }

      this._browserLock.releaseLock();
   }

   public final void setExecutingScriptState(BrowserContent param1) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.IndexOutOfBoundsException: Index 2 out of bounds for length 1
      //   at java.base/jdk.internal.util.Preconditions.outOfBounds(Preconditions.java:100)
      //   at java.base/jdk.internal.util.Preconditions.outOfBoundsCheckIndex(Preconditions.java:106)
      //   at java.base/jdk.internal.util.Preconditions.checkIndex(Preconditions.java:302)
      //   at java.base/java.util.Objects.checkIndex(Objects.java:385)
      //   at java.base/java.util.ArrayList.remove(ArrayList.java:551)
      //   at org.jetbrains.java.decompiler.modules.decompiler.FinallyProcessor.removeExceptionInstructionsEx(FinallyProcessor.java:1052)
      //   at org.jetbrains.java.decompiler.modules.decompiler.FinallyProcessor.verifyFinallyEx(FinallyProcessor.java:502)
      //   at org.jetbrains.java.decompiler.modules.decompiler.FinallyProcessor.iterateGraph(FinallyProcessor.java:90)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:185)
      //
      // Bytecode:
      // 00: aload 0
      // 01: getfield net/rim/device/apps/internal/browser/core/BrowserImpl._currentPage Lnet/rim/device/apps/internal/browser/page/Page;
      // 04: ifnull 12
      // 07: aload 0
      // 08: getfield net/rim/device/apps/internal/browser/core/BrowserImpl._currentPage Lnet/rim/device/apps/internal/browser/page/Page;
      // 0b: invokevirtual net/rim/device/apps/internal/browser/page/Page.getBrowserContent ()Lnet/rim/device/apps/internal/browser/page/BrowserContentImpl;
      // 0e: aload 1
      // 0f: if_acmpeq 13
      // 12: return
      // 13: aload 0
      // 14: getfield net/rim/device/apps/internal/browser/core/BrowserImpl._browserLock Lnet/rim/device/apps/internal/browser/util/Mutex;
      // 17: invokevirtual net/rim/device/apps/internal/browser/util/Mutex.waitForLock ()V
      // 1a: aload 0
      // 1b: invokespecial net/rim/device/apps/internal/browser/core/BrowserImpl.changeToExecutingScriptState ()V
      // 1e: aload 0
      // 1f: getfield net/rim/device/apps/internal/browser/core/BrowserImpl._browserLock Lnet/rim/device/apps/internal/browser/util/Mutex;
      // 22: invokevirtual net/rim/device/apps/internal/browser/util/Mutex.releaseLock ()V
      // 25: return
      // 26: astore 2
      // 27: aload 0
      // 28: getfield net/rim/device/apps/internal/browser/core/BrowserImpl._browserLock Lnet/rim/device/apps/internal/browser/util/Mutex;
      // 2b: invokevirtual net/rim/device/apps/internal/browser/util/Mutex.releaseLock ()V
      // 2e: return
      // 2f: astore 3
      // 30: aload 0
      // 31: getfield net/rim/device/apps/internal/browser/core/BrowserImpl._browserLock Lnet/rim/device/apps/internal/browser/util/Mutex;
      // 34: invokevirtual net/rim/device/apps/internal/browser/util/Mutex.releaseLock ()V
      // 37: aload 3
      // 38: athrow
      // try (12 -> 14): 18 null
      // try (12 -> 14): 23 null
      // try (18 -> 19): 23 null
      // try (23 -> 24): 23 null
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public final void setLoadingSecondaryURLsState(Page page) {
      if (this._currentPage != null && this._currentPage == page) {
         this._browserLock.waitForLock();
         boolean var4 = false /* VF: Semaphore variable */;

         try {
            var4 = true;
            this.changeToLoadingSecondaryURLsState(page);
            var4 = false;
         } finally {
            if (var4) {
               this._browserLock.releaseLock();
            }
         }

         this._browserLock.releaseLock();
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public final void setLoadingImageFragmentsState(Page page) {
      if (this._currentPage != null && this._currentPage == page) {
         this._browserLock.waitForLock();
         boolean var4 = false /* VF: Semaphore variable */;

         try {
            var4 = true;
            this._state = 8;
            this.updatePageLabel();
            var4 = false;
         } finally {
            if (var4) {
               this._browserLock.releaseLock();
            }
         }

         this._browserLock.releaseLock();
      }
   }

   public final MenuDelayThread getMenuDelayThread() {
      return this._menuDelayThread;
   }

   public final void setReleaseLock(boolean value) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   public final void showMenu() {
      this.showMenu(0);
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   final void showMenuSynchronously(boolean performDefaultAction, int menuInstance) {
      if (performDefaultAction && this._currentPage != null) {
         Verb defaultVerb = this._currentPage.getDefaultVerbUnderCursor();
         if (defaultVerb != null) {
            this._browserLock.releaseLock();
            this.invokeLater((Runnable)(new Object(null, defaultVerb, new Object(61), false)));
            return;
         }
      }

      this._releaseLock = true;
      boolean var6 = false /* VF: Semaphore variable */;

      try {
         var6 = true;
         if (this._browserScreen != null) {
            this._browserScreen.onMenu(menuInstance);
            var6 = false;
         } else {
            var6 = false;
         }
      } finally {
         if (var6) {
            if (this._releaseLock) {
               this._browserLock.releaseLock();
            }
         }
      }

      if (this._releaseLock) {
         this._browserLock.releaseLock();
      }
   }

   public final boolean trackwheelRoll(int amount, int status, int time) {
      return false;
   }

   final boolean getInCoverage() {
      return this._lastCoverage != 0;
   }

   final void radioTurnedOff() {
      this.updateLastCoverage();
   }

   final void networkServiceChange(int networkId, int service) {
      this.updateLastCoverage();
      this._ribbonManager.autoStartIfRequired(false);
   }

   final void pdpStateChange(int apn, int state, int cause) {
      this.updateLastCoverage();
      this._ribbonManager.autoStartIfRequired(false);
   }

   final void networkStarted(int networkId, int service) {
      this.updateLastCoverage();
      this._ribbonManager.autoStartIfRequired(false);
   }

   final void signalLevel(int level) {
      boolean inCoverage = level != -256;
      if (inCoverage != (this._lastCoverage != 0)) {
         if (level == -256 ^ this._lastCoverage == 0) {
            this.updateLastCoverage();
         }
      }
   }

   final void networkFail(int status, int error) {
      this.updateLastCoverage();
   }

   public final void performEscape(boolean programmatic) {
      this._navigationThread.requestTask(8, new Object(programmatic));
   }

   public final void getBrowserLock() {
      this._browserLock.waitForLock();
   }

   public final void releaseBrowserLock() {
      this._browserLock.releaseLock();
   }

   final void followLinkUnderCursor() {
      if (this._currentPage != null) {
         Verb defaultVerb = this._currentPage.getDefaultVerbUnderCursor();
         if (defaultVerb != null) {
            this.invokeLater((Runnable)(new Object(null, defaultVerb, new Object(61), false)));
         }
      }
   }

   final void pushDebugScreen() {
      Asserts.productionAssert(this._browserLock.lockAcquired());
      synchronized (this.getAppEventLock()) {
         this.pushScreen(new BrowserDebugScreen());
      }
   }

   final void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (guid == -4394903006263251010L) {
         this._fontCache.resetFonts();
      } else {
         if (guid != -7464003439710973532L) {
            if (guid == -273986034351666339L) {
               GeneralProperty.setDefaultMdsBrowserConfigServiceUID(GeneralProperty.getDefaultMdsBrowserConfigServiceUID());
               GeneralProperty.setDefaultWapBrowserConfigServiceUID(GeneralProperty.getDefaultWapBrowserConfigServiceUID());
               this._verbRepository.reinitialize();
               return;
            }

            if (guid == -7219683504990287771L) {
               BrowserScreen screen = this.getBrowserScreen();
               if (screen != null) {
                  PageFooterField footer = screen.getFooterField();
                  if (footer != null) {
                     footer.operatorNameStringChanged();
                     return;
                  }
               }
            } else {
               if (guid == -8896339270692810071L) {
                  BrowserDaemonRegistry registry = (BrowserDaemonRegistry)ApplicationRegistry.getApplicationRegistry().waitFor(4307171400805038204L);
                  synchronized (registry) {
                     registry.cardReady();
                     return;
                  }
               }

               if (guid == 1348796660760556312L) {
                  this._ribbonManager.stopProcessing();
                  return;
               }

               if (guid == -583230596614878690L) {
                  this.injectDefaultBrowserServiceRecords(true);
                  SBHelper.getInstance().injectServiceBooks();
                  this._ribbonManager.updateDesktopMdsConfigs(true);
                  this._ribbonManager.startProcessing();
                  return;
               }

               if (guid == -4220058463650496006L) {
                  ServiceRecord sr = ServiceBook.getSB().getRecordById(data0);
                  if (sr != null && sr.getType() == 0) {
                     this._ribbonManager.handleAddedServiceRecord(sr, false);
                  }

                  if (sr != null && StringUtilities.strEqualIgnoreCase(sr.getCid(), BrowserConfigRecord.SERVICE_CID, 1701707776)) {
                     BrowserSession.refresh(sr.getUid());
                     return;
                  }
               } else {
                  if (guid == 2522898683889177438L) {
                     this._ribbonManager.removeFromRibbon(data0);
                     this.refreshSessions();
                     return;
                  }

                  if (guid == 8288627527798139133L) {
                     ServiceRecord sr = ServiceBook.getSB().getRecordById(data0);
                     if (sr != null) {
                        int type = sr.getType();
                        if (type == 2) {
                           this._ribbonManager.handleRemovedServiceRecord(sr);
                           this.refreshSessions();
                           return;
                        }

                        if (type == 6) {
                           this._ribbonManager.handleRemovedServiceRecord(sr);
                           this.refreshSessions();
                           return;
                        }

                        this._ribbonManager.handleAddedServiceRecord(sr, true);
                        if (StringUtilities.strEqualIgnoreCase(sr.getCid(), BrowserConfigRecord.SERVICE_CID, 1701707776)) {
                           BrowserSession.refresh(sr.getUid());
                           return;
                        }
                     }
                  } else {
                     if (guid == 2950066364548195165L || guid == -5567093064078848383L || guid == 7181491349594683390L) {
                        this.injectWLANBrowserServiceRecords(false);
                        return;
                     }

                     if (guid == 8508406279413621091L || guid == -594020114676189989L || guid == 1077267820605375385L) {
                        this._ribbonManager.handleITPolicyChanged();
                        this.refreshAcceptValues();
                        this.scheduleAutoUpdate();
                        return;
                     }

                     if ((guid == -3864212166794284297L || guid == -6531073315810526672L || guid == 2200641410611652722L) && object0 == HRUtils.getDefaultHRT()
                        )
                      {
                        this.updateLastCoverage();
                        return;
                     }

                     if (guid == -1602902615298266273L) {
                        switch (data0) {
                           case 1:
                              this.updateBrowserExecutionState(6, null);
                              return;
                           case 2:
                              this.updateBrowserExecutionState(7, null);
                              return;
                           case 8:
                              this.updateBrowserExecutionState(1, null);
                              return;
                        }
                     } else if (guid == 3729075673904713354L) {
                        int currentState = this.getBrowserState();
                        if (currentState != 0 && currentState != 2) {
                           this.changeBrowserState(2);
                           return;
                        }
                     } else if (guid == -2269441167196113981L) {
                        if (object0 instanceof Object) {
                           QuincyUtil.sendQuincy((Throwable)object0, true);
                           return;
                        }
                     } else if (guid == 2610733293414932871L) {
                        if (object0 instanceof Object && object1 instanceof Object) {
                           QuincyUtil.sendLogworthyQuincy((String)object0, (String)object1);
                           return;
                        }
                     } else {
                        if (guid == 256826950193107649L) {
                           this._ribbonManager.moduleAdded(data0);
                           return;
                        }

                        if (guid == -4232371946002803201L) {
                           this._ribbonManager.moduleDeleted();
                           return;
                        }

                        if (guid == 6345609069135580235L) {
                           this._pendingRequestThread.kickPendingRequests();
                           this._ribbonManager.autoStartIfRequired(false);
                           return;
                        }

                        if (guid == 8877632280522743328L) {
                           this.scheduleAutoUpdate();
                        }
                     }
                  }
               }
            }
         } else {
            this.updateLastCoverage();
            this.injectDefaultBrowserServiceRecords(true);
            SimpleFolder folder = null;
            SimpleFolder hierarchy = null;
            hierarchy = SimpleFolder.getInstance(BrowserFolders.BROWSER_FAMILY, BrowserFolders.RIM_BROWSER_BOOKMARKS_HIERARCHY_ID);
            hierarchy.setFriendlyName(BrowserResources.getString(157));
            folder = (SimpleFolder)hierarchy.getFolder(BrowserFolders.BROWSER_BOOKMARKS_FOLDER_ID);
            if (folder != null) {
               folder.setFriendlyName(BrowserResources.getString(144));
            }

            BookmarksFolderList.setFolderNames();
            hierarchy = SimpleFolder.getInstance(BrowserFolders.BROWSER_FAMILY, BrowserFolders.RIM_BROWSER_MESSAGES_HIERARCHY_ID);
            hierarchy.setFriendlyName(BrowserResources.getString(122));
            hierarchy = SimpleFolder.getInstance(BrowserFolders.BROWSER_FAMILY, BrowserFolders.RIM_BROWSER_CHANNELS_HIERARCHY_ID);
            hierarchy.setFriendlyName(BrowserResources.getString(306));
            folder = (SimpleFolder)hierarchy.getFolder(BrowserFolders.BROWSER_CHANNELS_FOLDER_ID);
            if (folder != null) {
               folder.setFriendlyName(BrowserResources.getString(307));
            }

            hierarchy = SimpleFolder.getInstance(BrowserFolders.BROWSER_FAMILY, BrowserFolders.RIM_BROWSER_BOOKMARKS_HIERARCHY_ID);
            if (folder != null) {
               folder = (SimpleFolder)hierarchy.getFolder(BrowserFolders.BROWSER_BOOKMARKS_FOLDER_ID);
            }

            synchronized (FolderHierarchies.getLockObject()) {
               this.recursiveUpdate(folder);
            }

            Locale defaultLocale = Locale.getDefault();
            GeneralProperty.setDefaultCharsetValue(Helper.getSuggestedEncoding(defaultLocale.getCode()));
            if (StringUtilities.strEqualIgnoreCase(defaultLocale.getLanguage(), "zh", 1701707776)) {
               int fontSize = GeneralProperty.getCurrentPropertyAsInt(36);
               if (fontSize < 9) {
                  GeneralProperty.setCurrentProperty(36, 9);
                  GeneralProperty.setCurrentProperty(37, 0);
                  int defaultSize = GeneralProperty.getCurrentPropertyAsInt(27);
                  if (defaultSize < 9) {
                     GeneralProperty.setCurrentProperty(27, 9);
                     return;
                  }
               }
            }
         }
      }
   }

   public final void handleException(Throwable e) {
      this.popAllScreens();
      if (this.isForeground()) {
         this.pushSplashScreenIfNeeded();
      }

      QuincyUtil.sendQuincy(e, true);
      int errorToDisplay = 0;
      short var3;
      if (e instanceof Object) {
         var3 = 588;
      } else {
         var3 = 235;
      }

      if (this.isForeground()) {
         BackgroundDialog.showMessage(BrowserResources.getString(var3));
      }
   }

   public final ProgressManager getProgressManager() {
      return this._progressManager;
   }

   public final RawDataCache getRawDataCache() {
      return this._rawDataCache;
   }

   public final String getLastUrl() {
      return this._lastUrlLoaded.toString();
   }

   final void cleanup() {
      this.changeBrowserState(0);
      this._verbRepository.cleanup();
      this._rawDataCache.cleanup();
      this._cookieCache.cleanup();
      BrowserSession.clearAllSessions();
      this._ribbonManager.cleanup();
   }

   public final void enquePushModel(BasePushModel aModel) {
      this._pushThread.addToQueue(aModel);
   }

   public final void updateBrowserExecutionState(int status, FetchRequest testRequest) {
      if (testRequest == null || this._currentFetchRequest == testRequest) {
         this._navigationThread.requestTask(10, new Object(status));
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   final InputConnection processRequest(FetchRequest fetchRequest, boolean updateHeaders, boolean addToPending) {
      EventLogger.logEvent(1907089860548946979L, 1148219936, 5);
      if (fetchRequest == null) {
         return null;
      }

      InputConnection conn = null;
      int numRedirects = 0;

      while (true) {
         ModelResult modelResult = fetchRequest.getModelResult();
         if (updateHeaders) {
            HttpHeaders requestHeaders = modelResult.getRequestHeaders();
            if (requestHeaders == null) {
               modelResult.setRequestHeaders(this.getStandardRequestHeaders());
            } else {
               this.addStandardRequestHeaders(requestHeaders);
            }
         }

         if (modelResult.getURL().endsWith("_92d8ccd86360b48b.wml")) {
            synchronized (modelResult) {
               label831:
               try {
                  modelResult.wait(20000);
               } finally {
                  break label831;
               }
            }
         }

         EventLogger.logEvent(1907089860548946979L, 1147302508, 5);
         conn = this._rawDataCache.get(fetchRequest);
         boolean saved = (fetchRequest.getFlags() & 4) != 0;
         if (!saved) {
            this.getBrowserLock();
            boolean var64 = false /* VF: Semaphore variable */;

            try {
               var64 = true;
               saved = (fetchRequest.getFlags() & 4) != 0;
               var64 = false;
            } finally {
               if (var64) {
                  this.releaseBrowserLock();
               }
            }

            this.releaseBrowserLock();
         }

         String redirectURL = null;
         PageModel savedPageModel = null;
         synchronized (fetchRequest) {
            if (saved) {
               CacheResult cacheResult = modelResult.getCacheResult();
               boolean done = false;
               Pipe pipe = cacheResult.getData();
               if (pipe != null) {
                  pipe.waitUntilClosed();
                  done = pipe.isClosed();
               }

               savedPageModel = fetchRequest.getSavedPageModel();
               boolean inSavedQueue = false;
               Vector savedPendingRequests = this._pendingRequestThread.getSavedPendingRequests();
               synchronized (savedPendingRequests) {
                  if (savedPendingRequests.contains(savedPageModel)) {
                     inSavedQueue = true;
                     savedPendingRequests.removeElement(savedPageModel);
                  }
               }

               boolean successfulRequest = false;
               int status = cacheResult.getStatus();
               if (status >= 200 && status < 300 && done) {
                  successfulRequest = true;
                  savedPageModel.changeStatus(3);
               } else if (status >= 300
                  && status < 400
                  && status != 305
                  && (status != 307 || modelResult.getPostData() == null)
                  && done
                  && conn instanceof Object) {
                  label888:
                  try {
                     conn.close();
                  } finally {
                     break label888;
                  }

                  label875:
                  try {
                     redirectURL = RendererControl.getRedirectURL((HttpConnection)conn);
                     successfulRequest = true;
                     numRedirects++;
                  } finally {
                     break label875;
                  }
               }

               if (!successfulRequest) {
                  String cid = null;
                  if (!inSavedQueue) {
                     BrowserConfigRecord browserConfig = fetchRequest.getBrowserConfigRecord();
                     if (browserConfig == null) {
                        BrowserSession session = BrowserSession.getCurrentSession();
                        if (session != null) {
                           browserConfig = session.getConfig();
                        }
                     }

                     if (browserConfig != null) {
                        cid = browserConfig.getPropertyAsString(3);
                     }
                  }

                  if (!inSavedQueue && (this.isCoverageSufficient(cid) || !addToPending)) {
                     savedPageModel.changeStatus(5);
                  } else {
                     savedPageModel.changeStatus(2);
                     this._pendingRequestThread.addPendingRequest(savedPageModel);
                  }
               }

               if (redirectURL == null || numRedirects > 10) {
                  Vector pendingRequests = this._pendingRequestThread.getPendingRequests();
                  synchronized (pendingRequests) {
                     synchronized (savedPendingRequests) {
                        if (pendingRequests.size() == 0 && savedPendingRequests.size() == 0 && this._browserState == 1) {
                           this.changeBrowserState(0);
                        }
                     }
                  }
               }

               if (conn != null) {
                  label843:
                  try {
                     conn.close();
                  } finally {
                     break label843;
                  }
               }

               conn = null;
            }

            EventLogger.logEvent(1907089860548946979L, 1147564654, 5);
         }

         if (redirectURL == null || numRedirects > 10 || savedPageModel == null) {
            return conn;
         }

         modelResult.setCacheResult(null);
         modelResult.setURL(redirectURL);
         modelResult.setPostData(null);
         HttpHeaders httpHeaders = (HttpHeaders)(new Object());
         HttpHeaders oldHttpHeaders = modelResult.getRequestHeaders();
         if (oldHttpHeaders != null) {
            String referer = oldHttpHeaders.getPropertyValue(HeaderParser.REFERER);
            if (referer != null) {
               httpHeaders.addProperty(HeaderParser.REFERER, referer);
            }
         }

         this.addStandardRequestHeaders(httpHeaders);
         modelResult.setRequestHeaders(httpHeaders);
         updateHeaders = false;
         if (savedPageModel instanceof Object) {
            EncryptableProvider ep = (EncryptableProvider)savedPageModel;
            if (!ep.checkCrypt(true, true)) {
               ep.reCrypt(true, true);
            }
         }

         PersistentObject.commit(savedPageModel);
         fetchRequest = new FetchRequest(savedPageModel);
      }
   }

   public final void addSavedRequest(FetchRequest fetchRequest) {
      synchronized (fetchRequest) {
         if ((fetchRequest.getFlags() & 4) != 0) {
            PageModel savedPageModel = fetchRequest.getSavedPageModel();
            this._pendingRequestThread.addSavedPendingRequest(savedPageModel);
         }
      }
   }

   public final void addPendingRequest(FetchRequest fetchRequest, PendingRequestListener listener) {
      synchronized (fetchRequest) {
         if ((fetchRequest.getFlags() & 4) != 0) {
            PageModel savedPageModel = fetchRequest.getSavedPageModel();
            this._pendingRequestThread.addPendingRequest(savedPageModel, listener);
         }
      }
   }

   public final void addQueueRequest(FetchRequest fetchRequest) {
      synchronized (fetchRequest) {
         PageModel savedPageModel = fetchRequest.getSavedPageModel();
         if (savedPageModel != null) {
            savedPageModel.changeStatus(2);
            this._pendingRequestThread.addPendingRequest(savedPageModel);
         }
      }
   }

   public final OfflineQueue getOfflineQueue() {
      return this._offlineQueue;
   }

   public final int getLastCoverage() {
      return this._lastCoverage;
   }

   public final int getBrowserState() {
      return this._browserState;
   }

   public final void deregisterAcceptValueProvider(AcceptValueProvider acceptValueProvider) {
      this._acceptValueProviders.removeElement(acceptValueProvider);
      this.refreshAcceptValues();
   }

   public final void registerAcceptValueProvider(AcceptValueProvider acceptValueProvider) {
      this._acceptValueProviders.addElement(acceptValueProvider);
      this.refreshAcceptValues();
   }

   public final void setFoldersModified(boolean modified) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   public final boolean isSerialSync() {
      return this._serialSync;
   }

   final void serialSyncStarted() {
      this._serialSync = true;
      this._foldersModified = false;
   }

   final void serialSyncStopped() {
      this._serialSync = false;
      if (this._foldersModified) {
         BrowserFolders.cleanupFolders();
         BrowserFolders.updateFolders();
         this._foldersModified = false;
      }
   }

   public final synchronized void refreshAcceptValues() {
      this._acceptValueEncoding = null;
      this._acceptCharsetValueEncoding = null;
   }

   public final HttpHeaders getStandardRequestHeaders() {
      HttpHeaders tmpRequestHeaders = (HttpHeaders)(new Object());
      this.addStandardRequestHeaders(tmpRequestHeaders);
      return tmpRequestHeaders;
   }

   public final void addStandardRequestHeaders(HttpHeaders requestHeaders) {
      this.addStandardRequestHeaders(requestHeaders, null);
   }

   public final void addStandardRequestHeaders(HttpHeaders requestHeaders, RenderingSession renderingSession) {
      RenderingOptions renderingOptions = null;
      if (renderingSession != null) {
         renderingOptions = renderingSession.getRenderingOptions();
      } else if (this._renderingSession != null) {
         renderingOptions = this._renderingSession.getRenderingOptions();
      }

      String userAgent = null;
      if (renderingOptions != null) {
         userAgent = renderingOptions.getPropertyWithStringValue(4550690918222697397L, 24, null);
      }

      if (userAgent != null) {
         requestHeaders.setProperty("User-Agent", userAgent);
      }

      if (requestHeaders.getPropertyValue("Accept") == null) {
         String acceptValue = null;
         if (renderingSession != null) {
            acceptValue = this.getAcceptValues(renderingSession);
         } else {
            acceptValue = this.getAcceptValue();
         }

         if (acceptValue != null) {
            requestHeaders.setProperty("Accept", acceptValue);
         }
      }

      boolean isAccelerated = this.isAccelerated();
      if (isAccelerated && requestHeaders.getPropertyValue("x-rim-transcode-content") == null) {
         RenderingUtilities.setTranscodeHeader(requestHeaders, true);
      }

      String acceptCharsetValue = null;
      if (renderingSession != null) {
         acceptCharsetValue = AcceptValueProviderRegistry.getAcceptCharsetValues();
      } else {
         acceptCharsetValue = this.getAcceptCharsetValue();
      }

      if (acceptCharsetValue != null) {
         requestHeaders.setProperty("Accept-Charset", acceptCharsetValue);
      }

      Locale locale = Locale.getDefault();
      String acceptLanguage = locale.getLanguage();
      String country = locale.getCountry();
      if (country.length() > 0) {
         acceptLanguage = ((StringBuffer)(new Object()))
            .append(acceptLanguage)
            .append('-')
            .append(country)
            .append(',')
            .append(acceptLanguage)
            .append(";q=0.5")
            .toString();
      }

      requestHeaders.setProperty("Accept-Language", acceptLanguage);
      if (this._currentSession != null
         && !StringUtilities.strEqualIgnoreCase(this._currentSession.getConfig().getPropertyAsString(3), BrowserConfigRecord.IPPP_SERVICE_CID, 1701707776)) {
         requestHeaders.setProperty("Accept-Encoding", "gzip,deflate");
      }

      String currentUAProf = null;
      if (renderingOptions != null) {
         currentUAProf = renderingOptions.getPropertyWithStringValue(4550690918222697397L, 29, null);
      }

      if (currentUAProf == null) {
         currentUAProf = UAProf.getDefaultUAProfURI();
      }

      requestHeaders.setProperty(HeaderParser.PROFILE, currentUAProf);
      if (renderingOptions != null && this._currentSession != null && this._currentSession.getConfig().getPropertyAsBoolean(52)) {
         String profileDiff = UAProfDiff.getProfileDiff(renderingOptions);
         if (profileDiff != null) {
            requestHeaders.setProperty(HeaderParser.PROFILE_DIFF, profileDiff);
            requestHeaders.setProperty(HeaderParser.X_WAP_PROFILE_DIFF, UAProfDiff.getXWapProfileDiff(renderingOptions));
         }

         requestHeaders.setProperty(HeaderParser.X_WAP_PROFILE, UAProfDiff.getXWapProfile(currentUAProf));
      } else {
         requestHeaders.setProperty(HeaderParser.X_WAP_PROFILE, ((StringBuffer)(new Object())).append('"').append(currentUAProf).append('"').toString());
      }

      if (isAccelerated) {
         int imageCount = 0;
         int imageDitherValue = 1;
         int wideViewMode = 0;
         if (renderingOptions != null) {
            if (renderingOptions.getPropertyWithBooleanValue(4550690918222697397L, 5, false)) {
               imageCount = Integer.MAX_VALUE;
            }

            imageDitherValue = renderingOptions.getPropertyWithIntValue(4550690918222697397L, 43, 1);
            wideViewMode = renderingOptions.getPropertyWithIntValue(4550690918222697397L, 44, 0);
         }

         requestHeaders.setProperty("x-rim-gw-properties", Page._richTextMarkup ? "16.2" : "16.10");
         requestHeaders.setProperty("x-rim-image-threshold", Integer.toString(imageCount));
         StringBuffer imageSetting = (StringBuffer)(new Object());
         if (imageDitherValue != 1) {
            imageSetting.append("q=");
            imageSetting.append(Integer.toString(imageDitherValue));
         }

         if (wideViewMode == 1) {
            if (imageSetting.length() > 0) {
               imageSetting.append(';');
            }

            imageSetting.append("vwidth=1024;vheight=768");
         }

         boolean progressiveSupported = ProgressiveImage.isProgressiveSupported();
         boolean cfiSupported = EncodedImage.isCFISupported();
         if (progressiveSupported || cfiSupported) {
            if (imageSetting.length() > 0) {
               imageSetting.append(';');
            }

            imageSetting.append("c=");
            if (progressiveSupported && cfiSupported) {
               imageSetting.append("75");
            } else if (progressiveSupported) {
               imageSetting.append("70");
            } else {
               imageSetting.append('5');
            }
         }

         if (imageSetting.length() > 0) {
            requestHeaders.setProperty("x-rim-img-setting", imageSetting.toString());
         }

         if (YKDecode.isSupported()) {
            requestHeaders.setProperty(
               "x-rim-accept-encoding",
               ((StringBuffer)(new Object("yk;v="))).append(YKDecode.yk_get_codec_version()).append(";m=").append(this._ykMemLimitHint).toString()
            );
         }
      }
   }

   public final void enqueRunnable(Runnable r) {
      this._workerThread.addToQueue(r);
   }

   public final Vector getActiveDownloads() {
      return this._activeDownloads;
   }

   final boolean freeStaleObject(int priority) {
      boolean active = this.isForeground() && (this._browserState == 3 || this._browserState == 4);
      if (this._currentSession != null) {
         PageCache pc = this._currentSession.getPageCache();
         if (pc != null && (!active || priority == 2)) {
            pc.destroy(true);
         }
      }

      Object ticket = PersistentContent.getTicket();
      if (ticket == null) {
         ticket = null;
      }

      CacheNode cacheNode = this._rawDataCache.removeExpiredShortTermNode();
      if (cacheNode == null) {
         if (!active) {
            cacheNode = this._rawDataCache.removeOldestShortTermNode();
         }

         if (cacheNode == null) {
            cacheNode = this._rawDataCache.removeExpiredLongTermNode();
         }

         if (cacheNode == null && priority == 2) {
            if (active) {
               cacheNode = this._rawDataCache.removeOldestShortTermNode();
            }

            if (cacheNode == null) {
               cacheNode = this._rawDataCache.removeOldestLongTermNode();
            }
         }
      }

      if (cacheNode != null) {
         LowMemoryManager.markAsRecoverable(cacheNode);
         return true;
      } else {
         return false;
      }
   }

   final void updateLastCoverage() {
      int oldCoverage = this._lastCoverage;
      this._lastCoverage = determineCoverage();
      if (this._lastCoverage > oldCoverage && !this._duringStartup) {
         this._pendingRequestThread.kickPendingRequests();
         OTAStatusReportSender.getOTAStatusReportSender().sendAllReports();
         if (this._lastCoverage >= 2) {
            this.scheduleAutoUpdate();
         }
      }
   }

   public final void serviceStatus(boolean serviceState, String service) {
      this.updateLastCoverage();
      ServiceRouting sr = ServiceRouting.getInstance();
      int handle = sr.getRouteHandle(2);
      if (handle != -1 && sr.isServiceRoutable(service, handle) && serviceState) {
         if (serviceState && service != null) {
            this._pendingRequestThread.kickPendingRequests();
            this.doCacheRevalidation(service);
         }
      } else {
         this.cancelCacheRevalidation();
      }
   }

   public final void routeStatus(boolean routeState, int route) {
      this.updateLastCoverage();
      ServiceRouting sr = ServiceRouting.getInstance();
      ServiceRoutingProperties srps = sr.getInterface(route);
      if (srps != null && srps.getLinkType() == 2 && !routeState) {
         this.cancelCacheRevalidation();
      } else {
         if (routeState) {
            this._pendingRequestThread.kickPendingRequests();
         }
      }
   }

   public final void doCacheRevalidation(String serialByPassService) {
      if (this._lastConfigUID == null
         || serialByPassService == null
         || StringUtilities.strEqualIgnoreCase(this._lastConfigUID, serialByPassService, 1701707776)) {
         BrowserConfigRecord rec = BrowserConfigRecord.getDecodedConfig(
            this._lastConfigUID != null ? this._lastConfigUID : serialByPassService, BrowserConfigRecord.INVALID_VALUE, BrowserConfigRecord.IPPP_SERVICE_CID
         );
         if (rec != null
            && (
               serialByPassService == null
                  || StringUtilities.strEqualIgnoreCase(rec.getPropertyAsString(3), BrowserConfigRecord.IPPP_SERVICE_CID, 1701707776)
                     && StringUtilities.strEqualIgnoreCase(rec.getPropertyAsString(4), serialByPassService, 1701707776)
            )) {
            long currentTime = System.currentTimeMillis();
            if (this._lastExpiryCheck + 300000 <= currentTime) {
               long nextExpiryTime = currentTime;
               if (serialByPassService != null) {
                  nextExpiryTime += 28800000;
                  if (this._lastExpiryCheck + 28800000 > currentTime) {
                     return;
                  }
               }

               this._lastExpiryCheck = currentTime;
               this.startResourceValidationThread(false, true, rec, nextExpiryTime);
            }
         }
      }
   }

   protected final void startResourceValidationThread(boolean autoUpdate, boolean validateCache, BrowserConfigRecord rec, long nextExpiryTime) {
      this._autoUpdateInvokeLater = -1;
      ResourceValidationThread thread = (ResourceValidationThread)this._resourceValidator.get();
      if (thread != null && !thread.isTerminated()) {
         if (autoUpdate) {
            this._autoUpdateInvokeLater = this.invokeLater(new DoAutoUpdate(this), 300000, false);
         }
      } else {
         ResourceValidationThread validator = new ResourceValidationThread(rec, this._rawDataCache, nextExpiryTime);
         if (validateCache) {
            EventLogger.logEvent(1907089860548946979L, 1381392739, 5);
            validator.addUrlsToValidate();
         }

         if (autoUpdate) {
            if (this._lastCoverage < 2) {
               return;
            }

            EventLogger.logEvent(1907089860548946979L, 1381392757, 5);
            long nextTime = validator.addBookmarksToValidate(this._lastAutoUpdateTime);
            this._lastAutoUpdateTime = System.currentTimeMillis();
            if (nextTime != Long.MAX_VALUE) {
               EventLogger.logEvent(1907089860548946979L, 1381397365, 5);
               this._autoUpdateInvokeLater = this.invokeLater(new DoAutoUpdate(this), Math.max(nextTime - System.currentTimeMillis(), 300000), false);
            } else {
               EventLogger.logEvent(1907089860548946979L, 1381396083, 5);
            }
         }

         Process process = Process.getProcess(this.getProcessId());
         process.addThread(validator);
         this._resourceValidator.set(validator);
      }
   }

   public final void bookmarksChanged() {
      this.scheduleAutoUpdate();
   }

   public final boolean isCoverageSufficient(String transportCid) {
      return !DeviceInfo.isSimulator() && StringUtilities.strEqualIgnoreCase(transportCid, BrowserConfigRecord.IPPP_SERVICE_CID, 1701707776)
         ? this._lastCoverage >= 2
         : this._lastCoverage >= 1;
   }

   final void radioStatus(boolean started) {
      this.updateLastCoverage();
   }

   final void networkSuccess() {
      this.updateLastCoverage();
      this._ribbonManager.autoStartIfRequired(false);
   }

   @Override
   public final boolean keyUp(int keycode, int time) {
      if (!this._escapeCharPressed
         || UiInternal.map(keycode) != 27 && (!this._isReducedKeyboard || (Keypad.status(keycode) & 1) != 0 || Keypad.key(keycode) != 68)) {
         if (this._isReducedKeyboard && (Keypad.status(keycode) & 1) == 0) {
            if (this._currentPage != null && this._currentPage.getBrowserContent() != null) {
               Manager contentManager = this._currentPage.getBrowserContent().getContentManager();
               if (contentManager != null) {
                  Field field = contentManager.getLeafFieldWithFocus();
                  if (field instanceof Object) {
                     return false;
                  }
               }
            }

            Verb verb = this._verbRepository.getVerbByHotkey((char)Keypad.key(keycode), this.getVerbMask());
            if (verb != null) {
               this._navigationThread.requestHotKeyInvoke(verb);
               return true;
            }

            if (Keypad.key(keycode) == 68) {
               verb = this._verbRepository.getVerb(4, this.getVerbMask());
               if (verb != null) {
                  this._navigationThread.requestHotKeyInvoke(verb);
                  return true;
               }
            }
         }

         return false;
      } else {
         this._navigationThread.requestTask(8, null);
         this._escapeCharPressed = false;
         return true;
      }
   }

   @Override
   public final boolean keyStatus(int keycode, int time) {
      return false;
   }

   @Override
   public final boolean keyRepeat(int keycode, int time) {
      if (this._escapeCharPressed && (UiInternal.map(keycode) == 27 || this._isReducedKeyboard && Keypad.key(keycode) == 68)) {
         if (this._currentPage != null && this._currentPage.isModified() && (!this._currentPage.savesContext() || this._startupPage != 2)) {
            ConfirmLeavePageRunnable confirm = new ConfirmLeavePageRunnable();
            this.invokeAndWait(confirm);
            this._escapeCharPressed = false;
            if (!confirm.getContinue()) {
               return true;
            }
         }

         Verb verb = this._verbRepository.getVerb(3, this.getVerbMask());
         if (verb != null) {
            verb.invoke(Boolean.TRUE);
         }

         this._escapeCharPressed = false;
         return true;
      } else {
         return false;
      }
   }

   @Override
   public final boolean openDevelopmentBackdoor(int backdoorCode) {
      switch (backdoorCode) {
         case 1380073800:
            this._useReducedAcceptHeader = !this._useReducedAcceptHeader;
            this.refreshAcceptValues();
            Status.show(
               ((StringBuffer)(new Object("Reduced HTTP Accept header is now "))).append(this._useReducedAcceptHeader ? "enabled" : "disabled").toString()
            );
            return true;
         case 1380074562:
            this._navigationThread.requestTask(7, null);
            return true;
         case 1380076100:
            JavaScriptRegistry._debugMode = !JavaScriptRegistry._debugMode;
            this.refreshAcceptValues();
            Status.show(((StringBuffer)(new Object("Javascript debug mode is now "))).append(JavaScriptRegistry._debugMode ? "on" : "off").toString());
            return true;
         default:
            return false;
      }
   }

   @Override
   public final boolean openProductionBackdoor(int backdoorCode) {
      return false;
   }

   @Override
   public final boolean keyDown(int keycode, int time) {
      int key = Keypad.key(keycode);
      switch (key) {
         case 19:
         case 21:
            if (this.handleConvenienceKey(key)) {
               return true;
            }
            break;
         case 68:
            if (this._isReducedKeyboard && (Keypad.status(keycode) & 1) == 0) {
               this._escapeCharPressed = true;
               return true;
            }
      }

      return this._backdoor.keyDown(keycode);
   }

   @Override
   public final boolean keyChar(char key, int status, int time) {
      if (key == 27) {
         this._escapeCharPressed = true;
         return true;
      }

      if (key == '\b') {
         Verb verb = this._verbRepository.getVerb(4, this.getVerbMask());
         if (verb != null) {
            this._navigationThread.requestHotKeyInvoke(verb);
            return true;
         }
      } else {
         if (key == '\n') {
            this._navigationThread.requestTask(2, null);
            return true;
         }

         if (key == 149 && Trackball.isSupported() && Ui.getTrackballClickAction() != 0) {
            this.showMenu(1073741824);
            return true;
         }
      }

      if (!this._isReducedKeyboard) {
         SLKeyLayout keyLayout = Keypad.getLayout();
         if (keyLayout != null) {
            key = UiInternal.map(keyLayout.getOriginalKeyCode(key, SLKeyLayout.convertStatusToModifiers(status)), status);
         }

         Verb verb = this._verbRepository.getVerbByHotkey(key, this.getVerbMask());
         if (verb != null) {
            this._navigationThread.requestHotKeyInvoke(verb);
            return true;
         }
      }

      return false;
   }

   @Override
   public final void browserConfigInvalid() {
   }

   @Override
   public final void browserConfigChanged() {
      this.browserConfigChanged(true);
   }

   @Override
   public final void optionsChanged(BitSet changedOptions) {
      this.optionsChanged(changedOptions, true);
   }

   private final void sessionOver() {
      switch (this._browserState) {
         case 1:
            return;
         case 2:
         case 3:
         case 4:
         default:
            Vector pendingRequests = this._pendingRequestThread.getPendingRequests();
            Vector savedPendingRequests = this._pendingRequestThread.getSavedPendingRequests();
            synchronized (pendingRequests) {
               synchronized (savedPendingRequests) {
                  if (pendingRequests.size() == 0 && savedPendingRequests.size() == 0) {
                     this.changeBrowserState(0);
                  } else {
                     this.changeBrowserState(1);
                  }
               }
            }
      }
   }

   private final void pushPage(Page page, boolean doGC, boolean newTab) {
      this._currentPage = page;
      synchronized (this.getAppEventLock()) {
         if (this._browserScreen == null) {
            this._browserScreen = new BrowserScreen();
            this._browserScreen.addKeyListener(this);
         }

         this._browserScreen.setPage(page, doGC, newTab);
         if (!this._browserScreen.isDisplayed()) {
            this.pushScreen(this._browserScreen);
         }
      }
   }

   private final void activateInternal() {
      synchronized (this.getAppEventLock()) {
         synchronized (this._activationObject) {
            if (this._currentSession == null) {
               String configUid = GeneralProperty.getOrDetermineDefaultBrowserConfigServiceUID();
               this._singleBrowserMode = true;
               this._initialConfigUID = configUid;
               this.activateConfig(configUid, false);
            }

            this.loadStartPage();
            int state = this.getBrowserState();
            if (state != 3 && state != 4) {
               this.changeBrowserState(3);
            }
         }
      }
   }

   private final void refreshSessions() {
      BrowserSession.refresh(null);
      BrowserSession session = BrowserSession.getCurrentSession();
      if (session != null && session.getConfig() == null) {
         this.closeBrowser(false);
      }
   }

   private final void loadStartPage() {
      int error = -1;
      if (this._currentSession != null && !this._currentSession.getConfig().isITEnabled()) {
         error = 643;
      } else if (System.currentTimeMillis() == -1) {
         error = 508;
      }

      if (error != -1) {
         BackgroundDialog.showMessage(BrowserResources.getString(error));
         this.closeBrowser(false);
      } else {
         this._currentUrlEncoding = null;
         if (this._renderingSession == null || this._activateDueToFetchRequest || this._activateDueToPush || this._browserClosed) {
            this._renderingSession = RenderingSessionImpl.getNewInstance();
         }

         this.browserConfigChanged(false);
         this.optionsChanged(GeneralProperty.ALL_OPTIONS_SET_MASK, false);
         this.refreshAcceptValues();
         if (this._activateDueToFetchRequest || this._activateDueToPush) {
            Screen activeScreen = this.getActiveScreen();
            if (activeScreen instanceof BookmarksScreen) {
               activeScreen.close();
            }

            this.pushSplashScreenIfNeeded();
            this._browserClosed = false;
            this._activateDueToPush = false;
            if (this._activatePushObject != null) {
               this._activatePushObject.browserActivated();
               this._activatePushObject = null;
            }
         } else if (this.getScreenCount() == 0) {
            this._browserClosed = false;
            this.popAllScreens();
            if (this._startupPage == 0) {
               this.showBookmarksScreen();
            } else if (this._startupPage == 3) {
               this.showStartupPage(false);
            } else {
               this.pushSplashScreenIfNeeded();
               HistoryNode node = this._currentSession != null ? this._currentSession.getHistory().currentNode() : null;
               boolean historyUpdate = true;
               String url = null;
               IBrowserContext context = null;
               String errorString = null;
               boolean isHomePage = false;
               HttpHeaders requestHeaders = null;
               byte[] postData = null;
               boolean historyRequest = false;
               if (node == null) {
                  isHomePage = true;
                  url = this._currentSession != null ? this._currentSession.getConfig().getHomePageWithOverride() : null;
                  if (url == null) {
                     errorString = BrowserResources.getString(233);
                  }

                  historyUpdate = true;
               } else {
                  isHomePage = node.isHomePage();
                  url = node.getUrl();
                  context = node.getContext();
                  historyUpdate = false;
                  requestHeaders = node.getRequestHeaders();
                  postData = node.getPostData();
                  historyRequest = true;
                  String currentConfigUID = null;
                  if (this._currentSession != null) {
                     currentConfigUID = this._currentSession.getConfig().getUid();
                  }

                  String historyConfigUID = currentConfigUID;
                  BrowserConfigRecord historyConfig = BrowserConfigRecord.getDecodedConfig(node.getConfigUID(), node.getConfigType(), node.getTransportCID());
                  if (historyConfig != null) {
                     historyConfigUID = historyConfig.getUid();
                  }

                  if (!StringUtilities.strEqualIgnoreCase(historyConfigUID, currentConfigUID, 1701707776)) {
                     this.activateConfig(historyConfigUID, true);
                  }
               }

               ModelResult modelResult = new ModelResult(url, 1 | (historyUpdate ? 8192 : 0), requestHeaders);
               modelResult.setContext(context);
               modelResult.setHomePage(isHomePage);
               modelResult.setPostData(postData);
               FetchRequest fetchRequest = new FetchRequest(modelResult);
               fetchRequest.setError(errorString);
               fetchRequest.setHistoryRequest(historyRequest);
               this.initiateFetchRequest(fetchRequest);
            }
         }
      }
   }

   private final void recursiveUpdate(Folder folder) {
      if (folder != null) {
         ReadableList bookmarksList = (ReadableList)folder.getContainedItems();

         for (int i = 0; i < bookmarksList.size(); i++) {
            PageModel model = (PageModel)bookmarksList.getAt(i);
            if (model != null && model.isHomePage()) {
               model.setTitle(BrowserResources.getString(355));
            }
         }

         Enumeration enumeration = folder.getSubFolders();

         while (enumeration.hasMoreElements()) {
            this.recursiveUpdate((Folder)enumeration.nextElement());
         }
      }
   }

   @Override
   public final void deactivate() {
      super.deactivate();
      FastDormancyManager.getInstance().setFastDormancy(true);
   }

   private final void showBookmarksScreen() {
      if (!(this.getActiveScreen() instanceof BookmarksScreen)) {
         BookmarksVerb bookmarksVerb = (BookmarksVerb)this._verbRepository.getVerb(1, 4194303);
         if (bookmarksVerb != null) {
            bookmarksVerb.setIsInitialScreen(true);
            this._navigationThread.requestExclusiveHotKeyInvoke(bookmarksVerb);
         }
      }
   }

   private final boolean handleConvenienceKey(int key) {
      if (RibbonManagerThread.browserOwnsConvenienceKey(key)) {
         Verb verb = this._verbRepository.getVerb(1, this.getVerbMask());
         if (verb != null) {
            this._navigationThread.requestHotKeyInvoke(verb);
            return true;
         }
      }

      return false;
   }

   private final void optionsChanged(BitSet changedOptions, boolean refreshAcceptValuesIfNecessary) {
      if (this._renderingSession != null) {
         RenderingOptions renderingOptions = this._renderingSession.getRenderingOptions();
         this.setGeneralPropertyRenderingOptions(renderingOptions, changedOptions);
         if (refreshAcceptValuesIfNecessary && (changedOptions.isSet(2) || changedOptions.isSet(6) || changedOptions.isSet(21))) {
            this.refreshAcceptValues();
         }

         if (changedOptions.isSet(48) && this._browserScreen != null) {
            this._browserScreen.setMobileViewCursor(renderingOptions.getPropertyWithBooleanValue(4550690918222697397L, 47, false));
         }
      }
   }

   private final void setGeneralPropertyRenderingOptions(RenderingOptions renderingOptions, BitSet changedOptions) {
      synchronized (renderingOptions) {
         if (changedOptions.isSet(14)) {
            renderingOptions.setProperty(4550690918222697397L, 1, GeneralProperty.getDefaultCharsetModeValue() == 1);
         }

         if (changedOptions.isSet(13)) {
            renderingOptions.setProperty(4550690918222697397L, 8, GeneralProperty.getDefaultCharsetValue());
         }

         if (changedOptions.isSet(1)) {
            renderingOptions.setProperty(4550690918222697397L, 9, GeneralProperty.getCurrentPropertyAsBoolean(1));
         }

         if (changedOptions.isSet(2)) {
            renderingOptions.setProperty(4550690918222697397L, 12, this.getAnimationCount());
         }

         if (changedOptions.isSet(42)) {
            renderingOptions.setProperty(4550690918222697397L, 43, GeneralProperty.getCurrentPropertyAsInt(42));
         }

         renderingOptions.setProperty(4550690918222697397L, 10, 5);
         renderingOptions.setProperty(4550690918222697397L, 11, 10);
         if (changedOptions.isSet(23)) {
            renderingOptions.setProperty(4550690918222697397L, 16, GeneralProperty.getCurrentPropertyAsBoolean(23));
         }

         if (changedOptions.isSet(26)) {
            renderingOptions.setProperty(4550690918222697397L, 31, GeneralProperty.getDefaultFontFamily());
         }

         if (changedOptions.isSet(27)) {
            renderingOptions.setProperty(4550690918222697397L, 32, GeneralProperty.getCurrentPropertyAsInt(27));
         }

         if (changedOptions.isSet(36)) {
            renderingOptions.setProperty(4550690918222697397L, 35, GeneralProperty.getCurrentPropertyAsInt(36));
         }

         if (changedOptions.isSet(37)) {
            renderingOptions.setProperty(4550690918222697397L, 36, GeneralProperty.getCurrentPropertyAsInt(37));
         }

         if (changedOptions.isSet(35)) {
            this._menuDelayThread.setDelay(GeneralProperty.getCurrentPropertyAsInt(35));
         }

         if (changedOptions.isSet(43)) {
            renderingOptions.setProperty(4550690918222697397L, 44, GeneralProperty.getCurrentPropertyAsInt(43));
         }

         if (changedOptions.isSet(48)) {
            renderingOptions.setProperty(4550690918222697397L, 47, GeneralProperty.getCurrentPropertyAsBoolean(48));
         }
      }
   }

   private final int getAnimationCount() {
      if (!Display.isColor()) {
         return 0;
      }

      switch (GeneralProperty.getCurrentPropertyAsInt(2)) {
         case -1:
            return 100;
         case 0:
         default:
            return 0;
         case 1:
            return 1;
         case 2:
            return 10;
         case 3:
            return 100;
         case 4:
            return Integer.MAX_VALUE;
      }
   }

   private final int getMaxInputChars(String transportCid) {
      return StringUtilities.strEqualIgnoreCase(transportCid, WAPServiceRecord.SERVICE_CID, 1701707776) ? 512 : 60000;
   }

   private final void popAllScreens() {
      synchronized (this.getAppEventLock()) {
         for (Screen activeScreen = this.getActiveScreen(); activeScreen != null; activeScreen = this.getActiveScreen()) {
            if (activeScreen instanceof BrowserLockScreen) {
               ((BrowserLockScreen)activeScreen).cleanupScreen();
            }

            this.popScreen(activeScreen);
         }

         this._browserScreen = null;
         this._downloadPage = null;
      }
   }

   private final void closeBrowserInternal() {
      this._navigationThread.clear();
      this.changeToLoadedState(null);
      this.sessionOver();
      this.popAllScreens();
      this._currentPage = null;
      this._activateDueToFetchRequest = false;
      this._activateDueToPush = false;
      this._viewingMode = false;
      this._browserClosed = true;
      this._singleBrowserMode = false;
      this._initialConfigUID = null;
      this._startupPage = BrowserConfigRecord.INVALID_VALUE;
      if (WLAN.isSupported()) {
         RIMGlobalMessagePoster.postGlobalEvent(3212036545190435442L);
      }

      this._ribbonManager.autoStartIfRequired(true);
   }

   private final void initiateFetchRequestInternal(FetchRequest fetchRequest) {
      this.changeToLoadedState(null);
      this._requestingNewPage = true;
      this._currentRenderThread = new RenderThread(this, fetchRequest);
      EventLogger.logEvent(1907089860548946979L, 1114797171, 5);
      this._currentRenderThread.start();
   }

   private final Frame getOriginatingFrame(Event event) {
      if (event != null) {
         BrowserContent sourceFrame = getSource(event);
         if (sourceFrame != null) {
            Manager manager = sourceFrame.getDisplayableContent().getManager();
            if (manager instanceof Object) {
               return ((FrameManager)manager).getFrame();
            }
         }
      }

      return null;
   }

   private final void setTargetFrame(FetchRequest fetchRequest) {
      Frame frame = null;
      if ((fetchRequest.getModelResult().getRenderingFlags() & 16) != 0) {
         Event event = getOriginalEvent(fetchRequest.getEvent());
         Frame originatingFrame = frame = this.getOriginatingFrame(event);
         Event fetchRequestEvent = fetchRequest.getEvent();
         if (fetchRequestEvent instanceof Object) {
            UrlRequestedInternalEvent e = (UrlRequestedInternalEvent)fetchRequestEvent;
            String target = e.getTarget();
            Frame targetFrame = this.findFrame(target);
            if (targetFrame != null) {
               targetFrame.setUrl(fetchRequest.getModelResult().getURL());
            }
         }

         boolean useFullWindow = false;
         String target = null;
         if (event instanceof Object) {
            target = ((UrlRequestedInternalEvent)event).getTarget();
         }

         if (target != null) {
            if (target.length() >= 4 && target.charAt(0) == '_') {
               switch (target.charAt(1)) {
                  case 'B':
                  case 'b':
                     if (StringUtilities.strEqualIgnoreCase(target, "_blank", 1701707776)) {
                        useFullWindow = true;
                        frame = null;
                     }
                     break;
                  case 'P':
                  case 'p':
                     if (StringUtilities.strEqualIgnoreCase(target, "_parent", 1701707776)) {
                        if (frame != null) {
                           frame = frame.getParent();
                        }

                        target = null;
                     }
                     break;
                  case 'S':
                  case 's':
                     if (StringUtilities.strEqualIgnoreCase(target, "_self", 1701707776)) {
                        target = null;
                     }
                     break;
                  case 'T':
                  case 't':
                     if (StringUtilities.strEqualIgnoreCase(target, "_top", 1701707776)) {
                        useFullWindow = true;
                        frame = null;
                     }
               }
            }

            if (frame != null && target != null) {
               frame = frame.getTop().find(target);
               if (frame == null) {
                  useFullWindow = true;
               }
            }
         }

         if (frame != null) {
            frame = Frame.getClone(frame, false);
            frame.removeChildren();
            frame.setUrl(fetchRequest.getModelResult().getURL());
         }

         if ((frame == null || frame.getParent() == null) && (useFullWindow || originatingFrame != null)) {
            fetchRequest.getModelResult().setEmbedded(false);
         }
      }

      if (frame != null) {
         Frame f = Frame.findFrameById(frame.getTop(), frame.getId());
         if (f != null) {
            fetchRequest.setTarget(frame);
         } else {
            frame = this.findFrame(frame.getName());
            fetchRequest.setTarget(frame);
         }
      } else {
         fetchRequest.setTarget((Frame)(new Object(null, null, fetchRequest.getModelResult().getURL())));
      }
   }

   private final String getAbsoluteUrl(String url, FetchRequest fetchRequest) {
      URLCache urlCache = null;
      if (this._currentPage != null && this._currentPage.getBrowserContent() != null) {
         urlCache = this._currentPage.getBrowserContent().getUrlCache();
      }

      String absUrl;
      if (urlCache != null) {
         absUrl = urlCache.getAbsoluteURL(url, false);
      } else {
         absUrl = URI.getAbsoluteURL(url, null);
      }

      if (absUrl != null && absUrl.length() != 0 && !absUrl.equals("http://") && !absUrl.equals("https://")) {
         switch (absUrl.charAt(0)) {
            case 'M':
            case 'm':
               if (StringUtilities.startsWithIgnoreCase(absUrl, "mobile.", 1701707776)) {
                  absUrl = ((StringBuffer)(new Object("http://"))).append(absUrl).toString();
               }
               break;
            case 'W':
            case 'w':
               if (StringUtilities.startsWithIgnoreCase(absUrl, "www.", 1701707776) || StringUtilities.startsWithIgnoreCase(absUrl, "wap.", 1701707776)) {
                  return ((StringBuffer)(new Object("http://"))).append(absUrl).toString();
               }
         }

         return absUrl;
      } else {
         fetchRequest.setError(((StringBuffer)(new Object())).append(BrowserResources.getString(296)).append(url).toString());
         return absUrl;
      }
   }

   private final void resetJavaScriptOption(ModelResult modelResult, FetchRequest fetchRequest) {
      if ((modelResult.getRenderingFlags() & 8192) != 0 && this._renderingSession != null) {
         RenderingOptions renderingOptions = this._renderingSession.getRenderingOptions();
         if (renderingOptions != null) {
            boolean isLink = getOriginalEvent(fetchRequest.getEvent()) instanceof Object;
            int navigation = modelResult.getNavigation();
            boolean isRefresh = navigation == 3 || navigation == 4;
            if (renderingOptions.getPropertyWithBooleanValue(4550690918222697397L, 42, false) && !isLink) {
               if (this._currentSession != null) {
                  BrowserConfigRecord browserConfigRecord = this._currentSession.getConfig();
                  if (browserConfigRecord != null) {
                     renderingOptions.setProperty(4550690918222697397L, 2, browserConfigRecord.getPropertyAsBooleanWithOverride((byte)38));
                     this.refreshAcceptValues();
                  }
               }

               renderingOptions.setProperty(4550690918222697397L, 42, false);
            }

            if (!renderingOptions.getPropertyWithBooleanValue(4550690918222697397L, 2, false) && this._currentSession != null) {
               BrowserConfigRecord browserConfigRecord = this._currentSession.getConfig();
               if (browserConfigRecord != null
                  && "S TCP-WBC".equalsIgnoreCase(browserConfigRecord.getUid())
                  && "http://www.blackberry.com/select/wifiloginsuccess/".equals(modelResult.getURL())) {
                  renderingOptions.setProperty(4550690918222697397L, 2, true);
                  renderingOptions.setProperty(4550690918222697397L, 42, true);
               }
            }

            if (renderingOptions.getPropertyWithBooleanValue(4550690918222697397L, 45, false) && !isLink && !isRefresh) {
               renderingOptions.setProperty(4550690918222697397L, 45, false);
               renderingOptions.setProperty(4550690918222697397L, 44, GeneralProperty.getCurrentPropertyAsInt(43));
            }
         }
      }
   }

   private final StateHolder doIfRequestIsNotReload(
      FetchRequest fetchRequest, String absUrl, String currentUrl, ModelResult modelResult, boolean updateFrame, int navigation
   ) {
      StateHolder temp = new StateHolder();
      temp._absUrl = absUrl;
      temp._currentUrl = currentUrl;
      temp._updateFrame = updateFrame;
      if (fetchRequest.getError() == null
         && absUrl != null
         && currentUrl != null
         && modelResult.getPostData() == null
         && this._currentPage.supportsFragmentJumping()) {
         int indexOfFragment = currentUrl.indexOf(35);
         if (indexOfFragment != -1) {
            currentUrl = currentUrl.substring(0, indexOfFragment);
            temp._currentUrl = currentUrl;
         }

         String urlToLoad = absUrl;
         indexOfFragment = absUrl.indexOf(35);
         if (indexOfFragment != -1) {
            urlToLoad = absUrl.substring(0, indexOfFragment);
         }

         Frame newFrameset = fetchRequest.getTarget();
         if (newFrameset != null) {
            newFrameset = newFrameset.getTop();
         }

         boolean jumpToFragment = samePage(currentUrl, newFrameset != null ? newFrameset.getUrl() : urlToLoad);
         if (jumpToFragment) {
            Frame currentFrameset = this._currentPage.getFrameset();
            if (currentFrameset != null && newFrameset != null) {
               Frame[] newChildren = newFrameset.getChildren();
               if (newChildren != null && newChildren.length > 0) {
                  FrameComparator frameComparator = new FrameComparator();
                  updateFrame = !frameComparator.equals(newFrameset, currentFrameset);
                  temp._updateFrame = updateFrame;
                  if (updateFrame) {
                     jumpToFragment = false;
                     Frame differentFrame1 = frameComparator.getDifferentFrame1();
                     if (differentFrame1 != null) {
                        String newUrl = differentFrame1.getUrl();
                        if (newUrl != null) {
                           fetchRequest.setTarget(differentFrame1);
                           absUrl = newUrl;
                           temp._absUrl = absUrl;
                           indexOfFragment = absUrl.indexOf(35);
                           if (indexOfFragment != -1) {
                              urlToLoad = absUrl.substring(0, indexOfFragment);
                           } else {
                              urlToLoad = absUrl;
                           }

                           Frame differentFrame2 = frameComparator.getDifferentFrame2();
                           if (differentFrame2 != null && samePage(urlToLoad, differentFrame2.getUrl())) {
                              jumpToFragment = true;
                           } else {
                              if (!absUrl.equals(modelResult.getURL())) {
                                 modelResult.setURL(absUrl);
                                 modelResult.setRequestHeaders(null);
                              }

                              modelResult.setEmbedded(true);
                           }
                        }
                     }
                  }
               }
            }
         }

         if (jumpToFragment) {
            this._currentPage.updateScroll();
            if (!updateFrame) {
               ((ModelResult)this._currentPage.getModelResult()).setURL(absUrl);
            }

            this._currentPage.setFrameset(newFrameset);
            String historyUrl = null;
            if (newFrameset != null) {
               historyUrl = newFrameset.getUrl();
            }

            if (historyUrl == null) {
               historyUrl = absUrl;
            }

            if ((modelResult.getRenderingFlags() & 8192) == 0 && !this._currentPage.jumpToLastScrollPosition(absUrl)) {
               temp._exitFetchRequest = true;
               return temp;
            }

            if (indexOfFragment != -1) {
               String fragment = absUrl.substring(indexOfFragment + 1);
               boolean jumpSucceeded = false;
               if (updateFrame) {
                  BrowserContentImpl frameBrowserContent = this._currentPage.getBrowserContent(fetchRequest.getTarget().getName());
                  if (frameBrowserContent != null) {
                     jumpSucceeded = frameBrowserContent.jumpToFragment(fragment);
                  }
               } else {
                  jumpSucceeded = this._currentPage.jumpToFragment(fragment);
               }

               if (jumpSucceeded && newFrameset != null) {
                  if (this._currentPage.getFrameset() != null && newFrameset.getUrl().equalsIgnoreCase(this._currentPage.getFrameset().getUrl())) {
                     newFrameset = this._currentPage.getFrameset();
                  }

                  if (this._currentSession != null && (modelResult.getRenderingFlags() & 8192) != 0) {
                     this._currentSession
                        .getHistory()
                        .addNewNode(
                           new HistoryNode(
                              historyUrl,
                              this._currentPage.getFriendlyTitle(),
                              this._currentPage.getContext(),
                              modelResult.getPostData(),
                              modelResult.getRequestHeaders(),
                              modelResult.isHomePage(),
                              this._currentSession.getConfig(),
                              -1,
                              Frame.getClone(newFrameset, false)
                           ),
                           false
                        );
                  }
               }

               temp._exitFetchRequest = true;
               return temp;
            }

            if (navigation == 2
               && this._rawDataCache.get(urlToLoad, navigation, currentUrl, null, modelResult.getRequestHeaders(), fetchRequest.isHistoryRequest()) != null) {
               this._currentPage.scrollToTop();
               if (this._currentSession != null && (modelResult.getRenderingFlags() & 8192) != 0) {
                  this._currentSession
                     .getHistory()
                     .addNewNode(
                        new HistoryNode(
                           historyUrl,
                           this._currentPage.getFriendlyTitle(),
                           this._currentPage.getContext(),
                           modelResult.getPostData(),
                           modelResult.getRequestHeaders(),
                           modelResult.isHomePage(),
                           this._currentSession.getConfig(),
                           -1,
                           Frame.getClone(newFrameset, false)
                        ),
                        false
                     );
               }

               temp._exitFetchRequest = true;
               return temp;
            }
         }
      }

      return temp;
   }

   private final void closeWLAN() {
      if (this._currentSession != null) {
         boolean isBlackberryDotCom = false;
         if (this._currentPage != null) {
            String currentUrl = this._currentPage.getURL();
            if (currentUrl != null) {
               label64:
               try {
                  String host = DomainUtilities.parseAuthority((URI)(new Object(currentUrl)));
                  isBlackberryDotCom = DomainUtilities.isHostInDomain(host, "blackberry.com") || DomainUtilities.isHostInDomain(host, "blackberry.net");
               } finally {
                  break label64;
               }
            }
         }

         BrowserConfigRecord config = this._currentSession.getConfig();
         if (isBlackberryDotCom || config != null && (config.getPropertyAsInt(12) == 2 || config.getPropertyAsInt(7) == 2)) {
            this._ribbonManager.disableAutoStart();
            this.closeBrowser(false);
         }
      }
   }

   private final Page getPage(FetchRequest fetchRequest, String absUrl, ModelResult modelResult, boolean updateFrame, RenderThread localRenderThread) {
      Page page = null;
      if (page == null && fetchRequest.getError() == null) {
         if (this._currentSession != null && modelResult.getPostData() == null && !updateFrame) {
            PageCache pc = this._currentSession.getPageCache();
            if (pc != null) {
               PageCacheContent content = pc.removeContent(absUrl, modelResult.getNavigation(), PersistentContent.decodeString(this._currentUrlEncoding));
               if (content != null) {
                  CacheResult cr = content.getCacheResult();
                  modelResult.setCacheResult(cr);
                  if (cr != null && cr.getSecured()) {
                     fetchRequest.setSecurityInfo(new CachedSecurityInfo());
                  }

                  BrowserContentImpl ui = content.getUI();
                  page = new Page(fetchRequest, ui, content.getStyle());
                  ui.setRenderingApplication(page);
               }
            }
         }

         if (page == null) {
            page = this.getPageFromFetchRequest(fetchRequest, localRenderThread);
         }
      }

      return page;
   }

   private final void changeToLoadedState(Page page) {
      Asserts.productionAssert(this._browserLock.lockAcquired());
      switch (this._state) {
         case 1:
         case 2:
         case 3:
         case 6:
         case 7:
         case 8:
         case 9:
         default:
            if (this._currentFetchRequest != null && (this._currentFetchRequest.getFlags() & 4) == 0) {
               this._currentFetchRequest.abort();
               if (this._currentRenderThread != null) {
                  this._currentRenderThread.abort();
               }
            }

            if (this._currentPage != null) {
               this._currentPage.cancelAllJobs();
            }
         case 0:
         case 4:
            this._state = 0;
            if (page != null) {
               this._currentPage = page;
            }

            this._requestingNewPage = false;
            this._currentRenderThread = null;
            this._currentFetchRequest = null;
            this.updatePageLabel();
            return;
         case 5:
      }
   }

   private final boolean doRequestErrorHandling(FetchRequest fetchRequest, Page page) {
      this._progressManager.finishEarly();
      Object error = fetchRequest.getError();
      if (fetchRequest.isAborted()) {
         this._navigationThread.requestTask(5, fetchRequest);
         return false;
      }

      String errorMessage = null;
      if (!(error instanceof Object)) {
         if (!(error instanceof Object)) {
            errorMessage = error.toString();
         } else {
            errorMessage = ((Throwable)error).getMessage();
         }
      } else {
         errorMessage = (String)error;
      }

      if (errorMessage == null) {
         errorMessage = BrowserResources.getString(235);
      }

      BrowserError errorDialog = new BrowserError(errorMessage, page != null, fetchRequest.isSaveAllowed());
      fetchRequest.registerAbortListener(errorDialog, errorDialog);
      synchronized (errorDialog) {
         this.invokeLater(errorDialog);

         label85:
         try {
            errorDialog.wait();
         } finally {
            break label85;
         }
      }

      fetchRequest.deRegisterAbortListener(errorDialog, errorDialog);
      if (fetchRequest.isAborted()) {
         return false;
      }

      if (errorDialog.saveRequest()) {
         ModelResult saveResult = fetchRequest.getModelResult().clone();
         saveResult.setCacheResult(null);
         String uid = null;
         String cid = null;
         if (this._currentSession != null) {
            BrowserConfigRecord config = this._currentSession.getConfig();
            if (config != null) {
               uid = config.getUid();
               cid = config.getPropertyAsString(3);
            }
         }

         FetchRequest saveRequest = new FetchRequest(saveResult);
         saveRequest.saveInMessageList(false, uid, cid);
      }

      if (!errorDialog.showDetails()) {
         this._navigationThread.requestTask(5, fetchRequest);
         return false;
      } else {
         return true;
      }
   }

   private final void setBrowserConfigRenderingOptions(RenderingOptions renderingOptions, BrowserConfigRecord browserConfigRecord) {
      if (browserConfigRecord != null) {
         synchronized (renderingOptions) {
            int showImagesValue = browserConfigRecord.getPropertyAsIntWithOverride((byte)18);
            renderingOptions.setProperty(4550690918222697397L, 5, showImagesValue == 2);
            renderingOptions.setProperty(4550690918222697397L, 6, showImagesValue != 0);
            renderingOptions.setProperty(4550690918222697397L, 7, browserConfigRecord.getPropertyAsIntWithOverride((byte)19));
            String transportCid = browserConfigRecord.getPropertyAsString(3);
            renderingOptions.setProperty(4550690918222697397L, 14, this.getMaxInputChars(transportCid));
            boolean lockedDown = browserConfigRecord.getPropertyAsInt(7) == 2;
            renderingOptions.setProperty(4550690918222697397L, 39, lockedDown);
            renderingOptions.setProperty(4550690918222697397L, 30, !lockedDown);
            renderingOptions.setProperty(4550690918222697397L, 38, !lockedDown);
            renderingOptions.setProperty(4550690918222697397L, 34, !lockedDown);
            String homePage = browserConfigRecord.getHomePageWithOverride();
            if (homePage != null) {
               renderingOptions.setProperty(4550690918222697397L, 15, homePage);
            }

            int contentModeValue = browserConfigRecord.getContentModeWithOverride();
            renderingOptions.setProperty(4550690918222697397L, 22, contentModeValue == 1 || contentModeValue == 2);
            renderingOptions.setProperty(4550690918222697397L, 21, contentModeValue == 1 || contentModeValue == 0);
            renderingOptions.setProperty(
               4550690918222697397L, 24, GeneralProperty.getEmulationModeString(browserConfigRecord.getPropertyAsIntWithOverride((byte)17) - 1)
            );
            renderingOptions.setProperty(
               4550690918222697397L, 23, ((StringBuffer)(new Object())).append(UserAgent.getBrowserVersion()).append(" (BlackBerry; I)").toString()
            );
            boolean wapMode = StringUtilities.strEqualIgnoreCase(WAPServiceRecord.SERVICE_CID, transportCid, 1701707776);
            renderingOptions.setProperty(4550690918222697397L, 25, wapMode);
            int configType = browserConfigRecord.getPropertyAsInt(12);
            renderingOptions.setProperty(4550690918222697397L, 49, wapMode || configType == 1);
            int constrainedSaving = browserConfigRecord.getPropertyAsInt(27);
            renderingOptions.setProperty(4550690918222697397L, 26, !lockedDown && (constrainedSaving & 1) == 0);
            renderingOptions.setProperty(4550690918222697397L, 27, !lockedDown && (constrainedSaving & 2) == 0);
            renderingOptions.setProperty(4550690918222697397L, 28, true);
            if (this._currentSession != null) {
               renderingOptions.setProperty(4550690918222697397L, 33, this._currentSession.getHmacKey());
            }

            renderingOptions.setProperty(4550690918222697397L, 2, browserConfigRecord.getPropertyAsBooleanWithOverride((byte)38));
            renderingOptions.setProperty(4550690918222697397L, 3, browserConfigRecord.getPropertyAsBooleanWithOverride((byte)35));
            renderingOptions.setProperty(4550690918222697397L, 4, browserConfigRecord.getPropertyAsBooleanWithOverride((byte)40));
            renderingOptions.setProperty(4550690918222697397L, 13, browserConfigRecord.getPropertyAsBooleanWithOverride((byte)39));
            renderingOptions.setProperty(4550690918222697397L, 17, browserConfigRecord.getPropertyAsBooleanWithOverride((byte)36));
            renderingOptions.setProperty(4550690918222697397L, 18, browserConfigRecord.getPropertyAsBooleanWithOverride((byte)33));
            renderingOptions.setProperty(4550690918222697397L, 19, "screen");
            renderingOptions.setProperty(4550690918222697397L, 20, browserConfigRecord.getPropertyAsBooleanWithOverride((byte)37));
            renderingOptions.setProperty(4550690918222697397L, 37, browserConfigRecord.getPropertyAsBoolean(53));
            boolean useBrandedUAProf = false;
            if (configType == 0 || configType == 7) {
               useBrandedUAProf = true;
            }

            String uaProf = UAProf.getFormattedUAProfURI(browserConfigRecord.getPropertyAsString(8));
            if (uaProf == null) {
               uaProf = UAProf.getDefaultUAProfURI(useBrandedUAProf);
            }

            renderingOptions.setProperty(4550690918222697397L, 29, uaProf);
            renderingOptions.setProperty(4550690918222697397L, 46, browserConfigRecord.getPropertyAsInt(58));
         }
      }
   }

   private final void changeToLoadingFetchingState(FetchRequest fetchRequest) {
      Asserts.productionAssert(this._browserLock.lockAcquired());
      Asserts.productionStateAssert(this._state == 0);
      this._state = 1;
      this._currentFetchRequest = fetchRequest;
      this.updatePageLabel();
   }

   private final synchronized String getAcceptValue() {
      if (this._acceptValueEncoding != null) {
         return PersistentContent.decodeString(this._acceptValueEncoding);
      }

      String acceptValue = this.getAcceptValues(this._renderingSession);
      this._acceptValueEncoding = PersistentContent.encode(acceptValue, false, true);
      return acceptValue;
   }

   private final synchronized String getAcceptCharsetValue() {
      if (this._acceptCharsetValueEncoding != null) {
         return PersistentContent.decodeString(this._acceptCharsetValueEncoding);
      }

      String acceptCharsetValue = AcceptValueProviderRegistry.getAcceptCharsetValues();
      this._acceptCharsetValueEncoding = PersistentContent.encode(acceptCharsetValue, false, true);
      return acceptCharsetValue;
   }

   private final void changeToLoadingRenderingState() {
      Asserts.productionAssert(this._browserLock.lockAcquired());
      Asserts.productionStateAssert(this._state == 0 || this._state == 1 || this._state == 9);
      this._state = 2;
      this.updatePageLabel();
   }

   private final void attachTimerIfRequired(Page page, FetchRequest fetchRequest) {
      String value = fetchRequest.getModelResult().getCacheResult() != null
         ? fetchRequest.getModelResult().getCacheResult().getResponseHeaders().getPropertyValue(HeaderParser.REFRESH)
         : null;
      if (value != null && value.length() > 0) {
         int delay = getDelay(value);
         String refreshUrl = getURL(value);
         if (refreshUrl == null) {
            return;
         }

         BrowserContentImpl browserContent = page.getBrowserContent();
         if (browserContent != null) {
            browserContent.setOnTimer((Verb)(new Object((Event)(new Object(browserContent, refreshUrl, null, 3, delay * 1000)), page)));
            browserContent.addTimer((PageTimer)(new Object(delay, browserContent)));
         }
      }
   }

   private final void changeToLoadingSecondaryURLsState(Page page) {
      Asserts.productionAssert(this._browserLock.lockAcquired());
      this._state = 3;
      if (page != null) {
         this._currentPage = page;
      }

      this._requestingNewPage = false;
      if (Thread.currentThread() != this._currentRenderThread) {
         this._currentRenderThread = null;
         this._currentFetchRequest = null;
      }

      this.updatePageLabel();
   }

   private final boolean isAccelerated() {
      int configType = -1;
      if (this._currentSession != null) {
         configType = this._currentSession.getConfig().getPropertyAsInt(12);
      }

      return configType != 3 && configType != 0 && configType != 7;
   }

   private final String getAcceptValues(RenderingSession renderingSession) {
      StringBuffer acceptValues = (StringBuffer)(new Object());
      if (this._useReducedAcceptHeader && renderingSession != null) {
         RenderingOptions renderingOptions = renderingSession.getRenderingOptions();
         if (renderingOptions != null
            && renderingOptions.getPropertyWithBooleanValue(4550690918222697397L, 22, true)
            && renderingOptions.getPropertyWithBooleanValue(4550690918222697397L, 21, true)) {
            boolean isAccelerated = this.isAccelerated();
            if (isAccelerated) {
               acceptValues.append("application/vnd.rim.html,");
            }

            acceptValues.append(
               "text/html,application/xhtml+xml,application/vnd.wap.xhtml+xml,application/vnd.wap.wmlc;q=0.9,application/vnd.wap.wmlscriptc;q=0.7,"
            );
            if (!renderingOptions.getPropertyWithBooleanValue(4550690918222697397L, 25, false)) {
               acceptValues.append("text/vnd.wap.wml;q=0.7,");
            }

            if (!ITPolicy.getBoolean(24, 11, false)) {
               acceptValues.append("text/vnd.sun.j2me.app-descriptor,");
            }

            if (isAccelerated) {
               acceptValues.append("image/vnd.rim.png,image/jpeg,application/x-vnd.rim.pme.b,");
               Vector supportedTypes = renderingSession.getSupportedMimeType();
               if (supportedTypes != null && supportedTypes.contains("application/vnd.rim.ucs")) {
                  acceptValues.append("application/vnd.rim.ucs");
                  acceptValues.append(',');
               }

               if (renderingOptions.getPropertyWithIntValue(4550690918222697397L, 12, 10) != 0) {
                  acceptValues.append("image/gif;anim=1,");
               }

               if (renderingOptions.getPropertyWithBooleanValue(4550690918222697397L, 2, false) && JavaScriptRegistry.isInstalled()) {
                  String[] javaScriptTypes = JavaScriptRegistry.getMimeTypes(false);
                  if (javaScriptTypes != null) {
                     for (int i = 0; i < javaScriptTypes.length; i++) {
                        acceptValues.append(javaScriptTypes[i]);
                        acceptValues.append(',');
                     }
                  }
               }

               if (renderingOptions.getPropertyWithBooleanValue(4550690918222697397L, 18, true)) {
                  acceptValues.append("application/vnd.rim.css;v=1");
                  acceptValues.append(',');
                  acceptValues.append("text/css;media=");
                  acceptValues.append("screen");
                  acceptValues.append(',');
               }
            }

            acceptValues.append("*/*;q=0.5");
            return acceptValues.toString();
         }
      }

      boolean appendSeparator = false;
      if (renderingSession != null) {
         acceptValues.append(renderingSession.getAcceptTypes());
         appendSeparator = true;
      }

      Hashtable currentStore = (Hashtable)(new Object());
      int size = this._acceptValueProviders.size();

      for (int i = 0; i < size; i++) {
         AcceptValueProvider element = (AcceptValueProvider)this._acceptValueProviders.elementAt(i);
         String[] acceptValue = element.getAccept(null);
         if (acceptValue != null) {
            int valueSize = acceptValue.length;

            for (int j = 0; j < valueSize; j++) {
               String stringToFind = StringUtilities.toLowerCase(acceptValue[j], 1701707776);
               if (!currentStore.containsKey(stringToFind)) {
                  if (appendSeparator) {
                     acceptValues.append(',');
                  } else {
                     appendSeparator = true;
                  }

                  acceptValues.append(stringToFind);
                  currentStore.put(stringToFind, currentStore);
               }
            }
         }
      }

      if (renderingSession != null) {
         RenderingOptions renderingOptions = renderingSession.getRenderingOptions();
         if (renderingOptions != null && renderingOptions.getPropertyWithBooleanValue(4550690918222697397L, 25, false)) {
            if (appendSeparator) {
               acceptValues.append(',');
            }

            acceptValues.append("application/vnd.wap.sia");
         }
      }

      return acceptValues.toString();
   }

   private final void changeToExecutingScriptState() {
      Asserts.productionAssert(this._browserLock.lockAcquired());
      this._state = 4;
      this.updatePageLabel();
   }

   private final void changeToLoadingNegotiatingState() {
      Asserts.productionAssert(this._browserLock.lockAcquired());
      Asserts.productionStateAssert(this._state == 1);
      this._state = 6;
      this.updatePageLabel();
   }

   private final void changeToLoadingWaitingState() {
      Asserts.productionAssert(this._browserLock.lockAcquired());
      Asserts.productionStateAssert(this._state == 1);
      this._state = 9;
      this.updatePageLabel();
   }

   private final void changeToLoadingSecuringState() {
      Asserts.productionAssert(this._browserLock.lockAcquired());
      Asserts.productionStateAssert(this._state == 6);
      this._state = 7;
      this.updatePageLabel();
   }

   private final void changeToLoadingSecuringFinishedState() {
      Asserts.productionAssert(this._browserLock.lockAcquired());
      Asserts.productionStateAssert(this._state == 7);
      this._state = 1;
      this.updatePageLabel();
   }

   private final void updatePageLabel() {
      Asserts.productionAssert(this._browserLock.lockAcquired());
      synchronized (this.getAppEventLock()) {
         this._progressManager.changeState(this._state);
         if (this._browserScreen != null) {
            this._browserScreen.browserStateChanged();
         }
      }
   }

   private static final int getDelay(String content) {
      int delay = 0;
      int index = content.indexOf(59);

      try {
         if (index != -1) {
            return Integer.parseInt(content.substring(0, index));
         }

         index = content.indexOf(44);
         if (index == -1) {
            delay = Integer.parseInt(content);
         } else {
            delay = Integer.parseInt(content.substring(0, index));
         }
      } finally {
         return delay;
      }

      return delay;
   }

   private final void cancelCacheRevalidation() {
      ResourceValidationThread thread = (ResourceValidationThread)this._resourceValidator.get();
      if (thread != null) {
         thread.terminateValidating();
      }
   }

   private static final String getURL(String content) {
      String url = null;
      int index = content.indexOf(59);
      int contentLength = content.length();
      if (contentLength > index) {
         index = content.indexOf(61, index);
         if (index++ != -1 && contentLength > index) {
            int offset = 0;
            if (content.charAt(index) == '\'' || content.charAt(index) == '"') {
               offset = 1;
            }

            url = content.substring(index + offset, contentLength - offset).trim();
         }
      }

      return url;
   }

   private static final boolean samePage(String url1, String url2) {
      if (url1 != null && url2 != null) {
         int url1Length = getLengthWithoutFragmentAndTrailingSlash(url1);
         return url1Length == getLengthWithoutFragmentAndTrailingSlash(url2) && url1.regionMatches(false, 0, url2, 0, url1Length);
      } else {
         return url1 == url2;
      }
   }

   private final void scheduleAutoUpdate() {
      synchronized (this) {
         if (this._autoUpdateInvokeLater != -1) {
            this.cancelInvokeLater(this._autoUpdateInvokeLater);
         }

         if (this._lastCoverage >= 2) {
            this._autoUpdateInvokeLater = this.invokeLater(new DoAutoUpdate(this), Bookmarks._debugAutoUpdate ? 20000 : 120000, false);
         }
      }
   }

   private final void showMenu(int menuInstance) {
      if (this._browserLock.tryLock()) {
         this.showMenuSynchronously(false, menuInstance);
      } else {
         this._navigationThread.requestTask(1, new Object(menuInstance));
      }
   }

   private static final int determineCoverage() {
      int coverage = CoverageInfoInternalImpl.determineRadioCoverage();
      if ((coverage & 2) != 0) {
         return 2;
      } else {
         return (coverage & 1) != 0 ? 1 : 0;
      }
   }

   private static final int getLengthWithoutFragmentAndTrailingSlash(String url) {
      int length = url.indexOf(35);
      if (length < 0) {
         length = url.length();
      }

      if (length > 0 && url.charAt(length - 1) == '/') {
         length--;
      }

      return length;
   }

   private final Page getPageFromFetchRequest(FetchRequest param1, RenderThread param2) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.IndexOutOfBoundsException: Index 2 out of bounds for length 1
      //   at java.base/jdk.internal.util.Preconditions.outOfBounds(Preconditions.java:100)
      //   at java.base/jdk.internal.util.Preconditions.outOfBoundsCheckIndex(Preconditions.java:106)
      //   at java.base/jdk.internal.util.Preconditions.checkIndex(Preconditions.java:302)
      //   at java.base/java.util.Objects.checkIndex(Objects.java:385)
      //   at java.base/java.util.ArrayList.remove(ArrayList.java:551)
      //   at org.jetbrains.java.decompiler.modules.decompiler.FinallyProcessor.removeExceptionInstructionsEx(FinallyProcessor.java:1052)
      //   at org.jetbrains.java.decompiler.modules.decompiler.FinallyProcessor.verifyFinallyEx(FinallyProcessor.java:502)
      //   at org.jetbrains.java.decompiler.modules.decompiler.FinallyProcessor.iterateGraph(FinallyProcessor.java:90)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:185)
      //
      // Bytecode:
      // 000: aload 1
      // 001: invokevirtual net/rim/device/apps/internal/browser/stack/FetchRequest.getModelResult ()Lnet/rim/device/apps/internal/browser/stack/ModelResult;
      // 004: astore 3
      // 005: aload 3
      // 006: invokevirtual net/rim/device/apps/internal/browser/stack/ModelResult.getURL ()Ljava/lang/String;
      // 009: astore 4
      // 00b: aload 3
      // 00c: invokevirtual net/rim/device/apps/internal/browser/stack/ModelResult.getNavigation ()I
      // 00f: istore 5
      // 011: aload 3
      // 012: invokevirtual net/rim/device/apps/internal/browser/stack/ModelResult.getPostData ()[B
      // 015: astore 6
      // 017: aload 3
      // 018: invokevirtual net/rim/device/apps/internal/browser/stack/ModelResult.getCacheResult ()Lnet/rim/device/apps/internal/browser/stack/CacheResult;
      // 01b: astore 7
      // 01d: iload 5
      // 01f: bipush 3
      // 021: if_icmpeq 02b
      // 024: iload 5
      // 026: bipush 4
      // 028: if_icmpne 02f
      // 02b: bipush 1
      // 02c: goto 030
      // 02f: bipush 0
      // 030: istore 8
      // 032: aload 1
      // 033: invokevirtual net/rim/device/apps/internal/browser/stack/FetchRequest.isHistoryRequest ()Z
      // 036: istore 9
      // 038: aload 4
      // 03a: ldc_w "queue:"
      // 03d: invokevirtual java/lang/String.startsWith (Ljava/lang/String;)Z
      // 040: ifeq 04c
      // 043: aload 0
      // 044: invokevirtual net/rim/device/apps/internal/browser/core/BrowserImpl.getOfflineQueue ()Lnet/rim/device/apps/internal/browser/stack/OfflineQueue;
      // 047: aload 1
      // 048: invokevirtual net/rim/device/apps/internal/browser/stack/OfflineQueue.getPage (Lnet/rim/device/apps/internal/browser/stack/FetchRequest;)Lnet/rim/device/apps/internal/browser/page/Page;
      // 04b: areturn
      // 04c: aload 4
      // 04e: getstatic net/rim/device/apps/internal/browser/download/DownloadPage.DOWNLOADS_URL Ljava/lang/String;
      // 051: invokevirtual java/lang/String.equals (Ljava/lang/Object;)Z
      // 054: ifeq 075
      // 057: aload 0
      // 058: getfield net/rim/device/apps/internal/browser/core/BrowserImpl._downloadPage Lnet/rim/device/apps/internal/browser/download/DownloadPage;
      // 05b: ifnonnull 070
      // 05e: aload 0
      // 05f: new net/rim/device/apps/internal/browser/download/DownloadPage
      // 062: dup
      // 063: aload 0
      // 064: getfield net/rim/device/apps/internal/browser/core/BrowserImpl._renderingSession Lnet/rim/device/api/browser/field/RenderingSession;
      // 067: invokevirtual net/rim/device/api/browser/field/RenderingSession.getRenderingOptions ()Lnet/rim/device/api/browser/field/RenderingOptions;
      // 06a: invokespecial net/rim/device/apps/internal/browser/download/DownloadPage.<init> (Lnet/rim/device/api/browser/field/RenderingOptions;)V
      // 06d: putfield net/rim/device/apps/internal/browser/core/BrowserImpl._downloadPage Lnet/rim/device/apps/internal/browser/download/DownloadPage;
      // 070: aload 0
      // 071: getfield net/rim/device/apps/internal/browser/core/BrowserImpl._downloadPage Lnet/rim/device/apps/internal/browser/download/DownloadPage;
      // 074: areturn
      // 075: aload 4
      // 077: getstatic net/rim/device/apps/internal/browser/page/StartupPage.STARTUP_URL Ljava/lang/String;
      // 07a: invokevirtual java/lang/String.equals (Ljava/lang/Object;)Z
      // 07d: ifeq 093
      // 080: aload 3
      // 081: invokevirtual net/rim/device/apps/internal/browser/stack/ModelResult.clearHistoryFlags ()V
      // 084: new net/rim/device/apps/internal/browser/page/StartupPage
      // 087: dup
      // 088: aload 0
      // 089: getfield net/rim/device/apps/internal/browser/core/BrowserImpl._renderingSession Lnet/rim/device/api/browser/field/RenderingSession;
      // 08c: invokevirtual net/rim/device/api/browser/field/RenderingSession.getRenderingOptions ()Lnet/rim/device/api/browser/field/RenderingOptions;
      // 08f: invokespecial net/rim/device/apps/internal/browser/page/StartupPage.<init> (Lnet/rim/device/api/browser/field/RenderingOptions;)V
      // 092: areturn
      // 093: aload 7
      // 095: ifnull 09b
      // 098: goto 242
      // 09b: iload 8
      // 09d: ifeq 0a3
      // 0a0: goto 242
      // 0a3: bipush 0
      // 0a4: istore 10
      // 0a6: new java/lang/Object
      // 0a9: dup
      // 0aa: aload 1
      // 0ab: invokevirtual net/rim/device/apps/internal/browser/stack/FetchRequest.getModelResult ()Lnet/rim/device/apps/internal/browser/stack/ModelResult;
      // 0ae: invokevirtual net/rim/device/apps/internal/browser/stack/ModelResult.getURL ()Ljava/lang/String;
      // 0b1: invokespecial net/rim/device/apps/api/utility/general/URI.<init> (Ljava/lang/String;)V
      // 0b4: astore 11
      // 0b6: aload 11
      // 0b8: invokevirtual net/rim/device/apps/api/utility/general/URI.getAuthority ()Ljava/lang/String;
      // 0bb: astore 12
      // 0bd: aload 12
      // 0bf: ifnull 0ce
      // 0c2: aload 12
      // 0c4: invokestatic net/rim/device/api/io/http/HttpFilterRegistry.isLocalFilter (Ljava/lang/String;)Z
      // 0c7: istore 10
      // 0c9: goto 0ce
      // 0cc: astore 11
      // 0ce: iload 10
      // 0d0: ifeq 0d6
      // 0d3: goto 1fd
      // 0d6: aload 0
      // 0d7: getfield net/rim/device/apps/internal/browser/core/BrowserImpl._rawDataCache Lnet/rim/device/apps/internal/browser/stack/RawDataCache;
      // 0da: aload 4
      // 0dc: iload 5
      // 0de: aload 0
      // 0df: getfield net/rim/device/apps/internal/browser/core/BrowserImpl._currentUrlEncoding Ljava/lang/Object;
      // 0e2: invokestatic net/rim/device/api/system/PersistentContent.decodeString (Ljava/lang/Object;)Ljava/lang/String;
      // 0e5: aload 6
      // 0e7: aload 3
      // 0e8: invokevirtual net/rim/device/apps/internal/browser/stack/ModelResult.getRequestHeaders ()Lnet/rim/device/api/io/http/HttpHeaders;
      // 0eb: iload 9
      // 0ed: invokevirtual net/rim/device/apps/internal/browser/stack/RawDataCache.get (Ljava/lang/String;ILjava/lang/String;[BLnet/rim/device/api/io/http/HttpHeaders;Z)Lnet/rim/device/apps/internal/browser/stack/CacheResult;
      // 0f0: astore 7
      // 0f2: aload 7
      // 0f4: ifnull 0fa
      // 0f7: goto 1e5
      // 0fa: aload 0
      // 0fb: getfield net/rim/device/apps/internal/browser/core/BrowserImpl._rawDataCache Lnet/rim/device/apps/internal/browser/stack/RawDataCache;
      // 0fe: aload 4
      // 100: invokevirtual net/rim/device/apps/internal/browser/stack/RawDataCache.get (Ljava/lang/String;)Lnet/rim/device/apps/internal/browser/stack/CacheResult;
      // 103: astore 11
      // 105: aload 11
      // 107: ifnonnull 10d
      // 10a: goto 1e5
      // 10d: aload 0
      // 10e: getfield net/rim/device/apps/internal/browser/core/BrowserImpl._rawDataCache Lnet/rim/device/apps/internal/browser/stack/RawDataCache;
      // 111: invokevirtual net/rim/device/apps/internal/browser/stack/RawDataCache.getStackManager ()Lnet/rim/device/apps/internal/browser/stack/StackManager;
      // 114: invokevirtual net/rim/device/apps/internal/browser/stack/StackManager.ableToMakeRequest ()Z
      // 117: ifeq 11d
      // 11a: goto 1e5
      // 11d: bipush 0
      // 11e: istore 12
      // 120: aload 1
      // 121: invokevirtual net/rim/device/apps/internal/browser/stack/FetchRequest.getBrowserConfigRecord ()Lnet/rim/device/apps/internal/browser/options/BrowserConfigRecord;
      // 124: astore 13
      // 126: aload 13
      // 128: ifnonnull 13c
      // 12b: invokestatic net/rim/device/apps/internal/browser/core/BrowserSession.getCurrentSession ()Lnet/rim/device/apps/internal/browser/core/BrowserSession;
      // 12e: astore 14
      // 130: aload 14
      // 132: ifnull 13c
      // 135: aload 14
      // 137: invokevirtual net/rim/device/apps/internal/browser/core/BrowserSession.getConfig ()Lnet/rim/device/apps/internal/browser/options/BrowserConfigRecord;
      // 13a: astore 13
      // 13c: aload 13
      // 13e: ifnonnull 144
      // 141: goto 1c5
      // 144: aload 13
      // 146: bipush 3
      // 148: invokevirtual net/rim/device/apps/internal/browser/options/BrowserConfigRecord.getPropertyAsString (I)Ljava/lang/String;
      // 14b: astore 14
      // 14d: aload 14
      // 14f: getstatic net/rim/device/apps/internal/browser/options/BrowserConfigRecord.IPPP_SERVICE_CID Ljava/lang/String;
      // 152: ldc_w 1701707776
      // 155: invokestatic net/rim/device/api/util/StringUtilities.strEqualIgnoreCase (Ljava/lang/String;Ljava/lang/String;I)Z
      // 158: ifeq 16f
      // 15b: invokestatic net/rim/device/api/servicebook/ServiceRouting.getInstance ()Lnet/rim/device/api/servicebook/ServiceRouting;
      // 15e: aload 13
      // 160: bipush 4
      // 162: invokevirtual net/rim/device/apps/internal/browser/options/BrowserConfigRecord.getPropertyAsString (I)Ljava/lang/String;
      // 165: bipush -1
      // 167: invokevirtual net/rim/device/api/servicebook/ServiceRouting.isServiceRoutable (Ljava/lang/String;I)Z
      // 16a: istore 12
      // 16c: goto 1c5
      // 16f: invokestatic net/rim/device/api/system/WLAN.isSupported ()Z
      // 172: ifeq 1c5
      // 175: aload 14
      // 177: getstatic net/rim/device/internal/browser/wap/WPTCPServiceRecord.SERVICE_CID Ljava/lang/String;
      // 17a: ldc_w 1701707776
      // 17d: invokestatic net/rim/device/api/util/StringUtilities.strEqualIgnoreCase (Ljava/lang/String;Ljava/lang/String;I)Z
      // 180: ifeq 1c5
      // 183: invokestatic net/rim/device/api/servicebook/ServiceBook.getSB ()Lnet/rim/device/api/servicebook/ServiceBook;
      // 186: aload 13
      // 188: bipush 4
      // 18a: invokevirtual net/rim/device/apps/internal/browser/options/BrowserConfigRecord.getPropertyAsString (I)Ljava/lang/String;
      // 18d: aload 14
      // 18f: invokevirtual net/rim/device/api/servicebook/ServiceBook.getRecordByUidAndCid (Ljava/lang/String;Ljava/lang/String;)Lnet/rim/device/api/servicebook/ServiceRecord;
      // 192: astore 15
      // 194: aload 15
      // 196: ifnull 1c5
      // 199: aload 15
      // 19b: invokestatic net/rim/device/internal/browser/wap/WPTCPServiceRecord.getRecord (Lnet/rim/device/api/servicebook/ServiceRecord;)Lnet/rim/device/internal/browser/wap/WPTCPServiceRecord;
      // 19e: astore 16
      // 1a0: aload 16
      // 1a2: ifnull 1c5
      // 1a5: aload 16
      // 1a7: bipush 19
      // 1a9: invokevirtual net/rim/device/internal/browser/wap/WPTCPServiceRecord.getPropertyAsString (I)Ljava/lang/String;
      // 1ac: ldc_w "wifi"
      // 1af: ldc_w 1701707776
      // 1b2: invokestatic net/rim/device/api/util/StringUtilities.strEqualIgnoreCase (Ljava/lang/String;Ljava/lang/String;I)Z
      // 1b5: ifeq 1c5
      // 1b8: invokestatic net/rim/device/api/system/WLAN.isAssociated ()Ljava/lang/String;
      // 1bb: ifnull 1c2
      // 1be: bipush 1
      // 1bf: goto 1c3
      // 1c2: bipush 0
      // 1c3: istore 12
      // 1c5: iload 12
      // 1c7: ifne 1e5
      // 1ca: sipush 615
      // 1cd: invokestatic net/rim/device/apps/internal/browser/resources/BrowserResources.getString (I)Ljava/lang/String;
      // 1d0: bipush 0
      // 1d1: invokestatic net/rim/device/apps/api/ui/CommonResources.getYesNoArray (I)[Ljava/lang/String;
      // 1d4: bipush 0
      // 1d5: bipush 1
      // 1d6: invokestatic net/rim/device/api/system/Bitmap.getPredefinedBitmap (I)Lnet/rim/device/api/system/Bitmap;
      // 1d9: bipush 50
      // 1db: invokestatic net/rim/device/internal/ui/component/BackgroundDialog.getChoice (Ljava/lang/String;[Ljava/lang/Object;ILnet/rim/device/api/system/Bitmap;I)I
      // 1de: ifne 1e5
      // 1e1: aload 11
      // 1e3: astore 7
      // 1e5: aload 7
      // 1e7: ifnull 1fd
      // 1ea: aload 7
      // 1ec: invokevirtual net/rim/device/apps/internal/browser/stack/CacheResult.getSecured ()Z
      // 1ef: ifeq 1fd
      // 1f2: aload 1
      // 1f3: new net/rim/device/apps/internal/browser/stack/CachedSecurityInfo
      // 1f6: dup
      // 1f7: invokespecial net/rim/device/apps/internal/browser/stack/CachedSecurityInfo.<init> ()V
      // 1fa: invokevirtual net/rim/device/apps/internal/browser/stack/FetchRequest.setSecurityInfo (Ljavax/microedition/io/SecurityInfo;)V
      // 1fd: aload 7
      // 1ff: ifnonnull 23c
      // 202: aload 6
      // 204: ifnull 23c
      // 207: iload 9
      // 209: ifeq 23c
      // 20c: sipush 745
      // 20f: invokestatic net/rim/device/apps/internal/browser/resources/BrowserResources.getString (I)Ljava/lang/String;
      // 212: bipush 0
      // 213: invokestatic net/rim/device/apps/api/ui/CommonResources.getYesNoArray (I)[Ljava/lang/String;
      // 216: bipush 0
      // 217: bipush 1
      // 218: invokestatic net/rim/device/api/system/Bitmap.getPredefinedBitmap (I)Lnet/rim/device/api/system/Bitmap;
      // 21b: bipush 50
      // 21d: invokestatic net/rim/device/internal/ui/component/BackgroundDialog.getChoice (Ljava/lang/String;[Ljava/lang/Object;ILnet/rim/device/api/system/Bitmap;I)I
      // 220: ifeq 23c
      // 223: aload 0
      // 224: getfield net/rim/device/apps/internal/browser/core/BrowserImpl._currentSession Lnet/rim/device/apps/internal/browser/core/BrowserSession;
      // 227: ifnull 234
      // 22a: aload 0
      // 22b: getfield net/rim/device/apps/internal/browser/core/BrowserImpl._currentSession Lnet/rim/device/apps/internal/browser/core/BrowserSession;
      // 22e: invokevirtual net/rim/device/apps/internal/browser/core/BrowserSession.getHistory ()Lnet/rim/device/apps/internal/browser/history/History;
      // 231: invokevirtual net/rim/device/apps/internal/browser/history/History.resetToLastViewedNode ()V
      // 234: new java/lang/Object
      // 237: dup
      // 238: invokespecial net/rim/device/apps/internal/browser/common/UserAbortException.<init> ()V
      // 23b: athrow
      // 23c: aload 3
      // 23d: aload 7
      // 23f: invokevirtual net/rim/device/apps/internal/browser/stack/ModelResult.setCacheResult (Lnet/rim/device/apps/internal/browser/stack/CacheResult;)V
      // 242: aconst_null
      // 243: astore 10
      // 245: aload 7
      // 247: ifnull 24d
      // 24a: goto 326
      // 24d: aload 0
      // 24e: getfield net/rim/device/apps/internal/browser/core/BrowserImpl._browserLock Lnet/rim/device/apps/internal/browser/util/Mutex;
      // 251: invokevirtual net/rim/device/apps/internal/browser/util/Mutex.waitForLock ()V
      // 254: aload 0
      // 255: getfield net/rim/device/apps/internal/browser/core/BrowserImpl._currentRenderThread Lnet/rim/device/apps/internal/browser/page/RenderThread;
      // 258: aload 2
      // 259: if_acmpne 260
      // 25c: bipush 1
      // 25d: goto 261
      // 260: bipush 0
      // 261: invokestatic net/rim/device/apps/internal/browser/util/Asserts.productionUserAbortAssert (Z)V
      // 264: aload 0
      // 265: aload 1
      // 266: invokespecial net/rim/device/apps/internal/browser/core/BrowserImpl.changeToLoadingFetchingState (Lnet/rim/device/apps/internal/browser/stack/FetchRequest;)V
      // 269: aload 0
      // 26a: getfield net/rim/device/apps/internal/browser/core/BrowserImpl._browserLock Lnet/rim/device/apps/internal/browser/util/Mutex;
      // 26d: invokevirtual net/rim/device/apps/internal/browser/util/Mutex.releaseLock ()V
      // 270: goto 28b
      // 273: astore 11
      // 275: aload 0
      // 276: getfield net/rim/device/apps/internal/browser/core/BrowserImpl._browserLock Lnet/rim/device/apps/internal/browser/util/Mutex;
      // 279: invokevirtual net/rim/device/apps/internal/browser/util/Mutex.releaseLock ()V
      // 27c: goto 28b
      // 27f: astore 17
      // 281: aload 0
      // 282: getfield net/rim/device/apps/internal/browser/core/BrowserImpl._browserLock Lnet/rim/device/apps/internal/browser/util/Mutex;
      // 285: invokevirtual net/rim/device/apps/internal/browser/util/Mutex.releaseLock ()V
      // 288: aload 17
      // 28a: athrow
      // 28b: aload 0
      // 28c: getfield net/rim/device/apps/internal/browser/core/BrowserImpl._lastUrlLoaded Ljava/lang/StringBuffer;
      // 28f: bipush 0
      // 290: invokevirtual java/lang/StringBuffer.setLength (I)V
      // 293: aload 1
      // 294: invokevirtual net/rim/device/apps/internal/browser/stack/FetchRequest.getBrowserConfigRecord ()Lnet/rim/device/apps/internal/browser/options/BrowserConfigRecord;
      // 297: astore 11
      // 299: aload 11
      // 29b: ifnonnull 2af
      // 29e: invokestatic net/rim/device/apps/internal/browser/core/BrowserSession.getCurrentSession ()Lnet/rim/device/apps/internal/browser/core/BrowserSession;
      // 2a1: astore 12
      // 2a3: aload 12
      // 2a5: ifnull 2af
      // 2a8: aload 12
      // 2aa: invokevirtual net/rim/device/apps/internal/browser/core/BrowserSession.getConfig ()Lnet/rim/device/apps/internal/browser/options/BrowserConfigRecord;
      // 2ad: astore 11
      // 2af: aload 11
      // 2b1: ifnull 2f2
      // 2b4: ldc2_w 1907089860548946979
      // 2b7: ldc_w 1114859108
      // 2ba: bipush 5
      // 2bc: invokestatic net/rim/device/api/system/EventLogger.logEvent (JII)Z
      // 2bf: pop
      // 2c0: aload 0
      // 2c1: getfield net/rim/device/apps/internal/browser/core/BrowserImpl._lastUrlLoaded Ljava/lang/StringBuffer;
      // 2c4: aload 11
      // 2c6: bipush 3
      // 2c8: invokevirtual net/rim/device/apps/internal/browser/options/BrowserConfigRecord.getPropertyAsString (I)Ljava/lang/String;
      // 2cb: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 2ce: pop
      // 2cf: aload 0
      // 2d0: getfield net/rim/device/apps/internal/browser/core/BrowserImpl._lastUrlLoaded Ljava/lang/StringBuffer;
      // 2d3: bipush 45
      // 2d5: invokevirtual java/lang/StringBuffer.append (C)Ljava/lang/StringBuffer;
      // 2d8: pop
      // 2d9: aload 0
      // 2da: getfield net/rim/device/apps/internal/browser/core/BrowserImpl._lastUrlLoaded Ljava/lang/StringBuffer;
      // 2dd: aload 11
      // 2df: bipush 4
      // 2e1: invokevirtual net/rim/device/apps/internal/browser/options/BrowserConfigRecord.getPropertyAsString (I)Ljava/lang/String;
      // 2e4: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 2e7: pop
      // 2e8: aload 0
      // 2e9: getfield net/rim/device/apps/internal/browser/core/BrowserImpl._lastUrlLoaded Ljava/lang/StringBuffer;
      // 2ec: bipush 58
      // 2ee: invokevirtual java/lang/StringBuffer.append (C)Ljava/lang/StringBuffer;
      // 2f1: pop
      // 2f2: aload 0
      // 2f3: getfield net/rim/device/apps/internal/browser/core/BrowserImpl._lastUrlLoaded Ljava/lang/StringBuffer;
      // 2f6: aload 4
      // 2f8: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 2fb: pop
      // 2fc: invokestatic net/rim/device/api/system/PersistentContent.isEncryptionEnabled ()Z
      // 2ff: ifne 314
      // 302: ldc2_w 1907089860548946979
      // 305: aload 0
      // 306: getfield net/rim/device/apps/internal/browser/core/BrowserImpl._lastUrlLoaded Ljava/lang/StringBuffer;
      // 309: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 30c: invokevirtual java/lang/String.getBytes ()[B
      // 30f: bipush 0
      // 310: invokestatic net/rim/device/api/system/EventLogger.logEvent (J[BI)Z
      // 313: pop
      // 314: aload 0
      // 315: aload 1
      // 316: bipush 1
      // 317: bipush 1
      // 318: invokevirtual net/rim/device/apps/internal/browser/core/BrowserImpl.processRequest (Lnet/rim/device/apps/internal/browser/stack/FetchRequest;ZZ)Ljavax/microedition/io/InputConnection;
      // 31b: astore 10
      // 31d: aload 3
      // 31e: invokevirtual net/rim/device/apps/internal/browser/stack/ModelResult.getCacheResult ()Lnet/rim/device/apps/internal/browser/stack/CacheResult;
      // 321: astore 7
      // 323: goto 375
      // 326: new net/rim/device/apps/internal/browser/stack/CachedHttpConnection
      // 329: dup
      // 32a: aload 4
      // 32c: aload 7
      // 32e: aload 3
      // 32f: invokevirtual net/rim/device/apps/internal/browser/stack/ModelResult.getRequestHeaders ()Lnet/rim/device/api/io/http/HttpHeaders;
      // 332: invokespecial net/rim/device/apps/internal/browser/stack/CachedHttpConnection.<init> (Ljava/lang/String;Lnet/rim/device/apps/internal/browser/stack/CacheResult;Lnet/rim/device/api/io/http/HttpHeaders;)V
      // 335: astore 10
      // 337: aload 0
      // 338: getfield net/rim/device/apps/internal/browser/core/BrowserImpl._browserLock Lnet/rim/device/apps/internal/browser/util/Mutex;
      // 33b: invokevirtual net/rim/device/apps/internal/browser/util/Mutex.waitForLock ()V
      // 33e: aload 0
      // 33f: getfield net/rim/device/apps/internal/browser/core/BrowserImpl._currentRenderThread Lnet/rim/device/apps/internal/browser/page/RenderThread;
      // 342: aload 2
      // 343: if_acmpne 34a
      // 346: bipush 1
      // 347: goto 34b
      // 34a: bipush 0
      // 34b: invokestatic net/rim/device/apps/internal/browser/util/Asserts.productionUserAbortAssert (Z)V
      // 34e: aload 0
      // 34f: aload 1
      // 350: putfield net/rim/device/apps/internal/browser/core/BrowserImpl._currentFetchRequest Lnet/rim/device/apps/internal/browser/stack/FetchRequest;
      // 353: aload 0
      // 354: getfield net/rim/device/apps/internal/browser/core/BrowserImpl._browserLock Lnet/rim/device/apps/internal/browser/util/Mutex;
      // 357: invokevirtual net/rim/device/apps/internal/browser/util/Mutex.releaseLock ()V
      // 35a: goto 375
      // 35d: astore 11
      // 35f: aload 0
      // 360: getfield net/rim/device/apps/internal/browser/core/BrowserImpl._browserLock Lnet/rim/device/apps/internal/browser/util/Mutex;
      // 363: invokevirtual net/rim/device/apps/internal/browser/util/Mutex.releaseLock ()V
      // 366: goto 375
      // 369: astore 18
      // 36b: aload 0
      // 36c: getfield net/rim/device/apps/internal/browser/core/BrowserImpl._browserLock Lnet/rim/device/apps/internal/browser/util/Mutex;
      // 36f: invokevirtual net/rim/device/apps/internal/browser/util/Mutex.releaseLock ()V
      // 372: aload 18
      // 374: athrow
      // 375: aload 2
      // 376: aload 10
      // 378: invokevirtual net/rim/device/apps/internal/browser/page/RenderThread.setConnection (Ljavax/microedition/io/Connection;)V
      // 37b: aconst_null
      // 37c: astore 11
      // 37e: aload 7
      // 380: ifnonnull 390
      // 383: aload 1
      // 384: sipush 234
      // 387: invokestatic net/rim/device/apps/internal/browser/resources/BrowserResources.getString (I)Ljava/lang/String;
      // 38a: invokevirtual net/rim/device/apps/internal/browser/stack/FetchRequest.setError (Ljava/lang/Object;)V
      // 38d: goto 3a4
      // 390: aload 3
      // 391: invokevirtual net/rim/device/apps/internal/browser/stack/ModelResult.getCacheResult ()Lnet/rim/device/apps/internal/browser/stack/CacheResult;
      // 394: invokevirtual net/rim/device/apps/internal/browser/stack/CacheResult.getExceptionString ()Ljava/lang/String;
      // 397: astore 12
      // 399: aload 12
      // 39b: ifnull 3a4
      // 39e: aload 1
      // 39f: aload 12
      // 3a1: invokevirtual net/rim/device/apps/internal/browser/stack/FetchRequest.setError (Ljava/lang/Object;)V
      // 3a4: aload 1
      // 3a5: invokevirtual net/rim/device/apps/internal/browser/stack/FetchRequest.getError ()Ljava/lang/Object;
      // 3a8: ifnull 3ae
      // 3ab: goto 677
      // 3ae: aload 0
      // 3af: getfield net/rim/device/apps/internal/browser/core/BrowserImpl._currentRenderThread Lnet/rim/device/apps/internal/browser/page/RenderThread;
      // 3b2: aload 2
      // 3b3: if_acmpne 3ba
      // 3b6: bipush 1
      // 3b7: goto 3bb
      // 3ba: bipush 0
      // 3bb: invokestatic net/rim/device/apps/internal/browser/util/Asserts.productionUserAbortAssert (Z)V
      // 3be: aload 0
      // 3bf: getfield net/rim/device/apps/internal/browser/core/BrowserImpl._browserLock Lnet/rim/device/apps/internal/browser/util/Mutex;
      // 3c2: invokevirtual net/rim/device/apps/internal/browser/util/Mutex.waitForLock ()V
      // 3c5: aload 0
      // 3c6: getfield net/rim/device/apps/internal/browser/core/BrowserImpl._currentRenderThread Lnet/rim/device/apps/internal/browser/page/RenderThread;
      // 3c9: aload 2
      // 3ca: if_acmpne 3d1
      // 3cd: bipush 1
      // 3ce: goto 3d2
      // 3d1: bipush 0
      // 3d2: invokestatic net/rim/device/apps/internal/browser/util/Asserts.productionUserAbortAssert (Z)V
      // 3d5: aload 0
      // 3d6: invokespecial net/rim/device/apps/internal/browser/core/BrowserImpl.changeToLoadingRenderingState ()V
      // 3d9: aload 0
      // 3da: getfield net/rim/device/apps/internal/browser/core/BrowserImpl._browserLock Lnet/rim/device/apps/internal/browser/util/Mutex;
      // 3dd: invokevirtual net/rim/device/apps/internal/browser/util/Mutex.releaseLock ()V
      // 3e0: goto 3fb
      // 3e3: astore 12
      // 3e5: aload 0
      // 3e6: getfield net/rim/device/apps/internal/browser/core/BrowserImpl._browserLock Lnet/rim/device/apps/internal/browser/util/Mutex;
      // 3e9: invokevirtual net/rim/device/apps/internal/browser/util/Mutex.releaseLock ()V
      // 3ec: goto 3fb
      // 3ef: astore 19
      // 3f1: aload 0
      // 3f2: getfield net/rim/device/apps/internal/browser/core/BrowserImpl._browserLock Lnet/rim/device/apps/internal/browser/util/Mutex;
      // 3f5: invokevirtual net/rim/device/apps/internal/browser/util/Mutex.releaseLock ()V
      // 3f8: aload 19
      // 3fa: athrow
      // 3fb: aconst_null
      // 3fc: astore 12
      // 3fe: bipush 0
      // 3ff: istore 13
      // 401: aload 10
      // 403: invokestatic net/rim/device/apps/internal/browser/util/RendererControl.getContentType (Ljavax/microedition/io/InputConnection;)Ljava/lang/String;
      // 406: astore 14
      // 408: bipush 0
      // 409: i2l
      // 40a: lstore 15
      // 40c: aload 10
      // 40e: invokestatic net/rim/device/apps/internal/browser/util/RendererControl.getContentLength (Ljavax/microedition/io/InputConnection;)J
      // 411: lstore 15
      // 413: goto 418
      // 416: astore 17
      // 418: aload 10
      // 41a: dup
      // 41b: instanceof java/lang/Object
      // 41e: ifne 425
      // 421: pop
      // 422: goto 527
      // 425: checkcast java/lang/Object
      // 428: astore 17
      // 42a: sipush 200
      // 42d: istore 18
      // 42f: aload 17
      // 431: invokeinterface javax/microedition/io/HttpConnection.getResponseCode ()I 1
      // 436: istore 18
      // 438: goto 43d
      // 43b: astore 19
      // 43d: iload 18
      // 43f: sipush 200
      // 442: if_icmpge 448
      // 445: goto 527
      // 448: iload 18
      // 44a: sipush 300
      // 44d: if_icmplt 453
      // 450: goto 527
      // 453: aload 14
      // 455: ifnonnull 45b
      // 458: goto 524
      // 45b: aload 14
      // 45d: ldc_w "audio/"
      // 460: ldc_w 1701707776
      // 463: invokestatic net/rim/device/api/util/StringUtilities.startsWithIgnoreCase (Ljava/lang/String;Ljava/lang/String;I)Z
      // 466: ifne 47a
      // 469: aload 14
      // 46b: ldc_w "video/"
      // 46e: ldc_w 1701707776
      // 471: invokestatic net/rim/device/api/util/StringUtilities.startsWithIgnoreCase (Ljava/lang/String;Ljava/lang/String;I)Z
      // 474: ifne 47a
      // 477: goto 524
      // 47a: aload 0
      // 47b: getfield net/rim/device/apps/internal/browser/core/BrowserImpl._renderingSession Lnet/rim/device/api/browser/field/RenderingSession;
      // 47e: invokevirtual net/rim/device/api/browser/field/RenderingSession.getRenderingOptions ()Lnet/rim/device/api/browser/field/RenderingOptions;
      // 481: astore 19
      // 483: aload 19
      // 485: ifnull 498
      // 488: aload 19
      // 48a: ldc2_w 4550690918222697397
      // 48d: bipush 27
      // 48f: bipush 1
      // 490: invokevirtual net/rim/device/api/browser/field/RenderingOptions.getPropertyWithBooleanValue (JIZ)Z
      // 493: istore 13
      // 495: goto 49b
      // 498: bipush 1
      // 499: istore 13
      // 49b: aload 1
      // 49c: invokevirtual net/rim/device/apps/internal/browser/stack/FetchRequest.getEvent ()Lnet/rim/device/api/browser/field/Event;
      // 49f: instanceof java/lang/Object
      // 4a2: ifeq 4a8
      // 4a5: bipush 0
      // 4a6: istore 13
      // 4a8: iload 13
      // 4aa: ifeq 527
      // 4ad: new java/lang/Object
      // 4b0: dup
      // 4b1: sipush 881
      // 4b4: invokestatic net/rim/device/apps/internal/browser/resources/BrowserResources.getString (I)Ljava/lang/String;
      // 4b7: sipush 883
      // 4ba: invokestatic net/rim/device/apps/internal/browser/resources/BrowserResources.getStringArray (I)[Ljava/lang/String;
      // 4bd: aconst_null
      // 4be: bipush 0
      // 4bf: aconst_null
      // 4c0: invokespecial net/rim/device/api/ui/component/Dialog.<init> (Ljava/lang/String;[Ljava/lang/Object;[IILnet/rim/device/api/system/Bitmap;)V
      // 4c3: astore 20
      // 4c5: aload 20
      // 4c7: ldc_w "dialog_question"
      // 4ca: invokestatic net/rim/device/api/ui/theme/ThemeManager.getThemeAwareImage (Ljava/lang/String;)Lnet/rim/device/internal/ui/Image;
      // 4cd: invokevirtual net/rim/device/api/ui/component/Dialog.setIcon (Lnet/rim/device/internal/ui/Image;)V
      // 4d0: aload 0
      // 4d1: new java/lang/Object
      // 4d4: dup
      // 4d5: aload 20
      // 4d7: invokespecial net/rim/device/apps/api/ui/RunnableDialog.<init> (Lnet/rim/device/api/ui/component/Dialog;)V
      // 4da: invokevirtual net/rim/device/api/system/Application.invokeAndWait (Ljava/lang/Runnable;)V
      // 4dd: aload 20
      // 4df: invokevirtual net/rim/device/api/ui/component/Dialog.getSelectedValue ()I
      // 4e2: tableswitch 34 -2 2 69 58 69 34 58
      // 504: aload 4
      // 506: aload 14
      // 508: lload 15
      // 50a: invokestatic net/rim/device/apps/internal/browser/ui/SaveFileDialog.promptToSave (Ljava/lang/String;Ljava/lang/String;J)Lnet/rim/device/apps/internal/browser/ui/SaveFileDialog;
      // 50d: astore 12
      // 50f: aload 12
      // 511: ifnonnull 527
      // 514: new java/lang/Object
      // 517: dup
      // 518: invokespecial net/rim/device/apps/internal/browser/common/UserAbortException.<init> ()V
      // 51b: athrow
      // 51c: new java/lang/Object
      // 51f: dup
      // 520: invokespecial net/rim/device/apps/internal/browser/common/UserAbortException.<init> ()V
      // 523: athrow
      // 524: bipush 1
      // 525: istore 13
      // 527: aload 12
      // 529: ifnonnull 536
      // 52c: aload 0
      // 52d: aload 10
      // 52f: aload 1
      // 530: aload 2
      // 531: invokespecial net/rim/device/apps/internal/browser/core/BrowserImpl.processResponse (Ljavax/microedition/io/InputConnection;Lnet/rim/device/apps/internal/browser/stack/FetchRequest;Lnet/rim/device/apps/internal/browser/page/RenderThread;)Lnet/rim/device/apps/internal/browser/page/Page;
      // 534: astore 11
      // 536: iload 13
      // 538: ifne 53e
      // 53b: goto 661
      // 53e: aload 11
      // 540: ifnull 546
      // 543: goto 661
      // 546: aload 1
      // 547: invokevirtual net/rim/device/apps/internal/browser/stack/FetchRequest.getError ()Ljava/lang/Object;
      // 54a: astore 17
      // 54c: aload 12
      // 54e: ifnonnull 5ad
      // 551: aload 17
      // 553: dup
      // 554: instanceof java/lang/Object
      // 557: ifne 55e
      // 55a: pop
      // 55b: goto 5ad
      // 55e: checkcast java/lang/Object
      // 561: invokevirtual net/rim/device/apps/internal/browser/common/ResourcedRenderingException.getResourceId ()I
      // 564: sipush 265
      // 567: if_icmpne 5ad
      // 56a: new java/lang/Object
      // 56d: dup
      // 56e: bipush 3
      // 570: sipush 882
      // 573: invokestatic net/rim/device/apps/internal/browser/resources/BrowserResources.getString (I)Ljava/lang/String;
      // 576: bipush 4
      // 578: aconst_null
      // 579: bipush 0
      // 57a: i2l
      // 57b: invokespecial net/rim/device/api/ui/component/Dialog.<init> (ILjava/lang/String;ILnet/rim/device/api/system/Bitmap;J)V
      // 57e: astore 18
      // 580: aload 18
      // 582: ldc_w "dialog_question"
      // 585: invokestatic net/rim/device/api/ui/theme/ThemeManager.getThemeAwareImage (Ljava/lang/String;)Lnet/rim/device/internal/ui/Image;
      // 588: invokevirtual net/rim/device/api/ui/component/Dialog.setIcon (Lnet/rim/device/internal/ui/Image;)V
      // 58b: aload 0
      // 58c: new java/lang/Object
      // 58f: dup
      // 590: aload 18
      // 592: invokespecial net/rim/device/apps/api/ui/RunnableDialog.<init> (Lnet/rim/device/api/ui/component/Dialog;)V
      // 595: invokevirtual net/rim/device/api/system/Application.invokeAndWait (Ljava/lang/Runnable;)V
      // 598: aload 18
      // 59a: invokevirtual net/rim/device/api/ui/component/Dialog.getSelectedValue ()I
      // 59d: bipush 4
      // 59f: if_icmpne 5ad
      // 5a2: aload 4
      // 5a4: aload 14
      // 5a6: lload 15
      // 5a8: invokestatic net/rim/device/apps/internal/browser/ui/SaveFileDialog.promptToSave (Ljava/lang/String;Ljava/lang/String;J)Lnet/rim/device/apps/internal/browser/ui/SaveFileDialog;
      // 5ab: astore 12
      // 5ad: aload 12
      // 5af: ifnonnull 5b5
      // 5b2: goto 661
      // 5b5: aload 0
      // 5b6: getfield net/rim/device/apps/internal/browser/core/BrowserImpl._downloadPage Lnet/rim/device/apps/internal/browser/download/DownloadPage;
      // 5b9: ifnonnull 5ce
      // 5bc: aload 0
      // 5bd: new net/rim/device/apps/internal/browser/download/DownloadPage
      // 5c0: dup
      // 5c1: aload 0
      // 5c2: getfield net/rim/device/apps/internal/browser/core/BrowserImpl._renderingSession Lnet/rim/device/api/browser/field/RenderingSession;
      // 5c5: invokevirtual net/rim/device/api/browser/field/RenderingSession.getRenderingOptions ()Lnet/rim/device/api/browser/field/RenderingOptions;
      // 5c8: invokespecial net/rim/device/apps/internal/browser/download/DownloadPage.<init> (Lnet/rim/device/api/browser/field/RenderingOptions;)V
      // 5cb: putfield net/rim/device/apps/internal/browser/core/BrowserImpl._downloadPage Lnet/rim/device/apps/internal/browser/download/DownloadPage;
      // 5ce: aload 0
      // 5cf: getfield net/rim/device/apps/internal/browser/core/BrowserImpl._downloadPage Lnet/rim/device/apps/internal/browser/download/DownloadPage;
      // 5d2: astore 11
      // 5d4: aload 0
      // 5d5: getfield net/rim/device/apps/internal/browser/core/BrowserImpl._rawDataCache Lnet/rim/device/apps/internal/browser/stack/RawDataCache;
      // 5d8: aload 4
      // 5da: bipush 1
      // 5db: invokevirtual net/rim/device/apps/internal/browser/stack/RawDataCache.remove (Ljava/lang/String;Z)V
      // 5de: aload 1
      // 5df: aconst_null
      // 5e0: invokevirtual net/rim/device/apps/internal/browser/stack/FetchRequest.setError (Ljava/lang/Object;)V
      // 5e3: aload 2
      // 5e4: invokevirtual net/rim/device/apps/internal/browser/page/RenderThread.getInput ()Ljava/io/InputStream;
      // 5e7: astore 18
      // 5e9: aload 2
      // 5ea: aconst_null
      // 5eb: invokevirtual net/rim/device/apps/internal/browser/page/RenderThread.setConnection (Ljavax/microedition/io/Connection;)V
      // 5ee: aload 2
      // 5ef: aconst_null
      // 5f0: invokevirtual net/rim/device/apps/internal/browser/page/RenderThread.setInput (Ljava/io/InputStream;)V
      // 5f3: aload 1
      // 5f4: invokevirtual net/rim/device/apps/internal/browser/stack/FetchRequest.getStartTime ()J
      // 5f7: lstore 19
      // 5f9: aload 1
      // 5fa: invokevirtual net/rim/device/apps/internal/browser/stack/FetchRequest.getBrowserConfigRecord ()Lnet/rim/device/apps/internal/browser/options/BrowserConfigRecord;
      // 5fd: astore 21
      // 5ff: aload 21
      // 601: ifnonnull 615
      // 604: invokestatic net/rim/device/apps/internal/browser/core/BrowserSession.getCurrentSession ()Lnet/rim/device/apps/internal/browser/core/BrowserSession;
      // 607: astore 22
      // 609: aload 22
      // 60b: ifnull 615
      // 60e: aload 22
      // 610: invokevirtual net/rim/device/apps/internal/browser/core/BrowserSession.getConfig ()Lnet/rim/device/apps/internal/browser/options/BrowserConfigRecord;
      // 613: astore 21
      // 615: aload 21
      // 617: ifnull 636
      // 61a: aload 21
      // 61c: bipush 3
      // 61e: invokevirtual net/rim/device/apps/internal/browser/options/BrowserConfigRecord.getPropertyAsString (I)Ljava/lang/String;
      // 621: astore 22
      // 623: aload 22
      // 625: getstatic net/rim/device/internal/browser/wap/WPTCPServiceRecord.SERVICE_CID Ljava/lang/String;
      // 628: ldc_w 1701707776
      // 62b: invokestatic net/rim/device/api/util/StringUtilities.strEqualIgnoreCase (Ljava/lang/String;Ljava/lang/String;I)Z
      // 62e: ifeq 636
      // 631: invokestatic net/rim/device/internal/system/InternalServices.getUptime ()J
      // 634: lstore 19
      // 636: new net/rim/device/apps/internal/browser/download/SavingDownloadManager
      // 639: dup
      // 63a: aload 0
      // 63b: getfield net/rim/device/apps/internal/browser/core/BrowserImpl._downloadPage Lnet/rim/device/apps/internal/browser/download/DownloadPage;
      // 63e: aload 10
      // 640: aload 18
      // 642: aload 12
      // 644: lload 19
      // 646: invokespecial net/rim/device/apps/internal/browser/download/SavingDownloadManager.<init> (Lnet/rim/device/api/browser/field/RenderingApplication;Ljavax/microedition/io/InputConnection;Ljava/io/InputStream;Lnet/rim/device/apps/internal/browser/ui/SaveFileDialog;J)V
      // 649: astore 22
      // 64b: aload 0
      // 64c: getfield net/rim/device/apps/internal/browser/core/BrowserImpl._backgroundTask Lnet/rim/device/apps/internal/browser/threading/BackgroundTaskThreadPool;
      // 64f: aload 22
      // 651: invokevirtual net/rim/device/apps/internal/browser/threading/BackgroundTaskThreadPool.scheduleJob (Lnet/rim/device/apps/internal/browser/threading/Job;)V
      // 654: aload 0
      // 655: getfield net/rim/device/apps/internal/browser/core/BrowserImpl._downloadPage Lnet/rim/device/apps/internal/browser/download/DownloadPage;
      // 658: aload 22
      // 65a: invokevirtual net/rim/device/apps/internal/browser/download/DownloadPage.addDownload (Lnet/rim/device/apps/internal/browser/download/SavingDownloadManager;)V
      // 65d: aload 1
      // 65e: invokevirtual net/rim/device/apps/internal/browser/stack/FetchRequest.addToDownloadManager ()V
      // 661: aload 11
      // 663: ifnonnull 677
      // 666: aload 1
      // 667: invokevirtual net/rim/device/apps/internal/browser/stack/FetchRequest.getError ()Ljava/lang/Object;
      // 66a: ifnonnull 677
      // 66d: aload 1
      // 66e: sipush 235
      // 671: invokestatic net/rim/device/apps/internal/browser/resources/BrowserResources.getString (I)Ljava/lang/String;
      // 674: invokevirtual net/rim/device/apps/internal/browser/stack/FetchRequest.setError (Ljava/lang/Object;)V
      // 677: ldc2_w 1907089860548946979
      // 67a: ldc_w 1113682464
      // 67d: bipush 5
      // 67f: invokestatic net/rim/device/api/system/EventLogger.logEvent (JII)Z
      // 682: pop
      // 683: aload 11
      // 685: areturn
      // try (76 -> 91): 92 null
      // try (256 -> 267): 271 null
      // try (256 -> 267): 276 null
      // try (271 -> 272): 276 null
      // try (276 -> 277): 276 null
      // try (365 -> 376): 380 null
      // try (365 -> 376): 385 null
      // try (380 -> 381): 385 null
      // try (385 -> 386): 385 null
      // try (427 -> 437): 441 null
      // try (427 -> 437): 446 null
      // try (441 -> 442): 446 null
      // try (446 -> 447): 446 null
      // try (462 -> 465): 466 null
      // try (477 -> 480): 481 null
   }

   public static final BrowserContent getSource(Event currentEvent) {
      BrowserContent browserContent = null;

      while (currentEvent != null) {
         switch (currentEvent.getUID()) {
            case 10006:
               currentEvent = ((RedirectEvent)currentEvent).getOriginalEvent();
               break;
            case 10010:
               browserContent = (BrowserContent)currentEvent.getSource();
            default:
               currentEvent = null;
         }
      }

      return browserContent;
   }

   public static final Event getOriginalEvent(Event event) {
      while (event instanceof Object) {
         event = ((RedirectEvent)event).getOriginalEvent();
      }

      return event;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final void displayPage(Page page, RenderThread localRenderThread, FetchRequest fetchRequest) {
      if (page == null) {
         fetchRequest.setError(BrowserResources.getString(236));
         this._currentUrlEncoding = null;
      } else if (page.getBrowserContent() == null) {
         this._currentUrlEncoding = PersistentContent.encode(page.getURL(), false, true);
      } else {
         int renderingFlags = fetchRequest.getModelResult().getRenderingFlags();
         HistoryNode historyNode = null;
         page.setFrameset(fetchRequest.getTarget());
         boolean loadInFullWindow = true;
         if ((renderingFlags & 16) != 0) {
            if (this._currentPage == null) {
               return;
            }

            Manager browserField = page.getBrowserContent().getContentManager();
            Manager manager = null;
            BrowserContent sourceFrame = getSource(fetchRequest.getEvent());
            if (sourceFrame != null) {
               manager = sourceFrame.getDisplayableContent().getManager();
            } else {
               Manager contentManager = this._currentPage.getBrowserContent().getContentManager();
               if (contentManager != null) {
                  Field field = contentManager.getLeafFieldWithFocus();
                  if (field != null) {
                     manager = field.getManager();

                     while (!(manager instanceof Object) && manager != null) {
                        manager = manager.getManager();
                     }
                  } else {
                     manager = null;
                  }

                  if (manager == null && contentManager.getFieldCount() > 0) {
                     field = contentManager.getField(contentManager.getFieldCount() - 1);
                     if (field instanceof Object) {
                        manager = (Manager)field;
                     }
                  }
               }
            }

            if (manager instanceof Object) {
               FrameManager frameManager = (FrameManager)manager;
               Frame targetFrame = fetchRequest.getTarget();
               if (targetFrame != null) {
                  String target = targetFrame.getName();
                  if (target != null) {
                     manager = FrameManager.find(target, frameManager.getTopFrameManager().getManager());
                  }
               }
            }

            if (manager != null) {
               this._browserLock.waitForLock();
               boolean var59 = false /* VF: Semaphore variable */;

               try {
                  var59 = true;
                  Asserts.productionUserAbortAssert(this._currentRenderThread == localRenderThread);
                  this._requestingNewPage = false;
                  synchronized (Application.getEventLock()) {
                     manager.deleteAll();
                     manager.add(browserField);
                     browserField.setFocus();
                  }

                  loadInFullWindow = false;
                  Frame outermostFrameset = null;
                  Frame targetFrame = fetchRequest.getTarget();
                  Frame parent = targetFrame.getParent();
                  if (parent != null) {
                     Frame[] children = parent.getChildren();
                     int size = children.length;

                     for (int i = 0; i < size; i++) {
                        if (children[i].getName().equals(targetFrame.getName())) {
                           parent.replaceChild(targetFrame, i);
                           break;
                        }
                     }
                  }

                  for (Frame frame = fetchRequest.getTarget(); frame != null; frame = frame.getParent()) {
                     outermostFrameset = frame;
                  }

                  this._currentPage.setFrameset(outermostFrameset);
                  if (this._currentSession != null) {
                     if ((renderingFlags & 8192) == 0 && (renderingFlags & 16384) == 0) {
                        var59 = false;
                     } else {
                        History history = this._currentSession.getHistory();
                        HistoryNode containerHistoryNode = history.currentNode();
                        if (containerHistoryNode != null) {
                           historyNode = new HistoryNode(
                              containerHistoryNode.getUrl(),
                              page.getFriendlyTitle(),
                              containerHistoryNode.getContext(),
                              containerHistoryNode.getPostData(),
                              containerHistoryNode.getRequestHeaders(),
                              containerHistoryNode.isHomePage(),
                              this._currentSession.getConfig(),
                              -1,
                              Frame.getClone(outermostFrameset, false)
                           );
                           if ((renderingFlags & 8192) != 0) {
                              history.addNewNode(historyNode, false);
                              var59 = false;
                           } else if ((renderingFlags & 16384) != 0) {
                              history.replaceCurrentNode(historyNode);
                              var59 = false;
                           } else {
                              var59 = false;
                           }
                        } else {
                           var59 = false;
                        }
                     }
                  } else {
                     var59 = false;
                  }
               } finally {
                  if (var59) {
                     this._browserLock.releaseLock();
                  }
               }

               this._browserLock.releaseLock();
            }
         }

         fetchRequest.setEvent(null);
         if (loadInFullWindow) {
            this._currentUrlEncoding = PersistentContent.encode(page.getURL(), false, true);
            this._browserLock.waitForLock();
            boolean var44 = false /* VF: Semaphore variable */;

            try {
               var44 = true;
               Asserts.productionUserAbortAssert(this._currentRenderThread == localRenderThread);
               if (this._currentSession != null) {
                  if ((renderingFlags & 8192) != 0) {
                     ModelResult mr = (ModelResult)page.getModelResult();
                     this._currentSession
                        .getHistory()
                        .addNewNode(
                           historyNode = new HistoryNode(
                              page.getURL(),
                              page.getFriendlyTitle(),
                              page.getContext(),
                              mr.getPostData(),
                              mr.getRequestHeaders(),
                              mr.isHomePage(),
                              this._currentSession.getConfig(),
                              -1,
                              Frame.getClone(fetchRequest.getTarget(), false)
                           )
                        );
                  } else if ((renderingFlags & 16384) != 0) {
                     ModelResult mr = (ModelResult)page.getModelResult();
                     this._currentSession
                        .getHistory()
                        .replaceCurrentNode(
                           historyNode = new HistoryNode(
                              page.getURL(),
                              page.getFriendlyTitle(),
                              page.getContext(),
                              mr.getPostData(),
                              mr.getRequestHeaders(),
                              mr.isHomePage(),
                              this._currentSession.getConfig(),
                              -1,
                              Frame.getClone(fetchRequest.getTarget(), false)
                           )
                        );
                  }
               }

               synchronized (this.getAppEventLock()) {
                  if (this._browserScreen != null && this._browserScreen.isDisplayed()) {
                     Screen screen = this.getActiveScreen();
                     if (!(screen instanceof BrowserScreen)) {
                        this.popScreen(screen);
                     }
                  }

                  if (this._currentPage != null && this._currentSession != null) {
                     PageCache pc = this._currentSession.getPageCache();
                     if (pc != null && this._currentPage.includeInPageCache() && !StringUtilities.strEqual(this._currentPage.getURL(), page.getURL())) {
                        label845:
                        try {
                           Frame f = fetchRequest.getTarget();
                           if (f.getParent() == null) {
                              BrowserContentImpl ui = this._currentPage.getBrowserContent();
                              this._currentSession
                                 .getPageCache()
                                 .addContent(
                                    this._currentPage.getURL(),
                                    new PageCacheContent(ui, ((ModelResult)this._currentPage.getModelResult()).getCacheResult(), this._currentPage.getStyle())
                                 );
                              ui.setRenderingApplication(null);
                           }
                        } finally {
                           break label845;
                        }
                     }
                  }

                  this._currentPage = page;
                  this._requestingNewPage = false;
                  this.pushPage(page, true, false);
                  var44 = false;
               }
            } finally {
               if (var44) {
                  this._browserLock.releaseLock();
               }
            }

            this._browserLock.releaseLock();
         }

         this._rawDataCache.pauseCommit();
         boolean var36 = false /* VF: Semaphore variable */;

         try {
            var36 = true;
            page.finishLoading();
            var36 = false;
         } finally {
            if (var36) {
               if (historyNode != null) {
                  historyNode.setFrameset(Frame.getClone(fetchRequest.getTarget().getTop(), false));
                  historyNode.setTitle(page.getFriendlyTitle());
               }
            }
         }

         if (historyNode != null) {
            historyNode.setFrameset(Frame.getClone(fetchRequest.getTarget().getTop(), false));
            historyNode.setTitle(page.getFriendlyTitle());
         }

         this._rawDataCache.unPauseCommit();
         this._browserLock.waitForLock();
         boolean var28 = false /* VF: Semaphore variable */;

         try {
            var28 = true;
            if (!this._requestingNewPage) {
               Asserts.productionUserAbortAssert(this._currentRenderThread == localRenderThread);
               if (page.getNumberOfPendingJobs() > 0) {
                  this.changeToLoadingSecondaryURLsState(loadInFullWindow ? page : null);
                  var28 = false;
               } else {
                  this.changeToLoadedState(loadInFullWindow ? page : null);
                  this._currentPage.finishedPage(page);
                  var28 = false;
               }
            } else {
               var28 = false;
            }
         } finally {
            if (var28) {
               this._browserLock.releaseLock();
            }
         }

         this._browserLock.releaseLock();
         EventLogger.logEvent(1907089860548946979L, 1113679460, 0);
      }
   }

   private final Frame findFrame(String name) {
      Manager manager = null;
      Manager contentManager = this._currentPage.getBrowserContent().getContentManager();
      if (contentManager != null) {
         Field field = contentManager.getLeafFieldWithFocus();
         if (field != null) {
            manager = field.getManager();

            while (!(manager instanceof Object) && manager != null) {
               manager = manager.getManager();
            }
         } else {
            manager = null;
         }

         if (manager == null && contentManager.getFieldCount() > 0) {
            field = contentManager.getField(contentManager.getFieldCount() - 1);
            if (field instanceof Object) {
               manager = (Manager)field;
            }
         }
      }

      if (manager instanceof Object) {
         FrameManager frameManager = (FrameManager)manager;
         frameManager = FrameManager.find(name, frameManager.getTopFrameManager().getManager());
         if (frameManager instanceof Object) {
            return frameManager.getFrame();
         }
      }

      return null;
   }

   private final void browserConfigChanged(boolean refreshAcceptValuesIfNecessary) {
      BrowserConfigRecord browserConfigRecord = null;
      if (this._currentSession != null) {
         browserConfigRecord = this._currentSession.getConfig();
      }

      if (browserConfigRecord != null && this._renderingSession != null) {
         RenderingOptions renderingOptions = this._renderingSession.getRenderingOptions();
         this.setBrowserConfigRenderingOptions(renderingOptions, browserConfigRecord);
         BrowserConfigRecord initialConfig = browserConfigRecord;
         if (!StringUtilities.strEqualIgnoreCase(browserConfigRecord.getUid(), this._initialConfigUID, 1701707776)) {
            initialConfig = BrowserConfigRecord.getDecodedConfig(this._initialConfigUID, BrowserConfigRecord.INVALID_VALUE, null);
         }

         if (initialConfig != null) {
            this._startupPage = initialConfig.getPropertyAsIntWithOverride((byte)2);
         }

         if (refreshAcceptValuesIfNecessary) {
            this.refreshAcceptValues();
         }
      }
   }

   private final Page createPage(InputConnection conn, FetchRequest fetchRequest, RenderThread localRenderThread) {
      if (conn == null) {
         return null;
      }

      InputStream in = null;

      try {
         in = RendererControl.getInputStreamFromContentEncoding(conn, conn.openInputStream());
         localRenderThread.setInput(in);
      } finally {
         ;
      }

      String contentType = RendererControl.getContentType(conn);
      PageConverterWrapper wrapper = new PageConverterWrapper(fetchRequest, in, conn);
      Converter converter = BrowserConverterDescriptor.getConverter(contentType);
      return (Page)converter.convert((DataInput)((Object)null), wrapper);
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final Page processResponse(InputConnection conn, FetchRequest fetchRequest, RenderThread localRenderThread) {
      Page newPage = null;
      ModelResult modelResult = fetchRequest.getModelResult();
      if (modelResult.getCacheResult() == null) {
         fetchRequest.setError(((StringBuffer)(new Object())).append(BrowserResources.getString(141)).append(":\n\n").append(modelResult.getURL()).toString());
         return null;
      }

      try {
         if (fetchRequest.getEvent() instanceof Object) {
            Frame targetFrame = fetchRequest.getTarget();
            if (targetFrame != null) {
               targetFrame.setUrl(fetchRequest.getModelResult().getURL());
            }
         }

         newPage = this.createPage(conn, fetchRequest, localRenderThread);
      } catch (Throwable var8) {
         newPage = null;
         fetchRequest.setError(e.toString());
         return newPage;
      }

      return newPage;
   }

   @Override
   public final void activate() {
      super.activate();
      if (this._browserState == 3 || this._browserState == 4) {
         FastDormancyManager.getInstance().setFastDormancy(false);
      }

      RadioInternal.dataCallGoActive();
      this._navigationThread.requestExclusiveTask(11);
   }

   public BrowserImpl() {
      boolean systemStart = !BrowserDaemonRegistry.isRegistered();
      BrowserImpl oldInstance = BrowserDaemonRegistry.setInstance(this);
      if (systemStart) {
         EventLogger.register(1907089860548946979L, "net.rim.browser", 2);
         OptionsProviderRegistration.registerOptionsProvider(new BrowserOptionsProvider());
         ServiceBookSyncCollection sync = ServiceBookSyncCollection.getInstance();
         sync.registerCIDForRestoreDisable(BrowserConfigRecord.SERVICE_CID);
         sync.registerCIDForRestoreDisable(BrowserConfigRecord.IPPP_SERVICE_CID);
         sync.registerCIDForRestoreDisable(BrowserConfigRecord.WAP_SERVICE_CID);
         sync.registerCIDForRestoreDisable(BrowserConfigRecord.TCP_SERVICE_CID);
         sync.registerCIDForRestoreDisable("WAPPushConfig");
         BrowserFolders.registerOnceOnSystemStart();
         PackageManager.registerOnceOnSystemStart();
      }

      EventLogger.logEvent(1907089860548946979L, 1148417121, 0);
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      this._acceptValueProviders = (Vector)ar.getOrWaitFor(-728596185634430319L);
      if (this._acceptValueProviders == null) {
         this._acceptValueProviders = (Vector)(new Object());
         ar.put(-728596185634430319L, this._acceptValueProviders);
      }

      this._ribbonManager = new RibbonManagerThread(this);
      this._pendingRequestThread = PendingRequestThread.getInstance();
      this._rawDataCache = new RawDataCache();
      this._backgroundTask = new BackgroundTaskThreadPool();
      this.enableKeyUpEvents(true);
      this._fontCache = FontCache.getInstance();
      this._progressManager = new ProgressManager(this);
      this._cookieCache = CookieCache.getInstance();
      this._navigationThread = new BrowserImpl$NavigationThread(this);
      this._menuDelayThread = new MenuDelayThread(this._navigationThread, GeneralProperty.getCurrentPropertyAsInt(35));
      this._pushThread = new PushThread();
      this._workerThread = new WorkerThread();
      this._offlineQueue = new OfflineQueue();
      this._verbRepository = new BrowserVerbRepository();
      this._activeDownloads = (Vector)(new Object());
      if (systemStart) {
         ((Thread)(new Object(new Channels()))).start();
      }

      this.initialize();
      this._duringStartup = false;
      BrowserDaemonRegistry.doneStartup();
      ServiceRecord[] records = ServiceBook.getSB().findRecordsByCid(BrowserConfigRecord.SERVICE_CID);

      for (int i = 0; i < records.length; i++) {
         this._ribbonManager.handleAddedServiceRecord(records[i], false);
      }

      this._ribbonManager.updateDesktopMdsConfigs(false);
      this.injectDefaultBrowserServiceRecords(true);
      this._ribbonManager.autoStartIfRequired(false);
      this._offlineQueue.start();
      this._backdoor = (BackdoorKeyProcessor)(new Object(true, this));
      MemoryStats memStats = Memory.getRAMStats();
      this._ykMemLimitHint = (memStats.getAllocated() + memStats.getFree()) / 1048576;
      if ((this._ykMemLimitHint & 7) != 0) {
         this._ykMemLimitHint = (this._ykMemLimitHint & -8) + 8;
      }

      this._ykMemLimitHint *= 16;
      BrowserDaemonRegistry.addBrowserConfigChangeListener(this);
      GeneralProperty.addListener(this);
      if (oldInstance != null && oldInstance._lastConfigUID != null) {
         this._singleBrowserMode = oldInstance._singleBrowserMode;
         this._initialConfigUID = oldInstance._initialConfigUID;
         this.activateConfig(oldInstance._lastConfigUID, false);
      }

      BrowserImpl var6 = null;
   }

   private final void initialize() {
      this._ribbonManager.start();
      this._navigationThread.start();
      this._menuDelayThread.start();
      this._pushThread.start();
      this._workerThread.start();
      this.updateLastCoverage();
      long npc = HRUtils.getNpcForActiveNetwork();
      this.eventOccurred(-254931370837867202L, (int)npc, (int)(npc >> 32), null, null);
      this.signalLevel(RadioInfo.getSignalLevel());
   }

   private final void injectDefaultBrowserServiceRecords(boolean reinject) {
      this.injectWLANBrowserServiceRecords(reinject);
   }

   private final void injectWLANBrowserServiceRecords(boolean reinject) {
      boolean wlanEnabled = WLAN.isWLANAllowed();
      if (this._wlanEnabled != wlanEnabled || reinject && wlanEnabled) {
         this._wlanEnabled = wlanEnabled;
         if (wlanEnabled) {
            SBInjector.injectWLANTCPBrowser();
            return;
         }

         SBInjector.removeWLANTCPBrowser();
      }
   }
}
