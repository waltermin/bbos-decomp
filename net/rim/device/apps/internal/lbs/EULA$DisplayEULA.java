package net.rim.device.apps.internal.lbs;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.CheckboxField;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.container.DialogFieldManager;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.PopupScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.apps.internal.lbs.resources.LBSResources;
import net.rim.device.internal.i18n.CommonResource;

final class EULA$DisplayEULA extends PopupScreen implements FieldChangeListener {
   CheckboxField _acceptCheckbox;
   EULA$DisplayEULA$FocusButton _acceptButton;
   ButtonField _cancelButton;
   boolean _accepted;
   private final EULA this$0;

   public EULA$DisplayEULA(EULA this$0) {
      super((Manager)(new Object()));
      this.this$0 = this$0;
      DialogFieldManager mgr = (DialogFieldManager)this.getDelegate();
      RichTextField eulaText = (RichTextField)(new Object(9007199254740992L));
      mgr.setMessage(eulaText);
      eulaText.setText(LBSResources.getString(121));
      VerticalFieldManager vfm = (VerticalFieldManager)(new Object(281474976710656L));
      mgr.addCustomField(vfm);
      vfm.add(this._acceptCheckbox = (CheckboxField)(new Object(LBSResources.getString(122), false)));
      this._acceptCheckbox.setChangeListener(this);
      HorizontalFieldManager hfm = (HorizontalFieldManager)(new Object(12884901888L));
      vfm.add(hfm);
      hfm.add(this._acceptButton = new EULA$DisplayEULA$FocusButton(this, LBSResources.getString(124)));
      this._acceptButton.setChangeListener(this);
      hfm.add(this._cancelButton = (ButtonField)(new Object(CommonResource.getString(10044))));
      this._cancelButton.setChangeListener(this);
   }

   final boolean doModal() {
      if (this.this$0.showEULA()) {
         Ui.getUiEngine().pushModalScreen(this);
         return this._accepted;
      } else {
         return true;
      }
   }

   @Override
   public final void fieldChanged(Field field, int context) {
      if (field == this._acceptCheckbox) {
         this._acceptButton._focusableState = this._acceptCheckbox.getChecked();
         this.invalidate();
      } else {
         this._accepted = field == this._acceptButton;
         Ui.getUiEngine().popScreen(this);
      }
   }

   @Override
   protected final boolean keyChar(char key, int status, int time) {
      if (key == 27) {
         this._accepted = false;
         Ui.getUiEngine().popScreen(this);
         return true;
      } else {
         return super.keyChar(key, status, time);
      }
   }

   @Override
   protected final boolean trackwheelClick(int status, int time) {
      Field field = this.getLeafFieldWithFocus();
      if (field == this._acceptCheckbox) {
         this._acceptCheckbox.setChecked(!this._acceptCheckbox.getChecked());
         return true;
      } else {
         return super.trackwheelClick(status, time);
      }
   }
}
