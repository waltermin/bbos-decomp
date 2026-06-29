package net.rim.device.apps.api.setupwizard;

import net.rim.device.apps.api.setupwizard.categories.DevicePersonalizationWizardCategory;
import net.rim.device.apps.api.setupwizard.categories.DeviceSetupWizardCategory;

public class SetupWizardOrdering {
   public static final int INITIAL_PAGE = 50;
   public static final int SETUP = 100;
   public static final int PERSONALIZATION = 200;
   public static final int EMAIL_SETUP = 300;
   public static final int FINISHING_WIZARD = 1000;
   public static final int SHORTCUTS = 1100;
   public static final int APPLICATION_PERMISSIONS = 1110;
   public static final int TIPS_AND_TRICKS = 1200;
   public static final int LANGUAGE = 100;
   public static final int DATE_AND_TIME = 200;
   public static final int INTRO = 300;
   public static final int SURETYPE_TUTORIAL = 400;
   public static final int SIM_IMPORT = 500;
   public static final int OWNER_INFORMATION = 100;
   public static final int THEME = 200;
   public static final int FONT = 300;
   public static final int CONVENIENCE_KEYS = 400;
   public static final WizardCategory LANGUAGE_CATEGORY = DeviceSetupWizardCategory.getInstance();
   public static final WizardCategory DATE_AND_TIME_CATEGORY = DeviceSetupWizardCategory.getInstance();
   public static final WizardCategory INTRO_CATEGORY = DeviceSetupWizardCategory.getInstance();
   public static final WizardCategory SURETYPE_TUTORIAL_CATEGORY = DeviceSetupWizardCategory.getInstance();
   public static final WizardCategory SIM_IMPORT_CATEGORY = DeviceSetupWizardCategory.getInstance();
   public static final WizardCategory OWNER_INFORMATION_CATEGORY = DevicePersonalizationWizardCategory.getInstance();
   public static final WizardCategory FONT_CATEGORY = DevicePersonalizationWizardCategory.getInstance();
   public static final WizardCategory THEME_CATEGORY = DevicePersonalizationWizardCategory.getInstance();
   public static final WizardCategory CONVENIENCE_KEYS_CATEGORY = DevicePersonalizationWizardCategory.getInstance();
   public static final WizardCategory EMAIL_SETUP_CATEGORY = null;
   public static final WizardCategory TIPS_AND_TRICKS_CATEGORY = null;
   public static final WizardCategory SHORTCUTS_CATEGORY = null;
   public static final WizardCategory INITIAL_PAGE_CATEGORY = null;
   public static final WizardCategory FINISHING_WIZARD_CATEGORY = null;
}
