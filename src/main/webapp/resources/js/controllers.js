var fleetCommanderApp = angular.module('fleetCommanderApp', [ 'ngAnimate' ]);

fleetCommanderApp.controller('GamesCtrl', function($scope) {
	$scope.isIngame = false;
	$scope.runningGame = {
	  'url' : 'games/123/',
	  'userId' : 123456789
	};
});