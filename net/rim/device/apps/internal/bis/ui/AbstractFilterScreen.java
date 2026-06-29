package net.rim.device.apps.internal.bis.ui;

import java.util.Hashtable;
import java.util.Vector;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.component.BasicEditField;
import net.rim.device.api.ui.component.CheckboxField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.RadioButtonField;
import net.rim.device.api.ui.component.RadioButtonGroup;
import net.rim.device.api.ui.container.FlowFieldManager;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.apps.internal.bis.ApplicationResources;
import net.rim.device.apps.internal.bis.api.ui.BoldLabelField;
import net.rim.device.apps.internal.bis.api.ui.BoldLabelObjectChoiceField;
import net.rim.device.apps.internal.bis.api.ui.Button;
import net.rim.device.apps.internal.bis.api.ui.FilterLabelField;
import net.rim.device.apps.internal.bis.api.ui.FormattedTextField;
import net.rim.device.apps.internal.bis.data.Mailbox;
import net.rim.device.internal.ui.container.FrameLayout;

public class AbstractFilterScreen extends UserSettingsScreen {
   private BasicEditField _filterNameEdit;
   private BasicEditField _containsEdit;
   private String _filterId;
   private VerticalFieldManager _containsArea;
   private FrameLayout _containsListFrame;
   private RadioButtonField _forward1Choice;
   private RadioButtonField _forward2Choice;
   private CheckboxField _headersOnly;
   private CheckboxField _levelOne;
   private Vector _filterValues = (Vector)(new Object());
   private BoldLabelObjectChoiceField _filterOptions;
   private int _containsIndex;

   public AbstractFilterScreen() {
      super(30);
   }

   @Override
   public boolean importFormDataFromUI(Hashtable inputMap) {
      String filterName = this._filterNameEdit.getText();
      String filterOperatorChoice = (String)this._filterOptions.getChoice(this._filterOptions.getSelectedIndex());
      String filterOperator = null;
      if (filterOperatorChoice.equals(ApplicationResources.getString(297))) {
         filterOperator = "Cc";
      } else if (filterOperatorChoice.equals(ApplicationResources.getString(298))) {
         filterOperator = "From";
      } else if (filterOperatorChoice.equals(ApplicationResources.getString(62))) {
         filterOperator = "priorityMail";
      } else if (filterOperatorChoice.equals(ApplicationResources.getString(299))) {
         filterOperator = "newMail";
      } else if (filterOperatorChoice.equals(ApplicationResources.getString(300))) {
         filterOperator = "Subject";
      } else if (filterOperatorChoice.equals(ApplicationResources.getString(301))) {
         filterOperator = "To";
      }

      StringBuffer filterValue = (StringBuffer)(new Object());
      if (null != filterName && filterName.trim().length() != 0) {
         for (int i = 0; i < this._filterValues.size(); i++) {
            String value = (String)this._filterValues.elementAt(i);
            filterValue.append(value);
            if (i < this._filterValues.size() - 1) {
               filterValue.append(";");
            }
         }

         Boolean headersOnly = (Boolean)(new Object(this._headersOnly.getChecked()));
         Boolean levelOne = (Boolean)(new Object(this._levelOne.getChecked()));
         Boolean sendAlert = (Boolean)(new Object(this._forward1Choice.isSelected()));
         if (!filterOperator.equals("priorityMail") && !filterOperator.equals("newMail") && filterValue != null && filterValue.toString().trim().equals("")) {
            this.setError(ApplicationResources.getString(311));
            return false;
         }

         inputMap.put("filtername", filterName);
         inputMap.put("sendalert", sendAlert.toString());
         inputMap.put("headersonly", headersOnly.toString());
         inputMap.put("levelone", levelOne.toString());
         inputMap.put("filteroperator", filterOperator);
         inputMap.put("filtervalue", filterValue.toString());
         if (null != this._filterId) {
            inputMap.put("filterid", this._filterId);
         }

         return true;
      } else {
         this.setError(ApplicationResources.getString(314));
         return false;
      }
   }

   protected void setFilterId(String id) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   protected void eraseScreenData() {
      this._filterNameEdit = null;
      this._containsEdit = null;
      this._filterId = null;
      this._containsArea = null;
      this._containsListFrame = null;
      this._forward1Choice = null;
      this._forward2Choice = null;
      this._headersOnly = null;
      this._levelOne = null;
      this._filterValues = (Vector)(new Object());
      this._filterOptions = null;
      this._containsIndex = -1;
   }

   protected void addHeaderInfo(String filterName, String emailAccount) {
      this._filterNameEdit = (BasicEditField)(new Object(null, filterName, 40, 2147483648L));
      this.addContentField((Field)(new Object(ApplicationResources.getString(308))));
      this.addContentLineBreak();
      this.addContentField(new BoldLabelField(ApplicationResources.getString(309)));
      this.addContentField((Field)(new Object(emailAccount)));
      this.addContentLineBreak();
      this.addContentField(new BoldLabelField(ApplicationResources.getString(285)));
      this.addContentField(this._filterNameEdit, true);
      this.addContentLineBreak();
   }

   protected void addAction(boolean sendAlert, boolean headersOnly, boolean levelOne) {
      this.addContentField(new BoldLabelField(ApplicationResources.getString(281)));
      RadioButtonGroup _forwardRadioGroup = (RadioButtonGroup)(new Object());
      this._forward1Choice = (RadioButtonField)(new Object(ApplicationResources.getString(279), _forwardRadioGroup, false));
      this._forward2Choice = (RadioButtonField)(new Object(ApplicationResources.getString(280), _forwardRadioGroup, true));
      this.addContentField(this._forward1Choice);
      this._headersOnly = (CheckboxField)(new Object(ApplicationResources.getString(289), false, 4294967296L));
      this._levelOne = (CheckboxField)(new Object(ApplicationResources.getString(290), false, 4294967296L));
      if (sendAlert) {
         this._forward1Choice.setSelected(true);
      } else {
         this._forward2Choice.setSelected(true);
      }

      if (headersOnly && sendAlert) {
         this._headersOnly.setChecked(true);
      }

      if (levelOne && sendAlert) {
         this._levelOne.setChecked(true);
      }

      this._headersOnly.setPadding(0, 0, 0, 20);
      this._levelOne.setPadding(0, 0, 0, 20);
      this.addContentField(this._headersOnly);
      this.addContentField(this._levelOne);
      this.addContentField(this._forward2Choice);
      this.addContentLineBreak();
   }

   protected void addFilterOperator(Mailbox mailbox, String filterOperator) {
      String[] filterOnChoices = new Object[]{
         ApplicationResources.getString(297),
         ApplicationResources.getString(298),
         ApplicationResources.getString(62),
         ApplicationResources.getString(299),
         ApplicationResources.getString(300),
         ApplicationResources.getString(301)
      };
      this._filterOptions = new BoldLabelObjectChoiceField(ApplicationResources.getString(286), filterOnChoices);
      this.addContentField(this._filterOptions, false);
      this._filterOptions.setChangeListener(new AbstractFilterScreen$FilterChoicesChangeListener(this, this));
      if (filterOperator != null) {
         this._filterOptions.setSelectedIndex(this.pickSelected(filterOperator));
      }

      this.addContentLineBreak();
      if (mailbox != null && mailbox.getMailboxType() == 5) {
         this.addContentField((Field)(new Object(ApplicationResources.getString(291))));
         this.addContentLineBreak();
      }
   }

   protected void addContainsArea(String filterValues) {
      this._containsArea = (VerticalFieldManager)(new Object());
      this._containsArea.add(new BoldLabelField(ApplicationResources.getString(287)));
      this._containsArea.add(new FormattedTextField(ApplicationResources.getString(288)));
      this._containsEdit = (BasicEditField)(new Object(2147483648L));
      Button addButton = new Button(ApplicationResources.getString(302));
      addButton.setChangeListener(new AbstractFilterScreen$AddButtonFieldListener(this, null));
      FrameLayout frame = (FrameLayout)(new Object());
      frame.add(this._containsEdit);
      frame.setBorder(0, 4, 0, 4);
      this._containsArea.add(frame);
      this._containsArea.add(addButton);
      if (null != filterValues) {
         this._filterValues = this.getFilterValueAsList(filterValues);
      }

      this.addKeyListener(new AbstractFilterScreen$KeyDownListener(this, null));
      this.addContentField(this._containsArea);
      if (!this.canShowContains()) {
         this.clearFilterValues();
         this._containsIndex = this.deleteField(this._containsArea);
      }

      this._containsListFrame = (FrameLayout)this.buildContainsContent(this._filterValues);
      this._containsArea.add(this._containsListFrame);
      this.addContentLineBreak();
   }

   protected void clearFilterValues() {
      this._filterValues.removeAllElements();
   }

   private Field buildContainsContent(Vector filterValues) {
      FrameLayout containsListFrame = (FrameLayout)(new Object());
      FlowFieldManager hm = (FlowFieldManager)(new Object(1153202979583557632L));
      if (filterValues != null && !filterValues.isEmpty()) {
         for (int i = 0; i < filterValues.size(); i++) {
            FilterLabelField filterValue = new FilterLabelField(this._filterValues.elementAt(i));
            if (i >= 1) {
               LabelField separatorField = (LabelField)(new Object(
                  ((StringBuffer)(new Object(" "))).append(ApplicationResources.getString(315)).append(" ").toString()
               ));
               hm.add(separatorField);
               filterValue.setMenuListener(new AbstractFilterScreen$DeleteNotificationMenuItemListener(this, filterValue, separatorField));
            } else {
               filterValue.setMenuListener(new AbstractFilterScreen$DeleteNotificationMenuItemListener(this, filterValue));
            }

            hm.add(filterValue);
         }
      } else {
         hm.add((Field)(new Object(" ")));
      }

      containsListFrame.add(hm);
      containsListFrame.setBorder(0, 4, 0, 4);
      return containsListFrame;
   }

   private void addContainsContentToContainsArea() {
      if (this._containsEdit.getText() != null && !this._containsEdit.getText().trim().equals("")) {
         String containsEdit = this._containsEdit.getText().trim();
         this._filterValues.addElement(containsEdit);
         Field containsValueField = this.buildContainsContent(this._filterValues);
         this._containsArea.replace(this._containsListFrame, containsValueField);
         this._containsListFrame = (FrameLayout)containsValueField;
         this._containsEdit.setText("");
         this._containsEdit.setFocus();
      }
   }

   private Vector getFilterValueAsList(String filterValue) {
      Vector values = (Vector)(new Object());
      int fromIndex = 0;
      String token = (String)(new Object(";"));

      while (fromIndex < filterValue.length()) {
         int index = filterValue.indexOf(token, fromIndex);
         if (index < 0) {
            String value = filterValue.substring(fromIndex, filterValue.length());
            values.addElement(value);
            return values;
         }

         String value = filterValue.substring(fromIndex, index);
         values.addElement(value);
         fromIndex = index + 1;
      }

      return values;
   }

   private boolean canShowContains() {
      return this._filterOptions.getSelectedIndex() != 2 && this._filterOptions.getSelectedIndex() != 3;
   }

   private int pickSelected(String operator) {
      int selectedIndex = 0;
      if (operator.equals("From")) {
         return 1;
      }

      if (operator.equals("priorityMail")) {
         return 2;
      }

      if (operator.equals("newMail")) {
         return 3;
      }

      if (operator.equals("Subject")) {
         return 4;
      }

      if (operator.equals("To")) {
         selectedIndex = 5;
      }

      return selectedIndex;
   }
}
