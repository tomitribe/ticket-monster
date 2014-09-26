# TicketMonster - a JBoss example

TicketMonster is an online ticketing demo application that gets you started with JBoss technologies, and helps you learn and evaluate them.

Here are a few instructions for building and running it. You can learn more about the example from the [tutorial](http://www.jboss.org/ticket-monster).

## Updating the Performance dates

_NOTE: This step is optional. It is necessary only if you want to update the dates of the Performances in the `import.sql` script in an automated manner. Updating the performance dates ensure that they are always set to some timestamp in the future, and ensures that all performances are visible in the Monitor section of the TicketMonster application._

1. Run the `update_import_sql` Perl script. You'll need the `DateTime`, `DateTime::Format::Strptime` and `Tie::File` Perl modules. These are usually available by default in your Perl installation.
    
        $ perl update_import_sql.pl src/main/resources/import.sql

## Generating the administration site

_NOTE: This step is optional. The administration site is already present in the source code. If you want to regenerate it from Forge, and apply the changes outlined in the tutorial, you may continue to follow the steps outlined here. Otherwise, you can skip this step and proceed to build TicketMonster._

Before building and running TicketMonster, you must generate the administration site with Forge.

1. Ensure that you have [JBoss Forge](http://jboss.org/forge) installed. The current version of TicketMonster supports version 2.6.0.Final or higher of JBoss Forge. JBoss Developer Studio 8 is recommended, since it contains JBoss Forge 2 with all the necessary plugins for the TicketMonster app.

2. Start the JBoss Forge console in JBoss Developer Studio. This can be done from the Forge Console view. If the view is not already visible, it can be opened through the 'Window' menu: _Window_ -> _Show View_ -> _Other..._. Select the 'Forge Console' item in the dialog to open the Forge Console. Click the _Start_ button in the Forge Console tab, to start Forge. 

3. From the JBoss Forge prompt, browse to the 'demo' directory of the TicketMonster sources and execute the script for generating the administration site
    
	    $ cd ticket-monster/demo
	    $ run admin_layer.fsh

    The git patches need to be applied manually. Both the patches are located in the patches sub-directory. To apply the manual changes, first apply the patch located in file _admin_layer_functional.patch_. Then perform the same for the file _admin_layer_graphics.patch_ if you want to apply the style changes for the generated administration site. You can do so in JBoss Developer Studio, by opening the context-menu on the project (Right-click on the project) and then apply a git patch via _Team_ -> _Apply Patch..._. Locate the patch file in the Workspace, select it and click the 'Next' button. In the next dialog, select to apply the patch on the 'ticket-monster' project in the workspace. Click Finish in the final page of the wizard after satisfying that the patch applies cleanly.

4. Deployment to JBoss EAP 6.2 is optional. The project can be built and deployed to a running instance of JBoss EAP through the following command in JBoss Forge:

	    $ build clean package jboss-as:deploy

## Building TicketMonster

TicketMonster can be built from Maven, by runnning the following Maven command:

    mvn clean package
	
### Building TicketMonster with tests
	
If you want to run the Arquillian tests as part of the build, you can enable one of the two available Arquillian profiles.

For running the tests in an _already running_ application server instance, use the `arq-jbossas-remote` profile.

    mvn clean package -Parq-jbossas-remote

If you want the test runner to _start_ an application server instance, use the `arq-jbossas-managed` profile. You must set up the `JBOSS_HOME` property to point to the server location, or update the `src/main/test/resources/arquillian.xml` file.

    mvn clean package -Parq-jbossas-managed
	
### Building TicketMonster with Postgresql (for OpenShift)

If you intend to deploy into [OpenShift](http://openshift.com), you can use the `postgresql-openshift` profile

    mvn clean package -Ppostgresql-openshift
	
## Running TicketMonster

You can run TicketMonster into a local JBoss EAP 6.2 instance or on OpenShift.

### Running TicketMonster locally

#### Start JBoss Enterprise Application Platform 6.2

1. Open a command line and navigate to the root of the JBoss server directory.
2. The following shows the command line to start the server with the web profile:

        For Linux:   JBOSS_HOME/bin/standalone.sh
        For Windows: JBOSS_HOME\bin\standalone.bat
		
#### Deploy TicketMonster

1. Make sure you have started the JBoss Server as described above.
2. Type this command to build and deploy the archive into a running server instance.

        mvn clean package jboss-as:deploy
	
	(You can use the `arq-jbossas-remote` profile for running tests as well)

3. This will deploy `target/ticket-monster.war` to the running instance of the server.
4. Now you can see the application running at `http://localhost:8080/ticket-monster`

### Running TicketMonster in OpenShift

#### Create an OpenShift project

1. Make sure that you have an OpenShift domain and you have created an application using the `jbosseap-6` cartridge (for more details, get started [here](https://openshift.redhat.com/app/getting_started)). If you want to use PostgreSQL, add the `postgresql-8.4` cartridge too.
2. Ensure that the Git repository of the project is checked out.

#### Building and deploying

1. Build TicketMonster using either: 
    * the default profile (with H2 database support)
    
            mvn clean package	
    * the `postgresql-openshift` profile (with PostgreSQL support) if the PostgreSQL cartrdige is enabled in OpenShift.
            
            mvn clean package -Ppostgresql-openshift
			
2. Copy the `target/ticket-monster.war`file in the OpenShift Git repository(located at `<root-of-openshift-application-git-repository>`).

	    cp target/ticket-monster.war <root-of-openshift-application-git-repository>/deployments/ROOT.war

3. Navigate to `<root-of-openshift-application-git-repository>` folder
4. Remove the existing `src` folder and `pom.xml` file. 

        git rm -r src
		git rm pom.xml

5. Add the copied file to the repository, commit and push to Openshift
        
		git add deployments/ROOT.war
		git commit -m "Deploy TicketMonster"
		git push

6. Now you can see the application running at `http://<app-name>-<domain-name>.rhcloud.com`

_NOTE: this version of TicketMonster uses the *binary* deployment style._ 


# Application Stats

Contains 19 REST Service classes:

    INFO -      Service URI: http://localhost:8080/ticket-monster/rest/bookings                                      ->  EJB org.jboss.jdf.example.ticketmonster.rest.BookingService
    INFO -      Service URI: http://localhost:8080/ticket-monster/rest/eventcategories                               ->  EJB org.jboss.jdf.example.ticketmonster.rest.EventCategoryEndpoint
    INFO -      Service URI: http://localhost:8080/ticket-monster/rest/events                                        ->  EJB org.jboss.jdf.example.ticketmonster.rest.EventService
    INFO -      Service URI: http://localhost:8080/ticket-monster/rest/forge/bookings                                ->  EJB org.jboss.jdf.example.ticketmonster.rest.BookingEndpoint
    INFO -      Service URI: http://localhost:8080/ticket-monster/rest/forge/events                                  ->  EJB org.jboss.jdf.example.ticketmonster.rest.EventEndpoint
    INFO -      Service URI: http://localhost:8080/ticket-monster/rest/forge/shows                                   ->  EJB org.jboss.jdf.example.ticketmonster.rest.ShowEndpoint
    INFO -      Service URI: http://localhost:8080/ticket-monster/rest/forge/venues                                  ->  EJB org.jboss.jdf.example.ticketmonster.rest.VenueEndpoint
    INFO -      Service URI: http://localhost:8080/ticket-monster/rest/mediaitems                                    ->  EJB org.jboss.jdf.example.ticketmonster.rest.MediaItemEndpoint
    INFO -      Service URI: http://localhost:8080/ticket-monster/rest/metrics                                       ->  EJB org.jboss.jdf.example.ticketmonster.rest.MetricsService
    INFO -      Service URI: http://localhost:8080/ticket-monster/rest/performances                                  ->  EJB org.jboss.jdf.example.ticketmonster.rest.PerformanceEndpoint
    INFO -      Service URI: http://localhost:8080/ticket-monster/rest/sectionallocations                            ->  EJB org.jboss.jdf.example.ticketmonster.rest.SectionAllocationEndpoint
    INFO -      Service URI: http://localhost:8080/ticket-monster/rest/sections                                      ->  EJB org.jboss.jdf.example.ticketmonster.rest.SectionEndpoint
    INFO -      Service URI: http://localhost:8080/ticket-monster/rest/shows                                         ->  EJB org.jboss.jdf.example.ticketmonster.rest.ShowService
    INFO -      Service URI: http://localhost:8080/ticket-monster/rest/ticketcategories                              ->  EJB org.jboss.jdf.example.ticketmonster.rest.TicketCategoryEndpoint
    INFO -      Service URI: http://localhost:8080/ticket-monster/rest/ticketprices                                  ->  EJB org.jboss.jdf.example.ticketmonster.rest.TicketPriceEndpoint
    INFO -      Service URI: http://localhost:8080/ticket-monster/rest/tickets                                       ->  EJB org.jboss.jdf.example.ticketmonster.rest.TicketEndpoint
    INFO -      Service URI: http://localhost:8080/ticket-monster/rest/venues                                        ->  EJB org.jboss.jdf.example.ticketmonster.rest.VenueService
    INFO -      Service URI: http://localhost:8080/ticket-monster/rest/bot                                           -> Pojo org.jboss.jdf.example.ticketmonster.rest.BotStatusService
    INFO -      Service URI: http://localhost:8080/ticket-monster/rest/media                                         -> Pojo org.jboss.jdf.example.ticketmonster.rest.MediaService


With a Total of 82 REST operations:

    INFO -            DELETE http://localhost:8080/ticket-monster/rest/bookings/                                     ->      Response deleteAllBookings()
    INFO -            DELETE http://localhost:8080/ticket-monster/rest/bookings/{id:[0-9][0-9]*}                     ->      Response deleteBooking(Long)
    INFO -               GET http://localhost:8080/ticket-monster/rest/bookings/                                     ->      List<T> getAll(UriInfo)
    INFO -               GET http://localhost:8080/ticket-monster/rest/bookings/count                                ->      Map<String, Long> getCount(UriInfo)
    INFO -               GET http://localhost:8080/ticket-monster/rest/bookings/{id:[0-9][0-9]*}                     ->      T getSingleInstance(Long)
    INFO -              POST http://localhost:8080/ticket-monster/rest/bookings/                                     ->      Response createBooking(BookingRequest)
    INFO -            DELETE http://localhost:8080/ticket-monster/rest/eventcategories/{id:[0-9][0-9]*}              ->      Response deleteById(Long)
    INFO -               GET http://localhost:8080/ticket-monster/rest/eventcategories/                              ->      List<EventCategoryDTO> listAll(Integer, Integer)
    INFO -               GET http://localhost:8080/ticket-monster/rest/eventcategories/{id:[0-9][0-9]*}              ->      Response findById(Long)
    INFO -              POST http://localhost:8080/ticket-monster/rest/eventcategories/                              ->      Response create(EventCategoryDTO)
    INFO -               PUT http://localhost:8080/ticket-monster/rest/eventcategories/{id:[0-9][0-9]*}              ->      Response update(Long, EventCategoryDTO)
    INFO -               GET http://localhost:8080/ticket-monster/rest/events/                                       ->      List<T> getAll(UriInfo)
    INFO -               GET http://localhost:8080/ticket-monster/rest/events/count                                  ->      Map<String, Long> getCount(UriInfo)
    INFO -               GET http://localhost:8080/ticket-monster/rest/events/{id:[0-9][0-9]*}                       ->      T getSingleInstance(Long)
    INFO -            DELETE http://localhost:8080/ticket-monster/rest/forge/bookings/{id:[0-9][0-9]*}               ->      Response deleteById(Long)
    INFO -               GET http://localhost:8080/ticket-monster/rest/forge/bookings/                               ->      List<BookingDTO> listAll(Integer, Integer)
    INFO -               GET http://localhost:8080/ticket-monster/rest/forge/bookings/{id:[0-9][0-9]*}               ->      Response findById(Long)
    INFO -              POST http://localhost:8080/ticket-monster/rest/forge/bookings/                               ->      Response create(BookingDTO)
    INFO -               PUT http://localhost:8080/ticket-monster/rest/forge/bookings/{id:[0-9][0-9]*}               ->      Response update(Long, BookingDTO)
    INFO -            DELETE http://localhost:8080/ticket-monster/rest/forge/events/{id:[0-9][0-9]*}                 ->      Response deleteById(Long)
    INFO -               GET http://localhost:8080/ticket-monster/rest/forge/events/                                 ->      List<EventDTO> listAll(Integer, Integer)
    INFO -               GET http://localhost:8080/ticket-monster/rest/forge/events/{id:[0-9][0-9]*}                 ->      Response findById(Long)
    INFO -              POST http://localhost:8080/ticket-monster/rest/forge/events/                                 ->      Response create(EventDTO)
    INFO -               PUT http://localhost:8080/ticket-monster/rest/forge/events/{id:[0-9][0-9]*}                 ->      Response update(Long, EventDTO)
    INFO -            DELETE http://localhost:8080/ticket-monster/rest/forge/shows/{id:[0-9][0-9]*}                  ->      Response deleteById(Long)
    INFO -               GET http://localhost:8080/ticket-monster/rest/forge/shows/                                  ->      List<ShowDTO> listAll(Integer, Integer)
    INFO -               GET http://localhost:8080/ticket-monster/rest/forge/shows/{id:[0-9][0-9]*}                  ->      Response findById(Long)
    INFO -              POST http://localhost:8080/ticket-monster/rest/forge/shows/                                  ->      Response create(ShowDTO)
    INFO -               PUT http://localhost:8080/ticket-monster/rest/forge/shows/{id:[0-9][0-9]*}                  ->      Response update(Long, ShowDTO)
    INFO -            DELETE http://localhost:8080/ticket-monster/rest/forge/venues/{id:[0-9][0-9]*}                 ->      Response deleteById(Long)
    INFO -               GET http://localhost:8080/ticket-monster/rest/forge/venues/                                 ->      List<VenueDTO> listAll(Integer, Integer)
    INFO -               GET http://localhost:8080/ticket-monster/rest/forge/venues/{id:[0-9][0-9]*}                 ->      Response findById(Long)
    INFO -              POST http://localhost:8080/ticket-monster/rest/forge/venues/                                 ->      Response create(VenueDTO)
    INFO -               PUT http://localhost:8080/ticket-monster/rest/forge/venues/{id:[0-9][0-9]*}                 ->      Response update(Long, VenueDTO)
    INFO -            DELETE http://localhost:8080/ticket-monster/rest/mediaitems/{id:[0-9][0-9]*}                   ->      Response deleteById(Long)
    INFO -               GET http://localhost:8080/ticket-monster/rest/mediaitems/                                   ->      List<MediaItemDTO> listAll(Integer, Integer)
    INFO -               GET http://localhost:8080/ticket-monster/rest/mediaitems/{id:[0-9][0-9]*}                   ->      Response findById(Long)
    INFO -              POST http://localhost:8080/ticket-monster/rest/mediaitems/                                   ->      Response create(MediaItemDTO)
    INFO -               PUT http://localhost:8080/ticket-monster/rest/mediaitems/{id:[0-9][0-9]*}                   ->      Response update(Long, MediaItemDTO)
    INFO -               GET http://localhost:8080/ticket-monster/rest/metrics/                                      ->      List<ShowMetric> getMetrics()
    INFO -            DELETE http://localhost:8080/ticket-monster/rest/performances/{id:[0-9][0-9]*}                 ->      Response deleteById(Long)
    INFO -               GET http://localhost:8080/ticket-monster/rest/performances/                                 ->      List<PerformanceDTO> listAll(Integer, Integer)
    INFO -               GET http://localhost:8080/ticket-monster/rest/performances/{id:[0-9][0-9]*}                 ->      Response findById(Long)
    INFO -              POST http://localhost:8080/ticket-monster/rest/performances/                                 ->      Response create(PerformanceDTO)
    INFO -               PUT http://localhost:8080/ticket-monster/rest/performances/{id:[0-9][0-9]*}                 ->      Response update(Long, PerformanceDTO)
    INFO -            DELETE http://localhost:8080/ticket-monster/rest/sectionallocations/{id:[0-9][0-9]*}           ->      Response deleteById(Long)
    INFO -               GET http://localhost:8080/ticket-monster/rest/sectionallocations/                           ->      List<SectionAllocationDTO> listAll(Integer, Integer)
    INFO -               GET http://localhost:8080/ticket-monster/rest/sectionallocations/{id:[0-9][0-9]*}           ->      Response findById(Long)
    INFO -              POST http://localhost:8080/ticket-monster/rest/sectionallocations/                           ->      Response create(SectionAllocationDTO)
    INFO -               PUT http://localhost:8080/ticket-monster/rest/sectionallocations/{id:[0-9][0-9]*}           ->      Response update(Long, SectionAllocationDTO)
    INFO -            DELETE http://localhost:8080/ticket-monster/rest/sections/{id:[0-9][0-9]*}                     ->      Response deleteById(Long)
    INFO -               GET http://localhost:8080/ticket-monster/rest/sections/                                     ->      List<SectionDTO> listAll(Integer, Integer)
    INFO -               GET http://localhost:8080/ticket-monster/rest/sections/{id:[0-9][0-9]*}                     ->      Response findById(Long)
    INFO -              POST http://localhost:8080/ticket-monster/rest/sections/                                     ->      Response create(SectionDTO)
    INFO -               PUT http://localhost:8080/ticket-monster/rest/sections/{id:[0-9][0-9]*}                     ->      Response update(Long, SectionDTO)
    INFO -               GET http://localhost:8080/ticket-monster/rest/shows/                                        ->      List<T> getAll(UriInfo)
    INFO -               GET http://localhost:8080/ticket-monster/rest/shows/count                                   ->      Map<String, Long> getCount(UriInfo)
    INFO -               GET http://localhost:8080/ticket-monster/rest/shows/performance/{performanceId:[0-9][0-9]*} ->      Show getShowByPerformance(Long)
    INFO -               GET http://localhost:8080/ticket-monster/rest/shows/{id:[0-9][0-9]*}                        ->      T getSingleInstance(Long)
    INFO -            DELETE http://localhost:8080/ticket-monster/rest/ticketcategories/{id:[0-9][0-9]*}             ->      Response deleteById(Long)
    INFO -               GET http://localhost:8080/ticket-monster/rest/ticketcategories/                             ->      List<TicketCategoryDTO> listAll(Integer, Integer)
    INFO -               GET http://localhost:8080/ticket-monster/rest/ticketcategories/{id:[0-9][0-9]*}             ->      Response findById(Long)
    INFO -              POST http://localhost:8080/ticket-monster/rest/ticketcategories/                             ->      Response create(TicketCategoryDTO)
    INFO -               PUT http://localhost:8080/ticket-monster/rest/ticketcategories/{id:[0-9][0-9]*}             ->      Response update(Long, TicketCategoryDTO)
    INFO -            DELETE http://localhost:8080/ticket-monster/rest/ticketprices/{id:[0-9][0-9]*}                 ->      Response deleteById(Long)
    INFO -               GET http://localhost:8080/ticket-monster/rest/ticketprices/                                 ->      List<TicketPriceDTO> listAll(Integer, Integer)
    INFO -               GET http://localhost:8080/ticket-monster/rest/ticketprices/{id:[0-9][0-9]*}                 ->      Response findById(Long)
    INFO -              POST http://localhost:8080/ticket-monster/rest/ticketprices/                                 ->      Response create(TicketPriceDTO)
    INFO -               PUT http://localhost:8080/ticket-monster/rest/ticketprices/{id:[0-9][0-9]*}                 ->      Response update(Long, TicketPriceDTO)
    INFO -            DELETE http://localhost:8080/ticket-monster/rest/tickets/{id:[0-9][0-9]*}                      ->      Response deleteById(Long)
    INFO -               GET http://localhost:8080/ticket-monster/rest/tickets/                                      ->      List<TicketDTO> listAll(Integer, Integer)
    INFO -               GET http://localhost:8080/ticket-monster/rest/tickets/{id:[0-9][0-9]*}                      ->      Response findById(Long)
    INFO -              POST http://localhost:8080/ticket-monster/rest/tickets/                                      ->      Response create(TicketDTO)
    INFO -               PUT http://localhost:8080/ticket-monster/rest/tickets/{id:[0-9][0-9]*}                      ->      Response update(Long, TicketDTO)
    INFO -               GET http://localhost:8080/ticket-monster/rest/venues/                                       ->      List<T> getAll(UriInfo)
    INFO -               GET http://localhost:8080/ticket-monster/rest/venues/count                                  ->      Map<String, Long> getCount(UriInfo)
    INFO -               GET http://localhost:8080/ticket-monster/rest/venues/{id:[0-9][0-9]*}                       ->      T getSingleInstance(Long)
    INFO -               GET http://localhost:8080/ticket-monster/rest/bot/messages                                  ->      List<String> getMessages()
    INFO -               GET http://localhost:8080/ticket-monster/rest/bot/status                                    ->      Response getBotStatus()
    INFO -               PUT http://localhost:8080/ticket-monster/rest/bot/status                                    ->      Response updateBotStatus(BotState)
    INFO -               GET http://localhost:8080/ticket-monster/rest/media/cache/{cachedFileName:\S*}              ->      File getCachedMediaContent(String)
    INFO -               GET http://localhost:8080/ticket-monster/rest/media/{id:\d*}                                ->      File getMediaContent(Long)


