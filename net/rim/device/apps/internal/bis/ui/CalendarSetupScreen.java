package net.rim.device.apps.internal.bis.ui;

import java.util.Hashtable;
import net.rim.device.api.ui.component.RadioButtonField;
import net.rim.device.api.ui.component.RadioButtonGroup;
import net.rim.device.apps.internal.bis.ApplicationResources;
import net.rim.device.apps.internal.bis.api.ui.Button;
import net.rim.device.apps.internal.bis.api.ui.FormattedTextField;
import net.rim.device.apps.internal.bis.event.BackEvent;
import net.rim.device.apps.internal.bis.event.CloseEvent;
import net.rim.device.apps.internal.bis.event.CommandEvent;
import net.rim.device.apps.internal.bis.session.ClientSessionState;

public final class CalendarSetupScreen extends UserSettingsScreen {
   private RadioButtonGroup _synchCalendarGroup;
   private static final String PARAM_SYNCH_CALENDAR = "synch_calendar";

   public CalendarSetupScreen() {
      super(31);
   }

   @Override
   public final void refresh(Hashtable screenParams) {
      this.setTitle(ApplicationResources.getString(319));
      CommandEvent nextEvent = new CommandEvent(17, 36, new String[]{"synch_calendar"});
      String email = ClientSessionState.getInstance().getIntegrationEmail();
      this.addContentField(new FormattedTextField(this.getString(320, new Object[]{email})));
      this.addContentLineBreak();
      this._synchCalendarGroup = new RadioButtonGroup();
      RadioButtonField yesField = new RadioButtonField(ApplicationResources.getString(32), this._synchCalendarGroup, true);
      RadioButtonField noField = new RadioButtonField(ApplicationResources.getString(31), this._synchCalendarGroup, false);
      this.addContentField(yesField);
      this.addContentField(noField);
      this.addContentLineBreak();
      Button closeButton = new Button(ApplicationResources.getString(15));
      Button backButton = new Button(ApplicationResources.getString(16));
      Button nextButton = new Button(ApplicationResources.getString(17));
      this.addButtonBarButtons(new Button[]{closeButton, backButton, nextButton}, false, 2);
      this.attachEventToField(nextButton, nextEvent);
      this.setDefaultEvent(nextEvent);
      this.attachEventToField(closeButton, new CloseEvent());
      this.attachEventToField(backButton, new BackEvent());
      this.setHelp("????.wml");
   }

   @Override
   public final boolean importFormDataFromUI(Hashtable inputMap) {
      int selectedIndex = this._synchCalendarGroup.getSelectedIndex();
      if (selectedIndex == 0) {
         inputMap.put("synch_calendar", Boolean.TRUE);
         return true;
      } else {
         inputMap.put("synch_calendar", Boolean.FALSE);
         return true;
      }
   }
}
