const connect = require("gulp-connect");
const gulp = require("gulp");
const yargs = require("yargs");

const root = yargs.argv.root || ".";
const port = yargs.argv.port || 8000;
const host = yargs.argv.host || "localhost";

gulp.task("reload", () =>
  gulp.src(["org/*.html", "*.md"]).pipe(connect.reload())
);

gulp.task("serve", async () => {
  connect.server({
    root: root,
    port: port,
    host: host,
    livereload: true,
  });

  gulp.watch(["org/*.html", "*.md"], gulp.series("reload"));
});
