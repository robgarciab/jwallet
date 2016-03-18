'use strict';

angular.module('jwalletApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('transaction', {
                parent: 'options',
                url: '/{number}/transactions',
                data: {
                    authorities: ['ROLE_USER'],
                    pageTitle: 'jwalletApp.transaction.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/options/transaction/transactions.html',
                        controller: 'TransactionController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('transaction');
                        $translatePartialLoader.addPart('global');
                        return $translate.refresh();
                    }]
                }
            })
            .state('transaction.detail', {
                parent: 'options',
                url: '/transaction/{id}',
                data: {
                    authorities: ['ROLE_USER'],
                    pageTitle: 'jwalletApp.transaction.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/options/transaction/transaction-detail.html',
                        controller: 'TransactionDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('transaction');
                        return $translate.refresh();
                    }],
                    entity: ['$stateParams', 'Transaction', function($stateParams, Transaction) {
                        return Transaction.get({id : $stateParams.id});
                    }]
                }
            })
            .state('transaction.new', {
                parent: 'transaction',
                url: '/new',
                data: {
                    authorities: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'scripts/app/options/transaction/transaction-dialog.html',
                        controller: 'TransactionDialogController',
                        size: 'lg',
                        resolve: {
                            entity: function () {
                                return {
                                    amount: null,
                                    id: null
                                };
                            }
                        }
                    }).result.then(function(result) {
                        $state.go('transaction', null, { reload: true });
                    }, function() {
                        $state.go('transaction');
                    })
                }]
            })
            .state('transaction.edit', {
                parent: 'transaction',
                url: '/{id}/edit',
                data: {
                    authorities: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'scripts/app/options/transaction/transaction-dialog.html',
                        controller: 'TransactionDialogController',
                        size: 'lg',
                        resolve: {
                            entity: ['Transaction', function(Transaction) {
                                return Transaction.get({id : $stateParams.id});
                            }]
                        }
                    }).result.then(function(result) {
                        $state.go('transaction', null, { reload: true });
                    }, function() {
                        $state.go('^');
                    })
                }]
            })
            .state('transaction.delete', {
                parent: 'transaction',
                url: '/{id}/delete',
                data: {
                    authorities: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'scripts/app/options/transaction/transaction-delete-dialog.html',
                        controller: 'TransactionDeleteController',
                        size: 'md',
                        resolve: {
                            entity: ['Transaction', function(Transaction) {
                                return Transaction.get({id : $stateParams.id});
                            }]
                        }
                    }).result.then(function(result) {
                        $state.go('transaction', null, { reload: true });
                    }, function() {
                        $state.go('^');
                    })
                }]
            });
    });
