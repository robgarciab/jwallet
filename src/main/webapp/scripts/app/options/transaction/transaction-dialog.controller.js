'use strict';

angular.module('jwalletApp').controller('TransactionDialogController',
    ['$scope', '$stateParams', '$uibModalInstance', 'entity', 'Transaction', 'BankAccount', 'Principal', 'uibButtonConfig',
        function($scope, $stateParams, $uibModalInstance, entity, Transaction, BankAccount, Principal, buttonConfig) {

    	buttonConfig.activeClass = 'btn-danger';
    	$scope.number = $stateParams.number;
        $scope.transaction = entity;
        $scope.action = 'jwalletApp.transaction.actions.extract';
        
        Principal.identity().then(function(account) {
            $scope.account = account;
            BankAccount.query({}, {login:$scope.account.login}, function(result, headers) {
            	$scope.bankAccounts = result;
            	if ($scope.number != null) {
            		for (var i = 0; i < $scope.bankAccounts.length; i++) {
            		    if ($scope.bankAccounts[i].number === $scope.number) {
            		    	$scope.bankAccount = $scope.bankAccounts[i];
            		    }
            		}
            	}
            });
        });
        
        $scope.load = function(id) {
            Transaction.get({id : id}, function(result) {
                $scope.transaction = result;
            });
        };

        var onSaveSuccess = function (result) {
            $scope.$emit('jwalletApp:transactionUpdate', result);
            $uibModalInstance.close(result);
            $scope.isSaving = false;
        };

        var onSaveError = function (result) {
            $scope.isSaving = false;
        };

        $scope.save = function () {
            $scope.isSaving = true;
            $scope.transaction.bankAccountId = $scope.bankAccount.id;
            
            if ($scope.action === 'jwalletApp.transaction.actions.extract') {
            	$scope.transaction.amount = $scope.transaction.amount * -1;
            }
            
            if ($scope.transaction.id != null) {
                Transaction.update($scope.transaction, onSaveSuccess, onSaveError);
            } else {
                Transaction.save($scope.transaction, onSaveSuccess, onSaveError);
            }
        };

        $scope.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
}]);
