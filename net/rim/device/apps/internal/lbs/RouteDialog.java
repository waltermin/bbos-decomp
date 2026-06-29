package net.rim.device.apps.internal.lbs;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.FontRegistry;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.container.FlowFieldManager;
import net.rim.device.api.ui.container.PopupScreen;
import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.apps.internal.lbs.resources.LBSResources;

final class RouteDialog extends PopupScreen implements FieldChangeListener {
   private ButtonField _okField;
   private FlowFieldManager _hfm = (FlowFieldManager)(new Object(12884901888L));

   RouteDialog(Route route) {
      super((Manager)(new Object(1153220571769602048L)), 196608);
      this.applyTheme();
      this.addTitle(LBSResources.getString(206));
      this.addDescription(route._label);
      this.addButtons();
      this._okField.setFocus();
   }

   private final void addTitle(String title) {
      if (title != null && title.length() > 0) {
         LabelField labelField = (LabelField)(new Object(title));
         labelField.setFont(Font.getDefault().derive(1));
         this.add(labelField);
         this.add((Field)(new Object()));
      }
   }

   private final void addDescription(String description) {
      this.add((Field)(new Object(description)));
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

   private final void addButtons() {
      this._okField = (ButtonField)(new Object(CommonResources.getString(117), 65536));
      this._okField.setChangeListener(this);
      this._hfm.add(this._okField);
      this.add(this._hfm);
   }

   public final void doModal() {
      Ui.getUiEngine().pushModalScreen(this);
   }

   @Override
   public final void fieldChanged(Field field, int context) {
      if (this.isDisplayed()) {
         if (field == this._okField) {
            this.close();
         }
      }
   }

   @Override
   protected final boolean keyChar(char key, int status, int time) {
      if (key == 27) {
         this.close();
         return true;
      } else {
         return super.keyChar(key, status, time);
      }
   }
}
