package net.rim.device.apps.internal.secureemail;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Manager;

class CursorProviderManagerUtilities {
   public static int getCurrentCursorPosition(Manager manager, Object context) {
      int cursorPosition = 0;
      int fieldWithFocusIndex = manager.getFieldWithFocusIndex();
      if (fieldWithFocusIndex < 0) {
         return 0;
      }

      for (int i = 0; i < fieldWithFocusIndex; i++) {
         Field child = manager.getField(i);
         if (child instanceof CursorProviderField) {
            CursorProviderField cursorProviderField = (CursorProviderField)child;
            cursorPosition += cursorProviderField.getCursorCount(null);
         }
      }

      Field child = manager.getField(fieldWithFocusIndex);
      if (child instanceof CursorProviderField) {
         CursorProviderField cursorProviderField = (CursorProviderField)child;
         cursorPosition += cursorProviderField.getCurrentCursorPosition(null);
      }

      return cursorPosition;
   }

   public static Field setCursorPosition(Manager manager, int pos, Object context) {
      Field currentCursorField = null;
      int numFields = manager.getFieldCount();

      for (int i = 0; i < numFields; i++) {
         Field child = manager.getField(i);
         if (child instanceof CursorProviderField) {
            CursorProviderField cursorProviderField = (CursorProviderField)child;
            int cursorCount = cursorProviderField.getCursorCount(null);
            if (pos < cursorCount) {
               cursorProviderField.setCursorPosition(pos, null);
               return child;
            }

            pos -= cursorCount;
         }
      }

      return currentCursorField;
   }

   public static int getCursorCount(Manager manager, Object context) {
      int cursorCount = 0;
      int numFields = manager.getFieldCount();

      for (int i = 0; i < numFields; i++) {
         Field child = manager.getField(i);
         if (child instanceof CursorProviderField) {
            CursorProviderField cursorProviderField = (CursorProviderField)child;
            cursorCount += cursorProviderField.getCursorCount(null);
         }
      }

      return cursorCount;
   }
}
