var fleetCommanderServices = angular.module('fleetCommanderServices', [ 'ngResource' ]);

fleetCommanderServices.factory('GamesService', [ '$http', function($http) {
	return {
		create : function(playerName) {
			return $http({
				method : 'POST',
				url : 'api/games',
				data : {
					'playerName' : playerName
				}
			});
		},
		join : function(playerName, joinCode) {
			return $http({
				method : 'POST',
				url : 'api/games',
				data : {
					'playerName' : playerName,
					'joinCode' : joinCode
				}
			});
		},
		get : function(gameId, token) {
			return $http({
				method : 'GET',
				url : 'api/games/' + gameId,
				headers : {
					'Authorization' : 'Bearer ' + token
				}
			});
		},
		quit : function(gameId, token) {
			return $http({
				method : 'DELETE',
				url : 'api/games/' + gameId,
				headers : {
					'Authorization' : 'Bearer ' + token
				}
			});
		},
		start : function(gameId, token) {
			return $http({
				method : 'POST',
				url : 'api/games/' + gameId,
				headers : {
					'Authorization' : 'Bearer ' + token
				},
				data : {
					isStarted : true
				}
			});
		}
	};
} ]);

fleetCommanderServices.factory('JoinCodesService', [ '$http', function($http) {
	return {
		create : function(gameId, token) {
			return $http({
				method : 'POST',
				url : 'api/games/' + gameId + '/joinCodes',
				headers : {
					'Authorization' : 'Bearer ' + token
				}
			});
		},
		getAllActive : function(gameId, token) {
			return $http({
				method : 'GET',
				url : 'api/games/' + gameId + '/joinCodes',
				headers : {
					'Authorization' : 'Bearer ' + token
				}
			});
		}
	};
} ]);

fleetCommanderServices.factory('PlayersService', [ '$http', function($http) {
	return {
		addComputerPlayer : function(gameId, token) {
			return $http({
				method : 'POST',
				url : 'api/games/' + gameId + '/players',
				headers : {
					'Authorization' : 'Bearer ' + token
				}
			});
		}
	};
} ]);

fleetCommanderServices.factory('TurnsService', [ '$http', function($http) {
	return {
		endTurn : function(gameId, token) {
			return $http({
				method : 'POST',
				url : 'api/games/' + gameId + '/turns',
				headers : {
					'Authorization' : 'Bearer ' + token
				}
			});
		}
	};
} ]);

fleetCommanderServices.factory('PlanetsService', [ '$http', function($http) {
	return {
		buildFactory : function(gameId, token, planetId) {
			return $http({
				method : 'POST',
				url : 'api/games/' + gameId + '/universe/planets/' + planetId + '/factories',
				headers : {
					'Authorization' : 'Bearer ' + token
				}
			});
		},
		changeProductionFocus : function(gameId, token, planetId, focus) {
			return $http({
				method : 'POST',
				url : 'api/games/' + gameId + '/universe/planets/' + planetId,
				headers : {
					'Authorization' : 'Bearer ' + token
				},
				data : {
					productionFocus : focus
				}
			});
		}
	};
} ]);

fleetCommanderServices.factory('ShipsService', [ '$http', function($http) {
	return {
		sendShips : function(gameId, token, ships, origin, dest) {
			return $http({
				method : 'POST',
				url : 'api/games/' + gameId + '/universe/travellingShipFormations',
				headers : {
					'Authorization' : 'Bearer ' + token
				},
				data : {
					'shipCount' : ships,
					'originPlanetId' : origin,
					'destinationPlanetId' : dest
				}
			});
		}
	};
} ]);
