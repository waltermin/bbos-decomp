package net.rim.device.apps.internal.mms.service;

import java.io.InputStream;
import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import net.rim.device.api.io.http.HttpHeaders;
import net.rim.device.apps.internal.browser.util.RendererControl;
import net.rim.device.apps.internal.mms.options.MMSOptions;

final class HttpReader {
   private String _url;
   private HttpHeaders _headers;
   private int _responseCode;
   private byte[] _responseData;
   private HttpHeaders _responseHeaders;

   public HttpReader(String url) {
      this(url, MMSHttpUtilities.getStandardRequestHeaders());
   }

   public HttpReader(String url, HttpHeaders headers) {
      this._url = url;
      this._headers = headers;
   }

   public final int getResponseCode() {
      return this._responseCode;
   }

   public final HttpHeaders getResponseHeaders() {
      return this._responseHeaders;
   }

   public final byte[] getResponseData() {
      return this._responseData;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public final int read() {
      HttpConnection connection = null;
      InputStream inputstream = null;
      System.out.println("MMS retrieve: " + this._url);
      boolean var34 = false /* VF: Semaphore variable */;

      int var40;
      try {
         var34 = true;
         connection = (HttpConnection)Connector.open(this._url, 1);
         MMSHttpUtilities.setRequestProperties(connection, this._headers);
         inputstream = connection.openInputStream();
         String contentEncoding = connection.getHeaderField("Content-Encoding");
         inputstream = RendererControl.getInputStreamFromContentEncoding(contentEncoding, inputstream);
         this._responseHeaders = new HttpHeaders();
         int index = 0;

         for (String key = connection.getHeaderFieldKey(index); key != null; key = connection.getHeaderFieldKey(++index)) {
            this._responseHeaders.setProperty(key, connection.getHeaderField(key));
         }

         this._responseData = new byte[0];
         MMSHttpUtilities.readAll(inputstream, this._responseData);
         if (MMSOptions.getInstance().getOptionFlag(256)) {
            Debug.dumpBytes(this._responseData);
         }

         this._responseCode = connection.getResponseCode();
         var40 = this._responseCode;
         var34 = false;
      } finally {
         if (var34) {
            if (inputstream != null) {
               label226:
               try {
                  inputstream.close();
               } finally {
                  break label226;
               }
            }

            if (connection != null) {
               label222:
               try {
                  connection.close();
               } finally {
                  break label222;
               }
            }
         }
      }

      if (inputstream != null) {
         label236:
         try {
            inputstream.close();
         } finally {
            break label236;
         }
      }

      if (connection != null) {
         try {
            connection.close();
         } finally {
            return var40;
         }
      }

      return var40;
   }
}
