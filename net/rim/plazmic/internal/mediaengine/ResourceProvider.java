package net.rim.plazmic.internal.mediaengine;

public interface ResourceProvider {
   String MIME_TYPE_IMAGE = "image/png";
   String MIME_TYPE_AUDIO = "audio/midi";

   Object createResource(String var1, Object var2, ResourceContext var3, Object var4);

   Object createResourceFromURI(String var1, String var2, ResourceContext var3, Object var4);
}
