package net.rim.wica.runtime.metadata.internal.component.ui.control;

import java.util.Hashtable;
import net.rim.wica.runtime.metadata.component.DataCollection;
import net.rim.wica.runtime.metadata.component.ui.UIComponent;
import net.rim.wica.runtime.metadata.component.ui.UIContainer;
import net.rim.wica.runtime.metadata.component.ui.control.TableColumnModel;
import net.rim.wica.runtime.metadata.component.ui.control.TableModel;
import net.rim.wica.runtime.metadata.internal.component.ui.ScreenModelImpl;
import net.rim.wica.runtime.metadata.internal.component.ui.UIComponentImpl;
import net.rim.wica.runtime.metadata.internal.component.ui.UIControlImpl;
import net.rim.wica.runtime.metadata.internal.util.ArrayInValueResolver;
import net.rim.wica.runtime.util.LongVector;

public class TableModelImpl extends UIControlImpl implements TableModel {
   private UIComponent[] _children;
   private int _evenRowStyle = -1;
   private int _visibleRows = -1;
   private int _selectedRowIndex = -1;
   private int _selectedColumnIndex = -1;
   private int _headerStyle = -1;
   private int _oddRowStyle = -1;
   private boolean _showGridline;
   private boolean _showHeader;
   private boolean _showRowSelector;
   private String _gridlineColor;
   private Hashtable _componentMap;
   private boolean _hasFocus = false;
   private int _firstRowIndex;
   private int _firstColIndex;

   public void addColumn(String columnName, TableColumnModel column) {
      this._componentMap.put(columnName, column);
   }

   public int getColumnCount() {
      return this._children == null ? 0 : this._children.length;
   }

   @Override
   public int getOddRowStyle() {
      return this._oddRowStyle;
   }

   @Override
   public String getGridlineColor() {
      return this._gridlineColor;
   }

   @Override
   public int getHeaderStyle() {
      return this._headerStyle;
   }

   @Override
   public int getLayout() {
      return -1;
   }

   @Override
   public int getRowCount() {
      return super._value == null ? 0 : ((LongVector)super._value).size();
   }

   @Override
   public int getVisibleRows() {
      return this._visibleRows;
   }

   @Override
   public boolean isShowGridline() {
      return this._showGridline;
   }

   @Override
   public boolean isShowHeader() {
      return this._showHeader;
   }

   @Override
   public boolean isShowRowSelector() {
      return this._showRowSelector;
   }

   @Override
   public synchronized void setSelectedColumnIndex(int index, boolean fromUI) {
      if (this._selectedColumnIndex != index && (fromUI || this._hasFocus && index >= this._firstColIndex && index < this.getColumnCount())) {
         this._selectedColumnIndex = index;
         this.onValueChanged(fromUI);
      }
   }

   @Override
   public synchronized void setSelectedRowIndex(int index, boolean fromUI) {
      if (this._selectedRowIndex != index && (fromUI || this._hasFocus && index >= this._firstRowIndex && index < this.getRowCount())) {
         this._selectedRowIndex = index;
         this.onValueChanged(fromUI);
      }
   }

   @Override
   public Object getSelectedData() {
      return super._mappedValue;
   }

   @Override
   public long getSelectedDataType() {
      return 6;
   }

   @Override
   public int getSelectedColumnIndex() {
      return this._selectedColumnIndex;
   }

   @Override
   public int getSelectedRowIndex() {
      return this._selectedRowIndex;
   }

   @Override
   public int getEvenRowStyle() {
      return this._evenRowStyle;
   }

   @Override
   public boolean isMultiSelect() {
      return false;
   }

   @Override
   public synchronized int[] getSelectedIndices() {
      return null;
   }

   @Override
   public synchronized void setSelectedIndices(int[] selected, boolean fromUI) {
   }

   @Override
   public synchronized void addSelectedIndex(int index, boolean fromUI) {
      this.setSelectedRowIndex(index, fromUI);
   }

   @Override
   public synchronized void removeSelectedIndex(int index, boolean fromUI) {
      this.setSelectedRowIndex(-1, fromUI);
   }

   @Override
   public void setSelectedIndex(int index, boolean fromUI) {
      this.setSelectedRowIndex(index, fromUI);
   }

   @Override
   public int getSelectedIndex() {
      return this.getSelectedRowIndex();
   }

   @Override
   public boolean isSelected(int index) {
      return this._selectedRowIndex == index;
   }

   @Override
   public UIComponent[] getChildren() {
      return this._children;
   }

   @Override
   public UIComponent getComponent(String name) {
      return (UIComponent)this._componentMap.get(name);
   }

   @Override
   public void setFocus(boolean isFocus) {
      this._hasFocus = isFocus;
   }

   @Override
   public void updateData() {
      super.updateData();
      int size = this._children == null ? 0 : this._children.length;

      for (int i = 0; i < size; i++) {
         ((UIComponentImpl)this._children[i]).updateData();
      }
   }

   @Override
   public void init() {
      super.init();
      int size = this._children == null ? 0 : this._children.length;

      for (int i = 0; i < size; i++) {
         ((UIComponentImpl)this._children[i]).init();
      }
   }

   @Override
   public boolean isReadOnly() {
      return true;
   }

   @Override
   protected void updateMappedValue() {
      super._mappedValue = this._selectedRowIndex == -1 ? null : new Object(((LongVector)super._value).elementAt(this._selectedRowIndex));
   }

   protected TableModelImpl(
      int id,
      int style,
      int bits,
      int x,
      int y,
      int initId,
      int headerStyle,
      int evenRowStyle,
      int oddRowStyle,
      String gridlineColor,
      boolean gridline,
      boolean header,
      boolean selector,
      int childCount,
      int rows
   ) {
      super(id, 140, style, bits, x, y, initId);
      this._evenRowStyle = evenRowStyle;
      this._gridlineColor = gridlineColor;
      this._headerStyle = headerStyle;
      this._oddRowStyle = oddRowStyle;
      this._showGridline = gridline;
      this._showHeader = header;
      this._showRowSelector = selector;
      this._visibleRows = rows;
   }

   @Override
   protected void onValueChanged(boolean fromUI) {
      super._valueChanged = true;
      ((ScreenModelImpl)this.getScreen()).onModify();
   }

   @Override
   protected void resolveInValue() {
      if (super._inValue != null) {
         if (super._value == null) {
            super._value = new LongVector();
         } else {
            ((LongVector)super._value).removeAllElements();
         }

         ArrayInValueResolver.resolve(super._inValue, this, super._value, null);
         this.validateSelection();
         super._valueChanged = true;
      }
   }

   private void validateSelection() {
      int size = ((LongVector)super._value).size();
      if (size <= 0) {
         if (this._selectedRowIndex != -1) {
            this._selectedRowIndex = -1;
         }
      } else {
         if (this._selectedRowIndex < 0) {
            Object dataMappedValue = this.getDataMappedValue();
            long mappedHandle = dataMappedValue == null ? -1 : dataMappedValue;
            if (mappedHandle != -1) {
               DataCollection dc = this.getScreen().getWiclet().getDataCollection((int)(mappedHandle >> 32));
               if (dc.getDef().hasKey()) {
                  int index = ((LongVector)super._value).indexOf(mappedHandle);
                  if (index != -1) {
                     this._selectedRowIndex = index;
                  }
               } else if (dc.contains(mappedHandle)) {
                  LongVector dataHandles = (LongVector)super._value;

                  for (int i = dataHandles.size() - 1; i >= 0; i--) {
                     long handle = dataHandles.elementAt(i);
                     if (handle != -1 && dc.contains(handle) && dc.equals(handle, mappedHandle)) {
                        this._selectedRowIndex = i;
                        break;
                     }
                  }
               }
            }
         }

         if (this._selectedRowIndex >= size) {
            this._selectedRowIndex = -1;
            return;
         }
      }
   }

   public TableModelImpl(
      int id,
      UIContainer parent,
      int style,
      int bits,
      int x,
      int y,
      int initId,
      int changeEvent,
      int fetchMoreEvent,
      Object inValue,
      int[] mapping,
      int headerStyle,
      int evenRowStyle,
      int oddRowStyle,
      String gridlineColor,
      boolean gridline,
      boolean header,
      boolean selector,
      int childCount,
      int rows
   ) {
      super(id, 140, parent, style, bits, x, y, initId, inValue);
      this._evenRowStyle = evenRowStyle;
      this._gridlineColor = gridlineColor;
      this._headerStyle = headerStyle;
      this._oddRowStyle = oddRowStyle;
      this._showGridline = gridline;
      this._showHeader = header;
      this._firstRowIndex = this._showHeader ? -1 : 0;
      this._showRowSelector = selector;
      this._firstColIndex = this._showRowSelector ? -1 : 0;
      this._visibleRows = rows;
      this.setMapping(mapping);
      this.setEvent(1, changeEvent);
      this.setEvent(3, fetchMoreEvent);
      if (childCount > 0) {
         this._children = new UIComponent[childCount];
      }

      this._componentMap = (Hashtable)(new Object(childCount));
   }

   @Override
   protected boolean isMappingValid() {
      return !super._valueChanged;
   }

   @Override
   public void updateUI() {
      super.updateUI();
      int size = this._children == null ? 0 : this._children.length;

      for (int i = 0; i < size; i++) {
         ((UIComponentImpl)this._children[i]).updateUI();
      }
   }
}
