package net.rim.device.apps.internal.phone.control;

import net.rim.device.api.itpolicy.ITPolicy;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.api.phone.VoiceServices;

public final class FDNPolicyHandler implements GlobalEventListener {
   private FDNPolicyHandler$FDNCommandHandler _myCommandHandler;
   private FDNPolicyHandler$FDNEventHandler _myEventHandler;
   private int _type;
   private boolean _isRegistered;
   private String _incomingPolicy;
   private FDNPolicyHandler$BlackWhiteTable _incomingTable;
   private String _outgoingPolicy;
   private FDNPolicyHandler$BlackWhiteTable _outgoingTable;
   private static final int TYPE_CELL = 0;
   private static final int TYPE_WLAN = 1;

   public static final void registerOnceOnSystemStart() {
      new FDNPolicyHandler(0, 130).initialize();
      new FDNPolicyHandler(1, 530).initialize();
   }

   private FDNPolicyHandler(int type, int order) {
      this._type = type;
      this._myCommandHandler = new FDNPolicyHandler$FDNCommandHandler(this, order);
      this._myEventHandler = new FDNPolicyHandler$FDNEventHandler(this, order);
   }

   private final void initialize() {
      Application app = VoiceServices.getUiApplication();
      app.addGlobalEventListener(this);
      this.checkPolicies();
   }

   private final void checkPolicies() {
      boolean shouldRegister = this.updatePolicies();
      if (shouldRegister != this._isRegistered) {
         if (shouldRegister) {
            this._myCommandHandler.register();
            this._myEventHandler.register();
         } else {
            this._myCommandHandler.deregister();
            this._myEventHandler.deregister();
         }

         this._isRegistered = shouldRegister;
      }
   }

   private final boolean updatePolicies() {
      String incomingPolicy = this.getIncomingPolicy();
      if (!StringUtilities.strEqual(this._incomingPolicy, incomingPolicy)) {
         this._incomingPolicy = incomingPolicy;
         this._incomingTable = new FDNPolicyHandler$BlackWhiteTable(this, this._incomingPolicy);
      }

      String outgoingPolicy = this.getOutgoingPolicy();
      if (!StringUtilities.strEqual(this._outgoingPolicy, outgoingPolicy)) {
         this._outgoingPolicy = outgoingPolicy;
         this._outgoingTable = new FDNPolicyHandler$BlackWhiteTable(this, this._outgoingPolicy);
      }

      return this._incomingPolicy != null || this._outgoingPolicy != null;
   }

   private final String getIncomingPolicy() {
      switch (this._type) {
         case -1:
            return "";
         case 0:
         default:
            return ITPolicy.getString(103, 1);
         case 1:
            return ITPolicy.getString(103, 3);
      }
   }

   private final String getOutgoingPolicy() {
      switch (this._type) {
         case -1:
            return "";
         case 0:
         default:
            return ITPolicy.getString(103, 2);
         case 1:
            return ITPolicy.getString(103, 4);
      }
   }

   private final boolean isMyLine() {
      return this.isMyLine(-1);
   }

   private final boolean isMyLine(int callId) {
      try {
         int line = this._myCommandHandler.getAlternateLine(callId);
         switch (this._type) {
            case -1:
               return false;
            case 0:
            default:
               return line == 1 || line == 2;
            case 1:
               return line == 5000;
         }
      } finally {
         return false;
      }
   }

   private final void unitTest(String policy) {
      FDNPolicyHandler$BlackWhiteTable table = new FDNPolicyHandler$BlackWhiteTable(this, policy);
      if (!policy.equals(table.toString())) {
         throw new Object();
      }
   }

   @Override
   public final void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (guid == 8508406279413621091L) {
         this.checkPolicies();
      }
   }
}
