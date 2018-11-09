const functions = require('firebase-functions');
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
                return petSnapshot.docs[0].data();
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
                    .then((pet) => {
                    //check if pet is lost, safe:0, lost:1
                        if (pet.status === 1){
                            const message = {
                                data: {
                                    conanID: pet.conanID,
                                    pName: pet.pName
                                },
                                topic: pet.conanID
                            };
                            return admin.messaging().send(message)
                                .then((response) => {
                                return console.log('Successfully sent message: '+response);
                            });
                        }
                    //Pet is safe do other use case logic here
                    //just return log for now
                    return console.log('Pet '+pet.pName+' is safe!');
                    })
        .catch(error => {
            console.log('error', error);
    });
    });
