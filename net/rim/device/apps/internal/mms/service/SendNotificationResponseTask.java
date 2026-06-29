package net.rim.device.apps.internal.mms.service;

import net.rim.device.api.util.DataBuffer;
import net.rim.device.apps.internal.mms.MMSUtilities;
import net.rim.device.apps.internal.mms.api.MMSTask;
import net.rim.device.apps.internal.mms.options.MMSClientServiceBook;
import net.rim.device.apps.internal.mms.options.MMSOptions;
import net.rim.device.apps.internal.mms.options.MMSTransportServiceBook;
import net.rim.device.cldc.io.waphttp.WAPIOException;

final class SendNotificationResponseTask implements MMSTask {
   private String _url;
   private String _transactionID;
   private int _status;
   private int _attempts;
   private static final int MAX_ATTEMPTS;
   private static final int FIRST_RETRY_DELAY;
   private static final int LAST_RETRY_DELAY;
   private static final int RETRY_SCALE_FACTOR;

   SendNotificationResponseTask(String url, String transactionID, int status) {
      this._url = url;
      this._transactionID = transactionID;
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
      if (url != null && url.length() != 0) {
         url = ((StringBuffer)(new Object())).append(url).append(MMSTransportServiceBook.getMMSCConnectionParameters()).toString();
         System.out.println(((StringBuffer)(new Object("MMS NotifyResp.ind sending to "))).append(url).toString());
         int status = 0;
         int rc = 0;
         int wapIOExceptionError = 0;
         int wapIOExceptionAdditionalData = 0;

         label70:
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
            break label70;
         }

         if (status == 33554431) {
            System.out.println("MMS NotifyResp.ind successful.");
         } else if (MMSUtilities.isPermanentFailure(status, rc, 0, wapIOExceptionError, wapIOExceptionAdditionalData)) {
            System.out.println("MMS NotifyResp.ind failed. Permanent Failure");
         } else {
            this._attempts++;
            if (this._attempts < 5) {
               int delay = this.getRetryDelay();
               System.out.println(((StringBuffer)(new Object("MMS NotifyResp.ind failed. Retry in "))).append(delay).toString());
               BackgroundTaskThread.addTask(this, delay);
            } else {
               System.out.println("MMS NotifyResp.ind failed. Exceeded retry maximum.");
            }
         }
      } else {
         System.out.println("Can't send notification response without MMSC url.");
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final byte[] buildResponse() {
      DataBuffer dataBuffer = (DataBuffer)(new Object());
      MMSOptions options = MMSOptions.getInstance();

      try {
         MMSProtocolDataUnitWriter writer = new MMSProtocolDataUnitWriter(dataBuffer, 131);
         writer.writeTransactionID(this._transactionID);
         writer.writeMMSVersion();
         writer.writeStatus(this._status);
         if (MMSClientServiceBook.isLockedOption(1)) {
            if ((MMSClientServiceBook.getDefaultOptionFlags() & 2) != 0) {
               writer.writeReportAllowed(129);
            }
         } else if (options.getOptionFlag(1)) {
            writer.writeReportAllowed(129);
         }
      } catch (Throwable var5) {
         System.out.println(((StringBuffer)(new Object("SendTask.buildResponse "))).append(e.toString()).toString());
         return dataBuffer.toArray();
      }

      return dataBuffer.toArray();
   }

   private final int getRetryDelay() {
      return (this._attempts - 1) * this._attempts * 24 + 10;
   }
}
