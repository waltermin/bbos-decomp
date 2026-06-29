package net.rim.device.apps.internal.browser.ui;

import net.rim.device.api.system.Clipboard;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Trackball;
import net.rim.device.api.ui.XYRect;
import net.rim.device.apps.internal.browser.util.RendererControl;
import net.rim.device.internal.ui.Cursor;

final class TextFlowManager$FocusField extends Field {
   private final TextFlowManager this$0;

   public TextFlowManager$FocusField(TextFlowManager _1) {
      super(18014398509481984L);
      this.this$0 = _1;
   }

   @Override
   protected final boolean isFocusDrawn() {
      return false;
   }

   @Override
   protected final void layout(int width, int height) {
      this.setExtent(width, height);
   }

   @Override
   protected final void paint(Graphics graphics) {
   }

   @Override
   public final Cursor getFocusCursor() {
      return this.this$0.getTopmostTFM().getDefaultCursor();
   }

   public final void delegateUpdateLayout() {
      this.updateLayout();
   }

   @Override
   public final boolean isSelectionCopyable() {
      if (this.this$0.isMinimalMenuMode()) {
         return false;
      }

      switch (this.this$0._mode) {
         case 1:
            if (this.this$0._focusState == 0 && this.this$0.getFieldWithFocus() == this.this$0._focusField) {
               return true;
            }
         default:
            return false;
         case 3:
            return true;
      }
   }

   @Override
   public final void selectionCopy(Clipboard cb) {
      switch (this.this$0._mode) {
         case 1:
            cb.put(this.this$0._rootCell.getTextFocusText(this.this$0._focusIndex));
            return;
         case 3:
            cb.put(this.this$0._rootCell.getSelectionText());
      }
   }

   @Override
   protected final boolean keyChar(char character, int status, int time) {
      if (this.this$0._mode == 2 && character == 27) {
         this.position(false);
         return true;
      } else {
         return super.keyChar(character, status, time);
      }
   }

   private final int getSelectionPosition() {
      if (this.this$0._focusState == 0) {
         int pos = this.this$0._textFlowData.getRegionStartOffset(this.this$0._textFlowData.getFocusRegion(this.this$0._focusIndex));
         int forward = this.this$0._rootCell.getNextTextCharacter(pos, 1, true);
         return forward != -1 ? forward : this.this$0._rootCell.getNextTextCharacter(pos, -1, false);
      }

      int pos = this.this$0._rootCell.getVisibleTextPosition(-1, this.this$0.getCurrentScroll(), 1);
      if (pos == -1) {
         pos = this.this$0._rootCell.getVisibleTextPosition(-1, this.this$0.getCurrentScroll(), -1);
      }

      return pos;
   }

   @Override
   public final boolean isSelecting() {
      return this.this$0._mode == 3;
   }

   @Override
   public final boolean isSelectable() {
      return this.this$0._mode != 1 && (this.this$0._mode != 2 || this.this$0._selectionPosition.textPosition != -1);
   }

   @Override
   public final void select(boolean on) {
      if (this.this$0._mode != 1 && (this.this$0._mode != 2 || on) && (this.this$0._mode != 3 || !on)) {
         this.focusRemove();
         this.this$0._mode = on ? 3 : 1;
         if (on) {
            this.this$0.trackballSensitivityOff();
            this.this$0._selectionEnd = this.this$0._selectionStart;
            this.this$0.setSelection(this.this$0._selectionStart, this.this$0._selectionEnd);
            this.focusAdd(true);
         } else {
            this.this$0.trackballSensitivityOn();
            this.this$0.resetSelection();
            int zoom = this.this$0.getZoomValue();
            int startY = this.this$0._rootCell.getYOffsetFromTextPosition(Math.min(this.this$0._selectionStart, this.this$0._selectionEnd), true);
            startY = RendererControl.fixed32DivToInt(startY, zoom);
            int endY = this.this$0._rootCell.getYOffsetFromTextPosition(Math.max(this.this$0._selectionStart, this.this$0._selectionEnd), false);
            endY = RendererControl.fixed32DivToInt(endY, zoom);
            super.invalidate(0, startY, this.getWidth(), endY - startY + 1);
            this.this$0._selectionEnd = -1;
            this.this$0._selectionStart = -1;
            this.this$0._selectionPosition.reset();
            this.getManager().setFocus(0, this.this$0.getCurrentScroll(), 512);
         }
      }
   }

   public final void position(boolean on) {
      if ((!on || this.this$0._mode == 1) && (on || this.this$0._mode == 2)) {
         this.focusRemove();
         this.this$0.adjustZoom(Integer.MIN_VALUE);
         this.this$0._mode = on ? 2 : 1;
         if (on) {
            this.this$0.trackballSensitivityOff();
            this.this$0._selectionPosition.textPosition = this.this$0._selectionStart = this.getSelectionPosition();
            this.this$0._selectionPosition.selectedRegion = -1;
            this.this$0._focusState = -1;
            this.focusAdd(true);
         } else {
            this.this$0.trackballSensitivityOn();
            this.this$0._selectionStart = -1;
            this.this$0._selectionPosition.reset();
            this.getManager().setFocus(0, this.this$0.getCurrentScroll(), 512);
         }
      }
   }

   @Override
   protected final void drawFocus(Graphics graphics, boolean on) {
      switch (this.this$0._mode) {
         case 0:
            break;
         case 1:
         default:
            if (this.this$0._focusState == 0) {
               if (on) {
                  graphics.setDrawingStyle(8, true);
               }

               this.this$0._rootCell.drawTextFocus(this.this$0._focusIndex, graphics, on, this.this$0.getZoomValue());
               return;
            }
            break;
         case 2:
            if (this.this$0._selectionPosition.textPosition >= 0) {
               this.this$0._rootCell.drawPosition(this.this$0._selectionPosition.textPosition, graphics, on);
               return;
            }

            if (this.this$0._selectionPosition.selectedRegion != -1) {
               Object selectedObject = this.this$0._textFlowData.getRegionObject(this.this$0._selectionPosition.selectedRegion);
               if (selectedObject instanceof BrowserBitmapField) {
                  BrowserBitmapField bitmapField = (BrowserBitmapField)selectedObject;
                  if (graphics.pushRegion(bitmapField.getContentRect())) {
                     bitmapField.drawOutline(graphics, on);
                  }

                  graphics.popContext();
                  return;
               }
            }
            break;
         case 3:
            this.this$0._rootCell.drawSelection(this.this$0._selectionStart, this.this$0._selectionEnd, graphics, on);
      }
   }

   private final boolean isHorizontalFocusMovement(int status) {
      return (status & 65536) != 0 || !Trackball.isSupported() && (status & 1) != 0;
   }

   @Override
   protected final int moveFocus(int amount, int status, int time) {
      switch (this.this$0._mode) {
         case 1:
            return super.moveFocus(amount, status, time);
         case 2:
         default:
            if (this.this$0._rootCell.nextSelectionPosition(this.this$0._selectionPosition, amount, this.isHorizontalFocusMovement(status), true)) {
               if (this.this$0._selectionPosition.textPosition != -1 && (status & 2) != 0) {
                  if (this.this$0._selectionStart == -1) {
                     this.this$0._selectionStart = this.this$0._selectionPosition.textPosition;
                  }

                  this.select(true);
                  this.updateSelection(this.this$0._selectionPosition.textPosition);
                  return 0;
               }

               this.this$0._selectionStart = this.this$0._selectionPosition.textPosition;
               return 0;
            }
            break;
         case 3:
            if (this.this$0._rootCell.nextSelectionPosition(this.this$0._selectionPosition, amount, this.isHorizontalFocusMovement(status), false)
               && this.this$0._selectionPosition.textPosition != -1) {
               this.updateSelection(this.this$0._selectionPosition.textPosition);
               return 0;
            }
      }

      return 0;
   }

   private final void updateSelection(int carat) {
      int start;
      int end;
      if (this.this$0._selectionPosition.selectedRegion != -1) {
         this.this$0._selectionEnd = carat;
         int regionOffset = this.this$0._textFlowData.getRegionStartOffset(this.this$0._selectionPosition.selectedRegion);
         start = Math.min(carat, regionOffset);
         end = Math.max(carat, regionOffset + this.this$0._textFlowData.getRegionLength(this.this$0._selectionPosition.selectedRegion));
      } else {
         start = Math.min(carat, this.this$0._selectionStart);
         end = Math.max(carat, this.this$0._selectionStart);
      }

      int zoom = this.this$0.getZoomValue();
      int startY = RendererControl.fixed32DivToInt(this.this$0._rootCell.getYOffsetFromTextPosition(start, true), zoom);
      int endY = RendererControl.fixed32DivToInt(this.this$0._rootCell.getYOffsetFromTextPosition(end, false), zoom);
      this.this$0._selectionPosition.textPosition = this.this$0._selectionStart = carat;
      this.this$0._selectionPosition.selectedRegion = -1;
      this.this$0.setSelection(this.this$0._selectionStart, this.this$0._selectionEnd);
      super.invalidate(0, startY, this.getWidth(), endY - startY + 1);
   }

   @Override
   protected final void moveFocus(int x, int y, int status, int time) {
      switch (this.this$0._mode) {
         case 2:
         case 3:
         default:
            int pos;
            if (this.this$0._focusSearchDirection > 0) {
               pos = this.this$0._rootCell.getVisibleTextPosition(-1, y, 1);
               if (pos == -1) {
                  pos = this.this$0._rootCell.getVisibleTextPosition(-1, y, -1);
               }
            } else {
               pos = this.this$0._rootCell.getVisibleTextPosition(-1, y, -1);
               if (pos == -1) {
                  pos = this.this$0._rootCell.getVisibleTextPosition(-1, y, 1);
               }
            }

            if (this.this$0._mode == 3) {
               this.updateSelection(pos);
               return;
            } else {
               this.this$0._selectionPosition.textPosition = this.this$0._selectionStart = pos;
               this.this$0._selectionPosition.selectedRegion = -1;
            }
         case 1:
      }
   }

   @Override
   public final void getFocusRect(XYRect rect) {
      this.getFocusRectPhantom(rect);
   }

   @Override
   public final void getFocusRectPhantom(XYRect rect) {
      switch (this.this$0._mode) {
         case 0:
            break;
         case 1:
            rect.set(
               this.this$0.getHorizontalScroll(),
               this.this$0.getCurrentScroll(),
               this.this$0.getHorizontalVisibleScroll(),
               this.this$0.getVerticalVisibleScroll()
            );
            if (this.this$0._setScroll != Integer.MAX_VALUE) {
               rect.y = this.this$0._setScroll;
            }

            this.this$0.unscaleRect(rect);
            break;
         case 2:
         default:
            if (this.this$0._selectionPosition.textPosition >= 0) {
               this.this$0._rootCell.getCharacterRect(this.this$0._selectionPosition.textPosition, rect);
               return;
            }

            if (this.this$0._selectionPosition.selectedRegion >= 0) {
               Object selectedObject = this.this$0._textFlowData.getRegionObject(this.this$0._selectionPosition.selectedRegion);
               if (selectedObject instanceof Object) {
                  Field field = (Field)selectedObject;
                  rect.set(field.getLeft(), field.getTop(), field.getWidth(), field.getHeight());
                  return;
               }
            }
            break;
         case 3:
            this.this$0._rootCell.getCharacterRect(this.this$0._selectionStart, rect);
            return;
      }
   }

   @Override
   public final void focusChangeNotify(int action) {
      if (action == 1 && this.this$0._pendingFocusIndex != -1) {
         this.this$0._focusIndex = this.this$0._pendingFocusIndex;
         this.this$0._focusState = this.this$0._pendingFocusState;
         this.this$0._pendingFocusIndex = -1;
      }

      super.focusChangeNotify(action);
   }
}
