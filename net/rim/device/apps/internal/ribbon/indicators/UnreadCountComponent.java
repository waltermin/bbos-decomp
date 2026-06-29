package net.rim.device.apps.internal.ribbon.indicators;

import java.util.Hashtable;
import net.rim.device.apps.api.ribbon.RibbonComponent;
import net.rim.device.apps.api.ribbon.RibbonComponent$RibbonComponentChangeListener;
import net.rim.device.apps.api.ribbon.StringRibbonComponent;
import net.rim.device.apps.api.ribbon.indicators.CountIndicator;
import net.rim.device.apps.api.ribbon.indicators.UnreadCount;
import net.rim.device.apps.api.ribbon.indicators.UnreadCountManager;
import net.rim.device.apps.api.ribbon.indicators.VoicemailIconManager;
import net.rim.device.apps.internal.phone.data.VoiceUnopenedCount;

public final class UnreadCountComponent extends StringRibbonComponent implements RibbonComponent$RibbonComponentChangeListener {
   private Object _indicator;
   private RibbonComponent$RibbonComponentChangeListener _listener;
   private IndicatorManagerImpl _indicatorManagerImpl;
   private boolean _enclosing;
   private char _openChar;
   private char _closeChar;
   private String _type;
   private int _typeEnum;
   public static final String FACTORY_ID;
   public static final String MISSED_CALLS;
   public static final String VOICE_MAIL;
   private static final int TYPE_MISSED_CALLS;
   private static final int TYPE_VOICE_MAIL;
   private static final int TYPE_MESSAGE_COUNT;

   UnreadCountComponent(IndicatorManagerImpl indicatorManagerImpl) {
      this._indicatorManagerImpl = indicatorManagerImpl;
   }

   private final void setType(String t) {
      this._type = t;
      if (t.equals("missedphonecalls")) {
         this._typeEnum = 1;
      } else if (t.equals("voice")) {
         this._typeEnum = 2;
      } else {
         this._typeEnum = 3;
      }
   }

   @Override
   public final void applyTheme() {
   }

   public final int getCount() {
      switch (this._typeEnum) {
         case 1:
         default:
            return VoiceUnopenedCount.getInstance().getNewCount();
         case 2:
            return VoicemailIconManager.getInstance().getVoicemailCount();
         case 3:
            boolean displayMessageCount = UnreadCountManager.getCountOptions().getDisplayMessageCount() != 0;
            if (displayMessageCount) {
               return ((CountIndicator)this._indicator).getCount();
            }
         case 0:
            return 0;
      }
   }

   public final boolean hasNewStatus() {
      switch (this._typeEnum) {
         case 1:
            if (VoiceUnopenedCount.getInstance().getNewCount() > 0) {
               return true;
            }

            return false;
         case 3:
            if (!(this._indicator instanceof Object)) {
               return false;
            }

            UnreadCount uc = (UnreadCount)this._indicator;
            return uc.hasNewStatus();
         default:
            return false;
      }
   }

   @Override
   public final String getText() {
      if (this._indicator != null) {
         int count = this.getCount();
         boolean displayMessageCount = UnreadCountManager.getCountOptions().getDisplayMessageCount() != 0;
         if (displayMessageCount && count > 0) {
            if (this._enclosing) {
               return ((StringBuffer)(new Object())).append(this._openChar).append(Integer.toString(count)).append(this._closeChar).toString();
            }

            return Integer.toString(count);
         }
      }

      return "";
   }

   private final void fetchIndicator() {
      switch (this._typeEnum) {
         case 1:
         default:
            this._indicator = VoiceUnopenedCount.getInstance();
            return;
         case 2:
            this._indicator = VoicemailIconManager.getInstance();
            return;
         case 3:
            this._indicator = this._indicatorManagerImpl.getCountIndicator(this._type);
         case 0:
      }
   }

   @Override
   public final void initialize(Hashtable params, Object context) {
      this.setType((String)params.get("type"));
      if (this._type != null) {
         String enclosing = (String)params.get("enclosing");
         if (enclosing != null) {
            switch (enclosing.charAt(0)) {
               case '(':
                  this._openChar = '(';
                  this._closeChar = ')';
                  this._enclosing = true;
                  break;
               case '[':
                  this._openChar = '[';
                  this._closeChar = ']';
                  this._enclosing = true;
                  break;
               case '{':
                  this._openChar = '{';
                  this._closeChar = '}';
                  this._enclosing = true;
            }
         }

         this.fetchIndicator();
         super.initialize(params, context);
      }
   }

   @Override
   public final synchronized void ribbonComponentChanged(RibbonComponent component) {
      if (this._indicator == null) {
         this.fetchIndicator();
         if (this._indicator == null) {
            return;
         }
      }

      if (this._listener != null) {
         this._listener.ribbonComponentChanged(this);
      }
   }

   @Override
   public final synchronized void setChangeListener(RibbonComponent$RibbonComponentChangeListener listener) {
      this._listener = listener;
   }
}
