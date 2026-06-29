package net.rim.device.apps.internal.browser.core;

import java.util.Vector;
import javax.microedition.io.HttpConnection;
import javax.microedition.io.InputConnection;
import net.rim.device.api.browser.field.BrowserContent;
import net.rim.device.api.browser.field.Event;
import net.rim.device.api.browser.field.RenderingApplication;
import net.rim.device.api.browser.field.RenderingOptions;
import net.rim.device.api.browser.field.RenderingSession;
import net.rim.device.apps.internal.browser.util.RendererControl;
import net.rim.device.apps.internal.browser.wml.WMLContextManager;

public final class RenderingSessionImpl extends RenderingSession {
   private AcceptValueProviderRegistry _acceptValueProviderRegistry;
   private WMLContextManager _context = new WMLContextManager();

   private RenderingSessionImpl() {
      this._acceptValueProviderRegistry = AcceptValueProviderRegistry.getInstance(8967752585940069864L);
   }

   public static final RenderingSession getNewInstance() {
      return new RenderingSessionImpl();
   }

   @Override
   public final RenderingOptions getRenderingOptions() {
      return super._renderingOptions;
   }

   @Override
   public final Object getContext() {
      return this._context;
   }

   @Override
   public final BrowserContent getBrowserContent(HttpConnection connection, RenderingApplication renderingApplication, int flags) {
      return this.getBrowserContent(connection, null, renderingApplication, flags);
   }

   @Override
   public final BrowserContent getBrowserContent(HttpConnection connection, RenderingApplication renderingApplication, Event event) {
      return this.getBrowserContent(connection, null, renderingApplication, event);
   }

   @Override
   public final BrowserContent getBrowserContent(InputConnection connection, String url, RenderingApplication renderingApplication, int flags) {
      return RendererControl.renderBrowserContent(this, connection, null, url, renderingApplication, flags, null, null, null);
   }

   @Override
   public final BrowserContent getBrowserContent(InputConnection connection, String url, RenderingApplication renderingApplication, Event event) {
      return RendererControl.renderBrowserContent(this, connection, null, url, renderingApplication, 0, event, null, null);
   }

   @Override
   public final String getAcceptCharsetValues() {
      return AcceptValueProviderRegistry.getAcceptCharsetValues();
   }

   @Override
   public final String getAcceptTypes() {
      return this._acceptValueProviderRegistry.getAcceptTypes(super._renderingOptions);
   }

   @Override
   public final Vector getSupportedMimeType() {
      return this._acceptValueProviderRegistry.getSupportedMimeType(super._renderingOptions);
   }
}
