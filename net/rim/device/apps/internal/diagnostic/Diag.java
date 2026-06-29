package net.rim.device.apps.internal.diagnostic;

import net.rim.device.api.itpolicy.ITPolicy;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.PersistentStore;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.api.util.FactoryUtil;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.internal.blackberryemail.email.EmailComposeVerb;
import net.rim.device.apps.internal.blackberryemail.email.EmailMessageModelImpl;
import net.rim.device.apps.internal.blackberryemail.email.api.EmailBuilderApi;
import net.rim.device.internal.proxy.Proxy;

public final class Diag extends UiApplication {
   final String VERSION = "1.00.4";
   final String subLineReport = DiagnosticResources.getString(27);
   final String subLinePingEmail = "<$RemoveOnDelivery,SuppressSaveInSentItems>Self Ping Email - ";
   final long REPORTS_STORAGE_KEY = -1422526349460635256L;
   PersistentObject persistReport = PersistentStore.getPersistentObject(-1422526349460635256L);
   ReportDepot reports;
   DiagScreen mainScreen;
   static final long APP_DESCRIPTOR;
   private static final String INITIALIZE_ARGUMENT;
   static boolean appDisabled = false;
   private static Diag instance = null;
   private static DiagnosticDisplayVerb menuItemInstance = null;
   private static Dialog d;
   static final long FROMRADIOON;
   static final long FROMGPRSATT;
   static final long FROMPDPACT;
   static final long ICMPPING;
   static final int REGREQ;
   static final int MDPPING;
   static final int PIN2PIN;
   static final long EMAILPING;

   public final boolean isPinRecptSetByITPolicy() {
      return ITPolicy.getString(46, 3) != null;
   }

   public final boolean isEmailRecptSetByITPolicy() {
      return ITPolicy.getString(46, 2) != null;
   }

   public final void loadReports() {
      try {
         synchronized (this.persistReport) {
            this.reports = (ReportDepot)this.persistReport.getContents();
            if (this.reports == null) {
               this.reports = new ReportDepot();
               this.persistReport.setContents(this.reports, 51);
               this.persistReport.forceCommit();
            }
         }
      } finally {
         return;
      }
   }

   public final void saveReports() {
      try {
         synchronized (this.persistReport) {
            this.persistReport.setContents(this.reports, 51);
            this.persistReport.forceCommit();
         }

         this.mainScreen.populate();
      } finally {
         return;
      }
   }

   public final void saveEmailRecpt(String emailRecpt) {
      DiagOptions.getOptions().setEmailRecpt(emailRecpt);
   }

   public final void savePinRecpt(String pinRecpt) {
      DiagOptions.getOptions().setPinRecpt(pinRecpt);
   }

   public static final void main(String[] args) {
      if (args != null && args.length >= 1 && "init".equals(args[0])) {
         Proxy.getInstance().addGlobalEventListener(new DiagITPolicyListener());
         DiagOptions.getOptions().enableSynchronization();
         appDisabled = ITPolicy.getBoolean(46, 1, false);
         if (!appDisabled) {
            registerVerb();
         }

         ApplicationDescriptor original = ApplicationDescriptor.currentApplicationDescriptor();
         ApplicationDescriptor descriptor = (ApplicationDescriptor)(new Object(original, DiagnosticResources.getString(0), null));
         ApplicationRegistry.getApplicationRegistry().put(1548127712067364994L, descriptor);
      } else {
         Diag app = getInstance();

         try {
            app.enterEventDispatcher();
         } finally {
            return;
         }
      }
   }

   private Diag() {
      this.loadReports();
      this.startGUI();
   }

   private final void startGUI() {
      this.mainScreen = new DiagScreen(this);
      this.pushScreen(this.mainScreen);
   }

   public static final synchronized Diag getInstance() {
      if (instance == null) {
         instance = new Diag();
      }

      return instance;
   }

   public static final void registerVerb() {
      if (menuItemInstance == null) {
         menuItemInstance = new DiagnosticDisplayVerb();
      }

      menuItemInstance.registerVerb();
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public final void sendReport(int type, String dest, String subject, String body) {
      try {
         ContextObject contextObject = (ContextObject)(new Object());
         contextObject.setFlag(31);
         contextObject.setFlag(85);
         contextObject.setFlag(121);
         switch (type) {
            case -1:
               break;
            case 0:
            default:
               contextObject.setFlag(94);
               break;
            case 1:
               contextObject.setFlag(43);
         }

         EmailMessageModelImpl msg = (EmailMessageModelImpl)(new Object(contextObject));
         if (!dest.equals("")) {
            String[] names = new Object[2];
            names[0] = dest;
            names[1] = dest;
            ContextObject context = (ContextObject)(new Object());
            ContextObject.put(context, 251, names);
            Object recipient = FactoryUtil.createInstance(-2985347935260258684L, context);
            EmailBuilderApi.addRecipient(msg, 0, (RIMModel)recipient);
         }

         EmailBuilderApi.addSubjectLine(msg, subject);
         EmailBuilderApi.addMessageBody(msg, body);
         msg.setType((byte)32);
         EmailComposeVerb.showEditorScreen(contextObject, contextObject, msg);
      } catch (Throwable var11) {
         System.out.println(e.toString());
         return;
      }
   }

   public static final void showMessage(String msg) {
      d = (Dialog)(new Object(0, msg, 0, null, 33554432));
      d.setIcon(ThemeManager.getThemeAwareImage("dialog_exclamation"));
      Application.getApplication().invokeLater(new Diag$1());
      Application.getApplication().invokeLater(new Diag$2(), 3000, false);
   }
}
