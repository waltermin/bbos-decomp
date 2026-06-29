package net.rim.device.apps.internal.mms.options;

import net.rim.device.api.ui.component.CheckboxField;
import net.rim.device.apps.internal.mms.resources.MMSResources;

class CheckboxOptionField extends CheckboxField implements MMSOptionsScreen$Saveable {
   private int _optionFlag;
   private boolean _invert;

   protected void saveFlag(int optionFlag, boolean value) {
      MMSOptions.getInstance().setOptionFlag(optionFlag, value);
   }

   @Override
   public void saveOption() {
      if (this.isDirty()) {
         this.saveFlag(this._optionFlag, this.getChecked() ^ this._invert);
      }
   }

   public CheckboxOptionField(String message, int flag, boolean invert) {
      this(message, MMSOptions.getInstance().getOptionFlag(flag), flag, invert);
   }

   public CheckboxOptionField(String message, boolean value, int flag, boolean invert) {
      super(message, value ^ invert);
      this._optionFlag = flag;
      this._invert = invert;
   }

   public CheckboxOptionField(int msgid, int flag, boolean invert) {
      this(MMSResources.getString(msgid), flag, invert);
   }

   public CheckboxOptionField(int msgid, int flag) {
      this(msgid, flag, false);
   }
}
