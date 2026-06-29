package net.rim.device.apps.internal.browser.download;

import net.rim.device.api.system.Application;
import net.rim.device.api.util.Arrays;
import net.rim.device.internal.system.InternalServices;

class DownloadPage$DownloadItem$UpdateRunner implements Runnable {
   private long[] _upTimes;
   private long[] _bytesDownloaded;
   private int _head;
   private final DownloadPage$DownloadItem this$1;
   private static final int ITEMS_TO_TRACK;

   public DownloadPage$DownloadItem$UpdateRunner(DownloadPage$DownloadItem _1) {
      this.this$1 = _1;
      this._upTimes = new long[5];
      this._bytesDownloaded = new long[5];
      Arrays.fill(this._upTimes, _1._dm.getStartTime());
   }

   @Override
   public void run() {
      if (this.this$1._gauge == null) {
         Application.getApplication().cancelInvokeLater(this.this$1._progressUpdaterId);
         this.this$1._progressUpdaterId = -1;
      } else {
         long size = this.this$1._dm.getDownloadedSize();
         long total = this.this$1._dm.getTotalSize();
         long now = InternalServices.getUptime();
         long start = this.this$1._dm.getStartTime();
         if (total > 0) {
            label45:
            try {
               this.this$1._gauge.setValue((int)(size * 100 / total));
            } finally {
               break label45;
            }
         }

         int prevItem = (this._head + 5 - 1) % 5;
         if (this._bytesDownloaded[prevItem] != size) {
            this._bytesDownloaded[this._head] = size;
            this._upTimes[this._head] = now;
            int next = (this._head + 1) % 5;
            long diff = now - this._upTimes[next] / 1000;
            long avgRate = this.this$1._dm.getTransferRate();
            long instRate;
            if (diff > 0) {
               instRate = (size - this._bytesDownloaded[next]) / diff;
            } else {
               instRate = avgRate;
            }

            long runtime = now - start;
            long instRating;
            if (runtime > 5000) {
               instRating = 500000 / runtime;
            } else {
               instRating = 100;
            }

            long rate = (instRating * instRate + (100 - instRating) * avgRate) / 100;
            this._head = next;
            this.this$1._speed.setText(this.this$1.getTransferRate(rate));
         }
      }
   }
}
