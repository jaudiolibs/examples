package org.jaudiolibs.examples;

import java.nio.FloatBuffer;
import java.util.List;
import java.util.ServiceLoader;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jaudiolibs.audioservers.AudioClient;
import org.jaudiolibs.audioservers.AudioConfiguration;
import org.jaudiolibs.audioservers.AudioServer;
import org.jaudiolibs.audioservers.AudioServerProvider;
import org.jaudiolibs.audioservers.ext.ClientID;
import org.jaudiolibs.audioservers.ext.Connections;

/**
 * Basic example for processing audio using the AudioServer API.
 *
 * A simple AudioClient that just copies input to output.
 *
 * Run main() - if not using NetBeans, make sure to configure JVM arguments.
 * -Xincgc is recommended. -Djna.nosys=true may be required if using the JACK
 * AudioServer and an older version of JNA is installed on your system.
 *
 * @author Neil C Smith
 */
public class PassThroughAudioClient implements AudioClient {

    public static void main(String[] args) throws Exception {

        /* Search for an AudioServerProvider that matches the required library name
         * using the ServiceLoader mechanism. This removes the need for a direct
         * dependency on any particular server implementation.
         * 
         * It is also possible to create particular AudioServerProvider's 
         * directly. 
         */
        String lib = "JavaSound"; // or "JACK";

        AudioServerProvider provider = null;
        for (AudioServerProvider p : ServiceLoader.load(AudioServerProvider.class)) {
            if (lib.equals(p.getLibraryName())) {
                provider = p;
                break;
            }
        }
        if (provider == null) {
            throw new NullPointerException("No AudioServer found that matches : " + lib);
        }

        /* Create an instance of our client - see methods in the implementation 
         * below for more information.
         */
        AudioClient client = new PassThroughAudioClient();

        /* Create an audio configuration.
         * 
         * The configuration is a hint to the AudioServer. Some servers (eg. JACK)
         * will ignore the sample rate and buffersize here.
         * The correct values will be passed to the client during configuration.
         * 
         * Various extension objects can be added to the AudioConfiguration.
         * The ClientID and Connections parameters here will be used by the JACK server.
         * 
         */
        AudioConfiguration config = new AudioConfiguration(
                44100.0f, //sample rate
                2, // input channels
                2, // output channels
                256, //buffer size
                // extensions
                new ClientID("Pass Through"),
                Connections.ALL);


        /* Use the AudioServerProvider to create an AudioServer for the client. 
         */
        final AudioServer server = provider.createServer(config, client);

        /* Create a Thread to run our server. All servers require a Thread to run in.
         */
        Thread runner = new Thread(new Runnable() {
            public void run() {
                // The server's run method can throw an Exception so we need to wrap it
                try {
                    server.run();
                } catch (Exception ex) {
                    Logger.getLogger(SineAudioClient.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        // set the Thread priority as high as possible.
        runner.setPriority(Thread.MAX_PRIORITY);
        // and start processing audio - you'll have to kill the program manually!
        runner.start();

    }
    
    // AudioClient implementation

    public void configure(AudioConfiguration context) throws Exception {
        /* Check the configuration of the passed in context, and set up any
         * necessary resources. Throw an Exception if the sample rate, buffer
         * size, etc. cannot be handled. DO NOT assume that the context matches
         * the configuration you passed in to create the server - it will
         * be a best match.
         */
    }

    public boolean process(long time, List<FloatBuffer> inputs, List<FloatBuffer> outputs, int nframes) {
        int channels = Math.min(inputs.size(), outputs.size());
        for (int c = 0; c < channels; c++) {
            outputs.get(c).put(inputs.get(c));
        }
        return true;
    }

    public void shutdown() {
        //dispose resources.
    }
}


