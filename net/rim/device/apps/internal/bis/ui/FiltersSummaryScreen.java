package net.rim.device.apps.internal.bis.ui;

import java.util.Hashtable;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.component.RadioButtonField;
import net.rim.device.api.ui.component.RadioButtonGroup;
import net.rim.device.apps.internal.bis.ApplicationResources;
import net.rim.device.apps.internal.bis.api.ui.Button;
import net.rim.device.apps.internal.bis.api.ui.FilterList;
import net.rim.device.apps.internal.bis.api.ui.FilterList$FilterListField;
import net.rim.device.apps.internal.bis.data.Filter;
import net.rim.device.apps.internal.bis.data.Mailbox;
import net.rim.device.apps.internal.bis.event.CommandEvent;
import net.rim.device.apps.internal.bis.event.LinkEvent;
import net.rim.device.apps.internal.bis.session.ClientSessionState;

public final class FiltersSummaryScreen extends UserSettingsScreen {
   private RadioButtonField _forward1Choice;
   private RadioButtonField _forward2Choice;
   private Mailbox _mailbox;
   public static final String PARAM_DEFAULT_RULE_SEND = "defaultRuleSend";

   public FiltersSummaryScreen() {
      super(30);
   }

   @Override
   public final void refresh(Hashtable screenParams) {
      this._mailbox = ClientSessionState.getInstance().getMailboxToModify();
      this.setTitle(((StringBuffer)(new Object())).append(ApplicationResources.getString(276)).append(" ").append(this._mailbox.getEmail()).toString());
      this.addContentField((Field)(new Object(ApplicationResources.getString(275))));
      this.addContentLineBreak();
      FilterList filterList = new FilterList(this._mailbox.getFilters());
      this.addContentField(filterList);
      filterList.setMenuListener(this);
      this.addContentLineBreak();
      Button add = new Button(ApplicationResources.getString(274));
      this.addContentField(add);
      LinkEvent addEvent = new LinkEvent(274, 46);
      this.attachEventToField(add, addEvent);
      this.setDefaultEvent(addEvent);
      this.addContentLineBreak();
      this.addContentField((Field)(new Object(ApplicationResources.getString(278))));
      RadioButtonGroup _forwardRadioGroup = (RadioButtonGroup)(new Object());
      this._forward1Choice = (RadioButtonField)(new Object(ApplicationResources.getString(279), _forwardRadioGroup, false));
      this._forward2Choice = (RadioButtonField)(new Object(ApplicationResources.getString(280), _forwardRadioGroup, false));
      this._forward1Choice.setPadding(0, 0, 0, 20);
      this._forward2Choice.setPadding(0, 0, 0, 20);
      if (this._mailbox.isForwardMessagesToDevice()) {
         this._forward1Choice.setSelected(true);
      } else {
         this._forward2Choice.setSelected(true);
      }

      this.addContentField(this._forward1Choice);
      this.addContentField(this._forward2Choice);
      this.addContentLineBreak();
      Button cancelButton = new Button(ApplicationResources.getString(28));
      Button saveButton = new Button(ApplicationResources.getString(29));
      this.addButtonBarButtons(new Button[]{cancelButton, saveButton}, false, 1);
      LinkEvent cancelEvent = new LinkEvent(28, 7);
      this.attachEventToField(cancelButton, cancelEvent);
      CommandEvent saveEvent = new CommandEvent(29, 8, new String[]{"defaultRuleSend"});
      this.attachEventToField(saveButton, saveEvent);
      this.setCloseEvent(cancelEvent);
      this.setHelp("98952.wml");
   }

   @Override
   protected final MenuItem selectDefaultMenuItem(Menu menu) {
      return menu.getInstance() != 65538 ? this.findMenuItemForEvent(menu, this.getDefaultEvent()) : null;
   }

   @Override
   public final boolean importFormDataFromUI(Hashtable inputMap) {
      Field selectedField = this.getLeafFieldWithFocus();
      if (selectedField instanceof FilterList$FilterListField) {
         FilterList$FilterListField filterField = (FilterList$FilterListField)selectedField;
         Filter filter = (Filter)this._mailbox.getFilters().elementAt(filterField.getSelectedIndex());
         inputMap.put("filterid", filter.getId());
         inputMap.put("filtername", filter.getName());
      }

      Boolean defaultRule = (Boolean)(new Object(this._forward1Choice.isSelected()));
      inputMap.put("defaultRuleSend", defaultRule.toString());
      return true;
   }
}
