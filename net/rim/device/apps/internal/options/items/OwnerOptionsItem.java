package net.rim.device.apps.internal.options.items;

import net.rim.device.api.itpolicy.ITPolicy;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.AutoTextEditField;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.apps.api.framework.registration.VerbRepository;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.options.SaveableMainScreenOptionsListItem;
import net.rim.device.apps.api.utility.framework.VerbToMenu;
import net.rim.device.apps.internal.options.resources.OptionsResources;
import net.rim.device.internal.deviceoptions.Owner;

public final class OwnerOptionsItem extends SaveableMainScreenOptionsListItem implements GlobalEventListener {
   private AutoTextEditField _ownerNameField;
   private AutoTextEditField _ownerInfoField;

   public OwnerOptionsItem() {
      super(OptionsResources.getString(1000));
   }

   @Override
   protected final void populateMainScreen(MainScreen mainScreen) {
      this._ownerNameField = (AutoTextEditField)(new Object(OptionsResources.getString(1001), "", 39, 4503601774854144L));
      this._ownerInfoField = (AutoTextEditField)(new Object(OptionsResources.getString(1002), "", 127, 4503599627370496L));
      this.updateFields();
      mainScreen.add(this._ownerNameField);
      mainScreen.add(this._ownerInfoField);
   }

   private final void updateFields() {
      this._ownerNameField.setText(Owner.getOwnerName());
      this._ownerNameField.setDirty(false);
      this._ownerInfoField.setText(Owner.getOwnerInfo());
      this._ownerInfoField.setDirty(false);
      byte allowUserChanges = ITPolicy.getByte(21, 1, (byte)0);
      this._ownerNameField.setEditable((allowUserChanges & 2) == 0);
      this._ownerInfoField.setEditable((allowUserChanges & 1) == 0);
   }

   @Override
   protected final void open() {
      UiApplication.getUiApplication().addGlobalEventListener(this);
      super.open();
   }

   @Override
   protected final void addRepositoryVerbs(VerbToMenu verbToMenu, int instance) {
      VerbRepository verbRepository = VerbRepository.getVerbRepository(1822762289573304788L);
      Verb[] factoryVerbs = verbRepository.getVerbs(null);
      if (factoryVerbs != null && factoryVerbs.length > 0) {
         verbToMenu.addVerbs(factoryVerbs);
      }
   }

   @Override
   protected final boolean save() {
      Owner.setOwnerName(this._ownerNameField.getText());
      Owner.setOwnerInfo(this._ownerInfoField.getText());
      return super.save();
   }

   @Override
   public final boolean confirm(Verb verb, Object context) {
      boolean close = super.confirm(verb, context);
      if (close) {
         UiApplication.getUiApplication().removeGlobalEventListener(this);
      }

      return close;
   }

   @Override
   protected final boolean invokeOptionsAction(int action) {
      switch (action) {
         case 1:
            return true;
         default:
            return false;
      }
   }

   @Override
   public final boolean isValid(Object context) {
      throw new RuntimeException("cod2jar: tail call (jumpspecial)");
   }

   @Override
   public final void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (guid == 8508406279413621091L || guid == -594020114676189989L || guid == -8392006003204551101L) {
         this.updateFields();
      }
   }
}
