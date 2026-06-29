package net.rim.device.apps.internal.blackberryemail.email.filters;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.component.CheckboxField;
import net.rim.device.api.ui.component.ObjectChoiceField;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.apps.internal.blackberryemail.resources.EmailResources;

class EmailFilterBodyModelImpl$EmailForwardingChoiceField extends ObjectChoiceField implements FieldChangeListener {
   VerticalFieldManager _vfm;
   Object[] _forwardChoices;
   CheckboxField _forward;
   CheckboxField _forwardHeader;
   ObjectChoiceField _fcf;
   SeparatorField _sep;
   private final EmailFilterBodyModelImpl this$0;

   EmailFilterBodyModelImpl$EmailForwardingChoiceField(EmailFilterBodyModelImpl _1, VerticalFieldManager vfm) {
      this.this$0 = _1;
      this._vfm = vfm;
      this.init();
   }

   private void init() {
      this._forwardChoices = new Object[]{EmailResources.getString(178), CommonResources.getString(9149)};
      this._forward = new CheckboxField(EmailResources.getString(179), this.this$0.forwardFlagsSet(2));
      this._forwardHeader = new CheckboxField(EmailResources.getString(180), this.this$0.forwardFlagsSet(4));
      this._sep = new SeparatorField();
      if (this.this$0._action == 0) {
         this._fcf = new ObjectChoiceField(EmailResources.getString(181), this._forwardChoices, 0);
         this._vfm.add(this._fcf);
      } else {
         this._fcf = new ObjectChoiceField(EmailResources.getString(181), this._forwardChoices, this.this$0.forwardFlagsSet(1) ? 1 : 0);
         this._vfm.add(this._fcf);
         if (!this.this$0.forwardFlagsSet(0)) {
            this._vfm.add(new SeparatorField());
            this._vfm.add(this._forward);
            this._vfm.add(this._forwardHeader);
         }
      }

      this._fcf.setChangeListener(this);
      this._vfm.add(new SeparatorField());
      this._vfm.add(this._sep);
   }

   @Override
   public void fieldChanged(Field field, int context) {
      if (field instanceof ObjectChoiceField) {
         ObjectChoiceField ocf = (ObjectChoiceField)field;
         this._vfm.delete(this._sep);
         if (ocf.getSelectedIndex() == 0) {
            this._vfm.delete(this._forward);
            this._vfm.delete(this._forwardHeader);
         } else {
            this._vfm.add(this._forward);
            this._vfm.add(this._forwardHeader);
         }

         this._vfm.add(this._sep);
         this._sep.getScreen().ensureRegionVisible(this._sep, 0, 0, 0, 0);
      }
   }
}
