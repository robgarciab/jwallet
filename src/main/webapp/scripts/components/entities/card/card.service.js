'use strict';

angular.module('jwalletApp')
    .factory('Card', function ($resource, DateUtils) {
        return $resource('api/cards/:id', {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    data.openedDate = DateUtils.convertLocaleDateFromServer(data.openedDate);
                    return data;
                }
            },
            'update': {
                method: 'PUT',
                transformRequest: function (data) {
                    data.openedDate = DateUtils.convertLocaleDateToServer(data.openedDate);
                    return angular.toJson(data);
                }
            },
            'save': {
                method: 'POST',
                transformRequest: function (data) {
                    data.openedDate = DateUtils.convertLocaleDateToServer(data.openedDate);
                    return angular.toJson(data);
                }
            }
        });
    });
