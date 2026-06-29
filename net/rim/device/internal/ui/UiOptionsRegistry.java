package net.rim.device.internal.ui;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.Backlight;
import net.rim.device.api.ui.Trackball;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.internal.system.InternalServices;
import net.rim.device.internal.util.OptionsRegistry;
import net.rim.device.internal.util.OptionsRegistry$BooleanParameterDefinition;
import net.rim.device.internal.util.OptionsRegistry$CharParameterDefinition;
import net.rim.device.internal.util.OptionsRegistry$IntParameterDefinition;
import net.rim.device.internal.util.OptionsRegistry$Listener;
import net.rim.device.internal.util.OptionsRegistry$PercentParameterDefinition;
import net.rim.device.internal.util.OptionsRegistry$StringParameterDefinition;
import net.rim.vm.PersistentInteger;

public class UiOptionsRegistry extends OptionsRegistry implements OptionsRegistry$Listener {
   int _id = PersistentInteger.getId(-8725140013475467836L, 0);
   public static final int LEGACY_UI_STATE_REQUIRED = 0;
   public static final int LEGACY_UI_STATE_NOT_REQUIRED = 1;
   public static final int LEGACY_UI_STATE_PURGED = 2;
   public static final int LEGACY_UI_STATE_PURGE_PENDING = 3;
   public static final long PARAM_AUTOMATIC_BACKLIGHT = -4779732858771257140L;
   public static final long PARAM_BACKLIGHT_BRIGHTNESS = 1685157992482037073L;
   public static final long PARAM_BACKLIGHT_TIMEOUT = -4413078574218726736L;
   public static final long PARAM_FONT_THEME = -3809895234519942708L;
   public static final long PARAM_INCREASE_DIRECTION = 7732797588618697066L;
   public static final long PARAM_INCREASE_DIRECTION_USER_SET = -920146301111564303L;
   public static final long PARAM_LED_INDICATOR = 669566532873263048L;
   public static final long PARAM_NEW_INVALIDATE = -9149825875359456202L;
   public static final long PARAM_OFF_PROFILE_ENABLED = -3239010168274370595L;
   public static final long PARAM_SCREEN_CONTRAST = -1460892010845079752L;
   public static final long PARAM_SCROLL_VIEW_ALLOW = 9099188860628284837L;
   public static final long PARAM_THEME = -7276267599751932452L;
   public static final long PARAM_TRACKBALL_KEYLOCK_BACKLIGHT_TIMEOUT = 5292311981504290757L;
   public static final long PARAM_TRACKBALL_SENSITIVITY_X = 4925806619770988503L;
   public static final long PARAM_TRACKBALL_SENSITIVITY_Y = 1105523701474371332L;
   public static final long PARAM_TRACKBALL_FEEDBACK_AUDIBLE = 9173869926753706073L;
   public static final long PARAM_TRACKBALL_FILTER = -1211370300138911215L;
   public static final long PARAM_UI_MODE = -7317849688793253196L;
   public static final long PARAM_UI_STYLE = -2564832375572426041L;
   public static final long PARAM_TRACKBALL_CLICK_ACTION = -792512479819739610L;
   public static final long PARAM_TRACKWHEEL_CLICK_ACTION = -4786794427536080535L;
   public static final long PARAM_CURRENCY_KEY = -9137283790714193735L;
   public static final long PARAM_KEYPAD_TONE = 4710809342279106215L;
   public static final long PARAM_KEYPAD_REPEAT_RATE = 3372005855522553662L;
   public static final long PARAM_KEYPAD_REPEAT_DELAY = 4484666050398206415L;
   public static final long PARAM_CONVENIENCE_KEY_1 = 2233945566378975683L;
   public static final long PARAM_CONVENIENCE_KEY_1_CLICK_ARG = 4095133221719688519L;
   public static final long PARAM_CONVENIENCE_KEY_1_CLICK_AND_HOLD = -8314535069799645276L;
   public static final long PARAM_CONVENIENCE_KEY_1_CLICK_AND_HOLD_ARG = 3617424389142429872L;
   public static final long PARAM_CONVENIENCE_KEY_2 = -2722801897997978716L;
   public static final long PARAM_CONVENIENCE_KEY_2_CLICK_ARG = -1668923100586784160L;
   public static final long PARAM_CONVENIENCE_KEY_2_CLICK_AND_HOLD = -5923542525557144557L;
   public static final long PARAM_CONVENIENCE_KEY_2_CLICK_AND_HOLD_ARG = -5864237357067334068L;
   public static final long PARAM_MENU_STYLE = 8794318953449332169L;
   private static final long GUID = -8725140013475467836L;
   private static UiOptionsRegistry _instance;

   public void setLegacyUiOptionsState(int state) {
      switch (state) {
         case 0:
         case 1:
         case 2:
         case 3:
         default:
            PersistentInteger.set(this._id, state);
         case -1:
      }
   }

   public int getLegacyUiOptionsState() {
      return PersistentInteger.get(this._id);
   }

   @Override
   public void onOptionsRegistryChange(long key) {
      UiSettings.notifyRegistryChanged(key);
   }

   @Override
   public void setOptionsData(DataBuffer buffer) {
      UiSettings.setListenersActive(false);
      super.setOptionsData(buffer);
      UiSettings.initialize();
      UiSettings.setListenersActive(true);
      if (this.getLegacyUiOptionsState() != 2) {
         this.setLegacyUiOptionsState(1);
      }
   }

   public static UiOptionsRegistry getInstance() {
      if (_instance == null) {
         ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
         _instance = (UiOptionsRegistry)ar.getOrWaitFor(-8725140013475467836L);
         if (_instance == null) {
            _instance = new UiOptionsRegistry();
            ar.put(-8725140013475467836L, _instance);
         }
      }

      return _instance;
   }

   private UiOptionsRegistry() {
      super(-8725140013475467836L);
      this.define(-4779732858771257140L, new OptionsRegistry$BooleanParameterDefinition(true));
      this.define(1685157992482037073L, new OptionsRegistry$PercentParameterDefinition(Backlight.getBrightnessDefault()));
      this.define(-4413078574218726736L, new OptionsRegistry$IntParameterDefinition(Backlight.getTimeoutDefault(), 0, Integer.MAX_VALUE));
      this.define(-3809895234519942708L, new OptionsRegistry$StringParameterDefinition(null));
      this.define(7732797588618697066L, new OptionsRegistry$IntParameterDefinition(Trackball.isSupported() ? -1 : 1, -1, 1));
      this.define(-920146301111564303L, new OptionsRegistry$BooleanParameterDefinition(false));
      this.define(669566532873263048L, new OptionsRegistry$BooleanParameterDefinition(false));
      this.define(-9149825875359456202L, new OptionsRegistry$BooleanParameterDefinition(false));
      this.define(-3239010168274370595L, new OptionsRegistry$BooleanParameterDefinition(false));
      this.define(-1460892010845079752L, new OptionsRegistry$PercentParameterDefinition(50));
      this.define(9099188860628284837L, new OptionsRegistry$BooleanParameterDefinition(Trackball.isSupported()));
      this.define(-7276267599751932452L, new OptionsRegistry$StringParameterDefinition(null));
      this.define(5292311981504290757L, new OptionsRegistry$IntParameterDefinition(5, 0, Integer.MAX_VALUE));
      this.define(4925806619770988503L, new OptionsRegistry$PercentParameterDefinition(70));
      this.define(1105523701474371332L, new OptionsRegistry$PercentParameterDefinition(70));
      this.define(9173869926753706073L, new OptionsRegistry$BooleanParameterDefinition(false));
      this.define(-1211370300138911215L, new OptionsRegistry$IntParameterDefinition(-1, Integer.MIN_VALUE, Integer.MAX_VALUE));
      this.define(-7317849688793253196L, new OptionsRegistry$IntParameterDefinition(0, 0, 2));
      this.define(-792512479819739610L, new OptionsRegistry$IntParameterDefinition(1, 0, 1));
      this.define(-4786794427536080535L, new OptionsRegistry$IntParameterDefinition(0, 0, 1));
      this.define(2233945566378975683L, new OptionsRegistry$StringParameterDefinition(null));
      this.define(-2722801897997978716L, new OptionsRegistry$StringParameterDefinition(null));
      this.define(-8314535069799645276L, new OptionsRegistry$StringParameterDefinition(null));
      this.define(-5923542525557144557L, new OptionsRegistry$StringParameterDefinition(null));
      this.define(4095133221719688519L, new OptionsRegistry$StringParameterDefinition(null));
      this.define(3617424389142429872L, new OptionsRegistry$StringParameterDefinition(null));
      this.define(-1668923100586784160L, new OptionsRegistry$StringParameterDefinition(null));
      this.define(-5864237357067334068L, new OptionsRegistry$StringParameterDefinition(null));
      this.define(-9137283790714193735L, new OptionsRegistry$CharParameterDefinition('\u0000'));
      this.define(8794318953449332169L, new OptionsRegistry$IntParameterDefinition(2, 1, 2));
      this.define(4710809342279106215L, new OptionsRegistry$BooleanParameterDefinition(false));
      if (InternalServices.getFormFactor() != 9) {
         this.define(3372005855522553662L, new OptionsRegistry$IntParameterDefinition(200, 0, Integer.MAX_VALUE));
         this.define(4484666050398206415L, new OptionsRegistry$IntParameterDefinition(450, 0, Integer.MAX_VALUE));
      } else {
         this.define(3372005855522553662L, new OptionsRegistry$IntParameterDefinition(400, 0, Integer.MAX_VALUE));
         this.define(4484666050398206415L, new OptionsRegistry$IntParameterDefinition(600, 0, Integer.MAX_VALUE));
      }
   }
}
