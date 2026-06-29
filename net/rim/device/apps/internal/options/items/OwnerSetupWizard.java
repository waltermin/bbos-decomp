package net.rim.device.apps.internal.options.items;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.setupwizard.BasicWizardPage;
import net.rim.device.apps.api.setupwizard.SetupWizardOrdering;
import net.rim.device.apps.api.ui.AppsMainScreen;
import net.rim.device.apps.internal.options.resources.OptionsResources;

public final class OwnerSetupWizard extends BasicWizardPage {
   private OwnerOptionsController _controller;

   public OwnerSetupWizard() {
      super(OptionsResources.getResourceBundle(), 2115, 100, SetupWizardOrdering.OWNER_INFORMATION_CATEGORY);
   }

   @Override
   protected final void initialize() {
      this._controller = new OwnerOptionsController(true);
   }

   @Override
   protected final void populateContent(AppsMainScreen screen, Manager content) {
      Manager header = (Manager)(new Object());
      header.setFont(this.getHeaderFont());
      LabelField label = (LabelField)(new Object(OptionsResources.getString(2105)));
      label.setBorder(0, 0, 5, 0);
      header.add(label);
      header.add((Field)(new Object(OptionsResources.getString(2106))));
      content.add(header);
      content.add((Field)(new Object()));
      this._controller.populateMainScreen(screen, content);
   }

   @Override
   protected final boolean saveWizard(Verb sender) {
      return this._controller.save();
   }

   @Override
   protected final void discardWizard() {
   }

   @Override
   protected final void beforeOpen() {
      this.reloadTitle();
      this._controller.beforeOpen();
   }

   @Override
   protected final void afterClose() {
      this._controller.beforeClose();
   }
}
