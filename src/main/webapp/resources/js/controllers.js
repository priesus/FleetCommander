var fleetCommanderApp = angular.module('fleetCommanderApp', [ 'fleetCommanderServices', 'ngAnimate' ]);

fleetCommanderApp.controller('GamesCtrl', [ 'Universe', function($scope) {

} ]);

fleetCommanderApp.controller('GamesCtrl', [ '$scope', 'Universe', function($scope, Universe) {
	$scope.runningGame = {
	  'url' : 'game/100/',
	  'userId' : 123456789,
	  'universe' : Universe.get()
	};
	$scope.isIngame = $scope.runningGame.universe !== undefined;
} ]);