var fleetCommanderApp = angular.module('fleetCommanderApp', [ 'fleetCommanderServices', 'ngAnimate', 'ngCookies' ]);

fleetCommanderApp.controller('GamesCtrl', [
		'$scope',
		'$cookies',
		'GameService',
		'TurnService',
		'PlanetService',
		'ShipService',
		function($scope, $cookies, GameService, TurnService, PlanetService, ShipService) {

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
					$scope.showPlanetMenu = false;
					$scope.destinationSelectionActive = false;
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
					$scope.selectedPlanet = planet;
					$scope.showPlanetMenu = true;
					$scope.setShipCount(1);

				} else if ($scope.destinationSelectionActive) {
					// Send ships from previously selected planet to this planet
					ShipService.sendShips($scope.runningGameId, $scope.runningGameToken, $scope.shipCount,
							$scope.selectedPlanet.id, planet.id).success(function() {
						$scope.reloadGame();
					});
					$scope.destinationSelectionActive = false;
				}
			};

			$scope.setShipCount = function(ships) {
				if (ships < 1)
					ships = 1;
				else if (ships > $scope.selectedPlanet.shipCount)
					ships = $scope.selectedPlanet.shipCount
				$scope.shipCount = ships;
			};

			$scope.buildFactory = function(planet) {
				PlanetService.buildFactory($scope.runningGameId, $scope.runningGameToken, planet.id).success(function() {
					$scope.selectedPlanet.factorySite.factoryCount++;
					$scope.selectedPlanet.factorySite.availableSlots--;
					$scope.reloadGame();
				});
			};

			$scope.shipDestinationSelection = function() {
				$scope.showPlanetMenu = false;
				$scope.destinationSelectionActive = true;
			};

			$scope.getNumber = function(num) {
				return new Array(num);
			}
		} ]);