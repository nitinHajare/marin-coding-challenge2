Name: REST Service for Lok Sabha Attendance
Version: 1.0.0
Technologies: Java 1.7, Spring MVC 4.1.5, Jackson.
Description: Developed REST api for Lok Sabha Attendence & deployed them in Google App Engine

REST Endpoints:
Hostname: http://marinassignment.appspot.com OR http://localhost

1. To get top 'N' MPs with the highest attendance, where 'N' can range between 1 to 500
   Endpoint: http://hostname/marin/highest-attendance/{N}
   Method: GET
   Parameter Usage: {N} is path parameter for rest api, based on input it will return N number of members as a response
   Example: http://marinassignment.appspot.com/marin/highest-attendance/20
	
2. To get distribution of States to Number of Lok Sabha MPs elected from the State
   Endpoint:  http://hostname/marin/sansad-by-state
   Method: GET
   Example: http://marinassignment.appspot.com/marin/sansad-by-state

3. To get list of States sorted by MP attendance either by ascending or descending order.
   Endpoint: http://hostname/marin/states-by-attendance
   Method: GET
   Default Order: DESC
   Example: 1. http://marinassignment.appspot.com/marin/states-by-attendance
			2. http://marinassignment.appspot.com/marin/states-by-attendance?sortby=asc
   
	
Addition APIs:

1. To get top 'N' MPs with the lowest attendance, where 'N' can range betweem 1 to 500
   Endpoint: http://hostname/marin/lowest-attendance/{N}
   Method: GET
   Parameter Usage: {N} is path parameter for rest api, based on input it will return N number of members as a response
   Example: http://marinassignment.appspot.com/marin/lowest-attendance/100
 
2. To get the list of members sorted by attendance within a state
   Endpoint: http://hostname/marin/{statename}/attendance
   Method: GET
   Parameter Usage: {statename} state name
   Example: http://marinassignment.appspot.com/marin/maharashtra/attendance

  
Build Steps:

1. Go to git bash command terminal
2. git clone https://github.com/nitinHajare/marin-coding-challenge2.git
3. Import cloned project to Eclipse or IntellJ
4. Build a WAR 
5. Deploy war to local web server like Apache Tomcat, Jetty etc.
6. Browse url http://localhost:8080 to see welcome page
7. Start testing published REST APIs 
	