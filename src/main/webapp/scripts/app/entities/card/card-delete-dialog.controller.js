'use strict';

angular.module('jwalletApp')
	.controller('CardDeleteController', function($scope, $uibModalInstance, entity, Card) {

        $scope.card = entity;
        $scope.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
        $scope.confirmDelete = function (id) {
            Card.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        };

    });
