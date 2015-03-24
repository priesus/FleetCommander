var fleetCommanderApp = angular.module('fleetCommanderApp', [ 'fleetCommanderServices', 'ngAnimate', 'ngCookies' ]);

fleetCommanderApp.controller('GamesCtrl', [ '$scope', '$cookies', 'GameService', 'TurnService', 'ShipService',
    function($scope, $cookies, GameService, TurnService, ShipService) {

	    $scope.isIngame = false;
	    $scope.showPlanetMenu = false;
	    $scope.destinationSelectionActive = false;
	    $scope.blockingActionInProgress = false;

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

	    $scope.endTurn = function() {
		    $scope.blockingActionInProgress = true;
		    TurnService.endTurn({
			    gameId : $scope.runningGame.id
		    }, function() {
			    $scope.reloadGame();
			    $scope.blockingActionInProgress = false;
		    }, function() {
			    $scope.blockingActionInProgress = false;
		    });
	    };

	    $scope.quitGame = function() {
		    $scope.runningGame.$delete();
		    $scope.isIngame = false;
		    delete $scope.runningGame;
		    delete $cookies.runningGameId;
	    };

	    $scope.clickPlanetHandler = function(planet) {
		    if (!$scope.destinationSelectionActive && planet.inhabited) {
			    // Open planet menu
			    $scope.selectedOriginPlanet = planet;
			    $scope.showPlanetMenu = true;
			    $scope.setShipCount(1);

		    } else if ($scope.destinationSelectionActive) {
			    // Send ships from previously selected planet to this planet
			    ShipService.sendShips({
			      gameId : $scope.runningGame.id,
			      shipCount : $scope.shipCount,
			      originPlanet : $scope.selectedOriginPlanet.id,
			      destinationPlanet : planet.id
			    }, function() {
				    $scope.reloadGame();
			    });
			    $scope.destinationSelectionActive = false;
		    }
	    };

	    $scope.setShipCount = function(ships) {
		    if (ships < 0)
			    ships = 0;
		    else if (ships > $scope.selectedOriginPlanet.shipCount)
			    ships = $scope.selectedOriginPlanet.shipCount
		    $scope.shipCount = ships;
	    };

	    $scope.shipDestinationSelection = function() {
		    $scope.showPlanetMenu = false;
		    $scope.destinationSelectionActive = true;
	    };

	    $scope.reloadGame = function() {
		    $scope.runningGame = GameService.get({
			    gameId : $cookies.runningGameId
		    });
	    };
    } ]);