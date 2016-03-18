 'use strict';

angular.module('jwalletApp')
    .factory('notificationInterceptor', function ($q, AlertService) {
        return {
            response: function(response) {
                var alertKey = response.headers('X-jwalletApp-alert');
                if (angular.isString(alertKey)) {
                    AlertService.success(alertKey, { param : response.headers('X-jwalletApp-params')});
                }
                return response;
            }
        };
    });
