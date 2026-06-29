package net.rim.device.apps.api.setupwizard;

import net.rim.device.api.system.Application;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.component.RadioButtonField;
import net.rim.device.api.ui.component.RadioButtonGroup;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.ui.CommonResources;

public class YesNoField extends VerticalFieldManager {
   private RadioButtonGroup _radioGroup = (RadioButtonGroup)(new Object());
   private RadioButtonField _yesButton;
   private RadioButtonField _noButton;
   private Verb _activationVerb;

   public YesNoField() {
      this(CommonResources.getString(100), CommonResources.getString(101));
   }

   public YesNoField(String yesLabel, String noLabel) {
      super(12884901888L);
      this._yesButton = new MultiLineRadioButtonField(yesLabel, this._radioGroup, true, 2147483648L);
      this._noButton = new MultiLineRadioButtonField(noLabel, this._radioGroup, false, 2147483648L);
      this._yesButton.setPadding(0, 0, 5, 0);
      this._noButton.setPadding(0, 0, 5, 0);
      this.setBorder(20, 20, 0, 40);
      this.add(this._yesButton);
      this.add(this._noButton);
   }

   public void setYesLabel(String text) {
      this._yesButton.setLabel(text);
   }

   public void setNoLabel(String text) {
      this._noButton.setLabel(text);
   }

   public void setActivationVerb(Verb verb) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   public boolean isSelected() {
      return this._yesButton.isSelected();
   }

   public void setSelected(boolean selected) {
      this._radioGroup.setSelectedIndex(selected ? 0 : 1);
   }

   private void invokeButton(RadioButtonField button) {
      button.setFocus();
      button.setSelected(true);
      Application.getApplication().invokeLater(new YesNoField$1(this));
   }

   @Override
   public boolean keyDown(int keycode, int time) {
      if (keycode >> 16 == 10) {
         Field focus = this.getLeafFieldWithFocus();
         if (focus == this._yesButton || focus == this._noButton) {
            this.invokeButton((RadioButtonField)focus);
            return true;
         }
      } else {
         int k = SetupWizardHotkeys.map(keycode);
         switch (k) {
            case -1:
               break;
            case 0:
            default:
               this.invokeButton(this._yesButton);
               return true;
            case 1:
               this.invokeButton(this._noButton);
               return true;
         }
      }

      return super.keyDown(keycode, time);
   }

   private void activateVerb() {
      if (this._activationVerb != null) {
         this._activationVerb.invoke(this);
      }
   }
}
