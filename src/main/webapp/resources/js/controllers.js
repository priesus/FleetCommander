var fleetCommanderApp = angular.module('fleetCommanderApp', [ 'fleetCommanderServices', 'ngAnimate', 'ngCookies',
		'ngTouch' ]);

fleetCommanderApp.controller('GamesCtrl', [
		'$scope',
		'$cookies',
		'$interval',
		'GamesService',
		'JoinCodesService',
		'PlayersService',
		'TurnsService',
		'PlanetsService',
		'ShipsService',
		function($scope, $cookies, $interval, GamesService, JoinCodesService, PlayersService, TurnsService, PlanetsService,
				ShipsService) {

			$scope.gameScreen = 'home';
			$scope.showPlanetMenu = false;
			$scope.showTurnEvents = false;
			$scope.destinationSelectionActive = false;
			$scope.blockingActionInProgress = false;
			var gameStartPoller;
			var newTurnPoller;
			var currentTurnNumber;

			$scope.hasActiveGame = function() {
				return $cookies.gameId !== undefined;
			};

			$scope.createNewGame = function() {
				GamesService.create().success(function(data) {
					$scope.gameId = data.gameId;
					$scope.gameToken = data.fullAuthToken;
					$scope.gameScreen = 'players';
					$scope.requestJoinCode();
				});
			};

			$scope.openJoinGameMenu = function() {
				$scope.joinGameError = undefined;
				$scope.joiningPlayerCode = '';
				$scope.gameScreen = 'join';
			}

			$scope.requestJoinCode = function() {
				JoinCodesService.create($scope.gameId, $scope.gameToken).success(function() {
					$scope.refreshActiveJoinCodes();
				});
			}

			$scope.refreshActiveJoinCodes = function() {
				JoinCodesService.getAllActive($scope.gameId, $scope.gameToken).success(function(data) {
					$scope.activeJoinCodes = data.joinCodes;
				});
				$scope.refreshGame();
			}

			$scope.tryToJoinGame = function() {
				if ($scope.joiningPlayerCode === undefined || $scope.joiningPlayerCode.length != 6)
					return;

				GamesService.join($scope.joiningPlayerCode).success(function(data) {
					$scope.gameId = data.gameId;
					$scope.gameToken = data.fullAuthToken;
					$scope.gameScreen = 'players';
					$scope.refreshGame();
				}).error(function(data) {
					if (data !== null)
						$scope.joinGameError = data.error;
				});
			};

			$scope.addComputerPlayer = function() {
				PlayersService.addComputerPlayer($scope.gameId, $scope.gameToken).success(function() {
					$scope.refreshGame();
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
					currentTurnNumber = 1;

					$scope.refreshGame().success(function() {
						if ($scope.game.status === 'PENDING')
							$scope.pollForGameStart();
					});
				});
			};

			$scope.pollForGameStart = function() {
				if (angular.isDefined(gameStartPoller))
					return;

				$scope.waitingForOtherPlayers = true;

				gameStartPoller = $interval(function() {
					$scope.refreshGame().success(function() {
						if ($scope.game.status === 'RUNNING') {
							$scope.stopPollingForGameStart();
						}
					});
				}, 2500);
			};

			$scope.stopPollingForGameStart = function() {
				if (angular.isDefined(gameStartPoller)) {
					$interval.cancel(gameStartPoller);
					gameStartPoller = undefined;
					$scope.waitingForOtherPlayers = undefined;
				}
			};

			$scope.resumeGame = function() {
				$scope.gameId = $cookies.gameId;
				$scope.gameToken = $cookies.gameToken;
				$scope.refreshGame();
			};

			$scope.refreshGame = function() {
				return GamesService.get($scope.gameId, $scope.gameToken).success(function(data) {
					$scope.game = data;

					if ($scope.game.status === 'RUNNING')
						$scope.gameScreen = 'ingame';
				});
			};

			$scope.endTurn = function() {
				if ($scope.blockingActionInProgress || $scope.game.status === 'OVER')
					return;

				$scope.showPlanetMenu = false;
				$scope.blockingActionInProgress = true;
				TurnsService.endTurn($scope.gameId, $scope.gameToken).success(function() {
					$scope.refreshGame().success(function() {
						if ($scope.game.turnNumber > currentTurnNumber)
							$scope.handleNewTurn();
						else
							$scope.pollForNewTurn();
					});
				}).error(function() {
					$scope.blockingActionInProgress = false;
				});
			};

			$scope.pollForNewTurn = function() {
				if (angular.isDefined(newTurnPoller))
					return;

				$scope.waitingForOtherPlayers = true;

				newTurnPoller = $interval(function() {
					$scope.refreshGame().success(function() {
						if ($scope.game.turnNumber > currentTurnNumber) {
							$scope.stopPollingForNewTurn();
							$scope.handleNewTurn();
						}
					});
				}, 2500);
			};

			$scope.stopPollingForNewTurn = function() {
				if (angular.isDefined(newTurnPoller)) {
					$interval.cancel(newTurnPoller);
					newTurnPoller = undefined;
					$scope.waitingForOtherPlayers = undefined;
				}
			};

			$scope.handleNewTurn = function() {
				$scope.blockingActionInProgress = false;
				$scope.showPlanetMenu = false;
				$scope.showTurnEvents = true;
				$scope.destinationSelectionActive = false;
				currentTurnNumber = $scope.game.turnNumber;
			}

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
								$scope.refreshGame();
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
					$scope.refreshGame();
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
						$scope.openJoinGameMenu();
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
		} ]);