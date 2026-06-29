package net.rim.device.apps.internal.phone;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Manager;
import net.rim.device.apps.internal.phone.api.PhoneUtilities;

final class EditCallNotesScreen$EditCallNotesManager extends Manager {
   private static final int HEADER = 0;
   private static final int NOTES = 1;
   private static final int CALL_CONTAINER = 2;

   EditCallNotesScreen$EditCallNotesManager() {
      super(0);
   }

   @Override
   protected final void sublayout(int width, int height) {
      int count = this.getFieldCount();

      for (int i = 0; i < count; i++) {
         Field field = this.getField(i);
         switch (i) {
            case -1:
               break;
            case 0:
            default:
               this.setPositionChild(field, 0, 0);
               this.layoutChild(field, width, height);
               break;
            case 1:
               if (PhoneUtilities.smallScreen()) {
                  int notesHeightx = height / 3;
                  this.layoutChild(field, width, notesHeightx);
                  this.setPositionChild(field, 0, height - notesHeightx);
               } else {
                  this.layoutChild(field, width, height >> 1);
                  this.setPositionChild(field, 0, height >> 1);
               }
               break;
            case 2:
               int headerHeight = this.getField(0).getHeight();
               int notesHeight = this.getField(1).getHeight();
               this.layoutChild(field, width, height - headerHeight - notesHeight - 3);
               this.setPositionChild(field, 0, headerHeight + 3);
         }
      }

      this.setExtent(width, height);
   }
}
