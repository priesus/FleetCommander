var fleetCommanderControllers = angular.module('fleetCommanderControllers', [ 'fleetCommanderServices', 'ngCookies', 'ngTouch' ]);

fleetCommanderControllers.controller('HomeCtrl', [
	'$scope', '$cookies', '$location',
	function($scope, $cookies,  $location) {

		$scope.activeGameId = $cookies.get('gameId');
		$scope.playerName = $cookies.get('playerName');

		$scope.hasActiveGame = function() {
			return $scope.activeGameId !== undefined;
		};

		$scope.createNewGame = function() {
			storePlayerNameInCookie($scope.playerName);
			$location.path('/create');
		};

		$scope.resumeGame = function() {
			storePlayerNameInCookie($scope.playerName);
			$location.path('/games/{{activeGameId}}');
		};

		$scope.joinGame = function() {
			storePlayerNameInCookie($scope.playerName);
			$location.path('/join');
		};

		var storePlayerNameInCookie = function(playerName){
			var sixMonthsAhead = new Date();
			sixMonthsAhead.setMonth(sixMonthsAhead.getMonth()+6);
			$cookies.put('playerName', playerName, {'expires': sixMonthsAhead});
		};
	}]);

fleetCommanderControllers.controller('CreateCtrl', [
	'$scope', '$cookies', '$location', 'GamesService', 'JoinCodesService',
	function($scope, $cookies,  $location, GamesService, JoinCodesService) {

		var createNewGame = function() {
			var playerName = $cookies.get('playerName');
			GamesService.create(playerName).success(function(data) {
				var gameId = data.gameId;
				var gameToken = data.fullAuthToken;
				storeGameCredentialsInCookie(gameId, gameToken);
				requestJoinCode(gameId, gameToken);

				$location.path('/players');
			});
		};

		var storeGameCredentialsInCookie = function(gameId, gameToken){
			var sixMonthsAhead = new Date();
			sixMonthsAhead.setMonth(sixMonthsAhead.getMonth()+6);
			$cookies.put('gameId', gameId, {'expires': sixMonthsAhead});
			$cookies.put('gameToken', gameToken, {'expires': sixMonthsAhead});
		};

		var requestJoinCode = function(gameId, gameToken) {
			JoinCodesService.create(gameId, gameToken);
		};

		createNewGame();
	}]);

fleetCommanderControllers.controller('PlayersCtrl', [
	'$scope', '$document', '$cookies', '$interval', '$location', 'GamesService', 'JoinCodesService', 'PlayersService',
	function($scope, $document, $cookies, $interval, $location, GamesService, JoinCodesService, PlayersService) {

		$scope.playerName = $cookies.get('playerName');
		$scope.gameId = $cookies.get('gameId');
		$scope.gameToken = $cookies.get('gameToken');
		$scope.game = undefined;
		$scope.waitingForOtherPlayers = false;
		$scope.startGameError = undefined;
		$scope.requestJoinCodeError = undefined;

		var gameStartPoller;

		$scope.requestJoinCode = function() {
			JoinCodesService.create($scope.gameId, $scope.gameToken).success(function() {
				refreshActiveJoinCodes();
			}).error(function(data) {
				if (data !== null)
					$scope.requestJoinCodeError = data.error;
			});
		};

		$scope.addComputerPlayer = function() {
			if ($scope.game.otherPlayers.length < 5) {
				PlayersService.addComputerPlayer($scope.gameId, $scope.gameToken).success(function () {
					refreshGame();
				});
			}
		};

		$scope.startGame = function() {
			GamesService.start($scope.gameId, $scope.gameToken).success(function() {
				refreshGame();
			}).error(function(data) {
				if (data !== null)
					$scope.startGameError = data.error;
			});
		};

		$scope.cancelGameSetup = function() {
			GamesService.quit($scope.gameId, $scope.gameToken);
			$location.path('/');
		};

		var handlePlayersKeyDown = function(event) {
			switch (event.keyCode) {
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
					$scope.cancelGameSetup();
					break;
			}
			$scope.$apply();
		};
		$document.on('keydown', handlePlayersKeyDown);

		$scope.$on('$destroy', function() {
			stopPollingForGameStart();
			$document.unbind('keydown', handlePlayersKeyDown);
		});

		var pollForGameStart = function() {
			if (angular.isDefined(gameStartPoller))
				return;
			refreshActiveJoinCodes();
			refreshGame();

			gameStartPoller = $interval(function() {
				refreshActiveJoinCodes();
				refreshGame();
			}, 1000);
		};

		var refreshActiveJoinCodes = function() {
			JoinCodesService.getAllActive($scope.gameId, $scope.gameToken).success(function(data) {
				$scope.activeJoinCodes = data.joinCodes;
			});
		};

		var stopPollingForGameStart = function() {
			if (angular.isDefined(gameStartPoller)) {
				$interval.cancel(gameStartPoller);
				gameStartPoller = undefined;
				$scope.waitingForOtherPlayers = false;
			}
		};

		var refreshGame = function() {
			return GamesService.get($scope.gameId, $scope.gameToken).success(function(data) {
				$scope.game = data;

				$scope.waitingForOtherPlayers = $scope.game.status === 'PENDING' && $scope.game.me.status === 'READY';

				if ($scope.game.status !== 'PENDING')
					$location.path('/games/' + $scope.gameId);
			});
		};
		pollForGameStart();
	}]);

fleetCommanderControllers.controller('JoinCtrl', [
	'$scope', '$document', '$cookies', '$interval', '$location', 'GamesService',
	function($scope, $document, $cookies, $interval, $location, GamesService) {

		$scope.playerName = $cookies.get('playerName');

		$scope.tryToJoinGame = function() {
			if ($scope.joiningPlayerCode === undefined || $scope.joiningPlayerCode.length !== 6)
				return;

			GamesService.join($scope.playerName, $scope.joiningPlayerCode).success(function(data) {
				storeGameCredentialsInCookie(data.gameId, data.fullAuthToken);
				$location.path('/players');
			}).error(function(data) {
				if (data !== null)
					$scope.joinGameError = data.error;
			});
		};

		$scope.createNewGame = function() {
			storePlayerNameInCookie($scope.playerName);
			$location.path('/create');
		};

		var handleJoinKeyDown = function(event) {
			switch (event.keyCode) {
				case 27: // [Esc]
					$location.path('/');
					break;
			}
			$scope.$apply();
		};
		$document.on('keydown', handleJoinKeyDown);

		$scope.$on('$destroy', function() {
			$document.unbind('keydown', handleJoinKeyDown);
		});

		var storeGameCredentialsInCookie = function(gameId, gameToken){
			var sixMonthsAhead = new Date();
			sixMonthsAhead.setMonth(sixMonthsAhead.getMonth()+6);
			$cookies.put('gameId', gameId, {'expires': sixMonthsAhead});
			$cookies.put('gameToken', gameToken, {'expires': sixMonthsAhead});
		};

		var storePlayerNameInCookie = function(playerName){
			var sixMonthsAhead = new Date();
			sixMonthsAhead.setMonth(sixMonthsAhead.getMonth()+6);
			$cookies.put('playerName', playerName, {'expires': sixMonthsAhead});
		};
	}]);

fleetCommanderControllers.controller('IngameCtrl', [
	'$scope', '$document', '$cookies', '$interval', '$location',
	'GamesService', 'JoinCodesService', 'PlayersService', 'TurnsService', 'PlanetsService', 'ShipsService',
	function($scope, $document, $cookies, $interval, $location,
	         GamesService, JoinCodesService, PlayersService, TurnsService, PlanetsService, ShipsService) {

		$scope.playerName = $cookies.get('playerName');
		$scope.gameId = $cookies.get('gameId');
		$scope.gameToken = $cookies.get('gameToken');
		$scope.waitingForOtherPlayers = true;
		$scope.showTurnEvents = false;
		$scope.showPlanetMenu = false;
		$scope.blockingActionInProgress = false;
		$scope.game = undefined;

		var newTurnPoller;

		var handleNewTurn = function() {
			$scope.blockingActionInProgress = false;
			$scope.showPlanetMenu = false;
			$scope.showTurnEvents = true;
			$scope.destinationSelectionActive = false;
		};

		var refreshGame = function() {
			return GamesService.get($scope.gameId, $scope.gameToken).success(function(data) {
				$scope.game = data;

				if ($scope.game.me.status !== 'READY') {
					stopPollingForNewTurn();
					handleNewTurn();
				}
			}).error(function () {
				stopPollingForNewTurn();
			});
		};

		var refreshGameData = function() {
			GamesService.get($scope.gameId, $scope.gameToken).success(function(data) {
				$scope.game = data;
			});
		};

		var stopPollingForNewTurn = function() {
			if (angular.isDefined(newTurnPoller)) {
				$interval.cancel(newTurnPoller);
				newTurnPoller = undefined;
				$scope.waitingForOtherPlayers = false;
			}
		};

		var pollForNewTurn = function() {
			if (angular.isDefined(newTurnPoller))
				return;

			$scope.waitingForOtherPlayers = true;

			refreshGame().success(function(){

				if($scope.waitingForOtherPlayers)
					newTurnPoller = $interval(refreshGame(), 2500);
			});
		};
		pollForNewTurn();

		$scope.endTurn = function() {
			if ($scope.blockingActionInProgress || $scope.game.status === 'OVER')
				return;

			$scope.showPlanetMenu = false;
			$scope.blockingActionInProgress = true;
			TurnsService.endTurn($scope.gameId, $scope.gameToken).success(
				pollForNewTurn
			).error(function() {
				$scope.blockingActionInProgress = false;
			});
		};

		$scope.quitGame = function() {
			stopPollingForNewTurn();
			GamesService.quit($scope.gameId, $scope.gameToken);
			$cookies.remove('gameId');
			$scope.gameScreen = 'home';
			$location.path('/');
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
				$scope.productionFocus = planet.factorySite.shipProductionFocus;

			} else if ($scope.destinationSelectionActive) {
				// Send ships from previously selected planet to this planet
				var selectedPlanet = $scope.game.universe.planets[$scope.selectedPlanetIndex];
				ShipsService.sendShips($scope.gameId, $scope.gameToken, $scope.shipCount, selectedPlanet.id, planet.id)
					.success(function() {
						refreshGameData();
					});
				$scope.destinationSelectionActive = false;
			}
		};

		$scope.setShipCount = function(ships) {
			if (ships < 1)
				ships = 1;

			var selectedPlanet = $scope.game.universe.planets[$scope.selectedPlanetIndex];

			if (ships > selectedPlanet.shipCount)
				ships = selectedPlanet.shipCount;
			$scope.shipCount = ships;
		};

		$scope.buildFactoryOnSelectedPlanet = function() {
			if (!$scope.game.me.canAffordFactory)
				return;

			var selectedPlanet = $scope.game.universe.planets[$scope.selectedPlanetIndex];
			PlanetsService.buildFactory($scope.gameId, $scope.gameToken, selectedPlanet.id).success(function() {
				$scope.game.universe.planets[$scope.selectedPlanetIndex].factorySite.factoryCount++;
				$scope.game.universe.planets[$scope.selectedPlanetIndex].factorySite.availableSlots--;
				refreshGameData();
			});
		};

		$scope.changeProductionFocusOnSelectedPlanet = function() {
			var selectedPlanet = $scope.game.universe.planets[$scope.selectedPlanetIndex];
			PlanetsService
				.changeProductionFocus($scope.gameId, $scope.gameToken, selectedPlanet.id, $scope.productionFocus).success(
				function() {
					refreshGameData();
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

		var handleIngameKeyDown = function(event) {
			switch (event.keyCode) {
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
			$scope.$apply();
		};
		$document.on('keydown', handleIngameKeyDown);

		$scope.$on('$destroy', function() {
			stopPollingForNewTurn();
			$document.unbind('keydown', handleIngameKeyDown);
		});
	}]);
