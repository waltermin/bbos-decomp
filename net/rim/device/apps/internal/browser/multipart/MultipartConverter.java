package net.rim.device.apps.internal.browser.multipart;

import com.fourthpass.wapstack.wsp.WSPHeaderDecoder;
import java.io.DataInput;
import java.io.InputStream;
import net.rim.device.api.browser.field.BrowserContent;
import net.rim.device.api.io.http.HttpHeaders;
import net.rim.device.api.util.CharacterUtilities;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.api.utility.general.URI;
import net.rim.device.apps.api.utility.serialization.BaseConverter;
import net.rim.device.apps.api.utility.serialization.Converter;
import net.rim.device.apps.api.utility.serialization.SerializationManager;
import net.rim.device.apps.internal.browser.common.BrowserConverterDescriptor;
import net.rim.device.apps.internal.browser.core.BrowserDaemonRegistry;
import net.rim.device.apps.internal.browser.page.Page;
import net.rim.device.apps.internal.browser.page.PageConverterWrapper;
import net.rim.device.apps.internal.browser.resources.BrowserResources;
import net.rim.device.apps.internal.browser.stack.AccumulatorInputStream;
import net.rim.device.apps.internal.browser.stack.CacheResult;
import net.rim.device.apps.internal.browser.stack.CachedHttpConnection;
import net.rim.device.apps.internal.browser.stack.FetchRequest;
import net.rim.device.apps.internal.browser.stack.ModelResult;
import net.rim.device.apps.internal.browser.stack.RawDataCache;
import net.rim.device.apps.internal.browser.stack.WAPInputStream;
import net.rim.device.apps.internal.browser.util.RendererControl;
import net.rim.device.internal.browser.util.PipeContext;
import net.rim.device.internal.browser.util.PipeInput;
import net.rim.vm.Array;

public final class MultipartConverter extends BaseConverter {
   private Object _context;
   private static final int WAP_ALTERNATIVE;
   private static final int WAP_MIXED;
   private static final int WAP_RELATED;
   private static final int ALTERNATIVE;
   private static final int MIXED;
   private static final int RELATED;
   static final int RETURN_FIRST;
   static final int RETURN_FIRST_CONVERTED;
   static final int RETURN_JUST_CACHE;
   static final int RETURN_CONVERT_ALL;

   public MultipartConverter(Object context) {
      this._context = context;
   }

   @Override
   public final boolean canConvert(Object parameters) {
      return true;
   }

   @Override
   public final Object convert(byte[] inputBytes, Object contextObject) {
      if (!(contextObject instanceof FetchRequest)) {
         return null;
      }

      FetchRequest fr = (FetchRequest)contextObject;
      HttpHeaders headers = fr.getModelResult().getCacheResult().getResponseHeaders();
      String rootUrl = fr.getModelResult().getURL();
      return convert((InputStream)(new Object(inputBytes)), headers.getPropertyValue("Content-Type"), rootUrl, false, fr, null, this._context);
   }

   @Override
   public final Object convert(DataInput aDataInput, Object contextObject) {
      InputStream in = null;
      HttpHeaders headers = null;
      FetchRequest fr = null;
      String rootUrl = null;
      if (!(contextObject instanceof PageConverterWrapper)) {
         if (contextObject instanceof Object && aDataInput instanceof Object) {
            in = (InputStream)aDataInput;
            headers = (HttpHeaders)contextObject;
         }
      } else {
         PageConverterWrapper wrapper = (PageConverterWrapper)contextObject;
         fr = wrapper.getFetchRequest();
         in = wrapper.getInStream();
         headers = fr.getModelResult().getCacheResult().getResponseHeaders();
         rootUrl = fr.getModelResult().getURL();
      }

      return headers != null && in != null ? convert(in, headers.getPropertyValue("Content-Type"), rootUrl, false, fr, null, this._context) : null;
   }

   public static final Object convert(
      InputStream in,
      String mainContentType,
      String rootUrl,
      boolean isCache,
      FetchRequest fetchRequest,
      CacheResult parentCacheResult,
      Object conversionContext
   ) {
      try {
         int type = getMultipartType(mainContentType);
         if (type == 0) {
            return null;
         }

         ModelResult modelResult = null;
         if (fetchRequest != null) {
            modelResult = fetchRequest.getModelResult();
            if (modelResult != null) {
               modelResult.postponeSecondaryResources();
            }
         }

         int returnAction = -1;
         if ("net.rim.device.apps.internal.browser.wappush".equals(conversionContext)) {
            returnAction = 4;
         }

         if (isCache) {
            returnAction = 3;
         }

         switch (type) {
            case 0:
               return null;
            case 1:
            case 4:
            default:
               return handleContent(
                  type == 1, returnAction == -1 ? 2 : returnAction, in, mainContentType, rootUrl, fetchRequest, parentCacheResult, conversionContext
               );
            case 2:
            case 3:
            case 5:
            case 6:
               return handleContent(
                  type == 3 || type == 2,
                  returnAction == -1 ? 1 : returnAction,
                  in,
                  mainContentType,
                  rootUrl,
                  fetchRequest,
                  parentCacheResult,
                  conversionContext
               );
         }
      } finally {
         return null;
      }
   }

   public static final int getMultipartType(String mainContentType) {
      if (mainContentType != null && mainContentType.length() != 0) {
         char char0 = CharacterUtilities.toLowerCase(mainContentType.charAt(0), 1701707776);
         if (char0 == 'a') {
            int indexOfSemi = mainContentType.indexOf(59);
            if (indexOfSemi > 0) {
               mainContentType = mainContentType.substring(0, indexOfSemi);
            }

            if (StringUtilities.strEqualIgnoreCase(mainContentType, "application/vnd.wap.multipart.related", 1701707776)) {
               return 3;
            }

            if (StringUtilities.strEqualIgnoreCase(mainContentType, "application/vnd.wap.multipart.alternative", 1701707776)) {
               return 1;
            }

            if (StringUtilities.strEqualIgnoreCase(mainContentType, "application/vnd.wap.multipart.mixed", 1701707776)) {
               return 2;
            }
         } else if (char0 == 'm') {
            int indexOfSemi = mainContentType.indexOf(59);
            if (indexOfSemi > 0) {
               mainContentType = mainContentType.substring(0, indexOfSemi);
            }

            if (StringUtilities.strEqualIgnoreCase(mainContentType, "multipart/related", 1701707776)) {
               return 6;
            }

            if (StringUtilities.strEqualIgnoreCase(mainContentType, "multipart/alternative", 1701707776)) {
               return 4;
            }

            if (StringUtilities.strEqualIgnoreCase(mainContentType, "multipart/mixed", 1701707776)) {
               return 5;
            }
         }

         return 0;
      } else {
         return 0;
      }
   }

   private static final Object handleContent(
      boolean isWapEncoded,
      int returnAction,
      InputStream in,
      String mainContentType,
      String rootUrl,
      FetchRequest fetchRequest,
      CacheResult parentCacheResult,
      Object conversionContext
   ) {
      String firstContentType = null;
      WAPInputStream wapIn = null;
      MimeMultipartParser mimeMultipartParser = null;
      if (isWapEncoded) {
         wapIn = (WAPInputStream)(new Object(in));
         wapIn.readMBInt();
      } else {
         mimeMultipartParser = new MimeMultipartParser(in, mainContentType);
      }

      RawDataCache dataCache = BrowserDaemonRegistry.getInstance().getRawDataCache();
      Object[] result = null;

      try {
         do {
            Object entityResult = processEntity(
               isWapEncoded, returnAction, mimeMultipartParser, isWapEncoded ? wapIn : in, rootUrl, fetchRequest, parentCacheResult
            );
            if (entityResult == null) {
               throw new Object(
                  ((StringBuffer)(new Object())).append(BrowserResources.getString(265)).append(firstContentType).toString() != null ? firstContentType : ""
               );
            }

            if (returnAction == 3) {
               CacheResult cr = (CacheResult)entityResult;
               String nodeUrl = cr.getURLWithoutFragment();
               if (!StringUtilities.strEqualIgnoreCase(rootUrl, nodeUrl, 1701707776)) {
                  dataCache.put(nodeUrl, cr, true);
               }
            } else {
               CachedHttpConnection conn = (CachedHttpConnection)entityResult;
               Converter converter = null;
               String contentType = RendererControl.getContentType(conn);
               if ("net.rim.device.apps.internal.browser".equals(conversionContext)) {
                  converter = BrowserConverterDescriptor.getConverter(contentType);
               } else if ("net.rim.device.apps.internal.browser.wappush".equals(conversionContext)) {
                  String pushContentType = conn.getHeaderField("X-Rim-Push-Type");
                  if (pushContentType == null) {
                     pushContentType = StringUtilities.toLowerCase(contentType, 1701707776);
                  } else {
                     pushContentType = StringUtilities.toLowerCase(pushContentType, 1701707776);
                     int paramStartIndex = pushContentType.indexOf(59);
                     if (paramStartIndex != -1) {
                        pushContentType = pushContentType.substring(0, paramStartIndex).trim();
                     }
                  }

                  converter = SerializationManager.getConverter(pushContentType, conversionContext);
               } else if (contentType != null) {
                  converter = SerializationManager.getConverter(StringUtilities.toLowerCase(contentType, 1701707776), conversionContext);
               }

               if (converter != null) {
                  Object obj;
                  if ("net.rim.device.apps.internal.browser.wappush".equals(conversionContext)) {
                     obj = converter.convert(conn.openDataInputStream(), conn.getCacheResult().getResponseHeaders());
                  } else {
                     PageConverterWrapper wrapper = new PageConverterWrapper(fetchRequest, conn.openInputStream(), conn);
                     obj = converter.convert((DataInput)((Object)null), wrapper);
                  }

                  if (obj instanceof Page) {
                     BrowserContent field = ((Page)obj).getBrowserContent();
                     InputStream multIn = conn.openInputStream();
                     if (multIn instanceof MultipartInputStream) {
                        ((MultipartInputStream)multIn).setBrowserContent(field);
                     }
                  }

                  if (returnAction == 1 || obj != null && returnAction == 2) {
                     return obj;
                  }

                  if (obj != null) {
                     if (result == null) {
                        result = new Object[1];
                     } else {
                        Array.resize(result, result.length + 1);
                     }

                     result[result.length - 1] = obj;
                  }
               }
            }
         } while (mimeMultipartParser == null || mimeMultipartParser.nextPart());
      } finally {
         return result;
      }

      return result;
   }

   static final Object processEntity(
      boolean isWapEncoded,
      int returnAction,
      MimeMultipartParser mimeMultipartParser,
      InputStream in,
      String rootUrl,
      FetchRequest fetchRequest,
      CacheResult parentCacheResult
   ) {
      int dataLen = -1;
      HttpHeaders headerTable;
      if (isWapEncoded) {
         WAPInputStream wapIn = (WAPInputStream)in;
         int headersLen = wapIn.readMBInt();
         dataLen = wapIn.readMBInt();
         if (headersLen > 0) {
            byte[] headers = new byte[headersLen];
            wapIn.read(headers);
            headerTable = (HttpHeaders)(new Object());
            WSPHeaderDecoder oldHeaderDecoder = (WSPHeaderDecoder)(new Object(headerTable));
            boolean decodeContentType = true;
            if (headers[0] == -111) {
               decodeContentType = false;
            }

            oldHeaderDecoder.decode(headers, decodeContentType);
         } else {
            headerTable = (HttpHeaders)(new Object());
         }
      } else {
         headerTable = mimeMultipartParser.getPartHeaders();
      }

      String url = headerTable.getPropertyValue("X-Wap-Content-URI");
      if (url == null) {
         url = headerTable.getPropertyValue("Content-Location");
         if (url == null) {
            if (rootUrl != null) {
               url = rootUrl;
            } else {
               url = "";
            }
         }
      }

      url = URI.getAbsoluteURL(url, rootUrl);
      CacheResult cr = null;
      InputStream resultIn;
      if (returnAction == 1) {
         AccumulatorInputStream accumIn = (AccumulatorInputStream)(new Object(
            null, (InputStream)(mimeMultipartParser != null ? mimeMultipartParser.getPartContent() : new Object(in, dataLen, false, false)), null, false
         ));
         cr = new CacheResult(url, null, headerTable, 200);
         cr.setData(accumIn.getPipe());
         resultIn = new MultipartInputStream(isWapEncoded, in, cr.getData(), rootUrl, dataLen, mimeMultipartParser, fetchRequest);
      } else {
         if (parentCacheResult != null && dataLen >= 0) {
            PipeContext ptr = null;
            if (!(in instanceof Object)) {
               if (in instanceof Object) {
                  ptr = ((PipeInput)in).getPosition();
               }
            } else {
               ptr = ((WAPInputStream)in).getPosition();
            }

            if (ptr != null) {
               cr = new CacheResult(url, ptr._currentPacket, ptr._currentReadPos, dataLen, parentCacheResult, headerTable, 200);
               in.skip(dataLen);
            }
         }

         if (cr == null) {
            AccumulatorInputStream accumIn = (AccumulatorInputStream)(new Object(
               null, (InputStream)(mimeMultipartParser != null ? mimeMultipartParser.getPartContent() : new Object(in, dataLen, false, false)), null, false
            ));
            cr = new CacheResult(url, null, headerTable, 200);
            cr.setData(accumIn.getPipe());
         }

         if (returnAction == 3) {
            return cr;
         }

         resultIn = cr.getStream();
      }

      return new CachedHttpConnection(url, null, resultIn, cr, headerTable);
   }
}
