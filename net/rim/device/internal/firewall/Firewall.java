package net.rim.device.internal.firewall;

import net.rim.device.api.system.ApplicationRegistry;

public class Firewall implements FirewallInterface {
   static final long FIREWALL_GUID;
   public static final int ALLOW_TARGET;
   public static final int ALLOW_PROTOCOL;
   public static final int ALLOW_REQUEST;
   public static final int ASK;
   public static final int DENY_TARGET;
   public static final int DENY_PROTOCOL;
   public static final int DENY_REQUEST;
   public static final int DELETE;
   public static final int ALLOW_APPLICATION;
   public static final int DENY_APPLICATION;
   public static final byte BLOCK_SMS;
   public static final byte BLOCK_MMS;
   public static final byte BLOCK_BIS;
   public static final byte BLOCK_PUBLIC_PIN;
   public static final byte BLOCK_CORPORATE_PIN;
   public static final byte MAX_BLOCK_SETTING;
   public static final byte DECRYPT_FAILURE;
   public static final byte NOT_ENCRYPTED_ERROR;
   public static final byte ADDRESS_MISMATCH;
   public static final byte INVALID_DATAGRAM;
   public static final byte MISMATCHED_KEY;
   public static final byte MAX_DROPPINGS;

   public static final Firewall getInstance() {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      return (Firewall)ar.waitFor(6444309033832430955L);
   }

   @Override
   public void removeBlockedCountListener(BlockedCountListener _1) {
      throw null;
   }

   @Override
   public void addBlockedCountListener(BlockedCountListener _1) {
      throw null;
   }

   @Override
   public void resetBlockedCounts() {
      throw null;
   }

   @Override
   public void resetBlockedCount(byte _1) {
      throw null;
   }

   @Override
   public int getBlockedCount(byte _1) {
      throw null;
   }

   @Override
   public void incrementBlockedCount(byte _1) {
      throw null;
   }

   @Override
   public boolean setBlocking(byte _1, boolean _2) {
      throw null;
   }

   @Override
   public boolean isBlockingEnabledByItPolicy(byte _1) {
      throw null;
   }

   @Override
   public boolean isBlockingEnabled(byte _1) {
      throw null;
   }

   @Override
   public void setEnabled(boolean _1) {
      throw null;
   }

   @Override
   public boolean isEnabled() {
      throw null;
   }

   @Override
   public void reset(int _1) {
      throw null;
   }

   @Override
   public void reset() {
      throw null;
   }

   @Override
   public boolean allowConnection(String _1, String _2, int _3, FirewallContext _4) {
      throw null;
   }

   @Override
   public boolean allowConnection(String _1, String _2, int _3) {
      throw null;
   }

   @Override
   public boolean allowConnection(String _1, String _2, boolean _3) {
      throw null;
   }
}
