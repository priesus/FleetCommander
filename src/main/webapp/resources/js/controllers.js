var fleetCommanderApp = angular.module('fleetCommanderApp', [ 'fleetCommanderServices', 'ngAnimate', 'ngCookies' ]);

fleetCommanderApp.controller('GamesCtrl', [
		'$scope',
		'$cookies',
		'GamesService',
		'PlayersService',
		'TurnsService',
		'PlanetsService',
		'ShipsService',
		function($scope, $cookies, GamesService, PlayersService, TurnsService, PlanetsService, ShipsService) {

			$scope.isIngame = false;
			$scope.showPlanetMenu = false;
			$scope.destinationSelectionActive = false;
			$scope.blockingActionInProgress = false;

			$scope.hasActiveGame = function() {
				return $cookies.gameId !== undefined;
			};

			$scope.startGame = function() {
				GamesService.create().success(function(data) {
					$scope.gameId = data.gameId;
					$scope.gameToken = data.authToken;
					PlayersService.addComputerPlayer($scope.gameId, $scope.gameToken).success(function() {
						GamesService.start($scope.gameId, $scope.gameToken).success(function() {
							$cookies.gameId = $scope.gameId;
							$cookies.gameToken = $scope.gameToken;
							$scope.resumeGame();
						})
					})
				});
			};

			$scope.resumeGame = function() {
				$scope.gameId = $cookies.gameId;
				$scope.gameToken = $cookies.gameToken;
				$scope.isIngame = true;
				$scope.reloadGame();
			};

			$scope.reloadGame = function() {
				GamesService.get($scope.gameId, $scope.gameToken).success(function(data) {
					$scope.runningGame = data;
				});
			};

			$scope.endTurn = function() {
				$scope.blockingActionInProgress = true;
				TurnsService.endTurn($scope.gameId, $scope.gameToken).success(function() {
					$scope.reloadGame();
					$scope.blockingActionInProgress = false;
					$scope.showPlanetMenu = false;
					$scope.destinationSelectionActive = false;
				}).error(function() {
					$scope.blockingActionInProgress = false;
				});
			};

			$scope.quitGame = function() {
				GamesService.quit($scope.gameId, $scope.gameToken);
				$scope.isIngame = false;
				delete $scope.runningGame;
				delete $cookies.gameId;
			};

			$scope.clickPlanetHandler = function(planet) {
				if (!$scope.destinationSelectionActive && planet.inhabitedByMe) {
					// Open planet menu
					$scope.selectedPlanet = planet;
					$scope.showPlanetMenu = true;
					$scope.setShipCount(1);

				} else if ($scope.destinationSelectionActive) {
					// Send ships from previously selected planet to this planet
					ShipsService
							.sendShips($scope.gameId, $scope.gameToken, $scope.shipCount, $scope.selectedPlanet.id, planet.id)
							.success(function() {
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
				PlanetsService.buildFactory($scope.gameId, $scope.gameToken, planet.id).success(function() {
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