package net.rim.device.apps.api.messaging.messagelist;

import net.rim.device.api.collection.ReadableList;
import net.rim.device.api.ui.AccessibleEventDispatcher;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.accessibility.AccessibleContext;
import net.rim.device.api.ui.accessibility.AccessibleContextFactory;
import net.rim.device.api.ui.accessibility.AccessibleContextProxy;
import net.rim.device.api.ui.accessibility.AccessibleText;
import net.rim.device.api.ui.accessibility.AccessibleValue;
import net.rim.device.api.ui.component.VariableHeightListField;

final class SelectedIndexManager implements AccessibleContext {
   private int _selectedIndex;
   private Object _selectedObject;
   private Object _lastRemovedObject;
   private VariableHeightListField _listField;
   private ReadableList _readableList;
   private int _accessibleStateSet = 1;

   public final void setParameters(VariableHeightListField listField, ReadableList readableList) {
      this._listField = listField;
      this._readableList = readableList;
      this.setSelectedIndex(0);
   }

   public final void setSelectedIndex(int index) {
      this.setSelectedIndex(index, false);
   }

   public final void setSelectedIndex(int index, boolean scrollToTop) {
      int listFieldSize = this._listField.getSize();
      if (listFieldSize > 0) {
         this._lastRemovedObject = null;
         if (index >= listFieldSize) {
            index = listFieldSize - 1;
         }

         this._selectedIndex = index;

         label28:
         try {
            this._selectedObject = this._readableList.getAt(this._selectedIndex);
         } finally {
            break label28;
         }

         this._listField.setSelectedIndex(this._selectedIndex, scrollToTop);
         this._listField.setFocus();
      }
   }

   public final int getSelectedIndex() {
      return this._selectedIndex;
   }

   public final Object getSelectedObject() {
      return this._selectedObject;
   }

   public final void focusChanged(Field field, int action) {
      this._selectedIndex = ((VariableHeightListField)field).getSelectedIndex();
      this._lastRemovedObject = null;

      label26:
      try {
         this._selectedObject = this._readableList.getAt(this._selectedIndex);
      } finally {
         break label26;
      }

      if (Ui.isTTSEnabled()) {
         this.addAccessibleState(4);
         this.accessibleEventOccurred(6, new Integer(1), new Integer(2), this);
      }
   }

   public final void updateSelectedIndex() {
      ReadableList list = this._readableList;
      int size = list.size();
      if (size > 0) {
         try {
            int index = this._selectedIndex;
            if (index < 0) {
               index = 0;
            } else if (index >= size) {
               index = size - 1;
            }

            boolean found = true;
            if (this._lastRemovedObject != null && list.getAt(index) == this._lastRemovedObject) {
               this._selectedObject = this._lastRemovedObject;
               this._lastRemovedObject = null;
            } else {
               Object obj = this._selectedObject;
               if (obj == null) {
                  found = false;
               } else if (list.getAt(index) != obj) {
                  if (index + 1 < size && this.check(obj, list.getAt(index + 1))) {
                     index++;
                  } else if (index + 2 < size && this.check(obj, list.getAt(index + 2))) {
                     index += 2;
                  } else if (index > 0 && this.check(obj, list.getAt(index - 1))) {
                     index--;
                  } else if (index > 1 && this.check(obj, list.getAt(index - 2))) {
                     index -= 2;
                  } else {
                     found = false;
                  }
               }
            }

            if (!found) {
               int[] selection = this._listField.getSelection();
               if (selection != null && selection.length > 1 && selection[0] >= 0 && selection[0] < size) {
                  index = selection[0];
               }

               this._selectedObject = list.getAt(index);
            }

            this._selectedIndex = index;
         } finally {
            return;
         }
      }
   }

   public final void elementRemoved(Object element) {
      if (element == this._selectedObject) {
         this._lastRemovedObject = element;
      } else {
         this._lastRemovedObject = null;
      }
   }

   protected final void accessibleEventOccurred(int event, Object oldValue, Object newValue, AccessibleContext context) {
      AccessibleEventDispatcher.dispatchAccessibleEvent(event, oldValue, newValue, context);
   }

   protected final void removeAccessibleState(int state) {
      this._accessibleStateSet &= ~state;
   }

   protected final void addAccessibleState(int state) {
      if (this.isAccessibleStateSet(1)) {
         this.removeAccessibleState(1);
      }

      this._accessibleStateSet |= state;
   }

   @Override
   public final AccessibleText getAccessibleText() {
      return null;
   }

   @Override
   public final AccessibleValue getAccessibleValue() {
      return null;
   }

   @Override
   public final int getAccessibleStateSet() {
      return this._accessibleStateSet;
   }

   @Override
   public final String getAccessibleDescription() {
      return null;
   }

   @Override
   public final String getAccessibleName() {
      return this.toString();
   }

   @Override
   public final boolean isAccessibleStateSet(int state) {
      return (this._accessibleStateSet & state) != 0;
   }

   @Override
   public final int getAccessibleRole() {
      return 25;
   }

   @Override
   public final AccessibleContext getAccessibleParent() {
      return null;
   }

   @Override
   public final int getAccessibleChildCount() {
      return this._listField.getSize();
   }

   @Override
   public final AccessibleContext getAccessibleChildAt(int index) {
      return this._listField.getAccessibleChildAt(index);
   }

   @Override
   public final String getAccessibleIconDescription() {
      return null;
   }

   @Override
   public final int getAccessibleSelectionCount() {
      return 1;
   }

   @Override
   public final AccessibleContext getAccessibleSelectionAt(int index) {
      if (index != 0 || this._selectedObject == null) {
         return null;
      } else if (this._selectedObject instanceof AccessibleContext) {
         return (AccessibleContext)this._selectedObject;
      } else {
         return !(this._selectedObject instanceof AccessibleContextProxy)
            ? new AccessibleContextFactory(this._readableList.getAt(this._selectedIndex).toString(), 0, 4)
            : ((AccessibleContextProxy)this._selectedObject).getAccessibleContext();
      }
   }

   @Override
   public final boolean isAccessibleChildSelected(int index) {
      return this._listField.isAccessibleChildSelected(index);
   }

   private final boolean check(Object o1, Object o2) {
      if (o1 instanceof DateSeparator) {
         return o2 instanceof DateSeparator ? ((DateSeparator)o1).getDate() == ((DateSeparator)o2).getDate() : false;
      } else {
         return o1 == o2;
      }
   }
}
