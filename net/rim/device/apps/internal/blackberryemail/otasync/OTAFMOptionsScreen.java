package net.rim.device.apps.internal.blackberryemail.otasync;

import net.rim.device.api.servicebook.ServiceBook;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.RIMGlobalMessagePoster;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.ObjectChoiceField;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.api.framework.registration.VerbFactory;
import net.rim.device.apps.api.framework.registration.VerbFactoryRepository;
import net.rim.device.apps.api.messaging.resources.MessageResources;
import net.rim.device.apps.api.options.SaveableMainScreenOptionsListItem;
import net.rim.device.apps.api.transmission.rim.otasync.OTAFMConfiguration;
import net.rim.device.apps.api.transmission.rim.otasync.OTAFMConfigurationManager;
import net.rim.device.apps.api.transmission.rim.otasync.OTAFMConfigurationManager$Instance;
import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.apps.api.utility.framework.VerbToMenu;
import net.rim.device.apps.internal.blackberryemail.resources.EmailResources;
import net.rim.vm.PersistentInteger;

public final class OTAFMOptionsScreen extends SaveableMainScreenOptionsListItem implements FieldChangeListener {
   private int _id;
   private ObjectChoiceField _service;
   private MainScreen _mainScreen;
   private OTAFMConfigurationManager _otafmConfigManager;
   public static final long OTAFM_OPTIONS_FACTORY_REPOSITORY_GUID = 4950224479719224677L;
   private static final long ID = 4431471039491245661L;
   private static final int CONTINUE = 0;

   private OTAFMOptionsScreen() {
      super(EmailResources.getResourceBundle(), 198);
   }

   @Override
   protected final void populateMainScreen(MainScreen screen) {
      screen.setTitle(EmailResources.getString(198));
      this._id = PersistentInteger.getId(4431471039491245661L, 0);
      this._service = new OTAFMOptionsScreen$ServiceField(EmailResources.getString(161), null);
      this._mainScreen = screen;
      this._otafmConfigManager = OTAFMConfigurationManager$Instance.getInstance();
      this.populateService();
      if (this._service.getSize() != 0) {
         if (this._service.getSize() > 1) {
            this._service.setChangeListener(this);
         }

         this.populateConfiguration();
      }
   }

   public static final OTAFMOptionsScreen getInstance() {
      ApplicationRegistry applicationRegistry = ApplicationRegistry.getApplicationRegistry();
      OTAFMOptionsScreen instance = (OTAFMOptionsScreen)applicationRegistry.get(4431471039491245661L);
      if (instance == null) {
         instance = new OTAFMOptionsScreen();
         applicationRegistry.put(4431471039491245661L, instance);
      }

      return instance;
   }

   private final void populateService() {
      ServiceBook serviceBook = ServiceBook.getSB();
      ServiceRecord[] srCMIME = serviceBook.findRecordsByCid("CMIME");
      if (srCMIME.length != 0) {
         Object[] choices = new Object[srCMIME.length];
         int index = 0;

         for (int i = 0; i < srCMIME.length; i++) {
            choices[index++] = new OTAFMOptionsScreen$ServiceInfo(srCMIME[i]);
         }

         int id = PersistentInteger.get(this._id);
         this._service.setChoices(choices);
         if (id > 0 && id < choices.length) {
            this._service.setSelectedIndex(id);
         }
      }
   }

   private final void populateConfiguration() {
      int choice = this._service.getSelectedIndex();
      PersistentInteger.set(this._id, choice);
      OTAFMOptionsScreen$ServiceInfo info = (OTAFMOptionsScreen$ServiceInfo)this._service.getChoice(choice);
      this._mainScreen.deleteAll();
      int indentSize = 0;
      if (this._service.getSize() > 1) {
         this._mainScreen.add(this._service);
         indentSize = this._service.getFont().measureText(" ", 0, 1, null, null) * 2;
      }

      if (StringUtilities.strEqualIgnoreCase(info.getSR().getCid(), "CMIME", 1701707776)) {
         if (info._otafmFields == null) {
            OTAFMConfiguration configuration = this._otafmConfigManager.getConfiguration(info.getSR());
            info._otafmFields = configuration.getEditableField(info.getSR());
            info._otafmFields.setCookie(info.getSR());
         }

         if (info._otafmFields != null) {
            if (indentSize > 0) {
               info._otafmFields.setPadding(0, 0, 0, indentSize);
            }

            this._mainScreen.add(info._otafmFields);
         }
      }
   }

   @Override
   protected final void addScreenVerbs(VerbToMenu menuVerbs, int instance) {
      VerbFactory[] verbFactories = VerbFactoryRepository.getVerbFactories(4950224479719224677L);
      if (verbFactories != null) {
         for (int i = verbFactories.length - 1; i >= 0; i--) {
            menuVerbs.addVerbs(verbFactories[i].getVerbs(null));
         }
      }

      super.addScreenVerbs(menuVerbs, instance);
   }

   @Override
   protected final boolean save() {
      return !this.saveConfiguration() ? false : super.save();
   }

   private final boolean saveConfiguration() {
      boolean userChoseToContinue = false;

      for (int i = this._service.getSize() - 1; i >= 0; i--) {
         OTAFMOptionsScreen$ServiceInfo info = (OTAFMOptionsScreen$ServiceInfo)this._service.getChoice(i);
         if (info != null && info._otafmFields != null) {
            ServiceRecord sr = (ServiceRecord)info._otafmFields.getCookie();
            OTAFMConfiguration configuration = this._otafmConfigManager.getConfiguration(sr).clone();
            if (configuration.grabDataFromField(info._otafmFields, sr)) {
               if (!configuration.validate(sr)) {
                  Dialog.alert(sr.getName() + MessageResources.getString(176));
                  return false;
               }

               OTAFMConfiguration originalConfiguration = this._otafmConfigManager.getConfiguration(sr);
               if (originalConfiguration.getWirelessReconcileEnabled() != configuration.getWirelessReconcileEnabled()
                  && !configuration.getWirelessReconcileEnabled()
                  && !userChoseToContinue) {
                  switch (Dialog.ask(
                     EmailResources.getString(1011),
                     new String[]{CommonResources.getString(800), CommonResources.getString(9042)},
                     new int[]{0, -1, 51, 1866989824, 727916, -1569758719, 1661010020, 529165580},
                     0
                  )) {
                     case -2:
                        break;
                     case -1:
                        return false;
                     case 0:
                     default:
                        userChoseToContinue = true;
                  }
               }

               this._otafmConfigManager.updateConfiguration(sr, configuration, false);
            }
         }
      }

      return true;
   }

   @Override
   public final void fieldChanged(Field field, int context) {
      if (field == this._service) {
         this.populateConfiguration();
      }
   }

   @Override
   public final boolean openDevelopmentBackdoor(int backdoorCode) {
      switch (backdoorCode) {
         case 1330921804:
            RIMGlobalMessagePoster.postGlobalEvent(-2728804572467266389L);
            return true;
         case 1330922310:
            RIMGlobalMessagePoster.postGlobalEvent(-2728804572467266390L);
            return true;
         case 1330923084:
            RIMGlobalMessagePoster.postGlobalEvent(-2728804572467266388L);
            return true;
         case 1330923860:
            RIMGlobalMessagePoster.postGlobalEvent(-2728804572467266382L);
            return true;
         case 1330924876:
            RIMGlobalMessagePoster.postGlobalEvent(-2728804572467266387L);
            return true;
         default:
            return super.openDevelopmentBackdoor(backdoorCode);
      }
   }

   @Override
   public final boolean openProductionBackdoor(int backdoorCode) {
      switch (backdoorCode) {
         case 1330922565:
            RIMGlobalMessagePoster.postGlobalEvent(-2728804572467266383L);
            return true;
         case 1330922583:
            RIMGlobalMessagePoster.postGlobalEvent(-2728804572467266384L);
            return true;
         default:
            return super.openProductionBackdoor(backdoorCode);
      }
   }
}
