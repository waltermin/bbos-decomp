package net.rim.device.apps.internal.ribbon.components;

import java.util.Hashtable;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.apps.api.ribbon.RibbonComponent$RibbonComponentChangeListener;
import net.rim.device.apps.api.ribbon.StringRibbonComponent;
import net.rim.device.apps.internal.ribbon.launcher.ApplicationEntry;
import net.rim.device.apps.internal.ribbon.launcher.HierarchyManager;
import net.rim.device.apps.internal.ribbon.launcher.HierarchyManager$EntryChangeListener;

final class EntryDescriptionComponentFactory$EntryDescription extends StringRibbonComponent implements HierarchyManager$EntryChangeListener, GlobalEventListener {
   private int _type;
   private int _position;
   private String _id;
   private String _folder;
   private boolean _leftAlign;
   private boolean _showInfo;
   private boolean _showName;
   private boolean _lookUsingOrdinal;
   private char _openChar;
   private char _closeChar;
   RibbonComponent$RibbonComponentChangeListener _listener;
   private String _text;

   @Override
   public final void applyTheme() {
   }

   @Override
   public final String getText() {
      if (this._showInfo || this._text == null) {
         this.updateText();
      }

      return this._text;
   }

   private final void updateText() {
      ApplicationEntry entry = this.getCurrentApplicationEntry(this._id);
      switch (this._type) {
         case 0:
            this._text = "";
            if (entry != null) {
               this._text = this._showName ? entry.getDescriptionNoHotkey() : "";
               if (this._showInfo) {
                  String extra = entry.getExtraInfo();
                  if (extra.length() > 0 && extra.charAt(0) != '0') {
                     StringBuffer sb = (StringBuffer)(new Object(this._text.length() + extra.length() + 2 + 1));
                     if (this._leftAlign) {
                        sb.append(this._openChar).append(extra).append(this._closeChar).append(' ').append(this._text);
                     } else {
                        sb.append(this._text).append(' ').append(this._openChar).append(extra).append(this._closeChar);
                     }

                     this._text = sb.toString();
                  }
               }
            }
      }
   }

   @Override
   public final void initialize(Hashtable params, Object context) {
      this._id = (String)params.get("id");
      this._closeChar = this._openChar = ' ';
      String enclosing = (String)params.get("enclosing");
      if (enclosing != null) {
         switch (enclosing.charAt(0)) {
            case '(':
               this._openChar = '(';
               this._closeChar = ')';
               break;
            case '[':
               this._openChar = '[';
               this._closeChar = ']';
               break;
            case '{':
               this._openChar = '{';
               this._closeChar = '}';
         }
      }

      if (this._id.startsWith("slot")) {
         this._lookUsingOrdinal = true;

         label63:
         try {
            this._position = Integer.parseInt(this._id.substring(4));
         } finally {
            break label63;
         }
      }

      this._showName = params.get("showname") != null;
      this._showInfo = false;
      this._leftAlign = false;
      String infoAlign = (String)params.get("showinfo");
      if (infoAlign != null) {
         this._showInfo = true;
         if (infoAlign.equalsIgnoreCase("left")) {
            this._leftAlign = true;
         }
      }

      String type = (String)params.get("type");
      if (type == null) {
         this._type = 0;
      } else {
         this._type = Integer.parseInt(type);
      }

      this._folder = (String)params.get("folder");
      if (this._folder == null) {
         this._folder = "";
      }

      super.initialize(params, context);
   }

   @Override
   public final void onEntryChange(ApplicationEntry entry) {
      if (this._id != null) {
         ApplicationEntry thisEntry = this.getCurrentApplicationEntry(this._id);
         if (entry == null || thisEntry == null || entry.getUniqueName().equals(thisEntry.getUniqueName())) {
            this.updateText();
            if (this._listener != null) {
               this._listener.ribbonComponentChanged(null);
            }
         }
      }
   }

   @Override
   public final void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      super.eventOccurred(guid, data0, data1, object0, object1);
      if (guid == -7464003439710973532L) {
         this.updateText();
         if (this._listener != null) {
            this._listener.ribbonComponentChanged(null);
         }
      }
   }

   @Override
   public final synchronized void setChangeListener(RibbonComponent$RibbonComponentChangeListener listener) {
      this._listener = listener;
   }

   private final ApplicationEntry getCurrentApplicationEntry(String id) {
      if (this._lookUsingOrdinal && this._folder != null) {
         try {
            ApplicationEntry entry = HierarchyManager.getInstance().getAppByIndexInFolder(this._folder, this._position);
            if (entry != null) {
               id = entry.getPropertiesName();
            }
         } finally {
            ;
         }
      }

      return HierarchyManager.getInstance().getApplicationEntry(id);
   }
}
