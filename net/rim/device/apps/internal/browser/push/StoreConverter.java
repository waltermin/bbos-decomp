package net.rim.device.apps.internal.browser.push;

import java.io.ByteArrayInputStream;
import java.io.DataInput;
import java.io.InputStream;
import java.io.OutputStream;
import net.rim.device.api.crypto.RIMSignature;
import net.rim.device.api.io.Base64InputStream;
import net.rim.device.api.io.NoCopyByteArrayOutputStream;
import net.rim.device.api.io.http.HttpHeaders;
import net.rim.device.api.system.Branding;
import net.rim.device.api.system.EventLogger;
import net.rim.device.apps.api.utility.general.AsciiStringInputStream;
import net.rim.device.apps.api.utility.serialization.BaseConverter;
import net.rim.device.apps.api.utility.serialization.SerializationException;

final class StoreConverter extends BaseConverter {
   private static final String PUSH_LOCATION_KEY = "X-Rim-Content-Location";
   private static final String SIG_HTTP_HEADER_NAME = "X-ContentSig";
   private static final String CONTENT_LOCATION = "/appdata/rim/idlescreen/carrier/";
   private static final String CONTENT_TYPE = "Content-Type";

   @Override
   public final boolean canConvert(Object parameters) {
      return true;
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final Object convert(DataInput inputStreamObj, Object headersObj) throws SerializationException {
      if (headersObj instanceof HttpHeaders && inputStreamObj instanceof InputStream) {
         InputStream inputStream = (InputStream)inputStreamObj;
         HttpHeaders headers = (HttpHeaders)headersObj;

         try {
            EventLogger.logEvent(1907089860548946979L, 1347448947, 5);
            String path = headers.getPropertyValue("X-Rim-Content-Location");
            if (path == null) {
               EventLogger.logEvent(1907089860548946979L, 1347450220, 3);
               throw new SerializationException();
            } else if (!path.startsWith("/appdata/rim/idlescreen/carrier/")) {
               EventLogger.logEvent(1907089860548946979L, 1347447148, 3);
               throw new SerializationException();
            } else {
               StoreConverter$ContentData content = new StoreConverter$ContentData();
               this.readAndVerifyContent(inputStream, content, headers.getPropertyValue("X-ContentSig"));
               if (content._data == null) {
                  EventLogger.logEvent(1907089860548946979L, 1347449702, 3);
                  throw new SerializationException();
               } else {
                  return new StoreModel(content._data, "/store" + path);
               }
            }
         } catch (Throwable var8) {
            EventLogger.logEvent(1907089860548946979L, ("Content Store Push: " + e.getMessage()).getBytes(), 3);
            throw new SerializationException();
         }
      } else {
         throw new SerializationException();
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final void readAndVerifyContent(InputStream is, StoreConverter$ContentData content, String sigString) {
      boolean verified = false;
      long returnVal = 0;
      content._data = null;
      content._timestamp = -1;
      boolean var14 = false /* VF: Semaphore variable */;

      byte[] data;
      int dataLength;
      try {
         var14 = true;
         Base64InputStream e = new Base64InputStream(new AsciiStringInputStream(sigString));
         NoCopyByteArrayOutputStream sigByteArrayStream = new NoCopyByteArrayOutputStream();
         NoCopyByteArrayOutputStream dataByteArrayStream = new NoCopyByteArrayOutputStream();
         copyStreams(e, sigByteArrayStream);
         copyStreams(is, dataByteArrayStream);
         byte[] sig = sigByteArrayStream.getByteArray();
         data = dataByteArrayStream.getByteArray();
         dataLength = dataByteArrayStream.size();
         returnVal = RIMSignature.verify(data, 0, dataLength, sig, 0, Branding.getData(16387));
         verified = returnVal > 0;
         var14 = false;
      } finally {
         if (var14) {
            return;
         }
      }

      if (verified) {
         content._data = new ByteArrayInputStream(data, 0, dataLength);
         content._timestamp = returnVal;
      }
   }

   private static final int copyStreams(InputStream is, OutputStream os) {
      byte[] data = new byte[500];
      int totalBytes = 0;

      while (true) {
         int bytesRead = is.read(data);
         if (bytesRead <= 0) {
            return totalBytes;
         }

         os.write(data, 0, bytesRead);
         totalBytes += bytesRead;
      }
   }
}
