package net.rim.device.internal.media.metadata;

import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.system.GIFEncodedImage;
import net.rim.device.api.util.StringUtilities;

final class GIFMetadataExtractor extends MetaDataControlImpl {
   private byte[] _imageBytes;
   private int _blockIndex;
   private String _description = "";

   public GIFMetadataExtractor(EncodedImage encImage) {
      this(encImage, 0);
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public GIFMetadataExtractor(EncodedImage encImage, int modifyDataFlags) {
      if (encImage instanceof GIFEncodedImage) {
         GIFEncodedImage gifEncImg = (GIFEncodedImage)encImage;
         this._imageBytes = gifEncImg.getData();
         boolean var6 = false /* VF: Semaphore variable */;

         label28:
         try {
            var6 = true;
            this._description = this.getGIFComment();
            if ((modifyDataFlags & 1) == 0) {
               this._description = StringUtilities.removeChars(this._description, "\r");
               var6 = false;
            } else {
               var6 = false;
            }
         } finally {
            if (var6) {
               System.err.println("Invalid GIF file.");
               break label28;
            }
         }

         this.put("description", this._description);
      }
   }

   private final String getGIFComment() {
      this._blockIndex = 7;
      String comment = "";
      if ((this._imageBytes[this._blockIndex + 3] & 128) >> 7 == 1) {
         int globalColorTableSize = this._imageBytes[this._blockIndex + 3] & 7;
         this._blockIndex += 7 + 3 * (1 << globalColorTableSize + 1) - 1;
      } else {
         this._blockIndex += 7;
      }

      for (; (this._imageBytes[this._blockIndex] & 255) != 59; this._blockIndex++) {
         if ((this._imageBytes[this._blockIndex] & 255) == 44) {
            if ((this._imageBytes[this._blockIndex + 9] & 128) >> 7 == 1) {
               int localColorTableSize = this._imageBytes[this._blockIndex + 9] & 7;
               this._blockIndex += 10 + 3 * (1 << localColorTableSize + 1);
            } else {
               this._blockIndex += 10;
            }

            this._blockIndex++;

            while ((this._imageBytes[this._blockIndex] & 255) != 0) {
               this._blockIndex = this._blockIndex + (this._imageBytes[this._blockIndex] & 255) + 1;
            }
         }

         if ((this._imageBytes[this._blockIndex] & 255) == 33) {
            this._blockIndex++;
            if ((this._imageBytes[this._blockIndex] & 255) == 249) {
               this._blockIndex += 6;
            } else if ((this._imageBytes[this._blockIndex] & 255) == 254) {
               int commentSize = 0;
               this._blockIndex++;

               while ((this._imageBytes[this._blockIndex] & 255) == 255) {
                  commentSize = this._imageBytes[this._blockIndex] & 255;
                  this._blockIndex++;

                  for (int i = 0; i < 255; i++) {
                     comment = comment + (char)this._imageBytes[this._blockIndex + i];
                  }

                  this._blockIndex += commentSize;
               }

               commentSize = this._imageBytes[this._blockIndex] & 255;
               this._blockIndex++;

               for (int i = 0; i < commentSize; i++) {
                  comment = comment + (char)this._imageBytes[this._blockIndex + i];
               }

               this._blockIndex += commentSize;
            }
         }
      }

      return comment;
   }
}
