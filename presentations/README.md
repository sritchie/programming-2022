# Presentations

Welcome!

These presentations were developed using Emacs [org-mode](https://orgmode.org/)
and the [org-re-reveal](https://gitlab.com/oer/org-re-reveal) package to
generate [reveal.js](https://revealjs.com) presentations from the `org` files.

All of the actual presentation code lives in `org`, just to keep things clean.

Shared presentation options live in `org/reveal_header.org`.

The talk files are

- `org/lisp_as_renaissance.org`
- `org/building_sicmutils.org`
- `org/road_to_reality.org`

And the exported HTML files have matching names with `html` headers.

## Viewing the Presentations

To view any of the talks, simply open any of the `org/*.html` files.

Some available options:

- Hit `o` for an overview of all slides
- `option`-clicking on a slide will toggle "zoom" mode, for focusing in on some
  element
- `C-shift-f` will turn on a search dialogue, and `enter` repeatedly will scan
  through search results
- `f` activates full screen mode
- `v` will pause the presentation and get the screen to black.

## Print as PDF

If you would like a PDF of any of the talks:

- open any of the `*/.html`  files with `?print-pdf` appended
- Print as PDF from your browser, following the [instructions
  here](https://revealjs.com/pdf-export/) to tune the print dialogue correctly.

## Developing

If you want presenter notes available, you'll need to launch the included
webserver.

- From the `presentations`  directory, run

```
npm install
npm run serve
```

Then open http://localhost:8000 and navigate to the `org` folder. Click on any
of the `html` files to view a presentation.

Now, `s` will open a presenter notes view.

### Exporting HTML from Org

To generate HTML you'll need `org-re-reveal` installed.

If you're using Spacemacs, see [this Spacemacs doc
page](https://develop.spacemacs.org/layers/+emacs/org/README.html#revealjs-support)
for instructions on how to activate `org-re-reveal`. Here is my [spacemacs
config](https://github.com/sritchie/spacemacs.d) with instructions on Spacemacs
installation, if you're curious.

Otherwise, follow the instructions at
[org-re-reveal](https://gitlab.com/oer/org-re-reveal) to get the system set up.

To generate HTML:

- In any of the `org/*.org*` foles, run `C-c C-e` to view the export menu. `v`
  will activate the `org-re-reveal` submenu, and `v` will generate html.

I usually just generate html with `C-c C-e v v` and let the webserver from above
take care of auto-reloading. If you like, `C-c C-e v b` will open up your
browser to the new HTML file once it's generated.

### Evaluating Code

NOTE that the keybindings listed here are Spacemacs-specific.

To evaluate code in the talks:

- Navigate to `reveal_header.org`
- start a Clojure repl with `cider-jack-in-clj`
- `C-c C-c` on the Clojure form to establish
  the namespace.
- _also_ hit `C-c '` on the code block to open it, and run:
  - `C-c C-z` to switch to the repl
  - `C-M-x` on all code forms in the code block to send them to the REPL
  - `C-c M-n n` to change the REPL namespace.

Now you should be able to:

- evaluate code blocks in the org files by hitting `C-c C-c`, OR
- hit `C-c '` on any code block to edit it in a buffer with Clojure mode, and
  interact with the REPL that way (in the same process as the org evaluations is
  using.)
