const functions = require('firebase-functions');
var geolib = require('geolib');

// The Firebase Admin SDK to access the Firebase Realtime Database.
const admin = require('firebase-admin');
admin.initializeApp();

const settings = {timestampsInSnapshots: true};
admin.firestore().settings(settings);

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
    console.log('Snapbefore data assignment: '+snap);
    console.log('Snapshot with .data() method '+ snap.data());
    console.log('Snapshot contains: '+newAlert);
    const petID = newAlert.conanID;
    const alertLocation = newAlert.location;
    console.log('alertLocation is '+alertLocation);
    console.log('alertLocation latitude is '+alertLocation.latitude);
    //const alertTime = newAlert.time.getTime();
    const alertTime = newAlert.time;
    console.log('Time pulled in without toString()is: '+alertTime);
    console.log('Time pulled in is: '+alertTime.toString());
    var otherTime= newAlert.time._seconds;
    console.log('attempt to use method of timestamp '+otherTime);
    
    const MAX_DIST = 75; //make this a unit of distance, somewhat arbitrary, but 75m should accomodate lack of precision for now, could later be determined by user
    var isSafe = true;//variable for pet in safety time threshold
    var safeLoc = false;//variable for if pet is in safe/home location
    var petRefDoc;
    
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
                    console.log("Pet ref after callback: "+petRef);
                    console.log("Document ID for snapshot is "+petRef.id);
                    petRefDoc = petRef.id;
                    var pet = petRef.data();
                        if (pet.status === 1){
                            console.log("pet is lost!");
                            const message = {
                                data: {
                                    command: 'notify',
                                    //command: 'scan',
                                    //above can be used for Travis to test response to scan easily
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
                        //will use and modify isSafe and safeLoc values to evaluate and update status of pet
                        //isSafe = true;//variable for pet in safety time threshold
                        //safeLoc = false;//variable for if pet is in safe/home location
                        //check if within safety threshold
                        //var location;
                        const FIVE_MIN = 5 * 60;// do not 1000 since values for comparison already in seconds, may move this later
                        //may need to change values as timestamp data type can pull out seconds with ._seconds
                        //var safety = pet.lastSafe.getTime();
                        var safety = pet.lastSafe;//not sure if this compares in seconds or milliseconds
                        const safeTimeDiff = alertTime-safety;
                        const safeTimeDiffSecs = alertTime._seconds - safety._seconds;
                        console.log("Difference between now and when pet was last safe is: "+safeTimeDiff);
                        console.log("Difference between now and when pet was last safe in seconds: "+safeTimeDiffSecs);
                        if (safeTimeDiffSecs > FIVE_MIN){
                            console.log("Pet outside of safety threshold time");
                            isSafe = false;
                        }
                        //checking if pet is safe, can restructure to do this before safety threshold to reduce unnecessary call since inside safe distance will always update safety time regardless of current threshold.
                        var owners = pet.owners;
                        var ownerTest = owners[0];
                        console.log('Owners: ',owners);
                        console.log('Owner Test: ', ownerTest);
                        //var owner;//Is this required?
                        //var safeLocs
                        //var i=0;
                        //for (owner in owners){//may want error handling and checking existence
                        var ownerList =[];
                        console.log('OwnerList right after creation: '+ownerList);
                        for (i=0; i<owners.length;i++){
                            var ownerRef = db.collection('Users').doc(owners[0]);
                            console.log('Inside for loop!');
                            console.log('OwnerRef: ',ownerRef);
                            ownerList.push(ownerRef);
                            //functions should not be created in loops, so promises should be handled properly, possibly with firestore.getAll(<array of docreferences>)
                        }
                        console.log('ownerList array first element: '+ownerList[0]);
                        return ownerList;
                    }
        return console.log("Unknown issue, pet status not received by callback");
    })
    .then(ownerList =>{
                        return db.getAll(...ownerList)})//spread syntax to provide 'individual' arguments to method
                        .then(docs => {
                            for (i=0; i<docs.length;i++){
                                if(!docs[i].exists){
                                    console.log('No such document!');
                                }
                                else{
                                    console.log(`First document: ${JSON.stringify(docs[i])}`);
                                    //could get geolocation data from here with doc data, could push to another array if need be or adjust here?
                                    //console.log('Owner Data: '+ownerDat);
                                    //test value, Grant's home: Latitude: 33.602766 | Longitude: -101.932066  
                                    //May not want const because value could change if there are multiple owners
                                    ownerDat = docs[i].data();
                                    var location = ownerDat.geoLocation;
                                    console.log("Owner Location at: "+location.latitude +", "+location.longitude);
                                    /*
                                    //if within range, pet gets marked in safe location.*/
                                    //distance is returned as a float in METERS
                                    //`{latitude: 52.518611, longitude: 13.408056}` object example
                                    var locLat = location.latitude;
                                    var locLong = location.longitude;
                                    var start = {latitude: locLat, longitude: locLong};
                                    var alertLat = alertLocation.latitude;
                                    var alertLong = alertLocation.longitude;
                                    var end = {latitude: alertLat, longitude: alertLong}
                                    //var dist = geolib.getDistance({'latitude:' locLat, 'longitude:' locLong},
                                                         //{'latitude:' alertLat, 'longitude:' alertLong});
                                    var dist = geolib.getDistance(start, end, 1, 1);
                                    console.log("Distance calculated is: "+dist);
                                    if (dist<=MAX_DIST){
                                        console.log('Distance between coordinates is less than maximum safe distance from approved location, pet likely at safe location');
                                        safeLoc =true;
                                    }
                                }
                            }
                            //return safeLoc;
                            /*var ownerDoc = ownerRef.get()
                            .then(doc => {
                                if (!doc.exists){
                                    console.log('No such document!');
                                }
                                else{
                                    //console.log('OwnerDoc: ',ownerDoc);
                                    var ownerDat = doc.data();
                                }
                            })
                            .catch(err => {
                                console.log('Error getting document: ',err);
                            });*/
                                    //console.log('Current Owner: '+owners[i]);
                                    //console.log('Owner Data: '+ownerDat);
                                    //test value, Grant's home: Latitude: 33.602766 | Longitude: -101.932066  
                                    //May not want const because value could change if there are multiple owners
                                    //const location = ownerDat.geoLocation;
                                    //console.log("Owner Location at: "+location.latitude +", "+location.longitude);
                                    /*
                                    //if within range, pet gets marked in safe location.*/
                                    //distance is returned as a float in METERS
                                    /*var locLat = location.latitude;
                                    var locLong = location.longitude;
                                    var alertLat = alertLocation.latitude;
                                    var alertLong = alertLocation.longitude;
                                    var dist = geolib.getDistance({locLat, locLong},
                                                         {alertLat, alertLong});
                                    if (dist<=MAX_DIST){
                                        console.log('Distance between coordinates is less than maximum safe distance from approved location, pet likely at safe location');
                                        safeLoc =true;
                                    }*/
                        //}
                            //safeLocs[i] = docData.geoLocation;
                            //i++;
                                
                            
                        //logic to handle different cases based on isSafe and safeLoc values
                        
                        //Pet is estimated to be at an owner's home, updates threshold
                        console.log('Pet currently is in threshold: '+isSafe);
                        console.log('Pet currently at safe location: '+safeLoc);
                        if (safeLoc){
                            console.log('Pet safety threshold should be being updated now since pet is near safeLoc');
                            var petReference = db.collection('Pets').doc(petRefDoc);//may be able to set this higher up
                            //admin.firestore.Timestamp.fromDate(new Date()) to set timestamp -> db.Timestamp.fromDate(new Date()) may also work
                            return updateSafety = petReference.update({lastSafe: new Date()});
                        }
                        //pet is in safety threshold but not location
                        //needs to create a data message to deliver
                        else if (isSafe){
                            console.log('Pet not at approved location, but may still be safe, prompt owner scans now');
                            const message = {
                                data: {
                                    command: 'scan'
                                },
                                topic: pet.conanID
                            };
                            return admin.messaging().send(message);
                        }
                        else if (!isSafe){
                            //mark pet is lost automcatically, lost staus is 1
                            console.log('Pet is neither at an approved location or within their safety threshold, mark pet as lost now');
                            //return updateStatus = petRef.update({status: 1});
                            petReference = db.collection('Pets').doc(petRefDoc);
                            return updateStatus = petReference.update({status: 1});
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
