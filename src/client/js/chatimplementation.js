angular.module('Chatbox', ['ngResource']);



function Chatbox($scope, $http, $resource) {
	
	$scope.method = 'POST';
	$scope.url = 'http://localhost:1234/';
	$scope.users = ["i.nadim", "mjval_lin", "jdery", "nick"];

	$scope.sendMessage = function() {
		var objDiv = document.getElementById("div1");
		objDiv.scrollTop = objDiv.scrollHeight;
	
		$scope.code = null;
		$scope.response = null;
		var text  = document.getElementById('messageBox').value;
		var fromUser = $scope.user;
		var toUser = document.getElementById('toUser').value;
	
		$http({
			method: "POST",
			url: 'http://localhost:1234/sendmessage', 
			headers: {'Content-Type': 'application/x-www-form-urlencoded'},
			data: 'from=' + fromUser + "&to=" + toUser + "&message=" + text,
		}).
		  success(function(data, status) {
			$scope.status = status;
			$scope.messages = data;
			
		  }).
		  error(function(data, status) {
			$scope.data = data || "Request failed";
			$scope.status = status;
		});
	};

	$scope.login = function(){		
		var username = document.getElementById('exampleInputEmail2').value;
		var password = document.getElementById('exampleInputPassword2').value;
			
		$http({
			method: "POST",
			url: 'http://localhost:1234/login', 
			headers: {'Content-Type': 'application/x-www-form-urlencoded'},		
			data: 'username=' + username + "&password=" + password,
		}).
		  success(function(data, status) {
			$scope.user = username;
			$scope.loggedin = true;
			$scope.getAllMessages(username);
			alert("Logged in, THANK YOU");
		  }).
		  error(function(data, status) {
			$scope.data = data || "Request failed";
			$scope.status = status;
			alert("Invalid Credential. Please try again.");
		});

	}
	$scope.getAllMessages = function(user) {
		var objDiv = document.getElementById("div1");
		objDiv.scrollTop = objDiv.scrollHeight;
		$http({
			method: "GET",
			url: 'http://localhost:1234/allmessages='+user, 
			headers: {'Content-Type': 'application/x-www-form-urlencoded'},
		}).
		  success(function(data, status) {
			$scope.status = status;
			$scope.messages = data;
		  }).
		  error(function(data, status) {
			$scope.data = data || "Request failed";
			$scope.status = status;
		});
	}
  	$scope.updateModel = function(method, url) {
    	$scope.method = method;
    	$scope.url = url;
  	};
	$scope.getMessageClass = function(from){

		if(from==$scope.user)
		{
			return "alert alert-info";
		}
		else
		{
			return "alert alert-success";
		}
	}
	
}