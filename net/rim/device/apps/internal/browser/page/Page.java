package net.rim.device.apps.internal.browser.page;

import java.util.Hashtable;
import java.util.Vector;
import javax.microedition.io.HttpConnection;
import javax.microedition.io.InputConnection;
import javax.microedition.io.SecurityInfo;
import net.rim.device.api.browser.field.BrowserContent;
import net.rim.device.api.browser.field.BrowserContentChangedEvent;
import net.rim.device.api.browser.field.CancelRequestResource;
import net.rim.device.api.browser.field.ContentReadEvent;
import net.rim.device.api.browser.field.Destroyable;
import net.rim.device.api.browser.field.ErrorEvent;
import net.rim.device.api.browser.field.Event;
import net.rim.device.api.browser.field.ExecutingScriptEvent;
import net.rim.device.api.browser.field.HistoryEvent;
import net.rim.device.api.browser.field.RedirectEvent;
import net.rim.device.api.browser.field.RenderingApplication;
import net.rim.device.api.browser.field.RequestedResource;
import net.rim.device.api.browser.field.ResourceProvider;
import net.rim.device.api.browser.field.SetHeaderEvent;
import net.rim.device.api.browser.field.SetHttpCookieEvent;
import net.rim.device.api.browser.field.UrlRequestedEvent;
import net.rim.device.api.io.http.HttpHeaders;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.ApplicationManager;
import net.rim.device.api.system.BackdoorKeyListener;
import net.rim.device.api.system.BackdoorKeyProcessor;
import net.rim.device.api.system.Clipboard;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.CharacterUtilities;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.utility.general.URI;
import net.rim.device.apps.internal.browser.api.CacheSubDataEvent;
import net.rim.device.apps.internal.browser.api.DataModificationEvent;
import net.rim.device.apps.internal.browser.api.DeviceDataConversionEvent;
import net.rim.device.apps.internal.browser.api.DeviceDataWrongContentTypeEvent;
import net.rim.device.apps.internal.browser.api.InlineImageLoadingEvent;
import net.rim.device.apps.internal.browser.api.LoadingImagesEvent;
import net.rim.device.apps.internal.browser.api.LoadingStatusEvent;
import net.rim.device.apps.internal.browser.api.OptionsEvent;
import net.rim.device.apps.internal.browser.api.UIDirectionChangeEvent;
import net.rim.device.apps.internal.browser.api.UrlRequestedInternalEvent;
import net.rim.device.apps.internal.browser.bookmark.Bookmarks;
import net.rim.device.apps.internal.browser.common.RenderingUtilities;
import net.rim.device.apps.internal.browser.cookie.CookieCache;
import net.rim.device.apps.internal.browser.core.BrowserDaemonRegistry;
import net.rim.device.apps.internal.browser.core.BrowserError;
import net.rim.device.apps.internal.browser.core.BrowserImpl;
import net.rim.device.apps.internal.browser.core.BrowserSession;
import net.rim.device.apps.internal.browser.core.BrowserVerbRepository;
import net.rim.device.apps.internal.browser.core.IBrowserContext;
import net.rim.device.apps.internal.browser.history.History;
import net.rim.device.apps.internal.browser.history.HistoryNode;
import net.rim.device.apps.internal.browser.history.LongTermHistory;
import net.rim.device.apps.internal.browser.html.HTMLBrowserField;
import net.rim.device.apps.internal.browser.javascript.JavaScriptRegistry;
import net.rim.device.apps.internal.browser.options.BrowserConfigRecord;
import net.rim.device.apps.internal.browser.resources.BrowserResources;
import net.rim.device.apps.internal.browser.stack.CacheResult;
import net.rim.device.apps.internal.browser.stack.FetchRequest;
import net.rim.device.apps.internal.browser.stack.HeaderParser;
import net.rim.device.apps.internal.browser.stack.ImageFetchJob;
import net.rim.device.apps.internal.browser.stack.ModelResult;
import net.rim.device.apps.internal.browser.stack.OfflineQueue;
import net.rim.device.apps.internal.browser.stack.RawDataCache;
import net.rim.device.apps.internal.browser.ui.FrameManager;
import net.rim.device.apps.internal.browser.ui.TextFlowManager;
import net.rim.device.apps.internal.browser.util.Frame;
import net.rim.device.apps.internal.browser.util.RendererControl;
import net.rim.device.cldc.io.utility.URIDecoder;
import net.rim.device.internal.browser.util.Pipe;
import net.rim.device.internal.i18n.CommonResource;
import net.rim.device.internal.ui.component.SimpleChoiceDialog;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.html2.HTMLLinkElement;

public class Page implements BackdoorKeyListener, RenderingApplication, ResourceProvider {
   protected FetchRequest _fetchRequest;
   protected ModelResult _modelResult;
   private BackdoorKeyProcessor _backdoor;
   private SecurityInfo _securityInfo;
   private HTMLLinkElement[] _rssLinks;
   protected int _verbMask = 4194303;
   protected BrowserContentImpl _browserContent;
   private int _style;
   private String _cookieVal;
   private boolean _finishedLoading;
   private int _lastCookieCacheGeneration = CookieCache.getInstance().getCacheGeneration() - 1;
   private Frame _frameset;
   private ContentReadEvent _contentReadEvent;
   private ImageFetchJob[] _imageJobs = new ImageFetchJob[0];
   private Hashtable _activeFetchRequests = (Hashtable)(new Object());
   private long _lastClickID = -1;
   private UrlRequestedInternalEvent[] _pendingFrameEvents;
   private static final int MAX_APN_ID;
   private static final int MAX_FRIENDLY_NAME_LENGTH;
   public static boolean _richTextMarkup;

   public void setBrowserContent(BrowserContentImpl browserContent) {
      this._browserContent = browserContent;
   }

   public Verb getDefaultVerbUnderCursor() {
      return this._browserContent != null ? this._browserContent.getDefaultVerbUnderCursor() : null;
   }

   public void addPageSpecificMenuItems(Menu menu) {
      BrowserImpl browser = BrowserDaemonRegistry.getInstance();
      BrowserVerbRepository verbRepository = browser.getBrowserVerbRepository();
      int verbMask = this.getVerbMask();
      verbRepository.addVerbsToMenu(menu, verbMask);
      if (verbMask != 66056 && this._browserContent != null) {
         Document doc = this._browserContent.getDOMDocument();
         if (doc instanceof Object && this._rssLinks == null) {
            this._rssLinks = new Object[0];
            NodeList list = ((Document)doc).getElementsByTagName("LINK");
            int size = list.getLength();

            for (int i = 0; i < size; i++) {
               HTMLLinkElement link = (HTMLLinkElement)list.item(i);
               if (StringUtilities.strEqualIgnoreCase(link.getRel(), "alternate", 1701707776)
                  && (
                     StringUtilities.strEqualIgnoreCase(link.getType(), "application/rss+xml", 1701707776)
                        || StringUtilities.strEqualIgnoreCase(link.getType(), "application/atom+xml", 1701707776)
                  )
                  && link.getHref() != null) {
                  Arrays.add(this._rssLinks, link);
               }
            }
         }

         if (this._rssLinks != null && this._rssLinks.length > 0) {
            menu.add(new Page$1(this, BrowserResources.getString(750), 16864256, Integer.MAX_VALUE));
         }

         if ((verbMask & 16384) != 0) {
            String url = this._browserContent.getURL();
            if (url != null) {
               menu.add((MenuItem)(new Object((Verb)(new Object(this.getFriendlyTitle(), url, this._browserContent, 0)), Integer.MAX_VALUE)));
            }
         }
      }
   }

   public void setStyle(int style) {
      this._style = style;
   }

   public int getStyle() {
      return this._style;
   }

   public IBrowserContext getContext() {
      return this._browserContent != null ? this._browserContent.getContext() : null;
   }

   public boolean savesContext() {
      return this._browserContent != null ? this._browserContent.savesContext() : true;
   }

   public boolean isModified() {
      return this._browserContent != null ? this._browserContent.isModified() : false;
   }

   public Manager getContentManager() {
      return this._browserContent != null ? this._browserContent.getContentManager() : null;
   }

   public Frame getFrameset() {
      return this._frameset;
   }

   public void setFrameset(Frame frameset) {
      this._frameset = frameset;
   }

   public boolean jumpToFragment(String fragment) {
      return this._browserContent != null ? this._browserContent.jumpToFragment(fragment) : false;
   }

   public boolean jumpToNextHeading() {
      return this._browserContent != null ? this._browserContent.jumpToNextHeading() : false;
   }

   public void scrollToTop() {
      if (this._browserContent != null) {
         this._browserContent.scrollToTop();
      }
   }

   public int getVerbMask() {
      return this._browserContent != null && this._browserContent.getProvidesPrevVerb() ? this._verbMask & -17 : this._verbMask;
   }

   public void setVerbMask(int newMask) {
      this._verbMask = newMask;
   }

   public void destroy() {
      if (this._browserContent != null) {
         Field f = this._browserContent.getDisplayableContent();
         if (f instanceof Object) {
            ((Destroyable)f).destroy();
         }
      }
   }

   public void setDestroyOnUndisplay(boolean value) {
      if (this._browserContent != null) {
         Field f = this._browserContent.getDisplayableContent();
         if (f instanceof Object) {
            ((Destroyable)f).setDestroyMethod(value ? 0 : 1);
         }
      }
   }

   public BrowserContentImpl getBrowserContent() {
      return this._browserContent;
   }

   public BrowserContentImpl getBrowserContent(String frameName) {
      Manager contentManager = this.getContentManager();
      if (contentManager != null) {
         for (int i = contentManager.getFieldCount() - 1; i >= 0; i--) {
            Field field = contentManager.getField(i);
            if (field instanceof Object) {
               FrameManager frameManager = (FrameManager)field;
               frameManager = FrameManager.find(frameName, frameManager.getTopFrameManager().getManager());
               if (frameManager != null && frameManager.getFieldCount() > 0) {
                  field = frameManager.getField(0);
                  if (field instanceof Object) {
                     return ((HTMLBrowserField)field).getBrowserContent();
                  }
               }
               break;
            }
         }
      }

      return null;
   }

   public String getFriendlyTitle() {
      String title = this.getTitle();
      if (title == null || title.length() == 0 || title.equals(BrowserResources.getString(232))) {
         boolean displayable = false;
         int fieldCount = 0;
         Manager contentManager = null;
         if (this._browserContent != null) {
            contentManager = this._browserContent.getContentManager();
            fieldCount = contentManager.getFieldCount();
         }

         for (int i = 0; i < fieldCount; i++) {
            Field field = contentManager.getField(i);
            if (field instanceof Object) {
               title = ((RichTextField)field).getText();
               int length = title.length();
               if (length > 40) {
                  title = title.substring(0, 37).trim();
               } else {
                  title = title.trim();
               }

               int size = title.length();

               for (int j = 0; j < size; j++) {
                  if (!CharacterUtilities.isSpaceChar(title.charAt(j))) {
                     displayable = true;
                     break;
                  }
               }

               if (displayable) {
                  if (length > 40) {
                     title = ((StringBuffer)(new Object())).append(title).append("...").toString();
                  }
                  break;
               }
            }
         }

         if (!displayable) {
            title = this.getURL();
         }
      }

      return title;
   }

   public Object invokeFind(boolean findNext, Object context) {
      return this._browserContent != null ? this._browserContent.invokeFind(findNext, context) : null;
   }

   public Verb getPrevVerb() {
      return this._browserContent != null ? this._browserContent.getPrevVerb() : null;
   }

   public boolean submit(boolean validate, IBrowserContext context) {
      return this._browserContent != null ? this._browserContent.submit(validate, context) : false;
   }

   public void setTitle(String title) {
      if (this._browserContent != null) {
         this._browserContent.setTitle(title);
      }
   }

   public String getTitle() {
      return this._browserContent != null ? this._browserContent.getTitle() : null;
   }

   public EncodedImage getIcon() {
      return this._browserContent != null ? this._browserContent.getIcon() : null;
   }

   public ModelResult getModelResult() {
      return this._modelResult;
   }

   public PageModel getPageModel() {
      if (this._modelResult == null) {
         return null;
      }

      ModelResult newModel = this._modelResult;
      if (this._browserContent != null) {
         IBrowserContext context = this._browserContent.getContext();
         if (context != null) {
            context = context.clone();
            this._browserContent.submit(false, context);
            newModel = this._modelResult.clone();
            newModel.setContext(context);
         }
      }

      return new BrowserPageModel(0, this.getTitle(), newModel);
   }

   public boolean supportsFragmentJumping() {
      return this._browserContent.supportsFragmentJumping();
   }

   public boolean includeInPageCache() {
      if (this._modelResult != null) {
         CacheResult cr = this._modelResult.getCacheResult();
         if (cr != null && this._modelResult.getPostData() == null && this._browserContent != null && this._browserContent.includeInPageCache()) {
            if ((!this.isHttpsPage() || !HeaderParser.containsDirective(cr.getResponseHeaders(), HeaderParser.CACHE_DIRECTIVE_MUST_REVALIDATE))
               && cr.getStatus() == 200) {
               return true;
            }

            return false;
         }
      }

      return false;
   }

   public String getURL() {
      return this._modelResult == null ? null : this._modelResult.getURL();
   }

   public int getSize() {
      int dataSize = 0;
      if (this._modelResult != null && this._modelResult.getCacheResult() != null) {
         dataSize = this._modelResult.getCacheResult().getDataLength();
      }

      Manager contentManager = this.getContentManager();
      return contentManager != null ? 2 * dataSize + 1000 * this.getContentManager().getFieldCount() + 800 : 2 * dataSize + 800;
   }

   public BackdoorKeyProcessor getBackdoorKeyProcessor() {
      return this._backdoor;
   }

   public synchronized void finishedPage(Page page) {
      if (this._pendingFrameEvents != null && this._pendingFrameEvents.length > 0) {
         UrlRequestedInternalEvent pendingEvent = this._pendingFrameEvents[0];
         Arrays.removeAt(this._pendingFrameEvents, 0);
         this.processUrlRequestedEvent(pendingEvent, true);
      }
   }

   public void cancelAllJobs() {
      BrowserImpl browser = BrowserDaemonRegistry.getInstance();
      browser.getBackgroundTask().cancelJobs(this._imageJobs);
      this._imageJobs = new ImageFetchJob[0];
   }

   public void updateScroll() {
      if (!ApplicationManager.getApplicationManager().isSystemLocked()) {
         String url = this.getURL();
         if (url != null) {
            BrowserSession session = BrowserSession.getCurrentSession();
            if (session != null) {
               HistoryNode node = session.getHistory().getNode(url);
               if (node != null) {
                  Manager contentManager = this.getContentManager();
                  if (contentManager instanceof Object) {
                     node.setLastScrollPosition(((TextFlowManager)contentManager).getCurrentAnchor());
                     return;
                  }

                  if (contentManager != null) {
                     XYRect rect = (XYRect)(new Object());
                     contentManager.getFocusRect(rect);
                     node.setLastScrollPosition(contentManager.getVerticalScroll() + rect.y);
                  }
               }
            }
         }
      }
   }

   public boolean jumpToLastScrollPosition(String url) {
      if (url != null) {
         BrowserSession session = BrowserSession.getCurrentSession();
         if (session != null) {
            HistoryNode node = session.getHistory().getNode(url);
            if (node != null) {
               long lastScroll = node.getLastScrollPosition();
               if (lastScroll != -1 && this._browserContent != null) {
                  return this._browserContent.jumpToPosition(lastScroll);
               }
            }
         }
      }

      return true;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public void finishLoading() {
      boolean var8 = false /* VF: Semaphore variable */;

      try {
         var8 = true;
         if (this._browserContent != null) {
            this._browserContent.finishLoading();
            var8 = false;
         } else {
            var8 = false;
         }
      } finally {
         if (var8) {
            BrowserConfigRecord browserConfig = null;
            BrowserSession session = BrowserSession.getCurrentSession();
            if (session != null) {
               browserConfig = session.getConfig();
            }

            LongTermHistory longTermHistory = LongTermHistory.getInstance();
            longTermHistory.addUrl(this.getURL(), this.getFriendlyTitle(), browserConfig);
            this._finishedLoading = true;
            if (this._modelResult != null) {
               CacheResult cacheResult = this._modelResult.getCacheResult();
               if (cacheResult != null && cacheResult.hasUnecryptedPipe()) {
                  cacheResult.getData().waitUntilClosed();
               }
            }
         }
      }

      BrowserConfigRecord browserConfig = null;
      BrowserSession session = BrowserSession.getCurrentSession();
      if (session != null) {
         browserConfig = session.getConfig();
      }

      LongTermHistory longTermHistory = LongTermHistory.getInstance();
      longTermHistory.addUrl(this.getURL(), this.getFriendlyTitle(), browserConfig);
      this._finishedLoading = true;
      if (this._modelResult != null) {
         CacheResult cacheResult = this._modelResult.getCacheResult();
         if (cacheResult != null && cacheResult.hasUnecryptedPipe()) {
            cacheResult.getData().waitUntilClosed();
         }
      }

      boolean allowJumpToFragment = true;
      Manager contentManager = this.getContentManager();
      if (this._modelResult != null && (this._modelResult.getRenderingFlags() & 8192) == 0 && contentManager.getVerticalScroll() <= 0) {
         allowJumpToFragment = this.jumpToLastScrollPosition(this.getURL());
      }

      if (allowJumpToFragment) {
         String url = this.getURL();
         if (url != null) {
            int indexOfFragment = this.getURL().indexOf(35);
            if (indexOfFragment != -1) {
               this.jumpToFragment(url.substring(indexOfFragment + 1));
            }
         }
      }

      BrowserSession browserSession = BrowserSession.getCurrentSession();
      if (browserSession != null) {
         browserSession.requestCompleted();
      }
   }

   public void addVerbsToBrandingField(PageFooterField footer) {
      Vector verbs = null;
      if (this._browserContent != null) {
         verbs = this._browserContent.getVerbs();
      }

      if (verbs != null && verbs.size() > 0 && footer != null) {
         footer.addVerbs(verbs);
      }
   }

   public void setSecurityInfo(SecurityInfo info) {
      if (this.isHttpsPage()) {
         this._securityInfo = info;
      }
   }

   public SecurityInfo getSecurityInfo() {
      return this._securityInfo;
   }

   public void jobFinished(ImageFetchJob job) {
      Arrays.remove(this._imageJobs, job);
      synchronized (this._contentReadEvent) {
         if (this._browserContent != null) {
            this._contentReadEvent.setItemsToRead(this._browserContent.getImagesLoaded());
         } else {
            this._contentReadEvent.setItemsRead(this._contentReadEvent.getItemsRead() + 1);
         }
      }

      if (this._finishedLoading) {
         this.eventOccurred(this._contentReadEvent);
         if (this._imageJobs.length == 0) {
            BrowserImpl browser = BrowserDaemonRegistry.getInstance();
            browser.pageLoaded(this);
            this.finishedPage(this);
         }
      }
   }

   public int getNumberOfPendingJobs() {
      return this._imageJobs.length;
   }

   @Override
   public Object eventOccurred(Event event) {
      int requestFlags;
      int navigation;
      String absoluteUrl;
      byte[] postData;
      BrowserImpl browser;
      HttpHeaders requestHeaders;
      label4160: {
         int eventId = event.getUID();
         switch (eventId) {
            case 0:
               DeviceDataConversionEvent e = null;

               try {
                  e = (DeviceDataConversionEvent)event;
               } finally {
                  ;
               }

               if (this._fetchRequest != null && this._fetchRequest.isAborted()) {
                  return null;
               }

               if (this._modelResult == null) {
                  return null;
               }

               CacheResult cacheResult = this._modelResult.getCacheResult();
               if (cacheResult == null) {
                  return null;
               }

               cacheResult.setTranscodedData(e.getPipe(), e.getContentType());
               HttpHeaders responseHeaders = cacheResult.getResponseHeaders();
               String proxyEncoding = e.getEncoding();
               if (proxyEncoding != null) {
                  responseHeaders.setProperty("x-rim-proxy-encoding", proxyEncoding);
                  return null;
               }
               break;
            case 1:
               LoadingImagesEvent e = null;

               try {
                  var289 = event;
               } finally {
                  ;
               }

               BrowserImpl browserx = BrowserDaemonRegistry.getInstance();
               if (browserx == null) {
                  return null;
               }

               switch (((LoadingImagesEvent)var289).getState()) {
                  case 0:
                     if (this._contentReadEvent != null) {
                        this._contentReadEvent.setItemsRead(0);
                     }

                     browserx.setLoadingSecondaryURLsState(this);
                     return null;
                  default:
                     return null;
               }
            case 2:
               if (!(event instanceof Object)) {
                  return null;
               }

               if (this._modelResult == null) {
                  return null;
               }

               CacheResult cacheResult = this._modelResult.getCacheResult();
               if (cacheResult == null) {
                  return null;
               }

               Pipe convertedPipe = cacheResult.getTranscodedData();
               if (convertedPipe != null) {
                  return new Object[]{cacheResult.getTranscodedContentType(), convertedPipe.getInputStream()};
               }
               break;
            case 3:
               LoadingStatusEvent e = null;

               try {
                  var286 = event;
               } finally {
                  ;
               }

               BrowserImpl browserx = BrowserDaemonRegistry.getInstance();
               if (browserx == null) {
                  return null;
               }

               browserx.getProgressManager().setLabel(((LoadingStatusEvent)var286).getMessage());
               if (((LoadingStatusEvent)var286).getStatus() != 0) {
                  browserx.getProgressManager().update(3);
                  return null;
               }
               break;
            case 4:
               if (!(event instanceof Object)) {
                  return null;
               }

               RawDataCache rawDataCache = BrowserDaemonRegistry.getInstance().getRawDataCache();
               CacheSubDataEvent e = (CacheSubDataEvent)event;
               switch (e.getStatusCode()) {
                  case 200:
                     byte[] data = e.getData();
                     if (data != null) {
                        CacheResult cr = new CacheResult(e.getUrl(), data, e.getHeaders(), 200, false);
                        rawDataCache.put(e.getUrl(), cr, false);
                        return Boolean.TRUE;
                     }

                     return null;
                  case 304:
                     HttpHeaders headers = e.getHeaders();
                     headers.removeProperties("Content-Type");
                     headers.removeProperties("Content-Length");
                     headers.removeProperties("Content-Encoding");
                     rawDataCache.updateNode(e.getUrl(), headers);
                     return null;
                  default:
                     return null;
               }
            case 5:
               InlineImageLoadingEvent e = null;

               try {
                  var283 = event;
               } finally {
                  ;
               }

               BrowserImpl browserx = BrowserDaemonRegistry.getInstance();
               if (browserx == null) {
                  return null;
               }

               ProgressManager pg = browserx.getProgressManager();
               pg.changeState(((InlineImageLoadingEvent)var283).isFragments() ? 8 : 3);
               return null;
            case 6:
               OptionsEvent e = null;

               try {
                  var281 = event;
               } finally {
                  ;
               }

               switch (((OptionsEvent)var281).getType()) {
                  case 0:
                     return null;
                  case 1:
                  default:
                     String currentBrowserConfigUID = null;
                     BrowserSession session = BrowserSession.getCurrentSession();
                     if (session != null) {
                        currentBrowserConfigUID = session.getConfig().getUid();
                     }

                     if (currentBrowserConfigUID == null) {
                        return null;
                     }

                     BrowserConfigRecord browserConfigRecord = BrowserConfigRecord.getDecodedConfig(currentBrowserConfigUID, -1, null);
                     if (browserConfigRecord == null) {
                        return null;
                     }

                     browserConfigRecord.setPropertyAsBoolean(53, false);
                     browserConfigRecord.commitChangesToServiceBook();
                     return null;
                  case 2:
                     BrowserImpl browserx = BrowserDaemonRegistry.getInstance();
                     if (browserx != null) {
                        browserx.refreshAcceptValues();
                        return null;
                     }

                     return null;
               }
            case 7:
               if (!(event instanceof Object)) {
                  return null;
               }

               PageVerb.reload();
               return null;
            case 8:
               DataModificationEvent e = null;

               try {
                  var279 = event;
               } finally {
                  ;
               }

               if (this._fetchRequest != null && this._fetchRequest.isAborted()) {
                  return null;
               }

               if (this._modelResult == null) {
                  return null;
               }

               CacheResult cacheResult = this._modelResult.getCacheResult();
               if (cacheResult == null) {
                  return null;
               }

               browser = BrowserDaemonRegistry.getInstance();
               browser.getRawDataCache().cacheItemChanged(((DataModificationEvent)var279).getURL(), ((DataModificationEvent)var279).getSize());
               break;
            case 9:
               DeviceDataWrongContentTypeEvent e = null;

               try {
                  var277 = event;
               } finally {
                  ;
               }

               if (this._fetchRequest != null && this._fetchRequest.isAborted()) {
                  return null;
               }

               if (this._modelResult == null) {
                  return null;
               }

               CacheResult cacheResult = this._modelResult.getCacheResult();
               if (cacheResult == null) {
                  return null;
               }

               cacheResult.setTranscodedData(null, null);
               HttpHeaders responseHeaders = cacheResult.getResponseHeaders();
               responseHeaders.setProperty("Content-Type", ((DeviceDataWrongContentTypeEvent)var277).getNewMIMEType());
               return null;
            case 10001:
               BrowserContentChangedEvent e = null;

               try {
                  var275 = event;
               } finally {
                  ;
               }

               BrowserImpl browserx = BrowserDaemonRegistry.getInstance();
               if (browserx == null) {
                  return null;
               }

               BrowserScreen browserScreen = browserx.getBrowserScreen();
               if (browserScreen == null) {
                  return null;
               }

               PageHeaderField headerField = browserScreen.getHeaderField();
               if (headerField == null) {
                  return null;
               }

               if (this._browserContent != null
                  && this._browserContent.isBrowserContentEqual(((Event)var275).getSource())
                  && (this._browserContent.getSharedFlags() & 16) == 0) {
                  headerField.setTitle(this._browserContent.getTitle(), this._browserContent.getIcon());
                  return null;
               }
               break;
            case 10002:
               if (!(event instanceof Object)) {
                  return null;
               }

               Application.getApplication().invokeLater(new Page$3(this));
               return null;
            case 10003:
               ExecutingScriptEvent e = null;

               try {
                  var273 = event;
               } finally {
                  ;
               }

               BrowserImpl browserx = BrowserDaemonRegistry.getInstance();
               if (browserx == null) {
                  return null;
               }

               if (((Event)var273).getSource() instanceof Object) {
                  browserx.setExecutingScriptState((BrowserContent)((Event)var273).getSource());
                  return null;
               }
               break;
            case 10004:
               if (!(event instanceof Object)) {
                  return null;
               }

               BrowserImpl browserx = BrowserDaemonRegistry.getInstance();
               if (browserx == null) {
                  return null;
               }

               browserx.getBrowserScreen().showInFullScreen();
               return null;
            case 10005:
               HistoryEvent e = null;

               try {
                  e = (HistoryEvent)event;
               } finally {
                  ;
               }

               BrowserImpl browserx = BrowserDaemonRegistry.getInstance();
               if (browserx == null) {
                  return null;
               }

               switch (e.getType()) {
                  case -1:
                     return null;
                  case 0:
                  default:
                     int index = e.getIndex();
                     if (index == 0) {
                        return null;
                     }

                     BrowserSession session = BrowserSession.getCurrentSession();
                     if (session == null) {
                        return null;
                     }

                     History history = session.getHistory();
                     requestFlags = history.lookupCurrentNodeId();
                     navigation = requestFlags + index;
                     if (navigation < 0 && index == -1) {
                        browserx.handleEscapeOnLastPage(true);
                        return null;
                     }

                     HistoryNode node = history.getNode(navigation);
                     if (node == null) {
                        return null;
                     }

                     ModelResult modelResult = new ModelResult(node.getUrl(), index > 0 ? 1 : 2, node.getRequestHeaders());
                     modelResult.setContext(node.getContext());
                     modelResult.setPostData(node.getPostData());
                     modelResult.setHomePage(node.isHomePage());
                     FetchRequest fRequest = new FetchRequest(modelResult, e.isProgrammatic() ? 32 : 0);
                     fRequest.setTarget(node.getFrameset());
                     fRequest.setHistoryRequest(true);
                     String currentConfigUID = session.getConfig().getUid();
                     String historyConfigUID = currentConfigUID;
                     BrowserConfigRecord historyConfig = BrowserConfigRecord.getDecodedConfig(node.getConfigUID(), node.getConfigType(), node.getTransportCID());
                     if (historyConfig != null) {
                        historyConfigUID = historyConfig.getUid();
                     }

                     if (!StringUtilities.strEqualIgnoreCase(historyConfigUID, currentConfigUID, 1701707776)) {
                        browserx.activateConfig(historyConfigUID, true);
                     }

                     browserx.initiateFetchRequest(fRequest);
                     return null;
                  case 1:
                     BrowserSession session = BrowserSession.getCurrentSession();
                     if (session == null) {
                        return null;
                     }

                     History history = session.getHistory();
                     HistoryNode node = new HistoryNode(
                        e.getURL(),
                        this.getFriendlyTitle(),
                        this.getContext(),
                        this._modelResult.getPostData(),
                        this._modelResult.getRequestHeaders(),
                        this._modelResult.isHomePage(),
                        session.getConfig()
                     );
                     history.addNewNode(node);
                     return null;
                  case 2:
                     BrowserSession session = BrowserSession.getCurrentSession();
                     if (session == null) {
                        return null;
                     }

                     History history = session.getHistory();
                     int currentIndex = history.lookupCurrentNodeId();
                     HistoryNode node = history.getNode(currentIndex - 1);
                     if (node != null && StringUtilities.strEqual(node.getUrl(), e.getURL())) {
                        history.previousNode();
                        return null;
                     }

                     return null;
               }
            case 10006:
               RedirectEvent e = null;

               try {
                  e = (RedirectEvent)event;
               } finally {
                  ;
               }

               ModelResult modelResult = null;
               browser = BrowserDaemonRegistry.getInstance();
               if (browser == null) {
                  return null;
               }

               String referrer = e.getSourceURL();
               Event originalEvent = BrowserImpl.getOriginalEvent(e);
               requestFlags = !(originalEvent instanceof Object) ? this._modelResult.getRenderingFlags() : ((UrlRequestedEvent)originalEvent).getFlags() | 8192;
               navigation = 1;
               boolean useOriginalRequestMethod = false;
               absoluteUrl = URI.getAbsoluteURL(e.getLocation(), e.getSourceURL());
               switch (e.getType()) {
                  case -1:
                     break;
                  case 0:
                     Object eventSource = e.getSource();
                     if (eventSource instanceof Object) {
                        HttpConnection httpConnection = (HttpConnection)eventSource;
                        referrer = httpConnection.getRequestProperty(HeaderParser.REFERER);

                        label4151:
                        try {
                           useOriginalRequestMethod = httpConnection.getResponseCode() == 307;
                        } finally {
                           break label4151;
                        }
                     }
                     break;
                  case 1:
                  default:
                     Application.getApplication().invokeAndWait(new Page$2(this));
                     break;
                  case 2:
                     if (absoluteUrl != null && absoluteUrl.equals(this.getURL())) {
                        navigation = 3;
                     }
                     break;
                  case 3:
                     referrer = null;
                     if (e.getTimerValue() == 0) {
                        requestFlags &= -8193;
                        requestFlags |= 16384;
                     }

                     navigation = 4;
               }

               BrowserSession session = BrowserSession.getCurrentSession();
               if (session != null && session.getConfig().getPropertyAsInt(12) == 0) {
                  navigation = 3;
               }

               requestHeaders = (HttpHeaders)(new Object());
               RenderingUtilities.setReferrer(requestHeaders, referrer);
               postData = null;
               if (!useOriginalRequestMethod) {
                  break label4160;
               }

               Event previousEvent = e.getOriginalEvent();
               if (!(previousEvent instanceof Object)) {
                  break label4160;
               }

               UrlRequestedEvent urlRequestedEvent = (UrlRequestedEvent)previousEvent;
               postData = urlRequestedEvent.getPostData();
               if (postData == null) {
                  break label4160;
               }

               Page$ConfirmPostRunnable confirmPostRunnable = new Page$ConfirmPostRunnable();
               Application.getApplication().invokeAndWait(confirmPostRunnable);
               if (confirmPostRunnable.isPostDesired()) {
                  HttpHeaders previousHeaders = urlRequestedEvent.getHeaders();
                  if (previousHeaders != null) {
                     String contentType = previousHeaders.getPropertyValue("Content-Type");
                     if (contentType != null) {
                        requestHeaders.setProperty("Content-Type", contentType);
                     }
                  }
                  break label4160;
               }
               break;
            case 10007:
               SetHeaderEvent e = null;

               try {
                  e = (SetHeaderEvent)event;
               } finally {
                  ;
               }

               if (this._modelResult == null) {
                  return null;
               }

               CacheResult cacheResult = this._modelResult.getCacheResult();
               if (cacheResult == null) {
                  return null;
               }

               HttpHeaders responseHeaders = cacheResult.getResponseHeaders();
               if (responseHeaders == null) {
                  return null;
               }

               String key = e.getKey();
               if (key.equals(HeaderParser.CACHE_CONTROL)) {
                  HeaderParser.appendDirective(responseHeaders, e.getValue());
                  cacheResult.recalculateExpiry();
                  return null;
               }

               if (key.equals(HeaderParser.EXPIRES)) {
                  cacheResult.setExpiration(e.getValue());
                  return null;
               }
               break;
            case 10008:
               SetHttpCookieEvent e = null;

               try {
                  var264 = event;
               } finally {
                  ;
               }

               if (this._browserContent != null) {
                  CookieCache.getInstance().addCookies(((SetHttpCookieEvent)var264).getURL(), ((SetHttpCookieEvent)var264).getCookie());
                  return null;
               }
               break;
            case 10009:
               if (!(event instanceof Object)) {
                  return null;
               }

               Application.getApplication().invokeLater(new Page$4(this));
               return null;
            case 10010:
               try {
                  this.processUrlRequestedEvent((UrlRequestedEvent)event, false);
                  return null;
               } finally {
                  ;
               }
            case 10011:
               if (!(event instanceof Object)) {
                  return null;
               }

               ContentReadEvent readEvent = (ContentReadEvent)event;
               BrowserImpl browserx = BrowserDaemonRegistry.getInstance();
               if (browserx == null) {
                  return null;
               }

               boolean update = true;
               ProgressManager pg = browserx.getProgressManager();
               int numRead = readEvent.getItemsRead();
               if (numRead != -1) {
                  update = pg.setContentReadProgress(numRead, readEvent.getItemsToRead(), readEvent.getItemsToReadInBytes());
               }

               if (update) {
                  pg.update(1);
                  return null;
               }
               break;
            case 10012:
               if (!(event instanceof Object)) {
                  return null;
               }

               UIDirectionChangeEvent e = (UIDirectionChangeEvent)event;
               BrowserImpl browserx = BrowserDaemonRegistry.getInstance();
               if (browserx == null) {
                  return null;
               }

               BrowserScreen browserScreen = browserx.getBrowserScreen();
               if (browserScreen == null) {
                  return null;
               }

               if (this._browserContent != null
                  && this._browserContent.isBrowserContentEqual(e.getSource())
                  && (this._browserContent.getSharedFlags() & 16) == 0) {
                  boolean onLhs = e.getDirection() == 1;
                  this._style &= -33;
                  if (onLhs) {
                     this._style |= 32;
                  }

                  browserx.invokeAndWait(new Page$5(this, browserScreen, onLhs));
                  return null;
               }
               break;
            case 10013:
               if (!(event instanceof Object)) {
                  return null;
               }

               ErrorEvent e = (ErrorEvent)event;
               BrowserImpl browserx = BrowserDaemonRegistry.getInstance();
               if (browserx == null) {
                  return null;
               }

               browserx.invokeLater(new BrowserError(e.getErrorString(), false, false));
               return null;
            case 10014:
               if (!(event instanceof Object)) {
                  return null;
               }

               CancelRequestResource e = (CancelRequestResource)event;
               RequestedResource resource = e.getResource();
               if (resource != null) {
                  FetchRequest fRequest = (FetchRequest)this._activeFetchRequests.get(resource);
                  if (fRequest != null) {
                     fRequest.abort();
                     return null;
                  }
               }
         }

         return null;
      }

      ModelResult var297 = new ModelResult(absoluteUrl, requestFlags, requestHeaders);
      var297.setNavigation(navigation);
      var297.setHomePage(this._modelResult.isHomePage());
      var297.setPostData(postData);
      FetchRequest fetchRequest = new FetchRequest(var297, 32);
      fetchRequest.setEvent(event);
      browser.initiateFetchRequest(fetchRequest);
      return null;
   }

   @Override
   public String getHTTPCookie(String url) {
      if (this._browserContent != null && this._browserContent.getUrlCache() != null) {
         CookieCache cache = CookieCache.getInstance();
         if (cache.getCacheGeneration() != this._lastCookieCacheGeneration) {
            this._cookieVal = cache.getCookies(url);
            this._lastCookieCacheGeneration = cache.getCacheGeneration();
         }

         return this._cookieVal;
      } else {
         return null;
      }
   }

   @Override
   public HttpConnection getResource(RequestedResource resource, BrowserContent referrer) {
      InputConnection conn = this.getInputConnection(resource, referrer);
      return (HttpConnection)(!(conn instanceof Object) ? null : conn);
   }

   @Override
   public InputConnection getInputConnection(RequestedResource param1, BrowserContent param2) {
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
      // 000: aload 0
      // 001: getfield net/rim/device/apps/internal/browser/page/Page._finishedLoading Z
      // 004: ifne 01a
      // 007: aload 0
      // 008: getfield net/rim/device/apps/internal/browser/page/Page._fetchRequest Lnet/rim/device/apps/internal/browser/stack/FetchRequest;
      // 00b: ifnull 01a
      // 00e: aload 0
      // 00f: getfield net/rim/device/apps/internal/browser/page/Page._fetchRequest Lnet/rim/device/apps/internal/browser/stack/FetchRequest;
      // 012: invokevirtual net/rim/device/apps/internal/browser/stack/FetchRequest.isAborted ()Z
      // 015: ifeq 01a
      // 018: aconst_null
      // 019: areturn
      // 01a: aload 1
      // 01b: ifnonnull 020
      // 01e: aconst_null
      // 01f: areturn
      // 020: aload 1
      // 021: invokevirtual net/rim/device/api/browser/field/RequestedResource.getFlags ()I
      // 024: istore 3
      // 025: aload 1
      // 026: invokevirtual net/rim/device/api/browser/field/RequestedResource.getUrl ()Ljava/lang/String;
      // 029: astore 4
      // 02b: aload 1
      // 02c: invokevirtual net/rim/device/api/browser/field/RequestedResource.getRequestMethod ()Ljava/lang/String;
      // 02f: astore 5
      // 031: aload 1
      // 032: invokevirtual net/rim/device/api/browser/field/RequestedResource.getRequestData ()[B
      // 035: astore 6
      // 037: aload 4
      // 039: ifnonnull 03e
      // 03c: aconst_null
      // 03d: areturn
      // 03e: aload 0
      // 03f: invokevirtual net/rim/device/apps/internal/browser/page/Page.getURL ()Ljava/lang/String;
      // 042: astore 7
      // 044: aload 4
      // 046: ldc_w "file://"
      // 049: ldc_w 1701707776
      // 04c: invokestatic net/rim/device/api/util/StringUtilities.startsWithIgnoreCase (Ljava/lang/String;Ljava/lang/String;I)Z
      // 04f: ifeq 067
      // 052: aload 7
      // 054: ifnull 067
      // 057: aload 7
      // 059: ldc_w "file://"
      // 05c: ldc_w 1701707776
      // 05f: invokestatic net/rim/device/api/util/StringUtilities.startsWithIgnoreCase (Ljava/lang/String;Ljava/lang/String;I)Z
      // 062: ifne 067
      // 065: aconst_null
      // 066: areturn
      // 067: iload 3
      // 068: bipush 7
      // 06a: iand
      // 06b: istore 8
      // 06d: iload 3
      // 06e: ldc_w 32768
      // 071: iand
      // 072: ifeq 079
      // 075: bipush 1
      // 076: goto 07a
      // 079: bipush 0
      // 07a: istore 9
      // 07c: invokestatic net/rim/device/apps/internal/browser/core/BrowserDaemonRegistry.getInstance ()Lnet/rim/device/apps/internal/browser/core/BrowserImpl;
      // 07f: invokevirtual net/rim/device/apps/internal/browser/core/BrowserImpl.getRawDataCache ()Lnet/rim/device/apps/internal/browser/stack/RawDataCache;
      // 082: astore 10
      // 084: iload 8
      // 086: bipush 3
      // 088: if_icmpeq 0ff
      // 08b: aload 5
      // 08d: ldc_w "GET"
      // 090: ldc_w 1701707776
      // 093: invokestatic net/rim/device/api/util/StringUtilities.strEqualIgnoreCase (Ljava/lang/String;Ljava/lang/String;I)Z
      // 096: ifeq 0ff
      // 099: aload 10
      // 09b: aload 4
      // 09d: iload 8
      // 09f: aconst_null
      // 0a0: aload 6
      // 0a2: aload 1
      // 0a3: invokevirtual net/rim/device/api/browser/field/RequestedResource.getRequestHeaders ()Lnet/rim/device/api/io/http/HttpHeaders;
      // 0a6: aload 0
      // 0a7: getfield net/rim/device/apps/internal/browser/page/Page._fetchRequest Lnet/rim/device/apps/internal/browser/stack/FetchRequest;
      // 0aa: ifnull 0b7
      // 0ad: aload 0
      // 0ae: getfield net/rim/device/apps/internal/browser/page/Page._fetchRequest Lnet/rim/device/apps/internal/browser/stack/FetchRequest;
      // 0b1: invokevirtual net/rim/device/apps/internal/browser/stack/FetchRequest.isHistoryRequest ()Z
      // 0b4: goto 0b8
      // 0b7: bipush 0
      // 0b8: iload 9
      // 0ba: bipush 1
      // 0bb: invokevirtual net/rim/device/apps/internal/browser/stack/RawDataCache.get (Ljava/lang/String;ILjava/lang/String;[BLnet/rim/device/api/io/http/HttpHeaders;ZZZ)Lnet/rim/device/apps/internal/browser/stack/CacheResult;
      // 0be: astore 11
      // 0c0: aload 11
      // 0c2: ifnull 0ff
      // 0c5: aload 11
      // 0c7: invokevirtual net/rim/device/apps/internal/browser/stack/CacheResult.getData ()Lnet/rim/device/internal/browser/util/Pipe;
      // 0ca: astore 12
      // 0cc: aload 12
      // 0ce: ifnull 0ef
      // 0d1: aload 12
      // 0d3: invokevirtual net/rim/device/internal/browser/util/Pipe.isClosed ()Z
      // 0d6: ifeq 0ef
      // 0d9: new net/rim/device/apps/internal/browser/stack/CachedHttpConnection
      // 0dc: dup
      // 0dd: aload 4
      // 0df: aconst_null
      // 0e0: aload 11
      // 0e2: invokevirtual net/rim/device/apps/internal/browser/stack/CacheResult.getStream ()Ljava/io/InputStream;
      // 0e5: aload 11
      // 0e7: aload 1
      // 0e8: invokevirtual net/rim/device/api/browser/field/RequestedResource.getRequestHeaders ()Lnet/rim/device/api/io/http/HttpHeaders;
      // 0eb: invokespecial net/rim/device/apps/internal/browser/stack/CachedHttpConnection.<init> (Ljava/lang/String;Ljavax/microedition/io/HttpConnection;Ljava/io/InputStream;Lnet/rim/device/apps/internal/browser/stack/CacheResult;Lnet/rim/device/api/io/http/HttpHeaders;)V
      // 0ee: areturn
      // 0ef: new net/rim/device/apps/internal/browser/stack/CachedHttpConnection
      // 0f2: dup
      // 0f3: aload 4
      // 0f5: aload 11
      // 0f7: aload 1
      // 0f8: invokevirtual net/rim/device/api/browser/field/RequestedResource.getRequestHeaders ()Lnet/rim/device/api/io/http/HttpHeaders;
      // 0fb: invokespecial net/rim/device/apps/internal/browser/stack/CachedHttpConnection.<init> (Ljava/lang/String;Lnet/rim/device/apps/internal/browser/stack/CacheResult;Lnet/rim/device/api/io/http/HttpHeaders;)V
      // 0fe: areturn
      // 0ff: aload 1
      // 100: invokevirtual net/rim/device/api/browser/field/RequestedResource.isCacheOnly ()Z
      // 103: ifeq 108
      // 106: aconst_null
      // 107: areturn
      // 108: aload 2
      // 109: ifnull 10f
      // 10c: goto 230
      // 10f: aload 0
      // 110: getfield net/rim/device/apps/internal/browser/page/Page._browserContent Lnet/rim/device/apps/internal/browser/page/BrowserContentImpl;
      // 113: ifnull 11e
      // 116: aload 0
      // 117: getfield net/rim/device/apps/internal/browser/page/Page._browserContent Lnet/rim/device/apps/internal/browser/page/BrowserContentImpl;
      // 11a: aload 1
      // 11b: invokevirtual net/rim/device/apps/internal/browser/page/BrowserContentImpl.resourceRequested (Lnet/rim/device/api/browser/field/RequestedResource;)V
      // 11e: bipush 0
      // 11f: istore 11
      // 121: aload 4
      // 123: ifnonnull 129
      // 126: goto 225
      // 129: iload 11
      // 12b: iinc 11 1
      // 12e: bipush 10
      // 130: if_icmplt 136
      // 133: goto 225
      // 136: aload 1
      // 137: invokevirtual net/rim/device/api/browser/field/RequestedResource.getRequestHeaders ()Lnet/rim/device/api/io/http/HttpHeaders;
      // 13a: invokevirtual net/rim/device/api/io/http/HttpHeaders.cloneHeaders ()Lnet/rim/device/api/io/http/HttpHeaders;
      // 13d: astore 12
      // 13f: invokestatic net/rim/device/apps/internal/browser/core/BrowserDaemonRegistry.getInstance ()Lnet/rim/device/apps/internal/browser/core/BrowserImpl;
      // 142: aload 12
      // 144: invokevirtual net/rim/device/apps/internal/browser/core/BrowserImpl.addStandardRequestHeaders (Lnet/rim/device/api/io/http/HttpHeaders;)V
      // 147: new net/rim/device/apps/internal/browser/stack/ModelResult
      // 14a: dup
      // 14b: aload 4
      // 14d: iload 3
      // 14e: aload 12
      // 150: invokespecial net/rim/device/apps/internal/browser/stack/ModelResult.<init> (Ljava/lang/String;ILnet/rim/device/api/io/http/HttpHeaders;)V
      // 153: astore 13
      // 155: new net/rim/device/apps/internal/browser/stack/FetchRequest
      // 158: dup
      // 159: aload 13
      // 15b: invokespecial net/rim/device/apps/internal/browser/stack/FetchRequest.<init> (Lnet/rim/device/apps/internal/browser/stack/ModelResult;)V
      // 15e: astore 14
      // 160: aload 0
      // 161: getfield net/rim/device/apps/internal/browser/page/Page._activeFetchRequests Ljava/util/Hashtable;
      // 164: aload 1
      // 165: aload 14
      // 167: invokevirtual java/util/Hashtable.put (Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
      // 16a: pop
      // 16b: aload 10
      // 16d: aload 14
      // 16f: invokevirtual net/rim/device/apps/internal/browser/stack/RawDataCache.get (Lnet/rim/device/apps/internal/browser/stack/FetchRequest;)Ljavax/microedition/io/InputConnection;
      // 172: astore 15
      // 174: aload 15
      // 176: dup
      // 177: instanceof java/lang/Object
      // 17a: ifne 181
      // 17d: pop
      // 17e: goto 219
      // 181: checkcast java/lang/Object
      // 184: astore 16
      // 186: aload 16
      // 188: invokeinterface javax/microedition/io/HttpConnection.getResponseCode ()I 1
      // 18d: istore 17
      // 18f: iload 17
      // 191: sipush 300
      // 194: if_icmpge 19a
      // 197: goto 219
      // 19a: iload 17
      // 19c: sipush 400
      // 19f: if_icmpge 219
      // 1a2: aload 16
      // 1a4: invokestatic net/rim/device/apps/internal/browser/util/RendererControl.getRedirectURL (Ljavax/microedition/io/HttpConnection;)Ljava/lang/String;
      // 1a7: astore 4
      // 1a9: aload 16
      // 1ab: invokeinterface javax/microedition/io/InputConnection.openInputStream ()Ljava/io/InputStream; 1
      // 1b0: astore 18
      // 1b2: sipush 512
      // 1b5: newarray 8
      // 1b7: astore 19
      // 1b9: aload 18
      // 1bb: aload 19
      // 1bd: invokevirtual java/io/InputStream.read ([B)I
      // 1c0: bipush -1
      // 1c2: if_icmpeq 1c8
      // 1c5: goto 1b9
      // 1c8: aload 18
      // 1ca: invokevirtual java/io/InputStream.close ()V
      // 1cd: goto 1d2
      // 1d0: astore 19
      // 1d2: aload 15
      // 1d4: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 1d9: goto 121
      // 1dc: astore 19
      // 1de: goto 121
      // 1e1: astore 19
      // 1e3: aload 18
      // 1e5: invokevirtual java/io/InputStream.close ()V
      // 1e8: goto 1ed
      // 1eb: astore 19
      // 1ed: aload 15
      // 1ef: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 1f4: goto 121
      // 1f7: astore 19
      // 1f9: goto 121
      // 1fc: astore 20
      // 1fe: aload 18
      // 200: invokevirtual java/io/InputStream.close ()V
      // 203: goto 208
      // 206: astore 21
      // 208: aload 15
      // 20a: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 20f: goto 214
      // 212: astore 21
      // 214: aload 20
      // 216: athrow
      // 217: astore 17
      // 219: aload 0
      // 21a: getfield net/rim/device/apps/internal/browser/page/Page._activeFetchRequests Ljava/util/Hashtable;
      // 21d: aload 1
      // 21e: invokevirtual java/util/Hashtable.remove (Ljava/lang/Object;)Ljava/lang/Object;
      // 221: pop
      // 222: aload 15
      // 224: areturn
      // 225: aload 0
      // 226: getfield net/rim/device/apps/internal/browser/page/Page._activeFetchRequests Ljava/util/Hashtable;
      // 229: aload 1
      // 22a: invokevirtual java/util/Hashtable.remove (Ljava/lang/Object;)Ljava/lang/Object;
      // 22d: pop
      // 22e: aconst_null
      // 22f: areturn
      // 230: aload 0
      // 231: getfield net/rim/device/apps/internal/browser/page/Page._contentReadEvent Lnet/rim/device/api/browser/field/ContentReadEvent;
      // 234: ifnonnull 24b
      // 237: aload 0
      // 238: new java/lang/Object
      // 23b: dup
      // 23c: aload 0
      // 23d: invokespecial net/rim/device/api/browser/field/ContentReadEvent.<init> (Ljava/lang/Object;)V
      // 240: putfield net/rim/device/apps/internal/browser/page/Page._contentReadEvent Lnet/rim/device/api/browser/field/ContentReadEvent;
      // 243: aload 0
      // 244: getfield net/rim/device/apps/internal/browser/page/Page._contentReadEvent Lnet/rim/device/api/browser/field/ContentReadEvent;
      // 247: bipush 0
      // 248: invokevirtual net/rim/device/api/browser/field/ContentReadEvent.setItemsRead (I)V
      // 24b: aload 0
      // 24c: getfield net/rim/device/apps/internal/browser/page/Page._browserContent Lnet/rim/device/apps/internal/browser/page/BrowserContentImpl;
      // 24f: ifnull 263
      // 252: aload 0
      // 253: getfield net/rim/device/apps/internal/browser/page/Page._contentReadEvent Lnet/rim/device/api/browser/field/ContentReadEvent;
      // 256: aload 0
      // 257: getfield net/rim/device/apps/internal/browser/page/Page._browserContent Lnet/rim/device/apps/internal/browser/page/BrowserContentImpl;
      // 25a: invokevirtual net/rim/device/apps/internal/browser/page/BrowserContentImpl.getImagesToLoad ()I
      // 25d: invokevirtual net/rim/device/api/browser/field/ContentReadEvent.setItemsToRead (I)V
      // 260: goto 273
      // 263: aload 0
      // 264: getfield net/rim/device/apps/internal/browser/page/Page._contentReadEvent Lnet/rim/device/api/browser/field/ContentReadEvent;
      // 267: aload 0
      // 268: getfield net/rim/device/apps/internal/browser/page/Page._contentReadEvent Lnet/rim/device/api/browser/field/ContentReadEvent;
      // 26b: invokevirtual net/rim/device/api/browser/field/ContentReadEvent.getItemsToRead ()I
      // 26e: bipush 1
      // 26f: iadd
      // 270: invokevirtual net/rim/device/api/browser/field/ContentReadEvent.setItemsToRead (I)V
      // 273: invokestatic net/rim/device/apps/internal/browser/core/BrowserDaemonRegistry.getInstance ()Lnet/rim/device/apps/internal/browser/core/BrowserImpl;
      // 276: astore 11
      // 278: new net/rim/device/apps/internal/browser/stack/ImageFetchJob
      // 27b: dup
      // 27c: aload 1
      // 27d: aload 2
      // 27e: aload 0
      // 27f: invokespecial net/rim/device/apps/internal/browser/stack/ImageFetchJob.<init> (Lnet/rim/device/api/browser/field/RequestedResource;Lnet/rim/device/api/browser/field/BrowserContent;Lnet/rim/device/apps/internal/browser/page/Page;)V
      // 282: astore 12
      // 284: aload 0
      // 285: getfield net/rim/device/apps/internal/browser/page/Page._imageJobs [Lnet/rim/device/apps/internal/browser/stack/ImageFetchJob;
      // 288: aload 12
      // 28a: invokestatic net/rim/device/api/util/Arrays.add ([Ljava/lang/Object;Ljava/lang/Object;)V
      // 28d: aload 11
      // 28f: invokevirtual net/rim/device/apps/internal/browser/core/BrowserImpl.getBackgroundTask ()Lnet/rim/device/apps/internal/browser/threading/BackgroundTaskThreadPool;
      // 292: aload 12
      // 294: invokevirtual net/rim/device/apps/internal/browser/threading/BackgroundTaskThreadPool.scheduleJob (Lnet/rim/device/apps/internal/browser/threading/Job;)V
      // 297: aconst_null
      // 298: areturn
      // try (207 -> 209): 210 null
      // try (211 -> 213): 214 null
      // try (198 -> 207): 216 null
      // try (217 -> 219): 220 null
      // try (221 -> 223): 224 null
      // try (198 -> 207): 226 null
      // try (216 -> 217): 226 null
      // try (227 -> 229): 230 null
      // try (231 -> 233): 234 null
      // try (226 -> 227): 226 null
      // try (182 -> 237): 237 null
   }

   @Override
   public int getHistoryPosition(BrowserContent browserContent) {
      if (browserContent == null) {
         return -1;
      }

      BrowserSession session = BrowserSession.getCurrentSession();
      if (session == null) {
         return -1;
      }

      History history = session.getHistory();
      if (browserContent instanceof Object) {
         int navigation = ((BrowserContentImpl)browserContent).getNavigation();
         if (navigation == 1 && (this._modelResult.getRenderingFlags() & 8192) != 0) {
            return history.lookupCurrentNodeId() + 1;
         }
      }

      return history.lookupCurrentNodeId();
   }

   @Override
   public void invokeRunnable(Runnable runnable) {
      BrowserDaemonRegistry.getInstance().enqueRunnable(runnable);
   }

   @Override
   public boolean openDevelopmentBackdoor(int backdoorCode) {
      if (backdoorCode == 1279740999) {
         EventLogger.startEventLogViewer();
         return true;
      }

      if (backdoorCode == 1380077641) {
         this.showPageInfo();
         return true;
      }

      if (backdoorCode == 1380073814) {
         this._verbMask = 4194303;
         return true;
      }

      if (backdoorCode == 1380076879) {
         _richTextMarkup = !_richTextMarkup;
         Dialog.alert(((StringBuffer)(new Object("Markup version changed to :"))).append(_richTextMarkup ? "16.2" : "16.10").toString());
         return true;
      }

      if (backdoorCode == 1380074561) {
         Bookmarks._debugAutoUpdate = !Bookmarks._debugAutoUpdate;
         Dialog.alert(((StringBuffer)(new Object("Debug auto update set value as "))).append(Bookmarks._debugAutoUpdate ? "minutes" : "hours").toString());
         return true;
      }

      if (backdoorCode == 1212826443) {
         JavaScriptRegistry._ajaxed = !JavaScriptRegistry._ajaxed;
         Dialog.alert(((StringBuffer)(new Object("Javascript in mode: "))).append(JavaScriptRegistry._ajaxed ? "hajaxed" : "normal").toString());
         return true;
      }

      if (backdoorCode == 1329876557) {
         TextFlowManager._oneDNavigationMode = !TextFlowManager._oneDNavigationMode;
         Dialog.alert(((StringBuffer)(new Object("OneD Navigation Mode: "))).append(TextFlowManager._oneDNavigationMode ? "enabled" : "disabled").toString());
         return false;
      }

      if (backdoorCode == 1397509455) {
         HTMLBrowserField._singleLayoutMode = !HTMLBrowserField._singleLayoutMode;
         Dialog.alert(((StringBuffer)(new Object("Single Layout Mode: "))).append(HTMLBrowserField._singleLayoutMode ? "enabled" : "disabled").toString());
         return false;
      }

      if (backdoorCode == 1347571284) {
         BrowserImpl browser = BrowserDaemonRegistry.getInstance();
         if (browser != null) {
            BrowserScreen browserScreen = browser.getBrowserScreen();
            if (browserScreen != null) {
               browserScreen.print();
            }
         }

         return true;
      } else {
         return false;
      }
   }

   @Override
   public boolean openProductionBackdoor(int param1) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: iload 1
      // 01: ldc_w 1380079187
      // 04: if_icmpne 86
      // 07: aconst_null
      // 08: astore 2
      // 09: aconst_null
      // 0a: astore 3
      // 0b: aload 0
      // 0c: getfield net/rim/device/apps/internal/browser/page/Page._modelResult Lnet/rim/device/apps/internal/browser/stack/ModelResult;
      // 0f: ifnull 6f
      // 12: aload 0
      // 13: getfield net/rim/device/apps/internal/browser/page/Page._modelResult Lnet/rim/device/apps/internal/browser/stack/ModelResult;
      // 16: invokevirtual net/rim/device/apps/internal/browser/stack/ModelResult.getURL ()Ljava/lang/String;
      // 19: astore 3
      // 1a: aload 0
      // 1b: getfield net/rim/device/apps/internal/browser/page/Page._browserContent Lnet/rim/device/apps/internal/browser/page/BrowserContentImpl;
      // 1e: invokevirtual net/rim/device/apps/internal/browser/page/BrowserContentImpl.getDecompilerName ()Ljava/lang/String;
      // 21: astore 4
      // 23: new java/lang/Object
      // 26: dup
      // 27: ldc_w "net.rim.device.apps.internal.browser.debug."
      // 2a: invokespecial java/lang/StringBuffer.<init> (Ljava/lang/String;)V
      // 2d: aload 4
      // 2f: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 32: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 35: invokestatic java/lang/Class.forName (Ljava/lang/String;)Ljava/lang/Class;
      // 38: astore 5
      // 3a: aload 5
      // 3c: ifnull 6f
      // 3f: aload 5
      // 41: invokevirtual java/lang/Class.newInstance ()Ljava/lang/Object;
      // 44: checkcast net/rim/device/apps/internal/browser/debug/Decompiler
      // 47: astore 6
      // 49: aload 6
      // 4b: aload 0
      // 4c: getfield net/rim/device/apps/internal/browser/page/Page._modelResult Lnet/rim/device/apps/internal/browser/stack/ModelResult;
      // 4f: invokevirtual net/rim/device/apps/internal/browser/stack/ModelResult.getCacheResult ()Lnet/rim/device/apps/internal/browser/stack/CacheResult;
      // 52: invokevirtual net/rim/device/apps/internal/browser/stack/CacheResult.getDataAsArray ()[B
      // 55: invokeinterface net/rim/device/apps/internal/browser/debug/Decompiler.decompile ([B)Ljava/lang/String; 2
      // 5a: astore 2
      // 5b: goto 6f
      // 5e: astore 5
      // 60: goto 6f
      // 63: astore 5
      // 65: goto 6f
      // 68: astore 5
      // 6a: goto 6f
      // 6d: astore 5
      // 6f: aload 2
      // 70: ifnonnull 77
      // 73: ldc_w "Unavailable"
      // 76: astore 2
      // 77: new net/rim/device/apps/internal/browser/debug/DebugViewerScreen
      // 7a: dup
      // 7b: aload 2
      // 7c: ldc_w "Source Viewer"
      // 7f: aload 3
      // 80: invokespecial net/rim/device/apps/internal/browser/debug/DebugViewerScreen.<init> (Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V
      // 83: pop
      // 84: bipush 1
      // 85: ireturn
      // 86: bipush 0
      // 87: ireturn
      // try (18 -> 40): 41 null
      // try (18 -> 40): 43 null
      // try (18 -> 40): 45 null
      // try (18 -> 40): 47 null
   }

   @Override
   public int getAvailableWidth(BrowserContent browserContent) {
      if (this._browserContent != null && !this._browserContent.isBrowserContentEqual(browserContent)) {
         return 0;
      }

      BrowserScreen browserScreen = BrowserDaemonRegistry.getInstance().getBrowserScreen();
      return browserScreen != null ? browserScreen.getWidth() : 0;
   }

   @Override
   public int getAvailableHeight(BrowserContent browserContent) {
      if (this._browserContent != null && !this._browserContent.isBrowserContentEqual(browserContent)) {
         return 0;
      }

      BrowserScreen browserScreen = BrowserDaemonRegistry.getInstance().getBrowserScreen();
      return browserScreen != null ? browserScreen.getAvailableHeight() : 0;
   }

   public static void showPageInfoDialog(String info) {
      String[] choices = new Object[]{CommonResource.getString(100), CommonResource.getString(3)};
      SimpleChoiceDialog dialog = (SimpleChoiceDialog)(new Object((RichTextField)(new Object(info, 67108864)), choices, -1, null, 0));
      dialog.setModal(true);
      dialog.show();
      if (dialog.getSelectedIndex() == 1) {
         Clipboard.getClipboard().put(info);
      }
   }

   private synchronized void processUrlRequestedEvent(UrlRequestedEvent urlRequestedEvent, boolean eventCameFromFrameQueue) {
      if (urlRequestedEvent instanceof Object) {
         UrlRequestedInternalEvent urie = (UrlRequestedInternalEvent)urlRequestedEvent;
         if (this.doOfflineFormSubmission(urie)) {
            return;
         }

         if (!eventCameFromFrameQueue) {
            if (this._lastClickID > 0 && urie.getClickID() == this._lastClickID) {
               if (this._pendingFrameEvents == null) {
                  this._pendingFrameEvents = new Object[1];
                  this._pendingFrameEvents[0] = urie;
                  return;
               }

               Arrays.add(this._pendingFrameEvents, urie);
               return;
            }

            this._lastClickID = urie.getClickID();
            this._pendingFrameEvents = null;
         }
      }

      String absoluteUrl = urlRequestedEvent.getURL();
      HttpHeaders headers = urlRequestedEvent.getHeaders();
      ModelResult modelResult = new ModelResult(absoluteUrl, urlRequestedEvent.getFlags() | 8192, headers);
      modelResult.setPostData(urlRequestedEvent.getPostData());
      if (urlRequestedEvent instanceof Object) {
         modelResult.setLabel(((UrlRequestedInternalEvent)urlRequestedEvent).getLabel());
      }

      modelResult.setHomePage(urlRequestedEvent.isProgrammatic() ? this._modelResult.isHomePage() : false);
      FetchRequest fetchRequest = new FetchRequest(modelResult, urlRequestedEvent.isProgrammatic() ? 32 : 0);
      fetchRequest.setEvent(urlRequestedEvent);
      BrowserDaemonRegistry.getInstance().initiateFetchRequest(fetchRequest);
   }

   public Page(FetchRequest fetchRequest, BrowserContentImpl browserContent, int style) {
      this._browserContent = browserContent;
      this._fetchRequest = fetchRequest;
      this._modelResult = fetchRequest != null ? fetchRequest.getModelResult() : null;
      this._backdoor = (BackdoorKeyProcessor)(new Object(true, this));
      this._style = style;
      BrowserSession session = BrowserSession.getCurrentSession();
      if (session != null && session.getConfig().getPropertyAsInt(7) == 2) {
         this._verbMask = 66056;
      }
   }

   private void showPageInfo() {
      StringBuffer text = (StringBuffer)(new Object());
      if (this._modelResult == null) {
         text.append("modelResult is null!");
      } else {
         CacheResult cacheResult = this._modelResult.getCacheResult();
         if (cacheResult == null) {
            text.append("cacheResult is null!");
         } else {
            text.append(BrowserResources.getString(411));
            text.append(this._modelResult.getURL());
            text.append('\n');
            text.append(BrowserResources.getString(396));
            text.append(cacheResult.getStatus());
            text.append(' ');
            text.append(RendererControl.getStatusDescription(cacheResult.getStatus()));
            text.append('\n');
            text.append(BrowserResources.getString(395));
            text.append(cacheResult.getDataLength());
            text.append('\n');
            text.append(BrowserResources.getString(375));
            HttpHeaders responseHeaders = cacheResult.getResponseHeaders();
            if (responseHeaders != null) {
               int size = responseHeaders.size();

               for (int i = 0; i < size; i++) {
                  text.append(responseHeaders.getPropertyKey(i));
                  text.append('=');
                  text.append(responseHeaders.getPropertyValue(i));
                  text.append('\n');
               }
            }
         }
      }

      text.append('\n');
      text.append(BrowserResources.getString(412));
      boolean active = false;
      String apn = null;
      byte[] ipAddress = null;

      for (int apnId = 0; apnId < 7; apnId++) {
         try {
            apn = RadioInfo.getAccessPointName(apnId);
         } finally {
            continue;
         }

         active = RadioInfo.isPDPContextActive(apnId);
         ipAddress = RadioInfo.getIPAddress(apnId);
         text.append(apn);
         text.append(' ');
         text.append(ipAddress[0] & 255);
         text.append('.');
         text.append(ipAddress[1] & 255);
         text.append('.');
         text.append(ipAddress[2] & 255);
         text.append('.');
         text.append(ipAddress[3] & 255);
         text.append(active ? " active" : " inactive");
         text.append('\n');
      }

      text.append('\n');
      showPageInfoDialog(text.toString());
   }

   private boolean isHttpsPage() {
      String url = this.getURL();
      return url != null && url.startsWith("https:");
   }

   private boolean doOfflineFormSubmission(UrlRequestedInternalEvent event) {
      HttpHeaders offlineParameters = event.getOfflineParameters();
      String queue = offlineParameters != null ? offlineParameters.getPropertyValue("x-rim-queue-id") : null;
      if (queue != null && event.isSubmitOffline()) {
         queue = URIDecoder.decode(queue, "iso-8859-1");
         HttpHeaders headers = event.getHeaders();
         HttpHeaders queueHeaders = null;
         if (headers != null) {
            queueHeaders = (HttpHeaders)(new Object(headers.toHashtable()));
         } else {
            queueHeaders = (HttpHeaders)(new Object());
         }

         BrowserDaemonRegistry.getInstance().addStandardRequestHeaders(queueHeaders);
         ModelResult queueResult = new ModelResult(event.getURL(), event.getFlags() | 8192, queueHeaders);
         queueResult.setLabel(event.getLabel());
         queueResult.setHomePage(event.isProgrammatic() ? this._modelResult.isHomePage() : false);
         BrowserSession session = BrowserSession.getCurrentSession();
         if (session != null) {
            queueResult.setConfigUID(session.getConfig().getUid());
         }

         queueResult.setPostData(event.getPostData());
         if (!StringUtilities.strEqualIgnoreCase(offlineParameters.getPropertyValue("x-rim-request-id"), "false", 1701707776)) {
            queueHeaders.setProperty("x-rim-request-id", OfflineQueue.generateRequestId());
         }

         if (!StringUtilities.strEqualIgnoreCase(offlineParameters.getPropertyValue("x-rim-request-date"), "false", 1701707776)) {
            queueHeaders.setProperty("x-rim-request-date", OfflineQueue.generateRequestDate());
         }

         String title = offlineParameters.getPropertyValue("x-rim-request-title");
         if (title == null) {
            title = this.getTitle();
         }

         FetchRequest queueRequest = new FetchRequest(queueResult);
         queueRequest.addToQueue(queue, title);
         String nextTarget = offlineParameters.getPropertyValue("x-rim-next-target");
         ModelResult modelResult;
         if (nextTarget != null) {
            nextTarget = this._browserContent.getUrlCache().getAbsoluteURL(nextTarget, true);
            modelResult = new ModelResult(nextTarget, 8193, headers);
         } else {
            modelResult = new ModelResult(((StringBuffer)(new Object("queue:"))).append(queue).toString(), 8193, headers);
         }

         FetchRequest fetchRequest = new FetchRequest(modelResult);
         BrowserImpl browser = BrowserDaemonRegistry.getInstance();
         browser.initiateFetchRequest(fetchRequest);
         return true;
      } else {
         return false;
      }
   }
}
