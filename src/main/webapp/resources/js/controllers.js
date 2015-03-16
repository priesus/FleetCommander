var fleetCommanderApp = angular.module('fleetCommanderApp', [ 'ngAnimate' ]);

fleetCommanderApp.controller('GamesCtrl', function($scope) {
	$scope.runningGame = {
	  'url' : 'games/123/',
	  'userId' : 123456789,
	  'universe' : {
		  'planets' : [ {
		    'x' : 0,
		    'y' : 0
		  }, {
		    'x' : 5,
		    'y' : 5
		  }, {
		    'x' : 5,
		    'y' : 45
		  }, {
		    'x' : 45,
		    'y' : 5
		  }, {
		    'x' : 45,
		    'y' : 45
		  }, {
		    'x' : 25,
		    'y' : 25
		  }, {
		    'x' : 0,
		    'y' : 99
		  }, {
		    'x' : 99,
		    'y' : 0
		  }, {
		    'x' : 99,
		    'y' : 99
		  } ]
	  }
	};
	$scope.isIngame = $scope.runningGame.universe !== undefined;
});