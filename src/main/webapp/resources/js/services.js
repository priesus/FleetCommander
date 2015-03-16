var fleetCommanderServices = angular.module('fleetCommanderServices', [ 'ngResource' ]);

fleetCommanderServices.factory('Universe', [ '$resource', function($resource) {
	return $resource('rest/games/100/universe', {}, {
		query : {
		  method : 'GET',
		  isArray : true
		}
	});
} ]);