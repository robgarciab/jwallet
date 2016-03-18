'use strict';

angular.module('jwalletApp')
	.controller('BankAccountDeleteController', function($scope, $uibModalInstance, entity, BankAccount) {

        $scope.bankAccount = entity;
        $scope.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
        $scope.confirmDelete = function (id) {
            BankAccount.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        };

    });
