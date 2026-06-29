package net.rim.device.internal.media.metadata;

class JPEGMetadataExtractor$littleEndianReader extends JPEGMetadataExtractor$imageDataReader {
   private final JPEGMetadataExtractor this$0;

   private JPEGMetadataExtractor$littleEndianReader(JPEGMetadataExtractor _1) {
      super(null);
      this.this$0 = _1;
   }

   @Override
   public int getNext4BytesAsInt() {
      return (this.this$0._imageBytes[this.this$0._blockIndex + 3] & 0xFF) << 24
         | (this.this$0._imageBytes[this.this$0._blockIndex + 2] & 0xFF) << 16
         | (this.this$0._imageBytes[this.this$0._blockIndex + 1] & 0xFF) << 8
         | (this.this$0._imageBytes[this.this$0._blockIndex] & 0xFF) << 0;
   }

   @Override
   public int getNext2BytesAsInt() {
      return (this.this$0._imageBytes[this.this$0._blockIndex + 1] & 0xFF) << 8 | (this.this$0._imageBytes[this.this$0._blockIndex] & 0xFF) << 0;
   }

   @Override
   public int getNextByteAsInt() {
      return (this.this$0._imageBytes[this.this$0._blockIndex] & 0xFF) << 0;
   }

   JPEGMetadataExtractor$littleEndianReader(JPEGMetadataExtractor x0, JPEGMetadataExtractor$1 x1) {
      this(x0);
   }
}
