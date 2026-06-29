package net.rim.device.apps.internal.calendar.viewer;

import net.rim.device.api.system.EventLogger;
import net.rim.device.api.system.RIMGlobalMessagePoster;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.ui.theme.Tag;
import net.rim.device.apps.api.calendar.caldb.CalDB;
import net.rim.device.apps.api.calendar.caldb.CalendarFolder;
import net.rim.device.apps.api.calendar.caldb.CalendarKey;
import net.rim.device.apps.api.calendar.caldb.CalendarOptions;
import net.rim.device.apps.api.calendar.caldb.CalendarService;
import net.rim.device.apps.api.calendar.caldb.CalendarServiceManager;
import net.rim.device.apps.api.calendar.ota.CICALConfiguration;
import net.rim.device.apps.api.options.SaveableMainScreenOptionsListItem;
import net.rim.device.apps.api.ui.BooleanChoiceField;
import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.internal.ui.component.ColorChoiceField;
import net.rim.device.internal.ui.component.PropertyField;

final class CalendarOptionsPropertiesScreen extends SaveableMainScreenOptionsListItem {
   private CalendarOptions _calendarOptions = CalendarOptions.getOptions();
   private CalendarService _calendarService;
   private CalendarFolder _calendarFolder;
   private CalendarKey _calendarKey;
   private Field _calendarName;
   private PropertyField _numberOfEntries;
   private BooleanChoiceField _allowWirelessSync;
   private ColorChoiceField _calendarColour;
   private BooleanChoiceField _displayReminders;
   private CalendarOptionsPropertiesScreen$ChangeListener _changeListener;
   private static Tag OPTIONS_SECTION_AREA_TAG = Tag.create("options-section-area");
   private static Tag OPTIONS_SECTION_HEADER_TAG = Tag.create("options-section-header");

   private CalendarOptionsPropertiesScreen(CalendarKey calendarKey, CalendarOptionsPropertiesScreen$ChangeListener changeListener) {
      super(CalendarApp._rb.getString(658));
      this._calendarKey = calendarKey;
      this._calendarService = CalendarServiceManager.getInstance().findCalendarService(calendarKey.getCalendarServiceID());
      this._calendarFolder = this._calendarService.getCalendarFolder(calendarKey.getCalendarFolderID());
      this._changeListener = changeListener;
   }

   public static final void showEditPropertiesScreen(CalendarKey calendarKey, CalendarOptionsPropertiesScreen$ChangeListener changeListener) {
      CalendarOptionsPropertiesScreen propertiesScreen = new CalendarOptionsPropertiesScreen(calendarKey, changeListener);
      propertiesScreen.perform(6099736323056465049L, null);
   }

   @Override
   protected final void populateMainScreen(MainScreen screen) {
      this._calendarName = (Field)(new Object(
         ((StringBuffer)(new Object())).append(this._calendarService.getServiceName()).append(this._calendarFolder.getFolderNameSuffix()).toString(),
         36028797018963968L
      ));
      boolean isOTAEnabled = true;
      boolean isFieldReadOnly = false;
      CICALConfiguration config = this._calendarService.getCICALConfiguration();
      if (!config.isOTAConfigSupported() || !CICALConfiguration.isOTACalendarAllowed()) {
         isOTAEnabled = false;
         isFieldReadOnly = true;
      }

      if (isOTAEnabled) {
         isOTAEnabled = this._calendarOptions.isAllowWirelessSync(this._calendarService.getUniqueServiceID());
      }

      this._allowWirelessSync = (BooleanChoiceField)(new Object(CommonResources.getString(9117), 0, isOTAEnabled, isFieldReadOnly ? 9007199254740992L : 0));
      this._calendarColour = (ColorChoiceField)(new Object());
      this._calendarColour.setLabel(CalendarApp._rb.getString(659));
      this._calendarColour.setChoices(ColorChoiceField._defaultColors);
      int colour = this._calendarOptions.getCalendarColour(this._calendarKey);
      this._calendarColour.setSelectedIndex(new Object(colour, -1));
      boolean displayReminders = this._calendarOptions.isDisplayReminders(this._calendarKey);
      this._displayReminders = (BooleanChoiceField)(new Object(CalendarApp._rb.getString(660), 0, displayReminders));
      int size = this._calendarService.getCalendarDatabase().size();
      this._numberOfEntries = (PropertyField)(new Object(CommonResources.getString(9133), Integer.toString(size), 36028797018963968L));
      Manager section = this.createSection(this._calendarName, screen);
      section.add(this._calendarColour);
      section.add(this._allowWirelessSync);
      section.add(this._displayReminders);
      section = this.createSection(null, screen);
      section.add(this._numberOfEntries);
   }

   protected final Manager createSection(Field titleField, Manager parent) {
      Manager section = (Manager)(new Object(1153484454560268288L));
      section.setTag(OPTIONS_SECTION_AREA_TAG);
      if (titleField != null) {
         titleField.setTag(OPTIONS_SECTION_HEADER_TAG);
         section.add(titleField);
      }

      section.add((Field)(new Object()));
      parent.add(section);
      return section;
   }

   @Override
   protected final boolean save() {
      long calendarServiceID = this._calendarService.getUniqueServiceID();
      boolean currentSetting = this._calendarOptions.isAllowWirelessSync(calendarServiceID);
      boolean newSetting = this._allowWirelessSync.isAffirmative();
      if (currentSetting != newSetting) {
         boolean proceedWithChange = true;
         if (newSetting) {
            CalDB calDB = this._calendarService.getCalendarDatabase();
            if (calDB.isDirty() && Dialog.ask(3, CalendarApp._rb.getString(620), -1) == -1) {
               proceedWithChange = false;
            }
         }

         if (proceedWithChange) {
            EventLogger.logEvent(-256469206327664059L, 1465078613, 0);
            this._calendarOptions.setAllowWirelessSync(calendarServiceID, newSetting, true);
         }
      }

      int currentColour = this._calendarOptions.getCalendarColour(this._calendarKey);
      int newColour = this._calendarColour.getSelectedColor();
      if (currentColour != newColour) {
         this._calendarOptions.setCalendarColour(this._calendarKey, newColour);
      }

      currentSetting = this._calendarOptions.isDisplayReminders(this._calendarKey);
      newSetting = this._displayReminders.isAffirmative();
      if (currentSetting != newSetting) {
         this._calendarOptions.setDisplayReminders(this._calendarKey, newSetting);
      }

      this._calendarOptions.commit();
      boolean returnValue = super.save();
      if (this._changeListener != null) {
         this._changeListener.calendarPropertiesChanged(this._calendarKey);
      }

      RIMGlobalMessagePoster.postGlobalEvent(5483692278053761660L, 0, 0, null, null);
      return returnValue;
   }
}
