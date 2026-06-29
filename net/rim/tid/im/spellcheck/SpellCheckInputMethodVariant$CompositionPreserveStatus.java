package net.rim.tid.im.spellcheck;

import net.rim.device.api.system.Application;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.component.TextInputDialog;
import net.rim.device.api.ui.container.DialogFieldManager;
import net.rim.device.api.ui.container.PopupScreen;

class SpellCheckInputMethodVariant$CompositionPreserveStatus extends PopupScreen implements TextInputDialog {
   private SpellCheckInputMethodVariant$PopScreenRunnable _popScreenRunnable = new SpellCheckInputMethodVariant$PopScreenRunnable(this);
   private int _id = -1;

   private SpellCheckInputMethodVariant$CompositionPreserveStatus(String message) {
      super(new DialogFieldManager(), 0);
      this.setAcceptsInput(true);
      DialogFieldManager dfm = (DialogFieldManager)this.getDelegate();
      RichTextField label = new RichTextField(message, 36028797018963968L);
      dfm.setMessage(label);
   }

   private void show0(int time) {
      this._id = Application.getApplication().invokeLater(this._popScreenRunnable, time, false);
      if (this._id == -1) {
         throw new RuntimeException("No timer available for status popup.");
      }

      Ui.getUiEngine().pushModalScreen(this);
   }

   public static void show(String message, int time) {
      SpellCheckInputMethodVariant$CompositionPreserveStatus status = new SpellCheckInputMethodVariant$CompositionPreserveStatus(message);
      status.show0(time);
   }
}
