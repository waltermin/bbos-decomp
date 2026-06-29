package net.rim.device.apps.internal.mms.service;

import java.io.InputStream;
import java.io.OutputStream;
import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import net.rim.device.api.io.http.HttpHeaders;
import net.rim.device.apps.internal.browser.util.RendererControl;
import net.rim.device.apps.internal.mms.options.MMSOptions;

final class HttpSender {
   private String _url;
   private byte[] _data;
   private HttpHeaders _headers;
   private int _responseCode;
   private String _responseType;
   private byte[] _responseData;

   public HttpSender(String url, byte[] data) {
      this(url, data, MMSHttpUtilities.getStandardSendHeaders(data.length));
   }

   public HttpSender(String url, byte[] data, HttpHeaders headers) {
      this._url = url;
      this._data = data;
      this._headers = headers;
   }

   public final int getResponseCode() {
      return this._responseCode;
   }

   public final String getResponseType() {
      return this._responseType;
   }

   public final byte[] getResponseData() {
      return this._responseData;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public final int send() {
      HttpConnection connection = null;
      OutputStream os = null;
      InputStream aDataInput = null;
      System.out.println("MMS send 0x" + Integer.toHexString(this._data[1] & 255) + " (" + this._data.length + " bytes)");
      if (MMSOptions.getInstance().getOptionFlag(256)) {
         Debug.dumpBytes(this._data);
      }

      boolean var57 = false /* VF: Semaphore variable */;

      int var65;
      try {
         var57 = true;
         connection = (HttpConnection)Connector.open(this._url);
         MMSHttpUtilities.setRequestProperties(connection, this._headers);
         connection.setRequestMethod("POST");
         os = connection.openOutputStream();
         os.write(this._data);
         os.flush();
         this._responseType = connection.getHeaderField("content-type");
         if (this._responseType != null) {
            System.out.println("MMS send response type = " + this._responseType);
            aDataInput = connection.openInputStream();
            if (aDataInput != null) {
               String contentEncoding = connection.getHeaderField("Content-Encoding");
               aDataInput = RendererControl.getInputStreamFromContentEncoding(contentEncoding, aDataInput);
               this._responseData = new byte[0];
               MMSHttpUtilities.readAll(aDataInput, this._responseData);
               if (MMSOptions.getInstance().getOptionFlag(256)) {
                  Debug.dumpBytes(this._responseData);
               }
            }
         }

         this._responseCode = connection.getResponseCode();
         var65 = this._responseCode;
         var57 = false;
      } finally {
         if (var57) {
            if (aDataInput != null) {
               label344:
               try {
                  aDataInput.close();
               } finally {
                  break label344;
               }
            }

            if (os != null) {
               label340:
               try {
                  os.close();
               } finally {
                  break label340;
               }
            }

            if (connection != null) {
               label336:
               try {
                  connection.close();
               } finally {
                  break label336;
               }
            }
         }
      }

      if (aDataInput != null) {
         label358:
         try {
            aDataInput.close();
         } finally {
            break label358;
         }
      }

      if (os != null) {
         label354:
         try {
            os.close();
         } finally {
            break label354;
         }
      }

      if (connection != null) {
         try {
            connection.close();
         } finally {
            return var65;
         }
      }

      return var65;
   }
}
