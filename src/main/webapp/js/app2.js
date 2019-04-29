/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

var localVideo;
var localStream;
var remoteVideo;
var peerConnection;
var uuid;
var serverConnection;

var peerConnectionConfig = {
  'iceServers': [
    {'urls': 'stun:stun.stunprotocol.org:3478'},
    {'urls': 'stun:stun.l.google.com:19302'},
  ]
};

function pageReady() {
  uuid = createUUID();

  localVideo = document.getElementById('vid1');
  remoteVideo = document.getElementById('vid2');

  serverConnection = new WebSocket('wss://' + 'Your_IP_Adress:8443' + room); //window.location.hostname
  serverConnection.onmessage = gotMessageFromServer;
  
  
  

  var constraints = {
    video: true,
    audio: true,
  };

  if(navigator.mediaDevices.getUserMedia) {
    navigator.mediaDevices.getUserMedia(constraints).then(getUserMediaSuccess).catch(errorHandler);
  } else {
    alert('Your browser does not support getUserMedia API');
  }
}

function getUserMediaSuccess(stream) {
  localStream = stream;
  localVideo.srcObject = stream;
}

function start(isCaller) {
  peerConnection = new RTCPeerConnection(peerConnectionConfig);
  peerConnection.onicecandidate = gotIceCandidate;
  peerConnection.ontrack = gotRemoteStream;
  peerConnection.addStream(localStream);
  
  console.log('start');

  if(isCaller) {
    peerConnection.createOffer().then(createdDescription).catch(errorHandler);
  }
}

function W(message) {
  if(!peerConnection) start(false);

  var signal = JSON.parse(message.data);

  // Ignore messages from ourself
  if(signal.uuid == uuid) return;

  if(signal.sdp) {
    peerConnection.setRemoteDescription(new RTCSessionDescription(signal.sdp)).then(function() {
      // Only create answers in response to offers
      if(signal.sdp.type == 'offer') {
        peerConnection.createAnswer().then(createdDescription).catch(errorHandler);
      }
    }).catch(errorHandler);
  } else if(signal.ice) {
    peerConnection.addIceCandidate(new RTCIceCandidate(signal.ice)).catch(errorHandler);
  }
}

function gotIceCandidate(event) {
  if(event.candidate != null) {
    serverConnection.send(JSON.stringify({
		'ice': event.candidate, 
		'uuid': uuid
		}));
  }
}

window.onload = function() {
	start(initCall);
};

function createdDescription(description) {
  console.log('got description');

  peerConnection.setLocalDescription(description).then(function() {
    serverConnection.send(JSON.stringify({
		'sdp': peerConnection.localDescription, 
		'uuid': uuid
}));
  }).catch(errorHandler);
}

function gotRemoteStream(event) {
  console.log('got remote stream');
  remoteVideo.srcObject = event.streams[0];
}

function errorHandler(error) {
  console.log(error);
}


// Taken from http://stackoverflow.com/a/105074/515584
// Strictly speaking, it's not a real UUID, but it gets the job done here
function createUUID() {
  function s4() {
    return Math.floor((1 + Math.random()) * 0x10000).toString(16).substring(1);
  }
  return s4() + s4() + '-' + s4() + '-' + s4() + '-' + s4() + '-' + s4() + s4() + s4();
}


