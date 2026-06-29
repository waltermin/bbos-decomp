package net.rim.device.apps.internal.manageconnections;

import java.util.Hashtable;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.i18n.ResourceBundleFamily;
import net.rim.device.api.itpolicy.ITPolicy;
import net.rim.device.api.system.WLAN;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.util.Factory;
import net.rim.device.apps.api.ribbon.FactoryRepository;
import net.rim.device.apps.api.ribbon.GlobalFactoryRepository;
import net.rim.device.apps.api.ribbon.ImageProviderRibbonComponent;
import net.rim.device.apps.api.ribbon.RibbonComponent;
import net.rim.device.apps.api.ribbon.RibbonComponentInitializer;
import net.rim.device.apps.api.ui.VerbMenuItem;
import net.rim.device.internal.bluetooth.BluetoothME;

final class ServicesStatusScreen extends MainScreen {
   private final boolean _wifiEnabled;
   private final boolean _bluetoothEnabled;
   private ResourceBundleFamily _rbf = ResourceBundle.getBundle(-348546850453906601L, "net.rim.device.apps.internal.manageconnections.ManageConnections");
   private ServicesStatusData _data;
   private MySignalComponentField _wifiSignal;
   private MySignalComponentField _wirelessSignal;
   private LabelField _voiceStatus;
   private LabelField _voiceConnection;
   private LabelField _bisConnection;
   private LabelField _besConnection;
   private LabelField _coverage;
   private LabelField _networkProvider;
   private LabelField _activeProfile;
   private LabelField _wifiSSID;
   private LabelField _wifiType;
   private LabelField _bluetoothStatus;

   ServicesStatusScreen() {
      super(299067162755072L);
      this.setTitle(this._rbf.getString(24));
      if (WLAN.isWLANAllowed()) {
         this._wifiEnabled = true;
      } else {
         this._wifiEnabled = false;
      }

      this._bluetoothEnabled = BluetoothME.isSupported() && !ITPolicy.getBoolean(34, 1, false);
      this._data = new ServicesStatusData(this._wifiEnabled);
      this.addMenuItem(new VerbMenuItem(new MobileNetworkOptionsVerb(), 310));
      if (this._wifiEnabled) {
         this.addMenuItem(new VerbMenuItem(new WiFiOptionsVerb(), 320));
      }

      if (this._bluetoothEnabled) {
         this.addMenuItem(new VerbMenuItem(new BluetoothOptionsVerb(), 330));
      }

      FactoryRepository repos = GlobalFactoryRepository.getFactoryRepository(-4018062520840731194L);
      Factory factory = repos.getFactory("SignalLevel");
      ImageProviderRibbonComponent wirelessSignalComponent = (ImageProviderRibbonComponent)factory.createInstance(null);
      Hashtable signalParams = new Hashtable();
      signalParams.put("xOfs", "10");
      signalParams.put("yOfs", "2");
      signalParams.put("align", "left");
      signalParams.put("icon", "net_rim_Browser_SignalLevel");
      ((RibbonComponentInitializer)wirelessSignalComponent).initialize(signalParams, null);
      this._wirelessSignal = new MySignalComponentField(wirelessSignalComponent, 0);
      ((RibbonComponent)wirelessSignalComponent).setChangeListener(this._wirelessSignal);
      if (this._wifiEnabled) {
         signalParams.put("icon", "net_rim_Browser_WlanSignalLevel");
         factory = repos.getFactory("WLANSignalLevel");
         ImageProviderRibbonComponent wlanSignalComponent = (ImageProviderRibbonComponent)factory.createInstance(null);
         ((RibbonComponentInitializer)wlanSignalComponent).initialize(signalParams, null);
         this._wifiSignal = new MySignalComponentField(wlanSignalComponent, 0);
         ((RibbonComponent)wlanSignalComponent).setChangeListener(this._wifiSignal);
      }

      this.add(new LabelField(this._rbf.getString(15), 0));
      this._voiceStatus = new LabelField("", 18014398509481984L);
      this.add(this._voiceStatus);
      this._voiceConnection = new LabelField("", 18014398509481984L);
      this.add(this._voiceConnection);
      this.add(new LabelField(this._rbf.getString(16), 0));
      this._bisConnection = new LabelField("", 18014398509481984L);
      this.add(this._bisConnection);
      this.add(new LabelField(this._rbf.getString(17), 0));
      this._besConnection = new LabelField("", 18014398509481984L);
      this.add(this._besConnection);
      this.add(new SeparatorField());
      HorizontalFieldManager hm = new HorizontalFieldManager();
      hm.add(new LabelField(this._rbf.getString(2), 0));
      hm.add(this._wirelessSignal);
      this.add(hm);
      this._coverage = new LabelField("", 18014398509481984L);
      this.add(this._coverage);
      this.add(new LabelField(this._rbf.getString(20), 0));
      this._networkProvider = new LabelField("", 18014398509481984L);
      this.add(this._networkProvider);
      if (this._wifiEnabled) {
         this.add(new SeparatorField());
         hm = new HorizontalFieldManager();
         hm.add(new LabelField(this._rbf.getString(21), 0));
         hm.add(this._wifiSignal);
         this.add(hm);
         this._activeProfile = new LabelField("", 18014398509481984L);
         this.add(this._activeProfile);
         this._wifiSSID = new LabelField("", 18014398509481984L);
         this.add(this._wifiSSID);
         this._wifiType = new LabelField("", 18014398509481984L);
         this.add(this._wifiType);
      }

      if (this._bluetoothEnabled) {
         this.add(new SeparatorField());
         this.add(new LabelField(this._rbf.getString(34), 0));
         this._bluetoothStatus = new LabelField("", 18014398509481984L);
         this.add(this._bluetoothStatus);
      }

      this.update();
   }

   final ServicesStatusData getData() {
      return this._data;
   }

   public final void update() {
      this._data.update();
      String connectionLabel = this._rbf.getString(8);
      String statusLabel = this._rbf.getString(9);
      this._voiceStatus.setText('\t' + statusLabel + ' ' + this._data.getVoiceStatus());
      this._voiceConnection.setText('\t' + connectionLabel + ' ' + this._data.getVoiceConnection());
      this._bisConnection.setText('\t' + connectionLabel + ' ' + this._data.getBISConnection());
      this._besConnection.setText('\t' + connectionLabel + ' ' + this._data.getBESConnection());
      this._coverage.setText('\t' + this._data.getCoverage());
      this._networkProvider.setText('\t' + this._data.getProviderString());
      if (this._wifiEnabled) {
         this._activeProfile.setText('\t' + this._rbf.getString(22) + ' ' + this._data.getWiFiActiveProfile());
         this._wifiSSID.setText('\t' + this._rbf.getString(23) + ' ' + this._data.getWiFiSSID());
         this._wifiType.setText('\t' + this._rbf.getString(29) + ' ' + this._data.getWiFiType());
      }

      if (this._bluetoothEnabled) {
         this._bluetoothStatus.setText('\t' + this._data.getBluetoothStatus());
      }
   }
}
