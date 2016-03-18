'use strict';

angular.module('jwalletApp').controller('BankAccountCloseController',
    ['$scope', '$uibModalInstance', 'entity', 'BankAccount', 'Principal',
        function($scope, $uibModalInstance, entity, BankAccount, Principal) {

        $scope.bankAccount = entity;
        
        Principal.identity().then(function(account) {
            $scope.account = account;
            BankAccount.query({}, {login:$scope.account.login}, function(result, headers) {
                result = result.filter(function(account) {
                	if (account.number == $scope.bankAccount.number) {
                		return false;
                	} else {
                		return true;
                	}
                });
            	$scope.accounts = result;
            	if ($scope.accounts.length > 0) {
            		$scope.target = $scope.accounts[0];
            	}
            });
        });
        
        $scope.load = function(id) {
            BankAccount.get({id : id}, function(result) {
                $scope.bankAccount = result;
            });
        };

        var onSaveSuccess = function (result) {
            $scope.$emit('jwalletApp:bankAccountUpdate', result);
            $uibModalInstance.close(result);
            $scope.isSaving = false;
        };

        var onSaveError = function (result) {
            $scope.isSaving = false;
        };

        $scope.save = function () {
            $scope.isSaving = true;
            BankAccount.close({ id:$scope.bankAccount.id }, $scope.target.id, onSaveSuccess, onSaveError);
        };

        $scope.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
}]);
