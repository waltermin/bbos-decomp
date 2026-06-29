package net.rim.device.apps.internal.profiles;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.AutoTextEditField;
import net.rim.device.api.ui.component.CheckboxField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.ObjectChoiceField;
import net.rim.device.api.ui.component.Status;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.VerbProvider;
import net.rim.device.apps.api.framework.profiles.TuneChoiceField;
import net.rim.device.apps.api.framework.profiles.TuneManager;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.ui.AppsMainScreen;
import net.rim.device.apps.api.ui.Confirmation;
import net.rim.device.apps.api.ui.SystemEnabledMenu;
import net.rim.device.internal.i18n.CommonResource;

class OverrideEditScreen extends AppsMainScreen implements Confirmation {
   private OverrideFromField _fromField;
   private AutoTextEditField _titleField;
   private CheckboxField _useTuneCheckbox;
   private TuneChoiceField _tuneField;
   private ObjectChoiceField _profileField;
   private Override _override;
   private boolean _newOverride;
   private Verb _saveVerb;
   private ContextObject _context;
   private Verb _closeVerb = (Verb)(new Object(0, this));
   static final int ACTIVE_PROFILE_UID = -1;

   OverrideEditScreen(Override override, boolean newOverride) {
      super(0);
      this._context = (ContextObject)(new Object(newOverride ? 6 : 0));
      ResourceBundle resources = ResourceBundle.getBundle(2384708948246157241L, "net.rim.device.apps.internal.resource.Profiles");
      this._saveVerb = new OverrideEditScreen$SaveVerb(this, null);
      this._override = override;
      this._newOverride = newOverride;
      String name = null;
      if (!newOverride) {
         name = override.getName();
      }

      if (this._override.isFromAddressBook()) {
         this._titleField = (AutoTextEditField)(new Object(resources.getString(241), name, name.length(), 36028797018963968L));
      } else {
         this._titleField = (AutoTextEditField)(new Object(resources.getString(241), name));
      }

      this.setTitle(this._titleField);
      this._fromField = new OverrideFromField(this._override);
      this.add(this._fromField);
      this.add((Field)(new Object()));
      Profiles profiles = Profiles.getInstance();
      int numProfiles = profiles.size();
      int profileUID = this._override.getProfileUID();
      int selectedProfileIndex = 0;
      if (profileUID == -1) {
         selectedProfileIndex = 0;
      }

      String[] p = new Object[numProfiles + 1];
      p[0] = resources.getString(235);

      for (int i = 0; i < numProfiles; i++) {
         Profile profile = (Profile)profiles.getAt(i);
         if (profile.getUID() == profileUID) {
            selectedProfileIndex = i + 1;
         }

         p[i + 1] = profile.getName();
      }

      if (this._override.isFromAddressBook()) {
         this._profileField = (ObjectChoiceField)(new Object(resources.getString(236), p, selectedProfileIndex, 36028797018963968L));
      } else {
         this._profileField = (ObjectChoiceField)(new Object(resources.getString(236), p, selectedProfileIndex, 18014398509481984L));
      }

      this.add(this._profileField);
      boolean useTune = this._override.getUseTune();
      if (this._override.isFromAddressBook()) {
         this._useTuneCheckbox = (CheckboxField)(new Object(resources.getString(237), useTune, 45035996273704960L));
      } else {
         this._useTuneCheckbox = (CheckboxField)(new Object(resources.getString(237), useTune));
      }

      this.add(this._useTuneCheckbox);
      String tuneName = this._override.getTuneName();
      String[] defaultTuneNames = Profiles.getDefaultTuneNames(2868625504212929964L);
      String defaultTune = defaultTuneNames[0];
      this._tuneField = TuneManager.getTuneManager().getTuneChoiceField(null, tuneName, defaultTune, true);
      this.add(this._tuneField);
   }

   @Override
   protected void makeMenu(SystemEnabledMenu menu, int instance) {
      super.makeMenu(menu, instance);
      Field focusField = this.getFieldWithFocus();
      if (focusField instanceof Object) {
         VerbProvider verbProvider = (VerbProvider)focusField;
         menu.add(verbProvider, 0);
      }

      menu.add(this._saveVerb);
   }

   @Override
   protected boolean keyChar(char keyChar, int status, int time) {
      if (keyChar == 27) {
         this._closeVerb.invoke(this._context);
         return true;
      } else {
         return super.keyChar(keyChar, status, time);
      }
   }

   @Override
   public boolean confirm(Verb aVerb, Object contextObject) {
      boolean result = true;
      if (this.isDirty() && this._saveVerb != null) {
         switch (Dialog.ask(1, CommonResource.getString(10003))) {
            case -1:
               result = false;
               break;
            case 1:
               this._saveVerb.invoke(contextObject);
               return false;
         }
      }

      return result;
   }

   private void save(Override override, Object context) {
      String name = this._titleField.getText().trim();
      if (name.length() == 0) {
         ResourceBundle resources = ResourceBundle.getBundle(2384708948246157241L, "net.rim.device.apps.internal.resource.Profiles");
         Status.show(resources.getString(201));
         this._titleField.setFocus();
      } else {
         override.setName(name);
         boolean useTune = this._useTuneCheckbox.getChecked();
         override.setUseTune(useTune);
         String tuneName = this._tuneField.getSelectedTuneName();
         override.setTuneName(tuneName);
         int profileIndex = this._profileField.getSelectedIndex();
         int profileUID = -1;
         if (profileIndex != 0) {
            Profiles profiles = Profiles.getInstance();
            Profile profile = (Profile)profiles.getAt(profileIndex - 1);
            profileUID = profile.getUID();
         }

         override.setProfileUID(profileUID);
         if (this._newOverride) {
            override.setFromContacts(this._fromField._fromContacts);
            Overrides.getInstance().add(override, true);
         } else {
            Overrides.getInstance().updateHashtable(override, this._fromField._fromContacts);
            override.setFromContacts(this._fromField._fromContacts);
         }

         override.ensureContentProtected();
         Overrides overrides = Overrides.getInstance();
         synchronized (overrides) {
            overrides.commitChanges(override, true);
         }

         if (context != override) {
            UiApplication.getUiApplication().popScreen(this);
         }
      }
   }

   @Override
   public boolean onClose() {
      this._closeVerb.invoke(this._context);
      return true;
   }
}
