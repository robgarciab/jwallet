'use strict';

angular.module('jwalletApp')
    .controller('TransactionDetailController', function ($scope, $rootScope, $stateParams, entity, Transaction) {
        $scope.transaction = entity;
        $scope.load = function (id) {
            Transaction.get({id: id}, function(result) {
                $scope.transaction = result;
            });
        };
        var unsubscribe = $rootScope.$on('jwalletApp:transactionUpdate', function(event, result) {
            $scope.transaction = result;
        });
        $scope.$on('$destroy', unsubscribe);

    });
