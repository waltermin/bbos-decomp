package net.rim.device.apps.internal.iota;

import java.io.ByteArrayOutputStream;
import net.rim.device.api.crypto.RandomSource;
import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.internal.system.RadioInternal;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

public final class MMCDocHandler extends DefaultHandler {
   private boolean _currentMMC;
   private String _currentResult;
   private String _currentOp;
   private String _currentObj;
   private String _currentVal;
   private byte[] _is683Result;
   private Object _syncObject = new Object();
   private StringBuffer _reportHeader;
   private StringBuffer _reportFooter;
   private StringBuffer _reportBody;
   private ByteArrayOutputStream _byteStream;
   private int _reportStatus;
   private int _cidCount;
   private String _statusURI;
   private int _errorCount = 0;
   private boolean _isCriticalError;
   private boolean _disconnect = false;
   private int _mmcID;
   private ProvisioningServiceAgent _provisioningServiceAgent;
   private MimeMultipart _multipart;
   private HttpUserAgent _httpUserAgent;
   private MMCProcessor _processor;
   private static final String MMC;
   private static final String METHOD;
   private static final String ID;
   private static final String NAME;
   private static final String REPORTSTATUS;
   private static final String VALUEREF;
   private static final String VALUE;
   private static final String OBJECT;
   private static final String STATUS_URI;
   private static final String MMC_ID;
   private static final String SIZE;
   private static final String OFFSET;
   private static final String OPEN;
   private static final String WRITE;
   private static final String READ;
   private static final String COMMIT;
   private static final String DISCONNECT;
   public static final String PHONE_BOOT_URL;
   public static final String PHONE_BOOT_NAI_URL;
   public static final String PHONE_NAI_CURRENT;
   public static final String PHONE_MODEL;
   public static final String PHONE_OEM;
   public static final String PHONE_PSA_VERSION;
   public static final String PHONE_SOFTWARE_VERSION;
   public static final String PHONE_CDMA_IS683;
   public static final String PHONE_CDMA_IS683_RESULT;
   public static final String PHONE_CDMA_PRL;
   public static final String PHONE_CDMA_NAM_MDN;
   public static final String PHONE_CDMA_NAM_MSID;
   public static final String BROWSER_DOMAIN_TRUSTED;
   public static final String BROWSER_PROXY;
   public static final String BROWSER_RESET;
   public static final String PHONE_RESET;
   public static final String PHONE_HOMEPAGE;
   public static final String PRL_STORAGE_AVAILABLE;
   public static final String PHONE_RESTARTSESSION;
   private static final String CID_SUFFIX;
   private static final String VALUEREF_STATUS;
   private static final String CONTENT_ID;
   private static final String CONTENT_TYPE;
   private static final String BOUNDARY;
   private static final String APPLICATION_OCTETSTREAM;
   private static final char[] CR_LF = new char[]{'\r', '\n', '\u0002', '퀄'};
   private static final String ERROR;
   private static final String WARNING;
   private static final String ERROR_UNKNOWN;
   private static final String OBJECT_UNKNOWN;
   private static final String OK;
   private static final String CID_MISSING;
   private static final String DOWNLOAD_FAILED;
   public static final int PRL_STORAGE_SIZE;
   public static boolean _prlIsDirty = false;

   public MMCDocHandler(MMCProcessor mproc, ProvisioningServiceAgent provisioningServiceAgent, HttpUserAgent httpUserAgent, MimeMultipart multipart) {
      this._provisioningServiceAgent = provisioningServiceAgent;
      this._httpUserAgent = httpUserAgent;
      this._multipart = multipart;
      this._reportStatus = 0;
      this._mmcID = -1;
      this._cidCount = 0;
      this._isCriticalError = false;
      this._byteStream = (ByteArrayOutputStream)(new Object());
      this._processor = mproc;
   }

   @Override
   public final void endDocument() {
      this._currentMMC = false;
   }

   @Override
   public final void startDocument() {
      this._reportHeader = ((StringBuffer)(new Object("<?xml version=\"1.0\"?>"))).append(CR_LF);
      this._reportHeader.append("<!DOCTYPE mmc PUBLIC \"-//PHONE.COM//DTD MMC 2.0//EN\" ");
      this._reportHeader.append("\"http://www.phone.com/dtd/mmc20.dtd\">").append(CR_LF);
      this._reportFooter = ((StringBuffer)(new Object("</status>\n</mmc>"))).append(CR_LF);
      this._reportBody = ((StringBuffer)(new Object())).append(CR_LF);
   }

   @Override
   public final void startElement(String uri, String localName, String qName, Attributes attributes) {
      if (!"mmc".equals(localName)) {
         if ("method".equals(localName) && this._currentMMC) {
            String status = this.handleMethod(attributes);
            if (status != null) {
               this._reportStatus++;
               this._reportBody.append(status).append(CR_LF);
            }
         }
      } else {
         if (attributes != null) {
            for (int i = attributes.getLength() - 1; i >= 0; i--) {
               String att = attributes.getLocalName(i);
               if (att.equals("status-uri")) {
                  this._statusURI = attributes.getValue(i);
               } else if (att.equals("mmcID")) {
                  this._mmcID = Integer.parseInt(attributes.getValue(i));
               }
            }
         }

         this._currentMMC = true;
         this._reportHeader.append("<mmc>").append(CR_LF).append("<status mmc=");
         this._reportHeader.append('"');
      }
   }

   private final String handleMethod(Attributes attributes) {
      if (attributes == null) {
         return null;
      }

      boolean report = false;
      String valueref = null;
      int len = attributes.getLength();
      int size = -1;
      int offset = -1;
      StringBuffer status = (StringBuffer)(new Object("<detail "));

      for (int i = 0; i < len; i++) {
         String attName = attributes.getLocalName(i);
         String val = attributes.getValue(i);
         if (attName.equals("id")) {
            int v = Integer.parseInt(val);
            status.append("id").append('=').append('"');
            status.append(String.valueOf(v)).append('"').append(' ');
         } else if (!attName.equals("name")) {
            if (attName.equals("object")) {
               status.append("object").append('=').append('"');
               status.append(val).append('"').append(' ');
               this._currentObj = val;
            } else if (attName.equals("reportstatus")) {
               report = val.equals("true");
            } else if (attName.equals("value")) {
               this._currentVal = val;
            } else if (attName.equals("valueref")) {
               valueref = val;
            } else if (attName.equals("size")) {
               try {
                  size = Integer.parseInt(val);
               } finally {
                  continue;
               }
            } else if (attName.equals("offset")) {
               try {
                  offset = Integer.parseInt(val);
               } finally {
                  continue;
               }
            }
         } else {
            status.append("name").append('=').append('"');
            this._currentOp = val;
            if (val.equals("read")) {
               status.append("read");
            } else if (val.equals("open")) {
               status.append("open");
            } else if (val.equals("write")) {
               status.append("write");
            } else if (val.equals("commit")) {
               status.append("commit");
            } else if (val.equals("disconnect")) {
               status.append("disconnect");
            }

            status.append('"').append(' ');
         }
      }

      if (this._currentOp.equals("read")) {
         status.append(this.handleRead(this._currentObj));
      } else if (!this._currentOp.equals("write")) {
         if (this._currentOp.equals("disconnect")) {
            this._disconnect = true;
         } else if (this._currentOp.equals("commit")) {
            status.append(this.handleCommit());
         } else if (this._currentOp.equals("open")) {
            this._mmcID = RandomSource.getInt(2500);
            status.append(" result=").append('"');
            status.append("ok").append('"');
         }
      } else if (valueref == null) {
         if (this._currentObj.equals("phone:cdma.nam.mdn")) {
            status.append(this.handleWrite(this._currentObj, ((StringBuffer)(new Object())).append('1').append(this._currentVal).toString()));
         } else if (this._currentObj.equals("phone:cdma.nam.msid")) {
            status.append(this.handleWrite(this._currentObj, ((StringBuffer)(new Object())).append('2').append(this._currentVal).toString()));
         } else {
            status.append(this.handleWrite(this._currentObj, this._currentVal));
         }
      } else {
         byte[] data = null;
         String errorStr = null;
         if (!this.isSupportedObject(this._currentObj)) {
            errorStr = "UnknownObject";
         } else if (!valueref.startsWith("http")) {
            if (valueref.startsWith("cid:")) {
               BodyPart bodyPart = this._multipart.getBodyPart(MimeMultipart.cidURLtoContentID(valueref));
               if (bodyPart != null) {
                  data = bodyPart.getBytes();
               } else {
                  errorStr = "CidMissing";
                  EventLogger.logEvent(4411276428801970910L, 1298360685, 2);
               }
            }
         } else {
            label338:
            try {
               EventLogger.logEvent(4411276428801970910L, 1299605619, 5);
               data = this._httpUserAgent.get(valueref, offset, size);
               EventLogger.logEvent(4411276428801970910L, 1299605604, 5);
               String contentType = this._httpUserAgent.getContentType();
               if (contentType != null) {
                  String contentTypeWithoutParameters = contentType;
                  int semicolon = contentType.indexOf(59);
                  if (semicolon >= 0) {
                     contentTypeWithoutParameters = contentType.substring(0, semicolon).trim();
                  }

                  if (!StringUtilities.strEqualIgnoreCase(contentTypeWithoutParameters, "application/octet-stream", 1701707776)) {
                     EventLogger.logEvent(4411276428801970910L, 1299604340, 2);
                     data = null;
                  }
               }

               if (data != null) {
                  offset = this._httpUserAgent.getContentRangeStart();
               } else {
                  EventLogger.logEvent(4411276428801970910L, 1299608166, 2);
               }
            } finally {
               break label338;
            }
         }

         if (data != null) {
            status.append(this.handleWrite(this._currentObj, data, offset));
         } else {
            this._isCriticalError = true;
            this._errorCount++;
            status.append(" result=\"").append(errorStr != null ? errorStr : "DownLoadFailed").append('"');
         }
      }

      status.append("/>");
      return report ? status.toString() : null;
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final void waitForResults() {
      EventLogger.logEvent(4411276428801970910L, 1296659063, 5);
      synchronized (this._syncObject) {
         try {
            this._syncObject.wait(100);
         } catch (Throwable var6) {
            ProvisioningServiceAgent.logEvents(e.toString());
            return;
         }
      }
   }

   public final void notifyResults() {
      EventLogger.logEvent(4411276428801970910L, 1296659054, 5);
      synchronized (this._syncObject) {
         this._syncObject.notify();
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final String handleRead(String curObj) {
      if (this._isCriticalError) {
         return this.abortStatus();
      }

      StringBuffer result = (StringBuffer)(new Object());
      String res = null;
      if (!curObj.equals("phone:cdma.is683.result")) {
         if (curObj.equals("phone:cdma.prl")) {
            this._isCriticalError = true;
            this._errorCount++;
            res = "WriteOnly";
         } else if (!curObj.equals("phone:cdma.nam.mdn") && !curObj.equals("phone:cdma.nam.msid")) {
            if (curObj.equals("phone:psa.version")) {
               this._currentResult = ApplicationDescriptor.currentApplicationDescriptor().getVersion();
               res = "ok";
               System.out.println(((StringBuffer)(new Object("PHONE PSA VERSION: "))).append(this._currentResult).toString());
            } else if (curObj.equals("phone:version.software")) {
               this._currentResult = DeviceInfo.getPlatformVersion();
               res = "ok";
               ProvisioningServiceAgent.logEvents(((StringBuffer)(new Object("PHONE SOFTWARE VERSION: "))).append(this._currentResult).toString());
            } else if (curObj.equals("phone:oem")) {
               this._currentResult = "Research In Motion Ltd.";
               res = "ok";
            } else if (curObj.equals("phone:model")) {
               this._currentResult = DeviceInfo.getDeviceName();
               ProvisioningServiceAgent.logEvents(((StringBuffer)(new Object("PHONE MODEL: "))).append(this._currentResult).toString());
               res = "ok";
            } else if (curObj.equals("phone:prl.storage.available")) {
               this._currentResult = String.valueOf(8192);
               res = "ok";
            } else {
               this._currentResult = (String)this._provisioningServiceAgent.readFromStorage(curObj);
               if (this._currentResult == null) {
                  res = "UnknownObject";
                  this._errorCount++;
                  return " result=\"UnknownObject\"";
               }

               res = "ok";
            }

            result.append("value").append('=').append('"');
            result.append(this._currentResult).append('"');
         } else {
            this._isCriticalError = true;
            this._errorCount++;
            res = "WriteOnly";
         }
      } else {
         EventLogger.logEvent(4411276428801970910L, 1296659059, 5);
         this._is683Result = this._processor.getOTAResult();
         if (this._is683Result == null) {
            this.waitForResults();
         }

         this._is683Result = this._processor.getOTAResult();
         EventLogger.logEvent(4411276428801970910L, 1296659044, 5);
         if (this._is683Result == null) {
            EventLogger.logEvent(4411276428801970910L, 1296659045, 2);
            this._isCriticalError = true;
            this._errorCount++;
            res = "UnknownError";
         } else {
            this._processor.setOTAResult(null);
            result.append("valueref").append('=');
            result.append('"').append("cid:").append(++this._cidCount);
            result.append("@cdma.blackberry.com").append('"');
            res = "ok";
            StringBuffer stbuf = (StringBuffer)(new Object());
            stbuf.append('-').append('-');
            stbuf.append("cDmAbLaCkBeRrYiOtA").append(CR_LF).append("Content-ID: <").append(this._cidCount);
            stbuf.append("@cdma.blackberry.com").append('>').append(CR_LF).append("Content-Type: ");
            stbuf.append("application/octet-stream").append(CR_LF).append(CR_LF);

            label81:
            try {
               this._byteStream.write(stbuf.toString().getBytes());
               this._byteStream.write(this._is683Result);
               this._byteStream.write(new byte[]{13, 10});
            } catch (Throwable var7) {
               ProvisioningServiceAgent.logEvents(ioe.toString());
               break label81;
            }
         }
      }

      result.append(" result=").append('"').append(res);
      result.append('"');
      this._currentResult = null;
      return result.toString();
   }

   private final boolean isSupportedObject(String object) {
      if (this._provisioningServiceAgent.contains(object)) {
         return true;
      }

      switch (object.length()) {
         case 11:
            return object.equals("phone:reset");
         case 13:
            return object.equals("browser:reset");
         case 14:
            if (!object.equals("phone:boot.url") && !object.equals("phone:cdma.prl")) {
               return false;
            }

            return true;
         case 16:
            return object.equals("phone:cdma.is683");
         case 17:
            return object.equals("phone:boot.naiurl");
         case 18:
            return object.equals("phone:cdma.nam.mdn");
         case 19:
            return object.equals("phone:cdma.nam.msid");
         case 20:
            return object.equals("phone:restartsession");
         case 22:
            return object.equals("browser:domain.trusted");
         case 29:
            return object.equals("phone:proxy..0.address..0.wdp");
         default:
            return false;
      }
   }

   private final String abortStatus() {
      StringBuffer result = (StringBuffer)(new Object());
      result.append(" result=").append('"').append("Abort");
      result.append('"');
      return result.toString();
   }

   private final String handleWrite(String curObj, byte[] val) {
      return this.handleWrite(curObj, val, -1);
   }

   private final String handleWrite(String curObj, byte[] val, int offset) {
      if (this._isCriticalError) {
         return this.abortStatus();
      }

      if (curObj.equals("phone:cdma.is683")) {
         EventLogger.logEvent(4411276428801970910L, 1296660339, 5);
         if (offset <= 0 && RadioInternal.processOTASPMessage(0, val)) {
            this._currentResult = "ok";
         } else {
            EventLogger.logEvent(4411276428801970910L, 1296660325, 2);
            this._currentResult = "UnknownError";
            this._errorCount++;
            this._isCriticalError = true;
         }

         EventLogger.logEvent(4411276428801970910L, 1296660324, 5);
      } else if (curObj.equals("phone:cdma.nam.mdn") || curObj.equals("phone:cdma.nam.msid")) {
         EventLogger.logEvent(4411276428801970910L, 1296988019, 5);
         if (offset <= 0 && RadioInternal.processOTASPMessage(2, val)) {
            this._currentResult = "ok";
         } else {
            this._currentResult = "UnknownError";
            this._errorCount++;
            this._isCriticalError = true;
         }

         EventLogger.logEvent(4411276428801970910L, 1296988004, 5);
      } else if (curObj.equals("phone:cdma.prl")) {
         EventLogger.logEvent(4411276428801970910L, 1299214956, 5);
         if (this._provisioningServiceAgent.writeToStorage(curObj, val, offset)) {
            _prlIsDirty = true;
            this._currentResult = "ok";
         } else {
            this._currentResult = "UnknownError";
            this._errorCount++;
            this._isCriticalError = true;
         }
      } else {
         this._errorCount++;
         this._currentResult = "UnknownObject";
      }

      return ((StringBuffer)(new Object("result=\""))).append(this._currentResult).append("\"").toString();
   }

   private final String handleWrite(String curObj, String val) {
      if (this._isCriticalError) {
         return this.abortStatus();
      }

      if (!curObj.equals("phone:cdma.is683")
         && !curObj.equals("phone:cdma.prl")
         && !curObj.equals("phone:cdma.nam.mdn")
         && !curObj.equals("phone:cdma.nam.msid")) {
         if (curObj.equals("phone:boot.naiurl")) {
            this._currentResult = "ok";
         } else if (!curObj.equals("phone:reset")
            && !curObj.equals("browser:reset")
            && !curObj.equals("phone:restartsession")
            && !this._provisioningServiceAgent.writeToStorage(curObj, val)) {
            this._currentResult = "UnknownObject";
            this._errorCount++;
         } else {
            this._currentResult = "ok";
         }

         return ((StringBuffer)(new Object("result=\""))).append(this._currentResult).append("\"").toString();
      } else {
         return this.handleWrite(curObj, val.getBytes());
      }
   }

   private final String handleCommit() {
      if (this._isCriticalError) {
         return this.abortStatus();
      }

      String msg = null;
      if (_prlIsDirty) {
         EventLogger.logEvent(4411276428801970910L, 1299214963, 5);
         Object prl = this._provisioningServiceAgent.readFromStorage("phone:cdma.prl");
         if (!(prl instanceof byte[]) || !RadioInternal.processOTASPMessage(1, (byte[])prl)) {
            msg = "result=\"CheckFailed\"";
            EventLogger.logEvent(4411276428801970910L, 1299214950, 2);
         }

         EventLogger.logEvent(4411276428801970910L, 1299214948, 5);
      }

      this._provisioningServiceAgent.writeToStorage("phone:cdma.prl", new byte[0]);
      this._provisioningServiceAgent.commitStorage();
      _prlIsDirty = false;
      return msg == null ? "result=\"ok\"" : msg;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public final byte[] sendReport() {
      if (this._reportStatus > 0) {
         ByteArrayOutputStream reportStream = (ByteArrayOutputStream)(new Object());
         StringBuffer report = (StringBuffer)(new Object());
         String msg = this._errorCount > 0 ? "warning" : "ok";
         msg = this._isCriticalError ? "error" : msg;
         this._reportHeader.append(msg).append('"');
         if (this._mmcID != -1) {
            this._reportHeader.append(' ').append("mmcID").append('=');
            this._reportHeader.append('"');
            this._reportHeader.append(Integer.toString(this._mmcID)).append('"');
         }

         this._reportHeader.append('>');
         this._errorCount = 0;
         if (this._cidCount > 0) {
            report.append('-').append('-');
            report.append("cDmAbLaCkBeRrYiOtA").append(CR_LF).append("Content-ID: <").append(0);
            report.append("@cdma.blackberry.com").append('>').append(CR_LF).append("Content-Type: ");
            report.append("application/vnd.phonecom.mmc-xml").append(CR_LF).append(CR_LF);
         }

         boolean var11 = false /* VF: Semaphore variable */;

         try {
            var11 = true;
            reportStream.write(report.toString().getBytes());
            reportStream.write(this._reportHeader.toString().getBytes());
            reportStream.write(this._reportBody.toString().getBytes());
            reportStream.write(this._reportFooter.toString().getBytes());
            if (this._cidCount > 0) {
               StringBuffer stBuf = (StringBuffer)(new Object());
               stBuf.append('-').append('-');
               stBuf.append("cDmAbLaCkBeRrYiOtA").append('-');
               stBuf.append('-').append(CR_LF);
               this._byteStream.write(stBuf.toString().getBytes());
               stBuf = null;
               reportStream.write(this._byteStream.toByteArray());
               this._byteStream.flush();
               this._byteStream.close();
               var11 = false;
            } else {
               var11 = false;
            }
         } finally {
            if (var11) {
               throw new Object("Error while writing to byte Stream");
            }
         }

         boolean var8 = false /* VF: Semaphore variable */;

         try {
            var8 = true;
            String ctype;
            if (this._cidCount > 0) {
               StringBuffer buf = (StringBuffer)(new Object());
               buf.append("multipart/related");
               buf.append("; boundary=").append("cDmAbLaCkBeRrYiOtA").append("; type=\"");
               buf.append("application/vnd.phonecom.mmc-xml").append('"');
               ctype = buf.toString();
            } else {
               ctype = "application/vnd.phonecom.mmc-xml";
            }

            EventLogger.logEvent(4411276428801970910L, 1299411826);
            byte[] response = this._httpUserAgent.post(this._statusURI, ctype, reportStream.toByteArray());
            reportStream.flush();
            reportStream.close();
            if (!this._disconnect) {
               return response;
            }

            var8 = false;
         } finally {
            if (var8) {
               throw new Object("Error in sending Status Report");
            }
         }
      }

      EventLogger.logEvent(4411276428801970910L, 1298426227);
      return null;
   }

   public final void cancelParsing() {
      throw new Object("Parsing Cancelled");
   }
}
