package net.rim.device.apps.internal.browser.core;

import java.io.InputStream;
import javax.microedition.io.InputConnection;
import net.rim.device.api.browser.field.Event;
import net.rim.device.api.browser.field.RenderingApplication;
import net.rim.device.api.browser.field.RenderingSession;
import net.rim.device.api.browser.plugin.BrowserContentProviderContext;
import net.rim.device.apps.internal.browser.util.Frame;

public final class BrowserContentProviderRenderingContext extends BrowserContentProviderContext {
   private Object _context;
   private Frame _target;

   public BrowserContentProviderRenderingContext(
      InputConnection inputConnection,
      InputStream in,
      RenderingApplication renderingApplication,
      RenderingSession renderingSession,
      int flags,
      Event event,
      Object context,
      Frame target
   ) {
      super(inputConnection, in, renderingApplication, renderingSession, flags, event);
      this._context = context;
      this._target = target;
   }

   public final Object getContext() {
      return this._context;
   }

   public final Frame getTarget() {
      return this._target;
   }
}
