'use strict';

angular.module('jwalletApp').controller('CardDialogController',
    ['$scope', '$stateParams', '$uibModalInstance', 'entity', 'Card',
        function($scope, $stateParams, $uibModalInstance, entity, Card) {

        $scope.card = entity;
        $scope.load = function(id) {
            Card.get({id : id}, function(result) {
                $scope.card = result;
            });
        };

        var onSaveSuccess = function (result) {
            $scope.$emit('jwalletApp:cardUpdate', result);
            $uibModalInstance.close(result);
            $scope.isSaving = false;
        };

        var onSaveError = function (result) {
            $scope.isSaving = false;
        };

        $scope.save = function () {
            $scope.isSaving = true;
            if ($scope.card.id != null) {
                Card.update($scope.card, onSaveSuccess, onSaveError);
            } else {
                Card.save($scope.card, onSaveSuccess, onSaveError);
            }
        };

        $scope.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
        $scope.datePickerForOpenedDate = {};

        $scope.datePickerForOpenedDate.status = {
            opened: false
        };

        $scope.datePickerForOpenedDateOpen = function($event) {
            $scope.datePickerForOpenedDate.status.opened = true;
        };
}]);
