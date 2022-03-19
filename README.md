# `<programming>` 2022

Hello! Welcome to the repository for my (Sam Ritchie's) talks from the 2022
European Lisp Symposium and `<programming>` conferences.

> If you find this work interesting, please consider sponsoring it via [Github
> Sponsors](https://github.com/sponsors/sritchie). Thank you!

Here you'll find:

- More information about [SICMUtils][SICMUTILS] and the projects I discussed in
  the talks
- Working versions of the demos
- The `org-re-reveal`-generated slides from the presentations.

## Links

- [SICMUtils repository][SICMUTILS]

- The Road to Reality Newsletter

- Road to Reality Discord

- [Clerk][CLERK], the notebook rendering engine used by SICMUtils

- [In-progress executable version](https://github.com/sicmutils/fdg-book) of
  _Functional Differential Geometry_ ([book link][FDG])

- [In-progress executable version](https://github.com/sicmutils/sicm-book) of
  Sussman and Wisdom's _Structure and Interpretation of Classical Mechanics_
  ([book link][SICM])

- [mathbox](https://gitgud.io/unconed/mathbox) by @unconed

- [mathbox-react](https://github.com/ChristopherChudzicki/mathbox-react) by
  @ChristopherChudzicki

## Demo Instructions

The demos include both Clojure and Clojurescript code. Two environments means
two build tools, so you'll need to have these installed:

- [clj](), for running the JVM side

- [shadow-cljs]() for building the JS bundle used by the demos. This is a lovely
  system that will automatically rebuild the bundle any time you save a cljs
  file.

  - You'll also need node.js installed, to install the initial `npm`
    dependencies.

When those are all set, generate the JS bundle for the demos by running the
following commands in one terminal window:

```bash
npm install
shadow-cljs watch sicm-browser
```

Then start a Clojure repl with `clj`, and execute the following commands to start Clerk, the literate programming viewer:

```clojure
;; point Clerk at our newly-generated JS bundle instead of its default:
(swap! clerk-config/!resource->url
       assoc
       "/js/viewer.js"
       "http://localhost:9000/out/main.js")

;; Start the clerk server.
(clerk/serve!
 {:browse? true :port 7777})
```

Now run `(clerk/show! <path/to/file.clj>)` to run any of the demos.

> NOTE: Clojure is far more pleasant if you can get a REPL running from inside
> of your favorite code editor. I'll update this repo with links to a good
> "Getting Started" resource; but please open an issue if you're having trouble
> and I'll get you sorted.

If you're running a REPL from inside your editor, see the [Clerk
homepage][CLERK] for instructions on how to trigger `clerk-show!` with a key
command, making dynamic interaction much more fun.

## Presentations

The presentations themselves live in `presentations/org/*.html`. see
`presentations/README.md` for more detail on how to:

- build these `html` files from their associated `org` files
- generate slides by executing Clojure code
- Serve these presentations in presenter mode

Enjoy!

[CLERK]: https://github.com/nextjournal/clerk
[SICMUTILS]: https://github.com/sicmutils/sicmutils
[SICM]: http://mitpress.mit.edu/books/structure-and-interpretation-classical-mechanics
[FDG]: http://mitpress.mit.edu/books/functional-differential-geometry
