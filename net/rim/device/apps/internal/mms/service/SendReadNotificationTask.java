package net.rim.device.apps.internal.mms.service;

import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.i18n.SimpleDateFormat;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.internal.mms.MMSUtilities;
import net.rim.device.apps.internal.mms.api.MMSAttachment;
import net.rim.device.apps.internal.mms.api.MMSMessageModel;
import net.rim.device.apps.internal.mms.api.MMSPayloadModel;
import net.rim.device.apps.internal.mms.api.MMSTask;
import net.rim.device.apps.internal.mms.model.MMSAttachmentImpl;
import net.rim.device.apps.internal.mms.options.MMSClientServiceBook;
import net.rim.device.apps.internal.mms.options.MMSTransportServiceBook;
import net.rim.device.apps.internal.mms.resources.MMSResources;
import net.rim.device.cldc.io.waphttp.WAPIOException;

final class SendReadNotificationTask implements MMSTask {
   private String _url;
   private MMSMessageModel _message;
   private int _status;
   private int _attempts;
   private static final int MAX_ATTEMPTS;
   private static final int FIRST_RETRY_DELAY;
   private static final int LAST_RETRY_DELAY;
   private static final int RETRY_SCALE_FACTOR;

   SendReadNotificationTask(String url, MMSMessageModel message, int status) {
      this._url = url;
      this._message = message;
      this._status = status;
   }

   @Override
   public final long getTaskThreadGuid() {
      return -1627158063255138358L;
   }

   @Override
   public final boolean requiresRadioCoverage() {
      return true;
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void run() {
      String url = this._url;
      if (url == null || url.length() == 0) {
         System.out.println("Can't send without MMSC url.");
      } else if (this._message.getPayload().getSender() == null) {
         System.out.println("Can't send read report without sender info.");
      } else {
         url = ((StringBuffer)(new Object())).append(url).append(MMSTransportServiceBook.getMMSCConnectionParameters()).toString();
         System.out.println(((StringBuffer)(new Object("MMS ReadNotify sending to "))).append(url).toString());
         int status = 0;
         int rc = 0;
         int wapIOExceptionError = 0;
         int wapIOExceptionAdditionalData = 0;

         label74:
         try {
            HttpSender sender = new HttpSender(url, this.buildResponse());
            rc = sender.send();
            if (rc != 200 && rc != 204) {
               status = 8191;
            } else {
               status = 33554431;
            }
         } catch (Throwable var9) {
            status = MMSUtilities.hasDataCoverage() ? 8191 : 131071;
            if (e instanceof Object) {
               WAPIOException ioe = (WAPIOException)e;
               wapIOExceptionError = ioe.getError();
               wapIOExceptionAdditionalData = ioe.getAdditionalData();
            }
            break label74;
         }

         if (status == 33554431) {
            System.out.println("MMS ReadNotify successful.");
         } else if (MMSUtilities.isPermanentFailure(status, rc, 0, wapIOExceptionError, wapIOExceptionAdditionalData)) {
            System.out.println("MMS ReadNotify failed. Permanent Failure");
         } else {
            this._attempts++;
            if (this._attempts < 5) {
               int delay = this.getRetryDelay();
               System.out.println(((StringBuffer)(new Object("MMS ReadNotify failed. Retry in "))).append(delay).toString());
               BackgroundTaskThread.addTask(this, delay);
            } else {
               System.out.println("MMS ReadNotify failed. Exceeded retry maximum.");
            }
         }
      }
   }

   private final int getRetryDelay() {
      return (this._attempts - 1) * this._attempts * 24 + 10;
   }

   private final byte[] buildResponse() {
      ContextObject context = (ContextObject)(new Object(74));
      if (this._message.isSmartDialed()) {
         context.setFlag(117);
      }

      return MMSClientServiceBook.getMMSCVersion() == 16 ? this.buildResponse_1_0(context) : this.buildResponse_1_1(context);
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final byte[] buildResponse_1_1(Object context) {
      DataBuffer dataBuffer = (DataBuffer)(new Object());

      try {
         MMSProtocolDataUnitWriter writer = new MMSProtocolDataUnitWriter(dataBuffer, 135);
         writer.writeMMSVersion();
         writer.writeMessageID(this._message.getPayload().getAttribute("message-id"));
         writer.writeFrom(context);
         writer.writeTo(MMSUtilities.addressToString(this._message.getPayload().getSender(), context));
         writer.writeReadStatus(this._status);
      } catch (Throwable var5) {
         System.out.println(((StringBuffer)(new Object("ReadNotify.buildResponse "))).append(e.toString()).toString());
         return dataBuffer.toArray();
      }

      return dataBuffer.toArray();
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final byte[] buildResponse_1_0(Object context) {
      MMSPayloadModel payload = this._message.getPayload();
      DataBuffer dataBuffer = (DataBuffer)(new Object());

      try {
         MMSProtocolDataUnitWriter writer = new MMSProtocolDataUnitWriter(dataBuffer, 128);
         writer.writeTransactionID(Long.toString(System.currentTimeMillis()));
         writer.writeMMSVersion();
         String subject = payload.getAttribute("subject");
         if (subject == null) {
            subject = "";
         }

         subject = ((StringBuffer)(new Object())).append(MMSResources.getString(89)).append(subject).toString();
         writer.writeSubject(subject);
         writer.writeFrom(context);
         writer.writeTo(MMSUtilities.addressToString(this._message.getPayload().getSender(), context));
         writer.writeMessageClass(131);
         writer.beginContent(1);
         writer.addAttachment(this.buildNotificationBody());
         writer.endContent();
      } catch (Throwable var7) {
         System.out.println(((StringBuffer)(new Object("buildPDU "))).append(e.toString()).toString());
         return dataBuffer.toArray();
      }

      return dataBuffer.toArray();
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final MMSAttachment buildNotificationBody() {
      String attachmentName = "read.txt";
      int attachmentType = 3;
      SimpleDateFormat timeFormat = (SimpleDateFormat)(new Object(7));
      SimpleDateFormat dateFormat = (SimpleDateFormat)(new Object(56));
      long readTime = System.currentTimeMillis();
      long sendTime = this._message.getPayload().getCreationDate();
      String readDate = ((StringBuffer)(new Object())).append(dateFormat.formatLocal(readTime)).append(' ').append(timeFormat.formatLocal(readTime)).toString();
      String sendDate = ((StringBuffer)(new Object())).append(dateFormat.formatLocal(sendTime)).append(' ').append(timeFormat.formatLocal(sendTime)).toString();
      String statusString = MMSResources.getString(this._status == 128 ? 91 : 92);
      String template = MMSResources.getString(90);
      String body = MessageFormat.format(template, new Object[]{sendDate, statusString, readDate});
      boolean var18 = false /* VF: Semaphore variable */;

      String charset;
      byte[] data;
      try {
         var18 = true;
         charset = "utf-8";
         data = body.getBytes(charset);
         var18 = false;
      } finally {
         if (var18) {
            charset = null;
            data = body.getBytes();
            return new MMSAttachmentImpl(attachmentName, attachmentType, data, charset);
         }
      }

      return new MMSAttachmentImpl(attachmentName, attachmentType, data, charset);
   }
}
