package net.rim.device.internal.firewall;

import net.rim.device.api.system.ApplicationRegistry;

public class Firewall implements FirewallInterface {
   static final long FIREWALL_GUID = 6444309033832430955L;
   public static final int ALLOW_TARGET = 0;
   public static final int ALLOW_PROTOCOL = 1;
   public static final int ALLOW_REQUEST = 2;
   public static final int ASK = 3;
   public static final int DENY_TARGET = 4;
   public static final int DENY_PROTOCOL = 5;
   public static final int DENY_REQUEST = 6;
   public static final int DELETE = 7;
   public static final int ALLOW_APPLICATION = 8;
   public static final int DENY_APPLICATION = 9;
   public static final byte BLOCK_SMS = 1;
   public static final byte BLOCK_MMS = 2;
   public static final byte BLOCK_BIS = 3;
   public static final byte BLOCK_PUBLIC_PIN = 4;
   public static final byte BLOCK_CORPORATE_PIN = 5;
   public static final byte MAX_BLOCK_SETTING = 5;
   public static final byte DECRYPT_FAILURE = -1;
   public static final byte NOT_ENCRYPTED_ERROR = -2;
   public static final byte ADDRESS_MISMATCH = -3;
   public static final byte INVALID_DATAGRAM = -4;
   public static final byte MISMATCHED_KEY = -5;
   public static final byte MAX_DROPPINGS = -5;

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
