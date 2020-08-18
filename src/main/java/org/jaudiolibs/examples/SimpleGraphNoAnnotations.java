package org.jaudiolibs.examples;

import org.jaudiolibs.audioservers.ext.ClientID;
import org.jaudiolibs.audioservers.ext.Connections;
import org.jaudiolibs.pipes.Pipe;
import org.jaudiolibs.pipes.graph.Clock;
import org.jaudiolibs.pipes.graph.Graph;
import org.jaudiolibs.pipes.graph.GraphPlayer;
import org.jaudiolibs.pipes.graph.Property;
import org.jaudiolibs.pipes.units.Chorus;
import org.jaudiolibs.pipes.units.IIRFilter;
import org.jaudiolibs.pipes.units.Osc;
import org.jaudiolibs.pipes.units.Waveform;

/**
 * A simple Pipes Graph example without use of annotations.
 *
 * @author Neil C Smith
 */
public class SimpleGraphNoAnnotations extends Graph {

    @Override
    protected void init() {

        Property sweep = new Property();
        addDependent(sweep);

        IIRFilter filter = new IIRFilter();

        Pipe noise = link(
                fn(d -> Math.random() * 2 - 1),
                filter.frequency(110).resonance(15),
                new Chorus().depth(1.4).feedback(0.4).rate(8),
                tee());

        Property env = new Property();
        addDependent(env);

        Osc osc = new Osc();

        Pipe syn = link(
                osc.waveform(Waveform.Square).gain(0.),
                tee());

        link(add(noise, syn), out(0));
        link(add(noise, syn), out(1));

        sweep.animator().whenDone(p -> p.to(8000, 65).in(4, 0.2).easeInOut());
        sweep.link(filter::frequency);

        String[] notes = {"a2", "g2", "d2", "a3", "c#3", "e3"};

        Clock clock = new Clock();
        addDependent(clock);

        clock.bpm(120).on()
                .filter(i -> i % 8 < 7)
                .mapTo(i -> notes[i % notes.length])
                .link(n -> {
                    env.set(0.8).to(0).in(1);
                    osc.frequency(noteToFrequency(n));
                });

        env.link(d -> osc.gain(d * d * d * d));

    }

    public static void main(String[] args) {
        GraphPlayer.create(new SimpleGraphNoAnnotations())
                //                .library("JACK")
                //                .ext(new ClientID("Simple Graph"))
                //                .ext(Connections.ALL)
                .build()
                .start();
    }

}
