package net.rim.device.apps.games.brickbreaker;

import java.io.IOException;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.system.Alert;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.Trackball;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.CheckboxField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.ObjectChoiceField;
import net.rim.device.api.ui.component.PasswordEditField;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.container.MainScreen;

public final class OptionsScreen extends MainScreen implements BrickBreakerResResource {
   private ObjectChoiceField paddleSpeed;
   private CheckboxField paddleAccel;
   private CheckboxField muteField;
   private ObjectChoiceField volumeField;
   private EditField userNameField;
   private PasswordEditField passwordField;
   private ButtonField _buttonClearLocalHighScores;
   private String _originalName;
   private Options _options;
   boolean _cancel = false;
   private final String[] paddleSpeedOptions;
   private final String[] volumeOptions;
   private static ResourceBundle _resources = ResourceBundle.getBundle(4228639183813622747L, "net.rim.device.apps.games.brickbreaker.BrickBreakerRes");

   public OptionsScreen() {
      this.paddleSpeedOptions = new String[]{_resources.getString(37), _resources.getString(38), _resources.getString(39)};
      this.volumeOptions = new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};
      this.setTitle(new LabelField(_resources.getString(65)));
      this.volumeField = new ObjectChoiceField(_resources.getString(42), this.volumeOptions);
      this.add(this.volumeField);
      this.muteField = new CheckboxField(_resources.getString(71), false);
      this.add(this.muteField);
      this.paddleSpeed = new ObjectChoiceField(_resources.getString(16), this.paddleSpeedOptions);
      this.add(this.paddleSpeed);
      this.paddleAccel = new CheckboxField(_resources.getString(17), false);
      this.add(this.paddleAccel);
      this.add(new SeparatorField());
      this._originalName = Options.userName;
      this.userNameField = new EditField(_resources.getString(46) + " : ", "", 24, 4503601774854144L);
      this.userNameField.setFilter(new HighScoreTextFilter());
      this.userNameField.setAllowUnicodeInput(false);
      this.add(this.userNameField);
      this.passwordField = new PasswordEditField(_resources.getString(47), "", 24, 4503601774854144L);
      this.passwordField.setFilter(new HighScoreTextFilter());
      this.passwordField.setAllowUnicodeInput(false);
      this.add(this.passwordField);
      Options.loadUsernamePassword(this.userNameField, this.passwordField);
      this._buttonClearLocalHighScores = new ButtonField(_resources.getString(60));
      this.add(this._buttonClearLocalHighScores);
      MenuItem save = MenuItem.getPrefab(15);
      this.addMenuItem(save);
      this.getMenu(0).setDefault(save);
   }

   public final void setOptions(Options o) {
      this._options = o;
      switch (this._options.paddleSpeed) {
         case 2:
            this.paddleSpeed.setSelectedIndex(0);
            break;
         case 4:
            this.paddleSpeed.setSelectedIndex(1);
            break;
         case 6:
            this.paddleSpeed.setSelectedIndex(2);
      }

      if (Trackball.isSupported()) {
         Trackball.setSensitivityX(100);
         Trackball.setSensitivityY(70);
      }

      this.paddleAccel.setChecked(this._options.paddleAccel);
      this.muteField.setChecked(!Options.sounds);
      this.volumeField.setSelectedIndex(Options.getVolume() / 10);
      Options.loadUsernamePassword(this.userNameField, this.passwordField);
   }

   @Override
   protected final boolean trackwheelClick(int status, int time) {
      if (this.getFieldWithFocus() == this._buttonClearLocalHighScores) {
         if (Dialog.ask(3, _resources.getString(61), -1) == 4) {
            this._options.resetHighScore();
         }

         return true;
      } else {
         return false;
      }
   }

   @Override
   public final void save() throws IOException {
      if (this._originalName.equals(this.userNameField.getText())
         || this.userNameField.getText().trim().length() != 0 && HighScoreTextFilter.validate(this.userNameField.getText())) {
         this._options.paddleSpeed = 2 + 2 * this.paddleSpeed.getSelectedIndex();
         if (Trackball.isSupported()) {
            Trackball.setSensitivityX(100);
            Trackball.setSensitivityY(70);
         }

         this._options.paddleAccel = this.paddleAccel.getChecked();
         Options.sounds = !this.muteField.getChecked();
         Options.setVolume(this.volumeField.getSelectedIndex() * 10);
         Options.userName = this.userNameField.getText().trim();
         this._originalName = Options.userName.substring(0);
         Options.password = this.passwordField.getText().trim();
         if (Alert.isMIDISupported() && Options.sounds) {
            Alert.setVolume(Options.getVolume());
         }

         this._cancel = false;
      } else {
         Dialog.inform(_resources.getString(55));
         throw new IOException(_resources.getString(55));
      }
   }

   @Override
   public final boolean keyControl(char c, int status, int time) {
      switch (c) {
         case '\u0095':
            break;
         case '\u0096':
         default:
            int volumeSetting = this.volumeField.getSelectedIndex();
            if (volumeSetting < 10) {
               volumeSetting++;
            }

            this.volumeField.setSelectedIndex(volumeSetting);
            this.setDirty(true);
            break;
         case '\u0097':
            int volumeSetting = this.volumeField.getSelectedIndex();
            if (volumeSetting > 0) {
               volumeSetting--;
            }

            this.volumeField.setSelectedIndex(volumeSetting);
            this.setDirty(true);
      }

      return super.keyControl(c, status, time);
   }

   @Override
   public final boolean keyDown(int keycode, int time) {
      int key = Keypad.key(keycode);
      switch (key) {
         case 273:
            this.muteField.setChecked(!this.muteField.getChecked());
            this.setDirty(true);
         default:
            return super.keyDown(keycode, time);
      }
   }
}
