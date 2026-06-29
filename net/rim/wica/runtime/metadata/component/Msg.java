package net.rim.wica.runtime.metadata.component;

public interface Msg extends Component {
   int getMsgCode();

   String getMsgName();

   int[] getFieldMapping(int var1);

   boolean isSecure();

   int getScript();

   void send();

   void send(String var1);
}
