package net.rim.device.apps.api.options;

import net.rim.device.api.i18n.ResourceBundleFamily;
import net.rim.device.api.system.BackdoorKeyListener;
import net.rim.device.api.system.BackdoorKeyProcessor;
import net.rim.device.api.system.KeyListener;
import net.rim.device.api.system.StylusListener;
import net.rim.device.api.system.TrackwheelListener;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.ObjectListField;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.util.LongHashtable;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.framework.model.VerbProvider;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.ui.Confirmation;
import net.rim.device.apps.api.ui.ExitVerb;
import net.rim.device.apps.api.ui.SystemEnabledMenu;
import net.rim.device.apps.api.utility.framework.VerbToMenu;
import net.rim.device.apps.api.utility.framework.VerbToMenuFactory;
import net.rim.device.internal.i18n.CommonResource;

public class MainScreenOptionsListItem extends OptionsListItem implements KeyListener, TrackwheelListener, StylusListener, Confirmation, BackdoorKeyListener {
   protected Object _context;
   protected MainScreen _mainScreen;
   protected Verb _closeVerb;
   protected Verb _saveVerb;
   private BackdoorKeyProcessor _backdoor;

   protected boolean handleEndKey() {
      return false;
   }

   protected MainScreen createMainScreen() {
      MainScreen mainScreen = new OptionsMainScreen(this, 17592186044416L);
      mainScreen.setTitle(this.getTitleField());
      this.populateMainScreen(mainScreen);
      mainScreen.addStylusListener(this);
      mainScreen.addTrackwheelListener(this);
      mainScreen.addKeyListener(this);
      return mainScreen;
   }

   protected Field getTitleField() {
      return (Field)(new Object(this.getDisplayName(), 64));
   }

   protected void populateMainScreen(MainScreen _1) {
      throw null;
   }

   protected void populateMenuVerbs(VerbToMenu verbToMenu, int instance) {
      this._saveVerb = this.getSaveVerb();
      this._closeVerb = this.getCloseVerb();
      this.addScreenVerbs(verbToMenu);
      this.addScreenVerbs(verbToMenu, instance);
      this.addRepositoryVerbs(verbToMenu, instance);
      this.addCurrentItemVerbs(verbToMenu);
      Verb defaultVerb = this.addCurrentItemVerbs(verbToMenu, instance);
      if ((this._mainScreen.isMuddy() || this._mainScreen.isDirty()) && this._saveVerb != null) {
         verbToMenu.setDefaultVerb(this._saveVerb);
         if (this._mainScreen.isDirty()) {
            verbToMenu.setDefaultVerbPriority(150);
            return;
         }
      } else {
         if (defaultVerb != null) {
            verbToMenu.setDefaultVerb(defaultVerb);
            return;
         }

         if (verbToMenu.getDefaultVerb() == null) {
            verbToMenu.setDefaultVerb(this._closeVerb);
            verbToMenu.setDefaultVerbPriority(150);
         }
      }
   }

   protected void addScreenVerbs(VerbToMenu verbToMenu) {
   }

   protected void addScreenVerbs(VerbToMenu verbToMenu, int instance) {
      if (this._closeVerb != null) {
         verbToMenu.addVerb(this._closeVerb);
      }

      if (this._saveVerb != null) {
         verbToMenu.addVerb(this._saveVerb);
      }
   }

   protected void addRepositoryVerbs(VerbToMenu verbToMenu, int instance) {
   }

   protected Verb addCurrentItemVerbs(VerbToMenu verbToMenu) {
      return null;
   }

   protected Verb addCurrentItemVerbs(VerbToMenu verbToMenu, int instance) {
      Field field = UiApplication.getUiApplication().getActiveScreen().getLeafFieldWithFocus();
      if (field == null) {
         return null;
      }

      if (this._context instanceof Object) {
         ((LongHashtable)this._context).put(9045827404276417370L, field);
      }

      RIMModel model = null;
      Object cookie = field.getCookie();
      if (!(cookie instanceof Object)) {
         if (field instanceof Object) {
            ObjectListField olf = (ObjectListField)field;
            int i = olf.getSelectedIndex();
            if (i != -1) {
               Object o = olf.get(olf, i);
               if (o instanceof Object) {
                  model = (RIMModel)o;
               }
            }
         }
      } else {
         model = (RIMModel)cookie;
      }

      if (!(model instanceof Object)) {
         return null;
      }

      VerbProvider verbProvider = (VerbProvider)model;
      Verb[] itemVerbs = new Object[0];
      Verb defaultVerb = verbProvider.getVerbs(this._context, itemVerbs);
      verbToMenu.addVerbs(itemVerbs);
      return defaultVerb;
   }

   public Object getContext() {
      return this._context;
   }

   protected boolean save() {
      this._mainScreen.setDirty(false);
      return true;
   }

   protected boolean discard() {
      this._mainScreen.setDirty(false);
      return true;
   }

   protected Verb getSaveVerb() {
      return null;
   }

   protected Verb getCloseVerb() {
      return ExitVerb.createCloseVerb(0, this);
   }

   protected boolean doCloseVerb() {
      this._closeVerb = this.getCloseVerb();
      if (this._closeVerb != null && this._closeVerb.invoke(null) != null) {
         this._mainScreen = null;
         return true;
      } else {
         return false;
      }
   }

   protected boolean invokeOptionsAction(int action) {
      return false;
   }

   void makeOptionsMenu(SystemEnabledMenu menu, int instance) {
      VerbToMenu verbToMenu = VerbToMenuFactory.createInstance();
      this.populateMenuVerbs(verbToMenu, instance);
      menu.add(verbToMenu.getVerbs());
      Verb defaultVerb = verbToMenu.getDefaultVerb();
      if (defaultVerb != null) {
         menu.setVerbPriority(defaultVerb, verbToMenu.getDefaultVerbPriority());
      }
   }

   protected void setBackdoorAltStatus(boolean altStatus) {
      this._backdoor.setAltStatus(altStatus);
   }

   @Override
   public boolean stylusDown(int x, int y, int status, int time) {
      return false;
   }

   @Override
   public boolean stylusUp(int x, int y, int status, int time) {
      return false;
   }

   @Override
   public boolean stylusDrag(int x, int y, int status, int time) {
      return false;
   }

   @Override
   public boolean stylusTap(int x, int y, int status, int time) {
      return false;
   }

   @Override
   public boolean stylusDoubleTap(int x, int y, int status, int time) {
      return false;
   }

   @Override
   public boolean stylusTapHold(int x, int y, int status, int time) {
      return false;
   }

   @Override
   public boolean keyChar(char key, int time, int status) {
      if (key == 27) {
         this.doCloseVerb();
         return true;
      } else {
         return false;
      }
   }

   @Override
   public boolean trackwheelRoll(int amount, int status, int time) {
      return false;
   }

   @Override
   public boolean keyDown(int keycode, int time) {
      return this._backdoor.keyDown(keycode);
   }

   @Override
   public boolean openDevelopmentBackdoor(int backdoorCode) {
      return false;
   }

   @Override
   public boolean openProductionBackdoor(int backdoorCode) {
      return false;
   }

   @Override
   public boolean keyUp(int keycode, int time) {
      return false;
   }

   @Override
   public boolean keyRepeat(int keycode, int time) {
      return false;
   }

   @Override
   public boolean keyStatus(int keycode, int time) {
      return false;
   }

   @Override
   public boolean confirm(Verb verb, Object context) {
      if (verb == this._saveVerb) {
         return this.save();
      }

      if (this._mainScreen.isDirty()) {
         switch (Dialog.ask(1, CommonResource.getString(10003))) {
            case 0:
               return false;
            case 1:
            default:
               return this.save();
            case 2:
               return this.discard();
         }
      } else {
         return true;
      }
   }

   @Override
   public boolean trackwheelUnclick(int status, int time) {
      return false;
   }

   @Override
   public boolean trackwheelClick(int status, int time) {
      return false;
   }

   public MainScreenOptionsListItem(String displayString, Object context) {
      super(displayString);
      this._context = context;
      this._backdoor = (BackdoorKeyProcessor)(new Object(false, this));
   }

   @Override
   protected void setDisplayName(String displayName) {
      super.setDisplayName(displayName);
      if (this._mainScreen != null) {
         this._mainScreen.setTitle(this.getTitleField());
      }
   }

   public MainScreenOptionsListItem(ResourceBundleFamily rb, int key, Object context, long group) {
      super(rb, key, group);
      this._context = context;
      this._backdoor = (BackdoorKeyProcessor)(new Object(false, this));
   }

   @Override
   protected void open() {
      this._mainScreen = this.createMainScreen();
      UiApplication.getUiApplication().pushScreen(this._mainScreen);
   }

   public MainScreenOptionsListItem(ResourceBundleFamily rb, int key, Object context) {
      super(rb, key);
      this._context = context;
      this._backdoor = (BackdoorKeyProcessor)(new Object(false, this));
   }

   public MainScreenOptionsListItem(String displayString, Object context, long group) {
      super(displayString, group);
      this._context = context;
      this._backdoor = (BackdoorKeyProcessor)(new Object(false, this));
   }
}
