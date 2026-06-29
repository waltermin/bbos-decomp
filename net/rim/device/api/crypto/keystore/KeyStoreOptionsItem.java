package net.rim.device.api.crypto.keystore;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.itpolicy.ITPolicy;
import net.rim.device.api.servicebook.ServiceBook;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.component.ObjectChoiceField;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.options.SaveableMainScreenOptionsListItem;
import net.rim.device.apps.api.ui.BooleanChoiceField;
import net.rim.device.apps.api.ui.TimeChoiceField;
import net.rim.device.apps.api.utility.framework.VerbToMenu;
import net.rim.device.cldc.io.ippp.SocketTransportBase;
import net.rim.vm.Array;

final class KeyStoreOptionsItem extends SaveableMainScreenOptionsListItem {
   private BooleanChoiceField _allowBackupRestoreField;
   private TimeChoiceField _passphraseTimeoutField;
   private TimeChoiceField _staleStatusField;
   private BooleanChoiceField _allowUnverifiedCRLField;
   private BooleanChoiceField _keyStoreAddressInjectorField;
   private ObjectChoiceField _certificateServiceField;
   private String[] _corporateServiceUIDs;
   private static final ResourceBundle _rb = ResourceBundle.getBundle(60462186577914032L, "net.rim.device.internal.resource.crypto.KeyStore");

   public KeyStoreOptionsItem() {
      super(_rb.getFamily(), 7007, 5294015899860238835L);
      ContextObject.put(super._context, 244, "net_rim_bb_secureemail_help/smime_certificates");
   }

   @Override
   protected final void populateMainScreen(MainScreen mainScreen) {
      long[] times = new long[]{
         0L,
         60000L,
         120000L,
         300000L,
         600000L,
         1200000L,
         1800000L,
         3600000L,
         7784890369L,
         283681784856959L,
         7014208459855966468L,
         4702637964970631289L,
         7958535042040143931L,
         2322529620416995425L,
         3348276201498821956L,
         3348814963692957281L,
         5063288013772910435L,
         4697540284400272195L,
         6278151339540835950L,
         5557507091660296257L,
         5125203502941172833L,
         4270894726855025261L,
         7957404443810952992L,
         7885630523805103993L,
         281479322009602L,
         -6614894586175633280L,
         -6595555058107553860L,
         1219825740158469809L,
         -7092578747018880874L,
         1929637779808837518L,
         -9109409128398360652L,
         -4933173889804581685L,
         -2452176330302200575L,
         -2129652281379949041L,
         4489418347599800561L,
         3961034656705053469L,
         7013904739439489218L,
         -2189939658339496064L,
         2578875903013552068L,
         -5844220375344752133L,
         4813642843582044251L,
         479292087523606675L,
         -4705355920304850984L,
         -5833264603961224996L,
         -8116125622156797822L,
         -1135175159400368714L,
         5372324762523729521L,
         -1339845330536145525L,
         -7343418063851491595L,
         -958181195158919290L,
         7033640781109570789L,
         2640453638758076981L,
         3997970050120888264L,
         6379192102518350257L,
         -2412463304145589905L,
         5557312645602953552L,
         -8710290446950549001L,
         -3457636883037979396L,
         8737892590394868225L,
         8391179664624870754L,
         7883960631525777467L,
         4270900224415068783L,
         8029189659426898464L,
         8243102965928322418L
      };
      long maxTimeout = (long)ITPolicy.getInteger(24, 8, 60) * 60000;
      this._passphraseTimeoutField = (TimeChoiceField)(new Object(_rb.getString(7011), 268435456));
      this._passphraseTimeoutField.setTimeChoicesIncludeMaxTimeChoice(times, 0, maxTimeout);
      this._passphraseTimeoutField.setSelectedTimeInMillis(KeyStoreOptions.getPassphraseTimeout(), false);
      this._allowBackupRestoreField = (BooleanChoiceField)(new Object(_rb.getString(7005), 0, KeyStoreOptions.getAllowBackupRestore(), 268435456));
      if (ITPolicy.getBoolean(24, 32, false)) {
         this._allowBackupRestoreField.setEditable(false);
      }

      ServiceRecord[] allServiceRecords = ServiceBook.getSB().findRecordsByCid("IPPP");
      int numServiceRecords = allServiceRecords != null ? allServiceRecords.length : 0;
      ServiceRecord[] corporateServiceRecords = new Object[numServiceRecords];
      String[] corporateServiceNames = new Object[numServiceRecords];
      int defaultServiceRecordIndex = -1;
      if (numServiceRecords > 0) {
         int numCorporateServices = 0;

         for (int i = 0; i < numServiceRecords; i++) {
            if (SocketTransportBase.getTypeByUid(allServiceRecords[i].getUid()) == 0) {
               corporateServiceRecords[numCorporateServices] = allServiceRecords[i];
               corporateServiceNames[numCorporateServices] = allServiceRecords[i].getName();
               numCorporateServices++;
            }
         }

         Array.resize(corporateServiceRecords, numCorporateServices);
         Array.resize(corporateServiceNames, numCorporateServices);
         Arrays.sort(corporateServiceNames, 0, numCorporateServices, corporateServiceRecords, new KeyStoreOptionsItem$StringComparator());
         this._corporateServiceUIDs = new Object[numCorporateServices];
         String defaultServiceUID = KeyStoreOptions.getCertificateServiceUID();

         for (int i = 0; i < numCorporateServices; i++) {
            this._corporateServiceUIDs[i] = corporateServiceRecords[i].getUid();
            if (StringUtilities.strEqualIgnoreCase(defaultServiceUID, this._corporateServiceUIDs[i], 1701707776)) {
               defaultServiceRecordIndex = i;
            }
         }
      }

      this._certificateServiceField = (ObjectChoiceField)(new Object(_rb.getString(7023), corporateServiceNames));
      this._certificateServiceField.setEmptyString(_rb.getFamily(), 7022);
      if (defaultServiceRecordIndex >= 0) {
         this._certificateServiceField.setSelectedIndex(defaultServiceRecordIndex);
      }

      this._staleStatusField = (TimeChoiceField)(new Object(_rb.getString(7012), 268435456));
      if (ITPolicy.getBoolean(24, 57, false)) {
         this._staleStatusField
            .setTimeChoices(
               new long[]{
                  Long.MAX_VALUE,
                  7638386911574622464L,
                  8323317755766522819L,
                  72169106143838464L,
                  3115244560543606L,
                  -1432310690566785791L,
                  457966384284632576L,
                  432359261170741587L
               },
               0,
               Long.MAX_VALUE
            );
      } else {
         long[] staleTimes = new long[]{
            3600000L,
            7200000L,
            14400000L,
            43200000L,
            86400000L,
            604800000L,
            2592000000L,
            15552000000L,
            Long.MAX_VALUE,
            -5064126545732042736L,
            -9100124567503660425L,
            -3455386808148900321L,
            Long.MAX_VALUE,
            7638386911574622464L,
            8323317755766522819L,
            72169106143838464L,
            3115244560543606L,
            -1432310690566785791L,
            457966384284632576L,
            432359261170741587L,
            432448944100935259L,
            1830271611608908126L,
            7945933443253145965L,
            35858383118925918L,
            8216536738345151494L,
            7207670914914609524L,
            6081301120037160960L,
            576599811097624830L,
            504814562215690790L,
            576468591074347008L,
            2884556055376389160L,
            8081718330671307110L,
            576647031635591743L,
            8369948707642109992L,
            3420247939943984651L,
            7526466366080550912L,
            -2871035964235292300L,
            -1069315555124573337L,
            2526916903755122688L,
            2972666025325615144L,
            19220902655912329L,
            5702420698151600392L,
            -8563241713839936926L,
            2972666027267264873L,
            1950132851979676041L,
            7604654805075653665L,
            3056889749705859857L,
            7800524827493380139L,
            -8617230640533388246L,
            8531308814211811328L,
            223323371176722571L,
            2337491631214718312L,
            -8037480217907203472L,
            4685996011265805433L,
            8088755205095387248L,
            4858358910232481136L,
            8088755203775470352L,
            12840541867920752L,
            4685995935433638152L,
            576588571175669362L,
            -183680768360418751L,
            9107025468061714432L,
            7758931218917296128L,
            4685995465682690255L,
            -769160049749338684L,
            7950495756073828380L,
            5927620470146403190L,
            1185642154739921740L,
            4758053056902417855L,
            8016979085608323436L,
            2554686048899064940L,
            576671683858423122L
         };
         int staleMaxTimeout = ITPolicy.getInteger(24, 33, -1);
         long convertedStaleMaxTimeout = Long.MAX_VALUE;
         if (staleMaxTimeout >= 0) {
            convertedStaleMaxTimeout = (long)staleMaxTimeout * 60 * 60 * 1000;
         }

         this._staleStatusField.setTimeChoicesIncludeMaxTimeChoice(staleTimes, 0, convertedStaleMaxTimeout);
         this._staleStatusField.setSelectedTimeInMillis(CertificateStatusManager.getStaleTime(), false);
      }

      this._allowUnverifiedCRLField = (BooleanChoiceField)(new Object(_rb.getString(7013), 0, KeyStoreOptions.getAllowUnverifiedCRLs(), 268435456));
      if (ITPolicy.getBoolean(24, 41, false)) {
         this._allowUnverifiedCRLField.setEditable(false);
      }

      this._keyStoreAddressInjectorField = (BooleanChoiceField)(new Object(_rb.getString(7021), 2, KeyStoreOptions.getKeyStoreAddressInjectorEnabled()));
      mainScreen.add(this._allowBackupRestoreField);
      mainScreen.add(this._passphraseTimeoutField);
      mainScreen.add(this._keyStoreAddressInjectorField);
      mainScreen.add((Field)(new Object()));
      mainScreen.add(this._certificateServiceField);
      mainScreen.add(this._staleStatusField);
      mainScreen.add(this._allowUnverifiedCRLField);
   }

   @Override
   protected final void addScreenVerbs(VerbToMenu verbToMenu, int instance) {
      super.addScreenVerbs(verbToMenu, instance);
      verbToMenu.addVerb(new KeyStoreOptionsItem$ChangePassphraseVerb(_rb.getString(7008)));
   }

   @Override
   protected final void open() {
      try {
         KeyStorePasswordManager.getInstance().setPassword();
      } catch (KeyStoreCancelException e) {
         return;
      }

      super.open();
   }

   @Override
   protected final boolean save() {
      if (this.isChanged()) {
         try {
            KeyStorePasswordManager.getInstance().challenge(_rb.getString(7006));
            KeyStoreOptions.setPassphraseTimeout(this._passphraseTimeoutField.getSelectedTimeInMillis());
            KeyStoreOptions.setAllowBackupRestore(this._allowBackupRestoreField.isAffirmative());
            KeyStoreOptions.setStaleTime(this._staleStatusField.getSelectedTimeInMillis());
            KeyStoreOptions.setAllowUnverifiedCRLs(this._allowUnverifiedCRLField.isAffirmative());
            KeyStoreOptions.setKeyStoreAddressInjectorEnabled(this._keyStoreAddressInjectorField.isAffirmative());
            ObjectChoiceField certificateServiceField = this._certificateServiceField;
            if (certificateServiceField.getSize() > 0) {
               KeyStoreOptions.setCertificateServiceUID(this._corporateServiceUIDs[certificateServiceField.getSelectedIndex()]);
            }
         } catch (KeyStoreCancelException e) {
            return false;
         }
      }

      return super.save();
   }

   private final boolean isChanged() {
      if (this._passphraseTimeoutField.getSelectedTimeInMillis() == KeyStoreOptions.getPassphraseTimeout()
         && this._allowBackupRestoreField.isAffirmative() == KeyStoreOptions.getAllowBackupRestore()
         && this._staleStatusField.getSelectedTimeInMillis() == CertificateStatusManager.getStaleTime()
         && this._allowUnverifiedCRLField.isAffirmative() == KeyStoreOptions.getAllowUnverifiedCRLs()
         && this._keyStoreAddressInjectorField.isAffirmative() == KeyStoreOptions.getKeyStoreAddressInjectorEnabled()) {
         String optionsUID = KeyStoreOptions.getCertificateServiceUID();
         String choiceFieldUID = null;
         ObjectChoiceField certificateServiceField = this._certificateServiceField;
         if (certificateServiceField.getSize() > 0) {
            choiceFieldUID = this._corporateServiceUIDs[certificateServiceField.getSelectedIndex()];
         }

         return !StringUtilities.strEqualIgnoreCase(optionsUID, choiceFieldUID, 1701707776);
      } else {
         return true;
      }
   }
}
