package net.rim.device.apps.internal.browser.push;

import net.rim.device.api.browser.push.PushOptions;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.component.ObjectChoiceField;
import net.rim.device.apps.api.framework.verb.Verb;

final class PushOptionsItem$RestoreDefaultsVerb extends Verb {
   private final PushOptionsItem this$0;

   public PushOptionsItem$RestoreDefaultsVerb(PushOptionsItem _1) {
      super(16987200, -918730770534202080L, "net.rim.device.apps.internal.resource.BrowserPush", 27);
      this.this$0 = _1;
   }

   @Override
   public final Object invoke(Object context) {
      PushOptions options = PushOptions.getOptions();
      options.restoreDefaults();
      options.commit();
      this.this$0._enablePushField.setChecked(options.getEnablePush());
      this.this$0._mdsEnablePushField.setChecked(options.getMDSEnablePush());
      this.this$0._wapEnablePushField.setChecked(options.getWAPEnablePush());
      this.this$0._allowOtherApplicationsField.setChecked(options.getAllowOtherApplications());
      this.this$0._acceptSLMdsChoiceField.setSelectedIndex(options.getAcceptMode(0, 2));
      int filterMode = options.getFilterMode(0, 2);
      if (filterMode == 2) {
         filterMode--;
      }

      this.this$0._filterSLMdsChoiceField.setSelectedIndex(filterMode);
      this.this$0._acceptSLSmsChoiceField.setSelectedIndex(options.getAcceptMode(0, 1));
      this.resetValue(options.getFilterMode(0, 1), options.getFilterValue(0, 1), this.this$0._filterSLSmsChoiceField, this.this$0._filterSLSmsValueField);
      this.this$0._acceptSLIpChoiceField.setSelectedIndex(options.getAcceptMode(0, 0));
      this.resetValue(options.getFilterMode(0, 0), options.getFilterValue(0, 0), this.this$0._filterSLIpChoiceField, this.this$0._filterSLIpValueField);
      this.this$0._acceptSIMdsChoiceField.setSelectedIndex(options.getAcceptMode(1, 2));
      filterMode = options.getFilterMode(1, 2);
      if (filterMode == 2) {
         filterMode--;
      }

      this.this$0._filterSIMdsChoiceField.setSelectedIndex(filterMode);
      this.this$0._acceptSISmsChoiceField.setSelectedIndex(options.getAcceptMode(1, 1));
      this.resetValue(options.getFilterMode(1, 1), options.getFilterValue(1, 1), this.this$0._filterSISmsChoiceField, this.this$0._filterSISmsValueField);
      this.this$0._acceptSIIpChoiceField.setSelectedIndex(options.getAcceptMode(1, 0));
      this.resetValue(options.getFilterMode(1, 0), options.getFilterValue(1, 0), this.this$0._filterSIIpChoiceField, this.this$0._filterSIIpValueField);
      this.this$0._acceptOtherMdsChoiceField.setSelectedIndex(options.getAcceptMode(2, 2));
      filterMode = options.getFilterMode(2, 2);
      if (filterMode == 2) {
         filterMode--;
      }

      this.this$0._filterOtherMdsChoiceField.setSelectedIndex(filterMode);
      this.this$0._acceptOtherSmsChoiceField.setSelectedIndex(options.getAcceptMode(2, 1));
      this.resetValue(options.getFilterMode(2, 1), options.getFilterValue(2, 1), this.this$0._filterOtherSmsChoiceField, this.this$0._filterOtherSmsValueField);
      this.this$0._acceptOtherIpChoiceField.setSelectedIndex(options.getAcceptMode(2, 0));
      this.resetValue(options.getFilterMode(2, 0), options.getFilterValue(2, 0), this.this$0._filterOtherIpChoiceField, this.this$0._filterOtherIpValueField);
      this.this$0._mainScreen.setDirty(false);
      this.this$0.deleteFields();
      if (this.this$0._enablePushField.getChecked()) {
         this.this$0.addFields();
      }

      return null;
   }

   private final void resetValue(int index, String value, ObjectChoiceField choiceField, EditField valueField) {
      choiceField.setSelectedIndex(index);
      valueField.setText(value);
   }
}
