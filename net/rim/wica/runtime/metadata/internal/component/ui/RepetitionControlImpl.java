package net.rim.wica.runtime.metadata.internal.component.ui;

import net.rim.wica.runtime.metadata.component.DataCollection;
import net.rim.wica.runtime.metadata.component.ui.UIComponent;
import net.rim.wica.runtime.metadata.component.ui.UIContainer;
import net.rim.wica.runtime.metadata.component.ui.control.RepetitionControl;
import net.rim.wica.runtime.metadata.internal.util.ArrayInValueResolver;
import net.rim.wica.runtime.util.LongVector;

public class RepetitionControlImpl extends UIControlImpl implements RepetitionControl {
   private boolean _isCollapsible;
   private UIComponent[] _children;
   private int _selectedIndex = -1;
   private int _visibleRows;

   public RepetitionControlImpl(
      int id,
      UIContainer parent,
      int style,
      int bits,
      int x,
      int y,
      Object inValue,
      int initEventId,
      int changeEventId,
      int[] mapping,
      boolean collapsible,
      int childCount,
      int visibleRows
   ) {
      super(id, 137, parent, style, bits, x, y, initEventId, inValue);
      this._visibleRows = visibleRows;
      this._isCollapsible = collapsible;
      if (childCount > 0) {
         this._children = new UIComponent[childCount];
      }

      this.setEvent(1, changeEventId);
      this.setMapping(mapping);
   }

   @Override
   public void clean() {
      super.clean();
      int size = this._children == null ? 0 : this._children.length;

      for (int i = 0; i < size; i++) {
         ((UIComponentImpl)this._children[i]).clean();
      }
   }

   @Override
   public UIComponent[] getChildren() {
      return this._children;
   }

   @Override
   public boolean isCollapsible() {
      return this._isCollapsible;
   }

   @Override
   public synchronized void setSelectedIndex(int index, boolean fromUI) {
      if (this._selectedIndex != index && (fromUI || index > -1 && index < this.getRepetitionCount() && this._selectedIndex != -1)) {
         this._selectedIndex = index;
         this.onValueChanged(fromUI);
      }
   }

   @Override
   public synchronized int getSelectedIndex() {
      return this._selectedIndex;
   }

   @Override
   public int getLayout() {
      return -1;
   }

   @Override
   public int getRepetitionCount() {
      return super._value == null ? 0 : ((LongVector)super._value).size();
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
         if (this._selectedIndex != -1) {
            this._selectedIndex = -1;
         }
      } else {
         if (this._selectedIndex < 0) {
            Object dataMappedValue = this.getDataMappedValue();
            long mappedHandle = dataMappedValue == null ? -1 : (Long)dataMappedValue;
            if (mappedHandle != -1) {
               DataCollection dc = this.getScreen().getWiclet().getDataCollection((int)(mappedHandle >> 32));
               if (dc.getDef().hasKey()) {
                  int index = ((LongVector)super._value).indexOf(mappedHandle);
                  if (index != -1) {
                     this._selectedIndex = index;
                  }
               } else if (dc.contains(mappedHandle)) {
                  LongVector dataHandles = (LongVector)super._value;

                  for (int i = dataHandles.size() - 1; i >= 0; i--) {
                     long handle = dataHandles.elementAt(i);
                     if (handle != -1 && dc.contains(handle) && dc.equals(handle, mappedHandle)) {
                        this._selectedIndex = i;
                        break;
                     }
                  }
               }
            }
         }

         if (this._selectedIndex >= size) {
            this._selectedIndex = -1;
            return;
         }
      }
   }

   @Override
   protected void updateMappedValue() {
      super._mappedValue = this._selectedIndex == -1 ? null : new Long(((LongVector)super._value).elementAt(this._selectedIndex));
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
   protected void onValueChanged(boolean fromUI) {
      super._valueChanged = true;
      ((ScreenModelImpl)this.getScreen()).onModify();
   }

   @Override
   protected void reset() {
      super.reset();
      this._selectedIndex = -1;
      int size = this._children == null ? 0 : this._children.length;

      for (int i = 0; i < size; i++) {
         ((UIComponentImpl)this._children[i]).reset();
      }
   }

   @Override
   public synchronized boolean isSelected(int index) {
      return this._selectedIndex == index;
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
   public synchronized void addSelectedIndex(int index, boolean fromUI) {
   }

   @Override
   public synchronized void removeSelectedIndex(int index, boolean fromUI) {
   }

   @Override
   public synchronized void setSelectedIndices(int[] selected, boolean fromUI) {
   }

   @Override
   public int getVisibleRows() {
      return this._visibleRows;
   }
}
