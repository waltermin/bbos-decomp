package net.rim.device.apps.internal.options.items;

import net.rim.device.api.itpolicy.ITPolicy;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.AutoTextEditField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.apps.internal.options.resources.OptionsResources;
import net.rim.device.internal.deviceoptions.Owner;
import net.rim.device.internal.ui.container.FrameLayout;

final class OwnerOptionsController implements GlobalEventListener {
   private AutoTextEditField _ownerNameField;
   private AutoTextEditField _ownerInfoField;
   private boolean _isWizard;

   public OwnerOptionsController() {
      this(false);
   }

   public OwnerOptionsController(boolean isWizard) {
      this._isWizard = isWizard;
   }

   protected final void populateMainScreen(MainScreen mainScreen, Manager content) {
      String nameLabel = OptionsResources.getString(1001);
      String infoLabel = OptionsResources.getString(1002);
      this._ownerNameField = (AutoTextEditField)(new Object(this._isWizard ? "" : nameLabel, "", 39, 4503601774854144L));
      this._ownerInfoField = new OwnerOptionsController$1(this, this._isWizard ? "" : infoLabel, "", 127, 4503599627370496L);
      this.updateFields();
      if (!this._isWizard) {
         content.add(this._ownerNameField);
         content.add(this._ownerInfoField);
      } else {
         Font labelFont = mainScreen.getFont().derive(1);
         LabelField label = (LabelField)(new Object(nameLabel, 36028797018963968L));
         label.setFont(labelFont);
         content.add(label);
         FrameLayout frame = (FrameLayout)(new Object());
         frame.add(this._ownerNameField);
         frame.setBorder(0, 4, 0, 4);
         content.add(frame);
         label = (LabelField)(new Object(infoLabel, 36028797018963968L));
         label.setFont(labelFont);
         content.add(label);
         frame = (FrameLayout)(new Object());
         frame.add(this._ownerInfoField);
         frame.setBorder(0, 4, 0, 4);
         content.add(frame);
      }
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

   protected final void beforeOpen() {
      UiApplication.getUiApplication().addGlobalEventListener(this);
   }

   protected final boolean save() {
      Owner.setOwnerName(this._ownerNameField.getText());
      Owner.setOwnerInfo(this._ownerInfoField.getText());
      return true;
   }

   public final void beforeClose() {
      UiApplication.getUiApplication().removeGlobalEventListener(this);
   }

   @Override
   public final void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (guid == 8508406279413621091L || guid == -594020114676189989L || guid == -8392006003204551101L) {
         this.updateFields();
      }
   }
}
