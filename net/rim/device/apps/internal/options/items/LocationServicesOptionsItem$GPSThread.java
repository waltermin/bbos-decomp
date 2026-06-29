package net.rim.device.apps.internal.options.items;

import javax.microedition.location.Location;
import javax.microedition.location.LocationProvider;
import net.rim.device.api.bluetooth.BluetoothSerialPort;
import net.rim.device.api.gps.GPS;
import net.rim.device.api.gps.GPSLocationStandard;
import net.rim.device.api.ui.component.DateField;
import net.rim.device.apps.internal.options.resources.OptionsResources;

final class LocationServicesOptionsItem$GPSThread extends Thread {
   private final LocationServicesOptionsItem this$0;

   LocationServicesOptionsItem$GPSThread(LocationServicesOptionsItem _1) {
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      if (GPS.isSupportedOnCurrentNetwork() || BluetoothSerialPort.isSupported() && this.this$0.isPuckPaired()) {
         LocationProvider provider = null;
         Location location = null;
         if (!this.this$0.isRefreshRequested()) {
            this.this$0.setRefreshRequested(true);
            this.this$0.updateGPSDisplay();
            String gpsSource = null;
            String savedSource = null;
            if (this.this$0._gpsDataSourceField != null) {
               gpsSource = (String)this.this$0._gpsDataSourceField.getChoice(this.this$0._gpsDataSourceField.getSelectedIndex());
               synchronized (LocationServicesOptionsItem._gpsDataSourceStore) {
                  savedSource = (String)LocationServicesOptionsItem._gpsDataSourceStore.getContents();
                  LocationServicesOptionsItem._gpsDataSourceStore.setContents(gpsSource, 51);
                  LocationServicesOptionsItem._gpsDataSourceStore.commit();
               }
            }

            label145:
            try {
               provider = LocationProvider.getInstance(null);
            } finally {
               break label145;
            }

            if (this.this$0._gpsDataSourceField != null) {
               synchronized (LocationServicesOptionsItem._gpsDataSourceStore) {
                  LocationServicesOptionsItem._gpsDataSourceStore.setContents(savedSource, 51);
                  LocationServicesOptionsItem._gpsDataSourceStore.commit();
               }
            }

            label140:
            try {
               if (provider != null) {
                  location = provider.getLocation(180);
               }
            } finally {
               break label140;
            }

            if (location == null) {
               this.this$0.beep(false);
               this.this$0.showMsg(OptionsResources.getString(1931));
            } else {
               this.this$0.beep(true);
               String lat = GPSLocationStandard.getLatitudeString(location.getQualifiedCoordinates().getLatitude());
               lat = lat != null ? lat : " ";
               String lon = GPSLocationStandard.getLongitudeString(location.getQualifiedCoordinates().getLongitude());
               lon = lon != null ? lon : "";
               DateField fxDt = new DateField(OptionsResources.getString(1909), location.getTimestamp(), 48);
               String uncert = String.valueOf(location.getQualifiedCoordinates().getHorizontalAccuracy());
               StringBuffer uns = new StringBuffer(uncert != null ? uncert : "N/A");
               uns.append(' ').append('m');
               LocationServicesOptionsItem._locInfo.setValues(lon, lat, "" + location.getExtraInfo("satellites"), uns.toString(), fxDt.toString());
               LocationServicesOptionsItem._persist.commit();
            }

            this.this$0.setRefreshRequested(false);
            this.this$0.updateGPSDisplay();
         }
      }
   }
}
