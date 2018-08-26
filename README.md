# WaferMessengerChallenge

First of all I added recycler-view dependencies to my gradle file (app). I made use of Frame-Layout as the root layout in my activity-main file, so that my layout page shows only a particular view. The Frame Layout child views are: recycler view, progress bar and a text view.
The recycler view is to show a list of countries and there details.
The progress bar is to show a loading indicator while the countries information are fetched from the server.
The Text view is to display an error message in case of a bad internet connection.

The layout for the list-view which is “country_list_layout” contains three text view: country name, currency name, language name and an image button which is not visible by default. The image button is to display the bomb image at the background of the view-holder.

In my main-activity I made use of an async task Loader in Loader Manager to load data on a background thread and also to prevent duplicate loads form happening in parallel.
I also have a helper class named “NetworkUtils” in utilities packages, where my url is parsed and also a helper method which makes use of HttpURLConnection, InputStream and Scanner classes to read data passed from the server. When the async task loader finish loading the data at the background, it is then passed to the adapter class where it binds the data fetched to the view-holder for the list-view.

 In my main-activity class, I attached the adapter to the recycler-view to showcase the data on the recycler-view(list view). Also in my main-activity I added an item_touch_helper to the recycler-view to recognize when a user swipes to delete an item.

In my item_touch_helper class I made use of three method and they are: swiped method, getSwipeDirs method, clearView method and onChildDraw method.

Swiped method is called when a user swipes left on a ViewHolder. This is to delete the particular view-holder items that is active.

GetSwipeDirs method disable the swipe action of a view-holder when the bomb icon shows after swiping passed the anchor point.

ClearView method is user to displays bomb image button when the view-holder
returns to its origin position.

OnChildDraw method is used to display background color and image on swipe. It is also used to set the anchor point on swiping the view-holder.

In my adapter class I set the bomb image button to delete the view-holder items when clicked on. Lastly in my main-activity I set an on_click_listener to clear the bomb image button on the list-view. That is to cancel the current swipe when another view-holder is clicked on.



