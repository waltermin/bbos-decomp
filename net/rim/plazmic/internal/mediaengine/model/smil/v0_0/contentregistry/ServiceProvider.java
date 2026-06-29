package net.rim.plazmic.internal.mediaengine.model.smil.v0_0.contentregistry;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Hashtable;
import javax.microedition.io.HttpConnection;

public class ServiceProvider {
   private Hashtable _services;
   private String _drmStatus;
   public static final String PLAYER = "Player";
   public static final String UI_COMPONENT = "UI_Component";
   public static final String MODEL = "Model";
   public static final String URI = "Uri";
   private static final int BUFFER_SIZE = 2048;

   protected ServiceProvider(int numServices) {
      this._services = (Hashtable)(new Object(numServices));
   }

   public final Object getService(String serviceId) {
      return this._services.get(serviceId);
   }

   protected final void setService(String serviceId, Object service) {
      this._services.put(serviceId, service);
   }

   public void createServices(InputStream _1, String _2, HttpConnection _3) {
      throw null;
   }

   protected ByteArrayOutputStream bufferData(InputStream is) {
      ByteArrayOutputStream os = (ByteArrayOutputStream)(new Object());
      byte[] buffer = new byte[2048];
      int read = 0;

      while ((read = is.read(buffer)) != -1) {
         os.write(buffer, 0, read);
      }

      return os;
   }

   protected void setDrmStatus(HttpConnection conn) {
      this._drmStatus = conn.getRequestProperty("drm-status");
   }

   protected boolean isForwardLocked() {
      boolean result = false;

      try {
         return Integer.parseInt(this._drmStatus) == 2048;
      } finally {
         ;
      }
   }
}
