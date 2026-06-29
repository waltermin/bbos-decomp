package net.rim.device.apps.internal.browser.options;

import net.rim.device.apps.api.framework.verb.Verb;

final class BrowserConfigProperty$RestoreDefaultsVerb extends Verb {
   private BrowserConfigRecord _config;
   private final BrowserConfigProperty this$0;

   public BrowserConfigProperty$RestoreDefaultsVerb(BrowserConfigProperty _1, BrowserConfigRecord config) {
      super(16987200, -229261654107783483L, "net.rim.device.apps.internal.resource.Browser", 526);
      this.this$0 = _1;
      this._config = config;
   }

   @Override
   public final Object invoke(Object context) {
      this.this$0._homePageUrlField.setText(this._config.getPropertyAsString(1));
      this.this$0._showImagesField.setSelectedIndex(this._config.getPropertyAsInt(18));
      this.this$0._showImagePlaceholdersField.setSelectedIndex(this._config.getPropertyAsInt(19));
      this.this$0.showOrHideOptionalFields(this.this$0._showImagesField);
      this.this$0._defaultContentModeField.setSelectedIndex(this._config.getPropertyAsInt(10));
      this.this$0._defaultEmulationModeField.setSelectedIndex(this._config.getPropertyAsInt(17) - 1);
      this.this$0._javascriptEnabledField.setChecked(this._config.getPropertyAsBoolean(38));
      this.this$0.showOrHideOptionalFields(this.this$0._javascriptEnabledField);
      this.this$0._allowPopupsEnabledField.setChecked(this._config.getPropertyAsBoolean(39));
      this.this$0._foregroundBackgroundColorField.setChecked(this._config.getPropertyAsBoolean(35));
      this.this$0._backgroundImagesField.setChecked(this._config.getPropertyAsBoolean(40));
      this.this$0._useHtmlTablesField.setChecked(this._config.getPropertyAsBoolean(36));
      this.this$0._enableCssField.setChecked(this._config.getPropertyAsBoolean(33));
      this.this$0.showOrHideOptionalFields(this.this$0._enableCssField);
      this.this$0._enableEmbeddedRichContentField.setChecked(this._config.getPropertyAsBoolean(37));
      this.this$0._enableBSMField.setChecked(this._config.getPropertyAsBoolean(43));
      this.this$0._configValuesEditableField.setSelectedIndex(this._config.getPropertyAsInt(5));
      this.this$0._overriddenSet.reset();
      this.this$0._homePageUrlField.setDirty(true);
      return null;
   }
}
