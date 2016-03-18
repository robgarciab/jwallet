'use strict';

angular.module('jwalletApp')
    .factory('BankAccount', function ($resource, DateUtils) {
        return $resource('api/:login/bankAccounts/:id', {login: '@login'}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    return data;
                }
            },
            'update': { method:'PUT' },
            'close': {
            	method:'PUT',
            	url: 'api/bankAccounts/:id/close'
            }
        });
    });
