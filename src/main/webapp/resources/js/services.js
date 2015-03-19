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
