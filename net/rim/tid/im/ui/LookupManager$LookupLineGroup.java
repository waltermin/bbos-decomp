package net.rim.tid.im.ui;

import net.rim.device.api.ui.DrawTextParam;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;

class LookupManager$LookupLineGroup {
   private LookupManager$LookupLineGroup _next;
   protected int _startIndex;
   protected int _endIndex;
   protected boolean _isAdditionalVariants;
   protected byte _typeWidth;
   protected int _firstItem;
   protected int _lastItem;
   protected byte _style;
   private final LookupManager this$0;

   public void init() {
      throw null;
   }

   public void reset() {
      throw null;
   }

   public void listOfVariantsChanged() {
      throw null;
   }

   public void paint(Graphics _1, Font _2, DrawTextParam _3, int _4) {
      throw null;
   }

   public int getHeight(Font _1) {
      throw null;
   }

   public int getWidth(Font _1) {
      throw null;
   }

   public void setStyle(byte style) {
      this._style = style;
   }

   public void layout(Font _1, boolean _2, int _3) {
      throw null;
   }

   public boolean handleElementTraverse(int _1) {
      throw null;
   }

   public int getVerticalElementCount() {
      throw null;
   }

   public int getMaxVerticalElementsPerFrame() {
      throw null;
   }

   public LookupManager$LookupLineGroup(LookupManager _1, boolean isAdditionalVariants) {
      this.this$0 = _1;
      this._firstItem = -1;
      this._lastItem = -1;
      this._isAdditionalVariants = isAdditionalVariants;
   }

   public void setLabels(String leftLabel, String rightLabel) {
   }

   protected void computeIndexes() {
      int addCount = this.this$0._currentVariant.getAdditionalVariantsCount();
      if (this._isAdditionalVariants) {
         this._startIndex = 0;
         this._endIndex = addCount;
      } else {
         this._startIndex = addCount;
         this._endIndex = this.this$0._currentVariant.getVariantsCount();
      }
   }

   public int nextPage() {
      if (this._lastItem < this._endIndex - 1) {
         this.this$0._currentVariant.setVariantIndex(this._lastItem);
         return 0;
      } else {
         return 4;
      }
   }

   public int prevPage() {
      if (this._firstItem > this._startIndex) {
         this.this$0._currentVariant.setVariantIndex(this._firstItem - 1);
         return 0;
      } else {
         return 4;
      }
   }

   public int selectElement(int index) {
      if (index >= 1 && index <= this._lastItem - this._firstItem) {
         index += this._firstItem - 1;
         this.this$0._currentVariant.setVariantIndex(index);
         return 0;
      } else {
         return 4;
      }
   }

   public int selectElementGetShift(int[] res) {
      int index = res[0];
      if (index < this._lastItem - this._firstItem) {
         res[0] = index - (this.this$0._currentIndex - this._firstItem);
         if (this.this$0._currentVariant.isVariantsSeparated()) {
            res[0]++;
         }

         return 0;
      } else {
         return 4;
      }
   }

   public Font getFont(Font fm) {
      return fm;
   }
}
