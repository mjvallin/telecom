angular.module('Chatbox', ['ngResource']);



function Chatbox($scope, $http, $resource) {
	
	$scope.method = 'POST';
	$scope.url = 'http://localhost:1234/';
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
	
	/* 
		TODO(mingju): this function is called when Send button is clicked
		I dunno why, but my POST request turn into an OPTIONS request. 
	*/
	$scope.sendMessage = function() {
		$scope.code = null;
		$scope.response = null;
		var text  = document.getElementById('messageBox').value;
		var fromUser = document.getElementById('fromUser').value;
		var toUser = document.getElementById('toUser').value;
		//alert("from: " + fromUser + ", to: " + toUser + "\n content: " + text);
	
		$http({
			method: "POST",
			url: 'http://localhost:1234/', 
			headers: {'Content-Type': 'application/x-www-form-urlencoded'},
			//data : {'from': 'i.nadim', 'to': 'nick', 'message': 'hi bro'}			
			data: 'from=' + fromUser + "&to=" + toUser + "&message=" + text,
			//TODO(mingju): this is a bit ugly. Parse from JSON String is better
		}).
		  success(function(data, status) {
			$scope.status = status;
			$scope.data = data;
			alert("pass");
		  }).
		  error(function(data, status) {
			$scope.data = data || "Request failed";
			$scope.status = status;
			//alert($scope.data);
		});
	};
	
  	$scope.updateModel = function(method, url) {
    	$scope.method = method;
    	$scope.url = url;
  	};
	
	
}