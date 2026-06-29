package net.rim.device.api.ui.component;

import net.rim.device.api.system.Application;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.system.HolsterListener;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.accessibility.AccessibleContext;
import net.rim.device.api.ui.container.DialogFieldManager;
import net.rim.device.api.ui.container.PopupScreen;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.api.util.CharacterUtilities;
import net.rim.device.api.util.IntHashtable;
import net.rim.device.internal.i18n.CommonResource;
import net.rim.device.internal.ui.Image;
import net.rim.device.internal.ui.UiInternal;
import net.rim.device.internal.ui.component.ImageField;
import net.rim.tid.awt.im.InputContext;
import net.rim.tid.im.layout.SLKeyLayout;

public class Dialog extends PopupScreen implements FieldChangeListener, HolsterListener {
   private RichTextField _label;
   private DialogFieldManager _dfm;
   private ListField _list;
   private Field _focusField;
   private boolean _isModal;
   private boolean _escapeEnabled;
   private int _preferredWidth;
   private int _returnValue;
   private int[] _values;
   private Object[] _choices;
   private int _defaultChoice = Integer.MIN_VALUE;
   private DialogClosedListener _closeListener;
   private CheckboxField _dontAskAgainCheckbox;
   private long _drawStyle = 68;
   private Application _app;
   public static final int D_OK;
   public static final int D_SAVE;
   public static final int D_DELETE;
   public static final int D_YES_NO;
   public static final int D_OK_CANCEL;
   public static final int CANCEL;
   public static final int OK;
   public static final int SAVE;
   public static final int DISCARD;
   public static final int DELETE;
   public static final int YES;
   public static final int NO;
   public static final int LIST;
   public static final int UNDEFINED;
   public static final int GLOBAL_STATUS;
   private static final int[] _resources = new int[]{
      10004,
      10000,
      10008,
      10012,
      10041,
      51,
      4408146,
      4801362,
      5391186,
      5526098,
      -805044104,
      879126131,
      2560,
      100663296,
      0,
      16789504,
      1006632960,
      512,
      50353920,
      1795162112
   };
   private static IntHashtable _globalResources = new IntHashtable();

   public void cancel() {
      this._returnValue = -1;
      this.close();
   }

   public int doModal() {
      this._isModal = true;
      this._returnValue = this._escapeEnabled ? -1 : this._defaultChoice;
      Ui.getUiEngine().pushModalScreen(this);
      return this._returnValue;
   }

   public boolean getDontAskAgainValue() {
      return this.isDontAskAgainChecked();
   }

   public RichTextField getLabel() {
      return this._label;
   }

   public int getSelectedValue() {
      return this._returnValue;
   }

   public void show() {
      this.show(50);
   }

   public void show(int priority) {
      this._isModal = false;
      if (this.isStyle(33554432)) {
         Ui.getUiEngine().pushGlobalScreen(this, priority, 2);
      } else {
         Ui.getUiEngine().pushScreen(this);
      }
   }

   public void setDialogClosedListener(DialogClosedListener listener) {
      this._closeListener = listener;
   }

   public ListField getListField() {
      return this._list;
   }

   public void setIcon(Image image) {
      ImageField field = null;
      if (image != null) {
         field = new ImageField();
         field.setImage(image);
      }

      this._dfm.setIcon(field);
   }

   public boolean isDontAskAgainChecked() {
      return this._dontAskAgainCheckbox == null ? false : this._dontAskAgainCheckbox.getChecked();
   }

   protected void onHotkeySelected(char key) {
   }

   public void setIcon(EncodedImage image) {
      BitmapField field = null;
      if (image != null) {
         field = new BitmapField(null, 65568);
         field.setImage(image);
      }

      this._dfm.setIcon(field);
   }

   protected void select() {
      int selection = this._list != null && this.isStyle(1) ? this._list.getSelectedIndex() : this.getLeafFieldWithFocus().getIndex();
      this.selectOrdinal(selection);
   }

   public void select(int value) {
      this.selectOrdinal(this.ordinalOfValue(value));
   }

   public final void setDefault(int defaultChoice) {
      this._defaultChoice = defaultChoice;
   }

   public void setDontAskAgainPrompt(boolean prompt) {
      if (this._dontAskAgainCheckbox != null != prompt) {
         if (prompt) {
            this._dontAskAgainCheckbox = new CheckboxField(CommonResource.getString(10094), false, 1073741824);
            this._dfm.getBottomManager().add(this._dontAskAgainCheckbox);
            return;
         }

         this._dfm.getBottomManager().delete(this._dontAskAgainCheckbox);
         this._dontAskAgainCheckbox = null;
      }
   }

   public final void setEscapeEnabled(boolean escapeEnabled) {
      this._escapeEnabled = escapeEnabled;
   }

   @Override
   public void outOfHolster() {
   }

   @Override
   public void inHolster() {
      if (this._escapeEnabled) {
         this.cancel();
      }
   }

   @Override
   public void fieldChanged(Field field, int context) {
      if (field instanceof ButtonField) {
         this.select();
      }
   }

   @Override
   public AccessibleContext getAccessibleSelectionAt(int index) {
      return this._dfm.getAccessibleSelectionAt(index);
   }

   @Override
   public int getAccessibleSelectionCount() {
      return this._dfm.getAccessibleSelectionCount();
   }

   public Dialog(String message, Object[] choices, int[] values, int defaultChoice, Bitmap bitmap, long style, DialogFieldManager dialogFieldManager) {
      super(dialogFieldManager, style);
      this.setup(message, choices, values, defaultChoice, bitmap);
   }

   public Dialog(String message, Object[] choices, int[] values, int defaultChoice, Bitmap bitmap, long style, long drawStyle) {
      super(new DialogFieldManager(), style);
      this._drawStyle = drawStyle;
      this.setup(message, choices, values, defaultChoice, bitmap);
   }

   @Override
   public int getPreferredWidth() {
      return this._preferredWidth;
   }

   protected static String[] getResourceChoices(int type) {
      return CommonResource.getStringArray(_resources[type]);
   }

   protected static String getResourceMessage(int type) {
      return (String)CommonResource.getBundle().getObject(_resources[type] + 3, true);
   }

   protected static int[] getResourceValues(int type) {
      return ((Dialog$DialogResources)_globalResources.get(_resources[type]))._responses;
   }

   protected static int getResourceDefaultValue(int type) {
      return ((Dialog$DialogResources)_globalResources.get(_resources[type]))._defaultResponse;
   }

   public Dialog(int type, String message, int defaultChoice, Bitmap bitmap, long style) {
      super(new DialogFieldManager(), style);
      if (message == null) {
         message = getResourceMessage(type);
      }

      String[] choices = getResourceChoices(type);
      int[] values = getResourceValues(type);
      this.setEscapeEnabled(true);
      this.setup(message, choices, values, defaultChoice, bitmap);
   }

   @Override
   protected boolean invokeAction(int action) {
      if (super.invokeAction(action)) {
         return true;
      }

      switch (action) {
         case 1:
            if (this.isStyle(1)) {
               this.select();
               return true;
            }
         default:
            return false;
      }
   }

   @Override
   public boolean isAccessibleChildSelected(int index) {
      return this._dfm.isAccessibleChildSelected(index);
   }

   @Override
   protected boolean navigationClick(int status, int time) {
      boolean result = super.navigationClick(status, time);
      if (!result && this.isStyle(1)) {
         this.select();
         return true;
      } else {
         return result;
      }
   }

   @Override
   protected void onUiEngineAttached(boolean attached) {
      if (!attached) {
         if (this._app != null) {
            this._app.removeHolsterListener(this);
            this._app = null;
         }

         this.removeFocus();
      } else {
         this._app = Application.getApplication();
         this._app.addHolsterListener(this);
         this._returnValue = this._escapeEnabled ? -1 : this._defaultChoice;
         boolean focusSet = this.getFieldWithFocusIndex() > 0 && this._focusField != null;
         if (this._dfm.hasCustomFields() && !focusSet) {
            for (int index = 0; index < this._dfm.getCustomManager().getFieldCount(); index++) {
               if (this._dfm.getCustomField(index).isFocusable()) {
                  this._dfm.getCustomField(index).setFocus();
                  focusSet = true;
                  break;
               }
            }
         }

         if (!focusSet && this._defaultChoice != Integer.MIN_VALUE) {
            int ordinal = Math.max(0, this.ordinalOfValue(this._defaultChoice));
            if (this.isStyle(1)) {
               if (this._list != null) {
                  this._list.setFocus();
                  this._list.setSelectedIndex(ordinal);
               }
            } else if (this._dfm.hasButtons()) {
               this._dfm.getButtonField(ordinal).setFocus();
            }
         }
      }

      this._focusField = this.getLeafFieldWithFocus();
      super.onUiEngineAttached(attached);
   }

   @Override
   protected void onExposed() {
      super.onExposed();
   }

   @Override
   protected void onObscured() {
      super.onObscured();
   }

   public Dialog(int type, String message, int defaultChoice, Bitmap bitmap, long style, boolean dontAskAgain) {
      super(new DialogFieldManager(), style);
      if (message == null) {
         message = getResourceMessage(type);
      }

      String[] choices = getResourceChoices(type);
      int[] values = getResourceValues(type);
      this.setEscapeEnabled(true);
      this.setup(message, choices, values, defaultChoice, bitmap);
      this.setDontAskAgainPrompt(dontAskAgain);
   }

   private void setup(String message, Object[] choices, int[] values, int defaultChoice, Bitmap bitmap) {
      this._dfm = (DialogFieldManager)this.getDelegate();
      this._label = new RichTextField(message, 36028797086072832L);
      this._dfm.setMessage(this._label);
      if (bitmap != null) {
         this._dfm.setIcon(new BitmapField(bitmap, 65568));
      }

      this._returnValue = this._defaultChoice = defaultChoice;
      this.setChoices(choices, values);
      if (this.isStyle(1) && this._list != null) {
         this._list.setSelectedIndex(this._defaultChoice);
      }
   }

   @Override
   public void add(Field field) {
      this._dfm.addCustomField(field);
   }

   @Override
   public int getAccessibleRole() {
      return 7;
   }

   @Override
   public String getAccessibleName() {
      return this.getLabel().getText();
   }

   public static void inform(String message) {
      Dialog d = new Dialog(0, message, 0, null, 0);
      d.setIcon(ThemeManager.getThemeAwareImage("dialog_information"));
      d.doModal();
   }

   public static int ask(int type) {
      return ask(type, null);
   }

   @Override
   protected boolean keyChar(char key, int status, int time) {
      boolean handled = false;
      if (this.getLeafFieldWithFocus() != this._list) {
         handled = super.keyChar(key, status, time);
      }

      if (!handled) {
         if (key == 27) {
            if (this._escapeEnabled) {
               this._returnValue = -1;
               this.close();
               handled = true;
            }
         } else if (key == '\n') {
            if (this.isStyle(1)) {
               this.select();
               handled = true;
            } else {
               this._returnValue = this._defaultChoice;
            }
         } else {
            key = UiInternal.map(Keypad.getLayout().getOriginalKeyCode(key, SLKeyLayout.convertStatusToModifiers(status)), status);
            key = Character.toLowerCase(key);
            if (this._choices != null) {
               String chars = null;
               if (InputContext.getInstance(false).isSureType()) {
                  StringBuffer temp = Keypad.getLayout().getComplementaryChars(key, SLKeyLayout.convertStatusToModifiers(status));
                  if (temp != null) {
                     chars = temp.toString();
                  }
               }

               for (int item = 0; item < this._choices.length; item++) {
                  String choice = this._choices[item].toString();
                  int hotposition = choice.indexOf(818);
                  if (hotposition > 0) {
                     char hotkey = Character.toLowerCase(CharacterUtilities.getOriginal(choice.charAt(hotposition - 1)));
                     if (hotkey == key || chars != null && chars.indexOf(hotkey) != -1) {
                        this.selectOrdinal(item);
                        this.onHotkeySelected(key);
                        break;
                     }
                  }
               }

               handled = true;
            }
         }
      }

      return handled;
   }

   public static int ask(int type, String message) {
      return ask(type, message, getResourceDefaultValue(type));
   }

   private int ordinalOfValue(int value) {
      int choiceindex = value;
      if (this._values != null) {
         choiceindex = -1;

         for (int lv = 0; lv < this._values.length; lv++) {
            if (this._values[lv] == value) {
               choiceindex = lv;
            }
         }
      }

      return choiceindex;
   }

   @Override
   public int getAccessibleChildCount() {
      return this._dfm.getAccessibleChildCount();
   }

   public static int ask(String message, Object[] choices, int defaultChoice) {
      return ask(message, choices, null, defaultChoice);
   }

   public static int ask(String message, Object[] choices, int[] values, int defaultChoice) {
      Dialog d = new Dialog(message, choices, values, defaultChoice, null, 0);
      d.setIcon(ThemeManager.getThemeAwareImage("dialog_question"));
      return d.doModal();
   }

   private void selectOrdinal(int selection) {
      if (this._values != null) {
         this._returnValue = this._values[selection];
      } else {
         this._returnValue = selection;
      }

      if (this._dfm.hasButtons() && selection < this._dfm.getButtonManager().getFieldCount()) {
         this._dfm.getButtonField(selection).setFocus();
      }

      this.doPaint();
      this.close();
   }

   @Override
   public void close() {
      if (this._app != null) {
         this._app.removeHolsterListener(this);
         this._app = null;
      }

      if (this.isGlobal() || this.isDisplayed()) {
         if (this.getPushMethod() == 0) {
            super.close();
         } else {
            String logData = "Unexpected dismissStatus required.";
            EventLogger.logEvent(-7509200465648525729L, logData.getBytes(), 5);
            Ui.getUiEngine().dismissStatus(this);
         }
      }

      if (!this._isModal && this._closeListener != null) {
         try {
            this._closeListener.dialogClosed(this, this._returnValue);
            return;
         } catch (Throwable var2) {
         }
      }
   }

   private void setChoices(Object[] choices, int[] values) {
      this._values = values;
      this._choices = choices;
      if (choices != null) {
         if (this.isStyle(1)) {
            ObjectListField list = new ObjectListField(this._drawStyle);
            list.set(choices);
            this._list = list;
            this._dfm.addCustomField(this._list);

            for (int i = 0; i < choices.length; i++) {
               this._preferredWidth = Math.max(this._preferredWidth, this.getFont().getBounds(choices[i].toString(), 0, choices[i].toString().length()));
            }
         } else {
            for (int i = 0; i < choices.length; i++) {
               this.addChoice(choices[i].toString());
            }
         }
      }

      if (!this._escapeEnabled) {
         if (values == null) {
            this.setEscapeEnabled(true);
            return;
         }

         for (int lv = 0; lv < this._values.length; lv++) {
            if (this._values[lv] == -1) {
               this.setEscapeEnabled(true);
               return;
            }
         }
      }
   }

   public Dialog(String message, Object[] choices, int[] values, int defaultChoice, Bitmap bitmap) {
      this(message, choices, values, defaultChoice, bitmap, 0);
   }

   public Dialog(String message, Object[] choices, int[] values, int defaultChoice, Bitmap bitmap, long style) {
      super(new DialogFieldManager(), style);
      this.setup(message, choices, values, defaultChoice, bitmap);
   }

   @Override
   public AccessibleContext getAccessibleChildAt(int index) {
      return this._dfm.getAccessibleChildAt(index);
   }

   public static int ask(int type, String message, int defaultChoice) {
      Dialog d = new Dialog(type, message, defaultChoice, null, 0);
      d.setIcon(ThemeManager.getThemeAwareImage("dialog_question"));
      return d.doModal();
   }

   public static void alert(String message) {
      Dialog d = new Dialog(0, message, 0, null, 0);
      d.setIcon(ThemeManager.getThemeAwareImage("dialog_exclamation"));
      d.doModal();
   }

   @Override
   protected boolean stylusTap(int x, int y, int status, int time) {
      return super.stylusTap(x, y, status, time) ? true : this.trackwheelClick(status, time);
   }

   @Override
   protected boolean trackwheelClick(int status, int time) {
      boolean result = super.trackwheelClick(status, time);
      if (!result && this.isStyle(1)) {
         this.select();
         return true;
      } else {
         return result;
      }
   }

   @Override
   protected void onDisplay() {
      this._focusField = this.getLeafFieldWithFocus();
      super.onDisplay();
   }

   @Override
   protected void onUndisplay() {
      super.onUndisplay();
   }

   private void addChoice(String choice) {
      ButtonField button = new ButtonField(choice, 12884901888L);
      button.setChangeListener(this);
      this._dfm.addButtonField(button);
      this._preferredWidth = Math.max(this._preferredWidth, this.getFont().getBounds(choice, 0, choice.length()));
   }

   static {
      _globalResources.put(10000, new Dialog$DialogResources(new int[]{1, 2, -1, -804651006, 3, -1, -804651006, 4, -1, -804651001, 10, 5}, 1));
      _globalResources.put(10008, new Dialog$DialogResources(new int[]{3, -1, -804651006, 4, -1, -804651001, 10, 5}, 3));
      _globalResources.put(10012, new Dialog$DialogResources(new int[]{4, -1, -804651001, 10, 5, 2, 1, 12}, -1));
      _globalResources.put(10004, new Dialog$DialogResources(new int[]{0, 0, 0, 0}, 0));
      _globalResources.put(10041, new Dialog$DialogResources(new int[]{0, -1, -804651005, 1, 2, -1, -804651006, 3}, 0));
   }
}
