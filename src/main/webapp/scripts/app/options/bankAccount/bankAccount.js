'use strict';

angular.module('jwalletApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('bankAccount', {
                parent: 'options',
                url: '/myBankAccounts',
                data: {
                    authorities: ['ROLE_USER'],
                    pageTitle: 'jwalletApp.bankAccount.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/options/bankAccount/bankAccounts.html',
                        controller: 'BankAccountController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('bankAccount');
                        $translatePartialLoader.addPart('global');
                        return $translate.refresh();
                    }]
                }
            })
            .state('bankAccount.detail', {
                parent: 'options',
                url: '/bankAccount/{id}',
                data: {
                    authorities: ['ROLE_USER'],
                    pageTitle: 'jwalletApp.bankAccount.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/options/bankAccount/bankAccount-detail.html',
                        controller: 'BankAccountDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('bankAccount');
                        return $translate.refresh();
                    }],
                    entity: ['$stateParams', 'BankAccount', function($stateParams, BankAccount) {
                        return BankAccount.get({id : $stateParams.id});
                    }]
                }
            })
            .state('bankAccount.new', {
                parent: 'bankAccount',
                url: '/new',
                data: {
                    authorities: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'scripts/app/options/bankAccount/bankAccount-dialog.html',
                        controller: 'BankAccountDialogController',
                        size: 'lg',
                        resolve: {
                            entity: function () {
                                return {
                                    balance: null,
                                    id: null
                                };
                            }
                        }
                    }).result.then(function(result) {
                        $state.go('bankAccount', null, { reload: true });
                    }, function() {
                        $state.go('bankAccount');
                    })
                }]
            })
            .state('bankAccount.edit', {
                parent: 'bankAccount',
                url: '/{id}/edit',
                data: {
                    authorities: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'scripts/app/options/bankAccount/bankAccount-dialog.html',
                        controller: 'BankAccountDialogController',
                        size: 'lg',
                        resolve: {
                            entity: ['BankAccount', function(BankAccount) {
                                return BankAccount.get({id : $stateParams.id});
                            }]
                        }
                    }).result.then(function(result) {
                        $state.go('bankAccount', null, { reload: true });
                    }, function() {
                        $state.go('^');
                    })
                }]
            })
            .state('bankAccount.close', {
                parent: 'bankAccount',
                url: '/{id}/close',
                data: {
                    authorities: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'scripts/app/options/bankAccount/bankAccount-close.html',
                        controller: 'BankAccountCloseController',
                        size: 'lg',
                        resolve: {
                            entity: ['BankAccount', function(BankAccount) {
                                return BankAccount.get({id : $stateParams.id});
                            }]
                        }
                    }).result.then(function(result) {
                        $state.go('bankAccount', null, { reload: true });
                    }, function() {
                        $state.go('^');
                    })
                }]
            });
    });
