package net.rim.device.apps.internal.profiles;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.i18n.ResourceBundleFamily;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.apps.api.framework.model.VerbProvider;
import net.rim.device.apps.api.framework.profiles.TuneManager;
import net.rim.device.apps.api.ribbon.ConvenienceKeyOptionsProvider;
import net.rim.device.apps.api.ui.AppsMainScreen;
import net.rim.device.apps.api.ui.SystemEnabledMenu;

final class ProfilesScreen extends AppsMainScreen {
   private boolean _shouldDoSystemExit;
   private Profiles _profiles;
   private ProfilesScreen$ProfileListField _listField;
   private OverrideListField _overrideListField;
   private LabelField _overrideTitleField;
   private SeparatorField _overrideSeparatorField;
   private static final String PROFILE_MODULE_NAME = "net_rim_bb_profiles_app";

   ProfilesScreen(boolean shouldDoSystemExit) {
      super(0);
      this.setHelp("sounds");
      this._shouldDoSystemExit = shouldDoSystemExit;
      this._profiles = Profiles.getInstance();
      ResourceBundleFamily resources = ResourceBundle.getBundle(2384708948246157241L, "net.rim.device.apps.internal.resource.Profiles");
      LabelField titleField = (LabelField)(new Object((Object)null, (long)268435456));
      titleField.setText(resources, 0);
      this.setTitle(titleField);
      this._listField = new ProfilesScreen$ProfileListField(this);
      this.add(this._listField);
      this._overrideSeparatorField = (SeparatorField)(new Object());
      this.add(this._overrideSeparatorField);
      this._overrideTitleField = (LabelField)(new Object(resources, 240));
      this.add(this._overrideTitleField);
      this._overrideListField = new OverrideListField(this);
      this.add(this._overrideListField);
      Overrides.getInstance().addCollectionListener(this._overrideListField);
      this.setFocus();
      this._listField.setElementWithFocus(this._profiles.getEnabled());
   }

   @Override
   public final void close() {
      UiApplication.getUiApplication().popScreen(this);
      if (this._shouldDoSystemExit) {
         System.exit(0);
      }
   }

   @Override
   public final boolean keyDown(int keycode, int time) {
      int key = Keypad.key(keycode);
      if ((key == 19 || key == 21) && this.profilesOwnsConvenienceKey(key)) {
         this.keyChar(' ', 0, time);
         this.close();
         return true;
      } else {
         return super.keyDown(keycode, time);
      }
   }

   @Override
   protected final boolean keyChar(char keyChar, int statusInt, int timeInt) {
      if (keyChar == 27) {
         this.close();
         return true;
      } else {
         return super.keyChar(keyChar, statusInt, timeInt);
      }
   }

   @Override
   protected final void makeMenu(SystemEnabledMenu menu, int instance) {
      super.makeMenu(menu, instance);
      if (TuneManager.isTuneManagerAvailable()) {
         menu.add(new ProfilesScreen$ShowTunesVerb());
      }

      Field field = this.getLeafFieldWithFocus();
      if (field instanceof OverrideListField) {
         menu.add(new EditProfileVerb(null));
         if (this._overrideListField instanceof Object) {
            VerbProvider verbProvider = this._overrideListField;
            menu.add(verbProvider, 0);
            return;
         }
      } else if (field instanceof Object) {
         menu.add(new EditOverrideVerb(null));
         Profile profile = (Profile)this._listField.getSelectedElement();
         if (profile instanceof Object) {
            VerbProvider verbProvider = profile;
            menu.add(verbProvider, 0);
         }
      }
   }

   @Override
   public final void onUiEngineAttached(boolean attached) {
      if (attached) {
         this._listField.setSelectedIndex(this._profiles.indexOf(this._profiles.getEnabled()));
      } else {
         this._profiles.removeCollectionListener(this._listField);
         Overrides.getInstance().removeCollectionListener(this._overrideListField);
      }

      super.onUiEngineAttached(attached);
   }

   @Override
   protected final boolean openDevelopmentBackdoor(int backdoorCode) {
      boolean backlightOption = false;
      switch (backdoorCode) {
         case 1112297294:
            backlightOption = true;
         case 1112297286:
            ProfilesOptions.getOptions().enableBackLightOption(backlightOption);
            return true;
         default:
            return super.openDevelopmentBackdoor(backdoorCode);
      }
   }

   private final boolean profilesOwnsConvenienceKey(int key) {
      ConvenienceKeyOptionsProvider convKeyProvider = ConvenienceKeyOptionsProvider.getInstance();
      String keyOwner = null;
      if (convKeyProvider != null) {
         switch (key) {
            case 19:
               keyOwner = convKeyProvider.getConvenienceKey1Owner();
               break;
            case 21:
               keyOwner = convKeyProvider.getConvenienceKey2Owner();
               break;
            default:
               return false;
         }
      }

      return keyOwner == null ? key == 21 : keyOwner.startsWith("net_rim_bb_profiles_app");
   }
}
