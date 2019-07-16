var fleetCommanderServices = angular.module('fleetCommanderServices', [ 'ngResource' ]);

fleetCommanderServices.factory('GamesService', [ '$http', function($http) {
	return {
		create : function(playerName) {
			return $http({
				method : 'POST',
				url : 'games',
				data : {
					'player_name' : playerName
				}
			});
		},
		join : function(playerName, joinCode) {
			return $http({
				method : 'POST',
				url : 'games',
				data : {
					'player_name' : playerName,
					'join_code' : joinCode
				}
			});
		},
		get : function(gameId, token) {
			return $http({
				method : 'GET',
				url : 'games/' + gameId,
				headers : {
					'Authorization' : 'Bearer ' + token
				}
			});
		},
		quit : function(gameId, token) {
			return $http({
				method : 'DELETE',
				url : 'games/' + gameId,
				headers : {
					'Authorization' : 'Bearer ' + token
				}
			});
		},
		start : function(gameId, token) {
			return $http({
				method : 'POST',
				url : 'games/' + gameId,
				headers : {
					'Authorization' : 'Bearer ' + token
				},
				data : {
					'started' : true
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
				url : 'games/' + gameId + '/join-codes',
				headers : {
					'Authorization' : 'Bearer ' + token
				}
			});
		},
		getAllActive : function(gameId, token) {
			return $http({
				method : 'GET',
				url : 'games/' + gameId + '/join-codes',
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
				url : 'games/' + gameId + '/players',
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
				url : 'games/' + gameId + '/turns',
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
				url : 'games/' + gameId + '/universe/planets/' + planetId + '/factories',
				headers : {
					'Authorization' : 'Bearer ' + token
				}
			});
		},
		changeProductionFocus : function(gameId, token, planetId, focus) {
			return $http({
				method : 'POST',
				url : 'games/' + gameId + '/universe/planets/' + planetId,
				headers : {
					'Authorization' : 'Bearer ' + token
				},
				data : {
					'production_focus' : focus
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
				url : 'games/' + gameId + '/universe/travelling-ship-formations',
				headers : {
					'Authorization' : 'Bearer ' + token
				},
				data : {
					'ship_count' : ships,
					'origin_planet_id' : origin,
					'destination_planet_id' : dest
				}
			});
		}
	};
} ]);
