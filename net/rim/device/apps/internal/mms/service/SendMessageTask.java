package net.rim.device.apps.internal.mms.service;

import net.rim.device.api.io.MIMETypeAssociations;
import net.rim.device.api.io.http.HttpHeaders;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.internal.mms.MMSStorage;
import net.rim.device.apps.internal.mms.MMSUtilities;
import net.rim.device.apps.internal.mms.api.AttachmentDataProvider;
import net.rim.device.apps.internal.mms.api.MMSAttachment;
import net.rim.device.apps.internal.mms.api.MMSMessageModel;
import net.rim.device.apps.internal.mms.api.MMSTask;
import net.rim.device.apps.internal.mms.options.MMSTransportServiceBook;
import net.rim.device.cldc.io.waphttp.WAPIOException;

final class SendMessageTask implements MMSTask {
   private MMSMessageModel _message;
   private boolean _cancelSendIfDeleted;
   private String _url;
   private HttpHeaders _headers;
   private byte[] _pdu;
   private int _attempts;
   private static final int MAX_ATTEMPTS = 10;
   private static final int FIRST_RETRY_DELAY = 10;
   private static final int LAST_RETRY_DELAY = 600;
   private static final int RETRY_SCALE_FACTOR = 8;

   public SendMessageTask(MMSMessageModel message, String url, String authHeader, boolean cancelSendIfDeleted) {
      this._message = message;
      this._url = url;
      this._cancelSendIfDeleted = cancelSendIfDeleted;
      AttachmentDataProvider attachmentProvider = message.getAttachmentDataProvider();
      MMSAttachment pduAttachment = attachmentProvider.getAttachment("net_rim_ProtocolDataUnit");
      if (pduAttachment != null) {
         this._pdu = pduAttachment.getData();
      } else {
         ContextObject context = (ContextObject)(new Object());
         this._pdu = MMSServiceUtil.buildSendRequestPdu(message.getPayload(), attachmentProvider, context);
      }

      if (this._pdu != null) {
         this._headers = MMSHttpUtilities.getStandardSendHeaders(this._pdu.length);
         if (authHeader != null) {
            MMSHttpUtilities.addAuthenticationHeaders(this._headers, authHeader);
         }
      }
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
      if (this._cancelSendIfDeleted && this.isMessageDeleted()) {
         System.out.println("Send canceled due to deletion.");
      } else {
         label94:
         try {
            String url = ((StringBuffer)(new Object())).append(this._url).append(MMSTransportServiceBook.getMMSCConnectionParameters()).toString();
            MMSServiceUtil.updateMessageStatus(this._message, 67108863);
            String transactionID = this._message.getPayload().getAttribute("x-mms-transaction-id");
            HttpSender sender = new HttpSender(url, this._pdu, this._headers);
            int rc = sender.send();
            if (rc != 200 && rc != 204) {
               MMSServiceUtil.updateMessageStatus(this._message, 8191, rc, 0, 0, 0);
            } else {
               byte[] responseData = sender.getResponseData();
               String responseType = sender.getResponseType();
               if (responseType != null) {
                  responseType = MIMETypeAssociations.getNormalizedType(responseType);
               }

               if (responseData != null && "application/vnd.wap.mms-message".equals(responseType)) {
                  MMSProtocolDataUnit pdu = new MMSProtocolDataUnit(responseData);
                  MMSServiceUtil.processPDU(pdu, transactionID);
                  String responseStatus = pdu.getAttribute("x-mms-response-status");
                  int status = MMSUtilities.parseInt(responseStatus, 128);
                  if (status == 128) {
                     MMSServiceUtil.updateMessageStatus(this._message, 33554431);
                  } else {
                     MMSServiceUtil.updateMessageStatus(this._message, 8191, 0, status, 0, 0);
                  }
               } else {
                  System.out.println(((StringBuffer)(new Object("MMS Send Failed (no pdu): "))).append(responseType).toString());
                  MMSServiceUtil.updateMessageStatus(this._message, 8191);
               }
            }
         } catch (Throwable var11) {
            int status = MMSUtilities.hasDataCoverage() ? 16383 : 131071;
            if (!(e instanceof Object)) {
               MMSServiceUtil.updateMessageStatus(this._message, status);
            } else {
               WAPIOException ioe = (WAPIOException)e;
               MMSServiceUtil.updateMessageStatus(this._message, status, 0, 0, ioe.getError(), ioe.getAdditionalData());
            }
            break label94;
         }

         if (this._message.isSuccessfullySent()) {
            System.out.println("MMS SendTask successful");
         } else if (MMSUtilities.isPermanentFailure(this._message)) {
            System.out.println("MMS SendTask failed - permanent failure.");
         } else {
            this._attempts++;
            if (this._attempts < 10) {
               MMSServiceUtil.updateMessageStatus(this._message, 134217727);
               int delay = this.getRetryDelay();
               System.out.println(((StringBuffer)(new Object("MMS SendTask failed. Retry in "))).append(delay).toString());
               BackgroundTaskThread.addTask(this, delay);
            } else {
               System.out.println("MMS SendTask failed. Exceeded retry maximum.");
            }
         }
      }
   }

   private final boolean isMessageDeleted() {
      return !MMSStorage.isFiled(this._message);
   }

   private final int getRetryDelay() {
      return (this._attempts - 1) * this._attempts * 8 + 10;
   }
}
