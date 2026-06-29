package net.rim.device.apps.internal.bis.api.ui;

import java.util.Hashtable;
import java.util.Vector;
import net.rim.blackberry.api.browser.Browser;
import net.rim.blackberry.api.browser.BrowserSession;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.Trackball;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.component.BitmapField;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.component.NullField;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.apps.internal.bis.ApplicationResources;
import net.rim.device.apps.internal.bis.ClientPersistentState;
import net.rim.device.apps.internal.bis.Common;
import net.rim.device.apps.internal.bis.event.BackEvent;
import net.rim.device.apps.internal.bis.event.CloseEvent;
import net.rim.device.apps.internal.bis.event.CommandEvent;
import net.rim.device.apps.internal.bis.event.EventWrapper;
import net.rim.device.apps.internal.bis.session.ClientSessionState;
import net.rim.device.internal.ui.component.VerticalSpacerField;
import net.rim.device.internal.ui.container.FrameLayout;

public class BasicScreen extends RefreshableScreen implements GlobalEventListener, FieldChangeListener, NotificationMenuItemListener {
   private TitleBar _titleBar;
   private VerticalFieldManager _content;
   private ButtonBar _buttonBar;
   private PageLayoutManager _pageLayoutManager;
   private String _helpWMLFile;
   private String _helpURL;
   private BitmapField _helpImage;
   private NotificationMenuItem _helpMenuItem;
   private Vector _contentAreaEvents = new Vector();
   private Vector _buttonBarEvents = new Vector();
   private Hashtable _fieldToEvent = new Hashtable();
   private Hashtable _fieldToMenuItems = new Hashtable();
   private NotificationMenuItem _closeMenuItem;
   private boolean _canGoBack;
   private Event _defaultEvent;
   Event _closeEvent;

   public void setDefaultEvent(Event defaultEvent) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   public Event getDefaultEvent() {
      return this._defaultEvent;
   }

   protected void addCustomMenuItems(Menu menu, int instance) {
   }

   protected void resetFonts() {
      this.resetFontsInternal();
      if (this._buttonBar != null) {
         this._buttonBar.resetFonts();
      }
   }

   public boolean importFormDataFromUI(Hashtable inputMap) {
      return true;
   }

   public void attachEventToField(Field field, Event event) {
      Manager fieldManager = field.getManager();
      if (fieldManager == this._buttonBar) {
         this._buttonBarEvents.addElement(event);
      } else {
         this._contentAreaEvents.addElement(event);
      }

      field.setChangeListener(this);
      this._fieldToEvent.put(field, event);
      if (event instanceof BackEvent) {
         this._canGoBack = true;
      }
   }

   public void attachEventToMenuItem(NotificationMenuItem menuItem, Event event) {
      menuItem.setListener(this);
      menuItem.setEvent(event);
      if (event instanceof BackEvent) {
         this._canGoBack = true;
      }
   }

   public void insertAt(Field fieldToInsert, int index) {
      this._content.insert(fieldToInsert, index);
   }

   public void addContentField(Field field) {
      this.addContentField(field, false);
   }

   public void addContentField(Field field, boolean includeFrame) {
      if (includeFrame) {
         FrameLayout frame = new FrameLayout();
         frame.add(field);
         frame.setBorder(0, 4, 0, 4);
         this._content.add(frame);
      } else {
         this._content.add(field);
      }

      this._pageLayoutManager.setScrollbarEnabled(true);
   }

   public void addContentFieldRow(Field[] fields) {
      HorizontalFieldManager hfm = new HorizontalFieldManager();
      int numFields = fields.length;

      for (int i = 0; i < numFields; i++) {
         hfm.add(fields[i]);
      }

      this.addContentField(hfm);
   }

   public void addButtonBarButtons(Button[] buttons, boolean rightToLeft) {
      if (this._buttonBar == null) {
         this._buttonBar = new ButtonBar();
         this._pageLayoutManager.setFooter(this._buttonBar);
      }

      int numButtons = buttons.length;
      if (rightToLeft) {
         for (int i = numButtons - 1; i >= 0; i--) {
            this._buttonBar.add(buttons[i]);
         }
      } else {
         for (int i = 0; i < numButtons; i++) {
            this._buttonBar.add(buttons[i]);
         }
      }
   }

   public void addButtonBarButtons(Button[] buttons, boolean rightToLeft, int firstFocusIndex) {
      this.addButtonBarButtons(buttons, rightToLeft);
      this._buttonBar.setFirstFocusButton(buttons[firstFocusIndex]);
   }

   public void reloadHelpMenuItem() {
      this._helpMenuItem = new NotificationMenuItem(ApplicationResources.getString(214), 0, 0);
      this._helpMenuItem.setListener(this);
   }

   public void setCloseEvent(Event closeEvent) {
      if (closeEvent instanceof BackEvent) {
         this._canGoBack = true;
      }

      this._closeEvent = closeEvent;
   }

   public int deleteField(Field field) {
      int index = field.getIndex();
      this._content.delete(field);
      return index;
   }

   protected MenuItem findMenuItemForEvent(Menu menu, Event event) {
      int menuSize = menu.getSize();

      for (int i = 0; event != null && i < menuSize; i++) {
         MenuItem currentMenuItem = menu.getItem(i);
         if (currentMenuItem instanceof NotificationMenuItem) {
            NotificationMenuItem notificationMenuItem = (NotificationMenuItem)currentMenuItem;
            Event currentEvent = notificationMenuItem.getEvent();
            if (event.equals(currentEvent)) {
               return currentMenuItem;
            }
         }
      }

      return null;
   }

   protected MenuItem selectDefaultMenuItem(Menu menu) {
      return this.findMenuItemForEvent(menu, this._defaultEvent);
   }

   public void addContentLineBreak() {
      int verticalBufferSpace = this.getFont().getHeight();
      this.addContentField(new VerticalSpacerField(verticalBufferSpace));
   }

   @Override
   public void menuItemSelected(NotificationMenuItem menuItem) {
      Event event = menuItem.getEvent();
      if (event != null) {
         this.submitEvent(event);
      } else if (menuItem == this._helpMenuItem) {
         this.invokeHelp();
      } else {
         if (menuItem == this._closeMenuItem) {
            InputController.getInstance().submitEvent(new CloseEvent());
         }
      }
   }

   @Override
   public void fieldChanged(Field field, int context) {
      Event event = (Event)this._fieldToEvent.get(field);
      if (event != null) {
         this.submitEvent(event);
      } else {
         if (field == this._helpImage) {
            this.invokeHelp();
         }
      }
   }

   @Override
   public void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (guid == -4394903006263251010L) {
         Application.getApplication().invokeLater(new BasicScreen$1(this));
      }
   }

   @Override
   public void setTitle(String title) {
      if (this._titleBar == null) {
         this._titleBar = new TitleBar(title);
      }

      this._titleBar.setTitle(title);
      this.setTitle(this._titleBar);
   }

   private String generateHelpURL(String wmlFile) {
      String language = Common.getLocaleCode(ClientPersistentState.getInstance().getLocale());
      if (language != null && ClientSessionState.getInstance().getUserInfo() != null && ClientSessionState.getInstance().getBrandingInfo() != null) {
         if (language.indexOf(95) > -1 && language.length() > 2) {
            String languageCode = language.substring(0, 2);
            if (this.isCountryRequiredForHelp(languageCode)) {
               if (language.length() != 5) {
                  return null;
               }

               String countryCode = language.substring(3, 5);
               language = languageCode + "/" + countryCode;
            } else {
               language = language.substring(0, 2);
            }
         } else if (this.isCountryRequiredForHelp(language)) {
            return null;
         }

         String inputMethod = "trackwheel";
         if (Trackball.isSupported()) {
            inputMethod = "trackball";
         }

         String accountType = "bbm";
         if (!ClientSessionState.getInstance().getUserInfo().isBBMail()) {
            accountType = "bis";
         }

         return ClientSessionState.getInstance().getBrandingInfo().getHelpRootURL() + "/" + accountType + "/" + inputMethod + "/" + language + "/" + wmlFile;
      } else {
         return null;
      }
   }

   private boolean isCountryRequiredForHelp(String language) {
      return "pt".equals(language) || "zh".equals(language);
   }

   private void invokeHelp() {
      BrowserSession defaultSession = Browser.getDefaultSession();
      defaultSession.displayPage(this._helpURL);
   }

   @Override
   protected boolean onSavePrompt() {
      return true;
   }

   @Override
   public void close() {
      if (this._closeEvent != null) {
         this.submitEvent(this._closeEvent);
      } else if (this.canGoBack()) {
         this.submitEvent(new BackEvent(39));
      } else {
         super.close();
      }
   }

   @Override
   protected void onUndisplay() {
      Application.getApplication().removeGlobalEventListener(this);
   }

   @Override
   protected void makeMenu(Menu menu, int instance) {
      super.makeMenu(menu, instance);
      if (instance != 65536 || menu.getInstance() != 65538 || menu.getInstance() != 65538 || menu.getInstance() != 65538) {
         if (this._helpMenuItem != null && instance != 65536) {
            menu.add(this._helpMenuItem);
            menu.add(MenuItem.separator(1));
         }

         int numContentAreaEvents = this._contentAreaEvents.size();
         if (numContentAreaEvents > 0) {
            Event contentAreaEvent = null;

            for (int i = 0; i < numContentAreaEvents; i++) {
               contentAreaEvent = (Event)this._contentAreaEvents.elementAt(i);
               NotificationMenuItem eventMenuItem = new NotificationMenuItem(contentAreaEvent.getLabel(), 10000, 10000);
               menu.add(eventMenuItem);
               this.attachEventToMenuItem(eventMenuItem, contentAreaEvent);
            }

            menu.add(MenuItem.separator(10001));
         }

         int numButtonBarEvents = this._buttonBarEvents.size();
         if (numButtonBarEvents > 0) {
            Event buttonBarEvent = null;

            for (int i = 0; i < numButtonBarEvents; i++) {
               buttonBarEvent = (Event)this._buttonBarEvents.elementAt(i);
               if (buttonBarEvent.isOnMenu()) {
                  NotificationMenuItem eventMenuItem = new NotificationMenuItem(buttonBarEvent.getLabel(), 10000, 10000);
                  menu.add(eventMenuItem);
                  this.attachEventToMenuItem(eventMenuItem, buttonBarEvent);
               }
            }

            menu.add(MenuItem.separator(10001));
         }

         this.addCustomMenuItems(menu, instance);
      }

      this.removeExistingCloseMenuItem(menu);
      this._closeMenuItem = new NotificationMenuItem(ApplicationResources.getString(15), Integer.MAX_VALUE, 1);
      this._closeMenuItem.setListener(this);
      menu.add(this._closeMenuItem);
      MenuItem menuItem = this.selectDefaultMenuItem(menu);
      if (menuItem != null) {
         menu.setDefault(menuItem);
      }
   }

   @Override
   public void setHelp(String wmlFile) {
      this._helpWMLFile = wmlFile;
      this._helpURL = this.generateHelpURL(wmlFile);
      if (this._helpURL != null) {
         if (this._helpImage == null) {
            this._helpImage = new HelpButton();
            this._titleBar.setHelpImage(this._helpImage);
            this._helpImage.setChangeListener(this);
         }

         this.reloadHelpMenuItem();
      }
   }

   @Override
   protected void onDisplay() {
      super.onDisplay();
      Application.getApplication().addGlobalEventListener(this);
   }

   private void removeExistingCloseMenuItem(Menu menu) {
      int menuSize = menu.getSize();
      MenuItem closeMenuItem = MenuItem.getPrefab(14);

      for (int i = 0; closeMenuItem != null && i < menuSize; i++) {
         MenuItem currentMenuItem = menu.getItem(i);
         if (currentMenuItem.equals(closeMenuItem)) {
            menu.deleteItem(i);
            return;
         }
      }
   }

   private void resetFontsInternal() {
      Font defaultFont = Font.getDefault();
      int fontSize = Ui.convertSize(defaultFont.getHeight(), 0, 3);
      if (fontSize > 8) {
         fontSize--;
      }

      this.setFont(defaultFont.derive(defaultFont.getStyle(), fontSize, 3));
      if (fontSize > 7) {
         fontSize--;
      }
   }

   @Override
   public boolean canGoBack() {
      return this._canGoBack;
   }

   private void submitEvent(Event event) {
      if (!(event instanceof EventWrapper)) {
         if (!(event instanceof CommandEvent)) {
            InputController.getInstance().submitEvent(event);
         } else {
            CommandEvent commandEvent = (CommandEvent)event;
            CommandDescriptor commandDescriptor = commandEvent.getDescriptor();
            Hashtable inputMap = new Hashtable();
            if (this.importFormDataFromUI(inputMap)) {
               Hashtable paramMap = null;
               String[] paramNames = commandDescriptor.getParamNames();
               if (paramNames != null) {
                  paramMap = new Hashtable();
                  int numParams = paramNames.length;

                  for (int i = 0; i < numParams; i++) {
                     Object inputValue = inputMap.get(paramNames[i]);
                     if (inputValue != null) {
                        paramMap.put(paramNames[i], inputValue);
                     }
                  }
               }

               InputController.getInstance().submitInput(commandDescriptor.getCommandID(), paramMap);
               return;
            }
         }
      } else {
         EventWrapper eventWrapper = (EventWrapper)event;
         Event delegateEvent = eventWrapper.getEvent();
         if (delegateEvent != null) {
            this.submitEvent(delegateEvent);
            return;
         }
      }
   }

   @Override
   public void clearScreen() {
      if (this._buttonBar != null) {
         this._buttonBar.deleteAll();
      }

      this._buttonBarEvents.removeAllElements();
      if (this._content != null) {
         this._content.deleteAll();
         this._content.add(new NullField());
      }

      this._contentAreaEvents.removeAllElements();
      this._fieldToEvent.clear();
      this._fieldToMenuItems.clear();
   }

   public BasicScreen() {
      super(562949953421312L);
      this._pageLayoutManager = new PageLayoutManager();
      this._content = new VerticalFieldManager(281474976710656L);
      this._content.add(new NullField());
      this._pageLayoutManager.setContent(this._content);
      this.add(this._pageLayoutManager);
      this._closeMenuItem = new NotificationMenuItem(ApplicationResources.getString(15), Integer.MAX_VALUE, 1);
      this._closeMenuItem.setListener(this);
      this.resetFontsInternal();
   }
}
