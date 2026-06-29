package net.rim.device.apps.internal.browser.download;

import net.rim.device.api.io.file.FileIOException;
import net.rim.device.api.system.Application;
import net.rim.device.api.ui.component.GaugeField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.apps.internal.browser.resources.BrowserResources;

class DownloadPage$DownloadItem extends VerticalFieldManager implements SavingDownloadManagerListener {
   private SavingDownloadManager _dm;
   private GaugeField _gauge;
   private LabelField _speed;
   private boolean _success;
   private DownloadPage$DownloadItem$UpdateRunner _progressUpdater;
   private int _progressUpdaterId;
   private final DownloadPage this$0;

   public DownloadPage$DownloadItem(DownloadPage _1, SavingDownloadManager dm) {
      super(1152921504606846976L);
      this.this$0 = _1;
      this._progressUpdaterId = -1;
      this._dm = dm;
      this._progressUpdater = new DownloadPage$DownloadItem$UpdateRunner(this);
      dm.setListener(this);
      this.setBorder(new FocusBorder(this, 2, 2, 2, 2));
      this.add(new LabelField(dm.getFilename(), 18014398509481984L));
      this._gauge = new GaugeField();
      this._speed = new LabelField("");
      this.add(this._gauge);
      this.add(this._speed);
   }

   public String getSaveLocation() {
      return this._dm.getUrl();
   }

   @Override
   public void onDisplay() {
      super.onDisplay();
      this._progressUpdaterId = Application.getApplication().invokeLater(this._progressUpdater, 1000, true);
   }

   @Override
   public void onUndisplay() {
      super.onUndisplay();
      if (this._progressUpdaterId != -1) {
         Application.getApplication().cancelInvokeLater(this._progressUpdaterId);
         this._progressUpdaterId = -1;
      }
   }

   @Override
   public void error(int responseCode) {
      this.handleError(BrowserResources.getString(685));
   }

   @Override
   public void error(Throwable t) {
      if (t instanceof FileIOException) {
         this.handleError(t.getMessage());
      } else {
         this.handleError(BrowserResources.getString(685));
      }
   }

   @Override
   public void error(String errorMessage) {
      this.handleError(errorMessage);
   }

   @Override
   protected void makeMenu(Menu menu, int instance) {
      if (this._gauge == null) {
         menu.add(new DownloadPage$RemoveMenuItem(this.this$0, this, this._dm.getUrl()));
      }

      if (this._success) {
         menu.add(new DownloadPage$OpenMenuItem(this.this$0, this._dm.getUrl()));
      }
   }

   private void handleError(String errorMessage) {
      Application.getApplication().invokeLater(new DownloadPage$DownloadItem$1(this, errorMessage));
   }

   @Override
   public void progressCompleted() {
      if (this._dm.getDownloadedSize() >= this._dm.getTotalSize()) {
         this._success = true;
         Application.getApplication().invokeLater(new DownloadPage$DownloadItem$2(this));
      } else {
         this.handleError(BrowserResources.getString(685));
      }
   }

   private String getTransferRate(long tr) {
      if (!Float.isInfinite((float)tr)) {
         double modifiedTr = tr;
         boolean isKB = false;
         if (modifiedTr > 4652218415073722368L) {
            isKB = true;
            modifiedTr /= 4652218415073722368L;
         }

         String floatStr = Double.toString(modifiedTr);
         int dotIndex = floatStr.indexOf(46);
         if (dotIndex != -1) {
            floatStr = floatStr.substring(0, dotIndex + 2);
         }

         return floatStr + (isKB ? 75 : 32) + "B/s";
      } else {
         return "";
      }
   }
}
