package net.rim.device.apps.internal.docview.core;

final class ArznImageContainer extends ArznObject {
   private ArznImageData _imageData;

   @Override
   final void parse(ArznParsingInfo parsingData) {
      this._imageData = this.addImage(parsingData);
      super.parse(parsingData);
   }

   @Override
   final void initialize(UCSParser docParser, ArznClassFactory classFactory, ArznObject parent) {
      super.initialize(docParser, classFactory, parent);
      this._imageData = null;
   }

   @Override
   protected final int parseContainerData(ArznParsingInfo parsingData) {
      int offset = 0;
      switch (super._ucsParser.getCurrentCommandCode()) {
         case 82:
            if (this._imageData != null) {
               int width = super._ucsParser.readUnsignedInt();
               offset += 4;
               int height = super._ucsParser.readUnsignedInt();
               offset += 4;
               this._imageData.setDimensions(width, height);
               if (super._ucsParser.getCurrentCommandSize() > offset + 2) {
                  int imageNameLength = super._ucsParser.readUnsignedShort();
                  offset += 2;
                  if (imageNameLength > 0) {
                     this._imageData.setName(super._ucsParser.readString(true, imageNameLength).toString());
                     offset += imageNameLength;
                  }
               }
            }
         default:
            return offset;
      }
   }

   @Override
   protected final void addImageContents(byte[] imageContents) {
      if (this._imageData != null) {
         this._imageData.addContents(imageContents);
      }
   }
}
