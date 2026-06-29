package net.rim.device.apps.internal.docview.core;

final class ArznDocInfoContainer extends ArznObject {
   @Override
   protected final int parseContainerData(ArznParsingInfo parsingData) {
      int offset = 0;
      int commandCode = super._ucsParser.getCurrentCommandCode();
      boolean unicode = false;
      switch (commandCode) {
         case 40:
            int fileSizeInKBytes = this.getFileSize();
            offset += 4;
            parsingData.addDocInfoValue(commandCode, String.valueOf(fileSizeInKBytes));
            return offset;
         case 42:
         case 44:
         case 48:
            unicode = true;
         case 41:
         case 43:
         case 45:
         case 46:
         case 47:
         case 89:
            parsingData.addDocInfoValue(commandCode, super._ucsParser.readString(unicode, super._ucsParser.getCurrentCommandSize()).toString());
            return offset + super._ucsParser.getCurrentCommandSize();
         case 78:
            int width = super._ucsParser.readUnsignedInt();
            offset += 4;
            int height = super._ucsParser.readUnsignedInt();
            offset += 4;
            parsingData.addDocInfoValue(commandCode, String.valueOf(width) + 'x' + height);
            return offset;
         case 90:
         case 91:
         case 92:
         case 93:
            int uintInfo = super._ucsParser.readUnsignedInt();
            offset += 4;
            if (commandCode != 93) {
               parsingData.addDocInfoValue(commandCode, String.valueOf(uintInfo));
               return offset;
            } else {
               parsingData.addDocInfoValue(commandCode, uintInfo / 60 + ":" + uintInfo % 60);
            }
         default:
            return offset;
      }
   }

   private final int getFileSize() {
      int fileSizeInKBytes = super._ucsParser.readUnsignedInt() >> 10;
      return fileSizeInKBytes > 0 ? fileSizeInKBytes : 1;
   }
}
