var fleetCommanderApp = angular.module('fleetCommanderApp', [ 'fleetCommanderServices', 'ngAnimate', 'ngCookies' ]);

fleetCommanderApp.controller('GamesCtrl', [ '$scope', '$cookies', 'GameService',
    function($scope, $cookies, GameService) {

	    $scope.isIngame = false;

	    $scope.hasActiveGame = function() {
		    return $cookies.runningGameId !== undefined;
	    };

	    $scope.startGame = function() {
		    $scope.runningGame = GameService.start({}, function() {
			    $cookies.runningGameId = $scope.runningGame.id;
		    });
		    $scope.isIngame = true;
	    };

	    $scope.resumeGame = function() {
		    $scope.runningGame = GameService.get({
			    gameId : $cookies.runningGameId
		    });
		    $scope.isIngame = true;
	    };

	    $scope.quitGame = function() {
		    $scope.runningGame.$delete();
		    $scope.isIngame = false;
		    delete $scope.runningGame;
		    delete $cookies.runningGameId;
	    };
    } ]);