angular.module('Chatbox', ['ngResource']);



function Chatbox($scope,$resource) {
	
	$scope.method = 'GET';
	$scope.url = 'localhost:1234';
	
	$scope.users = ["i.nadim", "mjval_lin", "jdery", "nick"];
	
	
	$scope.fetch = function() {
    $scope.code = null;
    $scope.response = null;

    $http({method: $scope.method, url: $scope.url, cache: $templateCache}).
      	success(function(data, status) {
        	$scope.status = status;
        	$scope.data = data;
      	}).
      	error(function(data, status) {
        	$scope.data = data || "Request failed";
        	$scope.status = status;
    	});
  	};
	
  	$scope.updateModel = function(method, url) {
    	$scope.method = method;
    	$scope.url = url;
  	};
	
	
}