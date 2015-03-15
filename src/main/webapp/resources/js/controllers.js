var fleetCommanderApp = angular.module('fleetCommanderApp', []);

fleetCommanderApp.controller('GamesCtrl', function($scope) {
	$scope.runningGame = {
	  'url' : 'games/123/',
	  'userId' : 123456789
	};
});