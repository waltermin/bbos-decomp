package net.rim.device.internal.media;

import net.rim.device.api.system.AudioRouter;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.ActiveRichTextField;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.DialogClosedListener;
import net.rim.device.api.ui.container.DialogFieldManager;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.internal.i18n.CommonResource;
import net.rim.device.internal.util.OptionsRegistry$Listener;

final class MediaOptionsUtilities$VolumeBoostDialog extends Dialog implements DialogClosedListener, OptionsRegistry$Listener {
   private boolean _closeWhenVisible;
   private static final String[] _options = new String[]{CommonResource.getString(10174), CommonResource.getString(10175), CommonResource.getString(10173)};
   private static final int[] _values = new int[]{4, -1, 2, 51, 4408146, 4801362, 5391186, 5526098, -805044208, 962921524, 692610900, -1825351564};

   MediaOptionsUtilities$VolumeBoostDialog() {
      super("", _options, _values, 1, null, 0);
      this.setEscapeEnabled(true);
      this.setDontAskAgainPrompt(true);
      this.createDialog();
      this.setDialogClosedListener(this);
      MediaOptionsRegistry.getInstance().addOptionsRegistryChangeListener(this);
   }

   private final void createDialog() {
      this.setIcon(ThemeManager.getThemeAwareImage("dialog_question"));
      DialogFieldManager dialogManager = (DialogFieldManager)this.getDelegate();
      String message = CommonResource.getString(10169);
      ActiveRichTextField messageBody = new ActiveRichTextField(message, 18014398509481984L);
      dialogManager.setMessage(messageBody);
   }

   @Override
   public final void dialogClosed(Dialog dialog, int choice) {
      MediaOptionsRegistry registry = MediaOptionsRegistry.getInstance();
      registry.removeOptionsRegistryChangeListener(this);
      boolean activate = false;
      if (choice == 4) {
         registry.setInt(-811168513825316359L, MediaOptionsUtilities.computeCurrentImsiValue());
         activate = true;
      }

      registry.setBoolean(2886183832722201160L, activate);
      AudioRouter.getInstance().setVolumeBoostMode(activate);
      if (this.isDontAskAgainChecked()) {
         registry.setBoolean(-4387502259448276168L, true);
      } else {
         registry.setBoolean(-4387502259448276168L, false);
      }
   }

   @Override
   public final void fieldChanged(Field field, int context) {
      if (field instanceof ButtonField) {
         int choice;
         try {
            choice = _values[this.getLeafFieldWithFocus().getIndex()];
         } catch (Exception e) {
            choice = -1;
         }

         if (choice == 2) {
            UiApplication app = UiApplication.getUiApplication();
            String data = CommonResource.getString(10172);
            ActiveRichTextField moreInfoField = new ActiveRichTextField(data, 18014398509481984L);
            MediaOptionsUtilities$VolumeBoostMoreInformationScreen screen = new MediaOptionsUtilities$VolumeBoostMoreInformationScreen();
            screen.add(moreInfoField);
            app.pushScreen(screen);
            return;
         }

         super.fieldChanged(field, context);
      }
   }

   @Override
   public final void onOptionsRegistryChange(long key) {
      if (key == 2886183832722201160L) {
         MediaOptionsRegistry.getInstance().removeOptionsRegistryChangeListener(this);
         this.setDialogClosedListener(null);
         this._closeWhenVisible = true;
      }
   }

   @Override
   protected final void onExposed() {
      if (this._closeWhenVisible) {
         this._closeWhenVisible = false;
         this.close();
      }
   }
}
