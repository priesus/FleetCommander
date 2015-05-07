var fleetCommanderServices = angular.module('fleetCommanderServices', [ 'ngResource' ]);

fleetCommanderServices.factory('GamesService', [ '$http', function($http) {
	return {
		create : function() {
			return $http({
				method : 'POST',
				url : 'rest/games'
			});
		},
		join : function(joinCode) {
			return $http({
				method : 'POST',
				url : 'rest/games',
				data : {
					'joinCode' : joinCode
				}
			});
		},
		get : function(gameId, token) {
			return $http({
				method : 'GET',
				url : 'rest/games/' + gameId,
				headers : {
					'Authorization' : 'Bearer ' + token
				}
			});
		},
		quit : function(gameId, token) {
			return $http({
				method : 'DELETE',
				url : 'rest/games/' + gameId,
				headers : {
					'Authorization' : 'Bearer ' + token
				}
			});
		},
		start : function(gameId, token) {
			return $http({
				method : 'POST',
				url : 'rest/games/' + gameId,
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

fleetCommanderServices.factory('PlayersService', [ '$http', function($http) {
	return {
		addComputerPlayer : function(gameId, token) {
			return $http({
				method : 'POST',
				url : 'rest/games/' + gameId + '/players',
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
				url : 'rest/games/' + gameId + '/turns',
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
				url : 'rest/games/' + gameId + '/universe/planets/' + planetId + '/factories',
				headers : {
					'Authorization' : 'Bearer ' + token
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
				url : 'rest/games/' + gameId + '/universe/travellingShipFormations',
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
