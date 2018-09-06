# LiveSocketView
/////////////////////////  **PROJECT OVERVIEW** ////////////////////////
**/STEP 1/** As soon as Main Activity is created a ViewModel class is intantiated using ViewModelProviders class.
**/STEP 2/** ViewModel's constructor contains code to make a call to RestApi, initialize the socket connection with the given namespace for server using Socket.io library, also it creates two LiveData objects, i.e. (one for RestApi response, other is for socket connection live response).
**/STEP 3/** Inside onStart() of our MainActivity we have attached observers for both the LiveData created in ViewModel and have removed the listeners in onStop().
**/STEP 4/** Inside onStart() we ask ViewModel to establish connection with socket by subsribing to it and then we attach socket listeners for the response which updates the LiveData Objects whenever there is new data available via socket connection. This updating of LiveData triggers the observers present in our View(activity) and the layout will be updated.
**/STEP 5/** Inside onStop() we ask ViewModel to remove the socket listeners and unsubscribe from the live connection.
**/STEP 6/** This way we have implemented Android Architecture Components with Socket.io.......
/**NOTE**/ We have added a delay of 5 seconds before sending acknowledgement to server for the recieved data, this is done to reduce the ratio of "data updates/time"
