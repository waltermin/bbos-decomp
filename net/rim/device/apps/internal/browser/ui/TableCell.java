package net.rim.device.apps.internal.browser.ui;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;

public final class TableCell extends VerticalIndentFieldManager {
   private int _startRow;
   private int _startCol;
   private int _rowSpan;
   private int _colSpan;
   private int _drawGeneration;
   private boolean _containsNestedTable;
   public static final int ALIGN_LEFT;
   public static final int ALIGN_HCENTER;
   public static final int ALIGN_RIGHT;
   private static final long[] ALIGNMENT_TRANSLATOR = new long[]{
      4294967296L,
      12884901888L,
      8589934592L,
      3490316314L,
      25769803779L,
      34359738375L,
      42949672969L,
      51539607563L,
      64424509455L,
      64424509455L,
      73014444048L,
      103079215124L,
      107374182424L,
      146028888093L,
      150323855395L,
      154618822692L,
      -3455949652384284636L,
      21474836480L,
      90194313229L,
      193273528365L,
      244813135921L,
      266287972411L,
      270582939710L,
      429496729685L
   };

   public TableCell(Table parentTable, int startRow, int startCol, int rowSpan, int colSpan, int alignment) {
      super(ALIGNMENT_TRANSLATOR[alignment] | 1152921504606846976L);
      this._startRow = startRow;
      this._startCol = startCol;
      this._rowSpan = rowSpan;
      this._colSpan = colSpan;
   }

   public final int getStartRow() {
      return this._startRow;
   }

   public final int getStartCol() {
      return this._startCol;
   }

   public final int getRowSpan() {
      return this._rowSpan;
   }

   public final int getColSpan() {
      return this._colSpan;
   }

   @Override
   public final void add(Field field, int indentAmount) {
      if (field instanceof Table) {
         this._containsNestedTable = true;
      }

      super.add(field, indentAmount);
   }

   public final int getPreferredCellWidth(int maxLeafWidth) {
      int maxWidth = 0;
      int numFields = this.getFieldCount();

      for (int i = 0; i < numFields; i++) {
         Field currField = this.getField(i);
         if (currField instanceof Table) {
            this.layoutChild(currField, Integer.MAX_VALUE, Integer.MAX_VALUE);
         } else {
            this.layoutChild(currField, maxLeafWidth, Integer.MAX_VALUE);
         }

         maxWidth = Math.max(maxWidth, currField.getWidth());
      }

      return maxWidth;
   }

   public final int getPreferredCellHeight(int actualCellWidth) {
      int totalHeight = 0;
      int numFields = this.getFieldCount();

      for (int i = 0; i < numFields; i++) {
         Field currField = this.getField(i);
         totalHeight += currField.getHeight();
      }

      return totalHeight;
   }

   @Override
   public final int moveFocus(int amount, int status, int time) {
      throw new RuntimeException("cod2jar: tail call (jumpspecial)");
   }

   @Override
   public final void onUnfocus() {
      throw new RuntimeException("cod2jar: tail call (jumpspecial)");
   }

   @Override
   protected final void onFocus(int direction) {
      super.onFocus(direction);
      if (this._containsNestedTable) {
         Field currField = this.getFieldWithFocus();
         if (!(currField instanceof Table)) {
            this.getScreen().ensureRegionVisible(this, currField.getLeft(), currField.getTop(), currField.getWidth(), currField.getHeight());
         }
      } else {
         int offset = 2;
         this.getScreen().ensureRegionVisible(this, -offset, -offset, this.getWidth() + offset * 2, this.getHeight() + offset * 2);
      }
   }

   @Override
   protected final void paint(Graphics graphics) {
      throw new RuntimeException("cod2jar: tail call (jumpspecial)");
   }

   public final boolean requiresDraw(int drawGeneration) {
      if (this._drawGeneration == drawGeneration) {
         return false;
      }

      this._drawGeneration = drawGeneration;
      return true;
   }
}
