package net.rim.device.internal.media.metadata;

import java.io.InputStream;
import net.rim.device.api.compress.ZLibInputStream;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.system.PNGEncodedImage;
import net.rim.device.api.util.StringUtilities;

final class PNGMetadataExtractor extends MetaDataControlImpl {
   private byte[] _imageBytes;
   private String _signature;
   private int _chunkIndex;
   private String _title = "";
   private String _author = "";
   private String _description = "";
   private String _copyright = "";
   private String _creationTime = "";
   private String _software = "";
   private String _disclaimer = "";
   private String _warning = "";
   private String _source = "";
   private String _comment = "";
   private static String KEYWORD_TITLE = "Title";
   private static String KEYWORD_AUTHOR = "Author";
   private static String KEYWORD_DESCRPTION = "Description";
   private static String KEYWORD_COPYRIGHT = "Copyright";
   private static String KEYWORD_CREATIONTIME = "Creation Time";
   private static String KEYWORD_SOFTWARE = "Software";
   private static String KEYWORD_DISCLAIMER = "Disclaimer";
   private static String KEYWORD_WARNING = "Warning";
   private static String KEYWORD_SOURCE = "Source";
   private static String KEYWORD_COMMENT = "Comment";
   private static String TAG_tEXt = "tEXt";
   private static String TAG_zTXt = "zTXt";
   private static String TAG_iTXt = "iTXt";

   public PNGMetadataExtractor(EncodedImage encImage) {
      this(encImage, 0);
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public PNGMetadataExtractor(EncodedImage encImage, int modifyDataFlags) {
      if (encImage instanceof Object) {
         PNGEncodedImage pngEncImg = (PNGEncodedImage)encImage;
         this._imageBytes = pngEncImg.getData();
         this._signature = this.extractPNGSignature();
         this._chunkIndex = 8;
         String chunkType = null;
         boolean endFlag = false;

         while (!endFlag) {
            boolean var9 = false /* VF: Semaphore variable */;

            try {
               var9 = true;
               byte[] chunkBytes = this.getNextPNGChunk();
               chunkType = ((StringBuffer)(new Object()))
                  .append(String.valueOf((char)chunkBytes[4]))
                  .append(String.valueOf((char)chunkBytes[5]))
                  .append(String.valueOf((char)chunkBytes[6]))
                  .append(String.valueOf((char)chunkBytes[7]))
                  .toString();
               if (chunkType.equals(TAG_tEXt)) {
                  String[] tEXtPair = this.extracttEXt(chunkBytes, TAG_tEXt);
                  if ((modifyDataFlags & 1) == 0) {
                     tEXtPair[1] = StringUtilities.removeChars(tEXtPair[1], "\r");
                  }

                  this.setText(tEXtPair[0], tEXtPair[1]);
                  var9 = false;
               } else if (chunkType.equals(TAG_zTXt)) {
                  String[] zTXtPair = this.extracttEXt(chunkBytes, TAG_zTXt);
                  if ((modifyDataFlags & 1) == 0) {
                     zTXtPair[1] = StringUtilities.removeChars(zTXtPair[1], "\r");
                  }

                  this.setText(zTXtPair[0], zTXtPair[1]);
                  var9 = false;
               } else if (chunkType.equals(TAG_iTXt)) {
                  String[] iTXtPair = this.extracttEXt(chunkBytes, TAG_iTXt);
                  if ((modifyDataFlags & 1) == 0) {
                     iTXtPair[1] = StringUtilities.removeChars(iTXtPair[1], "\r");
                  }

                  this.setText(iTXtPair[0], iTXtPair[1]);
                  var9 = false;
               } else if (!chunkType.equals("IEND")) {
                  var9 = false;
               } else {
                  endFlag = true;
                  var9 = false;
               }
            } finally {
               if (var9) {
                  System.err.println("Invalid PNG file.");
                  continue;
               }
            }
         }

         this.initializeData();
      }
   }

   private final void initializeData() {
      this.put("title", this._title);
      this.put("author", this._author);
      this.put("description", this._description);
      this.put("copyright", this._copyright);
      this.put("date", this._creationTime);
      this.put("software", this._software);
      this.put("disclaimer", this._disclaimer);
      this.put("warning", this._warning);
      this.put("comment", this._comment);
      this.put("source", this._source);
      this.put("signature", this._signature);
   }

   private final String[] extracttEXt(byte[] chunkBytes, String textType) {
      int chunkLength = (chunkBytes[0] & 255) << 24 | (chunkBytes[1] & 255) << 16 | (chunkBytes[2] & 255) << 8 | (chunkBytes[3] & 255) << 0;
      String keyword = "";
      String value = "";

      int zw;
      for (zw = 8; (chunkBytes[zw] & 255) != 0; zw++) {
         keyword = ((StringBuffer)(new Object())).append(keyword).append((char)chunkBytes[zw]).toString();
      }

      zw++;
      if (textType.equals(TAG_tEXt)) {
         for (int i = zw; i < chunkLength + 8; i++) {
            value = ((StringBuffer)(new Object())).append(value).append((char)chunkBytes[i]).toString();
         }
      } else if (!textType.equals(TAG_zTXt)) {
         if (textType.equals(TAG_iTXt)) {
            boolean isCompressed = false;
            if ((chunkBytes[zw] & 255) != 0) {
               isCompressed = true;
            }

            if ((chunkBytes[++zw] & 255) != 0) {
               throw new Object();
            }

            zw++;
            String languageTag = "";

            while ((chunkBytes[zw] & 255) != 0) {
               languageTag = ((StringBuffer)(new Object())).append(languageTag).append((char)chunkBytes[zw]).toString();
               zw++;
            }

            if ((chunkBytes[++zw] & 255) != 0) {
               String translatedKeyword = "";

               while ((chunkBytes[zw] & 255) != 0) {
                  translatedKeyword = ((StringBuffer)(new Object())).append(translatedKeyword).append((char)chunkBytes[zw]).toString();
                  zw++;
               }

               value = ((StringBuffer)(new Object())).append(value).append("(").append(translatedKeyword).append(") ").toString();
               zw++;
            }

            if (isCompressed) {
               InputStream inStream = (InputStream)(new Object(chunkBytes, zw, chunkLength + 9));
               ZLibInputStream zInStream = (ZLibInputStream)(new Object(inStream));

               try {
                  byte[] decompData = new byte[chunkLength * 3];
                  int numOfBytes = zInStream.read(decompData, 0, decompData.length);
                  zInStream.close();

                  for (int i = 0; i < numOfBytes; i++) {
                     value = ((StringBuffer)(new Object())).append(value).append((char)decompData[i]).toString();
                  }
               } finally {
                  ;
               }
            } else {
               for (int i = zw; i < chunkLength + 8; i++) {
                  value = ((StringBuffer)(new Object())).append(value).append((char)chunkBytes[i]).toString();
               }
            }
         }
      } else {
         if ((chunkBytes[zw] & 255) != 0) {
            throw new Object();
         }

         InputStream inStream = (InputStream)(new Object(chunkBytes, ++zw, chunkLength + 9));
         ZLibInputStream zInStream = (ZLibInputStream)(new Object(inStream));

         try {
            byte[] decompData = new byte[chunkLength * 3];
            int numOfBytes = zInStream.read(decompData, 0, decompData.length);
            zInStream.close();

            for (int i = 0; i < numOfBytes; i++) {
               value = ((StringBuffer)(new Object())).append(value).append((char)decompData[i]).toString();
            }
         } finally {
            ;
         }
      }

      String[] textPair = new Object[2];
      textPair[0] = keyword;
      textPair[1] = value;
      return textPair;
   }

   private final void setText(String keyword, String value) {
      if (keyword.equals(KEYWORD_TITLE)) {
         this._title = value;
      } else if (keyword.equals(KEYWORD_AUTHOR)) {
         this._author = value;
      } else if (keyword.equals(KEYWORD_DESCRPTION)) {
         this._description = value;
      } else if (keyword.equals(KEYWORD_COPYRIGHT)) {
         this._copyright = value;
      } else if (keyword.equals(KEYWORD_CREATIONTIME)) {
         this._creationTime = value;
      } else if (keyword.equals(KEYWORD_SOFTWARE)) {
         this._software = value;
      } else if (keyword.equals(KEYWORD_DISCLAIMER)) {
         this._disclaimer = value;
      } else if (keyword.equals(KEYWORD_WARNING)) {
         this._warning = value;
      } else if (keyword.equals(KEYWORD_SOURCE)) {
         this._source = value;
      } else {
         if (keyword.equals(KEYWORD_COMMENT)) {
            this._comment = value;
         }
      }
   }

   private final String extractPNGSignature() {
      String signature = null;

      try {
         int[] arrySignature = new int[8];

         for (int i = 0; i < 8; i++) {
            arrySignature[i] = this._imageBytes[i];
         }

         return ((StringBuffer)(new Object()))
            .append(arrySignature[0])
            .append(" ")
            .append(arrySignature[1])
            .append(" ")
            .append(arrySignature[2])
            .append(" ")
            .append(arrySignature[3])
            .append(" ")
            .append(arrySignature[4])
            .append(" ")
            .append(arrySignature[5])
            .append(" ")
            .append(arrySignature[6])
            .append(" ")
            .append(arrySignature[7])
            .toString();
      } finally {
         System.err.println("Invalid PNG file");
         return signature;
      }
   }

   private final byte[] getNextPNGChunk() {
      int chunkLength = (this._imageBytes[this._chunkIndex + 0] & 255) << 24
         | (this._imageBytes[this._chunkIndex + 1] & 255) << 16
         | (this._imageBytes[this._chunkIndex + 2] & 255) << 8
         | (this._imageBytes[this._chunkIndex + 3] & 255) << 0;
      byte[] chunkBytes = new byte[chunkLength + 12];
      System.arraycopy(this._imageBytes, this._chunkIndex, chunkBytes, 0, chunkLength + 12);
      this._chunkIndex += chunkLength + 12;
      return chunkBytes;
   }
}
