# lein-simpleton

A Leiningen plugin to serve files out of a local directory -- very similar to `python -m SimpleHTTPServer <port>`.

## Usage

### System-wide install

Put `[lein-simpleton "1.2.0"]` into the `:plugins` vector of your
`:user` profile, or if you are on Leiningen 1.x do `lein plugin install lein-simpleton 1.2.0`.

### Per-project install

Put `[lein-simpleton "1.2.0"]` into the `:plugins` vector of your project.clj.

### Running

By default Simpleton provides a file-server in the directory where it's run.  To run:

    $ lein simpleton 5000

Navigate to <http://localhost:5000> and see a directory listing.  Click around to navigate directories and download (some) files.  If a directory contains a file named either `index.html` or `index.htm` then Simpleton will attempt to serve that automatically.

#### `:from`

If you need to run Simpleton to serve files from a specific directory, then you can run something like the following:

    lein simpleton 5000 file :from c:\Windows

This is especially useful if you would like to serve a specific sub-directory in a Leiningen-managed project:

    lein simpleton 5000 file :from src

By default, the `lein simpleton 5000` will always serve a Leiningen project's root without using the `:from` declaration above.

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

License
-------

Copyright (C) 2013 Fogus and contributors.

Distributed under the Eclipse Public License, the same as Clojure.
