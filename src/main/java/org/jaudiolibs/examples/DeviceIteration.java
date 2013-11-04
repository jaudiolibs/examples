
package org.jaudiolibs.examples;

import java.util.ServiceLoader;
import org.jaudiolibs.audioservers.AudioServerProvider;
import org.jaudiolibs.audioservers.ext.Device;

/**
 *
 * @author Neil C Smith
 */
public class DeviceIteration {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        for (AudioServerProvider provider : ServiceLoader.load(AudioServerProvider.class)) {
            System.out.println("Found library : " + provider.getLibraryName());
            System.out.println("==============================================");
            System.out.println("Devices");
            System.out.println("----------------------------------------------");
            for (Device dev : provider.findAll(Device.class)) {
                System.out.println(dev.getName() + " (inputs: " + dev.getMaxInputChannels() + ", outputs: " + dev.getMaxOutputChannels() + ")");
                
            }
        }
    }
}
