package org.jaudiolibs.examples;

import java.util.ServiceLoader;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jaudiolibs.audioops.impl.ChorusOp;
import org.jaudiolibs.audioservers.AudioConfiguration;
import org.jaudiolibs.audioservers.AudioServer;
import org.jaudiolibs.audioservers.AudioServerProvider;
import org.jaudiolibs.audioservers.ext.ClientID;
import org.jaudiolibs.audioservers.ext.Connections;
import org.jaudiolibs.pipes.impl.BusClient;
import org.jaudiolibs.pipes.impl.OpHolder;

/**
 * Basic example for processing audio using Pipe, AudioOps and the AudioServer API.
 *
 *
 * @author Neil C Smith
 */
public class ChorusPipe {

    public static void main(String[] args) throws Exception {

        String lib = "JACK"; // or "JavaSound";

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

        AudioConfiguration config = new AudioConfiguration(
                44100.0f, //sample rate
                2, // input channels
                2, // output channels
                256, //buffer size
                // extensions
                new ClientID("Chorus Pipe"),
                Connections.OUTPUT);

        
        BusClient bus = new BusClient(2, 2);
        
        ChorusOp chorusL = new ChorusOp();
        chorusL.setDepth(1.9f);
        chorusL.setRate(0.45f);
        ChorusOp chorusR = new ChorusOp();
        chorusR.setDepth(1.9f);
        chorusR.setRate(0.6f);
        
        OpHolder opL = new OpHolder(chorusL);
        OpHolder opR = new OpHolder(chorusR);
         
        bus.getSink(0).addSource(opL);
        opL.addSource(bus.getSource(0));
        bus.getSink(1).addSource(opR);
        opR.addSource(bus.getSource(1));
        
        final AudioServer server = provider.createServer(config, bus);

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
   
}


