package net.rim.device.apps.internal.bis.launch.ota;

import java.util.Hashtable;
import net.rim.device.api.system.CodeModuleManager;
import net.rim.device.apps.internal.bis.launch.config.BISClientConfigRecord;
import net.rim.device.apps.internal.bis.launch.http.HttpClient;
import net.rim.device.apps.internal.bis.launch.http.HttpListener;
import net.rim.device.apps.internal.bis.launch.http.HttpResponse;
import net.rim.vm.Memory;

public final class ApplicationDownloadManager implements Runnable {
   private String[] _codUrls;
   private byte[][] _digests;
   private ApplicationDownloadListener _downloadListener;
   private HttpListener _httpListener;
   private boolean _aborted;
   private static final String COD_MIME_TYPE = "application/vnd.rim.cod";
   public static final int DL_STATUS_FAILED = 0;
   public static final int DL_STATUS_SUCCESS = 1;
   public static final int DL_STATUS_ABORTED = 2;
   private static final int DOWNLOAD_PROGRESS_PERCENT = 1;
   public static int _transactionStatus;

   public final synchronized void abort() {
      this._aborted = true;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void run() {
      boolean downloadSucceeded = false;
      boolean rebootRequired = false;
      HttpResponse codDownloadResponse = null;
      String transportUID = BISClientConfigRecord.getBISClientConfigRecord().getTransportUID();
      long downloadTimeout = BISClientConfigRecord.getBISClientConfigRecord().getServiceTimeout();
      HttpClient client = new HttpClient(transportUID, downloadTimeout, this._httpListener, 1, false);
      Hashtable requestProperties = new Hashtable();
      requestProperties.put("Accept", "application/vnd.rim.cod");
      int numCods = this._codUrls.length;
      int lastCodIndex = numCods - 1;
      byte[][] codsBytes = new byte[numCods][];
      int[] moduleHandles = new int[numCods];
      int numInstalledModules = 0;
      int transactionHandle = 0;
      boolean var24 = false /* VF: Semaphore variable */;

      try {
         var24 = true;

         for (int e = 0; e < numCods; e++) {
            synchronized (this) {
               if (this._aborted) {
                  break;
               }
            }

            this._downloadListener.startingDownload();
            codDownloadResponse = client.doHttpExchange(this._codUrls[e], "GET", requestProperties, null, null, true);
            if (codDownloadResponse.getHttpResponseCode() != 200) {
               break;
            }

            byte[] codData = codDownloadResponse.getResponsePayload();
            if (codData == null || codData.length <= 0) {
               break;
            }

            codsBytes[e] = codData;
            if (e == lastCodIndex) {
               downloadSucceeded = true;
            }
         }

         synchronized (this) {
            boolean installSucceeded = false;
            System.gc();
            if (downloadSucceeded && !this._aborted) {
               this._downloadListener.startingInstallation();
               transactionHandle = CodeModuleManager.beginTransaction();

               for (int i = 0; i < numCods; i++) {
                  int handle = CodeModuleManager.createNewModule(codsBytes[i].length, codsBytes[i], codsBytes[i].length);
                  if (handle == 0) {
                     break;
                  }

                  int installationStatus = CodeModuleManager.saveNewModule(handle, true, transactionHandle);
                  if (installationStatus == 0 || installationStatus == 1) {
                     moduleHandles[i] = handle;
                     numInstalledModules++;
                  } else if (installationStatus == 6) {
                     moduleHandles[i] = handle;
                     numInstalledModules++;
                     rebootRequired = true;
                  }

                  if (numInstalledModules == numCods) {
                     installSucceeded = true;
                  }
               }
            }

            if (downloadSucceeded && installSucceeded) {
               Memory.maximizeContiguousRAM();
               int transactionStatus = CodeModuleManager.endTransaction(transactionHandle);
               _transactionStatus = transactionStatus;
               if (transactionStatus == 13) {
                  this._downloadListener.finishedInstallation(0, null, rebootRequired);
               } else {
                  rebootRequired = transactionStatus == 15;
                  this._downloadListener.finishedInstallation(1, null, rebootRequired);
               }
            } else {
               for (int i = 0; i < numInstalledModules; i++) {
                  CodeModuleManager.deleteNewModule(moduleHandles[i]);
               }

               CodeModuleManager.cancelTransaction(transactionHandle);
               this._downloadListener.finishedInstallation(this._aborted ? 2 : 0, null, rebootRequired);
            }

            var24 = false;
         }
      } finally {
         if (var24) {
            if (transactionHandle > 0) {
               CodeModuleManager.cancelTransaction(transactionHandle);
            }

            this._downloadListener.finishedInstallation(0, null, rebootRequired);
            return;
         }
      }
   }

   public ApplicationDownloadManager(String[] codUrls, byte[][] digests, ApplicationDownloadListener downloadListener, HttpListener httpListener) {
      this._codUrls = codUrls;
      this._digests = digests;
      this._downloadListener = downloadListener;
      this._httpListener = httpListener;
   }
}
