package net.rim.device.apps.internal.qm.peer.common;

import net.rim.device.api.system.Application;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.HolsterListener;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.component.BitmapField;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.CheckboxField;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.api.ui.component.ObjectListField;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.api.util.CharacterUtilities;
import net.rim.device.api.util.IntHashtable;
import net.rim.device.internal.i18n.CommonResource;
import net.rim.device.internal.ui.Image;
import net.rim.device.internal.ui.component.ImageField;
import net.rim.tid.im.layout.SLKeyLayout;

public final class QmThemedDialog extends QmThemedPopupScreen implements FieldChangeListener, HolsterListener {
   private RichTextField _label;
   private QmThemedDialogFieldManager _dfm;
   private ListField _list;
   private Field _focusField;
   private boolean _escapeEnabled;
   private int _preferredWidth;
   private int _returnValue;
   private int[] _values;
   private Object[] _choices;
   private int _defaultChoice = Integer.MIN_VALUE;
   private CheckboxField _checkbox;
   private long _drawStyle = 68;
   private Application _app;
   public static final int D_OK = 0;
   public static final int D_SAVE = 1;
   public static final int D_DELETE = 2;
   public static final int D_YES_NO = 3;
   public static final int D_OK_CANCEL = 4;
   public static final int CANCEL = -1;
   public static final int OK = 0;
   public static final int SAVE = 1;
   public static final int DISCARD = 2;
   public static final int DELETE = 3;
   public static final int YES = 4;
   public static final int NO = -1;
   public static final int LIST = 1;
   public static final int UNDEFINED = Integer.MIN_VALUE;
   public static final int GLOBAL_STATUS = 33554432;
   private static final int[] _resources = new int[]{
      10004,
      10000,
      10008,
      10012,
      10041,
      -805044220,
      169871904,
      51,
      -804651003,
      -1431655766,
      -858993460,
      -252645136,
      -16711936,
      -65536,
      -804651007,
      16777215,
      1866989824,
      727916,
      1987005697,
      16831589
   };
   private static IntHashtable _globalResources = new IntHashtable();

   public final void cancel() {
      this._returnValue = -1;
      this.close();
   }

   public final int doModal() {
      this._returnValue = this._escapeEnabled ? -1 : this._defaultChoice;
      Ui.getUiEngine().pushModalScreen(this);
      return this._returnValue;
   }

   public final void setEscapeEnabled(boolean escapeEnabled) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   public final void setIcon(Image image) {
      ImageField field = null;
      if (image != null) {
         field = new ImageField();
         field.setImage(image);
      }

      this._dfm.setIcon(field);
   }

   protected final void select() {
      int selection = this._list != null && this.isStyle(1) ? this._list.getSelectedIndex() : this.getLeafFieldWithFocus().getIndex();
      this.selectOrdinal(selection);
   }

   protected final void onHotkeySelected(char key) {
   }

   @Override
   public final void outOfHolster() {
   }

   @Override
   public final void inHolster() {
      if (this._escapeEnabled) {
         this.cancel();
      }
   }

   @Override
   public final void fieldChanged(Field field, int context) {
      if (field instanceof ButtonField) {
         this.select();
      }
   }

   public QmThemedDialog(String message, Object[] choices, int[] values, int defaultChoice, Bitmap bitmap) {
      this(message, choices, values, defaultChoice, bitmap, 0);
   }

   public QmThemedDialog(String message, Object[] choices, int[] values, int defaultChoice, Bitmap bitmap, long style) {
      super(new QmThemedDialogFieldManager(), style);
      this.setup(message, choices, values, defaultChoice, null, bitmap);
   }

   @Override
   public final int getPreferredWidth() {
      return this._preferredWidth;
   }

   protected static final String[] getResourceChoices(int type) {
      return CommonResource.getStringArray(_resources[type]);
   }

   protected static final String getResourceMessage(int type) {
      return (String)CommonResource.getBundle().getObject(_resources[type] + 3, true);
   }

   protected static final int[] getResourceValues(int type) {
      return ((QmThemedDialog$DialogResources)_globalResources.get(_resources[type]))._responses;
   }

   @Override
   protected final void onUiEngineAttached(boolean attached) {
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
   }

   @Override
   protected final void onExposed() {
      throw new RuntimeException("cod2jar: tail call (jumpspecial)");
   }

   @Override
   protected final void onObscured() {
      throw new RuntimeException("cod2jar: tail call (jumpspecial)");
   }

   public QmThemedDialog(String message, Object[] choices, int[] values, int defaultChoice, Bitmap bitmap, long style, long drawStyle) {
      super(new QmThemedDialogFieldManager(), style);
      this._drawStyle = drawStyle;
      this.setup(message, choices, values, defaultChoice, null, bitmap);
   }

   public static final void alert(String message) {
      long style = 0;
      QmThemedDialog d = new QmThemedDialog(0, message, 0, null, style);
      d.setIcon(ThemeManager.getThemeAwareImage("dialog_exclamation"));
      d.doModal();
   }

   private final void addChoice(String choice) {
      ButtonField button = new ButtonField(choice, 12884901888L);
      button.setChangeListener(this);
      this._dfm.addButtonField(button);
      this._preferredWidth = Math.max(this._preferredWidth, this.getFont().getBounds(choice, 0, choice.length()));
   }

   @Override
   protected final boolean keyChar(char key, int status, int time) {
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
            handled = this.handleOtherKeyHelper(key, status);
         }
      }

      return handled;
   }

   private final boolean handleOtherKeyHelper(char key, int status) {
      key = Character.toLowerCase(key);
      if (this._choices == null) {
         return false;
      }

      String chars = null;
      if (QmUtil.isReducedKeyboard()) {
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
               return true;
            }
         }
      }

      return true;
   }

   private final void setup(String message, Object[] choices, int[] values, int defaultChoice, CheckboxField checkbox, Bitmap bitmap) {
      this._dfm = (QmThemedDialogFieldManager)this.getMainManager();
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

      if (checkbox != null) {
         this._checkbox = checkbox;
         this._checkbox.setChangeListener(this);
         this.add(this._checkbox);
      }
   }

   private final int ordinalOfValue(int value) {
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
   public final void add(Field field) {
      this._dfm.addCustomField(field);
   }

   @Override
   protected final void paintBackground(Graphics graphics) {
      super.paintBackground(graphics);
   }

   public QmThemedDialog(int type, String message, int defaultChoice, CheckboxField checkbox, Bitmap bitmap, long style) {
      super(new QmThemedDialogFieldManager(), style);
      if (message == null) {
         message = getResourceMessage(type);
      }

      String[] choices = getResourceChoices(type);
      int[] values = getResourceValues(type);
      this.setEscapeEnabled(true);
      this.setup(message, choices, values, defaultChoice, checkbox, bitmap);
   }

   private final void selectOrdinal(int selection) {
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
   public final void close() {
      if (this._app != null) {
         this._app.removeHolsterListener(this);
         this._app = null;
      }

      Ui.getUiEngine().popScreen(this);
   }

   private final void setChoices(Object[] choices, int[] values) {
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

   public QmThemedDialog(int type, String message, int defaultChoice, Bitmap bitmap, long style) {
      this(type, message, defaultChoice, null, bitmap, style);
   }

   @Override
   protected final boolean stylusTap(int x, int y, int status, int time) {
      return super.stylusTap(x, y, status, time) ? true : this.trackwheelClick(status, time);
   }

   @Override
   protected final boolean trackwheelClick(int status, int time) {
      boolean result = super.trackwheelClick(status, time);
      if (!result && this.isStyle(1)) {
         this.select();
         return true;
      } else {
         return result;
      }
   }

   @Override
   protected final void onDisplay() {
      this._focusField = this.getLeafFieldWithFocus();
      super.onDisplay();
   }

   @Override
   protected final void onUndisplay() {
      throw new RuntimeException("cod2jar: tail call (jumpspecial)");
   }

   static {
      _globalResources.put(10000, new QmThemedDialog$DialogResources(new int[]{1, 2, -1, -804651006, 3, -1, -804651006, 4, -1, -804651003, 10004, 10000}, 1));
      _globalResources.put(10008, new QmThemedDialog$DialogResources(new int[]{3, -1, -804651006, 4, -1, -804651003, 10004, 10000}, 3));
      _globalResources.put(10012, new QmThemedDialog$DialogResources(new int[]{4, -1, -804651003, 10004, 10000, 10008, 10012, 10041}, -1));
      _globalResources.put(10004, new QmThemedDialog$DialogResources(new int[]{0, -804651004, 0, 0}, 0));
      _globalResources.put(10041, new QmThemedDialog$DialogResources(new int[]{0, -1, -804651007, 16711680, -804651007, 32768, -804651007, 40960}, 0));
   }
}
