var fleetCommanderApp = angular.module('fleetCommanderApp', [ 'fleetCommanderServices', 'ngAnimate', 'ngCookies' ]);

fleetCommanderApp.controller('GamesCtrl', [
		'$scope',
		'$cookies',
		'GameService',
		'TurnService',
		'ShipService',
		function($scope, $cookies, GameService, TurnService, ShipService) {

			$scope.isIngame = false;
			$scope.showPlanetMenu = false;
			$scope.destinationSelectionActive = false;
			$scope.blockingActionInProgress = false;

			$scope.hasActiveGame = function() {
				return $cookies.runningGameId !== undefined;
			};

			$scope.startGame = function() {

				GameService.create().success(function(data) {
					$cookies.runningGameId = data.gameId;
					$cookies.runningGameToken = data.gameAuthToken;
					$scope.resumeGame();
				});
			};

			$scope.resumeGame = function() {
				$scope.runningGameId = $cookies.runningGameId;
				$scope.runningGameToken = $cookies.runningGameToken;
				$scope.isIngame = true;
				$scope.reloadGame();
			};

			$scope.reloadGame = function() {
				GameService.get($scope.runningGameId, $scope.runningGameToken).success(function(data) {
					$scope.runningGame = data;
				});
			};

			$scope.endTurn = function() {
				$scope.blockingActionInProgress = true;
				TurnService.endTurn($scope.runningGameId, $scope.runningGameToken).success(function() {
					$scope.reloadGame();
					$scope.blockingActionInProgress = false;
				}).error(function() {
					$scope.blockingActionInProgress = false;
				});
			};

			$scope.quitGame = function() {
				GameService.quit($scope.runningGameId, $scope.runningGameToken);
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
					ShipService.sendShips($scope.runningGameId, $scope.runningGameToken, $scope.shipCount,
							$scope.selectedOriginPlanet.id, planet.id).success(function() {
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
		} ]);