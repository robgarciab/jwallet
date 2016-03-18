'use strict';

angular.module('jwalletApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('options', {
                abstract: true,
                parent: 'site'
            });
    });
