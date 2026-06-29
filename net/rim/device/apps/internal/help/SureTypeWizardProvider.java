package net.rim.device.apps.internal.help;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.system.Application;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FocusChangeListener;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.api.setupwizard.SetupWizardOrdering;
import net.rim.device.apps.api.setupwizard.WizardDialog;
import net.rim.device.cldc.io.utility.URIDecoder;
import net.rim.tid.awt.im.InputContext;

final class SureTypeWizardProvider extends HelpWizardProvider implements FocusChangeListener {
   ResourceBundle _resources = ResourceBundle.getBundle(-6124962742482799009L, "net.rim.device.apps.internal.resource.Help");

   protected final void validateTutorialEntry(String query) {
      String textfield = getQueryParam(query, "textfield=");
      String expected = getQueryParam(query, "expected=");
      if (textfield != null && expected != null) {
         textfield = URIDecoder.decode(textfield, "iso-8859-1", true).trim();
         expected = URIDecoder.decode(expected, "iso-8859-1", true).trim();
         if (!StringUtilities.strEqualIgnoreCase(textfield, expected)) {
            WizardDialog.inform(this._resources.getString(15), super._warnOnCloseOrHotKeys);
         } else {
            WizardDialog.inform(this._resources.getString(14), super._warnOnCloseOrHotKeys);
            this.doNext();
         }
      } else {
         this.doNext();
      }
   }

   @Override
   public final void focusChanged(Field field, int event) {
      if (event == 1) {
         this.scroll(2);
      }
   }

   private static final String getQueryParam(String query, String param) {
      String value = null;
      int pos = query.indexOf(param);
      if (pos >= 0) {
         value = query.substring(pos + param.length());
         pos = value.indexOf(38);
         if (pos >= 0) {
            value = value.substring(0, pos);
         }
      }

      return value;
   }

   @Override
   protected final void topicFinishedLoading() {
      synchronized (Application.getEventLock()) {
         Field editField = this.findEditField(this);
         if (editField != null) {
            editField.setFocusListener(this);
         }
      }
   }

   @Override
   protected final void launchUrl(String url) {
      String qualifier = "device:wizardNext";
      if (StringUtilities.startsWithIgnoreCase(url, qualifier)) {
         this.validateTutorialEntry(url.substring(qualifier.length()));
      } else {
         super.launchUrl(url);
      }
   }

   private final Field findEditField(Manager startingManager) {
      for (int i = 0; i <= startingManager.getFieldCount() - 1; i++) {
         Field field = startingManager.getField(i);
         if (field instanceof Object) {
            return field;
         }

         if (field instanceof Object) {
            field = this.findEditField((Manager)field);
            if (field != null) {
               return field;
            }
         }
      }

      return null;
   }

   public SureTypeWizardProvider() {
      super(
         ResourceBundle.getBundle(-6124962742482799009L, "net.rim.device.apps.internal.resource.Help"), 9, 400, SetupWizardOrdering.SURETYPE_TUTORIAL_CATEGORY
      );
      this.setTopics(HelpWizardProvider.WIZARD_SURETYPE_TOPICS);
   }

   @Override
   public final boolean isValid(Object context) {
      return super.isValid(context);
   }

   @Override
   public final boolean canSkipWizard() {
      switch (Keypad.getHardwareLayout()) {
         case 1364341300:
         case 1364346180:
            return false;
         default:
            return true;
      }
   }

   @Override
   public final boolean isHidden() {
      return !InputContext.getInstance().isSureType();
   }
}
