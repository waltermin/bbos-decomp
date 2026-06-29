package net.rim.device.apps.internal.ribbon.launcher;

import net.rim.device.api.synchronization.ConverterUtilities;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.Persistable;
import net.rim.device.apps.api.ribbon.ApplicationProperties;

final class InternalApplicationProperties extends ApplicationProperties implements Persistable {
   private static final byte END_OF_DATA = 0;
   private static final byte ICON_POSITION = 1;
   private static final byte ICON_VISIBLE = 2;
   private static final byte FOLDER_NAME = 3;
   private static final byte CUSTOM_IMAGE = 4;
   private static final byte CUSTOM_FOCUS_IMAGE = 5;
   private static final byte ALIAS = 6;

   InternalApplicationProperties() {
   }

   InternalApplicationProperties(ApplicationProperties properties) {
   }

   final void writeData(DataBuffer buffer) {
      ConverterUtilities.writeInt(buffer, 1, (short)this.getPosition());
      ConverterUtilities.writeInt(buffer, 2, this.getVisible() ? 1 : 0);
      ConverterUtilities.writeStringSmart(buffer, 3, this.getFolderName());
      ConverterUtilities.writeStringSmart(buffer, 4, this.getCustomImageName());
      ConverterUtilities.writeStringSmart(buffer, 5, this.getCustomFocusImageName());
      ConverterUtilities.writeStringSmart(buffer, 6, this.getAlias());
      ConverterUtilities.writeEmptyField(buffer, 0);
   }

   final void readData(DataBuffer buffer) {
      try {
         while (!buffer.eof()) {
            switch (ConverterUtilities.getType(buffer, true)) {
               case -1:
                  ConverterUtilities.skipField(buffer);
                  break;
               case 0:
               default:
                  ConverterUtilities.skipField(buffer);
                  return;
               case 1:
                  this.setPosition(ConverterUtilities.readInt(buffer));
                  break;
               case 2:
                  this.setVisible(ConverterUtilities.readInt(buffer) != 0);
                  break;
               case 3:
                  this.setFolderName(ConverterUtilities.readString(buffer));
                  break;
               case 4:
                  this.setCustomImageName(ConverterUtilities.readString(buffer));
                  break;
               case 5:
                  this.setCustomFocusImageName(ConverterUtilities.readString(buffer));
                  break;
               case 6:
                  this.setAlias(ConverterUtilities.readString(buffer));
            }
         }
      } finally {
         return;
      }
   }
}
