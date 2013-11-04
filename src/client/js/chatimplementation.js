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
	
	$scope.users = ["nadim","val","js"];

	
	
}
//parseuser({"user":{"id":"12489505@N04", "nsid":"12489505@N04", "username":{"_content":"robzhu"}}, "stat":"ok"})