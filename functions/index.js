const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp();

exports.newReview = functions.firestore.document('reviews/{randomId}').onWrite((change, context) => {

    const serviceUid = change.after.data().serviceUid;
    const type = change.after.data().type;
    const reviewScore = change.after.data().reviewScore; //Score from 1 to 5 of the review
    var serviceRef;

    if (type === 'expert_instructor')
        serviceRef = serviceRef = admin.firestore().collection('employees').doc(serviceUid);
    else if (type === 'rental')
        serviceRef = serviceRef = admin.firestore().collection('employees').doc(serviceUid);
    else if (type === 'spot')
        serviceRef = serviceRef = admin.firestore().collection('spots').doc(serviceUid);


    return admin.firestore().runTransaction(transaction => {
        return transaction.get(serviceRef).then(serviceDoc => {

            //New number of reviews, if the document already existed it's a change from before so the total stays the same
            var newNumReviews = change.before.exists ? serviceDoc.data().numReviews : serviceDoc.data().numReviews + 1;
            //If it's only an update to a review the old value must be subtracted before adding the new one
            var removeOld = change.before.exists ? change.before.data().reviewScore : 0;

            //Compute the old total and remove the previous score from it if it's an update instead of an insert
            var oldReviewTotal = (serviceDoc.data().averageReviews * serviceDoc.data().numReviews) - removeOld;
            //Compute the new average value for the reviews
            var newAvgReview = (oldReviewTotal + reviewScore) / newNumReviews;

            //Update the document
            return transaction.update(serviceRef, {
                averageReviews: newAvgReview,
                numReviews: newNumReviews
            });
        });
    });
});