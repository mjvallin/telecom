angular.module('Chatbox', ['ngResource']);



function Chatbox($scope, $http, $resource) {
	
	$scope.method = 'POST';
	$scope.url = 'localhost:1234';
	
	$scope.users = ["i.nadim", "mjval_lin", "jdery", "nick"];
	
	
	$scope.postdata = function() {
    $scope.code = null;
    $scope.response = null;
    alert(document.getElementById('s2').value);
    var text  = document.getElementById('s2').value;
    $http({method: $scope.method, url: $scope.url, cache: $templateCache, data : {varrrr: "post msg 1"}}).
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