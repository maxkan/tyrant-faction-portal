Steps to get it deployed to Google App Engine:
* Download Eclipse and Google plugin for it following this instruction:  https://developers.google.com/appengine/docs/java/tools/eclipse
Make sure you install non-conflicting versions of Eclipse/Plugin:
Eclipse Indigo only seems to work successfully with v3.7 of the GWT Plugin.

* Import project into Eclipse: File->Import->General->Existing project into Eclipse

* Fill your profile data in UserData class"
How to get your profile data(Chrome): CTRL+SHIFT+I. click on 'Network' tab. 
Load Tyrant. Find "api.php" request and press "headers" tab. You'll find authToken, flashcode and userId there.

* Configure frequency of update in cron.xml: background job will constantly update your history. you can configure how often will it do that.

You can test it locally first. Run it: run as...-> web application. You'll have no data at first.
Run your background job manually to get wars data: in the browser: [url]/factionportal/cron
At the first time it can take a lot of time to load your wars history so be patient and check the Console to see the progress.

Then create google app account, add application at Google and configure Eclipse to deploy FactionPortal there:
[right click on a project]->properties->google->app engine->application ID

*Deploy the app to Google using Eclipse toolbar button: (g)->Deploy to app engine.
*Wait for the first run of background job
*...
*Profit!