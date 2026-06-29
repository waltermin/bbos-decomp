package net.rim.device.apps.internal.mms.service;

import net.rim.device.api.io.MIMETypeAssociations;
import net.rim.device.api.io.http.HttpHeaders;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.internal.mms.MMSStorage;
import net.rim.device.apps.internal.mms.MMSUtilities;
import net.rim.device.apps.internal.mms.api.MMSMessageModel;
import net.rim.device.apps.internal.mms.api.MMSTask;
import net.rim.device.apps.internal.mms.model.MMSMessageModelBuilder;
import net.rim.device.apps.internal.mms.options.MMSClientServiceBook;
import net.rim.device.apps.internal.mms.options.MMSOptions;
import net.rim.device.apps.internal.mms.options.MMSTransportServiceBook;
import net.rim.device.cldc.io.mms.MultipartMessageImpl;
import net.rim.device.cldc.io.mms.Protocol;
import net.rim.device.cldc.io.waphttp.WAPIOException;

final class RetrieveMessageContentTask implements MMSTask {
   private MMSMessageModel _message;
   private boolean _autoDownload;
   private boolean _retry;
   private Runnable _onSuccess;
   private Runnable _onFailure;
   private int _attempts;
   private static final int MAX_ATTEMPTS = 10;
   private static final int FIRST_RETRY_DELAY = 10;
   private static final int LAST_RETRY_DELAY = 600;
   private static final int RETRY_SCALE_FACTOR = 8;

   RetrieveMessageContentTask(MMSMessageModel message, boolean autoDownload, Runnable onSuccess, Runnable onFailure, boolean retry) {
      this._message = message;
      this._autoDownload = autoDownload;
      this._onSuccess = onSuccess;
      this._onFailure = onFailure;
      this._retry = retry;
   }

   @Override
   public final long getTaskThreadGuid() {
      return -1385708141614342777L;
   }

   @Override
   public final boolean requiresRadioCoverage() {
      return true;
   }

   @Override
   public final void run() {
      String url = this._message.getPayload().getAttribute("x-mms-content-location");
      if (!MMSUtilities.canRequestContent(this._message)) {
         this.notifyOnCompletion(false);
      } else {
         if (MMSClientServiceBook.getRetrievalUrlScheme() == 1) {
            if (url == null || url.length() == 0) {
               String var4 = MMSTransportServiceBook.getMessageUrlPrefix();
               url = ((StringBuffer)(new Object())).append(var4).append(this._message.getPayload().getAttribute("x-mms-transaction-id")).toString();
               System.out.println(((StringBuffer)(new Object("empty url mapped to: "))).append(url).toString());
            } else if (url.endsWith("=")) {
               url = ((StringBuffer)(new Object())).append(url).append(this._message.getPayload().getAttribute("x-mms-transaction-id")).toString();
               System.out.println(((StringBuffer)(new Object("partial url mapped to: "))).append(url).toString());
            }
         }

         url = ((StringBuffer)(new Object())).append(url).append(MMSTransportServiceBook.getMMSCConnectionParameters()).toString();
         boolean isDone = this.readContent(url);
         if (!isDone) {
            if (MMSUtilities.isPermanentFailure(this._message)) {
               System.out.println("MMS Retrieve failed. Permanent failure.");
            } else if (!this._retry) {
               System.out.println("MMS Retrieve failed. No retry.");
            } else {
               this._attempts++;
               if (this._attempts < 10) {
                  int delay = this.getRetryDelay();
                  System.out.println(((StringBuffer)(new Object("MMS Retrieve failed. Retry in "))).append(delay).toString());
                  BackgroundTaskThread.addTask(this, delay);
                  return;
               }

               System.out.println("MMS Retrieve failed. Exceeded retry maximum.");
            }
         }

         this.notifyOnCompletion(isDone);
      }
   }

   private final int getRetryDelay() {
      return (this._attempts - 1) * this._attempts * 8 + 10;
   }

   private final void notifyOnCompletion(boolean success) {
      String id = this._message.getPayload().getAttribute("x-mms-transaction-id");
      String ackUrl = MMSServiceUtil.inferMMSCAcknowledgementUrl(this._message);
      if (success) {
         if (this._autoDownload) {
            BackgroundTaskThread.addTask(new SendNotificationResponseTask(ackUrl, id, 129));
         } else {
            BackgroundTaskThread.addTask(new SendDeliveryAcknowledgementTask(ackUrl, id));
         }

         if (this._message.isOpened()) {
            this._message.perform(5803508244060051872L, null);
         }

         if (this._onSuccess != null) {
            this._onSuccess.run();
            return;
         }
      } else {
         if (this._autoDownload) {
            BackgroundTaskThread.addTask(new SendNotificationResponseTask(ackUrl, id, 131));
         }

         if (this._onFailure != null) {
            this._onFailure.run();
         }
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final boolean readContent(String url) {
      try {
         MMSServiceUtil.updateMessageStatus(this._message, 4095);
         HttpReader reader = new HttpReader(url);
         int rc = reader.read();
         byte[] data = reader.getResponseData();
         HttpHeaders headers = reader.getResponseHeaders();
         String contentType = headers.getPropertyValue("content-type");
         if (data.length > 0 && contentType != null) {
            MMSMessageModelBuilder builder = new MMSMessageModelBuilder(this._message);
            builder.setAttributes(headers);
            String normalizedContentType = MIMETypeAssociations.getNormalizedType(contentType);
            int updatedStatus;
            if (StringUtilities.compareToIgnoreCase(normalizedContentType, "application/vnd.wap.mms-message", 1701707776) != 0) {
               builder.addAttachment("net_rim_RetrieveError", MMSUtilities.getMIMEType(contentType), data, null);
               updatedStatus = 1;
               System.out.println(((StringBuffer)(new Object("MMS: retrieve failed (contentType="))).append(contentType).append(')').toString());
            } else {
               try {
                  DataBuffer dataBuffer = (DataBuffer)(new Object(data, 0, data.length, false));
                  MMSProtocolDataUnit pdu = new MMSProtocolDataUnit(dataBuffer);
                  if (MMSOptions.getInstance().getOptionFlag(512)) {
                     pdu.dumpFields();
                  }

                  if (pdu.getType() != 132 || pdu.isTruncated()) {
                     MMSServiceUtil.updateMessageStatus(this._message, 511);
                     System.out.println("MMS ReadContent truncated");
                     return false;
                  }

                  String priority = pdu.getAttribute("x-mms-priority");
                  if (priority != null) {
                     builder.setAttribute("x-mms-priority", priority);
                  }

                  String readReport = pdu.getAttribute("x-mms-read-report");
                  if (readReport != null) {
                     builder.setAttribute("x-mms-read-report", readReport);
                  }

                  int drmStatus = MMSUtilities.parseInt(pdu.getAttribute("drm-status"), 0);
                  if (pdu.isForwardLocked() || (drmStatus & 1) != 0) {
                     builder.setForwardLocked();
                     data = MMSUtilities.encrypt(data);
                  }

                  builder.addRecipients(pdu.getRecipients());
                  builder.addCcRecipients(pdu.getCcRecipients());
                  String from = pdu.getAttribute("from");
                  if (from != null) {
                     builder.setSender(from);
                  }

                  String subject = pdu.getAttribute("subject");
                  if (subject != null) {
                     builder.setSubject(subject);
                  }

                  String date = pdu.getAttribute("date");
                  if (date != null) {
                     builder.setAttribute("date", date);
                  }

                  String tID = pdu.getAttribute("x-mms-transaction-id");
                  if (tID != null) {
                     builder.setAttribute("x-mms-transaction-id", tID);
                  }

                  String msgID = pdu.getAttribute("message-id");
                  if (msgID != null) {
                     builder.setAttribute("message-id", msgID);
                  }

                  Protocol protocol = Protocol.getConnection(pdu.getAttribute("content-type"));
                  if (protocol != null) {
                     label289:
                     try {
                        MultipartMessageImpl msg = Protocol.createWMAMessage(builder.getResult(), pdu.getAttribute("content-type"));
                        protocol.mmsMessageReceived(msg);
                        if (MMSStorage.isFiled(this._message)) {
                           MMSStorage.removeMessage(this._message);
                        }

                        this._onSuccess = null;
                        return true;
                     } finally {
                        break label289;
                     }
                  }
               } catch (Throwable var32) {
                  label300: {
                     if (e instanceof Object) {
                        throw e;
                     }

                     System.out.println("MMS: parse error on retrieval.");
                     break label300;
                  }
               }

               builder.removeAllAttachments();
               builder.addAttachment("net_rim_ProtocolDataUnit", 62, data, null);
               updatedStatus = 2047;
               rc = 0;
            }

            builder.commitResult();
            MMSServiceUtil.updateMessageStatus(this._message, updatedStatus, rc, 0, 0, 0);
            return updatedStatus == 2047;
         } else {
            MMSServiceUtil.updateMessageStatus(this._message, 1, rc, 0, 0, 0);
            return false;
         }
      } catch (Throwable var33) {
         if (e instanceof Object) {
            MMSServiceUtil.updateMessageStatus(this._message, 1023);
            System.out.println("MMS ReadContent OutOfMemory");
            return false;
         } else if (e instanceof Object) {
            int status = MMSUtilities.hasDataCoverage() ? 1 : 511;
            WAPIOException ioe = (WAPIOException)e;
            MMSServiceUtil.updateMessageStatus(this._message, status, 0, 0, ioe.getError(), ioe.getAdditionalData());
            System.out
               .println(
                  ((StringBuffer)(new Object("MMS ReadContent WAPIOException "))).append(ioe.getError()).append(' ').append(ioe.getAdditionalData()).toString()
               );
            return false;
         } else {
            int status = MMSUtilities.hasDataCoverage() ? 1 : 511;
            MMSServiceUtil.updateMessageStatus(this._message, status);
            System.out.println(((StringBuffer)(new Object("MMS ReadContent "))).append(e.toString()).toString());
            return false;
         }
      }
   }
}
