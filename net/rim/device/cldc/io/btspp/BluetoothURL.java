package net.rim.device.cldc.io.btspp;

import javax.bluetooth.BluetoothConnectionException;
import javax.bluetooth.UUID;
import net.rim.device.internal.bluetooth.BluetoothME;

public class BluetoothURL {
   private String _scheme;
   private String _schemeSpecificPart;
   private String _host;
   private byte[] _address;
   private UUID _uuid;
   private int _channel = -1;
   private int _psm = -1;
   private String _name;
   private int _receiveMTU = 672;
   private int _transmitMTU = 48;
   private boolean _requestedTransmitMTU;
   private boolean _requestedReceiveMTU;
   public static final int MAX_L2CAP_MTU;

   public BluetoothURL(String scheme, String schemeSpecificPart) {
      this._scheme = scheme;
      this._schemeSpecificPart = schemeSpecificPart;
      this.parseSchemeSpecificPart();
   }

   @Override
   public String toString() {
      return ((StringBuffer)(new Object())).append(this._scheme).append(':').append(this._schemeSpecificPart).toString();
   }

   public String getScheme() {
      return this._scheme;
   }

   public String getHost() {
      return this._host;
   }

   public byte[] getAddress() {
      return this._address;
   }

   public UUID getUUID() {
      return this._uuid;
   }

   public int getPSM() {
      return this._psm;
   }

   public int getChannel() {
      return this._channel;
   }

   public String getName() {
      return this._name;
   }

   public int getReceiveMTU() {
      return this._receiveMTU;
   }

   public int getTransmitMTU() {
      return this._transmitMTU;
   }

   public boolean wasTransmitMTURequested() {
      return this._requestedTransmitMTU;
   }

   public boolean wasReceiveMTURequested() {
      return this._requestedReceiveMTU;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private void parseSchemeSpecificPart() {
      int start = 2;
      int end = this._schemeSpecificPart.indexOf(58, start);
      int length = this._schemeSpecificPart.length();
      boolean authorize = false;
      boolean authorizeSet = false;
      boolean encrypt = false;
      boolean encryptSet = false;
      boolean authenticate = true;
      boolean authenticateSet = false;
      boolean master = false;
      boolean masterSet = false;
      if (end == -1) {
         throw new Object("Invalid host");
      }

      this._host = this._schemeSpecificPart.substring(start, end).toLowerCase();
      start = end + 1;
      end = this._schemeSpecificPart.indexOf(59, start);
      if (end == -1) {
         end = length;
      }

      String s = this._schemeSpecificPart.substring(start, end);
      if (this._host.equals("localhost")) {
         boolean var36 = false /* VF: Semaphore variable */;

         try {
            var36 = true;
            this._uuid = new UUID(s, s.length() <= 8);
            var36 = false;
         } finally {
            if (var36) {
               throw new Object("Invalid UUID");
            }
         }
      } else {
         if (this._host.length() != 12) {
            throw new Object("Invalid device address");
         }

         this._address = BluetoothME.stringToDeviceAddress(this._host);
         if (this._scheme.equals("btl2cap")) {
            if (s.length() != 4) {
               throw new Object("Invalid PSM");
            }

            boolean var31 = false /* VF: Semaphore variable */;

            try {
               var31 = true;
               this._psm = Integer.parseInt(s, 16);
               if (this._psm < 4097 || this._psm > 65535) {
                  throw new Object("Invalid PSM");
               }

               var31 = false;
            } finally {
               if (var31) {
                  throw new Object("Invalid PSM");
               }
            }
         } else {
            boolean var26 = false /* VF: Semaphore variable */;

            try {
               var26 = true;
               this._channel = Integer.parseInt(s);
               if (this._channel <= 0 || this._channel > 30) {
                  throw new Object("Invalid channel");
               }

               var26 = false;
            } finally {
               if (var26) {
                  throw new Object("Invalid channel");
               }
            }
         }
      }

      start = end + 1;

      while (start < length) {
         int equals = this._schemeSpecificPart.indexOf(61, start);
         if (equals == -1) {
            throw new Object("Invalid parameter");
         }

         end = this._schemeSpecificPart.indexOf(59, equals);
         if (end == -1) {
            end = length;
         }

         String name = this._schemeSpecificPart.substring(start, equals).toLowerCase();
         String value = this._schemeSpecificPart.substring(equals + 1, end);
         if (value.length() == 0) {
            throw new Object("Invalid parameter");
         }

         if (name.equals("name")) {
            if (value.length() == 0 || value.charAt(0) == '$') {
               throw new Object("Invalid parameter");
            }

            this._name = value;
         } else if (name.equals("receivemtu")) {
            this._receiveMTU = Integer.parseInt(value);
            if (this._receiveMTU < 48 || this._receiveMTU > 1024) {
               throw new Object();
            }

            this._requestedReceiveMTU = true;
         } else if (name.equals("transmitmtu")) {
            this._transmitMTU = Integer.parseInt(value);
            if (this._transmitMTU < 1 || this._transmitMTU > 1024) {
               throw new Object();
            }

            this._requestedTransmitMTU = true;
         } else if (name.equals("master")) {
            if (masterSet) {
               throw new Object();
            }

            masterSet = true;
            master = this.getBoolean(value);
         } else if (name.equals("encrypt")) {
            if (encryptSet) {
               throw new Object();
            }

            encryptSet = true;
            encrypt = this.getBoolean(value);
         } else if (name.equals("authorize")) {
            if (authorizeSet) {
               throw new Object();
            }

            authorizeSet = true;
            authorize = this.getBoolean(value);
         } else if (name.equals("authenticate")) {
            if (authenticateSet) {
               throw new Object();
            }

            authenticateSet = true;
            authenticate = this.getBoolean(value);
         } else {
            if (!name.equals("psm")) {
               throw new Object("Invalid parameter");
            }

            boolean var21 = false /* VF: Semaphore variable */;

            try {
               var21 = true;
               this._psm = Integer.parseInt(value, 16);
               var21 = false;
            } finally {
               if (var21) {
                  throw new Object("Invalid PSM");
               }
            }
         }

         start = end + 1;
      }

      if (master) {
         throw new BluetoothConnectionException(6);
      } else if (!authenticate && (authorize || encrypt)) {
         throw new BluetoothConnectionException(6);
      }
   }

   private boolean getBoolean(String value) {
      if (value.equalsIgnoreCase("true")) {
         return true;
      } else if (value.equalsIgnoreCase("false")) {
         return false;
      } else {
         throw new Object("Invalid parameter");
      }
   }
}
