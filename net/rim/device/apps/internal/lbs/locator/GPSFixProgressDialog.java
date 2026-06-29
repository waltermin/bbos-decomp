package net.rim.device.apps.internal.lbs.locator;

import net.rim.device.api.lbs.gps.GPSDevice;
import net.rim.device.api.lbs.gps.GPSLocationData;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.FontRegistry;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.container.FlowFieldManager;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.apps.internal.lbs.LBSApplication;
import net.rim.device.apps.internal.lbs.resources.LBSResources;
import net.rim.device.internal.ui.component.PopupDialog;

public final class GPSFixProgressDialog extends PopupDialog implements FieldChangeListener {
   private ButtonField _cancelField;
   private FlowFieldManager _hfm = new FlowFieldManager(12884901888L);
   private boolean _cancelPressed = false;
   private GPSFixProgressDialog$FixThread _fixThread;
   private GPSLocationData _location;
   private GPSDevice _device;

   public GPSFixProgressDialog(GPSDevice device, String title) {
      super(new VerticalFieldManager(1153220571769602048L), 196608);
      this.applyTheme();
      this.addTitle(title);
      this.addButtons();
      this._device = device;
      this._cancelField.setFocus();
   }

   private final void addTitle(String title) {
      if (title != null && title.length() > 0) {
         LabelField labelField = new LabelField(title);
         labelField.setFont(Font.getDefault().derive(1));
         this.add(labelField);
      }
   }

   private final void addButtons() {
      this._cancelField = new ButtonField(CommonResources.getString(9042), 65536);
      this._cancelField.setChangeListener(this);
      this._hfm.add(this._cancelField);
      this.add(this._hfm);
   }

   public final boolean isCancelPressed() {
      return this._cancelPressed;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public final GPSLocationData doModal() {
      this._fixThread = new GPSFixProgressDialog$FixThread(this, this._device, this);
      this._fixThread.start();
      boolean var3 = false /* VF: Semaphore variable */;

      try {
         var3 = true;
         Ui.getUiEngine().pushModalScreen(this);
         if (!this._cancelPressed) {
            if (this._location != null) {
               if (this._location == null) {
                  var3 = false;
                  return this._location;
               }

               if (this._location.isValid()) {
                  var3 = false;
                  return this._location;
               }
            }

            Dialog.alert(LBSResources.getString(394));
            var3 = false;
         } else {
            var3 = false;
         }
      } finally {
         if (var3) {
            EventLogger.logEvent(LBSApplication.UID, ("_location: " + this._location).getBytes(), 2);
            return this._location;
         }
      }

      return this._location;
   }

   @Override
   protected final void applyTheme() {
      super.applyTheme();
      int size = Ui.convertSize(FontRegistry.DEFAULT_SIZE, 3, 0);
      this.setFont(null);
      Font font = this.getFont();
      if (font.getHeight() > size && size > 0) {
         this.setFont(font.derive(font.getStyle(), size));
      }
   }

   @Override
   protected final boolean keyChar(char key, int status, int time) {
      if (key == 27) {
         this._cancelPressed = true;
         this.close();
         return true;
      } else {
         return super.keyChar(key, status, time);
      }
   }

   @Override
   public final void fieldChanged(Field field, int context) {
      if (this.isDisplayed()) {
         if (field == this._cancelField) {
            this._cancelPressed = true;
            this.close();
         }
      }
   }
}
