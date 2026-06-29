package net.rim.device.apps.internal.activation;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.setupwizard.BasicWizardPage;
import net.rim.device.apps.api.setupwizard.ScrollingVerticalFieldManager;
import net.rim.device.apps.api.setupwizard.WizardLayoutManager;
import net.rim.device.apps.api.setupwizard.YesNoField;
import net.rim.device.apps.api.ui.AppsMainScreen;

final class EmailSetupWizard$BesConfirmPage extends BasicWizardPage {
   private YesNoField _yesNoField;
   WizardLayoutManager _layoutManager;
   private final EmailSetupWizard this$0;

   public EmailSetupWizard$BesConfirmPage(EmailSetupWizard _1) {
      super(EmailSetupWizard._resources, 160, 20, null, 24);
      this.this$0 = _1;
   }

   @Override
   public final boolean canSkipWizardInternal() {
      return this.this$0._setupMode != 2;
   }

   @Override
   protected final void populateContent(AppsMainScreen mainScreen, Manager content) {
      content.add((Field)(new Object()));
      this._layoutManager = (WizardLayoutManager)(new Object());
      this._layoutManager.setScrollbarEnabled(true);
      VerticalFieldManager scrollingContent = (VerticalFieldManager)(new Object(281474976710656L));
      LabelField label = (LabelField)(new Object(EmailSetupWizard._resources.getString(168)));
      label.setBorder(0, 0, 5, 0);
      scrollingContent.add(label);
      this._yesNoField = (YesNoField)(new Object());
      this._yesNoField.setActivationVerb(new EmailSetupWizard$BesConfirmPage$1(this, 0));
      scrollingContent.add(this._yesNoField);
      this._layoutManager.setContent(scrollingContent);
      content.add(this._layoutManager);
   }

   @Override
   protected final boolean saveWizard(Verb sender) {
      if (this._yesNoField != null) {
         if (!this._yesNoField.isSelected()) {
            ScrollingVerticalFieldManager scrollingText = (ScrollingVerticalFieldManager)(new Object(0));
            scrollingText.add((Field)(new Object(EmailSetupWizard._resources.getString(169))));
            this._yesNoField = null;
            this._layoutManager.setContent(scrollingText);
            scrollingText.setFocus();
            return false;
         }

         ActivationApp.run(null);
         ActivationListener.register();
      }

      return true;
   }

   static final void access$300(EmailSetupWizard$BesConfirmPage x0) {
      x0.doNext();
   }
}
