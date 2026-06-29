package net.rim.device.apps.internal.browser.plugin.media.field;

import net.rim.device.api.system.Application;
import net.rim.device.api.system.AudioHeadsetListener;
import net.rim.device.api.system.Phone;
import net.rim.device.api.ui.Keypad;
import net.rim.device.apps.api.ribbon.system.StandbyManager;
import net.rim.device.apps.internal.browser.options.GeneralProperty;
import net.rim.device.internal.media.MediaRemoteControlListener;
import net.rim.device.internal.system.UnhandledGlobalKeyListener;
import net.rim.device.internal.vad.VADUserEventListener;

final class MediaBrowserField$MyKeyHandler implements UnhandledGlobalKeyListener, AudioHeadsetListener, VADUserEventListener, MediaRemoteControlListener {
   private boolean _vadActive;
   private boolean _voiceActiveOnClick;
   private final MediaBrowserField this$0;

   MediaBrowserField$MyKeyHandler(MediaBrowserField _1) {
      this.this$0 = _1;
   }

   private final void changeVolume(boolean up) {
      if (this.this$0._volumeField != null) {
         if (up) {
            this.this$0._volumeField.increment();
         } else {
            this.this$0._volumeField.decrement();
         }

         if (this.this$0._volumeControl != null) {
            int newVolume = this.this$0._volumeField.getVolumeLevel();
            int audioPath = this.this$0._audioPathControl.getAudioPath();
            this.this$0._volumeControl.setLevel(newVolume);
            GeneralProperty.setCurrentProperty(this.this$0._audioProperties[audioPath], newVolume);
         }
      }
   }

   @Override
   public final boolean keyDown(int keycode, int time) {
      int keyChar = Keypad.key(keycode);
      StandbyManager standbyManager = StandbyManager.getInstance();
      boolean resumePlay = false;
      if (!standbyManager.isEnteringStandby() && (this.this$0._paused || this.this$0._player != null && this.this$0._player.getState() == 400)) {
         resumePlay = true;
      }

      if (resumePlay && Keypad.hasMuteKey() && keyChar == 273 && this.this$0._player != null) {
         try {
            this.this$0.play(Application.isEventDispatchThread());
            return true;
         } finally {
            ;
         }
      } else if (keyChar == 4096) {
         this.changeVolume(true);
         return true;
      } else if (keyChar == 4097) {
         this.changeVolume(false);
         return true;
      } else {
         return false;
      }
   }

   @Override
   public final boolean keyChar(char character, int status, int time) {
      return false;
   }

   @Override
   public final boolean keyUp(int keycode, int time) {
      return false;
   }

   @Override
   public final boolean keyRepeat(int keycode, int time) {
      return false;
   }

   @Override
   public final boolean keyStatus(int keycode, int time) {
      return false;
   }

   @Override
   public final void headsetButtonClick(int button, int time) {
      if (button == 0) {
         this._voiceActiveOnClick = Phone.getInstance().isActive();
      }
   }

   @Override
   public final void headsetButtonUnclick(int button, int time) {
      if (!this._vadActive && !this._voiceActiveOnClick && !Phone.getInstance().isActive() && this.this$0._player != null) {
         switch (button) {
            case -1:
               break;
            case 0:
            case 1:
            default:
               try {
                  this.this$0.play(Application.isEventDispatchThread());
                  return;
               } finally {
                  return;
               }
            case 2:
               this.changeVolume(true);
               return;
            case 3:
               this.changeVolume(false);
               return;
            case 4:
               this.this$0.nextTrack();
               return;
            case 5:
               this.this$0.previousTrack();
         }
      }
   }

   @Override
   public final void headsetInserted(int type) {
   }

   @Override
   public final void headsetRemoved() {
   }

   @Override
   public final void vadEvent(int event, int context) {
      switch (event) {
         case 1:
            this._vadActive = true;
            return;
         case 3:
            this._vadActive = false;
      }
   }

   @Override
   public final void mediaPanelEvent(int operation, int action) {
      switch (action) {
         case 2:
            switch (operation) {
               case 67:
               case 71:
               case 74:
                  break;
               case 68:
               case 70:
               default:
                  try {
                     this.this$0.play(Application.isEventDispatchThread());
                     return;
                  } finally {
                     return;
                  }
               case 69:
                  this.this$0.stop(true);
                  return;
               case 72:
               case 76:
                  this.this$0.previousTrack();
                  break;
               case 73:
               case 75:
                  this.this$0.nextTrack();
                  return;
            }
      }
   }
}
