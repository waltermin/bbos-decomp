package net.rim.device.apps.internal.phone.api;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.AudioRouter;
import net.rim.device.apps.internal.phone.options.PhoneOptions;
import net.rim.device.apps.internal.phone.resource.PhoneResources;
import net.rim.device.internal.ui.IconCollection;
import net.rim.device.internal.ui.Image;

public class EnhanceCallAudioServices {
   private int _ecaState;
   private IconCollection _icons = PhoneResources.getECAIcons();
   private Image _currentIcon = this._icons.getImage(0);
   public static final int STATE_BLANK;
   public static final int STATE_BOOST_BASS;
   public static final int STATE_BOOST_TREBLE;
   public static final int STATE_BOOST_NONE;
   private static final int DEFAULT_WIDTH;
   private static final int DEFAULT_HEIGHT;
   private static EnhanceCallAudioServices _ecaInstance = null;
   private static final long ECA_GUID;

   EnhanceCallAudioServices() {
   }

   public static synchronized EnhanceCallAudioServices getInstance() {
      if (_ecaInstance == null) {
         ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
         _ecaInstance = (EnhanceCallAudioServices)ar.getOrWaitFor(-8184463205606674193L);
         if (_ecaInstance == null) {
            synchronized (ar) {
               ar.put(-8184463205606674193L, new EnhanceCallAudioServices());
               _ecaInstance = (EnhanceCallAudioServices)ar.getOrWaitFor(-8184463205606674193L);
            }
         }
      }

      return _ecaInstance;
   }

   public boolean isECASupported() {
      AudioRouter audioRouter = AudioRouter.getInstance();
      return audioRouter.isEQPresetSupported();
   }

   public int getEnhanceCallAudio() {
      return this._ecaState;
   }

   public int nextECASetting() {
      this._ecaState++;
      if (this._ecaState > 2) {
         this._ecaState = 0;
      }

      this.setState(this._ecaState);
      return this._ecaState;
   }

   public int getState() {
      return this._ecaState;
   }

   public void setState(int state) {
      if (state >= 0 && state <= 3) {
         this._ecaState = state;
         this._currentIcon = this._icons.getImage(this._ecaState);
         PhoneOptions opts = PhoneOptions.getOptions();
         opts.setPreviousEnhanceCallAudio(this._ecaState);
         opts.commit();
         AudioRouter audioRouter = AudioRouter.getInstance();
         switch (state) {
            case 0:
               audioRouter.setEQPreset(0);
               break;
            case 1:
            default:
               audioRouter.setEQPreset(1);
               return;
            case 2:
               audioRouter.setEQPreset(2);
               return;
         }
      }
   }

   public Image getIconForState(int state) {
      return this._icons.getImage(state);
   }

   public Image getIconForCurrentState() {
      return this._ecaState == 3 ? this.getIconForState(0) : this._currentIcon;
   }

   public int getIconWidth() {
      return this._currentIcon.getWidth(27, 19);
   }

   public int getIconHeight() {
      return this._currentIcon.getHeight(27, 19);
   }
}
