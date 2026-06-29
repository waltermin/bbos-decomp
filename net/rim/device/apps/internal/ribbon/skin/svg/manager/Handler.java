package net.rim.device.apps.internal.ribbon.skin.svg.manager;

import java.util.Hashtable;
import net.rim.device.api.i18n.Locale;
import net.rim.device.api.i18n.SimpleDateFormat;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.theme.Theme;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.api.util.DateTimeUtilities;
import net.rim.device.apps.api.addressbook.PersonNameModel;
import net.rim.plazmic.internal.mediaengine.model.intarray.v1_2.ModelInteractorImpl;
import net.rim.plazmic.internal.mediaengine.service.node.Node;
import net.rim.plazmic.internal.mediaengine.service.node.TextNode;
import net.rim.plazmic.internal.mediaengine.service.node.VisualNode;

class Handler {
   private final String SUFFIX = "_resourceID";
   protected int MaxEntries = 20;
   protected UiApplication _application;
   protected ModelInteractorImpl _modelInteractor;
   private Handler$HandlerUpdater _handlerUpdater;
   protected UnreadCountComponentInteractor _unreadCount;
   protected SimpleRibbonComponentContentInteractor _applicationName;
   protected TextNode[] _nodes = new TextNode[this.MaxEntries * 3];
   protected TextNode _nodeAlternateName;
   protected int _alternateNameId;
   protected char[] BLANK = new char[0];
   SimpleDateFormat _sameDateFormat;
   SimpleDateFormat _laterDateFormat;
   private static final String TIME_FORMAT_RESOURCE_ID = "TimeFormatResourceId";
   private static final String PARAMETER_ID = "id";
   private static final String PARAMETER_SHOW_NAME = "showname";
   static final String PARAMETER_TYPE = "type";
   private static final String PARAMETER_ONLY_NEW = "onlyNew";
   private static final String ENTRY_DESCRIPTION = "EntryDescription";
   private static final String ROUND_BRACKETS = "roundBrackets";
   protected static final String PHONE = "missedcall";
   protected static final String CALENDAR = "calendar";
   protected static final String EMAIL = "messages";
   protected static final String SMS = "sms";
   protected static final String HOTSPOT = "hotspot";

   public void update(boolean _1) {
      throw null;
   }

   public void updateLater() {
      this._handlerUpdater.invokeLater();
   }

   public void setDisplayable(String nodeId, boolean value) {
      Node node = this._modelInteractor.getNode(nodeId);
      if (node instanceof VisualNode) {
         ((VisualNode)node).setDisplayable(value);
      }
   }

   public Handler(ModelInteractorImpl mi, UiApplication app) {
      this._modelInteractor = mi;
      this._application = app;
      this._handlerUpdater = new Handler$HandlerUpdater(this);
      this.updateTimeFormat();
   }

   public void updateTimeFormat() {
      String timeFormat = null;

      label29:
      try {
         String resID = ThemeManager.getActiveTheme().getOption("TimeFormatResourceId");
         if (resID != null) {
            int id = Integer.parseInt(resID);
            timeFormat = ThemeManager.getActiveTheme().getString(id);
         }
      } finally {
         break label29;
      }

      if (timeFormat != null) {
         this._sameDateFormat = new SimpleDateFormat(timeFormat);
      } else {
         this._sameDateFormat = new SimpleDateFormat(7);
      }
   }

   void setDateFormat(String df) {
      this._laterDateFormat = new SimpleDateFormat(df);
   }

   public void setRibbonComponentUnreadCountInteractor(TextNode node, String type, boolean roundBrackets) {
      Hashtable params = new Hashtable();
      params.put("type", type);
      if ("missedphonecalls".equals(type)) {
         params.put("onlyNew", "");
      }

      if (roundBrackets) {
         params.put("roundBrackets", "true");
      }

      if ("message".equals(type)) {
         this._unreadCount = new MessageUnreadCountComponentInteractor(node, params, this._modelInteractor);
      } else if ("smsmms".equals(type)) {
         this._unreadCount = new SMSMMSUnreadCountComponentInteractor(node, params, this._modelInteractor);
      } else {
         this._unreadCount = new UnreadCountComponentInteractor(node, params);
      }
   }

   public void setRibbonComponentApplicationNameInteractor(TextNode node, String id) {
      Hashtable params = new Hashtable();
      params.put("id", id);
      params.put("showname", "");
      this._applicationName = new SimpleRibbonComponentContentInteractor(node, "EntryDescription", params);
   }

   public void updateAlternateName() {
      Theme current = ThemeManager.getActiveTheme();

      try {
         if (this._alternateNameId < 0) {
            String idName = this._nodeAlternateName.getId();
            String resourceTextId = current.getOption(idName + "_resourceID");
            this._alternateNameId = Integer.parseInt(resourceTextId);
         }

         String altName = current.getString(this._alternateNameId);
         synchronized (this._modelInteractor) {
            this._nodeAlternateName.setString(altName.toCharArray());
         }
      } finally {
         return;
      }
   }

   public void setAlternateNameNode(TextNode node, int nameResourceId) {
      this._nodeAlternateName = node;
      this._alternateNameId = nameResourceId;
      this.updateAlternateName();
   }

   public void setNode(int index, TextNode node) {
      this._nodes[index] = node;
   }

   public void setMaxEntries(int entries) {
      this.MaxEntries = entries;
   }

   public Node getNode(int index) {
      return this._nodes[index];
   }

   public void invoke(int _1) {
      throw null;
   }

   protected String formattedTime(long t) {
      long now = System.currentTimeMillis();
      return DateTimeUtilities.isSameDate(now, t) ? this._sameDateFormat.formatLocal(t) : this._laterDateFormat.formatLocal(t);
   }

   private static void addString(StringBuffer buffer, String string) {
      if (string != null) {
         if (buffer.length() != 0) {
            buffer.append(' ');
         }

         buffer.append(string);
      }
   }

   private StringBuffer attachNames(String salutation, String firstName, String lastName) {
      StringBuffer result = new StringBuffer();
      switch (Locale.getSystemNameOrder()) {
         case 1:
            if (lastName != null && lastName.length() > 0) {
               addString(result, lastName);
               result.append(',');
            }

            addString(result, salutation);
            addString(result, firstName);
            break;
         default:
            addString(result, salutation);
            addString(result, firstName);
            addString(result, lastName);
      }

      return result;
   }

   public String getFullName(PersonNameModel name) {
      StringBuffer result = this.attachNames(name.getSalutation(), name.getFirstName(), name.getLastName());
      return result.length() == 0 ? "" : result.toString();
   }
}
