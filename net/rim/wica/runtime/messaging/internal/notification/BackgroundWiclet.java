package net.rim.wica.runtime.messaging.internal.notification;

import net.rim.device.api.system.UnsupportedOperationException;
import net.rim.wica.runtime.metadata.WicletContext;
import net.rim.wica.runtime.metadata.WicletRuntime;
import net.rim.wica.runtime.metadata.component.DataCollection;
import net.rim.wica.runtime.metadata.component.ScriptCollection;
import net.rim.wica.runtime.metadata.component.ui.ResourceCollection;
import net.rim.wica.runtime.metadata.component.ui.ScreenModel;
import net.rim.wica.runtime.metadata.component.ui.StyleCollection;
import net.rim.wica.runtime.metadata.internal.WicletImpl;
import net.rim.wica.runtime.metadata.util.ValueResolver;
import net.rim.wica.runtime.util.LongVector;

public class BackgroundWiclet extends WicletImpl {
   public BackgroundWiclet(WicletContext context, WicletRuntime runtime) {
      super(context, runtime);
   }

   @Override
   public boolean isBackground() {
      return true;
   }

   @Override
   public ScreenModel getScreenModel(int screenDef) {
      throw new UnsupportedOperationException();
   }

   @Override
   public ScriptCollection getScripts() {
      throw new UnsupportedOperationException();
   }

   @Override
   public StyleCollection getStyles() {
      throw new UnsupportedOperationException();
   }

   @Override
   public ResourceCollection getResources() {
      throw new UnsupportedOperationException();
   }

   @Override
   public void findWhere(LongVector results, DataCollection dataCollection, long[] handles, String expression, ValueResolver resolver) {
      throw new UnsupportedOperationException();
   }
}
