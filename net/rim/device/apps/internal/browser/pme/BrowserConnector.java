package net.rim.device.apps.internal.browser.pme;

import java.io.InputStream;
import javax.microedition.io.HttpConnection;
import net.rim.device.api.browser.field.RenderingApplication;
import net.rim.device.api.browser.field.RequestedResource;
import net.rim.device.api.io.http.HttpHeaders;
import net.rim.device.apps.internal.browser.common.RenderingUtilities;
import net.rim.plazmic.mediaengine.io.ConnectionInfo;
import net.rim.plazmic.mediaengine.io.Connector;

final class BrowserConnector implements Connector {
   private HttpConnection _httpConnection;
   private RenderingApplication _renderingApplication;
   private int _flags;

   public BrowserConnector(RenderingApplication app, int flags) {
      this._renderingApplication = app;
      this._flags = flags;
   }

   @Override
   public final void releaseConnection(ConnectionInfo info) {
      if (this._httpConnection != null) {
         try {
            this._httpConnection.close();
         } finally {
            return;
         }
      }
   }

   @Override
   public final void setProperty(String name, String value) {
   }

   @Override
   public final InputStream getInputStream(String url, ConnectionInfo info) {
      if (this._renderingApplication == null) {
         return null;
      }

      try {
         int flags = this._flags & 7;
         HttpHeaders requestHeaders = (HttpHeaders)(new Object());
         RenderingUtilities.setTranscodeHeader(requestHeaders, false);
         RequestedResource resourceInfo = (RequestedResource)(new Object(url, requestHeaders, flags));
         this._httpConnection = this._renderingApplication.getResource(resourceInfo, null);
         if (this._httpConnection != null) {
            int statusCode = this._httpConnection.getResponseCode();
            if (statusCode >= 200 && statusCode < 300) {
               info.setContentType(this._httpConnection.getType());
               info.setLength(this._httpConnection.getLength());
               return this._httpConnection.openInputStream();
            } else {
               throw new Object();
            }
         } else {
            throw new Object(5);
         }
      } finally {
         ;
      }
   }
}
