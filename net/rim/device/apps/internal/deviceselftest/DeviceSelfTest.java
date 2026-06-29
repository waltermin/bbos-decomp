package net.rim.device.apps.internal.deviceselftest;

import java.util.Vector;
import net.rim.device.api.system.ApplicationManager;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.PersistentStore;
import net.rim.device.api.ui.UiApplication;

public final class DeviceSelfTest extends UiApplication {
   final int HANDSET_INTERNAL_SPEAKER = 1;
   final int HANDSET_HIGH_AUDIO = 2;
   final int HEADSET_SPEAKER = 3;
   final int BLUETOOTH_SPEAKER = 4;
   final String version = "1.00.1";
   Vector taskList;
   TestMainScreen mainScreen = null;
   TestTaskBase currentTest = null;
   int currentTestIndex;
   Report currentReport;
   PersistentObject persistReport;
   final long REPORTS_STORAGE_KEY = 7975587778218477079L;
   ReportDepot reports;
   Class testTaskName;
   final String[] taskSuiteInput = new String[]{"TestKeyboard", "TestTrackball"};
   final String[] taskSuiteMisc = new String[]{"TestLightSensor", "TestKeypadBacklight", "TestLED", "TestBacklight", "TestLCD", "TestVibrator", "TestHolster"};
   final String[] taskSuiteHandsetAudio = new String[]{"TestHandsetSpeaker", "TestHandsfreeSpeaker", "TestHandsetMicrophone"};
   final String[] taskSuiteHeadsetAudio = new String[]{"TestHeadsetDetect", "TestHeadsetSpeaker", "TestHeadsetMicrophone"};
   final String[] taskSuiteBluetoothAudio = new String[]{"TestBluetoothSpeaker", "TestBluetoothMicrophone"};
   final String[] taskSuiteRF = new String[]{"TestRFAntenna"};
   final String[] taskSuiteGPS = new String[]{"TestGPS"};
   private static DeviceSelfTest instance = null;
   private static final String INITIALIZE_ARGUMENT = "init";
   static final String packageName = "net.rim.device.apps.internal.deviceselftest";

   public final void loadReports() {
      this.reports = (ReportDepot)this.persistReport.getContents();
      if (this.reports == null) {
         this.reports = new ReportDepot();
         this.persistReport.setContents(this.reports);
         this.persistReport.forceCommit();
      }
   }

   public final void saveReports() {
      this.persistReport.setContents(this.reports);
      this.persistReport.forceCommit();
      this.mainScreen.populate();
   }

   final void injectTaskSuite(String[] taskSuite) {
      for (int i = 0; i < taskSuite.length; i++) {
         this.taskList.addElement(taskSuite[i]);
      }
   }

   final void initialize() {
      this.taskList.removeAllElements();
      this.currentTestIndex = -1;
      this.currentReport = null;
      this.currentTest = null;
   }

   final void getNextTest() {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aload 0
      // 01: aload 0
      // 02: getfield net/rim/device/apps/internal/deviceselftest/DeviceSelfTest.currentTestIndex I
      // 05: bipush 1
      // 06: iadd
      // 07: putfield net/rim/device/apps/internal/deviceselftest/DeviceSelfTest.currentTestIndex I
      // 0a: aload 0
      // 0b: getfield net/rim/device/apps/internal/deviceselftest/DeviceSelfTest.currentTestIndex I
      // 0e: aload 0
      // 0f: getfield net/rim/device/apps/internal/deviceselftest/DeviceSelfTest.taskList Ljava/util/Vector;
      // 12: invokevirtual java/util/Vector.size ()I
      // 15: if_icmpne 26
      // 18: aload 0
      // 19: invokevirtual net/rim/device/apps/internal/deviceselftest/DeviceSelfTest.initialize ()V
      // 1c: ldc_w "The whole test suite has been finisehd and the test report is available at the main screen!"
      // 1f: sipush 2000
      // 22: invokestatic net/rim/device/api/ui/component/Status.show (Ljava/lang/String;I)V
      // 25: return
      // 26: aload 0
      // 27: getfield net/rim/device/apps/internal/deviceselftest/DeviceSelfTest.taskList Ljava/util/Vector;
      // 2a: aload 0
      // 2b: getfield net/rim/device/apps/internal/deviceselftest/DeviceSelfTest.currentTestIndex I
      // 2e: invokevirtual java/util/Vector.elementAt (I)Ljava/lang/Object;
      // 31: checkcast java/lang/String
      // 34: astore 1
      // 35: new java/lang/StringBuffer
      // 38: dup
      // 39: ldc_w "net.rim.device.apps.internal.deviceselftest."
      // 3c: invokespecial java/lang/StringBuffer.<init> (Ljava/lang/String;)V
      // 3f: aload 1
      // 40: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 43: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 46: astore 1
      // 47: aload 0
      // 48: aload 1
      // 49: invokestatic java/lang/Class.forName (Ljava/lang/String;)Ljava/lang/Class;
      // 4c: putfield net/rim/device/apps/internal/deviceselftest/DeviceSelfTest.testTaskName Ljava/lang/Class;
      // 4f: aload 0
      // 50: aload 0
      // 51: getfield net/rim/device/apps/internal/deviceselftest/DeviceSelfTest.testTaskName Ljava/lang/Class;
      // 54: invokevirtual java/lang/Class.newInstance ()Ljava/lang/Object;
      // 57: checkcast net/rim/device/apps/internal/deviceselftest/TestTaskBase
      // 5a: putfield net/rim/device/apps/internal/deviceselftest/DeviceSelfTest.currentTest Lnet/rim/device/apps/internal/deviceselftest/TestTaskBase;
      // 5d: return
      // 5e: astore 1
      // 5f: getstatic java/lang/System.out Ljava/io/PrintStream;
      // 62: aload 1
      // 63: invokevirtual java/io/PrintStream.println (Ljava/lang/Object;)V
      // 66: return
      // 67: astore 1
      // 68: getstatic java/lang/System.out Ljava/io/PrintStream;
      // 6b: aload 1
      // 6c: invokevirtual java/io/PrintStream.println (Ljava/lang/Object;)V
      // 6f: return
      // 70: astore 1
      // 71: getstatic java/lang/System.out Ljava/io/PrintStream;
      // 74: aload 1
      // 75: invokevirtual java/io/PrintStream.println (Ljava/lang/Object;)V
      // 78: return
      // try (18 -> 43): 44 null
      // try (18 -> 43): 49 null
      // try (18 -> 43): 54 null
   }

   DeviceSelfTest() {
      this.taskList = new Vector();
      this.persistReport = PersistentStore.getPersistentObject(7975587778218477079L);
      this.loadReports();
      this.currentTestIndex = -1;
      this.startGUI();
   }

   public static final void main(String[] args) {
      DeviceSelfTest app = getInstance();
      if (app == null) {
         app = new DeviceSelfTest();
         instance = app;
         app.enterEventDispatcher();
      } else {
         ApplicationManager.getApplicationManager().requestForeground(app.getProcessId());
      }
   }

   private final void startGUI() {
      this.mainScreen = new TestMainScreen();
      this.pushScreen(this.mainScreen);
   }

   public static final synchronized DeviceSelfTest getInstance() {
      return instance;
   }
}
