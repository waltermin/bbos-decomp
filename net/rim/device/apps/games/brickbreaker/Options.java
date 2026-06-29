package net.rim.device.apps.games.brickbreaker;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import net.rim.device.api.games.util.HighScoreAccessor;
import net.rim.device.api.games.util.HighScoreServerListener;
import net.rim.device.api.games.util.RMSAccessor;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.component.PasswordEditField;
import net.rim.device.api.ui.component.TextField;
import net.rim.device.internal.deviceoptions.Owner;

public final class Options implements HighScoreServerListener, BrickBreakerResResource {
   int highScore = 0;
   int highLevel = 0;
   int paddleSpeed = 4;
   boolean paddleAccel = false;
   private static ResourceBundle _resources = ResourceBundle.getBundle(4228639183813622747L, "net.rim.device.apps.games.brickbreaker.BrickBreakerRes");
   private static int volume = 0;
   public static boolean sounds = false;
   public static boolean isThereAHSS = true;
   public static boolean isScoreSent = true;
   public static boolean _cheatMode = false;
   public static String userName = Owner.getOwnerName();
   public static String password = "";
   public static boolean isServiceActive = false;
   static final String NAME = "BrickBreakerOptions";

   Options() {
      this.restore();
   }

   public static final void setVolume(int vol) {
      volume = vol;
      Sounds.getSounds().updatePlayerVolumes();
   }

   public static final int getVolume() {
      return volume;
   }

   public final void resetHighScore() {
      isScoreSent = true;
      this.highScore = 0;
      this.highLevel = 0;
      this.save();
   }

   public final byte[] toBytes() {
      ByteArrayOutputStream o = new ByteArrayOutputStream();
      DataOutputStream out = new DataOutputStream(o);

      try {
         out.writeInt(this.highScore);
         out.writeInt(this.paddleSpeed);
         out.writeBoolean(this.paddleAccel);
         out.writeBoolean(sounds);
         out.writeInt(volume);
         out.writeBoolean(isThereAHSS);
         out.writeUTF(userName);
         out.writeUTF(password);
         out.writeBoolean(isScoreSent);
         out.writeBoolean(isServiceActive);
         out.writeInt(this.highLevel);
      } finally {
         ;
      }

      byte[] ret = o.toByteArray();

      try {
         out.close();
         return ret;
      } finally {
         ;
      }
   }

   public final void createFromBytes(byte[] input) {
      if (input != null) {
         ByteArrayInputStream i = new ByteArrayInputStream(input);
         DataInputStream in = new DataInputStream(i);

         label44:
         try {
            this.highScore = in.readInt();
            this.paddleSpeed = in.readInt();
            this.paddleAccel = in.readBoolean();
            sounds = in.readBoolean();
            volume = in.readInt();
            isThereAHSS = in.readBoolean();
            userName = in.readUTF();
            password = in.readUTF();
            isScoreSent = in.readBoolean();
            isServiceActive = in.readBoolean();
            this.highLevel = in.readInt();
            if (userName.equals("")) {
               userName = Owner.getOwnerName();
            }
         } finally {
            break label44;
         }

         try {
            in.close();
         } finally {
            return;
         }
      }
   }

   public final void restore() {
      this.createFromBytes(RMSAccessor.restore("BrickBreakerOptions"));
   }

   public final void save() {
      RMSAccessor.save(this.toBytes(), "BrickBreakerOptions");
   }

   public final void isGameActive() {
      if (!isThereAHSS) {
         HighScoreAccessor.isGameActivated("BrickBreaker", "4.1", this);
      }
   }

   static final void loadUsernamePassword(TextField userField, TextField passField) {
      boolean saveOptions = false;
      if (userName.length() == 0) {
         userName = Owner.getOwnerName().trim();
         saveOptions = true;
      }

      if (userName.length() > 24) {
         userName = userName.substring(0, 24).trim();
         saveOptions = true;
      }

      if (!HighScoreTextFilter.validate(userName)) {
         userName = "";
         saveOptions = true;
      }

      if (!HighScoreTextFilter.validate(password)) {
         password = "";
         saveOptions = true;
      }

      userField.setText(userName);
      passField.setText(password);
      if (saveOptions) {
         try {
            MenuScreen.getOptions().save();
         } finally {
            return;
         }
      }
   }

   public final void sendScore(boolean ask) {
      EditField user = new EditField(_resources.getString(46) + " : ", "", 24, 4503601774854144L);
      user.setFilter(new HighScoreTextFilter());
      user.setAllowUnicodeInput(false);
      PasswordEditField pass = new PasswordEditField(_resources.getString(47), "", 24, 4503601774854144L);
      pass.setFilter(new HighScoreTextFilter());
      pass.setAllowUnicodeInput(false);
      loadUsernamePassword(user, pass);
      if (isThereAHSS) {
         isScoreSent = false;
         this.save();
         int r = 4;
         if (ask) {
            Dialog diag = new Dialog(3, _resources.getString(50), 0, null, 0);
            diag.add(user);
            diag.add(pass);
            r = diag.doModal();
         }

         if (r == 4) {
            while (user.getText().length() == 0 || !HighScoreTextFilter.validate(user.getText())) {
               Options$StringPopup namePopup = new Options$StringPopup(_resources.getString(49), false);
               r = namePopup.doModal();
               if (r == -1) {
                  return;
               }

               try {
                  user.setText(namePopup.getResult().trim());
               } finally {
                  continue;
               }
            }

            while (pass.getText().length() < 6 || !HighScoreTextFilter.validate(pass.getText())) {
               Options$StringPopup pwdPopup = new Options$StringPopup(_resources.getString(54) + ":", true);
               r = pwdPopup.doModal();
               if (r == -1) {
                  return;
               }

               try {
                  pass.setText(pwdPopup.getResult());
               } finally {
                  continue;
               }
            }

            Options$SendingScorePopup popup = new Options$SendingScorePopup(this);
            HighScoreAccessor.sendScore(user.getText().trim(), pass.getText(), this.highScore, this.highLevel, "BrickBreaker", "4.1", popup);
            popup.doModal();
            userName = user.getText().trim();
            password = pass.getText();
            this.save();
         }
      }
   }

   @Override
   public final void serverReply(String result) {
      if (result != null) {
         try {
            int value = Integer.parseInt(result);
            switch (value) {
               case 12:
                  isThereAHSS = true;
                  isServiceActive = true;
                  isScoreSent = false;
                  this.save();
                  return;
            }
         } finally {
            return;
         }
      }
   }
}
