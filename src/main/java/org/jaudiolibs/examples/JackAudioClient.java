
package org.jaudiolibs.examples;

import java.nio.FloatBuffer;
import java.util.List;
import org.jaudiolibs.audioservers.AudioClient;
import org.jaudiolibs.audioservers.AudioConfiguration;
import org.jaudiolibs.audioservers.jack.JackAudioServer;

/**
 * Basic example for processing audio using the AudioServer API and JACK.
 * 
 * A simple stereo JACK client that halves the volume of any audio passing
 * through it.
 * 
 * Run main() - if not using NetBeans, make sure to configure JVM arguments.
 * -Xincgc is recommended.
 * -Djna.nosys=true may be required if an older version of JNA is installed
 * on your system.
 * 
 * @author Neil C Smith
 */
public class JackAudioClient implements AudioClient {
    
    public static void main(String[] args) {
        // Create an instance of our client - see methods below for more info.
        JackAudioClient client = new JackAudioClient();
        
        /* Create an audio configuration - the JACK audio server currently ignores
         * the sample rate and buffer size parameters and will instead pass in the
         * correct values from the running server tot he client.  This may change 
         * if/when the server adds support for starting JACK.
         */
        AudioConfiguration config = new AudioConfiguration(44100, 2, 2, 512);
        
        /* Create the JACK server. You can name the JACK client and optionally
         * tell it to autoconnect inputs and outputs to the soundcard (generally
         * considered bad practice!)
         */
        final JackAudioServer server = JackAudioServer.create("jnajack-example", config, false, client);
        
        /* Create a Thread to run our server. All servers require a Thread to run in,
         * though this server will process audio in the JACK provided Thread, not 
         * this one.
         */     
        Thread runner = new Thread(new Runnable() {
            // The server's run method can throw an Exception so we need to wrap it
            public void run() {
                try {
                    server.run();
                } catch (Exception ex) {
                    ex.printStackTrace(System.out);
                }
            }
        });
        // set the Thread priority - not strictly necessary with JACK, but a good habit.
        runner.setPriority(Thread.MAX_PRIORITY);
        // and start processing audio - you'll have to kill the program manually!
        runner.start();
    }
    
    
    // AudioClient setup

    private float[] scratch;

    public void configure(AudioConfiguration context) throws Exception {
        /* Check the configuration of the passed in context, and set up any
         * necessary resources. Throw an Exception if the sample rate, buffer
         * size, etc. cannot be handled. DO NOT assume that the context matches
         * the configuration you passed in to create the server - it will
         * be a best match.
         */
        scratch = new float[context.getMaxBufferSize()];
    }

    public boolean process(long time, List<FloatBuffer> inputs, List<FloatBuffer> outputs, int nframes) {
        // channel count should match context passed in to configure()
        int channels = Math.min(inputs.size(), outputs.size());
        // loop through inputs and outputs
        for (int i=0; i<channels; i++) {
            // read input into scratch buffer - use nframes instead of depending on
            // array length in case server supports variable buffer size
            inputs.get(i).get(scratch, 0, nframes);
            // reduce volume by multiplying each sample by 0.5
            for (int k=0; k<nframes; k++) {
                scratch[k] *= 0.5f;
            }
            // write output - again use nframes
            outputs.get(i).put(scratch, 0, nframes);
        }
        // returning false will cause the server to disconnect your client -
        // use it if you want to process a set amount of audio.
        return true;
    }

    public void shutdown() {
        // dispose resources
        scratch = null;
    }
    
    
}
