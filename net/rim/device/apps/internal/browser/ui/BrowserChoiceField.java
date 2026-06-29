package net.rim.device.apps.internal.browser.ui;

import net.rim.device.api.ui.component.ChoiceField;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.internal.browser.options.BrowserConfigRecord;

public final class BrowserChoiceField extends ChoiceField {
   private BrowserConfigRecord[] _browserConfigs;
   private String[] _browserNames;

   public BrowserChoiceField() {
      this(null, null);
   }

   public BrowserChoiceField(String label) {
      this(label, null);
   }

   public BrowserChoiceField(String label, String initialBrowserUID) {
      super(label, 0, 0);
      int initialIndex = -1;
      this._browserConfigs = BrowserConfigRecord.getValidBrowserConfigRecords(true);

      for (int i = 0; i < this._browserConfigs.length; i++) {
         if (StringUtilities.strEqualIgnoreCase(initialBrowserUID, this._browserConfigs[i].getUid(), 1701707776)) {
            initialIndex = i;
            break;
         }
      }

      this._browserNames = new String[this._browserConfigs.length];
      this.setSize(this._browserConfigs.length);
      if (initialIndex != -1) {
         this.setSelectedIndex(initialIndex);
      }
   }

   public final BrowserConfigRecord getSelectedBrowser() {
      return this._browserConfigs.length == 0 ? null : this._browserConfigs[this.getSelectedIndex()];
   }

   public final void setSelectedBrowser(String browserUID) {
      for (int i = 0; i < this._browserConfigs.length; i++) {
         if (StringUtilities.strEqualIgnoreCase(browserUID, this._browserConfigs[i].getUid(), 1701707776)) {
            this.setSelectedIndex(i);
            return;
         }
      }
   }

   @Override
   public final Object getChoice(int index) {
      if (this._browserConfigs.length == 0 && index == 0) {
         return null;
      }

      if (index >= 0 && this._browserConfigs.length > index) {
         if (this._browserNames[index] == null) {
            this._browserNames[index] = this._browserConfigs[index].getLocalizedString(11);
         }

         return this._browserNames[index];
      } else {
         throw new IllegalArgumentException();
      }
   }
}
