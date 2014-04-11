module.exports = function(grunt) {
    grunt.initConfig({
        copy: {
            site: {
                expand: true,
                cwd: 'output',
                src: '**',
                dest: 'deploy'
            },
            index: {
                files: [{
                    cwd: 'output/en',
                    src: 'index.html',
                    dest: 'deploy',
                    expand: true
                }]
            }
        },
        clean: ['deploy'],
        autoprefixer: {
            site: {
                expand: true,
                flatten: true,
                src: 'deploy/css/*.css',
                dest: 'deploy/css/'
            }
        },
        htmlcompressor: {
            site: {
                expand: true,
                cwd: 'deploy',
                src: '**/*.html',
                dest: 'deploy'
            }
        },
        imagemin: {
            site: {
                options: {
                  pngquant: true
                },
                files: [{
                  cwd: 'output/',
                  src: ['**/*.{png,gif,jpg}'],
                  dest: 'deploy/',
                  expand: true
                }]
            }
        },
        replace: {
            html: {
                src: ['deploy/**/*.html'],
                overwrite: true,
                replacements: [{
                    from: /build:css \/css\/([^ ]+)(.*href=")(.*)\/css\//,
                    to: 'build:css $3/css/$1$2$3/css/'
                }]
            }
        },
        useminPreparePrepare: {
            html: 'deploy/**/*.html',
            options: {
              src: 'deploy',
              dest: 'deploy'
            }
        },
        usemin: {
            html: 'deploy/**/*.html',
            options: {
              dest: 'deploy'
            }
        }
    });

    grunt.loadNpmTasks('grunt-contrib-copy');
    grunt.loadNpmTasks('grunt-contrib-concat');
    grunt.loadNpmTasks('grunt-contrib-clean');
    grunt.loadNpmTasks('grunt-contrib-cssmin');
    grunt.loadNpmTasks('grunt-contrib-imagemin');
    grunt.loadNpmTasks('grunt-autoprefixer');
    grunt.loadNpmTasks('grunt-htmlcompressor');
    grunt.loadNpmTasks('grunt-usemin');
    grunt.loadNpmTasks('grunt-text-replace');
    grunt.loadTasks('grunt-utils/tasks');

    grunt.registerTask('default', ['clean', 'copy', 'imagemin', 'replace', 'useminPreparePrepare', 'useminPrepare', 'usemin', 'autoprefixer', 'concat', 'cssmin', 'htmlcompressor']);
};
