package net.rim.device.apps.internal.keystore.browser;

import java.util.Vector;
import net.rim.device.api.collection.Collection;
import net.rim.device.api.collection.CollectionListener;
import net.rim.device.api.crypto.CryptoSystemProperties;
import net.rim.device.api.crypto.certificate.Certificate;
import net.rim.device.api.crypto.certificate.CertificateStatusProviderFacade;
import net.rim.device.api.crypto.certificate.CertificateUtilities;
import net.rim.device.api.crypto.keystore.CertificateStatusManager;
import net.rim.device.api.crypto.keystore.CertificateStatusManagerTicket;
import net.rim.device.api.crypto.keystore.KeyStore;
import net.rim.device.api.crypto.keystore.KeyStoreData;
import net.rim.device.api.crypto.keystore.KeyStoreTicket;
import net.rim.device.api.crypto.keystore.TrustedKeyStore;
import net.rim.device.api.memorycleaner.MemoryCleanerDaemon;
import net.rim.device.api.memorycleaner.MemoryCleanerListener;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.Comparator;
import net.rim.device.api.util.Factory;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.VerbProvider;
import net.rim.device.apps.api.framework.registration.VerbRepository;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.options.MainScreenOptionsListItem;
import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.apps.api.utility.framework.VerbToMenu;
import net.rim.device.apps.internal.api.crypto.certificate.CertificateAttachmentModelFactory;
import net.rim.device.apps.internal.api.crypto.certificate.SendCertificatesVerb;
import net.rim.device.apps.internal.api.crypto.verb.DisplayCertificateChainVerb;
import net.rim.device.apps.internal.api.crypto.verb.DisplayCertificateVerb;
import net.rim.device.internal.resource.crypto.CryptoIndicatorImages;
import net.rim.vm.Array;

final class KeyStoreBrowserOptionsItem extends MainScreenOptionsListItem implements CollectionListener, MemoryCleanerListener, Comparator {
   private KeyStore _keyStore;
   private TrustedKeyStore _trustedKeyStore;
   private KeyStoreBrowserContext _browserContext;
   private Object _displayContext;
   private Vector _keyStoreItems;
   private int[] _displayedIndices;
   private Object _keyStoreItemsSyncObject;
   private UiApplication _app;
   private CertificateStatusManager _certificateStatusManager;
   private CryptoSystemProperties _cryptoSystemProperties;
   private CertificateStatusManagerTicket _managerTicket;
   private KeyStoreTicket _keyStoreTicket;
   private KeyStoreTicket _trustedKeyStoreTicket;
   private ListField _listField;
   private BrowserListFieldCallBack _listFieldCallBack;
   private BrowserTitleField _titleField;
   private Factory _emailAddressFactory;
   private int _epoch;
   private int _epochType;
   private Object _epochSyncObject;
   private Verb _showMyCertsVerb;
   private Verb _showOthersVerb;
   private Verb _showCASVerb;
   private Verb _showRootsVerb;
   private Verb _showEndEntityVerb;
   private Verb _showAllVerb;
   private DisplayCertificateVerb _displayCertificateVerb;
   private DisplayCertificateChainVerb _displayCertificateChainVerb;
   private MoveTrustVerb _trustVerb;
   private MoveTrustVerb _distrustVerb;
   private SendCertificatesVerb _emailCertificateVerb;
   private SendCertificatesVerb _pinCertificateVerb;
   private KeyStoreBrowserVerb _deleteVerb;
   private KeyStoreBrowserVerb _revokeVerb;
   private KeyStoreBrowserVerb _cancelHoldVerb;
   private KeyStoreBrowserVerb _fetchStatusVerb;
   private KeyStoreBrowserVerb _changeLabelVerb;
   private KeyStoreBrowserVerb _changeSecurityLevelVerb;
   private KeyStoreBrowserVerb _associateAddressesVerb;
   private KeyStoreBrowserVerb _continueVerb;
   private int _showType = 5;
   private Object _reloadCounterLock = new Object();
   private int _reloadCounter;
   private long _reloadCounterLastIncremented;
   private KeyStoreBrowserOptionsItem$RunnablesThread _runnablesThread;
   private static final int PROPERTY_SERIAL_NUMBER = 0;
   private static final int PROPERTY_LABEL = 1;
   private static final int PROPERTY_SECURITY_LEVEL = 2;
   private static final boolean DEBUG = false;

   public KeyStoreBrowserOptionsItem(KeyStoreBrowserContext browserContext, Object displayContext) {
      super(browserContext.getKeyStoreBrowserName(), new Object(0, 2), 5294015899860238835L);
      this._browserContext = browserContext;
      this._displayContext = displayContext;
      this._keyStore = browserContext.getKeyStore();
      this._trustedKeyStore = (TrustedKeyStore)TrustedKeyStore.getInstance();
      this._certificateStatusManager = CertificateStatusManager.getInstance();
      this._cryptoSystemProperties = this._browserContext.getCryptoSystemProperties();
      this._keyStoreItems = (Vector)(new Object());
      this._displayedIndices = new int[0];
      this._keyStoreItemsSyncObject = new Object();
      this._epochSyncObject = new Object();
      this._listFieldCallBack = new BrowserListFieldCallBack(this._keyStoreItems, this._displayedIndices, this._keyStoreItemsSyncObject);
      this._emailAddressFactory = (Factory)ApplicationRegistry.getApplicationRegistry().waitFor(-2985347935260258684L);
      this._showMyCertsVerb = new KeyStoreBrowserOptionsItem$ShowHideCertsVerb(this, 0, 1142880);
      this._showOthersVerb = new KeyStoreBrowserOptionsItem$ShowHideCertsVerb(this, 1, 1142896);
      this._showCASVerb = new KeyStoreBrowserOptionsItem$ShowHideCertsVerb(this, 2, 1142928);
      this._showRootsVerb = new KeyStoreBrowserOptionsItem$ShowHideCertsVerb(this, 3, 1143040);
      this._showEndEntityVerb = new KeyStoreBrowserOptionsItem$ShowHideCertsVerb(this, 4, 1142912);
      this._showAllVerb = new KeyStoreBrowserOptionsItem$ShowHideCertsVerb(this, 5, 1142864);
      this._displayCertificateVerb = (DisplayCertificateVerb)(new Object(CommonResources.getString(9046)));
      this._displayCertificateChainVerb = (DisplayCertificateChainVerb)(new Object(KeyStoreBrowserResources.getResourceBundle(), 6042));
      this._trustVerb = new MoveTrustVerb();
      this._distrustVerb = new MoveTrustVerb();
      CertificateAttachmentModelFactory attachmentFactory = this._browserContext.getCertificateAttachmentModelFactory();
      this._emailCertificateVerb = new SendCertificatesVerb(false, attachmentFactory);
      this._pinCertificateVerb = new SendCertificatesVerb(true, attachmentFactory);
      this._cancelHoldVerb = new KeyStoreBrowserVerb(0, 1048832);
      this._changeLabelVerb = new KeyStoreBrowserVerb(1, 1048848);
      this._changeSecurityLevelVerb = new KeyStoreBrowserVerb(9, 1048849);
      this._deleteVerb = new KeyStoreBrowserVerb(2, 611472);
      this._revokeVerb = new KeyStoreBrowserVerb(3, 1048720);
      this._fetchStatusVerb = new KeyStoreBrowserVerb(4, 1200240);
      this._associateAddressesVerb = new KeyStoreBrowserVerb(7, 1200245);
      this._continueVerb = new KeyStoreBrowserVerb(8, 327936);
      ContextObject.put(super._context, 244, this._browserContext.getHelpContextString());
      ContextObject.setPrivateFlag(super._context, -337556985625701066L, 0);
   }

   @Override
   protected final void open() {
      this.openImpl();
      super.open();
   }

   protected final void openModal() {
      this.openImpl();
      super._mainScreen = this.createMainScreen();
      UiApplication.getUiApplication().pushModalScreen(super._mainScreen);
   }

   protected final void openImpl() {
      this._app = UiApplication.getUiApplication();
      this._showType = this._browserContext.getCertificateFilter();
      this._certificateStatusManager.addCollectionListener(new Object(this));
      this._keyStore.addCollectionListener(new Object(this));
      this._trustedKeyStore.addCollectionListener(new Object(this));
      MemoryCleanerDaemon.addWeakListener(this, false);
   }

   @Override
   public final boolean confirm(Verb verb, Object context) {
      this._certificateStatusManager.removeCollectionListener(this);
      this._keyStore.removeCollectionListener(this);
      this._trustedKeyStore.removeCollectionListener(this);
      MemoryCleanerDaemon.removeListener(this);
      synchronized (this._epochSyncObject) {
         this._epoch++;
         this._epochType = -1;
      }

      this._keyStoreTicket = null;
      this._trustedKeyStoreTicket = null;
      this._managerTicket = null;
      this._keyStoreItems.removeAllElements();
      Array.resize(this._displayedIndices, 0);
      this._browserContext.setCertificateFilter(this._showType);
      return super.confirm(verb, context);
   }

   @Override
   protected final void populateMainScreen(MainScreen mainScreen) {
      int height = CryptoIndicatorImages.getImageHeight();
      height = Math.max(height, Font.getDefault().getHeight()) + 1;
      this._listField = (ListField)(new Object(0, 2));
      this._listField.setCallback(this._listFieldCallBack);
      this._listField.setEmptyString(KeyStoreBrowserResources.getResourceBundle(), 6015, 4);
      this._listField.setRowHeight(height);
      this._listField.setSearchable(true);
      mainScreen.add(this._listField);
      String title = this._browserContext.getKeyStoreBrowserName();
      if (ContextObject.getFlag(this._displayContext, 5)) {
         title = KeyStoreBrowserResources.getString(6091);
      }

      this._titleField = new BrowserTitleField(title);
      mainScreen.setTitle(this._titleField);
      this.loadCertificates(1);
   }

   private final void sort(Vector vector) {
      int size = vector.size();
      KeyStoreBrowserData[] data = new KeyStoreBrowserData[size];
      vector.copyInto(data);
      Arrays.sort(data, this);

      for (int i = 0; i < size; i++) {
         vector.setElementAt(data[i], i);
      }
   }

   private final void setTitleFilterType() {
      this._titleField.setValueType(this._showType);
   }

   private final void displayCertProperty(KeyStoreBrowserData data, int property) {
      if (data != null) {
         StringBuffer text = (StringBuffer)(new Object());
         switch (property) {
            case -1:
               int level = data.getKeyStoreData().getSecurityLevel();
               text.append(KeyStoreBrowserResources.getString(6051));
               text.append(' ');
               if (level == 1) {
                  text.append(KeyStoreBrowserResources.getString(6052));
               } else if (level == 3) {
                  text.append(KeyStoreBrowserResources.getString(6075));
               } else {
                  if (level != 2) {
                     return;
                  }

                  text.append(KeyStoreBrowserResources.getString(6053));
               }
               break;
            case 0:
            default:
               String serialNumber = data.getSerialNumber();
               if (serialNumber != null) {
                  text.append(KeyStoreBrowserResources.getString(6037));
                  text.append(serialNumber);
               } else {
                  text.append(KeyStoreBrowserResources.getString(6038));
               }
               break;
            case 1:
               text.append(KeyStoreBrowserResources.getString(6039));
               text.append(data.getKeyStoreData().getLabel());
               if (!data.isRoot()) {
                  Certificate cert = data.getCertificate();
                  String issuerFriendlyName = CertificateUtilities.getFriendlyName(cert.getIssuer());
                  if (issuerFriendlyName != null) {
                     text.append('\n');
                     text.append(KeyStoreBrowserResources.getString(6057));
                     text.append(issuerFriendlyName);
                  }
               }
         }

         Dialog.inform(text.toString());
      }
   }

   private final void getEmailAddressVerbs(VerbToMenu verbToMenu, String emailAddress) {
      ContextObject emailContext = (ContextObject)(new Object(44));
      emailContext.put(253, emailAddress);
      Object eam = this._emailAddressFactory.createInstance(emailContext);
      if (eam instanceof Object) {
         VerbProvider verbProvider = (VerbProvider)eam;
         Verb[] verbs = new Object[0];
         verbProvider.getVerbs(emailContext, verbs);
         verbToMenu.addVerbs(verbs);
      }
   }

   public final KeyStoreBrowserContext getBrowserContext() {
      return this._browserContext;
   }

   @Override
   protected final void addScreenVerbs(VerbToMenu verbToMenu, int instance) {
      super.addScreenVerbs(verbToMenu, instance);
      VerbRepository verbRepository = VerbRepository.getVerbRepository(-3067780115376710723L);
      long[] verbTypes = this._browserContext.getVerbRepositoryTypes();

      for (int i = verbTypes.length - 1; i >= 0; i--) {
         Verb[] verbs = verbRepository.getVerbs(verbTypes[i]);
         if (verbs != null) {
            verbToMenu.addVerbs(verbs);
         }
      }

      if (this._keyStoreItems.size() > 0 && this._browserContext.getAllowHideCertificateTypes()) {
         if (this._showType != 0) {
            verbToMenu.addVerb(this._showMyCertsVerb);
         }

         if (this._showType != 1) {
            verbToMenu.addVerb(this._showOthersVerb);
         }

         if (this._showType != 2) {
            verbToMenu.addVerb(this._showCASVerb);
         }

         if (this._showType != 3) {
            verbToMenu.addVerb(this._showRootsVerb);
         }

         if (this._showType != 5) {
            verbToMenu.addVerb(this._showAllVerb);
         }
      }
   }

   private final boolean isTrustVerbAllowed(KeyStoreBrowserData[] browserDatas) {
      for (int i = 0; i < browserDatas.length; i++) {
         if (browserDatas[i].isUntrusted() && this._trustedKeyStore.isAllowed(browserDatas[i].getCertificate())) {
            return true;
         }
      }

      return false;
   }

   private final boolean isDistrustVerbAllowed(KeyStoreBrowserData[] browserDatas) {
      for (int i = 0; i < browserDatas.length; i++) {
         if (browserDatas[i].isExplicitlyTrusted()) {
            return true;
         }
      }

      return false;
   }

   @Override
   protected final Verb addCurrentItemVerbs(VerbToMenu verbToMenu, int instance) {
      if (this._keyStoreItems.size() <= 0) {
         return null;
      }

      KeyStoreBrowserData[] selectedDatas = this.getAndLoadSelectedBrowserDatas();
      if (selectedDatas != null && selectedDatas.length != 0) {
         KeyStoreBrowserData selectedData = selectedDatas[0];
         boolean multipleSelected = selectedDatas.length > 1;
         KeyStoreData[] selectedKeyStoreDatas = new Object[selectedDatas.length];

         for (int i = 0; i < selectedDatas.length; i++) {
            selectedKeyStoreDatas[i] = selectedDatas[i].getKeyStoreData();
         }

         Verb defaultVerb = null;
         if (ContextObject.getFlag(this._displayContext, 5)) {
            verbToMenu.addVerb(this._continueVerb);
            this._continueVerb.initialize(selectedKeyStoreDatas, this);
            defaultVerb = this._continueVerb;
         }

         this._browserContext.addCurrentItemsVerb(verbToMenu, selectedDatas);
         if (!multipleSelected && CertificateStatusProviderFacade.queryStatusAvailability(selectedData.getBestCertificateChain(), false)) {
            this._fetchStatusVerb.initialize(selectedData, this._browserContext);
            verbToMenu.addVerb(this._fetchStatusVerb);
         }

         if (!multipleSelected && !selectedData.isRoot()) {
            this._displayCertificateChainVerb.setCertificate(selectedData.getLabel(), selectedData.getCertificate(), this._keyStore);
            verbToMenu.addVerb(this._displayCertificateChainVerb);
         }

         this._deleteVerb.initialize(selectedDatas, this);
         verbToMenu.addVerb(this._deleteVerb);
         if (!multipleSelected) {
            String[] emailAddresses = selectedData.getEmailAddresses();
            if (emailAddresses != null) {
               for (int i = 0; i < emailAddresses.length; i++) {
                  this.getEmailAddressVerbs(verbToMenu, emailAddresses[i]);
               }
            }

            verbToMenu.coalesce(-3072555018635390988L, null);
         }

         this._emailCertificateVerb.initialize(selectedKeyStoreDatas);
         verbToMenu.addVerb(this._emailCertificateVerb);
         this._pinCertificateVerb.initialize(selectedKeyStoreDatas);
         verbToMenu.addVerb(this._pinCertificateVerb);
         if (!multipleSelected) {
            this._changeLabelVerb.initialize(selectedData, this);
            verbToMenu.addVerb(this._changeLabelVerb);
            if (selectedData.isPrivateKeySet() && selectedKeyStoreDatas[0] instanceof Object && selectedKeyStoreDatas[0].getPasswordVersion() != -1) {
               this._changeSecurityLevelVerb.initialize(selectedData, this);
               verbToMenu.addVerb(this._changeSecurityLevelVerb);
            }
         }

         Certificate _certificate = selectedData.getCertificate();
         boolean _isEndEntity = _certificate.getInformation(-7341435958452683242L, null, Boolean.FALSE);
         if (!multipleSelected && _isEndEntity && !selectedData.isPrivateKeySet()) {
            this._associateAddressesVerb.initialize(selectedData, this);
            verbToMenu.addVerb(this._associateAddressesVerb);
         }

         if (this.isTrustVerbAllowed(selectedDatas)) {
            this._trustVerb.initialize(false, true, selectedDatas, this);
            verbToMenu.addVerb(this._trustVerb);
         }

         if (this.isDistrustVerbAllowed(selectedDatas)) {
            this._distrustVerb.initialize(true, true, selectedDatas, this);
            verbToMenu.addVerb(this._distrustVerb);
         }

         if (!multipleSelected && !selectedData.isRevoked()) {
            this._revokeVerb.initialize(selectedData, this);
            verbToMenu.addVerb(this._revokeVerb);
         } else if (!multipleSelected && selectedData.getIsOnRevocationHold()) {
            this._cancelHoldVerb.initialize(selectedData, this);
            verbToMenu.addVerb(this._cancelHoldVerb);
         }

         if (!multipleSelected) {
            this._displayCertificateVerb
               .setCertificate(selectedData.getCertificate(), null, selectedData.getKeyStore(), this._cryptoSystemProperties, this._managerTicket);
            verbToMenu.addVerb(this._displayCertificateVerb);
            if (defaultVerb == null) {
               defaultVerb = this._displayCertificateVerb;
            }
         }

         return defaultVerb;
      } else {
         return null;
      }
   }

   final KeyStoreBrowserData[] getAndLoadSelectedBrowserDatas() {
      int[] selectedIndexes = this._listField.getSelection();
      if (selectedIndexes != null && selectedIndexes.length <= this._keyStoreItems.size()) {
         int size = this._displayedIndices.length;
         KeyStoreBrowserData[] selectedDatas = new KeyStoreBrowserData[0];

         for (int j = 0; j < selectedIndexes.length; j++) {
            int i = selectedIndexes[j];
            if (i != -1) {
               while (i < size) {
                  if (this._displayedIndices[i] == selectedIndexes[j]) {
                     selectedIndexes[j] = i;
                     break;
                  }

                  i++;
               }

               if (selectedIndexes[j] != i) {
                  return null;
               }

               KeyStoreBrowserData selectedData = (KeyStoreBrowserData)this._keyStoreItems.elementAt(selectedIndexes[j]);
               if (selectedData.loadDataIfNeeded()) {
                  this._listField.invalidate(selectedIndexes[j]);
               }

               Array.resize(selectedDatas, selectedDatas.length + 1);
               selectedDatas[selectedDatas.length - 1] = selectedData;
            }
         }

         return selectedDatas;
      } else {
         return null;
      }
   }

   @Override
   public final boolean keyDown(int keycode, int time) {
      char keyPressed = Keypad.map(keycode);
      KeyStoreBrowserData[] selectedDatas = this.getAndLoadSelectedBrowserDatas();
      if (selectedDatas != null && selectedDatas.length != 0) {
         KeyStoreBrowserData selectedData = selectedDatas[0];
         boolean multipleSelected = selectedDatas.length > 1;
         if (keyPressed == 127 || keyPressed == '\b') {
            this._deleteVerb.initialize(selectedDatas, this);
            this._deleteVerb.invoke(null);
            return true;
         } else if (!multipleSelected && keyPressed == '\n') {
            this._displayCertificateVerb
               .setCertificate(selectedData.getCertificate(), null, selectedData.getKeyStore(), this._cryptoSystemProperties, this._managerTicket);
            this._displayCertificateVerb.invoke(null);
            return true;
         } else if (!multipleSelected && keyPressed == ' ') {
            this.displayCertProperty(selectedData, 1);
            return true;
         } else {
            int hotkeyID = KeyStoreBrowserHotkeys.map(keycode);
            return !this.invokeHotkey(selectedDatas, hotkeyID) ? super.keyDown(keycode, time) : true;
         }
      } else {
         int hotkeyID = KeyStoreBrowserHotkeys.map(keycode);
         return !this.invokeHotkey(null, hotkeyID) ? super.keyDown(keycode, time) : true;
      }
   }

   @Override
   protected final boolean invokeOptionsAction(int action) {
      switch (action) {
         case 1:
            this.keyDown(Keypad.keycode('\n', 32768), 0);
            return true;
         default:
            return false;
      }
   }

   private final boolean invokeHotkey(KeyStoreBrowserData[] datas, int hotkeyID) {
      switch (hotkeyID) {
         case 6059:
            if (this._showType != 1) {
               this._showOthersVerb.invoke(null);
               return true;
            }
            break;
         case 6060:
            if (this._showType != 3) {
               this._showRootsVerb.invoke(null);
               return true;
            }
            break;
         case 6061:
            if (this._showType != 4) {
               this._showEndEntityVerb.invoke(null);
               return true;
            }
            break;
         case 6062:
            if (this._showType != 2) {
               this._showCASVerb.invoke(null);
               return true;
            }
            break;
         case 6063:
            if (this._showType != 5) {
               this._showAllVerb.invoke(null);
               return true;
            }
            break;
         case 6064:
            if (datas != null && datas.length == 1) {
               this.displayCertProperty(datas[0], 0);
               return true;
            }

            return false;
         case 6065:
            if (datas != null && datas.length == 1) {
               this.displayCertProperty(datas[0], 2);
               return true;
            }

            return false;
         case 6066:
         case 6077:
            if (this._showType != 0) {
               this._showMyCertsVerb.invoke(null);
               return true;
            }
      }

      return false;
   }

   final void loadCertificates(int type) {
      synchronized (this._epochSyncObject) {
         type = this._epochType == 1 ? 1 : type;
         this._epochType = type;
         this._epoch++;
         if (type == 1) {
            this._app.invokeLater(new KeyStoreBrowserOptionsItem$KeyStoreReloader(this, this._epoch));
         } else {
            new KeyStoreBrowserOptionsItem$LoadCertificatesThread(this, type, this._epoch).start();
         }
      }
   }

   private final void configureDisplayFilter() {
      synchronized (this._app.getAppEventLock()) {
         synchronized (this._keyStoreItemsSyncObject) {
            boolean havePrivateKeys = false;
            int size = this._displayedIndices.length;
            int displayIndex = 0;
            int numDisplayed = 0;

            for (int i = 0; i < size; i++) {
               KeyStoreBrowserData data = (KeyStoreBrowserData)this._keyStoreItems.elementAt(i);
               if (this.determineDisplayStatus(data)) {
                  this._displayedIndices[i] = displayIndex++;
                  numDisplayed++;
                  if (!havePrivateKeys && data.isPrivateKeySet()) {
                     havePrivateKeys = true;
                  }
               } else {
                  this._displayedIndices[i] = -1;
               }
            }

            if (numDisplayed == 0) {
               int resourceId;
               if (size <= 0) {
                  resourceId = 6016;
               } else {
                  resourceId = 6013;
               }

               this._listField.setEmptyString(KeyStoreBrowserResources.getResourceBundle(), resourceId, 4);
            }

            this._listField.setSize(numDisplayed, this._listField.getSelectedIndex());
            this._listFieldCallBack.setHavePrivateKeys(havePrivateKeys);
            this.setTitleFilterType();
         }
      }
   }

   private final boolean determineDisplayStatus(KeyStoreBrowserData data) {
      return this._showType == 5
         || this._showType == 3 && data.isRoot()
         || this._showType == 2 && data.isCA()
         || this._showType == 4 && data.isEndEntity()
         || this._showType == 1 && data.isEndEntity() && !data.isPrivateKeySet()
         || this._showType == 0 && data.isPrivateKeySet();
   }

   final void selectCertificates(KeyStoreData[] keyStoreDatas) {
      ContextObject.put(this._displayContext, -5328662892314083964L, keyStoreDatas);
      Verb closeVerb = this.getCloseVerb();
      if (closeVerb != null) {
         closeVerb.invoke(null);
      }
   }

   final void invalidateItem(KeyStoreBrowserData data) {
      synchronized (this._keyStoreItemsSyncObject) {
         int index = this._keyStoreItems.indexOf(data);
         if (index != -1) {
            index = this._displayedIndices[index];
            if (index != -1) {
               this._listField.invalidate(index);
            }
         }
      }
   }

   final CertificateStatusManagerTicket getManagerTicket() {
      if (this._managerTicket == null) {
         this._managerTicket = this._certificateStatusManager.getTicket();
      }

      return this._managerTicket;
   }

   final KeyStoreTicket getKeyStoreTicket() {
      if (this._keyStoreTicket == null) {
         this._keyStoreTicket = this._keyStore.getTicket();
      }

      return this._keyStoreTicket;
   }

   final KeyStoreTicket getTrustedKeyStoreTicket() {
      if (this._trustedKeyStoreTicket == null) {
         this._trustedKeyStoreTicket = this._trustedKeyStore.getTicket();
      }

      return this._trustedKeyStoreTicket;
   }

   @Override
   public final void elementAdded(Collection collection, Object element) {
      this.processElementAddedOrRemoved();
   }

   @Override
   public final void elementRemoved(Collection collection, Object element) {
      this.processElementAddedOrRemoved();
   }

   private final void processElementAddedOrRemoved() {
      synchronized (this._reloadCounterLock) {
         this._reloadCounter++;
         this._reloadCounterLastIncremented = System.currentTimeMillis();
         if (this._runnablesThread == null) {
            this._runnablesThread = new KeyStoreBrowserOptionsItem$RunnablesThread(this, null);
            this._runnablesThread.start();
         }
      }
   }

   @Override
   public final void reset(Collection collection) {
   }

   @Override
   public final void elementUpdated(Collection collection, Object oldElement, Object newElement) {
      this._app.invokeLater(new KeyStoreBrowserOptionsItem$ElementUpdatedRunnable(this, (Certificate)newElement));
   }

   @Override
   public final boolean cleanNow(int event) {
      boolean cleaned = this._managerTicket != null || this._keyStoreTicket != null || this._trustedKeyStoreTicket != null;
      this._managerTicket = null;
      this._keyStoreTicket = null;
      this._trustedKeyStoreTicket = null;
      if (event == 6) {
         this._app.invokeLater(new KeyStoreBrowserOptionsItem$1(this));
      }

      return cleaned;
   }

   @Override
   public final String getDescription() {
      return KeyStoreBrowserResources.getString(6030);
   }

   @Override
   public final int compare(Object o1, Object o2) {
      return StringUtilities.compareObjectToStringIgnoreCase(
         ((KeyStoreBrowserData)o1).getKeyStoreData().getLabel(), ((KeyStoreBrowserData)o2).getKeyStoreData().getLabel(), 1701707776
      );
   }

   static final MainScreen access$200(KeyStoreBrowserOptionsItem x0) {
      return x0._mainScreen;
   }

   static final MainScreen access$2400(KeyStoreBrowserOptionsItem x0) {
      return x0._mainScreen;
   }

   static final Verb access$2500(KeyStoreBrowserOptionsItem x0) {
      return x0.getCloseVerb();
   }
}
