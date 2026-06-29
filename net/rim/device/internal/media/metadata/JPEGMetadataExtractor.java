package net.rim.device.internal.media.metadata;

import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.system.JPEGEncodedImage;
import net.rim.device.api.util.StringUtilities;

final class JPEGMetadataExtractor extends MetaDataControlImpl {
   private byte[] _imageBytes;
   private int _blockIndex;
   private int _headerStartIndex;
   private int _exifSubIFDOffset;
   private JPEGMetadataExtractor$imageDataReader _imgReader;
   private String _title = "";
   private String _comment = "";
   private String _author = "";
   private String _keywords = "";
   private String _copyright = "";
   private String _dateTimeTaken = "";
   private String _description = "";
   private String _make = "";
   private String _model = "";
   private String _orientation = "";
   private String _exposureTime = "";
   private String _FNumber = "";
   private String _ISOSpeed = "";
   private static final int WINDOWS_XP_EXIF = 1;
   private static final int JEIDA_EXIF = 2;
   private static final int TAG_TYPE_BYTE = 1;
   private static final int TAG_TYPE_ASCII = 2;
   private static final int TAG_TYPE_SHORT = 3;
   private static final int TAG_TYPE_LONG = 4;
   private static final int TAG_TYPE_RATIONAL = 5;
   private static final String MAKE_CANON = "CANON";
   private static final String MAKE_FUJIFILM = "FUJIFILM";
   private static final String MAKE_KODAK = "EASTMAN KODAK COMPANY";
   private static final String MAKE_NIKON = "NIKON";
   private static final String MAKE_OLYMPUS = "OLYMPUS OPTICAL CO.,LTD";
   private static final String MAKE_RICOH = "RICOH";
   private static final String MAKE_SANYO = "SANYO ELECTRIC CO.,LTD.";
   private static final String MAKE_SONY = "SONY";
   private static final int TAG_LITTLE_ENDIAN_TAG = 18761;
   private static final int TAG_BIG_ENDIAN_TAG = 19789;
   private static final int TAG_EXIF_SEGMENT_TAG = 65505;
   private static final int TAG_END_OF_IMAGE_TAG = 65497;
   private static final int TAG_ITU_COMMENT = 65534;
   private static final int TAG_TITLE = 40091;
   private static final int TAG_COMMENT = 40092;
   private static final int TAG_AUTHOR = 40093;
   private static final int TAG_KEYWORDS = 40094;
   private static final int TAG_MAKE = 271;
   private static final int TAG_MODEL = 272;
   private static final int TAG_COPYRIGHT = 33432;
   private static final int TAG_DATE_TIME_TAKEN = 36867;
   private static final int TAG_DESCRIPTION = 270;
   private static final int TAG_ORIENTATION = 274;
   private static final int TAG_EXPOSURE_TIME = 33434;
   private static final int TAG_F_NUMBER = 33437;
   private static final int TAG_ISO_SPEED = 34855;
   private static final int TAG_EXIF_SUB_IFD = 34665;
   private static final int TAG_MAKERNOTE = 37500;

   public JPEGMetadataExtractor(EncodedImage encImage) {
      this(encImage, 0);
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public JPEGMetadataExtractor(EncodedImage encImage, int modifyDataFlags) {
      if (encImage instanceof Object) {
         JPEGEncodedImage jpegEncImg = (JPEGEncodedImage)encImage;
         this._imageBytes = jpegEncImg.getData();
         boolean var6 = false /* VF: Semaphore variable */;

         label38:
         try {
            var6 = true;
            if (jpegEncImg.getFileType() == 1) {
               this.initJPEGCommentITUFormat();
               var6 = false;
            } else if (jpegEncImg.getFileType() == 2) {
               this.initJPEGValuesEXIFFormat();
               var6 = false;
            } else {
               var6 = false;
            }
         } finally {
            if (var6) {
               System.err.println("Invalid JPEG file.");
               break label38;
            }
         }

         if ((modifyDataFlags & 1) == 0) {
            this.removeFromOutput("\r");
         }

         this.initializeData();
      }
   }

   private final void initializeData() {
      this.put("title", this._title);
      this.put("comment", this._comment);
      this.put("author", this._author);
      this.put("keywords", this._keywords);
      this.put("copyright", this._copyright);
      this.put("date", this._dateTimeTaken);
      this.put("description", this._description);
      this.put("cameraMake", this._make);
      this.put("cameraModel", this._model);
      this.put("orientation", this._orientation);
      this.put("exposureTime", this._exposureTime);
      this.put("FNumber", this._FNumber);
      this.put("ISOSpeed", this._ISOSpeed);
   }

   private final void initJPEGCommentITUFormat() {
      this._blockIndex = 2;
      this._imgReader = new JPEGMetadataExtractor$bigEndianReader(this, null);

      for (int tag = this._imgReader.getNext2BytesAsInt(); tag != 65497; tag = this._imgReader.getNext2BytesAsInt()) {
         if (tag == 65534) {
            this._blockIndex += 2;
            int entryLength = this._imgReader.getNext2BytesAsInt();
            this._blockIndex += 2;

            for (int i = 0; i < entryLength - 2; i++) {
               this._comment = ((StringBuffer)(new Object())).append(this._comment).append((char)this._imageBytes[this._blockIndex + i]).toString();
            }
            break;
         }

         if (this._imgReader.getNextByteAsInt() != 255) {
            return;
         }

         this._blockIndex += 2;
         int segmentLength = this._imgReader.getNext2BytesAsInt();
         this._blockIndex += segmentLength;
      }
   }

   private final void initJPEGValuesEXIFFormat() {
      this._blockIndex = 2;
      this._headerStartIndex = 0;
      this._exifSubIFDOffset = 0;
      this._imgReader = new JPEGMetadataExtractor$bigEndianReader(this, null);

      for (int tag = this._imgReader.getNext2BytesAsInt(); tag != 65497; tag = this._imgReader.getNext2BytesAsInt()) {
         if (tag == 65505) {
            this.parseExifHeader();
            this.parse0thIFDSegment();
            if (this._exifSubIFDOffset > 0) {
               this._blockIndex = this._headerStartIndex + this._exifSubIFDOffset;
               this.parseSubIFDSegment();
               return;
            }
            break;
         }

         this._blockIndex += 2;
         this._blockIndex = this._blockIndex + this._imgReader.getNext2BytesAsInt();
      }
   }

   private final void parseExifHeader() {
      this._blockIndex += 10;
      int memoryMode = this._imgReader.getNext2BytesAsInt();
      if (memoryMode == 18761) {
         this._imgReader = new JPEGMetadataExtractor$littleEndianReader(this, null);
      } else {
         if (memoryMode != 19789) {
            throw new Object();
         }

         this._imgReader = new JPEGMetadataExtractor$bigEndianReader(this, null);
      }

      this._headerStartIndex = this._blockIndex;
      this._blockIndex += 8;
   }

   private final void parse0thIFDSegment() {
      boolean jeidaTagsExists = false;
      boolean supportedMake = false;
      int tag = 0;
      int numOfTotalEntries = this._imgReader.getNext2BytesAsInt();
      this._blockIndex += 2;

      for (int i = 0; i < numOfTotalEntries; i++) {
         tag = this._imgReader.getNext2BytesAsInt();
         if (tag == 40091) {
            this._blockIndex += 2;
            this._title = this.getEntryValue(1);
         } else if (tag == 40092) {
            this._blockIndex += 2;
            this._comment = this.getEntryValue(1);
         } else if (tag == 40093) {
            this._blockIndex += 2;
            this._author = this.getEntryValue(1);
         } else if (tag == 40094) {
            this._blockIndex += 2;
            this._keywords = this.getEntryValue(1);
         } else if (tag == 271) {
            this._blockIndex += 2;
            this._make = this.getEntryValue(2);
            supportedMake = this.isSupportedMake(this._make.toUpperCase().trim());
            jeidaTagsExists = true;
         } else if (tag == 33432) {
            this._blockIndex += 2;
            this._copyright = this.getEntryValue(2);
            jeidaTagsExists = true;
         } else if (tag == 270) {
            this._blockIndex += 2;
            this._description = this.getEntryValue(2);
            jeidaTagsExists = true;
         } else if (tag == 272) {
            this._blockIndex += 2;
            this._model = this.getEntryValue(2);
            jeidaTagsExists = true;
         } else if (tag == 274) {
            this._blockIndex += 2;
            this._orientation = this.getEntryValue(2);
            jeidaTagsExists = true;
         } else if (tag == 34665) {
            int tempBlockIndex = this._blockIndex;
            this._blockIndex += 8;
            this._exifSubIFDOffset = this._imgReader.getNext4BytesAsInt();
            this._blockIndex = tempBlockIndex;
            this._blockIndex += 12;
            jeidaTagsExists = true;
         } else {
            this._blockIndex += 12;
         }
      }

      if (!supportedMake && jeidaTagsExists) {
         this._make = "";
         throw new Object();
      }
   }

   private final void parseSubIFDSegment() {
      int tag = 0;
      int numOfTotalEntries = this._imgReader.getNext2BytesAsInt();
      this._blockIndex += 2;
      boolean makerNoteExists = false;
      int tempBlockIndex = this._blockIndex;

      for (int i = 0; i < numOfTotalEntries; i++) {
         tag = this._imgReader.getNext2BytesAsInt();
         if (tag == 37500) {
            makerNoteExists = true;
            i = numOfTotalEntries;
         } else {
            this._blockIndex += 12;
         }
      }

      this._blockIndex = tempBlockIndex;

      for (int i = 0; i < numOfTotalEntries; i++) {
         tag = this._imgReader.getNext2BytesAsInt();
         if (tag == 40092) {
            this._blockIndex += 2;
            this._comment = this.getEntryValue(2, makerNoteExists);
         } else if (tag == 33434) {
            this._blockIndex += 2;
            this._exposureTime = this.getEntryValue(2, makerNoteExists);
         } else if (tag == 33437) {
            this._blockIndex += 2;
            this._FNumber = this.getEntryValue(2, makerNoteExists);
         } else if (tag == 34855) {
            this._blockIndex += 2;
            this._ISOSpeed = this.getEntryValue(2, makerNoteExists);
         } else if (tag == 36867) {
            this._blockIndex += 2;
            this._dateTimeTaken = this.getEntryValue(2, makerNoteExists);
         } else {
            this._blockIndex += 12;
         }
      }
   }

   private final boolean isSupportedMake(String make) {
      return make.equals("CANON")
         || make.equals("FUJIFILM")
         || make.equals("EASTMAN KODAK COMPANY")
         || make.equals("NIKON")
         || make.equals("OLYMPUS OPTICAL CO.,LTD")
         || make.equals("RICOH")
         || make.equals("SANYO ELECTRIC CO.,LTD.")
         || make.equals("SONY");
   }

   private final String getEntryValue(int exifType) {
      return this.getEntryValue(exifType, false);
   }

   private final String getEntryValue(int exifType, boolean makerNoteExists) {
      int entryOffset = 0;
      int makerNoteOffset = 0;
      int entryType = this._imgReader.getNext2BytesAsInt();
      this._blockIndex += 2;
      int entryLength = this._imgReader.getNext4BytesAsInt();
      this._blockIndex += 4;
      if (makerNoteExists && !this._make.toUpperCase().equals("NIKON")) {
         this._headerStartIndex = 0;
         makerNoteOffset = 12;
      }

      if (entryType == 2 || exifType == 1) {
         int size = Math.max(4, entryLength);
         byte[] byteValue = new byte[size + 1];
         if (entryLength > 4) {
            entryOffset = this._imgReader.getNext4BytesAsInt();
            this._blockIndex += 4;

            for (int j = 0; j < entryLength; j++) {
               byteValue[j + 1] = this._imageBytes[this._headerStartIndex + entryOffset + makerNoteOffset + j];
            }
         } else if (entryLength != 0) {
            String stringValue = "";

            for (int i = 0; i < 4; i++) {
               byteValue[i + 1] = this._imageBytes[this._blockIndex + i];
            }

            this._blockIndex += 4;
         } else {
            this._blockIndex += 4;
         }

         try {
            String stringValue;
            if (exifType == 1) {
               stringValue = (String)(new Object(byteValue, "UTF-16BE"));
            } else if (exifType == 2) {
               stringValue = (String)(new Object(byteValue, "US-ASCII"));
            } else {
               stringValue = (String)(new Object(byteValue));
            }

            return stringValue.trim();
         } finally {
            ;
         }
      } else if (entryType == 1) {
         int size = Math.max(4, entryLength);
         byte[] byteValue = new byte[size + 1];
         String var15 = "";
         int tempBlockIndex = this._blockIndex;
         if (entryLength > 4) {
            entryOffset = this._imgReader.getNext4BytesAsInt();
            this._blockIndex = this._headerStartIndex + entryOffset + makerNoteOffset;
         }

         for (int i = 0; i < entryLength; i++) {
            var15 = ((StringBuffer)(new Object())).append(var15).append(String.valueOf(this._imgReader.getNextByteAsInt())).toString();
            this._blockIndex++;
         }

         this._blockIndex = tempBlockIndex;
         this._blockIndex += 4;
         return var15;
      } else if (entryType == 3) {
         String stringValue = "";
         int tempBlockIndex = this._blockIndex;
         if (entryLength > 4) {
            entryOffset = this._imgReader.getNext4BytesAsInt();
            this._blockIndex = this._headerStartIndex + entryOffset + makerNoteOffset;
         }

         for (int i = 0; i < entryLength; i++) {
            stringValue = ((StringBuffer)(new Object())).append(stringValue).append(String.valueOf(this._imgReader.getNext2BytesAsInt())).toString();
            this._blockIndex += 2;
         }

         this._blockIndex = tempBlockIndex;
         this._blockIndex += 4;
         return stringValue;
      } else if (entryType == 4) {
         String stringValue = "";
         int tempBlockIndex = this._blockIndex;
         if (entryLength > 4) {
            entryOffset = this._imgReader.getNext4BytesAsInt();
            this._blockIndex = this._headerStartIndex + entryOffset + makerNoteOffset;
         }

         for (int i = 0; i < entryLength; i++) {
            stringValue = ((StringBuffer)(new Object())).append(stringValue).append(String.valueOf(this._imgReader.getNext4BytesAsInt())).toString();
            this._blockIndex += 4;
         }

         this._blockIndex = tempBlockIndex;
         this._blockIndex += 4;
         return stringValue;
      } else if (entryType == 5) {
         entryOffset = this._imgReader.getNext4BytesAsInt();
         this._blockIndex += 4;
         int tempBlockIndex = this._blockIndex;
         this._blockIndex = this._headerStartIndex + entryOffset + makerNoteOffset;
         int numerator = this._imgReader.getNext4BytesAsInt();
         this._blockIndex += 4;
         int denominator = this._imgReader.getNext4BytesAsInt();
         this._blockIndex = tempBlockIndex;
         return ((StringBuffer)(new Object())).append(String.valueOf(numerator)).append("/").append(String.valueOf(denominator)).toString();
      } else {
         return "";
      }
   }

   private final void removeFromOutput(String removeString) {
      this._comment = StringUtilities.removeChars(this._comment, removeString);
      this._author = StringUtilities.removeChars(this._author, removeString);
      this._keywords = StringUtilities.removeChars(this._keywords, removeString);
      this._copyright = StringUtilities.removeChars(this._copyright, removeString);
      this._dateTimeTaken = StringUtilities.removeChars(this._dateTimeTaken, removeString);
      this._title = StringUtilities.removeChars(this._title, removeString);
      this._description = StringUtilities.removeChars(this._description, removeString);
      this._make = StringUtilities.removeChars(this._make, removeString);
      this._model = StringUtilities.removeChars(this._model, removeString);
      this._orientation = StringUtilities.removeChars(this._orientation, removeString);
      this._exposureTime = StringUtilities.removeChars(this._exposureTime, removeString);
      this._FNumber = StringUtilities.removeChars(this._FNumber, removeString);
      this._ISOSpeed = StringUtilities.removeChars(this._ISOSpeed, removeString);
   }
}
