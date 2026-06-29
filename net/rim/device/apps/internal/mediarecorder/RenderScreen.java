package net.rim.device.apps.internal.mediarecorder;

import javax.microedition.io.HttpConnection;
import javax.microedition.io.InputConnection;
import net.rim.device.api.browser.field.BrowserContent;
import net.rim.device.api.browser.field.Event;
import net.rim.device.api.browser.field.RenderingApplication;
import net.rim.device.api.browser.field.RenderingOptions;
import net.rim.device.api.browser.field.RenderingSession;
import net.rim.device.api.browser.field.RequestedResource;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.theme.Tag;
import net.rim.device.apps.api.ribbon.RibbonBanner;
import net.rim.device.apps.api.ui.AppsMainScreen;
import net.rim.device.internal.system.ActiveMedia;
import net.rim.device.internal.system.ActiveMediaObservable;

public final class RenderScreen extends AppsMainScreen implements RenderingApplication, ActiveMedia {
   private UiApplication _uiApp;
   private BrowserContent _content;
   private Field _field;
   private Field _banner;
   private static final Tag EXPLORER_BANNER_TAG = Tag.create("explorer-banner");

   public RenderScreen(long style) {
      super(style);
   }

   public final void init(InputConnection inputConnection) {
      this._banner = RibbonBanner.getInstance().getStatusBanner("", 3);
      this._banner.setTag(EXPLORER_BANNER_TAG);
      this.getDelegate().add(this._banner);
      this._field = this.createFieldForItem(inputConnection);
      if (this._field == null) {
         this._field = this.createRenderErrorField();
      }

      this.getDelegate().add(this._field);
   }

   private final Field createFieldForItem(InputConnection input) {
      try {
         if (input == null) {
            return null;
         }

         RenderingSession sess = RenderingSession.getNewInstance();
         RenderingOptions options = sess.getRenderingOptions();
         options.setProperty(4550690918222697397L, 41, false);
         options.setProperty(4550690918222697397L, 26, true);
         options.setProperty(4550690918222697397L, 43, 2);
         options.setProperty(4550690918222697397L, 40, true);
         this._content = sess.getBrowserContent(input, "", this, 0);
         if (this._content != null) {
            Thread finishLoading = new RenderScreen$1(this);
            finishLoading.start();
            this._field = this._content.getDisplayableContent();
            return this._field;
         }
      } finally {
         return null;
      }

      return null;
   }

   public final void doModal() {
      this._uiApp = UiApplication.getUiApplication();
      this._uiApp.pushModalScreen(this);
   }

   final void loadContent() {
   }

   private final Field createRenderErrorField() {
      ResourceBundle rb = ResourceBundle.getBundle(-6927834585541129670L, "net.rim.device.apps.internal.resource.MediaRecorder");
      return new RichTextField(rb.getString(13), 36028797018963968L);
   }

   public final int getContentWindowWidth() {
      return Display.getWidth();
   }

   public final int getContentWindowHeight() {
      int height = Display.getHeight();
      if (this._banner != null) {
         height -= this._banner.getPreferredHeight();
      }

      return height;
   }

   @Override
   protected final void sublayout(int width, int height) {
      this.setPosition(0, 0);
      this.setExtent(width, height);
      this.setPositionDelegate(0, 0);
      this.layoutDelegate(width, height);
   }

   @Override
   public final void close() {
      this._uiApp = null;
      super.close();
   }

   @Override
   protected final void onVisibilityChange(boolean visible) {
      if (visible) {
         ActiveMediaObservable.setActive(this);
      } else {
         ActiveMediaObservable.setInactive(this);
      }

      super.onVisibilityChange(visible);
   }

   @Override
   public final boolean isAudioInUse() {
      return true;
   }

   @Override
   public final int codecUsed() {
      return -1;
   }

   @Override
   public final boolean isForce() {
      return true;
   }

   @Override
   public final boolean isAlert() {
      return false;
   }

   @Override
   public final Object eventOccurred(Event event) {
      return null;
   }

   @Override
   public final String getHTTPCookie(String url) {
      return null;
   }

   @Override
   public final HttpConnection getResource(RequestedResource resource, BrowserContent referrer) {
      return null;
   }

   @Override
   public final int getAvailableHeight(BrowserContent browserContent) {
      return this.getContentWindowHeight();
   }

   @Override
   public final int getAvailableWidth(BrowserContent browserContent) {
      return this.getContentWindowWidth();
   }

   @Override
   public final int getHistoryPosition(BrowserContent browserContent) {
      return -1;
   }

   @Override
   public final void invokeRunnable(Runnable runnable) {
      new RenderScreen$2(this, runnable).start();
   }
}
