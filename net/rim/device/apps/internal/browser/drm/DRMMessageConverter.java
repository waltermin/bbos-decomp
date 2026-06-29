package net.rim.device.apps.internal.browser.drm;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.InputStream;
import javax.microedition.io.ContentConnection;
import javax.microedition.io.HttpConnection;
import javax.microedition.io.InputConnection;
import net.rim.device.api.browser.field.BrowserContent;
import net.rim.device.api.browser.field.ContentReadEvent;
import net.rim.device.api.browser.field.Event;
import net.rim.device.api.browser.field.RenderingApplication;
import net.rim.device.api.browser.field.RenderingOptions;
import net.rim.device.api.browser.field.RenderingSession;
import net.rim.device.api.browser.plugin.BrowserContentProvider;
import net.rim.device.api.browser.plugin.BrowserContentProviderContext;
import net.rim.device.api.io.Base64InputStream;
import net.rim.device.api.io.MIMETypeAssociations;
import net.rim.device.api.io.http.HttpHeaders;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.api.utility.serialization.Converter;
import net.rim.device.apps.internal.browser.cod.JADConverterDescriptor;
import net.rim.device.apps.internal.browser.common.BrowserConverterDescriptor;
import net.rim.device.apps.internal.browser.page.Page;
import net.rim.device.apps.internal.browser.plugin.media.MediaRenderingConverter;
import net.rim.device.apps.internal.browser.stack.CacheResult;
import net.rim.device.apps.internal.browser.stack.CachedHttpConnection;
import net.rim.device.apps.internal.browser.stack.FetchRequest;
import net.rim.device.apps.internal.browser.stack.ModelResult;
import net.rim.device.apps.internal.browser.ui.SaveFileDialog;
import net.rim.device.apps.internal.browser.util.CountedInputStream;
import net.rim.device.apps.internal.browser.util.RendererControl;
import net.rim.vm.Array;

public final class DRMMessageConverter extends BrowserContentProvider {
   private static final String[] ACCEPT = new String[]{"application/vnd.oma.drm.message"};

   @Override
   public final String[] getSupportedMimeTypes() {
      return ACCEPT;
   }

   @Override
   public final String[] getAccept(RenderingOptions renderingOptions) {
      return ACCEPT;
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final BrowserContent getBrowserContent(BrowserContentProviderContext providerContext) {
      RenderingApplication renderingApplication = providerContext.getRenderingApplication();
      RenderingSession renderingSession = providerContext.getRenderingSession();
      RenderingOptions renderingOptions = renderingSession.getRenderingOptions();
      InputConnection inputConnection = providerContext.getInputConnection();
      InputStream inputStream = providerContext.getInputStream();
      int flags = providerContext.getFlags();

      try {
         if (inputStream == null) {
            inputStream = inputConnection.openInputStream();
         }

         inputStream = RendererControl.getInputStreamFromContentEncoding(inputConnection, inputStream, true);
         int expectedDataLength = -1;
         if (inputConnection instanceof Object) {
            ContentConnection contentConnection = (ContentConnection)inputConnection;
            expectedDataLength = (int)contentConnection.getLength();
         }

         if (!(inputStream instanceof Object)) {
            inputStream = (InputStream)(new Object(inputStream));
         }

         int boundaryState = 1;

         while (true) {
            int b = inputStream.read();
            if (b == -1) {
               return null;
            }

            if (b == 45) {
               if (boundaryState < 4 && boundaryState > 0) {
                  boundaryState++;
               }
            } else if (b == 10) {
               if (boundaryState >= 4) {
                  HttpHeaders headerTable = (HttpHeaders)(new Object());
                  headerTable.readFromStream((DataInputStream)(new Object(inputStream)));
                  String contentType = headerTable.getPropertyValue("Content-Type");
                  if (contentType == null) {
                     contentType = "text/plain";
                     headerTable.addProperty("Content-Type", contentType);
                  }

                  ContentReadEvent contentReadEvent = null;
                  if (expectedDataLength > 0) {
                     contentReadEvent = (ContentReadEvent)(new Object(inputConnection));
                     contentReadEvent.setItemsToReadInBytes(true);
                     contentReadEvent.setItemsToRead(expectedDataLength);
                     expectedDataLength -= ((CountedInputStream)inputStream).getCompressedBytesRead();
                  }

                  byte[] actualContent = RendererControl.readBytesFromInputStream(inputStream, contentReadEvent, renderingApplication, expectedDataLength);
                  if (actualContent == null) {
                     throw new Object();
                  }

                  for (int i = actualContent.length - 6; i >= 0; i--) {
                     if (actualContent[i] == 10 && actualContent[i + 1] == 45 && actualContent[i + 2] == 45) {
                        if (i >= 1 && actualContent[i - 1] == 13) {
                           i--;
                        }

                        Array.resize(actualContent, i);
                        break;
                     }
                  }

                  String contentTransferEncoding = headerTable.getPropertyValue("Content-Transfer-Encoding");
                  if (contentTransferEncoding != null && StringUtilities.strEqualIgnoreCase(contentTransferEncoding, "base64", 1701707776)) {
                     label425:
                     try {
                        actualContent = Base64InputStream.decode(actualContent, 0, actualContent.length);
                     } finally {
                        break label425;
                     }
                  }

                  String url = RendererControl.getUrl(inputConnection);
                  String savedFileURL = null;
                  if ((flags & 16) == 0 && renderingOptions != null && renderingOptions.getPropertyWithBooleanValue(4550690918222697397L, 28, false)) {
                     String autoSaveHeader = headerTable.getPropertyValue("x-rim-auto-save");
                     if (autoSaveHeader == null || StringUtilities.strEqualIgnoreCase(autoSaveHeader, "yes", 1701707776)) {
                        savedFileURL = this.saveContent(actualContent, contentType, url);
                     }
                  }

                  int responseCode = 200;
                  if (inputConnection instanceof Object) {
                     HttpConnection httpConnection = (HttpConnection)inputConnection;
                     responseCode = httpConnection.getResponseCode();
                     if (renderingOptions != null && renderingOptions.getPropertyWithBooleanValue(4550690918222697397L, 49, false)) {
                        String contentLocation = httpConnection.getHeaderField("Content-Location");
                        if (contentLocation == null) {
                           contentLocation = httpConnection.getHeaderField("Location");
                        }

                        if (contentLocation != null && headerTable.getPropertyValue("Content-Location") == null) {
                           headerTable.setProperty("Content-Location", contentLocation);
                        }
                     }

                     String contentDisposition = httpConnection.getHeaderField("Content-Disposition");
                     if (contentDisposition != null && headerTable.getPropertyValue("Content-Disposition") == null) {
                        headerTable.setProperty("Content-Disposition", contentDisposition);
                     }
                  }

                  headerTable.setProperty("Content-Length", String.valueOf(actualContent.length));
                  CacheResult cacheResult = (CacheResult)(new Object(url, actualContent, headerTable, responseCode));
                  CachedHttpConnection conn = (CachedHttpConnection)(new Object(url, null, (InputStream)(new Object(actualContent)), cacheResult, null));
                  flags |= 2048;
                  if (savedFileURL != null) {
                     renderingApplication.eventOccurred((Event)(new Object(conn, savedFileURL, providerContext.getEvent(), 3)));
                     return null;
                  }

                  String contentTypeWithoutParameters = RendererControl.stripContentTypeParameters(contentType);
                  String[] midletTypes = JADConverterDescriptor.ACCEPT;
                  if (midletTypes != null) {
                     for (int i = midletTypes.length - 1; i >= 0; i--) {
                        if (contentTypeWithoutParameters.equalsIgnoreCase(midletTypes[i])) {
                           ModelResult modelResult = (ModelResult)(new Object(url, flags, null));
                           FetchRequest fetchRequest = (FetchRequest)(new Object(modelResult));
                           modelResult.setCacheResult(cacheResult);
                           Converter converter = BrowserConverterDescriptor.getConverter(contentType);
                           if (converter != null) {
                              try {
                                 Object page = converter.convert((DataInput)((Object)null), new Object(fetchRequest, conn.openInputStream(), conn));
                                 if (page instanceof Object) {
                                    return ((Page)page).getBrowserContent();
                                 }
                              } finally {
                                 break;
                              }
                           }
                           break;
                        }
                     }
                  }

                  return renderingSession.getBrowserContent(conn, renderingApplication, flags);
               }

               boundaryState = 1;
            } else if (boundaryState == 3 && b != 13) {
               boundaryState = 4;
            } else if (boundaryState < 4) {
               boundaryState = 0;
            }
         }
      } catch (Throwable var37) {
         System.out.println(ioe.toString());
         return null;
      }
   }

   private final String saveContent(byte[] data, String contentType, String url) {
      if (contentType == null) {
         return null;
      }

      contentType = MIMETypeAssociations.getNormalizedType(contentType);
      int mediaType = 0;
      byte var6;
      if (contentType.startsWith("image/")) {
         if (!Graphics.isColor()) {
            return null;
         }

         if (!EncodedImage.isMIMETypeSupported(contentType)) {
            return null;
         }

         var6 = 1;
      } else if (contentType.startsWith("audio/")) {
         if (!isTypeSupported(new MediaRenderingConverter().getSupportedMimeTypes(), contentType)) {
            return null;
         }

         var6 = 2;
      } else {
         if (!contentType.startsWith("video/")) {
            return null;
         }

         if (!isTypeSupported(new MediaRenderingConverter().getSupportedMimeTypes(), contentType)) {
            return null;
         }

         var6 = 3;
      }

      return SaveFileDialog.save(url, contentType, var6, true, data);
   }

   private static final boolean isTypeSupported(String[] supportedTypes, String type) {
      if (supportedTypes != null && type != null) {
         for (int i = 0; i < supportedTypes.length; i++) {
            if (type.equalsIgnoreCase(RendererControl.stripContentTypeParameters(supportedTypes[i]))) {
               return true;
            }
         }
      }

      return false;
   }
}
