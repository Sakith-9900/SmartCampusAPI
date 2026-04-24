# Smart Campus Sensor and Room Management API
 
## Name: H.V.S.M.Gunarathna
## Student ID: 20231895
 
## Module Details
- Module code: 5COSC022W
- Module: Client-Server Architectures
- Technology: JAX-RS (Jersey), Maven, Java 11, in-memory data structures only
  
## API Design Overview
 
This is a RESTful API built with JAX-RS (Jersey) and Apache Tomcat for managing university campus rooms and sensors. All data is kept in memory using ConcurrentHashMap - no database is used.
 
The API is versioned under `/api/v1`. Rooms and sensors are the two main resources. Each sensor belongs to a room, and each sensor has its own reading history accessible through a nested sub-resource path.
 
**Base URL:** `http://localhost:8080/SmartCampusAPI/api/v1`

### Discovery
- GET /api/v1
### Rooms
- GET /api/v1/rooms
- POST /api/v1/rooms
- GET /api/v1/rooms/{roomId}
- DELETE /api/v1/rooms/{roomId}
### Sensors
- GET /api/v1/sensors
- GET /api/v1/sensors?type=CO2
- POST /api/v1/sensors
- GET /api/v1/sensors/{sensorId}
- DELETE /api/v1/sensors/{sensorId}
### Sensor Readings (Nested Sub-Resource)
- GET /api/v1/sensors/{sensorId}/readings
- POST /api/v1/sensors/{sensorId}/readings
  
**Error responses always return JSON** - never a raw stack trace:
```json
{ "status": 409, "error": "Conflict", "message": "Room still has sensors assigned." }
```
 
---
 
## How to Build and Run
 
**You will need:** JDK 11+, Maven 3.6+, Apache Tomcat 9, NetBeans IDE
 
**Step 1 - Clone the repository**
```bash
git clone https://github.com/Sakith-9900/SmartCampusAPI.git
cd SmartCampusAPI
```
 
**Step 2 - Build the project**
```bash
mvn clean package
```
This creates `target/SmartCampusAPI.war`
 
**Step 3 - Set up Tomcat in NetBeans**
- Open NetBeans → go to the Services tab
- Right-click Servers → Add Server → Apache Tomcat
- Point it to your Tomcat installation folder
- Set a username and password when prompted
**Step 4 - Run the project**
- Right-click the project in NetBeans → Clean and Build
- Right-click the project → Run
- NetBeans will deploy the WAR to Tomcat automatically
**Step 5 - Confirm it is running**
 
Open your browser and visit:
```
http://localhost:8080/SmartCampusAPI/api/v1
```
## Sample curl Commands
 
**1. Discovery endpoint**
```bash
curl -i http://localhost:8080/SmartCampusAPI/api/v1
```

**2. Create a room**
```bash
curl -i -X POST http://localhost:8080/SmartCampusAPI/api/v1/rooms -H "Content-Type: application/json" -d "{\"id\":\"LIB-301\",\"name\":\"Library Quiet Study\",\"capacity\":50}"
```

**3. Get all rooms**
```bash
curl -i http://localhost:8080/SmartCampusAPI/api/v1/rooms
```

**4. Create a sensor linked to a room**
```bash
curl -i -X POST http://localhost:8080/SmartCampusAPI/api/v1/sensors -H "Content-Type: application/json" -d "{\"id\":\"CO2-001\",\"type\":\"CO2\",\"status\":\"ACTIVE\",\"currentValue\":0.0,\"roomId\":\"LIB-301\"}"
```

**5. Filter sensors by type**
```bash
curl -i "http://localhost:8080/SmartCampusAPI/api/v1/sensors?type=CO2"
```

**6. Post a reading to a sensor**
```bash
curl -i -X POST http://localhost:8080/SmartCampusAPI/api/v1/sensors/CO2-001/readings -H "Content-Type: application/json" -d "{\"value\":412.5}"
```

**7. Get the reading history for a sensor**
```bash
curl -i http://localhost:8080/SmartCampusAPI/api/v1/sensors/CO2-001/readings
```

**8. Trigger 422 - sensor with non-existent roomId**
```bash
curl -i -X POST http://localhost:8080/SmartCampusAPI/api/v1/sensors -H "Content-Type: application/json" -d "{\"id\":\"TEMP-999\",\"type\":\"Temperature\",\"status\":\"ACTIVE\",\"currentValue\":0.0,\"roomId\":\"FAKE-000\"}"
```

**9. Trigger 403 - create a MAINTENANCE sensor then post a reading to it**
```bash
curl -i -X POST http://localhost:8080/SmartCampusAPI/api/v1/sensors -H "Content-Type: application/json" -d "{\"id\":\"MAINT-001\",\"type\":\"Temperature\",\"status\":\"MAINTENANCE\",\"currentValue\":0.0,\"roomId\":\"LIB-301\"}"
```
```bash
curl -i -X POST http://localhost:8080/SmartCampusAPI/api/v1/sensors/MAINT-001/readings -H "Content-Type: application/json" -d "{\"value\":500.0}"
```

**10. Trigger 409 - delete a room that still has sensors**
```bash
curl -i -X DELETE http://localhost:8080/SmartCampusAPI/api/v1/rooms/LIB-301
```
# Coursework Report

## Part 1: Service Architecture & Setup

### 1. Project & Application Configuration 

**Question:** In your report, explain the default lifecycle of a JAX-RS Resource class. Is a new instance instantiated for every incoming request, or does the runtime treat it as a singleton? Elaborate on how this architectural decision impacts the way you manage and synchronize your in-memory data structures (maps/lists) to prevent data loss or race conditions.

**Answer:** By default, JAX-RS uses a per-request lifecycle, meaning a new resource instance is created for every HTTP request and destroyed immediately after. This means we cannot store data in standard instance variables as they would be wiped out between requests. To prevent data loss and race conditions, data is managed by Singleton DAOs which live for the entire server lifetime. These DAOs use ConcurrentHashMap instead of a regular HashMap because it is thread-safe and handles multiple simultaneous requests reading and writing data without causing race conditions or data corruption.

### 2. The Discovery Endpoint
**Question:** Why is the provision of "Hypermedia" (links and navigation within responses) considered a hallmark of advanced RESTful design (HATEOAS)? How does this approach benefit client developers compared to static documentation?

**Answer:** HATEOAS (Hypermedia as the Engine of Application State) means API responses include navigation links to related resources. In this API the discovery endpoint returns a collections map containing the full URLs for rooms and sensors so clients can find all available endpoints without relying on hardcoded URLs or static documentation. This makes the API self-describing and flexible because if backend routing changes, clients who follow the provided links will automatically adjust without needing to update their code. Static documentation can become outdated but hypermedia links are always live and accurate.

---

## Part 2: Room Management 

### 1. Room Resource Implementation 

*Question:* When returning a list of rooms, what are the implications of returning only IDs versus returning the full room objects? Consider network bandwidth and client-side processing.

**Answer:** Returning only IDs saves initial network bandwidth but creates an N+1 query problem, forcing the client to make multiple follow-up HTTP requests to fetch room details which increases overall latency. Returning full objects increases the initial payload size but is more efficient for the client as all necessary data is retrieved in a single request. For very large collections, pagination with full objects or returning IDs with a follow-up fetch are both valid strategies depending on the use case. In my implementation I return full room objects for simplicity and usability.

### 2. Room Deletion & Safety Logic 

*Question:* Is the DELETE operation idempotent in your implementation? Provide a detailed justification by describing what happens if a client mistakenly sends the exact same DELETE request for a room multiple times.

**Answer:** Yes, the DELETE operation is idempotent - repeated requests yield the same final state. If a client successfully deletes a room the API returns 204 No Content. If they mistakenly send the exact same request again, the room no longer exists so the server returns 404 Not Found. The server data state does not change after the initial deletion. No data is corrupted or duplicated by the repeated request. The only exception is if the room has sensors assigned - the first DELETE would return 409 Conflict and nothing would be deleted, which is correct business logic not a violation of idempotency.

---

## Part 3: Sensor Operations & Linking

### 1. Sensor Resource & Integrity

*Question:* We explicitly use the @Consumes(MediaType.APPLICATION_JSON) annotation on the POST method. Explain the technical consequences if a client attempts to send data in a different format, such as text/plain or application/xml. How does JAX-RS handle this mismatch?

**Answer:** The @Consumes annotation strictly enforces the accepted data format. JAX-RS framework prevents the method logic from executing when a client sends text/plain or application/xml because of the mismatched content type. The system automatically rejects the request and sends an HTTP 415 Unsupported Media Type error. This behavior protects the application from parsing errors because it operates without any customized Java error management. Jersey handles this entire process through its framework capabilities.

### 2. Filtered Retrieval & Search

*Question:* You implemented this filtering using @QueryParam. Contrast this with an alternative design where the type is part of the URL path (e.g., /api/v1/sensors/type/CO2). Why is the query parameter approach generally considered superior for filtering and searching collections?

**Answer:** URL paths are designed to identify specific resources in a noun-based hierarchy such as /sensors/123. Query parameters are superior for filtering because they act as optional modifiers applied to a larger collection. They are easily composable without changing the URL structure, for example ?type=CO2&status=ACTIVE, whereas path-based filtering would need completely different URL patterns for each combination of filters making the API rigid and hard to maintain. Query parameters also follow REST conventions for collection filtering and are more familiar to API consumers.

---

## Part 4: Deep Nesting with Sub-Resources 

### 1. The Sub-Resource Locator Pattern 
*Question:* Discuss the architectural benefits of the Sub-Resource Locator pattern. How does delegating logic to separate classes help manage complexity in large APIs compared to defining every nested path (e.g., sensors/{id}/readings/{rid}) in one massive controller class?

**Answer:** The Sub-Resource Locator pattern provides excellent separation of concerns. Instead of writing one massive controller class to handle every single nested endpoint, SensorResource acts as a router and delegates sub-paths to a dedicated SensorReadingResource. This keeps each class small, highly cohesive, and modular. SensorResource handles sensor CRUD and SensorReadingResource handles reading history exclusively. Changes to reading logic do not risk breaking sensor logic. In a large API, putting everything in one class would make it thousands of lines long and very hard to debug. The locator pattern keeps the codebase easy to test, maintain, and extend.


---

## Part 5: Advanced Error Handling, Exception Mapping & Logging 

### 2. Dependency Validation (422 Unprocessable Entity) 

*Question:* Why is HTTP 422 often considered more semantically accurate than a standard 404 when the issue is a missing reference inside a valid JSON payload?

**Answer:** A standard 404 Not Found indicates that the requested URL does not exist. The system has a valid operational path through the /sensors endpoint which accepts correctly formatted JSON data when users submit sensor information. The problem occurs because the roomId present in the payload points to a nonexistent room. The client receives HTTP 422 Unprocessable Entity status which shows that the endpoint functions properly and the JSON data structure is valid but the contained information has an unresolvable logical reference. The system provides the client with better information about the error which occurred and the location of the error.

### 4. The Global Safety Net (500) 

*Question:* From a cybersecurity standpoint, explain the risks associated with exposing internal Java stack traces to external API consumers. What specific information could an attacker gather from such a trace?

**Answer:** Stack traces should not be shown because they provide attackers with critical system information. The application stack trace displays complete internal application details which include package names and class names and method names and file paths and exact line numbers. An attacker can use this to identify exactly which framework and library versions are in use and look up known CVEs to craft targeted exploits. Stack traces provide information about database query structures and internal logic flows which attackers use to prepare SQL injection attacks and path traversal attacks and method-specific attacks. My GlobalExceptionMapper prevents this by catching all unhandled exceptions and logging them privately server-side through java.util.logging.Logger and returning only a safe generic 500 Internal Server Error message to the client.

### 5. API Request & Response Logging Filters

*Question:* Why is it advantageous to use JAX-RS filters for cross-cutting concerns like logging, rather than manually inserting Logger.info() statements inside every single resource method?

**Answer:** The API requires logging to be implemented as a fundamental requirement that must be present at every endpoint. The development of a JAX-RS filter through a single class which uses the @Provider annotation enables centralized management of all logging operations while maintaining the DRY principle of programming. The requirement to use Logger.info() throughout all resource methods results in redundant code which increases the likelihood that developers will neglect to implement logging for new endpoints. The filter system ensures automatic logging of all requests and responses because developers do not need to remember this task. The system requires only one file update to change the log format instead of needing multiple updates for every resource class in the project.
