package net.rim.device.apps.internal.qm.peer;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.apps.internal.qm.resource.QmResources;

final class UserInfoScreen extends MainScreen implements FieldChangeListener {
   Field[] _fields;
   ButtonField _okButton;

   UserInfoScreen(Field[] fields) {
      this._fields = fields;
      this.createScreen();
   }

   private final void createScreen() {
      TitleField title = new TitleField(PeerResources.getString(3011), false);
      this.setTitle(title);
      HorizontalFieldManager hfm = new HorizontalFieldManager(12884901888L);
      this._okButton = new ButtonField(QmResources.getString(51), 12884901888L);
      this._okButton.setChangeListener(this);
      if (this._fields != null && this._fields.length != 0) {
         for (int i = 0; i < this._fields.length; i++) {
            if (this._fields[i] != null) {
               this.add(this._fields[i]);
            }
         }
      } else {
         RichTextField rtf = new RichTextField(PeerResources.getString(3012));
         this.add(rtf);
      }

      this.add(new SeparatorField());
      hfm.add(this._okButton);
      this.add(hfm);
      this._okButton.setFocus();
   }

   public final void go() {
      Ui.getUiEngine().pushScreen(this);
   }

   @Override
   protected final boolean trackwheelClick(int status, int time) {
      if (this.getLeafFieldWithFocus() == this._okButton) {
         this.fieldChanged(this._okButton, 0);
         return true;
      } else {
         return super.trackwheelClick(status, time);
      }
   }

   @Override
   public final void fieldChanged(Field field, int context) {
      if (field == this._okButton) {
         this.close();
      }
   }
}
