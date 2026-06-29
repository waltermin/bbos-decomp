package net.rim.device.apps.internal.calendar.ota;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.apps.api.calendar.caldb.CalendarKey;
import net.rim.device.apps.api.calendar.caldb.CalendarService;
import net.rim.device.apps.api.calendar.caldb.CalendarServiceManager;
import net.rim.device.apps.api.calendar.ota.CICALConfiguration;
import net.rim.device.apps.api.calendar.ota.CICALEventLogger;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.service.ServiceIdentifier;
import net.rim.device.apps.api.transmission.TransmissionServiceManager;
import net.rim.device.internal.proxy.Proxy;

public class CICALConfigConverter extends CICALBaseConverter implements GlobalEventListener {
   ContextObject _otaConfigContext = (ContextObject)(new Object());
   private static final long SINGLETON_ID = -3049572849057432930L;
   private static final byte[] CONFIG_COMPONENT_HEADER = new byte[]{16, 1, 1, 4};

   CICALConfigConverter() {
      Proxy.getInstance().addGlobalEventListener(this);
   }

   public static CICALConfigConverter getInstance() {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      CICALConfigConverter configConverter = null;
      synchronized (ar) {
         Object obj = ar.get(-3049572849057432930L);
         if (obj instanceof CICALConfigConverter) {
            configConverter = (CICALConfigConverter)obj;
         }
      }

      if (configConverter == null) {
         configConverter = new CICALConfigConverter();
         synchronized (ar) {
            ar.replace(-3049572849057432930L, configConverter);
            return configConverter;
         }
      } else {
         return configConverter;
      }
   }

   @Override
   public boolean canConvert(Object parameters) {
      return true;
   }

   @Override
   public byte[] convert(Object inputObject, Object contextObject) {
      if (!(inputObject instanceof CICALConfigConverter$OTAConfigEvent)) {
         return null;
      }

      CICALConfigConverter$OTAConfigEvent configEvent = (CICALConfigConverter$OTAConfigEvent)inputObject;
      DataBuffer dataBuffer = (DataBuffer)(new Object());
      int command = configEvent.getCommand();
      dataBuffer.writeByte(command);
      dataBuffer.write(CONFIG_COMPONENT_HEADER);
      switch (command) {
         case 25:
            CICALEventLogger.logEvent(1430472259, 2);
            return null;
         case 26:
            break;
         case 27:
            this.writeByteArrayField(dataBuffer, 68, configEvent.getCapabilitySettings());
            this.writeByteArrayField(dataBuffer, 69, configEvent.getConflictSettings());
            this.writeByteArrayField(dataBuffer, 81, configEvent.getUserSettings());
            break;
         case 28:
         default:
            this.writeByteField(dataBuffer, 71, configEvent.getResult());
      }

      dataBuffer.writeByte(0);
      dataBuffer.writeByte(0);
      return dataBuffer.toArray();
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public Object convert(byte[] inputBytes, Object contextObject) {
      DataBuffer data = (DataBuffer)(new Object(inputBytes, 0, inputBytes.length, true));
      byte result = 0;
      CalendarService calendarService = (CalendarService)ContextObject.get(contextObject, 6741741218837016896L);
      CICALConfigConverter$OTAConfigEvent configEvent = null;

      label155:
      try {
         byte command = data.readByte();
         if (data.readByte() != 16) {
            throw new Object("Wrong version number");
         }

         byte componentId = data.readByte();
         if (componentId == 0) {
            return null;
         }

         if (componentId != 1) {
            throw new Object("Expecting component ID");
         }

         int intData = data.readCompressedInt();
         if (intData != 1) {
            throw new Object("Expecting length of 1");
         }

         if (data.readByte() != 4) {
            throw new Object("Wrong component type: Configuration");
         }

         configEvent = new CICALConfigConverter$OTAConfigEvent(command, calendarService);
         switch (command) {
            case 25:
               result = 1;
            case 26:
               break;
            case 27:
            default:
               try {
                  while (!data.eof()) {
                     byte fieldId = data.readByte();
                     if (fieldId == 0) {
                        break;
                     }

                     switch (fieldId) {
                        case 67:
                           intData = data.readCompressedInt();
                           data.skipBytes(intData);
                           break;
                        case 68:
                        case 69:
                        default:
                           intData = data.readCompressedInt();
                           byte[] configData = new byte[intData];
                           data.readFully(configData);
                           switch (fieldId) {
                              case 68:
                                 configEvent.setCapabilitySettings(configData);
                                 break;
                              case 69:
                                 configEvent.setConflictSettings(configData);
                                 break;
                              case 81:
                                 configEvent.setUserSettings(configData);
                           }
                     }
                  }
               } finally {
                  ;
               }
         }
      } catch (Throwable var17) {
         result = 1;
         if (e instanceof Object) {
            throw (Object)e;
         }
         break label155;
      }

      if (configEvent != null) {
         configEvent.setResult(result);
      }

      return configEvent;
   }

   public void sendDeviceConfiguration(CalendarService calendarService) {
      CICALConfiguration cicalConfiguration = calendarService.getCICALConfiguration();
      CICALConfigConverter$OTAConfigEvent configEvent = new CICALConfigConverter$OTAConfigEvent(27, calendarService);
      configEvent.setCapabilitySettings(cicalConfiguration.getCapabilities(true));
      configEvent.setConflictSettings(cicalConfiguration.getConflictResolution(true));
      configEvent.setUserSettings(cicalConfiguration.getUserSettings(true));
      if (this.transmitOTAConfigEvent(configEvent, true)) {
         CICALEventLogger.logEvent(1396917831, 0);
      }
   }

   public void requestConfiguration(CalendarService calendarService) {
      this.transmitOTAConfigEvent(new CICALConfigConverter$OTAConfigEvent(26, calendarService), true);
   }

   public void ackConfigurationUpdate(byte result, CalendarService calendarService) {
      CICALConfigConverter$OTAConfigEvent configAck = new CICALConfigConverter$OTAConfigEvent(28, calendarService);
      configAck.setResult(result);
      this.transmitOTAConfigEvent(configAck, false);
   }

   private boolean transmitOTAConfigEvent(CICALConfigConverter$OTAConfigEvent event, boolean overrideUserSettings) {
      this._otaConfigContext.setPrivateFlag(-1677359872409272575L, 1);
      byte command = (byte)event.getCommand();
      byte[] commandHex = Integer.toHexString(command).getBytes();
      short commandCode = (short)((commandHex[0] << 8) + commandHex[1]);
      int code = 1128071168 | commandCode;
      CICALEventLogger.logEvent(code, 0);

      try {
         CalendarService calendarService = (CalendarService)event.getServiceIdentifier();
         CICALConfiguration configuration = calendarService.getCICALConfiguration();
         if (!configuration.isOTAConfigSupported() || !configuration.isSendSyncEnabled() && !overrideUserSettings) {
            CICALEventLogger.logEvent(1396917805, 2);
            return false;
         } else {
            ContextObject.put(this._otaConfigContext, 6741741218837016896L, calendarService);
            OTACalendarTransmissionService otaCalendarTransmissionService = (OTACalendarTransmissionService)TransmissionServiceManager.get(
               calendarService.getTransmissionServiceID()
            );
            otaCalendarTransmissionService.transmitObject("net.rim.RIMCalendarConfig", event, this._otaConfigContext);
            ContextObject.remove(this._otaConfigContext, 6741741218837016896L);
            return true;
         }
      } finally {
         CICALEventLogger.logEvent(1398363181, 2);
         return false;
      }
   }

   @Override
   public void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (guid == 6345609069135580235L || guid == -254931370837867202L && data0 != 0) {
         CICALSlowSyncConverter slowSyncConverter = CICALSlowSyncConverter.getInstance();
         ServiceIdentifier[] services = CalendarServiceManager.getInstance().getCalendarServices();

         for (int i = 0; i < services.length; i++) {
            CalendarService calendarService = (CalendarService)services[i];
            if (slowSyncConverter != null && slowSyncConverter.isSlowSyncRequired(calendarService)) {
               slowSyncConverter.startCalendarSlowSync(6345609069135580235L, calendarService);
            }
         }
      } else if (guid == -1426098722237447363L) {
         CICALEventLogger.logEvent(1346981418, 0);
         CICALSlowSyncConverter slowSyncConverter = CICALSlowSyncConverter.getInstance();
         if (slowSyncConverter != null) {
            ServiceIdentifier[] services = CalendarServiceManager.getInstance().getCalendarServices();

            for (int i = 0; i < services.length; i++) {
               CalendarService calendarService = (CalendarService)services[i];
               slowSyncConverter.startCalendarSlowSync(-1426098722237447363L, calendarService);
            }
         }
      } else if (guid == -8535280995918704471L) {
         CICALSlowSyncConverter slowSyncConverter = CICALSlowSyncConverter.getInstance();
         if (slowSyncConverter != null) {
            CalendarService calendarService = null;
            if (object0 == null) {
               ServiceIdentifier[] services = CalendarServiceManager.getInstance().getCalendarServices();

               for (int i = 0; i < services.length; i++) {
                  calendarService = (CalendarService)services[i];
                  slowSyncConverter.startCalendarSlowSync(-8535280995918704471L, calendarService, Long.MIN_VALUE, Long.MAX_VALUE, (byte)82, data0 == 1, false);
               }
            } else if (object0 instanceof Object) {
               calendarService = (CalendarService)object0;
               slowSyncConverter.startCalendarSlowSync(-8535280995918704471L, calendarService);
               return;
            }
         }
      } else if (guid == -2909397183263701247L) {
         ServiceIdentifier[] services = CalendarServiceManager.getInstance().getCalendarServices();

         for (int i = 0; i < services.length; i++) {
            CalendarService calendarService = (CalendarService)services[i];
            this.requestConfiguration(calendarService);
         }
      } else if (guid == 2937849988243900229L) {
         ServiceIdentifier[] services = CalendarServiceManager.getInstance().getCalendarServices();

         for (int i = 0; i < services.length; i++) {
            CalendarService calendarService = (CalendarService)services[i];
            this.sendDeviceConfiguration(calendarService);
         }
      } else if (guid == 1103480146496078488L && object0 != null) {
         CalendarKey[] calendarKeys = (Object[])object0;
         CICALEventLogger.logEvent(1328152576 | (byte)data0, 0);
         CalendarService[] services = new Object[0];

         for (int i = 0; i < calendarKeys.length; i++) {
            CalendarKey key = calendarKeys[i];
            CalendarService service = CalendarServiceManager.getInstance().findCalendarService(key.getCalendarServiceID());
            if (service != null) {
               Arrays.add(services, service);
            }
         }

         CICALSlowSyncConverter slowSyncConverter = CICALSlowSyncConverter.getInstance();
         if (slowSyncConverter != null) {
            for (int i = 0; i < services.length; i++) {
               CalendarService calendarService = services[i];
               calendarService.getCICALConfiguration().setOTACalendarStatus(data0 != 0);
               this.sendDeviceConfiguration(calendarService);
               if (data0 != 0) {
                  slowSyncConverter.startCalendarSlowSync(1103480146496078488L, calendarService, true);
               } else {
                  slowSyncConverter.stopCurrentCalendarSlowSync(calendarService);
                  slowSyncConverter.purgeSlowSyncStatistics(calendarService);
               }
            }
         }
      } else if (guid == 158775118060600435L && object0 != null && object1 != null && object0 instanceof Object) {
         String cid = (String)object0;
         if (cid.equals("CICAL")) {
            CalendarService service = (CalendarService)object1;
            if (!service.isSystemDefault()) {
               CICALSlowSyncConverter slowSyncConverter = CICALSlowSyncConverter.getInstance();
               if (slowSyncConverter != null) {
                  slowSyncConverter.startCalendarSlowSync(158775118060600435L, service);
               }
            }
         }
      }
   }
}
