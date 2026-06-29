package net.rim.device.apps.games.brickbreaker;

import net.rim.device.api.games.util.HighScoreServerListener;
import net.rim.device.api.system.Application;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.util.StringTokenizer;
import net.rim.device.apps.api.ui.CommonResources;

public final class Options$SendingScorePopup extends Dialog implements HighScoreServerListener {
   private ButtonField _button;
   private boolean _success;
   private Options _options;

   public Options$SendingScorePopup(Options opt) {
      super(Options._resources.getString(67), null, null, 0, null);
      this._options = opt;
      this.setEscapeEnabled(false);
      this._button = (ButtonField)(new Object(CommonResources.getString(9042)));
      this._button.setChangeListener(this);
      this.add(this._button);
      this._success = false;
   }

   @Override
   public final void fieldChanged(Field field, int context) {
      if (field == this._button) {
         if (this._success) {
            this.close();
            return;
         }

         this.cancel();
      }
   }

   @Override
   protected final boolean keyChar(char key, int status, int time) {
      boolean handled = super.keyChar(key, status, time);
      if (handled) {
         return handled;
      }

      if (key == 27 || key == '\n') {
         if (this._success) {
            this.close();
            return true;
         }

         this.cancel();
      }

      return true;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void serverReply(String result) {
      if (result != null) {
         StringTokenizer st = (StringTokenizer)(new Object(result, ';'));
         boolean var9 = false /* VF: Semaphore variable */;

         String newText;
         label69:
         try {
            var9 = true;
            int any = Integer.parseInt(st.nextToken());
            if (!this.isDisplayed()) {
               return;
            }

            int rank = -1;
            if (any == 4) {
               rank = Integer.parseInt(st.nextToken());
               this._options.highScore = Integer.parseInt(st.nextToken());
            }

            newText = this.getLabel().getText();
            switch (any) {
               case 1:
               case 7:
               case 10:
                  var9 = false;
                  break;
               case 2:
               default:
                  newText = Options._resources.getString(68);
                  var9 = false;
                  break;
               case 3:
                  newText = Options._resources.getString(56);
                  var9 = false;
                  break;
               case 4:
                  if (rank > 0) {
                     newText = ((StringBuffer)(new Object()))
                        .append(Options._resources.getString(57))
                        .append("\n")
                        .append(Options._resources.getString(63))
                        .append(rank)
                        .toString();
                  } else {
                     this.getLabel().setText(Options._resources.getString(57));
                  }

                  Options.isScoreSent = true;
                  this._options.save();
                  var9 = false;
                  break;
               case 5:
                  newText = Options._resources.getString(58);
                  var9 = false;
                  break;
               case 6:
                  newText = Options._resources.getString(59);
                  var9 = false;
                  break;
               case 8:
                  newText = Options._resources.getString(55);
                  var9 = false;
                  break;
               case 9:
                  newText = Options._resources.getString(54);
                  var9 = false;
                  break;
               case 11:
                  newText = Options._resources.getString(62);
                  var9 = false;
            }
         } finally {
            if (var9) {
               newText = CommonResources.getString(105);
               break label69;
            }
         }

         synchronized (Application.getApplication().getAppEventLock()) {
            this.getLabel().setText(newText);
            this._button.setLabel(CommonResources.getString(117));
         }

         this.invalidate();
      }
   }
}
