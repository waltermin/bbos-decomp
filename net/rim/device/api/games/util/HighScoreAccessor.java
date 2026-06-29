package net.rim.device.api.games.util;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.cldc.io.utility.URIEncoder;

public final class HighScoreAccessor {
   private static final boolean RELEASE = true;
   private static final boolean SENDVERSION = true;
   private static final String INTERNAL_SERVER = "http://highscores.rim.net";
   private static String SERVER = "http://blackberrycanbefun.plazmic.com";
   private static String DEFAULT_SERVER = "http://blackberrycanbefun.plazmic.com";
   private static final String VERSION = "01";
   public static final int WRONG_PASSWORD = 2;
   public static final int GAME_NOT_SUPPORTED = 3;
   public static final int SCORE_SUBMITTED = 4;
   public static final int SCORE_NOT_SUBMITTED = 5;
   public static final int USERNAME_REJECTED = 6;
   public static final int USERNAME_INVALID = 8;
   public static final int PASSWORD_INVALID = 9;
   public static final int USER_NOT_ADDED = 11;
   public static final int GAME_EXISTS = 12;
   public static final int GAME_NOT_EXISTS = 13;
   private static final byte[] laLlave;

   private static final String getServer() {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      return (String)ar.get(-5985654863873213975L);
   }

   private static final String crypt(String data) {
      StringBuffer result = new StringBuffer();
      byte[] pinData = laLlave;
      byte[] urlData = data.getBytes();
      int k = 0;

      for (int i = 0; i < urlData.length; i++) {
         int temp = urlData[i] + pinData[k++];
         result.append(Integer.toHexString(temp));
         if (k >= pinData.length) {
            k = 0;
         }
      }

      return new String(result);
   }

   private static final String decrypt(String data) {
      StringBuffer result = new StringBuffer();
      byte[] pinData = laLlave;
      byte[] urlData = data.getBytes();
      int k = 0;

      for (int i = 0; i < urlData.length; i += 2) {
         String temp = data.substring(i, i + 2);
         result.append((char)(Integer.parseInt(temp, 16) - pinData[k++]));
         if (k >= pinData.length) {
            k = 0;
         }
      }

      return new String(result);
   }

   public static final void sendScore(
      String userName, String password, int highScore, int highLevel, String game, String version, HighScoreServerListener listener
   ) {
      sendScore(userName, password, highScore, highLevel, game, version, listener, 30000);
   }

   public static final void sendScore(
      String userName, String password, int highScore, int highLevel, String game, String version, HighScoreServerListener listener, int timeout
   ) {
      if (!DeviceInfo.isSimulator()) {
         userName = URIEncoder.encode(null, userName, "utf-8", true);
         password = URIEncoder.encode(null, password, "utf-8", true);
         String crypted = crypt(
            "user_name="
               + userName
               + ";password="
               + password
               + ";game_name="
               + game
               + ";game_version="
               + version
               + ";score_attr1="
               + highScore
               + ";score_attr2="
               + highLevel
         );
         String url = getServer() + "/mss/submithighscore?data=" + "01" + crypted + ";connectiontimeout=" + timeout;
         Sender sender = new Sender(url, listener);
         new Thread(sender).start();
      }
   }

   public static final String getHighScoreURL(String game, String version) {
      String crypted = crypt("game_name=" + game + ";game_version=" + version);
      return getServer() + "/mss/leaderboard?data=" + "01" + crypted;
   }

   public static final void isGameActivated(String game, String version, HighScoreServerListener listener) {
      if (!DeviceInfo.isSimulator()) {
         String crypted = crypt("game_name=" + game + ";game_version=" + version);
         String url = getServer() + "/mss/checkgame?data=" + "01" + crypted + ";connectiontimeout=30000";
         Sender sender = new Sender(url, listener);
         new Thread(sender).start();
      }
   }

   public static final void setServerString(String newServer) {
      SERVER = newServer;
   }

   public static final void resetServerString() {
      SERVER = DEFAULT_SERVER;
   }

   static {
      if (getServer() == null) {
         ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
         ar.replace(-5985654863873213975L, SERVER);
      }

      laLlave = new byte[]{97, 108, 108, 121, 111, 117, 114, 98, 97, 115, 101, 97, 114, 101, 98, 101, 108, 111, 110, 103, 116, 111, 117, 115};
   }
}
