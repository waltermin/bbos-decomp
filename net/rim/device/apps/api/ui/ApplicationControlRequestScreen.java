package net.rim.device.apps.api.ui;

import net.rim.device.api.applicationcontrol.ApplicationPermissions;
import net.rim.device.api.ui.MenuItem;

public final class ApplicationControlRequestScreen extends ApplicationControlScreen {
   private ApplicationPermissions _permissions;

   public ApplicationControlRequestScreen(String name) {
      this.setTitle(name);
      this.expandAllNodes();
   }

   @Override
   public final void save() {
      this._permissions = this.getSelectedPermissions();
   }

   public final ApplicationPermissions getRequestedPermissions() {
      return this._permissions;
   }

   @Override
   protected final void makeMenu() {
      this.addMenuItem((MenuItem)super.SAVE_MI);
      this.setHelp("third_party_program_control");
   }

   @Override
   protected final void addModules() {
   }

   @Override
   protected final String[] getChoicesBinary(boolean state) {
      return state ? ApplicationControlScreen.ALLOW_DENY_A : ApplicationControlScreen.ALLOW_DENY_D;
   }

   @Override
   protected final boolean[] getBoldingBinary() {
      return ApplicationControlScreen.TWO_PLUS_ONE;
   }

   @Override
   protected final String[] getChoicesTernary(boolean allowState, boolean promptState, boolean allow) {
      if (allow) {
         if (allowState) {
            return promptState ? ApplicationControlScreen.ALLOW_PROMPT_DENY_P : ApplicationControlScreen.ALLOW_PROMPT_DENY_A;
         } else {
            return ApplicationControlScreen.ALLOW_PROMPT_DENY_D;
         }
      } else {
         return promptState ? ApplicationControlScreen.PROMPT_DENY_P : ApplicationControlScreen.PROMPT_DENY_D;
      }
   }

   @Override
   protected final boolean[] getBoldingTernary(boolean allow) {
      return allow ? ApplicationControlScreen.THREE_PLUS_ONE : ApplicationControlScreen.TWO_PLUS_ONE;
   }
}
