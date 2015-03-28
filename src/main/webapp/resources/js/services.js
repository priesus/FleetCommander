var fleetCommanderServices = angular.module('fleetCommanderServices', [ 'ngResource' ]);

fleetCommanderServices.factory('GameService_', [ '$http', function($http) {
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
	  }
	};
} ]);

fleetCommanderServices.factory('TurnService_', [ '$http', function($http) {
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

fleetCommanderServices.factory('ShipService_', [ '$http', function($http) {
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

fleetCommanderServices.factory('GameService', [ '$resource', function($resource) {
	return $resource('rest/games/:gameId', {
		gameId : '@id'
	}, {
		start : {
			method : 'POST'
		}
	});
} ]);

fleetCommanderServices.factory('TurnService', [ '$resource', function($resource) {
	return $resource('rest/games/:gameId/turns', {
		gameId : '@gameId'
	}, {
		endTurn : {
			method : 'POST'
		}
	});
} ]);

fleetCommanderServices.factory('ShipService', [ '$resource', function($resource) {
	return $resource('rest/games/:gameId/universe/travellingShipFormations/:ships/:origin/:dest', {
	  gameId : '@gameId',
	  ships : '@shipCount',
	  origin : '@originPlanet',
	  dest : '@destinationPlanet'
	}, {
		sendShips : {
			method : 'POST'
		}
	});
} ]);
