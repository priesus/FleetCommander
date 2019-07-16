var fleetCommanderApp = angular.module('fleetCommanderApp', ['ngRoute', 'fleetCommanderControllers']);

fleetCommanderApp.config(['$routeProvider',
	function ($routeProvider) {
		$routeProvider
			.when('/', {
				templateUrl: 'pages/home.html',
				controller: 'HomeCtrl'
			})
			.when('/create', {
				templateUrl: 'pages/players.html',
				controller: 'CreateCtrl'
			})
			.when('/players', {
				templateUrl: 'pages/players.html',
				controller: 'PlayersCtrl'
			})
			.when('/join', {
				templateUrl: 'pages/join.html',
				controller: 'JoinCtrl'
			})
			.when('/games/:gameId', {
				templateUrl: 'pages/ingame.html',
				controller: 'IngameCtrl'
			})
			.otherwise({
				redirectTo: '/'
			});
	}]);