"use strict";
var path = require("path");

function groupPaths(/* [""] */ paths) {
    paths.sort();

    var result = [];
    var curGroup = null;

    paths.forEach(function(p) {
        var dir = path.dirname(p);
        if (!curGroup || curGroup.dir !== dir) {
            // if group has changed, push curGroup to result then initialize
            // new group.
            if (curGroup && curGroup.dir !== dir) {
                result.push(curGroup);
            }

            // Initialize new group.
            curGroup = {};
            curGroup.dir = dir;
            curGroup.paths = [];
        }

        curGroup.paths.push(p);
    });

    // Don't forget, the last group is not pushed.
    if (curGroup) {
        result.push(curGroup);
    }

    return result;
}

/**
 * This task is a workaround for a usemin issue documented here:
 * https://github.com/yeoman/grunt-usemin/issues/266
 * Once this issue is resolved, this whole task should be removed. Its config
 * is intended to be exactly the same as useminPrepare, except you need to
 * specify an additional options property, src, which specifies where your src
 * directory is supposed to be. This task will create useminPrepare config that
 * basically specifies dest and staging paths to be relative to where HTML files
 * are found as a way to work around the above issue.
 */
module.exports = function(grunt) {
    grunt.registerMultiTask("useminPreparePrepare",
            "Prepares useminPrepare config.",
            function() {
        var src = this.target === "html" ? this.data: this.data.src;
        var options = this.options();
        var srcDir = options.src || "src";
        var destDir = options.dest || srcDir;
        var stagingDir = options.staging || ".tmp";

        var useminPrepareConfig = {};
        var pathGroups = groupPaths(grunt.file.expand(src));

        function stripPrefixDir(/* "" */ path, /* "" */ prefix) {
            var result = "";

            if (grunt.file.doesPathContain(prefix, path)) {
                result = path.substring(prefix.length);
                if (result.indexOf(path.sep) == 0) {
                    result = result.substring(1);
                }
            }

            return result;
        }

        pathGroups.forEach(function(g) {
            var target = "target-" + g.dir;
            useminPrepareConfig[target] = {src: g.paths};
            useminPrepareConfig[target].options = {};

            var strippedDir = stripPrefixDir(g.dir, srcDir);
            useminPrepareConfig[target].options.staging = path.join(stagingDir, strippedDir);
            useminPrepareConfig[target].options.dest = path.join(destDir, strippedDir);
        });

        useminPrepareConfig.options = options;
        delete useminPrepareConfig.options.src;

        grunt.config("useminPrepare", useminPrepareConfig);

        grunt.log.writeln("useminPrepare config is now:");
        grunt.log.writeln(JSON.stringify(useminPrepareConfig, null, 4));
    });
}
