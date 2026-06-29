package net.rim.device.apps.api.setupwizard;

import java.util.Vector;
import net.rim.device.api.system.ApplicationRegistry;

public class SetupWizardRegistration {
   private static Vector _wizardProviders;
   private static final long KEY_NAME;

   public static void registerWizardProvider(WizardPageProvider wizardProvider) {
      synchronized (_wizardProviders) {
         if (!_wizardProviders.contains(wizardProvider)) {
            _wizardProviders.addElement(wizardProvider);
         }
      }
   }

   public static void deRegisterWizardsProvider(WizardPageProvider wizardProvider) {
      synchronized (_wizardProviders) {
         if (_wizardProviders.contains(wizardProvider)) {
            _wizardProviders.removeElement(wizardProvider);
         }
      }
   }

   public static Vector getWizardPages() {
      Vector allWizardPages = (Vector)(new Object());
      synchronized (_wizardProviders) {
         int numProviders = _wizardProviders.size();

         for (int i = 0; i < numProviders; i++) {
            WizardPageProvider provider = (WizardPageProvider)_wizardProviders.elementAt(i);
            Vector wizardPages = provider.getWizardPages();
            int numPages = wizardPages.size();

            for (int j = 0; j < numPages; j++) {
               allWizardPages.addElement(wizardPages.elementAt(j));
            }
         }

         return allWizardPages;
      }
   }

   public static Vector extractWizardCategories(Vector wizardPages) {
      Vector categories = (Vector)(new Object());
      int numProviders = _wizardProviders.size();

      for (int i = 0; i < numProviders; i++) {
         WizardPage page = (WizardPage)wizardPages.elementAt(i);
         WizardCategory category = page.getCategory();
         if (category != null && !categories.contains(category)) {
            categories.addElement(category);
         }
      }

      return categories;
   }

   static {
      ApplicationRegistry registry = ApplicationRegistry.getApplicationRegistry();
      _wizardProviders = (Vector)registry.get(-6881666848190429556L);
      if (_wizardProviders == null) {
         synchronized (registry) {
            _wizardProviders = (Vector)registry.get(-6881666848190429556L);
            if (_wizardProviders == null) {
               _wizardProviders = (Vector)(new Object());
               registry.put(-6881666848190429556L, _wizardProviders);
            }
         }
      }
   }
}
