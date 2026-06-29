package net.rim.wica.runtime.ui.internal.component;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.FocusChangeListener;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.FontFamily;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.MathUtilities;
import net.rim.wica.runtime.metadata.component.ui.StyleCollection;
import net.rim.wica.runtime.metadata.component.ui.UIComponent;
import net.rim.wica.runtime.metadata.component.ui.control.TableColumnModel;
import net.rim.wica.runtime.metadata.component.ui.control.TableModel;
import net.rim.wica.runtime.resources.RuntimeResources;
import net.rim.wica.runtime.ui.internal.component.table.ColumnData;
import net.rim.wica.runtime.ui.internal.component.table.FontStyleColor;
import net.rim.wica.runtime.ui.internal.component.table.TableData;
import net.rim.wica.runtime.ui.internal.component.table.TableField;

public class WicletTable extends PagedView implements FocusChangeListener, FieldChangeListener {
   private TableModel _tableModel;
   private TableField _tableField;
   private PagingController _paging;
   private StyleCollection _styles;

   public WicletTable(ScreenContext context, TableModel tableModel, long style) {
      super(context, tableModel, style);
      this._tableModel = tableModel;
      this._paging = new PagingController(this, tableModel.getRowCount(), tableModel.getVisibleRows(), tableModel.getSelectedIndex());
   }

   @Override
   public void add(int fromIndex, int count) {
      UIComponent[] columnModels = this._tableModel.getChildren();
      int rowCount = this._tableModel.getRowCount();
      int numOfCols = columnModels == null ? 0 : columnModels.length;
      if ((rowCount > 0 || this._tableModel.isShowHeader()) && numOfCols > 0) {
         if (this._tableField != null) {
            this.update(fromIndex, count);
         } else {
            this._styles = ((UIComponent)this.getModel()).getScreen().getWiclet().getStyles();
            ColumnData[] columns = new ColumnData[numOfCols];

            for (int i = numOfCols - 1; i >= 0; i--) {
               TableColumnModel columnModel = (TableColumnModel)columnModels[i];
               String[] cells = new Object[count];
               int j = fromIndex + count - 1;

               for (int rowIndex = count - 1; j >= fromIndex; rowIndex--) {
                  cells[rowIndex] = columnModel.getCell(j);
                  j--;
               }

               ColumnData column = columns[i] = new ColumnData(cells, columnModel.getTitle());
               if (!columnModel.isAutoSized()) {
                  column.setWidthPercentage(columnModel.getWidthPercentage());
               }

               column.setFrozen(columnModel.isFrozen());
               column.setHidden(!columnModel.isVisible());
               column.setSuperStyle(this.getFontStyleColor(columnModel.getStyle()));
               column.setHeaderStyle(this.getFontStyleColor(columnModel.getHeaderStyle()));
               column.setEvenRowStyle(this.getFontStyleColor(columnModel.getEvenRowStyle()));
               column.setOddRowStyle(this.getFontStyleColor(columnModel.getOddRowStyle()));
            }

            TableData tableData = new TableData(columns);
            tableData.setSuperStyle(this.getFontStyleColor(this._tableModel.getStyle()));
            tableData.setHeaderStyle(this.getFontStyleColor(this._tableModel.getHeaderStyle()));
            tableData.setEvenRowStyle(this.getFontStyleColor(this._tableModel.getEvenRowStyle()));
            tableData.setOddRowStyle(this.getFontStyleColor(this._tableModel.getOddRowStyle()));
            tableData.setFont(this.getTableFont());
            tableData.setGridVisible(this._tableModel.isShowGridline());
            tableData.setRowSelectorVisible(this._tableModel.isShowRowSelector());
            tableData.setHeaderVisible(this._tableModel.isShowHeader());
            this._tableField = new WicletTable$TableFieldMultiFocusable(tableData);
            this._tableField.setFocusListener(this);
            this._tableField.setChangeListener(this);
         }

         this.add(this._tableField);
      }
   }

   @Override
   public void update(int fromIndex, int count) {
      if (this._tableField != null) {
         TableData tableData = this._tableField.getTableData();
         UIComponent[] columnModels = this._tableModel.getChildren();
         tableData.setNumOfRows(count);

         for (int j = (columnModels == null ? 0 : columnModels.length) - 1; j >= 0; j--) {
            ColumnData columnData = tableData.getColumn(j);
            TableColumnModel columnModel = (TableColumnModel)columnModels[j];
            columnData.setHidden(!columnModel.isVisible());
            columnData.setFrozen(columnModel.isFrozen());
            columnData.setHeader(columnModel.getTitle());
            int i = fromIndex + count - 1;

            for (int row = count - 1; i >= fromIndex; row--) {
               columnData.setCell(row, columnModel.getCell(i));
               i--;
            }
         }

         this._tableField.refresh();
      }
   }

   @Override
   public void setSelected(int index) {
      if (this._tableField != null) {
         int selectedColumnIndex = this._tableModel.getSelectedColumnIndex();
         Field fieldWithFocus = this.getFieldWithFocus();
         if (this._tableField.equals(fieldWithFocus)) {
            if (this._paging != null && index == -1 && this._tableModel.getSelectedRowIndex() != -1) {
               this._paging.setSelectedRelative(this._tableField.getSelectedRow());
               this.setTableModelSelection(this._paging.getSelected(), this._tableField.getSelectedCol());
               return;
            }
         } else if (fieldWithFocus != null) {
            if (index == -1) {
               this._tableModel.setFocus(false);
               this.setTableModelSelection(-1, -1);
               return;
            }

            this.delete(fieldWithFocus);
            this.insert(fieldWithFocus, 0);
            this._paging.setSelectedRelative(index);
            this.setTableModelSelection(this._paging.getSelected(), selectedColumnIndex);
         } else if (this._tableModel.getScreen().isDisplayed()) {
            this._tableField.setSelectedRow(index);
            return;
         }

         this._tableField.setSelectedCol(selectedColumnIndex);
         this._tableField.setSelectedRow(index);
      }
   }

   @Override
   public void focusChanged(Field field, int eventType) {
      boolean isFocus = eventType != 3;
      int selectedRow = -1;
      int selectedColumn = -1;
      if (isFocus) {
         selectedRow = this._tableField.getSelectedRow();
         selectedColumn = this._tableField.getSelectedCol();
      }

      this._paging.setSelectedRelative(selectedRow);
      this.setTableModelSelection(this._paging.getSelected(), selectedColumn);
      this._tableModel.setFocus(isFocus);
      this._tableModel.eventOccurred(1);
   }

   @Override
   public void fieldChanged(Field field, int context) {
      int colIndex = this._tableField.getSelectedCol();
      ((TableColumnModel)this._tableModel.getChildren()[colIndex]).setFrozen(this._tableField.getTableData().getColumn(colIndex).isFrozen(), true);
   }

   @Override
   public void update(int row) {
      super.update(row);
      this._paging.setSelectedAndSize(this._tableModel.getSelectedIndex(), this._tableModel.getRowCount());
   }

   @Override
   protected void makeMenu(Menu menu, int context) {
      this._paging.makeContextMenu(menu, context);
   }

   private Font getTableFont() {
      int styleId = this._tableModel.getStyle();
      if (styleId == -1) {
         return null;
      }

      Font font = this.getFont();
      int fontSize = font.getHeight();
      int fontStyle = font.getStyle();
      FontFamily fontFamily = font.getFontFamily();
      if (this._styles.hasProperty(styleId, 2)) {
         label34:
         try {
            fontFamily = FontFamily.forName(this._styles.getStringProperty(styleId, 2));
         } finally {
            break label34;
         }
      }

      if (this._styles.hasProperty(styleId, 3)) {
         fontSize = Ui.convertSize(this._styles.getIntProperty(styleId, 3), 3, 0);
         if (!fontFamily.isHeightSupported(fontSize)) {
            int[] heights = fontFamily.getHeights();
            int heightIndex = MathUtilities.clamp(0, -Arrays.binarySearch(heights, fontSize) - 2, heights.length - 1);
            fontSize = heights[heightIndex];
         }
      }

      return fontFamily.getFont(fontStyle, fontSize, 0);
   }

   private void setTableModelSelection(int row, int col) {
      this._tableModel.setSelectedRowIndex(row, true);
      this._tableModel.setSelectedColumnIndex(col, true);
   }

   private FontStyleColor getFontStyleColor(int styleId) {
      if (styleId == -1) {
         return null;
      }

      FontStyleColor style = new FontStyleColor();
      if (this._styles.hasProperty(styleId, 0)) {
         style.setBackgroundColor(this._styles.getIntProperty(styleId, 0));
      }

      if (this._styles.hasProperty(styleId, 1)) {
         style.setForegroundColor(this._styles.getIntProperty(styleId, 1));
      }

      int fontStyle = 0;
      if (this._styles.hasProperty(styleId, 4)) {
         fontStyle |= 1;
      }

      if (this._styles.hasProperty(styleId, 5)) {
         fontStyle |= 2;
      }

      if (this._styles.hasProperty(styleId, 7)) {
         fontStyle |= 4;
      }

      if (fontStyle != 0) {
         style.setFontStyle(fontStyle);
      }

      return style;
   }

   static {
      TableField.setMenuLabels(RuntimeResources.getResourceBundleFamily(), 104, 106, 108);
   }
}
