package net.rim.wica.runtime.ui.internal.component;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.component.CheckboxField;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.wica.runtime.metadata.component.ui.control.ChoiceControl;

final class WicletCheckboxListField extends ChoiceField implements FieldChangeListener {
   private boolean _isMandatorySatisfied;
   private WicletCheckboxListField$MandatoryLabel _mandatoryLabel;

   WicletCheckboxListField(ScreenContext context, ChoiceControl controller, int row, long style) {
      super(context, controller, style);
   }

   @Override
   public final void fieldChanged(Field field, int context) {
      throw new RuntimeException("cod2jar: invokevirtual: slot out of range");
   }

   @Override
   public final void update(int fromIndex, int count) {
      throw new RuntimeException("cod2jar: invokevirtual: slot out of range");
   }

   @Override
   public final void setSelected(int index) {
   }

   @Override
   protected final void addInternal(int fromIndex, int count) {
      ChoiceControl controller = (ChoiceControl)this.getModel();
      Manager manager = this;
      if (controller.isMandatory()) {
         HorizontalFieldManager hManager = (HorizontalFieldManager)(new Object());
         this._mandatoryLabel = new WicletCheckboxListField$MandatoryLabel(this, "*", 17179869184L);
         this._isMandatorySatisfied = this.isMandatorySatisfied(controller);
         manager = (Manager)(new Object());
         hManager.add(this._mandatoryLabel);
         hManager.add(manager);
         this.add(hManager);
      }

      this.addCheckBoxes(manager, fromIndex, count);
   }

   private final void addCheckBoxes(Manager manager, int fromIndex, int count) {
      ChoiceControl controller = (ChoiceControl)this.getModel();

      for (int i = fromIndex; i < fromIndex + count; i++) {
         CheckboxField check = new WicletCheckboxListField$WicletCheckboxField(this.getLabel(i), controller.isSelected(i));
         check.setEditable(this.isEditable());
         check.setChangeListener(this);
         manager.add(check);
      }
   }

   private final Manager getCheckboxesManager() {
      throw new RuntimeException("cod2jar: invokevirtual: slot out of range");
   }

   private final boolean isMandatorySatisfied(ChoiceControl controller) {
      int[] selected = controller.getSelectedIndices();
      return selected != null && selected.length > 0;
   }
}
