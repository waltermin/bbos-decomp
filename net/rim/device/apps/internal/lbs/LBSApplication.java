package net.rim.device.apps.internal.lbs;

import net.rim.device.api.bugreport.BugReportManager;
import net.rim.device.api.gps.GPS;
import net.rim.device.api.lbs.Logger;
import net.rim.device.api.lbs.gps.GPSDevice;
import net.rim.device.api.lbs.gps.GPSProvider;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.ApplicationManager;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.CodeModuleManager;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.api.system.RIMGlobalMessagePoster;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.api.addressbook.MailingAddressModel;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.internal.lbs.maplet.MapFieldImplementation;
import net.rim.device.apps.internal.lbs.maplet.MapletCache;
import net.rim.device.apps.internal.lbs.maplet.NoMapletCache;
import net.rim.device.apps.internal.lbs.resources.LBSResources;
import net.rim.device.apps.internal.lbs.verbs.LBSStringPattern;
import net.rim.device.apps.internal.lbs.verbs.LBSVerbFactory;
import net.rim.device.apps.internal.lbs.verbs.ViewAddressVerb;

public final class LBSApplication extends UiApplication implements GlobalEventListener {
   private boolean _externalOpen;
   MapScreen _mapScreen;
   EULA _eula;
   public static long UID = -1037010874164756539L;
   private static final String APP_INVALID = "APP_INVALID";
   private static long GUID_VIEW_LOCATION = 6982071258373876883L;
   private static long GUID_VIEW_ADDRESS = 9060179257426397655L;
   private static long GUID_OPEN_DOCUMENT = 5481368234910671574L;
   private static long GUID_APP_STATUS = -127223899735346493L;
   private static final String EXTERNAL_OPEN = "externalOpen";

   public static final void main(String[] args) {
      if (args.length != 0 && !args[0].equals("externalOpen")) {
         if (args[0].equals("init")) {
            MapsServices.regsiterOnStartup();
            Logger.register(UID, "LBS");
            LBSOptions.register();
            ContentConverter.register();
            MapFieldImplementation.registerOnStartup();
            registrationStatus(true);
            MapletCache.registerOnStartup();
            NoMapletCache.registerOnStartup();
            LocationDocumentCollection.registerOnStartup();
            FavouritesManager.registerOnStartup();
            ApplicationDescriptor original = ApplicationDescriptor.currentApplicationDescriptor();
            ApplicationDescriptor descriptor = (ApplicationDescriptor)(new Object(
               original, "LBS", null, Bitmap.getBitmapResource("net_rim_bb_lbs.png"), original.getPosition(), "net.rim.device.apps.internal.lbs.LBS", 7
            ));
            ApplicationRegistry.getApplicationRegistry().put(UID, descriptor);
            String url = LBSOptions.getURL(-7064416726417485961L);
            if (url != null) {
               url.toUpperCase();
               if (url.indexOf("DEC") != -1 || url.indexOf("JAN") != -1 || url.indexOf("FEB") != -1) {
                  MapletCache.getInstance().clear();
                  LBSOptions.setURL(-7064416726417485961L, null, false);
               }
            }

            updateAppVersion();
            MapsOptionsScreen.registerOptionsProvider();
         }
      } else {
         LBSOptions._dataCount = LBSOptions.getInt(8640332184073563572L, 0);
         LBSOptions.checkSyncOTA(false);
         new LBSApplication(args.length > 0 && args[0].equals("externalOpen")).enterEventDispatcher();
      }
   }

   private static final void registrationStatus(boolean register) {
      if (register) {
         LBSVerbFactory.registerOnceOnSystemStart();
         LBSStringPattern.registerOnStartup();
      } else {
         LBSVerbFactory.unregister();
         LBSStringPattern.unregister();
      }
   }

   public static final void displayMap(Location location) {
      ApplicationDescriptor descriptor = (ApplicationDescriptor)ApplicationRegistry.getApplicationRegistry().get(UID);
      if (descriptor != null) {
         if (!ApplicationDescriptor.currentApplicationDescriptor().equals(descriptor)) {
            descriptor = (ApplicationDescriptor)(new Object(descriptor, new String[]{"externalOpen"}));
         }

         ApplicationManager applicationManager = ApplicationManager.getApplicationManager();

         try {
            int pid = applicationManager.runApplication(descriptor, true);
            RIMGlobalMessagePoster.postGlobalEvent(pid, GUID_VIEW_LOCATION, 0, 0, location, null);
         } finally {
            return;
         }
      }
   }

   public static final void openDocument(String contentType, Object content) {
      openDocument(contentType, content, 0);
   }

   public static final void openDocument(String contentType, Object content, int uid) {
      ApplicationDescriptor descriptor = (ApplicationDescriptor)ApplicationRegistry.getApplicationRegistry().get(UID);
      if (descriptor != null) {
         if (!ApplicationDescriptor.currentApplicationDescriptor().equals(descriptor)) {
            descriptor = (ApplicationDescriptor)(new Object(descriptor, new String[]{"externalOpen"}));
         }

         ApplicationManager applicationManager = ApplicationManager.getApplicationManager();

         try {
            int pid = applicationManager.runApplication(descriptor, true);
            RIMGlobalMessagePoster.postGlobalEvent(pid, GUID_OPEN_DOCUMENT, uid, 0, contentType, content);
         } finally {
            return;
         }
      }
   }

   static final void setMapsStatus(boolean enabled) {
      ApplicationDescriptor descriptor = (ApplicationDescriptor)ApplicationRegistry.getApplicationRegistry().get(UID);
      if (descriptor != null) {
         ApplicationManager applicationManager = ApplicationManager.getApplicationManager();

         try {
            int pid = applicationManager.runApplication(descriptor, false);
            RIMGlobalMessagePoster.postGlobalEvent(pid, GUID_APP_STATUS, 0, 0, new Object(enabled), null);
         } finally {
            return;
         }
      }
   }

   LBSApplication(boolean externalOpen) {
      this._externalOpen = externalOpen;
      this.addGlobalEventListener(this);
      if (!LBSOptions.getInstance().isDisabled() && (!MapsServices.getInstance().isMapsDisabled() || this._externalOpen)) {
         this.enableKeyUpEvents(true);
         this._eula = new EULA();
         if (!this._eula.showEULA()) {
            this._mapScreen = new MapScreen();
            BugReportManager.setReportLocation("lbsfeedback@rim.com");
            BugReportManager.setApplicationReportable(new LBSApplication$LBSBugReportable(this._mapScreen._mapField));
            setPOIMenuVisibility();
            this.pushScreen(this._mapScreen);
         }
      }
   }

   @Override
   public final void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      boolean showMapScreen = false;
      if (guid == GUID_VIEW_LOCATION) {
         if (object0 instanceof Object) {
            ContextObject context = (ContextObject)object0;
            int latitude = context.get(-200747095229876690L);
            int longitude = context.get(6606581876924152793L);
            int zoom = context.get(581052036187634982L);
            object0 = new Location(latitude, longitude, zoom);
         }

         if (object0 instanceof Location) {
            this.showEULA();
            Location location = (Location)object0;
            this._mapScreen.showLocation(location);
            showMapScreen = true;
         }
      }

      if (guid == GUID_VIEW_ADDRESS && object0 instanceof Object) {
         this.showEULA();
         MailingAddressModel mailingAddressModel = (MailingAddressModel)object0;
         ViewAddressVerb.doAction(mailingAddressModel, null);
      }

      if (guid == GUID_OPEN_DOCUMENT) {
         this.showEULA();
         if (object0 instanceof Object) {
            this._mapScreen.openDocument((String)object0, object1, data0);
         }

         showMapScreen = true;
      }

      if (showMapScreen && Ui.getUiEngine().getActiveScreen() != this._mapScreen) {
         this.popScreen(this._mapScreen);
         this.pushScreen(this._mapScreen);
      }

      if ((guid == -4220058463650496006L || guid == 8288627527798139133L || guid == 6213587377148297993L || guid == 2522898683889177438L)
         && object0 instanceof Object) {
         ServiceRecord record = (ServiceRecord)object0;
         if (StringUtilities.strEqualIgnoreCase(record.getUid(), "SYNC", 1701707776)) {
            LBSOptions.checkSyncOTA(false);
         }
      }

      if (guid == GUID_APP_STATUS) {
         this.handleAppStatus(object0);
      }
   }

   private final void handleAppStatus(boolean enable) {
      if (!enable) {
         GPSProvider.getInstance().stopReporting(null);
         if (this._mapScreen != null) {
            GPSProvider.getInstance().removeLocationListener(this._mapScreen);
         }

         this.invokeLater(new LBSApplication$1(this));
      }
   }

   private final void showEULA() {
      if (this._eula == null) {
         this._eula = new EULA();
      }

      if (!this._eula.confirmAgreement()) {
         System.exit(0);
      }
   }

   @Override
   public final void deactivate() {
      this._mapScreen.onDeactivate();
   }

   @Override
   public final void activate() {
      boolean disabledByServiceBook = MapsServices.getInstance().isMapsDisabled();
      if (LBSOptions.getInstance().isDisabled() || disabledByServiceBook && !this._externalOpen) {
         String disableMessage = LBSResources.getString(194);
         String disableMessageSR = MapsServices.getInstance().getMapsDisabledMessage();
         if (disabledByServiceBook) {
            if (disableMessageSR != null) {
               disableMessage = disableMessageSR;
            } else {
               disableMessage = LBSResources.getString(485);
            }
         }

         Dialog.alert(disableMessage);
         this.removeGlobalEventListener(this);
         System.exit(0);
      }

      if (LBSOptions.getString(7544824750646866888L, "0").equals("APP_INVALID")) {
         runApplicationUpdate();
      } else {
         if (this._eula != null && this._eula.showEULA()) {
            if (!this._eula.confirmAgreement()) {
               System.exit(0);
            } else {
               this._mapScreen = new MapScreen();
               this.pushScreen(this._mapScreen);
            }
         }

         this._mapScreen.onActivate();
      }

      GPSProvider.init();
      String id = GPS.getLAPIDataSource();
      GPSDevice inUse = GPSProvider.getInstance().getDeviceInUse();
      if (inUse == null) {
         GPSProvider.getInstance().setDeviceToUse(id);
         GPSDevice device = GPSProvider.getInstance().getDeviceInUse();
         if (device == null) {
            device = GPSDevice.NO_DEVICE;
            GPS.setLAPIDataSource(device.getName());
         }

         LBSOptions.setString(6531936621597631078L, device.getDeviceID().toString());
      } else {
         GPSDevice[] devices = GPSProvider.getInstance().getLocationDevices(true);
         boolean available = false;

         for (int i = 0; i < devices.length; i++) {
            if (devices[i].getName().equals(inUse.getName())) {
               available = true;
               break;
            }
         }

         if (!available) {
            GPSProvider.getInstance().setDeviceToUse(GPSDevice.NO_DEVICE);
            GPSDevice device = GPSDevice.NO_DEVICE;
            LBSOptions.setString(6531936621597631078L, device.getDeviceID().toString());
            return;
         }

         if (!inUse.equals(id)) {
            GPSProvider.getInstance().setDeviceToUse(id);
            GPSDevice device = GPSProvider.getInstance().getDeviceInUse();
            LBSOptions.setString(6531936621597631078L, device.getDeviceID().toString());
            return;
         }
      }
   }

   private static final void updateAppVersion() {
      int handle = CodeModuleManager.getModuleHandle("net_rim_bb_lbs");
      String version = "";
      if (handle != 0) {
         version = CodeModuleManager.getModuleVersion(handle);
      }

      String currentVersion = LBSOptions.getString(7544824750646866888L, "0");
      if (!version.equals(currentVersion)) {
         MapletCache.getInstance().clear();
      }

      if (currentVersion.equals("0")) {
         version = EULA.getTaggedVersion(version);
      }

      LBSOptions.setStringNoBackup(7544824750646866888L, version);
      LBSOptions.getInstance().commit();
   }

   public static final void runApplicationUpdate() {
      LBSOptions.setStringNoBackup(7544824750646866888L, "APP_INVALID");
      String url = LBSOptions.getString(-9040565055715388692L, "http://maps.blackberry.com");
      EventLogger.logEvent(UID, ((StringBuffer)(new Object("Client to receive new app from "))).append(url).toString().getBytes(), 5);
      synchronized (Application.getEventLock()) {
         Application.getApplication().invokeLater(new LBSApplication$2(url));
      }
   }

   public static final void setPOIMenuVisibility() {
      boolean showPOI = LBSOptions.getBoolean(4717295063260546653L, false);
      System.out.println(((StringBuffer)(new Object("pingPOIserver, showPOI? "))).append(showPOI).toString());
      if (RadioInfo.getActiveWAFs() != 0) {
         if (!showPOI) {
            if (MapsServices.getInstance().isRimBranded() || LBSOptions.getURL(3589376987760903020L) != null) {
               LBSOptions.setBoolean(4717295063260546653L, true);
               return;
            }

            if (!MapsServices.getInstance().isRimBranded() && LBSOptions.getURL(3589376987760903020L) == null) {
               LBSOptions.setBoolean(4717295063260546653L, false);
            }
         }
      }
   }
}
