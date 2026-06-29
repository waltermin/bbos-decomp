package net.rim.device.apps.internal.vad;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.i18n.ResourceBundleFamily;
import net.rim.device.api.ui.component.ObjectChoiceField;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.options.OptionsProviderRegistration;
import net.rim.device.apps.api.options.OptionsProviderRegistration$OptionsProvider;
import net.rim.device.apps.api.options.SaveableMainScreenOptionsListItem;
import net.rim.device.apps.api.ui.BooleanChoiceField;
import net.rim.device.apps.api.utility.framework.VerbToMenu;
import net.rim.device.internal.vad.VADParameters;

final class VADOptionsScreen extends SaveableMainScreenOptionsListItem {
   private ObjectChoiceField _choiceLists;
   private ObjectChoiceField _sensitivity;
   private BooleanChoiceField _audioPrompts;
   private BooleanChoiceField _digitPlayback;
   private BooleanChoiceField _namePlayback;
   private ObjectChoiceField _namePlaybackSpeed;
   private ObjectChoiceField _namePlaybackVolume;
   private static ResourceBundleFamily _rb = ResourceBundle.getBundle(6972709272210014688L, "net.rim.device.apps.internal.vad.resource.VAD");

   private VADOptionsScreen() {
      super(_rb.getString(0));
      ContextObject.put(super._context, 244, new Object(32872));
   }

   @Override
   protected final void populateMainScreen(MainScreen mainScreen) {
      VADEngineManager manager = VADEngineManager.getInstance();
      VADParameters data = manager.getParameters();
      this._choiceLists = (ObjectChoiceField)(new Object(_rb.getString(14), _rb.getStringArray(15), data._confirmation));
      mainScreen.add(this._choiceLists);
      int index;
      switch (data._sensitivity) {
         case 35:
            index = 6;
            break;
         case 45:
            index = 5;
            break;
         case 55:
            index = 4;
            break;
         case 75:
            index = 2;
            break;
         case 85:
            index = 1;
            break;
         case 95:
            index = 0;
            break;
         default:
            index = 3;
      }

      this._sensitivity = (ObjectChoiceField)(new Object(_rb.getString(17), _rb.getStringArray(23), index, 134217728));
      mainScreen.add(this._sensitivity);
      this._audioPrompts = (BooleanChoiceField)(new Object(_rb.getString(18), 2, data._playPrompts));
      mainScreen.add(this._audioPrompts);
      this._digitPlayback = (BooleanChoiceField)(new Object(_rb.getString(19), 2, data._playDigits));
      mainScreen.add(this._digitPlayback);
      this._namePlayback = (BooleanChoiceField)(new Object(_rb.getString(20), 2, data._playNames));
      mainScreen.add(this._namePlayback);
      byte var5;
      switch (data._ttsSpeed) {
         case 100:
            var5 = 0;
            break;
         case 108:
            var5 = 1;
            break;
         case 116:
            var5 = 2;
            break;
         case 133:
            var5 = 4;
            break;
         case 141:
            var5 = 5;
            break;
         case 150:
            var5 = 6;
            break;
         default:
            var5 = 3;
      }

      this._namePlaybackSpeed = (ObjectChoiceField)(new Object(_rb.getString(22), _rb.getStringArray(24), var5, 134217728));
      mainScreen.add(this._namePlaybackSpeed);
      switch (data._ttsVolume) {
         case -10:
            var5 = 0;
            break;
         case -7:
            var5 = 1;
            break;
         case -4:
            var5 = 2;
            break;
         case 3:
            var5 = 4;
            break;
         case 6:
            var5 = 5;
            break;
         case 10:
            var5 = 6;
            break;
         default:
            var5 = 3;
      }

      this._namePlaybackVolume = (ObjectChoiceField)(new Object(_rb.getString(25), _rb.getStringArray(26), var5, 134217728));
      mainScreen.add(this._namePlaybackVolume);
   }

   @Override
   protected final void addScreenVerbs(VerbToMenu verbToMenu, int instance) {
      super.addScreenVerbs(verbToMenu, instance);
      verbToMenu.addVerb(new VADOptionsScreen$AdaptDigitsVerb());
      verbToMenu.addVerb(new VADOptionsScreen$ResetDigitsVerb());
   }

   @Override
   protected final boolean save() {
      VADEngineManager manager = VADEngineManager.getInstance();
      VADParameters data = manager.getParameters();
      data._confirmation = this._choiceLists.getSelectedIndex();
      int x = 0;
      switch (this._sensitivity.getSelectedIndex()) {
         case -1:
            break;
         case 0:
         default:
            x = 95;
            break;
         case 1:
            x = 85;
            break;
         case 2:
            x = 75;
            break;
         case 3:
            x = 65;
            break;
         case 4:
            x = 55;
            break;
         case 5:
            x = 45;
            break;
         case 6:
            x = 35;
      }

      data._sensitivity = x;
      data._playPrompts = this._audioPrompts.isAffirmative();
      data._playDigits = this._digitPlayback.isAffirmative();
      data._playNames = this._namePlayback.isAffirmative();
      switch (this._namePlaybackSpeed.getSelectedIndex()) {
         case -1:
            break;
         case 0:
         default:
            x = 100;
            break;
         case 1:
            x = 108;
            break;
         case 2:
            x = 116;
            break;
         case 3:
            x = 125;
            break;
         case 4:
            x = 133;
            break;
         case 5:
            x = 141;
            break;
         case 6:
            x = 150;
      }

      data._ttsSpeed = x;
      switch (this._namePlaybackVolume.getSelectedIndex()) {
         case -1:
            break;
         case 0:
         default:
            x = -10;
            break;
         case 1:
            x = -7;
            break;
         case 2:
            x = -4;
            break;
         case 3:
            x = 0;
            break;
         case 4:
            x = 3;
            break;
         case 5:
            x = 6;
            break;
         case 6:
            x = 10;
      }

      data._ttsVolume = x;
      manager.commitPersistentData();
      return super.save();
   }

   static final void init() {
      OptionsProviderRegistration$OptionsProvider op = new VADOptionsScreen$1();
      OptionsProviderRegistration.registerOptionsProvider(op);
   }

   VADOptionsScreen(VADOptionsScreen$1 x0) {
      this();
   }
}
