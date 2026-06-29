package com.fourthpass.wapstack.ota;

import com.fourthpass.wapstack.wsp.WSPHeaderDecoder;
import com.fourthpass.wapstack.wsp.pdu.WSP_PushPDU;
import com.fourthpass.wapstack.wtp.WTPLayer;
import com.fourthpass.wapstack.wtp.pdu.WTP_PDU;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import net.rim.device.api.browser.push.PushEventLogger;
import net.rim.device.api.io.IOUtilities;
import net.rim.device.api.io.http.HttpHeaders;
import net.rim.device.api.system.EventLogger;

public final class Dispatcher implements PushEventLogger {
   private static final String CONTENT_TYPE_SIA = "application/vnd.wap.sia";

   public static final Object[] processRawData(boolean isConnectionless, byte[] rawData, int bearer, String source, WTPLayer wtpLayer, WTP_PDU wtpPDU) {
      EventLogger.logEvent(-1133226195824034738L, 1148215908, 5);
      Object[] data = decode(isConnectionless, rawData, bearer, source, wtpLayer, wtpPDU);
      EventLogger.logEvent(-1133226195824034738L, 1148215913, 5);
      return data != null ? processSIA(data) : null;
   }

   private static final Object[] decode(boolean isConnectionless, byte[] rawData, int bearer, String source, WTPLayer wtpLayer, WTP_PDU wtpPDU) {
      try {
         WSP_PushPDU pushPDU = new WSP_PushPDU(isConnectionless, new ByteArrayInputStream(rawData));
         if (pushPDU != null) {
            InputStream pushData = pushPDU.getData();
            byte[] header = pushPDU.getHeader();
            Object[] result = new Object[]{new HttpHeaders(), null};
            if (header != null) {
               WSPHeaderDecoder decoder = new WSPHeaderDecoder((HttpHeaders)result[0]);
               decoder.decode(header, true);
            }

            result[1] = new WAPPushInputStream(pushData, bearer == 1 ? 1 : 2, source, wtpLayer, wtpPDU);
            return result;
         }
      } finally {
         return null;
      }

      return null;
   }

   private static final Object[] processSIA(Object[] data) {
      try {
         String contentType = ((HttpHeaders)data[0]).getPropertyValue("Content-Type");
         if (contentType == null) {
            return null;
         } else if (contentType.equals("application/vnd.wap.sia")) {
            EventLogger.logEvent(-1133226195824034738L, 1148216435, 5);
            SIA sia = new SIA();
            sia.processSIAContent(IOUtilities.streamToBytes((InputStream)data[1]));
            EventLogger.logEvent(-1133226195824034738L, 1148215667, 5);
            return null;
         } else {
            return data;
         }
      } finally {
         ;
      }
   }
}
