'use strict';

angular.module('jwalletApp')
    .controller('TransactionController', function ($scope, $state, $stateParams, Transaction, ParseLinks) {

        $scope.transactions = [];
        $scope.predicate = 'id';
        $scope.reverse = true;
        $scope.page = 1;
        $scope.bankAccountNumber = $stateParams.number;
        if ($scope.bankAccountNumber == null || $scope.bankAccountNumber === "") {
        	$scope.title = 'jwalletApp.transaction.home.allTitle';
        } else {
        	$scope.title = 'jwalletApp.transaction.home.title';
        }
        $scope.loadAll = function() {
            Transaction.query({page: $scope.page - 1, size: 20, sort: [$scope.predicate + ',' + ($scope.reverse ? 'asc' : 'desc'), 'id']}, {number:$stateParams.number}, function(result, headers) {
                $scope.links = ParseLinks.parse(headers('link'));
                $scope.totalItems = headers('X-Total-Count');
                $scope.transactions = result;
            });
        };
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll();
        };
        $scope.loadAll();


        $scope.refresh = function () {
            $scope.loadAll();
            $scope.clear();
        };

        $scope.clear = function () {
            $scope.transaction = {
                amount: null,
                id: null
            };
        };
    });
