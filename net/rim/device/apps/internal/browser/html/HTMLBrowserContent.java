package net.rim.device.apps.internal.browser.html;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import javax.microedition.io.HttpConnection;
import net.rim.device.api.browser.field.BrowserContent;
import net.rim.device.api.browser.field.BrowserContentChangedEvent;
import net.rim.device.api.browser.field.Event;
import net.rim.device.api.browser.field.RenderingApplication;
import net.rim.device.api.browser.field.RenderingOptions;
import net.rim.device.api.browser.field.RequestedResource;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Manager;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.browser.core.IBrowserContext;
import net.rim.device.apps.internal.browser.javascript.JavaScriptInterpreter;
import net.rim.device.apps.internal.browser.javascript.JavaScriptResourceCallback;
import net.rim.device.apps.internal.browser.javascript.JavaScriptResourceProvider;
import net.rim.device.apps.internal.browser.page.BrowserContentImpl;
import net.rim.device.apps.internal.browser.page.Renderer;
import net.rim.device.apps.internal.browser.page.RendererImageContainer;
import net.rim.device.apps.internal.browser.ui.TFMFindManager;
import net.rim.device.apps.internal.browser.util.Frame;
import net.rim.vm.Array;
import org.w3c.dom.Document;
import org.w3c.dom.html2.HTMLElement;

final class HTMLBrowserContent extends BrowserContentImpl implements RenderingApplication, JavaScriptResourceProvider {
   private HTMLBrowserField _browserField;
   private TFMFindManager _tfmFindManager;
   private JavaScriptInterpreter _scriptEngine;
   private boolean _allTagsProvided;
   private Hashtable _javascriptCallbacks;
   private long[] _headingPositions = new long[0];
   private int _headingCount;
   private HTMLDocumentImpl _document;
   private Vector _inlinedData = (Vector)(new Object(0));
   private Vector _requestedResources = (Vector)(new Object(0));
   private Frame _frame;

   public final Frame getFrame() {
      return this._frame;
   }

   final void flushPendingLayout() {
      if (super._renderer != null) {
         ((HTMLRendererWithTextFlow)super._renderer).activateLayoutIfFieldsPending();
      }
   }

   public final void addInlinedUrl(String url, int status) {
      if (this._inlinedData != null) {
         InlinedItem item = new InlinedItem();
         item._status = status;
         item._url = url;
         this._inlinedData.addElement(item);
      }
   }

   final boolean markupSupportsJavaScript() {
      return this._allTagsProvided;
   }

   final void setAllTagsProvided() {
      this._allTagsProvided = true;
   }

   protected final void secondaryURLReady(RendererImageContainer container) {
      super._secondaryURLManager.secondaryURLReady(container, this);
   }

   protected final void addHeading() {
      int currentSize = this._headingPositions.length;
      if (this._headingCount >= currentSize) {
         Array.resize(this._headingPositions, currentSize + 10);
      }

      this._headingPositions[this._headingCount++] = this._browserField.getAnchor();
   }

   final void elementChanged(HTMLGenericElement element, boolean relayout) {
      boolean wasActive = this._browserField.isLayoutActive();
      this._browserField.setLayoutActive(false);
      this._browserField.regionChanged(element, relayout);
      if (wasActive) {
         this._browserField.setLayoutActive(true);
      }
   }

   final synchronized void setScriptEngine(JavaScriptInterpreter scriptEngine) {
      this._scriptEngine = scriptEngine;
   }

   public final JavaScriptInterpreter getScriptEngine() {
      return this._scriptEngine;
   }

   final synchronized void pauseScriptEngine() {
      new HTMLBrowserContent$ShutdownScriptEngine(this, true).runIt();
   }

   final synchronized void shutDownScriptEngine() {
      new HTMLBrowserContent$ShutdownScriptEngine(this, false).runIt();
   }

   @Override
   public final void cancelResourceFromJavascript(RequestedResource resource) {
      this._javascriptCallbacks.remove(resource);
      if (super._renderingApplication != null) {
         super._renderingApplication.eventOccurred((Event)(new Object(this, resource)));
      }
   }

   @Override
   public final void requestResourceFromJavascript(RequestedResource resource, JavaScriptResourceCallback ready) {
      if (super._renderingApplication != null) {
         if (ready != null) {
            this._javascriptCallbacks.put(resource, ready);
         }

         super._renderingApplication.getResource(resource, ready != null ? this : null);
      }
   }

   @Override
   public final Object eventOccurred(Event event) {
      if (this.getRenderingApplication() == null) {
         return null;
      }

      int eventId = event.getUID();
      switch (eventId) {
         case 1:
         case 10003:
         case 10006:
         case 10008:
         case 10010:
         case 10011:
            this.getRenderingApplication().eventOccurred(event);
         default:
            return null;
      }
   }

   @Override
   public final int getAvailableHeight(BrowserContent browserContent) {
      if (browserContent == null) {
         return 0;
      }

      Manager manager = browserContent.getDisplayableContent().getManager();
      return manager.getHeight();
   }

   @Override
   public final int getAvailableWidth(BrowserContent browserContent) {
      if (browserContent == null) {
         return 0;
      }

      Manager manager = browserContent.getDisplayableContent().getManager();
      return manager.getWidth();
   }

   @Override
   public final int getHistoryPosition(BrowserContent browserContent) {
      return 0;
   }

   @Override
   public final String getHTTPCookie(String url) {
      return this.getRenderingApplication() != null ? this.getRenderingApplication().getHTTPCookie(url) : null;
   }

   @Override
   public final HttpConnection getResource(RequestedResource resource, BrowserContent referrer) {
      return this.getRenderingApplication() != null ? this.getRenderingApplication().getResource(resource, referrer) : null;
   }

   @Override
   public final void invokeRunnable(Runnable runnable) {
      if (this.getRenderingApplication() != null) {
         this.getRenderingApplication().invokeRunnable(runnable);
      }
   }

   @Override
   public final boolean executeJavaScriptAction(Object thiz, String action, Object[] parms, long clickID) {
      boolean res = false;
      if (this._scriptEngine != null && action != null && action.length() > 0) {
         if (this._frame != null) {
            this._frame.setUserBaseActionAcrossFrames(true);
         }

         res = this._scriptEngine.executeMethod(thiz, action, parms, true, clickID);
         if (this._frame != null) {
            this._frame.setUserBaseActionAcrossFrames(false);
         }

         return res;
      } else {
         return true;
      }
   }

   @Override
   public final void scrollToTop() {
      synchronized (Application.getEventLock()) {
         if (this._browserField.getScreen() != null) {
            this._browserField.setVerticalScroll(0);
            this._browserField.setFocus(0, 0, 0);
         }
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final boolean jumpToPosition(long value) {
      synchronized (Application.getEventLock()) {
         boolean var8 = false /* VF: Semaphore variable */;

         boolean var10000;
         try {
            var8 = true;
            this._browserField.jumpToAnchor(value);
            var10000 = var8 = false;
         } finally {
            if (var8) {
               return true;
            }
         }

         return var10000;
      }
   }

   @Override
   public final boolean jumpToFragment(String fragment) {
      HTMLElement element = (HTMLElement)this._document.getElementById(fragment);
      synchronized (Application.getEventLock()) {
         if (element != null) {
            this._browserField.jumpToAnchor(element);
         } else {
            this._browserField.jumpToAnchor(0);
         }

         return true;
      }
   }

   @Override
   public final boolean supportsFragmentJumping() {
      return true;
   }

   @Override
   public final boolean jumpToNextHeading() {
      long currentPosition = this._browserField.getCurrentAnchor();

      for (int i = 0; i < this._headingCount; i++) {
         if (this._headingPositions[i] > currentPosition) {
            synchronized (Application.getEventLock()) {
               this._browserField.jumpToAnchor(this._headingPositions[i]);
            }

            if (this._browserField.getCurrentAnchor() > currentPosition) {
               return true;
            }
         }
      }

      if (this._headingCount > 0) {
         synchronized (Application.getEventLock()) {
            this._browserField.jumpToAnchor(this._headingPositions[0]);
         }

         if (this._browserField.getCurrentAnchor() != currentPosition) {
            return true;
         }
      }

      return false;
   }

   @Override
   public final boolean supportsHeadingJumping() {
      return true;
   }

   @Override
   public final Verb[] getFindVerb() {
      return !super._renderingOptions.getPropertyWithBooleanValue(4550690918222697397L, 39, false) ? this._tfmFindManager.getVerbs() : null;
   }

   @Override
   public final Object invokeFind(boolean findNext, Object context) {
      return !super._renderingOptions.getPropertyWithBooleanValue(4550690918222697397L, 39, false) ? this._tfmFindManager.invokeFind(findNext, context) : null;
   }

   @Override
   protected final String getDecompilerName() {
      return "HTMLD";
   }

   @Override
   public final boolean showImages() {
      return super._renderingOptions.getPropertyWithBooleanValue(4550690918222697397L, 5, true);
   }

   @Override
   public final boolean isModified() {
      Enumeration elements = this._document.getAllElementsEnumeration();

      while (elements.hasMoreElements()) {
         HTMLNode element = (HTMLNode)elements.nextElement();
         if (element instanceof HTMLInput) {
            Field field = ((HTMLInput)element).getPeer();
            if (field != null && field.isDirty()) {
               return true;
            }
         }
      }

      return false;
   }

   @Override
   public final IBrowserContext getContext() {
      return this._browserField.getContext();
   }

   @Override
   public final boolean submit(boolean validate, IBrowserContext context) {
      if (!(context instanceof HTMLContext)) {
         return false;
      }

      HTMLContext htmlContext = (HTMLContext)context;
      Enumeration elements = this._document.getAllElementsEnumeration();

      while (elements.hasMoreElements()) {
         HTMLNode element = (HTMLNode)elements.nextElement();
         if (element instanceof HTMLInput) {
            ((HTMLInput)element).submit(htmlContext);
         }
      }

      return true;
   }

   @Override
   public final void finishLoading() {
      super.finishLoading();
      this.startTimer();
   }

   public HTMLBrowserContent(
      String referrer,
      String host,
      Renderer renderer,
      String url,
      RenderingApplication renderingApplication,
      RenderingOptions renderingOptions,
      Frame frame,
      int flags
   ) {
      super(renderer, url, null, renderingApplication, renderingOptions, flags);
      this._javascriptCallbacks = (Hashtable)(new Object(0));
      this._document = new HTMLDocumentImpl(referrer, host, this);
      this._frame = frame;
   }

   @Override
   public final void resourceRequested(RequestedResource resource) {
      if (resource != null) {
         this._requestedResources.addElement(resource.getUrl());
      }
   }

   @Override
   public final String toString() {
      if (this._inlinedData != null && this._requestedResources != null) {
         StringBuffer buffer = (StringBuffer)(new Object());
         int size = this._inlinedData.size();
         if ((super._flags & 4096) != 0) {
            buffer.append(" - Cached content - \n");
         }

         buffer.append("Requested ");
         buffer.append(this._requestedResources.size());
         buffer.append(" resource(s) \n");

         for (int i = 0; i < this._requestedResources.size(); i++) {
            buffer.append(this._requestedResources.elementAt(i));
            buffer.append("\n");
         }

         buffer.append("Missing images: ");
         buffer.append(super._enquedImages);
         buffer.append('\n');
         buffer.append("Inline data count: ");
         buffer.append(size);
         buffer.append('\n');

         for (int i = 0; i < size; i++) {
            InlinedItem item = (InlinedItem)this._inlinedData.elementAt(i);
            buffer.append(item._status);
            buffer.append(" - ");
            buffer.append(item._url);
            buffer.append("\n");
         }

         return buffer.toString();
      } else {
         return super.toString();
      }
   }

   @Override
   public final void resourceReady(RequestedResource resource) {
      JavaScriptResourceCallback callback = (JavaScriptResourceCallback)this._javascriptCallbacks.get(resource);
      if (callback != null) {
         this._javascriptCallbacks.remove(resource);
         callback.resourceReady(resource);
      } else {
         super.resourceReady(resource);
      }
   }

   @Override
   public final Document getDOMDocument() {
      return this._document;
   }

   @Override
   public final void setContent(Field content) {
      if (!(content instanceof HTMLBrowserField)) {
         throw new Object("field must be HTMLBrowserField");
      }

      super.setContent(content);
      this._browserField = (HTMLBrowserField)content;
      this._tfmFindManager = new TFMFindManager(this._browserField);
   }

   @Override
   public final void setTitle(String title) {
      super.setTitle(title);
      if (super._renderingApplication != null) {
         BrowserContentChangedEvent event = (BrowserContentChangedEvent)(new Object(this));
         super._renderingApplication.eventOccurred(event);
      }
   }

   @Override
   public final void setIcon(EncodedImage icon) {
      super.setIcon(icon);
      if (super._renderingApplication != null) {
         BrowserContentChangedEvent event = (BrowserContentChangedEvent)(new Object(this));
         super._renderingApplication.eventOccurred(event);
      }
   }

   static final RenderingApplication access$000(HTMLBrowserContent x0) {
      return x0._renderingApplication;
   }

   static final RenderingApplication access$100(HTMLBrowserContent x0) {
      return x0._renderingApplication;
   }
}
