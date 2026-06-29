package net.rim.device.api.lbs;

import net.rim.device.api.ui.component.CheckboxField;

class LBSLoggingConfigScreen$FlagSettingCheckboxField extends CheckboxField {
   int _flag;
   LoggingFlags _logFlags;
   private final LBSLoggingConfigScreen this$0;

   LBSLoggingConfigScreen$FlagSettingCheckboxField(LBSLoggingConfigScreen this$0, String label, LoggingFlags logFlags, int flag) {
      super(label, logFlags.getFlag(flag));
      this.this$0 = this$0;
      this._flag = -1;
      this._flag = flag;
      this._logFlags = logFlags;
   }

   @Override
   protected void fieldChangeNotify(int context) {
      super.fieldChangeNotify(context);
      if (this._flag != -1) {
         synchronized (this._logFlags) {
            if (this.getChecked()) {
               this._logFlags.setFlag(this._flag);
            } else {
               this._logFlags.clearFlag(this._flag);
            }
         }
      }
   }
}
