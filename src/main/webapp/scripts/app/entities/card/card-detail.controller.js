'use strict';

angular.module('jwalletApp')
    .controller('CardDetailController', function ($scope, $rootScope, $stateParams, entity, Card) {
        $scope.card = entity;
        $scope.load = function (id) {
            Card.get({id: id}, function(result) {
                $scope.card = result;
            });
        };
        var unsubscribe = $rootScope.$on('jwalletApp:cardUpdate', function(event, result) {
            $scope.card = result;
        });
        $scope.$on('$destroy', unsubscribe);

    });
