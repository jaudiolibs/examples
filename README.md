JAudioLibs Examples
===================

Example code for the various JAudioLibs projects.

## Pipes

- `SimpleGraph` : a basic example of some of the Pipes Graph features.
- `SimpleGraphNoAnnotations` : as above but without use of PraxisLIVE compatible
annotations for unit and dependency creation.

## AudioServers

- `SineAudioClient` : a simple sine wave producing audio client demonstrating
usage of the AudioServer API.
- `PassThroughAudioClient` : a simple example passing audio from input to output
without processing using the AudioServer API.
- `DeviceIteration` : an example of looking up available audio server providers
and their devices.

## JNAJack

- `JackTransport` : direct use of JACK transport via JNAJack.
- `MidiThru` : simple example of JACK MIDI support passing data through unchanged.