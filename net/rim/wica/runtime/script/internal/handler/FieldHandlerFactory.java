package net.rim.wica.runtime.script.internal.handler;

import net.rim.device.api.util.IntHashtable;
import net.rim.wica.runtime.script.internal.WicaAppContext;

public final class FieldHandlerFactory {
   private static IntHashtable _handlers = new IntHashtable();
   private static WicaAppContext _context;

   public static final PropertyHandler getHandler(int type) {
      PropertyHandler handler = (PropertyHandler)_handlers.get(type);
      if (handler == null) {
         handler = initHandler(type);
      }

      return handler;
   }

   private static final PropertyHandler initHandler(int type) {
      PropertyHandler handler = null;
      switch (type) {
         case 0:
            handler = new BooleanFieldHandler();
            break;
         case 1:
            handler = new IntegerFieldHandler();
            break;
         case 2:
            handler = new DecimalFieldHandler();
            break;
         case 3:
            handler = new StringFieldHandler();
            break;
         case 4:
            handler = new DateFieldHandler();
            break;
         case 5:
            handler = new EnumFieldHandler(_context);
            break;
         case 6:
            handler = new CmpFieldHandler(_context);
            break;
         case 8:
            handler = new LongFieldHandler();
            break;
         case 32768:
         case 32769:
         case 32770:
         case 32771:
         case 32772:
         case 32773:
         case 32774:
         case 32776:
            handler = new ArrayFieldHandler(type, _context);
      }

      if (handler != null) {
         _handlers.put(type, handler);
      }

      return handler;
   }

   public static final void setWicaAppContext(WicaAppContext context) {
      _context = context;
   }
}
