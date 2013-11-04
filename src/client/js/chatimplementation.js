angular.module('Chatbox', ['ngResource']);

function Chatbox($scope,$resource) {
	$scope.userFound = true;
	$scope.hasGallery = true;
	$scope.displayGallery = false;
	$scope.flickr = $resource('http://api.flickr.com/services/rest/',
	{
	api_key:'5c96da7d1ca991e1a1b05abfc4ad7fbb',
	format:'json',
	jsoncallback:'parseuser'},
	{get:{method:'JSONP'}});
	
	$scope.users = ["i.nadim", "mjval_lin", "jdery", "nick"];
	
	
	send = function(from, to, message) {
		
		if(from == to || !isValidUser(to)) {
			//alert
		} else {
			//post  w/o going to another page. 
			//clear message box
			//message sucessfully sent, get this from post result
		}
	}
	
	isValidUser = function(username) {
		for(var user in $scope.users) {
			if(user = username) {
				return true;
			}		
		}
		
		return false;
	}
	
	
}
//parseuser({"user":{"id":"12489505@N04", "nsid":"12489505@N04", "username":{"_content":"robzhu"}}, "stat":"ok"})