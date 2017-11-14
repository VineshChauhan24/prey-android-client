
function init() {
 
}
 
function signIn(){

        var pass=$('#lpass').val();
	 Android.signIn(pass);
	// $('#modal-login').hide();




	// $('#button2').html("<i class=\"icon-off\"  onclick=\"logout()\"></i><span>Logout</span>");
}

function closeSignIn(){
  Android.signIn("hola3 close");
  $('#modal-login').hide();
}

function panel(){
    Android.panel();
}
function settings(){
	
	//Android.settings(); 
	$('#modal-login').show();
	
	 $('#modal-login').toggleClass("show");
	 $('#modal-pin').toggleClass("");
	 $('#modal-RemoveDevice').toggleClass("");
	 
	
}

function logout(){
	 $('#button2').html("<i class=\"icon-prey-term\" onclick=\"settings()\"></i><span>Settings</span>");
	 
	 $('#device-viewer').show();
	 $('#settings-div').hide();
	 $('#device-status').show();
}
 
function removeDevice(){
	//Android.removeDevice(); 
	 $('#modal-RemoveDevice').css({ 'display': "" });
	$('#modal-RemoveDevice').show();
	 $('#modal-RemoveDevice').toggleClass("show");
	 $('#modal-pin').toggleClass("");
	 $('#modal-login').toggleClass("");
	
}

function borrarDevice(){
	//Android.borrarDevice(); 
	
}

function pinPrey(){
	 $('#modal-pin').show();
	 $('#modal-pin').toggleClass("show");
	 $('#modal-RemoveDevice').toggleClass("");
	 $('#modal-login').toggleClass("");
}


function closeRemoveDevice(){
	 
	 $('#modal-RemoveDevice').css({ 'display': "none" });
 
}


function closePin(){
	 
	 $('#modal-pin').css({ 'display': "none" });

}

function grabarPin(){
	 $('#modal-pin').css({ 'display': "none" });
	 $('#comandosSMS').toggleClass("disable");
}
/*
var Android ={
	signIn: function (){
		
		
	},
	settings: function (){
		 
	},
	removeDevice: function (){
		
		 
	},
	borrarDevice: function (){
		 
	}
	 
} */

$(function(){
	  init();
});