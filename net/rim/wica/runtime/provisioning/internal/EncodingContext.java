package net.rim.wica.runtime.provisioning.internal;

import java.util.Vector;
import net.rim.device.api.util.IntVector;
import net.rim.wica.runtime.metadata.internal.def.ComponentDefStruct;

public class EncodingContext {
   IntVector _defs = new IntVector(10, 1);
   IntVector _varData = new IntVector(10, 1);
   Vector _objectData = new Vector();

   public ComponentDefStruct toComponentDefStruct() {
      ComponentDefStruct cds = new ComponentDefStruct();
      this._defs.trimToSize();
      cds._defs = this._defs.getArray();
      this._varData.trimToSize();
      cds._varData = this._varData.getArray();
      this._objectData.trimToSize();
      cds._objectData = new Object[this._objectData.size()];
      this._objectData.copyInto(cds._objectData);
      return cds;
   }
}
