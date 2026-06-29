package net.rim.device.cldc.impl.softtoken.rimsecuridlib;

import com.rsa.securidlib.blackberry.SecurIDLib;

public final class RimSecurIDLib {
   private SecurIDLib _securIDLib;
   public static final int ACTIVE_TOKEN;
   public static final String LIB_VERSION_STRING;
   public static final int MAJOR_VERSION;
   public static final int MAX_TOKENS;
   public static final int MINOR_VERSION;

   public final boolean isLicenseAccepted() {
      throw new RuntimeException("cod2jar: invokevirtual: negative slot, no fixup");
   }

   public final void setLicenseAccepted(boolean isLicenseAccepted) {
      throw new RuntimeException("cod2jar: invokevirtual: negative slot, no fixup");
   }

   public final int getActiveToken() {
      throw new RuntimeException("cod2jar: invokevirtual: negative slot, no fixup");
   }

   public final void setActiveToken(int index) {
      throw new RuntimeException("cod2jar: invokevirtual: negative slot, no fixup");
   }

   public final String getGMT() {
      throw new RuntimeException("cod2jar: invokevirtual: negative slot, no fixup");
   }

   public final String getSeededPasscode(byte[] seed, int offset, int nDigits, int period, String pin, boolean isAESAlg, String serialNo) {
      throw new RuntimeException("cod2jar: invokevirtual: negative slot, no fixup");
   }

   public final int getSeededSecondsRemaining(int period) {
      throw new RuntimeException("cod2jar: invokevirtual: negative slot, no fixup");
   }

   public final void importToken(String data, String password) {
      throw new RuntimeException("cod2jar: invokevirtual: negative slot, no fixup");
   }

   public final void setTokenName(int index, String name) {
      throw new RuntimeException("cod2jar: invokevirtual: negative slot, no fixup");
   }

   public final void setTokenPassphrase(int index, String oldPassphrase, String newPassphrase) {
      throw new RuntimeException("cod2jar: invokevirtual: negative slot, no fixup");
   }

   public final void deleteToken(int index, String serialNum) {
      throw new RuntimeException("cod2jar: invokevirtual: negative slot, no fixup");
   }

   public final String getTokenName(int index) {
      throw new RuntimeException("cod2jar: invokevirtual: negative slot, no fixup");
   }

   public final String getTokenSerialNo(int index) {
      throw new RuntimeException("cod2jar: invokevirtual: negative slot, no fixup");
   }

   public final void testTokenPassphrase(int index, String passphrase) {
      throw new RuntimeException("cod2jar: invokevirtual: negative slot, no fixup");
   }

   public final String getTokenPasscode(int index, String passphrase, int offset, String pin) {
      throw new RuntimeException("cod2jar: invokevirtual: negative slot, no fixup");
   }

   public final int getTokenSecondsRemaining(int index) {
      throw new RuntimeException("cod2jar: invokevirtual: negative slot, no fixup");
   }

   public final int getTokenIndex(String name) {
      throw new RuntimeException("cod2jar: invokevirtual: negative slot, no fixup");
   }

   public final int size() {
      throw new RuntimeException("cod2jar: invokevirtual: negative slot, no fixup");
   }
}
