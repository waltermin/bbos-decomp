package net.rim.device.api.browser.field;

import java.util.Vector;
import javax.microedition.io.HttpConnection;
import javax.microedition.io.InputConnection;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.util.Factory;

public class RenderingSession {
   protected RenderingOptions _renderingOptions = new RenderingOptions();
   public static final long APP_REGISTRY_KEY;

   public static RenderingSession getNewInstance() {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      Factory factory = (Factory)ar.get(6405613126867753457L);
      synchronized (ar) {
         return factory != null ? (RenderingSession)factory.createInstance(null) : null;
      }
   }

   protected RenderingSession() {
   }

   public String getAcceptTypes() {
      throw null;
   }

   public Vector getSupportedMimeType() {
      throw null;
   }

   public RenderingOptions getRenderingOptions() {
      throw null;
   }

   public String getAcceptCharsetValues() {
      throw null;
   }

   public Object getContext() {
      throw null;
   }

   public BrowserContent getBrowserContent(HttpConnection _1, RenderingApplication _2, int _3) {
      throw null;
   }

   public BrowserContent getBrowserContent(HttpConnection _1, RenderingApplication _2, Event _3) {
      throw null;
   }

   public BrowserContent getBrowserContent(InputConnection connection, String url, RenderingApplication renderingApplication, int flags) {
      if (connection instanceof Object) {
         return this.getBrowserContent((HttpConnection)connection, renderingApplication, flags);
      } else {
         throw new RenderingException();
      }
   }

   public BrowserContent getBrowserContent(InputConnection connection, String url, RenderingApplication renderingApplication, Event event) {
      if (connection instanceof Object) {
         return this.getBrowserContent((HttpConnection)connection, renderingApplication, event);
      } else {
         throw new RenderingException();
      }
   }
}
