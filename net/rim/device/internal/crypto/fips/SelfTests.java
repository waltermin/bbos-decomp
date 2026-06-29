package net.rim.device.internal.crypto.fips;

import net.rim.device.api.crypto.SelfTestModule;
import net.rim.device.api.system.EventLogger;
import net.rim.vm.Array;

public final class SelfTests {
   private SelfTestsDialog _detailedDialog;
   private SelfTestsStartupDialog _startupDialog;
   private String[] _tests;
   private boolean _testsFailed;
   private boolean _startupRun;
   SelfTestModule[] _modules;
   private static final long LOGGER_GUID = 7146679653930594060L;

   public SelfTests(boolean startupRun) {
      EventLogger.register(7146679653930594060L, "Security Self Tests", 2);
      this._modules = new SelfTestModule[]{
         this.getSelfTestModule("net.rim.device.api.crypto.OSSelfTestModule"),
         this.getSelfTestModule("net.rim.device.internal.crypto.CryptoBlockSelfTestModule"),
         this.getSelfTestModule("net.rim.device.api.crypto.Crypto1SelfTestModule"),
         this.getSelfTestModule("net.rim.device.api.crypto.Crypto3SelfTestModule")
      };
      this._tests = new String[0];
      this._startupRun = startupRun;

      for (int i = 0; i < this._modules.length; i++) {
         this.getTestStrings(this._modules[i]);
      }
   }

   private final SelfTestModule getSelfTestModule(String module) {
      try {
         return (SelfTestModule)Class.forName(module).newInstance();
      } catch (IllegalAccessException var3) {
         return null;
      } catch (InstantiationException var4) {
         return null;
      } catch (ClassNotFoundException var5) {
         return null;
      }
   }

   private final void getTestStrings(SelfTestModule selfTestModule) {
      if (selfTestModule != null) {
         String[] newStrings = selfTestModule.getTestNames(this._startupRun);
         int copyIndex = this._tests.length;
         Array.resize(this._tests, this._tests.length + newStrings.length);
         System.arraycopy(newStrings, 0, this._tests, copyIndex, newStrings.length);
      }
   }

   public final void start() {
      if (this._startupRun) {
         this._startupDialog = new SelfTestsStartupDialog(this, this._tests);
         this._startupDialog.display();
      } else {
         this._detailedDialog = new SelfTestsDialog(this, this._tests, this._startupRun, null);
         this._detailedDialog.display();
      }
   }

   public final void dialogDisplayed() {
      new SelfTests$Tester(this).start();
   }

   public final void failure() {
      this._testsFailed = true;
   }
}
