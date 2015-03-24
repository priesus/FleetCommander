var fleetCommanderServices = angular.module('fleetCommanderServices', [ 'ngResource' ]);

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
