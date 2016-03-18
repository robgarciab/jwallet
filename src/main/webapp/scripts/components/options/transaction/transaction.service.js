'use strict';

angular.module('jwalletApp')
    .factory('Transaction', function ($resource, DateUtils) {
        return $resource('api/:number/transactions/:id', {number: '@number'}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    });
