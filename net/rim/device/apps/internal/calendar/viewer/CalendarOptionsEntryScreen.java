package net.rim.device.apps.internal.calendar.viewer;

import java.util.Enumeration;
import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.system.RIMGlobalMessagePoster;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.component.ChoiceField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.ObjectChoiceField;
import net.rim.device.api.ui.component.ObjectListField;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.ui.theme.Tag;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.LongHashtable;
import net.rim.device.apps.api.calendar.caldb.CalDB;
import net.rim.device.apps.api.calendar.caldb.CalendarFolder;
import net.rim.device.apps.api.calendar.caldb.CalendarKey;
import net.rim.device.apps.api.calendar.caldb.CalendarOptions;
import net.rim.device.apps.api.calendar.caldb.CalendarService;
import net.rim.device.apps.api.calendar.caldb.CalendarServiceManager;
import net.rim.device.apps.api.calendar.ota.CICALConfiguration;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.options.MainScreenOptionsListItem;
import net.rim.device.apps.api.reminders.ReminderManager;
import net.rim.device.apps.api.service.ServiceIdentifier;
import net.rim.device.apps.api.utility.framework.VerbToMenu;
import net.rim.device.apps.internal.calendar.eventprovider.ViewByLUID;
import net.rim.device.apps.internal.calendar.sync.CalendarSupportCollection;
import net.rim.device.internal.ui.component.ColorChoiceField;
import net.rim.device.internal.ui.component.ColorChoiceField$NamedColor;

final class CalendarOptionsEntryScreen extends MainScreenOptionsListItem implements CalendarOptionsPropertiesScreen$ChangeListener {
   private CalendarOptions _calendarOptions = CalendarOptions.getOptions();
   private Field _generalOptionsField;
   private VerticalFieldManager _numberOfEntriesFieldManager;
   private ChoiceField _colorModeField;
   private ColorChoiceField _calendarPrivateColourField;
   private VerticalFieldManager _reminderLogManager;
   private VerticalFieldManager _configSettingsManager;
   private ObjectListField _reminderStatusLog;
   private static Tag OPTIONS_SECTION_AREA_TAG = Tag.create("options-section-area");

   private CalendarOptionsEntryScreen() {
      super(CalendarApp._rb.getString(360), null);
   }

   @Override
   protected final void populateMainScreen(MainScreen screen) {
      Manager section = new VerticalFieldManager(562949953421312L);
      screen.add(section);
      section.setTag(OPTIONS_SECTION_AREA_TAG);
      this._generalOptionsField = new CalendarOptionsEntryScreen$1(this, CalendarApp._rb.getString(662), 1170935903116328960L);
      section.add(this._generalOptionsField);
      section = this.createSection(CalendarApp._rb.getString(663), screen);
      this.addCalendarFields(section);
      this._reminderLogManager = new VerticalFieldManager();
      this._configSettingsManager = new VerticalFieldManager();
      screen.add(this._configSettingsManager);
      screen.add(this._reminderLogManager);
      this._reminderStatusLog = new ObjectListField(6);
   }

   protected final Manager createSection(String title, Manager parent) {
      Manager section = new VerticalFieldManager(1153484454560268288L);
      section.setTag(OPTIONS_SECTION_AREA_TAG);
      if (title != null) {
         LabelField titleField = new LabelField(title, 1152921504606846976L);
         titleField.setFont(titleField.getFont().derive(1));
         section.add(titleField);
      }

      section.add(new SeparatorField());
      parent.add(section);
      return section;
   }

   protected final void addCalendarFields(Manager section) {
      this._numberOfEntriesFieldManager = new VerticalFieldManager();
      section.add(this._numberOfEntriesFieldManager);
      ServiceIdentifier[] services = CalendarServiceManager.getInstance().getCalendarServices(true, true);

      for (int i = 0; i < services.length; i++) {
         CalendarService service = (CalendarService)services[i];
         if (!service.isSystemDefault() || services.length <= 1) {
            LongHashtable folderTable = service.getCalendarFolders();
            Enumeration folders = folderTable.elements();

            while (folders.hasMoreElements()) {
               CalendarFolder folder = (CalendarFolder)folders.nextElement();
               CalendarKey key = new CalendarKey(service.getUniqueServiceID(), folder.getFolderID());
               CalendarOptionsEntryScreen$CalendarFolderField dbEntriesField = new CalendarOptionsEntryScreen$CalendarFolderField(
                  service.getServiceName() + folder.getFolderNameSuffix(), "", key
               );
               dbEntriesField.setCookie(key);
               this._numberOfEntriesFieldManager.add(dbEntriesField);
            }
         }
      }
   }

   @Override
   protected final boolean invokeOptionsAction(int action) {
      if (action == 1) {
         Field fieldWithFocus = super._mainScreen.getLeafFieldWithFocus();
         if (fieldWithFocus == this._generalOptionsField) {
            CalendarOptionsScreen.showEditOptionsScreen();
            return true;
         }

         if (fieldWithFocus instanceof CalendarOptionsEntryScreen$CalendarFolderField) {
            Object cookie = fieldWithFocus.getCookie();
            if (cookie instanceof CalendarKey) {
               CalendarKey calendarKey = (CalendarKey)cookie;
               if (CalendarServiceManager.getInstance().findCalendarService(calendarKey.getCalendarServiceID()) != null) {
                  CalendarOptionsPropertiesScreen.showEditPropertiesScreen(calendarKey, this);
                  return true;
               }
            }
         }
      }

      return super.invokeOptionsAction(action);
   }

   @Override
   public final boolean trackwheelClick(int status, int time) {
      return this.invokeOptionsAction(1);
   }

   @Override
   protected final void addScreenVerbs(VerbToMenu verbToMenu, int instance) {
      super.addScreenVerbs(verbToMenu, instance);
      if (this._reminderStatusLog.getSize() > 0) {
         verbToMenu.addVerb(new CalendarOptionsEntryScreen$CopyReminderLogVerb());
      }
   }

   public static final void showEditOptionsScreen() {
      CalendarOptionsEntryScreen optionsScreen = new CalendarOptionsEntryScreen();
      optionsScreen.perform(6099736323056465049L, null);
   }

   @Override
   public final boolean keyChar(char key, int status, int time) {
      super.keyChar(key, status, time);
      return key == '\n' ? this.invokeOptionsAction(1) : key == ' ' || key == 27;
   }

   @Override
   public final boolean confirm(Verb verb, Object context) {
      boolean isDirty = false;
      if (this._colorModeField != null && this._colorModeField.isDirty()) {
         isDirty = true;
         this._calendarOptions.setAppointmentColorMode(this._colorModeField.getSelectedIndex());
      }

      if (this._calendarPrivateColourField != null && this._calendarPrivateColourField.isDirty()) {
         isDirty = true;
         this._calendarOptions.setPrivateAppointmentsColour(this._calendarPrivateColourField.getSelectedColor());
      }

      if (isDirty) {
         this._calendarOptions.commit();
         RIMGlobalMessagePoster.postGlobalEvent(5483692278053761660L, 0, 0, null, null);
      }

      return true;
   }

   @Override
   public final boolean openProductionBackdoor(int backdoorCode) {
      CICALConfiguration configuration = CICALConfiguration.getDefaultConfiguration();
      CalendarSupportCollection supportReportCollection = null;
      switch (backdoorCode) {
         case 1280657732:
            ViewByLUID.register();
            Dialog.alert("View by UID is enabled.");
            break;
         case 1297045061:
            CalendarService defaultCalendarService = CalendarServiceManager.getInstance().getDefaultCalendarService();
            int result = Dialog.ask(3, "Move all appointments in base system calendar to " + defaultCalendarService.getServiceName() + "?");
            if (result == 4) {
               defaultCalendarService.purgeFromBaseSystemCalendarService();
               Dialog.alert("All appointments moved.");
            }

            return true;
         case 1380140615:
            if (configuration.isOTAConfigSupported()) {
               RIMGlobalMessagePoster.postGlobalEvent(-2909397183263701247L, 0, 0, null, null);
               Dialog.alert("BES Configuration Requested.");
            } else {
               Dialog.alert("Wireless Configuration Not Supported.");
            }
            break;
         case 1381188948:
            CalendarKey[] wipedKeys = new CalendarKey[0];
            String message = null;
            String serviceName = null;
            ServiceIdentifier[] services = CalendarServiceManager.getInstance().getCalendarServices(true);

            for (int i = 0; i < services.length; i++) {
               CalendarService calendarService = (CalendarService)services[i];
               if (calendarService != null) {
                  CalendarKey key = new CalendarKey(calendarService.getUniqueServiceID(), calendarService.getUniqueServiceID());
                  CalDB calDB = calendarService.getCalendarDatabase();
                  if (calendarService.isSystemDefault()) {
                     calDB.removeAll();
                  } else {
                     CICALConfiguration cicalConfig = calendarService.getCICALConfiguration();
                     if (calendarService.getServiceKey() instanceof ServiceRecord) {
                        ServiceRecord sr = (ServiceRecord)calendarService.getServiceKey();
                        serviceName = sr.getName();
                     } else {
                        serviceName = "Unknown";
                     }

                     if (cicalConfig != null && cicalConfig.isOTACalendarEnabled()) {
                        message = MessageFormat.format(CalendarApp._rb.getString(14), new String[]{serviceName});
                        int result = Dialog.ask(3, message);
                        if (result == 4) {
                           cicalConfig.suspendOutboundOTATraffic(true);
                           calDB.removeAll();
                           cicalConfig.suspendOutboundOTATraffic(false);
                           Arrays.add(wipedKeys, key);
                           message = MessageFormat.format(CalendarApp._rb.getString(15), new String[]{serviceName});
                           Dialog.alert(message);
                        }
                     } else {
                        message = MessageFormat.format(CalendarApp._rb.getString(16), new String[]{serviceName});
                        int result = Dialog.ask(3, message);
                        if (result == 4) {
                           calDB.removeAll();
                           message = MessageFormat.format(CalendarApp._rb.getString(651), new String[]{serviceName});
                           Dialog.alert(message);
                        }
                     }
                  }
               }
            }

            RIMGlobalMessagePoster.postGlobalEvent(1103480146496078488L, 1, 0, wipedKeys, null);
            return true;
         case 1396917831:
            if (configuration.isOTAConfigSupported()) {
               RIMGlobalMessagePoster.postGlobalEvent(2937849988243900229L, 0, 0, null, null);
               Dialog.alert("Device Configuration sent.");
            } else {
               Dialog.alert("Wireless Configuration Not Supported.");
            }
            break;
         case 1397904204:
            this._reminderLogManager.deleteAll();
            this._reminderLogManager.add(new SeparatorField());
            ReminderManager rm = ReminderManager.getInstance();
            if (rm != null) {
               this._reminderStatusLog.set(rm.getReminderLog());
            }

            this._reminderLogManager.add(this._reminderStatusLog);
            return true;
         case 1398100036:
            supportReportCollection = CalendarSupportCollection.getInstance();
            Dialog.alert("Calendar Detailed Report Enabled.");
            if (supportReportCollection != null) {
               supportReportCollection.setMode(1);
               supportReportCollection.enableReportGeneration(true);
            }
            break;
         case 1398100046:
            supportReportCollection = CalendarSupportCollection.getInstance();
            Dialog.alert("Calendar Report Disabled.");
            if (supportReportCollection != null) {
               supportReportCollection.enableReportGeneration(false);
            }
            break;
         case 1398100051:
            supportReportCollection = CalendarSupportCollection.getInstance();
            Dialog.alert("Calendar Simple Report Enabled.");
            if (supportReportCollection != null) {
               supportReportCollection.setMode(0);
               supportReportCollection.enableReportGeneration(true);
            }
            break;
         case 1398361667:
            if (configuration.isOTAConfigSupported() && configuration.isSendSyncEnabled()) {
               RIMGlobalMessagePoster.postGlobalEvent(-8535280995918704471L, 0, 0, null, null);
               Dialog.alert("Calendar Slow Sync Requested.");
               return true;
            }

            Dialog.alert("Calendar SlowSync Not Supported.");
            return true;
      }

      return super.openProductionBackdoor(backdoorCode);
   }

   @Override
   public final boolean openDevelopmentBackdoor(int backdoorCode) {
      CICALConfiguration configuration = CICALConfiguration.getDefaultConfiguration();
      switch (backdoorCode) {
         case 1129270351:
            if (this._colorModeField == null) {
               Object[] choices = new Object[]{"None", "Right Bar", "Right Bar Filled", "Left Bar", "Left Bar Filled", "Round Border", "Round Border Filled"};
               int initialIndex = this._calendarOptions.getAppointmentColorMode();
               if (initialIndex < 0 || initialIndex >= choices.length) {
                  initialIndex = 0;
               }

               this._colorModeField = new ObjectChoiceField("Color Mode:", choices, initialIndex);
               this._reminderLogManager.add(this._colorModeField);
            }

            if (this._calendarPrivateColourField == null) {
               this._calendarPrivateColourField = new ColorChoiceField();
               this._calendarPrivateColourField.setLabel("Private Appointments Colour: ");
               this._calendarPrivateColourField.setChoices(ColorChoiceField._defaultColors);
               int colour = this._calendarOptions.getPrivateAppointmentsColour();
               this._calendarPrivateColourField.setSelectedIndex(new ColorChoiceField$NamedColor(colour, -1));
               this._reminderLogManager.add(this._calendarPrivateColourField);
            }

            return true;
         case 1380929350:
         case 1380929358:
            Dialog.alert("Recur Optimizations toggles no longer supported.");
            return true;
         case 1398361668:
            if (configuration.isOTAConfigSupported() && configuration.isSendSyncEnabled()) {
               RIMGlobalMessagePoster.postGlobalEvent(-8535280995918704471L, 1, 0, null, null);
               Dialog.alert("Calendar Slow Sync Requested(Debug).");
               return true;
            }

            Dialog.alert("Calendar SlowSync Not Supported.");
            return true;
         default:
            return super.openDevelopmentBackdoor(backdoorCode);
      }
   }

   @Override
   public final void calendarPropertiesChanged(CalendarKey calendarKey) {
      if (this._numberOfEntriesFieldManager != null) {
         int numFields = this._numberOfEntriesFieldManager.getFieldCount();

         for (int i = 0; i < numFields; i++) {
            Field field = this._numberOfEntriesFieldManager.getField(i);
            if (calendarKey.equals(field.getCookie()) && field instanceof CalendarOptionsEntryScreen$CalendarFolderField) {
               ((CalendarOptionsEntryScreen$CalendarFolderField)field).refresh();
               return;
            }
         }
      }
   }
}
