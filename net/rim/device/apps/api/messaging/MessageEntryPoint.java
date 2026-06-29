package net.rim.device.apps.api.messaging;

import java.util.Hashtable;
import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.CodeModuleManager;
import net.rim.device.api.ui.theme.Theme;
import net.rim.device.apps.api.messaging.resources.MessageResources;
import net.rim.device.apps.api.ribbon.Action;
import net.rim.device.apps.api.ribbon.indicators.UnreadCount$NewAndUnreadCount;

public final class MessageEntryPoint extends Action {
   private int _descriptionId;
   private Theme _currentTheme;
   String _state;
   boolean _isForeground;
   Object _extraInfo;
   private static Bitmap _icon = Bitmap.getBitmapResource("MessagingIcon28.gif");
   private static final String MESSAGING_MODULE_NAME = "net_rim_bb_messaging_app";
   static Hashtable _entries;
   private static final long ENTRIES_UID = 1654139945643910585L;

   public static final MessageEntryPoint register(String name, String argument, int descriptionId, Theme currentTheme) {
      MessageEntryPoint entry = new MessageEntryPoint(name, argument, descriptionId);
      _entries.put(argument, entry);
      entry.setCurrentTheme(currentTheme);
      entry.register();
      return entry;
   }

   public static final MessageEntryPoint register(String name, String argument, int descriptionId) {
      return register(name, argument, descriptionId, null);
   }

   public static final MessageEntryPoint findEntry(String argument) {
      return (MessageEntryPoint)_entries.get(argument);
   }

   static final ApplicationDescriptor getApplicationDescriptor() {
      int moduleHandle = CodeModuleManager.getModuleHandle("net_rim_bb_messaging_app");
      return CodeModuleManager.getApplicationDescriptors(moduleHandle)[0];
   }

   private MessageEntryPoint(String name, String argument, int descriptionId) {
      super(new ApplicationDescriptor(getApplicationDescriptor(), new String[]{argument}), "net_rim_bb_messaging_app." + name, 10);
      this._descriptionId = descriptionId;
   }

   @Override
   public final Object get(long propID, Object defaultReturned) {
      if (propID == 5) {
         return _icon;
      } else {
         return propID == 3 ? this.getDescription() : super.get(propID, defaultReturned);
      }
   }

   @Override
   public final String get(long propID, String defaultReturned) {
      return propID == 3 ? this.getDescription() : super.get(propID, defaultReturned);
   }

   @Override
   public final void set(long propID, String valueToSet) {
      if (propID == 2) {
         this.setState(valueToSet);
      } else {
         super.set(propID, valueToSet);
      }
   }

   @Override
   public final void set(long propID, Object valueToSet) {
      if (propID == 12) {
         this.setExtraInfo(valueToSet);
         if (valueToSet instanceof UnreadCount$NewAndUnreadCount) {
            UnreadCount$NewAndUnreadCount newCount = (UnreadCount$NewAndUnreadCount)valueToSet;
            newCount.updateActionState();
            return;
         }
      } else {
         super.set(propID, valueToSet);
      }
   }

   @Override
   public final void set(long propID, Integer valueToSet) {
      if (propID == 3) {
         this._descriptionId = valueToSet;
      } else {
         super.set(propID, valueToSet);
      }
   }

   @Override
   protected final String getDescription() {
      return this._currentTheme != null ? this._currentTheme.getString(this._descriptionId) : MessageResources.getString(this._descriptionId);
   }

   @Override
   protected final Object getExtraInfo() {
      return this._extraInfo;
   }

   protected final void setCurrentTheme(Theme current) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   @Override
   protected final String getState() {
      return this._state;
   }

   final void setState(String state) {
      if (this._state != null || state != null) {
         if (this._state == null || !this._state.equals(state)) {
            this._state = state;
            this.update();
         }
      }
   }

   final void setExtraInfo(Object info) {
      if (this._extraInfo != null || info != null) {
         this._extraInfo = info;
         this.update();
      }
   }

   public final void activate() {
      this._isForeground = true;
   }

   public final void deactivate() {
      this.setState(null);
      this._isForeground = false;
   }

   static {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      _entries = (Hashtable)ar.getOrWaitFor(1654139945643910585L);
      if (_entries == null) {
         _entries = new Hashtable(1);
         ar.put(1654139945643910585L, _entries);
      }
   }
}
