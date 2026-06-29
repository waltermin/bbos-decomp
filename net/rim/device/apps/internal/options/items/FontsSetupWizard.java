package net.rim.device.apps.internal.options.items;

import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.NullField;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.setupwizard.BasicWizardPage;
import net.rim.device.apps.api.setupwizard.SetupWizardOrdering;
import net.rim.device.apps.api.ui.AppsMainScreen;
import net.rim.device.apps.internal.options.resources.OptionsResources;

public final class FontsSetupWizard extends BasicWizardPage {
   private ScreenKeyboardController _controller;

   public FontsSetupWizard() {
      super(OptionsResources.getResourceBundle(), 2112, 300, SetupWizardOrdering.FONT_CATEGORY, 0, 3458764513820540928L);
   }

   @Override
   protected final void initialize() {
      this._controller = new ScreenKeyboardController(true);
      this._controller.initialize();
   }

   @Override
   protected final void populateContent(AppsMainScreen screen, Manager content) {
      content.add(new NullField(18014398509481984L));
      Manager header = new VerticalFieldManager();
      header.setFont(this.getHeaderFont());
      header.add(new LabelField(OptionsResources.getString(2113)));
      content.add(header);
      content.add(new SeparatorField());
      this._controller.populateMainScreen(screen, content);
   }

   @Override
   protected final boolean saveWizard(Verb sender) {
      return this._controller.save();
   }

   @Override
   protected final void discardWizard() {
      this._controller.discard();
   }
}
