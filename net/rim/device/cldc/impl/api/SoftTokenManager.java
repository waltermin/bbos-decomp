package net.rim.device.cldc.impl.api;

import java.util.Vector;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.util.StringUtilities;

public class SoftTokenManager {
   public static final long SINGLETON_ID;
   public static final long EVENT_LOGGER_GUID;
   public static final String EVENT_LOGGER_NAME;
   public static final int EVENT_SUCCESSFUL_SOFT_TOKEN_IMPORT;
   public static final int EVENT_SUCCESSFUL_SOFT_TOKEN_UNINSTALL;
   public static final int ERROR_PARSING_STORE;
   public static final int ERROR_SOFT_TOKEN_IMPORT;
   public static final int ERROR_UNABLE_TO_UNINSTALL_TOKEN;
   public static final int ERROR_TOKEN_NOT_INSTALLED;
   public static final int ERROR_UNABLE_TO_SET_ACTIVE_TOKEN;
   public static final int ERROR_GET_PASSPHRASE_INVALID_PARAMETER;
   public static final int ERROR_GET_PASSPHRASE_DECRYPT;
   public static final int ERROR_GET_PASSPHRASE;
   public static final int ERROR_PASSCODE_FAILURE;
   public static final int SOFTTOKEN_OK;
   public static final int SOFTTOKEN_ERR_FAIL;
   public static final int SOFTTOKEN_ERR_DUPLICATE_NICKNAME;
   public static final int SOFTTOKEN_ERR_DUPLICATE_SEED;
   public static final int SOFTTOKEN_ERR_DB_FULL;
   public static final int SOFTTOKEN_ERR_TOKEN_BAD_FORMAT;
   public static final int SOFTTOKEN_ERR_DECRYPT;
   public static final int SOFTTOKEN_ERR_WRONG_DEVICE_ID;
   public static final int SOFTTOKEN_ERR_WRONG_FORM_FACTOR;
   public static final String SDTID_SEED_EXTENSION;
   private static SoftTokenManager _instance;

   public static SoftTokenManager getInstance() {
      if (_instance == null) {
         _instance = (SoftTokenManager)ApplicationRegistry.getApplicationRegistry().get(8929046088285360721L);
      }

      return _instance;
   }

   public static void logEvent(int value, String text, int level) {
      StringBuffer tempBuffer = (StringBuffer)(new Object());
      tempBuffer.append(StringUtilities.intToString(value));
      tempBuffer.append(":");
      tempBuffer.append(text);
      EventLogger.logEvent(-334688660027749397L, tempBuffer.toString().getBytes(), level);
   }

   public static void logEvent(int value, int level) {
      EventLogger.logEvent(-334688660027749397L, value, level);
   }

   public void addListener(SoftTokenListener _1) {
      throw null;
   }

   public void removeListener(SoftTokenListener _1) {
      throw null;
   }

   public int save(String _1, String _2, int _3, boolean _4, String _5) {
      throw null;
   }

   public Vector getSoftTokens() {
      throw null;
   }

   public String[] getSoftTokenSerialNums() {
      throw null;
   }

   public SoftToken getSoftToken(String _1) {
      throw null;
   }

   public boolean delete(String _1, boolean _2) {
      throw null;
   }

   public String showDialog(String _1, String _2, boolean _3, boolean _4, String _5) {
      throw null;
   }

   public SoftTokenDialog getDialog(String _1, String _2, boolean _3, boolean _4, String _5) {
      throw null;
   }
}
