package net.rim.device.apps.internal.browser.ui;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Screen;
import net.rim.device.apps.internal.browser.util.RendererControl;
import net.rim.device.internal.ui.Cursor;

final class TextFlowManager$HoverRunnable implements Runnable {
   private final TextFlowManager this$0;

   TextFlowManager$HoverRunnable(TextFlowManager _1) {
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      this.this$0._hoverRunnableId = -1;
      Screen screen = this.this$0.getScreen();
      if (screen != null) {
         if (this.this$0.getZoomValue() > TextFlowManager.HOTSPOT_ACTIVATION_THRESHOLD) {
            if (this.this$0.getLeafFieldWithFocus() != this.this$0._focusField || this.this$0._focusState != -1) {
               this.this$0._cursorFocusRect.set(0, 0, 0, 0);
               this.this$0._pendingFocusState = -1;
               this.this$0._pendingFocusIndex = this.this$0._focusIndex;
               screen.setFocus(this.this$0._focusField, 0, 0, 0, 0);
               screen.updateDisplay();
            }
         } else {
            boolean setFocusToFocusField = false;
            Cursor cursorToDisplay = this.this$0._currentCursor;
            int zoomValue = this.this$0.getZoomValue();
            int unscaledCursorX = RendererControl.fixed32MultToInt(this.this$0._cursorXPos, zoomValue);
            int unscaledCursorY = RendererControl.fixed32MultToInt(this.this$0._cursorYPos, zoomValue);
            int focusIndex = this.this$0._rootCell.getFocusIndexFromXYPosition(unscaledCursorX, unscaledCursorY);
            if (focusIndex == -1) {
               this.this$0._cursorFocusRect.set(0, 0, 0, 0);
               setFocusToFocusField = true;
            } else {
               Field f = this.this$0.getFocusFieldFromIndex(focusIndex);
               TextFlowManager subFrame = this.this$0;
               int deltaX = 0;
               int deltaY = 0;
               if (f instanceof FrameManager) {
                  this.this$0._focusIndex = focusIndex;
                  this.this$0._focusState = 0;

                  while (true) {
                     if (!(f instanceof FrameManager)) {
                        break;
                     }

                     FrameManager fm = (FrameManager)f;
                     subFrame = (TextFlowManager)fm.getField(0);
                     deltaX += fm.getLeft();
                     deltaY += fm.getTop();
                     focusIndex = subFrame._rootCell.getFocusIndexFromXYPosition(unscaledCursorX - deltaX, unscaledCursorY - deltaY);
                     if (focusIndex == -1) {
                        break;
                     }

                     f = subFrame.getFocusFieldFromIndex(focusIndex);
                  }
               }

               if (f != null && focusIndex >= 0 && focusIndex < this.this$0._layoutFocusCount) {
                  subFrame.getFocusRectFromIndex(focusIndex, this.this$0._cursorFocusRect, true, true);
                  if (subFrame != this.this$0) {
                     this.this$0._cursorFocusRect.x += deltaX;
                     this.this$0._cursorFocusRect.y += deltaY;
                     this.this$0.scaleRect(this.this$0._cursorFocusRect);
                  }

                  if (!this.this$0._cursorFocusRect.contains(this.this$0._cursorXPos, this.this$0._cursorYPos)) {
                     setFocusToFocusField = true;
                  } else if (f instanceof TextFlowManager$FocusField) {
                     cursorToDisplay = Cursor.getPredefinedCursor(1);
                     subFrame._pendingFocusIndex = focusIndex;
                     subFrame._pendingFocusState = 0;
                     if (subFrame != this.this$0) {
                        subFrame._focusIndex = focusIndex;
                        subFrame._focusState = 0;
                     }

                     screen.setFocus(f, 0, 0, 0, 0);
                     screen.updateDisplay();
                  } else {
                     subFrame._focusIndex = focusIndex;
                     subFrame._focusState = 0;
                     if (!f.isFocus()) {
                        f.setFocus();
                     }

                     if (TextFlowManager.access$1700(this.this$0, unscaledCursorX, unscaledCursorY)) {
                        subFrame.getFocusRectFromIndex(focusIndex, this.this$0._cursorFocusRect, true, false);
                        if (subFrame != this.this$0) {
                           this.this$0._cursorFocusRect.x += deltaX;
                           this.this$0._cursorFocusRect.y += deltaY;
                           this.this$0.scaleRect(this.this$0._cursorFocusRect);
                        }
                     }

                     cursorToDisplay = f.getFocusCursor();
                  }
               } else {
                  this.this$0._cursorFocusRect.set(0, 0, 0, 0);
                  setFocusToFocusField = true;
               }
            }

            if (setFocusToFocusField && (!this.this$0._focusField.isFocus() || this.this$0._focusState != -1)) {
               this.this$0._pendingFocusState = -1;
               this.this$0._pendingFocusIndex = this.this$0._focusIndex;
               screen.setFocus(this.this$0._focusField, 0, 0, 0, 0);
            }

            this.this$0.updateCursor(cursorToDisplay);
         }
      }
   }
}
