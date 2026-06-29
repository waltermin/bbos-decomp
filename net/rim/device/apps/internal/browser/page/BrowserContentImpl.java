package net.rim.device.apps.internal.browser.page;

import java.util.Vector;
import javax.microedition.io.HttpConnection;
import javax.microedition.io.InputConnection;
import net.rim.device.api.browser.field.BrowserContentBaseImpl;
import net.rim.device.api.browser.field.RenderingApplication;
import net.rim.device.api.browser.field.RenderingOptions;
import net.rim.device.api.browser.field.RequestedResource;
import net.rim.device.api.browser.field.ResourceProvider;
import net.rim.device.api.io.http.HttpHeaders;
import net.rim.device.api.system.Application;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.component.CookieProvider;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.URLProvider;
import net.rim.device.apps.api.framework.model.VerbProvider;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.ui.CookieProviderUtilities;
import net.rim.device.apps.api.utility.general.URI;
import net.rim.device.apps.internal.browser.api.LoadingImagesEvent;
import net.rim.device.apps.internal.browser.common.BrowserPhoneConfirmation;
import net.rim.device.apps.internal.browser.common.RenderingUtilities;
import net.rim.device.apps.internal.browser.core.IBrowserContext;
import net.rim.device.apps.internal.browser.core.SecondaryURLManager;
import net.rim.device.apps.internal.browser.core.SecondaryURLNode;
import net.rim.device.apps.internal.browser.img.ImageRenderingConverter;
import net.rim.device.apps.internal.browser.model.HTTPAddressModel;
import net.rim.device.apps.internal.browser.resources.BrowserResources;
import net.rim.device.apps.internal.browser.ui.BrowserTextFlowManager;
import net.rim.device.apps.internal.browser.util.URLCache;
import net.rim.device.apps.internal.browser.verbs.MoreImagesVerb;
import net.rim.device.apps.internal.browser.verbs.ShowUrlVerb;
import org.w3c.dom.Document;

public class BrowserContentImpl extends BrowserContentBaseImpl {
   protected SecondaryURLManager _secondaryURLManager;
   protected boolean _imageEnqued;
   private int _imagesToLoad;
   private int _imagesLoaded;
   protected Renderer _renderer;
   protected int _enquedImages;
   private Verb _onTimer;
   protected PageTimer _timer;
   protected int _imagesToRequest;
   protected URLCache _urlCache;
   private Verb _prevVerb;
   private boolean _providesPrevVerb;
   protected Vector _fieldVerbs;
   private String _baseTarget;
   private static BrowserPhoneConfirmation _browserPhoneConfirmation = new BrowserPhoneConfirmation();

   public BrowserContentImpl(
      Renderer renderer, String url, Manager contentManager, RenderingApplication renderingApplication, RenderingOptions renderingOptions, int flags
   ) {
      super(url, (Field)(contentManager == null ? new Object(3460171888704094208L) : contentManager), renderingApplication, renderingOptions, flags);
      this._renderer = renderer;
      this._secondaryURLManager = new SecondaryURLManager(super._renderingOptions);
      super._url = url;
      if (this.showImages()) {
         this._imagesToRequest = super._renderingOptions.getPropertyWithIntValue(4550690918222697397L, 11, 10);
      } else {
         this._imagesToRequest = 0;
      }

      if (super._url != null) {
         this._urlCache = new URLCache(super._url, super._renderingOptions);
         if (super._renderingOptions != null && super._renderingOptions.getPropertyWithBooleanValue(4550690918222697397L, 49, false) && renderer != null) {
            InputConnection inputConnection = renderer._inputConnection;
            if (inputConnection instanceof Object) {
               HttpConnection httpConnection = (HttpConnection)inputConnection;

               try {
                  String baseURI = httpConnection.getHeaderField("content-location");
                  if (baseURI == null) {
                     baseURI = httpConnection.getHeaderField("location");
                  }

                  if (baseURI != null) {
                     this._urlCache.setBase(URI.getAbsoluteURL(baseURI, url));
                     return;
                  }
               } finally {
                  return;
               }
            }
         }
      }
   }

   @Override
   public String toString() {
      return "";
   }

   public Document getDOMDocument() {
      return null;
   }

   public boolean savesContext() {
      return true;
   }

   public BrowserPhoneConfirmation getBrowserPhoneConfirmation() {
      return _browserPhoneConfirmation;
   }

   public Vector getVerbs() {
      return this._fieldVerbs;
   }

   public void setVerbs(Vector verbs) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   public final int getNavigation() {
      return super._flags & 7;
   }

   public void setRenderingApplication(RenderingApplication renderingApplication) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   public int getImagesToLoad() {
      return this._imagesToLoad;
   }

   public int getImagesLoaded() {
      return this._imagesLoaded;
   }

   public void setImagesToLoad(int value) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   public String getBaseTarget() {
      return this._baseTarget;
   }

   public void setBaseTarget(String baseTarget) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   public boolean jumpToPosition(long value) {
      return true;
   }

   public boolean showImages() {
      return true;
   }

   protected Verb[] getFindVerb() {
      return null;
   }

   public Object invokeFind(boolean findNext, Object context) {
      return null;
   }

   protected String getDecompilerName() {
      return null;
   }

   public Manager getContentManager() {
      return (Manager)this.getDisplayableContent();
   }

   public void resourceRequested(RequestedResource resource) {
   }

   public boolean isDisplayed() {
      Manager mgr = this.getDisplayableContent().getManager();
      return mgr != null ? mgr.getScreen() != null : false;
   }

   public Verb getPrevVerb() {
      return this._prevVerb;
   }

   public void setPrevVerb(Verb verb) {
      this._providesPrevVerb = true;
      this._prevVerb = verb;
   }

   public boolean getProvidesPrevVerb() {
      return this._providesPrevVerb;
   }

   public URLCache getUrlCache() {
      return this._urlCache;
   }

   @Override
   public String resolveUrl(String relative) {
      return this._urlCache != null ? this._urlCache.getAbsoluteURL(relative, true) : URI.getAbsoluteURL(relative, null);
   }

   public void addLinkAddressVerb(Menu menu) {
      if (super._renderingOptions.getPropertyWithBooleanValue(4550690918222697397L, 34, false)) {
         Object model = null;
         Field fieldWithFocus = this.getDisplayableContent().getLeafFieldWithFocus();
         Manager fieldsMgr = this.getContentManager();
         if (!(fieldWithFocus instanceof Object)) {
            if (fieldsMgr instanceof BrowserTextFlowManager) {
               model = CookieProviderUtilities.getDefaultCookie(((BrowserTextFlowManager)fieldsMgr).getCookieWithFocus());
            }
         } else {
            model = CookieProviderUtilities.getDefaultCookie(((CookieProvider)fieldWithFocus).getCookieWithFocus());
         }

         String url = null;
         if (model != null && !(model instanceof HTTPAddressModel)) {
            if (!(model instanceof Object)) {
               url = BrowserResources.getString(390);
            } else {
               URLProvider urlProvider = (URLProvider)model;
               url = urlProvider.getURL();
               if (url != null) {
                  url = this.resolveUrl(url);
               }
            }
         }

         if (url != null) {
            ShowUrlVerb verb = new ShowUrlVerb(null, url, this, 1);
            menu.add((MenuItem)(new Object(verb, Integer.MAX_VALUE)));
         }
      }
   }

   public void addBrowserContentMenuItems(Menu menu) {
      this.addLinkAddressVerb(menu);
      if (this.hasUnrequestedImages()) {
         MoreImagesVerb verb = new MoreImagesVerb(this, true);
         menu.add((MenuItem)(new Object(verb, 30)));
         verb = new MoreImagesVerb(this, false);
         MenuItem item = (MenuItem)(new Object(verb, 30));
         menu.add(item);
         if (menu.getDefault() == null) {
            menu.setDefault(item);
         }
      }

      Verb[] findVerbs = this.getFindVerb();
      if (findVerbs != null) {
         int size = findVerbs.length;

         for (int i = 0; i < size; i++) {
            menu.add((MenuItem)(new Object(findVerbs[i], Integer.MAX_VALUE)));
         }
      }

      if (this._fieldVerbs != null) {
         int size = this._fieldVerbs.size();

         for (int i = 0; i < size; i++) {
            MenuItem item = (MenuItem)(new Object((Verb)this._fieldVerbs.elementAt(i), 20));
            menu.add(item);
            if (i == 0 && menu.getDefault() == null) {
               menu.setDefault(item);
            }
         }
      }
   }

   public void requestMoreImages(boolean doMoreAll) {
      this._imagesToLoad = 0;
      this._imagesLoaded = 0;
      this.requestMoreImagesInternal(doMoreAll);
   }

   public void requestImage(String urlToLoad) {
      this._imagesToLoad = 0;
      this._imagesLoaded = 0;
      this.requestMoreImages(new Object[]{urlToLoad});
   }

   private void requestMoreImagesInternal(boolean doMoreAll) {
      int amount = doMoreAll
         ? super._renderingOptions.getPropertyWithIntValue(4550690918222697397L, 11, 10)
         : super._renderingOptions.getPropertyWithIntValue(4550690918222697397L, 10, 5);
      if (this.hasUnrequestedImages()) {
         this.requestMoreImages(this._secondaryURLManager.getSecondaryURLs(amount));
      }
   }

   private void requestMoreImages(String[] items) {
      if (items != null) {
         this._imagesToRequest = items.length;
         if (super._renderingApplication != null) {
            LoadingImagesEvent event = new LoadingImagesEvent(this, 0);
            super._renderingApplication.eventOccurred(event);
         }

         int count = items.length;

         for (int i = 0; i < count; i++) {
            this.requestSecondaryURL(items[i], true, false);
         }

         if (super._renderingApplication != null) {
            LoadingImagesEvent event = new LoadingImagesEvent(this, 1);
            super._renderingApplication.eventOccurred(event);
         }
      }
   }

   public boolean addSecondaryURL(String url, SecondaryURLNode node, boolean returnExpired) {
      if (!this._secondaryURLManager.addSecondaryURL(url, node) && (super._flags & 512) == 0 && this.requestSecondaryURL(url, true, returnExpired)) {
         this._imageEnqued = true;
         return true;
      } else {
         return false;
      }
   }

   public boolean requestSecondaryURL(String url, boolean isImageRequest, boolean returnExpired) {
      if (super._renderingApplication != null
         && (this._imagesToRequest == super._renderingOptions.getPropertyWithIntValue(4550690918222697397L, 11, 10) || this._imagesToRequest-- > 0)) {
         if (!returnExpired) {
            this._imagesToLoad++;
         }

         HttpHeaders requestHeaders = (HttpHeaders)(new Object());
         RenderingUtilities.setReferrer(requestHeaders, super._url);
         if (isImageRequest) {
            requestHeaders.addProperty("Accept", ImageRenderingConverter.getAcceptString());
         }

         RequestedResource resource = (RequestedResource)(new Object(url, requestHeaders, super._flags & 0xFF | (returnExpired ? 32768 : 0)));
         InputConnection conn = null;
         if (!(super._renderingApplication instanceof Object)) {
            conn = super._renderingApplication.getResource(resource, this);
         } else {
            conn = ((ResourceProvider)super._renderingApplication).getInputConnection(resource, this);
         }

         this._secondaryURLManager.setSecondaryURLRequested(url);
         if (conn != null) {
            resource.setInputConnection(conn);
            this.resourceReady(resource);
            return false;
         } else {
            return true;
         }
      } else {
         return false;
      }
   }

   @Override
   public void resourceReady(RequestedResource resource) {
      this._imagesLoaded++;
      this._secondaryURLManager.secondaryURLReady(resource, this);
   }

   public void inlineDataRefReceived(String url) {
      this._secondaryURLManager.inlineDataRefReceived(url);
   }

   public void inlineImageReceived() {
      this._imagesLoaded++;
   }

   public boolean removeInlineDataRef(String url) {
      return this._secondaryURLManager.removeInlineDataRef(url);
   }

   public boolean hasUnrequestedImages() {
      return this._secondaryURLManager.hasUnrequestedImages();
   }

   public Verb getDefaultVerbUnderCursor() {
      Field fieldWithFocus = this.getDisplayableContent().getLeafFieldWithFocus();
      if (fieldWithFocus == null) {
         return null;
      }

      Object model = null;
      if (!(fieldWithFocus instanceof Object)) {
         if (fieldWithFocus != null) {
            Manager manager = fieldWithFocus.getManager();
            if (!(manager instanceof BrowserTextFlowManager)) {
               while (manager != null && !(manager instanceof Object)) {
                  manager = manager.getManager();
               }

               model = manager;
            } else {
               BrowserTextFlowManager tfm = (BrowserTextFlowManager)manager;
               model = CookieProviderUtilities.getDefaultCookie(tfm.getCookieWithFocus());
            }
         }
      } else {
         model = CookieProviderUtilities.getDefaultCookie(((CookieProvider)fieldWithFocus).getCookieWithFocus());
      }

      boolean isModelVerbProvider = model instanceof Object;
      Field field = this.getDisplayableContent();
      if (!isModelVerbProvider && field instanceof Object) {
         fieldWithFocus = field;
         model = field;
         isModelVerbProvider = true;
      }

      if (isModelVerbProvider) {
         Verb[] verbs = new Object[0];
         VerbProvider verbProvider = (VerbProvider)model;
         ContextObject context = (ContextObject)(new Object(2, 73, 96));
         context.setFlag(83);
         context.setFlag(61);
         context.put(8128293842573788963L, _browserPhoneConfirmation);
         context.put(9045827404276417370L, fieldWithFocus);
         Verb defaultVerb = verbProvider.getVerbs(context, verbs);
         if (defaultVerb != null) {
            return defaultVerb;
         }
      }

      return null;
   }

   public boolean submit(boolean validate, IBrowserContext context) {
      return true;
   }

   public boolean isModified() {
      return false;
   }

   public int getAvailableHeight(int height) {
      return super._renderingApplication != null ? super._renderingApplication.getAvailableHeight(this) : height;
   }

   public IBrowserContext getContext() {
      return null;
   }

   public SecondaryURLManager getSecondaryURLManager() {
      return this._secondaryURLManager;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public void finishLoading() {
      if (Application.isEventDispatchThread()) {
         throw new Object("finishLoading is called on the event dispatch thread");
      }

      boolean var4 = false /* VF: Semaphore variable */;

      try {
         var4 = true;
         if (this._renderer != null) {
            this._renderer.finishProcessingData();
         }

         this._enquedImages = this._secondaryURLManager.getUnrequestedCount();
         if (this.showImages()) {
            this.requestMoreImagesInternal(true);
         }

         if (this._imageEnqued) {
            if (super._renderingApplication != null) {
               LoadingImagesEvent event = new LoadingImagesEvent(this, 1);
               super._renderingApplication.eventOccurred(event);
               var4 = false;
            } else {
               var4 = false;
            }
         } else {
            var4 = false;
         }
      } finally {
         if (var4) {
            this._renderer = null;
         }
      }

      this._renderer = null;
   }

   public void setOnTimer(Verb verb) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   public void removeOnTimer() {
      this._onTimer = null;
   }

   public void addTimer(PageTimer timer) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   public void startTimer() {
      if (this._timer != null) {
         this._timer.start();
      }
   }

   public void stopTimer() {
      if (this._timer != null) {
         this._timer.stop();
      }
   }

   public void destroyTimer() {
      this._timer = null;
   }

   public boolean hasTimer() {
      return this._timer != null;
   }

   public void invokeOnTimer() {
      if (this._onTimer != null) {
         new TimerInvokerThread(this._onTimer).start();
      }
   }

   public boolean jumpToFragment(String fragment) {
      return false;
   }

   public boolean supportsFragmentJumping() {
      return false;
   }

   public boolean jumpToNextHeading() {
      return false;
   }

   public boolean supportsHeadingJumping() {
      return false;
   }

   public boolean includeInPageCache() {
      return true;
   }

   public void scrollToTop() {
   }

   public boolean executeJavaScriptAction(Object thiz, String action, Object[] parms) {
      return this.executeJavaScriptAction(thiz, action, parms, -1);
   }

   public boolean executeJavaScriptAction(Object thiz, String action, Object[] parms, long clickID) {
      return false;
   }

   public boolean isBrowserContentEqual(Object bc) {
      return bc == this;
   }
}
