var fleetCommanderServices = angular.module('fleetCommanderServices', [ 'ngResource' ]);

fleetCommanderServices.factory('Game', [ '$resource', function($resource) {
	return $resource('rest/games', {}, {
		query : {
		  method : 'POST',
		  params : {
			  playerName : 'Player 1'
		  }
		}
	});
} ]);

fleetCommanderServices.factory('Universe', [ '$resource', function($resource) {
	return $resource('rest/games/100/universe', {}, {
		query : {
			method : 'GET'
		}
	});
} ]);