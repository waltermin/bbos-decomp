package net.rim.device.apps.api.setupwizard;

import net.rim.device.api.i18n.Locale;
import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.apps.api.setupwizard.resources.SetupWizardAPIResources;

public class WizardTitleBar extends VerticalFieldManager {
   private WizardProgressField _progressBar = new WizardProgressField();
   private LabelField _label;
   private StringBuffer _buffer;
   private int _progressMode;
   public static final int PROGRESS_INCREMENTAL = 0;
   public static final int PROGRESS_PERCENTAGE = 1;
   public static final int PROGRESS_NONE = 2;
   private static final int TITLEBAR_FONT_SIZE_PT = 7;
   private static final int TITLEBAR_ALT_FONT_SIZE_PT = 8;

   public WizardTitleBar(String label) {
      this._label = (LabelField)(new Object(label, 64));
      this._buffer = (StringBuffer)(new Object());
      this.add(this._label);
      this.add(this._progressBar);
   }

   public void setProgressHidden(boolean val) {
      this._progressBar.setHidden(val);
   }

   public void setProgress(int current, int max) {
      this._buffer.setLength(0);
      switch (this._progressMode) {
         case -1:
            break;
         case 0:
         default:
            this._buffer.append(MessageFormat.format(SetupWizardAPIResources.getString(15), new Object[]{Integer.toString(current), Integer.toString(max)}));
            break;
         case 1:
            if (max > 0) {
               int pct = current * 100 / max;
               this._buffer.append(Integer.toString(pct));
               this._buffer.append("%");
            }
      }

      this._progressBar.reset(this._buffer.toString(), 0, max, current);
   }

   public void setLabel(String label) {
      this._label.setText(label);
   }

   public void setProgressTextMode(int progressTextMode) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   public int getProgressTextMode() {
      return this._progressMode;
   }

   @Override
   protected void applyTheme() {
      super.applyTheme();
      int mask = -65536;
      int currentLocale = Locale.getDefault().getCode() & mask;
      if ((2053653326 & mask) != currentLocale && (1784741888 & mask) != currentLocale) {
         this.setFont(Font.getDefault().derive(1, 7, 3));
      } else {
         this.setFont(Font.getDefault().derive(1, 8, 3));
      }
   }
}
