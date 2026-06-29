package net.rim.device.apps.internal.camera;

import java.io.ByteArrayInputStream;
import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import net.rim.device.api.math.Fixed32;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.system.PNGEncodedImage;
import net.rim.device.apps.api.framework.file.ExplorerServices;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.internal.camera.Camera;
import net.rim.device.internal.io.file.FileIndexService;
import net.rim.device.internal.io.file.FileUtilities;
import net.rim.device.internal.io.file.MetaDataFile;

final class CameraScreen$SavePicture implements Runnable {
   private final CameraScreen this$0;

   CameraScreen$SavePicture(CameraScreen _1) {
      this.this$0 = _1;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void run() {
      String filename = null;
      FileConnection file = null;
      boolean invalidSaveLocation = false;
      boolean showPreview = !ContextObject.getFlag(this.this$0._context, 39);
      boolean thumbCreated = true;

      try {
         if (this.this$0._jpegData == null) {
            Camera.getPreview(this.this$0._thumbnailBitmap);
            this.this$0._jpegData = this.this$0.fetchJpegData();
         } else {
            EncodedImage ei = EncodedImage.createEncodedImage(this.this$0._jpegData, 0, this.this$0._jpegData.length);
            int tbw = this.this$0._thumbnailBitmap.getWidth();
            int tbh = this.this$0._thumbnailBitmap.getHeight();
            int reductionFactorX = Fixed32.div(Fixed32.toFP(ei.getWidth()), Fixed32.toFP(tbw));
            int reductionFactorY = Fixed32.div(Fixed32.toFP(ei.getHeight()), Fixed32.toFP(tbh));
            int reductionFactor = Math.max(reductionFactorX, reductionFactorY);
            ei.setScaleX32(reductionFactor);
            ei.setScaleY32(reductionFactor);
            this.this$0._graphics.drawImage(0, 0, tbw, tbh, ei, 0, 0, 0);
         }
      } finally {
         ;
      }

      boolean log = this.this$0._options.isDevOptionEnabled(4);
      if (log) {
         System.out.println("Encode picture " + this.this$0.getTimestamp());
      }

      String filepath = this.this$0.getPath(true);
      boolean var44 = false /* VF: Semaphore variable */;
      boolean var52 = false /* VF: Semaphore variable */;

      label605: {
         label604: {
            try {
               label602:
               try {
                  var52 = true;
                  var44 = true;
                  int var82 = this.this$0._cameraData._pictureCounter;

                  int var76;
                  for (var76 = 0; var76 <= 99999; var76++) {
                     var82 = (var82 + 1) % 100000;
                     filename = this.this$0.getPictureFilename(var82);
                     file = (FileConnection)Connector.open(FileUtilities.makeFileURL(filepath, filename));
                     if (!file.exists()) {
                        this.this$0._cameraData._pictureCounter = var82;
                        break;
                     }

                     file.close();
                  }

                  if (var76 > 99999) {
                     filename = null;
                  }

                  String filenameTemp = ExplorerServices.saveInputStream(
                     filepath, filename, new ByteArrayInputStream(this.this$0._jpegData, 0, this.this$0._jpegData.length), 1, false, false
                  );
                  if (filenameTemp != null) {
                     filepath = FileUtilities.getPath(filenameTemp);
                     filename = FileUtilities.getName(filenameTemp);
                     if (thumbCreated) {
                        MetaDataFile metadata = MetaDataFile.getOrCreate(FileUtilities.makeFileURL(filepath));
                        if (metadata != null) {
                           try {
                              metadata.saveMetaDataImage(filename, PNGEncodedImage.encode(this.this$0._thumbnailBitmap));
                              var44 = false;
                              var52 = false;
                           } catch (Throwable var71) {
                              String msg = e.toString() + " creating thumb " + filename;
                              EventLogger.logEvent(-2562843282228934904L, msg.getBytes(), 3);
                              var44 = false;
                              var52 = false;
                              break label604;
                           }
                        } else {
                           var44 = false;
                           var52 = false;
                        }
                     } else {
                        var44 = false;
                        var52 = false;
                     }
                  } else if (showPreview) {
                     this.this$0._preview.displayMessage(CameraMain._rb.getString(31));
                     var44 = false;
                     var52 = false;
                  } else {
                     var44 = false;
                     var52 = false;
                  }
                  break label604;
               } finally {
                  if (var52) {
                     invalidSaveLocation = true;
                     if (showPreview) {
                        this.this$0._preview.displayMessage(CameraMain._rb.getString(31));
                        var44 = false;
                     } else {
                        var44 = false;
                     }
                     break label602;
                  }
               }
            } finally {
               if (var44) {
                  if (file != null) {
                     label568:
                     try {
                        file.close();
                     } finally {
                        break label568;
                     }
                  }
               }
            }

            if (file != null) {
               try {
                  file.close();
               } finally {
                  break label605;
               }
            }
            break label605;
         }

         if (file != null) {
            label580:
            try {
               file.close();
            } finally {
               break label580;
            }
         }
      }

      if (log) {
         System.out.println("Picture Saved " + this.this$0.getTimestamp());
         this.this$0.createEventLog(this.this$0._jpegData.length);
      }

      if (invalidSaveLocation) {
         this.this$0._path = FileIndexService.getCurrentMediaFolder(1, -1);
         Object obj = null;
         obj = new ByteArrayInputStream(this.this$0._jpegData, 0, this.this$0._jpegData.length);
         String fileURL = FileUtilities.makeFileURL(this.this$0._path, filename);
         Verb renameVerb = ExplorerServices.getSaveInputStreamVerb(fileURL, 1, true, false);
         if (renameVerb != null) {
            obj = renameVerb.invoke(obj);
            if (obj instanceof String) {
               String newname = (String)obj;
               filepath = FileUtilities.getPath(newname);
               filename = FileUtilities.getName(newname);
            }
         }
      }

      if (showPreview) {
         this.this$0._preview.setImageData(this.this$0._jpegData);
         this.this$0._preview.setFileName(filepath, filename);
      } else {
         Object obj = ContextObject.get(this.this$0._context, -3185095355580406181L);
         if (obj instanceof Verb) {
            Verb terminalVerb = (Verb)obj;
            terminalVerb.invoke(FileUtilities.makeFileURL(filepath, filename));
         }

         this.this$0.close();
      }

      int quality = this.this$0._options.getImageQualityIndex();
      int avgFactor = this.this$0._cameraData._avgImageSizeFactor[quality];
      int[] size = this.this$0._options.getImageSize();
      int newFactor = this.this$0._jpegData.length * 1000 / size[0] / size[1];
      this.this$0._cameraData._avgImageSizeFactor[quality] = (avgFactor << 4) - avgFactor + newFactor >> 4;
      this.this$0._jpegData = null;
      this.this$0.updatePicturesLeftCounter();
   }
}
