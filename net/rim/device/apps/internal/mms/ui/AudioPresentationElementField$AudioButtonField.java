package net.rim.device.apps.internal.mms.ui;

import javax.microedition.media.Player;
import javax.microedition.media.PlayerListener;
import net.rim.device.api.system.Application;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.apps.internal.mms.resources.MMSResources;

class AudioPresentationElementField$AudioButtonField extends ButtonField implements PlayerListener {
   private int _action;
   private final AudioPresentationElementField this$0;

   AudioPresentationElementField$AudioButtonField(AudioPresentationElementField _1, int action) {
      this.this$0 = _1;
      this.setAction(action);
   }

   void setAction(int action) {
      this._action = action;
      String label = null;
      switch (this._action) {
         case 0:
            break;
         case 1:
         default:
            label = MMSResources.getString(96);
            break;
         case 2:
            label = MMSResources.getString(97);
      }

      if (label != null) {
         Application app = this.getApplication();
         if (app != null) {
            synchronized (app.getAppEventLock()) {
               this.setLabel(label);
               return;
            }
         }

         this.setLabel(label);
      }
   }

   private Application getApplication() {
      try {
         return this.getScreen().getApplication();
      } finally {
         ;
      }
   }

   @Override
   protected boolean trackwheelClick(int status, int time) {
      this.performAction();
      return true;
   }

   @Override
   protected boolean invokeAction(int action) {
      switch (action) {
         case 1:
            this.performAction();
            return true;
         default:
            return super.invokeAction(action);
      }
   }

   @Override
   protected boolean trackwheelRoll(int amount, int status, int time) {
      if (this._action == 1 && this.this$0._volumeControl != null) {
         int volume = this.this$0._volumeControl.getLevel() - 10 * amount;
         volume = Math.min(100, Math.max(volume, 10));
         this.this$0._volumeControl.setLevel(volume);
         return true;
      } else {
         return super.trackwheelRoll(amount, status, time);
      }
   }

   @Override
   protected void fieldChangeNotify(int context) {
      super.fieldChangeNotify(context);
      this.performAction();
   }

   private void performAction() {
      switch (this._action) {
         case 1:
         default:
            this.this$0.stopTune();
            return;
         case 2:
            this.this$0.startTune();
         case 0:
      }
   }

   @Override
   public void playerUpdate(Player player, String event, Object eventData) {
      if (event.equals("started")) {
         this.setAction(1);
      } else if (event.equals("stopped")) {
         this.setAction(2);
      } else if (event.equals("stoppedAtTime")) {
         this.setAction(2);
      } else if (event.equals("endOfMedia")) {
         this.setAction(2);
      } else {
         if (event.equals("error")) {
            this.setAction(2);
         }
      }
   }
}
