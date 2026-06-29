package net.rim.device.apps.internal.videorecorder;

import java.io.InputStream;
import java.io.OutputStream;
import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import net.rim.device.api.util.MathUtilities;
import net.rim.device.api.util.NumberUtilities;
import net.rim.device.apps.internal.mms.options.MMSClientServiceBook;
import net.rim.device.internal.camera.Camera;
import net.rim.device.internal.io.file.FileHandleProvider;
import net.rim.device.internal.io.file.FileUtilities;
import net.rim.device.internal.media.MediaStreamingCallback;
import net.rim.vm.PersistentInteger;

public final class VideoRecordController implements MediaStreamingCallback {
   private int _currentState = 0;
   private long _totalMemoryAvail = -1;
   private long _startingMemoryAvail = -1;
   private int _mmsMode = -1;
   private String _path = null;
   private String _filename;
   private FileConnection _tempFile;
   private FileConnection _finalFile;
   private OutputStream _tempOS;
   private InputStream _tempIS;
   private VideoRecorderOptions _options = VideoRecorderOptions.getOptions();
   protected static final long NEXT_VIDEONUM_KEY = -357719501564298138L;
   public static final int STATE_INACTIVE = 0;
   public static final int STATE_SOURCED = 1;
   public static final int STATE_RECORDING = 2;
   public static final int STATE_TRANSCODING = 3;
   private static final String VIDEO_MEDIA_TYPE = "video/3gpp";
   private static final String FINAL_VIDEO_FILENAME_PREFIX = "VID ";
   private static final String FINAL_VIDEO_FILENAME_SUFFIX = ".3GP";
   private static final int FIXED_VIDEO_FILESIZE = 307200;
   private static final int FILESPACE_HEADROOM = 25600;
   private static final int MMS_OVERHEAD = 10240;
   private static VideoRecordController _instance;

   private VideoRecordController() {
      try {
         String directory = FileUtilities.makeFileURL(this.getVideoSaveDirectory());
         FileConnection file = (FileConnection)Connector.open(directory);
         this._totalMemoryAvail = file.totalSize();
         this._startingMemoryAvail = file.availableSize();
         file.close();
      } finally {
         return;
      }
   }

   public static final VideoRecordController getInstance() {
      if (_instance == null) {
         _instance = new VideoRecordController();
      }

      return _instance;
   }

   public final void exitController() {
      try {
         if (this._tempFile != null) {
            this._tempFile.close();
         }

         if (this._finalFile != null) {
            this._finalFile.close();
            return;
         }
      } finally {
         return;
      }
   }

   public final FileConnection getTempFileConnection() {
      if (this._tempFile == null) {
         try {
            this._filename = this.getNextAvailableFilename();
            this._tempFile = (FileConnection)Connector.open(this._filename);
            this._totalMemoryAvail = this._tempFile.totalSize();
         } finally {
            return this._tempFile;
         }
      }

      return this._tempFile;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final boolean openTempFileWrite() {
      this.getTempFileConnection();
      if (this._currentState == 0) {
         boolean var4 = false /* VF: Semaphore variable */;

         try {
            var4 = true;
            this._totalMemoryAvail = this._tempFile.totalSize();
            this._startingMemoryAvail = this._tempFile.availableSize();
            long ioe = this._startingMemoryAvail / 2 - 25600;
            if (ioe < 102400) {
               return false;
            }

            if (this.isMmsMode()) {
               ioe = Math.min(ioe, MMSClientServiceBook.getMaxMessageSize() - 10240);
            }

            if (ioe > Integer.MAX_VALUE) {
               ioe = Integer.MAX_VALUE;
            }

            if (!this._tempFile.exists()) {
               this._tempFile.create();
            }

            this._tempOS = this._tempFile.openOutputStream(Integer.MAX_VALUE);
            if (!(this._tempOS instanceof Object)) {
               return false;
            }

            Camera.recordVideoToFile(((FileHandleProvider)this._tempOS).getFileHandle(), (int)ioe);
            this._currentState = 1;
            var4 = false;
         } finally {
            if (var4) {
               System.out.println("Video temp file failed");
               return false;
            }
         }
      }

      return true;
   }

   public final int getPercentMemoryRemaining() {
      long tempFileSize = 0;

      label29:
      try {
         if (this._tempFile != null) {
            tempFileSize = this._tempFile.fileSize();
         }
      } finally {
         break label29;
      }

      int percent = 0;
      if (this._totalMemoryAvail > 0) {
         percent = (int)((this._startingMemoryAvail - tempFileSize * 2 - 25600) * 100 / this._totalMemoryAvail);
         percent = MathUtilities.clamp(0, percent, 100);
      }

      return percent;
   }

   public final boolean startRecord() {
      if (this._currentState == 0 && !this.openTempFileWrite()) {
         return false;
      }

      if (this._currentState == 1) {
         try {
            Camera.startVideoRecord();
            this._currentState = 2;
            return true;
         } finally {
            ;
         }
      } else {
         return true;
      }
   }

   public final void pauseRecord() {
      if (this._currentState == 2) {
         Camera.stopVideoRecord();
         this._currentState = 1;
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public final void stopRecord() {
      if (this._currentState == 2) {
         this.pauseRecord();
      }

      if (this._currentState == 1) {
         Camera.finalizeVideoRecord();
         boolean var7 = false /* VF: Semaphore variable */;

         try {
            var7 = true;
            if (this._tempOS != null) {
               this._tempOS.flush();
               this._tempOS.close();
               var7 = false;
            } else {
               var7 = false;
            }
         } finally {
            if (var7) {
               label55:
               try {
                  this._tempOS.close();
                  break label55;
               } finally {
                  break label55;
               }
            }
         }

         this._tempOS = null;
         this._currentState = 0;
      }
   }

   final void fileAlreadyTranscoded() {
      try {
         if (this._finalFile != null && this._finalFile.exists()) {
            this._finalFile.delete();
            this._finalFile.close();
         }

         this._tempFile.rename(FileUtilities.getName(this._filename));
         this._finalFile = this._tempFile;
         this._tempFile = null;
      } finally {
         return;
      }
   }

   public final void transcodeVideo() {
      if (this._currentState == 0) {
         this._currentState = 3;
         new VideoRecordController$1(this).start();
      }
   }

   public final String getVideoFileName(boolean fullFilename) {
      return fullFilename ? this._filename : FileUtilities.getDisplayBaseName(this._filename);
   }

   public final void setVideoFileName(String filename) {
      this._filename = filename;
   }

   public final String getVideoFileSize() {
      long filesize = 0;

      try {
         if (this._tempFile != null) {
            filesize = this._tempFile.fileSize();
         }
      } finally {
         return FileUtilities.sizeToString(filesize, filesize >= 1048576 ? 1 : 0);
      }

      return FileUtilities.sizeToString(filesize, filesize >= 1048576 ? 1 : 0);
   }

   final String getVideoSaveDirectory() {
      if (this._path == null) {
         this._path = this._options.getDestinationFolder();
      }

      return this._path;
   }

   final void setVideoSaveDirectory(String path) {
      if (path != null) {
         this._path = path;
      } else {
         this._path = this._options.getDestinationFolder();
      }
   }

   final void setMmsMode(int mode) {
      this._mmsMode = mode;
   }

   final boolean isMmsMode() {
      return this._mmsMode == 1;
   }

   private final String getNextAvailableFilename() {
      String filename = null;
      String location = this.getVideoSaveDirectory();
      FileUtilities.ensureDirectoryExists(location);
      int nextFileNumHandle = PersistentInteger.getId(-357719501564298138L, 0);
      int nextFileNum = PersistentInteger.get(nextFileNumHandle);

      for (boolean fileExists = true; fileExists; nextFileNum = (nextFileNum + 1) % 100000) {
         String num = NumberUtilities.toString(nextFileNum, 10, 5);
         filename = FileUtilities.makeFileURL(location, ((StringBuffer)(new Object("VID "))).append(num).append(".3GP").toString());
         fileExists = FileUtilities.checkFileExists(filename);
      }

      PersistentInteger.set(nextFileNumHandle, nextFileNum);
      return filename;
   }

   public final void cleanupStreamsAndFiles(boolean forceDelete) {
      this.stopRecord();

      label48:
      try {
         if (this._tempIS != null) {
            this._tempIS.close();
            this._tempIS = null;
         }

         if (this._tempOS != null) {
            this._tempOS.flush();
            this._tempOS.close();
            this._tempOS = null;
         }

         if (this._tempFile != null) {
            this._tempFile.delete();
            this._tempFile.close();
            this._tempFile = null;
         }

         if (this._finalFile != null) {
            if (forceDelete) {
               this._finalFile.delete();
               this._filename = null;
            }

            this._finalFile.close();
            this._finalFile = null;
         }
      } finally {
         break label48;
      }

      this._currentState = 0;
   }

   @Override
   public final boolean moreData() {
      System.out.println("VIDEO -more data");
      return true;
   }

   @Override
   public final void recordingDone(int reason, int data) {
      System.out.println("VIDEO -recording done");
   }

   @Override
   public final boolean sessionSourceEnded() {
      System.out.println("VIDEO -session source ended");
      return true;
   }

   @Override
   public final void streamingDone(int reason) {
      System.out.println("VIDEO -streaming done");
   }

   @Override
   public final void streamingSentAllData() {
      System.out.println("VIDEO -streaming sent all data");
   }
}
