package net.rim.device.apps.internal.browser.download;

import net.rim.device.api.i18n.ResourceBundleFamily;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.apps.internal.browser.resources.BrowserResources;

public class DescriptorField extends VerticalFieldManager {
   private Font _labelFont;
   private static final int LONG_VALUE_LEFT_MARGIN = 40;

   public DescriptorField(long style) {
      super(style);
   }

   protected void addNameValueField(ResourceBundleFamily nameResourceBundle, int nameResourceKey, String value) {
      HorizontalFieldManager hfm = (HorizontalFieldManager)(new Object(1152921504606846976L));
      LabelField nameField = (LabelField)(new Object(nameResourceBundle, nameResourceKey));
      RichTextField valueField = (RichTextField)(new Object(value, 524288));
      if (this._labelFont == null) {
         this.setLabelFont(nameField, valueField);
      }

      nameField.setFont(this._labelFont);
      hfm.add(nameField);
      hfm.add(valueField);
      this.add(hfm);
   }

   protected void addDescriptionField(String description) {
      this.addLongNameValueField(BrowserResources.getResourceBundle(), 514, description);
   }

   protected void addLongNameValueField(ResourceBundleFamily nameResourceBundle, int nameResourceKey, String value) {
      EditField valueField = (EditField)(new Object(9007199254740992L));
      valueField.setText(value);
      LabelField labelField = (LabelField)(new Object(nameResourceBundle, nameResourceKey));
      if (this._labelFont == null) {
         this.setLabelFont(labelField, valueField);
      }

      labelField.setFont(this._labelFont);
      this.add(labelField);
      HorizontalFieldManager hfm = (HorizontalFieldManager)(new Object(1152921504606846976L));
      hfm.add((Field)(new Object(40)));
      hfm.add(valueField);
      this.add(hfm);
   }

   private void setLabelFont(Field nameField, Field valueField) {
      Font nameFont = nameField.getFont();
      int nameFontStyle = nameFont.getStyle();
      int valueFontStyle = valueField.getFont().getStyle();
      if ((valueFontStyle & 1) == 0 && (valueFontStyle & 64) == 0) {
         nameFontStyle |= 1;
      } else if ((valueFontStyle & 4) == 0) {
         nameFontStyle |= 4;
      } else if ((valueFontStyle & 2) == 0) {
         nameFontStyle |= 2;
      } else {
         nameFontStyle |= 64;
      }

      this._labelFont = nameFont.derive(nameFontStyle);
   }
}
