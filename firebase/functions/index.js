const functions = require('firebase-functions');
var geolib = require('geolib');
//This is version 2, attempting to make use of promises

// The Firebase Admin SDK to access the Firebase Realtime Database.
const admin = require('firebase-admin');
admin.initializeApp();

var db = admin.firestore();
var db1 = admin.database();

exports.NotifyUsers = functions.firestore
    .document('Alerts/{alertID}')
    .onCreate((snap, context) => {
      console.log('I spy with my little eye an alert');
    // Get an object representing the document
      // e.g. {'conanID': '0x4a72b2b79943', 'location': [33.58741368N, -101.87611725W], 'time': November 7, 2018 at 7:37:22 PM UTC-6}
      // Alert has int, geolocation, and timestamp data types
    const newAlert = snap.data();
    const petID = newAlert.conanID;
    const alertLocation = newAlert.location;
    constf alertTime = newAlert.time.getTime();
    
    console.log('Pet ID pulled from alert is '+petID);
    
    var petCollectionReference = db.collection('Pets');
    var query = petCollectionReference.where('conanID', '==', petID);
    query.get().then((querySnapshot) => {
        if (querySnapshot.size >0){
            //do something here
            return console.log(querySnapshot.docs[0].data());
            /*querySnapshot.forEach(function (documentSnapshot) {
                var data = documentSnapshot.data();
                //do something with data
            });*/
        } else {
            return console.log('no documents found');
        }
    })
    .catch((error) => {
        console.log("Error getting document: ", error);
    });
    
    
    return db.collection('Pets').where('conanID', "==", petID)
        .get()
        .then((petSnapshot) => {
            if (petSnapshot.size >0){
                console.log("Pet exists in DB");
                return petSnapshot.docs[0];//this should return the document reference, not just the data
                //return petSnapshot.docs[0].data();
            } else {
                return console.log("Pet not in DB");
            }
        })
                /*pets.forEach((doc) => {
                    console.log(doc.id, " => ", doc.data());
                    const petStatus = pets.data().status;
                    const conan = pets.data().conanID;
                    const petName = pets.data().pName;
                });
                return Promise.all([petStatus, conan, petName])*/
                    .then((petRef) => {
                    //check if pet is lost, safe:0, lost:1
                    var pet = petRef.data();
                        if (pet.status === 1){
                            const message = {
                                data: {
                                    action: 'notify'
                                    conanID: pet.conanID,
                                    pName: pet.pName
                                },
                                topic: pet.conanID
                            };
                            return admin.messaging().send(message);
                                /*.then((response) => {
                                return console.log('Successfully sent message: '+response);
                            });*/
                        }
                    //Pet is safe do other use case logic here
                    //just return log for now
                    else if (pet.status === 0){
                        var isSafe = true;//variable for pet in safety time threshold
                        var safeLoc = false;//variable for if pet is in safe/home location
                        //check if within safety threshold
                        const FIVE_MIN = 5 * 60 * 1000; //may move this later
                        var safety = pet.pLastSafe.getTime();
                        if (alertTime-safety > FIVE_MIN){
                            isSafe = false;
                        }
                        //checking if pet is safe, can restructure to do this before safety threshold to reduce unnecessary call since inside safe distance will always update safety time regardless of current threshold.
                        const MAX_DIST = 100 //make this a unit of distance, arbitrary for now
                        var owners = pet.Owners;
                        var owner;
                        //var safeLocs
                        //var i=0;
                        for (owner in owners){//may want error handling and checking existence
                            var ownerRef = db.collection('Users').doc(owner);
                            var ownerDoc = ownerRef.get()
                            .then((doc) =>  {
                                  return doc.data()
                                  })
                            .then((docData) => {//The below method does not work if geopoint.js is available, not built in datatype?
                                 safeLoc = docData.geoLocation;/*
                                 var dist = safeLoc.milesTo(alertLocation)*5280;//gets miles between and converts to feet
                                //if within range, pet gets marked in safe location.*/
                                //distance is returned as a float in METERS
                                dist = geolib.getDistance({safeLoc.latitude, safeLoc.longitude},
                                                         {alertLocation.latitude, alertLocation.longitude});
                                if (dist<=MAX_DIST){ 
                                    safeLoc =true;
                                
                                }
                                //safeLocs[i] = docData.geoLocation;
                                //i++;
                            });
                        }
                        //logic to handle different cases based on isSafe and safeLoc values
                        
                        //Pet is estimated to be at an owner's home, updates threshold
                        if (safeLoc){
                            return updateSafety = petRef.update({pLastSafe: firebase.firestore.Timestamp.now()});
                        }
                        //pet is in safety threshold but not location
                        //needs to create a data message to deliver
                        else if (isSafe){
                            const message = {
                                data: {
                                    action: 'scan'
                                },
                                topic: pet.conanID
                            };
                            return admin.messaging().send(message);
                        }
                        else if (!isSafe){
                            return updateStatus = petRef.update({status: 1});
                        }
                                            }
                    return console.log('Pet '+pet.pName+' is safe!');
                    })
        .then((response) => {
        return console.log('Sent message: '+ response);
    })
        .catch(error => {
            console.log('error', error);
    });
    });
    
/*function distance(lat1, lon1, lat2, lon2) {
  var p = 0.017453292519943295;    // Math.PI / 180
  var c = Math.cos;
  var a = 0.5 - c((lat2 - lat1) * p)/2 + 
          c(lat1 * p) * c(lat2 * p) * 
          (1 - c((lon2 - lon1) * p))/2;

  return 12742 * Math.asin(Math.sqrt(a)); // 2 * R; R = 6371 km
}*/
