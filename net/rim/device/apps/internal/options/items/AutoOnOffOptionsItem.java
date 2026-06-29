package net.rim.device.apps.internal.options.items;

import java.util.Calendar;
import java.util.TimeZone;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.component.DateField;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.util.DateTimeUtilities;
import net.rim.device.apps.api.framework.registration.VerbRepository;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.options.SaveableMainScreenOptionsListItem;
import net.rim.device.apps.api.ui.BooleanChoiceField;
import net.rim.device.apps.api.utility.framework.VerbToMenu;
import net.rim.device.apps.internal.options.resources.OptionsResources;
import net.rim.device.cldc.util.CalendarExtensions;
import net.rim.device.internal.deviceoptions.AutoOnOff;

public final class AutoOnOffOptionsItem extends SaveableMainScreenOptionsListItem {
   private DateField _weekdayOnField;
   private DateField _weekdayOffField;
   private BooleanChoiceField _weekdayAutoOnOffField;
   private DateField _weekendOnField;
   private DateField _weekendOffField;
   private BooleanChoiceField _weekendAutoOnOffField;
   private static final TimeZone GMT = TimeZone.getTimeZone(DateTimeUtilities.GMT);

   public AutoOnOffOptionsItem() {
      super(OptionsResources.getString(1300));
   }

   @Override
   protected final void populateMainScreen(MainScreen mainScreen) {
      this._weekdayOnField = (DateField)(new Object(OptionsResources.getString(1301), AutoOnOff.getWeekdayOnTime(), 32));
      this._weekdayOnField.setTimeZone(GMT);
      this._weekdayOffField = (DateField)(new Object(OptionsResources.getString(1302), AutoOnOff.getWeekdayOffTime(), 32));
      this._weekdayOffField.setTimeZone(GMT);
      this._weekdayAutoOnOffField = (BooleanChoiceField)(new Object(OptionsResources.getString(1303), 2, AutoOnOff.isWeekdayAutoOnOffEnabled()));
      this._weekendOnField = (DateField)(new Object(OptionsResources.getString(1301), AutoOnOff.getWeekendOnTime(), 32));
      this._weekendOnField.setTimeZone(GMT);
      this._weekendOffField = (DateField)(new Object(OptionsResources.getString(1302), AutoOnOff.getWeekendOffTime(), 32));
      this._weekendOffField.setTimeZone(GMT);
      this._weekendAutoOnOffField = (BooleanChoiceField)(new Object(OptionsResources.getString(1304), 2, AutoOnOff.isWeekendAutoOnOffEnabled()));
      mainScreen.add(this._weekdayAutoOnOffField);
      mainScreen.add(this._weekdayOnField);
      mainScreen.add(this._weekdayOffField);
      mainScreen.add((Field)(new Object()));
      mainScreen.add(this._weekendAutoOnOffField);
      mainScreen.add(this._weekendOnField);
      mainScreen.add(this._weekendOffField);
   }

   @Override
   protected final void addRepositoryVerbs(VerbToMenu verbToMenu, int instance) {
      VerbRepository verbRepository = VerbRepository.getVerbRepository(9132292084531901175L);
      Verb[] factoryVerbs = verbRepository.getVerbs(null);
      if (factoryVerbs != null && factoryVerbs.length > 0) {
         verbToMenu.addVerbs(factoryVerbs);
      }
   }

   private static final int getMillisecondsPastMidnight(long date) {
      Calendar cal = Calendar.getInstance(GMT);
      ((CalendarExtensions)cal).setTimeLong(date);
      return cal.get(11) * 3600000 + cal.get(12) * 60000;
   }

   @Override
   protected final boolean save() {
      AutoOnOff.setWeekdayOnTime(getMillisecondsPastMidnight(this._weekdayOnField.getDate()));
      AutoOnOff.setWeekdayOffTime(getMillisecondsPastMidnight(this._weekdayOffField.getDate()));
      AutoOnOff.enableWeekdayAutoOnOff(this._weekdayAutoOnOffField.isAffirmative());
      AutoOnOff.setWeekendOnTime(getMillisecondsPastMidnight(this._weekendOnField.getDate()));
      AutoOnOff.setWeekendOffTime(getMillisecondsPastMidnight(this._weekendOffField.getDate()));
      AutoOnOff.enableWeekendAutoOnOff(this._weekendAutoOnOffField.isAffirmative());
      return super.save();
   }
}
