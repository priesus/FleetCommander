var fleetCommanderApp = angular.module('fleetCommanderApp', [ 'fleetCommanderServices', 'ngAnimate', 'ngCookies' ]);

fleetCommanderApp.controller('GamesCtrl', [ '$scope', '$cookies', 'Universe', function($scope, $cookies, Universe) {

	var savedGameUrl = $cookies.runningGameUrl;

	if (savedGameUrl !== undefined) {
		$scope.runningGame = {
			'url' : savedGameUrl,
		};
	}
	$scope.isIngame = false;

	$scope.hasActiveGame = function() {
		return $scope.runningGame !== undefined;
	};

	$scope.startGame = function() {
		$scope.runningGame = {};
		$scope.runningGame.universe = Universe.get();
		$scope.isIngame = true;

		$cookies.runningGameUrl = 'game/100/';
	};

	$scope.resumeGame = function() {
		$scope.runningGame.universe = Universe.get();
		$scope.isIngame = true;
	};

	$scope.quitGame = function() {
		$scope.runningGame = undefined;
		$scope.isIngame = false;

		delete $cookies.runningGameUrl;
	};
} ]);