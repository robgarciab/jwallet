'use strict';

angular.module('jwalletApp')
    .controller('BankAccountController', function ($scope, $state, BankAccount, ParseLinks, Principal) {

        $scope.bankAccounts = [];
        $scope.predicate = 'id';
        $scope.reverse = true;
        $scope.page = 1;
        $scope.loadAll = function() {
        	Principal.identity().then(function(account) {
                $scope.account = account;
                BankAccount.query({page: $scope.page - 1, size: 20, sort: [$scope.predicate + ',' + ($scope.reverse ? 'asc' : 'desc'), 'id']}, {login:$scope.account.login}, function(result, headers) {
                	$scope.links = ParseLinks.parse(headers('link'));
                    $scope.totalItems = headers('X-Total-Count');
                	$scope.bankAccounts = result;
                });
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
            $scope.bankAccount = {
                balance: null,
                id: null
            };
        };
    });
