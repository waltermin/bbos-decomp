package net.rim.device.apps.internal.setupwizard.logging;

import net.rim.device.api.i18n.DateFormat;
import net.rim.device.apps.api.setupwizard.Log;

public final class LogImpl implements Log {
   private StringBuffer _buffer = (StringBuffer)(new Object());
   private DateFormat _formatter;
   private String _name;
   private LogManagerImpl _manager;

   LogImpl(String name, LogManagerImpl manager) {
      this._name = name;
      this._manager = manager;
      this._formatter = DateFormat.getInstance(61);
   }

   @Override
   public final void log(String text) {
      this._buffer.setLength(0);
      this._formatter.formatLocal(this._buffer, System.currentTimeMillis());
      this._buffer.append(": [");
      this._buffer.append(this._name);
      this._buffer.append("] ");
      this._buffer.append(text);
      this._manager.append(this._buffer.toString());
   }

   @Override
   public final String getCategoryName() {
      return this._name;
   }

   @Override
   public final Log clone(String categoryName) {
      return new LogImpl(categoryName, this._manager);
   }
}
