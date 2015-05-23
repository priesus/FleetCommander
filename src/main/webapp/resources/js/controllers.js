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
			$scope.playerName = $cookies.playerName;
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
				$cookies.playerName = $scope.playerName;
				GamesService.create($scope.playerName).success(function(data) {
					$scope.gameId = data.gameId;
					$scope.gameToken = data.fullAuthToken;
					$scope.gameScreen = 'players';
					$scope.startGameError = undefined;
					$scope.requestJoinCodeError = undefined;
					$scope.requestJoinCode();
					$scope.refreshGame();
					$scope.pollForGameStart();
				});
			};

			$scope.openJoinGameMenu = function() {
				$scope.joinGameError = undefined;
				$scope.joiningPlayerCode = '';
				$cookies.playerName = $scope.playerName;
				$scope.gameScreen = 'join';
			};

			$scope.requestJoinCode = function() {
				JoinCodesService.create($scope.gameId, $scope.gameToken).success(function() {
					$scope.refreshActiveJoinCodes();
				}).error(function(data) {
					if (data !== null)
						$scope.requestJoinCodeError = data.error;
				});
			};

			$scope.tryToJoinGame = function() {
				if ($scope.joiningPlayerCode === undefined || $scope.joiningPlayerCode.length != 6)
					return;

				GamesService.join($scope.playerName, $scope.joiningPlayerCode).success(function(data) {
					$scope.gameId = data.gameId;
					$scope.gameToken = data.fullAuthToken;
					$scope.gameScreen = 'players';
					$scope.refreshGame();
					$scope.pollForGameStart();
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
							$scope.waitingForOtherPlayers = true;
					});
				}).error(function(data) {
					if (data !== null)
						$scope.startGameError = data.error;
				});
			};

			$scope.pollForGameStart = function() {
				if (angular.isDefined(gameStartPoller))
					return;

				gameStartPoller = $interval(function() {
					$scope.refreshActiveJoinCodes();
					$scope.refreshGame().success(function() {
						if ($scope.game.status === 'RUNNING') {
							$scope.stopPollingForGameStart();
						}
					}).error(function() {
						$scope.stopPollingForGameStart();
					});
				}, 1000);
			};

			$scope.stopPollingForGameStart = function() {
				if (angular.isDefined(gameStartPoller)) {
					$interval.cancel(gameStartPoller);
					gameStartPoller = undefined;
					$scope.waitingForOtherPlayers = undefined;
				}
			};

			$scope.refreshActiveJoinCodes = function() {
				JoinCodesService.getAllActive($scope.gameId, $scope.gameToken).success(function(data) {
					$scope.activeJoinCodes = data.joinCodes;
				});
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
					}).error(function() {
						$scope.stopPollingForNewTurn();
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
			};

			$scope.quitGame = function() {
				$scope.stopPollingForGameStart();
				GamesService.quit($scope.gameId, $scope.gameToken);
				$scope.showTurnEvents = false;
				$scope.gameScreen = 'home';
				delete $scope.game;
				delete $cookies.gameId;
			};

			$scope.clickPlanetHandler = function(planetIndex) {
				if ($scope.game.status === 'OVER')
					return;

				var planet = $scope.game.universe.planets[planetIndex];

				if (!$scope.destinationSelectionActive && planet.inhabitedByMe) {
					// Open planet menu
					$scope.selectedPlanetIndex = planetIndex;
					$scope.showPlanetMenu = true;
					$scope.setShipCount(1);

				} else if ($scope.destinationSelectionActive) {
					// Send ships from previously selected planet to this planet
					var selectedPlanet = $scope.game.universe.planets[$scope.selectedPlanetIndex];
					ShipsService.sendShips($scope.gameId, $scope.gameToken, $scope.shipCount, selectedPlanet.id, planet.id)
							.success(function() {
								$scope.refreshGame();
							});
					$scope.destinationSelectionActive = false;
				}
			};

			$scope.setShipCount = function(ships) {
				if (ships < 1)
					ships = 1;

				var selectedPlanet = $scope.game.universe.planets[$scope.selectedPlanetIndex];

				if (ships > selectedPlanet.shipCount)
					ships = selectedPlanet.shipCount
				$scope.shipCount = ships;
			};

			$scope.buildFactoryOnSelectedPlanet = function() {
				var selectedPlanet = $scope.game.universe.planets[$scope.selectedPlanetIndex];
				PlanetsService.buildFactory($scope.gameId, $scope.gameToken, selectedPlanet.id).success(function() {
					$scope.game.universe.planets[$scope.selectedPlanetIndex].factorySite.factoryCount++;
					$scope.game.universe.planets[$scope.selectedPlanetIndex].factorySite.availableSlots--;
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
			};

			$scope.$on('$destroy', function() {
				$scope.stopPollingForGameStart();
				$scope.stopPollingForNewTurn();
			});

			$scope.onKeyDown = function($event) {
				if ($scope.gameScreen === 'players') {
					switch ($event.keyCode) {
					case 80: // [P]lay
						$scope.startGame();
						break;
					case 65: // [A]dd computer player
						if ($scope.game.otherPlayers.length < 5)
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
					case 27: // [Esc]
						$scope.gameScreen = 'home';
						break;
					}
				} else if ($scope.gameScreen === 'ingame') {
					switch ($event.keyCode) {
					case 83: // [s]end ships
						$scope.prepareSendShips();
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