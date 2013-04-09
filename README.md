# lein-simpleton

A Leiningen plugin to serve files out of a local directory -- very similar to `python -m SimpleHTTPServer <port>`.

## Usage

### System-wide install

Put `[lein-simpleton "1.0.0"]` into the `:plugins` vector of your
`:user` profile, or if you are on Leiningen 1.x do `lein plugin install lein-simpleton 1.0.0`.

### Per-project install

Put `[lein-simpleton "1.0.0"]` into the `:plugins` vector of your project.clj.

### Running

By default Simpleton provides a file-server in the directory where it's run.  To run:

    $ lein simpleton <port>

Navigate to `localhost:<port>` and see a directory listing.  Click around to navigate directories and download (some) files.

#### Running the echo server

Simpleton can also run an echo server that reflects the incomming HTTP headers back as [EDN](https://github.com/edn-format/edn) data.

    $ lein simpleton <port> echo

Navigating to `localhost:<port>` to download an EDN file.

### Running the hello server

Simpleton can also run a "Hello" server that just returns a canned text string.

    $ lein simpleton <port> hello

Navigating to `localhost:<port>` to see the message.

## License

Copyright © 2013 Fogus

Distributed under the Eclipse Public License, the same as Clojure.
