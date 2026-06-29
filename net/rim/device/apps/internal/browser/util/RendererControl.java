package net.rim.device.apps.internal.browser.util;

import java.io.DataInput;
import java.io.InputStream;
import javax.microedition.io.ContentConnection;
import javax.microedition.io.HttpConnection;
import javax.microedition.io.InputConnection;
import javax.microedition.io.file.FileConnection;
import net.rim.device.api.browser.field.BrowserContent;
import net.rim.device.api.browser.field.ContentReadEvent;
import net.rim.device.api.browser.field.Event;
import net.rim.device.api.browser.field.RedirectEvent;
import net.rim.device.api.browser.field.RenderingApplication;
import net.rim.device.api.browser.field.RenderingException;
import net.rim.device.api.browser.field.RenderingSession;
import net.rim.device.api.browser.plugin.BrowserContentProviderContext;
import net.rim.device.api.compress.GZIPInputStream;
import net.rim.device.api.compress.ZLibInputStream;
import net.rim.device.api.io.MIMETypeAssociations;
import net.rim.device.api.io.http.HttpHeaders;
import net.rim.device.api.math.Fixed32;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.util.StringTokenizer;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.api.framework.model.URLProvider;
import net.rim.device.apps.api.utility.general.URI;
import net.rim.device.apps.api.utility.serialization.Converter;
import net.rim.device.apps.api.utility.serialization.SerializationManager;
import net.rim.device.apps.internal.browser.common.ResourcedRenderingException;
import net.rim.device.apps.internal.browser.core.BrowserContentProviderRenderingContext;
import net.rim.device.apps.internal.browser.resources.BrowserResources;
import net.rim.device.apps.internal.browser.stack.RTSPConnection;
import net.rim.device.cldc.io.http.HttpProtocolBase;
import net.rim.vm.Array;

public final class RendererControl {
   public static final String CONTENT_TYPE_HEADER = "content-type";
   public static final int MAX_REDIRECT = 10;
   public static final int MIME_MAGIC_BUFFER_SIZE = 512;
   private static final String[] OFFLINE_QUEUE_VALUES = new String[]{
      "x-rim-queue-id", "x-rim-request-id", "x-rim-request-date", "x-rim-request-title", "x-rim-next-target"
   };
   private static final String OPENWAVE_REDIRECT_PREFIX = "uplink:///goto?url=";
   private static final int STATE_NO_MATCH = 0;
   private static final int STATE_LESSTHAN = 1;
   private static final int STATE_LESSTHAN_H = 2;
   private static final int STATE_LESSTHAN_HT = 3;
   private static final int STATE_LESSTHAN_HTM = 4;
   private static final int STATE_LESSTHAN_W = 12;
   private static final int STATE_LESSTHAN_WM = 13;

   public static final String getStatusDescription(int statusCode) {
      try {
         return BrowserResources.getHttpString(statusCode);
      } finally {
         return BrowserResources.getString(614);
      }
   }

   private static final int getStatusMessageResourceId(int code) {
      switch (code) {
         case 400:
            return 585;
         case 401:
            return 554;
         case 402:
            return 567;
         case 403:
            return 554;
         case 404:
            return 569;
         case 405:
            return 567;
         case 406:
            return 571;
         case 407:
            return 572;
         case 408:
            return 573;
         case 409:
            return 567;
         case 410:
            return 575;
         case 411:
         case 412:
         case 413:
         case 414:
         case 415:
         case 416:
         case 417:
            return 567;
         case 500:
            return 583;
         case 501:
            return 584;
         case 502:
            return 10;
         case 503:
            return 11;
         case 504:
            return 12;
         case 505:
            return 13;
         default:
            return -1;
      }
   }

   public static final int fixed32DivToInt(int iPos, int fScale) {
      if (fScale == 65536) {
         return iPos;
      }

      if (iPos <= 32767) {
         return Fixed32.divtoInt(Fixed32.toFP(iPos), fScale);
      }

      float floatingScale = Fixed32.toIntTenThou(fScale) / 1176256512;
      return (int)(iPos / floatingScale);
   }

   public static final int fixed32MultToInt(int iPos, int fScale) {
      return fScale == 65536 ? iPos : iPos * Fixed32.toIntTenThou(fScale) / 10000;
   }

   public static final String getStatusMessage(int statusCode) {
      String message = null;
      int resource_id = getStatusMessageResourceId(statusCode);
      if (resource_id != -1) {
         message = BrowserResources.getString(resource_id);
      }

      return message;
   }

   public static final String getErrorCode(int statusCode) {
      if (statusCode >= 200 && statusCode < 400) {
         return null;
      }

      StringBuffer error = new StringBuffer(BrowserResources.getString(264));
      error.append(statusCode);
      error.append(':');
      error.append(' ');
      String status = getStatusDescription(statusCode);
      if (status != null) {
         error.append(status);
         status = getStatusMessage(statusCode);
         if (status != null) {
            error.append('\n');
            error.append('\n');
            error.append(status);
         }
      }

      return error.toString();
   }

   public static final String getRedirectURL(HttpConnection httpConnection) {
      int statusCode = httpConnection.getResponseCode();
      if (statusCode >= 300 && statusCode < 400) {
         String location = httpConnection.getHeaderField("Location");
         if (location == null || location.length() == 0) {
            location = httpConnection.getHeaderField("Content-Location");
         }

         if (location != null && location.length() != 0) {
            if (location.startsWith("uplink:///goto?url=")) {
               location = location.substring(19);
               if (!location.startsWith("http://") && !location.startsWith("https://")) {
                  location = "http://" + location;
               }
            }

            return URI.getAbsoluteURL(location, httpConnection.getURL());
         }
      }

      return null;
   }

   private RendererControl() {
   }

   // $VF: Could not inline inconsistent finally blocks
   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public static final BrowserContent renderBrowserContent(
      RenderingSession renderingSession,
      InputConnection connection,
      InputStream in,
      String url,
      RenderingApplication renderingApplication,
      int flags,
      Event event,
      Object context,
      Frame target
   ) throws RenderingException, ResourcedRenderingException {
      if (connection == null) {
         return null;
      }

      String errorString = null;
      String contentType = null;
      if (connection instanceof HttpConnection) {
         HttpConnection httpConnection = (HttpConnection)connection;
         int statusCode = 0;
         boolean var43 = false /* VF: Semaphore variable */;

         try {
            var43 = true;
            statusCode = httpConnection.getResponseCode();
            var43 = false;
         } finally {
            if (var43) {
               throw new RenderingException("IOException in connection");
            }
         }

         if (statusCode >= 300 && statusCode < 400) {
            String location = null;
            boolean var37 = false /* VF: Semaphore variable */;

            try {
               var37 = true;
               location = getRedirectURL(httpConnection);
               var37 = false;
            } finally {
               if (var37) {
                  throw new RenderingException("IOException in connection");
               }
            }

            if (location != null && renderingApplication != null) {
               Event originalEvent = event;

               for (int numRedirects = 0; originalEvent instanceof RedirectEvent; numRedirects++) {
                  if (numRedirects >= 10) {
                     throw new RenderingException(BrowserResources.getString(215) + location);
                  }

                  originalEvent = ((RedirectEvent)originalEvent).getOriginalEvent();
               }

               RedirectEvent redirectEvent = new RedirectEvent(httpConnection, location, event, 0);
               renderingApplication.eventOccurred(redirectEvent);
               return null;
            }
         } else if (statusCode < 200 || statusCode >= 400) {
            errorString = getErrorCode(statusCode);
         }

         url = httpConnection.getURL();
      }

      contentType = getContentType(connection);
      if ((contentType == null || contentType.length() == 0) && in != null && in.markSupported()) {
         boolean var31 = false /* VF: Semaphore variable */;

         try {
            var31 = true;
            in.mark(512);
            byte[] var50 = new byte[512];
            int var53 = 0;

            do {
               int newBytesRead = in.read(var50, var53, 512 - var53);
               if (newBytesRead < 0) {
                  break;
               }

               var53 += newBytesRead;
            } while (var53 < 512);

            contentType = guessContentType(var50, 0, var53);
            in.reset();
            var31 = false;
         } finally {
            if (var31) {
               throw new RenderingException(BrowserResources.getString(234));
            }
         }
      }

      Converter converter = null;
      if (contentType != null && contentType.length() > 0) {
         String lcContentType = StringUtilities.toLowerCase(contentType, 1701707776);
         converter = SerializationManager.getConverter(lcContentType, "net.rim.device.apps.internal.rendering");
         if (converter == null) {
            String aliasContentType = MIMETypeAssociations.getNormalizedType(lcContentType);
            if (aliasContentType != null && !lcContentType.equals(aliasContentType)) {
               converter = SerializationManager.getConverter(
                  StringUtilities.toLowerCase(aliasContentType, 1701707776), "net.rim.device.apps.internal.rendering"
               );
            }
         }
      }

      if (converter == null && url != null) {
         int colonIndex = url.indexOf(58);
         if (colonIndex != -1) {
            String scheme = StringUtilities.toLowerCase(url.substring(0, colonIndex), 1701707776);
            converter = SerializationManager.getConverter(scheme, "net.rim.device.apps.internal.rendering");
         }
      }

      if (converter != null) {
         BrowserContentProviderRenderingContext convert = new BrowserContentProviderRenderingContext(
            connection, in, renderingApplication, renderingSession, flags, event, context, target
         );
         BrowserContent browserContent = null;

         try {
            browserContent = (BrowserContent)converter.convert((DataInput)null, convert);
         } catch (Throwable var44) {
            throw new RenderingException(se.getMessage());
         }

         if (browserContent != null && errorString != null) {
            browserContent.setError(errorString);
         }

         return browserContent;
      } else {
         if (contentType != null && contentType.length() != 0) {
            throw new ResourcedRenderingException(265, contentType);
         }

         if (errorString != null) {
            throw new RenderingException(errorString);
         }

         if (connection instanceof HttpConnection) {
            HttpConnection httpConnection = (HttpConnection)connection;

            try {
               EventLogger.logEvent(1907089860548946979L, ("No Content-Type on " + httpConnection.getResponseCode()).getBytes(), 3);
               EventLogger.logEvent(
                  1907089860548946979L,
                  ("type=" + httpConnection.getType() + ", length=" + httpConnection.getLength() + ", date=" + httpConnection.getDate()).getBytes()
               );
            } finally {
               throw new RenderingException(BrowserResources.getString(216));
            }
         }

         throw new RenderingException(BrowserResources.getString(216));
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public static final BrowserContent renderBrowserContent(String mimeType, BrowserContentProviderContext convert) throws RenderingException {
      Converter converter = SerializationManager.getConverter(mimeType, "net.rim.device.apps.internal.rendering");
      if (converter == null) {
         EventLogger.logEvent(1907089860548946979L, ("No converter for " + mimeType).getBytes(), 3);
         throw new RenderingException(BrowserResources.getString(216));
      }

      BrowserContent browserContent = null;

      try {
         return (BrowserContent)converter.convert((DataInput)null, convert);
      } catch (Throwable var6) {
         throw new RenderingException(se.getMessage());
      }
   }

   public static final void setContentType(HttpHeaders responseHeaders, String type) {
      if (responseHeaders != null) {
         responseHeaders.setProperty("content-type", type);
      }
   }

   public static final byte[] readBytesFromInputStream(InputStream in) {
      return readBytesFromInputStream(in, null, null, -1);
   }

   public static final byte[] readBytesFromInputStream(InputStream in, ContentReadEvent event, RenderingApplication app, int contentLength) {
      if (in == null) {
         return null;
      }

      if (app == null && event != null) {
         event = null;
      }

      CountedInputStream countedInputStream = null;
      if (in instanceof CountedInputStream) {
         countedInputStream = (CountedInputStream)in;
      }

      try {
         byte[] data = new byte[contentLength > 0 ? contentLength : 0];
         int offset = 0;

         while (true) {
            int available = in.available();
            if (available >= 0) {
               int toRead = Math.max(available, 1024);
               if (offset + toRead > data.length) {
                  Array.resize(data, offset + toRead);
               }

               int bytesRead = in.read(data, offset, toRead);
               if (bytesRead < 0) {
                  break;
               }

               offset += bytesRead;
               if (event != null) {
                  event.setItemsRead(countedInputStream != null ? countedInputStream.getCompressedBytesRead() : offset);
                  app.eventOccurred(event);
               }
            } else if (available == -1) {
               break;
            }
         }

         in.close();
         Array.resize(data, offset);
         return data;
      } finally {
         ;
      }
   }

   public static final String getCharacterEncoding(InputConnection conn) {
      try {
         if (conn instanceof HttpConnection) {
            return getCharacterEncoding(((HttpConnection)conn).getHeaderField("content-type"));
         }
      } finally {
         return null;
      }

      return null;
   }

   public static final String getCharacterEncoding(String contentType) {
      String characterEncoding = null;
      if (contentType != null) {
         int semicolon = contentType.indexOf(59);
         if (semicolon >= 0) {
            StringTokenizer tokenizer = new StringTokenizer(contentType.substring(semicolon + 1), ';');
            String charsetPrefix = "charset=";

            while (tokenizer.hasMoreTokens()) {
               String parameter = tokenizer.nextToken().trim();
               if (StringUtilities.startsWithIgnoreCase(parameter, charsetPrefix, 1701707776)) {
                  characterEncoding = parameter.substring(charsetPrefix.length());
                  if (characterEncoding.length() > 1 && characterEncoding.charAt(0) == '"') {
                     return characterEncoding.substring(1, characterEncoding.length() - 1);
                  }
                  break;
               }
            }
         }
      }

      return characterEncoding;
   }

   public static final String getContentType(InputConnection conn) {
      String contentType = null;
      if (!(conn instanceof ContentConnection)) {
         if (conn instanceof FileConnection) {
            FileConnection fileConn = (FileConnection)conn;
            String url = fileConn.getURL();
            if (url != null) {
               contentType = MIMETypeAssociations.getMIMEType(url);
            }
         }
      } else {
         ContentConnection httpConn = (ContentConnection)conn;
         contentType = httpConn.getType();
         if (contentType != null) {
            int paramStartIndex = contentType.indexOf(59);
            if (paramStartIndex != -1) {
               contentType = contentType.substring(0, paramStartIndex);
            }

            paramStartIndex = contentType.indexOf(44);
            if (paramStartIndex != -1) {
               return contentType.substring(0, paramStartIndex);
            }
         }
      }

      return contentType;
   }

   public static final long getContentLength(InputConnection conn) {
      if (!(conn instanceof ContentConnection)) {
         if (!(conn instanceof FileConnection)) {
            return 0;
         }

         FileConnection fileConn = (FileConnection)conn;
         return fileConn.fileSize();
      } else {
         ContentConnection httpConn = (ContentConnection)conn;
         return httpConn.getLength();
      }
   }

   public static final String getUrl(InputConnection inputConnection) {
      if (!(inputConnection instanceof HttpProtocolBase)) {
         if (!(inputConnection instanceof HttpConnection)) {
            if (!(inputConnection instanceof FileConnection)) {
               if (!(inputConnection instanceof RTSPConnection)) {
                  return !(inputConnection instanceof URLProvider) ? null : ((URLProvider)inputConnection).getURL();
               } else {
                  return ((RTSPConnection)inputConnection).getURL();
               }
            } else {
               return ((FileConnection)inputConnection).getURL();
            }
         } else {
            return ((HttpConnection)inputConnection).getURL();
         }
      } else {
         return ((HttpProtocolBase)inputConnection).getURLWithoutRIMParams();
      }
   }

   public static final String stripContentTypeParameters(String contentType) {
      if (contentType != null) {
         int semicolon = contentType.indexOf(59);
         if (semicolon != -1) {
            contentType = contentType.substring(0, semicolon).trim();
         }
      }

      return contentType;
   }

   public static final boolean isOfflineQueueHeader(String name) {
      if (name != null && StringUtilities.regionMatches(name, true, 0, "x-rim", 0, 5, 1701707776)) {
         for (int i = OFFLINE_QUEUE_VALUES.length - 1; i >= 0; i--) {
            if (StringUtilities.strEqualIgnoreCase(name, OFFLINE_QUEUE_VALUES[i], 1701707776)) {
               return true;
            }
         }
      }

      return false;
   }

   public static final HttpHeaders getOfflineQueueHeaders(InputConnection connection) {
      HttpHeaders offlineParameters = null;
      if (connection instanceof HttpConnection) {
         HttpConnection httpConnection = (HttpConnection)connection;

         try {
            for (int i = OFFLINE_QUEUE_VALUES.length - 1; i >= 0; i--) {
               String newValue = httpConnection.getHeaderField(OFFLINE_QUEUE_VALUES[i]);
               if (newValue != null) {
                  if (offlineParameters == null) {
                     offlineParameters = new HttpHeaders();
                  }

                  offlineParameters.setProperty(OFFLINE_QUEUE_VALUES[i], newValue);
               }
            }
         } finally {
            return offlineParameters;
         }
      }

      return offlineParameters;
   }

   public static final int valueAsPixels(String value, int defaultValue) {
      if (value == null) {
         return defaultValue;
      }

      int result = valueAsInt(value, defaultValue);
      if (result != defaultValue) {
         return result;
      }

      int index = value.indexOf("px");
      if (index >= 0) {
         label223:
         try {
            return Integer.parseInt(value.substring(0, index));
         } finally {
            break label223;
         }
      }

      index = value.indexOf("pt");
      if (index >= 0) {
         label218:
         try {
            return Ui.convertSize(Integer.parseInt(value.substring(0, index)), 2, 0);
         } finally {
            break label218;
         }
      }

      index = value.indexOf("cm");
      if (index >= 0) {
         label213:
         try {
            return Ui.convertSize(Integer.parseInt(value.substring(0, index)), 4194308, 0);
         } finally {
            break label213;
         }
      }

      index = value.indexOf("mm");
      if (index >= 0) {
         label208:
         try {
            return Ui.convertSize(Integer.parseInt(value.substring(0, index)), 2097156, 0);
         } finally {
            break label208;
         }
      }

      index = value.indexOf("in");
      if (index >= 0) {
         try {
            return Ui.convertSize(Integer.parseInt(value.substring(0, index)) * 5, 4194308, 0) / 2;
         } finally {
            return defaultValue;
         }
      } else {
         return defaultValue;
      }
   }

   public static final int valueAsInt(String value, int defaultValue) {
      if (value == null) {
         return defaultValue;
      }

      try {
         return Integer.parseInt(value);
      } finally {
         ;
      }
   }

   public static final InputStream getInputStreamFromContentEncoding(InputConnection conn, InputStream in) {
      return getInputStreamFromContentEncoding(conn, in, false);
   }

   public static final InputStream getInputStreamFromContentEncoding(InputConnection conn, InputStream in, boolean compressedByteCountNeeded) {
      if (!(conn instanceof HttpConnection)) {
         return in;
      }

      HttpConnection httpConn = (HttpConnection)conn;
      return getInputStreamFromContentEncoding(httpConn.getHeaderField("Content-Encoding"), in, compressedByteCountNeeded);
   }

   public static final InputStream getInputStreamFromContentEncoding(String contentEncoding, InputStream in) {
      return getInputStreamFromContentEncoding(contentEncoding, in, false);
   }

   public static final InputStream getInputStreamFromContentEncoding(String contentEncoding, InputStream in, boolean compressedByteCountNeeded) {
      if (contentEncoding != null) {
         if (StringUtilities.strEqualIgnoreCase(contentEncoding, "gzip", 1701707776)) {
            if (in instanceof GZIPInputStream) {
               return in;
            }

            if (compressedByteCountNeeded) {
               CountedInputStream compressedInputStream = new CountedInputStream(in);
               return new CountedInputStream(new GZIPInputStream(compressedInputStream), compressedInputStream);
            }

            return new GZIPInputStream(in);
         }

         if (StringUtilities.startsWithIgnoreCase(contentEncoding, "deflate", 1701707776)) {
            if (in instanceof ZLibInputStream) {
               return in;
            }

            boolean rawDeflate = true;
            if (in.markSupported()) {
               label65:
               try {
                  in.mark(2);
                  int byte0 = in.read();
                  int byte1 = in.read();
                  if ((byte0 & 15) == 8) {
                     int header = (byte0 << 8) + byte1;
                     if (header % 31 == 0) {
                        rawDeflate = false;
                     }
                  }

                  in.reset();
               } finally {
                  break label65;
               }
            }

            if (compressedByteCountNeeded) {
               CountedInputStream compressedInputStream = new CountedInputStream(in);
               return new CountedInputStream(new ZLibInputStream(compressedInputStream, rawDeflate), compressedInputStream);
            }

            return new ZLibInputStream(in, rawDeflate);
         }
      }

      return in;
   }

   private static final String guessContentType(byte[] data, int offset, int length) {
      if (data != null) {
         if (length >= 3 && data[offset] == 71 && data[offset + 1] == 73 && data[offset + 2] == 70) {
            return "image/gif";
         }

         if (length >= 2 && (data[offset] & 255) == 255 && (data[offset + 1] & 255) == 216) {
            return "image/jpeg";
         }

         int state = 0;

         for (int i = offset; i < offset + length; i++) {
            byte ch = data[i];
            switch (state) {
               case 0:
                  if (ch == 60) {
                     state = 1;
                  }
                  break;
               case 1:
                  switch (ch) {
                     case 72:
                     case 104:
                        state = 2;
                        continue;
                     case 87:
                     case 119:
                        state = 12;
                        continue;
                     default:
                        state = 0;
                        continue;
                  }
               case 2:
                  switch (ch) {
                     case 84:
                     case 116:
                        state = 3;
                        continue;
                     default:
                        state = 0;
                        continue;
                  }
               case 3:
                  switch (ch) {
                     case 77:
                     case 109:
                        state = 4;
                        continue;
                     default:
                        state = 0;
                        continue;
                  }
               case 4:
                  switch (ch) {
                     case 76:
                     case 108:
                        return "text/html";
                     default:
                        state = 0;
                        continue;
                  }
               case 12:
                  switch (ch) {
                     case 77:
                     case 109:
                        state = 13;
                        continue;
                     default:
                        state = 0;
                        continue;
                  }
               case 13:
                  switch (ch) {
                     case 76:
                     case 108:
                        return "text/vnd.wap.wml";
                     default:
                        state = 0;
                  }
            }
         }
      }

      return null;
   }
}
