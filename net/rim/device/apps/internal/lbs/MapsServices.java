package net.rim.device.apps.internal.lbs;

import net.rim.device.api.servicebook.ServiceBook;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.Branding;
import net.rim.device.api.system.CodeModuleManager;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.api.util.TLEUtilities;
import net.rim.device.apps.api.ribbon.ApplicationEntryPoint;
import net.rim.device.apps.api.ribbon.RibbonLauncher;
import net.rim.device.internal.proxy.Proxy;

public final class MapsServices implements GlobalEventListener {
   private boolean _showIcon = false;
   private boolean _mapsDisabled = false;
   private String _disabledMessage = null;
   private static final long GUID = -8658320507517835992L;
   private static final String MAPS_MODULE_NAME = "net_rim_bb_lbs";
   public static final String SERVICEBOOK_UID = "LBS";
   public static final String SERVICEBOOK_CID = "LbsConfig";
   private static final int ICON_VISIBLE = 36;
   private static final int DISABLE_MAPS = 37;
   private static final int DISABLE_MESSAGE = 38;
   private static final int MDS = 39;
   private static final int MAPS_URL = 61;
   private static final int MAPS_CONTEXT = 62;
   private static final int LOCATE_URL = 64;
   private static final int LOCATE_CONTEXT = 65;
   private static final int ROUTE_URL = 67;
   private static final int ROUTE_CONTEXT = 68;
   private static final int POI_URL = 70;
   private static final int POI_CONTEXT = 71;

   public final boolean isRimBranded() {
      int id = Branding.getVendorId();
      return id == -1 || id == 0 || id == 1 || id == 163;
   }

   final boolean checkAppData(ServiceRecord sr) {
      LBSOptions.setString(4638775647628867689L, null);
      this._mapsDisabled = false;
      this._disabledMessage = null;
      if (sr == null) {
         if (this.isRimBranded()) {
            this._showIcon = true;
         } else {
            this._showIcon = false;
            this._mapsDisabled = true;
         }

         this.showRibbonIcon();
         return false;
      } else {
         byte[] rawdata = sr.getApplicationData();
         if (rawdata == null) {
            return false;
         }

         boolean dataOk = false;
         DataBuffer data = new DataBuffer(rawdata, 0, rawdata.length, true);
         LBSOptions.setString(7706156913208477511L, sr.getName());
         String urlList = null;

         label113:
         try {
            while (data.available() > 0) {
               int type = data.readByte();
               switch (type) {
                  case 36:
                     this._showIcon = TLEUtilities.readIntegerField(data) != 0;
                     this.showRibbonIcon();
                     dataOk = true;
                     break;
                  case 37:
                     this._mapsDisabled = TLEUtilities.readIntegerField(data) != 0;
                     dataOk = true;
                     break;
                  case 38:
                     this._disabledMessage = TLEUtilities.readStringField(data, false);
                     dataOk = true;
                     break;
                  case 39:
                     LBSOptions.setString(4638775647628867689L, TLEUtilities.readStringField(data, false).trim());
                     dataOk = true;
                     break;
                  case 61:
                     urlList = TLEUtilities.readStringField(data, false).trim();
                     dataOk = true;
                     break;
                  case 62:
                     if (urlList != null) {
                        this.confirmURLs(urlList, TLEUtilities.readStringField(data, false).trim(), "/map2/", -7064416726417485961L);
                     }

                     urlList = null;
                     dataOk = true;
                     break;
                  case 64:
                     urlList = TLEUtilities.readStringField(data, false).trim();
                     dataOk = true;
                     break;
                  case 65:
                     if (urlList != null) {
                        this.confirmURLs(urlList, TLEUtilities.readStringField(data, false).trim(), "/locate2/", 6933732722635403673L);
                     }

                     urlList = null;
                     dataOk = true;
                     break;
                  case 67:
                     urlList = TLEUtilities.readStringField(data, false).trim();
                     dataOk = true;
                     break;
                  case 68:
                     if (urlList != null) {
                        this.confirmURLs(urlList, TLEUtilities.readStringField(data, false).trim(), "/route2/", -254277793043409026L);
                     }

                     urlList = null;
                     dataOk = true;
                     break;
                  case 70:
                     urlList = TLEUtilities.readStringField(data, false).trim();
                     dataOk = true;
                     break;
                  case 71:
                     if (urlList != null) {
                        this.confirmURLs(urlList, TLEUtilities.readStringField(data, false).trim(), "/poi1/", 3589376987760903020L);
                        this.checkPOIServerAvailability();
                     }

                     urlList = null;
                     dataOk = true;
                     break;
                  default:
                     data.skipBytes(data.readCompressedInt());
               }
            }
         } finally {
            break label113;
         }

         if (!this._showIcon) {
            this._mapsDisabled = true;
         }

         if (!dataOk) {
            LBSOptions.setString(7706156913208477511L, null);
         }

         return dataOk;
      }
   }

   final String getMapsDisabledMessage() {
      return this._disabledMessage;
   }

   final boolean isMapsDisabled() {
      return this._mapsDisabled;
   }

   @Override
   public final void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if ((guid == -4220058463650496006L || guid == 8288627527798139133L || guid == 6213587377148297993L || guid == 2522898683889177438L)
         && object0 instanceof ServiceRecord) {
         ServiceRecord record = (ServiceRecord)object0;
         if (StringUtilities.strEqualIgnoreCase(record.getCid(), "LbsConfig", 1701707776)) {
            if (guid != 2522898683889177438L && record.getType() == 0) {
               this.checkAppData(record);
               LBSApplication.setMapsStatus(!this.isMapsDisabled() && !LBSOptions.getInstance().isDisabled());
            } else {
               String nameRemoved = record.getName();
               String nameCurrent = LBSOptions.getString(7706156913208477511L, "");
               if (StringUtilities.strEqualIgnoreCase(nameRemoved, nameCurrent, 1701707776)) {
                  this.findServiceRecord();
               }
            }
         }
      }

      if (guid == 8508406279413621091L || guid == -594020114676189989L) {
         LBSOptions options = LBSOptions.getInstance();
         if (options.checkITPolicy()) {
            boolean enabled = !options.isDisabled();
            this._showIcon = enabled;
            LBSApplication.setMapsStatus(enabled);
         }
      }
   }

   static final void regsiterOnStartup() {
      MapsServices ms = getInstance();
      ms.findServiceRecord();
      ms.showRibbonIcon();
   }

   private final void confirmURLs(String urlList, String contextList, String orgContext, long serverParam) {
      int startUrlIx = 0;
      int startCxtIx = 0;
      int ix = 0;
      int endUrlIx = urlList.indexOf(44, startUrlIx);

      for (int endCxtIx = contextList.indexOf(124, startCxtIx); endUrlIx != -1; endCxtIx = urlList.indexOf(124, startCxtIx)) {
         String url = urlList.substring(startUrlIx, endUrlIx);
         String cxtList = contextList.substring(startCxtIx, endCxtIx);
         if (url != null && cxtList != null) {
            ix = 0;
            int eix = cxtList.indexOf(44, ix);

            do {
               if (eix == -1 && cxtList.length() > 0) {
                  eix = cxtList.length();
               }

               String context = cxtList.substring(ix, eix);
               if (context.equals(orgContext)) {
                  LBSOptions.setURL(serverParam, url + orgContext, false);
                  return;
               }

               ix = eix + 1;
               if (ix >= cxtList.length()) {
                  break;
               }

               eix = cxtList.indexOf(44, ix);
            } while (eix != -1);
         }

         startUrlIx = endUrlIx + 1;
         startCxtIx = endCxtIx + 1;
         endUrlIx = urlList.indexOf(44, startUrlIx);
      }

      LBSOptions.setURL(serverParam, null, false);
   }

   private final void showRibbonIcon() {
      int moduleHandle = CodeModuleManager.getModuleHandle("net_rim_bb_lbs");
      ApplicationDescriptor baseDescriptor = CodeModuleManager.getApplicationDescriptors(moduleHandle)[0];
      if (this._showIcon) {
         if (baseDescriptor != null) {
            ApplicationEntryPoint entry = new ApplicationEntryPoint(baseDescriptor);
            RibbonLauncher rib = RibbonLauncher.getInstance();
            rib.registerAction(baseDescriptor.getName(), entry);
            return;
         }
      } else if (baseDescriptor != null) {
         RibbonLauncher rib = RibbonLauncher.getInstance();
         rib.unregisterAction(baseDescriptor.getName());
      }
   }

   private final void checkPOIServerAvailability() {
      String url = LBSOptions.getURL(3589376987760903020L);
      LBSOptions.setBoolean(4717295063260546653L, false);
      if (url != null) {
         LBSApplication.setPOIMenuVisibility();
      }
   }

   static final MapsServices getInstance() {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      MapsServices instance = (MapsServices)ar.getOrWaitFor(-8658320507517835992L);
      if (instance == null) {
         instance = new MapsServices();
         ar.put(-8658320507517835992L, instance);
      }

      return instance;
   }

   private final void findServiceRecord() {
      String mapsSR = LBSOptions.getString(7706156913208477511L, null);
      ServiceBook sb = ServiceBook.getSB();
      ServiceRecord[] records = sb.findRecordsByCid("LbsConfig");
      int count = records.length;
      boolean haveSR = false;
      if (mapsSR != null) {
         for (int i = 0; i < count; i++) {
            ServiceRecord sr = records[i];
            if (StringUtilities.strEqualIgnoreCase(mapsSR, sr.getName(), 1701707776) && this.checkAppData(sr)) {
               haveSR = true;
               break;
            }
         }
      }

      if (!haveSR) {
         for (int i = 0; i < count; i++) {
            ServiceRecord sr = records[i];
            if (this.checkAppData(sr)) {
               haveSR = true;
               break;
            }
         }
      }

      if (!haveSR) {
         this.checkAppData(null);
         LBSOptions.setString(7706156913208477511L, null);
      }

      LBSApplication.setMapsStatus(!this.isMapsDisabled() && !LBSOptions.getInstance().isDisabled());
   }

   private MapsServices() {
      Proxy.getInstance().addGlobalEventListener(this);
   }
}
