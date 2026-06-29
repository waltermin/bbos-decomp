package net.rim.wica.runtime.metadata.component;

public interface Data extends Component {
   boolean isPersistable(int var1);

   void save();

   boolean isModified();
}
