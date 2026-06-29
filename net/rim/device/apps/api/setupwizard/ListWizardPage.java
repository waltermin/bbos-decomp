package net.rim.device.apps.api.setupwizard;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.FocusChangeListener;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.component.RadioButtonField;
import net.rim.device.api.ui.component.RadioButtonGroup;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.apps.api.ui.AppsMainScreen;

public class ListWizardPage extends BasicWizardPage implements FieldChangeListener, FocusChangeListener {
   private Field _headerField;
   private Field _footerField;
   private Field _sideBar;
   private int _sideBarWidth;
   private String[] _listItems;
   private RadioButtonGroup _radioGroup;
   private int _initialSelectedIndex;
   private RadioButtonField _initialFocusField;
   private boolean _advanceOnEnterKey;
   private boolean _useSmallListFont;
   private Manager _listContainer;
   private MainScreen _screen;
   private int _verticalSpacing;
   private boolean _noAutoFocus;
   private boolean _advanceOnSelect;
   public static final int ADVANCE_ON_ENTER_KEY = 131072;
   public static final int SMALL_LIST_FONT = 262144;
   public static final int NO_AUTO_FOCUS = 524288;
   public static final int ADVANCE_ON_SELECT = 1048576;

   public ListWizardPage(String title, int priority, WizardCategory category, int wizardFlags) {
      super(title, priority, category, wizardFlags | 16);
      this.initFlags(wizardFlags);
   }

   public ListWizardPage(String title, int priority, WizardCategory category) {
      this(title, priority, category, 16);
   }

   public ListWizardPage(ResourceBundle rb, int rbTitleId, int priority, WizardCategory category, int wizardFlags) {
      super(rb, rbTitleId, priority, category, wizardFlags | 16);
      this.initFlags(wizardFlags);
   }

   public ListWizardPage(ResourceBundle rb, int rbTitleId, int priority, WizardCategory category) {
      this(rb, rbTitleId, priority, category, 16);
   }

   protected void initFlags(int wizardFlags) {
      this._advanceOnEnterKey = (wizardFlags & 131072) != 0;
      this._noAutoFocus = (wizardFlags & 524288) != 0;
      this._advanceOnSelect = (wizardFlags & 1048576) != 0;
   }

   protected void populateFields() {
      throw null;
   }

   protected void selectedIndexChanged(int selectedIndex) {
   }

   @Override
   protected void resetFontsInternal() {
      if (this._useSmallListFont && this._listContainer != null) {
         this._listContainer.setFont(this.getHeaderFont());
      }
   }

   protected void onRadioFocus(RadioButtonField radioButton) {
   }

   @Override
   protected void afterClose() {
      if (this._listContainer != null) {
         this._listContainer.setFocusListener(null);
      }
   }

   protected void setHeaderField(Field field) {
      this._headerField = field;
   }

   protected void setFooterField(Field field) {
      this._footerField = field;
   }

   protected void setListItems(String[] items) {
      this._listItems = items;
   }

   protected void setSideBar(Field sideBar, int width) {
      this._sideBar = sideBar;
      this._sideBarWidth = width;
   }

   @Override
   protected void beforeShow() {
      if (this._initialFocusField != null && !this._noAutoFocus) {
         this._initialFocusField.setFocus();
      }
   }

   @Override
   protected void populateContent(AppsMainScreen mainScreen, Manager content) {
      this._initialSelectedIndex = 0;
      this._initialFocusField = null;
      this._sideBar = null;
      this._headerField = null;
      this._footerField = null;
      this._listItems = null;
      this._screen = mainScreen;
      this._radioGroup = (RadioButtonGroup)(new Object());
      WizardLayoutManager wizardLayoutManager = new WizardLayoutManager();
      wizardLayoutManager.setScrollbarEnabled(true);
      this.populateFields();
      Manager myContent = (Manager)(new Object(3459045988797251584L));
      wizardLayoutManager.setHeader(this._headerField);
      this.populateList(myContent);
      wizardLayoutManager.setContent(myContent);
      wizardLayoutManager.setSideBar(this._sideBar, this._sideBarWidth);
      wizardLayoutManager.setFooter(this._footerField);
      content.add(wizardLayoutManager);
   }

   public void setAdvanceOnEnterKey(boolean val) {
      this._advanceOnEnterKey = val;
   }

   public void setSelectedIndex(int index) {
      if (this._radioGroup.getSize() == 0) {
         this._initialSelectedIndex = index;
      } else {
         if (index != this._radioGroup.getSelectedIndex()) {
            this._radioGroup.setSelectedIndex(index);
         }
      }
   }

   public int getSelectedIndex() {
      return this._radioGroup.getSize() == 0 ? this._initialSelectedIndex : this._radioGroup.getSelectedIndex();
   }

   public void useSmallListFont(boolean val) {
      this._useSmallListFont = val;
   }

   public void setVerticalSpacing(int val) {
      this._verticalSpacing = val;
   }

   public int getVerticalSpacing() {
      return this._verticalSpacing;
   }

   protected void populateList(Manager content) {
      int numItems = 0;
      if (this._listItems != null) {
         numItems = this._listItems.length;
      }

      this._listContainer = content;
      this._listContainer.setBorder(4, 8, 4, 8);
      if (this._useSmallListFont) {
         this._listContainer.setFont(this.getHeaderFont());
      }

      if (this._noAutoFocus) {
         this._listContainer.add((Field)(new Object()));
      }

      for (int i = 0; i < numItems; i++) {
         String item = this._listItems[i];
         RadioButtonField radioButton = (RadioButtonField)(new Object(item));
         radioButton.setFocusListener(this);
         this._radioGroup.add(radioButton);
         if (i == this._initialSelectedIndex) {
            this._initialFocusField = radioButton;
         }

         if (this._verticalSpacing > 0) {
            radioButton.setBorder(0, 0, this._verticalSpacing, 0);
         }

         this._listContainer.add(radioButton);
      }

      this._radioGroup.setChangeListener(null);
      if (this._radioGroup.getSize() > 0) {
         this._radioGroup.setSelectedIndex(this._initialSelectedIndex);
      }

      this._radioGroup.setChangeListener(this);
      this._listContainer.setFocusListener(this);
   }

   @Override
   public boolean keyDown(int keycode, int time) {
      if (keycode >> 16 == 10 && this.getMainScreen() != null) {
         Field focus = this.getMainScreen().getLeafFieldWithFocus();
         if (focus instanceof Object) {
            RadioButtonField radioButton = (RadioButtonField)focus;
            if (!radioButton.isSelected()) {
               radioButton.setSelected(true);
            } else if (this._advanceOnEnterKey) {
               this.getButtonBar().invokeNext();
               return true;
            }
         }
      }

      return super.keyDown(keycode, time);
   }

   @Override
   public void fieldChanged(Field field, int context) {
      if (field instanceof Object) {
         int index = this._radioGroup.getSelectedIndex();
         this._screen.setDirty(this._initialSelectedIndex != index);
         if (this.loggingEnabled()) {
            this.log(((StringBuffer)(new Object("Selected index changed: "))).append(((RadioButtonField)field).getLabel()).toString());
         }

         this.selectedIndexChanged(this._radioGroup.getSelectedIndex());
         if (this._advanceOnSelect) {
            this.getButtonBar().invokeNext();
            return;
         }
      } else {
         super.fieldChanged(field, context);
      }
   }

   @Override
   public void focusChanged(Field field, int eventType) {
      if (!(field instanceof Object)) {
         if (field == this._listContainer && eventType == 3) {
            this.onRadioFocus(null);
         }
      } else {
         RadioButtonField radioButton = (RadioButtonField)field;
         if (radioButton.getGroup() == this._radioGroup && eventType == 1) {
            this.onRadioFocus(radioButton);
            return;
         }
      }
   }
}
