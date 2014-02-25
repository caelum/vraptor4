module.exports = function(grunt) {
    grunt.initConfig({
        copy: {
            site: {
                expand: true,
                cwd: 'output',
                src: '**',
                dest: 'deploy'
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
        useminPrepare: {
            html: {
                src: ['deploy/**/*.html'],
                options: {
                  dest: 'deploy'
                }
            }
        },
        usemin: {
            html: {
                src: ['deploy/**/*.html'],
                options: {
                  dest: 'deploy'
                }
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

    grunt.registerTask('default', ['clean', 'copy', 'imagemin', 'useminPrepare', 'usemin', 'autoprefixer', 'concat', 'cssmin', 'htmlcompressor']);
};
