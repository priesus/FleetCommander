var fleetCommanderServices = angular.module('fleetCommanderServices', [ 'ngResource' ]);

fleetCommanderServices.factory('GameService', [ '$http', function($http) {
	return {
		create : function() {
			return $http({
				method : 'POST',
				url : 'rest/games'
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
		}
	};
} ]);

fleetCommanderServices.factory('TurnService', [ '$http', function($http) {
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

fleetCommanderServices.factory('ShipService', [ '$http', function($http) {
	return {
		sendShips : function(gameId, token, ships, origin, dest) {
			return $http({
				method : 'POST',
				url : 'rest/games/' + gameId + '/universe/travellingShipFormations/' + ships + '/' + origin + '/' + dest,
				headers : {
					'Authorization' : 'Bearer ' + token
				}
			});
		}
	};
} ]);
