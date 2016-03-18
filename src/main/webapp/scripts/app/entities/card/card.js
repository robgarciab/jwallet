'use strict';

angular.module('jwalletApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('card', {
                parent: 'entity',
                url: '/cards',
                data: {
                    authorities: ['ROLE_USER'],
                    pageTitle: 'jwalletApp.card.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/card/cards.html',
                        controller: 'CardController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('card');
                        $translatePartialLoader.addPart('global');
                        return $translate.refresh();
                    }]
                }
            })
            .state('card.detail', {
                parent: 'entity',
                url: '/card/{id}',
                data: {
                    authorities: ['ROLE_USER'],
                    pageTitle: 'jwalletApp.card.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/card/card-detail.html',
                        controller: 'CardDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('card');
                        return $translate.refresh();
                    }],
                    entity: ['$stateParams', 'Card', function($stateParams, Card) {
                        return Card.get({id : $stateParams.id});
                    }]
                }
            })
            .state('card.new', {
                parent: 'card',
                url: '/new',
                data: {
                    authorities: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'scripts/app/entities/card/card-dialog.html',
                        controller: 'CardDialogController',
                        size: 'lg',
                        resolve: {
                            entity: function () {
                                return {
                                    number: null,
                                    openedDate: null,
                                    id: null
                                };
                            }
                        }
                    }).result.then(function(result) {
                        $state.go('card', null, { reload: true });
                    }, function() {
                        $state.go('card');
                    })
                }]
            })
            .state('card.edit', {
                parent: 'card',
                url: '/{id}/edit',
                data: {
                    authorities: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'scripts/app/entities/card/card-dialog.html',
                        controller: 'CardDialogController',
                        size: 'lg',
                        resolve: {
                            entity: ['Card', function(Card) {
                                return Card.get({id : $stateParams.id});
                            }]
                        }
                    }).result.then(function(result) {
                        $state.go('card', null, { reload: true });
                    }, function() {
                        $state.go('^');
                    })
                }]
            })
            .state('card.delete', {
                parent: 'card',
                url: '/{id}/delete',
                data: {
                    authorities: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'scripts/app/entities/card/card-delete-dialog.html',
                        controller: 'CardDeleteController',
                        size: 'md',
                        resolve: {
                            entity: ['Card', function(Card) {
                                return Card.get({id : $stateParams.id});
                            }]
                        }
                    }).result.then(function(result) {
                        $state.go('card', null, { reload: true });
                    }, function() {
                        $state.go('^');
                    })
                }]
            });
    });
