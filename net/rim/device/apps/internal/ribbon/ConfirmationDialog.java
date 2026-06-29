package net.rim.device.apps.internal.ribbon;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.i18n.ResourceBundleFamily;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.XYRect;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.internal.ui.component.SimpleChoiceDialog;

public final class ConfirmationDialog extends SimpleChoiceDialog {
   private Verb _optionsVerb = (Verb)ApplicationRegistry.getApplicationRegistry().get(6173420044896290124L);
   Application _foregroundBeforeRibbon = null;
   private static ResourceBundleFamily _resources = ResourceBundle.getBundle(1137270090621229274L, "net.rim.device.apps.internal.resource.Ribbon");
   private static final long DATE_TIME_OPTIONS_SCREEN_KEY = 6173420044896290124L;
   private static boolean _dialogShownFlag;

   public ConfirmationDialog() {
      super(_resources.getString(13), new Object[]{_resources.getString(55), _resources.getString(56)}, 1, Bitmap.getPredefinedBitmap(1), 33554432);
   }

   @Override
   protected final void applyTheme() {
      super.applyTheme();
      if (Display.getHeight() < 160) {
         this.setFont(null);
         Font font = this.getFont();
         if (font.getHeight() > 10) {
            this.setFont(font.derive(font.getStyle(), 10));
         }
      }
   }

   @Override
   public final void show() {
      if (!_dialogShownFlag) {
         _dialogShownFlag = true;
         super.show();
      }
   }

   @Override
   protected final void sublayout(int width, int height) {
      super.sublayout(width, height);
      if (height < 160) {
         XYRect extent = this.getExtent();
         this.setPosition(extent.x, height - extent.height);
      }
   }

   @Override
   protected final void close(int reason) {
      throw new RuntimeException("cod2jar: tail call (jumpspecial)");
   }

   @Override
   public final void select(int index) {
      super.select(index);
      if (index != 0 && this._optionsVerb != null) {
         UiApplication app = UiApplication.getUiApplication();
         this._optionsVerb.invoke(null);
         this._optionsVerb = null;
         Screen screen = app.getActiveScreen();
         if (screen != null) {
            try {
               int count = screen.getFieldCount();

               for (int idx = 0; idx < count; idx++) {
                  Field field = screen.getField(idx);
                  if (field instanceof Object) {
                     field.setFocus();
                     break;
                  }
               }
            } finally {
               return;
            }
         }
      }
   }
}
