package com.hp.asi.hpic4vc.storage.provider.api;

/**
 * Interface used to test a java service call from the Flex UI.
 *
 * It must be declared as osgi:service with the same name in
 * main/resources/META-INF/spring/bundle-context-osgi.xml
 */
public interface Hpic4vc_storage_provider {
   /**
    * @return the same message it received
    */
   String echo(String message);

}
