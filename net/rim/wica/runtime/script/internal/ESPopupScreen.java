package net.rim.wica.runtime.script.internal;

import net.rim.ecmascript.runtime.ESObject;
import net.rim.ecmascript.runtime.GlobalObject;
import net.rim.ecmascript.runtime.Value;

final class ESPopupScreen extends ESObject {
   private WicaAppContext _context;
   private static final String TypeOk;
   private static final String TypeYesNo;
   private static final String ButtonOk;
   private static final String ButtonYes;
   private static final String ButtonNo;
   private static final String[] Types = new String[]{"OK", "YES_NO", "OK_BUTTON", "YES_BUTTON", "NO_BUTTON"};
   private static final int[] TypeValues = new int[]{
      -1,
      2,
      0,
      2,
      3,
      521863424,
      1886404972,
      16827085,
      -1033101541,
      1701869908,
      712179968,
      -1975817147,
      16806977,
      -2104615050,
      16795989,
      100713909,
      100664923,
      526977907,
      524524,
      1679894024
   };

   ESPopupScreen(WicaAppContext context) {
      super("Dialog", GlobalObject.getInstance().getObjectPrototype());
      this._context = context;
      int length = Types.length;
      this.setGrowthIncrement(length + 1);

      for (int i = 0; i < length; i++) {
         this.addField(Types[i], 5, Value.makeIntegerValue(TypeValues[i]));
      }

      this.addHostFunction(new ESPopupScreen$1(this, "Dialog", "display", 2, context));
   }
}
