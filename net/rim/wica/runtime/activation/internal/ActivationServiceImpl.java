package net.rim.wica.runtime.activation.internal;

import java.util.Enumeration;
import java.util.Vector;
import net.rim.blackberry.api.mail.AttachmentHandler;
import net.rim.blackberry.api.mail.AttachmentHandlerManager;
import net.rim.blackberry.api.mail.Message;
import net.rim.blackberry.api.mail.SupportedAttachmentPart;
import net.rim.device.api.itpolicy.ITPolicy;
import net.rim.device.api.servicebook.ServiceBook;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.ApplicationManager;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.StringTokenizer;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.api.options.OptionsProviderRegistration;
import net.rim.wica.runtime.activation.ActivationService;
import net.rim.wica.runtime.event.EventListener;
import net.rim.wica.runtime.event.EventService;
import net.rim.wica.runtime.logging.Logger;
import net.rim.wica.runtime.management.AGInfo;
import net.rim.wica.runtime.management.ManagementService;
import net.rim.wica.runtime.management.RuntimeInfo;
import net.rim.wica.runtime.provisioning.ProvisioningEvent;
import net.rim.wica.runtime.resources.RuntimeResources;
import net.rim.wica.runtime.service.ServiceProvider;
import net.rim.wica.runtime.service.Serviceable;
import net.rim.wica.runtime.service.Startable;
import net.rim.wica.runtime.ui.HomeScreenEntry;
import net.rim.wica.runtime.ui.HomeScreenUtilities;
import net.rim.wica.runtime.util.Util;

public final class ActivationServiceImpl implements ActivationService, Serviceable, Startable, EventListener, HomeScreenEntry, AttachmentHandler {
   private ApplicationDescriptor _descriptor;
   private RuntimeInfo _runtimeInfo;
   private AGMainScreen _screen;
   private MDSOptionsProvider _optionsProvider;
   private int _state;
   private Bitmap _entrypointIcon;
   private Integer _cachedEntrypointPosition;
   private ServiceProvider _services;
   private ManagementService _managementService;
   private int _activationProgressStage = -1;
   private String _activationProgressString;
   static final int ACTIVATION_MAX_STAGE = 6;
   static final int STATE_DEACTIVATED = 0;
   static final int STATE_ACTIVATED = 1;
   static final int STATE_ACTIVATING = 2;
   static final int STATE_FAILED = 4;
   static final int STATE_DISABLED = 8;
   private static final String IPPP_SERVICE_CID = "IPPP";
   private static final int[] EVENTS = new int[]{
      100,
      108,
      109,
      112,
      111,
      1870004480,
      1883333995,
      12766576,
      1802466817,
      185683045,
      1870004480,
      1738564971,
      12743326,
      12956929,
      -1637413882,
      336286475,
      272890957,
      757619,
      -1637413882,
      -62004469
   };
   private static final String EMAIL_URL_TOKEN = "RegistrationURL";
   private static final String EMAIL_TRANSPORT_UID_TOKEN = "IPPPUID";
   private static final char EMAIL_SEPARATOR = '=';
   private static final String ENTRYPOINT_ID = "net.rim.mds.runtime.activation";
   private static final String ENTRYPOINT_ICON_RESOURCE = "activation_icon.png";
   private static final int ENTRYPOINT_POSITION = 60;
   static Class class$net$rim$wica$runtime$management$ManagementService;
   static Class class$net$rim$wica$runtime$event$EventService;

   final void activate(AGInfo agInfo) {
      this._managementService.register(agInfo);
   }

   final String getActivationProgressString() {
      return this._activationProgressString;
   }

   final int getActivationProgressStage() {
      return this._activationProgressStage;
   }

   final int getState() {
      return this._state;
   }

   final void registerUI(AGMainScreen screen) {
      this._screen = screen;
   }

   final void unregisterUI(AGMainScreen screen) {
      this._screen = null;
   }

   final RuntimeInfo getRuntimeInfo() {
      return this._runtimeInfo;
   }

   final boolean disallowUserInitiatedActivation() {
      return !DeviceInfo.isSimulator() && (this.getTransportRecords().length == 0 || ITPolicy.getBoolean(44, 5, false));
   }

   final ServiceRecord[] getTransportRecords() {
      ServiceRecord[] result = new Object[0];
      ServiceRecord[] records = ServiceBook.getSB().findRecordsByCid("IPPP");
      if (DeviceInfo.isSimulator()) {
         return records;
      }

      for (int i = records.length - 1; i >= 0; i--) {
         if (this.validateEnterpriseRecord(records[i])) {
            Arrays.add(result, records[i]);
         }
      }

      return result;
   }

   final boolean validateEnterpriseRecord(ServiceRecord record) {
      return record.getEncryptionMode() == 2;
   }

   final boolean validatePublicRecord(ServiceRecord record) {
      return !this.validateEnterpriseRecord(record);
   }

   final ServiceRecord getTransportRecord(String uid) {
      return ServiceBook.getSB().getRecordByUidAndCid(uid, "IPPP");
   }

   final ServiceRecord getDefaultTransportRecord() {
      ServiceRecord selected = null;
      ServiceRecord[] records = this.getTransportRecords();
      if (DeviceInfo.isSimulator()) {
         for (int i = records.length - 1; i >= 0; i--) {
            if ("MDS Transport (Sim)".equals(records[i].getName())) {
               selected = records[i];
            }
         }
      }

      if (selected == null && records.length > 0) {
         selected = records[0];
      }

      return selected;
   }

   @Override
   public final void stop() {
      EventService eventService = (EventService)this._services
         .getService(
            class$net$rim$wica$runtime$event$EventService == null
               ? (class$net$rim$wica$runtime$event$EventService = class$("net.rim.wica.runtime.event.EventService"))
               : class$net$rim$wica$runtime$event$EventService
         );
      eventService.removeListener(EVENTS, this);
      if (this._screen != null) {
         this._screen.exit();
      }

      this.unregisterRibbon();
      OptionsProviderRegistration.deRegisterOptionsProvider(this._optionsProvider);
      this.refreshOptionsList();
      AttachmentHandlerManager.getInstance().removeAttachmentHandler(this);
   }

   @Override
   public final void handleEvent(Object sender, int event, int eventParam, Object data) {
      switch (event) {
         case 100:
            if (eventParam == 12) {
               this.setState(2);
               return;
            }

            if (eventParam == 1) {
               this.setState(1);
               this.activationRegistrationEvent();
               return;
            }

            if (eventParam == 11) {
               this.setState(4);
               return;
            }

            if (eventParam == 2) {
               this.setState(0);
               return;
            }
            break;
         case 108:
            this._activationProgressStage = eventParam;
            this._activationProgressString = (String)data;
            this.updateRibbon();
            if (this._screen != null) {
               this._screen.activationProgressEvent(this._activationProgressString, this._activationProgressStage);
            }
            break;
         case 109:
            this.setState(1);
            this.activationRegistrationEvent();
            return;
         case 111:
            this.setState(8);
            return;
         case 112:
            this.setState(1);
            return;
         case 500:
            if (this._runtimeInfo.getDoingRegistration()) {
               ProvisioningEvent pe = (ProvisioningEvent)data;
               if (pe.getType() == 2) {
                  this._activationProgressStage++;
                  this.updateRibbon();
                  if (this._screen != null) {
                     this._screen.activationProgressIncrementEvent();
                     return;
                  }
               }
            }
      }
   }

   @Override
   public final void start() {
      this._optionsProvider = new MDSOptionsProvider(this);
      OptionsProviderRegistration.registerOptionsProvider(this._optionsProvider);
      this.refreshOptionsList();
      AttachmentHandlerManager.getInstance().addAttachmentHandler(this);
      EventService eventService = (EventService)this._services
         .getService(
            class$net$rim$wica$runtime$event$EventService == null
               ? (class$net$rim$wica$runtime$event$EventService = class$("net.rim.wica.runtime.event.EventService"))
               : class$net$rim$wica$runtime$event$EventService
         );
      eventService.addListener(EVENTS, this);
      if (this._runtimeInfo.getDoingRegistration() || this._runtimeInfo.isReactivate()) {
         this.setState(2);
      } else if (this._runtimeInfo.isRegistered()) {
         this.setState(1);
      } else if (this._runtimeInfo.getNewAGInfo() != null) {
         this.setState(4);
      } else {
         this.setState(0);
      }
   }

   @Override
   public final void setServices(ServiceProvider services) {
      this._services = services;
      this._managementService = (ManagementService)this._services
         .getService(
            class$net$rim$wica$runtime$management$ManagementService == null
               ? (class$net$rim$wica$runtime$management$ManagementService = class$("net.rim.wica.runtime.management.ManagementService"))
               : class$net$rim$wica$runtime$management$ManagementService
         );
      this._runtimeInfo = this._managementService.getRuntimeInfo();
   }

   @Override
   public final Bitmap getEntryBitmap() {
      return this._entrypointIcon;
   }

   @Override
   public final Bitmap getEntryBitmapFocus() {
      return this._entrypointIcon;
   }

   @Override
   public final Integer getEntryDefaultPosition() {
      return this._cachedEntrypointPosition;
   }

   @Override
   public final String getEntryDescription() {
      String result = RuntimeResources.getString(34);
      if (this._activationProgressStage >= 0) {
         float percent = this._activationProgressStage / 1086324736 * 1120403456;
         result = ((StringBuffer)(new Object())).append(result).append(" - ").toString();
         result = ((StringBuffer)(new Object())).append(result).append((int)percent).toString();
         result = ((StringBuffer)(new Object())).append(result).append("%").toString();
      }

      return result;
   }

   @Override
   public final String getEntryId() {
      return "net.rim.mds.runtime.activation";
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void run() {
      try {
         ApplicationManager manager = ApplicationManager.getApplicationManager();
         manager.runApplication(this._descriptor);
      } catch (Throwable var3) {
         new Object(e.getMessage());
         return;
      }
   }

   @Override
   public final void pushActivationScreen() {
      new ActivationServiceImpl$PushActivationScreen(this);
   }

   @Override
   public final void invokeApplication() {
      this.run();
   }

   @Override
   public final boolean supports(String contentType) {
      return contentType.endsWith(RuntimeResources.getString(136));
   }

   @Override
   public final String menuString() {
      return RuntimeResources.getString(146);
   }

   @Override
   public final void run(Message msg, SupportedAttachmentPart part) {
      if (part.hasMore() && !part.moreRequestSent()) {
         Application.getApplication().invokeLater(new ActivationServiceImpl$1(this, part));
      } else {
         Application.getApplication().invokeLater(new ActivationServiceImpl$2(this, part));
      }
   }

   private final void registerRibbon() {
      HomeScreenUtilities.registerEntry(this);
   }

   private final void unregisterRibbon() {
      HomeScreenUtilities.unregisterEntry(this);
   }

   public ActivationServiceImpl() {
      this._cachedEntrypointPosition = (Integer)(new Object(60));
      this._entrypointIcon = RuntimeResources.getBitmapResource("activation_icon.png");
      this._descriptor = (ApplicationDescriptor)(new Object(
         ApplicationDescriptor.currentApplicationDescriptor(),
         null,
         new String[]{"activation"},
         this._entrypointIcon,
         60,
         "net.rim.wica.runtime.resources.Runtime",
         34
      ));
   }

   private final void setState(int state) {
      switch (state) {
         case 0:
         case 3:
            break;
         case 1:
         case 4:
            this.unregisterRibbon();
            break;
         case 2:
         default:
            this._activationProgressStage = -1;
            this._activationProgressString = "";
            this.registerRibbon();
            EventService eventService = (EventService)this._services
               .getService(
                  class$net$rim$wica$runtime$event$EventService == null
                     ? (class$net$rim$wica$runtime$event$EventService = class$("net.rim.wica.runtime.event.EventService"))
                     : class$net$rim$wica$runtime$event$EventService
               );
            eventService.addListener(500, this);
      }

      this._state = state;
      this.activationDrawableEvent();
   }

   private final void activationDrawableEvent() {
      if (this._screen != null) {
         this._screen.activationStatusEvent();
      }
   }

   private final void activationRegistrationEvent() {
      if (this._screen != null) {
         this._screen.activationStateEvent();
      }

      EventService eventService = (EventService)this._services
         .getService(
            class$net$rim$wica$runtime$event$EventService == null
               ? (class$net$rim$wica$runtime$event$EventService = class$("net.rim.wica.runtime.event.EventService"))
               : class$net$rim$wica$runtime$event$EventService
         );
      eventService.removeListener(500, this);
   }

   private final void updateRibbon() {
      HomeScreenUtilities.updateEntry(this);
   }

   private final void invokeRegistration(SupportedAttachmentPart part) {
      boolean alreadyActivatedWithSB = this._managementService.getRuntimeInfo().isRegistered()
         && Util.isNonEmptyString(this._managementService.getRuntimeInfo().getDefaultAGInfo().getMDSUID());
      if (!alreadyActivatedWithSB && !this.disallowUserInitiatedActivation()) {
         String partText = (String)(new Object((byte[])part.getContent()));
         Vector lines = (Vector)(new Object());
         StringTokenizer st = (StringTokenizer)(new Object(partText, "\n"));

         while (st.hasMoreTokens()) {
            lines.addElement(st.nextToken());
         }

         String serverUrl = null;
         String transportUid = null;
         Enumeration e = lines.elements();

         while (e.hasMoreElements()) {
            String token = (String)e.nextElement();
            int equalsIndex = token.indexOf(61);
            if (equalsIndex != -1) {
               if (StringUtilities.startsWithIgnoreCase(token, "RegistrationURL")) {
                  serverUrl = token.substring(equalsIndex + 1).trim();
               } else if (StringUtilities.startsWithIgnoreCase(token, "IPPPUID")) {
                  transportUid = token.substring(equalsIndex + 1).trim();
               }
            }
         }

         if (Util.isValidURL(serverUrl) && Util.isNonEmptyString(transportUid)) {
            ServiceRecord record = this.getTransportRecord(transportUid);
            if (record != null && this.validateEnterpriseRecord(record)) {
               AGInfo serverInfo = new AGInfo();
               serverInfo.setAGCompactMsgURL(AGInfo.createAGCompactMsgURL(serverUrl));
               serverInfo.setAGRegURL(serverUrl);
               serverInfo.setIPPP_UID(transportUid);
               Logger.log(
                  ((StringBuffer)(new Object("Email-Initiated Activation: URL=")))
                     .append(serverInfo.getAgRegURL())
                     .append(", Transport=")
                     .append(serverInfo.getIPPP_UID())
                     .toString()
               );
               this.activate(serverInfo);
               return;
            }
         } else {
            Logger.log("Email-Initiated Activation Failed");
         }
      }
   }

   private final void refreshOptionsList() {
      ApplicationManager appManager = ApplicationManager.getApplicationManager();
      ApplicationDescriptor[] descriptors = appManager.getVisibleApplications();

      for (int i = 0; i < descriptors.length; i++) {
         if (descriptors[i].getModuleName().equals("net_rim_bb_options_app")) {
            appManager.postGlobalEvent(appManager.getProcessId(descriptors[i]), -7464003439710973532L, 0, 0, null, null);
            return;
         }
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   static final Class class$(String x0) {
      try {
         return Class.forName(x0);
      } catch (Throwable var3) {
         throw new Object(x1.getMessage());
      }
   }
}
