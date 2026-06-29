package net.rim.device.apps.internal.manageconnections;

import java.util.Hashtable;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.i18n.ResourceBundleFamily;
import net.rim.device.api.itpolicy.ITPolicy;
import net.rim.device.api.system.WLAN;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.util.Factory;
import net.rim.device.apps.api.ribbon.FactoryRepository;
import net.rim.device.apps.api.ribbon.GlobalFactoryRepository;
import net.rim.device.apps.api.ribbon.ImageProviderRibbonComponent;
import net.rim.device.apps.api.ribbon.RibbonComponent;
import net.rim.device.apps.api.ribbon.RibbonComponentInitializer;
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
      this.addMenuItem((MenuItem)(new Object(new MobileNetworkOptionsVerb(), 310)));
      if (this._wifiEnabled) {
         this.addMenuItem((MenuItem)(new Object(new WiFiOptionsVerb(), 320)));
      }

      if (this._bluetoothEnabled) {
         this.addMenuItem((MenuItem)(new Object(new BluetoothOptionsVerb(), 330)));
      }

      FactoryRepository repos = GlobalFactoryRepository.getFactoryRepository(-4018062520840731194L);
      Factory factory = repos.getFactory("SignalLevel");
      ImageProviderRibbonComponent wirelessSignalComponent = (ImageProviderRibbonComponent)factory.createInstance(null);
      Hashtable signalParams = (Hashtable)(new Object());
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

      this.add((Field)(new Object(this._rbf.getString(15), 0)));
      this._voiceStatus = (LabelField)(new Object("", 18014398509481984L));
      this.add(this._voiceStatus);
      this._voiceConnection = (LabelField)(new Object("", 18014398509481984L));
      this.add(this._voiceConnection);
      this.add((Field)(new Object(this._rbf.getString(16), 0)));
      this._bisConnection = (LabelField)(new Object("", 18014398509481984L));
      this.add(this._bisConnection);
      this.add((Field)(new Object(this._rbf.getString(17), 0)));
      this._besConnection = (LabelField)(new Object("", 18014398509481984L));
      this.add(this._besConnection);
      this.add((Field)(new Object()));
      HorizontalFieldManager hm = (HorizontalFieldManager)(new Object());
      hm.add((Field)(new Object(this._rbf.getString(2), 0)));
      hm.add(this._wirelessSignal);
      this.add(hm);
      this._coverage = (LabelField)(new Object("", 18014398509481984L));
      this.add(this._coverage);
      this.add((Field)(new Object(this._rbf.getString(20), 0)));
      this._networkProvider = (LabelField)(new Object("", 18014398509481984L));
      this.add(this._networkProvider);
      if (this._wifiEnabled) {
         this.add((Field)(new Object()));
         hm = (HorizontalFieldManager)(new Object());
         hm.add((Field)(new Object(this._rbf.getString(21), 0)));
         hm.add(this._wifiSignal);
         this.add(hm);
         this._activeProfile = (LabelField)(new Object("", 18014398509481984L));
         this.add(this._activeProfile);
         this._wifiSSID = (LabelField)(new Object("", 18014398509481984L));
         this.add(this._wifiSSID);
         this._wifiType = (LabelField)(new Object("", 18014398509481984L));
         this.add(this._wifiType);
      }

      if (this._bluetoothEnabled) {
         this.add((Field)(new Object()));
         this.add((Field)(new Object(this._rbf.getString(34), 0)));
         this._bluetoothStatus = (LabelField)(new Object("", 18014398509481984L));
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
      this._voiceStatus.setText(((StringBuffer)(new Object())).append('\t').append(statusLabel).append(' ').append(this._data.getVoiceStatus()).toString());
      this._voiceConnection
         .setText(((StringBuffer)(new Object())).append('\t').append(connectionLabel).append(' ').append(this._data.getVoiceConnection()).toString());
      this._bisConnection
         .setText(((StringBuffer)(new Object())).append('\t').append(connectionLabel).append(' ').append(this._data.getBISConnection()).toString());
      this._besConnection
         .setText(((StringBuffer)(new Object())).append('\t').append(connectionLabel).append(' ').append(this._data.getBESConnection()).toString());
      this._coverage.setText(((StringBuffer)(new Object())).append('\t').append(this._data.getCoverage()).toString());
      this._networkProvider.setText(((StringBuffer)(new Object())).append('\t').append(this._data.getProviderString()).toString());
      if (this._wifiEnabled) {
         this._activeProfile
            .setText(
               ((StringBuffer)(new Object())).append('\t').append(this._rbf.getString(22)).append(' ').append(this._data.getWiFiActiveProfile()).toString()
            );
         this._wifiSSID
            .setText(((StringBuffer)(new Object())).append('\t').append(this._rbf.getString(23)).append(' ').append(this._data.getWiFiSSID()).toString());
         this._wifiType
            .setText(((StringBuffer)(new Object())).append('\t').append(this._rbf.getString(29)).append(' ').append(this._data.getWiFiType()).toString());
      }

      if (this._bluetoothEnabled) {
         this._bluetoothStatus.setText(((StringBuffer)(new Object())).append('\t').append(this._data.getBluetoothStatus()).toString());
      }
   }
}
