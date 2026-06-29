package net.rim.device.api.ui.component;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.XYEdges;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.accessibility.AccessibleContext;
import net.rim.device.api.ui.accessibility.AccessibleText;
import net.rim.device.api.ui.accessibility.AccessibleValue;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.tid.awt.im.InputContext;

public class ComboField extends HorizontalFieldManager implements FieldChangeListener {
   private BasicEditField _editable;
   private ListField _list;
   private ComboFieldController _control;
   private ComboField$DropListScreen _dropList;
   private String _inputText;
   private static final XYEdges DROP_MARGIN = new XYEdges(0, 10, 0, 10);

   public ComboFieldController getController() {
      return this._control;
   }

   public BasicEditField getEditable() {
      return this._editable;
   }

   public ListField getList() {
      return this._list;
   }

   public String getText() {
      return this._inputText;
   }

   public void hideDropList() {
      if (this._dropList != null && this._dropList.isDisplayed()) {
         Ui.getUiEngine().popScreen(this._dropList);
      }
   }

   protected XYEdges getDropMargin() {
      return DROP_MARGIN;
   }

   public void setController(ComboFieldController control) {
      this._control = control;
      this._control.setComboField(this);
   }

   public void setEditable(BasicEditField editable) {
      if (this._editable != null) {
         this.replace(this._editable, editable);
      } else {
         this.add(editable);
      }

      this._editable = editable;
      this._editable.setChangeListener(this);
      this._inputText = this._editable.getText();
   }

   public void setList(ListField list) {
      this.hideDropList();
      this._list = list;
      this._dropList = new ComboField$DropListScreen(this);
   }

   public void setText(String text) {
      this._editable.setText(text);
      this._editable.setMuddy(true);
   }

   public void showDropList() {
      XYRect rect = new XYRect(this._editable.getExtent());
      this.transformToScreen(rect);
      XYEdges dropMargin = this.getDropMargin();
      this._dropList.setPositionAndWidth(rect.x + dropMargin.left, rect.Y2(), rect.width - (dropMargin.left + dropMargin.right));
      if (this._dropList.isDisplayed()) {
         this._dropList.update();
      } else {
         Ui.getUiEngine().pushScreen(this._dropList);
      }
   }

   protected boolean usingSureType() {
      return InputContext.getInstance(false).isSureType();
   }

   @Override
   public void fieldChanged(Field field, int context) {
      if (field != null && field.getOriginal() == this._editable && this._control != null) {
         String newText = this._editable.getText();
         if (!newText.equals(this._inputText)) {
            this._inputText = newText;
            this._control.textChanged(this._inputText, context);
         }
      }
   }

   @Override
   protected void onUndisplay() {
      super.onUndisplay();
      this.hideDropList();
   }

   public ComboField() {
      this.$initialize(null, null, null);
   }

   public ComboField(BasicEditField editable, ListField list, ComboFieldController control) {
      this.$initialize(editable, list, control);
   }

   private void $initialize(BasicEditField editable, ListField list, ComboFieldController control) {
      this._editable = editable;
      this._list = list;
      this._control = control;
      if (this._list != null) {
         this._dropList = new ComboField$DropListScreen(this);
      }

      if (this._control != null) {
         this._control.setComboField(this);
      }

      if (this._editable != null) {
         this.add(this._editable);
         this._editable.setChangeListener(this);
      }
   }

   @Override
   public String getAccessibleName() {
      return null;
   }

   @Override
   public String getAccessibleDescription() {
      return null;
   }

   @Override
   public AccessibleText getAccessibleText() {
      return this._editable instanceof AccessibleText ? this._editable : null;
   }

   @Override
   public AccessibleValue getAccessibleValue() {
      return null;
   }

   @Override
   public int getAccessibleStateSet() {
      return super.getAccessibleStateSet();
   }

   @Override
   public boolean isAccessibleStateSet(int state) {
      return (super.getAccessibleStateSet() & state) != 0;
   }

   @Override
   public int getAccessibleRole() {
      return 12;
   }

   @Override
   public AccessibleContext getAccessibleParent() {
      return this.getScreen();
   }

   @Override
   public int getAccessibleChildCount() {
      return 2;
   }

   @Override
   public AccessibleContext getAccessibleChildAt(int index) {
      switch (index) {
         case -1:
            return null;
         case 0:
         default:
            return this._editable;
         case 1:
            return this._list;
      }
   }

   @Override
   public String getAccessibleIconDescription() {
      return null;
   }

   @Override
   public int getAccessibleSelectionCount() {
      return 1;
   }

   @Override
   public AccessibleContext getAccessibleSelectionAt(int index) {
      if (index == 0) {
         Field temp = super.getFieldWithFocus();
         if (temp instanceof AccessibleContext && (temp == this._editable || temp == this._list)) {
            return temp;
         }
      }

      return null;
   }

   @Override
   public boolean isAccessibleChildSelected(int index) {
      AccessibleContext _child = this.getAccessibleChildAt(index);
      AccessibleContext _selected = this.getAccessibleSelectionAt(0);
      return _child.equals(_selected);
   }
}
