package net.rim.device.apps.api.framework.verb;

import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.apps.api.ui.HintPollingThread;
import net.rim.device.apps.api.ui.HintPollingThread$HintProvider;
import net.rim.device.apps.api.ui.HintPopup;

public class PopupVerbWrapperSelectionDialog extends Dialog implements HintPollingThread$HintProvider {
   private boolean _showHint;

   public PopupVerbWrapperSelectionDialog(String prompt, String[] descriptions, int defaultChoiceIndex, boolean showHint) {
      super(prompt, descriptions, null, defaultChoiceIndex, null, 0);
      this._showHint = showHint;
   }

   @Override
   protected boolean trackwheelRoll(int amount, int status, int time) {
      HintPollingThread.reset();
      return super.trackwheelRoll(amount, status, time);
   }

   @Override
   protected void onUiEngineAttached(boolean attached) {
      super.onUiEngineAttached(attached);
      if (attached) {
         HintPollingThread.reset();
      }
   }

   @Override
   protected boolean navigationMovement(int dx, int dy, int status, int time) {
      HintPollingThread.reset();
      return super.navigationMovement(dx, dy, status, time);
   }

   @Override
   public Object getHint() {
      if (this._showHint) {
         Field field = this.getLeafFieldWithFocus();
         if (field instanceof ButtonField) {
            ButtonField button = (ButtonField)field;
            int numPixels = button.getFont().getAdvance(button.getLabel());
            if (numPixels > button.getContentWidth()) {
               String buttonString = button.getLabel();
               int separator = buttonString.indexOf(58);
               if (separator != -1) {
                  VerticalFieldManager vfm = new VerticalFieldManager();
                  vfm.setFont(Font.getDefault());
                  vfm.add(new LabelField(buttonString.substring(0, separator)));
                  vfm.add(new LabelField(buttonString.substring(separator + 2), 64));
                  return vfm;
               }

               LabelField hintLabel = new LabelField(buttonString, 64);
               hintLabel.setFont(Font.getDefault());
               return hintLabel;
            }
         }
      }

      return null;
   }

   @Override
   public void getHintPosition(XYRect rect) {
      HintPopup.transformToScreen(this.getLeafFieldWithFocus(), rect);
      rect.x = 1;
      rect.width = Display.getWidth() - 2;
   }
}
