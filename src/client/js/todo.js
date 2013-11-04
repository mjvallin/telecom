angular.module('Flickr', ['ngResource']);

function TodoCtrl($scope,$resource) {
	$scope.userFound = true;
	$scope.hasGallery = true;
	$scope.displayGallery = false;
	$scope.flickr = $resource('http://api.flickr.com/services/rest/',
	{
	api_key:'5c96da7d1ca991e1a1b05abfc4ad7fbb',
	format:'json',
	jsoncallback:'parseuser'},
	{get:{method:'JSONP'}});
	
	/*
	$scope.windowWidth = 0;
	
	$scope.getWindowWidth = function() {
        return $(window).width();
    };
	
    $scope.$watch($scope.getWindowWidth, function(newValue, oldValue) {
        $scope.windowWidth = newValue;
    });
	
    window.onresize = function(){
        $scope.$apply();
    };	
	*/
	
	parseuser = function(result){
		$scope.displayGallery = false;
		$scope.albums = null;
		$scope.photos = null;
		
		$scope.displayAlbumTitle = null;
		if(result.stat=="fail")
		{
			$scope.userFound = false;
			
		}
		else
		{
			$scope.userFound = true;
			$scope.flickr.get(
				{method:'flickr.photosets.getList',
				 jsoncallback:'listAlbums',
				 user_id : result.user.id})
		}
	}
	listAlbums = function(result) {
		if(result.photosets.total > 0)
		{
			$scope.albums = result.photosets.photoset;
			$scope.hasGallery = true;
		}
		else
			$scope.hasGallery = false;
	}
	
	$scope.getAlbum = function(albumid,title) {
		$scope.displayAlbumTitle = title;
		$scope.flickr.get(
			{ method:'flickr.photosets.getPhotos',
			  photoset_id: albumid,
			  jsoncallback:'displayAlbum'
			});
	}
	
	displayAlbum = function(result) {
		
		//display first picture
		$scope.photos = Array();
		$scope.album = result.photoset.photo;
		displayPicture(0);  //display first image
		$scope.displayGallery = true;
		$scope.margins = Array();
	}

	//This function loads a SINGLE image and waits for it to load to move to the next one
	displayPicture = function(i)
	{
		
		
		var img = new Image();
		img.src = "http://farm"+$scope.album[i].farm+".staticflickr.com/"+$scope.album[i].server+
			"/"+$scope.album[i].id+"_"+$scope.album[i].secret+".jpg"; 
	
		
		img.onload = function() {  //Callback once image has been loaded to the client's browser
			var photo = new Object();
			photo.src = img.src;
			photo.index = i;
			photo.height = 200;//img.height; //pull these data for future use
			photo.width = Math.floor((200/img.height)*img.width); //proportionally reduce width
			$scope.photos.push(photo); //add photo
			$scope.$apply();
			//call function again if more photos are present
			if(i < $scope.album.length-1){
				displayPicture(i+1); 
				
			}
			GreedyAlgorithm();
			
			//displayAlbumTest($scope.photos.length);
			//@Ming-Ju : Call greedy again HERE, we have more pictures now so update spacing
		}
		
		
	}
	$scope.rows = Array();
	//SEN: let me know if you think anything is wrong with this algorithm
	GreedyAlgorithm = function(){
		var minWidth = 10;// minimum width between pics , 10px
		//950 giving 10 pixels of offset allowance, since the value wont be exact
		var displayWidth = 950; // 960 pixels because bootstrap uses this for rows
		var w=0;
		var count=0;
		
		for(var i=0; i<$scope.photos.length; i++){
			if(w+ $scope.photos[i].width+ count*10>displayWidth){
				var RowWidth = Math.floor((displayWidth-w)/(count-1)); // division by 0 will happen only ,
																		//if the width of the picture is larger than the screen
				for(var j=count; j>1; j--){
					$scope.margins[i-j]=RowWidth; 
				}
				$scope.margins[i-1]=0;
				w= $scope.photos[i].width;
				count = 1;
			}
			else{
				w+=$scope.photos[i].width;
				count++;
			}
		}
		//for the last row, need to deal with it
		for(var j=count; j>0; j--){
			$scope.margins[$scope.photos.length-j]=30; 
		}
		$scope.$apply();

	}
	
	//5c96da7d1ca991e1a1b05abfc4ad7fbb
	displayAlbumTest = function(numPhotos) {
		
		var minWidth = 10;// minimum width between pics , 10px
		var displayWidth = 960; // 960 pixels because bootstrap uses this for rows
		var currWidth = 0;
		var currPhotos = 0;
		for(var i=0; i<numPhotos; i++)
		{
			//add up width of the pictures
			currWidth += $scope.photos[i].width;
			

			
			
				//This is the last picture, last row, add a random margin for now @todo: discuss any other solution
				if(i+1 == numPhotos)
				{
					for(var j = i-currPhotos; j<i; j++)
					{
						$scope.margins[j] = 30; //random spacing because we're at the last row
					}
					$scope.margins[i] = 30; //random spacing
				}
				//check if adding another picture is possible without going over the display width
				else if( currWidth+$scope.photos[i+1].width + currPhotos*10 > displayWidth)
				{
					maxPossibleSpace = (displayWidth - currWidth)/(currPhotos); //calculate max possible spacing, note that 
																				//currPhotos is the index, not the number of pics
						
					//add margins based on the max calculated space
					for(var j = i-currPhotos; j<i; j++)
					{
						$scope.margins[j] = Math.floor(maxPossibleSpace);
					}

					$scope.margins[i] = 0; //make the last border right 0 
					currWidth = 0;
					currPhotos = 0;
					
				}
				else
					currPhotos++;
			
			
		}
	}
	
	/*
		TODO(mingju): I think there is an off by one error here
		the last picture is not caught
	*/
	displayAlbumTest1 = function(numPhotos) {
		var minWidth = 10;		// minimum width between pics , 10px
		var L = 960; 			// 960 pixels because bootstrap uses this for rows
		var spaceCount = 0;
		
		var i = 0;
		var start = 0;
		var end = 0;
		var w = 0;
		
		while(i < $scope.photos.length) {
			
			$scope.margins[i] = 0;
		
			spaceCount = 0;
			w = $scope.photos[i].width; 
			
			++i;
			start = i;
			/* 	
			as long as there is still photo
				and it can still fit in one line
			*/ 
			while(i < $scope.photos.length && 
				w + minWidth + $scope.photos[i].width <= L) {
				++spaceCount; //add one space
				w += $scope.photos[i].width + minWidth;
				++i;
			}
			
			end = i;

			if(spaceCount > 0) {
				var residual = L - w;
				var avg = Math.floor(residual / spaceCount); 
				//var rem = residual % spaceCount;
				
				var k = start;
				var j = 0;
				while(j < spaceCount && k < end) {

					// if can't divide evenly
					//var extraSpace = j < rem ? avg + 1 : avg;
					extraSpace = avg;
					$scope.margins[k] = minWidth + extraSpace;
					++j;
					++k;
					//console.log("index = " + k);
				}
			} else { 
				// if no more photos or actually no
				// extra space is needed
				var k = start;
				var j = 0;
				//var residual = L - w;
				
				while(j < spaceCount && k < end) {
					$scope.margins[k] = minWidth;
					++j;
					++k;					
				}
			}
		}
	}
	
	$scope.getAlbums = function getAlbums(){
	//pushing value to previously initialized todos
		$scope.flickr.get(
		{method:'flickr.people.findByUsername',
		username:$scope.username,
		jsoncallback:'parseuser'});
		
	}
	
	getsize = function(result) {
		$scope.sizes = result.sizes.size;
		/*
		for(var size in $scope.sizes){
			if(size.label=="Original")
				$scope.height= size.height;
				$scope.weight= size.width;
			}
		}
		*/
		
	}
	
	
}
//parseuser({"user":{"id":"12489505@N04", "nsid":"12489505@N04", "username":{"_content":"robzhu"}}, "stat":"ok"})