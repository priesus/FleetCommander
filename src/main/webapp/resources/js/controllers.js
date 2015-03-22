var fleetCommanderApp = angular.module('fleetCommanderApp', [ 'fleetCommanderServices', 'ngAnimate', 'ngCookies' ]);

fleetCommanderApp.controller('GamesCtrl', [ '$scope', '$cookies', 'GameService',
    function($scope, $cookies, GameService) {

	    $scope.isIngame = false;
	    $scope.showPlanetMenu = false;

	    $scope.hasActiveGame = function() {
		    return $cookies.runningGameId !== undefined;
	    };

	    $scope.startGame = function() {
		    $scope.runningGame = GameService.start({}, function() {
			    $cookies.runningGameId = $scope.runningGame.id;
			    $scope.isIngame = true;
		    });
	    };

	    $scope.resumeGame = function() {
		    $scope.runningGame = GameService.get({
			    gameId : $cookies.runningGameId
		    }, function() {
			    $scope.isIngame = true;
		    });
	    };

	    $scope.quitGame = function() {
		    $scope.runningGame.$delete();
		    $scope.isIngame = false;
		    delete $scope.runningGame;
		    delete $cookies.runningGameId;
	    };

	    $scope.openPlanetMenu = function(planet) {
		    $scope.selectedPlanet = planet;
		    $scope.showPlanetMenu = true;
		    $scope.shipCount = 0;
	    };

	    $scope.setShipCount = function(ships) {
		    if (ships < 0)
			    ships = 0;
		    else if (ships > $scope.selectedPlanet.shipCount)
			    ships = $scope.selectedPlanet.shipCount
		    $scope.shipCount = ships;
	    };
    } ]);