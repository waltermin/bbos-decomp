package net.rim.device.apps.internal.bis.launch.ota;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.system.Application;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.GaugeField;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.container.PopupScreen;
import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.apps.internal.bis.launch.http.HttpListener;
import net.rim.vm.Memory;

public final class ApplicationDownloadField extends PopupScreen implements ApplicationDownloadListener, HttpListener, FieldChangeListener {
   private RichTextField _statusText;
   private GaugeField _statusGauge;
   private ButtonField _cancelButton;
   private int _downloadSize;
   private int _totalBytesRead;
   private boolean _rebootRequired;
   private String[] _codUrls;
   private byte[][] _digests;
   private boolean _isDownloading;
   private Thread _downloadThread;
   private ApplicationDownloadManager _downloadManager;
   private int _downloadStatus;
   public static final int DOWNLOAD_STATUS_FAILED = 0;
   public static final int DOWNLOAD_STATUS_SUCCESS = 1;
   public static final int DOWNLOAD_STATUS_CANCELLED = 2;
   public static final int DOWNLOAD_STATUS_NO_MEMORY = 3;
   private static final int LMM_ADDITIONAL_MEMORY_CONSTANT_FACTOR = 131072;

   public ApplicationDownloadField(String[] codUrls, byte[][] digests, int size) {
      super((Manager)(new Object()), 0);
      if (codUrls != null && digests != null && codUrls.length == digests.length) {
         this._codUrls = codUrls;
         this._digests = digests;
         this._downloadSize = size;
         ResourceBundle bundle = ResourceBundle.getBundle(1322930605485095732L, "net.rim.device.apps.internal.bis.launch.resource.BISLaunch");
         this._statusText = (RichTextField)(new Object(bundle.getString(0), 36028797018963968L));
         this._statusGauge = (GaugeField)(new Object(null, 0, this._downloadSize, 0, 2));
         this._cancelButton = (ButtonField)(new Object(CommonResources.getString(9042), 12884967424L));
         this._cancelButton.setChangeListener(this);
         this.add(this._statusText);
         this.add(this._statusGauge);
         this.add(this._cancelButton);
      } else {
         throw new Object();
      }
   }

   public final void doDownload() {
      if (Memory.getFlashFree() < this._downloadSize + 131072) {
         this._downloadStatus = 3;
      } else {
         UiApplication.getUiApplication().pushModalScreen(this);
      }
   }

   @Override
   public final void onDisplay() {
      super.onDisplay();
      this._isDownloading = true;
      this._downloadManager = new ApplicationDownloadManager(this._codUrls, this._digests, this, this);
      this._downloadThread = (Thread)(new Object(this._downloadManager));
      this._downloadThread.start();
      this._cancelButton.setFocus();
   }

   @Override
   protected final boolean keyChar(char c, int status, int time) {
      if (c != 27 && c != '\n') {
         return super.keyChar(c, status, time);
      }

      this.fieldChanged(this._cancelButton, 0);
      return true;
   }

   public final boolean isRebootRequired() {
      return this._rebootRequired;
   }

   public final int getDownloadStatus() {
      return this._downloadStatus;
   }

   @Override
   public final void startingDownload() {
   }

   @Override
   public final void startingInstallation() {
   }

   @Override
   public final void finishedInstallation(int reason, String error, boolean rebootRequired) {
      synchronized (Application.getEventLock()) {
         this._statusGauge.setValue(this._statusGauge.getValueMax());
         this._rebootRequired = this._rebootRequired || rebootRequired;
         switch (reason) {
            case -1:
               break;
            case 0:
               this._downloadStatus = 0;
               break;
            case 1:
            default:
               this._downloadStatus = 1;
               break;
            case 2:
               this._downloadStatus = 2;
         }

         UiApplication.getUiApplication().popScreen(this);
      }
   }

   @Override
   public final void connectionEstablished(boolean connectionWasEstablished) {
   }

   @Override
   public final void connectionClosed(int reason) {
   }

   @Override
   public final void sentRequest() {
   }

   @Override
   public final void readingResponse(int size) {
   }

   @Override
   public final void readResponseProgressUpdate(int bytesRead) {
      this._totalBytesRead += bytesRead;
      this._statusGauge.setValue(this._totalBytesRead);
   }

   @Override
   public final void finishedReadingResponse() {
   }

   @Override
   public final void fieldChanged(Field field, int context) {
      if (field == this._cancelButton && this._isDownloading && this._downloadThread != null && this._downloadThread.isAlive()) {
         this._downloadManager.abort();
      }
   }
}
