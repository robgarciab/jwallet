'use strict';

angular.module('jwalletApp')
    .controller('BankAccountDetailController', function ($scope, $rootScope, $stateParams, entity, BankAccount) {
        $scope.bankAccount = entity;
        $scope.load = function (id) {
            BankAccount.get({id: id}, function(result) {
                $scope.bankAccount = result;
            });
        };
        var unsubscribe = $rootScope.$on('jwalletApp:bankAccountUpdate', function(event, result) {
            $scope.bankAccount = result;
        });
        $scope.$on('$destroy', unsubscribe);

    });
