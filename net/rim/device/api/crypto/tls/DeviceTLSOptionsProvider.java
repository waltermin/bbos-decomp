package net.rim.device.api.crypto.tls;

import java.util.Enumeration;
import java.util.Vector;
import net.rim.device.api.collection.Collection;
import net.rim.device.api.collection.CollectionListener;
import net.rim.device.api.crypto.certificate.Certificate;
import net.rim.device.api.crypto.certificate.CertificateChoiceField;
import net.rim.device.api.crypto.keystore.DeviceKeyStore;
import net.rim.device.api.crypto.keystore.KeyStore;
import net.rim.device.api.crypto.keystore.KeyStoreData;
import net.rim.device.api.crypto.keystore.KeyStoreIndex;
import net.rim.device.api.crypto.keystore.TrustedKeyStore;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.itpolicy.ITPolicy;
import net.rim.device.api.system.Display;
import net.rim.device.api.system.KeyListener;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.api.ui.component.ListFieldCallback;
import net.rim.device.api.ui.component.ObjectChoiceField;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.util.Arrays;
import net.rim.device.apps.api.framework.model.FieldProvider;
import net.rim.device.apps.api.framework.model.VerbProvider;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.ui.BooleanChoiceField;
import net.rim.device.cldc.io.ssl.TLSOptionStore;
import net.rim.device.internal.system.FIPSPolicy;
import net.rim.vm.Array;

final class DeviceTLSOptionsProvider implements FieldProvider, ListFieldCallback, VerbProvider, KeyListener, CollectionListener {
   private ObjectChoiceField _protocolChoiceField;
   private BooleanChoiceField _encryptionStrengthField;
   private BooleanChoiceField _promptForServerTrustField;
   private BooleanChoiceField _promptForDomainNameField;
   private BooleanChoiceField _promptForCertificateField;
   private BooleanChoiceField _promptForNoClientCertField;
   private BooleanChoiceField _fipsAlgorithmsOnlyField;
   private CertificateChoiceField _defaultClientCertField;
   private ListField _hostDefaultsListField;
   private Vector _currentHosts;
   private TLSOptionStore _optionStore = TLSOptionStore.getOptions();
   private TLSDeviceOptionStore _deviceOptionsStore = TLSDeviceOptionStore.getOptions();
   private KeyStoreData[] _dataArray;
   private VerticalFieldManager _vfm;
   private String[] _hostsToAddNames;
   private int[] _hostsToAddIndexes;
   private String[] _hostsToRemoveNames;
   private static final ResourceBundle _rb = ResourceBundle.getBundle(5710659227867441061L, "net.rim.device.internal.resource.crypto.SSL");

   @Override
   public final Field getField(Object context) {
      this._vfm = (VerticalFieldManager)(new Object());
      Font boldFont = Font.getDefault();
      boldFont = boldFont.derive(boldFont.getStyle() | 1);
      LabelField generalOptionsLabel = (LabelField)(new Object(_rb.getString(44)));
      generalOptionsLabel.setFont(boldFont);
      this._vfm.add(generalOptionsLabel);
      int choice = 0;
      if (this._optionStore.useTLS() && this._optionStore.useSSL()) {
         choice = 0;
      } else if (this._optionStore.useTLS()) {
         choice = 1;
      } else if (this._optionStore.useSSL()) {
         choice = 2;
      }

      this._protocolChoiceField = (ObjectChoiceField)(new Object(_rb.getString(53), _rb.getStringArray(54), choice));
      this._vfm.add(this._protocolChoiceField);
      this._encryptionStrengthField = (BooleanChoiceField)(new Object(
         _rb.getString(28), _rb.getStringArray(48), this._optionStore.allowExportCipherSuites(), 268435456
      ));
      this._encryptionStrengthField.setEditable(ITPolicy.getInteger(28, 1, 2) == 2);
      this._vfm.add(this._encryptionStrengthField);
      this._fipsAlgorithmsOnlyField = (BooleanChoiceField)(new Object(_rb.getString(52), 0, this._optionStore.restrictFIPSCipherSuites(), 268435456));
      this._fipsAlgorithmsOnlyField.setEditable(!FIPSPolicy.getBoolean(28, 7, false, true));
      this._vfm.add(this._fipsAlgorithmsOnlyField);
      this._vfm.add((Field)(new Object()));
      LabelField serverAuthenticationLabel = (LabelField)(new Object(_rb.getString(42)));
      serverAuthenticationLabel.setFont(boldFont);
      this._vfm.add(serverAuthenticationLabel);
      this._promptForServerTrustField = (BooleanChoiceField)(new Object(_rb.getString(31), 0, this._optionStore.getPromptForCertificateTrust()));
      this._vfm.add(this._promptForServerTrustField);
      this._promptForDomainNameField = (BooleanChoiceField)(new Object(_rb.getString(30), 0, this._optionStore.getPromptForDomainName()));
      this._vfm.add(this._promptForDomainNameField);
      this._vfm.add((Field)(new Object()));
      LabelField clientAuthenticationLabel = (LabelField)(new Object(_rb.getString(43)));
      clientAuthenticationLabel.setFont(boldFont);
      this._vfm.add(clientAuthenticationLabel);
      this._promptForCertificateField = (BooleanChoiceField)(new Object(_rb.getString(29), 0, this._optionStore.getPromptForCertificate()));
      this._vfm.add(this._promptForCertificateField);
      this._promptForNoClientCertField = (BooleanChoiceField)(new Object(_rb.getString(55), 0, this._optionStore.getPromptForNoClientCertificate()));
      this._vfm.add(this._promptForNoClientCertField);
      this._dataArray = new Object[0];
      KeyStore keyStore = DeviceKeyStore.getInstance();
      Enumeration enumeration = keyStore.elements(-8376547269562148933L);

      while (enumeration.hasMoreElements()) {
         KeyStoreData data = (KeyStoreData)enumeration.nextElement();
         Certificate certificate = data.getCertificate();
         if (certificate != null) {
            Arrays.add(this._dataArray, data);
         }
      }

      this._hostsToAddNames = new Object[0];
      this._hostsToAddIndexes = new int[0];
      this._hostsToRemoveNames = new Object[0];
      this._defaultClientCertField = (CertificateChoiceField)(new Object(_rb.getString(45), DeviceKeyStore.getInstance(), TrustedKeyStore.getInstance()));
      this._defaultClientCertField.setEmptyString(_rb.getFamily(), 25);
      this.updateDefaultClientCertField();
      this._vfm.add(this._defaultClientCertField);
      this._vfm.add((Field)(new Object(_rb.getString(46))));
      Enumeration hostNamesEnum = this._deviceOptionsStore.getCurrentHostnames();
      this._currentHosts = (Vector)(new Object());

      while (hostNamesEnum.hasMoreElements()) {
         String hostName = (String)hostNamesEnum.nextElement();
         KeyStoreData data = this._deviceOptionsStore.getClientCert(hostName);
         this._currentHosts.addElement(new DeviceTLSOptionsProvider$Host(hostName, data == null ? null : data.getLabel()));
      }

      this._hostDefaultsListField = (ListField)(new Object(this._deviceOptionsStore.getCurrentSize()));
      this._hostDefaultsListField.setCallback(this);
      this._vfm.add(this._hostDefaultsListField);
      return this._vfm;
   }

   @Override
   public final Verb getVerbs(Object context, Verb[] verbs) {
      Verb defaultVerb = null;
      if (this._dataArray.length > 0) {
         Array.resize(verbs, 1);
         verbs[0] = new DeviceTLSOptionsProvider$AddHostVerb(this);
      }

      Field field = this._vfm.getLeafFieldWithFocus();
      if (field == this._hostDefaultsListField) {
         int selectedItem = this._hostDefaultsListField.getSelectedIndex();
         if (selectedItem != -1) {
            int numVerbs = verbs.length + 3;
            Array.resize(verbs, numVerbs);
            verbs[numVerbs - 3] = new DeviceTLSOptionsProvider$DeleteHostVerb(this, selectedItem);
            verbs[numVerbs - 2] = new DeviceTLSOptionsProvider$ViewHostVerb(this, selectedItem);
            verbs[numVerbs - 1] = new DeviceTLSOptionsProvider$EditHostVerb(this, selectedItem);
            defaultVerb = verbs[numVerbs - 1];
         }
      }

      return defaultVerb;
   }

   @Override
   public final int getPreferredWidth(ListField listField) {
      return Display.getWidth();
   }

   @Override
   public final int indexOfList(ListField listField, String prefix, int start) {
      return -1;
   }

   @Override
   public final Object get(ListField listField, int index) {
      return listField == this._hostDefaultsListField && index >= 0 && index < this._currentHosts.size() ? this._currentHosts.elementAt(index) : null;
   }

   @Override
   public final boolean grabDataFromField(Field field, Object context) {
      int choice = this._protocolChoiceField.getSelectedIndex();
      if (choice == 0) {
         this._optionStore.useTLS(true);
         this._optionStore.useSSL(true);
      } else if (choice == 1) {
         this._optionStore.useTLS(true);
         this._optionStore.useSSL(false);
      } else if (choice == 2) {
         this._optionStore.useTLS(false);
         this._optionStore.useSSL(true);
      }

      this._optionStore.setAllowExportCipherSuites(this._encryptionStrengthField.isAffirmative());
      this._optionStore.setPromptForCertificateTrust(this._promptForServerTrustField.isAffirmative());
      this._optionStore.setPromptForCertificate(this._promptForCertificateField.isAffirmative());
      this._optionStore.setPromptForNoClientCertificate(this._promptForNoClientCertField.isAffirmative());
      this._optionStore.setPromptForDomainName(this._promptForDomainNameField.isAffirmative());
      this._optionStore.setRestrictFIPSCipherSuites(this._fipsAlgorithmsOnlyField.isAffirmative());
      int index = this._defaultClientCertField.getSelectedIndex();
      if (index >= 0) {
         this._deviceOptionsStore.setDefaultCert(this._dataArray[index]);
      } else {
         this._deviceOptionsStore.setDefaultCert(null);
      }

      this.commitFlaggedItems();
      return true;
   }

   @Override
   public final int getOrder(Object context) {
      return 0;
   }

   @Override
   public final boolean validate(Field field, Object context) {
      return true;
   }

   @Override
   public final void drawListRow(ListField listField, Graphics graphics, int index, int y, int width) {
      DeviceTLSOptionsProvider$Host host = (DeviceTLSOptionsProvider$Host)this.get(listField, index);
      if (host != null) {
         graphics.drawText(((StringBuffer)(new Object("    "))).append(host._hostName).append(" / ").append(host._hostCertificate).toString(), 0, y, 0, width);
      }
   }

   @Override
   public final boolean keyChar(char key, int time, int status) {
      Field field = this._vfm.getLeafFieldWithFocus();
      if (field == this._hostDefaultsListField) {
         int selectedItem = this._hostDefaultsListField.getSelectedIndex();
         if (selectedItem != -1) {
            if (key == 127) {
               new DeviceTLSOptionsProvider$DeleteHostVerb(this, selectedItem).invoke(null);
               return true;
            }

            if (key == '\n') {
               new DeviceTLSOptionsProvider$ViewHostVerb(this, selectedItem).invoke(null);
               return true;
            }
         }
      }

      return false;
   }

   @Override
   public final boolean keyDown(int keycode, int time) {
      return false;
   }

   @Override
   public final boolean keyUp(int keycode, int time) {
      return false;
   }

   @Override
   public final boolean keyRepeat(int keycode, int time) {
      return false;
   }

   @Override
   public final boolean keyStatus(int keycode, int time) {
      return false;
   }

   @Override
   public final void reset(Collection collection) {
   }

   @Override
   public final void elementAdded(Collection collection, Object element) {
      if (element instanceof Object) {
         KeyStoreData data = (KeyStoreData)element;
         if (data.isPrivateKeySet() && data.getCertificate() != null) {
            Arrays.add(this._dataArray, data);
            this.updateDefaultClientCertField();
         }
      }
   }

   @Override
   public final void elementUpdated(Collection collection, Object oldElement, Object newElement) {
   }

   @Override
   public final void elementRemoved(Collection collection, Object element) {
      if (element instanceof Object) {
         KeyStoreData data = (KeyStoreData)element;
         Arrays.remove(this._dataArray, data);
         this.updateDefaultClientCertField();
      }
   }

   private final void updateDefaultClientCertField() {
      if (this._dataArray.length > 0) {
         KeyStoreData data = this._deviceOptionsStore.getDefaultClientCert();
         int index = Arrays.getIndex(this._dataArray, data);
         this._defaultClientCertField.setCertificates(this.getCertificatesFromDataArray(), this.getLabelsFromDataArray(), index > 0 ? index : 0);
      }
   }

   private final Certificate[] getCertificatesFromDataArray() {
      int dataLength = this._dataArray.length;
      Certificate[] certificates = new Object[dataLength];

      for (int i = 0; i < dataLength; i++) {
         certificates[i] = this._dataArray[i].getCertificate();
      }

      return certificates;
   }

   @Override
   public final String toString() {
      return _rb.getString(38);
   }

   public DeviceTLSOptionsProvider() {
      KeyStore keyStore = DeviceKeyStore.getInstance();
      keyStore.addCollectionListener(new Object(this));
      keyStore.addIndex((KeyStoreIndex)(new Object()));
   }

   private final void flagHostForAddition(String host, int index) {
      int length = this._hostsToAddNames.length;
      Array.resize(this._hostsToAddNames, length + 1);
      Array.resize(this._hostsToAddIndexes, length + 1);
      this._hostsToAddNames[length] = host;
      this._hostsToAddIndexes[length] = index;
   }

   private final void flagHostForRemoval(String host) {
      int length = this._hostsToRemoveNames.length;
      Array.resize(this._hostsToRemoveNames, length + 1);
      this._hostsToRemoveNames[length] = host;
   }

   private final void commitFlaggedItems() {
      int length = this._hostsToRemoveNames.length;

      for (int i = 0; i < length; i++) {
         this._deviceOptionsStore.removeDefaultCert(this._hostsToRemoveNames[i]);
      }

      length = this._hostsToAddNames.length;

      for (int i = 0; i < length; i++) {
         int index = this._hostsToAddIndexes[i];
         if (this._dataArray != null && this._dataArray.length > index && this._dataArray[index] != null) {
            this._deviceOptionsStore.addDefaultCert(this._hostsToAddNames[i], this._dataArray[index]);
         }
      }
   }

   private final String[] getLabelsFromDataArray() {
      int dataLength = this._dataArray.length;
      String[] labels = new Object[dataLength];

      for (int i = 0; i < dataLength; i++) {
         labels[i] = this._dataArray[i].getLabel();
      }

      return labels;
   }
}
