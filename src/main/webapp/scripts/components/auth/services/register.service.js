'use strict';

angular.module('jwalletApp')
    .factory('Register', function ($resource) {
        return $resource('api/register', {}, {
        });
    });


