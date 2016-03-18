'use strict';

angular.module('jwalletApp').controller('TransactionDialogController',
    ['$scope', '$stateParams', '$uibModalInstance', 'entity', 'Transaction',
        function($scope, $stateParams, $uibModalInstance, entity, Transaction) {

        $scope.transaction = entity;
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
