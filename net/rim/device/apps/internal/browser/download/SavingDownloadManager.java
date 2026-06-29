package net.rim.device.apps.internal.browser.download;

import java.io.InputStream;
import java.io.OutputStream;
import javax.microedition.io.Connector;
import javax.microedition.io.InputConnection;
import javax.microedition.io.file.FileConnection;
import net.rim.device.api.browser.field.RenderingApplication;
import net.rim.device.api.util.MathUtilities;
import net.rim.device.apps.internal.browser.core.BrowserDaemonRegistry;
import net.rim.device.apps.internal.browser.threading.Job;
import net.rim.device.apps.internal.browser.ui.SaveFileDialog;
import net.rim.device.apps.internal.browser.util.CountedInputStream;
import net.rim.device.apps.internal.browser.util.QuincyUtil;
import net.rim.device.apps.internal.browser.util.RendererControl;
import net.rim.device.internal.io.file.FileUtilities;
import net.rim.device.internal.system.InternalServices;
import net.rim.vm.WeakReference;

public class SavingDownloadManager extends DownloadManager implements Job {
   private long _totalSize;
   private InputConnection _in;
   private String _fileUrl;
   private SaveFileDialog _saveFileDialog;
   private long _timeStarted;
   private InputStream _inStream;
   private WeakReference _listener;
   private long _timeFinished;

   public void setListener(SavingDownloadManagerListener listener) {
      this._listener = (WeakReference)(new Object(listener));
   }

   public String getFilename() {
      return FileUtilities.getName(this._fileUrl);
   }

   public String getUrl() {
      return this._fileUrl;
   }

   public long getTransferRate() {
      long endTime;
      if (this._timeFinished != 0) {
         endTime = this._timeFinished;
      } else {
         endTime = InternalServices.getUptime();
      }

      return this.getDownloadedSize() / (Math.max(1000, endTime - this._timeStarted) / 1000);
   }

   public long getDownloadedSize() {
      if (!(this._inStream instanceof Object)) {
         return super._totalDecodedSizeDownloaded;
      }

      CountedInputStream countedInputStream = (CountedInputStream)this._inStream;
      return countedInputStream.getCompressedBytesRead();
   }

   public long getTotalSize() {
      return this._totalSize;
   }

   public long getStartTime() {
      return this._timeStarted;
   }

   @Override
   public void cancel() {
   }

   // $VF: Could not inline inconsistent finally blocks
   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public void run() {
      BrowserDaemonRegistry.getInstance().getActiveDownloads().addElement(this);
      FileConnection newFile = null;
      OutputStream out = null;
      boolean var280 = false /* VF: Semaphore variable */;

      label1735: {
         try {
            label1729:
            try {
               var280 = true;
               if (this._inStream == null) {
                  this._inStream = RendererControl.getInputStreamFromContentEncoding(this._in, this._in.openInputStream(), true);
               }

               newFile = (FileConnection)Connector.open(this._fileUrl);
               if (newFile.exists()) {
                  newFile.delete();
               }

               newFile.create();
               out = newFile.openOutputStream();
               int bufferSize = MathUtilities.clamp(2048, (int)this._totalSize, 65536);
               this.saveStream(this._inStream, out, bufferSize);
               var280 = false;
               break label1735;
            } catch (Throwable var313) {
               this.setError(t);
               var280 = false;
               break label1729;
            }
         } finally {
            if (var280) {
               this._timeFinished = InternalServices.getUptime();
               BrowserDaemonRegistry.getInstance().getActiveDownloads().removeElement(this);
               if (this._inStream != null) {
                  label1675:
                  try {
                     this._inStream.close();
                  } finally {
                     break label1675;
                  }
               }

               label1672:
               try {
                  this._in.close();
               } finally {
                  break label1672;
               }

               if (out != null) {
                  label1668:
                  try {
                     out.close();
                  } finally {
                     break label1668;
                  }
               }

               if (newFile != null) {
                  label1664:
                  try {
                     newFile.close();
                  } finally {
                     break label1664;
                  }
               }

               SavingDownloadManagerListener listener = null;
               if (this._listener != null) {
                  listener = (SavingDownloadManagerListener)this._listener.get();
               }

               if (listener != null) {
                  listener.progressCompleted();
               }

               label1658:
               try {
                  if (this.getDownloadedSize() >= this.getTotalSize()) {
                     this._saveFileDialog.runPostSaveActions();
                     this._saveFileDialog = null;
                  }
               } catch (Throwable var298) {
                  QuincyUtil.sendQuincy(t, false);
                  break label1658;
               }

               this._inStream = null;
            }
         }

         this._timeFinished = InternalServices.getUptime();
         BrowserDaemonRegistry.getInstance().getActiveDownloads().removeElement(this);
         if (this._inStream != null) {
            label1698:
            try {
               this._inStream.close();
            } finally {
               break label1698;
            }
         }

         label1695:
         try {
            this._in.close();
         } finally {
            break label1695;
         }

         if (out != null) {
            label1691:
            try {
               out.close();
            } finally {
               break label1691;
            }
         }

         if (newFile != null) {
            label1687:
            try {
               newFile.close();
            } finally {
               break label1687;
            }
         }

         SavingDownloadManagerListener listener = null;
         if (this._listener != null) {
            listener = (SavingDownloadManagerListener)this._listener.get();
         }

         if (listener != null) {
            listener.progressCompleted();
         }

         label1681:
         try {
            if (this.getDownloadedSize() >= this.getTotalSize()) {
               this._saveFileDialog.runPostSaveActions();
               this._saveFileDialog = null;
            }
         } catch (Throwable var303) {
            QuincyUtil.sendQuincy(t, false);
            break label1681;
         }

         this._inStream = null;
         return;
      }

      this._timeFinished = InternalServices.getUptime();
      BrowserDaemonRegistry.getInstance().getActiveDownloads().removeElement(this);
      if (this._inStream != null) {
         label1719:
         try {
            this._inStream.close();
         } finally {
            break label1719;
         }
      }

      label1716:
      try {
         this._in.close();
      } finally {
         break label1716;
      }

      if (out != null) {
         label1712:
         try {
            out.close();
         } finally {
            break label1712;
         }
      }

      if (newFile != null) {
         label1708:
         try {
            newFile.close();
         } finally {
            break label1708;
         }
      }

      SavingDownloadManagerListener listener = null;
      if (this._listener != null) {
         listener = (SavingDownloadManagerListener)this._listener.get();
      }

      if (listener != null) {
         listener.progressCompleted();
      }

      label1702:
      try {
         if (this.getDownloadedSize() >= this.getTotalSize()) {
            this._saveFileDialog.runPostSaveActions();
            this._saveFileDialog = null;
         }
      } catch (Throwable var308) {
         QuincyUtil.sendQuincy(t, false);
         break label1702;
      }

      this._inStream = null;
   }

   public SavingDownloadManager(RenderingApplication app, InputConnection in, InputStream inStream, SaveFileDialog saveFileDialog, long timeStarted) {
      super(app);
      this._in = in;
      this._inStream = inStream;

      label20:
      try {
         this._totalSize = RendererControl.getContentLength(in);
      } finally {
         break label20;
      }

      this._fileUrl = saveFileDialog.getURL();
      this._saveFileDialog = saveFileDialog;
      this._timeStarted = timeStarted;
   }

   @Override
   protected void progressUpdate(long totalSizeDownloaded) {
   }

   @Override
   protected void setError(String errorMessage) {
      SavingDownloadManagerListener listener = null;
      if (this._listener != null) {
         listener = (SavingDownloadManagerListener)this._listener.get();
      }

      if (listener != null) {
         listener.error(errorMessage);
      }
   }

   @Override
   protected void setError(int httpResponseCode) {
      SavingDownloadManagerListener listener = null;
      if (this._listener != null) {
         listener = (SavingDownloadManagerListener)this._listener.get();
      }

      if (listener != null) {
         listener.error(httpResponseCode);
      }
   }

   @Override
   protected void setError(Throwable t) {
      SavingDownloadManagerListener listener = null;
      if (this._listener != null) {
         listener = (SavingDownloadManagerListener)this._listener.get();
      }

      if (listener != null) {
         listener.error(t);
      }
   }

   @Override
   protected void checkContentType(String contentType) {
   }
}
