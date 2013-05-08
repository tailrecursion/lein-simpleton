# lein-simpleton

A Leiningen plugin to serve files out of a local directory -- very similar to `python -m SimpleHTTPServer <port>`.

## Usage

### System-wide install

Put `[lein-simpleton "1.1.0"]` into the `:plugins` vector of your
`:user` profile, or if you are on Leiningen 1.x do `lein plugin install lein-simpleton 1.1.0`.

### Per-project install

Put `[lein-simpleton "1.1.0"]` into the `:plugins` vector of your project.clj.

### Running

By default Simpleton provides a file-server in the directory where it's run.  To run:

    $ lein simpleton 5000

Navigate to <http://localhost:5000> and see a directory listing.  Click around to navigate directories and download (some) files.  If a directory contains a file named either `index.html` or `index.htm` then Simpleton will attempt to serve that automatically.

#### Running the echo server

Simpleton can also run an echo server that reflects the incomming HTTP headers back as [EDN](https://github.com/edn-format/edn) data.

    $ lein simpleton 5000 echo

Navigate to <http://localhost:5000>` to download an EDN file.

### Running the hello server

Simpleton can also run a "Hello" server that just returns a canned text string.

    $ lein simpleton 5000 hello

Navigating to <http://localhost:5000> to see the message.

## Contributing

Patches and pushes welcomed.  Please see the [lein-simpleton tickets page](https://github.com/fogus/lein-simpleton/issues) to see if there's something that you like added, or add it, hack and push away.

## License

Copyright © 2013 Fogus

Distributed under the Eclipse Public License, the same as Clojure.
