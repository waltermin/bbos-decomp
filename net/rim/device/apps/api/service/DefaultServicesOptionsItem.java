package net.rim.device.apps.api.service;

import java.util.Hashtable;
import java.util.Vector;
import net.rim.device.api.servicebook.ServiceBook;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.servicebook.selector.SRSelector;
import net.rim.device.api.servicebook.selector.SRSelectorApp;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.component.ObjectChoiceField;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.registration.RIMModelFactory;
import net.rim.device.apps.api.framework.registration.RIMModelFactoryRepository;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.options.SaveableMainScreenOptionsListItem;
import net.rim.device.apps.api.transmission.rim.RIMMessagingService;
import net.rim.device.apps.api.transmission.rim.options.MessageServicesContentTypeOptionsProvider;
import net.rim.device.apps.api.utility.framework.FieldUtility;
import net.rim.vm.Array;

public final class DefaultServicesOptionsItem extends SaveableMainScreenOptionsListItem implements FieldChangeListener {
   private ObjectChoiceField[] _defaultServicesFields;
   private int _defaultServicesFieldCount;
   private Hashtable _allServices;
   private Field[] _contentTypeOptionsProviderFields;
   private Hashtable _contentTypeOptionsProviderHashtable;
   private static ServiceBook _sb;
   private static SRSelector _srs;
   private static final int OPTIONS_PROVIDER_FIELD_INDENT = 12;

   protected final ServiceRecord getServiceRecord(SRSelectorApp srsApp, String defaultService) {
      String cid = srsApp.getCid();

      for (ServiceRecord sr : _sb.findRecordsByName(defaultService)) {
         if (StringUtilities.strEqualIgnoreCase(cid, sr.getCid(), 1701707776)) {
            return sr;
         }
      }

      return null;
   }

   protected final ServiceRecord getSelectedServiceRecord(ObjectChoiceField defaultChoiceField) {
      SRSelectorApp srsApp = (SRSelectorApp)defaultChoiceField.getCookie();
      String defaultService = (String)defaultChoiceField.getChoice(defaultChoiceField.getSelectedIndex());
      return this.getServiceRecord(srsApp, defaultService);
   }

   @Override
   public final void fieldChanged(Field field, int context) {
      Object fieldCookie = field.getCookie();
      if (fieldCookie instanceof Object) {
         SRSelectorApp srsApp = (SRSelectorApp)fieldCookie;
         String contentType = StringUtilities.toUpperCase(srsApp.getCid(), 1701707776);
         MessageServicesContentTypeOptionsProvider[] optionsProviders = (MessageServicesContentTypeOptionsProvider[])this._contentTypeOptionsProviderHashtable
            .get(contentType);
         int numOptionsProviders = optionsProviders != null ? optionsProviders.length : 0;

         for (int i = 0; i < numOptionsProviders; i++) {
            optionsProviders[i].serviceRecordChanged(this.getSelectedServiceRecord((ObjectChoiceField)field));
         }
      }
   }

   @Override
   protected final void populateMainScreen(MainScreen mainScreen) {
      this.populateServices();
      Vector srsAppVector = _srs.getRegisteredApps();
      SRSelectorApp[] srsApps = new Object[srsAppVector.size()];
      srsAppVector.copyInto(srsApps);
      Arrays.sort(srsApps, new DefaultServicesOptionsItem$SRSelectorAppComparator());
      this._defaultServicesFields = new Object[srsApps.length];
      this._defaultServicesFieldCount = 0;
      this._contentTypeOptionsProviderFields = new Object[0];
      this._contentTypeOptionsProviderHashtable = (Hashtable)(new Object());
      if (srsApps.length == 0) {
         mainScreen.add((Field)(new Object(RIMMessagingService.getResourceString(5), 12884901888L)));
      } else {
         for (int i = 0; i < srsApps.length; i++) {
            this.addDefaultServiceField(mainScreen, srsApps[i]);
         }
      }
   }

   private final void addDefaultServiceField(MainScreen mainScreen, SRSelectorApp srsApp) {
      String contentType = StringUtilities.toUpperCase(srsApp.getCid(), 1701707776);
      String[] services = (Object[])this._allServices.get(contentType);
      if (services != null) {
         StringBuffer labelBuffer = (StringBuffer)(new Object(srsApp.getName()));
         labelBuffer.append(' ');
         labelBuffer.append('(');
         labelBuffer.append(contentType);
         labelBuffer.append(')');
         labelBuffer.append(':');
         labelBuffer.append(' ');
         ObjectChoiceField field = (ObjectChoiceField)(new Object(labelBuffer.toString(), services));
         field.setCookie(srsApp);
         this.setDefaultService(field);
         this._defaultServicesFields[this._defaultServicesFieldCount++] = field;
         mainScreen.add(field);
         ServiceRecord selectedServiceRecord = this.getSelectedServiceRecord(field);
         RIMModelFactory[] factories = RIMModelFactoryRepository.getModelFactories(-1203249910507515082L);
         int numFactories = factories != null ? factories.length : 0;
         MessageServicesContentTypeOptionsProvider[] optionsProviders = new MessageServicesContentTypeOptionsProvider[0];
         ContextObject optionsCreationContext = (ContextObject)(new Object());
         ContextObject.put(optionsCreationContext, 253, contentType);
         ContextObject.put(optionsCreationContext, 250, selectedServiceRecord);

         for (int i = 0; i < numFactories; i++) {
            MessageServicesContentTypeOptionsProvider optionsProvider = (MessageServicesContentTypeOptionsProvider)factories[i]
               .createInstance(optionsCreationContext);
            if (optionsProvider != null) {
               Arrays.add(optionsProviders, optionsProvider);
            }
         }

         this._contentTypeOptionsProviderHashtable.put(contentType, optionsProviders);
         int numOptionsProviders = optionsProviders.length;
         if (numOptionsProviders > 0) {
            VerticalFieldManager subFieldManager = (VerticalFieldManager)(new Object());
            Vector fields = FieldUtility.sortFieldsByOrder(optionsProviders, null);
            int numFields = fields.size();

            for (int i = 0; i < numFields; i++) {
               Field currentSubField = (Field)fields.elementAt(i);
               subFieldManager.add(currentSubField);
               Arrays.add(this._contentTypeOptionsProviderFields, currentSubField);
            }

            HorizontalFieldManager indentManager = (HorizontalFieldManager)(new Object());
            indentManager.add((Field)(new Object(12)));
            indentManager.add(subFieldManager);
            mainScreen.add(indentManager);
            field.setChangeListener(this);
         }
      }
   }

   @Override
   protected final void initialize() {
      super.initialize();
      if (_sb == null) {
         _sb = ServiceBook.getSB();
      }

      if (_srs == null) {
         _srs = SRSelector.getInstance();
      }

      if (this._allServices == null) {
         this._allServices = (Hashtable)(new Object());
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final void setDefaultService(ObjectChoiceField field) {
      SRSelectorApp srsApp = (SRSelectorApp)field.getCookie();
      String[] services = (Object[])this._allServices.get(StringUtilities.toUpperCase(srsApp.getCid(), 1701707776));
      ServiceRecord defaultService = null;
      boolean var7 = false /* VF: Semaphore variable */;

      try {
         var7 = true;
         defaultService = _sb.getRecordById(_srs.getDefault(srsApp.getGuid(), srsApp.getCid()));
         var7 = false;
      } finally {
         if (var7) {
            return;
         }
      }

      if (defaultService != null && defaultService.getType() == 0) {
         for (int i = 0; i < services.length; i++) {
            if (services[i].equals(defaultService.getName())) {
               field.setSelectedIndex(i);
            }
         }
      }
   }

   private final void populateServices() {
      this._allServices.clear();

      for (ServiceRecord sr : _sb.findRecordsByType(0)) {
         String contentType = StringUtilities.toUpperCase(sr.getCid(), 1701707776);
         String[] choices = (Object[])this._allServices.get(contentType);
         if (choices == null) {
            choices = new Object[0];
            this._allServices.put(contentType, choices);
         }

         Array.resize(choices, choices.length + 1);
         choices[choices.length - 1] = sr.getName();
      }
   }

   @Override
   protected final boolean save() {
      int numDefaultServicesFields = this._defaultServicesFields.length;

      for (int i = 0; i < numDefaultServicesFields; i++) {
         ObjectChoiceField field = this._defaultServicesFields[i];
         if (field != null) {
            ServiceRecord serviceRecord = this.getSelectedServiceRecord(field);
            if (serviceRecord != null) {
               try {
                  SRSelectorApp srsApp = (SRSelectorApp)field.getCookie();
                  _srs.setDefault(srsApp, serviceRecord.getId(), true);
               } finally {
                  continue;
               }
            }
         }
      }

      int numContentTypeOptionsProviderFields = this._contentTypeOptionsProviderFields.length;

      for (int i = 0; i < numContentTypeOptionsProviderFields; i++) {
         Field currentField = this._contentTypeOptionsProviderFields[i];
         MessageServicesContentTypeOptionsProvider optionsProvider = (MessageServicesContentTypeOptionsProvider)currentField.getCookie();
         optionsProvider.grabDataFromField(currentField, null);
      }

      return super.save();
   }

   @Override
   protected final Verb getSaveVerb() {
      return this._defaultServicesFields.length > 0 ? super.getSaveVerb() : null;
   }

   public DefaultServicesOptionsItem() {
      super(RIMMessagingService.getResourceString(10), -1514481539159318190L);
   }
}
