package net.rim.device.apps.internal.phone;

import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.apps.api.ui.AppsMainScreen;
import net.rim.device.apps.api.ui.SystemEnabledMenu;
import net.rim.device.apps.internal.phone.api.PhoneUtilities;
import net.rim.device.apps.internal.phone.resource.PhoneResources;

public final class EditLineDescriptionScreen extends AppsMainScreen {
   EditField[] _phoneDescriptionField;
   int[] _lineIds;

   public EditLineDescriptionScreen() {
      super(0);
      this.setTitle(PhoneResources.getString(6322));
      this._lineIds = PhoneUtilities.getAllLineIds();
      this.populateScreen();
   }

   private final void populateScreen() {
      String[] numbers = PhoneUtilities.getAllLineNumbers();
      this._phoneDescriptionField = new Object[this._lineIds.length];

      for (int i = 0; i < this._lineIds.length; i++) {
         if (i != 0) {
            this.add((Field)(new Object()));
         }

         this.add((Field)(new Object(numbers[i])));
         this._phoneDescriptionField[i] = new EditLineDescriptionScreen$LineDescriptionField(PhoneUtilities.getLineDescription(this._lineIds[i]));
         this.add(this._phoneDescriptionField[i]);
      }
   }

   @Override
   protected final void makeMenu(SystemEnabledMenu menu, int instance) {
      super.makeMenu(menu, instance);
      if (this.isDirty()) {
         menu.add(new EditLineDescriptionScreen$SaveVerb(this));
      }
   }

   @Override
   public final boolean onSave() {
      for (int i = this._lineIds.length - 1; i >= 0; i--) {
         PhoneUtilities.setLineDescription(this._lineIds[i], this._phoneDescriptionField[i].getText(), RadioInfo.getNetworkType() != 4);
      }

      return true;
   }
}
