const functions = require('firebase-functions');
var geolib = require('geolib');

// The Firebase Admin SDK to access the Firebase Realtime Database.
const admin = require('firebase-admin');
admin.initializeApp();

const settings = {timestampsInSnapshots: true};
admin.firestore().settings(settings);

var db = admin.firestore();

exports.NotifyUsers = functions.firestore
    .document('Alerts/{alertID}')
    .onCreate((snap, context) => {
      console.log('I spy with my little eye an alert');
    // Get an object representing the document
    // Alert has int, geolocation, and timestamp data types
    const newAlert = snap.data();
    const petID = newAlert.conanID;
    const alertLocation = newAlert.location;
    const alertTime = newAlert.time;
    var otherTime= newAlert.time._seconds;
    
    const MAX_DIST = 75; //make this a unit of distance, somewhat arbitrary, but 75m should accomodate lack of precision for now, could later be determined by user
    var isSafe = true;//variable for pet in safety time threshold
    var safeLoc = false;//variable for if pet is in safe/home location
    var petRefDoc;//come back to this
    
    var petCollectionReference = db.collection('Pets');
    var query = petCollectionReference.where('conanID', '==', petID);
    query.get().then((querySnapshot) => {
        if (querySnapshot.size >0){
            
            return console.log(querySnapshot.docs[0].data());
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
            } else {
                return console.log("Pet not in DB");
            }
        })
                
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
                                    command: 'notify',//identifies type of action for client to take
                                    conanID: pet.conanID,
                                    pName: pet.pName
                                },
                                topic: pet.conanID
                            };
                            return admin.messaging().send(message);
                        }
                    //Pet is safe do other use case logic here
                    //just return log for now
                    else if (pet.status === 0){
                        //will use and modify isSafe and safeLoc values to evaluate and update status of pet
                        //check if within safety threshold
                        const FIVE_MIN = 5 * 60;// do not use 1000 since values for comparison already in seconds, may move this later
                        //may need to change values as timestamp data type can pull out seconds with ._seconds
                        var safety = pet.lastSafe;
                        const safeTimeDiffSecs = alertTime._seconds - safety._seconds;
                        console.log("Difference between now and when pet was last safe in seconds: "+safeTimeDiffSecs);
                        if (safeTimeDiffSecs > FIVE_MIN){
                            console.log("Pet outside of safety threshold time");
                            isSafe = false;
                        }
                        //checking if pet is safe, can restructure to do this before safety threshold to reduce unnecessary call since inside safe distance will always update safety time regardless of current threshold.
                        var owners = pet.owners;
                        //for (owner in owners){//may want error handling and checking existence
                        var ownerList =[];
                        for (i=0; i<owners.length;i++){
                            var ownerRef = db.collection('Users').doc(owners[i]);
                            //console.log(`OwnerRef Stringified? ${JSON.stringify(ownerRef)}`);
                            ownerList.push(ownerRef);
                            //functions should not be created in loops, so promises should be handled properly, possibly with firestore.getAll(<array of docreferences>)
                        }
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
                                    ownerDat = docs[i].data();
                                    //may be able to send these locations straight to library, test this
                                    var pLocation = ownerDat.p_address_geo;
                                    var sLocation = ownerDat.s_address_geo;
                                    //if within range, pet gets marked in safe location
                                    //distance is returned as a float in METERS
                                    var pLocLat = pLocation.latitude;
                                    var pLocLong = pLocation.longitude;
                                    var pStart = {latitude: pLocLat, longitude: pLocLong};
                                    var alertLat = alertLocation.latitude;
                                    var alertLong = alertLocation.longitude;
                                    var end = {latitude: alertLat, longitude: alertLong}
                                    var pDist = geolib.getDistance(pStart, end, 1, 1);
                                    console.log("Distance calculated is: "+pDist);
                                    if (pDist<=MAX_DIST){
                                        console.log('Distance between coordinates is less than maximum safe distance from approved primary location, pet likely at safe location');
                                        safeLoc =true;
                                    }
                                    else if (sLocation !== null){
                                        var sLocLat = sLocation.latitude;
                                        var sLocLong = sLocation.longitude;
                                        var sStart = {latitude: sLocLat, longitude: sLocLong};
                                        var sDist = geolib.getDistance(sStart, end, 1, 1);
                                        if (sDist<=MAX_DIST){
                                            console.log('Distance between coordinates is less than maximum safe distance from approved secondary location, pet likely at safe location');
                                            safeLoc=true;
                                        }
                                    }
                                }
                            }
 
                        //logic to handle different cases based on isSafe and safeLoc values
                        
                        //Pet is estimated to be at an owner's home, updates threshold
                        console.log('Pet currently is in threshold: '+isSafe);
                        console.log('Pet currently at safe location: '+safeLoc);
                        if (safeLoc){
                            console.log('Pet safety threshold should be being updated now since pet is near safeLoc');
                            var petReference = db.collection('Pets').doc(petRefDoc);//may be able to set this higher up
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
                                topic: petID //const petID as determined as above
                            };
                            return admin.messaging().send(message);
                        }
                        else if (!isSafe){
                            //mark pet is lost automcatically, lost staus is 1
                            console.log('Pet is neither at an approved location or within their safety threshold, mark pet as lost now');
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
    

