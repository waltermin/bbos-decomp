package net.rim.device.apps.internal.passwordkeeper;

import net.rim.device.api.crypto.RandomSource;
import net.rim.device.api.i18n.ResourceBundleFamily;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.ActiveAutoTextEditField;
import net.rim.device.api.ui.component.BasicEditField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.PasswordEditField;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.ui.AppsMainScreen;
import net.rim.device.apps.api.ui.SystemEnabledMenu;
import net.rim.device.internal.i18n.CommonResource;
import net.rim.device.internal.ui.component.SimpleOKCancelInputDialog;
import net.rim.device.internal.ui.container.VerticalIndentFieldManager;

public final class PasswordKeeperElementScreen extends AppsMainScreen {
   private Font _boldFont;
   private PasswordKeeperElement _element;
   private VerticalIndentFieldManager _vifm;
   private RichTextField _usernameLabelField;
   private RichTextField _passwordLabelField;
   private RichTextField _websiteLabelField;
   private RichTextField _notesLabelField;
   private Field _titleField;
   private Field _usernameField;
   private Field _passwordField;
   private Field _websiteField;
   private Field _notesField;
   private PasswordKeeperScreen _screen;
   private Verb _randomVerb;
   private Verb _saveVerb;
   private Verb _editLabelsVerb;
   private Verb _deleteVerb;
   private Verb _closeVerb;
   private UiApplication _app = UiApplication.getUiApplication();
   private static String HTTP_STRING = "http://";
   private static final int MENU_ORDERING_SAVE = 16912384;
   private static final int MENU_ORDERING_EDIT = 16916480;
   private static final int MENU_ORDERING_EDIT_LABEL = 16917760;
   private static final int MENU_ORDERING_DELETE = 16920576;
   private static final int MENU_ORDERING_RANDOM = 16977920;
   private static final int MENU_ORDERING_CLOSE = 268439552;

   public PasswordKeeperElementScreen(PasswordKeeperScreen screen, PasswordKeeperElement element) {
      super(0);
      this.setDefaultClose(false);
      this._screen = screen;
      this._element = element;
      this._vifm = (VerticalIndentFieldManager)(new Object(1153220571769602048L));
      this.addFields();
   }

   private final void addFields() {
      Font font = Font.getDefault();
      this._boldFont = font.derive(font.getStyle() | 1);
      String usernameLabel = null;
      String passwordLabel = null;
      String websiteLabel = null;
      String notesLabel = null;
      String title = null;
      String username = null;
      String password = null;
      String website = null;
      String notes = null;
      if (this._element != null) {
         try {
            usernameLabel = this._element.getLabel(0);
            passwordLabel = this._element.getLabel(1);
            websiteLabel = this._element.getLabel(2);
            notesLabel = this._element.getLabel(3);
            title = this._element.getTitle();
            username = this._element.getField(1);
            password = this._element.getField(2);
            website = this._element.getField(3);
            notes = this._element.getField(4);
         } catch (DecryptionException e) {
            throw new Object();
         } catch (PasswordKeeperLockedException e) {
            throw new Object();
         }
      }

      if (PasswordKeeperUtilities.isAvailable(title)) {
         StringBuffer buffer = (StringBuffer)(new Object());
         buffer.append(CommonResource.getString(16)).append(':').append(' ').append(title);
         this.setTitle(buffer.toString());
      } else {
         this.setTitle(PasswordKeeper.getString(3016));
      }

      RichTextField titleLabel = (RichTextField)(new Object(PasswordKeeper.getString(2000), 45035996273704960L));
      titleLabel.setFont(this._boldFont);
      this._vifm.add(titleLabel);
      this._titleField = (Field)(new Object(null, title, 1000000, 16384));
      this._vifm.add(this._titleField);
      this._usernameLabelField = this.getReadOnlyField(1, usernameLabel);
      this._usernameLabelField.setFont(this._boldFont);
      this._vifm.add(this._usernameLabelField);
      this._usernameField = this.getAppropriateField(1, username);
      this._vifm.add(this._usernameField);
      this._passwordLabelField = this.getReadOnlyField(2, passwordLabel);
      this._passwordLabelField.setFont(this._boldFont);
      this._vifm.add(this._passwordLabelField);
      this._passwordField = this.getAppropriateField(2, password);
      this._vifm.add(this._passwordField);
      this._websiteLabelField = this.getReadOnlyField(3, websiteLabel);
      this._websiteLabelField.setFont(this._boldFont);
      this._vifm.add(this._websiteLabelField);
      this._websiteField = this.getAppropriateField(3, website);
      this._vifm.add(this._websiteField);
      this._notesLabelField = this.getReadOnlyField(4, notesLabel);
      this._notesLabelField.setFont(this._boldFont);
      this._vifm.add(this._notesLabelField);
      this._notesField = this.getAppropriateField(4, notes);
      this._vifm.add(this._notesField);
      this.add(this._vifm);
   }

   @Override
   protected final boolean keyChar(char key, int status, int time) {
      Field focus = this._app.getActiveScreen().getLeafFieldWithFocus();
      switch (key) {
         case '\n':
            if (focus != null) {
               if (focus == this._titleField) {
                  return this.setFocusOnNextField(1);
               } else if (focus == this._usernameField) {
                  return this.setFocusOnNextField(2);
               } else if (focus == this._passwordField) {
                  return this.setFocusOnNextField(3);
               } else if (focus == this._websiteField) {
                  return this.setFocusOnNextField(4);
               }
            }
         default:
            return super.keyChar(key, status, time);
         case '\u001b':
            if (this.changed()) {
               this.promptForSave();
               return true;
            } else {
               this.doClose();
               return true;
            }
      }
   }

   private final boolean setFocusOnNextField(int offset) {
      switch (offset) {
         case 0:
         default:
            if (this._titleField != null) {
               this._titleField.setFocus();
               return true;
            }
         case 1:
            if (this._usernameField != null) {
               this._usernameField.setFocus();
               return true;
            }
         case 2:
            if (this._passwordField != null) {
               this._passwordField.setFocus();
               return true;
            }
         case 3:
            if (this._websiteField != null) {
               this._websiteField.setFocus();
               return true;
            }
         case 4:
            if (this._notesField != null) {
               this._notesField.setFocus();
               return true;
            }
         case -1:
            return false;
      }
   }

   @Override
   protected final void makeMenu(SystemEnabledMenu menu, int instance) {
      ResourceBundleFamily rb = CommonResource.getBundle();
      if (this._saveVerb == null) {
         this._saveVerb = new PasswordKeeperElementScreen$PasswordVerb(this, 2, 16912384, rb, 18);
      }

      menu.add(this._saveVerb);
      if (this.changed()) {
         menu.setDefault(this._saveVerb);
      }

      if (this._element != null) {
         if (this._deleteVerb == null) {
            this._deleteVerb = new PasswordKeeperElementScreen$PasswordVerb(this, 4, 16920576, rb, 17);
         }

         menu.add(this._deleteVerb);
      }

      if (instance == 0) {
         if (this._randomVerb == null) {
            this._randomVerb = new PasswordKeeperElementScreen$PasswordVerb(this, 1, 16977920, PasswordKeeper.getBundle(), 3026);
         }

         menu.add(this._randomVerb);
         if (this.showEditLabel()) {
            if (this._editLabelsVerb == null) {
               this._editLabelsVerb = new PasswordKeeperElementScreen$PasswordVerb(this, 3, 16917760, PasswordKeeper.getBundle(), 3039);
            }

            menu.add(this._editLabelsVerb);
         }

         if (this._closeVerb == null) {
            this._closeVerb = new PasswordKeeperElementScreen$PasswordVerb(this, 5, 268439552, rb, 9);
         }

         menu.add(this._closeVerb);
      }
   }

   private final boolean showEditLabel() {
      Field focusField = this._vifm.getLeafFieldWithFocus();
      return focusField != this._titleField;
   }

   @Override
   protected final void verbInvoked(Verb verb, Object context, Object result) {
      this.invokeEndlineCheck();
   }

   public final PasswordKeeperElement getElement() {
      String usernameLabel = this._usernameLabelField.getText();
      String passwordLabel = this._passwordLabelField.getText();
      String websiteLabel = this._websiteLabelField.getText();
      String notesLabel = this._notesLabelField.getText();
      String[] labels = this.buildUniqueLabels(usernameLabel, passwordLabel, websiteLabel, notesLabel);
      String title = ((BasicEditField)this._titleField).getText();
      String username = ((BasicEditField)this._usernameField).getText();
      String password = null;
      if (!(this._passwordField instanceof Object)) {
         password = ((BasicEditField)this._passwordField).getText();
      } else {
         password = ((PasswordEditField)this._passwordField).getText();
      }

      String website = ((BasicEditField)this._websiteField).getText();
      String notes = ((BasicEditField)this._notesField).getText();
      String[] fields = new Object[]{title, username, password, website, notes};
      return new PasswordKeeperElement(labels, fields);
   }

   private final String[] buildUniqueLabels(String usernameLabel, String passwordLabel, String websiteLabel, String notesLabel) {
      String[] labels = new Object[4];
      labels[0] = usernameLabel.equals(PasswordKeeper.getString(2001)) ? null : usernameLabel;
      labels[1] = passwordLabel.equals(PasswordKeeper.getString(2002)) ? null : passwordLabel;
      labels[2] = websiteLabel.equals(PasswordKeeper.getString(3031)) ? null : websiteLabel;
      labels[3] = notesLabel.equals(PasswordKeeper.getString(2003)) ? null : notesLabel;
      return labels;
   }

   private final RichTextField getReadOnlyField(int fieldType, String fieldString) {
      if (!PasswordKeeperUtilities.isAvailable(fieldString)) {
         switch (fieldType) {
            case 0:
               throw new Object();
            case 1:
            default:
               fieldString = PasswordKeeper.getString(2001);
               break;
            case 2:
               fieldString = PasswordKeeper.getString(2002);
               break;
            case 3:
               fieldString = PasswordKeeper.getString(3031);
               break;
            case 4:
               fieldString = PasswordKeeper.getString(2003);
         }
      }

      return (RichTextField)(new Object(fieldString, 45035996273704960L));
   }

   private final Field getAppropriateField(int fieldType, String fieldString) {
      switch (fieldType) {
         case 0:
            throw new Object();
         case 1:
         default:
            return (Field)(new Object(null, fieldString, 1000000, 540672));
         case 2:
            if (PasswordKeeperOptions.getOptions().getShowPassword()) {
               return (Field)(new Object(null, fieldString, 32, 2202781892608L));
            }

            return (Field)(new Object(null, fieldString, 32, 2199560142848L));
         case 3:
            if (PasswordKeeperUtilities.isAvailable(fieldString)) {
               return (Field)(new Object(null, fieldString, 1000000, 117981184));
            }

            ActiveAutoTextEditField tempField = (ActiveAutoTextEditField)(new Object(null, HTTP_STRING, 1000000, 117981184));
            tempField.setCursorPosition(HTTP_STRING.length());
            return tempField;
         case 4:
            return (Field)(new Object(null, fieldString, 1000000, 16384));
      }
   }

   private final void invokeEndlineCheck() {
      BasicEditField titleField = (BasicEditField)this._titleField;
      BasicEditField usernameField = (BasicEditField)this._usernameField;
      BasicEditField passwordField = (BasicEditField)this._passwordField;
      BasicEditField websiteField = (BasicEditField)this._websiteField;
      BasicEditField notesField = (BasicEditField)this._notesField;
      String title = titleField.getText();
      String username = usernameField.getText();
      String password = passwordField.getText();
      String website = websiteField.getText();
      String notes = notesField.getText();
      int index = title.indexOf(10);
      if (index != -1) {
         titleField.setText(title.substring(0, index));
         usernameField.setText(((StringBuffer)(new Object())).append(title.substring(index + 1, title.length())).append(username).toString());
         username = usernameField.getText();
      }

      index = username.indexOf(10);
      if (index != -1) {
         usernameField.setText(username.substring(0, index));
         passwordField.setText(((StringBuffer)(new Object())).append(username.substring(index + 1, username.length())).append(password).toString());
         password = passwordField.getText();
      }

      index = password.indexOf(10);
      if (index != -1) {
         passwordField.setText(password.substring(0, index));
         websiteField.setText(((StringBuffer)(new Object())).append(password.substring(index + 1, password.length())).append(website).toString());
      }

      index = website.indexOf(10);
      if (index != -1) {
         websiteField.setText(website.substring(0, index));
         notesField.setText(((StringBuffer)(new Object())).append(website.substring(index + 1, website.length())).append(notes).toString());
      }
   }

   private final boolean changed() {
      if (this._element == null) {
         return this._usernameLabelField.isDirty()
            || this._passwordLabelField.isDirty()
            || this._websiteLabelField.isDirty()
            || this._notesLabelField.isDirty()
            || this._titleField.isDirty()
            || this._usernameField.isDirty()
            || this._passwordField.isDirty()
            || this._websiteField.isDirty()
            || this._notesField.isDirty();
      }

      try {
         PasswordKeeperElement newElement = this.getElement();
         return !this._element.equalFields(newElement);
      } catch (DecryptionException var2) {
         return true;
      } catch (PasswordKeeperLockedException var3) {
         return true;
      }
   }

   private final void doRandom() {
      PasswordKeeperOptions options = PasswordKeeperOptions.getOptions();
      int passwordLength = options.getRandomPasswordLength();
      byte[] password = new byte[passwordLength];
      boolean alpha = false;
      boolean numeric = false;
      boolean symbol = false;

      do {
         int count = 0;

         while (count < passwordLength) {
            int randomByte = RandomSource.getBytes(1)[0];
            if (options.getNumeric() && Character.isDigit((char)randomByte)) {
               password[count++] = (byte)randomByte;
               numeric = true;
            }

            if (options.getAlpha() && (Character.isLowerCase((char)randomByte) || Character.isUpperCase((char)randomByte))) {
               password[count++] = (byte)randomByte;
               alpha = true;
            }

            if (options.getSymbol() && this.isSymbol((char)randomByte)) {
               password[count++] = (byte)randomByte;
               symbol = true;
            }
         }
      } while (
         (!options.getAlpha() || !alpha) && (options.getAlpha() || alpha)
            || (!options.getNumeric() || !numeric) && (options.getNumeric() || numeric)
            || (!options.getSymbol() || !symbol) && (options.getSymbol() || symbol)
      );

      if (!(this._passwordField instanceof Object)) {
         if (this._passwordField instanceof Object) {
            ((BasicEditField)this._passwordField).setText((String)(new Object(password)));
         }
      } else {
         ((PasswordEditField)this._passwordField).setText((String)(new Object(password)));
      }

      this._passwordField.setDirty(true);
   }

   private final boolean isSymbol(char character) {
      return character >= '!' && character <= '/' || character >= ':' && character <= '@' || character >= '^' && character <= '`';
   }

   private final void doSave() {
      if (this.changed()) {
         String title = ((BasicEditField)this._titleField).getText();
         if (title == null || title.length() <= 0) {
            Dialog.inform(PasswordKeeper.getString(3028));
            return;
         }

         PasswordKeeperSync collection = this._screen.getCollection();
         if (this._element != null) {
            collection.removeSyncObject(this._element);
         }

         PasswordKeeperElement newElement = this.getElement();
         collection.addSyncObject(newElement);
         this._screen.setFocus(newElement);
      }

      this.doClose();
   }

   private final void doEditLabel() {
      int fieldType = 0;
      Field focusField = this._vifm.getLeafFieldWithFocus();
      byte var6;
      if (focusField == this._usernameField) {
         var6 = 0;
      } else if (focusField == this._passwordField) {
         var6 = 1;
      } else if (focusField == this._websiteField) {
         var6 = 2;
      } else {
         if (focusField != this._notesField) {
            throw new Object();
         }

         var6 = 3;
      }

      String existingLabel = null;
      switch (var6) {
         case -1:
            break;
         case 0:
         default:
            existingLabel = this._usernameLabelField.getText();
            break;
         case 1:
            existingLabel = this._passwordLabelField.getText();
            break;
         case 2:
            existingLabel = this._websiteLabelField.getText();
            break;
         case 3:
            existingLabel = this._notesLabelField.getText();
      }

      SimpleOKCancelInputDialog editFieldDialog = (SimpleOKCancelInputDialog)(new Object(0, PasswordKeeper.getString(3040)));
      editFieldDialog.setText(existingLabel);
      editFieldDialog.show();
      if (editFieldDialog.getCloseReason() != -1) {
         String newLabel = editFieldDialog.getText();
         switch (var6) {
            case 0:
            default:
               this._usernameLabelField.setText(newLabel);
               this._usernameLabelField.setDirty(true);
               return;
            case 1:
               this._passwordLabelField.setText(newLabel);
               this._passwordLabelField.setDirty(true);
               return;
            case 2:
               this._websiteLabelField.setText(newLabel);
               this._websiteLabelField.setDirty(true);
               return;
            case 3:
               this._notesLabelField.setText(newLabel);
               this._notesLabelField.setDirty(true);
            case -1:
         }
      }
   }

   private final void promptForSave() {
      int choice = Dialog.ask(1);
      if (choice == 1) {
         this.doSave();
      } else {
         if (choice == 2) {
            this.doClose();
         }
      }
   }

   private final void doDelete() {
      PasswordKeeperSync collection = this._screen.getCollection();
      collection.removeSyncObject(this._element);
   }

   private final void doClose() {
      this._app.popScreen(this);
   }
}
