var fleetCommanderApp = angular.module('fleetCommanderApp', [ 'fleetCommanderServices', 'ngAnimate', 'ngCookies',
		'ngTouch' ]);

fleetCommanderApp.controller('GamesCtrl', [
		'$scope',
		'$cookies',
		'GamesService',
		'PlayersService',
		'TurnsService',
		'PlanetsService',
		'ShipsService',
		function($scope, $cookies, GamesService, PlayersService, TurnsService, PlanetsService, ShipsService) {

			$scope.gameScreen = 'home';
			$scope.showPlanetMenu = false;
			$scope.showTurnEvents = false;
			$scope.destinationSelectionActive = false;
			$scope.blockingActionInProgress = false;

			$scope.hasActiveGame = function() {
				return $cookies.gameId !== undefined;
			};

			// TODO refactor to make this more readable
			$scope.onKeyDown = function($event) {
				if ($scope.gameScreen === 'home') {
					switch ($event.keyCode) {
					case 83: // [S]tart new game
						$scope.createNewGame();
						break;
					case 82: // [R]esume game
						$scope.resumeGame();
						break;
					case 74: // [J]oin game
						$scope.gameScreen = 'join';
						break;
					}
				} else if ($scope.gameScreen === 'players') {
					switch ($event.keyCode) {
					case 80: // [P]lay
						$scope.startGame();
						break;
					case 65: // [A]dd computer player
						$scope.addComputerPlayer();
						break;
					case 13: // [Enter]
					case 32: // [Space]
						$scope.startGame();
						break;
					case 27: // [Esc]
						$scope.quitGame();
						break;
					}
				} else if ($scope.gameScreen === 'join') {
					switch ($event.keyCode) {
					case 13: // [Enter]
						$scope.tryToJoinGame();
						break;
					case 27: // [Esc]
						$scope.gameScreen = 'home';
						break;
					}
				} else if ($scope.gameScreen === 'ingame') {
					switch ($event.keyCode) {
					case 83: // [s]end ships
						$scope.prepareSendShips();
						break;
					case 65: // [a]ll ships
						$scope.setShipCount($scope.selectedPlanet.shipCount);
						break;
					case 69: // [E]nd turn
						$scope.endTurn();
						break;
					case 70: // Build [f]actory
						$scope.buildFactoryOnSelectedPlanet();
						break;
					case 13: // [Enter]
					case 32: // [Space]
						$scope.showTurnEvents = false;
						break;
					case 27: // [Esc]
						$scope.showTurnEvents = false;
						$scope.showPlanetMenu = false;
						break;
					}
				}
			};

			$scope.createNewGame = function() {
				GamesService.create().success(function(data) {
					$scope.gameId = data.gameId;
					$scope.gameToken = data.authToken;
					$scope.gameScreen = 'players';
					$scope.addComputerPlayer();
				});
			};

			$scope.tryToJoinGame = function() {
				if ($scope.joiningPlayerCode === undefined || $scope.joiningPlayerCode.length != 6)
					return;

				GamesService.join($scope.joiningPlayerCode).success(function(data) {
					$scope.gameId = data.gameId;
					$scope.gameToken = data.authToken;
					$scope.gameScreen = 'players';
				}).error(function(data, status, statusText) {
					$scope.joinGameError = data;
				});
			};

			$scope.addComputerPlayer = function() {
				PlayersService.addComputerPlayer($scope.gameId, $scope.gameToken).success(function() {
					$scope.reloadGame();
				});
			};

			$scope.startGame = function() {
				GamesService.start($scope.gameId, $scope.gameToken).success(function() {
					$cookies.gameId = $scope.gameId;
					$cookies.gameToken = $scope.gameToken;

					$scope.showPlanetMenu = false;
					$scope.showTurnEvents = false;
					$scope.destinationSelectionActive = false;
					$scope.blockingActionInProgress = false;

					$scope.resumeGame();
				});
			};

			$scope.resumeGame = function() {
				$scope.gameId = $cookies.gameId;
				$scope.gameToken = $cookies.gameToken;
				$scope.reloadGame().success(function() {
					$scope.gameScreen = 'ingame';
				});
			};

			$scope.reloadGame = function() {
				return GamesService.get($scope.gameId, $scope.gameToken).success(function(data) {
					$scope.game = data;
				});
			};

			$scope.endTurn = function() {
				if ($scope.blockingActionInProgress || $scope.game.status === 'OVER')
					return;

				$scope.blockingActionInProgress = true;
				TurnsService.endTurn($scope.gameId, $scope.gameToken).success(function() {
					$scope.reloadGame();
					$scope.blockingActionInProgress = false;
					$scope.showPlanetMenu = false;
					$scope.showTurnEvents = true;
					$scope.destinationSelectionActive = false;
				}).error(function() {
					$scope.blockingActionInProgress = false;
				});
			};

			$scope.quitGame = function() {
				GamesService.quit($scope.gameId, $scope.gameToken);
				$scope.showTurnEvents = false;
				$scope.gameScreen = 'home';
				delete $scope.game;
				delete $cookies.gameId;
			};

			$scope.clickPlanetHandler = function(planet) {
				if ($scope.game.status === 'OVER')
					return;
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
				if (ships > $scope.selectedPlanet.shipCount)
					ships = $scope.selectedPlanet.shipCount
				$scope.shipCount = ships;
			};

			$scope.buildFactoryOnSelectedPlanet = function() {
				PlanetsService.buildFactory($scope.gameId, $scope.gameToken, $scope.selectedPlanet.id).success(function() {
					$scope.selectedPlanet.factorySite.factoryCount++;
					$scope.selectedPlanet.factorySite.availableSlots--;
					$scope.reloadGame();
				});
			};

			$scope.prepareSendShips = function() {
				if ($scope.shipCount > 0) {
					$scope.showPlanetMenu = false;
					$scope.destinationSelectionActive = true;
				}
			};

			$scope.getNumber = function(num) {
				return new Array(num);
			}
		} ]);