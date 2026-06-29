package net.rim.wica.runtime.metadata;

import net.rim.wica.common.metadata.component.ComponentDef;
import net.rim.wica.common.metadata.component.EnumCollection;
import net.rim.wica.runtime.metadata.component.Data;
import net.rim.wica.runtime.metadata.component.DataCollection;
import net.rim.wica.runtime.metadata.component.Msg;
import net.rim.wica.runtime.metadata.component.ScriptCollection;
import net.rim.wica.runtime.metadata.component.ui.ResourceCollection;
import net.rim.wica.runtime.metadata.component.ui.ScreenModel;
import net.rim.wica.runtime.metadata.component.ui.StyleCollection;
import net.rim.wica.runtime.util.Lock;

public interface Wiclet {
   long getId();

   WicletContext getContext();

   WicletRuntime getRuntime();

   int getDefHandle(String var1);

   int getDefType(int var1);

   ComponentDef getComponentDef(int var1);

   Lock getDataLock();

   Data getGlobals();

   boolean isGlobalVar(String var1);

   DataCollection getDataCollection(int var1);

   Object getData(int var1);

   Msg getMsg(int var1);

   Msg getMsgFromCode(int var1);

   ScreenModel getScreenModel(int var1);

   ScriptCollection getScripts();

   EnumCollection getEnums();

   StyleCollection getStyles();

   ResourceCollection getResources();

   long verifyHandle(long var1);
}
