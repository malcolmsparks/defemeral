# Defemeral

Clojure programs often run services that are required to be unique. For
example, anything that listens on a network port cannot be duplicated,
since only one service can accept connections on a given port.

It is common during development to compile and subsequently recompile
namespaces, many times over. However, when re-initialising state that
represents services, it is necessary to stop services that might get
restarted. This is often done in an ad-hoc manner, and this small
library presents a pattern that can take away much of the manual service
teardown work that a developer might need to do.

By providing shutdown logic in ```defemeral``` declarations, you can
recompile the namespace safely, and if the service that the
```defemeral``` represents will be started (after stopping any existing
services if necessary).

## Usage

A call to ```defemeral``` causes an object to be registered that
satisfies the ```defemeral.defemeral/Ephemeral``` protocol, which
contains ```begin``` and ```end``` functions. The result of the
```begin``` function is given to the ```end``` function - this is
usually a value that represents the service and gives the ```end```
function the means to shutdown the service cleanly, closing any
resources involved.

    (defemeral my-service
      (begin [_]
        (println "Starting my service")
        :something)
      (end [_ something]
        (println "Stopped my service")))

## License

Copyright © 2013 Malcolm Sparks

Distributed under the Eclipse Public License, the same as Clojure.
