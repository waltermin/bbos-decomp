package net.rim.device.apps.internal.blackberryemail.email.emailsetting;

import net.rim.device.api.collection.Collection;
import net.rim.device.api.collection.CollectionListener;
import net.rim.device.api.servicebook.ServiceBook;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.ObjectChoiceField;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.Factory;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.FieldProvider;
import net.rim.device.apps.api.messaging.implus.IMPlusServiceModel;
import net.rim.device.apps.api.options.SaveableMainScreenOptionsListItem;
import net.rim.device.apps.api.transmission.rim.otasync.OTAFMConfiguration;
import net.rim.device.apps.api.transmission.rim.otasync.OTAFMConfigurationManager$Instance;
import net.rim.device.apps.api.utility.framework.VerbToMenu;
import net.rim.device.apps.internal.blackberryemail.otasync.OTAFMConfigurationManagerImpl;
import net.rim.device.apps.internal.blackberryemail.resources.EmailResources;
import net.rim.vm.PersistentInteger;
import net.rim.vm.WeakReference;

public final class EmailSettingOptionsScreen extends SaveableMainScreenOptionsListItem implements FieldChangeListener, CollectionListener {
   private int _id;
   private ObjectChoiceField _service;
   MainScreen _mainScreen;
   private WeakReference _weakReference = (WeakReference)(new Object(this));
   private static final long ID;

   final Field addIMPlusOption(int contextFlag, EmailSettingOptionsScreen$ServiceInfo serviceInfo, int indentSize) {
      Factory factory = (Factory)ApplicationRegistry.getApplicationRegistry().get(2620647646956286337L);
      if (factory != null) {
         ContextObject context = (ContextObject)(new Object(contextFlag));
         context.putIntegerData(serviceInfo.getSR().getId());
         Object model = factory.createInstance(context);
         if (model instanceof Object) {
            Field field = ((FieldProvider)model).getField(null);
            if (field != null) {
               if (indentSize > 0) {
                  field.setPadding(0, 0, 0, indentSize);
               }

               serviceInfo._vfm.add(field);
            }

            return field;
         }
      }

      return null;
   }

   @Override
   public final void fieldChanged(Field field, int context) {
      if (field == this._service) {
         this.populateConfiguration();
      }
   }

   @Override
   public final void reset(Collection collection) {
      this.exitLater();
   }

   @Override
   public final void elementAdded(Collection collection, Object element) {
   }

   @Override
   public final void elementRemoved(Collection collection, Object element) {
   }

   @Override
   public final void elementUpdated(Collection collection, Object oldElement, Object newElement) {
      this.exitLater();
   }

   private final boolean saveConfiguration() {
      for (int i = this._service.getSize() - 1; i >= 0; i--) {
         EmailSettingOptionsScreen$ServiceInfo info = (EmailSettingOptionsScreen$ServiceInfo)this._service.getChoice(i);
         if (info != null) {
            EmailSettingCollectionImpl.getInstance(String.valueOf(info.getSR().getUserId())).removeCollectionListener(this._weakReference);
            if (info._emailSettingFields != null) {
               EmailSettingModelImpl model = (EmailSettingModelImpl)info._emailSettingFields.getCookie();
               if (model.grabDataFromField(info._emailSettingFields)) {
                  if (!this.validateModel(model)) {
                     return false;
                  }

                  model.updateConfig();
               }
            }

            if (info._vfm != null) {
               for (int j = info._vfm.getFieldCount() - 1; j >= 0; j--) {
                  Field field = info._vfm.getField(j);
                  if (field != null && field.getCookie() instanceof Object && !((FieldProvider)field.getCookie()).grabDataFromField(field, null)) {
                     return false;
                  }
               }
            }
         }
      }

      return true;
   }

   private final boolean validateModel(EmailSettingModelImpl model) {
      return true;
   }

   private final void populateService() {
      ServiceBook serviceBook = ServiceBook.getSB();
      ServiceRecord[] emailSettingsSRs = serviceBook.findRecordsByCid("SYNC");
      IMPlusServiceModel implusService = (IMPlusServiceModel)ApplicationRegistry.getApplicationRegistry().get(-2205884509140292945L);
      if (implusService != null) {
         int[] implusServiceRecIds = implusService.getReceiptCapableServiceRecIds();

         for (int j = implusServiceRecIds.length - 1; j >= 0; j--) {
            boolean foundMatch = false;

            for (int k = emailSettingsSRs.length - 1; k >= 0; k--) {
               if (implusServiceRecIds[j] == emailSettingsSRs[k].getId()) {
                  foundMatch = true;
                  break;
               }
            }

            if (!foundMatch) {
               Arrays.add(emailSettingsSRs, serviceBook.getRecordById(implusServiceRecIds[j]));
            }
         }
      }

      Object[] choices = new Object[emailSettingsSRs.length];
      int index = 0;

      for (int i = 0; i < emailSettingsSRs.length; i++) {
         choices[index++] = new EmailSettingOptionsScreen$ServiceInfo(emailSettingsSRs[i]);
      }

      int id = PersistentInteger.get(this._id);
      if (id < 0 || id >= choices.length) {
         id = 0;
      }

      if (choices.length > 0) {
         this._service.setChoices(choices);
         this._service.setSelectedIndex(id);
      }
   }

   private final void populateConfiguration() {
      int selectedServiceIndex = this._service.getSelectedIndex();
      if (selectedServiceIndex >= 0) {
         PersistentInteger.set(this._id, selectedServiceIndex);
      }

      EmailSettingOptionsScreen$ServiceInfo info = this.getSelectedServiceInfo();
      if (info != null) {
         this._mainScreen.deleteAll();
         int indentSize = 0;
         if (this._service.getSize() > 1) {
            this._mainScreen.add(this._service);
            indentSize = this._service.getFont().measureText(" ", 0, 1, null, null) * 2;
         }

         ServiceRecord serviceRecord = info.getSR();
         if (StringUtilities.strEqualIgnoreCase(serviceRecord.getCid(), "SYNC", 1701707776)) {
            if (info._emailSettingFields == null) {
               String userId = String.valueOf(serviceRecord.getUserId());
               EmailSettingModelImpl currentModel = (EmailSettingModelImpl)EmailSettingCollectionImpl.getInstance(userId).getAt(0);
               EmailSettingModelImpl newModel = new EmailSettingModelImpl(currentModel);
               newModel.setId(userId);
               Field emailSettingFields = newModel.getEditableField();
               if (emailSettingFields != null) {
                  emailSettingFields.setCookie(newModel);
                  info._emailSettingFields = emailSettingFields;
                  if (indentSize > 0) {
                     emailSettingFields.setPadding(0, 0, 0, indentSize);
                  }
               }

               EmailSettingCollectionImpl.getInstance(userId).addCollectionListener(this._weakReference);
            }

            if (info._emailSettingFields != null) {
               this._mainScreen.add(info._emailSettingFields);
            }
         }

         if (info._vfm == null) {
            info._vfm = (VerticalFieldManager)(new Object());
            this.addIMPlusOption(75, info, indentSize);
            this.addIMPlusOption(0, info, indentSize);
            this.addIMPlusOption(73, info, indentSize);
         }

         if (info._vfm.getFieldCount() > 0) {
            this._mainScreen.add(info._vfm);
         }
      }
   }

   private EmailSettingOptionsScreen() {
      super(EmailResources.getResourceBundle(), 159);
   }

   public static final EmailSettingOptionsScreen getInstance() {
      ApplicationRegistry applicationRegistry = ApplicationRegistry.getApplicationRegistry();
      EmailSettingOptionsScreen instance = (EmailSettingOptionsScreen)applicationRegistry.get(656705520964886472L);
      if (instance == null) {
         instance = new EmailSettingOptionsScreen();
         applicationRegistry.put(656705520964886472L, instance);
      }

      return instance;
   }

   @Override
   protected final void populateMainScreen(MainScreen screen) {
      screen.setTitle(EmailResources.getString(159));
      this._id = PersistentInteger.getId(656705520964886472L, 0);
      this._service = (ObjectChoiceField)(new Object(EmailResources.getString(161), null));
      this._mainScreen = screen;
      this.populateService();
      if (this._service.getSize() != 0) {
         if (this._service.getSize() > 1) {
            this._service.setChangeListener(this);
         }

         this.populateConfiguration();
      }
   }

   @Override
   protected final boolean save() {
      return !this.saveConfiguration() ? false : super.save();
   }

   private final EmailSettingOptionsScreen$ServiceInfo getSelectedServiceInfo() {
      int selectedServiceIndex = this._service.getSelectedIndex();
      return selectedServiceIndex < 0 ? null : (EmailSettingOptionsScreen$ServiceInfo)this._service.getChoice(selectedServiceIndex);
   }

   private final void exitLater() {
      for (int i = this._service.getSize() - 1; i >= 0; i--) {
         EmailSettingOptionsScreen$ServiceInfo info = (EmailSettingOptionsScreen$ServiceInfo)this._service.getChoice(i);
         if (info != null) {
            EmailSettingCollectionImpl.getInstance(String.valueOf(info.getSR().getUserId())).removeCollectionListener(this._weakReference);
         }
      }

      if (this._mainScreen.isDisplayed()) {
         this._mainScreen.getApplication().invokeLater(new EmailSettingOptionsScreen$1(this));
      }
   }

   private final void forceExit() {
      Dialog.alert(EmailResources.getString(1005));
      UiApplication.getUiApplication().popScreen(UiApplication.getUiApplication().getActiveScreen());
   }

   @Override
   protected final void addScreenVerbs(VerbToMenu verbToMenu, int instance) {
      if (this.isWirelessFilingEnabled()) {
         verbToMenu.addVerb(new EmailSettingOptionsScreen$FolderRedirectionVerb(this, 602464));
      }

      super.addScreenVerbs(verbToMenu, instance);
   }

   private final boolean isWirelessFilingEnabled() {
      boolean available = false;
      EmailSettingOptionsScreen$ServiceInfo info = this.getSelectedServiceInfo();
      if (info != null) {
         OTAFMConfigurationManagerImpl manager = (OTAFMConfigurationManagerImpl)OTAFMConfigurationManager$Instance.getInstance();
         ServiceRecord syncServiceRecord = info.getSR();
         if (syncServiceRecord != null) {
            ServiceBook serviceBook = ServiceBook.getSB();
            ServiceRecord cmimeServiceRecord;
            if (syncServiceRecord.getUserId() != -1) {
               cmimeServiceRecord = serviceBook.getRecordByCidAndUserId("CMIME", syncServiceRecord.getUserId());
            } else {
               cmimeServiceRecord = serviceBook.getRecordByUidAndCid(syncServiceRecord.getUid(), "CMIME");
            }

            OTAFMConfiguration config = manager.getConfiguration(cmimeServiceRecord);
            if (config != null) {
               available = config.getWirelessReconcileEnabled();
            }
         }
      }

      return available;
   }
}
