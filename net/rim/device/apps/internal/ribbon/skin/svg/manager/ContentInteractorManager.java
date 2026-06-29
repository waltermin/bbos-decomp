package net.rim.device.apps.internal.ribbon.skin.svg.manager;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.i18n.ResourceBundleFamily;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.api.system.SystemListener;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.api.ribbon.indicators.VoicemailIconManager;
import net.rim.plazmic.internal.mediaengine.model.intarray.v1_2.ModelInteractorImpl;
import net.rim.plazmic.internal.mediaengine.model.intarray.v1_2.NodeImpl;
import net.rim.plazmic.internal.mediaengine.service.FocusInteractor;
import net.rim.plazmic.internal.mediaengine.service.node.Node;
import net.rim.plazmic.internal.mediaengine.service.node.TextNode;

final class ContentInteractorManager implements GlobalEventListener, SystemListener {
   private final String HOTSPOT = "hotspot";
   private final String EMAIL = "email";
   private final String CALENDAR = "calendar";
   private final String SMS = "sms";
   private final String TIME = "time";
   private final String FROM = "from";
   private final String SUBJECT = "subject";
   private final String BODY = "body";
   private final String LOCATION = "location";
   private final String INFO = "info";
   private final String MISSEDCALLS = "missedcalls";
   private final String MISSEDCALL = "missedcall";
   private final String EMAIL_COUNT = "emailcount";
   private final String SMS_COUNT = "smscount";
   private final String PHONE_COUNT = "phonecount";
   private final String PHONE_COUNT_ROUND = "phonecount-round";
   private final String EMAIL_COUNT_ROUND = "emailcount-round";
   private final String MISSED_CALLS_APP = "missed_calls";
   private final String ACTIVATE_VOICEMAIL = "voicemail";
   private final String DEACTIVATE_VOICEMAIL = "noVoicemail";
   private final String SMS_APP = "sms_app";
   private final String SMS_APP_ALT = "sms_app_alternate";
   private final String PHONE_APP = "phone_app";
   private final String CALENDAR_APP = "calendar_app";
   private final String[] _possibleKeys = new String[]{
      "emailcount",
      "smscount",
      "phonecount",
      "phonecount-round",
      "emailcount-round",
      "email_app",
      "sms_app",
      "sms_app_alternate",
      "phone_app",
      "calendar_app",
      "missed_calls"
   };
   protected ResourceBundleFamily _rbf = ResourceBundle.getBundle(1137270090621229274L, "net.rim.device.apps.internal.resource.Ribbon");
   Handler[] _handlers = new Handler[4];
   private FocusInteractor _focusInteractor;
   private ModelInteractorImpl _modelInteractor;
   UiApplication _application = (UiApplication)Application.getApplication();
   private ContentInteractorManager$GlobalUpdater _globalUpdater = new ContentInteractorManager$GlobalUpdater(this);
   private static final String DATE_FORMAT_RESOURCE_ID = "DateFormatResourceId";
   static final String COUNT_TYPE_MESSAGE = "message";
   static final String COUNT_TYPE_SMSMMS = "smsmms";
   static final String COUNT_TYPE_PHONE = "missedphonecalls";
   static final String EMAIL_APP = "email_app";
   private static final int PHONE_APP_ALTERNATE_ID = 20;

   public final void launchInternal(String id) {
      if (id.startsWith("email")) {
         int index = Integer.parseInt(id.substring(5));
         this._handlers[1].invoke(index - 1);
      } else if (id.startsWith("calendar")) {
         int index = Integer.parseInt(id.substring(8));
         this._handlers[2].invoke(index - 1);
      } else if (id.startsWith("sms")) {
         int index = Integer.parseInt(id.substring(3));
         this._handlers[3].invoke(index - 1);
      } else {
         if (id.startsWith("missedcall")) {
            int index = Integer.parseInt(id.substring(10));
            this._handlers[0].invoke(index - 1);
         }
      }
   }

   protected final boolean makePhoneCall() {
      CallHandler handler = null;
      Handler var10000 = this._handlers[0];
      if (this._handlers[0] instanceof CallHandler) {
         handler = (CallHandler)var10000;
      }

      if (handler != null && this._focusInteractor != null) {
         int focus = this._focusInteractor.getItemInFocus();
         String selected = NodeImpl.getId(focus, this._modelInteractor);
         if (selected == null) {
            return false;
         }

         if (selected.startsWith("missedcall")) {
            selected = StringUtilities.removeChars(selected, "missedcall");
            selected = StringUtilities.removeChars(selected, "hotspot");
            int index = Integer.parseInt(selected);
            return handler.callSelected(index - 1);
         }
      }

      return false;
   }

   @Override
   public final void batteryStatusChange(int status) {
   }

   @Override
   public final void powerOff() {
   }

   @Override
   public final void powerUp() {
      this._globalUpdater.invokeLater();
   }

   @Override
   public final void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (guid == -7464003439710973532L) {
         this.updateHandlersDateFormat();
         this.updateHandlersTimeFormat();
      }

      if (guid == 7207871974803693937L) {
         this.updateHandlersTimeFormat();
      }

      if (guid == 8877632280522743328L || guid == 3596208183088439728L || guid == 7207871974803693937L || guid == -7464003439710973532L) {
         this._globalUpdater.invokeLater();
      } else if (guid == 6345609069135580235L) {
         this._globalUpdater.run();
      } else {
         if (guid == 6291453494459897456L) {
            this.checkVoicemailIndicator();
         }
      }
   }

   @Override
   public final void batteryLow() {
   }

   @Override
   public final void batteryGood() {
   }

   private final void checkVoicemailIndicator() {
      try {
         boolean value = VoicemailIconManager.getInstance().isIndicatorOn();
         if (!value) {
            this._modelInteractor.trigger(107, this._modelInteractor.getHandle("noVoicemail"), null);
            return;
         }

         this._modelInteractor.trigger(107, this._modelInteractor.getHandle("voicemail"), null);
      } finally {
         return;
      }
   }

   ContentInteractorManager(FocusInteractor fi, ModelInteractorImpl mi) {
      this._handlers[0] = CallHandlerFactory.getFactory().createInstance(mi, this._application);
      this._handlers[1] = EmailHandlerFactory.getFactory().createInstance(mi, this._application);
      this._handlers[2] = CalendarHandlerFactory.getFactory().createInstance(mi, this._application);
      this._handlers[3] = SMSHandlerFactory.getFactory().createInstance(mi, this._application);
      this.updateHandlersDateFormat();
      this._focusInteractor = fi;
      this._modelInteractor = mi;
      String[][] fieldLists = new Object[][]{{"time", "info"}, {"time", "from", "subject"}, {"time", "subject", "location"}, {"time", "from", "body"}};
      String[] names = new String[]{"missedcalls", "email", "calendar", "sms"};
      int maxCount = 0;

      for (int k = 0; k < names.length; k++) {
         String name = names[k];
         String[] fields = fieldLists[k];

         for (int i = 0; i < fields.length; i++) {
            int count = 0;

            for (Node workingNode = mi.getNode(((StringBuffer)(new Object())).append(name).append(count + 1).append(fields[i]).toString());
               workingNode != null && workingNode instanceof Object;
               workingNode = mi.getNode(((StringBuffer)(new Object())).append(name).append(count + 1).append(fields[i]).toString())
            ) {
               this._handlers[k].setNode(i + count * fields.length, (TextNode)workingNode);
               if (++count > maxCount) {
                  maxCount = count;
               }
            }
         }

         this._handlers[k].setMaxEntries(maxCount);
         maxCount = 0;
      }

      for (int i = this._possibleKeys.length - 1; i >= 0; i--) {
         Node node = mi.getNode(this._possibleKeys[i]);
         if (node != null && node instanceof Object) {
            if (this._possibleKeys[i].equals("emailcount")) {
               this._handlers[1].setRibbonComponentUnreadCountInteractor((TextNode)node, "message", false);
            } else if (this._possibleKeys[i].equals("emailcount-round")) {
               this._handlers[1].setRibbonComponentUnreadCountInteractor((TextNode)node, "message", true);
            } else if (this._possibleKeys[i].equals("email_app")) {
               this._handlers[1].setRibbonComponentApplicationNameInteractor((TextNode)node, "net_rim_bb_messaging_app.Messages");
            }

            if (this._possibleKeys[i].equals("phonecount")) {
               this._handlers[0].setRibbonComponentUnreadCountInteractor((TextNode)node, "missedphonecalls", false);
            } else if (this._possibleKeys[i].equals("phonecount-round")) {
               this._handlers[0].setRibbonComponentUnreadCountInteractor((TextNode)node, "missedphonecalls", true);
            } else if (this._possibleKeys[i].equals("phone_app")) {
               this._handlers[0].setRibbonComponentApplicationNameInteractor((TextNode)node, "net_rim_bb_phone_app.Phone");
            } else if (this._possibleKeys[i].equals("missed_calls")) {
               this._handlers[0].setAlternateNameNode((TextNode)node, 20);
            }

            if (this._possibleKeys[i].equals("calendar_app")) {
               this._handlers[2].setRibbonComponentApplicationNameInteractor((TextNode)node, "net_rim_bb_calendar_app.Calendar");
            }

            if (this._possibleKeys[i].equals("smscount")) {
               this._handlers[3].setRibbonComponentUnreadCountInteractor((TextNode)node, "smsmms", false);
            } else if (this._possibleKeys[i].equals("sms_app")) {
               this._handlers[3].setRibbonComponentApplicationNameInteractor((TextNode)node, "net_rim_bb_messaging_app.sms_and_mms");
            } else if (this._possibleKeys[i].equals("sms_app_alternate")) {
               this._handlers[3].setAlternateNameNode((TextNode)node, -1);
            }
         }
      }

      this.checkVoicemailIndicator();
   }

   private final void updateHandlersDateFormat() {
      String df = this._rbf.getString(11);

      label38:
      try {
         String resID = ThemeManager.getActiveTheme().getOption("DateFormatResourceId");
         if (resID != null) {
            int id = Integer.parseInt(resID);
            df = ThemeManager.getActiveTheme().getString(id);
         }
      } finally {
         break label38;
      }

      for (int i = 0; i < this._handlers.length; i++) {
         this._handlers[i].setDateFormat(df);
      }
   }

   private final void updateHandlersTimeFormat() {
      for (int i = 0; i < this._handlers.length; i++) {
         this._handlers[i].updateTimeFormat();
      }
   }
}
