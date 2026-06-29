package net.rim.device.apps.internal.options.items;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.NullField;
import net.rim.device.api.ui.component.RadioButtonField;
import net.rim.device.api.ui.component.RadioButtonGroup;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.setupwizard.BasicWizardPage;
import net.rim.device.apps.api.setupwizard.SetupWizardOrdering;
import net.rim.device.apps.api.ui.ApplicationControlScreen;
import net.rim.device.apps.api.ui.AppsMainScreen;
import net.rim.device.apps.internal.options.resources.OptionsResources;
import net.rim.device.internal.applicationcontrol.ApplicationControl;
import net.rim.device.internal.ui.container.VerticalIndentFieldManager;

public final class ApplicationControlSetupWizard extends BasicWizardPage {
   private ApplicationRegistry _ar = ApplicationRegistry.getApplicationRegistry();
   private RadioButtonGroup _settingRadios;
   private static final long ID = 8230353045335363579L;
   private static final int INDENT = 20;
   private static final int RESTRICTIVE = 0;
   private static final int DEFAULT = 1;
   private static final int PERMISSIVE = 2;
   private static final int CUSTOM = 3;
   private static final int NUM_RADIOS = 4;

   public ApplicationControlSetupWizard() {
      super(OptionsResources.getResourceBundle(), 2061, 1110, SetupWizardOrdering.TIPS_AND_TRICKS_CATEGORY, 2);
   }

   @Override
   protected final void populateContent(AppsMainScreen screen, Manager content) {
      this._settingRadios = new RadioButtonGroup();
      RadioButtonField[] radios = new RadioButtonField[]{
         new RadioButtonField(OptionsResources.getString(2063), this._settingRadios, false),
         new RadioButtonField(OptionsResources.getString(2056), this._settingRadios, false),
         new RadioButtonField(OptionsResources.getString(2057), this._settingRadios, false),
         new RadioButtonField(OptionsResources.getString(2060), this._settingRadios, false)
      };
      content.add(new NullField(18014398509481984L));
      content.setFont(this.getHeaderFont());
      Font italicFont = this.getHeaderFont().derive(2);
      content.add(new LabelField(OptionsResources.getString(2062)));
      content.add(new SeparatorField());
      content.add(radios[0]);
      content.add(radios[1]);
      content.add(radios[2]);
      VerticalIndentFieldManager customLine = new VerticalIndentFieldManager();
      customLine.add(radios[3]);
      LabelField note = new LabelField(OptionsResources.getString(2064));
      note.setFont(italicFont);
      customLine.add(note, 20);
      content.add(customLine);
      LabelField trailer1 = new LabelField(OptionsResources.getString(2058));
      LabelField trailer2 = new LabelField(OptionsResources.getString(2059));
      trailer2.setFont(italicFont);
      content.add(trailer1);
      content.add(trailer2);
      Integer previousSelection = (Integer)this._ar.get(8230353045335363579L);
      int selectionIntVal = previousSelection != null ? previousSelection : 1;
      this._settingRadios.setSelectedIndex(selectionIntVal);
   }

   @Override
   protected final boolean saveWizard(Verb sender) {
      int selection = this._settingRadios.getSelectedIndex();
      boolean resetRequired = false;
      switch (selection) {
         case -1:
            break;
         case 0:
         default:
            resetRequired = ApplicationControl.setRestrictiveDefaultPermission();
            break;
         case 1:
            resetRequired = ApplicationControl.setDefaultPermission();
            break;
         case 2:
            resetRequired = ApplicationControl.setPermissiveDefaultPermission();
            break;
         case 3:
            UiApplication.getUiApplication().pushModalScreen(new ApplicationControlScreen());
      }

      if (resetRequired) {
         ApplicationControl.scheduleDeviceReset("USR-SW");
      }

      this._ar.replace(8230353045335363579L, new Integer(selection));
      return true;
   }
}
